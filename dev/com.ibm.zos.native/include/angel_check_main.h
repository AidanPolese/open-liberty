/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2016
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
#ifndef _BBGZ_ANGEL_CHECK_H
#define _BBGZ_ANGEL_CHECK_H

/**
 * Check whether the named angel (or the default angel 
 * when angel_name=NULL) is active.
 *
 * @param angel_name The name of the angel to look up, or
 *                   NULL for the default angel
 */
int angel_check(char* angel_name);

#endif
