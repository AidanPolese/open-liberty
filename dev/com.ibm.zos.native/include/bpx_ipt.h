/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2014
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
#ifndef _BBOZ_BPX_IPT_H
#define _BBOZ_BPX_IPT_H

#include "gen/ikjtcb.h"

#include "common_defines.h"

/**
 * Gets the TTOKEN for the IPT.  We use this to identify the process.  We would
 * use BPX4GPI (getPid()) but we can't call this when we are under the BPX4IPT
 * exit or we'll take a EC6 abend.
 *
 * @param ttoken_p A pointer to an area where the IPT's ttoken should be copied.
 *
 * @return 0 if the IPT ttoken was obtained and copied into the caller's area.
 *         non-zero if the IPT could not be located in the parent task chain.
 */
int getIPT_TToken(TToken* ttoken_p);

/**
 * Looks in the current task's TCB chain to find the initial pthread creating
 * task (IPT), and if found, returns a reference to it.
 *
 * @return The address of the TCB which is the IPT, if the IPT is found in the
 *         parent chain of the calling TCB.
 */
tcb* getIPTandVerifyCallerIsRelated(void);

#endif
