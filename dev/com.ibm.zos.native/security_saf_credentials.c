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
 * JNI code for working with SAF credentials.
 */
#include <jni.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#include "include/ras_tracing.h"
#include "include/security_saf_authentication.h"
#include "include/server_jni_method_manager.h"
#include "include/server_native_service_tracker.h"
#include "include/util_jni.h"
#include "include/util_registry.h"
#include "include/gen/cvt.h"
#include "include/gen/ichprcvt.h"

#define RAS_MODULE_CONST RAS_MODULE_SECURITY_SAF_CREDENTIALS

//---------------------------------------------------------------------
// JNI function declaration and export
//---------------------------------------------------------------------
/**
 * Create a native credential for a given user using password authentication.
 *
 * @param env          The JNI environment reference provided by the JVM.
 * @param jobj         The object instance this method was invoked against.
 * @param jusername    The user to create the native credential for.
 * @param jpassword    The password for @c jusername.
 * @param jauditString An optional audit string used for writing SMF records associated with this credential.
 * @param japplName    An optional application name.
 * @param jreturnCodes A 20-byte byte array used for storing return code information.  The return codes are
 *                     only populated if the creation of the credential was unsuccessful.
 *
 * @return A byte array with a native security credential token that references the underlying native
 *         credential.  The token can be passed to future method invocations that need to access the native
 *         credential.
 */
#pragma export(ntv_createPasswordCredential)
JNIEXPORT jbyteArray JNICALL
ntv_createPasswordCredential(JNIEnv* env,
                             jclass jobj,
                             jbyteArray jusername,
                             jbyteArray jpassword,
                             jbyteArray jauditString,
                             jbyteArray japplName,
                             jbyteArray jreturnCodes);

/**
 * Create a native credential for a given user.
 *
 * @param env               The JNI environment reference provided by the JVM.
 * @param jobj              The object instance this method was invoked against.
 * @param jusername         The user to create the native credential for.
 * @param jauditString      An optional audit string used for writing SMF records associated with this credential.
 * @param japplName         An optional application name.
 * @param jsafServiceResult Output - a byte array used for storing return and reason codes from the SAF service.
 *                          The array is populated only if the SAF service failed.
 *
 * @return A byte array with a native security credential token that references the underlying native
 *         credential.  The token can be passed to future method invocations that need to access the native
 *         credential.
 */
#pragma export(ntv_createAssertedCredential)
JNIEXPORT jbyteArray JNICALL
ntv_createAssertedCredential(JNIEnv* env,
                             jclass jobj,
                             jbyteArray jusername,
                             jbyteArray jauditString,
                             jbyteArray japplName,
                             jbyteArray jsafServiceResult);

/**
 * Native method to create a credential for the given certificate. The method
 * returns a native token that indirectly references a RACO. The native token
 * gets wrapped in Java by SAFCredentialToken.
 *
 * @param env             The JNI environment reference provided by the JVM.
 * @param jobj            The object instance this method was invoked against.
 * @param jcertificate    The certificate for which to create the credential.
 * @param jcertLen        The length of the certificate (cannot use strlen due to potential null characters in certificate)
 * @param jauditString    An optional audit string used for logging and SMF recording.
 * @param japplName       An optional SAF profile prefix.
 * @param joutputUsername Output parm that is populated with the user ID that the
 *                          certificate was mapped to.
 * @param jsafResult      Output parm that is populed with SAF service return/reason codes.
 *
 * @return byte[] representing the native credential token. This byte[] gets wrapped in
 *         Java by a SAFCredentialToken. The native token indirectly references the RACO. If a
 *         RACO failed to be created, null is returned. In that event, the safResult output
 *         parm will contain the return and reason codes of the failed SAF service.
 */
#pragma export(ntv_createCertificateCredential)
JNIEXPORT jbyteArray JNICALL
ntv_createCertificateCredential(JNIEnv*    env,
                                jclass     jobj,
                                jbyteArray jcertificate,
                                jint       jcertLen,
                                jbyteArray jauditString,
                                jbyteArray japplName,
                                jbyteArray joutputUsername,
                                jbyteArray jsafResult);

