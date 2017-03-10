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


#include <assert.h>
#include <dlfcn.h>
#include <errno.h>
#include <jni.h>
#include <stdlib.h>

#include "include/ras_tracing.h"
#include "include/server_function_module_stub.h"
#include "include/server_jni_method_manager.h"
#include "include/server_kernel_common.h"
#include "include/server_native_service_tracker.h"
#include "include/util_jni.h"

#define RAS_MODULE_CONST  RAS_MODULE_SERVER_KERNEL_FUNCTIONS_JNI


#define _TP_SERVER_KERNEL_ENTER_CLEANUPACTIVATE    1
#define _TP_SERVER_KERNEL_EXIT_CLEANUPACTIVATE     2
#define _TP_SERVER_KERNEL_ENTER_CLEANUPDEACTIVATE  3
#define _TP_SERVER_KERNEL_EXIT_CLEANUPDEACTIVATE   4


//---------------------------------------------------------------------
// JNI function declaration and export
//---------------------------------------------------------------------
#pragma export(ntv_hardFailureCleanupActivate)
JNIEXPORT jint JNICALL ntv_hardFailureCleanupActivate(
    JNIEnv* env,
    jclass thisClass);

#pragma export(ntv_hardFailureCleanupDeactivate)
JNIEXPORT jint JNICALL ntv_hardFailureCleanupDeactivate(
    JNIEnv* env,
    jclass thisClass);

// ---------------------------------------------------------------------
// JNI native method structure for the HardFailureNativeCleanup methods
// ---------------------------------------------------------------------
#pragma convert("ISO8859-1")
static const JNINativeMethod hardFailureNativeCleanupMethods[] = {
    { "ntv_hardFailureCleanupActivate",
      "()I",
      (void*) ntv_hardFailureCleanupActivate },
    { "ntv_hardFailureCleanupDeactivate",
      "()I",
      (void*) ntv_hardFailureCleanupDeactivate }
};
#pragma convert(pop)

// ---------------------------------------------------------------------
// NativeMethodDescriptor for the HardFailureNativeCleanup
// ---------------------------------------------------------------------
#pragma export(zJNI_com_ibm_ws_zos_core_internal_HardFailureNativeCleanup)
NativeMethodDescriptor zJNI_com_ibm_ws_zos_core_internal_HardFailureNativeCleanup = {
    .registrationFunction = NULL,
    .deregistrationFunction = NULL,
    .nativeMethodCount = sizeof(hardFailureNativeCleanupMethods) / sizeof(hardFailureNativeCleanupMethods[0]),
    .nativeMethods = hardFailureNativeCleanupMethods
};


/**
 * Mark this thread as a special hard failure cleanup thread.  Our task-level resmgr will
 * now recognize that if this thread goes through task termination "marked" then it will
 * drive any registered cleanup routines in hardfailureCleanupRegistry in the
 * server_process_data.
 *
 * @return int indication of success of native processing.
 */
JNIEXPORT jint JNICALL
ntv_hardFailureCleanupActivate(
    JNIEnv* env,
    jclass thisClass
) {

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(_TP_SERVER_KERNEL_ENTER_CLEANUPACTIVATE),
                    "Entered ntv_hardFailureCleanupActivate()",
                    TRACE_DATA_END_PARMS);
    }

    int  rc = 0;
    KERNEL_HardFailureCleanupActivateParms kernelCleanupActivateParms = {
        .returnCode   = &rc
    };

    const struct server_authorized_function_stubs* auth_stubs_p =
             (server_authorized_function_stubs*) getServerAuthorizedFunctionStubs();

    auth_stubs_p->hardFailureCleanupActivate(&kernelCleanupActivateParms);


    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(_TP_SERVER_KERNEL_EXIT_CLEANUPACTIVATE),
                    "Exit ntv_hardFailureCleanupActivate()",
                    TRACE_DATA_INT(rc, "rc"),
                    TRACE_DATA_END_PARMS);
    }

    return rc;
}

/**
 * UnMark this thread as the HardFailure cleanup thread.  Our task-level resmgr will now only
 * perform normal thread cleanup activities for this thread.
 *
 * @return int indication of success of native processing.
 */
JNIEXPORT jint JNICALL
ntv_hardFailureCleanupDeactivate(
    JNIEnv* env,
    jclass thisClass
) {
    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(_TP_SERVER_KERNEL_ENTER_CLEANUPDEACTIVATE),
                    "Entered ntv_hardFailureCleanupDeactivate()",
                    TRACE_DATA_END_PARMS);
    }

    int  rc = 0;
    KERNEL_HardFailureCleanupDeactivateParms kernelCleanupDeactivateParms = {
        .returnCode   = &rc
    };

    const struct server_authorized_function_stubs* auth_stubs_p =
             (server_authorized_function_stubs*) getServerAuthorizedFunctionStubs();

    auth_stubs_p->hardFailureCleanupDeactivate(&kernelCleanupDeactivateParms);

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(_TP_SERVER_KERNEL_EXIT_CLEANUPDEACTIVATE),
                    "Exit ntv_hardFailureCleanupDeactivate()",
                    TRACE_DATA_INT(rc, "rc"),
                    TRACE_DATA_END_PARMS);
    }

    return rc;
}
