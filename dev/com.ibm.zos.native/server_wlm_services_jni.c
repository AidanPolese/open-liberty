/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2011, 2016
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */

/**
 * @file
 *
 * JNI assorted routines that interface with z/OS Workload Management Services
 * to perform authorized and unauthorzied tasks.

 */
#include <assert.h>
#include <dlfcn.h>
#include <errno.h>
#include <jni.h>
#include <stdio.h>
#include <stdlib.h>
#include <sys/__wlm.h>             // C interfaces to WLM services

#include "include/ras_tracing.h"
#include "include/server_ipt_stubs.h"
#include "include/server_jni_method_manager.h"
#include "include/server_native_service_tracker.h"
#include "include/server_wlm_services.h"

#define RAS_MODULE_CONST  RAS_MODULE_SERVER_WLM_SERVICES_JNI

#define TP_CREATEWORKUNIT_CALL                      1
#define TP_CREATEWORKUNIT_RETURN                    2
#define TP_DELETEWORKUNIT_CALL                      3
#define TP_DELETEWORKUNIT_RETURN                    4
#define TP_JOINWORKUNIT_CALL                        5
#define TP_JOINWORKUNIT_RETURN                      6
#define TP_LEAVEWORKUNIT_CALL                       7
#define TP_LEAVEWORKUNIT_RETURN                     8
#define TP_POPULATEWLMSERVICERESULTS_ENTRY         12
#define TP_POPULATEWLMSERVICERESULTS_PUSHRESULTS   13
#define TP_CONNECTASWORKMGR_ENTRY                  14
#define TP_CONNECTASWORKMGR_EXIT                   15
#define TP_A_CONNECTASWORKMGR_ENTRY                16
#define TP_A_CONNECTASWORKMGR_EXIT                 17
#define TP_A_CREATEWORKUNIT_ENTRY                  18
#define TP_A_CREATEWORKUNIT_CALL                   19
#define TP_A_CREATEWORKUNIT_RETURN                 20
#define TP_A_CREATEWORKUNIT_BAD_PC_RC              21
#define TP_A_JOINWORKUNIT_ENTRY                    22
#define TP_A_JOINWORKUNIT_CALL                     23
#define TP_A_JOINWORKUNIT_RETURN                   24
#define TP_A_JOINWORKUNIT_BAD_PC_RC                25
#define TP_A_JOINWORKUNIT_EXIT                     26
#define TP_A_LEAVEWORKUNIT_ENTRY                   27
#define TP_A_LEAVEWORKUNIT_CALL                    28
#define TP_A_LEAVEWORKUNIT_RETURN                  29
#define TP_A_LEAVEWORKUNIT_BAD_PC_RC               30
#define TP_A_LEAVEWORKUNIT_EXIT                    31
#define TP_A_DELETEWORKUNIT_ENTRY                  32
#define TP_A_DELETEWORKUNIT_CALL                   33
#define TP_A_DELETEWORKUNIT_RETURN                 34
#define TP_A_DELETEWORKUNIT_BAD_PC_RC              35
#define TP_A_DELETEWORKUNIT_EXIT                   36
#define TP_A_CONNECTASWORKMGR_IPTEXIT_SKIPPED      41
#define TP_A_CONNECTASWORKMGR_IPT_BAD_BPX_RC       42
#define TP_A_DISCONNECTASWORKMGR_ENTRY             43
#define TP_A_DISCONNECTASWORKMGR_PRECALL           44
#define TP_A_DISCONNECTASWORKMGR_RETURN            45
#define TP_A_DISCONNECTASWORKMGR_BAD_PC_RC         46
#define TP_A_DISCONNECTASWORKMGR_EXIT              47
#define TP_A_CREATEJOINWORKUNIT_ENTRY              48
#define TP_A_CREATEJOINWORKUNIT_CALL               49
#define TP_A_CREATEJOINWORKUNIT_RETURN             50
#define TP_A_CREATEJOINWORKUNIT_BAD_PC_RC          51
#define TP_A_LEAVEDELETEWORKUNIT_ENTRY             52
#define TP_A_LEAVEDELETEWORKUNIT_CALL              53
#define TP_A_LEAVEDELETEWORKUNIT_RETURN            54
#define TP_A_LEAVEDELETEWORKUNIT_BAD_PC_RC         55
#define TP_A_LEAVEDELETEWORKUNIT_EXIT              56
#define TP_CREATEJOINWORKUNIT_ENTRY                57
#define TP_CREATEJOIN_CREATEWORKUNIT_CALL          58
#define TP_CREATEJOIN_CREATEWORKUNIT_RETURN        59
#define TP_CREATEJOIN_JOINWORKUNIT_RETURN          60
#define TP_LEAVEDELETE_LEAVEWORKUNIT_CALL          61
#define TP_LEAVEDELETE_LEAVEWORKUNIT_RETURN        62
#define TP_LEAVEDELETE_DELETEWORKUNIT_RETURN       63
#define TP_DISCONNECTASWORKMGR_ENTRY               64
#define TP_DISCONNECTASWORKMGR_EXIT                65

// --------------------------------------------------------------------
// Unauthorized JNI function declaration and export
// --------------------------------------------------------------------
#pragma export(ntv_le_connectAsWorkMgr)
JNIEXPORT jint JNICALL
ntv_le_connectAsWorkMgr(JNIEnv* env, jclass jobj, jstring subSystem, jstring subSystemName,
                        jstring createFunctionName, jstring classifyCollectionName);

#pragma export(ntv_le_disconnectAsWorkMgr)
JNIEXPORT jint JNICALL
ntv_le_disconnectAsWorkMgr(JNIEnv* env, jclass jobj, jint connectToken);

#pragma export(ntv_le_joinWorkUnit)
JNIEXPORT jboolean JNICALL
ntv_le_joinWorkUnit(JNIEnv* env, jclass jobj, jbyteArray token);

#pragma export(ntv_le_leaveWorkUnit)
JNIEXPORT jboolean JNICALL
ntv_le_leaveWorkUnit(JNIEnv* env, jclass jobj, jbyteArray token);

#pragma export(ntv_le_createWorkUnit)
JNIEXPORT jbyteArray JNICALL
ntv_le_createWorkUnit(JNIEnv* env,
                      jclass jobj,
                      jint connectToken,
                      jbyteArray classificationInfo,
                      jstring createFunctionName,
                      jstring classifyCollectionName,
                      jlong arrivalTime);

#pragma export(ntv_le_createJoinWorkUnit)
JNIEXPORT jbyteArray JNICALL
ntv_le_createJoinWorkUnit(JNIEnv* env,
                          jclass jobj,
                          jint connectToken,
                          jbyteArray transactionClass,
                          jlong arrivalTime);

#pragma export(ntv_le_deleteWorkUnit)
JNIEXPORT int JNICALL
ntv_le_deleteWorkUnit(JNIEnv* env, jclass jobj, jbyteArray etoken);

#pragma export(ntv_le_leaveDeleteWorkUnit)
JNIEXPORT jint JNICALL
ntv_le_leaveDeleteWorkUnit(JNIEnv* env, jclass jobj, jbyteArray token);

// --------------------------------------------------------------------
// JNI native method structure for the UnauthorizedWLMNativeServices
// methods
// --------------------------------------------------------------------
#pragma convert("ISO8859-1")
static const JNINativeMethod UnauthorizedWLMNativeServicesMethods[] = {
    { "ntv_le_connectAsWorkMgr",
      "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)I",
      (void *) ntv_le_connectAsWorkMgr },
    { "ntv_le_disconnectAsWorkMgr",
      "(I)I",
      (void *) ntv_le_disconnectAsWorkMgr},
    { "ntv_le_joinWorkUnit",
      "([B)Z",
      (void *) ntv_le_joinWorkUnit },
    { "ntv_le_leaveWorkUnit",
      "([B)Z",
      (void *) ntv_le_leaveWorkUnit },
    { "ntv_le_createWorkUnit",
      "(I[BLjava/lang/String;Ljava/lang/String;J)[B",
      (void *) ntv_le_createWorkUnit },
    { "ntv_le_createJoinWorkUnit",
      "(I[BJ)[B",
      (void *) ntv_le_createJoinWorkUnit },
    { "ntv_le_deleteWorkUnit",
      "([B)I",
      (void *) ntv_le_deleteWorkUnit },
    { "ntv_le_leaveDeleteWorkUnit",
      "([B)I",
      (void *) ntv_le_leaveDeleteWorkUnit }
};
#pragma convert(pop)

// --------------------------------------------------------------------
// Callback methods for the Unauthorized WLM services
// --------------------------------------------------------------------
int
unAuthRegFunction(JNIEnv* env, jclass clazz, jobjectArray extraInfo);

int
unAuthDeRegFunction(JNIEnv* env, jclass clazz, jobjectArray extraInfo);

// --------------------------------------------------------------------
// NativeMethodDescriptor for the UnauthorizedWLMNativeServices
// --------------------------------------------------------------------
#pragma export(zJNI_com_ibm_ws_zos_wlm_internal_UnauthorizedWLMNativeServices)
NativeMethodDescriptor zJNI_com_ibm_ws_zos_wlm_internal_UnauthorizedWLMNativeServices = {
    .registrationFunction = unAuthRegFunction,
    .deregistrationFunction = unAuthDeRegFunction,
    .nativeMethodCount = sizeof(UnauthorizedWLMNativeServicesMethods) / sizeof(UnauthorizedWLMNativeServicesMethods[0]),
    .nativeMethods = UnauthorizedWLMNativeServicesMethods
};

// --------------------------------------------------------------------
// Module scoped data
// --------------------------------------------------------------------

// Upcall information
static jclass wlmServiceResultsClass = NULL;
static jmethodID setResultsMethodID = NULL;

// Connection Information shared with subsequent calls (ex., create)
static char wlmFunctionName[WLM_CREATE_FUNCTION_NAME_MAX + 1];
static char wlmCollectionName[WLM_COLLECTIONLIMIT + 1];

/**
 * Classification Information
 *
 * Note: Must match java definition defined in ClassificationInfoImpl.java
 */
#pragma pack(packed)
typedef struct ClassificationInfoImpl {
    unsigned char version;
    unsigned char collectionLength;
    char collection[18];
    char transactionClass[8];
    char user[8];
    char transactionName[8];
} ClassificationInfoImpl;
#pragma pack(reset)

#define MAX_SERVICENAME_LENGTH  256

void
populateWLMServiceResults(JNIEnv* env,
                          int wlmReturnCode,
                          int wlmReasonCode,
                          char* wlmRoutine,
                          int wasReturnCode,
                          char* wasRoutine,
                          jbyteArray returnData);



/**
 * Respond to the registerNatives call performed out of bundle activation and
 * setup the required native infrastructure.  This method will resolve and cache
 * the method ID's for the UnauthorizedWLMNativeService function, and prepare to manage
 * thread-specific references to JNI environments.
 *
 * @param env the JNI environment for the calling thread.
 * @param clazz the class that native registration is occurring for.
 * @param extraInfo context information from the caller.  For the native trace
 *        handler, the first element is the handler instance and the second
 *        element is the name of the trace callback method.
 *
 * @return JNI_OK on success
 * @return JNI_ERR or on error
 */