/**
 * Native method to create a credential for the given mapped identity. The method
 * returns a native token that indirectly references a RACO. The native token
 * gets wrapped in Java by SAFCredentialToken.
 *
 * @param env              The JNI environment reference provided by the JVM.
 * @param jobj             The object instance this method was invoked against.
 * @param juserName        The User's distinguished name for which to create the credential.
 * @param jregistryName    The registry name to be used when mapping.
 * @param jauditString     An optional audit string used for logging and SMF recording.
 * @param japplName        An optional SAF profile prefix.
 * @param joutputUserName  Output parm that is populated with the user ID that the
 *                         user name/registry name was mapped to.
 * @param jsafResult       Output parm that is populated with SAF service return/reason codes.
 *
 * @return byte[] representing the native credential token. This byte[] gets wrapped in
 *         Java by a SAFCredentialToken. The native token indirectly references the RACO. If a
 *         RACO failed to be created, null is returned. In that event, the safResult output
 *         parm will contain the return and reason codes of the failed SAF service.
 */
#pragma export(ntv_createMappedCredential)
JNIEXPORT jbyteArray JNICALL
ntv_createMappedCredential(JNIEnv*    env,
                                jclass     jobj,
                                jbyteArray juserName,
                                jbyteArray jregistryName,
                                jbyteArray jauditString,
                                jbyteArray japplName,
                                jbyteArray joutputUserName,
                                jbyteArray jsafResult);

/**
 * Delete the native credential associated with a native security credential token.
 *
 * @param env       The JNI environment reference provided by the JVM.
 * @param jobj      The object instance this method was invoked against.
 * @param jtoken    A native security credential token referencing the underlying
 *                  native credential to delete.  Only tokens returned from a previous
 *                  createXXXCredential method should be used.
 *
 * @return A return code indicating that the underlying native credential was successfully
 *         deleted or, if not, the reason why it was not.
 */
#pragma export(ntv_deleteCredential)
JNIEXPORT int JNICALL
ntv_deleteCredential(JNIEnv* env, jclass jobj, jbyteArray jtoken);

/**
 * Determine if mixed-case passwords are supported by the SAF product.
 *
 * @param env the JNI environment reference provided by the JVM
 * @param jobj The object instance this method was invoked against.
 *
 * @returns JNI_TRUE if mixed-case passwords are supported; JNI_FALSE otherwise
 */
#pragma export(ntv_isMixedCasePWEnabled)
JNIEXPORT jboolean JNICALL
ntv_isMixedCasePWEnabled(JNIEnv* env, jobject jobj);

/**
 * Check if the user associated with the given credential has the RESTRICTED
 * attribute set.  This is determined by checking the aceeraui bit in the ACEE.
 *
 * @param env          The JNI environment reference provided by the JVM.
 * @param jobj         The object instance this method was invoked against.
 * @param jsafCredentialToken   Token returned by a previous call to SAFCredentialsService.create*Credential.
 * @param jsafServiceResult     Output parm where SAF return/reason codes are copied back to Java.
 *
 * @return 1 if credential is RESTRICTED;
 *         0 if not;
 *         some other value if an error occurred.  Check SAFServiceResult for rc/rsn codes.
 */
#pragma export(ntv_isRESTRICTED)
JNIEXPORT jint JNICALL
ntv_isRESTRICTED(JNIEnv* env,
                 jclass jobj,
                 jbyteArray jsafCredentialToken,
                 jbyteArray jsafServiceResult);

/**
 * Clear out the penalty box cache.
 *
 * @param env the JNI environment reference provided by the JVM
 * @param jobj The object instance this method was invoked against.
 */
#pragma export(ntv_flushPenaltyBoxCache)
JNIEXPORT void JNICALL
ntv_flushPenaltyBoxCache(JNIEnv* env, jobject jobj);

/**
 *  Extract the UTOKEN from the ACEE associated with the given SAFCredentialToken.
 *
 * @param env          The JNI environment reference provided by the JVM.
 * @param jobj         The object instance this method was invoked against.
 * @param jsafCredentialToken   Token returned by a previous call to SAFCredentialsService.create*Credential.
 * @param jsafServiceResult     Output parm where SAF return/reason codes are copied back to Java.
 *
 * @return byte[] representing the extracted UTOKEN.
 */
