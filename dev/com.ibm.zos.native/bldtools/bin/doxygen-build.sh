#!/bin/bash

# To make this work you need to install Doxygen and set an RTC
# variable to tell the script where you put it.
#
# For windows you can get doxygen from the IBM-supported-tools site, but this script only works
# if you get the cygwin version (back into setup.exe and find and install doxygen).
# If you insist on using the windows version you will need to add some code to 
# the script to convert the local dir.
#
#case "`uname`" in
#    CYGWIN*) cygwin=true;;
#esac

#if [ ! -z $cygwin ]; then
#    LOCAL_DIR=`cygpath -w $LOCAL_DIR`
#fi
#
# Then in RTC go to Window->Preferences->C/C++->Build Variables and click 'Add'
#     The variable name is 'Doxygen_Dir'
#     Set it to the path where you put Doxygen.
#
# On Windows it's likely:   'C:\Program Files'
# Under cygwin it's likely: '/usr/bin'
# On Mac OS X it's likely:  '/Applications/Doxygen.app/Contents/Resources'
# On Linux it's likely:     '/usr/bin'
# 
# Note that these variables seem to 'stick' so after setting the value you
# probably need to restart RTC# and if you edit the value you seem to have
# to restart also.  I'm sure there's a refresh-environment button somewhere.
#

LOCAL_DIR=$1
HOST="$2"
RCD="$3" 

shift 3

DOXYGEN_DIR=$@

export PATH="$PATH":/bin:/usr/local/bin:/usr/bin

# Issue an error message and exit the script
function error {
    echo "$1" >&2
    exit $2
}

echo
echo "LOCAL_DIR is \"$LOCAL_DIR\""
echo "DOXYGEN_DIR is \"$DOXYGEN_DIR\""
echo


if [ -z "$LOCAL_DIR" ]; then
    error "Local directory is required" -8 
fi

if [ -z "$HOST" ]; then
    error "Remote host is required" -8
fi

if [ -z "$RCD" ]; then
    error "Remote dir is required" -8
fi

if [ -z "$DOXYGEN_DIR" ]; then
    error "Doxygen_Dir must be set to generate doc" -8
fi

echo "Fetching generated .h files from host"
cd $LOCAL_DIR/include
if [ ! -x gen ]; then
  mkdir gen
fi

lftp -c "
set xfer:clobber 1;
open $HOST;
lcd $LOCAL_DIR/include/gen;
cd $RCD/include/gen;
mget -a *.h"

echo "checking for Doxygen at "$DOXYGEN_DIR

if [ -x "$DOXYGEN_DIR"/doxygen ]; then
    echo "Performing Doxygen Build"
    cd $LOCAL_DIR
    "$DOXYGEN_DIR"/doxygen "$LOCAL_DIR"/bldtools/config/Doxyfile
    echo "Doxygen Build Complete";
else
    error "doxygen not found in \"$DOXYGEN_DIR\"" -8
fi

