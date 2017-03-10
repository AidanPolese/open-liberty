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

/**
 * @file
 *
 * JNI code for SAF authorization.
 */
#include <jni.h>
#include <stdio.h>

#include "include/ras_tracing.h"
#include "include/security_saf_authz.h"
#include "include/security_saf_common.h"
#include "include/server_jni_method_manager.h"
#include "include/server_native_service_tracker.h"
#include "include/util_jni.h"
#include "include/util_registry.h"

#define RAS_MODULE_CONST RAS_MODULE_SECURITY_SAF_AUTHZ_JNI

//---------------------------------------------------------------------
// JNI function declaration and export
//---------------------------------------------------------------------
/**
 * Perform a SAF authorization check of the given credential against the
 * given resource, class, and applname.
 *
 * @param env          The JNI environment reference provided by the JVM.
 * @param jobj         The object instance this method was invoked against.
 * @param jsafCredentialToken   Token returned by a previous call to SAFCredentialsService.create*Credential.
 * @param jresource             The SAF resource profile to be authorized against.
 * @param jclassName            The CLASS of the given resource profile.
 * @param japplid               The APPLNAME.
 * @param jaccessLevel          A saf_access_level indicating the required authority (e.g. READ, UPDATE, etc).
 * @param jlogOption            A saf_log_option that tells SAF how to log the authz request.
 * @param jmsgSuppress          Boolean that tells SAF whether or not to suppress SAF messages.
 * @param jfastAuth             Use RACROUTE REQUEST=FASTAUTH instead of REQUEST=AUTH.
 * @param jsafServiceResult     Output parm where SAF return/reason codes are copied back to Java.
 *
 * @return 0 if credential is authorized; otherwise a non-zero error code.  See jsafServiceResult
 *         for SAF failure codes.
 */
#pragma export(ntv_checkAccess)
JNIEXPORT jint JNICALL
ntv_checkAccess(JNIEnv* env,
                jclass jobj,
                jbyteArray jsafCredentialToken,
                jbyteArray jresource,
                jbyteArray jclassName,
                jbyteArray japplid,
                jint jaccessLevel,
                jint jlogOption,
                jboolean jmsgSuppress,
                jboolean jfastAuth,
                jbyteArray jsafServiceResult);

/**
 * Perform a SAF check to see if the given CLASS is active.
 *
 * @param env           The JNI environment reference provided by the JVM.
 * @param jobj          The object instance this method was invoked against.
 * @param jclassName    The CLASS to check.
 *
 * @return 1 if CLASS is active, 0 if CLASS is inactive, or an error code.
 */
#pragma export(ntv_isSAFClassActive)
JNIEXPORT jint JNICALL
ntv_isSAFClassActive(JNIEnv* env,
                     jclass jobj,
                     jbyteArray jclassName);

//---------------------------------------------------------------------
// JNI native method structure
//---------------------------------------------------------------------
#pragma convert("ISO8859-1")
static const JNINativeMethod safAuthorizationServiceMethods[] = {
    { "ntv_checkAccess",
      "([B[B[B[BIIZZ[B)I",
      (void *) ntv_checkAccess },
    { "ntv_isSAFClassActive",
      "([B)I",
      (void *) ntv_isSAFClassActive }
};
#pragma convert(pop)

//---------------------------------------------------------------------
// NativeMethodDescriptor
//---------------------------------------------------------------------
#pragma export(zJNI_com_ibm_ws_security_authorization_saf_internal_SAFAuthorizationServiceImpl)
NativeMethodDescriptor zJNI_com_ibm_ws_security_authorization_saf_internal_SAFAuthorizationServiceImpl = {
    .registrationFunction = NULL,
    .deregistrationFunction = NULL,
    .nativeMethodCount = sizeof(safAuthorizationServiceMethods) / sizeof(safAuthorizationServiceMethods[0]),
    .nativeMethods = safAuthorizationServiceMethods 
};

//---------------------------------------------------------------------
// JNI function implementation
//---------------------------------------------------------------------
/*
 * See function declaration for documentation.
 */
