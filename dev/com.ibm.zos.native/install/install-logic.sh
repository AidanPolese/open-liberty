#!/bin/sh
#----------------------------------------------------------------------
# This script is intended to help setup the your test environment on
# ezWAS.  While this isn't something I expect we'll ever deliver to
# our customers (it relies on some of our own infrastructure), many of
# the steps they will need to perform will be encapsulated in this
# logic.
#----------------------------------------------------------------------

PATH=$PATH:/usr/local/bin

# Issue an error message and exit the script
function error {
    echo "$1" >&2
    exit $2
}

function message {
    echo "$1"
}

# Pipe encoded payload through uudecode and pax
function unpax_payload {
    payload_marker_line=$(grep -n '^PAX_PAYLOAD:$' $0 | cut -d ':' -f 1)
    payload_start=$((match + 1))
    tail -n +$payload_start $0 | uudecode | pax -r -ppx
}

# Verify FACILTY BPX.FILEATTR.APF
function assertApf {
    testfile=aptftest.$$.tmp
    rm -f $testfile
    touch $testfile

    extattr +a $testfile > /dev/null 2>&1
    if [ $? -eq 0 ]; then
        rm -f $testfile
    else
        rm -rf $testfile
        error "User not authorized to use extattr +a" -4
    fi
}

# Verify FACILTY BPX.FILEATTR.PROGCTL
function assertProgramControl {
    testfile=pgctl.$$.tmp
    rm -f $testfile
    touch $testfile

    extattr +p $testfile > /dev/null 2>&1
    if [ $? -eq 0 ]; then
        rm -f $testfile
    else
        rm -rf $testfile
        error "User not authorized to use extattr +p" -4
    fi
}

# Verify FACILTY BPX.FILEATTR.SHARELIB
function assertShareLib {
    testfile=sharelib.$$.tmp
    rm -f $testfile
    touch $testfile

    extattr +s $testfile > /dev/null 2>&1
    if [ $? -eq 0 ]; then
        rm -f $testfile
    else
        rm -rf $testfile
        error "User not authorized to use extattr +s" -4
    fi
}

# Verify all extended attributes can be set
function assertExtAttrs {
    assertApf
    assertProgramControl
    assertShareLib
}

