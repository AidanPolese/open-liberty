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
 * JNI assorted routines for Local Communications support.
 */
#include <assert.h>
#include <dlfcn.h>
#include <errno.h>
#include <jni.h>
#include <stdio.h>
#include <stdlib.h>

#include "include/ras_tracing.h"
#include "include/server_jni_method_manager.h"
#include "include/server_native_service_tracker.h"
#include "include/server_lcom_services.h"

#define RAS_MODULE_CONST  RAS_MODULE_SERVER_LCOM_SERVICES_JNI

#define TP_INITCHANNEL_ENTRY                        1
#define TP_INITCHANNEL_INIT_CALL                    2
#define TP_INITCHANNEL_INIT_RETURN                  3
#define TP_INITCHANNEL_BAD_PC_RC                    4
#define TP_INITCHANNEL_EXIT                         5
#define TP_POPULATELCOMSERVICERESULTS_ENTRY         6
#define TP_POPULATELCOMSERVICERESULTS_EXIT          7
#define TP_UNINITCHANNEL_ENTRY                      8
#define TP_UNINITCHANNEL_UNINIT_CALL                9
#define TP_UNINITCHANNEL_UNINIT_RETURN             10
#define TP_UNINITCHANNEL_BAD_PC_RC                 11
#define TP_UNINITCHANNEL_EXIT                      12
#define TP_NRH_GETWORK_ENTRY                       13
#define TP_NRH_GETWORK_GETWRQ_CALL                 14
#define TP_NRH_GETWORK_GETWRQ_RETURN               15
#define TP_NRH_GETWORK_GETWRQ_BAD_PC_RC            16
#define TP_NRH_GETWORK_GETWRQ_EXIT                 17
#define TP_NRH_FREEWORK_ENTRY                      18
#define TP_NRH_FREEWWRQES_CALL                     19
#define TP_NRH_FREEWWRQES_RETURN                   20
#define TP_NRH_FREEWWRQES_BAD_PC_RC                21
#define TP_NRH_FREEWWRQES_EXIT                     22
#define TP_NRH_STOPLISTENING_ENTRY                 23
#define TP_NRH_STOPLISTENING_CALL                  24
#define TP_NRH_STOPLISTENING_RETURN                25
#define TP_NRH_STOPLISTENING_BAD_PC_RC             26
#define TP_NRH_STOPLISTENING_EXIT                  27
#define TP_NRH_CONRESP_ENTRY                       28
#define TP_NRH_CONRESP_EXIT                        29
#define TP_NRH_CONRESP_AUTH_CALL                   30
#define TP_NRH_CONRESP_AUTH_RETURN                 31
#define TP_NRH_CONRESP_AUTH_BAD_PC_RC              32
#define TP_NRH_CONCSHARED_ENTRY                    33
#define TP_NRH_CONCSHARED_AUTH_CALL                34
#define TP_NRH_CONCSHARED_AUTH_RETURN              35
#define TP_NRH_CONCSHARED_AUTH_BAD_PC_RC           36
#define TP_NRH_CONCSHARED_EXIT                     37
#define TP_NRH_READ_ENTRY                          38
#define TP_NRH_READ_AUTH_CALL                      39
#define TP_NRH_READ_AUTH_RETURN                    40
#define TP_NRH_READ_AUTH_BAD_PC_RC                 41
#define TP_NRH_READ_EXIT                           42
#define TP_NRH_RELLMSG_ENTRY                       43
#define TP_NRH_RELLMSG_AUTH_CALL                   44
#define TP_NRH_RELLMSG_AUTH_RETURN                 45
#define TP_NRH_RELLMSG_AUTH_BAD_PC_RC              46
#define TP_NRH_RELLMSG_EXIT                        47
#define TP_NRH_WRITE_ENTRY                         48
#define TP_NRH_WRITE_AUTH_CALL                     49
#define TP_NRH_WRITE_AUTH_RETURN                   50
#define TP_NRH_WRITE_AUTH_BAD_PC_RC                51
#define TP_NRH_WRITE_EXIT                          52
#define TP_NRH_CLOSE_ENTRY                         53
#define TP_NRH_CLOSE_AUTH_CALL                     54
#define TP_NRH_CLOSE_AUTH_RETURN                   55
#define TP_NRH_CLOSE_AUTH_BAD_PC_RC                56
#define TP_NRH_CLOSE_EXIT                          57
#define TP_NRH_DISCONCSHARED_ENTRY                 58
#define TP_NRH_DISCONCSHARED_AUTH_CALL             59
#define TP_NRH_DISCONCSHARED_AUTH_RETURN           60
#define TP_NRH_DISCONCSHARED_AUTH_BAD_PC_RC        61
#define TP_NRH_DISCONCSHARED_EXIT                  62
#define TP_NRH_INITWRQFLAGS_ENTRY                  63
#define TP_NRH_INITWRQFLAGS_CALL                   64
#define TP_NRH_INITWRQFLAGS_RETURN                 65
#define TP_NRH_INITWRQFLAGS_BAD_PC_RC              66
#define TP_NRH_INITWRQFLAGS_EXIT                   67
#define TP_NRH_GETWORK_GETWRQ_SERVERHARDFAILURE    68

// Forward declares for local routines
int releaseDataMessage(OpaqueClientConnectionHandle_t* clientConnHandle, LCOM_AvailableDataVector* dataVector_p);


// --------------------------------------------------------------------
// Local Comm JNI function declaration and export
// --------------------------------------------------------------------
#pragma export(ntv_initializeChannel)
JNIEXPORT jint JNICALL
ntv_initializeChannel(JNIEnv* env, jclass jobj);

#pragma export(ntv_uninitializeChannel)
JNIEXPORT jint JNICALL
ntv_uninitializeChannel(JNIEnv* env, jclass jobj);

// --------------------------------------------------------------------
// JNI native method structure for the LocalChannelProviderImpl methods
// --------------------------------------------------------------------
#pragma convert("ISO8859-1")
static const JNINativeMethod LocalChannelProviderImplMethods[] = {
    { "ntv_initializeChannel",
      "()I",
      (void *) ntv_initializeChannel },
    { "ntv_uninitializeChannel",
      "()I",
      (void *) ntv_uninitializeChannel }
};
#pragma convert(pop)

// --------------------------------------------------------------------
// Callback methods for the local comm services
// --------------------------------------------------------------------
int
lcomRegFunction(JNIEnv* env, jclass clazz, jobjectArray extraInfo);

int
lcomDeRegFunction(JNIEnv* env, jclass clazz, jobjectArray extraInfo);

// --------------------------------------------------------------------
// NativeMethodDescriptor for the LocalChannelProviderImpl
// --------------------------------------------------------------------
#pragma export(zJNI_com_ibm_ws_zos_channel_local_queuing_internal_LocalChannelProviderImpl)
NativeMethodDescriptor zJNI_com_ibm_ws_zos_channel_local_queuing_internal_LocalChannelProviderImpl = {
    .registrationFunction = lcomRegFunction,
    .deregistrationFunction = lcomDeRegFunction,
    .nativeMethodCount = sizeof(LocalChannelProviderImplMethods) / sizeof(LocalChannelProviderImplMethods[0]),
    .nativeMethods = LocalChannelProviderImplMethods
};

// --------------------------------------------------------------------
// Local Comm JNI function declaration and exports for
// NativeRequestHandler
// --------------------------------------------------------------------
#pragma export(ntv_getWorkRequestElements)
JNIEXPORT jlong JNICALL
ntv_getWorkRequestElements(JNIEnv* env, jclass jobj, jboolean otherWorkToDo);

#pragma export(ntv_freeWorkRequestElements)
JNIEXPORT jint JNICALL
ntv_freeWorkRequestElements(JNIEnv* env, jclass jobj);

#pragma export(ntv_stopListeningForRequests)
JNIEXPORT jint JNICALL
ntv_stopListeningForRequests(JNIEnv* env, jclass jobj);

#pragma export(ntv_connectResponse)
JNIEXPORT jint JNICALL
ntv_connectResponse(JNIEnv* env, jclass jobj, jbyteArray clientConnHandleBytes);

#pragma export(ntv_connectToClientsSharedMemory)
JNIEXPORT jint JNICALL
ntv_connectToClientsSharedMemory(JNIEnv* env, jclass jobj, jbyteArray clientConnHandleBytes, jbyteArray requestSpecificParms);

#pragma export(ntv_disconnectFromClientsSharedMemory)
JNIEXPORT jint JNICALL
ntv_disconnectFromClientsSharedMemory(JNIEnv* env, jclass jobj, jbyteArray clientConnHandleBytes, jbyteArray requestSpecificParms);

#pragma export(ntv_read)
JNIEXPORT jbyteArray JNICALL
ntv_read(JNIEnv* env, jclass jobj, jbyteArray clientConnHandleBytes, jboolean forceAsync, jbyteArray requestServiceResultByteArray);