int unAuthRegFunction(JNIEnv* env, jclass clazz, jobjectArray extraInfo) {
    int rc = JNI_OK;

    // --------------------------------------------------------------------
    // Setup the WLMServiceResults class and methods we need to invoke
    // --------------------------------------------------------------------

    // Get the class/method for "setResults" of the callback function from extra info
    jclass  callbackClass = (jclass) (*env)->GetObjectArrayElement(env, extraInfo, 0);

    //TODO: check each JNI service on whether it can set an exception and/or return 0/null, then
    //      add/enhance checks.


    jstring callbackName = (jstring) (*env)->GetObjectArrayElement(env, extraInfo, 1);
    const char* utfCallbackName = (*env)->GetStringUTFChars(env, callbackName, NULL);
#pragma convert("iso8859-1")
    const char* signature = "(IILjava/lang/String;ILjava/lang/String;[B)V";
#pragma convert(pop)

    wlmServiceResultsClass = callbackClass;
    if ((*env)->ExceptionCheck(env)) {
        return JNI_ERR;
    }

    wlmServiceResultsClass = (jclass) (*env)->NewGlobalRef(env, wlmServiceResultsClass);
    if ((*env)->ExceptionCheck(env)) {
        return JNI_ERR;
    }

    // Get the callback method ID for "setResult"
    setResultsMethodID = (*env)->GetStaticMethodID(env, callbackClass, utfCallbackName, signature);
    (*env)->ReleaseStringUTFChars(env, callbackName, utfCallbackName);
    if ((*env)->ExceptionCheck(env)) {
        return JNI_ERR;
    }

    return rc;
}


/**
 * Respond to the host bundle stop by performing any necessary cleanup.
 *
 * @param env the calling thread's JNI environment.
 * @param clazz the class that deregistration is occurring for.
 * @param extraInfo context provided to the registration function.
 *
 * @return JNI_OK
 */
int
unAuthDeRegFunction(JNIEnv* env, jclass clazz, jobjectArray extraInfo) {

    if (wlmServiceResultsClass != NULL) {
        (*env)->DeleteGlobalRef(env, wlmServiceResultsClass);
        wlmServiceResultsClass = NULL;
    }

    setResultsMethodID = NULL;

    return JNI_OK;
}

/**
 * Connect to WLM as work manager
 */
JNIEXPORT jint JNICALL
ntv_le_connectAsWorkMgr(JNIEnv* env, jclass jobj, jstring jsubSys, jstring jsubSysName,
                        jstring createFunctionName, jstring classifyCollectionName) {

    // Convert subSys and subSysName to EBCDIC
    char* subSysName = NULL;
    char* subSys = NULL;
    const char* utfString = (*env)->GetStringUTFChars(env, jsubSys, NULL);
    if (utfString != NULL) {
        subSys = alloca(strlen(utfString) + 1);
        strcpy(subSys, utfString);
        __atoe(subSys);
        (*env)->ReleaseStringUTFChars(env, jsubSys, utfString);
    } else {
        // OOM Exception pending--just return
        return -1;
    }

    const char* utfString2 = (*env)->GetStringUTFChars(env, jsubSysName, NULL);
    if (utfString2 != NULL) {
        subSysName = alloca(strlen(utfString2) + 1);
        strcpy(subSysName, utfString2);
        __atoe(subSysName);
        (*env)->ReleaseStringUTFChars(env, jsubSysName, utfString2);
    } else {
        // OOM Exception pending--just return
        return -1;
    }

    // Get Classify CollectionName save in global for later use in create
    // (Blank-padded in Java)
    const char* utfString3 = (*env)->GetStringUTFChars(env, classifyCollectionName, NULL);
    if (utfString3 != NULL) {
        strncpy(wlmCollectionName, utfString3, WLM_COLLECTIONLIMIT);
        wlmCollectionName[WLM_COLLECTIONLIMIT] = '\0';
        __atoe(wlmCollectionName);
        (*env)->ReleaseStringUTFChars(env, classifyCollectionName, utfString3);
    } else {
        // OOM Exception pending--just return
        return -1;
    }

    // Get Create Function Name save in global for later use in create
    utfString = (*env)->GetStringUTFChars(env, createFunctionName, NULL);
    if (utfString != NULL) {
        strncpy(wlmFunctionName, utfString, WLM_CREATE_FUNCTION_NAME_MAX);
        wlmFunctionName[WLM_CREATE_FUNCTION_NAME_MAX] = '\0';
        __atoe(wlmFunctionName);
        (*env)->ReleaseStringUTFChars(env, createFunctionName, utfString);

        // Uppercase it...
        for (int i = 0; wlmFunctionName[i]; i++) {
            wlmFunctionName[i] = toupper(wlmFunctionName[i]);
        }
    } else {
        // OOM Exception pending--just return
        return -1;
    }

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(TP_CONNECTASWORKMGR_ENTRY),
                    "ntv_le_connectAsWorkMgr entry",
                    TRACE_DATA_STRING(subSys, "Subsystem"),
                    TRACE_DATA_STRING(subSysName, "Subsystem name"),
                    TRACE_DATA_RAWDATA(sizeof(wlmFunctionName),
                                       wlmFunctionName,
                                       "Create Function name"),
                    TRACE_DATA_RAWDATA(sizeof(wlmCollectionName),
                                       wlmCollectionName,
                                       "Collection name"),
                    TRACE_DATA_END_PARMS);
    }

    // Would like to drive this Unauthorized Connect on the IPT (BPX4IPT) but I
    // dont see an unauthorized option on the IWM4CON service to allow WORK_MANAGER=YES
    // for Enclave management services.
    unsigned int unAuthorizedWlmConnectToken = ConnectWorkMgr(subSys, subSysName);

    if (unAuthorizedWlmConnectToken == -1) {
        // Gather information of failure into results object
        populateWLMServiceResults(env,
                                  errno,
                                  (__errno2() & 0x0000FFFF),
                                  "ConnectWorkMgr",
                                  unAuthorizedWlmConnectToken,
                                  "ntv_le_connectAsWorkMgr",
                                  NULL);
    }

    if (TraceActive(trc_level_detailed)) {
      TraceRecord(trc_level_detailed,
                  TP(TP_CONNECTASWORKMGR_EXIT),
                  "ntv_le_connectAsWorkMgr exit",
                  TRACE_DATA_HEX_INT(
                      unAuthorizedWlmConnectToken,
                      "unAuthorizedWlmConnectToken"),
                  TRACE_DATA_END_PARMS);
    }

    return unAuthorizedWlmConnectToken;
}

/**
 * Disconnect from WLM
 *
 * @parms connectToken is the WLM Connection token returned from connect
 *
 * @return 0 if successful, -1 if not.
 */
JNIEXPORT jint JNICALL
ntv_le_disconnectAsWorkMgr(JNIEnv* env, jclass jobj, jint connectToken) {
    if (TraceActive(trc_level_detailed)) {
        TraceRecord( trc_level_detailed,
                    TP(TP_DISCONNECTASWORKMGR_ENTRY),
                    "ntv_disconnectAsWorkMgr entry",
                    TRACE_DATA_INT(connectToken, "connectToken"),
                    TRACE_DATA_END_PARMS);
    }

    // Call the LE service to disconnect.
    // It is equivalent to a WLM service call IWMDISC.
    // Reference: z/OS MVS Programming: Workload Management Services:
    // Appendix D. C Language Interfaces for Workload Management Services
    int discRC = DisconnectServer((unsigned int*)&connectToken);

    if (discRC == -1) {
        // Gather information of failure into results object
        populateWLMServiceResults(env,
                                  errno,
                                  (__errno2() & 0x0000FFFF),
                                  "DisconnectServer",
                                  connectToken,
                                  "ntv_le_disconnectAsWorkMgr",
                                  NULL);
        return -1;
    }

    if (TraceActive(trc_level_detailed)) {
        TraceRecord( trc_level_detailed,
                    TP(TP_DISCONNECTASWORKMGR_EXIT),
                    "ntv_disconnectAsWorkMgr exit",
                    TRACE_DATA_INT(discRC, "return code"),
                    TRACE_DATA_END_PARMS);
    }

    return discRC;
}

/**
 * Create a WLM work unit
 *
 * @return WLM Enclave token if sucessful, null if not.  Error "results" are contained within the <code>results</code> object
 */
JNIEXPORT jbyteArray JNICALL
ntv_le_createWorkUnit(JNIEnv *env,
                      jclass jobj,
                      jint connectToken,
                      jbyteArray classificationInfo,
                      jstring createFunctionName,
                      jstring classifyCollectionName,
                      jlong arrivalTime
) {
    wlmetok_t nativeToken;
    jbyteArray javaToken;
    server_classify_t clsfy;
    ClassificationInfoImpl nativeData;
    int retcode = -1;
    char localUserId[sizeof(nativeData.user) + 1];
    char localFunctionName[WLM_CREATE_FUNCTION_NAME_MAX + 1];
    char localCollectionName[WLM_COLLECTIONLIMIT + 1];
    char localTransactionClass[WLM_TRANSACTION_CLASS_MAX + 1];
    char localTransactionName[WLM_TRANSACTION_NAME_MAX + 1];

    // ------------------------------------------------------------------
    // Get a copy of the data from the JVM.  We'll either have a fully
    // populated set of data or we'll just have a version number and a
    // transaction class
    // ------------------------------------------------------------------
    (*env)->GetByteArrayRegion(env,
                               classificationInfo,
                               0,
                               (*env)->GetArrayLength(env, classificationInfo),
                               (jbyte*) &nativeData);

    clsfy = __server_classify_create();

    // ------------------------------------------------------------------
    // WebSphere for z/OS classifies work based on the following:
    //     Collection Name (Generic Server Name)
    //     Transaction Class
    //     UserID
    // Considering we only save classification data for our subsystem
    // type, this should be sufficient.
    //
    // When copying the data, we'll work from the end up.
    // ------------------------------------------------------------------
    strcpy(localUserId, "        ");
    for (int i=0; (i < sizeof(nativeData.user)) && nativeData.user[i]; i++) {
        localUserId[i] = nativeData.user[i];
    }
    __server_classify(clsfy, _SERVER_CLASSIFY_USERID, localUserId);


    if (nativeData.transactionClass[0] != 0) {
        memset(localTransactionClass, ' ', WLM_TRANSACTION_CLASS_MAX);
        localTransactionClass[WLM_TRANSACTION_CLASS_MAX] = '\0';
        for (int i=0; (i < WLM_TRANSACTION_CLASS_MAX) && nativeData.transactionClass[i]; i++) {
            localTransactionClass[i] = nativeData.transactionClass[i];
        }
        __server_classify(clsfy, _SERVER_CLASSIFY_TRANSACTION_CLASS, localTransactionClass);
    }

    if (nativeData.transactionName[0] != 0) {
        memset(localTransactionName, ' ', WLM_TRANSACTION_NAME_MAX);
        localTransactionName[WLM_TRANSACTION_NAME_MAX] = '\0';
         for (int i=0; (i < WLM_TRANSACTION_NAME_MAX) && nativeData.transactionName[i]; i++) {
             localTransactionName[i] = nativeData.transactionName[i];
         }
        __server_classify(clsfy, _SERVER_CLASSIFY_TRANSACTION_NAME, localTransactionName);
    }

    // Get Classify CollectionName (blank-padded in Java)
    const char* utfString = (*env)->GetStringUTFChars(env, classifyCollectionName, NULL);
    if (utfString != NULL) {
        strncpy(localCollectionName, utfString, WLM_COLLECTIONLIMIT);
        localCollectionName[WLM_COLLECTIONLIMIT] = '\0';
        __atoe(localCollectionName);
        (*env)->ReleaseStringUTFChars(env, classifyCollectionName, utfString);

        __server_classify(clsfy, _SERVER_CLASSIFY_COLLECTION, localCollectionName);
    } else {
        // OOM Exception pending--just return
        __server_classify_destroy(clsfy); // Release area w/o side-effects
        return NULL;
    }

    // Get Create Function Name
    const char* utfString2 = (*env)->GetStringUTFChars(env, createFunctionName, NULL);
    if (utfString2 != NULL) {
        strncpy(localFunctionName, utfString2, WLM_CREATE_FUNCTION_NAME_MAX);
        localFunctionName[WLM_CREATE_FUNCTION_NAME_MAX] = '\0';
        __atoe(localFunctionName);
        (*env)->ReleaseStringUTFChars(env, createFunctionName, utfString2);
    } else {
        // OOM Exception pending--just return
        __server_classify_destroy(clsfy); // Release area w/o side-effects
        return NULL;
    }

    __server_classify(clsfy, _SERVER_CLASSIFY_CONNTKN, (char*) &connectToken);

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(TP_CREATEWORKUNIT_CALL),
                    "ntv_le_createWorkUnit CreateWorkUnit call",
                    TRACE_DATA_PTR(clsfy, "server_classify_t@"),
                    TRACE_DATA_RAWDATA(sizeof(nativeData),
                                       &nativeData,
                                       "classification data"),
                    TRACE_DATA_HEX_LONG(arrivalTime, "arrivalTime"),
                    TRACE_DATA_RAWDATA(sizeof(localFunctionName),
                                       localFunctionName,
                                       "Create Function name"),
                    TRACE_DATA_RAWDATA(sizeof(localCollectionName),
                                       localCollectionName,
                                       "Collection name"),
                    TRACE_DATA_RAWDATA(sizeof(localUserId),
                                       localUserId,
                                       "User Id"),
                    TRACE_DATA_RAWDATA(sizeof(localTransactionClass),
                                       localTransactionClass,
                                       "Transaction Class"),
                    TRACE_DATA_RAWDATA(sizeof(localTransactionName),
                                       localTransactionName,
                                       "Transaction Name"),
                    TRACE_DATA_END_PARMS);
    }

    retcode = CreateWorkUnit(&nativeToken,
                             clsfy,
                             (char*) &arrivalTime,
                             localFunctionName);

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(TP_CREATEWORKUNIT_RETURN),
                    "ntv_le_createWorkUnit CreateWorkUnit return",
                    TRACE_DATA_INT(retcode, "return code"),
                    TRACE_DATA_RAWDATA(sizeof(nativeToken),
                                       &nativeToken,
                                       "enclave token"),
                    TRACE_DATA_END_PARMS);
    }

    __server_classify_destroy(clsfy); // Release area w/o side-effects

    // ------------------------------------------------------------------
    // Handle failure to create an enclave by returning a null token
    // ------------------------------------------------------------------
    if (retcode == -1) {
        populateWLMServiceResults(env,
                                  errno,
                                  (__errno2() & 0x0000FFFF),
                                  "CreateWorkUnit",
                                  retcode,
                                  "ntv_le_createWorkUnit",
                                  NULL);
        return NULL;
    }

    javaToken = (*env)->NewByteArray(env, sizeof(nativeToken));
    if (javaToken == NULL) {
        // Clear the exception so we can report back
        (*env)->ExceptionDescribe(env);
        (*env)->ExceptionClear(env);
        populateWLMServiceResults(env,
                                  0,
                                  0,
                                  NULL,
                                  ENOMEM,
                                  "ntv_le_createWorkUnit",
                                  NULL);

        return NULL;
    }

    (*env)->SetByteArrayRegion(env,
                               javaToken,
                               0,
                               sizeof(nativeToken),
                               (jbyte*) &nativeToken);

    return javaToken;
}