#pragma export(ntv_extractUtoken)
JNIEXPORT jbyteArray JNICALL
ntv_extractUtoken(JNIEnv* env,
                 jclass jobj,
                 jbyteArray jsafCredentialToken,
                 jbyteArray jsafServiceResult);


//---------------------------------------------------------------------
// JNI native method structure
//---------------------------------------------------------------------
#pragma convert("ISO8859-1")
static const JNINativeMethod securitySafCredentialsMethods[] = {
    { "ntv_createPasswordCredential",
      "([B[B[B[B[B)[B",
      (void *) ntv_createPasswordCredential },
    { "ntv_createAssertedCredential",
      "([B[B[B[B)[B",
      (void *) ntv_createAssertedCredential },
    { "ntv_createCertificateCredential",
      "([BI[B[B[B[B)[B",
      (void *) ntv_createCertificateCredential },
    { "ntv_deleteCredential",
      "([B)I",
      (void *) ntv_deleteCredential },
    { "ntv_isMixedCasePWEnabled",
      "()Z",
      (void *) ntv_isMixedCasePWEnabled },
    { "ntv_isRESTRICTED",
      "([B[B)I",
      (void *) ntv_isRESTRICTED },
    { "ntv_flushPenaltyBoxCache",
      "()V",
      (void *) ntv_flushPenaltyBoxCache },
    { "ntv_extractUtoken",
      "([B[B)[B",
      (void *) ntv_extractUtoken },
    { "ntv_createMappedCredential",
      "([B[B[B[B[B[B)[B",
      (void *) ntv_createMappedCredential }
};
#pragma convert(pop)

//---------------------------------------------------------------------
// NativeMethodDescriptor
//---------------------------------------------------------------------
#pragma export(zJNI_com_ibm_ws_security_credentials_saf_internal_SAFCredentialsServiceImpl)
NativeMethodDescriptor zJNI_com_ibm_ws_security_credentials_saf_internal_SAFCredentialsServiceImpl = {
    .registrationFunction = NULL,
    .deregistrationFunction = NULL,
    .nativeMethodCount = sizeof(securitySafCredentialsMethods) / sizeof(securitySafCredentialsMethods[0]),
    .nativeMethods = securitySafCredentialsMethods
};

//---------------------------------------------------------------------
// JNI function implementation
//---------------------------------------------------------------------
/*
 * See function declaration for documentation.
 */
JNIEXPORT jbyteArray JNICALL
ntv_createPasswordCredential(JNIEnv* env,
                             jclass jobj,
                             jbyteArray jusername,
                             jbyteArray jpassword,
                             jbyteArray jauditString,
                             jbyteArray japplName,
                             jbyteArray jsafServiceResult) {
    if (TraceActive(trc_level_basic)) {
        TraceRecord(trc_level_basic,
                    TP(1),
                    TRACE_DESC_FUNCTION_ENTRY,
                    TRACE_DATA_END_PARMS);
    }

    jbyteArray jreturnToken = NULL;
    jbyte* username         = NULL;
    jbyte* password         = NULL;
    jbyte* auditString      = NULL;
    jbyte* applName         = NULL;

    JNI_try {
        const server_authorized_function_stubs* auth_stubs_p = getServerAuthorizedFunctionStubs();
        if (auth_stubs_p == NULL) {
            JNI_throwNullPointerException(env, "auth_stubs_p is null");
        }

        JNI_GetByteArrayElements(username,    env, jusername,    "username is null", NULL);
        JNI_GetByteArrayElements(password,    env, jpassword,    "password is null", NULL);
        JNI_GetByteArrayElements(auditString, env, jauditString, NULL,               NULL);
        JNI_GetByteArrayElements(applName,    env, japplName,    "applName is null", NULL);

        RegistryToken token;

        SAFServiceResult safServiceResult = {
            .wasReturnCode = SECURITY_AUTH_RC_UNAUTHORIZED,
            .safReturnCode = -1,
            .racfReturnCode = -1,
            .racfReasonCode = -1
        };

        CreatePasswordCredentialParms parms = {
            .usernamePtr      = (char*) username,
            .usernameLen      = strlen((char*)username),
            .passwordPtr      = (char*) password,
            .passwordLen      = strlen((char*)password),
            .auditStringPtr   = (char*) auditString,
            .auditStringLen   = strlen((char*)auditString),
            .applNamePtr      = (char*) applName,
            .applNameLen      = strlen((char*)applName),
            .outputToken      = &token,
            .safServiceResult = &safServiceResult
        };

        int rc = auth_stubs_p->createPasswordCredential(&parms);
        if (rc != 0) {
            JNI_throwPCRoutineFailedException(env, "createPasswordCredential", rc)
        }

        if (safServiceResult.wasReturnCode == 0) {
            // Success!  Allocate the token to return to Java.
            JNI_NewByteArray(jreturnToken, env, sizeof(RegistryToken));
            JNI_SetByteArrayRegion(env, jreturnToken, 0, sizeof(RegistryToken), (jbyte *) &token);
        } else if (jsafServiceResult != NULL) {
            // Fail! Copy the SAF return codes back to Java.
            JNI_SetByteArrayRegion(env, jsafServiceResult, 0, sizeof(SAFServiceResult), (jbyte*) &safServiceResult);
        }
    }
    JNI_catch(env);

    JNI_ReleaseByteArrayElements(env, jusername,    username,    NULL);
    JNI_ReleaseByteArrayElements(env, jpassword,    password,    NULL);
    JNI_ReleaseByteArrayElements(env, jauditString, auditString, NULL);
    JNI_ReleaseByteArrayElements(env, japplName,    applName,    NULL);

    JNI_reThrowAndReturn(env);

    if (TraceActive(trc_level_basic)) {
        TraceRecord(trc_level_basic,
                    TP(2),
                    TRACE_DESC_FUNCTION_EXIT,
                    TRACE_DATA_END_PARMS);
    }

    return jreturnToken;
}