#pragma export(ntv_write)
JNIEXPORT jint JNICALL
ntv_write(JNIEnv* env, jclass jobj, jbyteArray clientConnhandleBytes, jbyteArray dataArray, jbyteArray requestServiceResultByteArray);

#pragma export(ntv_close)
JNIEXPORT jint JNICALL
ntv_close(JNIEnv* env, jclass jobj, jbyteArray clientConnHandleBytes, jbyteArray requestServiceResultByteArray);

#pragma export(ntv_initWRQFlags)
JNIEXPORT jint JNICALL
ntv_initWRQFlags(JNIEnv* env, jclass jobj);

// --------------------------------------------------------------------
// JNI native method structure for the NativeRequestHandler methods
// --------------------------------------------------------------------
#pragma convert("ISO8859-1")
static const JNINativeMethod NativeRequestHandlerMethods[] = {
    { "ntv_getWorkRequestElements",
      "(Z)J",
      (void *) ntv_getWorkRequestElements },
    { "ntv_freeWorkRequestElements",
      "()I",
      (void *) ntv_freeWorkRequestElements },
    { "ntv_stopListeningForRequests",
      "()I",
      (void *) ntv_stopListeningForRequests },
    { "ntv_connectResponse",
      "([B)I",
      (void *) ntv_connectResponse },
    { "ntv_connectToClientsSharedMemory",
      "([B[B)I",
      (void *) ntv_connectToClientsSharedMemory },
    { "ntv_disconnectFromClientsSharedMemory",
      "([B[B)I",
      (void *) ntv_disconnectFromClientsSharedMemory },
    { "ntv_read",
      "([BZ[B)[B",
      (void *) ntv_read },
    { "ntv_write",
      "([B[B[B)I",
      (void *) ntv_write },
    { "ntv_close",
      "([B[B)I",
      (void *) ntv_close },
    { "ntv_initWRQFlags",
      "()I",
      (void *) ntv_initWRQFlags }
};
#pragma convert(pop)

// --------------------------------------------------------------------
// Callback methods for the local comm services
// --------------------------------------------------------------------
int
nativeRequestHandlerRegFunction(JNIEnv* env, jclass clazz, jobjectArray extraInfo);

int
nativeRequestHandlerDeRegFunction(JNIEnv* env, jclass clazz, jobjectArray extraInfo);

// --------------------------------------------------------------------
// NativeMethodDescriptor for the NativeRequestHandler
// --------------------------------------------------------------------
#pragma export(zJNI_com_ibm_ws_zos_channel_local_queuing_internal_NativeRequestHandler)
NativeMethodDescriptor zJNI_com_ibm_ws_zos_channel_local_queuing_internal_NativeRequestHandler = {
    .registrationFunction = NULL,
    .deregistrationFunction = NULL,
    .nativeMethodCount = sizeof(NativeRequestHandlerMethods) / sizeof(NativeRequestHandlerMethods[0]),
    .nativeMethods = NativeRequestHandlerMethods
};


// --------------------------------------------------------------------
// Module scoped data
// --------------------------------------------------------------------

// Upcall information
jclass lcomServiceResultsClass = NULL;
jmethodID setResultsMethodID = NULL;


#define MAX_SERVICENAME_LENGTH  256

void
populateLComServiceResults(JNIEnv* env,
                           int lcomReturnCode,
                           int lcomReasonCode,
                           char* lcomRoutine,
                           int wasReturnCode,
                           char* wasRoutine,
                           jbyteArray returnData);


/**
 * Respond to the registerNatives call performed out of bundle activation and
 * setup the required native infrastructure.  This method will resolve and cache
 * the method ID's for the LocalChannelProviderImpl function, and prepare to manage
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
//TODO: make static so its not exported???
int lcomRegFunction(JNIEnv* env, jclass clazz, jobjectArray extraInfo) {
    int rc = JNI_OK;

    // --------------------------------------------------------------------
    // Setup the LComServiceResults class and methods we need to invoke
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

    lcomServiceResultsClass = callbackClass;
    if ((*env)->ExceptionCheck(env)) {
        return JNI_ERR;
    }

    lcomServiceResultsClass = (jclass) (*env)->NewGlobalRef(env, lcomServiceResultsClass);
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
lcomDeRegFunction(JNIEnv* env, jclass clazz, jobjectArray extraInfo) {

    if (lcomServiceResultsClass != NULL) {
        (*env)->DeleteGlobalRef(env, lcomServiceResultsClass);
        lcomServiceResultsClass = NULL;
    }

    setResultsMethodID = NULL;

    return JNI_OK;
}


/**
 * Initialize Local Comm native structures and setup listening code
 */
JNIEXPORT jint JNICALL
ntv_initializeChannel(JNIEnv* env, jclass jobj) {

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(TP_INITCHANNEL_ENTRY),
                    "ntv_initializeChannel entry",
                    TRACE_DATA_END_PARMS);
    }

    // ------------------------------------------------------------------
    // Initialize a bunch of Stuff to allow clients to find us and to
    // be able to start taking requests (connects, receives, ...)
    //
    // 1) Initialize BBGZLCOM structure
    // 2) Initialize the Async Completion Queue (aka "Black Queue")
    // 3) Expose this server to Local Comm clients (get an MVS Enq that
    //    LCOM Clients expect to use to find this server)
    // ------------------------------------------------------------------

    int localLComInitRC, localLComInitRSN;
    int pc_rc = -1;

    LCOM_InitParms initParms = {
        .outRC = &localLComInitRC,
        .outRSN = &localLComInitRSN
    };

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(TP_INITCHANNEL_INIT_CALL),
                    "ntv_initializeChannel lcom_init call",
                    TRACE_DATA_RAWDATA(sizeof(initParms),
                                       &initParms,
                                       "LCOM_InitParms"),
                    TRACE_DATA_END_PARMS);
    }

    // Get the authorized server PC stub to be able to call authorized services.
    const server_authorized_function_stubs* auth_stubs_p = getServerAuthorizedFunctionStubs();

    pc_rc = auth_stubs_p->lcom_init(&initParms);

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(TP_INITCHANNEL_INIT_RETURN),
                    "ntv_initializeChannel lcom_init return",
                    TRACE_DATA_INT(pc_rc, "pc_rc"),
                    TRACE_DATA_HEX_INT(localLComInitRC, "localLComInitRC"),
                    TRACE_DATA_HEX_INT(localLComInitRSN, "localLComInitRSN"),
                    TRACE_DATA_END_PARMS);
    }

    if (pc_rc != 0) {
        if (TraceActive(trc_level_exception)) {
            TraceRecord(trc_level_exception,
                        TP(TP_INITCHANNEL_BAD_PC_RC),
                        "ntv_initializeChannel Bad PC RC",
                        TRACE_DATA_INT(pc_rc, "Authorized PC Return Code"),
                        TRACE_DATA_END_PARMS);
        }

        populateLComServiceResults(env,
                                   0,
                                   0,
                                   "lcom_init",
                                   pc_rc,
                                   "ntv_initializeChannel",
                                   NULL);

        return -1;
    }

    // ------------------------------------------------------------------
    // Handle failure to initialize
    // ------------------------------------------------------------------
    if (localLComInitRC != 0) {

        populateLComServiceResults(env,
                                  localLComInitRC,
                                  (localLComInitRSN & 0x0000FFFF),
                                  "lcom_init",
                                  -1,
                                  "ntv_initializeChannel",
                                  NULL);

        return -1;
    }


    if (TraceActive(trc_level_detailed)) {
      TraceRecord(trc_level_detailed,
                  TP(TP_INITCHANNEL_EXIT),
                  "ntv_initializeChannel exit",
                  TRACE_DATA_HEX_INT(
                      localLComInitRC,
                      "localLComInitRC"),
                  TRACE_DATA_HEX_INT(
                      localLComInitRSN,
                      "localLComInitRSN"),
                  TRACE_DATA_END_PARMS);
    }

    return 0;
}

/**
 * Uninitialize Local Comm native structures and listening code
 */