/**
 * Delete a WLM enclave
 */
JNIEXPORT jint JNICALL
ntv_le_deleteWorkUnit(JNIEnv *env, jclass jobj, jbyteArray etoken) {
    wlmetok_t nativeToken;
    int retcode = -1;
    int rsncode = -1;

    (*env)->GetByteArrayRegion(env, etoken, 0, sizeof(nativeToken), (jbyte*) &nativeToken);

    if (TraceActive(trc_level_detailed)) {
        TraceRecord( trc_level_detailed,
                    TP(TP_DELETEWORKUNIT_CALL),
                    "ntv_le_deleteWorkUnit DeleteWorkUnit call",
                    TRACE_DATA_RAWDATA(sizeof(nativeToken),
                                       &nativeToken,
                                       "enclave token"),
                    TRACE_DATA_END_PARMS);
    }

    retcode = DeleteWorkUnit(&nativeToken);

    if (TraceActive(trc_level_detailed)) {
        TraceRecord( trc_level_detailed,
                    TP(TP_DELETEWORKUNIT_RETURN),
                    "ntv_le_deleteWorkUnit DeleteWorkUnit return",
                    TRACE_DATA_INT(retcode, "return code"),
                    TRACE_DATA_END_PARMS);
    }

    // ------------------------------------------------------------------
    // Handle failure to delete an enclave
    // ------------------------------------------------------------------
    if (retcode == -1) {
        populateWLMServiceResults(env,
                                  errno,
                                  (__errno2() & 0x0000FFFF),
                                  "DeleteWorkUnit",
                                  retcode,
                                  "ntv_le_deleteWorkUnit",
                                  NULL);
        return -1;
    }

    return 0;
}

/**
 * Join a WLM work unit
 */
JNIEXPORT jboolean JNICALL
ntv_le_joinWorkUnit(JNIEnv *env, jclass jobj, jbyteArray etoken) {
    wlmetok_t nativeToken;
    int retcode = -1;
    int rsncode = -1;

    (*env)->GetByteArrayRegion(env, etoken, 0, sizeof(nativeToken), (jbyte*) &nativeToken);

    if (TraceActive(trc_level_detailed)) {
        TraceRecord( trc_level_detailed,
                    TP(TP_JOINWORKUNIT_CALL),
                    "ntv_le_joinWorkUnit JoinWorkUnit call",
                    TRACE_DATA_RAWDATA(sizeof(nativeToken),
                                       &nativeToken,
                                       "enclave token"),
                    TRACE_DATA_END_PARMS);
    }

    retcode = JoinWorkUnit(&nativeToken);

    if (TraceActive(trc_level_detailed)) {
        TraceRecord( trc_level_detailed,
                    TP(TP_JOINWORKUNIT_RETURN),
                    "ntv_le_joinWorkUnit JoinWorkUnit return",
                    TRACE_DATA_INT(retcode, "return code"),
                    TRACE_DATA_END_PARMS);
    }

    // ------------------------------------------------------------------
    // Handle failure to join an enclave
    // ------------------------------------------------------------------
    if (retcode == -1) {
        populateWLMServiceResults(env,
                                  errno,
                                  (__errno2() & 0x0000FFFF),
                                  "JoinWorkUnit",
                                  retcode,
                                  "ntv_le_joinWorkUnit",
                                  NULL);

        return 0;
    }

    return 1;
}

/**
 * Leave a WLM work unit
 */
JNIEXPORT jboolean JNICALL
ntv_le_leaveWorkUnit(JNIEnv *env, jclass jobj, jbyteArray etoken) {
    wlmetok_t nativeToken;
    int retcode = -1;
    int rsncode = -1;

    (*env)->GetByteArrayRegion(env, etoken, 0, sizeof(nativeToken), (jbyte*) &nativeToken);

    if (TraceActive(trc_level_detailed)) {
        TraceRecord( trc_level_detailed,
                    TP(TP_LEAVEWORKUNIT_CALL),
                    "ntv_le_leaveWorkUnit LeaveWorkUnit call",
                    TRACE_DATA_RAWDATA(sizeof(nativeToken),
                                       &nativeToken,
                                       "enclave token"),
                    TRACE_DATA_END_PARMS);
    }

    retcode = LeaveWorkUnit(&nativeToken);

    if (TraceActive(trc_level_detailed)) {
        TraceRecord( trc_level_detailed,
                    TP(TP_LEAVEWORKUNIT_RETURN),
                    "ntv_le_leaveWorkUnit LeaveWorkUnit return",
                    TRACE_DATA_INT(retcode, "return code"),
                    TRACE_DATA_END_PARMS);
    }

    // ------------------------------------------------------------------
    // Handle failure to leave an enclave
    // ------------------------------------------------------------------
    if (retcode == -1) {
        populateWLMServiceResults(env,
                                  errno,
                                  (__errno2() & 0x0000FFFF),
                                  "LeaveWorkUnit",
                                  retcode,
                                  "ntv_le_leaveWorkUnit",
                                  NULL);

        return 0;
    }

    return 1;
}

/**
 * Create a WLM work unit and join it the current thread
 *
 * @return WLM Enclave token if sucessful, null if not.  Error "results" are contained within the <code>results</code> object
 */
JNIEXPORT jbyteArray JNICALL
ntv_le_createJoinWorkUnit(JNIEnv *env,
                      jclass jobj,
                      jint connectToken,
                      jbyteArray transactionClass,
                      jlong arrivalTime) {
    wlmetok_t nativeToken;
    jbyteArray javaToken;
    server_classify_t clsfy;
    int retcode = -1;

    char localTransactionClass[WLM_TRANSACTION_CLASS_MAX + 1];

    if (TraceActive(trc_level_detailed)) {
         TraceRecord(trc_level_detailed,
                     TP(TP_CREATEJOINWORKUNIT_ENTRY),
                     "ntv_le_createJoinWorkUnit entry",
                     TRACE_DATA_END_PARMS);
    }

    // ------------------------------------------------------------------
    // WebSphere for z/OS classifies work based on the following:
    //     Collection Name (Generic Server Name)
    //     Transaction Class
    //
    // ------------------------------------------------------------------

    // Get the Transaction Class (Cached up in java, already blanked padded and EBCDIC)
    (*env)->GetByteArrayRegion(env,
                               transactionClass,
                               0,
                               (*env)->GetArrayLength(env, transactionClass),
                               (jbyte*) localTransactionClass);

    if ((*env)->ExceptionOccurred(env)) {
        // Clear the exception so we can report back
        (*env)->ExceptionDescribe(env);
        (*env)->ExceptionClear(env);
        populateWLMServiceResults(env,
                                  EINVAL,
                                  0,
                                  "ntv_createJoinWorkUnit",
                                  0,
                                  NULL,
                                  NULL);

        return NULL;
    }
    localTransactionClass[WLM_TRANSACTION_CLASS_MAX] = '\0';

    clsfy = __server_classify_create();
    __server_classify(clsfy, _SERVER_CLASSIFY_TRANSACTION_CLASS, localTransactionClass);


    // Classify CollectionName (blank-padded in Java -- cached on native Connect call)
    __server_classify(clsfy, _SERVER_CLASSIFY_COLLECTION, wlmCollectionName);


    __server_classify(clsfy, _SERVER_CLASSIFY_CONNTKN, (char*) &connectToken);

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(TP_CREATEJOIN_CREATEWORKUNIT_CALL),
                    "ntv_le_createJoinWorkUnit CreateJoinWorkUnit call",
                    TRACE_DATA_PTR(clsfy, "server_classify_t@"),
                    TRACE_DATA_HEX_LONG(arrivalTime, "arrivalTime"),
                    TRACE_DATA_RAWDATA(sizeof(wlmFunctionName),
                                       wlmFunctionName,
                                       "Create Function name"),
                    TRACE_DATA_RAWDATA(sizeof(wlmCollectionName),
                                       wlmCollectionName,
                                       "Collection name"),
                    TRACE_DATA_RAWDATA(sizeof(localTransactionClass),
                                       localTransactionClass,
                                       "Transaction Class"),
                    TRACE_DATA_END_PARMS);
    }

    retcode = CreateWorkUnit(&nativeToken,
                             clsfy,
                             (char*) &arrivalTime,
                             wlmFunctionName);

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(TP_CREATEJOIN_CREATEWORKUNIT_RETURN),
                    "ntv_le_createJoinWorkUnit CreateWorkUnit return",
                    TRACE_DATA_INT(retcode, "return code"),
                    TRACE_DATA_RAWDATA(sizeof(nativeToken),
                                       &nativeToken,
                                       "enclave token"),
                    TRACE_DATA_END_PARMS);
    }

    __server_classify_destroy(clsfy); // Release area w/o side-effects

    // ------------------------------------------------------------------
    // Handle failure to create an enclave by returning a null token
    // ------------------------------------------------------------------
    if (retcode == -1) {
        populateWLMServiceResults(env,
                                  errno,
                                  (__errno2() & 0x0000FFFF),
                                  "CreateWorkUnit",
                                  retcode,
                                  "ntv_le_createJoinWorkUnit",
                                  NULL);
        return NULL;
    }

    // ------------------------------------------------------------------
    // Attempt join the enclave
    // ------------------------------------------------------------------
    retcode = JoinWorkUnit(&nativeToken);

    if (TraceActive(trc_level_detailed)) {
        TraceRecord( trc_level_detailed,
                    TP(TP_CREATEJOIN_JOINWORKUNIT_RETURN),
                    "ntv_le_createJoinWorkUnit JoinWorkUnit return",
                    TRACE_DATA_INT(retcode, "return code"),
                    TRACE_DATA_END_PARMS);
    }

    // ------------------------------------------------------------------
    // Handle failure to join an enclave
    // ------------------------------------------------------------------
    if (retcode == -1) {
        populateWLMServiceResults(env,
                                  errno,
                                  (__errno2() & 0x0000FFFF),
                                  "JoinWorkUnit",
                                  retcode,
                                  "ntv_le_createJoinWorkUnit",
                                  NULL);

        return NULL;
    }

    javaToken = (*env)->NewByteArray(env, sizeof(nativeToken));
    if (javaToken == NULL) {
        // Clear the exception so we can report back
        (*env)->ExceptionDescribe(env);
        (*env)->ExceptionClear(env);
        populateWLMServiceResults(env,
                                  0,
                                  0,
                                  NULL,
                                  ENOMEM,
                                  "ntv_le_createJoinWorkUnit",
                                  NULL);

        return NULL;
    }

    (*env)->SetByteArrayRegion(env,
                               javaToken,
                               0,
                               sizeof(nativeToken),
                               (jbyte*) &nativeToken);

    return javaToken;
}   // end, ntv_le_createJoinWorkUnit

/**
 * Leave a WLM work unit and then delete it
 */
JNIEXPORT jint JNICALL
ntv_le_leaveDeleteWorkUnit(JNIEnv *env, jclass jobj, jbyteArray etoken) {
    wlmetok_t nativeToken;
    int retcode = -1;

    (*env)->GetByteArrayRegion(env, etoken, 0, sizeof(nativeToken), (jbyte*) &nativeToken);

    if (TraceActive(trc_level_detailed)) {
        TraceRecord( trc_level_detailed,
                    TP(TP_LEAVEDELETE_LEAVEWORKUNIT_CALL),
                    "ntv_le_leaveDeleteWorkUnit LeaveWorkUnit call",
                    TRACE_DATA_RAWDATA(sizeof(nativeToken),
                                       &nativeToken,
                                       "enclave token"),
                    TRACE_DATA_END_PARMS);
    }

    retcode = LeaveWorkUnit(&nativeToken);

    if (TraceActive(trc_level_detailed)) {
        TraceRecord( trc_level_detailed,
                    TP(TP_LEAVEDELETE_LEAVEWORKUNIT_RETURN),
                    "ntv_le_leaveDeleteWorkUnit LeaveWorkUnit return",
                    TRACE_DATA_INT(retcode, "return code"),
                    TRACE_DATA_END_PARMS);
    }

    // ------------------------------------------------------------------
    // Handle failure to leave an enclave
    // ------------------------------------------------------------------
    if (retcode == -1) {
        populateWLMServiceResults(env,
                                  errno,
                                  (__errno2() & 0x0000FFFF),
                                  "LeaveWorkUnit",
                                  retcode,
                                  "ntv_le_leaveDeleteWorkUnit",
                                  NULL);

        return -1;
    }

    // ------------------------------------------------------------------
    // Delete an enclave
    // ------------------------------------------------------------------
    retcode = DeleteWorkUnit(&nativeToken);

    if (TraceActive(trc_level_detailed)) {
        TraceRecord( trc_level_detailed,
                    TP(TP_LEAVEDELETE_DELETEWORKUNIT_RETURN),
                    "ntv_le_deleteWorkUnit DeleteWorkUnit return",
                    TRACE_DATA_INT(retcode, "return code"),
                    TRACE_DATA_END_PARMS);
    }

    // ------------------------------------------------------------------
    // Handle failure to delete an enclave
    // ------------------------------------------------------------------
    if (retcode == -1) {
        populateWLMServiceResults(env,
                                  errno,
                                  (__errno2() & 0x0000FFFF),
                                  "DeleteWorkUnit",
                                  retcode,
                                  "ntv_le_leaveDeleteWorkUnit",
                                  NULL);
        return -1;
    }

    return 0;
}   // end, ntv_le_leaveDeleteWorkUnit


// --------------------------------------------------------------------------
//
// Populate this thread's result from the WLM Service call
//
// --------------------------------------------------------------------------
void populateWLMServiceResults(JNIEnv* env,
                               int wlmReturnCode,
                               int wlmReasonCode,
                               char* wlmRoutine,
                               int wasReturnCode,
                               char* wasRoutine,
                               jbyteArray returnData) {

    if (TraceActive(trc_level_detailed)) {
        TraceRecord( trc_level_detailed,
                    TP(TP_POPULATEWLMSERVICERESULTS_ENTRY),
                    "populateWLMServiceResults, entry",
                    TRACE_DATA_PTR(env, "JNIEnv"),
                    TRACE_DATA_HEX_INT(wlmReturnCode, "WLM return code"),
                    TRACE_DATA_HEX_INT(wlmReasonCode, "WLM reason code"),
                    TRACE_DATA_STRING(wlmRoutine, "WLM routine"),
                    TRACE_DATA_HEX_INT(wasReturnCode, "WAS return code"),
                    TRACE_DATA_STRING(wasRoutine, "WAS routine"),
                    TRACE_DATA_END_PARMS);
    }

    char serviceName[MAX_SERVICENAME_LENGTH + 1];
    jstring jwlmRoutine = NULL;
    jstring jwasRoutine = NULL;

    if (wlmRoutine != NULL) {
        strncpy(serviceName, wlmRoutine, MAX_SERVICENAME_LENGTH);
        serviceName[MAX_SERVICENAME_LENGTH] = '\0';

        __etoa(serviceName);
        jwlmRoutine = (*env)->NewStringUTF(env, serviceName);
    }

    if (wasRoutine != NULL) {
        strncpy(serviceName, wasRoutine, MAX_SERVICENAME_LENGTH);
        serviceName[MAX_SERVICENAME_LENGTH] = '\0';
        __etoa(serviceName);
        jwasRoutine = (*env)->NewStringUTF(env, serviceName);
    }

    (*env)->CallStaticVoidMethod(env,
                                 wlmServiceResultsClass,
                                 setResultsMethodID,
                                 wlmReturnCode,
                                 wlmReasonCode,
                                 jwlmRoutine,
                                 wasReturnCode,
                                 jwasRoutine,
                                 returnData);

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(TP_POPULATEWLMSERVICERESULTS_PUSHRESULTS),
                    "populateWLMServiceResults, pushed results data up",
                    TRACE_DATA_END_PARMS);
    }

}



// --------------------------------------------------------------------
// Authorized JNI function Support
// --------------------------------------------------------------------
unsigned int getAuthConnectToken(JNIEnv* env);

// --------------------------------------------------------------------
// Authorized JNI function declaration and export
// --------------------------------------------------------------------
#pragma export(ntv_connectAsWorkMgr)
JNIEXPORT jint JNICALL
ntv_connectAsWorkMgr(JNIEnv* env, jobject jobj,
                     jstring subSystem,
                     jstring subSystemName,
                     jstring createFunctionName,
                     jstring classifyCollectionName);

#pragma export(ntv_disconnectAsWorkMgr)
JNIEXPORT jint JNICALL
ntv_disconnectAsWorkMgr(JNIEnv* env, jobject jobj, jint connectToken);

#pragma export(ntv_joinWorkUnit)
JNIEXPORT jboolean JNICALL
ntv_joinWorkUnit(JNIEnv* env, jobject jobj, jbyteArray token);

#pragma export(ntv_leaveWorkUnit)
JNIEXPORT jboolean JNICALL
ntv_leaveWorkUnit(JNIEnv* env, jobject jobj, jbyteArray token);

#pragma export(ntv_leaveDeleteWorkUnit)
JNIEXPORT jint JNICALL
ntv_leaveDeleteWorkUnit(JNIEnv* env, jobject jobj, jbyteArray token, jbyteArray outputDeleteData);

#pragma export(ntv_createWorkUnit)
JNIEXPORT jbyteArray JNICALL
ntv_createWorkUnit(JNIEnv* env,
                   jobject jobj,
                   jint connectToken,
                   jbyteArray classificationInfo,
                   jstring createFunctionName,
                   jstring classifyCollectionName,
                   jlong arrivalTime,
                   jint serviceClassToken,
                   jbyteArray outputServiceClassToken);

#pragma export(ntv_createJoinWorkUnit)
JNIEXPORT jbyteArray JNICALL
ntv_createJoinWorkUnit(JNIEnv* env,
                   jobject jobj,
                   jint connectToken,
                   jbyteArray transactionClass,
                   jlong arrivalTime,
                   jint serviceClassToken,
                   jbyteArray outputServiceClassToken);

#pragma export(ntv_deleteWorkUnit)
JNIEXPORT int JNICALL
ntv_deleteWorkUnit(JNIEnv* env, jobject jobj, jbyteArray etoken, jbyteArray outputDeleteData);

// --------------------------------------------------------------------
// JNI native method structure for the AuthorizedWLMNativeServices
// methods
// --------------------------------------------------------------------
#pragma convert("ISO8859-1")
static const JNINativeMethod AuthorizedWLMNativeServicesMethods[] = {
    { "ntv_connectAsWorkMgr",
      "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)I",
      (void *) ntv_connectAsWorkMgr },
    { "ntv_disconnectAsWorkMgr",
      "(I)I",
      (void *) ntv_disconnectAsWorkMgr },
    { "ntv_joinWorkUnit",
      "([B)Z",
      (void *) ntv_joinWorkUnit },
    { "ntv_leaveWorkUnit",
      "([B)Z",
      (void *) ntv_leaveWorkUnit },
    { "ntv_leaveDeleteWorkUnit",
      "([B[B)I",
      (void *) ntv_leaveDeleteWorkUnit },
    { "ntv_createWorkUnit",
      "(I[BLjava/lang/String;Ljava/lang/String;JI[B)[B",
      (void *) ntv_createWorkUnit },
    { "ntv_createJoinWorkUnit",
      "(I[BJI[B)[B",
      (void *) ntv_createJoinWorkUnit },
    { "ntv_deleteWorkUnit",
      "([B[B)I",
      (void *) ntv_deleteWorkUnit }
};
#pragma convert(pop)

// --------------------------------------------------------------------
// Callback methods for the Authorized WLM services
// --------------------------------------------------------------------
int
authRegFunction(JNIEnv* env, jclass clazz, jobjectArray extraInfo);

int
authDeRegFunction(JNIEnv* env, jclass clazz, jobjectArray extraInfo);

