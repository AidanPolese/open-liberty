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

/**
 * @file
 *
 * Functions related to dumping native structures in the server process.
 */
#include <jni.h>
#include <stdarg.h>
#include <stdlib.h>

#include "include/ras_tracing.h"
#include "include/server_jni_method_manager.h"
#include "include/server_native_service_tracker.h"
#include "include/server_process_data.h"

#define RAS_MODULE_CONST  RAS_MODULE_SERVER_DUMP_FUNCTIONS_JNI

#define TP_GETPGOO_ENTRY                      1
#define TP_GETPGOO_EXIT                       2


//---------------------------------------------------------------------
// JNI function declaration and export
//---------------------------------------------------------------------
#pragma export(ntv_getPGOO)
JNIEXPORT jlong JNICALL
ntv_getPGOO(JNIEnv* env, jobject jobj);


//---------------------------------------------------------------------
// JNI native method structure for the NativeIntrospection methods
//---------------------------------------------------------------------
#pragma convert("ISO8859-1")
static const JNINativeMethod nativeIntrospectionMethods[] = {
    { "ntv_getPGOO",
      "()J",
      (void *) ntv_getPGOO }
};
#pragma convert(pop)

//---------------------------------------------------------------------
// NativeMethodDescriptor for the NativeIntrospection
//---------------------------------------------------------------------
#pragma export(zJNI_com_ibm_ws_zos_core_diagnostics_NativeIntrospection)
NativeMethodDescriptor zJNI_com_ibm_ws_zos_core_diagnostics_NativeIntrospection = {
   .registrationFunction = NULL,
   .deregistrationFunction = NULL,
   .nativeMethodCount = sizeof(nativeIntrospectionMethods) / sizeof(nativeIntrospectionMethods[0]),
   .nativeMethods = nativeIntrospectionMethods
};

/**
 * Get the address of the server_process_data (PGOO)
 */
JNIEXPORT jlong JNICALL
ntv_getPGOO(JNIEnv* env, jclass jobj) {

    server_process_data * localPGOO_Ptr = 0;

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(TP_GETPGOO_ENTRY),
                    "ntv_getPGOO entry",
                    TRACE_DATA_END_PARMS);
    }

    //-------------------------------------------------------------------------
    // Drive unauthorized routine to get the PGOO address
    //-------------------------------------------------------------------------
    const server_unauthorized_function_stubs* unauth_stubs_p = NULL;
    unauth_stubs_p = getServerUnauthorizedFunctionStubs();

    if (unauth_stubs_p) {
        const server_process_data ** localPGOO_PtrPtr = (const server_process_data **) &localPGOO_Ptr;
        unauth_stubs_p->getServerProcessDataUnauth(localPGOO_PtrPtr);
    }

    if (TraceActive(trc_level_detailed)) {
      TraceRecord(trc_level_detailed,
                  TP(TP_GETPGOO_EXIT),
                  "ntv_getPGOO exit",
                  TRACE_DATA_PTR(
                      localPGOO_Ptr,
                      "localPGOO_Ptr"),
                  TRACE_DATA_END_PARMS);
    }

    return (long)localPGOO_Ptr;
}