/*
 * See function declaration for documentation.
 */
JNIEXPORT jbyteArray JNICALL
ntv_createAssertedCredential(JNIEnv* env,
                             jclass jobj,
                             jbyteArray jusername,
                             jbyteArray jauditString,
                             jbyteArray japplName,
                             jbyteArray jsafServiceResult) {

    if (TraceActive(trc_level_basic)) {
        TraceRecord(trc_level_basic,
                    TP(3),
                    TRACE_DESC_FUNCTION_ENTRY,
                    TRACE_DATA_END_PARMS);
    }

    jbyteArray jreturnToken = NULL;
    jbyte* username         = NULL;
    jbyte* auditString      = NULL;
    jbyte* applName         = NULL;

    JNI_try {
        const server_authorized_function_stubs* auth_stubs_p = getServerAuthorizedFunctionStubs();
        if (auth_stubs_p == NULL) {
            JNI_throwNullPointerException(env, "auth_stubs_p is null");
        }

        JNI_GetByteArrayElements(username,    env, jusername,    "jusername is null", NULL);
        JNI_GetByteArrayElements(auditString, env, jauditString, NULL,                NULL);
        JNI_GetByteArrayElements(applName,    env, japplName,    "applName is null",  NULL);

        RegistryToken token;

        SAFServiceResult safServiceResult = {
            .wasReturnCode = SECURITY_AUTH_RC_UNAUTHORIZED,
            .safReturnCode = -1,
            .racfReturnCode = -1,
            .racfReasonCode = -1
        };

        CreateAssertedCredentialParms parms = {
            .usernamePtr    = (char*) username,
            .usernameLen    = strlen((char*)username),
            .auditStringPtr = (char*) auditString,
            .auditStringLen = strlen((char*)auditString),
            .applNamePtr    = (char*) applName,
            .applNameLen    = strlen((char*)applName),
            .outputToken    = &token,
            .safServiceResult = &safServiceResult
        };

        int rc = auth_stubs_p->createAssertedCredential(&parms);
        if (rc != 0) {
            JNI_throwPCRoutineFailedException(env, "createAssertedCredential", rc);
        }

        if (safServiceResult.wasReturnCode == 0) {
            // Success!  Allocate the token to return to Java.
            JNI_NewByteArray(jreturnToken, env, sizeof(RegistryToken));
            JNI_SetByteArrayRegion(env, jreturnToken, 0, sizeof(RegistryToken), (jbyte *) &token);
        } else if (jsafServiceResult != NULL) {
            // Fail! Copy the SAF return codes back to Java.
            JNI_SetByteArrayRegion(env, jsafServiceResult, 0, sizeof(SAFServiceResult), (jbyte*) &safServiceResult);
        }
    }
    JNI_catch(env);

    JNI_ReleaseByteArrayElements(env, jusername,    username,    NULL);
    JNI_ReleaseByteArrayElements(env, jauditString, auditString, NULL);
    JNI_ReleaseByteArrayElements(env, japplName,    applName,    NULL);

    JNI_reThrowAndReturn(env);

    if (TraceActive(trc_level_basic)) {
        TraceRecord(trc_level_basic,
                    TP(4),
                    TRACE_DESC_FUNCTION_EXIT,
                    TRACE_DATA_END_PARMS);
    }

    return jreturnToken;
}