// --------------------------------------------------------------------
// NativeMethodDescriptor for the AuthorizedWLMNativeServices
// --------------------------------------------------------------------
#pragma export(zJNI_com_ibm_ws_zos_wlm_internal_AuthorizedWLMNativeServices)
NativeMethodDescriptor zJNI_com_ibm_ws_zos_wlm_internal_AuthorizedWLMNativeServices = {
    .registrationFunction = authRegFunction,
    .deregistrationFunction = authDeRegFunction,
    .nativeMethodCount = sizeof(AuthorizedWLMNativeServicesMethods) / sizeof(AuthorizedWLMNativeServicesMethods[0]),
    .nativeMethods = AuthorizedWLMNativeServicesMethods
};



/**
 * Respond to the registerNatives call performed out of bundle activation and
 * setup the required native infrastructure.  This method will resolve and cache
 * the method ID's for the AuthorizedWLMNativeServices function, and prepare to manage
 * thread-specific references to JNI environments.
 *
 * @param env the JNI environment for the calling thread.
 * @param clazz the class that native registration is occurring for.
 * @param extraInfo context information from the caller.  For the native trace
 *        handler, the first element is the handler instance and the second
 *        element is the name of the trace callback method.
 *
 * @return JNI_OK on success; JNI_ERR or on error.
 */
int authRegFunction(JNIEnv* env, jclass clazz, jobjectArray extraInfo) {
    int rc = JNI_OK;

    // --------------------------------------------------------------------
    // Setup the WLMServiceResults class and methods we need to invoke
    // --------------------------------------------------------------------

    // Get the class/method for "setResults" of the callback function from extra info
    jclass  callbackClass = (jclass) (*env)->GetObjectArrayElement(env, extraInfo, 0);
    jstring callbackName = (jstring) (*env)->GetObjectArrayElement(env, extraInfo, 1);
    const char* utfCallbackName = (*env)->GetStringUTFChars(env, callbackName, NULL);
#pragma convert("iso8859-1")
    const char* signature = "(IILjava/lang/String;ILjava/lang/String;[B)V";
#pragma convert(pop)

    wlmServiceResultsClass = callbackClass;
    if ((*env)->ExceptionCheck(env)) {
        return JNI_ERR;
    }

    wlmServiceResultsClass = (jclass) (*env)->NewGlobalRef(env, wlmServiceResultsClass);
    if ((*env)->ExceptionCheck(env)) {
        return JNI_ERR;
    }

    // Get the callback method ID for "setResult"
    setResultsMethodID = (*env)->GetStaticMethodID(env, callbackClass, utfCallbackName, signature);
    (*env)->ReleaseStringUTFChars(env, callbackName, utfCallbackName);
    if ((*env)->ExceptionCheck(env)) {
        return JNI_ERR;
    }

    return rc;
}


/**
 * Respond to the host bundle stop by performing any necessary cleanup.
 *
 * @param env the calling thread's JNI environment.
 * @param clazz the class that deregistration is occurring for.
 * @param extraInfo context provided to the registration function.
 *
 * @return JNI_OK.
 */
int
authDeRegFunction(JNIEnv* env, jclass clazz, jobjectArray extraInfo) {

    if (wlmServiceResultsClass != NULL) {
        (*env)->DeleteGlobalRef(env, wlmServiceResultsClass);
        wlmServiceResultsClass = NULL;
    }

    setResultsMethodID = NULL;

    return JNI_OK;
}

/**
 * Connect to WLM as work manager
 */
JNIEXPORT jint JNICALL
ntv_connectAsWorkMgr(JNIEnv* env, jobject jobj, jstring jsubSys, jstring jsubSysName,
                     jstring createFunctionName, jstring classifyCollectionName) {

    // Convert subSys and subSysName to EBCDIC
    char* subSysName = NULL;
    char* subSys = NULL;
    const char* utfString = (*env)->GetStringUTFChars(env, jsubSys, NULL);
    if (utfString != NULL) {
        subSys = alloca(strlen(utfString) + 1);
        strcpy(subSys, utfString);
        __atoe(subSys);
        (*env)->ReleaseStringUTFChars(env, jsubSys, utfString);
    } else {
        // OOM Exception pending--just return
        return -1;
    }

    const char* utfString2 = (*env)->GetStringUTFChars(env, jsubSysName, NULL);
    if (utfString2 != NULL) {
        subSysName = alloca(strlen(utfString2) + 1);
        strcpy(subSysName, utfString2);
        __atoe(subSysName);
        (*env)->ReleaseStringUTFChars(env, jsubSysName, utfString2);
    } else {
        // OOM Exception pending--just return
        return -1;
    }

    // Get Classify CollectionName save in global for later use in create
    // (Blank-padded in Java)
    const char* utfString3 = (*env)->GetStringUTFChars(env, classifyCollectionName, NULL);
    if (utfString3 != NULL) {
        strncpy(wlmCollectionName, utfString3, WLM_COLLECTIONLIMIT);
        wlmCollectionName[WLM_COLLECTIONLIMIT] = '\0';
        __atoe(wlmCollectionName);
        (*env)->ReleaseStringUTFChars(env, classifyCollectionName, utfString3);
    } else {
        // OOM Exception pending--just return
        return -1;
    }

    // Get Create Function Name save in global for later use in create
    utfString = (*env)->GetStringUTFChars(env, createFunctionName, NULL);
    if (utfString != NULL) {
        strncpy(wlmFunctionName, utfString, WLM_CREATE_FUNCTION_NAME_MAX);
        wlmFunctionName[WLM_CREATE_FUNCTION_NAME_MAX] = '\0';
        __atoe(wlmFunctionName);
        (*env)->ReleaseStringUTFChars(env, createFunctionName, utfString);

        // Uppercase it...
        for (int i = 0; wlmFunctionName[i]; i++) {
            wlmFunctionName[i] = toupper(wlmFunctionName[i]);
        }
    } else {
        // OOM Exception pending--just return
        return -1;
    }

    if (TraceActive(trc_level_detailed)) {
        TraceRecord( trc_level_detailed,
                    TP(TP_A_CONNECTASWORKMGR_ENTRY),
                    "ntv_connectAsWorkMgr entry",
                    TRACE_DATA_STRING(subSys, "Subsystem"),
                    TRACE_DATA_STRING(subSysName, "Subsystem name"),
                    TRACE_DATA_RAWDATA(sizeof(wlmFunctionName),
                                       wlmFunctionName,
                                       "Create Function name"),
                    TRACE_DATA_RAWDATA(sizeof(wlmCollectionName),
                                       wlmCollectionName,
                                       "Collection name"),
                    TRACE_DATA_END_PARMS);
    }


    // Get the authorized server PC stub to be able to call authorized services.
    const server_authorized_function_stubs* auth_stubs_p = getServerAuthorizedFunctionStubs();

    int pc_rc = -1;
    unsigned int localWLMConnToken = 0;
    int localWLMConnRC = 0;
    int localWLMConnRSN = 0;
    WLM_ConnectParms wlmConnParms = {
        .inSubSys = subSys,
        .inSubSysName = subSysName,
        .outWLMConnectToken = &localWLMConnToken,
        .outRC = &localWLMConnRC,
        .outRSN = &localWLMConnRSN
    };

    // Build the parameter list for the BPX4IPT wrapper service.
    DriveAuthorizedServiceOnIPTParms_t iptParms;
    iptParms.pcReturnCode = -1;
    iptParms.parmStructSize = sizeof(wlmConnParms);
    iptParms.authRoutine_p = (void*)&(auth_stubs_p->wlm_connect);
    iptParms.parmStruct_p = &wlmConnParms;

    // Drive the IPT wrapper service.
    int bpx_rc = -1, bpx_rsn = -1, bpx_rv = -1;
    int wrapper_rc = driveAuthorizedServiceOnIPT(&iptParms, &bpx_rv, &bpx_rc, &bpx_rsn);

    if (wrapper_rc != 0) {
        if (TraceActive(trc_level_exception)) {
            TraceRecord(trc_level_exception,
                        TP(TP_A_CONNECTASWORKMGR_IPTEXIT_SKIPPED),
                        "ntv_connectAsWorkMgr error finding unauthorized function table",
                        TRACE_DATA_INT(wrapper_rc, "RC"),
                        TRACE_DATA_END_PARMS);
        }

        // Gather information of failure into results object
        populateWLMServiceResults(env,
                                  0,
                                  0,
                                  "IWM4CON",
                                  WASRETURNCODE_FAILED_TO_FIND_UNAUTH_FUNCTION_STUBS,
                                  "ntv_connectAsWorkMgr",
                                  NULL);
        return -1;
    }

    if (bpx_rv != 0) {
        if (TraceActive(trc_level_exception)) {
            TraceRecord(trc_level_exception,
                        TP(TP_A_CONNECTASWORKMGR_IPT_BAD_BPX_RC),
                        "ntv_connectAsWorkMgr error calling BPX4IPT",
                        TRACE_DATA_INT(bpx_rv, "RV"),
                        TRACE_DATA_INT(bpx_rc, "RC (dec)"),
                        TRACE_DATA_HEX_INT(bpx_rsn, "RSN (hex)"),
                        TRACE_DATA_END_PARMS);
        }

        // Gather information of failure into results object
        populateWLMServiceResults(env,
                                  0,
                                  0,
                                  "IWM4CON",
                                  WASRETURNCODE_FAILED_CALLING_BPX4IPT,
                                  "ntv_connectAsWorkMgr",
                                  NULL);
        return -1;
    }

    if ((iptParms.pcReturnCode != 0) || (localWLMConnRC != 0)) {
        // Gather information of failure into results object
        populateWLMServiceResults(env,
                                  localWLMConnRC,
                                  (localWLMConnRSN & 0x0000FFFF),
                                  "IWM4CON",
                                  -1,
                                  "ntv_connectAsWorkMgr",
                                  NULL);

        return -1;
    }

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(TP_A_CONNECTASWORKMGR_EXIT),
                    "ntv_connectAsWorkMgr exit",
                    TRACE_DATA_HEX_INT(localWLMConnToken, "localWLMConnToken"),
                    TRACE_DATA_END_PARMS);
    }

    return localWLMConnToken;
}

/**
 * Disconnect from WLM
 *
 * @parms connectToken is the WLM Connection token returned from connect
 *
 * @return 0 if successful, -1 if not.
 */
JNIEXPORT jint JNICALL
ntv_disconnectAsWorkMgr(JNIEnv* env, jobject jobj, jint connectToken) {
    int retcode = -1;
    int rsncode = -1;
    int pc_rc = -1;

    if (TraceActive(trc_level_detailed)) {
        TraceRecord( trc_level_detailed,
                    TP(TP_A_DISCONNECTASWORKMGR_ENTRY),
                    "ntv_disconnectAsWorkMgr entry",
                    TRACE_DATA_INT(connectToken, "connectToken"),
                    TRACE_DATA_END_PARMS);
    }

    WLM_DisconnectParms disconnectParms = {
        .inWLMConnectToken = connectToken,
        .outRC = &retcode,
        .outRSN = &rsncode
    };

    if (TraceActive(trc_level_detailed)) {
        TraceRecord( trc_level_detailed,
                    TP(TP_A_DISCONNECTASWORKMGR_PRECALL),
                    "ntv_disconnectAsWorkMgr wlm_disconnect call",
                    TRACE_DATA_RAWDATA(sizeof(disconnectParms),
                                       &disconnectParms,
                                       "WLM_DisconnectParms"),
                    TRACE_DATA_END_PARMS);
    }

    // Get the authorized server PC stub to be able to call authorized services.
    const server_authorized_function_stubs* auth_stubs_p = getServerAuthorizedFunctionStubs();

    pc_rc = auth_stubs_p->wlm_disconnect(&disconnectParms);

    if (TraceActive(trc_level_detailed)) {
        TraceRecord( trc_level_detailed,
                    TP(TP_A_DISCONNECTASWORKMGR_RETURN),
                    "ntv_disconnectAsWorkMgr wlm_disconnect return",
                    TRACE_DATA_INT(pc_rc, "pc_rc"),
                    TRACE_DATA_HEX_INT(retcode, "retcode"),
                    TRACE_DATA_HEX_INT(rsncode, "rsncode"),
                    TRACE_DATA_END_PARMS);
    }

    if (pc_rc != 0) {
        if (TraceActive(trc_level_exception)) {
            TraceRecord(trc_level_exception,
                        TP(TP_A_DISCONNECTASWORKMGR_BAD_PC_RC),
                        "ntv_disconnectAsWorkMgr Bad PC RC",
                        TRACE_DATA_INT(pc_rc, "Authorized PC Return Code"),
                        TRACE_DATA_END_PARMS);
        }

        populateWLMServiceResults(env, 0, 0, "IWMDISC", pc_rc, "ntv_disconnectAsWorkMgr", NULL);

        return -1;
    }

    // ------------------------------------------------------------------
    // Handle failure to disconnect from WLM
    // ------------------------------------------------------------------
    if (retcode != 0) {
        populateWLMServiceResults(env,
                                  retcode,
                                  (rsncode & 0x0000FFFF),
                                  "IWMDISC",
                                  0,
                                  "ntv_disconnectAsWorkMgr",
                                  NULL);

        return -1;
    }

    if (TraceActive(trc_level_detailed)) {
        TraceRecord( trc_level_detailed,
                    TP(TP_A_DISCONNECTASWORKMGR_EXIT),
                    "ntv_disconnectAsWorkMgr exit",
                    TRACE_DATA_INT(retcode, "return code"),
                    TRACE_DATA_INT(rsncode, "reason code"),
                    TRACE_DATA_END_PARMS);
    }

    return 0;
}