# Environment
pathName=$0
scriptName=${pathName##*/} 
echo scriptName=${scriptName}
if [ ${scriptName} = "install-logic-fat.sh" ];
 then  
    PROC_HOME=/u/MSTONE1/wlp/templates/zos/procs
 else 
    PROC_HOME=./wlp/templates/zos/procs
fi
BOSS_USER=$(sysvar BOSSUSER)
CURRENT_USER=`whoami`
SPECIAL_USER="IBMUSER"
PROCLIB="BOSS.$BOSS_USER.PROCLIB"

# Vicom systems have STARTED SUBMIT*.* associated with IBMUSER,
# they allow the MSTONE1 ID to use the operator console, and the
# MSTONE1 ID can write to BOSS.&BOSSUSER..PROCLIB.  We can build
# on this configuration to allow us to submit jobs as IBMUSER
# that alter the security configuration.

#
# 1. Copy SUBMITVZ into BOSS.&BOSSUSER.PROCLIB:
#
cat << SUBMITVZ_PROC_END > /tmp/submitvz.$$.jcl
//SUBMITVZ  PROC JOB=
//*
//* PROC to submit a job in the HFS from the operator console
//* tsocmd 'console syscmd(start submitvz,job=...)'
//*
//         EXEC PGM=IEBGENER
//SYSIN    DD   DUMMY
//SYSPRINT DD   DUMMY
//SYSUT2   DD   SYSOUT=(A,INTRDR)
//SYSUT1   DD   PATH='&JOB.',PATHOPTS=(ORDONLY),
//  FILEDATA=TEXT,RECFM=FB,LRECL=80,BLKSIZE=80
SUBMITVZ_PROC_END

cp /tmp/submitvz.$$.jcl "//'$PROCLIB(SUBMITVZ)'"
if [ $? -eq 0 ]; then
    rm -f /tmp/submitvz.$$.jcl
else
    error "Failed to place SUBMITVZ into proclib" -8
fi

#
# 2. Create the following job in the HFS.  It will run as IBMUSER:
#
cat << GRANT_JOB_END > /tmp/grant.$$.jcl
//GRANTSU  JOB MSGLEVEL=(1,1),CLASS=A,USER=IBMUSER
//*-----------------------------------------------------------------
//* Make the current user a USS surrogate of the RACF special user
//*-----------------------------------------------------------------
//STEP1    EXEC PGM=IKJEFT01,REGION=0K
//SYSTSPRT DD SYSOUT=*
//SYSTSIN  DD *
RDEF SURROGAT BPX.SRV.$SPECIAL_USER
PE   BPX.SRV.IBMUSER CLASS(SURROGAT) ID($CURRENT_USER) ACC(READ)

SETR RACLIST(SURROGAT) REFR
END
/*
//STEP2    EXEC PGM=IEFBR14
//OUTPUT   DD PATH='/tmp/grant.$$.output',
//  PATHOPTS=(OCREAT,OTRUNC,OWRONLY)
GRANT_JOB_END

#
# 3. Use the SUBMITVZ started task to submit the GRANTSU job.
#
tsocmd "console syscmd(s submitvz,job='/tmp/grant.$$.jcl')"
if [ $? -ne 0 ]; then
    message "Failed to submit the GRANTSU job"
    message "If you have an active session with SDSF, please exit SDSF and retry."
    # Don't give up so fast... perhaps MSTONE1 has already been granted SU authority.
    # If it hasn't, then we'll fail out at step 4 when we test out MSTONE1's SU access.
    # error "Failed to submit the GRANTSU job" -8
else
    # Now wait for the job to complete
    x=0
    while [ $x -lt 10 ] && [ ! -e /tmp/grant.$$.output ]; do
        x=$((x+1))
        sleep 1
    done
    
    if [ -e /tmp/grant.$$.output ]; then
        su -s $SPECIAL_USER -c "/bin/rm -f /tmp/grant.$$.output"
        rm -f /tmp/grant.$$.jcl
    else
        error "Grant job did not complete in 10 seconds" -24
    fi
fi

#
# 4. Test out our access
#
su -s $SPECIAL_USER -c '/bin/true'
if [ $? -ne 0 ]; then
    message "Unable to su to RACF special user $SPECIAL_USER" 
    message "Check that the GRANTSU job completed successfully"
    error "Unable to su to RACF special user $SPECIAL_USER" -12
fi

## Started task profiles
#for i in `tsocmd 'search class(started)' | grep '^B' | cut -d' ' -f1`; do
#    tsocmd 'rlist started '$i' stdata noracf noyouracc';
#done

##
## TODO: Create users and groups for angel and servers
##

#
# 5. Create the started profiles
#
su -s $SPECIAL_USER -c 'tsocmd "rdef started bbgzangl.* uacc(none) stdata(user(mstone1) group(wasuser) privileged(no) trusted(no) trace(yes))"'
su -s $SPECIAL_USER -c 'tsocmd "rdef started bbgzsrv.* uacc(none) stdata(user(mstone1) group(wasuser) privileged(no) trusted(no) trace(yes))"'
su -s $SPECIAL_USER -c 'tsocmd "setr raclist(started) refr"'
su -s $SPECIAL_USER -c 'tsocmd "setr raclist(started) generic(started) refr"'

#
# 6. Create the extended attribute profiles and verify
#
su -s $SPECIAL_USER -c 'tsocmd "rdef facility bpx.fileattr.apf uacc(none)"'
su -s $SPECIAL_USER -c 'tsocmd "rdef facility bpx.fileattr.progctl uacc(none)"'
su -s $SPECIAL_USER -c 'tsocmd "rdef facility bpx.fileattr.sharelib uacc(none)"'
su -s $SPECIAL_USER -c 'tsocmd "setr raclist(facility) refr"'
assertExtAttrs

#
# 7. Create permissions in the server class
#
su -s $SPECIAL_USER -c 'tsocmd "rdef server bbg.angel uacc(none)"'
su -s $SPECIAL_USER -c 'tsocmd "permit bbg.angel class(server) access(read) id(mstone1)"'
su -s $SPECIAL_USER -c 'tsocmd "rdef server bbg.authmod.bbgzsafm uacc(none)"'
su -s $SPECIAL_USER -c 'tsocmd "permit bbg.authmod.bbgzsafm class(server) access(read) id(mstone1)"'
su -s $SPECIAL_USER -c 'tsocmd "rdef server bbg.authmod.bbgzsafm.zoswlm uacc(none)"'
su -s $SPECIAL_USER -c 'tsocmd "permit bbg.authmod.bbgzsafm.zoswlm class(server) access(read) id(mstone1)"'
su -s $SPECIAL_USER -c 'tsocmd "rdef server bbg.authmod.bbgzsafm.txrrs uacc(none)"'
su -s $SPECIAL_USER -c 'tsocmd "permit bbg.authmod.bbgzsafm.txrrs class(server) access(read) id(mstone1)"'
su -s $SPECIAL_USER -c 'tsocmd "rdef server bbg.authmod.bbgzsafm.safcred uacc(none)"'
su -s $SPECIAL_USER -c 'tsocmd "permit bbg.authmod.bbgzsafm.safcred class(server) access(read) id(mstone1)"'
su -s $SPECIAL_USER -c 'tsocmd "rdef server bbg.authmod.bbgzsafm.zosdump uacc(none)"'
su -s $SPECIAL_USER -c 'tsocmd "permit bbg.authmod.bbgzsafm.zosdump class(server) access(read) id(mstone1)"'
su -s $SPECIAL_USER -c 'tsocmd "rdef server bbg.authmod.bbgzsafm.prodmgr uacc(none)"'
su -s $SPECIAL_USER -c 'tsocmd "permit bbg.authmod.bbgzsafm.prodmgr class(server) access(read) id(mstone1)"'
su -s $SPECIAL_USER -c 'tsocmd "rdef server bbg.authmod.bbgzscfm uacc(none)"'
su -s $SPECIAL_USER -c 'tsocmd "permit bbg.authmod.bbgzscfm class(server) access(read) id(mstone1)"'
su -s $SPECIAL_USER -c 'tsocmd "rdef server bbg.authmod.bbgzscfm.testracf uacc(none)"'
su -s $SPECIAL_USER -c 'tsocmd "permit bbg.authmod.bbgzscfm.testracf class(server) access(read) id(mstone1)"'
su -s $SPECIAL_USER -c 'tsocmd "rdef server bbg.authmod.bbgzscfm.localcom uacc(none)"'
su -s $SPECIAL_USER -c 'tsocmd "permit bbg.authmod.bbgzscfm.localcom class(server) access(read) id(mstone1)"'
su -s $SPECIAL_USER -c 'tsocmd "rdef server bbg.authmod.bbgzsafm.localcom uacc(none)"'
su -s $SPECIAL_USER -c 'tsocmd "permit bbg.authmod.bbgzsafm.localcom class(server) access(read) id(mstone1)"'
su -s $SPECIAL_USER -c 'tsocmd "rdef server bbg.authmod.bbgzsafm.wola uacc(none)"'
su -s $SPECIAL_USER -c 'tsocmd "permit bbg.authmod.bbgzsafm.wola class(server) access(read) id(mstone1)"'
su -s $SPECIAL_USER -c 'tsocmd "rdef server bbg.authmod.bbgzscfm.wola uacc(none)"'
su -s $SPECIAL_USER -c 'tsocmd "permit bbg.authmod.bbgzscfm.wola class(server) access(read) id(mstone1)"'
su -s $SPECIAL_USER -c 'tsocmd "rdef server bbg.authmod.bbgzsafm.zosaio uacc(none)"'
su -s $SPECIAL_USER -c 'tsocmd "permit bbg.authmod.bbgzsafm.zosaio class(server) access(read) id(mstone1)"'
su -s $SPECIAL_USER -c 'tsocmd "setr raclist(server) generic(server) refr"'

#
# 8. Unpax the distribution
#
if [ ${scriptName} != "install-logic-fat.sh" ]; then  
    echo "Extracting payload..."
    unpax_payload
fi

#
# 9. Copy the procs into proclib
#
cp $PROC_HOME/bbgzsrv.jcl "//'$PROCLIB(bbgzsrv)'"
if [ $? -ne 0 ]; then
    error "Unable to copy procs/bbgzsrv into proclib" -16
fi

cp $PROC_HOME/bbgzangl.jcl "//'$PROCLIB(bbgzangl)'"
if [ $? -ne 0 ]; then
    error "Unable to copy procs/bbgzangl into proclib" -16
fi

#read -p "Install files? " ans
#if [[ "${ans:0:1}"  ||  "${ans:0:1}" ]]; then
#    untar_payload
#    # Do remainder of install steps.
#fi

exit 0

PAX_PAYLOAD:
