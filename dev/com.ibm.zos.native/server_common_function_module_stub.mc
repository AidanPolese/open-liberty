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
#include <metal.h>

#include "include/angel_client_pc_stub.h"
#include "include/server_common_function_module_stub.h"

//---------------------------------------------------------------------
// Stub routine implementations.
//---------------------------------------------------------------------
#define COMMON_DEF(svc_name, auth_name, impl_name, arg_type)        \
int impl_name##_stub (void* bindToken_p, unsigned long long fcnIndex, arg_type *p1, AngelAnchor_t* angelAnchor_p) { \
    int arg_struct_size = sizeof(arg_type);                         \
    return angelClientInvokeStub((LibertyBindToken_t*)bindToken_p, fcnIndex, arg_struct_size, p1, angelAnchor_p); \
}
#include "include/server_common_functions.def"
#undef COMMON_DEF

#pragma insert_asm(" IEANTASM")
