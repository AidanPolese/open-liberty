#!/bin/sh
#
# FTP the given files to the given EZWAS host.
# 
# Files are transferred to directory ~MSTONE1/.wlp-ezwas-ftp/
#
# useage: ezwas-ftp.sh <ezwas-hostname> <files>
#

if [ -z $1 ]; then
    echo "ERROR: must define ezwas.hostname under Preferences -> C/C++ -> Build Variables"
    exit 1
fi

EZWAS_HOST=$1
shift
FILES=$*

if [ -z $FILES ]; then
    echo "ERROR: no files to transfer"
    exit 2
fi

echo "Transfering to $EZWAS_HOST: $FILES"

cat <<HERE | ftp -n -i
$EZWAS_HOST
quote USER mstone1
quote PASS m00ntest
mkdir .wlp-ezwas-ftp
cd .wlp-ezwas-ftp
mput $FILES
quit
HERE


