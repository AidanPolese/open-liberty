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
#ifndef _BBOZ_ANGEL_FIXED_SHIM_RESMGR_H
#define _BBOZ_ANGEL_FIXED_SHIM_RESMGR_H
/** @file
 * Defines the Angel Fixed Shim ResManager function
 */
#include "mvs_resmgr.h"


/**
 * The RESMGR stub client processes.  This RESMGR delegates to the RESMGR in
 * the current dynamic replaceable module.
 *
 * @param rmpl_p A pointer to the RMPL control block provided by MVS.
 * @param user_p The 8 byte user token provided when the RESMGR was
 *               registered.
 */
void fixedShimClientRESMGR(rmpl* rmpl_p, long long* user_p);

/**
 * The RESMGR stub for angel and server processes.  This RESMGR delegates
 * to the RESMGR in the current dynamic replaceable module.
 *
 * @param rmpl_p A pointer to the RMPL control block provided by MVS.
 * @param user_p The 8 byte user token provided when the RESMGR was
 *               registered.
 */
void fixedShimRESMGR(rmpl* rmpl_p, long long* user_p);

#endif
