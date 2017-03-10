#!/bin/bash

LOCAL_DIR=$1
REMOTE_HOST=$2
REMOTE_DIR=$3

export PATH="$PATH":/bin:/usr/local/bin:/usr/bin

# Issue an error message and exit the script
function error {
    echo "$1" >&2
    exit $2
}

echo
echo "LOCAL_DIR is \"$LOCAL_DIR\""
echo "REMOTE_HOST is \"$REMOTE_HOST\""
echo "REMOTE_DIR is \"$REMOTE_DIR\""
echo

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

shift 3

echo "Starting remote build process"
"$LOCAL_DIR"/bldtools/bin/sync-tree.sh "$LOCAL_DIR" "$REMOTE_HOST" "$REMOTE_DIR" && ssh -q "$REMOTE_HOST" "$REMOTE_DIR/bldtools/bin/remote-build.sh" -C "$REMOTE_DIR" $@
echo "Completed remote build process"
