#!/bin/sh
#
# Convenience script for installing bbgznut on an EZWAS system.
#
# Note: this script assumes you've already installed Liberty at ~/wlp.
# 
# This script does the following:
#
# 1. Installs the unit test dll(s) at ~/wlp/lib/native/zos/s390x/
# 2. chmods and APF-authorizes the dll(s)
# 3. creates a STARTED class BBGZNUT.* in RACF and associates it with MSTONE1
# 4. Installs proc BBGZNUT into BOSS.$BOSS_USER.PROCLIB(BBGZNUT)
# 5. Prints out some commands for running the tests
# 

PATH=$PATH:/usr/local/bin


#
# Pipe encoded payload through uudecode and pax
#
function unpax_payload {
    payload_marker_line=$(grep -n '^PAX_PAYLOAD:$' $0 | cut -d ':' -f 1)
    payload_start=$((match + 1))
    tail -n +$payload_start $0 | uudecode | pax -r -ppx
}


#
# Create BBGZNUT JCL in BOSS.<BOSS_ID>.PROCLIB
# 
function createBbgznutJcl {

    TMP_FILE=/tmp/bbgznut.$$.jcl
cat << BBGZNUT_PROC_END > $TMP_FILE
//BBGZNUT  PROC PARMS=''                                                        
//*------------------------------------------------------------------           
//  SET ROOT='/u/MSTONE1/wlp'                                                   
//*------------------------------------------------------------------           
//* Run Liberty for zos native unit tests                                       
//*------------------------------------------------------------------           
//STEP1   EXEC PGM=BPXBATA2,REGION=0M,                                          
//  PARM='PGM &ROOT./lib/native/zos/s390x/bbgznut &PARMS'                       
//STDOUT    DD SYSOUT=*                                                         
//STDERR    DD SYSOUT=*                                                         
//* ================================================================ */         
//* PROPRIETARY-STATEMENT:                                           */         
//* Licensed Material - Property of IBM                              */         
//*                                                                  */         
//* (C) Copyright IBM Corp. 2013                                     */         
//* All Rights Reserved                                              */         
//* US Government Users Restricted Rights - Use, duplication or      */         
//* disclosure restricted by GSA ADP Schedule Contract with IBM Corp.*/         
//* ================================================================ */         
BBGZNUT_PROC_END

    BOSS_USER=$(sysvar BOSSUSER)
    PROCLIB="BOSS.$BOSS_USER.PROCLIB"
    PROC_NAME=BBGZNUT
    
    cp $TMP_FILE "//'$PROCLIB($PROC_NAME)'"
    if [ $? -eq 0 ]; then
        rm -f $TMP_FILE
        echo "Installed proc $PROC_NAME to $PROCLIB($PROC_NAME)" 
    else
        echo "Failed to copy $TMP_FILE into proclib $PROCLIB($PROC_NAME)" 
        exit 1
    fi
}


#
# Create JCL: BOSS.<BOSS-ID>.PROCLIB(BBGZLCMC).
#
function createBbgzlcmcJcl {
    
    TMP_FILE=/tmp/bbgzlcmc.$$.jcl
cat << BBGZLCMC_PROC_END > $TMP_FILE
//BBGZLCMC PROC PARMS=''                                                        
//*------------------------------------------------------------------           
//  SET ROOT='/u/MSTONE1/wlp'                                                   
//*------------------------------------------------------------------           
//* Run Liberty for zos native unit tests                                       
//*------------------------------------------------------------------           
//STEP1   EXEC PGM=BPXBATA2,REGION=0M,                                          
//  PARM='PGM &ROOT./lib/native/zos/s390x/bbgzlcmc &PARMS'                      
//STDOUT    DD SYSOUT=*                                                         
//STDERR    DD SYSOUT=*                                                         
//* ================================================================ */         
//* PROPRIETARY-STATEMENT:                                           */         
//* Licensed Material - Property of IBM                              */         
//*                                                                  */         
//* (C) Copyright IBM Corp. 2013                                     */         
//* All Rights Reserved                                              */         
//* US Government Users Restricted Rights - Use, duplication or      */         
//* disclosure restricted by GSA ADP Schedule Contract with IBM Corp.*/         
//* ================================================================ */         
BBGZLCMC_PROC_END

    BOSS_USER=$(sysvar BOSSUSER)
    PROCLIB="BOSS.$BOSS_USER.PROCLIB"
    PROC_NAME=BBGZLCMC
    
    cp $TMP_FILE "//'$PROCLIB($PROC_NAME)'"
    if [ $? -eq 0 ]; then
        rm -f $TMP_FILE
        echo "Installed proc $PROC_NAME to $PROCLIB($PROC_NAME)" 
    else
        echo "Failed to copy $TMP_FILE into proclib $PROCLIB($PROC_NAME)" 
        exit 1
    fi
}


