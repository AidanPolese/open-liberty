/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2012
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
#ifndef _BBOZ_SERVER_COMMON_FUNCTION_MODULE_STUB_H
#define _BBOZ_SERVER_COMMON_FUNCTION_MODULE_STUB_H

//---------------------------------------------------------------------
// Include definitions (structs, etc) used by COMMON_DEF
//---------------------------------------------------------------------
#define COMMON_DEF_INCLUDES
#include "server_common_functions.def"
#undef COMMON_DEF_INCLUDES

//---------------------------------------------------------------------
// Declare function prototypes for common stubs.
//---------------------------------------------------------------------
#define COMMON_DEF(svc_name, auth_name, impl_name, arg_type) \
int impl_name##_stub (void* bindToken_p, unsigned long long fcnIndex, arg_type *p1, AngelAnchor_t* angelAnchor_p);
#include "server_common_functions.def"
#undef COMMON_DEF

#endif
