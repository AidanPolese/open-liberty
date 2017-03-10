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
#include <stdlib.h>
#include "include/angel_fixed_shim_module.h"
#include "include/angel_fixed_shim_pc.h"
#include "include/angel_fixed_shim_resmgr.h"
#include "include/mvs_user_token_manager.h"

const struct bbgzafsm BBGZAFSM = {
    .eyecatcher                 = "BBGZAFSM",
    .build_date                 = BUILD_DATE_STAMP,
    .build_time                 = BUILD_TIME_STAMP,
    .getVersionString           = getFixedShimVersionString,
    .getVersionNumber           = getFixedShimVersionInt,
    .associatedRecoveryRoutine  = fixedShim_ARR,
    .register_pc_stub           = fixedShimPC_Register,
    .invoke_pc_stub             = fixedShimPC_Invoke,
    .deregister_pc_stub         = fixedShimPC_Deregister,
    .client_bind_stub           = fixedShimPC_clientBind,
    .client_invoke_stub         = fixedShimPC_clientInvoke,
    .client_unbind_stub         = fixedShimPC_clientUnbind,
    ._reserved_PC7              = 0, /* Indicates version info to follow */
    .v1_verInfo.version         = 1, /* Version of this struct, not module */
    .v1_verInfo._available      = 0,
    .v1_verInfo.length          = sizeof(struct bbgzafsm), /* size of struct, not module */
    .resmgr_stub                = fixedShimRESMGR,
    .client_resmgr_stub         = fixedShimClientRESMGR,
    .minorVersion               = BBGZ_FIXED_SHIM_MODULE_CODE_MINOR_VERSION,
    .available                  = ""
};

const UserTokenBias_t CLIENT_SGOO_BIAS = {
    ._rsvd = 0,
    .bias = 1
};