JNIEXPORT jint JNICALL
ntv_uninitializeChannel(JNIEnv* env, jclass jobj) {

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(TP_UNINITCHANNEL_ENTRY),
                    "ntv_uninitializeChannel entry",
                    TRACE_DATA_END_PARMS);
    }

    // ------------------------------------------------------------------
    // Uninitialize:
    // 1) Drop the ENQ obtained that allows new clients to find us
    // 2) Unhook the BBGZLCOM structure from the PGOO
    //   2.A) If successful, then we can clean it up
    //   2.B) If not (in use), then ???? Mark it for deletion
    //
    // ------------------------------------------------------------------

    int localLComUninitRC, localLComUninitRSN;
    int pc_rc = -1;

    LCOM_UninitParms uninitParms = {

        .outRC = &localLComUninitRC,
        .outRSN = &localLComUninitRSN
    };

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(TP_UNINITCHANNEL_UNINIT_CALL),
                    "ntv_uninitializeChannel lcom_uninit call",
                    TRACE_DATA_RAWDATA(sizeof(uninitParms),
                                       &uninitParms,
                                       "LCOM_UninitParms"),
                    TRACE_DATA_END_PARMS);
    }

    // Get the authorized server PC stub to be able to call authorized services.
    const server_authorized_function_stubs* auth_stubs_p = getServerAuthorizedFunctionStubs();

    pc_rc = auth_stubs_p->lcom_uninit(&uninitParms);

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(TP_UNINITCHANNEL_UNINIT_RETURN),
                    "ntv_uninitializeChannel lcom_uninit return",
                    TRACE_DATA_INT(pc_rc, "pc_rc"),
                    TRACE_DATA_HEX_INT(localLComUninitRC, "localLComUninitRC"),
                    TRACE_DATA_HEX_INT(localLComUninitRSN, "localLComUninitRSN"),
                    TRACE_DATA_END_PARMS);
    }

    if (pc_rc != 0) {
        if (TraceActive(trc_level_exception)) {
            TraceRecord(trc_level_exception,
                        TP(TP_UNINITCHANNEL_BAD_PC_RC),
                        "ntv_uninitializeChannel Bad PC RC",
                        TRACE_DATA_INT(pc_rc, "Authorized PC Return Code"),
                        TRACE_DATA_END_PARMS);
        }

        populateLComServiceResults(env,
                                   0,
                                   0,
                                   "lcom_uninit",
                                   pc_rc,
                                   "ntv_uninitializeChannel",
                                   NULL);

        return -1;
    }

    // ------------------------------------------------------------------
    // Handle failure to uninitialize
    // ------------------------------------------------------------------
    if (localLComUninitRC != 0) {

        populateLComServiceResults(env,
                                  localLComUninitRC,
                                  (localLComUninitRSN & 0x0000FFFF),
                                  "lcom_uninit",
                                  0,
                                  "ntv_uninitializeChannel",
                                  NULL);

        return -1;
    }


    if (TraceActive(trc_level_detailed)) {
      TraceRecord(trc_level_detailed,
                  TP(TP_UNINITCHANNEL_EXIT),
                  "ntv_uninitializeChannel exit",
                  TRACE_DATA_HEX_INT(
                      localLComUninitRC,
                      "localLComUninitRC"),
                  TRACE_DATA_HEX_INT(
                      localLComUninitRSN,
                      "localLComUninitRSN"),
                  TRACE_DATA_END_PARMS);
    }

    return 0;
}


// --------------------------------------------------------------------------
//
// Populate this thread's result from the LCom Service call
//
// --------------------------------------------------------------------------
void populateLComServiceResults(JNIEnv* env,
                                int lcomReturnCode,
                                int lcomReasonCode,
                                char* lcomRoutine,
                                int wasReturnCode,
                                char* wasRoutine,
                                jbyteArray returnData) {

    if (TraceActive(trc_level_detailed)) {
        TraceRecord( trc_level_detailed,
                    TP(TP_POPULATELCOMSERVICERESULTS_ENTRY),
                    "populateLComServiceResults entry",
                    TRACE_DATA_PTR(env, "JNIEnv"),
                    TRACE_DATA_HEX_INT(lcomReturnCode, "LCom return code"),
                    TRACE_DATA_HEX_INT(lcomReasonCode, "LCom reason code"),
                    TRACE_DATA_STRING(lcomRoutine, "LCom routine"),
                    TRACE_DATA_HEX_INT(wasReturnCode, "WAS return code"),
                    TRACE_DATA_STRING(wasRoutine, "WAS routine"),
                    TRACE_DATA_END_PARMS);
    }

    char serviceName[MAX_SERVICENAME_LENGTH + 1];
    jstring jlcomRoutine = NULL;
    jstring jwasRoutine = NULL;

    if (lcomRoutine != NULL) {
        strncpy(serviceName, lcomRoutine, MAX_SERVICENAME_LENGTH);
        serviceName[MAX_SERVICENAME_LENGTH] = '\0';

        __etoa(serviceName);
        jlcomRoutine = (*env)->NewStringUTF(env, serviceName);
    }

    if (wasRoutine != NULL) {
        strncpy(serviceName, wasRoutine, MAX_SERVICENAME_LENGTH);
        serviceName[MAX_SERVICENAME_LENGTH] = '\0';
        __etoa(serviceName);
        jwasRoutine = (*env)->NewStringUTF(env, serviceName);
    }

    (*env)->CallStaticVoidMethod(env,
                                 lcomServiceResultsClass,
                                 setResultsMethodID,
                                 lcomReturnCode,
                                 lcomReasonCode,
                                 jlcomRoutine,
                                 wasReturnCode,
                                 jwasRoutine,
                                 returnData);

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(TP_POPULATELCOMSERVICERESULTS_EXIT),
                    "populateLComServiceResults exit, pushed results data up",
                    TRACE_DATA_END_PARMS);
    }
}


// --------------------------------------------------------------------------
// NativeRequestHandler native methods
// --------------------------------------------------------------------------

/**
 * Free current list of WorkRequests and Get a new list of request(s) to process
 */
JNIEXPORT jlong JNICALL
ntv_getWorkRequestElements(JNIEnv* env, jclass jobj, jboolean otherWorkToDo) {


    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(TP_NRH_GETWORK_ENTRY),
                    "ntv_getWorkRequestElements entry",
                    TRACE_DATA_BOOLEAN(otherWorkToDo, "Other work to do"),
                    TRACE_DATA_END_PARMS);
    }

    // ------------------------------------------------------------------
    // Call into authorized code to free the previously returned list
    // and retrieve the current list of work requests.
    // ------------------------------------------------------------------
    int localRC, localRSN;
    int pc_rc = -1;
    long long wrqe_p = 0;

    LCOM_GetWRQParms getWRQParms = {
        .otherWorkToDo = otherWorkToDo,
        .outWRQE_Ptr = &wrqe_p,
        .outRC = &localRC,
        .outRSN = &localRSN
    };

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(TP_NRH_GETWORK_GETWRQ_CALL),
                    "ntv_getWorkRequestElements lcom_getWRQ call",
                    TRACE_DATA_RAWDATA(sizeof(getWRQParms),
                                       &getWRQParms,
                                       "LCOM_GetWRQParms"),
                    TRACE_DATA_END_PARMS);
    }

    // Get the authorized server PC stub to be able to call authorized services.
    const server_authorized_function_stubs* auth_stubs_p = getServerAuthorizedFunctionStubs();

    pc_rc = auth_stubs_p->lcom_getWRQ(&getWRQParms);

    // Check for a server hard failure before doing anything else.  If detected we can NOT do
    // anything but try to get this thread to end.
    if (localRSN == LCOM_WRQ_WAITONWORK_RC_SERVERHARD_FAILURE) {
        // Build and throw a RuntimeError?  We can NOT.  On a hard failure JVM and LE services
        // are hosed.
        //JNI_throwByName(env,"java/lang/RuntimeException", "Severe server failure detected");

        // Do NOT return to Java...the entire path may be all JIT'd and will just call back down.
        // We need to end the thread now to prevent termination hangs.
        //
        // Beware: using pthread_exit(nn) is generally not a good idea.  It could fool LE to
        // think this is a "normal" thread termination and may lead them to commit a current
        // transaction that may be marked for rollback.  We've seen this problems in WASt.
        pthread_exit((void *)TP(TP_NRH_GETWORK_GETWRQ_SERVERHARDFAILURE));
    }


    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(TP_NRH_GETWORK_GETWRQ_RETURN),
                    "ntv_getWorkRequestElements lcom_getWRQ return",
                    TRACE_DATA_HEX_LONG(wrqe_p, "wrqe_p"),
                    TRACE_DATA_INT(pc_rc, "pc_rc"),
                    TRACE_DATA_HEX_INT(localRC, "localRC"),
                    TRACE_DATA_HEX_INT(localRSN, "localRSN"),
                    TRACE_DATA_END_PARMS);
    }

    if (pc_rc != 0) {
        if (TraceActive(trc_level_exception)) {
            TraceRecord(trc_level_exception,
                        TP(TP_NRH_GETWORK_GETWRQ_BAD_PC_RC),
                        "ntv_getWorkRequestElements Bad PC RC",
                        TRACE_DATA_INT(pc_rc, "Authorized PC Return Code"),
                        TRACE_DATA_END_PARMS);
        }

        populateLComServiceResults(env,
                                   0,
                                   0,
                                   "lcom_getWRQ",
                                   pc_rc,
                                   "ntv_getWorkRequestElements",
                                   NULL);

        return -1;
    }

    // ------------------------------------------------------------------
    // Handle failure to initialize
    // ------------------------------------------------------------------
    if (localRC != 0) {

        populateLComServiceResults(env,
                                   localRC,
                                   (localRSN & 0x0000FFFF),
                                   "lcom_getWRQ",
                                   -1,
                                   "ntv_getWorkRequestElements",
                                   NULL);

        return -1;
    }


    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(TP_NRH_GETWORK_GETWRQ_EXIT),
                    "ntv_getWorkRequestElements exit",
                    TRACE_DATA_HEX_LONG(wrqe_p,
                                       "wrqe_p"),
                    TRACE_DATA_HEX_INT(localRC,
                                       "localRC"),
                    TRACE_DATA_HEX_INT(localRSN,
                                       "localRSN"),
                    TRACE_DATA_END_PARMS);
    }

    return wrqe_p;
}