/**
 * Create a WLM work unit
 *
 * @return jbyteArray if sucessful, NULL if not.  On NULL return the "results"
 * are contained within the <code>WLMServiceResults</code> object
 */
JNIEXPORT jbyteArray JNICALL
ntv_createWorkUnit(JNIEnv* env,
                   jobject jobj,
                   jint connectToken,
                   jbyteArray classificationInfo,
                   jstring createFunctionName,
                   jstring classifyCollectionName,
                   jlong arrivalTime,
                   jint serviceClassToken,
                   jbyteArray outputServiceClassToken) {
    jbyteArray javaToken;
    ClassificationInfoImpl nativeData;
    int pc_rc = -1;

    char localFunctionName[WLM_CREATE_FUNCTION_NAME_MAX + 1] = {{0}};
    char localCollectionName[WLM_COLLECTIONLIMIT + 1] = {{0}};
    char localTransactionClass[WLM_TRANSACTION_CLASS_MAX + 1] = {{0}};
    char localTransactionName[WLM_TRANSACTION_NAME_MAX + 1] = {{0}};

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(TP_A_CREATEWORKUNIT_ENTRY),
                    "ntv_createWorkUnit entry",
                    TRACE_DATA_END_PARMS);
    }

    // ------------------------------------------------------------------
    // Get a copy of the data from the JVM.  We'll either have a fully
    // populated set of data or we'll just have a version number and a
    // transaction class, ...
    // ------------------------------------------------------------------
    (*env)->GetByteArrayRegion(env,
                               classificationInfo,
                               0,
                               (*env)->GetArrayLength(env, classificationInfo),
                               (jbyte*) &nativeData);

    // ------------------------------------------------------------------
    // WebSphere for z/OS classifies work based on the following:
    //     Collection Name (Generic Server Name)
    //     Transaction Class
    //     UserID
    // Considering we only save classification data for our subsystem
    // type, this should be sufficient.
    //
    // When copying the data, we'll work from the end up.
    // ------------------------------------------------------------------

    // Get Classify CollectionName (Blank-padded in Java)
    const char* utfString = (*env)->GetStringUTFChars(env, classifyCollectionName, NULL);
    if (utfString != NULL) {
        strncpy(localCollectionName, utfString, WLM_COLLECTIONLIMIT);
        localCollectionName[WLM_COLLECTIONLIMIT] = '\0';
        __atoe(localCollectionName);
        (*env)->ReleaseStringUTFChars(env, classifyCollectionName, utfString);
    } else {
        // OOM Exception pending--just return
        return NULL;
    }

    // Get Create Function Name
    const char* utfString2 = (*env)->GetStringUTFChars(env, createFunctionName, NULL);
    if (utfString2 != NULL) {
        strncpy(localFunctionName, utfString2, WLM_CREATE_FUNCTION_NAME_MAX);
        localFunctionName[WLM_CREATE_FUNCTION_NAME_MAX] = '\0';
        __atoe(localFunctionName);
        (*env)->ReleaseStringUTFChars(env, createFunctionName, utfString2);
    } else {
        // OOM Exception pending--just return
        return NULL;
    }

    // Transaction Class (already in EBCDIC within ClassificationInfo area)
    strcpy(localTransactionClass, "        ");
    if (nativeData.transactionClass[0] != 0) {
        for (int i=0; (i < WLM_TRANSACTION_CLASS_MAX) && nativeData.transactionClass[i]; i++) {
            localTransactionClass[i] = nativeData.transactionClass[i];
        }
    }

    // Transaction Name (used by OLA in tWAS)
    strcpy(localTransactionName, "        ");
    if (nativeData.transactionName[0] != 0) {
        for (int i=0; (i < WLM_TRANSACTION_NAME_MAX) && nativeData.transactionName[i]; i++) {
            localTransactionName[i] = nativeData.transactionName[i];
        }
    }
    
    int localWLMCreateRC, localWLMCreateRSN, localOutputServiceClassToken;

    RegistryToken localEncRegToken = {{0}};

    WLM_EnclaveCreateParms encCreateParms = {
        .inWLMConnectToken = connectToken,
        .inFunctionName = localFunctionName,
        .inCollectionName = localCollectionName,
        .inCollectionNameLen = strlen(localCollectionName),
        .inTransactionClass = localTransactionClass,
        .inTransactionName = localTransactionName,
        .inStartTime = (unsigned long long) arrivalTime,
        .inServiceClassToken = serviceClassToken,
        .outEnclaveToken = &localEncRegToken,
        .outServiceClassToken = &localOutputServiceClassToken,
        .outRC = &localWLMCreateRC,
        .outRSN = &localWLMCreateRSN
    };

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(TP_A_CREATEWORKUNIT_CALL),
                    "ntv_createWorkUnit wlm_enclave_create call",
                    TRACE_DATA_HEX_LONG(arrivalTime, "arrival time"),
                    TRACE_DATA_RAWDATA(sizeof(nativeData),
                                       &nativeData,
                                       "classification data"),
                    TRACE_DATA_RAWDATA(sizeof(localFunctionName),
                                       localFunctionName,
                                       "Create Function name"),
                    TRACE_DATA_RAWDATA(sizeof(localCollectionName),
                                       localCollectionName,
                                       "Collection name"),
                    TRACE_DATA_RAWDATA(sizeof(localTransactionClass),
                                       localTransactionClass,
                                       "Transaction Class"),
                    TRACE_DATA_RAWDATA(sizeof(localTransactionName),
                                       localTransactionName,
                                       "Transaction Name"),
                    TRACE_DATA_RAWDATA(sizeof(encCreateParms),
                                       &encCreateParms,
                                       "WLM_EnclaveCreateParms"),
                    TRACE_DATA_HEX_INT(serviceClassToken, "Service Class Token"),
                    TRACE_DATA_END_PARMS);
    }

    // Get the authorized server PC stub to be able to call authorized services.
    const server_authorized_function_stubs* auth_stubs_p = getServerAuthorizedFunctionStubs();

    pc_rc = auth_stubs_p->wlm_enclave_create(&encCreateParms);

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(TP_A_CREATEWORKUNIT_RETURN),
                    "ntv_createWorkUnit wlm_enclave_create return",
                    TRACE_DATA_INT(pc_rc, "pc_rc"),
                    TRACE_DATA_HEX_INT(localOutputServiceClassToken, "localOutputServiceClassToken"),
                    TRACE_DATA_HEX_INT(localWLMCreateRC, "localWLMCreateRC"),
                    TRACE_DATA_HEX_INT(localWLMCreateRSN, "localWLMCreateRSN"),
                    TRACE_DATA_RAWDATA(sizeof(localEncRegToken),
                                       &localEncRegToken,
                                      "localEncRegToken"),
                    TRACE_DATA_END_PARMS);
    }

    if (pc_rc != 0) {
        if (TraceActive(trc_level_exception)) {
            TraceRecord(trc_level_exception,
                        TP(TP_A_CREATEWORKUNIT_BAD_PC_RC),
                        "ntv_createWorkUnit Bad PC RC",
                        TRACE_DATA_INT(pc_rc, "Authorized PC Return Code"),
                        TRACE_DATA_END_PARMS);
        }

        populateWLMServiceResults(env,
                                  0,
                                  0,
                                  "IWM4ECRE",
                                  pc_rc,
                                  "ntv_createWorkUnit",
                                  NULL);

        return NULL;
    }

    // ------------------------------------------------------------------
    // Handle failure to create an enclave by returning a null token
    // ------------------------------------------------------------------
    if (localWLMCreateRC != 0) {
        if (localWLMCreateRSN == WASRETURNCODE_FAILED_TO_REGISTER) {
            populateWLMServiceResults(env,
                                      0,
                                      0,
                                      "UTIL_REGISTRY",
                                      WASRETURNCODE_FAILED_TO_REGISTER,
                                      "ntv_createWorkUnit",
                                      NULL);
        } else {
            populateWLMServiceResults(env,
                                      localWLMCreateRC,
                                      (localWLMCreateRSN & 0x0000FFFF),
                                      "IWM4ECRE",
                                      0,
                                      "ntv_createWorkUnit",
                                      NULL);
        }

        return NULL;
    }

    // set output service class token
    (*env)->SetByteArrayRegion(env, outputServiceClassToken, 0, sizeof(int), (jbyte*)&localOutputServiceClassToken);

    javaToken = (*env)->NewByteArray(env, sizeof(localEncRegToken));
    if (javaToken == NULL) {
        // Clear the exception so we can report back
        (*env)->ExceptionDescribe(env);
        (*env)->ExceptionClear(env);
        populateWLMServiceResults(env,
                                  0,
                                  0,
                                  NULL,
                                  ENOMEM,
                                  "ntv_createWorkUnit",
                                  NULL);

        return NULL;
    }

    (*env)->SetByteArrayRegion(env,
                               javaToken,
                               0,
                               sizeof(localEncRegToken),
                               (jbyte*) &localEncRegToken);


    return javaToken;
}

/**
 * Create a WLM work unit and Join it to the current thread
 *
 * @return jbyteArray if sucessful, NULL if not.  On NULL return the "results"
 * are contained within the <code>WLMServiceResults</code> object
 */