/*
 * See function declaration for documentation.
 */
JNIEXPORT jbyteArray JNICALL
ntv_createCertificateCredential(JNIEnv*    env,
                                jclass     jobj,
                                jbyteArray jcertificate,
                                jint       jcertLength,
                                jbyteArray jauditString,
                                jbyteArray japplName,
                                jbyteArray joutputUsername,
                                jbyteArray jsafServiceResult) {
    if (TraceActive(trc_level_basic)) {
        TraceRecord(trc_level_basic,
                    TP(5),
                    TRACE_DESC_FUNCTION_ENTRY,
                    TRACE_DATA_END_PARMS);
    }

    jbyteArray jreturnToken = NULL;
    jbyte*     certificate  = NULL;
    jbyte*     auditString  = NULL;
    jbyte*     applName     = NULL;

    JNI_try {
        const server_authorized_function_stubs* auth_stubs_p = getServerAuthorizedFunctionStubs();
        if (auth_stubs_p == NULL) {
            JNI_throwNullPointerException(env, "auth_stubs_p is null");
        }

        JNI_GetByteArrayElements(certificate, env, jcertificate, "certificate is null", NULL);
        JNI_GetByteArrayElements(auditString, env, jauditString, NULL,                  NULL);
        JNI_GetByteArrayElements(applName,    env, japplName,    "applName is null",    NULL);

        RegistryToken token;

        SAFServiceResult safServiceResult = {
            .wasReturnCode = SECURITY_AUTH_RC_UNAUTHORIZED,
            .safReturnCode = -1,
            .racfReturnCode = -1,
            .racfReasonCode = -1
        };

        char outputUsername[8];
        memset(outputUsername,0,8);

        CreateCertificateCredentialParms parms = {
            .certificatePtr   = (char*) certificate,
            .certificateLen   = jcertLength,
            .auditStringPtr   = (char*) auditString,
            .auditStringLen = strlen((char*)auditString),
            .applNamePtr      = (char*) applName,
            .applNameLen    = strlen((char*)applName),
            .outputUsernamePtr = outputUsername,
            .outputToken      = &token,
            .safServiceResult = &safServiceResult
        };

        int rc = auth_stubs_p->createCertificateCredential(&parms);
        if (rc != 0) {
            JNI_throwPCRoutineFailedException(env, "createCertificateCredential", rc)
        }

        if (safServiceResult.wasReturnCode == 0) {
            // Success!  Allocate the token to return to Java.
            JNI_NewByteArray(jreturnToken, env, sizeof(RegistryToken));
            JNI_SetByteArrayRegion(env, jreturnToken, 0, sizeof(RegistryToken), (jbyte *) &token);
            JNI_SetByteArrayRegion(env, joutputUsername, 0, sizeof(outputUsername), (jbyte *) outputUsername);
        } else if (jsafServiceResult != NULL) {
            // Fail! Copy the SAF return codes back to Java.
            JNI_SetByteArrayRegion(env, jsafServiceResult, 0, sizeof(SAFServiceResult), (jbyte*) &safServiceResult);
        }
    }
    JNI_catch(env);

    JNI_ReleaseByteArrayElements(env, jcertificate, certificate, NULL);
    JNI_ReleaseByteArrayElements(env, jauditString, auditString, NULL);
    JNI_ReleaseByteArrayElements(env, japplName,    applName,    NULL);

    JNI_reThrowAndReturn(env);

    if (TraceActive(trc_level_basic)) {
        TraceRecord(trc_level_basic,
                    TP(6),
                    TRACE_DESC_FUNCTION_EXIT,
                    TRACE_DATA_END_PARMS);
    }

    return jreturnToken;
}