/**
* Free current list of WorkRequests
*/
JNIEXPORT jint JNICALL
ntv_freeWorkRequestElements(JNIEnv* env, jclass jobj) {


    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(TP_NRH_FREEWORK_ENTRY),
                    "ntv_freeWorkRequestElements entry",
                    TRACE_DATA_END_PARMS);
    }

    // ------------------------------------------------------------------
    // Call into authorized code to free the previously returned list
    // and retrieve the current list of work requests.
    // ------------------------------------------------------------------
    int localRC, localRSN;
    int pc_rc = -1;

    LCOM_FreeWRQEsParms freeWRQEsParms = {
        .outRC = &localRC,
        .outRSN = &localRSN
    };

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(TP_NRH_FREEWWRQES_CALL),
                    "ntv_freeWorkRequestElements lcom_freeWRQEs call",
                    TRACE_DATA_RAWDATA(sizeof(freeWRQEsParms),
                                       &freeWRQEsParms,
                                       "LCOM_FreeWRQEsParms"),
                    TRACE_DATA_END_PARMS);
    }

    // Get the authorized server PC stub to be able to call authorized services.
    const server_authorized_function_stubs* auth_stubs_p = getServerAuthorizedFunctionStubs();

    pc_rc = auth_stubs_p->lcom_freeWRQEs(&freeWRQEsParms);

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(TP_NRH_FREEWWRQES_RETURN),
                    "ntv_freeWorkRequestElements lcom_freeWRQEs return",
                    TRACE_DATA_INT(pc_rc, "pc_rc"),
                    TRACE_DATA_HEX_INT(localRC, "localRC"),
                    TRACE_DATA_HEX_INT(localRSN, "localRSN"),
                    TRACE_DATA_END_PARMS);
    }

    if (pc_rc != 0) {
        if (TraceActive(trc_level_exception)) {
            TraceRecord(trc_level_exception,
                        TP(TP_NRH_FREEWWRQES_BAD_PC_RC),
                        "ntv_freeWorkRequestElements Bad PC RC",
                        TRACE_DATA_INT(pc_rc, "Authorized PC Return Code"),
                        TRACE_DATA_END_PARMS);
        }

        populateLComServiceResults(env,
                                   0,
                                   0,
                                   "lcom_freeWRQEs",
                                   pc_rc,
                                   "ntv_freeWorkRequestElements",
                                   NULL);

        return -1;
    }

    // ------------------------------------------------------------------
    // Handle failure to initialize
    // ------------------------------------------------------------------
    if (localRC != 0) {

        populateLComServiceResults(env,
                                   localRC,
                                   (localRSN & 0x0000FFFF),
                                   "lcom_freeWRQEs",
                                   -1,
                                   "ntv_freeWorkRequestElements",
                                   NULL);

        return -1;
    }


    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(TP_NRH_FREEWWRQES_EXIT),
                    "ntv_freeWorkRequestElements exit",
                    TRACE_DATA_HEX_INT(localRC,
                                       "localRC"),
                    TRACE_DATA_HEX_INT(localRSN,
                                       "localRSN"),
                    TRACE_DATA_END_PARMS);
    }

    return 0;
}

/**
* Release Listener thread so it can end.
*/
JNIEXPORT jint JNICALL
ntv_stopListeningForRequests(JNIEnv* env, jclass jobj) {


    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(TP_NRH_STOPLISTENING_ENTRY),
                    "ntv_stopListeningForRequests entry",
                    TRACE_DATA_END_PARMS);
    }

    // ------------------------------------------------------------------
    // Call into authorized code to free the previously returned list
    // and retrieve the current list of work requests.
    // ------------------------------------------------------------------
    int localRC, localRSN;
    int pc_rc = -1;

    LCOM_StopListeningOnWRQParms stopListeningOnWRQParms = {
        .outRC = &localRC,
        .outRSN = &localRSN
    };

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(TP_NRH_STOPLISTENING_CALL),
                    "ntv_stopListeningForRequests lcom_stopListeningOnWRQ call",
                    TRACE_DATA_RAWDATA(sizeof(stopListeningOnWRQParms),
                                       &stopListeningOnWRQParms,
                                       "LCOM_StopListeningOnWRQParms"),
                    TRACE_DATA_END_PARMS);
    }

    // Get the authorized server PC stub to be able to call authorized services.
    const server_authorized_function_stubs* auth_stubs_p = getServerAuthorizedFunctionStubs();

    pc_rc = auth_stubs_p->lcom_stopListeningOnWRQ(&stopListeningOnWRQParms);

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(TP_NRH_STOPLISTENING_RETURN),
                    "ntv_stopListeningForRequests lcom_stopListeningOnWRQ return",
                    TRACE_DATA_INT(pc_rc, "pc_rc"),
                    TRACE_DATA_HEX_INT(localRC, "localRC"),
                    TRACE_DATA_HEX_INT(localRSN, "localRSN"),
                    TRACE_DATA_END_PARMS);
    }

    if (pc_rc != 0) {
        if (TraceActive(trc_level_exception)) {
            TraceRecord(trc_level_exception,
                        TP(TP_NRH_STOPLISTENING_BAD_PC_RC),
                        "ntv_stopListeningForRequests Bad PC RC",
                        TRACE_DATA_INT(pc_rc, "Authorized PC Return Code"),
                        TRACE_DATA_END_PARMS);
        }

        populateLComServiceResults(env,
                                   0,
                                   0,
                                   "lcom_stopListeningOnWRQ",
                                   pc_rc,
                                   "ntv_stopListeningForRequests",
                                   NULL);

        return -1;
    }

    // ------------------------------------------------------------------
    // Handle failure to initialize
    // ------------------------------------------------------------------
    if (localRC != 0) {

        populateLComServiceResults(env,
                                   localRC,
                                   (localRSN & 0x0000FFFF),
                                   "lcom_stopListeningOnWRQ",
                                   -1,
                                   "ntv_stopListeningForRequests",
                                   NULL);

        return -1;
    }


    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(TP_NRH_STOPLISTENING_EXIT),
                    "ntv_stopListeningForRequests exit",
                    TRACE_DATA_HEX_INT(localRC,
                                       "localRC"),
                    TRACE_DATA_HEX_INT(localRSN,
                                       "localRSN"),
                    TRACE_DATA_END_PARMS);
    }

    return 0;
}

/**
 * Send Connect request results back to the client
 */
JNIEXPORT jint JNICALL
ntv_connectResponse(JNIEnv* env, jclass jobj, jbyteArray clientConnHandleBytes) {

    OpaqueClientConnectionHandle_t clientConnHandle;

    (*env)->GetByteArrayRegion(env, clientConnHandleBytes, 0, sizeof(clientConnHandle), (jbyte*) &clientConnHandle);

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(TP_NRH_CONRESP_ENTRY),
                    "ntv_connectResponse entry",
                    TRACE_DATA_RAWDATA(sizeof(clientConnHandle),
                                       &clientConnHandle,
                                       "Connection handle"),
                    TRACE_DATA_END_PARMS);
    }

    // ------------------------------------------------------------------
    // Call into authorized code to send the connection response
    // ------------------------------------------------------------------
    int localRC, localRSN;
    int pc_rc = -1;

    LCOM_ConnectResponseParms connRespParms = {
        .inConnHandle = clientConnHandle,
        .outRC = &localRC,
        .outRSN = &localRSN
    };

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(TP_NRH_CONRESP_AUTH_CALL),
                    "ntv_connectResponse lcom_connectResponse call",
                    TRACE_DATA_RAWDATA(sizeof(connRespParms),
                                       &connRespParms,
                                       "LCOM_ConnectResponseParms"),
                    TRACE_DATA_END_PARMS);
    }

    // Get the authorized server PC stub to be able to call authorized services.
    const server_authorized_function_stubs* auth_stubs_p = getServerAuthorizedFunctionStubs();

    pc_rc = auth_stubs_p->lcom_connectResponse(&connRespParms);

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(TP_NRH_CONRESP_AUTH_RETURN),
                    "ntv_connectResponse lcom_connectResponse return",
                    TRACE_DATA_INT(pc_rc, "pc_rc"),
                    TRACE_DATA_HEX_INT(localRC, "localRC"),
                    TRACE_DATA_HEX_INT(localRSN, "localRSN"),
                    TRACE_DATA_END_PARMS);
    }

    if (pc_rc != 0) {
        if (TraceActive(trc_level_exception)) {
            TraceRecord(trc_level_exception,
                        TP(TP_NRH_CONRESP_AUTH_BAD_PC_RC),
                        "ntv_connectResponse Bad PC RC",
                        TRACE_DATA_INT(pc_rc, "Authorized PC Return Code"),
                        TRACE_DATA_END_PARMS);
        }

        populateLComServiceResults(env,
                                   0,
                                   0,
                                   "lcom_connectResponse",
                                   pc_rc,
                                   "ntv_connectResponse",
                                   NULL);

        return -1;
    }

    // ------------------------------------------------------------------
    // Handle failure driving connect response
    // ------------------------------------------------------------------
    if (localRC != 0) {

        populateLComServiceResults(env,
                                   localRC,
                                   (localRSN & 0x0000FFFF),
                                   "lcom_connectResponse",
                                   -1,
                                   "ntv_connectResponse",
                                   NULL);

        return -1;
    }


    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(TP_NRH_CONRESP_EXIT),
                    "ntv_connectResponse exit",
                    TRACE_DATA_HEX_INT(localRC,
                                       "localRC"),
                    TRACE_DATA_HEX_INT(localRSN,
                                       "localRSN"),
                    TRACE_DATA_END_PARMS);
    }

    return 0;
}


