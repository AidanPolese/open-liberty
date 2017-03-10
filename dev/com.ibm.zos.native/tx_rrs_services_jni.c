/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2011,2013
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */

#include <atrrc.h>
#include <crgc.h>
#include <ctxc.h>
#include <stdlib.h>
#include <stdio.h>

#include "include/tx_rrs_services_jni.h"

#include "include/gen/ihapsa.h"
#include "include/gen/ihaascb.h"
#include "include/gen/ihaassb.h"

#include "include/ras_tracing.h"
#include "include/server_ipt_stubs.h"
#include "include/server_jni_method_manager.h"
#include "include/server_native_service_tracker.h"
#include "include/tx_authorized_rrs_services.h"

//-----------------------------------------------------------------------------
// Constants. Must in sync with tx_authorized_rrs_services.mc
//-----------------------------------------------------------------------------
#define MAX_EXIT_COUNT                                        32
#define MAX_ELEMENT_COUNT                                     32
#define MAX_RMNAME_PREFIX_LENGTH                              8
#define RMNAME_STCK_LENGTH                                    16

//-----------------------------------------------------------------------------
// Macro definitions.
//-----------------------------------------------------------------------------
#define CHECK_JAVA_EXCEPTION_INPUT(env, input) \
    if (input == NULL && (*env)->ExceptionCheck(env) == JNI_TRUE) { \
        (*env)->ExceptionDescribe(env); \
        return NULL; \
    }

#define CHECK_JAVA_EXCEPTION(env) \
    if ((*env)->ExceptionCheck(env) == JNI_TRUE) { \
        (*env)->ExceptionDescribe(env); \
        return NULL; \
    }

#define JNI_ERR_IF_NULL(object) \
    if (object == NULL) { \
        return JNI_ERR; \
    }

#define JNI_ERR_IF_NULL_AND_DEBUG(env, object) \
    if (object == NULL) { \
        if ((*env)->ExceptionCheck(env) == JNI_TRUE) { \
            (*env)->ExceptionDescribe(env); \
        } \
        return JNI_ERR; \
    }

//-----------------------------------------------------------------------------
// RAS Trace constants.
//-----------------------------------------------------------------------------
#define RAS_MODULE_CONST RAS_MODULE_TX_RRS_SERVICES_JNI
#define TP_TX_RRS_JNI_ATR4BEG_ENTRY                                1
#define TP_TX_RRS_JNI_ATR4BEG_EXIT                                 2
#define TP_TX_RRS_JNI_ATR4END_ENTRY                                3
#define TP_TX_RRS_JNI_ATR4END_EXIT                                 4
#define TP_TX_RRS_JNI_ATR4BACK_ENTRY                               5
#define TP_TX_RRS_JNI_ATR4BACK_EXIT                                6
#define TP_TX_RRS_JNI_ATR4RUSF_ENTRY                               7
#define TP_TX_RRS_JNI_ATR4RUSF_EXIT                                8
#define TP_TX_RRS_JNI_ATR4RURD_ENTRY                               9
#define TP_TX_RRS_JNI_ATR4RURD_EXIT                                10
#define TP_TX_RRS_JNI_CRG4GRM_ENTRY                                11
#define TP_TX_RRS_JNI_CRG4GRM_EXIT                                 13
#define TP_TX_RRS_JNI_CRG4DRM_ENTRY                                14
#define TP_TX_RRS_JNI_CRG4DRM_EXIT                                 16
#define TP_TX_RRS_JNI_CRG4SEIF_ENTRY                               17
#define TP_TX_RRS_JNI_ATR4SEIF_BAD_PC_RC                           18
#define TP_TX_RRS_JNI_CRG4SEIF_EXIT                                19
#define TP_TX_RRS_JNI_ATR4IBRS_ENTRY                               20
#define TP_TX_RRS_JNI_ATR4IBRS_EXIT                                22
#define TP_TX_RRS_JNI_ATR4IERS_ENTRY                               23
#define TP_TX_RRS_JNI_ATR4IERS_EXIT                                25
#define TP_TX_RRS_JNI_ATR4IRLN_ENTRY                               26
#define TP_TX_RRS_JNI_ATR4IRLN_BAD_PC_RC                           27
#define TP_TX_RRS_JNI_ATR4IRLN_EXIT                                28
#define TP_TX_RRS_JNI_ATR4ISLN_ENTRY                               29
#define TP_TX_RRS_JNI_ATR4ISLN_EXIT                                31
#define TP_TX_RRS_JNI_ATR4RWID_ENTRY                               32
#define TP_TX_RRS_JNI_ATR4RWID_BAD_PC_RC                           33
#define TP_TX_RRS_JNI_ATR4RWID_EXIT                                34
#define TP_TX_RRS_JNI_ATR4SWID_ENTRY                               35
#define TP_TX_RRS_JNI_ATR4SWID_EXIT                                37
#define TP_TX_RRS_JNI_ATR4EINT_ENTRY                               38
#define TP_TX_RRS_JNI_ATR4EINT_BAD_PC_RC                           39
#define TP_TX_RRS_JNI_ATR4EINT_EXIT                                40
#define TP_TX_RRS_JNI_ATR4RUSI_ENTRY                               41
#define TP_TX_RRS_JNI_ATR4RUSI_BAD_PC_RC                           42
#define TP_TX_RRS_JNI_ATR4RUSI_EXIT                                43
#define TP_TX_RRS_JNI_ATR4IRNI_ENTRY                               44
#define TP_TX_RRS_JNI_ATR4IRNI_BAD_PC_RC                           45
#define TP_TX_RRS_JNI_ATR4IRNI_EXIT                                46
#define TP_TX_RRS_JNI_ATR4SENV_ENTRY                               47
#define TP_TX_RRS_JNI_ATR4SENV_EXIT                                49
#define TP_TX_RRS_JNI_ATR4APRP_ENTRY                               50
#define TP_TX_RRS_JNI_ATR4APRP_BAD_PC_RC                           51
#define TP_TX_RRS_JNI_ATR4APRP_EXIT                                52
#define TP_TX_RRS_JNI_ATR4ACMT_ENTRY                               53
#define TP_TX_RRS_JNI_ATR4ACMT_EXIT                                55
#define TP_TX_RRS_JNI_ATR4ADCT_ENTRY                               56
#define TP_TX_RRS_JNI_ATR4ADCT_EXIT                                58
#define TP_TX_RRS_JNI_ATR4ABAK_ENTRY                               59
#define TP_TX_RRS_JNI_ATR4ABAK_EXIT                                61
#define TP_TX_RRS_JNI_ATR4AFGT_ENTRY                               62
#define TP_TX_RRS_JNI_ATR4AFGT_EXIT                                64
#define TP_TX_RRS_JNI_ATR4PDUE_ENTRY                               65
#define TP_TX_RRS_JNI_ATR4PDUE_EXIT                                67
#define TP_TX_RRS_JNI_ATR4IRRI_ENTRY                               68
#define TP_TX_RRS_JNI_ATR4IRRI_EXIT                                70
#define TP_TX_RRS_JNI_ATR4SPID_ENTRY                               71
#define TP_TX_RRS_JNI_ATR4SPID_EXIT                                73
#define TP_TX_RRS_JNI_ATR4SSPC_ENTRY                               74
#define TP_TX_RRS_JNI_ATR4SSPC_EXIT                                76
#define TP_TX_RRS_JNI_ATR4SUSI_ENTRY                               77
#define TP_TX_RRS_JNI_ATR4SUSI_EXIT                                79
#define TP_TX_RRS_JNI_ATR4BEGC_ENTRY                               80
#define TP_TX_RRS_JNI_ATR4BEGC_BAD_PC_RC                           81
#define TP_TX_RRS_JNI_ATR4BEGC_EXIT                                82
#define TP_TX_RRS_JNI_ATR4SWCH_ENTRY                               83
#define TP_TX_RRS_JNI_ATR4SWCH_BAD_PC_RC                           84
#define TP_TX_RRS_JNI_ATR4SWCH_EXIT                                85
#define TP_TX_RRS_JNI_ATR4ENDC_ENTRY                               86
#define TP_TX_RRS_JNI_ATR4ENDC_EXIT                                88
#define TP_TX_RRS_JAVA_ENV_REG_ENTRY                               89
#define TP_TX_RRS_JAVA_ENV_REG_EXIT                                90
#define TP_TX_RRS_JAVA_ENV_DEREG_ENTRY                             91
#define TP_TX_RRS_JAVA_ENV_DEREG_EXIT                              92
#define TP_TX_RRS_JNI_CTX4RCC_ENTRY                                93
#define TP_TX_RRS_JNI_CTX4RCC_EXIT                                 94
#define TP_TX_RRS_JNI_CRG4GRM_IPT_BAD_RC                           95
#define TP_TX_RRS_JNI_CRG4GRM_IPT_BAD_BPX_RC                       96
#define TP_TX_RRS_JNI_BUILD_RMNAME_FAILED_TO_COPY_USER_PREFIX      97
#define TP_TX_RRS_JNI_ATR4SDTA_ENTRY                               98
#define TP_TX_RRS_JNI_ATR4SDTA_EXIT                                99
#define TP_TX_RRS_JNI_ATR4RDTA_ENTRY                              100
#define TP_TX_RRS_JNI_ATR4RDTA_BAD_PC_RC                          101
#define TP_TX_RRS_JNI_ATR4RDTA_EXIT                               102
#define TP_TX_RRS_JNI_ATR4SEIF_BAD_REGISTRY_RM_NAME_RC            103
#define TP_TX_RRS_JNI_ATR4SEIF_BAD_REGISTRY_RM_TOKEN_RC           104
#define TP_TX_RRS_JNI_ATR4IRLN_BAD_REGISTRY_RM_TOKEN_RC           105
#define TP_TX_RRS_JNI_ATR4RDTA_BAD_REGISTRY_RM_NAME_RC            106
#define TP_TX_RRS_JNI_ATR4BEGC_BAD_REGISTRY_RM_TOKEN_RC           107
#define TP_TX_RRS_JNI_ATR4EINT_BAD_REGISTRY_RM_TOKEN_RC           108

//-----------------------------------------------------------------------------
// Strings used by the exception classes
//-----------------------------------------------------------------------------
#pragma convert("ISO8859-1")
static const char* NOT_FOUND_URI_ERROR = "Could not find the specified URI token in the registry.";
static const char* COULD_NOT_ADD_URI_ERROR = "Could not add the new URI token to the registry.";
static const char* NOT_FOUND_CTX_TOKEN_ERROR = "Could not find the specified context token in the registry.";
static const char* COULD_NOT_ADD_CI_TOKEN_ERROR = "Could not add the new context interest token to the registry.";
static const char* NOT_FOUND_CI_TOKEN_ERROR = "Could not find the specified context interest token in the registry.";
static const char* NOT_FOUND_RM_TOKEN_ERROR = "Could not find the specified resource manager token in the registry.";
#pragma convert(pop)

//-----------------------------------------------------------------------------
// JNI native method signatures.
//-----------------------------------------------------------------------------
#pragma convert("ISO8859-1")
static const JNINativeMethod RRSServiceMethods[] = {
    { "ntv_beginTransaction",
      "(I)Lcom/ibm/ws/zos/tx/internal/rrs/BeginTransactionReturnType;",
      (void *) ntv_beginTransaction },
    { "ntv_endUR",
      "(I[B)I",
      (void *) ntv_endUR },
    { "ntv_backoutUR",
      "()I",
      (void *) ntv_backoutUR },
    { "ntv_retireveSideInformationFast",
      "([BI)Lcom/ibm/ws/zos/tx/internal/rrs/RetrieveSideInformationFastReturnType;",
      (void *) ntv_retireveSideInformationFast },
    { "ntv_retireveURData",
      "([BI)Lcom/ibm/ws/zos/tx/internal/rrs/RetrieveURDataReturnType;",
      (void *) ntv_retireveURData },
    { "ntv_registerResourceManager",
      "(I[B[BI[B)Lcom/ibm/ws/zos/tx/internal/rrs/RegisterResMgrReturnType;",
      (void *) ntv_registerResourceManager},
    { "ntv_unregisterResourceManager",
      "([B[B)I",
      (void *) ntv_unregisterResourceManager },
    { "ntv_setExitInformation",
      "([B[BZ)Lcom/ibm/ws/zos/tx/internal/rrs/SetExitInformationReturnType;",
      (void *) ntv_setExitInformation },
    { "ntv_beginRestart",
      "([B)I",
      (void *) ntv_beginRestart },
    { "ntv_endRestart",
      "([B)I",
      (void *) ntv_endRestart },
    { "ntv_retrieveLogName",
      "([B)Lcom/ibm/ws/zos/tx/internal/rrs/RetrieveLogNameReturnType;",
      (void *) ntv_retrieveLogName },
    { "ntv_setLogName",
      "([BI[B)I",
      (void *) ntv_setLogName },
    { "ntv_retrieveWorkIdentifier",
      "([BIII)Lcom/ibm/ws/zos/tx/internal/rrs/RetrieveWorkIdentifierReturnType;",
      (void *) ntv_retrieveWorkIdentifier },
    { "ntv_setWorkIdentifier",
      "([BIII[B)I",
      (void *) ntv_setWorkIdentifier },
    { "ntv_expressUrInterest",
      "([B[BI[B[BI[BI[B)Lcom/ibm/ws/zos/tx/internal/rrs/ExpressInterestReturnType;",
      (void *) ntv_expressUrInterest },
    { "ntv_retrieveSideInformation",
      "([B[II)Lcom/ibm/ws/zos/tx/internal/rrs/RetrieveSideInformationReturnType;",
      (void *) ntv_retrieveSideInformation },
    { "ntv_retrieveUrInterest",
      "([B)Lcom/ibm/ws/zos/tx/internal/rrs/RetrieveURInterestReturnType;",
      (void *) ntv_retrieveUrInterest },
    { "ntv_setEnvironment",
      "([BI[I[I[I)I",
      (void *) ntv_setEnvironment },
    { "ntv_prepareAgentUR",
      "([B[B[BI)Lcom/ibm/ws/zos/tx/internal/rrs/PrepareAgentURReturnType;",
      (void *) ntv_prepareAgentUR },
    { "ntv_commitAgentUR",
      "([B[BI)I",
      (void *) ntv_commitAgentUR },
    { "ntv_delegateCommitAgentUR",
      "([BII)I",
      (void *) ntv_delegateCommitAgentUR },
    { "ntv_backoutAgentUR",
      "([B[BI)I",
      (void *) ntv_backoutAgentUR },
    { "ntv_forgetAgentURInterest",
      "([BI)I",
      (void *) ntv_forgetAgentURInterest },
    { "ntv_postDeferredURExit",
      "([BII)I",
      (void *) ntv_postDeferredURExit },
    { "ntv_respondToRetrievedInterest",
      "([BI[B)I",
      (void *) ntv_respondToRetrievedInterest },
    { "ntv_setPersistentInterestData",
      "([BI[B)I",
      (void *) ntv_setPersistentInterestData },
    { "ntv_setSyncpointControls",
      "([BIIII)I",
      (void *) ntv_setSyncpointControls },
    { "ntv_setSideInformation",
      "([BI[I)I",
      (void *) ntv_setSideInformation },
    { "ntv_beginContext",
      "([B)Lcom/ibm/ws/zos/tx/internal/rrs/BeginContextReturnType;",
      (void *) ntv_beginContext },
    { "ntv_contextSwitch",
      "([B)Lcom/ibm/ws/zos/tx/internal/rrs/SwitchContextReturnType;",
     (void *) ntv_contextSwitch },
    { "ntv_endContext",
      "([BI)I",
      (void *) ntv_endContext },
    { "ntv_retrieveCurrentContextToken",
      "()Lcom/ibm/ws/zos/tx/internal/rrs/RetrieveCurrentContextTokenReturnType;",
      (void*) ntv_retrieveCurrentContextToken },
    { "ntv_setRMMetadata",
        "([BI[B)I",
      (void*) ntv_setRMMetadata },
    { "ntv_retrieveRMMetadata",
      "([B)Lcom/ibm/ws/zos/tx/internal/rrs/RetrieveRMMetadataReturnType;",
      (void *) ntv_retrieveRMMetadata }
};
#pragma convert(pop)

//-----------------------------------------------------------------------------
// NativeMethodDescriptor for RRSServices.
//-----------------------------------------------------------------------------
#pragma export(zJNI_com_ibm_ws_zos_tx_internal_rrs_RRSServicesImpl)
NativeMethodDescriptor zJNI_com_ibm_ws_zos_tx_internal_rrs_RRSServicesImpl = {
    .registrationFunction = javaEnvironmentRegistration,
    .deregistrationFunction = javaEnvironmentDeregistration,
    .nativeMethodCount = sizeof(RRSServiceMethods) / sizeof(RRSServiceMethods[0]),
    .nativeMethods = RRSServiceMethods
};

//-----------------------------------------------------------------------------
// Global variables.
//-----------------------------------------------------------------------------
jclass jExpressIntReturnParmsClass = NULL;
jclass jRetLogNameReturnParmsClass = NULL;
jclass jRetSideInfoFastReturnParmsClass = NULL;
jclass jRetSideInfoReturnParmsClass = NULL;
jclass jRetURDataReturnParmsClass = NULL;
jclass jRetURInterestReturnParmsClass = NULL;
jclass jRetWorkIdReturnParmsClass = NULL;
jclass jSwitchCtxReturnParmsClass = NULL;
jclass jRegResMgrReturnParmsClass = NULL;
jclass jBegingCtxReturnParmsClass = NULL;
jclass jBeginTranReturnParmsClass = NULL;
jclass jRetrieveCurrentCtxTokenReturnParmsClass = NULL;
jclass jPrepareAgentURReturnParmsClass = NULL;
jclass jRegistryExceptionClass = NULL;
jclass jRetrieveRMMetadataReturnParmsClass = NULL;
jclass jSetExitInformationReturnParmsClass = NULL;

jmethodID jExpressIntReturnParmsConstructor = NULL;
jmethodID jRetLogNameReturnParmsConstructor = NULL;
jmethodID jRetSideInfoFastReturnParmsConstructor = NULL;
jmethodID jRetSideInfoReturnParmsConstructor = NULL;
jmethodID jRetURDataReturnParmsConstructor = NULL;
jmethodID jRetURInterestReturnParmsConstructor = NULL;
jmethodID jRetWorkIdReturnParmsConstructor = NULL;
jmethodID jSwitchCtxReturnParmsConstructor = NULL;
jmethodID jRegResMgrReturnParmsConstructor = NULL;
jmethodID jBegingCtxReturnParmsConstructor = NULL;
jmethodID jBeginTranReturnParmsConstructor = NULL;
jmethodID jRetrieveCurrentCtxTokenReturnParmsConstructor = NULL;
jmethodID jPrepareAgentURReturnParmsConstructor = NULL;
jmethodID jRegistryExceptionConstructor = NULL;
jmethodID jRetrieveRMMetadataReturnParmsConstructor = NULL;
jmethodID jSetExitInformationReturnParmsConstructor = NULL;

/**
 * Loads a class via JNI.
 *
 * @param env The JNI environment.
 * @param classloader The class loader to use to load the class.
 * @param findClassMethodID A local reference to the methodID for the findClass
 *                          method on the classloader passed in the classloader
 *                          parameter.
 * @param className The class name to load.  The name should be in the Java
 *                  binary format, with qualifiers separated by dots, not
 *                  slashes.
 *
 * @return A global reference to the class loader, or NULL if there was a
 *         problem.  If there was a problem, control should return to the JVM
 *         immediately because it's likely that there is an exception pending
 *         on the Java stack.
 */
static jclass loadClassJNI(JNIEnv* env, jclass classloader, jmethodID findClassMethodID, const char* className) {
    jclass classGlobalReference = NULL;
    jstring clazzNameString = (*env)->NewStringUTF(env, className);

    if (clazzNameString != NULL) {
        jclass clazz = (*env)->CallObjectMethod(env, classloader, findClassMethodID, clazzNameString);
        if (clazz != NULL) {
            classGlobalReference = (jclass)(*env)->NewGlobalRef(env, clazz);
        }
    }

    return classGlobalReference;
}

/**
 * Throws an exception whose class resides in the JDK.  This distinction is
 * important because the context classloader will be used to look up the
 * exception class.
 *
 * @param env A pointer to the java environment.
 * @param classname An EBCDIC string containing the classname of the exception.
 * @param message An EBCDIC string describing the reason for the exception.
 *
 * @return 0 if the exception was successfully thrown, nonzero on error.
 */