/*
 * See function declaration for documentation.
 */
JNIEXPORT int JNICALL
ntv_deleteCredential(JNIEnv* env, jclass jobj, jbyteArray jtoken) {
    int rc = SECURITY_AUTH_RC_UNAUTHORIZED;
    const server_authorized_function_stubs* auth_stubs_p = getServerAuthorizedFunctionStubs();

    if (auth_stubs_p != NULL) {
        RegistryToken* token = (RegistryToken*) (*env)->GetByteArrayElements(env, jtoken, NULL);

        if (token != NULL) {
            DeleteCredentialParms parms = {
                .inputToken = token,
                .returnCode = &rc
            };

            auth_stubs_p->deleteCredential(&parms);
            (*env)->ReleaseByteArrayElements(env, jtoken, (jbyte*) token, 0);
        }
    }

    return rc;
}

//---------------------------------------------------------------------
// Retrieve the RCVTPLC flag from the RCVT.  This flag indicates
// whether or not the SAF product supports lower-case passwords.
//---------------------------------------------------------------------
JNIEXPORT jboolean JNICALL
ntv_isMixedCasePWEnabled(JNIEnv* env, jobject jobj) {
    psa*  psa_p  = NULL;
    cvt*  cvt_p  = (cvt* __ptr32) psa_p->flccvt;
    rcvt* rcvt_p = (rcvt* __ptr32) cvt_p->cvtrac;

    int isMixedCaseEnabled = ((rcvt_p->rcvtflg3 & rcvtplc) != 0);

    if (TraceActive(trc_level_basic)) {
        TraceRecord(trc_level_basic,
                    TP(10),
                    "isMixedCasePWEnabled return",
                    TRACE_DATA_INT(isMixedCaseEnabled,"isMixedCaseEnabled"),
                    TRACE_DATA_RAWDATA(sizeof(rcvt), rcvt_p, "rcvt"),
                    TRACE_DATA_RAWDATA(sizeof(char), &rcvt_p->rcvtflg3, "rcvtflg3"),
                    TRACE_DATA_RAWDATA(sizeof(rcvt_p->rcvtvrmn), rcvt_p->rcvtvrmn, "rcvtvrmn"),
                    TRACE_DATA_END_PARMS);
    }

    return (isMixedCaseEnabled) ? JNI_TRUE : JNI_FALSE;
}

/*
 * See function declaration for documentation.
 */
JNIEXPORT jint JNICALL
ntv_isRESTRICTED(JNIEnv* env,
                 jclass jobj,
                 jbyteArray jsafCredentialToken,
                 jbyteArray jsafServiceResult) {

    if (TraceActive(trc_level_basic)) {
        TraceRecord(trc_level_basic,
                    TP(7),
                    TRACE_DESC_FUNCTION_ENTRY,
                    TRACE_DATA_END_PARMS);
    }

    int isRestrictedBit = 0;
    jbyte* safCredentialToken       = NULL;

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

        if (TraceActive(trc_level_basic)) {
            TraceRecord(trc_level_basic,
                        TP(8),
                        "data",
                        TRACE_DATA_RAWDATA(sizeof(RegistryToken), safCredentialToken, "token"),
                        TRACE_DATA_END_PARMS);
        }

        IsRESTRICTEDParms parms = {
            .safCredentialToken = (RegistryToken*) safCredentialToken,
            .isRestrictedBit    = &isRestrictedBit,
            .safServiceResult   = &safServiceResult
         };

        int rc = auth_stubs_p->isRESTRICTED(&parms);
        if (rc != 0) {
            JNI_throwPCRoutineFailedException(env, "isRESTRICTED", rc)
        }

        if (safServiceResult.wasReturnCode != 0) {
            // An error occurred. Copy the SAF rc/rsn codes back to Java.
            isRestrictedBit = -1;
            if (jsafServiceResult != NULL) {
                JNI_SetByteArrayRegion(env, jsafServiceResult, 0, sizeof(SAFServiceResult), (jbyte*) &safServiceResult);
            }
        }
    }
    JNI_catch(env);
    JNI_ReleaseByteArrayElements(env, jsafCredentialToken,  safCredentialToken, NULL);
    JNI_reThrowAndReturn(env);

    if (TraceActive(trc_level_basic)) {
        TraceRecord(trc_level_basic,
                    TP(9),
                    TRACE_DESC_FUNCTION_EXIT,
                    TRACE_DATA_INT(isRestrictedBit, "isRestrictedBit"),
                    TRACE_DATA_RAWDATA(sizeof(SAFServiceResult),&safServiceResult,"safServiceResult"),
                    TRACE_DATA_END_PARMS);
    }

    return isRestrictedBit;
}

