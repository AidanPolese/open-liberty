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
#include "include/angel_client_pc.h"
#include "include/angel_dynamic_replaceable_module.h"
#include "include/angel_functions.h"
#include "include/angel_server_pc.h"

const struct bbgzadrm BBGZADRM = {
    .eyecatcher          = "BBGZADRM",
    .build_date          = BUILD_DATE_STAMP,
    .build_time          = BUILD_TIME_STAMP,
    .initialize          = BBGZDRM_Init,
    .run                 = BBGZDRM_Run,
    .cleanup             = BBGZDRM_UnInit,
    .reinitialize        = BBGZDRM_ReInit,
    .getVersionString    = getDynamicReplaceableVersionString,
    .getVersionNumber    = getDynamicReplaceableVersionInt,
    ._angelReserved7     = NULL,
    ._angelReserved8     = NULL,
    ._angelReserved9     = NULL,
    ._angelReserved10    = NULL,
    ._angelReserved11    = NULL,
    ._angelReserved12    = NULL,
    ._angelReserved13    = NULL,
    ._angelReserved14    = NULL,
    ._angelReserved15    = NULL,
    .verInfo.minorVersion = BBGZ_DYN_MODULE_CODE_MINOR_VERSION,
    .verInfo._available   = 0,
    .verInfo.length       = sizeof(struct bbgzadrm),
    .resmgr              = dynamicReplaceableRESMGR,
    .register_pc         = dynamicReplaceablePC_Register,
    .invoke_pc           = dynamicReplaceablePC_Invoke,
    .deregister_pc       = dynamicReplaceablePC_Deregister,
    .arr                 = dynamicReplaceableARR,
    .clientBind_pc       = dynamicReplaceablePC_ClientBind,
    .clientInvoke_pc     = dynamicReplaceablePC_ClientInvoke,
    .clientUnbind_pc     = dynamicReplaceablePC_ClientUnbind,
    .clientArr           = dynamicReplaceableClientARR,
    .clientResmgr        = dynamicReplaceableClientRESMGR

};