static int throwBaseJavaException(JNIEnv* env, const char* classname, const char* message) {
#pragma convert("ISO8859-1")
    const char* baseExceptionConstructorArgs = "(Ljava/lang/String;)V";
    const char* baseExceptionConstructorMethodName = "<init>";
#pragma convert(pop)

    size_t maxStringLen = 512;

    if ((memchr(classname, 0, maxStringLen) == NULL) || (memchr(message, 0, maxStringLen) == NULL)) {
        return -1;
    }

    char* asciiExceptionClassname = strdup(classname);
    if (asciiExceptionClassname == NULL) {
        return -1;
    }

    if (__etoa(asciiExceptionClassname) <= 0) {
        free(asciiExceptionClassname);
        return -1;
    }

    char* asciiExceptionMessage = strdup(message);
    if (asciiExceptionMessage == NULL) {
        free(asciiExceptionClassname);
        return -1;
    }

    if (__etoa(asciiExceptionMessage) <= 0) {
        free(asciiExceptionClassname);
        free(asciiExceptionMessage);
        return -1;
    }

    jstring excStr = (*env)->NewStringUTF(env, asciiExceptionMessage);
    free(asciiExceptionMessage);
    if ((*env)->ExceptionCheck(env) == JNI_TRUE) {
        (*env)->ExceptionDescribe(env);
        free(asciiExceptionClassname);
        return -1;
    }

    jclass clazz = (*env)->FindClass(env, asciiExceptionClassname);\
    free(asciiExceptionClassname);
    if ((*env)->ExceptionCheck(env) == JNI_TRUE) {
        (*env)->ExceptionDescribe(env);
        return -1;
    }

    jmethodID method = (*env)->GetMethodID(env, clazz, baseExceptionConstructorMethodName, baseExceptionConstructorArgs);
    if ((*env)->ExceptionCheck(env) == JNI_TRUE) {
        (*env)->ExceptionDescribe(env);
        return -1;
    }

    jobject excObj = (*env)->NewObject(env, clazz, method, excStr);
    if ((*env)->ExceptionCheck(env) == JNI_TRUE) {
        (*env)->ExceptionDescribe(env);
        return -1;
    }

    return (*env)->Throw(env, excObj);
}

/**
 * throws a RegistryException with the specified message.
 *
 * @param env A pointer to the JNIEnv
 * @param returnCode The return code from the registry service.
 * @param message The message to include in the exception.  This is an ASCII null-terminated string.
 *
 * @return 0 if the throw was successful.
 */
int throwRegistryException(JNIEnv* env, jint returnCode, const char* message) {
    jstring excStr = (*env)->NewStringUTF(env, message);
    if ((*env)->ExceptionCheck(env) == JNI_TRUE) {
        (*env)->ExceptionDescribe(env);
        return -1;
    }

    jthrowable exception = (*env)->NewObject(env, jRegistryExceptionClass, jRegistryExceptionConstructor, excStr, returnCode);
    if (exception == NULL) {
        return -1;
    }

    return (*env)->Throw(env, exception);
}

/**
 * Throws a RegistryException for a "Context token not found in registry" error.
 *
 * @param env A pointer to the JNIEnv
 * @param returnCode The return code from the registry service.
 *
 * @return 0 if the throw was successful.
 */
int throwContextTokenNotFound(JNIEnv* env, jint returnCode) {
    return throwRegistryException(env, returnCode, NOT_FOUND_CTX_TOKEN_ERROR);
}

/**
 * Throws a RegistryException for a "Resource manager token not found in registry" error.
 *
 * @param env A pointer to the JNIEnv
 * @param returnCode The return code from the registry service.
 *
 * @return 0 if the throw was successful.
 */
int throwResourceManagerTokenNotFound(JNIEnv* env, jint returnCode) {
    return throwRegistryException(env, returnCode, NOT_FOUND_RM_TOKEN_ERROR);
}

/**
 * Throws a RegistryException for a "URI not found in registry" error.
 *
 * @param env A pointer to the JNIEnv
 * @param returnCode The return code from the registry service.
 *
 * @return 0 if the throw was successful.
 */
int throwURINotFound(JNIEnv* env, jint returnCode) {
    return throwRegistryException(env, returnCode, NOT_FOUND_URI_ERROR);
}

/**
 * Throws a RegistryException for a "Context interst token not found in registry" error.
 *
 * @param env A pointer to the JNIEnv
 * @param returnCode The return code from the registry service.
 *
 * @return 0 if the throw was successful.
 */
int throwContextInterestTokenNotFound(JNIEnv* env, jint returnCode) {
    return throwRegistryException(env, returnCode, NOT_FOUND_CI_TOKEN_ERROR);
}

/**
 * Throws a RegistryException for a "add URI to registry" error.
 *
 * @param env A pointer to the JNIEnv
 * @param returnCode The return code from the registry service.
 *
 * @return 0 if the throw was successful.
 */
int throwURIAddError(JNIEnv* env, jint returnCode) {
    return throwRegistryException(env, returnCode, COULD_NOT_ADD_URI_ERROR);
}

int throwContextInterestTokenAddError(JNIEnv* env, jint returnCode) {
    return throwRegistryException(env, returnCode, COULD_NOT_ADD_CI_TOKEN_ERROR);
}

/**
 * Registration callback used to resolve java object references.
 *
 * @param env The JNI environment for the calling thread.
 * @param myClazz The class for which deregistration is taking place.
 * @param extraInfo The context information from the caller.
 *
 * @return JNI_OK if successful completion. JNI_ERR otherwise.
 */
int javaEnvironmentRegistration(JNIEnv* env, jclass myClazz, jobjectArray extraInfo) {

    if (TraceActive(trc_level_detailed)) {
    TraceRecord(trc_level_detailed,
                TP(TP_TX_RRS_JAVA_ENV_REG_ENTRY),
                "tx_rrs_services_jni.javaEnvironmentRegistration. Entry",
                TRACE_DATA_END_PARMS);
    }

#pragma convert("iso8859-1")
    // Return types' class names.
    const char* begingTranRTClassName = "com.ibm.ws.zos.tx.internal.rrs.BeginTransactionReturnType";
    const char* retSideInfoFastRTClassName = "com.ibm.ws.zos.tx.internal.rrs.RetrieveSideInformationFastReturnType";
    const char* retURDataRTClassName = "com.ibm.ws.zos.tx.internal.rrs.RetrieveURDataReturnType";
    const char* regResMgrRTClassName = "com.ibm.ws.zos.tx.internal.rrs.RegisterResMgrReturnType";
    const char* retLogNameRTClassName = "com.ibm.ws.zos.tx.internal.rrs.RetrieveLogNameReturnType";
    const char* retWorkIdRTClassName = "com.ibm.ws.zos.tx.internal.rrs.RetrieveWorkIdentifierReturnType";
    const char* expressIntRTClassName = "com.ibm.ws.zos.tx.internal.rrs.ExpressInterestReturnType";
    const char* retSideInfoRTClassName = "com.ibm.ws.zos.tx.internal.rrs.RetrieveSideInformationReturnType";
    const char* retURInterestRTClassName = "com.ibm.ws.zos.tx.internal.rrs.RetrieveURInterestReturnType";
    const char* prepareAgentURRTClassName = "com.ibm.ws.zos.tx.internal.rrs.PrepareAgentURReturnType";
    const char* begingCtxRTClassName = "com.ibm.ws.zos.tx.internal.rrs.BeginContextReturnType";
    const char* switchCtxRTClassName = "com.ibm.ws.zos.tx.internal.rrs.SwitchContextReturnType";
    const char* retrieveCurrentCtxTknRTClassName = "com.ibm.ws.zos.tx.internal.rrs.RetrieveCurrentContextTokenReturnType";
    const char* retrieveRMMetadataRTClassName = "com.ibm.ws.zos.tx.internal.rrs.RetrieveRMMetadataReturnType";
    const char* setExitInformationRTClassName = "com.ibm.ws.zos.tx.internal.rrs.SetExitInformationReturnType";

    // Return types' method description.
    const char* constructorMethodName = "<init>";
    const char* begingTranRTCMethodDesc = "(I[B[B)V";
    const char* retSideInfoFastRTMethodDesc = "(II)V";
    const char* retURDataRTMethodDesc = "(I[BI[B)V";
    const char* regResMgrRTMethodDesc = "(I[B[B[B[BIIII)V";
    const char* retLogNameRTMethodDesc = "(I[B[B)V";
    const char* retWorkIdRTMethodDesc = "(I[B)V";
    const char* expressIntRTMethodDesc = "(I[B[B[B[B[B[BI[B)V";
    const char* retSideInfoRTMethodDesc = "(I[I)V";
    const char* retURInterestRTMethodDesc = "(I[B[B[B[BII[B)V";
    const char* prepareAgentURRTMethodDesc = "(II[B)V";
    const char* begingCtxRTMethodDesc = "(I[B[B)V";
    const char* switchCtxRTmethodDesc = "(I[B)V";
    const char* retrieveCurrentCtxTknRTMethodDesc = "(I[B)V";
    const char* retrieveRMMetadataRTMethodDesc = "(I[B)V";
    const char* setExitInformationRTMethodDesc = "(IZ)V";

    // Exceptions
    const char* registryExceptionClassName = "com.ibm.ws.zos.tx.internal.rrs.RegistryException";
    const char* registryExceptionConstructorMethodDesc = "(Ljava/lang/String;I)V";

    // Classloader
    const char* findClassMethodName = "loadClass";
    const char* findClassMethodDesc = "(Ljava/lang/String;)Ljava/lang/Class;";
#pragma convert(pop)

    // Find the return type classes and create global references.
    // These references will be deleted when javaEnvironmentDeregistration
    // is called.
    //
    // The classloader used to load the transaction bundle is passed down to
    // this method, and must be used to load classes because the classloader
    // which is currently on this thread is the classloader for the kernel
    // bundle.
    jobject tranClassLoader = (*env)->GetObjectArrayElement(env, extraInfo, 0);
    JNI_ERR_IF_NULL_AND_DEBUG(env, tranClassLoader);
    jclass tranClassLoaderClass = (*env)->GetObjectClass(env, tranClassLoader);
    JNI_ERR_IF_NULL_AND_DEBUG(env, tranClassLoaderClass);
    jmethodID findClassMethod = (*env)->GetMethodID(env, tranClassLoaderClass, findClassMethodName, findClassMethodDesc);
    JNI_ERR_IF_NULL_AND_DEBUG(env, findClassMethod);

    jBeginTranReturnParmsClass = loadClassJNI(env, tranClassLoader, findClassMethod, begingTranRTClassName);
    JNI_ERR_IF_NULL(jBeginTranReturnParmsClass);

    jRetSideInfoFastReturnParmsClass = loadClassJNI(env, tranClassLoader, findClassMethod, retSideInfoFastRTClassName);
    JNI_ERR_IF_NULL(jRetSideInfoFastReturnParmsClass);

    jRetURDataReturnParmsClass = loadClassJNI(env, tranClassLoader, findClassMethod, retURDataRTClassName);
    JNI_ERR_IF_NULL(jRetURDataReturnParmsClass);

    jRegResMgrReturnParmsClass = loadClassJNI(env, tranClassLoader, findClassMethod, regResMgrRTClassName);
    JNI_ERR_IF_NULL(jRegResMgrReturnParmsClass);

    jRetLogNameReturnParmsClass = loadClassJNI(env, tranClassLoader, findClassMethod, retLogNameRTClassName);
    JNI_ERR_IF_NULL(jRetLogNameReturnParmsClass);

    jRetWorkIdReturnParmsClass = loadClassJNI(env, tranClassLoader, findClassMethod, retWorkIdRTClassName);
    JNI_ERR_IF_NULL(jRetWorkIdReturnParmsClass);

    jExpressIntReturnParmsClass = loadClassJNI(env, tranClassLoader, findClassMethod, expressIntRTClassName);
    JNI_ERR_IF_NULL(jExpressIntReturnParmsClass);

    jRetSideInfoReturnParmsClass = loadClassJNI(env, tranClassLoader, findClassMethod, retSideInfoRTClassName);
    JNI_ERR_IF_NULL(jRetSideInfoReturnParmsClass);

    jRetURInterestReturnParmsClass = loadClassJNI(env, tranClassLoader, findClassMethod, retURInterestRTClassName);
    JNI_ERR_IF_NULL(jRetURInterestReturnParmsClass);

    jBegingCtxReturnParmsClass = loadClassJNI(env, tranClassLoader, findClassMethod, begingCtxRTClassName);
    JNI_ERR_IF_NULL(jBegingCtxReturnParmsClass);

    jSwitchCtxReturnParmsClass = loadClassJNI(env, tranClassLoader, findClassMethod, switchCtxRTClassName);
    JNI_ERR_IF_NULL(jSwitchCtxReturnParmsClass);

    jRetrieveCurrentCtxTokenReturnParmsClass = loadClassJNI(env, tranClassLoader, findClassMethod, retrieveCurrentCtxTknRTClassName);
    JNI_ERR_IF_NULL(jRetrieveCurrentCtxTokenReturnParmsClass);

    jPrepareAgentURReturnParmsClass = loadClassJNI(env, tranClassLoader, findClassMethod, prepareAgentURRTClassName);
    JNI_ERR_IF_NULL(jPrepareAgentURReturnParmsClass);

    jRegistryExceptionClass = loadClassJNI(env, tranClassLoader, findClassMethod, registryExceptionClassName);
    JNI_ERR_IF_NULL(jRegistryExceptionClass);

    jRetrieveRMMetadataReturnParmsClass = loadClassJNI(env, tranClassLoader, findClassMethod, retrieveRMMetadataRTClassName);
    JNI_ERR_IF_NULL(jRetrieveRMMetadataReturnParmsClass);
    
    jSetExitInformationReturnParmsClass = loadClassJNI(env, tranClassLoader, findClassMethod, setExitInformationRTClassName);
    JNI_ERR_IF_NULL(jSetExitInformationReturnParmsClass);

    // Get constructor references for the above return types' class references.
    jBeginTranReturnParmsConstructor = (*env)->GetMethodID(env, jBeginTranReturnParmsClass, constructorMethodName, begingTranRTCMethodDesc);
    JNI_ERR_IF_NULL(jBeginTranReturnParmsConstructor);

    jRetSideInfoFastReturnParmsConstructor = (*env)->GetMethodID(env, jRetSideInfoFastReturnParmsClass, constructorMethodName, retSideInfoFastRTMethodDesc);
    JNI_ERR_IF_NULL(jRetSideInfoFastReturnParmsConstructor);

    jRetURDataReturnParmsConstructor = (*env)->GetMethodID(env, jRetURDataReturnParmsClass, constructorMethodName, retURDataRTMethodDesc);
    JNI_ERR_IF_NULL(jRetURDataReturnParmsConstructor);

    jRegResMgrReturnParmsConstructor = (*env)->GetMethodID(env, jRegResMgrReturnParmsClass, constructorMethodName, regResMgrRTMethodDesc);
    JNI_ERR_IF_NULL(jRegResMgrReturnParmsConstructor);

    jRetLogNameReturnParmsConstructor = (*env)->GetMethodID(env, jRetLogNameReturnParmsClass, constructorMethodName, retLogNameRTMethodDesc);
    JNI_ERR_IF_NULL(jRetLogNameReturnParmsConstructor);

    jRetWorkIdReturnParmsConstructor = (*env)->GetMethodID(env, jRetWorkIdReturnParmsClass, constructorMethodName, retWorkIdRTMethodDesc);
    JNI_ERR_IF_NULL(jRetWorkIdReturnParmsConstructor);

    jExpressIntReturnParmsConstructor = (*env)->GetMethodID(env, jExpressIntReturnParmsClass, constructorMethodName, expressIntRTMethodDesc);
    JNI_ERR_IF_NULL(jExpressIntReturnParmsConstructor);

    jRetSideInfoReturnParmsConstructor = (*env)->GetMethodID(env, jRetSideInfoReturnParmsClass, constructorMethodName, retSideInfoRTMethodDesc);
    JNI_ERR_IF_NULL(jRetSideInfoReturnParmsConstructor);

    jRetURInterestReturnParmsConstructor = (*env)->GetMethodID(env, jRetURInterestReturnParmsClass, constructorMethodName, retURInterestRTMethodDesc);
    JNI_ERR_IF_NULL(jRetURInterestReturnParmsConstructor);

    jBegingCtxReturnParmsConstructor = (*env)->GetMethodID(env, jBegingCtxReturnParmsClass, constructorMethodName, begingCtxRTMethodDesc);
    JNI_ERR_IF_NULL(jBegingCtxReturnParmsConstructor);

    jSwitchCtxReturnParmsConstructor = (*env)->GetMethodID(env, jSwitchCtxReturnParmsClass, constructorMethodName, switchCtxRTmethodDesc);
    JNI_ERR_IF_NULL(jSwitchCtxReturnParmsConstructor);

    jRetrieveCurrentCtxTokenReturnParmsConstructor = (*env)->GetMethodID(env, jRetrieveCurrentCtxTokenReturnParmsClass, constructorMethodName, retrieveCurrentCtxTknRTMethodDesc);
    JNI_ERR_IF_NULL(jRetrieveCurrentCtxTokenReturnParmsConstructor);

    jPrepareAgentURReturnParmsConstructor = (*env)->GetMethodID(env, jPrepareAgentURReturnParmsClass, constructorMethodName, prepareAgentURRTMethodDesc);
    JNI_ERR_IF_NULL(jPrepareAgentURReturnParmsConstructor);

    jRegistryExceptionConstructor = (*env)->GetMethodID(env, jRegistryExceptionClass, constructorMethodName, registryExceptionConstructorMethodDesc);
    JNI_ERR_IF_NULL(jRegistryExceptionConstructor);

    jRetrieveRMMetadataReturnParmsConstructor = (*env)->GetMethodID(env, jRetrieveRMMetadataReturnParmsClass, constructorMethodName, retrieveRMMetadataRTMethodDesc);
    JNI_ERR_IF_NULL(jRetrieveRMMetadataReturnParmsConstructor);
    
    jSetExitInformationReturnParmsConstructor = (*env)->GetMethodID(env, jSetExitInformationReturnParmsClass, constructorMethodName, setExitInformationRTMethodDesc);
    JNI_ERR_IF_NULL(jSetExitInformationReturnParmsConstructor);

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(TP_TX_RRS_JAVA_ENV_REG_EXIT),
                    "tx_rrs_services_jni.javaEnvironmentRegistration. Exit",
                    TRACE_DATA_END_PARMS);
    }

    return JNI_OK;
}

/**
 * Deletes a global ref.
 *
 * @param env The JNI environment for the calling thread.
 * @param ref_p A pointer to the global reference to delete.
 */
static void deleteGlobalRef(JNIEnv* env, jobject* ref_p) {
    if (*ref_p != NULL) {
        (*env)->DeleteGlobalRef(env, *ref_p);
        *ref_p = NULL;
    }
}

/**
 * Deregistration callback used java object reference cleanup.
 *
 * @param env The calling thread's JNI environment.
 * @param clazz The class for which deregistration is taking place.
 * @param extraInfo The context provided to the registration function.
 *
 * @return JNI_OK
 */
int javaEnvironmentDeregistration(JNIEnv* env, jclass clazz, jobjectArray extraInfo) {

    if (TraceActive(trc_level_detailed)) {
    TraceRecord(trc_level_detailed,
                TP(TP_TX_RRS_JAVA_ENV_DEREG_ENTRY),
                "tx_rrs_services_jni.javaEnvironmentDeregistration. Entry",
                TRACE_DATA_END_PARMS);
    }

    // Delete all global references obtained previously.
    deleteGlobalRef(env, &jExpressIntReturnParmsClass);
    deleteGlobalRef(env, &jRetLogNameReturnParmsClass);
    deleteGlobalRef(env, &jRetSideInfoFastReturnParmsClass);
    deleteGlobalRef(env, &jRetSideInfoReturnParmsClass);
    deleteGlobalRef(env, &jRetURDataReturnParmsClass);
    deleteGlobalRef(env, &jRetURInterestReturnParmsClass);
    deleteGlobalRef(env, &jRetWorkIdReturnParmsClass);
    deleteGlobalRef(env, &jSwitchCtxReturnParmsClass);
    deleteGlobalRef(env, &jRegResMgrReturnParmsClass);
    deleteGlobalRef(env, &jBegingCtxReturnParmsClass);
    deleteGlobalRef(env, &jBeginTranReturnParmsClass);
    deleteGlobalRef(env, &jRetrieveCurrentCtxTokenReturnParmsClass);
    deleteGlobalRef(env, &jPrepareAgentURReturnParmsClass);
    deleteGlobalRef(env, &jRegistryExceptionClass);
    deleteGlobalRef(env, &jRetrieveRMMetadataReturnParmsClass);
    deleteGlobalRef(env, &jSetExitInformationReturnParmsClass);
    
    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(TP_TX_RRS_JAVA_ENV_DEREG_EXIT),
                    "tx_rrs_services_jni.javaEnvironmentDeregistration. Exit",
                    TRACE_DATA_END_PARMS);
    }

    return JNI_OK;
}