/**
 * Connect to the clients local comm related shared memory segments.
 */
JNIEXPORT jint JNICALL
ntv_connectToClientsSharedMemory(JNIEnv* env, jclass jobj, jbyteArray clientConnHandleBytes, jbyteArray inRequestSpecificParms) {
    unsigned long long bbgzldat_p;
    unsigned long long bbgzlocl_p;
    unsigned long long sharedMemoryUserToken;
    OpaqueClientConnectionHandle_t clientConnHandle;

    (*env)->GetByteArrayRegion(env, clientConnHandleBytes, 0, sizeof(clientConnHandle), (jbyte*) &clientConnHandle);
    (*env)->GetByteArrayRegion(env, inRequestSpecificParms, 0, sizeof(bbgzldat_p), (jbyte*) &bbgzldat_p);
    (*env)->GetByteArrayRegion(env, inRequestSpecificParms, 8, sizeof(bbgzlocl_p), (jbyte*) &bbgzlocl_p);
    (*env)->GetByteArrayRegion(env, inRequestSpecificParms, 16, sizeof(sharedMemoryUserToken), (jbyte*) &sharedMemoryUserToken);   //TODO: compute offsetof instead "8"

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(TP_NRH_CONCSHARED_ENTRY),
                    "ntv_connectToClientsSharedMemory entry",
                    TRACE_DATA_RAWDATA(sizeof(clientConnHandle),
                                       &clientConnHandle,
                                       "Connection handle"),
                    TRACE_DATA_HEX_LONG(bbgzldat_p,
                                        "bbgzldat_p       "),
                    TRACE_DATA_HEX_LONG(bbgzlocl_p,
                                        "bbgzlocl_p       "),
                    TRACE_DATA_HEX_LONG(sharedMemoryUserToken,
                                        "sharedMemoryUserToken"),
                    TRACE_DATA_END_PARMS);
    }

    // ------------------------------------------------------------------
    // Call into authorized code to connect to the clients shared memory
    // ------------------------------------------------------------------
    int localRC, localRSN;
    int pc_rc = -1;

    LCOM_ConnectClientSharedMemoryParms connSharedParms = {
        .inConnHandle = clientConnHandle,
        .inBBGZLDAT_p = bbgzldat_p,
        .inBBGZLOCL_p = bbgzlocl_p,
        .inSharingUserToken = sharedMemoryUserToken,
        .outRC = &localRC,
        .outRSN = &localRSN
    };

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(TP_NRH_CONCSHARED_AUTH_CALL),
                    "ntv_connectToClientsSharedMemory lcom_connectClientSharedMemory call",
                    TRACE_DATA_RAWDATA(sizeof(connSharedParms),
                                       &connSharedParms,
                                       "LCOM_ConnectClientSharedMemoryParms"),
                    TRACE_DATA_END_PARMS);
    }

    // Get the authorized server PC stub to be able to call authorized services.
    const server_authorized_function_stubs* auth_stubs_p = getServerAuthorizedFunctionStubs();

    pc_rc = auth_stubs_p->lcom_connectClientSharedMemory(&connSharedParms);

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(TP_NRH_CONCSHARED_AUTH_RETURN),
                    "ntv_connectToClientsSharedMemory lcom_connectClientSharedMemory return",
                    TRACE_DATA_INT(pc_rc, "pc_rc"),
                    TRACE_DATA_HEX_INT(localRC, "localRC"),
                    TRACE_DATA_HEX_INT(localRSN, "localRSN"),
                    TRACE_DATA_END_PARMS);
    }

    if (pc_rc != 0) {
        if (TraceActive(trc_level_exception)) {
            TraceRecord(trc_level_exception,
                        TP(TP_NRH_CONCSHARED_AUTH_BAD_PC_RC),
                        "ntv_connectToClientsSharedMemory Bad PC RC",
                        TRACE_DATA_INT(pc_rc, "Authorized PC Return Code"),
                        TRACE_DATA_END_PARMS);
        }

        populateLComServiceResults(env,
                                   0,
                                   0,
                                   "lcom_connectClientSharedMemory",
                                   pc_rc,
                                   "ntv_connectToClientsSharedMemory",
                                   NULL);

        return -1;
    }

    // ------------------------------------------------------------------
    // Handle failure driving connect to clients shared memory
    // ------------------------------------------------------------------
    if (localRC != 0) {

        populateLComServiceResults(env,
                                   localRC,
                                   (localRSN & 0x0000FFFF),
                                   "lcom_connectClientSharedMemory",
                                   -1,
                                   "ntv_connectToClientsSharedMemory",
                                   NULL);

        return -1;
    }


    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(TP_NRH_CONCSHARED_EXIT),
                    "ntv_connectToClientsSharedMemory exit",
                    TRACE_DATA_HEX_INT(localRC,
                                       "localRC"),
                    TRACE_DATA_HEX_INT(localRSN,
                                       "localRSN"),
                    TRACE_DATA_END_PARMS);
    }

    return 0;
}

/**
 * Disconnect from the clients local comm related shared memory segments.
 */
JNIEXPORT jint JNICALL
ntv_disconnectFromClientsSharedMemory(JNIEnv* env, jclass jobj, jbyteArray clientConnHandleBytes, jbyteArray inRequestSpecificParms) {
    unsigned long long bbgzldat_p;
    unsigned long long bbgzlocl_p;
    unsigned long long sharedMemoryUserToken;
    OpaqueClientConnectionHandle_t clientConnHandle;

    (*env)->GetByteArrayRegion(env, clientConnHandleBytes, 0, sizeof(clientConnHandle), (jbyte*) &clientConnHandle);
    (*env)->GetByteArrayRegion(env, inRequestSpecificParms, 0, sizeof(bbgzldat_p), (jbyte*) &bbgzldat_p);
    (*env)->GetByteArrayRegion(env, inRequestSpecificParms, 8, sizeof(bbgzlocl_p), (jbyte*) &bbgzlocl_p);
    (*env)->GetByteArrayRegion(env, inRequestSpecificParms, 16, sizeof(sharedMemoryUserToken), (jbyte*) &sharedMemoryUserToken);   //TODO: compute offsetof instead "8"

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(TP_NRH_DISCONCSHARED_ENTRY),
                    "ntv_disconnectFromClientsSharedMemory entry",
                    TRACE_DATA_RAWDATA(sizeof(clientConnHandle),
                                       &clientConnHandle,
                                       "Connection handle"),
                    TRACE_DATA_HEX_LONG(bbgzldat_p,
                                        "bbgzldat_p       "),
                    TRACE_DATA_HEX_LONG(bbgzlocl_p,
                                        "bbgzlocl_p       "),
                    TRACE_DATA_HEX_LONG(sharedMemoryUserToken,
                                        "sharedMemoryUserToken"),
                    TRACE_DATA_END_PARMS);
    }

    // --------------------------------------------------------------------
    // Call into authorized code to disconnect to the clients shared memory
    // --------------------------------------------------------------------
    int localRC, localRSN;
    int pc_rc = -1;

    LCOM_DisconnectClientSharedMemoryParms disconnSharedParms = {
        .inConnHandle = clientConnHandle,
        .inBBGZLDAT_p = bbgzldat_p,
        .inBBGZLOCL_p = bbgzlocl_p,
        .inSharingUserToken = sharedMemoryUserToken,
        .outRC = &localRC,
        .outRSN = &localRSN
    };

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(TP_NRH_DISCONCSHARED_AUTH_CALL),
                    "ntv_disconnectFromClientsSharedMemory lcom_disconnectClientSharedMemory call",
                    TRACE_DATA_RAWDATA(sizeof(disconnSharedParms),
                                       &disconnSharedParms,
                                       "LCOM_DisconnectClientSharedMemoryParms"),
                    TRACE_DATA_END_PARMS);
    }

    // Get the authorized server PC stub to be able to call authorized services.
    const server_authorized_function_stubs* auth_stubs_p = getServerAuthorizedFunctionStubs();

    pc_rc = auth_stubs_p->lcom_disconnectClientSharedMemory(&disconnSharedParms);

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(TP_NRH_DISCONCSHARED_AUTH_RETURN),
                    "ntv_disconnectFromClientsSharedMemory lcom_disconnectClientSharedMemory return",
                    TRACE_DATA_INT(pc_rc, "pc_rc"),
                    TRACE_DATA_HEX_INT(localRC, "localRC"),
                    TRACE_DATA_HEX_INT(localRSN, "localRSN"),
                    TRACE_DATA_END_PARMS);
    }

    if (pc_rc != 0) {
        if (TraceActive(trc_level_exception)) {
            TraceRecord(trc_level_exception,
                        TP(TP_NRH_DISCONCSHARED_AUTH_BAD_PC_RC),
                        "ntv_disconnectFromClientsSharedMemory Bad PC RC",
                        TRACE_DATA_INT(pc_rc, "Authorized PC Return Code"),
                        TRACE_DATA_END_PARMS);
        }

        populateLComServiceResults(env,
                                   0,
                                   0,
                                   "lcom_disconnectClientSharedMemory",
                                   pc_rc,
                                   "ntv_disconnectFromClientsSharedMemory",
                                   NULL);

        return -1;
    }

    // ------------------------------------------------------------------
    // Handle failure driving disconnect to clients shared memory
    // ------------------------------------------------------------------
    if (localRC != 0) {

        populateLComServiceResults(env,
                                   localRC,
                                   (localRSN & 0x0000FFFF),
                                   "lcom_disconnectClientSharedMemory",
                                   -1,
                                   "ntv_disconnectFromClientsSharedMemory",
                                   NULL);

        return -1;
    }


    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(TP_NRH_DISCONCSHARED_EXIT),
                    "ntv_disconnectFromClientsSharedMemory exit",
                    TRACE_DATA_HEX_INT(localRC,
                                       "localRC"),
                    TRACE_DATA_HEX_INT(localRSN,
                                       "localRSN"),
                    TRACE_DATA_END_PARMS);
    }

    return 0;
}

