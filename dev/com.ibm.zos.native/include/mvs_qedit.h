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
#ifndef _BBOZ_MVS_QEDIT_H
#define _BBOZ_MVS_QEDIT_H

#include "gen/iezcib.h"
#include "gen/iezcom.h"

/**
 * Frees a CIB from the chain using QEDIT.
 *
 * @param com_ptr A pointer to the COM structure returned from EXTRACT.
 * @param cib_to_free_p A pointer to the CIB control block that is to be freed.
 */
int free_cib_from_chain(iezcom* com_ptr, void* cib_to_free_p);

/**
 * Sets a limit on the number of simultaneous commands that can be processed.
 *
 * @param com_ptr A pointer to the COM structure returned from EXTRACT.
 * @param limit The maximum number of simultaneous commands (usually 1).
 */
int set_cib_limit(iezcom* com_ptr, int limit);

#endif