//-----------------------------------------------------------------------------
// Generic Functions.
//-----------------------------------------------------------------------------

/**
 * Copies a jbyteArray to the supplied local storage.
 * If false is returned, the JVM has an exception pending.
 * The caller should return control to the JVM immediately.
 *
 * @param source The source byte array.
 * @param destination The destination array pointer.
 * @param length The amount of bytes to copy from the source array.
 * @param env The JNIEnv pointer.
 *
 * @return 1 if the data was copied was successfully. 0 if the JVM has an exception pending, or
 *         the source NULL, or the destination is NULL.
 *
 */
int getByteArray(jbyteArray source,
                 char* destination,
                 jint length,
                 JNIEnv* env) {
    if (source == NULL) {
        throwBaseJavaException(env, "java/lang/IllegalArgumentException", "getByteArray: source cannot be null");
        return 0;
    }

    if (destination == NULL) {
        throwBaseJavaException(env, "java/lang/OutOfMemoryError", "getByteArray: destination could not be allocated");
        return 0;
    }

    (*env)->GetByteArrayRegion(env, source, 0, length, (jbyte*) destination);
    if ((*env)->ExceptionCheck(env) == JNI_TRUE) {
        (*env)->ExceptionDescribe(env);
        return 0;
    }

    return 1;
}

/**
 * Copies a jintArray to the supplied local storage.
 * If false is returned, the JVM has an exception pending.
 * The caller should return control to the JVM immediately.
 *
 * @param source The source byte array.
 * @param destination The destination array pointer.
 * @param length The amount of bytes to copy from the source array.
 * @param env The JNIEnv pointer.
 *
 * @return 1 if the data was copied was successfully. 0 if the JVM has an exception pending.
 */
int getIntArray(jintArray source,
                int* destination,
                jint length,
                JNIEnv* env) {
    if (source == NULL) {
        throwBaseJavaException(env, "java/lang/IllegalArgumentException", "getIntArray: source cannot be null");
        return 0;
    }

    if (destination == NULL) {
        throwBaseJavaException(env, "java/lang/OutOfMemoryError", "getIntArray: destination could not be allocated");
        return 0;
    }

    (*env)->GetIntArrayRegion(env, source, 0, length, (jint*) destination);
    if ((*env)->ExceptionCheck(env) == JNI_TRUE) {
        (*env)->ExceptionDescribe(env);
        return 0;
    }

    return 1;
}

//-----------------------------------------------------------------------------
// RRS service calls that do NOT require key 0-7 or supervisor state.
//-----------------------------------------------------------------------------

//-----------------------------------------------------------------------------
// Begins a Transaction.
//-----------------------------------------------------------------------------
JNIEXPORT jobject JNICALL
ntv_beginTransaction(JNIEnv* env,
                     jclass clazz,
                     jint transactionMode) {

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(TP_TX_RRS_JNI_ATR4BEG_ENTRY),
                    "tx_rrs_services_jni.ntv_beginTransaction. Entry",
                    TRACE_DATA_END_PARMS);
    }

    atr_return_code rrs_rc;
    jobject returnObj = NULL;
    atr_diag_area diagArea;
    atr_ur_token urToken;
    atr_urid urid;

    atr4beg(&rrs_rc, diagArea, transactionMode, urToken, urid);

    // Package the service's output data into a jobject.
    jbyteArray jUrToken = NULL;
    jbyteArray jUrid = NULL;

    if (rrs_rc == ATR_OK) {
        jUrToken = (*env)->NewByteArray(env, sizeof(atr_ur_token));
        CHECK_JAVA_EXCEPTION_INPUT(env, jUrToken);
        (*env)->SetByteArrayRegion(env, jUrToken, 0, sizeof(atr_ur_token), (jbyte*) urToken);
        CHECK_JAVA_EXCEPTION(env);

        jUrid = (*env)->NewByteArray(env, sizeof(atr_urid));
        CHECK_JAVA_EXCEPTION_INPUT(env, jUrid);
        (*env)->SetByteArrayRegion(env, jUrid, 0, sizeof(atr_urid), (jbyte*) urid);
        CHECK_JAVA_EXCEPTION(env);
    }

    returnObj = (*env)->NewObject(env,
                                  jBeginTranReturnParmsClass,
                                  jBeginTranReturnParmsConstructor,
                                  (jint) rrs_rc,
                                  jUrToken,
                                  jUrid);
    CHECK_JAVA_EXCEPTION_INPUT(env, returnObj);

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(TP_TX_RRS_JNI_ATR4BEGC_EXIT),
                    "tx_rrs_services_jni.ntv_beginTransaction. Exit",
                    TRACE_DATA_INT(rrs_rc, "RRS Return Code"),
                    TRACE_DATA_RAWDATA(sizeof(atr_ur_token), urToken, "URToken"),
                    TRACE_DATA_RAWDATA(sizeof(atr_urid), urid, "URID"),
                    TRACE_DATA_RAWDATA(sizeof(atr_diag_area), diagArea, "Diagnostic area"),
                    TRACE_DATA_END_PARMS);
    }

    return returnObj;
}

//-----------------------------------------------------------------------------
// Completes the unit of recovery associated with provided URToken.
//-----------------------------------------------------------------------------
JNIEXPORT jint JNICALL
ntv_endUR(JNIEnv* env,
          jclass clazz,
          jint action,
          jbyteArray currentUrToken) {

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(TP_TX_RRS_JNI_ATR4END_ENTRY),
                    "tx_rrs_services_jni.ntv_endUR. Entry",
                    TRACE_DATA_END_PARMS);
    }

    atr_return_code rrs_rc = -1;
    atr_diag_area diagArea;
    atr_ur_token urToken;
    if (currentUrToken != NULL) {
        int gotBytes = getByteArray(currentUrToken, urToken, sizeof(atr_ur_token), env);
        if (!gotBytes) {
            return rrs_rc;
        }
    } else {
        memset(urToken, 0x00, sizeof(atr_ur_token));
    }

    atr4end(&rrs_rc, diagArea, action, urToken);

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(TP_TX_RRS_JNI_ATR4END_EXIT),
                    "tx_rrs_services_jni.ntv_endUR. Exit",
                    TRACE_DATA_INT(rrs_rc, "RRS Return Code"),
                    TRACE_DATA_RAWDATA(sizeof(atr_diag_area), diagArea, "Diagnostic area"),
                    TRACE_DATA_END_PARMS);
    }

    return rrs_rc;
}

//-----------------------------------------------------------------------------
// Rolls back the UR on the current thread.
//-----------------------------------------------------------------------------
JNIEXPORT jint JNICALL
ntv_backoutUR(JNIEnv* env,
              jclass clazz) {

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(TP_TX_RRS_JNI_ATR4BACK_ENTRY),
                    "tx_rrs_services_jni.ntv_backoutUR. Entry",
                    TRACE_DATA_END_PARMS);
    }

    atr_return_code rrs_rc;

    atr4back(&rrs_rc);

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(TP_TX_RRS_JNI_ATR4BACK_EXIT),
                    "tx_rrs_services_jni.ntv_backoutUR. Exit",
                    TRACE_DATA_INT(rrs_rc, "RRS Return Code"),
                    TRACE_DATA_END_PARMS);
    }

    return rrs_rc;
}

//-----------------------------------------------------------------------------
// Retrieves UR related attributes.
//-----------------------------------------------------------------------------
JNIEXPORT jobject JNICALL
ntv_retireveSideInformationFast(JNIEnv* env,
                                jclass clazz,
                                jbyteArray ctxToken,
                                jint infoOptions) {

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(TP_TX_RRS_JNI_ATR4RUSF_ENTRY),
                    "tx_rrs_services_jni.ntv_retireveSideInformationFast. Entry",
                    TRACE_DATA_END_PARMS);
    }

    jobject returnObj = NULL;
    atr_return_code rrs_rc;
    char localCtxToken[sizeof(atr_context_token)];

    int gotBytes = getByteArray(ctxToken, localCtxToken, sizeof(atr_context_token), env);
    if (!gotBytes) {
        return returnObj;
    }

    atr_environ_info env_info;

    atr4rusf(&rrs_rc, localCtxToken, &env_info, infoOptions);

    // Package the service's output data into a jobject.
    returnObj = (*env)->NewObject(env,
                                  jRetSideInfoFastReturnParmsClass,
                                  jRetSideInfoFastReturnParmsConstructor,
                                  (jint) rrs_rc,
                                  (jint) env_info);
    CHECK_JAVA_EXCEPTION_INPUT(env, returnObj);

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(TP_TX_RRS_JNI_ATR4RUSF_EXIT),
                    "tx_rrs_services_jni.ntv_retireveSideInformationFast. Exit",
                    TRACE_DATA_INT(rrs_rc, "RRS Return Code"),
                    TRACE_DATA_INT(env_info, "Environment Info"),
                    TRACE_DATA_END_PARMS);
    }

    return returnObj;
}

//-----------------------------------------------------------------------------
// Retrieves data for a UR.
//-----------------------------------------------------------------------------
JNIEXPORT jobject JNICALL
ntv_retireveURData(JNIEnv* env,
                   jclass clazz,
                   jbyteArray uriToken,
                   jint stateOptions) {

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(TP_TX_RRS_JNI_ATR4RURD_ENTRY),
                    "tx_rrs_services_jni.ntv_retireveURData. Entry",
                    TRACE_DATA_END_PARMS);
    }

    jobject returnObj = NULL;
    atr_return_code rrs_rc;

    char localUriToken[sizeof(atr_uri_token)];
    int gotBytes = getByteArray(uriToken, localUriToken, sizeof(atr_uri_token), env);
    if (!gotBytes) {
        return returnObj;
    }

    atr_urid urid;
    atr_ur_state urState;
    atr_ur_token ur_token;

    atr4rurd(&rrs_rc, localUriToken, urid, &urState, stateOptions, ur_token);

    // Package the service's output data into a jobject.
    jbyteArray jUrid = NULL;
    jbyteArray jUrToken = NULL;

    if (rrs_rc == ATR_OK) {
        jUrid = (*env)->NewByteArray(env, sizeof(atr_urid));
        CHECK_JAVA_EXCEPTION_INPUT(env, jUrid);
        (*env)->SetByteArrayRegion(env, jUrid, 0, sizeof(atr_urid), (jbyte*) urid);
        CHECK_JAVA_EXCEPTION(env);

        jUrToken = (*env)->NewByteArray(env, sizeof(atr_ur_token));
        CHECK_JAVA_EXCEPTION_INPUT(env, jUrToken);
        (*env)->SetByteArrayRegion(env, jUrToken, 0, sizeof(atr_ur_token), (jbyte*) ur_token);
        CHECK_JAVA_EXCEPTION(env);
    }

    returnObj = (*env)->NewObject(env,
                                  jRetURDataReturnParmsClass,
                                  jRetURDataReturnParmsConstructor,
                                  (jint)rrs_rc,
                                  jUrid,
                                  (jint)urState,
                                  jUrToken);
    CHECK_JAVA_EXCEPTION_INPUT(env, returnObj);

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(TP_TX_RRS_JNI_ATR4RURD_EXIT),
                    "tx_rrs_services_jni.ntv_retireveURData. Exit",
                    TRACE_DATA_INT(rrs_rc, "RRS Return Code"),
                    TRACE_DATA_INT(urState, "URState"),
                    TRACE_DATA_RAWDATA(sizeof(atr_urid), urid, "URID"),
                    TRACE_DATA_RAWDATA(sizeof(atr_ur_token), ur_token, "URToken"),
                    TRACE_DATA_END_PARMS);
    }

    return returnObj;
}

//-----------------------------------------------------------------------------
// RRS service calls that require key 0-7 or supervisor state.
//-----------------------------------------------------------------------------

//-----------------------------------------------------------------------------
// Register a Resource manager with registration services.
//-----------------------------------------------------------------------------
JNIEXPORT jobject JNICALL
ntv_registerResourceManager(JNIEnv* env,
                            jclass clazz,
                            jint unregOption,
                            jbyteArray globalData,
                            jbyteArray rmNamePrefix,
                            jint rmNamePrefixLength,
                            jbyteArray rmNameSTCK) {

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(TP_TX_RRS_JNI_CRG4GRM_ENTRY),
                    "tx_rrs_services_jni.ntv_registerResourceManager. Entry",
                    TRACE_DATA_END_PARMS);
    }

    int rc = 0;
    int internalAuthCheckRc = 0;
    int safRc = 0;
    int racfRc = 0;
    int racfRsn = 0;

    // Get the authorized server PC stub to be able to call authorized services.
    const server_authorized_function_stubs* auth_stubs_p = getServerAuthorizedFunctionStubs();
    if (auth_stubs_p == NULL) {
        throwBaseJavaException(env, "java/lang/RuntimeException", "Could not find authorized function stubs");
        return NULL;
    }

    // Validate prefix input length. It must always be greater than 0.
    if (rmNamePrefixLength < 1 || rmNamePrefixLength > MAX_RMNAME_PREFIX_LENGTH) {
        char excMessage[256];
        snprintf(excMessage, 256, "The rmNamePrefixLength input of %i to registerResourceManager is invalid. The maximum allowed length is %i bytes", rmNamePrefixLength, MAX_RMNAME_PREFIX_LENGTH);
        throwBaseJavaException(env, "java/lang/RuntimeException", excMessage);
        return NULL;
    }

    char prefix[rmNamePrefixLength];
    if (getByteArray(rmNamePrefix, prefix, rmNamePrefixLength, env) == 0) {
            return NULL;
    }

    char * rmNameSTCK_p = NULL;
    char stck[RMNAME_STCK_LENGTH];
    if (rmNameSTCK != NULL) {
        if (getByteArray(rmNameSTCK, stck, RMNAME_STCK_LENGTH, env) == 0) {
            return NULL;
        }
        rmNameSTCK_p = stck;
    }

    crg_resource_manager_token rmToken;
    RegistryToken rmRegistryToken;
    crg_resource_manager_name rmName;
    RegistryToken rmNameRegistryToken;

    struct crg4grm_parms p = {
        .returnCode = &rc,
        .resMgrName = rmName,
        .resMgrNameRegistryToken = &rmNameRegistryToken,
        .resMgrToken = rmToken,
        .resMgrRegistryToken = &rmRegistryToken,
        .unregisterOption = unregOption,
        .rmNamePrefix = prefix,
        .rmNamePrefixLength = rmNamePrefixLength,
        .rmNameSTCK = rmNameSTCK_p,
        .internalAuthCheckRc = &internalAuthCheckRc,
        .safRc = &safRc,
        .racfRc = &racfRc,
        .racfRsn = &racfRsn
    };

    if (getByteArray(globalData, p.crgGlobalData, sizeof(crg_rm_global_data), env) == 0) {
        return NULL;
    }

    // Build the parameter list for the BPX4IPT wrapper service.
    DriveAuthorizedServiceOnIPTParms_t iptParms;
    iptParms.pcReturnCode = -1;
    iptParms.parmStructSize = sizeof(p);
    iptParms.authRoutine_p = (void*)&(auth_stubs_p->crg_register_resource_manager);
    iptParms.parmStruct_p = &p;

    // Drive the IPT wrapper service.
    int bpx_rc = -1, bpx_rsn = -1, bpx_rv = -1;
    int wrapper_rc = driveAuthorizedServiceOnIPT(&iptParms, &bpx_rv, &bpx_rc, &bpx_rsn);

    if ((wrapper_rc != 0) || (bpx_rv != 0) || (iptParms.pcReturnCode != 0)) {
        if (TraceActive(trc_level_exception)) {
            TraceRecord(trc_level_exception,
                        TP(TP_TX_RRS_JNI_CRG4GRM_IPT_BAD_RC),
                        "tx_rrs_services_jni.ntv_registerResourceManager. error calling BPX4IPT",
                        TRACE_DATA_INT(wrapper_rc, "RC"),
                        TRACE_DATA_INT(bpx_rv, "BPX4IPT RV"),
                        TRACE_DATA_INT(bpx_rc, "BPX4IPT RC (dec)"),
                        TRACE_DATA_HEX_INT(bpx_rsn, "BPX4IPT RSN (hex)"),
                        TRACE_DATA_INT(iptParms.pcReturnCode, "Authorized PC Return Code"),
                        TRACE_DATA_END_PARMS);
        }

        char excMessage[512];
        snprintf(excMessage, 512, "Error driving CRG4GRM on IPT: RC %i, BPX4IPT RV %x, BPX4IPT RC %x, BPX4IPT RSN %x, PC RC %i", wrapper_rc, bpx_rv, bpx_rc, bpx_rsn, iptParms.pcReturnCode);
        throwBaseJavaException(env, "java/lang/RuntimeException", excMessage);
        return NULL;
    }

    // Check for internal failures during the invocation of the authorized code.
    if (rc == -1) {
        throwBaseJavaException(env, "java/lang/RuntimeException", "Failure detected during the invocation authorized routine to register the resource manager");
        return NULL;
    }

    // Package the service's output data into a jobject.
    jbyteArray jRMToken = NULL;
    jbyteArray jRMRegistryToken = NULL;
    jbyteArray jRMName = NULL;
    jbyteArray jRMNameRegistryToken = NULL;

    if (rc == ATR_OK) {
        jRMName = (*env)->NewByteArray(env, sizeof(crg_resource_manager_name));
        CHECK_JAVA_EXCEPTION_INPUT(env, jRMName);

        (*env)->SetByteArrayRegion(env, jRMName, 0, sizeof(crg_resource_manager_name), (jbyte*) rmName);
        CHECK_JAVA_EXCEPTION(env);

        jRMNameRegistryToken = (*env)->NewByteArray(env, sizeof(RegistryToken));
        CHECK_JAVA_EXCEPTION_INPUT(env, jRMNameRegistryToken);

        (*env)->SetByteArrayRegion(env, jRMNameRegistryToken, 0, sizeof(RegistryToken), (jbyte*) &rmNameRegistryToken);
        CHECK_JAVA_EXCEPTION(env);

        jRMToken = (*env)->NewByteArray(env, sizeof(crg_resource_manager_token));
        CHECK_JAVA_EXCEPTION_INPUT(env, jRMToken);

        (*env)->SetByteArrayRegion(env, jRMToken, 0, sizeof(crg_resource_manager_token), (jbyte*) rmToken);
        CHECK_JAVA_EXCEPTION(env);

        jRMRegistryToken = (*env)->NewByteArray(env, sizeof(RegistryToken));
        CHECK_JAVA_EXCEPTION_INPUT(env, jRMRegistryToken);

        (*env)->SetByteArrayRegion(env, jRMRegistryToken, 0, sizeof(RegistryToken), (jbyte*) &rmRegistryToken);
        CHECK_JAVA_EXCEPTION(env);
    }

    jobject returnObj = (*env)->NewObject(env,
                                          jRegResMgrReturnParmsClass,
                                          jRegResMgrReturnParmsConstructor,
                                          (jint) rc,
                                          jRMName,
                                          jRMNameRegistryToken,
                                          jRMToken,
                                          jRMRegistryToken,
                                          (jint)internalAuthCheckRc,
                                          (jint)safRc,
                                          (jint)racfRc,
                                          (jint)racfRsn);
    CHECK_JAVA_EXCEPTION_INPUT(env, returnObj);

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(TP_TX_RRS_JNI_CRG4GRM_EXIT),
                    "tx_rrs_services_jni.ntv_registerResourceManager. Exit",
                    TRACE_DATA_INT(rc, "Return Code"),
                    TRACE_DATA_INT(internalAuthCheckRc, "InternalAuthCheckRC"),
                    TRACE_DATA_INT(safRc, "SAFAuthenticationRC"),
                    TRACE_DATA_INT(racfRc, "RACFAuthenticationRC"),
                    TRACE_DATA_INT(racfRsn, "RACFAuthenticationRSNCode"),
                    TRACE_DATA_RAWDATA(sizeof(crg_resource_manager_name), rmName, "RMName"),
                    TRACE_DATA_RAWDATA(sizeof(RegistryToken), &rmNameRegistryToken, "RMNameRegistryToken"),
                    TRACE_DATA_RAWDATA(sizeof(crg_resource_manager_token), rmToken, "RMToken"),
                    TRACE_DATA_RAWDATA(sizeof(RegistryToken), &rmRegistryToken, "RMRegistryToken"),
                    TRACE_DATA_END_PARMS);
    }

    return returnObj;
}

