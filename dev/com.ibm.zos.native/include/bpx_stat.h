/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2011
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
#ifndef _BBOZ_BPX_STAT_H
#define _BBOZ_BPX_STAT_H

#include "gen/bpxystat.h"

/**
 * This version of stat will return 0 on success, and fill in the
 * info struct.  On failure it will return the errno.  This is similar
 * to the posix stat() function.
 *
 * @param pathname The pathname to the file to check.
 * @param info_p A stat info struct to be filled in.
 *
 * @return 0 on success, errno on failure.
 */
int stat(char* pathname, struct stat* info_p);

#endif