JNIEXPORT jint JNICALL
ntv_checkAccess(JNIEnv* env,
                jclass jobj,
                jbyteArray jsafCredentialToken,
                jbyteArray jresource,
                jbyteArray jclassName,
                jbyteArray japplid,
                jint jaccessLevel,
                jint jlogOption,
                jboolean jmsgSuppress,
                jboolean jfastAuth,
                jbyteArray jsafServiceResult) {

    if (TraceActive(trc_level_basic)) {
        TraceRecord(trc_level_basic,
                    TP(1),
                    TRACE_DESC_FUNCTION_ENTRY,
                    TRACE_DATA_END_PARMS);
    }

    jbyte* safCredentialToken       = NULL;
    jsize  safCredentialTokenLen    = 0;
    jbyte* resource                 = NULL;
    jbyte* className                = NULL;
    jbyte* applid                   = NULL;

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

        JNI_GetByteArrayElements(safCredentialToken,    env, jsafCredentialToken,    "jsafCredentialToken is null", NULL);
        JNI_GetArrayLength(safCredentialTokenLen,       env, jsafCredentialToken,    "jsafCredentialToken is null", 0);

        JNI_GetByteArrayElements(resource,              env, jresource,              "jresource is null",           NULL);
        JNI_GetByteArrayElements(className,             env, jclassName,             "jclassName is null",          NULL);
        JNI_GetByteArrayElements(applid,                env, japplid,                "japplid is null",             NULL);

        if (TraceActive(trc_level_basic)) {
            TraceRecord(trc_level_basic,
                        TP(3),
                        "checkAccess data",
                        TRACE_DATA_RAWDATA(16, resource, "resource"),
                        TRACE_DATA_RAWDATA(16, className, "className"),
                        TRACE_DATA_INT(safCredentialTokenLen, "safCredentialToken length"),
                        TRACE_DATA_RAWDATA(sizeof(RegistryToken), safCredentialToken, "token"),
                        TRACE_DATA_END_PARMS);
        }

        CheckAccessParms parms = {
            .safCredentialToken = (RegistryToken*) safCredentialToken,
            .resource           = (char*) resource,
            .resourceLen        = strlen((char*)resource),
            .className          = (char*) className,
            .classNameLen       = strlen((char*)className),
            .applName           = (char*) applid,
            .applNameLen        = strlen((char*)applid),
            .accessLevel        = (saf_access_level) jaccessLevel,
            .logOption          = (saf_log_option) jlogOption,
            .msgSuppress        = (int) jmsgSuppress,
            .fastAuth           = (int) jfastAuth,
            .safServiceResult   = &safServiceResult
         };

        int rc = auth_stubs_p->checkAccess(&parms);
        if (rc != 0) {
            JNI_throwPCRoutineFailedException(env, "checkAccess", rc)
        }

        if (jsafServiceResult != NULL) {
            JNI_SetByteArrayRegion(env, jsafServiceResult, 0, sizeof(SAFServiceResult), (jbyte*) &safServiceResult);
        }
    }
    JNI_catch(env);

    JNI_ReleaseByteArrayElements(env, jsafCredentialToken,  safCredentialToken, NULL);
    JNI_ReleaseByteArrayElements(env, jresource,            resource,           NULL);
    JNI_ReleaseByteArrayElements(env, jclassName,           className,          NULL);
    JNI_ReleaseByteArrayElements(env, japplid,              applid,             NULL);

    JNI_reThrowAndReturn(env);

    if (TraceActive(trc_level_basic)) {
        TraceRecord(trc_level_basic,
                    TP(2),
                    TRACE_DESC_FUNCTION_EXIT,
                    TRACE_DATA_RAWDATA(sizeof(SAFServiceResult),&safServiceResult,"safServiceResult"),
                    TRACE_DATA_END_PARMS);
    }

    // If both wasReturnCode and safReturnCode are 0, then the authz check was successful,
    // and the OR'ing of two 0s will return 0, which indicates success. Otherwise, if either
    // value is NOT 0, then the OR'ing will return non-zero, which indicates failure.
    return (safServiceResult.wasReturnCode | safServiceResult.safReturnCode);
}

/*
 * See function declaration for documentation.
 */
JNIEXPORT jint JNICALL
ntv_isSAFClassActive(JNIEnv* env,
                     jclass jobj,
                     jbyteArray jclassName) {

    if (TraceActive(trc_level_basic)) {
        TraceRecord(trc_level_basic,
                    TP(4),
                    TRACE_DESC_FUNCTION_ENTRY,
                    TRACE_DATA_END_PARMS);
    }

    jbyte* className = NULL;
    int rc = 0;

    JNI_try {
        const server_authorized_function_stubs* auth_stubs_p = getServerAuthorizedFunctionStubs();
        if (auth_stubs_p == NULL) {
            JNI_throwNullPointerException(env, "auth_stubs_p is null");
        }

        JNI_GetByteArrayElements(className, env, jclassName, "jclassName is null", NULL);

        if (TraceActive(trc_level_basic)) {
            TraceRecord(trc_level_basic,
                        TP(6),
                        "isSAFClassActive data",
                        TRACE_DATA_STRING((char *)className, "className"),
                        TRACE_DATA_END_PARMS);
        }

        IsSAFClassActiveParms parms = {
            .className          = (char*) className,
            .classNameLen       = strlen((char*)className),
            .rc                 = &rc 
         };

        int pc_rc = auth_stubs_p->isSAFClassActive(&parms);
        if (pc_rc != 0) {
            JNI_throwPCRoutineFailedException(env, "isSAFClassActive", rc)
        }
    }
    JNI_catch(env);

    JNI_ReleaseByteArrayElements(env, jclassName, className, NULL);
    JNI_reThrowAndReturn(env);

    if (TraceActive(trc_level_basic)) {
        TraceRecord(trc_level_basic,
                    TP(5),
                    TRACE_DESC_FUNCTION_EXIT,
                    TRACE_DATA_INT(rc, "rc"),
                    TRACE_DATA_END_PARMS);
    }

    return rc;
}