//-----------------------------------------------------------------------------
// Unregister a Resource manager with registration services.
//-----------------------------------------------------------------------------
JNIEXPORT jint JNICALL
ntv_unregisterResourceManager(JNIEnv* env,
                              jclass clazz,
                              jbyteArray rmNameRegistryToken,
                              jbyteArray rmRegistryToken) {
    int traceLevel = trc_level_detailed;
    if (TraceActive(traceLevel)) {
        TraceRecord(traceLevel,
                    TP(TP_TX_RRS_JNI_CRG4DRM_ENTRY),
                    "tx_rrs_services_jni.ntv_unregisterResourceManager. Entry",
                    TRACE_DATA_END_PARMS);
    }

    int rrs_rc = -1;
    int resMgrNameRegistryRC = 0;
    int resMgrTokenRegistryRC = 0;

    // Get the authorized server PC stub to be able to call authorized services.
    const server_authorized_function_stubs* auth_stubs_p = getServerAuthorizedFunctionStubs();
    if (auth_stubs_p == NULL) {
        throwBaseJavaException(env, "java/lang/RuntimeException", "Could not find authorized function stubs");
        return -1;
    }

    struct crg4drm_parms p = {
        .returnCode = &rrs_rc,
        .resMgrNameRegistryReturnCode = &resMgrNameRegistryRC,
        .resMgrTokenRegistryReturnCode = &resMgrTokenRegistryRC
    };

    if (getByteArray(rmRegistryToken, (char*)&(p.resMgrToken), sizeof(RegistryToken), env) == 0) {
        return -1;
    }

    if (getByteArray(rmNameRegistryToken, (char*)&(p.resMgrNameRegistryToken), sizeof(RegistryToken), env) == 0) {
        return -1;
    }

    // PC to the authorized metal C code to drive the service.
    int pc_rc = auth_stubs_p->crg_unregister_resource_manager(&p);
    if (pc_rc != 0 || (resMgrNameRegistryRC != 0) || (resMgrTokenRegistryRC != 0)) traceLevel = trc_level_exception;

    if (TraceActive(traceLevel)) {
        TraceRecord(traceLevel,
                    TP(TP_TX_RRS_JNI_CRG4DRM_EXIT),
                    "tx_rrs_services_jni.ntv_unregisterResourceManager. Exit",
                    TRACE_DATA_INT(pc_rc, "Authorized PC Return Code"),
                    TRACE_DATA_INT(rrs_rc, "RRS Return Code"),
                    TRACE_DATA_INT(resMgrNameRegistryRC, "Resource Manager Name Registry Return Code"),
                    TRACE_DATA_INT(resMgrTokenRegistryRC, "Resource Manager Token Registry Return Code"),
                    TRACE_DATA_END_PARMS);
    }

    if (pc_rc != 0) {
        throwBaseJavaException(env, "java/lang/RuntimeException", "Bad PC Return Code");
        return -1;
    }

    if (resMgrNameRegistryRC != 0) {
        throwBaseJavaException(env, "java/lang/RuntimeException", "Bad Registry Return Code For Resource Manager Name");
        return -1;
    }

    if (resMgrTokenRegistryRC != 0) {
        throwBaseJavaException(env, "java/lang/RuntimeException", "Bad Registry Return Code For Resource Manager Token");
        return -1;
    }

    return rrs_rc;
}

//-----------------------------------------------------------------------------
// Set exit information.
//-----------------------------------------------------------------------------
JNIEXPORT jobject JNICALL
ntv_setExitInformation(JNIEnv* env,
                       jclass clazz,
                       jbyteArray rmNameRegistryToken,
                       jbyteArray rmRegistryToken,
                       jboolean recovery) {
    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(TP_TX_RRS_JNI_CRG4SEIF_ENTRY),
                    "tx_rrs_services_jni.ntv_setExitInformation. Entry",
                    TRACE_DATA_END_PARMS);
    }

    atr_return_code rrs_rc = -1;
    int resMgrNameRegistryRC = 0;
    int resMgrTokenRegistryRC = 0;
    int metaDataLoggingAllowed = FALSE;

    // Get the authorized server PC stub to be able to call authorized services.
    const server_authorized_function_stubs* auth_stubs_p = getServerAuthorizedFunctionStubs();
    if (auth_stubs_p == NULL) {
        throwBaseJavaException(env, "java/lang/RuntimeException", "Could not find authorized function stubs");
        return NULL;
    }

    struct crg4seif_parms p = {
        .returnCode = &rrs_rc,
        .resMgrNameRegistryReturnCode = &resMgrNameRegistryRC,
        .resMgrTokenRegistryReturnCode = &resMgrTokenRegistryRC,
        .metaDataLoggingAllowed = &metaDataLoggingAllowed,
        .recovery = recovery
    };

    if (getByteArray(rmNameRegistryToken, (char*)&(p.resMgrNameRegistryToken), sizeof(RegistryToken), env) == 0) {
        return NULL;
    }

    if (getByteArray(rmRegistryToken, (char*)&(p.resMgrToken), sizeof(RegistryToken), env) == 0) {
        return NULL;
    }

    // PC to the authorized metal C code to drive the service.
    int pc_rc = auth_stubs_p->atr_set_exit_information(&p);

    if (pc_rc != 0) {
        if (TraceActive(trc_level_exception)) {
            TraceRecord(trc_level_exception,
                        TP(TP_TX_RRS_JNI_ATR4SEIF_BAD_PC_RC),
                        "tx_rrs_services_jni.ntv_setExitInformation. Bad PC RC",
                        TRACE_DATA_INT(pc_rc, "Authorized PC Return Code"),
                        TRACE_DATA_END_PARMS);
        }
        throwBaseJavaException(env, "java/lang/RuntimeException", "Bad PC Return Code");
        return NULL;
    }

    if (resMgrNameRegistryRC != 0) {
        if (TraceActive(trc_level_exception)) {
            TraceRecord(trc_level_exception,
                        TP(TP_TX_RRS_JNI_ATR4SEIF_BAD_REGISTRY_RM_NAME_RC),
                        "tx_rrs_services_jni.ntv_setExitInformation. Bad registry RC",
                        TRACE_DATA_INT(resMgrNameRegistryRC, "Resource manager name registry Return Code"),
                        TRACE_DATA_END_PARMS);
        }
        throwBaseJavaException(env, "java/lang/RuntimeException", "Bad registry return code for resource manager name");
        return NULL;
    }

    if (resMgrTokenRegistryRC != 0) {
        if (TraceActive(trc_level_exception)) {
            TraceRecord(trc_level_exception,
                        TP(TP_TX_RRS_JNI_ATR4SEIF_BAD_REGISTRY_RM_TOKEN_RC),
                        "tx_rrs_services_jni.ntv_setExitInformation. Bad registry RC",
                        TRACE_DATA_INT(resMgrTokenRegistryRC, "Resource manager token registry Return Code"),
                        TRACE_DATA_END_PARMS);
        }
        throwBaseJavaException(env, "java/lang/RuntimeException", "Bad registry return code for resource manager token");
        return NULL;
    }

    //-------------------------------------------------------------------------
    // Package the output data into a jobject.
    //-------------------------------------------------------------------------
    jobject returnObj = (*env)->NewObject(env,
                                          jSetExitInformationReturnParmsClass,
                                          jSetExitInformationReturnParmsConstructor,
                                          (jint) rrs_rc,
                                          (jboolean) metaDataLoggingAllowed);

    CHECK_JAVA_EXCEPTION_INPUT(env, returnObj);

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(TP_TX_RRS_JNI_CRG4SEIF_EXIT),
                    "tx_rrs_services_jni.ntv_setExitInformation. Exit",
                    TRACE_DATA_INT(pc_rc, "Authorized PC Return Code"),
                    TRACE_DATA_INT(rrs_rc, "RRS Return Code"),
                    TRACE_DATA_INT(metaDataLoggingAllowed, "MetaDataLoggingAllowed"),
                    TRACE_DATA_END_PARMS);
    }

    return returnObj;
}

//-----------------------------------------------------------------------------
// Begin restart with RRS
//-----------------------------------------------------------------------------
JNIEXPORT jint JNICALL
ntv_beginRestart(JNIEnv* env,
                 jclass clazz,
                 jbyteArray rmRegistryToken) {
    int traceLevel = trc_level_detailed;
    if (TraceActive(traceLevel)) {
        TraceRecord(traceLevel,
                    TP(TP_TX_RRS_JNI_ATR4IBRS_ENTRY),
                    "tx_rrs_services_jni.ntv_beginRestart. Entry",
                    TRACE_DATA_END_PARMS);
    }

    int rrs_rc = -1;
    int resMgrTokenRegistryRC = 0;

    // Get the authorized server PC stub to be able to call authorized services.
    const server_authorized_function_stubs* auth_stubs_p = getServerAuthorizedFunctionStubs();
    if (auth_stubs_p == NULL) {
        throwBaseJavaException(env, "java/lang/RuntimeException", "Could not find authorized function stubs");
        return rrs_rc;
    }

    struct atr4ibrs_parms p = {
        .returnCode = &rrs_rc,
        .resMgrTokenRegistryReturnCode = &resMgrTokenRegistryRC
    };

    if (getByteArray(rmRegistryToken, (char*)&(p.resMgrToken), sizeof(RegistryToken), env) == 0) {
        return -1;
    }

    // PC to the authorized metal C code to drive the service.
    int pc_rc = auth_stubs_p->atr_begin_restart(&p);
    if (pc_rc != 0 || (resMgrTokenRegistryRC != 0)) traceLevel = trc_level_exception;

    if (TraceActive(traceLevel)) {
        TraceRecord(traceLevel,
                    TP(TP_TX_RRS_JNI_ATR4IBRS_EXIT),
                    "tx_rrs_services_jni.ntv_beginRestart. Exit",
                    TRACE_DATA_INT(pc_rc, "Authorized PC Return Code"),
                    TRACE_DATA_INT(rrs_rc, "RRS Return Code"),
                    TRACE_DATA_INT(resMgrTokenRegistryRC, "Registry Return Code"),
                    TRACE_DATA_END_PARMS);
    }

    if (pc_rc != 0) {
        throwBaseJavaException(env, "java/lang/RuntimeException", "Base PC return code");
        return -1;
    }

    if (resMgrTokenRegistryRC != 0) {
        throwBaseJavaException(env, "java/lang/RuntimeException", "Bad registry return code for resource manager token");
        return -1;
    }

    return rrs_rc;
}

//-----------------------------------------------------------------------------
// End restart with RRS
//-----------------------------------------------------------------------------
JNIEXPORT jint JNICALL
ntv_endRestart(JNIEnv* env,
               jclass clazz,
               jbyteArray rmRegistryToken) {
    int traceLevel = trc_level_detailed;
    if (TraceActive(traceLevel)) {
        TraceRecord(traceLevel,
                    TP(TP_TX_RRS_JNI_ATR4IERS_ENTRY),
                    "tx_rrs_services_jni.ntv_endRestart. Entry",
                    TRACE_DATA_END_PARMS);
    }

    int rrs_rc = -1;
    int resMgrTokenRegistryRC = 0;

    // Get the authorized server PC stub to be able to call authorized services.
    const server_authorized_function_stubs* auth_stubs_p = getServerAuthorizedFunctionStubs();
    if (auth_stubs_p == NULL) {
        throwBaseJavaException(env, "java/lang/RuntimeException", "Could not find authorized function stubs");
        return -1;
    }

    struct atr4iers_parms p = {
        .returnCode = &rrs_rc,
        .resMgrTokenRegistryReturnCode = &resMgrTokenRegistryRC
    };

    if (getByteArray(rmRegistryToken, (char*)&(p.resMgrToken), sizeof(RegistryToken), env) == 0) {
        return -1;
    }

    // PC to the authorized metal C code to drive the service.
    int pc_rc = auth_stubs_p->atr_end_restart(&p);
    if (pc_rc != 0 || (resMgrTokenRegistryRC != 0)) traceLevel = trc_level_exception;

    if (TraceActive(traceLevel)) {
        TraceRecord(traceLevel,
                    TP(TP_TX_RRS_JNI_ATR4IERS_EXIT),
                    "tx_rrs_services_jni.ntv_endRestart. Exit",
                    TRACE_DATA_INT(pc_rc, "Authorized PC Return Code"),
                    TRACE_DATA_INT(rrs_rc, "RRS Return Code"),
                    TRACE_DATA_INT(resMgrTokenRegistryRC, "Registry Return Code"),
                    TRACE_DATA_END_PARMS);
    }

    if (pc_rc != 0) {
        throwBaseJavaException(env, "java/lang/RuntimeException", "Bad PC return code");
        return -1;
    }

    if (resMgrTokenRegistryRC != 0) {
        throwBaseJavaException(env, "java/lang/RuntimeException", "Bad registry return code for resource manager token");
        return -1;
    }

    return rrs_rc;
}

//-----------------------------------------------------------------------------
// Retrieve log name from RRS.
//-----------------------------------------------------------------------------
JNIEXPORT jobject JNICALL
ntv_retrieveLogName(JNIEnv* env,
                    jclass clazz,
                    jbyteArray rmRegistryToken) {

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(TP_TX_RRS_JNI_ATR4IRLN_ENTRY),
                    "tx_rrs_services_jni.ntv_retrieveLogName. Entry",
                    TRACE_DATA_END_PARMS);
    }

    atr_return_code rrs_rc = -1;
    int resMgrTokenRegistryRC = 0;

    // Get the authorized server PC stub to be able to call authorized services.
    const server_authorized_function_stubs* auth_stubs_p = getServerAuthorizedFunctionStubs();
    if (auth_stubs_p == NULL) {
        throwBaseJavaException(env, "java/lang/RuntimeException", "Could not find authorized function stubs");
        return NULL;
    }

    atr_rm_logname_length rrsLogNameLen = 0;
    atr_rm_logname_length rmLogNameLen = 0;
    char rrs_Logname[ATR_MAX_RM_LOGNAME_LENGTH];
    char rm_LogName[ATR_MAX_RM_LOGNAME_LENGTH];

    struct atr4irln_parms p = {
        .returnCode = &rrs_rc,
        .resMgrTokenRegistryReturnCode = &resMgrTokenRegistryRC,
        .resMgrLogNameBuffLen = ATR_MAX_RM_LOGNAME_LENGTH,
        .resMgrLogNameLength = &rmLogNameLen,
        .resMgrLogName = rm_LogName,
        .rrsLogNameLength = &rrsLogNameLen,
        .rrsLogName = rrs_Logname
    };

    if (getByteArray(rmRegistryToken, (char*)&(p.resMgrToken), sizeof(RegistryToken), env) == 0) {
        return NULL;
    }

    // PC to the authorized metal C code to drive the service.
    int pc_rc = auth_stubs_p->atr_retrieve_log_name(&p);

    if (pc_rc != 0) {
        if (TraceActive(trc_level_exception)) {
            TraceRecord(trc_level_exception,
                        TP(TP_TX_RRS_JNI_ATR4IRLN_BAD_PC_RC),
                        "tx_rrs_services_jni.ntv_retrieveLogName. Bad PC RC",
                        TRACE_DATA_INT(pc_rc, "Authorized PC Return Code"),
                        TRACE_DATA_END_PARMS);
        }
        throwBaseJavaException(env, "java/lang/RuntimeException", "Bad PC return code");
        return NULL;
    }

    if (resMgrTokenRegistryRC != 0) {
        if (TraceActive(trc_level_exception)) {
            TraceRecord(trc_level_exception,
                        TP(TP_TX_RRS_JNI_ATR4IRLN_BAD_REGISTRY_RM_TOKEN_RC),
                        "tx_rrs_services_jni.ntv_retrieveLogName. Bad registry RC",
                        TRACE_DATA_INT(resMgrTokenRegistryRC, "Resource manager token registry Return Code"),
                        TRACE_DATA_END_PARMS);
        }
        throwBaseJavaException(env, "java/lang/RuntimeException", "Bad registry return code for resource manager token");
        return NULL;
    }

    //-------------------------------------------------------------------------
    // Package the service's output data into a jobject.
    //-------------------------------------------------------------------------
    jbyteArray jRMLogName = NULL;
    jbyteArray jRRSLogName = NULL;

    if (rrs_rc == ATR_OK) {
        jRMLogName = (*env)->NewByteArray(env, rmLogNameLen);
        CHECK_JAVA_EXCEPTION_INPUT(env, jRMLogName);
        (*env)->SetByteArrayRegion(env, jRMLogName, 0, rmLogNameLen, (jbyte*) rm_LogName);
        CHECK_JAVA_EXCEPTION(env);

        jRRSLogName = (*env)->NewByteArray(env, rrsLogNameLen);
        CHECK_JAVA_EXCEPTION_INPUT(env, jRRSLogName);
        (*env)->SetByteArrayRegion(env, jRRSLogName, 0, rrsLogNameLen, (jbyte*) rrs_Logname);
        CHECK_JAVA_EXCEPTION(env);
    }

    jobject returnObj = (*env)->NewObject(env,
                                          jRetLogNameReturnParmsClass,
                                          jRetLogNameReturnParmsConstructor,
                                          (jint) rrs_rc,
                                          jRMLogName,
                                          jRRSLogName);
    CHECK_JAVA_EXCEPTION_INPUT(env, returnObj);

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(TP_TX_RRS_JNI_ATR4IRLN_EXIT),
                    "tx_rrs_services_jni.ntv_retrieveLogName. Exit",
                    TRACE_DATA_INT(rrs_rc, "RRS Return Code"),
                    TRACE_DATA_RAWDATA(rmLogNameLen, rm_LogName, "RM Log Name"),
                    TRACE_DATA_RAWDATA(rrsLogNameLen, rrs_Logname, "RRS Log Name"),
                    TRACE_DATA_END_PARMS);
    }

    return returnObj;
}

//-----------------------------------------------------------------------------
// Sets the log name with RRS.
//-----------------------------------------------------------------------------
JNIEXPORT jint JNICALL
ntv_setLogName(JNIEnv* env,
               jclass clazz,
               jbyteArray rmRegistryToken,
               jint rmLogNameLength,
               jbyteArray rm_logName) {
    int traceLevel = trc_level_detailed;
    if (TraceActive(traceLevel)) {
        TraceRecord(traceLevel,
                    TP(TP_TX_RRS_JNI_ATR4ISLN_ENTRY),
                    "tx_rrs_services_jni.ntv_setLogName. Entry",
                    TRACE_DATA_END_PARMS);
    }

    atr_return_code rrs_rc = -1;
    int resMgrTokenRegistryRC = 0;

    // Get the authorized server PC stub to be able to call authorized services.
    const server_authorized_function_stubs* auth_stubs_p = getServerAuthorizedFunctionStubs();
    if (auth_stubs_p == NULL) {
        throwBaseJavaException(env, "java/lang/RuntimeException", "Could not find authorized function stubs");
        return -1;
    }

    // Validate the resource manager name length input.
    if (rmLogNameLength < 0 || rmLogNameLength > ATR_MAX_RM_LOGNAME_LENGTH) {
        char excMessage[256];
        snprintf(excMessage, 256, "The rmLogNameLength input of %i to setLogName is invalid. The maximum allowed length is %i bytes", rmLogNameLength, ATR_MAX_RM_LOGNAME_LENGTH);
        throwBaseJavaException(env, "java/lang/RuntimeException", excMessage);
        return -1;
    }

    char resMgrLogName[rmLogNameLength];
    if (getByteArray(rm_logName, resMgrLogName, rmLogNameLength, env) == 0) {
        return -1;
    }

    struct atr4isln_parms p = {
        .returnCode = &rrs_rc,
        .resMgrTokenRegistryReturnCode = &resMgrTokenRegistryRC,
        .resMgrLogNameLen = rmLogNameLength,
        .resMgrLogName = resMgrLogName
    };

    if (getByteArray(rmRegistryToken, (char*)&(p.resMgrToken), sizeof(RegistryToken), env) == 0) {
        return -1;
    }

    // PC to the authorized metal C code to drive the service.
    int pc_rc = auth_stubs_p->atr_set_log_name(&p);
    if (pc_rc != 0 || (resMgrTokenRegistryRC != 0)) traceLevel = trc_level_exception;

    if (TraceActive(traceLevel)) {
        TraceRecord(traceLevel,
                    TP(TP_TX_RRS_JNI_ATR4ISLN_EXIT),
                    "tx_rrs_services_jni.ntv_setLogName. Exit",
                    TRACE_DATA_INT(pc_rc, "Authorized PC Return Code"),
                    TRACE_DATA_INT(rrs_rc, "RRS Return Code"),
                    TRACE_DATA_INT(resMgrTokenRegistryRC, "Resource manager token registry Return Code"),
                    TRACE_DATA_END_PARMS);
    }

    if (pc_rc != 0) {
        throwBaseJavaException(env, "java/lang/RuntimeException", "Bad PC return code");
        return -1;
    }

    if (resMgrTokenRegistryRC != 0) {
        throwBaseJavaException(env, "java/lang/RuntimeException", "Bad registry return code for resource manager token");
        return -1;
    }

    // Check for internal failures during the invocation of the authorized code.
    if (rrs_rc == -1) {
        throwBaseJavaException(env, "java/lang/RuntimeException", "Failure detected during the invocation authorized routine to set log name");
        return -1;
    }
    
    return rrs_rc;
}