JNIEXPORT jbyteArray JNICALL
ntv_createJoinWorkUnit(JNIEnv* env,
                   jobject jobj,
                   jint connectToken,
                   jbyteArray transactionClass,
                   jlong arrivalTime,
                   jint serviceClassToken,
                   jbyteArray outputServiceClassToken) {
    jbyteArray javaToken;
    int pc_rc = -1;

    char localTransactionClass[WLM_TRANSACTION_CLASS_MAX + 1] = {{0}};

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(TP_A_CREATEJOINWORKUNIT_ENTRY),
                    "ntv_createJoinWorkUnit entry",
                    TRACE_DATA_END_PARMS);
    }

    // ------------------------------------------------------------------
    // WebSphere for z/OS classifies work based on the following:
    //     Collection Name (Generic Server Name)
    //     Transaction Class
    //     UserID
    // Considering we only save classification data for our subsystem
    // type, this should be sufficient.
    //
    // When copying the data, we'll work from the end up.
    // ------------------------------------------------------------------

    // Get the Transaction Class (Cached up in java, already blanked padded and EBCDIC)
    (*env)->GetByteArrayRegion(env,
                               transactionClass,
                               0,
                               (*env)->GetArrayLength(env, transactionClass),
                               (jbyte*) localTransactionClass);

    if ((*env)->ExceptionOccurred(env)) {
        // Clear the exception so we can report back
        (*env)->ExceptionDescribe(env);
        (*env)->ExceptionClear(env);
        populateWLMServiceResults(env,
                                  EINVAL,
                                  0,
                                  "ntv_createJoinWorkUnit",
                                  0,
                                  NULL,
                                  NULL);

        return NULL;
    }


    int localWLMCreateRC, localWLMCreateRSN, localOutputServiceClassToken;
    int localWLMJoinRC, localWLMJoinRSN;

    RegistryToken localEncRegToken = {{0}};

    WLM_EnclaveCreateJoinParms encCreateJoinParms = {
        .inWLMConnectToken = connectToken,
        .inFunctionName = wlmFunctionName,
        .inCollectionName = wlmCollectionName,
        .inCollectionNameLen = strlen(wlmCollectionName),
        .inTransactionClass = localTransactionClass,
        .inStartTime = (unsigned long long) arrivalTime,
        .inServiceClassToken = serviceClassToken,
        .outEnclaveToken = &localEncRegToken,
        .outServiceClassToken = &localOutputServiceClassToken,
        .outCreateRC = &localWLMCreateRC,
        .outCreateRSN = &localWLMCreateRSN,
        .outJoinRC = &localWLMJoinRC,
        .outJoinRSN = &localWLMJoinRSN
    };

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(TP_A_CREATEJOINWORKUNIT_CALL),
                    "ntv_createJoinWorkUnit wlm_enclave_create_join call",
                    TRACE_DATA_HEX_LONG(arrivalTime, "arrival time"),
                    TRACE_DATA_RAWDATA(sizeof(wlmFunctionName),
                                       wlmFunctionName,
                                       "Create Function name"),
                    TRACE_DATA_RAWDATA(sizeof(wlmCollectionName),
                                       wlmCollectionName,
                                       "Collection name"),
                    TRACE_DATA_RAWDATA(sizeof(localTransactionClass),
                                       localTransactionClass,
                                       "Transaction Class"),
                    TRACE_DATA_RAWDATA(sizeof(encCreateJoinParms),
                                       &encCreateJoinParms,
                                       "WLM_EnclaveCreateJoinParms"),
                    TRACE_DATA_HEX_INT(serviceClassToken, "Service Class Token"),
                    TRACE_DATA_END_PARMS);
    }

    // Get the authorized server PC stub to be able to call authorized services.
    const server_authorized_function_stubs* auth_stubs_p = getServerAuthorizedFunctionStubs();

    pc_rc = auth_stubs_p->wlm_enclave_create_join(&encCreateJoinParms);

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(TP_A_CREATEJOINWORKUNIT_RETURN),
                    "ntv_createJoinWorkUnit wlm_enclave_create_join return",
                    TRACE_DATA_INT(pc_rc, "pc_rc"),
                    TRACE_DATA_HEX_INT(localOutputServiceClassToken, "localOutputServiceClassToken"),
                    TRACE_DATA_HEX_INT(localWLMCreateRC, "localWLMCreateRC"),
                    TRACE_DATA_HEX_INT(localWLMCreateRSN, "localWLMCreateRSN"),
                    TRACE_DATA_HEX_INT(localWLMJoinRC, "localWLMJoinRC"),
                    TRACE_DATA_HEX_INT(localWLMJoinRSN, "localWLMJoinRSN"),
                    TRACE_DATA_RAWDATA(sizeof(localEncRegToken),
                                       &localEncRegToken,
                                      "localEncRegToken"),
                    TRACE_DATA_END_PARMS);
    }

    if (pc_rc != 0) {
        if (TraceActive(trc_level_exception)) {
            TraceRecord(trc_level_exception,
                        TP(TP_A_CREATEJOINWORKUNIT_BAD_PC_RC),
                        "ntv_createJoinWorkUnit Bad PC RC",
                        TRACE_DATA_INT(pc_rc, "Authorized PC Return Code"),
                        TRACE_DATA_END_PARMS);
        }

        populateWLMServiceResults(env,
                                  0,
                                  0,
                                  "IWM4ECRE",
                                  pc_rc,
                                  "ntv_createJoinWorkUnit",
                                  NULL);

        return NULL;
    }

    // ------------------------------------------------------------------
    // Handle failure to create an enclave by returning a null token
    // ------------------------------------------------------------------
    if (localWLMCreateRC != 0) {
        if (localWLMCreateRSN == WASRETURNCODE_FAILED_TO_REGISTER) {
            populateWLMServiceResults(env,
                                      0,
                                      0,
                                      "UTIL_REGISTRY",
                                      WASRETURNCODE_FAILED_TO_REGISTER,
                                      "ntv_createJoinWorkUnit",
                                      NULL);
        } else {
            populateWLMServiceResults(env,
                                      localWLMCreateRC,
                                      (localWLMCreateRSN & 0x0000FFFF),
                                      "IWM4ECRE",
                                      0,
                                      "ntv_createJoinWorkUnit",
                                      NULL);
        }

        return NULL;
    }

    // ------------------------------------------------------------------
    // Handle failure to join created enclave by returning a null token
    // ------------------------------------------------------------------
    if (localWLMJoinRC != 0) {
        populateWLMServiceResults(env,
                                  localWLMJoinRC,
                                  (localWLMJoinRSN & 0x0000FFFF),
                                  "IWMEJOIN",
                                  0,
                                  "ntv_createJoinWorkUnit",
                                  NULL);

        return NULL;
    }

    // set output service class token
    (*env)->SetByteArrayRegion(env, outputServiceClassToken, 0, sizeof(int), (jbyte*)&localOutputServiceClassToken);

    javaToken = (*env)->NewByteArray(env, sizeof(localEncRegToken));
    if (javaToken == NULL) {
        // Clear the exception so we can report back
        (*env)->ExceptionDescribe(env);
        (*env)->ExceptionClear(env);
        populateWLMServiceResults(env,
                                  0,
                                  0,
                                  NULL,
                                  ENOMEM,
                                  "ntv_createJoinWorkUnit",
                                  NULL);

        return NULL;
    }

    (*env)->SetByteArrayRegion(env,
                               javaToken,
                               0,
                               sizeof(localEncRegToken),
                               (jbyte*) &localEncRegToken);


    return javaToken;
}

/**
 * Join a WLM work unit
 *
 * @parms etoken is the WLM Enclave to join to the current thread.
 *
 * @return TRUE (1) if the passed WLM Enclave Token was joined to the current
 * thread.  FALSE(0) if not.
 */
JNIEXPORT jboolean JNICALL
ntv_joinWorkUnit(JNIEnv* env, jobject jobj, jbyteArray etoken) {

    RegistryToken localEncRegToken;
    int retcode = -1;
    int rsncode = -1;
    int pc_rc = -1;

    (*env)->GetByteArrayRegion(env,
                               etoken,
                               0,
                               sizeof(localEncRegToken),
                               (jbyte*) &localEncRegToken);

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(TP_A_JOINWORKUNIT_ENTRY),
                    "ntv_joinWorkUnit entry",
                    TRACE_DATA_RAWDATA(sizeof(localEncRegToken),
                                       &localEncRegToken,
                                       "enclave token"),
                    TRACE_DATA_END_PARMS);
    }

    WLM_EnclaveJoinParms encJoinParms = {
          .inEnclaveToken = &localEncRegToken,
          .outRC = &retcode,
          .outRSN = &rsncode
    };

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(TP_A_JOINWORKUNIT_CALL),
                    "ntv_joinWorkUnit wlm_enclave_join call",
                    TRACE_DATA_RAWDATA(sizeof(encJoinParms),
                                       &encJoinParms,
                                       "WLM_EnclaveJoinParms"),
                    TRACE_DATA_END_PARMS);
    }

    // Get the authorized server PC stub to be able to call authorized services.
    const server_authorized_function_stubs* auth_stubs_p = getServerAuthorizedFunctionStubs();

    pc_rc = auth_stubs_p->wlm_enclave_join(&encJoinParms);

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(TP_A_JOINWORKUNIT_RETURN),
                    "ntv_joinWorkUnit WLM_EnclaveJoinParms return",
                    TRACE_DATA_INT(pc_rc, "pc_rc"),
                    TRACE_DATA_HEX_INT(retcode, "retcode"),
                    TRACE_DATA_HEX_INT(rsncode, "rsncode"),
                    TRACE_DATA_END_PARMS);
    }

    if (pc_rc != 0) {
        if (TraceActive(trc_level_exception)) {
            TraceRecord(trc_level_exception,
                        TP(TP_A_JOINWORKUNIT_BAD_PC_RC),
                        "ntv_joinWorkUnit Bad PC RC",
                        TRACE_DATA_INT(pc_rc, "Authorized PC Return Code"),
                        TRACE_DATA_END_PARMS);
        }

        populateWLMServiceResults(env,
                                  0,
                                  0,
                                  "IWMEJOIN",
                                  pc_rc,
                                  "ntv_joinWorkUnit",
                                  NULL);

        return 0;
    }

    // ------------------------------------------------------------------
    // Handle failure to join an enclave
    // ------------------------------------------------------------------
    if (retcode != 0) {
        if (rsncode == WASRETURNCODE_FAILED_TO_VALIDATE_IN_REGISTRY) {
            populateWLMServiceResults(env,
                                      0,
                                      0,
                                      "UTIL_REGISTRY",
                                      WASRETURNCODE_FAILED_TO_VALIDATE_IN_REGISTRY,
                                      "ntv_joinWorkUnit",
                                      NULL);
        } else {
            populateWLMServiceResults(env,
                                      retcode,
                                      (rsncode & 0x0000FFFF),
                                      "IWMEJOIN",
                                      0,
                                      "ntv_joinWorkUnit",
                                      NULL);
        }

        return 0;
    }

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(TP_A_JOINWORKUNIT_EXIT),
                    "ntv_joinWorkUnit exit",
                    TRACE_DATA_INT(retcode, "return code"),
                    TRACE_DATA_END_PARMS);
    }

    return 1;
}

/**
 * Leave a WLM work unit
 */
