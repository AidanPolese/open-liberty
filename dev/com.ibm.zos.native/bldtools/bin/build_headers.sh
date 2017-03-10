#!/bin/sh

# The 3 arguments here are the base Liberty directory and the directory which contains
# the header files to be generated along with the list of .nlsprops files
BASE_DIR=$1;
HEADER_DIR=$2;
shift 2;
NLSPROPS_TARGETS=$@;

# Loop over each target and generate the appropriate header file
for target in ${NLSPROPS_TARGETS}
do
        filename=`basename $target ".nlsprops"`;
        if [ \( ! -f $HEADER_DIR/${filename}_mc.h \) -o \( $HEADER_DIR/${filename}_mc.h -ot $target \) ]; then
        	$BASE_DIR/bldtools/bin/nlsprops_to_header.pl $target $HEADER_DIR/${filename}_mc.h;
        fi
done