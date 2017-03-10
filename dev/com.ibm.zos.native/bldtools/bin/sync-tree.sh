#!/bin/bash

export PATH=/usr/local/bin:$PATH:/bin:/usr/bin

# Issue an error message and exit the script
function error {
    echo "$1" >&2
    exit $2
}

LCD="$1"
HOST="$2"
RCD="$3"

if [ -z "$LCD" ]; then
    error "Local directory is required" -8 
fi

if [ -z "$HOST" ]; then
    error "Remote host is required" -8
fi

if [ -z "$RCD" ]; then
    error "Remote dir is required" -8
fi

# These need to match the shipped modules
TARGET_EXCLUDES=" \
--exclude-glob batchManagerZos \
--exclude-glob bbgzadrm \
--exclude-glob bbgzafsm \
--exclude-glob bbgzangl \
--exclude-glob bbgzachk \
--exclude-glob bbgzcsl \
--exclude-glob bbgzlcmc \
--exclude-glob bbgznut \
--exclude-glob bbgznut_lec \
--exclude-glob bbgznut_lecpp \
--exclude-glob bbgzsafm \
--exclude-glob bbgzscan \
--exclude-glob bbgzscfm \
--exclude-glob bbgzscfms \
--exclude-glob bbgzshrc \
--exclude-glob bbgzsrv \
--exclude-glob bbgzsufm \
--exclude-glob bbgzzfat \
--exclude-glob bboacall \
--exclude-glob bboacntl \
--exclude-glob bboacsrv \
--exclude-glob bboaclnk \
--exclude-glob bboatrue \
--exclude-glob bboaiemi \
--exclude-glob bboaiemt \
--exclude-glob bboa1???"

lftp -c "
set ftp:list-options -a;
set ftp:list-empty-ok yes;
set mirror:parallel-directories true;
open $HOST;
lcd $LCD;
cd $RCD;
echo Tranferring ASCII files... ;
mirror --reverse \
       --ascii \
       --delete \
       --depth-first \
       --delete-first \
       --verbose=1 \
       --parallel=8 \
       --exclude compat \
       --exclude dist \
       --exclude image-staging-dir \
       --exclude im_install_temporary_extract_directory \
       --exclude include/gen \
       --exclude build/ \
       --include dsects/* \
       --exclude-glob *~ \
       --exclude-glob resources/com/ibm/ws/zos/*.cat \
       --exclude-glob resources/com/ibm/ws/zos/*.h \
       --exclude-glob *.o \
       --exclude-glob *.o.twas \
       --exclude-glob *.so \
       --exclude-glob .*.swp \
       --exclude-glob *.x \
       --exclude-glob *.alst \
       --exclude-glob *.clst \
       --exclude-glob *.llst \
       --exclude-glob *.maplst \
       --exclude-glob *.scanc \
       --exclude-glob .*project \
       --exclude-glob .DS_Store \
       --exclude-glob .deps/ \
       --exclude-glob .jazzignore \
       --exclude-glob .svn/ \
       --exclude-glob .settings/ \
       --exclude-glob tags \
       $TARGET_EXCLUDES ;
echo Transferring binary files... ;
mirror --reverse \
       --delete \
       --depth-first \
       --delete-first \
       --verbose=1 \
       --parallel=8 \
       --include-glob *.o.twas \
       server server "

lftp_rc=$?
if [ $lftp_rc -eq 0 ]; then
    echo "Copy completed successfully."
else
    echo "Copy failed!"
fi

exit $lftp_rc
