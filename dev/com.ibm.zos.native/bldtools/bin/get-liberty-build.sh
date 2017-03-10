#!/bin/sh

#
# GSA related information - set in environment variables
#
#LIBERTY_FTP_SERVER=rtpgsa.ibm.com
#LIBERTY_BUILDS_PATH=/projects/l/liberte/builds/85o
#LIBERTY_ZIP_STEM=wlp

#
# Retrieve the latest zip file
#
get_liberty_zip () {
ftp $LIBERTY_FTP_SERVER << END_GET_SCRIPT 2>/dev/null
cd $LIBERTY_BUILDS_PATH/$ftpdir
lcd $name
bin
get $liberty_zip
quit
END_GET_SCRIPT
}

#
# Bail out quickly if a .netrc doesn't exist.
#
if [ ! -e ~/.netrc ]; then
    echo "Please setup your ~/.netrc file!"
    echo "  touch ~/.netrc"
    echo "  chmod 600 ~/.netrc"
    echo "  echo \"machine rtpgsa.ibm.com login [gsa_user_id] password [gsa_password]\" >> ~/.netrc"
    exit -1;
fi

# Local defaults
staging_dir=./image-staging-dir

# default package to build
pkg='liberty'

# default versions
beta_version=''
service_version=''

#
# Process the command line argument
#
while getopts "d:a:b:s:" arg;
do
    case $arg in
        d) staging_dir="$OPTARG";;
        a) pkg="$OPTARG";;
        b) beta_version="$OPTARG";;
        s) service_version="$OPTARG";;
        [?]) echo "Usage: $0 [-d staging_directory] [-a 'application1 [application2 ...]'] [-b beta_version] [-s service_version] [level] [ftpdir]"
            exit -1;;
    esac
done
shift $((OPTIND-1))

#
# We only support one level specification & the ftp location
#
if [ $# -gt 2 ]; then
    echo "Only one level & ftpdir specification is supported"
    exit -2;
fi

#
# Use the specified level if present, otherwise the newest
#
if [ $1 ]; then
    level="$1";
else
    level=`(echo cd $LIBERTY_BUILDS_PATH; echo ls; echo quit) | ftp $LIBERTY_FTP_SERVER | grep -E '^[0-9]{8}-[0-9]{4}' | sort | uniq | tail -1`
fi

if [ $2 ]; then
    ftpdir="$2";
else
    ftpdir=$level
fi

#
# Iterate through each of the packages passed in and
# download the zip file and unzip it into its own directory
#

# Clean the current staging area
rm -rf $staging_dir
mkdir $staging_dir

cd $staging_dir

for name in $pkg
do
  app=$name;
  appname=-$name;
  if [ $name = liberty ]; then
      app=;
      appname=;
  fi
  
  # use the service version unless beta or bluemix appear in the package name, then use the beta version
  version=;
  case $name in
      *beta*|*bluemix*) version=$beta_version;;
      *) version=$service_version;;
  esac

  mkdir $name

  liberty_zip=$LIBERTY_ZIP_STEM$appname-$level.zip
  get_liberty_zip

  # Sometimes the zip file doesn't exist out on the server
  # If it isn't available delete the directory it was going
  # to be unzipped in and issue a message.  The rest of the build process
  # doesn't tolerate empty directories
  if [ ! -e $name/$liberty_zip ]; then
      echo "Unable to download ftp://$LIBERTY_FTP_SERVER/$LIBERTY_BUILDS_PATH/$ftpdir/$liberty_zip"
      liberty_zip=$LIBERTY_ZIP_STEM$appname-$version.zip
      echo "Trying $liberty_zip..."
      get_liberty_zip
      if [ ! -e $name/$liberty_zip ]; then
          echo "Unable to download ftp://$LIBERTY_FTP_SERVER/$LIBERTY_BUILDS_PATH/$ftpdir/$liberty_zip"
          rm -r $name
      fi
  fi
done

cd ..
# vim: filetype=sh
