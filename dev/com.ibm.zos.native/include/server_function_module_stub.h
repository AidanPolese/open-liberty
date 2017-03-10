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
#ifndef _BBOZ_SERVER_FUNCTION_MODULE_STUB_H
#define _BBOZ_SERVER_FUNCTION_MODULE_STUB_H

//---------------------------------------------------------------------
// Include definitions (structs, etc) used by AUTH_DEF and UNAUTH_DEF
//---------------------------------------------------------------------
#define UNAUTH_DEF_INCLUDES
#include "server_unauthorized_functions.def"
#undef UNAUTH_DEF_INCLUDES

#define AUTH_DEF_INCLUDES
#include "server_authorized_functions.def"
#undef AUTH_DEF_INCLUDES

//---------------------------------------------------------------------
// Macros for declaring OS linkage stubs with custom prolog code
//---------------------------------------------------------------------
#define LINKAGE_T(x) PRAGMA(linkage(x##_t,OS))
#define LINKAGE_STUB(x) PRAGMA(linkage(x##_stub,OS))
#define PROLOG_STUB(x) PRAGMA(prolog(x##_stub,"SUAUTHPR"));PRAGMA(epilog(x##_stub,"SUAUTHEP"))
#define PRAGMA(x) _Pragma(#x)

//---------------------------------------------------------------------
// Create stub prototypes for the unauthorized functions
//---------------------------------------------------------------------
// Hard coded here by defect 61857. If we ever get a fix for BPX4IPT giving us a 144
// byte save area, uncomment the line in server_unauthorized_functions.def and
// delete the following line.
_Pragma("linkage(driveAuthorizedServiceOnIPTExitRoutine_t,OS)") ; typedef int driveAuthorizedServiceOnIPTExitRoutine_t (DriveAuthorizedServiceOnIPTParms_t* parms_p) ; _Pragma("linkage(driveAuthorizedServiceOnIPTExitRoutine_stub,OS)") ; int driveAuthorizedServiceOnIPTExitRoutine_stub (DriveAuthorizedServiceOnIPTParms_t* parms_p) ;
#define UNAUTH_DEF(ret_type, func_name, parms_with_type, parms) \
LINKAGE_T( func_name ) ; \
typedef ret_type func_name##_t parms_with_type ; \
LINKAGE_STUB( func_name ) ; \
ret_type func_name##_stub parms_with_type ;
#include "server_unauthorized_functions.def"
#undef UNAUTH_DEF


//---------------------------------------------------------------------
// Create stub prototypes for the authorized functions
//---------------------------------------------------------------------
#define AUTH_DEF(svc_name, auth_name, impl_name, arg_type) \
LINKAGE_T( impl_name ) ; \
typedef int impl_name##_t (arg_type *p1);
#include "server_authorized_functions.def"
#undef AUTH_DEF


//---------------------------------------------------------------------
// Declare structures that provide access to stub implementations
// that are compatible with the target.
//---------------------------------------------------------------------

/**
 * Stubs to authorized metal C functions that are available to the server.
 */
typedef struct server_authorized_function_stubs {
#define AUTH_DEF(svc_name, auth_name, impl_name, arg_type) impl_name##_t* impl_name;
#include "server_authorized_functions.def"
#undef AUTH_DEF
} server_authorized_function_stubs;

/**
 * Stubs to unauthorized metal C functions that are available to the server.
 */
typedef struct server_unauthorized_function_stubs {
    void* header;
#define UNAUTH_DEF(ret_type, func_name, parms_with_type, parms) func_name##_t* func_name;
#include "server_unauthorized_functions.def"
#undef UNAUTH_DEF
    // Hard coded here by defect 61857. If we ever get a fix for BPX4IPT giving us a 144
    // byte save area, uncomment the line in server_unauthorized_functions.def and
    // delete the following line.
    driveAuthorizedServiceOnIPTExitRoutine_t* driveAuthorizedServiceOnIPTExitRoutine;
    void* trailer;
} server_unauthorized_function_stubs;

/**
 * Stubs to the authorized and unauthorized metal C functions that are
 * available to the server.
 */
typedef struct server_function_stubs {
    const server_authorized_function_stubs*   authorized_p;
    const server_unauthorized_function_stubs* unauthorized_p;
} server_function_stubs;

#endif