//-----------------------------------------------------------------------------
// Retrieve the work ID (XID) for the specified UR.
// NOTE: the caller of ATR4RWID needs to be authorized (supervisor or Key 0-7)
// only when the specified UR is not currently on the thread (i.e. recovery).
//-----------------------------------------------------------------------------
JNIEXPORT jobject JNICALL
ntv_retrieveWorkIdentifier(JNIEnv* env,
                           jclass clazz,
                           jbyteArray uriRegistryToken,
                           jint retrieve_option,
                           jint generate_Option,
                           jint uwidType) {

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(TP_TX_RRS_JNI_ATR4RWID_ENTRY),
                    "tx_rrs_services_jni.ntv_retrieveWorkIdentifier. Entry",
                    TRACE_DATA_END_PARMS);
    }

    atr_return_code rrs_rc = -1;
    int registry_rc = -1;

    // Get the authorized server PC stub to be able to call authorized services.
    const server_authorized_function_stubs* auth_stubs_p = getServerAuthorizedFunctionStubs();
    if (auth_stubs_p == NULL) {
        throwBaseJavaException(env, "java/lang/RuntimeException", "Could not find authorized function stubs");
        return NULL;
    }

    int uwidLen = 0;
    char uwid[ATR_MAX_XID_LENGTH];

    struct atr4rwid_parms p = {
        .returnCode = &rrs_rc,
        .registryReturnCode = &registry_rc,
        .retrieveOption = retrieve_option,
        .generateOption = generate_Option,
        .workIdType = uwidType,
        .workIdBuffLen = ATR_MAX_XID_LENGTH,
        .workIdLength = &uwidLen,
        .workId = uwid
    };

    if (getByteArray(uriRegistryToken, (char*)&(p.uriRegistryToken), sizeof(p.uriRegistryToken), env) == 0) {
        return NULL;
    }

    // PC to the authorized metal C code to drive the service.
    int pc_rc = auth_stubs_p->atr_retrieve_work_identifier(&p);

    if (pc_rc != 0) {
        if (TraceActive(trc_level_exception)) {
            TraceRecord(trc_level_exception,
                        TP(TP_TX_RRS_JNI_ATR4RWID_BAD_PC_RC),
                        "tx_rrs_services_jni.ntv_retrieveWorkIdentifier. Bad PC RC",
                        TRACE_DATA_INT(pc_rc, "Authorized PC Return Code"),
                        TRACE_DATA_END_PARMS);
        }
        throwBaseJavaException(env, "java/lang/RuntimeException", "Bad PC return code");
        return NULL;
    }

    // If we could not find the URI in the registry, throw an exception.
    if (registry_rc != 0) {
        throwURINotFound(env, (jint)registry_rc);
        return NULL;
    }

    // Package the service's output data into a jobject.
    jbyteArray jUWId = NULL;

    if (rrs_rc == ATR_OK) {
        jUWId = (*env)->NewByteArray(env, uwidLen);
        CHECK_JAVA_EXCEPTION_INPUT(env, jUWId);
        (*env)->SetByteArrayRegion(env, jUWId, 0, uwidLen, (jbyte*) uwid);
        CHECK_JAVA_EXCEPTION(env);
    }

    jobject returnObj = (*env)->NewObject(env,
                                          jRetWorkIdReturnParmsClass,
                                          jRetWorkIdReturnParmsConstructor,
                                          (jint) rrs_rc,
                                          jUWId);
    CHECK_JAVA_EXCEPTION_INPUT(env, returnObj);

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(TP_TX_RRS_JNI_ATR4RWID_EXIT),
                    "tx_rrs_services_jni.ntv_retrieveWorkIdentifier. Exit",
                    TRACE_DATA_INT(rrs_rc, "RRS Return Code"),
                    TRACE_DATA_INT(uwidLen, "XID length"),
                    TRACE_DATA_RAWDATA(uwidLen, uwid, "XID"),
                    TRACE_DATA_END_PARMS);
    }

    return returnObj;
}

//-----------------------------------------------------------------------------
// Set the work ID (XID) for the specified UR.
//-----------------------------------------------------------------------------
JNIEXPORT jint JNICALL
ntv_setWorkIdentifier(JNIEnv* env,
                      jclass clazz,
                      jbyteArray ur_or_uriToken,
                      jint set_option,
                      jint workId_type,
                      jint xidLength,
                      jbyteArray xid) {
    int traceLevel = trc_level_detailed;
    if (TraceActive(traceLevel)) {
        TraceRecord(traceLevel,
                    TP(TP_TX_RRS_JNI_ATR4SWID_ENTRY),
                    "tx_rrs_services_jni.ntv_setWorkIdentifier. Entry",
                    TRACE_DATA_END_PARMS);
    }

    atr_return_code rrs_rc = -1;

    // Get the authorized server PC stub to be able to call authorized services.
    const server_authorized_function_stubs* auth_stubs_p = getServerAuthorizedFunctionStubs();
    if (auth_stubs_p == NULL) {
        throwBaseJavaException(env, "java/lang/RuntimeException", "Could not find authorized function stubs");
        return -1;
    }

    // Validate the XID length input.
    if (xidLength < 0 || xidLength >  ATR_MAX_XID_LENGTH) {
        char excMessage[256];
        snprintf(excMessage, 256, "The xidLength input of %i to setWorkIdentifier is invalid. The maximum allowed length is %i bytes", xidLength, ATR_MAX_XID_LENGTH);
        throwBaseJavaException(env, "java/lang/RuntimeException", excMessage);
        return -1;
    }

    char localXid[xidLength];
    if (getByteArray(xid, localXid, xidLength, env) == 0) {
        return -1;
    }

    struct atr4swid_parms p = {
        .returnCode = &rrs_rc,
        .setOption = set_option,
        .workIdType = workId_type,
        .workIdLen = xidLength,
        .workId = localXid
    };

    if (getByteArray(ur_or_uriToken, p.ur_or_uriToken, sizeof(atr_ur_or_uri_token), env) == 0) {
        return -1;
    }

    // PC to the authorized metal C code to drive the service.
    int pc_rc = auth_stubs_p->atr_set_work_identifier(&p);
    if (pc_rc != 0) traceLevel = trc_level_exception;

    if (TraceActive(traceLevel)) {
        TraceRecord(traceLevel,
                    TP(TP_TX_RRS_JNI_ATR4SWID_EXIT),
                    "tx_rrs_services_jni.ntv_setWorkIdentifier. Exit",
                    TRACE_DATA_INT(pc_rc, "Authorized PC Return Code"),
                    TRACE_DATA_INT(rrs_rc, "RRS Return Code"),
                    TRACE_DATA_END_PARMS);
    }

    if (pc_rc != 0) {
        throwBaseJavaException(env, "java/lang/RuntimeException", "Bad PC Return Code");
        return -1;
    }

    // Check for internal failures during the invocation of the authorized code.
    if (rrs_rc == -1) {
        throwBaseJavaException(env, "java/lang/RuntimeException", "Failure detected during the invocation authorized routine to set work id");
        return -1;
    }
    
    return rrs_rc;
}

//-----------------------------------------------------------------------------
// Express interest in the UR.
//-----------------------------------------------------------------------------
JNIEXPORT jobject JNICALL
ntv_expressUrInterest(JNIEnv* env,
                      jclass clazz,
                      jbyteArray rmRegistryToken,
                      jbyteArray ctx_token,
                      jint interestOptions,
                      jbyteArray non_pdata,
                      jbyteArray ur_pdata,
                      jint pdata_length,
                      jbyteArray xid,
                      jint xidLength,
                      jbyteArray parent_ur_token)

{
    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(TP_TX_RRS_JNI_ATR4EINT_ENTRY),
                    "tx_rrs_services_jni.ntv_expressUrInterest. Entry",
                    TRACE_DATA_END_PARMS);
    }

    atr_return_code rrs_rc = -1;

    // Get the authorized server PC stub to be able to call authorized services.
    const server_authorized_function_stubs* auth_stubs_p = getServerAuthorizedFunctionStubs();
    if (auth_stubs_p == NULL) {
        throwBaseJavaException(env, "java/lang/RuntimeException", "Could not find authorized function stubs");
        return NULL;
    }

    // Validate the PDATA length input.
    if (pdata_length < 0 || pdata_length > ATR_MAX_PDATA_LENGTH) {
        char excMessage[256];
        snprintf(excMessage, 256, "The pdata_length input of %i to expressUrInterest is invalid. The maximum allowed length is %i bytes", pdata_length, ATR_MAX_PDATA_LENGTH);
        throwBaseJavaException(env, "java/lang/RuntimeException", excMessage);
        return;
    }

    char pdata[pdata_length];
    if (getByteArray(ur_pdata, pdata, pdata_length, env) == 0) {
        return NULL;
    }

    if (xidLength < 0 || xidLength > ATR_MAX_XID_LENGTH) {
        char excMessage[256];
        snprintf(excMessage, 256, "The xidLength input of %i to expressUrInterest is invalid. The maximum allowed length is %i bytes", xidLength, ATR_MAX_XID_LENGTH);
        throwBaseJavaException(env, "java/lang/RuntimeException", excMessage);
        return;
    }

    char localXid[xidLength];
    if (getByteArray(xid, localXid, xidLength, env) == 0) {
        return NULL;
    }

    char parentUrToken[sizeof(atr_ur_token)];

    int registryReturnCode;
    int resourceManagerTokenRegistryReturnCode = 0;
    RegistryToken uriRegistryToken;
    atr_uri_token uriToken;
    atr_ur_token urToken;
    atr_context_token currentContextToken;
    atr_urid urid;
    atr_non_persistent_data currentNonPData;
    atr_diag_area diagArea;
    atr_transaction_mode tranMode;

    struct atr4eint_parms p = {
        .rrsReturnCode = &rrs_rc,
        .registryReturnCode = &registryReturnCode,
        .resMgrTokenRegistryReturnCode = &resourceManagerTokenRegistryReturnCode,
        .urInterestToken = uriToken,
        .urInterestRegistryToken = &uriRegistryToken,
        .ur_token = urToken,
        .currCtxToken = currentContextToken,
        .ur_id = urid,
        .interest_options = interestOptions,
        .currentNonPersistentData = currentNonPData,
        .pdataLength = pdata_length,
        .persistentData = pdata,
        .workIdLen = xidLength,
        .workId = localXid,
        .diagnosticArea = diagArea,
        .transactionMode = &tranMode
    };

    if (getByteArray(rmRegistryToken, (char*)&(p.resMgrToken), sizeof(RegistryToken), env) == 0) {
        return NULL;
    }

    if (getByteArray(ctx_token, p.contextToken, sizeof(atr_context_token), env) == 0) {
        return NULL;
    }

    if (getByteArray(non_pdata, p.nonPersistentData, sizeof(atr_non_persistent_data), env) == 0) {
        return NULL;
    }

    if (parent_ur_token != NULL) {
        if (getByteArray(parent_ur_token, p.parent_urToken, sizeof(atr_ur_token), env) == 0) {
            return NULL;
        }
    } else {
        memset(parentUrToken, 0x00, sizeof(atr_ur_token));
    }

    // PC to the authorized metal C code to drive the service.
    int pc_rc = auth_stubs_p->atr_express_ur_interest(&p);

    if (pc_rc != 0) {
        if (TraceActive(trc_level_exception)) {
            TraceRecord(trc_level_exception,
                        TP(TP_TX_RRS_JNI_ATR4EINT_BAD_PC_RC),
                        "tx_rrs_services_jni.ntv_expressUrInterest. Bad PC RC",
                        TRACE_DATA_INT(pc_rc, "Authorized PC Return Code"),
                        TRACE_DATA_END_PARMS);
        }

        throwBaseJavaException(env, "java/lang/RuntimeException", "Bad PC return code");
        return NULL;
    }

    if (resourceManagerTokenRegistryReturnCode != 0) {
        if (TraceActive(trc_level_exception)) {
            TraceRecord(trc_level_exception,
                        TP(TP_TX_RRS_JNI_ATR4EINT_BAD_REGISTRY_RM_TOKEN_RC),
                        "tx_rrs_services_jni.ntv_expressUrInterest. Bad registry RC",
                        TRACE_DATA_INT(resourceManagerTokenRegistryReturnCode, "Resource manager token registry Return Code"),
                        TRACE_DATA_END_PARMS);
        }
        throwResourceManagerTokenNotFound(env, (jint)resourceManagerTokenRegistryReturnCode);
        return NULL;
    }

    // Check for internal failures during the invocation of the authorized code.
    if (rrs_rc == -1) {
        throwBaseJavaException(env, "java/lang/RuntimeException", "Failure detected during the invocation authorized routine to express interest in the UR");
        return NULL;
    }
    
    // If we could not add the URI to the registry, throw.
    if ((rrs_rc == 0) && (registryReturnCode != 0)) {
        throwURIAddError(env, (jint)registryReturnCode);
        return NULL;
    }

    // Package the service's output data into a jobject.
    jbyteArray jUriRegistryToken = NULL;
    jbyteArray jUriToken = NULL;
    jbyteArray jCurrentContextToken = NULL;
    jbyteArray jUrid = NULL;
    jbyteArray jCurrentNonPData = NULL;
    jbyteArray jUrToken = NULL;
    jbyteArray jDiagArea = NULL;

    if (rrs_rc == ATR_OK) {
        jUriToken = (*env)->NewByteArray(env, sizeof(atr_uri_token));
        CHECK_JAVA_EXCEPTION_INPUT(env, jUriToken);
        (*env)->SetByteArrayRegion(env, jUriToken, 0, sizeof(atr_uri_token), (jbyte*) uriToken);
        CHECK_JAVA_EXCEPTION(env);

        jUriRegistryToken = (*env)->NewByteArray(env, sizeof(RegistryToken));
        CHECK_JAVA_EXCEPTION_INPUT(env, jUriRegistryToken);
        (*env)->SetByteArrayRegion(env, jUriRegistryToken, 0, sizeof(RegistryToken), (jbyte*)(&uriRegistryToken));
        CHECK_JAVA_EXCEPTION(env);

        jCurrentContextToken = (*env)->NewByteArray(env, sizeof(atr_context_token));
        CHECK_JAVA_EXCEPTION_INPUT(env, jCurrentContextToken);
        (*env)->SetByteArrayRegion(env,
                                   jCurrentContextToken,
                                   0,
                                   sizeof(atr_context_token),
                                   (jbyte*) currentContextToken);
        CHECK_JAVA_EXCEPTION(env);

        jUrid = (*env)->NewByteArray(env, sizeof(atr_urid));
        CHECK_JAVA_EXCEPTION_INPUT(env, jUrid);
        (*env)->SetByteArrayRegion(env, jUrid, 0, sizeof(atr_urid), (jbyte*) urid);
        CHECK_JAVA_EXCEPTION(env);

        jCurrentNonPData = (*env)->NewByteArray(env, sizeof(atr_non_persistent_data));
        CHECK_JAVA_EXCEPTION_INPUT(env, jCurrentNonPData);
        (*env)->SetByteArrayRegion(env,
                                   jCurrentNonPData,
                                   0,
                                   sizeof(atr_non_persistent_data),
                                   (jbyte*) currentNonPData);
        CHECK_JAVA_EXCEPTION(env);

        jUrToken = (*env)->NewByteArray(env, sizeof(atr_ur_token));
        CHECK_JAVA_EXCEPTION_INPUT(env, jUrToken);
        (*env)->SetByteArrayRegion(env, jUrToken, 0, sizeof(atr_ur_token), (jbyte*) urToken);
        CHECK_JAVA_EXCEPTION(env);
    }

    jDiagArea = (*env)->NewByteArray(env, sizeof(atr_diag_area));
    CHECK_JAVA_EXCEPTION_INPUT(env, jDiagArea);
    (*env)->SetByteArrayRegion(env, jDiagArea, 0, sizeof(atr_diag_area), (jbyte*) diagArea);
    CHECK_JAVA_EXCEPTION(env);

    jobject returnObj = (*env)->NewObject(env, jExpressIntReturnParmsClass,
                                          jExpressIntReturnParmsConstructor,
                                          (jint) rrs_rc,
                                          jUriToken,
                                          jUriRegistryToken,
                                          jCurrentContextToken,
                                          jUrid,
                                          jCurrentNonPData,
                                          jDiagArea,
                                          (jint)tranMode,
                                          jUrToken);
    CHECK_JAVA_EXCEPTION_INPUT(env, returnObj);

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(TP_TX_RRS_JNI_ATR4EINT_EXIT),
                    "tx_rrs_services_jni.ntv_expressUrInterest. Exit",
                    TRACE_DATA_INT(rrs_rc, "RRS Return Code"),
                    TRACE_DATA_INT(tranMode, "Transaction Mode"),
                    TRACE_DATA_RAWDATA(sizeof(atr_uri_token), uriToken, "URIToken"),
                    TRACE_DATA_RAWDATA(sizeof(atr_context_token), currentContextToken, "CtxToken"),
                    TRACE_DATA_RAWDATA(sizeof(atr_urid), urid, "URID"),
                    TRACE_DATA_RAWDATA(sizeof(atr_non_persistent_data), currentNonPData, "Non PDATA"),
                    TRACE_DATA_RAWDATA(sizeof(atr_ur_token), urToken, "URToken"),
                    TRACE_DATA_RAWDATA(sizeof(atr_diag_area), diagArea, "Diagnostic Area"),
                    TRACE_DATA_END_PARMS);
    }

    return returnObj;
}

//-----------------------------------------------------------------------------
// Retrieve side information for the given interest in the UR.
//-----------------------------------------------------------------------------
JNIEXPORT jobject JNICALL
ntv_retrieveSideInformation(JNIEnv* env,
                            jclass clazz,
                            jbyteArray uriRegistryToken,
                            jintArray info_ids,
                            jint info_id_count) {

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(TP_TX_RRS_JNI_ATR4RUSI_ENTRY),
                    "tx_rrs_services_jni.ntv_retrieveSideInformation. Entry",
                    TRACE_DATA_END_PARMS);
    }

    int registryRC;
    atr_return_code rrs_rc;

    // Get the authorized server PC stub to be able to call authorized services.
    const server_authorized_function_stubs* auth_stubs_p = getServerAuthorizedFunctionStubs();
    if (auth_stubs_p == NULL) {
        throwBaseJavaException(env, "java/lang/RuntimeException", "Could not find authorized function stubs");
        return NULL;
    }

    // Validate the element count input.
    if (info_id_count < 0 || info_id_count > MAX_ELEMENT_COUNT) {
        char excMessage[256];
        snprintf(excMessage, 256, "The info_id_count input of %i to retrieveSideInformation is invalid. The maximum allowed length is %i bytes", info_id_count, MAX_ELEMENT_COUNT);
        throwBaseJavaException(env, "java/lang/RuntimeException", excMessage);
        return NULL;
    }

    int infoIds[info_id_count];
    if (getIntArray(info_ids, infoIds, info_id_count, env) == 0) {
        return NULL;
    }

    int infoStates[info_id_count];

    struct atr4rusi_parms p = {
        .rrsReturnCode = &rrs_rc,
        .registryReturnCode = &registryRC,
        .infoIdCount = info_id_count,
        .sideInfoIds = infoIds,
        .sideInfoStates = infoStates
    };

    if (getByteArray(uriRegistryToken, (char*)&(p.uriRegistryToken), sizeof(RegistryToken), env) == 0) {
        return NULL;
    }

    // PC to the authorized metal C code to drive the service.
    int pc_rc = auth_stubs_p->atr_retrieve_side_information(&p);

    if (pc_rc != 0) {
        if (TraceActive(trc_level_exception)) {
            TraceRecord(trc_level_exception,
                        TP(TP_TX_RRS_JNI_ATR4RUSI_BAD_PC_RC),
                        "tx_rrs_services_jni.ntv_retrieveSideInformation. Bad PC RC",
                        TRACE_DATA_INT(pc_rc, "Authorized PC Return Code"),
                        TRACE_DATA_END_PARMS);
        }
        throwBaseJavaException(env, "java/lang/RuntimeException", "Bad PC return code");
        return NULL;
    }

    // Check for internal failures during the invocation of the authorized code.
    if (rrs_rc == -1) {
        throwBaseJavaException(env, "java/lang/RuntimeException", "Failure detected during the invocation authorized routine to retrieve side information");
        return NULL;
    }

    // If we had a registry error, throw.
    if (registryRC != 0) {
        throwURINotFound(env, (jint)registryRC);
        return NULL;
    }

    // Package the service's output data into a jobject.
    jbyteArray jInfoStates = NULL;

    if ((rrs_rc == ATR_OK) && (registryRC == 0)) {
        jInfoStates = (*env)->NewIntArray(env, info_id_count);
        CHECK_JAVA_EXCEPTION_INPUT(env, jInfoStates);
        (*env)->SetIntArrayRegion(env, jInfoStates, 0, info_id_count, (jint*) infoStates);
        CHECK_JAVA_EXCEPTION(env);
    }

    jobject returnObj = (*env)->NewObject(env,
                                          jRetSideInfoReturnParmsClass,
                                          jRetSideInfoReturnParmsConstructor,
                                          (jint) rrs_rc,
                                          jInfoStates);
    CHECK_JAVA_EXCEPTION_INPUT(env, returnObj);

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(TP_TX_RRS_JNI_ATR4RUSI_EXIT),
                    "tx_rrs_services_jni.ntv_retrieveSideInformation. Exit",
                    TRACE_DATA_INT(rrs_rc, "RRS Return Code"),
                    TRACE_DATA_END_PARMS);
    }

    return returnObj;
}

