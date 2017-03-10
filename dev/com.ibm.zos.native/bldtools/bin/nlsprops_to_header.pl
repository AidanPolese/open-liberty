#!/usr/bin/perl -w
#
# IBM Confidential
#
# OCO Source Materials
#
# Copyright IBM Corp. 2011
#
# The source code for this program is not published or otherwise divested
# of its trade secrets, irrespective of what has been deposited with the
# U.S. Copyright Office.
#
use File::Basename;

#------------------------------------------------------------------------------
# Open the input and output files
#------------------------------------------------------------------------------
$inputFile = $ARGV[0];
$outputFile = $ARGV[1];

# Open the input and output files
open(NLSPROPS, "<", $inputFile) || die "Unable to open $inputFile for input";
open(HEADER, ">", $outputFile) || die "Unable to open $outputFile for output";

#------------------------------------------------------------------------------
# Read the .nlsprops file into a hash skipping comments and merging multi-line
# values.
#------------------------------------------------------------------------------
# Clear the messageData hash
%messageData = ();
%messageIds = ();

while (<NLSPROPS>) {
    chomp($_);                          # Get rid of the line feed

    # Ignore comments and empty lines
    if (/^\#/ || /^\s+$/) {
        next;
    }

    # Join lines that are continued by a backslash
    while (/\\$/) {
        s/\\$//g;
        $_ = $_ . <NLSPROPS>;
    }

    # Look for key = value patterns
    if (/^([^=]+)=(.*)/) {
        my $key = $1;
        my $value = $2;
        $messageData->{$key} = $value;

        if ($value =~ /^([A-Z]{4,5}\d{4,5}[ADEIW]):/) {
            $messageIds->{$1} = $key;
        }
    }
}

#------------------------------------------------------------------------------
# Top of header file comment
#------------------------------------------------------------------------------
format HEADER_BOILERPLATE =
/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. ^*
                       $year
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
^*
$ifndef
^*
$define

/**
 * @file
  '@'
 * ^<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
   $generatedComment
 * ^<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<~~
   $generatedComment
 */

.

#------------------------------------------------------------------------------
# Comment containing the source file name to add as a comment.
#------------------------------------------------------------------------------
$guardName = basename($inputFile);
$guardName =~ tr/a-z/A-Z/;
$guardName =~ s/\./\_/g;
$generatedComment = "This file is generated from $inputFile during the build and should not be modified by hand.";
$ifndef = "\#ifndef $guardName";
$define = "\#define $guardName";
$year = `date +%Y`;

#------------------------------------------------------------------------------
# Print the boilerplate
#------------------------------------------------------------------------------
$ofh = select(HEADER);
$~ = "HEADER_BOILERPLATE";
write(HEADER);


#------------------------------------------------------------------------------
# Format to use for the meat of the header
#------------------------------------------------------------------------------
format HEADER =
/**
 * Message text associated with ID @c ^*.
                                  '@', $messageId
 *
 * @explanation ^<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
  '@',          $explanation
 *              ^<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<~~
                $explanation
 * @useraction ^<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
  '@',         $userAction
 *             ^<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<~~
               $userAction
 */
^* ^* ^*
$define, $key, "\"$message\""

.

$ofh = select(HEADER);
$~ = "HEADER";

#------------------------------------------------------------------------------
# Iterate over the hash keys (ignoring the explanation and user action keys)
# and generate the main section of the header.
#------------------------------------------------------------------------------
#for (keys %$messageData) {
for (sort keys %$messageIds) {
    $define = "\#define";
    $messageId = $_;
    $key = $messageIds->{$messageId};
    $message = $messageData->{$key};
    $explanation = $messageData->{"$key.explanation"};
    $userAction = $messageData->{"$key.useraction"};

    $key =~ tr/a-z/A-Z/;                              # Force the message key to upper case
    $key =~ tr/\./_/;                                 # Replace dot separators with underscores
    $message =~ s/\{\d+\}/\%s/g;                      # Replace fillins with %s
    $message =~ s/\"/\\\"/g;                          # Escape double quotes
    $message =~ s/^([A-Z]{4,5}\d{4,5}[ADEIW]):/$1/g;  # Remove colon from message ID
    $messageId = $message;
    $messageId =~ s/^([A-Z]{4,5}\d{4,5}[ADEIW]).*/$1/;

    write (HEADER);
}

#------------------------------------------------------------------------------
# Add the endif
#------------------------------------------------------------------------------
print HEADER "\#endif\n";
