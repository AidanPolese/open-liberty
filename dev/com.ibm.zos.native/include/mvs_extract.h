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
#ifndef _BBOZ_MVS_EXTRACT_H
#define _BBOZ_MVS_EXTRACT_H

#include "gen/iezcib.h"
#include "gen/iezcom.h"

/* Extract the command scheduler communications list from the TCB */
iezcom* extract_comm(void);

#endif
