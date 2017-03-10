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
#ifndef _BBOZ_MVS_WAIT_H
#define _BBOZ_MVS_WAIT_H

/**
 * MVS WAIT
 *
 * @param wait_ecb_p A pointer to the ECB to wait on.
 *
 * @return the completion code
 */
int wait(void* __ptr32 wait_ecb_p);

/**
 * MVS WAIT on List of ECBs, last in list has HOB on to terminate the list.
 *
 * @param waitlist_p The waitlist
 *
 * @return the completion code
 */
int waitlist (void * __ptr32 waitlist_p);

/**
 * MVS POST
 *
 * @param post_ecb_p A pointer to the ECB to wait on.
 * @param comp_code The completion cod eto post with.
 *
 * @return Return code from the POST macro
 */
int post(void* __ptr32 post_ecb_p, int comp_code);

#endif