/**
 * Perform a server-side read request.
 *
 * Call over to the authorized code to obtain the length of available data to read.   If none, then the
 * "read pending" flag was turned on and we should return that this read went async.
 *
 * Otherwise, an LMSG was popped off and returned.
 *     -Allocate a byteBuffer and copy the LMSG data into it for the return parameter.  Call back to
 *     the authorized code to release the LMSG.
 *
 * @param connHandle Local Comm Connection Handle reference (BBGZHLDL) identifying the connection.
 * @param requestServiceResultByteArray Byte[] containing return code information if needed.
 * @return Byte[] containing the inbound data or NULL.  If NULL, the return code information will be
 * updated in the requestServiceResultByteArray.
 *

TODO:
It is understood that at some point in the future we will change the
code to pass the LMSG back up to java and wrap it in a direct byte
buffer such that the channel is accessing the LMSG directly.  We will
need a way to release the direct byte buffer so that it can be
returned to the LDAT pool.  We agreed that WsByteBuffer is not the
correct interface to use for this, we should have a new interface that
allows us to wrap an arbitrary storage address which also includes a
release method which we can implement to return the LMSG buffer to the
appropriate LDAT pool.

TODO: Also, we may change to pass back a list of LMSG's to be wrapped and presented as the "read" data.
For now we are dequeuing 1 data queue element and returning the LMSG from it.
 *
 */
JNIEXPORT jbyteArray JNICALL
ntv_read(JNIEnv* env, jclass jobj, jbyteArray clientConnHandleBytes, jboolean forceAsync, jbyteArray requestServiceResultByteArray) {
    jbyteArray returnData = NULL;

    OpaqueClientConnectionHandle_t clientConnHandle;

    (*env)->GetByteArrayRegion(env, clientConnHandleBytes, 0, sizeof(clientConnHandle), (jbyte*) &clientConnHandle);

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(TP_NRH_READ_ENTRY),
                    "ntv_read entry",
                    TRACE_DATA_RAWDATA(sizeof(clientConnHandle),
                                       &clientConnHandle,
                                       "Connection handle"),
                    TRACE_DATA_END_PARMS);
    }

    // ------------------------------------------------------------------
    // Call into authorized code to get some data.
    // ------------------------------------------------------------------
    int localRC, localRSN;
    int pc_rc = -1;
    LCOM_AvailableDataVector* dataVector_p = NULL;

    LCOM_ReadParms readParms = {
        .inConnHandle         = clientConnHandle,
        .inForceAsync         = forceAsync,
        .outDataVector_p      = &dataVector_p,
        .outRC                = &localRC,
        .outRSN               = &localRSN
    };

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(TP_NRH_READ_AUTH_CALL),
                    "ntv_read lcom_read call",
                    TRACE_DATA_RAWDATA(sizeof(readParms),
                                       &readParms,
                                       "LCOM_ReadParms"),
                    TRACE_DATA_END_PARMS);
    }

    // Get the authorized server PC stub to be able to call authorized services.
    const server_authorized_function_stubs* auth_stubs_p = getServerAuthorizedFunctionStubs();

    pc_rc = auth_stubs_p->lcom_read(&readParms);

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(TP_NRH_READ_AUTH_RETURN),
                    "ntv_read lcom_read return",
                    TRACE_DATA_INT(pc_rc, "pc_rc"),
                    TRACE_DATA_HEX_INT(localRC, "localRC"),
                    TRACE_DATA_HEX_INT(localRSN, "localRSN"),
                    TRACE_DATA_PTR(dataVector_p, "dataVector_p"),
                    TRACE_DATA_INT((dataVector_p != NULL) ? dataVector_p->blockCount : 0, "blockCount"),
                    TRACE_DATA_END_PARMS);
    }

    if (pc_rc != 0) {
        if (TraceActive(trc_level_exception)) {
            TraceRecord(trc_level_exception,
                        TP(TP_NRH_READ_AUTH_BAD_PC_RC),
                        "ntv_read Bad PC RC",
                        TRACE_DATA_INT(pc_rc, "Authorized PC Return Code"),
                        TRACE_DATA_END_PARMS);
        }

        // Error. PC invoke problem
        localRC  = -1;
        localRSN = LOCAL_COMM_READ_RC_PC_FAILED;

        (*env)->SetByteArrayRegion(env, requestServiceResultByteArray, 0, sizeof(int), (jbyte*)&localRC);
        (*env)->SetByteArrayRegion(env, requestServiceResultByteArray, (1*sizeof(int)), sizeof(localRSN), (jbyte*)&localRSN);
        (*env)->SetByteArrayRegion(env, requestServiceResultByteArray, (2*sizeof(int)), sizeof(pc_rc), (jbyte*)&pc_rc);
        return NULL;
    }

    // ------------------------------------------------------------------
    // Handle failure driving read
    // ------------------------------------------------------------------
    if (localRC != 0) {
        int errorOccurred = -1;
        (*env)->SetByteArrayRegion(env, requestServiceResultByteArray, 0, sizeof(int), (jbyte*)&errorOccurred);
        (*env)->SetByteArrayRegion(env, requestServiceResultByteArray, (1*sizeof(int)), sizeof(localRC), (jbyte*)&localRC);
        (*env)->SetByteArrayRegion(env, requestServiceResultByteArray, (2*sizeof(int)), sizeof(localRSN), (jbyte*)&localRSN);
        return NULL;
    }

    /**
    * Read successful.  Extract information from the Message element
    */
    if ((dataVector_p != NULL) && (dataVector_p->blockCount > 0)) {
        unsigned int dataSizeInt = dataVector_p->totalDataSize;  //TODO:  Long to Int assignment.
        returnData = (*env)->NewByteArray(env, dataSizeInt);

        if (returnData == NULL) {
            // Error. NO_MEMORY
            int errorOccurred = -1;
            localRC = LOCAL_COMM_READ_RC_NO_MEMORY;
            (*env)->SetByteArrayRegion(env, requestServiceResultByteArray, 0, sizeof(int), (jbyte*)&errorOccurred);
            (*env)->SetByteArrayRegion(env, requestServiceResultByteArray, (1*sizeof(int)), sizeof(localRC), (jbyte*)&localRC);
            (*env)->SetByteArrayRegion(env, requestServiceResultByteArray, (2*sizeof(int)), sizeof(dataSizeInt), (jbyte*)&dataSizeInt);
            return NULL;
        } else {
            int currentByte = 0;
            for (int x = 0; x < dataVector_p->blockCount; x++) {
                LCOM_ReadDataBlock* currentBlock_p = ((LCOM_ReadDataBlock*)(dataVector_p + 1)) + x;
                (*env)->SetByteArrayRegion(env, returnData, currentByte,
                                           currentBlock_p->dataSize, (jbyte*)(currentBlock_p->data_p));
                currentByte += currentBlock_p->dataSize;
            }
        }

        //TODO: Release the LMSG here for now.  We made a copy to return instead.
        localRSN = releaseDataMessage(&clientConnHandle, dataVector_p);
        if (localRSN != 0) {
            int errorOccurred = -1;
            localRC = LOCAL_COMM_READ_RC_RELMSG_FAILED;
            (*env)->SetByteArrayRegion(env, requestServiceResultByteArray, 0, sizeof(int), (jbyte*)&errorOccurred);
            (*env)->SetByteArrayRegion(env, requestServiceResultByteArray, (1*sizeof(int)), sizeof(localRC), (jbyte*)&localRC);
            (*env)->SetByteArrayRegion(env, requestServiceResultByteArray, (2*sizeof(int)), sizeof(localRSN), (jbyte*)&localRSN);
            return NULL;
        }
    } else {
        // Read went async.  RC == 0 with no Data queue element.
        // Note: we don't need to set a 0 return code into the requestServiceResult.  It's the callers
        // responsibility to initialized it prior to this call to help avoid unnecessary sets.
    }

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(TP_NRH_READ_EXIT),
                    "ntv_read exit",
                    TRACE_DATA_HEX_INT(((returnData != NULL)? ((*env)->GetArrayLength(env, returnData)): 0),
                                       "returnData length"),
                    TRACE_DATA_END_PARMS);
    }

    return returnData;
}

