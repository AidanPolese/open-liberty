#!/bin/bash
#
# This script is kicked off by the "ezwas install" make target (from the C/C++ perspective in RTC).
# It FTPs from your AQ sandbox all <rtc-sandbox-dir>/dist/install*.sh scripts to the given EZWAS host.
# Then it executes those scripts on the EZWAS host.
#
# Note: you must setup ssh auto-login from your RTC workstation to your EZWAS system.  This is done
# by copying your ssh public key (~/.ssh/id_rsa.pub) into /u/MSTONE1/.ssh/authorized_keys on the EZWAS host.
# You can verify that it works by trying to ssh to the EZWAS host as user mstone1 (you should not be prompted
# for a password): 
#
#   $ ssh mstone1@<ezwas-host>  
#

LOCAL_DIR=$1
REMOTE_HOST=$2
REMOTE_DIR=$3
EZWAS_HOSTNAME=$4

export PATH="$PATH":/bin:/usr/local/bin:/usr/bin

# Issue an error message and exit the script
function error {
    echo "$1" >&2
    exit $2
}

cat <<HERE
LOCAL_DIR is "$LOCAL_DIR"
REMOTE_HOST is "$REMOTE_HOST"
REMOTE_DIR is "$REMOTE_DIR"
EZWAS_HOSTNAME is "$EZWAS_HOSTNAME"

!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! NOTE !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

In order to use this script, you must: 

1) Define ezwas.hostname in RTC under Preferences -> C/C++ -> Build Variables
2) Setup ssh auto-login from your RTC workstation to your ezwas system:
   Copy+paste your public key (~/.ssh/id_rsa.pub) into /u/MSTONE1/.ssh/authorized_keys

This script will copy dist/install*.sh scripts from your AQ sandbox to your
EZWAS machine, then execute them on the EZWAS machine.

!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! ^^^^ !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

HERE

case "`uname`" in
    CYGWIN*) cygwin=true;;
esac

if [ ! -z $cygwin ]; then
    LOCAL_DIR=`cygpath -u "$LOCAL_DIR"`
fi

if [ -z "$LOCAL_DIR" ]; then
    error "Local directory is required" -8 
fi

if [ ${LOCAL_DIR:0:1} == "-" ]; then
    error "Local directory starts with '-'.  Are you sure that's what you want?" -8
fi

if [ -z "$REMOTE_HOST" ]; then
    error "Remote host is required" -8
fi

if [ ${REMOTE_HOST:0:1} == "-" ]; then
    error "Remote host starts with '-'.  Are you sure that's what you want?" -8
fi

if [ -z "$REMOTE_DIR" ]; then
    error "Remote dir is required" -8
fi

if [ ${REMOTE_DIR:0:1} == "-" ]; then
    error "Remote dir starts with '-'.  Are you sure that's what you want?" -8
fi

if [ -z "$EZWAS_HOSTNAME" ]; then
    error "Must define ezwas.hostname under Preferences -> C/C++ -> Build Variables" -8
fi

echo "Starting ezwas install..."

# remove old install scripts
ssh "mstone1@$EZWAS_HOSTNAME" rm .wlp-ezwas-ftp/*

echo "Transferring install scripts from AQ sandbox..."
echo "$REMOTE_HOST: $REMOTE_DIR/bldtools/bin/ezwas-ftp.sh $EZWAS_HOSTNAME $REMOTE_DIR/dist/install*.sh"
ssh -q "$REMOTE_HOST" "$REMOTE_DIR/bldtools/bin/ezwas-ftp.sh" $EZWAS_HOSTNAME "$REMOTE_DIR/dist/install*.sh"

echo "Executing install scripts on EZWAS system..."

# execute only the scripts we know about
for x in install.sh install-unit-test.sh; do
    ssh "mstone1@$EZWAS_HOSTNAME" "if [ -e .wlp-ezwas-ftp/$x ]; then chmod a+x .wlp-ezwas-ftp/$x; eval .wlp-ezwas-ftp/$x; fi"
done

echo "Tagging wlp text files and scripts as ASCII and executable..."

# If we're installing directly from our AQ sandbox then it's likely 
# we're only installing the native DLLs.  The rest of the installation
# came from build.image/output/wlp-2014xxx.zip in our RTC sandbox.
# The various text files and scripts in this zip are not tagged as ASCII
# files for use on z/OS, so let's tag them now.
ssh "mstone1@$EZWAS_HOSTNAME" "find wlp -name '*.xml' -o -name '*.TXT' -o -name '*.txt' -o -name '*.properties' -o -name '*.mf' -o -name '*.bat' -o -name '*.py' -o -name 'README' | xargs chtag -t -c iso8859-1"

# All *.bat scripts in the wlp installation have a companion script (same name, 
# sans the ".bat") for use on non-WINDOWS systems.  Tag all those ASCII as well
# (unless they've already been tagged, which would be the case if they were installed
# from a wlp image properly built for z/OS (and in that case, they may have been 
# converted to and tagged EBCDIC, so we definitely don't want to tag them ASCII)).
ssh "mstone1@$EZWAS_HOSTNAME" "find wlp -name '*.bat' | sed 's/.bat$//' | xargs chtag -p | grep untagged | while read a b c d; do chtag -t -c iso8859-1 \$d; done; chmod -R a+x wlp/bin"


echo "Completed ezwas install"
