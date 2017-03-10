#!/bin/sh

# The directory containing the .nlsprops file is /%liberty_base%/resources
# Inside /%liberty_base%/resources/ can be an arbitrary number of .nlsprops files
#	in a nested file structure
BASE_DIR=$1;
NLSPROPS_DIR=$BASE_DIR/resources;
shift 1;
NLSPROPS_TARGETS=$@;

# Loop over each of the nlsprops, creating a message catlog for every one
# Note: Make sure to only generate the header on the first pass
for target in $NLSPROPS_TARGETS
do
	# Grab the path to the current target for use later
	CURRENT_DIR=`dirname $target`;
	
	stem=`echo $target | sed 's/\.nlsprops//'`;
	
	# TODO: Fix this post-GA when the .nlsprops are available for update.
	# 
	# Overlay the .nlsprops file with the .props file so the message catalog is generated 
	# using the native English only properties file which is a superset of the .nlsprops file.
	# This overlay is temporary and should be removed when we are able to merge the .props 
	# into the .nlsprops file.  
	cp $stem.props $target

	# Process the nlsprops file for input into the mkcatdefs utility
	# This includes removing \uNNNN sequences and replacing with the correct character
	${BASE_DIR}/bldtools/bin/nlsprops_to_msgcat.pl $target $target.mkcat_input;

	# Run the generated file through mkcatdefs.  The if statement is used
	# to determine if a header file needs to be generated
	if [ ! -f "$CURRENT_DIR/*.h" ]
	then
        echo "generating ${target}.msg with header"
        mkcatdefs $stem $target.mkcat_input > $target.msg
        cp ${CURRENT_DIR}/*.h ${BASE_DIR}/include/gen/         
	else
        echo "generating ${target}.msg with no header"
        mkcatdefs -h $stem $target.mkcat_input > $target.msg		               
	fi

	# Take the mkcatdefs output and run it through gencat
	gencat $stem.cat $target.msg	
	
done

# Cleanup temporary files used to feed the scripts and utilities
find $NLSPROPS_DIR -name "*.fixed" -type f -exec rm {} \;
find $NLSPROPS_DIR -name "*.mkcat_input" -type f -exec rm {} \;
find $NLSPROPS_DIR -name "*.msg" -type f -exec rm {} \;
