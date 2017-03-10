#!/bin/sh
export _BPX_SHAREAS=NO
export _BPXK_AUTOCVT=ON
export JAVA_HOME=/usr/lpp/java/J6.0.1_64
export LANG=C
export LIBPATH=/lib:/usr/lib:$HOME/lib:.
export PATH=$HOME/bin:$JAVA_HOME/bin:$PATH:/bin:/usr/bin:/usr/local/bin

umask 027

echo "gmake $*"
/u/sykesm/bin/gmake $*
if [ $? -ne 0 ]; then
    echo "*** gmake encountered an error during remote build ***"
fi