#
# Create JCL: BOSS.<BOSS-ID>.PROCLIB(BBGZSHRC).
#
function createBbgzshrcJcl {
    
    TMP_FILE=/tmp/bbgzshrc.$$.jcl
cat << BBGZSHRC_PROC_END > $TMP_FILE
//BBGZSHRC PROC PARMS=''                                                        
//*------------------------------------------------------------------           
//  SET ROOT='/u/MSTONE1/wlp'                                                   
//*------------------------------------------------------------------           
//* Run Liberty for zos native unit tests                                       
//*------------------------------------------------------------------           
//STEP1   EXEC PGM=BPXBATA2,REGION=0M,                                          
//  PARM='PGM &ROOT./lib/native/zos/s390x/bbgzshrc &PARMS'                      
//STDOUT    DD SYSOUT=*                                                         
//STDERR    DD SYSOUT=*                                                         
//* ================================================================ */         
//* PROPRIETARY-STATEMENT:                                           */         
//* Licensed Material - Property of IBM                              */         
//*                                                                  */         
//* (C) Copyright IBM Corp. 2013                                     */         
//* All Rights Reserved                                              */         
//* US Government Users Restricted Rights - Use, duplication or      */         
//* disclosure restricted by GSA ADP Schedule Contract with IBM Corp.*/         
//* ================================================================ */         
BBGZSHRC_PROC_END

    BOSS_USER=$(sysvar BOSSUSER)
    PROCLIB="BOSS.$BOSS_USER.PROCLIB"
    PROC_NAME=BBGZSHRC
    
    cp $TMP_FILE "//'$PROCLIB($PROC_NAME)'"
    if [ $? -eq 0 ]; then
        rm -f $TMP_FILE
        echo "Installed proc $PROC_NAME to $PROCLIB($PROC_NAME)" 
    else
        echo "Failed to copy $TMP_FILE into proclib $PROCLIB($PROC_NAME)" 
        exit 1
    fi
}


#
# Create BBGZNUT.*, BBGZLCMC.*, BBGZSHRC.* STARTED class
#
function createRacfStartedClassProfiles {
    su -s $SPECIAL_USER -c 'tsocmd "rdef started bbgznut.* uacc(none) stdata(user(mstone1) group(wasuser) privileged(no) trusted(no) trace(yes))"'
    su -s $SPECIAL_USER -c 'tsocmd "rdef started bbgzlcmc.* uacc(none) stdata(user(mstone1) group(wasuser) privileged(no) trusted(no) trace(yes))"'
    su -s $SPECIAL_USER -c 'tsocmd "rdef started bbgzshrc.* uacc(none) stdata(user(mstone1) group(wasuser) privileged(no) trusted(no) trace(yes))"'
    su -s $SPECIAL_USER -c 'tsocmd "setr raclist(started) refr"'
    su -s $SPECIAL_USER -c 'tsocmd "setr raclist(started) generic(started) refr"'
}


#
# Peel the pax file from the end of this script, 
# then un-pax the dll and copy the DLLs to the appropriate places,
# then mark them executable and APF-authorized
# 
function installDlls {

    BBGZNUT_TARGET=$HOME/wlp/lib/native/zos/s390x/bbgznut
    BBGZNUT_LEC_TARGET=$HOME/wlp/lib/native/zos/s390x/bbgznut_lec
    BBGZNUT_LECPP_TARGET=$HOME/wlp/lib/native/zos/s390x/bbgznut_lecpp
    BBGZLCMC_TARGET=$HOME/wlp/lib/native/zos/s390x/bbgzlcmc
    BBGZSHRC_TARGET=$HOME/wlp/lib/native/zos/s390x/bbgzshrc

    unpax_payload

    mv server/bbgznut $BBGZNUT_TARGET
    echo "Installed $BBGZNUT_TARGET"

    mv server/bbgznut_lec $BBGZNUT_LEC_TARGET
    echo "Installed $BBGZNUT_LEC_TARGET"

    mv server/bbgznut_lecpp $BBGZNUT_LECPP_TARGET
    echo "Installed $BBGZNUT_LECPP_TARGET"
    
    mv server/bbgzlcmc $BBGZLCMC_TARGET
    echo "Installed $BBGZLCMC_TARGET"

    mv server/bbgzshrc $BBGZSHRC_TARGET
    echo "Installed $BBGZSHRC_TARGET"
    
    #
    # chmod and APF-authorize 
    #
    chmod a+x $BBGZNUT_TARGET $BBGZNUT_LEC_TARGET $BBGZNUT_LECPP_TARGET $BBGZLCMC_TARGET $BBGZSHRC_TARGET
    extattr +ap $BBGZNUT_TARGET $BBGZLCMC_TARGET $BBGZSHRC_TARGET
    
    ls -lE $BBGZNUT_TARGET $BBGZNUT_LEC_TARGET $BBGZNUT_LECPP_TARGET $BBGZLCMC_TARGET $BBGZSHRC_TARGET
}


#
# 1. Install the DLLs into the HFS
#
installDlls


#
# 2. Test out our access
#
SPECIAL_USER=IBMUSER
su -s $SPECIAL_USER -c '/bin/true'
if [ $? -ne 0 ]; then
    message "Unable to su to RACF special user $SPECIAL_USER" 
    message "Check that the GRANTSU job from install.sh completed successfully"
    error "Unable to su to RACF special user $SPECIAL_USER" -12
fi

#
# 3 create STARTED class profiles
#
createRacfStartedClassProfiles


#
# 4.1 Create and install BBGZLCMC JCL into BOSS.<BOSS_ID>.PROCLIB
# 
createBbgznutJcl
createBbgzlcmcJcl
createBbgzshrcJcl


#
# 5. Write out some instructions for executing bbgznut
# 
cat <<HERE

*********************************************************************
**************************  SUCCESS!!  ******************************
*********************************************************************

To run all suites:
       $ execsh exconcmd "s bbgznut" 

To run a single suite, specify its name as the first parm:
       $ execsh exconcmd "s bbgznut,PARMS=''server_wola_services_test''"

To view the output:
       $ sysout -o \`sysout -s BBGZNUT | head -1\`

(Of course you could also run/view from SDSF).

HERE

exit 0