/*
 * See function declaration for documentation.
 */
JNIEXPORT void JNICALL
ntv_flushPenaltyBoxCache(JNIEnv* env, jobject jobj) {

    int flushReturnCode = -1;

    if (TraceActive(trc_level_basic)) {
        TraceRecord(trc_level_basic,
                    TP(11),
                    TRACE_DESC_FUNCTION_ENTRY,
                    TRACE_DATA_END_PARMS);
    }

    JNI_try {
        const server_authorized_function_stubs* auth_stubs_p = getServerAuthorizedFunctionStubs();
        if (auth_stubs_p == NULL) {
            JNI_throwNullPointerException(env, "auth_stubs_p is null");
        }

        FlushPenaltyBoxParms parms = {
            .returnCodePtr = &flushReturnCode
        };

        int rc = auth_stubs_p->flushPenaltyBoxCache(&parms);
        if (rc != 0) {
            JNI_throwPCRoutineFailedException(env, "flushPenaltyBoxCache", rc)
        }
    }
    JNI_catch(env);
    JNI_reThrowAndReturn(env);

    if (TraceActive(trc_level_basic)) {
        TraceRecord(trc_level_basic,
                    TP(12),
                    TRACE_DESC_FUNCTION_EXIT,
                    TRACE_DATA_INT(flushReturnCode, "flushReturnCode"),
                    TRACE_DATA_END_PARMS);
    }
}

/*
 * See function declaration for documentation.
 */
JNIEXPORT jbyteArray JNICALL
ntv_extractUtoken(JNIEnv* env,
                  jclass jobj,
                  jbyteArray jsafCredentialToken,
                  jbyteArray jsafServiceResult) {

    if (TraceActive(trc_level_basic)) {
        TraceRecord(trc_level_basic,
                    TP(13),
                    TRACE_DESC_FUNCTION_ENTRY,
                    TRACE_DATA_END_PARMS);
    }

    jbyte* safCredentialToken       = NULL;
    jbyteArray jreturnUToken = NULL;
    ExtractedUtoken uToken;

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

        if (TraceActive(trc_level_basic)) {
            TraceRecord(trc_level_basic,
                        TP(14),
                        "data",
                        TRACE_DATA_RAWDATA(sizeof(RegistryToken), safCredentialToken, "token"),
                        TRACE_DATA_END_PARMS);
        }

        ExtractUtokenParms parms = {
            .safCredentialToken = (RegistryToken*) safCredentialToken,
            .safExtractedUtoken = &uToken,
            .safServiceResult   = &safServiceResult
         };

        int rc = auth_stubs_p->extractUtoken(&parms);
        if (rc != 0) {
            JNI_throwPCRoutineFailedException(env, "extractUtoken", rc)
        }

        if (safServiceResult.wasReturnCode != 0) {
            // An error occurred. Copy the SAF rc/rsn codes back to Java.
            if (jsafServiceResult != NULL) {
                JNI_SetByteArrayRegion(env, jsafServiceResult, 0, sizeof(SAFServiceResult), (jbyte*) &safServiceResult);
            }
        } else {
            // Success!  Allocate the utoken to return to Java.
            JNI_NewByteArray(jreturnUToken, env, sizeof(ExtractedUtoken));
            JNI_SetByteArrayRegion(env, jreturnUToken, 0, sizeof(ExtractedUtoken), (jbyte *) &uToken);
        }
    }
    JNI_catch(env);
    JNI_ReleaseByteArrayElements(env, jsafCredentialToken,  safCredentialToken, NULL);
    JNI_reThrowAndReturn(env);

    if (TraceActive(trc_level_basic)) {
        TraceRecord(trc_level_basic,
                    TP(15),
                    TRACE_DESC_FUNCTION_EXIT,
                    TRACE_DATA_RAWDATA(sizeof(uToken),&uToken,"utoken"),
                    TRACE_DATA_RAWDATA(sizeof(SAFServiceResult),&safServiceResult,"safServiceResult"),
                    TRACE_DATA_END_PARMS);
    }

    return jreturnUToken;
}

