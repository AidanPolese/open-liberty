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
 * JNI code for sync to thread.
 */
#include <jni.h>
#include <stdio.h>

#include "include/ras_tracing.h"
#include "include/security_saf_common.h"
#include "include/security_saf_sync_to_thread.h"
#include "include/server_jni_method_manager.h"
#include "include/server_native_service_tracker.h"
#include "include/util_jni.h"

#define RAS_MODULE_CONST RAS_MODULE_SECURITY_SAF_SYNC_TO_THREAD_JNI

//---------------------------------------------------------------------
// JNI function declaration and export
//---------------------------------------------------------------------
/**
 * Invoke PC routine to delete the previous thread security environment,
 * perform a surrogate authorization check,
 * and create the new thread security environment with the ACEE retrieved from
 * the RACO referenced by the given credential Token.
 *
 * @param env                   The JNI environment reference provided by the JVM.
 * @param jobj                  The object instance this method was invoked against.
 * @param jsafCredentialToken   Credential Token.
 * @param jprofilePrefix        The profile prefix to use with the FACILITY class checks.
 * @param jsafServiceResult     Output parm where SAF return/reason codes are copied back to Java.
 *
 * @return 0 if sync is successful; otherwise a non-zero error code.  See jsafServiceResult
 *         for SAF failure codes.
 */
#pragma export(ntv_setThreadSecurityEnvironment)
JNIEXPORT jint JNICALL
ntv_setThreadSecurityEnvironment(JNIEnv* env,
                                 jclass jobj,
                                 jbyteArray jsafCredentialToken,
                                 jbyteArray jprofilePrefix,
                                 jbyteArray jsafServiceResult);
/**
 * Invoke PC routine to see if sync to thread is enabled.
 *
 * @param env                   The JNI environment reference provided by the JVM.
 * @param jobj                  The object instance this method was invoked against.
 * @param jprofilePrefix        The profile prefix to use with the FACILITY class checks.
 *
 * @return 1 if sync to thread is enabled. 0 otherwise.
 *
 */
#pragma export(ntv_isSyncToThreadEnabled)
JNIEXPORT jboolean JNICALL
ntv_isSyncToThreadEnabled(JNIEnv* env,
                          jclass jobj,
                          jbyteArray jprofilePrefix);

/**
 * Invoke PC routine to reset indicators that we have checked and cached the
 * sync to thread is enabled result.
 *
 * @param env                   The JNI environment reference provided by the JVM.
 * @param jobj                  The object instance this method was invoked against.
 *
 */
#pragma export(ntv_resetIsNativeEnabledCache)
JNIEXPORT void JNICALL
ntv_resetIsNativeEnabledCache(JNIEnv* env,
                              jclass jobj);

//---------------------------------------------------------------------
// JNI native method structure
//---------------------------------------------------------------------
#pragma convert("ISO8859-1")
static const JNINativeMethod threadIdentityServiceMethods[] = {
    { "ntv_setThreadSecurityEnvironment",
      "([B[B[B)I",
      (void *) ntv_setThreadSecurityEnvironment },
    { "ntv_isSyncToThreadEnabled",
      "([B)Z",
      (void *) ntv_isSyncToThreadEnabled },
    { "ntv_resetIsNativeEnabledCache",
      "()V",
      (void *) ntv_resetIsNativeEnabledCache }
};
#pragma convert(pop)

//---------------------------------------------------------------------
// NativeMethodDescriptor
//---------------------------------------------------------------------
#pragma export(zJNI_com_ibm_ws_security_thread_zos_internal_ThreadIdentityServiceImpl)
NativeMethodDescriptor zJNI_com_ibm_ws_security_thread_zos_internal_ThreadIdentityServiceImpl = {
    .registrationFunction = NULL,
    .deregistrationFunction = NULL,
    .nativeMethodCount = sizeof(threadIdentityServiceMethods) / sizeof(threadIdentityServiceMethods[0]),
    .nativeMethods = threadIdentityServiceMethods
};

//---------------------------------------------------------------------
// JNI function implementation
//---------------------------------------------------------------------
/*
 * See function declaration for documentation.
 */