//-----------------------------------------------------------------------------
// Retrieves a UR interest during restart
//-----------------------------------------------------------------------------
JNIEXPORT jobject JNICALL
ntv_retrieveUrInterest(JNIEnv* env,
                       jclass clazz,
                       jbyteArray rmRegistryToken) {

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(TP_TX_RRS_JNI_ATR4IRNI_ENTRY),
                    "tx_rrs_services_jni.ntv_retrieveUrInterest. Entry",
                    TRACE_DATA_END_PARMS);
    }

    atr_return_code rrs_rc;

    // Get the authorized server PC stub to be able to call authorized services.
    const server_authorized_function_stubs* auth_stubs_p = getServerAuthorizedFunctionStubs();
    if (auth_stubs_p == NULL) {
        throwBaseJavaException(env, "java/lang/RuntimeException", "Could not find authorized function stubs");
        return NULL;
    }

    RegistryToken uriRegistryToken;
    int registryRC = 0;
    int resourceManagerTokenRegistryRC = 0;
    atr_context_token ctxToken;
    atr_uri_token uriToken;
    atr_urid urid;
    atr_role role;
    atr_ur_state urState;
    atr_pdata_length pdataLength = 0;
    char pdata[ATR_MAX_PDATA_LENGTH];

    struct atr4irni_parms p = {
        .rrsReturnCode = &rrs_rc,
        .registryReturnCode = &registryRC,
        .resMgrNameRegistryReturnCode = &resourceManagerTokenRegistryRC,
        .contextToken = ctxToken,
        .urInterestToken = uriToken,
        .uriRegistryToken = &uriRegistryToken,
        .urIdentifier = urid,
        .rmRole = &role,
        .loggedState = &urState,
        .pdataBuffLen = ATR_MAX_PDATA_LENGTH,
        .pdataLen = &pdataLength,
        .persistentData = pdata
    };

    if (getByteArray(rmRegistryToken, (char*)&(p.resMgrToken), sizeof(RegistryToken), env) == 0) {
        return NULL;
    }

    // PC to the authorized metal C code to drive the service.
    int pc_rc = auth_stubs_p->atr_retrieve_ur_interest(&p);

    if (pc_rc != 0) {
        if (TraceActive(trc_level_exception)) {
            TraceRecord(trc_level_exception,
                        TP(TP_TX_RRS_JNI_ATR4IRNI_BAD_PC_RC),
                        "tx_rrs_services_jni.ntv_retrieveUrInterest. Bad PC RC",
                        TRACE_DATA_INT(pc_rc, "Authorized PC Return Code"),
                        TRACE_DATA_END_PARMS);
        }
        throwBaseJavaException(env, "java/lang/RuntimeException", "Bad PC return code");
        return NULL;
    }

    // If the URI could not be added to the registry, throw.
    if ((rrs_rc == 0) && (registryRC != 0)) {
        throwURIAddError(env, (jint)registryRC);
        return NULL;
    }

    // If the resource manager token could not be found in the registry, throw.
    if (resourceManagerTokenRegistryRC !=0) {
        throwResourceManagerTokenNotFound(env, (jint)resourceManagerTokenRegistryRC);
        return NULL;
    }

    // Package the service's output data into a jobject.
    jbyteArray jCtxToken = NULL;
    jbyteArray jUriToken = NULL;
    jbyteArray jUrid = NULL;
    jbyteArray jPdata = NULL;
    jbyteArray jUriRegistryToken = NULL;

    if ((rrs_rc == ATR_OK) && (registryRC == 0)) {
        jCtxToken = (*env)->NewByteArray(env, sizeof(atr_context_token));
        CHECK_JAVA_EXCEPTION_INPUT(env, jCtxToken);
        (*env)->SetByteArrayRegion(env, jCtxToken, 0, sizeof(atr_context_token), (jbyte*) ctxToken);
        CHECK_JAVA_EXCEPTION(env);

        jUriToken = (*env)->NewByteArray(env, sizeof(atr_uri_token));
        CHECK_JAVA_EXCEPTION_INPUT(env, jUriToken);
        (*env)->SetByteArrayRegion(env, jUriToken, 0, sizeof(atr_uri_token), (jbyte*) uriToken);
        CHECK_JAVA_EXCEPTION(env);

        jUriRegistryToken = (*env)->NewByteArray(env, sizeof(RegistryToken));
        CHECK_JAVA_EXCEPTION_INPUT(env, jUriRegistryToken);
        (*env)->SetByteArrayRegion(env, jUriRegistryToken, 0, sizeof(RegistryToken), (jbyte*)(&uriRegistryToken));
        CHECK_JAVA_EXCEPTION(env);

        jUrid = (*env)->NewByteArray(env, sizeof(atr_urid));
        CHECK_JAVA_EXCEPTION_INPUT(env, jUrid);
        (*env)->SetByteArrayRegion(env, jUrid, 0, sizeof(atr_urid), (jbyte*) urid);
        CHECK_JAVA_EXCEPTION(env);

        jPdata = (*env)->NewByteArray(env, pdataLength);
        CHECK_JAVA_EXCEPTION_INPUT(env, jPdata);
        (*env)->SetByteArrayRegion(env, jPdata, 0, pdataLength, (jbyte*) pdata);
        CHECK_JAVA_EXCEPTION(env);
    }

    jobject returnObj = (*env)->NewObject(env,
                                          jRetURInterestReturnParmsClass,
                                          jRetURInterestReturnParmsConstructor,
                                          (jint) rrs_rc,
                                          jCtxToken,
                                          jUriToken,
                                          jUriRegistryToken,
                                          jUrid,
                                          (jint)role,
                                          (jint)urState,
                                          jPdata);
    CHECK_JAVA_EXCEPTION_INPUT(env, returnObj);

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(TP_TX_RRS_JNI_ATR4IRNI_EXIT),
                    "tx_rrs_services_jni.ntv_retrieveUrInterest. Exit",
                    TRACE_DATA_INT(rrs_rc, "RRS Return Code"),
                    TRACE_DATA_INT(role, "RM Role"),
                    TRACE_DATA_INT(urState, "UR State"),
                    TRACE_DATA_RAWDATA(sizeof(atr_context_token), ctxToken, "ContextToken"),
                    TRACE_DATA_RAWDATA(sizeof(atr_uri_token), uriToken, "URIToken"),
                    TRACE_DATA_RAWDATA(sizeof(atr_urid), urid, "URID"),
                    TRACE_DATA_RAWDATA(pdataLength, pdata, "PDATA"),
                    TRACE_DATA_END_PARMS);
    }

    return returnObj;
}

//-----------------------------------------------------------------------------
// Set environment.
// Requires authorization if the environment protection is set to
// PROTECTED.
//-----------------------------------------------------------------------------
JNIEXPORT jint JNICALL
ntv_setEnvironment(JNIEnv* env,
                   jclass clazz,
                   jbyteArray stoken,
                   jint elementCount,
                   jintArray envIds,
                   jintArray envIdValues,
                   jintArray protectionValues) {

    // TODO: Update this to use the context registry token.
    int traceLevel = trc_level_detailed;
    if (TraceActive(traceLevel)) {
        TraceRecord(traceLevel,
                    TP(TP_TX_RRS_JNI_ATR4SENV_ENTRY),
                    "tx_rrs_services_jni.ntv_setEnvironment. Entry",
                    TRACE_DATA_END_PARMS);
    }

    atr_return_code rrs_rc = -1;
    atr_diag_area diagArea;

    // Get the authorized server PC stub to be able to call authorized services.
    const server_authorized_function_stubs* auth_stubs_p = getServerAuthorizedFunctionStubs();
    if (auth_stubs_p == NULL) {
        throwBaseJavaException(env, "java/lang/RuntimeException", "Could not find authorized function stubs");
        return -1;
    }

    // Validate the element count input.
    if (elementCount < 0 || elementCount > MAX_ELEMENT_COUNT) {
        char excMessage[256];
        snprintf(excMessage, 256, "The elementCount input of %i to setEnvironment is invalid. The maximum allowed length is %i bytes", elementCount, MAX_ELEMENT_COUNT);
        throwBaseJavaException(env, "java/lang/RuntimeException", excMessage);
        return -1;
    }

    int localEnvIds[elementCount];
    if (getIntArray(envIds, localEnvIds, elementCount, env) == 0) {
        return -1;
    }

    int localEnvIdValues[elementCount];
    if (getIntArray(envIdValues, localEnvIdValues, elementCount, env) == 0) {
        return -1;
    }

    int localProtectionValues[elementCount];
    if (getIntArray(protectionValues, localProtectionValues, elementCount, env) == 0) {
        return -1;
    }

    struct atr4senv_parms p = {
        .returnCode = &rrs_rc,
        .diagnosticArea = diagArea,
        .contentCount = elementCount,
        .environmentIds = localEnvIds,
        .environmentIdValues = localEnvIdValues,
        .envProtectionValues = localProtectionValues
    };

    if (stoken != NULL) {
        if (getByteArray(stoken, p.envStoken, sizeof(atr_stoken), env) == 0) {
            return -1;
        }
    } else {
        memset(p.envStoken, 0x00, sizeof(atr_stoken));
    }

    // PC to the authorized metal C code to drive the service.
    int pc_rc = auth_stubs_p->atr_set_environment(&p);
    if (pc_rc != 0) traceLevel = trc_level_exception;

    if (TraceActive(traceLevel)) {
        TraceRecord(traceLevel,
                    TP(TP_TX_RRS_JNI_ATR4SENV_EXIT),
                    "tx_rrs_services_jni.ntv_setEnvironment. Exit",
                    TRACE_DATA_INT(pc_rc, "Authorized PC Return Code"),
                    TRACE_DATA_INT(rrs_rc, "RRS Return Code"),
                    TRACE_DATA_RAWDATA(sizeof(atr_diag_area), diagArea, "Diagnostic area"),
                    TRACE_DATA_END_PARMS);
    }

    if (pc_rc != 0) {
        throwBaseJavaException(env, "java/lang/RuntimeException", "Bad PC return code");
        return -1;
    }

    // Check for internal failures during the invocation of the authorized code.
    if (rrs_rc == -1) {
        throwBaseJavaException(env, "java/lang/RuntimeException", "Failure detected during the invocation authorized routine to set environment information");
        return -1;
    }

    return rrs_rc;
}

//-----------------------------------------------------------------------------
// Prepares the UR associated with the given UR interest token.
//-----------------------------------------------------------------------------
JNIEXPORT jobject JNICALL
ntv_prepareAgentUR(JNIEnv* env,
                   jclass clazz,
                   jbyteArray uriRegistryToken,
                   jbyteArray ctxTokenRegistryToken,
                   jbyteArray rmRegistryToken,
                   jint log_option) {

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(TP_TX_RRS_JNI_ATR4APRP_ENTRY),
                    "tx_rrs_services_jni.ntv_prepareAgentUR. Entry",
                    TRACE_DATA_END_PARMS);
    }

    atr_return_code rrs_rc = -1;
    ctx_return_code ctx_rc = -1;
    int getUriRegistryRC = -1;
    int getContextTokenRegistryRC = -1;
    int addContextInterestTokenRegistryRC = -1;
    int getResourceManagerTokenRegistryRC = -1;

    RegistryToken contextInterestRegistryToken;
    memset(&contextInterestRegistryToken, 0, sizeof(contextInterestRegistryToken));

    // Get the authorized server PC stub to be able to call authorized services.
    const server_authorized_function_stubs* auth_stubs_p = getServerAuthorizedFunctionStubs();
    if (auth_stubs_p == NULL) {
        throwBaseJavaException(env, "java/lang/RuntimeException", "Could not find authorized function stubs");
        return NULL;
    }

    struct atr4aprp_parms p = {
        .rrsPrepareReturnCode = &rrs_rc,
        .ctxExpressInterestReturnCode = &ctx_rc,
        .registryReturnCodeUri = &getUriRegistryRC,
        .registryReturnCodeContext = &getContextTokenRegistryRC,
        .registryReturnCodeContextInterest = &addContextInterestTokenRegistryRC,
        .registryReturnCodeResMgrToken = &getResourceManagerTokenRegistryRC,
        .contextInterestRegistryToken_p = &contextInterestRegistryToken,
        .logOption = log_option
    };

    if (getByteArray(uriRegistryToken, (char*)&(p.uriRegistryToken), sizeof(RegistryToken), env) == 0) {
        return NULL;
    }

    if (getByteArray(ctxTokenRegistryToken, (char*)&(p.contextRegistryToken), sizeof(RegistryToken), env) == 0) {
        return NULL;
    }

    if (getByteArray(rmRegistryToken, (char*)&(p.rmToken), sizeof(p.rmToken), env) == 0) {
        return NULL;
    }

    // PC to the authorized metal C code to drive the service.
    int pc_rc = auth_stubs_p->atr_prepare_agent_ur(&p);

    if (pc_rc != 0) {
        if (TraceActive(trc_level_exception)) {
            TraceRecord(trc_level_exception,
                        TP(TP_TX_RRS_JNI_ATR4APRP_BAD_PC_RC),
                        "tx_rrs_services_jni.ntv_prepareAgentUR. Bad PC RC",
                        TRACE_DATA_INT(pc_rc, "Authorized PC Return Code"),
                        TRACE_DATA_END_PARMS);
        }

        throwBaseJavaException(env, "java/lang/RuntimeException", "Bad PC return code");
        return NULL;
    }

    // If we could not find the URI in the registry, throw.
    if (getUriRegistryRC != 0) {
        throwURINotFound(env, (jint)getUriRegistryRC);
        return NULL;
    }

    // If we could not find the context token in the registry, throw.
    if (getContextTokenRegistryRC != 0) {
        throwContextTokenNotFound(env, (jint)getContextTokenRegistryRC);
        return NULL;
    }

    // If we could not add the context interest token to the registry, throw.
    if (addContextInterestTokenRegistryRC != 0) {
        throwContextInterestTokenAddError(env, (jint)addContextInterestTokenRegistryRC);
        return NULL;
    }

    // If we could not find the resource manager token in the registry, throw.
    if (getResourceManagerTokenRegistryRC != 0) {
        throwResourceManagerTokenNotFound(env, (jint)getResourceManagerTokenRegistryRC);
        return NULL;
    }

    // Package the service's output data into a jobject.
    RegistryToken nullContextInterestRegistryToken;
    memset(&(nullContextInterestRegistryToken), 0, sizeof(RegistryToken));

    jbyteArray jContextInterestRegistryToken = NULL;
    if (memcmp(&contextInterestRegistryToken, &nullContextInterestRegistryToken, sizeof(nullContextInterestRegistryToken)) != 0) {
        jContextInterestRegistryToken = (*env)->NewByteArray(env, sizeof(contextInterestRegistryToken));
        CHECK_JAVA_EXCEPTION_INPUT(env, jContextInterestRegistryToken);
        (*env)->SetByteArrayRegion(env, jContextInterestRegistryToken, 0, sizeof(contextInterestRegistryToken), (jbyte*)&contextInterestRegistryToken);
        CHECK_JAVA_EXCEPTION(env);
    }

    jobject returnObj = (*env)->NewObject(env,
                                          jPrepareAgentURReturnParmsClass,
                                          jPrepareAgentURReturnParmsConstructor,
                                          (jint) rrs_rc,
                                          (jint) ctx_rc,
                                          jContextInterestRegistryToken);
    CHECK_JAVA_EXCEPTION_INPUT(env, returnObj);

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(TP_TX_RRS_JNI_ATR4APRP_EXIT),
                    "tx_rrs_services_jni.ntv_prepareAgentUR. Exit",
                    TRACE_DATA_INT(rrs_rc, "RRS Return Code"),
                    TRACE_DATA_INT(ctx_rc, "CTX Return Code"),
                    TRACE_DATA_RAWDATA(sizeof(contextInterestRegistryToken), &contextInterestRegistryToken, "Context interest registry token"),
                    TRACE_DATA_END_PARMS);
    }

    return returnObj;
}

//-----------------------------------------------------------------------------
// Commits the UR associated with the given UR interest token
//-----------------------------------------------------------------------------
JNIEXPORT jint JNICALL
ntv_commitAgentUR(JNIEnv* env,
                  jclass clazz,
                  jbyteArray uriRegistryToken,
                  jbyteArray ciRegistryToken,
                  jint log_option) {
    int traceLevel = trc_level_detailed;
    if (TraceActive(traceLevel)) {
        TraceRecord(traceLevel,
                    TP(TP_TX_RRS_JNI_ATR4ACMT_ENTRY),
                    "tx_rrs_services_jni.ntv_commitAgentUR. Entry",
                    TRACE_DATA_END_PARMS);
    }

    int registryUriRC = -1;
    int registryCiRC = -1;
    atr_return_code rrs_rc = -1;

    // Get the authorized server PC stub to be able to call authorized services.
    const server_authorized_function_stubs* auth_stubs_p = getServerAuthorizedFunctionStubs();
    if (auth_stubs_p == NULL) {
        throwBaseJavaException(env, "java/lang/RuntimeException", "Could not find authorized function stubs");
        return -1;
    }

    struct atr4acmt_parms p = {
        .rrsReturnCode = &rrs_rc,
        .registryReturnCodeUri = &registryUriRC,
        .registryReturnCodeCi = &registryCiRC,
        .logOption = log_option
    };

    if (getByteArray(uriRegistryToken, (char*)&(p.uriRegistryToken), sizeof(RegistryToken), env) == 0) {
        return -1;
    }

    if (ciRegistryToken == NULL) {
        memset(&(p.ciRegistryToken), 0, sizeof(p.ciRegistryToken));
        registryCiRC = 0;
    } else {
        if (getByteArray(ciRegistryToken, (char*)&(p.ciRegistryToken), sizeof(RegistryToken), env) == 0) {
            return -1;
        }
    }

    // PC to the authorized metal C code to drive the service.
    int pc_rc = auth_stubs_p->atr_commit_agent_ur(&p);
    if (pc_rc != 0) traceLevel = trc_level_exception;

    if (TraceActive(traceLevel)) {
        TraceRecord(traceLevel,
                    TP(TP_TX_RRS_JNI_ATR4ACMT_EXIT),
                    "tx_rrs_services_jni.ntv_commitAgentUR. Exit",
                    TRACE_DATA_INT(pc_rc, "Authorized PC Return Code"),
                    TRACE_DATA_INT(rrs_rc, "RRS Return Code"),
                    TRACE_DATA_INT(registryUriRC, "Registry URI Return Code"),
                    TRACE_DATA_INT(registryCiRC, "Registry CI Return Code"),
                    TRACE_DATA_END_PARMS);
    }

    if (pc_rc != 0) {
        throwBaseJavaException(env, "java/lang/RuntimeException", "Bad PC return code");
        return -1;
    }

    // If we had a registry error, throw.
    if (registryUriRC != 0) {
        throwURINotFound(env, (jint)registryUriRC);
        return -1;
    }

    if (registryCiRC != 0) {
        throwContextInterestTokenNotFound(env, (jint)registryCiRC);
        return -1;
    }

    return rrs_rc;
}