JNIEXPORT jboolean JNICALL
ntv_leaveWorkUnit(JNIEnv* env, jobject jobj, jbyteArray etoken) {
    RegistryToken localEncRegToken;
    int retcode = -1;
    int rsncode = -1;
    int pc_rc = -1;

    (*env)->GetByteArrayRegion(env,
                               etoken,
                               0,
                               sizeof(localEncRegToken),
                               (jbyte*) &localEncRegToken);

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(TP_A_LEAVEWORKUNIT_ENTRY),
                    "ntv_leaveWorkUnit Entry",
                    TRACE_DATA_RAWDATA(sizeof(localEncRegToken),
                                       &localEncRegToken,
                                       "enclave token"),
                    TRACE_DATA_END_PARMS);
    }

    WLM_EnclaveLeaveParms encLeaveParms = {
          .inEnclaveToken = &localEncRegToken,
          .outRC = &retcode,
          .outRSN = &rsncode
    };

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(TP_A_LEAVEWORKUNIT_CALL),
                    "ntv_leaveWorkUnit wlm_enclave_leave call",
                    TRACE_DATA_RAWDATA(sizeof(encLeaveParms),
                                       &encLeaveParms,
                                       "WLM_EnclaveLeaveParms"),
                    TRACE_DATA_END_PARMS);
    }

    // Get the authorized server PC stub to be able to call authorized services.
    const server_authorized_function_stubs* auth_stubs_p = getServerAuthorizedFunctionStubs();

    pc_rc = auth_stubs_p->wlm_enclave_leave(&encLeaveParms);

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(TP_A_LEAVEWORKUNIT_RETURN),
                    "ntv_leaveWorkUnit WLM_EnclaveLeaveParms return",
                    TRACE_DATA_INT(pc_rc, "pc_rc"),
                    TRACE_DATA_HEX_INT(retcode, "retcode"),
                    TRACE_DATA_HEX_INT(rsncode, "rsncode"),
                    TRACE_DATA_END_PARMS);
    }

    if (pc_rc != 0) {
        if (TraceActive(trc_level_exception)) {
            TraceRecord(trc_level_exception,
                        TP(TP_A_LEAVEWORKUNIT_BAD_PC_RC),
                        "ntv_leaveWorkUnit Bad PC RC",
                        TRACE_DATA_INT(pc_rc, "Authorized PC Return Code"),
                        TRACE_DATA_END_PARMS);
        }

        populateWLMServiceResults(env,
                                  0,
                                  0,
                                  "IWMELEAV",
                                  pc_rc,
                                  "ntv_leaveWorkUnit",
                                  NULL);

        return 0;
    }

    // ------------------------------------------------------------------
    // Handle failure to leave an enclave
    // ------------------------------------------------------------------
    if (retcode != 0) {
        if (rsncode == WASRETURNCODE_FAILED_TO_VALIDATE_IN_REGISTRY) {
            populateWLMServiceResults(env,
                                      0,
                                      0,
                                      "UTIL_REGISTRY",
                                      WASRETURNCODE_FAILED_TO_VALIDATE_IN_REGISTRY,
                                      "ntv_leaveWorkUnit",
                                      NULL);
        } else {
            populateWLMServiceResults(env,
                                      retcode,
                                      (rsncode & 0x0000FFFF),
                                      "IWMELEAV",
                                      0,
                                      "ntv_leaveWorkUnit",
                                      NULL);
        }

        return 0;
    }

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(TP_A_LEAVEWORKUNIT_EXIT),
                    "ntv_leaveWorkUnit Exit",
                    TRACE_DATA_INT(retcode, "return code"),
                    TRACE_DATA_END_PARMS);
    }

    return 1;
}

/**
 * Delete a WLM work unit
 */
JNIEXPORT jint JNICALL
ntv_deleteWorkUnit(JNIEnv *env, jobject jobj, jbyteArray etoken, jbyteArray outputDeleteData) {
    RegistryToken localEncRegToken;
    int retcode = -1;
    int rsncode = -1;
    int pc_rc = -1;
    WLM_EnclaveDeleteDataPlusToken deleteData;

    (*env)->GetByteArrayRegion(env,
                               etoken,
                               0,
                               sizeof(localEncRegToken),
                               (jbyte*) &localEncRegToken);

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(TP_A_DELETEWORKUNIT_ENTRY),
                    "ntv_deleteWorkUnit Entry",
                    TRACE_DATA_RAWDATA(sizeof(localEncRegToken),
                                       &localEncRegToken,
                                       "enclave token"),
                    TRACE_DATA_END_PARMS);
    }

    WLM_EnclaveDeleteParms encDeleteParms = {
          .inEnclaveToken = &localEncRegToken,
          .outRC = &retcode,
          .outRSN = &rsncode,
          .outDeleteData = &deleteData
    };

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(TP_A_DELETEWORKUNIT_CALL),
                    "ntv_deleteWorkUnit wlm_enclave_delete call",
                    TRACE_DATA_RAWDATA(sizeof(encDeleteParms),
                                       &encDeleteParms,
                                       "WLM_EnclaveDeleteParms"),
                    TRACE_DATA_END_PARMS);
    }

    // Get the authorized server PC stub to be able to call authorized services.
    const server_authorized_function_stubs* auth_stubs_p = getServerAuthorizedFunctionStubs();

    pc_rc = auth_stubs_p->wlm_enclave_delete(&encDeleteParms);

    if (TraceActive(trc_level_detailed)) {
        TraceRecord( trc_level_detailed,
                    TP(TP_A_DELETEWORKUNIT_RETURN),
                    "ntv_deleteWorkUnit WLM_EnclaveDeleteParms return",
                    TRACE_DATA_INT(pc_rc, "pc_rc"),
                    TRACE_DATA_HEX_INT(retcode, "retcode"),
                    TRACE_DATA_HEX_INT(rsncode, "rsncode"),
                    TRACE_DATA_RAWDATA(sizeof(deleteData), &deleteData, "delete data"),
                    TRACE_DATA_END_PARMS);
    }

    if (pc_rc != 0) {
        if (TraceActive(trc_level_exception)) {
            TraceRecord(trc_level_exception,
                        TP(TP_A_DELETEWORKUNIT_BAD_PC_RC),
                        "ntv_deleteWorkUnit Bad PC RC",
                        TRACE_DATA_INT(pc_rc, "Authorized PC Return Code"),
                        TRACE_DATA_END_PARMS);
        }

        populateWLMServiceResults(env,
                                  0,
                                  0,
                                  "IWMEDELE",
                                  pc_rc,
                                  "ntv_deleteWorkUnit",
                                  NULL);

        return -1;
    }

    // ------------------------------------------------------------------
    // Handle failure to delete an enclave
    // ------------------------------------------------------------------
    if (retcode != 0) {
        if (rsncode == WASRETURNCODE_FAILED_TO_VALIDATE_IN_REGISTRY) {
            populateWLMServiceResults(env,
                                      0,
                                      0,
                                      "UTIL_REGISTRY",
                                      WASRETURNCODE_FAILED_TO_VALIDATE_IN_REGISTRY,
                                      "ntv_deleteWorkUnit",
                                      NULL);
        } else {
            // set output delete data
            // Data seems to be filled in for WLM 0411 so copy back even if a bad return code.
            (*env)->SetByteArrayRegion(env, outputDeleteData, 0, sizeof(deleteData), (jbyte*)&deleteData);

            populateWLMServiceResults(env,
                                      retcode,
                                      (rsncode & 0x0000FFFF),
                                      "IWMEDELE",
                                      0,
                                      "ntv_deleteWorkUnit",
                                      NULL);
        }

        return -1;
    }

    // set output delete data
    (*env)->SetByteArrayRegion(env, outputDeleteData, 0, sizeof(deleteData), (jbyte*)&deleteData);

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(TP_A_DELETEWORKUNIT_EXIT),
                    "ntv_deleteWorkUnit Exit",
                    TRACE_DATA_INT(retcode, "return code"),
                    TRACE_DATA_END_PARMS);
    }

    return 0;
}

/**
 * Leave a WLM work unit then Delete the Enclave
 */
JNIEXPORT jint JNICALL
ntv_leaveDeleteWorkUnit(JNIEnv* env, jobject jobj, jbyteArray etoken, jbyteArray outputDeleteData) {

    RegistryToken localEncRegToken;
    int leaveRC, leaveRSN;
    int deleteRC, deleteRSN;
    int pc_rc = -1;
    WLM_EnclaveDeleteDataPlusToken deleteData;

    (*env)->GetByteArrayRegion(env,
                               etoken,
                               0,
                               sizeof(localEncRegToken),
                               (jbyte*) &localEncRegToken);

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(TP_A_LEAVEDELETEWORKUNIT_ENTRY),
                    "ntv_leaveDeleteWorkUnit Entry",
                    TRACE_DATA_RAWDATA(sizeof(localEncRegToken),
                                       &localEncRegToken,
                                       "enclave token"),
                    TRACE_DATA_END_PARMS);
    }

    WLM_EnclaveLeaveDeleteParms encLeaveDeleteParms = {
          .inEnclaveToken = &localEncRegToken,
          .outLeaveRC = &leaveRC,
          .outLeaveRSN = &leaveRSN,
          .outDeleteRC = &deleteRC,
          .outDeleteRSN = &deleteRSN,
          .outDeleteData = &deleteData
    };

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(TP_A_LEAVEDELETEWORKUNIT_CALL),
                    "ntv_leaveDeleteWorkUnit wlm_enclave_leave_delete call",
                    TRACE_DATA_RAWDATA(sizeof(encLeaveDeleteParms),
                                       &encLeaveDeleteParms,
                                       "WLM_EnclaveLeaveDeleteParms"),
                    TRACE_DATA_END_PARMS);
    }

    // Get the authorized server PC stub to be able to call authorized services.
    const server_authorized_function_stubs* auth_stubs_p = getServerAuthorizedFunctionStubs();

    pc_rc = auth_stubs_p->wlm_enclave_leave_delete(&encLeaveDeleteParms);

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(TP_A_LEAVEDELETEWORKUNIT_RETURN),
                    "ntv_leaveDeleteWorkUnit WLM_EnclaveLeaveDeleteParms return",
                    TRACE_DATA_INT(pc_rc, "pc_rc"),
                    TRACE_DATA_HEX_INT(leaveRC, "leaveRC"),
                    TRACE_DATA_HEX_INT(leaveRSN, "leaveRSN"),
                    TRACE_DATA_HEX_INT(deleteRC, "deleteRC"),
                    TRACE_DATA_HEX_INT(deleteRSN, "deleteRSN"),
                    TRACE_DATA_RAWDATA(sizeof(deleteData), &deleteData, "delete data"),
                    TRACE_DATA_END_PARMS);
    }

    if (pc_rc != 0) {
        if (TraceActive(trc_level_exception)) {
            TraceRecord(trc_level_exception,
                        TP(TP_A_LEAVEDELETEWORKUNIT_BAD_PC_RC),
                        "ntv_leaveDeleteWorkUnit Bad PC RC",
                        TRACE_DATA_INT(pc_rc, "Authorized PC Return Code"),
                        TRACE_DATA_END_PARMS);
        }

        populateWLMServiceResults(env,
                                  0,
                                  0,
                                  "IWMELEAV",
                                  pc_rc,
                                  "ntv_leaveDeleteWorkUnit",
                                  NULL);

        return -1;
    }

    // ------------------------------------------------------------------
    // Handle failure to leave an enclave
    // ------------------------------------------------------------------
    if (leaveRC != 0) {
        if (leaveRSN == WASRETURNCODE_FAILED_TO_VALIDATE_IN_REGISTRY) {
            populateWLMServiceResults(env,
                                      0,
                                      0,
                                      "UTIL_REGISTRY",
                                      WASRETURNCODE_FAILED_TO_VALIDATE_IN_REGISTRY,
                                      "ntv_leaveDeleteWorkUnit",
                                      NULL);
        } else {
            populateWLMServiceResults(env,
                                      leaveRC,
                                      (leaveRSN & 0x0000FFFF),
                                      "IWMELEAV",
                                      0,
                                      "ntv_leaveDeleteWorkUnit",
                                      NULL);
        }

        return -1;
    }

    // set output delete data
    // Data seems to be filled in for WLM 0411 so copy back even if a bad return code.
    (*env)->SetByteArrayRegion(env, outputDeleteData, 0, sizeof(deleteData), (jbyte*)&deleteData);

    // ------------------------------------------------------------------
    // Handle failure to delete an enclave
    // ------------------------------------------------------------------
    if (deleteRC != 0) {
        populateWLMServiceResults(env,
                                  deleteRC,
                                  (deleteRSN & 0x0000FFFF),
                                  "IWMEDELE",
                                  0,
                                  "ntv_leaveDeleteWorkUnit",
                                  NULL);

        return -1;
    }

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(TP_A_LEAVEDELETEWORKUNIT_EXIT),
                    "ntv_leaveDeleteWorkUnit Exit",
                    TRACE_DATA_END_PARMS);
    }

    return 0;
}