/*
 * See function declaration for documentation.
 */
JNIEXPORT jbyteArray JNICALL
ntv_createMappedCredential(JNIEnv*    env,
                                jclass     jobj,
                                jbyteArray juserName,
                                jbyteArray jregistryName,
                                jbyteArray jauditString,
                                jbyteArray japplName,
                                jbyteArray joutputUserName,
                                jbyteArray jsafServiceResult) {
    if (TraceActive(trc_level_basic)) {
        TraceRecord(trc_level_basic,
                    TP(16),
                    TRACE_DESC_FUNCTION_ENTRY,
                    TRACE_DATA_END_PARMS);
    }

    jbyteArray jreturnToken = NULL;
    jbyte*     userName     = NULL;
    jbyte*     registryName = NULL;
    jbyte*     auditString  = NULL;
    jbyte*     applName     = NULL;

    JNI_try {
        const server_authorized_function_stubs* auth_stubs_p = getServerAuthorizedFunctionStubs();
        if (auth_stubs_p == NULL) {
            JNI_throwNullPointerException(env, "auth_stubs_p is null");
        }

        JNI_GetByteArrayElements(userName,     env, juserName,     "user name is null", NULL);
        JNI_GetByteArrayElements(registryName, env, jregistryName, "registry name is null", NULL);
        JNI_GetByteArrayElements(auditString,  env, jauditString,  NULL,                  NULL);
        JNI_GetByteArrayElements(applName,     env, japplName,     "applName is null",    NULL);

        RegistryToken token;

        SAFServiceResult safServiceResult = {
            .wasReturnCode = SECURITY_AUTH_RC_UNAUTHORIZED,
            .safReturnCode = -1,
            .racfReturnCode = -1,
            .racfReasonCode = -1
        };

        char outputUserName[8];
        memset(outputUserName,0,8);

        CreateMappedCredentialParms parms = {
            .userNamePtr      = (char*) userName,
            .userNameLen      = strlen((char*)userName),
            .registryNamePtr  = (char*) registryName,
            .registryNameLen  = strlen((char*)registryName),
            .auditStringPtr   = (char*) auditString,
            .auditStringLen   = strlen((char*)auditString),
            .applNamePtr      = (char*) applName,
            .applNameLen      = strlen((char*)applName),
            .outputUserNamePtr = outputUserName,
            .outputToken      = &token,
            .safServiceResult = &safServiceResult
        };

        int rc = auth_stubs_p->createMappedCredential(&parms);
        if (rc != 0) {
            JNI_throwPCRoutineFailedException(env, "createMappedCredential", rc)
        }

        if (safServiceResult.wasReturnCode == 0) {
            // Success!  Allocate the token to return to Java.
            JNI_NewByteArray(jreturnToken, env, sizeof(RegistryToken));
            JNI_SetByteArrayRegion(env, jreturnToken, 0, sizeof(RegistryToken), (jbyte *) &token);
            JNI_SetByteArrayRegion(env, joutputUserName, 0, sizeof(outputUserName), (jbyte *) outputUserName);
        } else if (jsafServiceResult != NULL) {
            // Fail! Copy the SAF return codes back to Java.
            JNI_SetByteArrayRegion(env, jsafServiceResult, 0, sizeof(SAFServiceResult), (jbyte*) &safServiceResult);
        }
    }
    JNI_catch(env);

    JNI_ReleaseByteArrayElements(env, juserName,     userName,     NULL);
    JNI_ReleaseByteArrayElements(env, jregistryName, registryName, NULL);
    JNI_ReleaseByteArrayElements(env, jauditString,  auditString,  NULL);
    JNI_ReleaseByteArrayElements(env, japplName,     applName,     NULL);

    JNI_reThrowAndReturn(env);

    if (TraceActive(trc_level_basic)) {
        TraceRecord(trc_level_basic,
                    TP(17),
                    TRACE_DESC_FUNCTION_EXIT,
                    TRACE_DATA_END_PARMS);
    }

    return jreturnToken;
}