//-----------------------------------------------------------------------------
// Delegates the 2 phase commit process to RRS.
//-----------------------------------------------------------------------------
JNIEXPORT jint JNICALL
ntv_delegateCommitAgentUR(JNIEnv* env,
                          jclass clazz,
                          jbyteArray uriRegistryToken,
                          jint log_option,
                          jint commit_options) {
    int traceLevel = trc_level_detailed;
    if (TraceActive(traceLevel)) {
        TraceRecord(traceLevel,
                    TP(TP_TX_RRS_JNI_ATR4ADCT_ENTRY),
                    "tx_rrs_services_jni.ntv_delegateCommitAgentUR. Entry",
                    TRACE_DATA_END_PARMS);
    }

    int registryRC = -1;
    atr_return_code rrs_rc = -1;

    // Get the authorized server PC stub to be able to call authorized services.
    const server_authorized_function_stubs* auth_stubs_p = getServerAuthorizedFunctionStubs();
    if (auth_stubs_p == NULL) {
        throwBaseJavaException(env, "java/lang/RuntimeException", "Could not find authorized function stubs");
        return -1;
    }

    struct atr4adct_parms p = {
        .rrsReturnCode = &rrs_rc,
        .registryReturnCode = &registryRC,
        .logOption = log_option,
        .commitOptions = commit_options
    };

    if (getByteArray(uriRegistryToken, (char*)&(p.uriRegistryToken), sizeof(RegistryToken), env) == 0) {
        return -1;
    }

    // PC to the authorized metal C code to drive the service.
    int pc_rc = auth_stubs_p->atr_delegate_commit_agent_ur(&p);
    if (pc_rc != 0) traceLevel = trc_level_exception;

    if (TraceActive(traceLevel)) {
        TraceRecord(traceLevel,
                    TP(TP_TX_RRS_JNI_ATR4ADCT_EXIT),
                    "tx_rrs_services_jni.ntv_delegateCommitAgentUR. Exit",
                    TRACE_DATA_INT(pc_rc, "Authorized PC Return Code"),
                    TRACE_DATA_INT(rrs_rc, "RRS Return Code"),
                    TRACE_DATA_INT(registryRC, "Registry Return Code"),
                    TRACE_DATA_END_PARMS);
    }

    if (pc_rc != 0) {
        throwBaseJavaException(env, "java/lang/RuntimeException", "Bad PC return code");
        return -1;
    }

    // If we had a registry error, throw.
    if (registryRC != 0) {
        throwURINotFound(env, (jint)registryRC);
        return -1;
    }

    return rrs_rc;
}

//-----------------------------------------------------------------------------
// Rolls back the UR associated with the given UR interest token
//-----------------------------------------------------------------------------
JNIEXPORT jint JNICALL
ntv_backoutAgentUR(JNIEnv* env,
                   jclass clazz,
                   jbyteArray uriRegistryToken,
                   jbyteArray ciRegistryToken,
                   jint log_option) {
    int traceLevel = trc_level_detailed;
    if (TraceActive(traceLevel)) {
        TraceRecord(traceLevel,
                    TP(TP_TX_RRS_JNI_ATR4ABAK_ENTRY),
                    "tx_rrs_services_jni.ntv_backoutAgentUR. Entry",
                    TRACE_DATA_END_PARMS);
    }

    int registryUriRC = -1;
    int registryCiRC = -1;
    atr_return_code rrs_rc = -1;

    // Get the authorized server PC stub to be able to call authorized services.
    const server_authorized_function_stubs* auth_stubs_p = getServerAuthorizedFunctionStubs();
    if (auth_stubs_p == NULL) {
        throwBaseJavaException(env, "java/lang/RuntimeException", "Could not find authorized function stubs");
        return -1;
    }

    struct atr4abak_parms p = {
        .rrsReturnCode = &rrs_rc,
        .registryReturnCodeUri = &registryUriRC,
        .registryReturnCodeCi = &registryCiRC,
        .logOption = log_option
    };

    if (getByteArray(uriRegistryToken, (char*)&(p.uriRegistryToken), sizeof(RegistryToken), env) == 0) {
        return -1;
    }

    if (ciRegistryToken == NULL) {
        memset(&(p.ciRegistryToken), 0, sizeof(p.ciRegistryToken));
        registryCiRC = 0;
    } else {
        if (getByteArray(ciRegistryToken, (char*)&(p.ciRegistryToken), sizeof(RegistryToken), env) == 0) {
            return -1;
        }
    }

    // PC to the authorized metal C code to drive the service.
    int pc_rc = auth_stubs_p->atr_backout_agent_ur(&p);
    if (pc_rc != 0) traceLevel = trc_level_exception;

    if (TraceActive(traceLevel)) {
        TraceRecord(traceLevel,
                    TP(TP_TX_RRS_JNI_ATR4ABAK_EXIT),
                    "tx_rrs_services_jni.ntv_backoutAgentUR. Exit",
                    TRACE_DATA_INT(pc_rc, "Authorized PC Return Code"),
                    TRACE_DATA_INT(rrs_rc, "RRS Return Code"),
                    TRACE_DATA_INT(registryUriRC, "Registry URI Return Code"),
                    TRACE_DATA_INT(registryCiRC, "Registry CI Return Code"),
                    TRACE_DATA_END_PARMS);
    }

    if (pc_rc != 0) {
        throwBaseJavaException(env, "java/lang/RuntimeException", "Bad PC return code");
        return -1;
    }

    // If we had a registry error, throw.
    if (registryUriRC != 0) {
        throwURINotFound(env, (jint)registryUriRC);
        return -1;
    }

    if (registryCiRC != 0) {
        throwContextInterestTokenNotFound(env, (jint)registryCiRC);
        return -1;
    }

    return rrs_rc;
}

//-----------------------------------------------------------------------------
// Forgets the UR interest associated with the given UR interest token.
//-----------------------------------------------------------------------------
JNIEXPORT jint JNICALL
ntv_forgetAgentURInterest(JNIEnv* env,
                          jclass clazz,
                          jbyteArray uriRegistryToken,
                          jint log_option) {
    int traceLevel = trc_level_detailed;
    if (TraceActive(traceLevel)) {
        TraceRecord(traceLevel,
                    TP(TP_TX_RRS_JNI_ATR4AFGT_ENTRY),
                    "tx_rrs_services_jni.ntv_forgetAgentURInterest. Entry",
                    TRACE_DATA_END_PARMS);
    }

    int registryRC = -1;
    atr_return_code rrs_rc = -1;

    // Get the authorized server PC stub to be able to call authorized services.
    const server_authorized_function_stubs* auth_stubs_p = getServerAuthorizedFunctionStubs();
    if (auth_stubs_p == NULL) {
        throwBaseJavaException(env, "java/lang/RuntimeException", "Could not find authorized function stubs");
        return -1;
    }

    struct atr4afgt_parms p = {
        .rrsReturnCode = &rrs_rc,
        .registryReturnCode = &registryRC,
        .logOption = log_option
    };

    if (getByteArray(uriRegistryToken, (char*)&(p.uriRegistryToken), sizeof(RegistryToken), env) == 0) {
        return -1;
    }

    // PC to the authorized metal C code to drive the service.
    int pc_rc = auth_stubs_p->atr_forget_agent_ur_interest(&p);
    if (pc_rc != 0) traceLevel = trc_level_exception;

    if (TraceActive(traceLevel)) {
        TraceRecord(traceLevel,
                    TP(TP_TX_RRS_JNI_ATR4AFGT_EXIT),
                    "tx_rrs_services_jni.ntv_forgetAgentURInterest. Exit",
                    TRACE_DATA_INT(pc_rc, "Authorized PC Return Code"),
                    TRACE_DATA_INT(rrs_rc, "RRS Return Code"),
                    TRACE_DATA_INT(registryRC, "Registry Return Code"),
                    TRACE_DATA_END_PARMS);
    }

    if (pc_rc != 0) {
        throwBaseJavaException(env, "java/lang/RuntimeException", "Bad PC return code");
        return -1;
    }

    // If we had a registry error, throw.
    if (registryRC != 0) {
        throwURINotFound(env, (jint)registryRC);
        return -1;
    }

    return rrs_rc;
}

//-----------------------------------------------------------------------------
// Allows the resource manager to initiate asynchronous processing, and
// return to RRS with a return code that indicates a deferred response.
//-----------------------------------------------------------------------------
JNIEXPORT jint JNICALL
ntv_postDeferredURExit(JNIEnv* env,
                       jclass clazz,
                       jbyteArray uriRegistryToken,
                       jint exit_number,
                       jint completion_code) {
    int traceLevel = trc_level_detailed;
    if (TraceActive(traceLevel)) {
        TraceRecord(traceLevel,
                    TP(TP_TX_RRS_JNI_ATR4PDUE_ENTRY),
                    "tx_rrs_services_jni.ntv_postDeferredURExit. Entry",
                    TRACE_DATA_END_PARMS);
    }

    int registryRC = -1;
    atr_return_code rrs_rc = -1;

    // Get the authorized server PC stub to be able to call authorized services.
    const server_authorized_function_stubs* auth_stubs_p = getServerAuthorizedFunctionStubs();
    if (auth_stubs_p == NULL) {
        throwBaseJavaException(env, "java/lang/RuntimeException", "Could not find authorized function stubs");
        return -1;
    }

    struct atr4pdue_parms p = {
        .rrsReturnCode = &rrs_rc,
        .registryReturnCode = &registryRC,
        .exitNumber = exit_number,
        .completionCode = completion_code
    };

    if (getByteArray(uriRegistryToken, (char*)&(p.uriRegistryToken), sizeof(RegistryToken), env) == 0) {
        return -1;
    }

    // PC to the authorized metal C code to drive the service.
    int pc_rc = auth_stubs_p->atr_post_deferred_ur_exit(&p);
    if (pc_rc != 0) traceLevel = trc_level_exception;

    if (TraceActive(traceLevel)) {
        TraceRecord(traceLevel,
                    TP(TP_TX_RRS_JNI_ATR4PDUE_EXIT),
                    "tx_rrs_services_jni.ntv_postDeferredURExit. Exit",
                    TRACE_DATA_INT(pc_rc, "Authorized PC Return Code"),
                    TRACE_DATA_INT(rrs_rc, "RRS Return Code"),
                    TRACE_DATA_INT(registryRC, "Registry Return Code"),
                    TRACE_DATA_END_PARMS);
    }

    if (pc_rc != 0) {
        throwBaseJavaException(env, "java/lang/RuntimeException", "Bad PC return code");
        return -1;
    }

    // If we had a registry error, throw.
    if (registryRC != 0) {
        throwURINotFound(env, (jint)registryRC);
        return -1;
    }

    return rrs_rc;
}

//-----------------------------------------------------------------------------
// Respond to the retrieved interest.
//-----------------------------------------------------------------------------
JNIEXPORT jint JNICALL
ntv_respondToRetrievedInterest(JNIEnv* env,
                               jclass clazz,
                               jbyteArray uriRegistryToken,
                               jint response_code,
                               jbyteArray non_pdata) {
    int traceLevel = trc_level_detailed;
    if (TraceActive(traceLevel)) {
        TraceRecord(traceLevel,
                    TP(TP_TX_RRS_JNI_ATR4IRRI_ENTRY),
                    "tx_rrs_services_jni.ntv_respondToRetrievedInterest. Entry",
                    TRACE_DATA_END_PARMS);
    }

    int registryRC = -1;
    atr_return_code rrs_rc = -1;

    // Get the authorized server PC stub to be able to call authorized services.
    const server_authorized_function_stubs* auth_stubs_p = getServerAuthorizedFunctionStubs();
    if (auth_stubs_p == NULL) {
        throwBaseJavaException(env, "java/lang/RuntimeException", "Could not find authorized function stubs");
        return -1;
    }

    struct atr4irri_parms p = {
        .rrsReturnCode = &rrs_rc,
        .registryReturnCode = &registryRC,
        .responseCode = response_code,
    };

    if (getByteArray(uriRegistryToken, (char*)&(p.uriRegistryToken), sizeof(RegistryToken), env) == 0) {
        return -1;
    }

    if (getByteArray(non_pdata, p.nonPData, sizeof(atr_non_persistent_data), env) == 0) {
        return -1;
    }

    // PC to the authorized metal C code to drive the service.
    int pc_rc = auth_stubs_p->atr_respond_to_retrieved_interest(&p);
    if (pc_rc != 0) traceLevel = trc_level_exception;

    if (TraceActive(traceLevel)) {
        TraceRecord(traceLevel,
                    TP(TP_TX_RRS_JNI_ATR4IRRI_EXIT),
                    "tx_rrs_services_jni.ntv_respondToRetrievedInterest. Exit",
                    TRACE_DATA_INT(pc_rc, "Authorized PC Return Code"),
                    TRACE_DATA_INT(rrs_rc, "RRS Return Code"),
                    TRACE_DATA_INT(registryRC, "Registry Return Code"),
                    TRACE_DATA_END_PARMS);
    }

    if (pc_rc != 0) {
        throwBaseJavaException(env, "java/lang/RuntimeException", "Bad PC return code");
        return -1;
    }

    // If we had a registry error, throw.
    if (registryRC != 0) {
        throwURINotFound(env, (jint)registryRC);
        return -1;
    }

    return rrs_rc;
}

//-----------------------------------------------------------------------------
// Sets the persistent interest data for the UR associated with the input URI token.
//-----------------------------------------------------------------------------
JNIEXPORT jint JNICALL
ntv_setPersistentInterestData(JNIEnv* env,
                              jclass clazz,
                              jbyteArray uriRegistryToken,
                              jint pdataLength,
                              jbyteArray persistentData) {
    int traceLevel = trc_level_detailed;
    if (TraceActive(traceLevel)) {
        TraceRecord(traceLevel,
                    TP(TP_TX_RRS_JNI_ATR4SPID_ENTRY),
                    "tx_rrs_services_jni.ntv_setPersistentInterestData. Entry",
                    TRACE_DATA_END_PARMS);
    }

    int registryRC = -1;
    atr_return_code rrs_rc = -1;

    // Get the authorized server PC stub to be able to call authorized services.
    const server_authorized_function_stubs* auth_stubs_p = getServerAuthorizedFunctionStubs();
    if (auth_stubs_p == NULL) {
        throwBaseJavaException(env, "java/lang/RuntimeException", "Could not find authorized function stubs");
        return -1;
    }

    // Validate PDATA length input.
    if (pdataLength < 0 || pdataLength > ATR_MAX_PDATA_LENGTH) {
        char excMessage[256];
        snprintf(excMessage, 256, "The pdataLength input of %i to setPersistentInterestData is invalid. The maximum allowed length is %i bytes", pdataLength, ATR_MAX_PDATA_LENGTH);
        throwBaseJavaException(env, "java/lang/RuntimeException", excMessage);
        return -1;
    }

    char pdata[pdataLength];
    if (getByteArray(persistentData, pdata, pdataLength, env) == 0){
        return -1;
    }

    struct atr4spid_parms p = {
        .rrsReturnCode = &rrs_rc,
        .registryReturnCode = &registryRC,
        .pdataLength = pdataLength,
        .pdata = pdata
    };

    if (getByteArray(uriRegistryToken, (char*)&(p.uriRegistryToken), sizeof(RegistryToken), env) == 0) {
        return -1;
    }

    // PC to the authorized metal C code to drive the service.
    int pc_rc = auth_stubs_p->atr_set_persistent_interest_data(&p);
    if (pc_rc != 0) traceLevel = trc_level_exception;

    if (TraceActive(traceLevel)) {
        TraceRecord(traceLevel,
                    TP(TP_TX_RRS_JNI_ATR4SPID_EXIT),
                    "tx_rrs_services_jni.ntv_setPersistentInterestData. Exit",
                    TRACE_DATA_INT(pc_rc, "Authorized PC Return Code"),
                    TRACE_DATA_INT(rrs_rc, "RRS Return Code"),
                    TRACE_DATA_INT(registryRC, "Registry Return Code"),
                    TRACE_DATA_END_PARMS);
    }

    if (pc_rc != 0) {
        throwBaseJavaException(env, "java/lang/RuntimeException", "Bad PC return code");
        return -1;
    }

    // Check for internal failures during the invocation of the authorized code.
    if (rrs_rc == -1) {
        throwBaseJavaException(env, "java/lang/RuntimeException", "Failure detected during the invocation authorized routine to set persistent interest data");
        return -1;
    }
    
    // If we had a registry error, throw.
    if (registryRC != 0) {
        throwURINotFound(env, (jint)registryRC);
        return -1;
    }

    return rrs_rc;
}

//-----------------------------------------------------------------------------
// Sets syncpoint controls to define the resource manager role and pre-vote exits.
//-----------------------------------------------------------------------------
JNIEXPORT jint JNICALL
ntv_setSyncpointControls(JNIEnv* env,
                         jclass clazz,
                         jbyteArray uriRegistryToken,
                         jint prepare_exitCode,
                         jint commit_exitCode,
                         jint backout_exitCode,
                         jint role) {
    int traceLevel = trc_level_detailed;
    if (TraceActive(traceLevel)) {
        TraceRecord(traceLevel,
                    TP(TP_TX_RRS_JNI_ATR4SSPC_ENTRY),
                    "tx_rrs_services_jni.ntv_setSyncpointControls. Entry",
                    TRACE_DATA_END_PARMS);
    }

    int registryRC = -1;
    atr_return_code rrs_rc = -1;

    // Get the authorized server PC stub to be able to call authorized services.
    const server_authorized_function_stubs* auth_stubs_p = getServerAuthorizedFunctionStubs();
    if (auth_stubs_p == NULL) {
        throwBaseJavaException(env, "java/lang/RuntimeException", "Could not find authorized function stubs");
        return -1;
    }

    struct atr4sspc_parms p = {
        .rrsReturnCode = &rrs_rc,
        .registryReturnCode = &registryRC,
        .prepareExitCode = prepare_exitCode,
        .commitExitCode = commit_exitCode,
        .backoutExitCode = backout_exitCode,
        .rmRole = role
    };

    if (getByteArray(uriRegistryToken, (char*)&(p.uriRegistryToken), sizeof(RegistryToken), env) == 0) {
        return -1;
    }

    // PC to the authorized metal C code to drive the service.
    int pc_rc = auth_stubs_p->atr_set_syncpoint_controls(&p);
    if (pc_rc != 0) traceLevel = trc_level_exception;

    if (TraceActive(traceLevel)) {
        TraceRecord(traceLevel,
                    TP(TP_TX_RRS_JNI_ATR4SSPC_EXIT),
                    "tx_rrs_services_jni.ntv_setSyncpointControls. Exit",
                    TRACE_DATA_INT(pc_rc, "Authorized PC Return Code"),
                    TRACE_DATA_INT(rrs_rc, "RRS Return Code"),
                    TRACE_DATA_INT(registryRC, "Registry Return Code"),
                    TRACE_DATA_END_PARMS);
    }

    if (pc_rc != 0) {
        throwBaseJavaException(env, "java/lang/RuntimeException", "Bad PC return code");
        return -1;
    }

    // If we had a registry error, throw.
    if (registryRC != 0) {
        throwURINotFound(env, (jint)registryRC);
        return -1;
    }

    return rrs_rc;
}

//-----------------------------------------------------------------------------
// Sets side information for an interest in a unit of recovery.
//-----------------------------------------------------------------------------
JNIEXPORT jint JNICALL
ntv_setSideInformation(JNIEnv* env,
                       jclass clazz,
                       jbyteArray uriRegistryToken,
                       jint elementCount,
                       jintArray infoIds) {
    int traceLevel = trc_level_detailed;
    if (TraceActive(traceLevel)) {
        TraceRecord(traceLevel,
                    TP(TP_TX_RRS_JNI_ATR4SUSI_ENTRY),
                    "tx_rrs_services_jni.ntv_setSideInformation. Entry",
                    TRACE_DATA_END_PARMS);
    }

    int registryRC = -1;
    atr_return_code rrs_rc = -1;

    // Get the authorized server PC stub to be able to call authorized services.
    const server_authorized_function_stubs* auth_stubs_p = getServerAuthorizedFunctionStubs();
    if (auth_stubs_p == NULL) {
        throwBaseJavaException(env, "java/lang/RuntimeException", "Could not find authorized function stubs");
        return -1;
    }

    // Validate the element count input.
    if (elementCount < 0 || elementCount > MAX_ELEMENT_COUNT) {
        char excMessage[256];
        snprintf(excMessage, 256, "The elementCount input of %i to setSideInformation is invalid. The maximum allowed length is %i bytes", elementCount, MAX_ELEMENT_COUNT);
        throwBaseJavaException(env, "java/lang/RuntimeException", excMessage);
        return -1;
    }

    int infoIdsArray[MAX_EXIT_COUNT];
    if (getIntArray(infoIds, infoIdsArray, elementCount, env) == 0) {
        return -1;
    }

    struct atr4susi_parms p = {
        .rrsReturnCode = &rrs_rc,
        .registryReturnCode = &registryRC,
        .elementCount = elementCount,
        .infoIdsArray = infoIdsArray
    };

    if (getByteArray(uriRegistryToken, (char*)&(p.uriRegistryToken), sizeof(RegistryToken), env) == 0) {
        return -1;
    }

    // PC to the authorized metal C code to drive the service.
    int pc_rc = auth_stubs_p->atr_set_side_information(&p);
    if (pc_rc != 0) traceLevel = trc_level_exception;

    if (TraceActive(traceLevel)) {
        TraceRecord(traceLevel,
                    TP(TP_TX_RRS_JNI_ATR4SUSI_EXIT),
                    "tx_rrs_services_jni.ntv_setSideInformation. Exit",
                    TRACE_DATA_INT(pc_rc, "Authorized PC Return Code"),
                    TRACE_DATA_INT(rrs_rc, "RRS Return Code"),
                    TRACE_DATA_INT(registryRC, "Registry Return Code"),
                    TRACE_DATA_END_PARMS);
    }

    if (pc_rc != 0) {
        throwBaseJavaException(env, "java/lang/RuntimeException", "Bad PC return code");
        return -1;
    }

    // Check for internal failures during the invocation of the authorized code.
    if (rrs_rc == -1) {
        throwBaseJavaException(env, "java/lang/RuntimeException", "Failure detected during the invocation authorized routine to set side information");
        return -1;
    }
    
    // If we had a registry error, throw.
    if (registryRC != 0) {
        throwURINotFound(env, (jint)registryRC);
        return -1;
    }

    return rrs_rc;
}