/**
 * Drive authorized routine to release the message data from a read (ie. the LMSG).
 *
 * @param bbgzlmsg_p Pointer to the BBGZLMSG to free
 *
 * @return Non-zero return code if something bad occurred, otherwise 0.
 */
static int releaseDataMessage(OpaqueClientConnectionHandle_t* clientConnHandle_p, LCOM_AvailableDataVector* dataVector_p) {
    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(TP_NRH_RELLMSG_ENTRY),
                    "releaseDataMessage entry",
                    TRACE_DATA_PTR(dataVector_p,
                                   "dataVector_p"),
                    TRACE_DATA_RAWDATA(sizeof(*clientConnHandle_p),
                                       clientConnHandle_p,
                                   "clientConnHandle_p"),
                    TRACE_DATA_END_PARMS);
    }

    // ------------------------------------------------------------------
    // Call into authorized code to free an LMSG.
    // ------------------------------------------------------------------
    int localRC, localRSN;
    int pc_rc = -1;


    LCOM_ReleaseDataMessageParms releaseDataMessageParms = {
        .inConnHandle         = *clientConnHandle_p,
        .inDataVector_p       = dataVector_p,
        .outRC                = &localRC,
        .outRSN               = &localRSN
    };

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(TP_NRH_RELLMSG_AUTH_CALL),
                    "releaseDataMessage lcom_releaseDataMessage call",
                    TRACE_DATA_RAWDATA(sizeof(releaseDataMessageParms),
                                       &releaseDataMessageParms,
                                       "LCOM_ReleaseDataMessageParms"),
                    TRACE_DATA_END_PARMS);
    }

    // Get the authorized server PC stub to be able to call authorized services.
    const server_authorized_function_stubs* auth_stubs_p = getServerAuthorizedFunctionStubs();

    pc_rc = auth_stubs_p->lcom_releaseDataMessage(&releaseDataMessageParms);

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(TP_NRH_RELLMSG_AUTH_RETURN),
                    "releaseDataMessage lcom_releaseDataMessage return",
                    TRACE_DATA_INT(pc_rc, "pc_rc"),
                    TRACE_DATA_HEX_INT(localRC, "localRC"),
                    TRACE_DATA_HEX_INT(localRSN, "localRSN"),
                    TRACE_DATA_END_PARMS);
    }

    if (pc_rc != 0) {
        if (TraceActive(trc_level_exception)) {
            TraceRecord(trc_level_exception,
                        TP(TP_NRH_RELLMSG_AUTH_BAD_PC_RC),
                        "releaseDataMessage Bad PC RC",
                        TRACE_DATA_INT(pc_rc, "Authorized PC Return Code"),
                        TRACE_DATA_END_PARMS);
        }

        // Error. PC invoke problem
        return LOCAL_COMM_RELLMSG_RC_PC_FAILED;
    }

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(TP_NRH_RELLMSG_EXIT),
                    "releaseDataMessage exit",
                    TRACE_DATA_HEX_INT(localRC,
                                       "localRC"),
                    TRACE_DATA_END_PARMS);
    }
    return localRSN;
}   // end, releaseDataMessage


/**
 * Perform a server-side read request.
 *
 * Call over to the authorized code to obtain the length of available data to read.   If none, then the
 * "read pending" flag was turned on and we should return that this read went async.
 *
 * Otherwise, an LDAT was popped off and returned.
 *     -Allocate a byteBuffer and copy the LMSG data into it for the return parameter.  Call back to
 *     the authorized code to release the LMSG.
 *
 * @param connHandle Local Comm Connection Handle reference (BBGZHLDL) identifying the connection.
 * @param dataArray Byte[] containing data to send.
 * @param requestServiceResultByteArray Byte[] containing return code information if needed.
 * @return 0 if successful, -1 on failure.
 */
JNIEXPORT jint JNICALL
ntv_write(JNIEnv* env, jclass jobj, jbyteArray clientConnHandleBytes, jbyteArray dataArray, jbyteArray requestServiceResultByteArray) {
    jbyteArray returnData = NULL;

    OpaqueClientConnectionHandle_t clientConnHandle;

    (*env)->GetByteArrayRegion(env, clientConnHandleBytes, 0, sizeof(clientConnHandle), (jbyte*) &clientConnHandle);

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(TP_NRH_WRITE_ENTRY),
                    "ntv_write entry",
                    TRACE_DATA_RAWDATA(sizeof(clientConnHandle),
                                       &clientConnHandle,
                                       "Connection handle"),
                    TRACE_DATA_END_PARMS);
    }

    int localRC = 0, localRSN = 0;
    int pc_rc = -1;

    jbyte*             data_p = NULL;
    unsigned long long dataSize = 0;

    // Get length of data to send.
    dataSize = (*env)->GetArrayLength(env, dataArray);
    if (dataSize <= 0) {
        int errorOccurred = -1;
        localRC = LOCAL_COMM_WRITE_RC_NODATA;
        (*env)->SetByteArrayRegion(env, requestServiceResultByteArray, 0, sizeof(int), (jbyte*)&errorOccurred);
        (*env)->SetByteArrayRegion(env, requestServiceResultByteArray, (1*sizeof(int)), sizeof(localRC), (jbyte*)&localRC);
        (*env)->SetByteArrayRegion(env, requestServiceResultByteArray, (2*sizeof(int)), sizeof(localRSN), (jbyte*)&localRSN);
        return errorOccurred;
    }
    data_p = (*env)->GetByteArrayElements(env, dataArray, 0);

    // ------------------------------------------------------------------
    // Call into authorized code to send the data.
    //TODO: Perhaps in future we could implement a List or IOV.
    // ------------------------------------------------------------------
    LCOM_WriteParms writeParms = {
        .inConnHandle         = clientConnHandle,
        .inData_p             = (char*)data_p,
        .inDataSize           = dataSize,
        .outRC                = &localRC,
        .outRSN               = &localRSN
    };

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(TP_NRH_WRITE_AUTH_CALL),
                    "ntv_write lcom_write call",
                    TRACE_DATA_RAWDATA(sizeof(writeParms),
                                       &writeParms,
                                       "LCOM_WriteParms"),
                    TRACE_DATA_END_PARMS);
    }

    // Get the authorized server PC stub to be able to call authorized services.
    const server_authorized_function_stubs* auth_stubs_p = getServerAuthorizedFunctionStubs();

    pc_rc = auth_stubs_p->lcom_write(&writeParms);

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(TP_NRH_WRITE_AUTH_RETURN),
                    "ntv_write lcom_write return",
                    TRACE_DATA_INT(pc_rc, "pc_rc"),
                    TRACE_DATA_HEX_INT(localRC, "localRC"),
                    TRACE_DATA_HEX_INT(localRSN, "localRSN"),
                    TRACE_DATA_PTR(data_p, "data_p"),
                    TRACE_DATA_HEX_LONG(dataSize, "dataSize"),
                    TRACE_DATA_END_PARMS);
    }

    // Cleanup reference to sent bytes
    if (data_p != NULL)
      (*env)->ReleaseByteArrayElements(env, dataArray, data_p, JNI_ABORT);


    if (pc_rc != 0) {
        if (TraceActive(trc_level_exception)) {
            TraceRecord(trc_level_exception,
                        TP(TP_NRH_WRITE_AUTH_BAD_PC_RC),
                        "ntv_write Bad PC RC",
                        TRACE_DATA_INT(pc_rc, "Authorized PC Return Code"),
                        TRACE_DATA_END_PARMS);
        }

        // Error. PC invoke problem
        localRC  = -1;
        localRSN = LOCAL_COMM_WRITE_RC_PC_FAILED;

        (*env)->SetByteArrayRegion(env, requestServiceResultByteArray, 0, sizeof(int), (jbyte*)&localRC);
        (*env)->SetByteArrayRegion(env, requestServiceResultByteArray, (1*sizeof(int)), sizeof(localRSN), (jbyte*)&localRSN);
        (*env)->SetByteArrayRegion(env, requestServiceResultByteArray, (2*sizeof(int)), sizeof(pc_rc), (jbyte*)&pc_rc);
        return localRC;
    }

    // ------------------------------------------------------------------
    // Handle failure driving send to client
    // ------------------------------------------------------------------
    if (localRC != 0) {
        int errorOccurred = -1;
        (*env)->SetByteArrayRegion(env, requestServiceResultByteArray, 0, sizeof(int), (jbyte*)&errorOccurred);
        (*env)->SetByteArrayRegion(env, requestServiceResultByteArray, (1*sizeof(int)), sizeof(localRC), (jbyte*)&localRC);
        (*env)->SetByteArrayRegion(env, requestServiceResultByteArray, (2*sizeof(int)), sizeof(localRSN), (jbyte*)&localRSN);
        return errorOccurred;
    }

    // Send was successful.
    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(TP_NRH_WRITE_EXIT),
                    "ntv_write exit",
                    TRACE_DATA_END_PARMS);
    }

    return 0;
}   // end, ntv_write

