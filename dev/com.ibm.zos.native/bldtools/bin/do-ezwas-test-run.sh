#!/bin/bash
#
# This script is kicked off by the "test run" make target (from the C/C++ perspective in RTC).
#
# It SSH's into your ezwas system and submits the native unit test job, BBGZNUT.
#
# Note: you must run make targets 'test build' and 'ezwas install' before running 'test run'. 
#
# Note: you must setup ssh auto-login from your RTC workstation to your EZWAS system.  This is done
# by copying your ssh public key (~/.ssh/id_rsa.pub) into /u/MSTONE1/.ssh/authorized_keys on the EZWAS host.
# You can verify that it works by trying to ssh to the EZWAS host as user mstone1 (you should not be prompted
# for a password): 
#
#   $ ssh mstone1@<ezwas-host>  
#

EZWAS_HOSTNAME=$1

export PATH="$PATH":/bin:/usr/local/bin:/usr/bin



# Issue an error message and exit the script
function error {
    echo "$1" >&2
    exit $2
}

cat <<HERE
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

if [ -z "$EZWAS_HOSTNAME" ]; then
    error "Must define ezwas.hostname under Preferences -> C/C++ -> Build Variables" -8
fi

echo "Running metal-C unit tests (s BBGZNUT) on EzWAS system $EZWAS_HOSTNAME ..."
echo "(Note: you can check BBGZNUT's joblog in SDSF.  It will be dumped here to the console when it finishes)."
echo "(Note: don't forget to run 'ezwas install'!!  Typical flow would be: test build -> ezwas install -> test run)"
echo ""

ssh mstone1@$EZWAS_HOSTNAME 'export PATH="$PATH":/usr/local/bin; execsh exconcmd "s bbgznut"; sysout -o `sysout -s BBGZNUT | head -1`'

echo ""
echo "The remote SSH command returned. If you do not see 'Test Run Complete' in the output above"
echo "then the test run has not yet completed.  Check the BBGZNUT joblog in SDSF to see the full results."
echo ""
echo ""

echo "Running LE C unit tests (bbgznut_lec) on EzWAS system $EZWAS_HOSTNAME ..."
echo ""

# TODO: assumes wlp is at /u/MSTONE1/wlp
ssh mstone1@$EZWAS_HOSTNAME '/u/MSTONE1/wlp/lib/native/zos/s390x/bbgznut_lec'

echo ""
echo "The remote SSH command returned."

echo ""
echo ""
echo "Running LE C++ unit tests (bbgznut_lecpp) on EzWAS system $EZWAS_HOSTNAME ..."
echo ""

# TODO: assumes wlp is at /u/MSTONE1/wlp
ssh mstone1@$EZWAS_HOSTNAME '/u/MSTONE1/wlp/lib/native/zos/s390x/bbgznut_lecpp'

echo ""
echo "The remote SSH command returned."