//-----------------------------------------------------------------------------
// Sets metadata information for the specified resource manager.
//-----------------------------------------------------------------------------
JNIEXPORT jint JNICALL
ntv_setRMMetadata(JNIEnv* env,
                  jclass clazz,
                  jbyteArray rmRegistryToken,
                  jint metadataLength,
                  jbyteArray metadata) {
    int traceLevel = trc_level_detailed;
    if (TraceActive(traceLevel)) {
        TraceRecord(traceLevel,
                    TP(TP_TX_RRS_JNI_ATR4SDTA_ENTRY),
                    "tx_rrs_services_jni.ntv_setRMMetadata. Entry",
                    TRACE_DATA_END_PARMS);
    }

    atr_return_code rrs_rc = -1;
    int resMgrTokenRegistryRC = 0;

    // Get the authorized server PC stub to be able to call authorized services.
    const server_authorized_function_stubs* auth_stubs_p = getServerAuthorizedFunctionStubs();
    if (auth_stubs_p == NULL) {
        throwBaseJavaException(env, "java/lang/RuntimeException", "Could not find authorized function stubs");
        return -1;
    }

    // Validate the data length.
    if (metadataLength < 0 || metadataLength > ATR_MAX_RM_METADATA_LENGTH) {
        char excMessage[256];
        snprintf(excMessage, 256, "The metadataLength input of %i to setLogName is invalid. The maximum allowed length is %i bytes", metadataLength, ATR_MAX_RM_METADATA_LENGTH);
        throwBaseJavaException(env, "java/lang/RuntimeException", excMessage);
        return -1;
    }

    char rmMetadata[metadataLength];
    if (getByteArray(metadata, rmMetadata, metadataLength, env) == 0) {
        return -1;
    }

    struct atr4sdta_parms p = {
        .returnCode = &rrs_rc,
        .resMgrTokenRegistryReturnCode = &resMgrTokenRegistryRC,
        .resMgrMetadataLen = metadataLength,
        .resMgrMetadata = rmMetadata
    };

    if (getByteArray(rmRegistryToken, (char*)&(p.resMgrToken), sizeof(RegistryToken), env) == 0) {
        return -1;
    }

    // PC to the authorized metal C code to drive the service.
    int pc_rc = auth_stubs_p->atr_set_rm_metadata(&p);
    if (pc_rc != 0 || (resMgrTokenRegistryRC != 0)) traceLevel = trc_level_exception;

    if (TraceActive(traceLevel)) {
        TraceRecord(traceLevel,
                    TP(TP_TX_RRS_JNI_ATR4SDTA_EXIT),
                    "tx_rrs_services_jni.ntv_setRMMetadata. Exit",
                    TRACE_DATA_INT(pc_rc, "Authorized PC Return Code"),
                    TRACE_DATA_INT(rrs_rc, "RRS Return Code"),
                    TRACE_DATA_INT(resMgrTokenRegistryRC, "Registry Return Code"),
                    TRACE_DATA_END_PARMS);
    }

    if (pc_rc != 0) {
        throwBaseJavaException(env, "java/lang/RuntimeException", "Bad PC return code");
        return -1;
    }

    if (resMgrTokenRegistryRC != 0) {
        throwBaseJavaException(env, "java/lang/RuntimeException", "Bad registry return code for resource manager token");
        return -1;
    }

    // Check for internal failures during the invocation of the authorized code.
    if (rrs_rc == -1) {
        throwBaseJavaException(env, "java/lang/RuntimeException", "Failure detected during the invocation authorized routine to set resource manager metadata");
        return -1;
    }

    return rrs_rc;
}

//-----------------------------------------------------------------------------
// Retrieves metadata information for the specified resource manager.
//-----------------------------------------------------------------------------
JNIEXPORT jobject JNICALL
ntv_retrieveRMMetadata(JNIEnv* env,
                       jclass clazz,
                       jbyteArray rmRegistryToken) {

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(TP_TX_RRS_JNI_ATR4RDTA_ENTRY),
                    "tx_rrs_services_jni.ntv_retrieveRMMetadata. Entry",
                    TRACE_DATA_END_PARMS);
    }

    atr_return_code rrs_rc = -1;
    int resMgrTokenRegistryRC = 0;

    // Get the authorized server PC stub to be able to call authorized services.
    const server_authorized_function_stubs* auth_stubs_p = getServerAuthorizedFunctionStubs();
    if (auth_stubs_p == NULL) {
        throwBaseJavaException(env, "java/lang/RuntimeException", "Could not find authorized function stubs");
        return NULL;
    }

    int rmMetadataLen = 0;
    char rmMetadata[ATR_MAX_RM_METADATA_LENGTH];

    struct atr4rdta_parms p = {
        .returnCode = &rrs_rc,
        .resMgrTokenRegistryReturnCode = &resMgrTokenRegistryRC,
        .resMgrMetadataBuffLen = ATR_MAX_RM_METADATA_LENGTH,
        .resMgrMetadataLength = &rmMetadataLen,
        .resMgrMetadata = rmMetadata
    };

    if (getByteArray(rmRegistryToken, (char*)&(p.resMgrToken), sizeof(RegistryToken), env) == 0) {
        return NULL;
    }

    // PC to the authorized metal C code to drive the service.
    int pc_rc = auth_stubs_p->atr_retrieve_rm_metadata(&p);

    if (pc_rc != 0) {
        if (TraceActive(trc_level_exception)) {
            TraceRecord(trc_level_exception,
                        TP(TP_TX_RRS_JNI_ATR4RDTA_BAD_PC_RC),
                        "tx_rrs_services_jni.ntv_retrieveRMMetadata. Bad PC RC",
                        TRACE_DATA_INT(pc_rc, "Authorized PC Return Code"),
                        TRACE_DATA_END_PARMS);
        }
        throwBaseJavaException(env, "java/lang/RuntimeException", "Bad PC return code");
        return NULL;
    }

    if (resMgrTokenRegistryRC != 0) {
        if (TraceActive(trc_level_exception)) {
            TraceRecord(trc_level_exception,
                        TP(TP_TX_RRS_JNI_ATR4RDTA_BAD_REGISTRY_RM_NAME_RC),
                        "tx_rrs_services_jni.ntv_retrieveRMMetadata. Bad registry RC",
                        TRACE_DATA_INT(resMgrTokenRegistryRC, "Resource manager token registry Return Code"),
                        TRACE_DATA_END_PARMS);
        }
        throwBaseJavaException(env, "java/lang/RuntimeException", "Bad registry return code for resource manager token");
        return NULL;
    }

    //-------------------------------------------------------------------------
    // Package the service's output data into a jobject.
    //-------------------------------------------------------------------------
    jbyteArray jRMMetadata = NULL;

    if (rrs_rc == ATR_OK) {
        jRMMetadata = (*env)->NewByteArray(env, rmMetadataLen);
        CHECK_JAVA_EXCEPTION_INPUT(env, jRMMetadata);
        (*env)->SetByteArrayRegion(env, jRMMetadata, 0, rmMetadataLen, (jbyte*) rmMetadata);
        CHECK_JAVA_EXCEPTION(env);
    }

    jobject returnObj = (*env)->NewObject(env,
                                          jRetrieveRMMetadataReturnParmsClass,
                                          jRetrieveRMMetadataReturnParmsConstructor,
                                          (jint) rrs_rc,
                                          jRMMetadata);

    CHECK_JAVA_EXCEPTION_INPUT(env, returnObj);

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(TP_TX_RRS_JNI_ATR4RDTA_ENTRY),
                    "tx_rrs_services_jni.ntv_retrieveRMMetadata. Exit",
                    TRACE_DATA_INT(rrs_rc, "RRS Return Code"),
                    TRACE_DATA_RAWDATA(rmMetadataLen, rmMetadata, "RM Metadata"),
                    TRACE_DATA_END_PARMS);
    }

    return returnObj;
}

//-----------------------------------------------------------------------------
// Creates a privately managed context.
//-----------------------------------------------------------------------------
JNIEXPORT jobject JNICALL
ntv_beginContext(JNIEnv* env,
                 jclass clazz,
                 jbyteArray rmRegistryToken) {

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(TP_TX_RRS_JNI_ATR4BEGC_ENTRY),
                    "tx_rrs_services_jni.ntv_beginContext. Entry",
                    TRACE_DATA_END_PARMS);
    }

    atr_return_code rrs_rc;
    int resMgrTokenRegistryRC = 0;

    // Get the authorized server PC stub to be able to call authorized services.
    const server_authorized_function_stubs* auth_stubs_p = getServerAuthorizedFunctionStubs();
    if (auth_stubs_p == NULL) {
        throwBaseJavaException(env, "java/lang/RuntimeException", "Could not find authorized function stubs");
        return NULL;
    }

    ctx_context_token localCtxToken;
    RegistryToken ctxRegistryToken;

    struct ctx4begc_parms p = {
        .returnCode = &rrs_rc,
        .resMgrTokenRegistryReturnCode = &resMgrTokenRegistryRC,
        .ctxToken = localCtxToken,
        .ctxRegistryToken = &ctxRegistryToken
    };

    if (getByteArray(rmRegistryToken, (char*)&(p.rmRegistryToken), sizeof(RegistryToken), env) == 0) {
        return NULL;
    }

    // PC to the authorized metal C code to drive the service.
    int pc_rc = auth_stubs_p->ctx_begin_context(&p);

    if (pc_rc != 0) {
        if (TraceActive(trc_level_exception)) {
            TraceRecord(trc_level_exception,
                        TP(TP_TX_RRS_JNI_ATR4BEGC_BAD_PC_RC),
                        "tx_rrs_services_jni.ntv_beginContext. Bad PC RC",
                        TRACE_DATA_INT(pc_rc, "Authorized PC Return Code"),
                        TRACE_DATA_END_PARMS);
        }
        throwBaseJavaException(env, "java/lang/RuntimeException", "Bad PC return code");
        return NULL;
    }

    if (resMgrTokenRegistryRC != 0) {
        if (TraceActive(trc_level_exception)) {
            TraceRecord(trc_level_exception,
                        TP(TP_TX_RRS_JNI_ATR4BEGC_BAD_REGISTRY_RM_TOKEN_RC),
                        "tx_rrs_services_jni.ntv_beginContext. Bad registry RC",
                        TRACE_DATA_INT(resMgrTokenRegistryRC, "Resource manager token registry Return Code"),
                        TRACE_DATA_END_PARMS);
        }
        throwBaseJavaException(env, "java/lang/RuntimeException", "Bad registry return code for resource manager token");
        return NULL;
    }

    // Package the service's output data into a jobject.
    jbyteArray jLocalCtxToken = NULL;
    jbyteArray jLocalCtxRegistryToken = NULL;

    if (rrs_rc == ATR_OK) {
        jLocalCtxToken = (*env)->NewByteArray(env, sizeof(ctx_context_token));
        CHECK_JAVA_EXCEPTION_INPUT(env, jLocalCtxToken);
        (*env)->SetByteArrayRegion(env, jLocalCtxToken, 0, sizeof(ctx_context_token), (jbyte*) localCtxToken);
        CHECK_JAVA_EXCEPTION(env);

        jLocalCtxRegistryToken = (*env)->NewByteArray(env, sizeof(ctxRegistryToken));
        CHECK_JAVA_EXCEPTION_INPUT(env, jLocalCtxRegistryToken);
        (*env)->SetByteArrayRegion(env, jLocalCtxRegistryToken, 0, sizeof(ctxRegistryToken), (jbyte*)&ctxRegistryToken);
        CHECK_JAVA_EXCEPTION(env);
    }

    jobject returnObj = (*env)->NewObject(env,
                                          jBegingCtxReturnParmsClass,
                                          jBegingCtxReturnParmsConstructor,
                                          (jint) rrs_rc,
                                          jLocalCtxToken,
                                          jLocalCtxRegistryToken);
    CHECK_JAVA_EXCEPTION_INPUT(env, returnObj);

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(TP_TX_RRS_JNI_ATR4BEGC_EXIT),
                    "tx_rrs_services_jni.ntv_beginContext. Exit",
                    TRACE_DATA_INT(rrs_rc, "RRS Return Code"),
                    TRACE_DATA_RAWDATA(sizeof(ctx_context_token), &localCtxToken, "Context Token"),
                    TRACE_DATA_END_PARMS);
    }

    return returnObj;
}

//-----------------------------------------------------------------------------
// Switches the context on the current thread with the one specified.
//-----------------------------------------------------------------------------
JNIEXPORT jobject JNICALL
ntv_contextSwitch(JNIEnv* env,
                  jclass clazz,
                  jbyteArray ctxRegistryToken) {

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(TP_TX_RRS_JNI_ATR4SWCH_ENTRY),
                    "tx_rrs_services_jni.ntv_contextSwitch. Entry",
                    TRACE_DATA_END_PARMS);
    }

    ctx_return_code ctxs_rc;

    // Get the authorized server PC stub to be able to call authorized services.
    const server_authorized_function_stubs* auth_stubs_p = getServerAuthorizedFunctionStubs();
    if (auth_stubs_p == NULL) {
        throwBaseJavaException(env, "java/lang/RuntimeException", "Could not find authorized function stubs");
        return NULL;
    }

    ctx_context_token localOutCtxToken;

    struct ctx4swch_parms p = {
        .returnCode = &ctxs_rc,
        .outputCtxToken = localOutCtxToken
    };

    // -----------------------------------------------------------------------
    // If no context registry token was passed down, we're switching to the
    // native context.
    // -----------------------------------------------------------------------
    if (ctxRegistryToken != NULL) {
        if (getByteArray(ctxRegistryToken, (char*)(&(p.inputCtxRegistryToken)), sizeof(p.inputCtxRegistryToken), env) == 0) {
            return NULL;
        }
    } else {
        memset(&(p.inputCtxRegistryToken), 0, sizeof(p.inputCtxRegistryToken));
    }

    // PC to the authorized metal C code to drive the service.
    int pc_rc = auth_stubs_p->ctx_context_switch(&p);

    if (pc_rc != 0) {
        if (TraceActive(trc_level_exception)) {
            TraceRecord(trc_level_exception,
                        TP(TP_TX_RRS_JNI_ATR4SWCH_BAD_PC_RC),
                        "tx_rrs_services_jni.ntv_contextSwitch. Bad PC RC",
                        TRACE_DATA_INT(pc_rc, "Authorized PC Return Code"),
                        TRACE_DATA_END_PARMS);
        }
        throwBaseJavaException(env, "java/lang/RuntimeException", "Bad PC return code");
        return NULL;
    }

    // Package the service's output data into a jobject.
    jbyteArray jLocalOutCtxToken = NULL;

    if (ctxs_rc == ATR_OK) {
        jLocalOutCtxToken = (*env)->NewByteArray(env, sizeof(ctx_context_token));
        CHECK_JAVA_EXCEPTION_INPUT(env, jLocalOutCtxToken);
        (*env)->SetByteArrayRegion(env, jLocalOutCtxToken, 0, sizeof(ctx_context_token), (jbyte*) localOutCtxToken);
        CHECK_JAVA_EXCEPTION(env);
    }

    jobject returnObj = (*env)->NewObject(env,
                                          jSwitchCtxReturnParmsClass,
                                          jSwitchCtxReturnParmsConstructor,
                                          (jint) ctxs_rc,
                                          jLocalOutCtxToken);
    CHECK_JAVA_EXCEPTION_INPUT(env, returnObj);

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed, TP(TP_TX_RRS_JNI_ATR4SWCH_EXIT),
            "tx_rrs_services_jni.ntv_contextSwitch. Exit",
            TRACE_DATA_INT(ctxs_rc, "RRS Return Code"),
            TRACE_DATA_RAWDATA(sizeof(ctx_context_token), &localOutCtxToken, "Context Token"),
            TRACE_DATA_END_PARMS);
    }

    return returnObj;
}

//-----------------------------------------------------------------------------
// Ends the context associated with the given context token.
//-----------------------------------------------------------------------------
JNIEXPORT jint JNICALL
ntv_endContext(JNIEnv* env,
               jclass clazz,
               jbyteArray ctxRegistryToken,
               jint completionType) {
    int traceLevel = trc_level_detailed;
    if (TraceActive(traceLevel)) {
        TraceRecord(traceLevel,
                    TP(TP_TX_RRS_JNI_ATR4ENDC_ENTRY),
                    "tx_rrs_services_jni.ntv_endContext. Entry",
                    TRACE_DATA_END_PARMS);
    }

    ctx_return_code ctxs_rc = -1;

    // Get the authorized server PC stub to be able to call authorized services.
    const server_authorized_function_stubs* auth_stubs_p = getServerAuthorizedFunctionStubs();
    if (auth_stubs_p == NULL) {
        throwBaseJavaException(env, "java/lang/RuntimeException", "Could not find authorized function stubs");
        return ctxs_rc;
    }

    struct ctx4endc_parms p = {
        .returnCode = &ctxs_rc,
        .completionType = completionType
    };

    // If no context registry token was passed down, we end the native context.
    if (ctxRegistryToken != NULL) {
        if (getByteArray(ctxRegistryToken, (char*)(&(p.inputCtxRegistryToken)), sizeof(p.inputCtxRegistryToken), env) == 0) {
            return -1;
        }
    } else {
        memset(&(p.inputCtxRegistryToken), 0, sizeof(p.inputCtxRegistryToken));
    }

    // PC to the authorized metal C code to drive the service.
    int pc_rc = auth_stubs_p->ctx_end_context(&p);
    if (pc_rc != 0) traceLevel = trc_level_exception;

    if (TraceActive(traceLevel)) {
        TraceRecord(traceLevel,
                    TP(TP_TX_RRS_JNI_ATR4ENDC_EXIT),
                    "tx_rrs_services_jni.ntv_endContext. Exit",
                    TRACE_DATA_INT(pc_rc, "Authorized PC Return Code"),
                    TRACE_DATA_INT(ctxs_rc, "RRS Return Code"),
                    TRACE_DATA_END_PARMS);
    }

    if (pc_rc != 0) {
        throwBaseJavaException(env, "java/lang/RuntimeException", "Bad PC return code");
        return -1;
    }

    return ctxs_rc;
}

//-----------------------------------------------------------------------------
// Retrieves the current context token.
//-----------------------------------------------------------------------------
JNIEXPORT jobject JNICALL
ntv_retrieveCurrentContextToken(JNIEnv* env,
                                jclass clazz)
{
    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(TP_TX_RRS_JNI_CTX4RCC_ENTRY),
                    "tx_rrs_services_jni.ntv_retrieveCurrentContextToken. Entry",
                    TRACE_DATA_END_PARMS);
    }

    ctx_context_token ctxToken;
    memset(&ctxToken, 0, sizeof(ctxToken));

    int rc = -1;

    ctx4rcc(&rc, ctxToken);

    // Package the service's output data into a jobject.
    jbyteArray jCtxToken = NULL;

    if (rc == ATR_OK) {
        jCtxToken = (*env)->NewByteArray(env, sizeof(ctx_context_token));
        CHECK_JAVA_EXCEPTION_INPUT(env, jCtxToken);
        (*env)->SetByteArrayRegion(env, jCtxToken, 0, sizeof(ctx_context_token), (jbyte*) ctxToken);
        CHECK_JAVA_EXCEPTION(env);
    }

    jobject returnObj = (*env)->NewObject(env,
                                          jRetrieveCurrentCtxTokenReturnParmsClass,
                                          jRetrieveCurrentCtxTokenReturnParmsConstructor,
                                          (jint) rc,
                                          jCtxToken);
    CHECK_JAVA_EXCEPTION_INPUT(env, returnObj);

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed, TP(TP_TX_RRS_JNI_CTX4RCC_EXIT),
            "tx_rrs_services_jni.ntv_retrieveCurrentCtxToken. Exit",
            TRACE_DATA_INT(rc, "RRS Return Code"),
            TRACE_DATA_RAWDATA(sizeof(ctx_context_token), &ctxToken, "Context Token"),
            TRACE_DATA_END_PARMS);
    }

    return returnObj;
}