/**
 * Perform a server-side close request.
 *
 * Call over to the authorized code to drive close processing for a Connection.
 *
 * @param connHandle Local Comm Connection Handle reference (BBGZHLDL) identifying the connection.
 * @param requestServiceResultByteArray Byte[] containing return code information if needed.
 * @return 0 if successful, -1 on failure.
 */
JNIEXPORT jint JNICALL
ntv_close(JNIEnv* env, jclass jobj, jbyteArray clientConnHandleBytes, jbyteArray requestServiceResultByteArray) {

    OpaqueClientConnectionHandle_t clientConnHandle;

    (*env)->GetByteArrayRegion(env, clientConnHandleBytes, 0, sizeof(clientConnHandle), (jbyte*) &clientConnHandle);

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(TP_NRH_CLOSE_ENTRY),
                    "ntv_close entry",
                    TRACE_DATA_RAWDATA(sizeof(clientConnHandle),
                                       &clientConnHandle,
                                       "Connection handle"),
                    TRACE_DATA_END_PARMS);
    }

    int localRC = 0, localRSN = 0;
    int pc_rc = -1;


    // ------------------------------------------------------------------
    // Call into authorized code to close the connection.
    // ------------------------------------------------------------------
    LCOM_CloseParms closeParms = {
        .inConnHandle         = clientConnHandle,
        .outRC                = &localRC,
        .outRSN               = &localRSN
    };

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(TP_NRH_CLOSE_AUTH_CALL),
                    "ntv_close lcom_close call",
                    TRACE_DATA_RAWDATA(sizeof(closeParms),
                                       &closeParms,
                                       "LCOM_CloseParms"),
                    TRACE_DATA_END_PARMS);
    }

    // Get the authorized server PC stub to be able to call authorized services.
    const server_authorized_function_stubs* auth_stubs_p = getServerAuthorizedFunctionStubs();

    pc_rc = auth_stubs_p->lcom_close(&closeParms);

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(TP_NRH_CLOSE_AUTH_RETURN),
                    "ntv_close lcom_close return",
                    TRACE_DATA_INT(pc_rc, "pc_rc"),
                    TRACE_DATA_HEX_INT(localRC, "localRC"),
                    TRACE_DATA_HEX_INT(localRSN, "localRSN"),
                    TRACE_DATA_END_PARMS);
    }

    if (pc_rc != 0) {
        if (TraceActive(trc_level_exception)) {
            TraceRecord(trc_level_exception,
                        TP(TP_NRH_CLOSE_AUTH_BAD_PC_RC),
                        "ntv_close Bad PC RC",
                        TRACE_DATA_INT(pc_rc, "Authorized PC Return Code"),
                        TRACE_DATA_END_PARMS);
        }

        // Error. PC invoke problem
        localRC  = -1;
        localRSN = LOCAL_COMM_CLOSE_RC_PC_FAILED;

        (*env)->SetByteArrayRegion(env, requestServiceResultByteArray, 0, sizeof(int), (jbyte*)&localRC);
        (*env)->SetByteArrayRegion(env, requestServiceResultByteArray, (1*sizeof(int)), sizeof(localRSN), (jbyte*)&localRSN);
        (*env)->SetByteArrayRegion(env, requestServiceResultByteArray, (2*sizeof(int)), sizeof(pc_rc), (jbyte*)&pc_rc);
        return localRC;
    }

    // ------------------------------------------------------------------
    // Handle failure driving close
    // ------------------------------------------------------------------
    if (localRC != 0) {
        int errorOccurred = -1;
        (*env)->SetByteArrayRegion(env, requestServiceResultByteArray, 0, sizeof(int), (jbyte*)&errorOccurred);
        (*env)->SetByteArrayRegion(env, requestServiceResultByteArray, (1*sizeof(int)), sizeof(localRC), (jbyte*)&localRC);
        (*env)->SetByteArrayRegion(env, requestServiceResultByteArray, (2*sizeof(int)), sizeof(localRSN), (jbyte*)&localRSN);
        return errorOccurred;
    }

    // Close was successful.
    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(TP_NRH_CLOSE_EXIT),
                    "ntv_close exit",
                    TRACE_DATA_INT(localRC, "return code"),
                    TRACE_DATA_END_PARMS);
    }

    return localRC;
}   // end, ntv_close

/**
 * Reset the work request queue closure flags
 * @return 0 if successful, -1 on failure.
 */
JNIEXPORT jint JNICALL
ntv_initWRQFlags(JNIEnv* env, jclass jobj) {


    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(TP_NRH_INITWRQFLAGS_ENTRY),
                    "ntv_initWRQFlags entry",
                    TRACE_DATA_END_PARMS);
    }

    // ------------------------------------------------------------------
    // Call into authorized code to reset the flags
    // ------------------------------------------------------------------
    int localRC, localRSN;
    int pc_rc = -1;

    LCOM_InitWRQFlagsParms initWRQFlagsParms = {
        .outRC = &localRC,
        .outRSN = &localRSN
    };

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(TP_NRH_INITWRQFLAGS_CALL),
                    "ntv_initWRQFlags lcom_initWRQFlags call",
                    TRACE_DATA_RAWDATA(sizeof(initWRQFlagsParms),
                                       &initWRQFlagsParms,
                                       "LCOM_InitWRQFlagsParms"),
                    TRACE_DATA_END_PARMS);
    }

    // Get the authorized server PC stub to be able to call authorized services.
    const server_authorized_function_stubs* auth_stubs_p = getServerAuthorizedFunctionStubs();

    pc_rc = auth_stubs_p->lcom_initWRQFlags(&initWRQFlagsParms);

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(TP_NRH_INITWRQFLAGS_RETURN),
                    "ntv_initWRQFlags lcom_initWRQFlags return",
                    TRACE_DATA_INT(pc_rc, "pc_rc"),
                    TRACE_DATA_HEX_INT(localRC, "localRC"),
                    TRACE_DATA_HEX_INT(localRSN, "localRSN"),
                    TRACE_DATA_END_PARMS);
    }

    if (pc_rc != 0) {
        if (TraceActive(trc_level_exception)) {
            TraceRecord(trc_level_exception,
                        TP(TP_NRH_INITWRQFLAGS_BAD_PC_RC),
                        "ntv_initWRQFlags Bad PC RC",
                        TRACE_DATA_INT(pc_rc, "Authorized PC Return Code"),
                        TRACE_DATA_END_PARMS);
        }

        populateLComServiceResults(env,
                                   0,
                                   0,
                                   "lcom_initWRQFlags",
                                   pc_rc,
                                   "ntv_initWRQFlags",
                                   NULL);

        return -1;
    }

    // ------------------------------------------------------------------
    // Handle failure to initialize
    // ------------------------------------------------------------------
    if (localRC != 0) {

        populateLComServiceResults(env,
                                   localRC,
                                   (localRSN & 0x0000FFFF),
                                   "lcom_initWRQFlags",
                                   -1,
                                   "ntv_initWRQFlags",
                                   NULL);

        return -1;
    }


    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(TP_NRH_INITWRQFLAGS_EXIT),
                    "ntv_initWRQFlags exit",
                    TRACE_DATA_HEX_INT(localRC,
                                       "localRC"),
                    TRACE_DATA_HEX_INT(localRSN,
                                       "localRSN"),
                    TRACE_DATA_END_PARMS);
    }

    return 0;
}