JNIEXPORT jint JNICALL
ntv_setThreadSecurityEnvironment(JNIEnv* env,
                                 jclass jobj,
                                 jbyteArray jsafCredentialToken,
                                 jbyteArray jprofilePrefix,
                                 jbyteArray jsafServiceResult) {

    if (TraceActive(trc_level_basic)) {
        TraceRecord(trc_level_basic,
                    TP(1),
                    TRACE_DESC_FUNCTION_ENTRY,
                    TRACE_DATA_END_PARMS);
    }

    jbyte* safCredentialToken       = NULL;
    jsize  safCredentialTokenLen    = 0;
    jbyte* profilePrefix            = NULL;

    SAFServiceResult safServiceResult = {
        .wasReturnCode = SECURITY_AUTH_RC_UNAUTHORIZED,
        .safReturnCode = -1,
        .racfReturnCode = -1,
        .racfReasonCode = -1
    };

    JNI_try {
        const server_authorized_function_stubs* auth_stubs_p = getServerAuthorizedFunctionStubs();
        if (auth_stubs_p == NULL) {
            JNI_throwNullPointerException(env, "auth_stubs_p is null");
        }

        if (jsafCredentialToken != NULL) {
            JNI_GetByteArrayElements(safCredentialToken,    env, jsafCredentialToken,    "jsafCredentialToken is null", NULL);
            JNI_GetArrayLength(safCredentialTokenLen,       env, jsafCredentialToken,    "jsafCredentialToken is null", 0);
        }

        JNI_GetByteArrayElements(profilePrefix, env, jprofilePrefix, "jprofilePrefix is null", NULL);

        if (TraceActive(trc_level_basic)) {
            TraceRecord(trc_level_basic,
                        TP(2),
                        "setThreadSecurityEnvironment data",
                        TRACE_DATA_INT(safCredentialTokenLen, "safCredentialToken length"),
                        TRACE_DATA_RAWDATA(sizeof(RegistryToken), safCredentialToken, "token"),
                        TRACE_DATA_RAWDATA(8, profilePrefix, "profilePrefix"),
                        TRACE_DATA_END_PARMS);
        }

        SyncToThreadParms parms = {
            .safCredentialToken = (RegistryToken*) safCredentialToken,
            .profilePrefix      = (char*) profilePrefix,
            .profilePrefixLen   = strlen((char*)profilePrefix),
            .safServiceResult = &safServiceResult
        };

        int rc = auth_stubs_p->syncToThread(&parms);
        if (rc != 0) {
            JNI_throwPCRoutineFailedException(env, "syncToThread", rc)
        }

        if (jsafServiceResult != NULL) {
            JNI_SetByteArrayRegion(env, jsafServiceResult, 0, sizeof(SAFServiceResult), (jbyte*) &safServiceResult);
        }
    }
    JNI_catch(env);

    if (jsafCredentialToken != NULL) {
        JNI_ReleaseByteArrayElements(env, jsafCredentialToken,  safCredentialToken, NULL);
    }

    JNI_ReleaseByteArrayElements(env, jprofilePrefix, profilePrefix, NULL);

    JNI_reThrowAndReturn(env);

    if (TraceActive(trc_level_basic)) {
        TraceRecord(trc_level_basic,
                    TP(3),
                    TRACE_DESC_FUNCTION_EXIT,
                    TRACE_DATA_RAWDATA(sizeof(SAFServiceResult),&safServiceResult,"safServiceResult"),
                    TRACE_DATA_END_PARMS);
    }

    // If both wasReturnCode and safReturnCode are 0, then the sync was successful,
    // and the OR'ing of two 0s will return 0, which indicates success. Otherwise, if either
    // value is NOT 0, then the OR'ing will return non-zero, which indicates failure.
    return (safServiceResult.wasReturnCode | safServiceResult.safReturnCode);
}

/*
 * See function declaration for documentation.
 */
JNIEXPORT jboolean JNICALL
ntv_isSyncToThreadEnabled(JNIEnv* env,
                                 jclass jobj,
                                 jbyteArray jprofilePrefix) {
    int pcRc = 0;

    if (TraceActive(trc_level_basic)) {
        TraceRecord(trc_level_basic,
                    TP(4),
                    TRACE_DESC_FUNCTION_ENTRY,
                    TRACE_DATA_END_PARMS);
    }

    jbyte* profilePrefix = NULL;

    JNI_try {
        const server_authorized_function_stubs* auth_stubs_p = getServerAuthorizedFunctionStubs();
        if (auth_stubs_p == NULL) {
            JNI_throwNullPointerException(env, "auth_stubs_p is null");
        }

        JNI_GetByteArrayElements(profilePrefix, env, jprofilePrefix, "jprofilePrefix is null", NULL);

        if (TraceActive(trc_level_basic)) {
            TraceRecord(trc_level_basic,
                        TP(5),
                        "ntv_isSyncToThreadEnabled",
                        TRACE_DATA_RAWDATA(8, profilePrefix, "profilePrefix"),
                        TRACE_DATA_END_PARMS);
        }

        IsSyncToThreadEnabledParms parms = {
            .profilePrefix      = (char*) profilePrefix,
            .profilePrefixLen   = strlen((char*)profilePrefix),
            .syncToThreadEnabled = &pcRc
        };

        int rc = auth_stubs_p->isSyncToThreadEnabled(&parms);
        if (rc != 0) {
            JNI_throwPCRoutineFailedException(env, "isSyncToThreadEnabled", rc)
        }
    }
    JNI_catch(env);

    JNI_ReleaseByteArrayElements(env, jprofilePrefix, profilePrefix, NULL);

    JNI_reThrowAndReturn(env);

    if (TraceActive(trc_level_basic)) {
        TraceRecord(trc_level_basic,
                    TP(6),
                    TRACE_DESC_FUNCTION_EXIT,
                    TRACE_DATA_INT(pcRc, "is sync enabled"),
                    TRACE_DATA_END_PARMS);
    }

    return pcRc;
}

/*
 * See function declaration for documentation.
 */
JNIEXPORT void JNICALL
ntv_resetIsNativeEnabledCache(JNIEnv* env, jclass jobj) {
    int pcRc;

    if (TraceActive(trc_level_basic)) {
        TraceRecord(trc_level_basic,
                    TP(7),
                    TRACE_DESC_FUNCTION_ENTRY,
                    TRACE_DATA_END_PARMS);
    }

    JNI_try {
        const server_authorized_function_stubs* auth_stubs_p = getServerAuthorizedFunctionStubs();
        if (auth_stubs_p == NULL) {
            JNI_throwNullPointerException(env, "auth_stubs_p is null");
        }

        ResetSyncToThreadEnabledParms parms = {
            .returnCode  = &pcRc
        };

        int rc = auth_stubs_p->resetSyncToThreadEnabled(&parms);
        if (rc != 0) {
            JNI_throwPCRoutineFailedException(env, "resetSyncToThreadEnabled", rc)
        }
    }
    JNI_catch(env);

    JNI_reThrowAndReturn(env);

    if (TraceActive(trc_level_basic)) {
        TraceRecord(trc_level_basic,
                    TP(8),
                    TRACE_DESC_FUNCTION_EXIT,
                    TRACE_DATA_INT(pcRc, "return code"),
                    TRACE_DATA_END_PARMS);
    }
}
