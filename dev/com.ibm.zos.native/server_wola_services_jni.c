/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2013
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */

/**
 * @file
 *
 * JNI assorted routines for WOLA support.
 */

#include <jni.h>

#include "include/bboaims.h"
#include "include/ras_tracing.h"
#include "include/server_jni_method_manager.h"
#include "include/server_local_comm_client.h"
#include "include/server_native_service_tracker.h"
#include "include/server_wola_services.h"
#include "include/server_wola_unauth_services.h"
#include "include/util_jni.h"
#include "include/util_registry.h"

#define RAS_MODULE_CONST  RAS_MODULE_SERVER_WOLA_SERVICES_JNI

/**
 * Post code to use when we're cancelling an OTMA reqeust.  The post code is delivered to the
 * ECB that we supplied on otma_send_receivex.  OTMA C/I uses post codes 0, 4, 8, 16 and 20
 * in decimal.
 */
#define OTMA_CANCEL_INTERNAL_POST_CODE 32

// --------------------------------------------------------------------
// WOLA JNI function declaration and export
// --------------------------------------------------------------------
#pragma export(ntv_attachToBboashr)
JNIEXPORT jint JNICALL
ntv_attachToBboashr(JNIEnv* env, jobject jobj, 
                    jbyteArray jwolaGroup, 
                    jbyteArray jregistryToken, 
                    jbyteArray jreturnCodeArea);

#pragma export(ntv_detachFromBboashr)
JNIEXPORT jint JNICALL
ntv_detachFromBboashr(JNIEnv* env, jobject jobj, jbyteArray jregistryToken);

#pragma export(ntv_advertiseWolaServer)
JNIEXPORT jint JNICALL
ntv_advertiseWolaServer(JNIEnv* env, jobject jobj, 
                        jbyteArray jwolaGroup, 
                        jbyteArray jwolaName2, 
                        jbyteArray jwolaName3, 
                        jbyteArray jloadModuleName, 
                        jbyteArray jregistryToken, 
                        jbyteArray jreturnCodeArea);

#pragma export(ntv_deadvertiseWolaServer)
JNIEXPORT jint JNICALL
ntv_deadvertiseWolaServer(JNIEnv* env, jobject jobj, 
                          jbyteArray jregistryToken, 
                          jbyteArray jreturnCodeArea);

#pragma export(ntv_activateWolaRegistration)
JNIEXPORT jint JNICALL
ntv_activateWolaRegistration(JNIEnv* env, jobject jobj, 
                             jbyteArray jwolaGroup, 
                             jbyteArray jwolaName2, 
                             jbyteArray jwolaName3, 
                             jboolean juseCicsTaskUserId,
                             jbyteArray jregistryToken, 
                             jbyteArray jreturnCodeArea);

#pragma export(ntv_deactivateWolaRegistration)
JNIEXPORT jint JNICALL
ntv_deactivateWolaRegistration(JNIEnv* env, jobject jobj, jbyteArray jregistryToken);

#pragma export(ntv_getClientService)
JNIEXPORT jint JNICALL
ntv_getClientService(JNIEnv* env, jobject jobj, 
                     jbyteArray jwolaGroup, 
                     jbyteArray jregisterName, 
                     jbyteArray jserviceName, 
                     jint jtimeout_s,
                     jbyteArray jclientConnHandle, 
                     jlong waiterToken,
                     jbyteArray jreturnCodeArea);

#pragma export(ntv_OpenOTMAConnection)
JNIEXPORT jint JNICALL
ntv_OpenOTMAConnection(JNIEnv* env, jobject jobj,
                       jbyteArray jGroupName,
                       jbyteArray jMemberName,
                       jbyteArray jPartnerName,
                       jbyteArray janchor,
                       jintArray jreturnCodeArea);

#pragma export(ntv_otmaSendReceive)
JNIEXPORT jint JNICALL
ntv_otmaSendReceive(JNIEnv* env, jobject jobj,
                    jbyteArray janchor,
                    jintArray jsendSegmentList,
                    jbyteArray jsendSegmentData,
                    jint jsendDataLength,
                    jint jsyncLevel,
                    jint jrecvSegmentNum,
                    jintArray jrecvSegmentList,
                    jbyteArray jrecvSegmentData,
                    jint jrecvDataLength,
                    jintArray jreturnReasonCodes,
                    jbyteArray jerrorMessage,
                    jobject jinterruptObjectBridge);

#pragma export(ntv_closeOtmaConnection)
JNIEXPORT jint JNICALL
ntv_closeOtmaConnection(JNIEnv* env, jobject jobj,
                        jbyteArray janchor,
                        jintArray jreturnCodeArea);

#pragma export (ntv_cancelOtmaRequest)
JNIEXPORT void JNICALL
ntv_cancelOtmaRequest(JNIEnv* env, jobject jobj,
                      jlong jotmaAnchor,
                      jlong jotmaSession,
                      jint jecbPtr);

#pragma export (ntv_cancelWolaClientWaiter)
JNIEXPORT void JNICALL
ntv_cancelWolaClientWaiter(JNIEnv* env, jobject obj,
                           jbyteArray jwolaGroup,
                           jbyteArray jregisterName,
                           jlong waiterToken);

/**
 * Registration callback used to resolve java object references.
 *
 * @param env The JNI environment for the calling thread.
 * @param myClazz The class for which deregistration is taking place.
 * @param extraInfo The context information from the caller.
 *
 * @return JNI_OK on success; JNI_ERR or on error
 */
int javaEnvironmentRegistrationWola(JNIEnv* env, jclass myClazz, jobjectArray extraInfo);

/**
 * Deregistration callback used java object reference cleanup.
 *
 * @param env The calling thread's JNI environment.
 * @param clazz The class for which deregistration is taking place.
 * @param extraInfo The context provided to the registration function.
 *
 * @return JNI_OK
 */
int javaEnvironmentDeregistrationWola(JNIEnv* env, jclass clazz, jobjectArray extraInfo);

// --------------------------------------------------------------------
// JNI native method structure
// --------------------------------------------------------------------
#pragma convert("ISO8859-1")
static const JNINativeMethod wolaNativeUtilsMethods[] = {
    { "ntv_attachToBboashr",
      "([B[B[B)I",
      (void *) ntv_attachToBboashr },
    { "ntv_detachFromBboashr",
      "([B)I",
      (void *) ntv_detachFromBboashr },
    { "ntv_advertiseWolaServer",
      "([B[B[B[B[B[B)I",
      (void *) ntv_advertiseWolaServer},
    { "ntv_deadvertiseWolaServer",
      "([B[B)I",
      (void *) ntv_deadvertiseWolaServer},
    { "ntv_activateWolaRegistration",
      "([B[B[BZ[B[B)I",
      (void *) ntv_activateWolaRegistration},
    { "ntv_deactivateWolaRegistration",
      "([B)I",
      (void *) ntv_deactivateWolaRegistration},
    { "ntv_getClientService",
      "([B[B[BI[BJ[B)I",
      (void *) ntv_getClientService},
    { "ntv_OpenOTMAConnection",
      "([B[B[B[B[I)I",
      (void *) ntv_OpenOTMAConnection},
    { "ntv_otmaSendReceive",
      "([B[I[BIII[I[BI[I[BLcom/ibm/ws/zos/channel/wola/WolaInterruptObjectBridge;)I",
      (void *) ntv_otmaSendReceive},
    { "ntv_closeOtmaConnection",
      "([B[I)I",
      (void *) ntv_closeOtmaConnection}
};

static const JNINativeMethod wolaInterruptObjectBridgeImplMethods[] = {
    { "ntv_cancelOtmaRequest",
      "(JJI)V",
      (void *) ntv_cancelOtmaRequest},
    { "ntv_cancelWolaClientWaiter",
      "([B[BJ)V",
      (void *) ntv_cancelWolaClientWaiter}
};
#pragma convert(pop)

jclass jwolaInterruptObjectBridgeIface = NULL;
jmethodID jregisterOtmaMethod = NULL;
jmethodID jderegisterMethod = NULL;
//---------------------------------------------------------------------
// NativeMethodDescriptor
//---------------------------------------------------------------------
#pragma export(zJNI_com_ibm_ws_zos_channel_wola_internal_natv_WOLANativeUtils)
NativeMethodDescriptor zJNI_com_ibm_ws_zos_channel_wola_internal_natv_WOLANativeUtils = {
    .registrationFunction = javaEnvironmentRegistrationWola,
    .deregistrationFunction = javaEnvironmentDeregistrationWola,
    .nativeMethodCount = sizeof(wolaNativeUtilsMethods) / sizeof(wolaNativeUtilsMethods[0]),
    .nativeMethods = wolaNativeUtilsMethods
};

#pragma export(zJNI_com_ibm_ws_zos_channel_wola_odi_WolaInterruptObjectBridgeImpl)
NativeMethodDescriptor zJNI_com_ibm_ws_zos_channel_wola_odi_WolaInterruptObjectBridgeImpl = {
    .registrationFunction = NULL,
    .deregistrationFunction = NULL,
    .nativeMethodCount = sizeof(wolaInterruptObjectBridgeImplMethods) / sizeof(wolaInterruptObjectBridgeImplMethods[0]),
    .nativeMethods = wolaInterruptObjectBridgeImplMethods
};

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
 * Registration callback used to resolve java object references.
 *
 * @param env The JNI environment for the calling thread.
 * @param myClazz The class for which deregistration is taking place.
 * @param extraInfo The context information from the caller.
 *
 * @return JNI_OK if successful completion. JNI_ERR otherwise.
 */
int javaEnvironmentRegistrationWola(JNIEnv* env, jclass myClazz, jobjectArray extraInfo) {

    if (TraceActive(trc_level_detailed)) {
    TraceRecord(trc_level_detailed,
                TP(4),
                "server_wola_services_jni.javaEnvironmentRegistration. Entry",
                TRACE_DATA_END_PARMS);
    }

#pragma convert("iso8859-1")
    // Class and Interface names.
    const char* wolaInterruptObjectBridgeIfaceName = "com.ibm.ws.zos.channel.wola.WolaInterruptObjectBridge";

    // Methods
    const char* registerOtmaMethodName = "registerOtma";
    const char* registerOtmaMethodDesc = "(JJI)Ljava/lang/Object;";
    const char* deregisterMethodName = "deregister";
    const char* deregisterMethodDesc = "(Ljava/lang/Object;)V";

    // Classloader
    const char* findClassMethodName = "loadClass";
    const char* findClassMethodDesc = "(Ljava/lang/String;)Ljava/lang/Class;";
#pragma convert(pop)

    // Find the classes/interfaces and create global references.
    // These references will be deleted when javaEnvironmentDeregistration
    // is called.
    //
    // The classloader used to load the WOLA bundle is passed down to
    // this method, and must be used to load classes because the classloader
    // which is currently on this thread is the classloader for the kernel
    // bundle.
    jobject wolaClassLoader = (*env)->GetObjectArrayElement(env, extraInfo, 0);
    if (wolaClassLoader == NULL) {
        if ((*env)->ExceptionCheck(env) == JNI_TRUE) {
            (*env)->ExceptionDescribe(env);
        }
        return JNI_ERR;
    }

    jclass wolaClassLoaderClass = (*env)->GetObjectClass(env, wolaClassLoader);
    if (wolaClassLoaderClass == NULL) {
        if ((*env)->ExceptionCheck(env) == JNI_TRUE) {
            (*env)->ExceptionDescribe(env);
        }
        return JNI_ERR;
    }

    jmethodID findClassMethod = (*env)->GetMethodID(env, wolaClassLoaderClass, findClassMethodName, findClassMethodDesc);
    if (findClassMethod == NULL) {
        if ((*env)->ExceptionCheck(env) == JNI_TRUE) {
            (*env)->ExceptionDescribe(env);
        }
        return JNI_ERR;
    }

    jwolaInterruptObjectBridgeIface = loadClassJNI(env, wolaClassLoader, findClassMethod, wolaInterruptObjectBridgeIfaceName);
    if (jwolaInterruptObjectBridgeIface == NULL) {
        if ((*env)->ExceptionCheck(env) == JNI_TRUE) {
            (*env)->ExceptionDescribe(env);
        }
        return JNI_ERR;
    }

    jregisterOtmaMethod = (*env)->GetMethodID(env, jwolaInterruptObjectBridgeIface, registerOtmaMethodName, registerOtmaMethodDesc);
    if (jregisterOtmaMethod == NULL) {
        if ((*env)->ExceptionCheck(env) == JNI_TRUE) {
            (*env)->ExceptionDescribe(env);
        }
        return JNI_ERR;
    }

    jderegisterMethod = (*env)->GetMethodID(env, jwolaInterruptObjectBridgeIface, deregisterMethodName, deregisterMethodDesc);
    if (jderegisterMethod == NULL) {
        if ((*env)->ExceptionCheck(env) == JNI_TRUE) {
            (*env)->ExceptionDescribe(env);
        }
        return JNI_ERR;
    }

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(5),
                    "server_wola_services_jni.javaEnvironmentRegistration. Exit",
                    TRACE_DATA_END_PARMS);
    }

    return JNI_OK;
}

int javaEnvironmentDeregistrationWola(JNIEnv* env, jclass clazz, jobjectArray extraInfo) {

    if (TraceActive(trc_level_detailed)) {
    TraceRecord(trc_level_detailed,
                TP(6),
                "server_wola_services_jni.javaEnvironmentDeregistration. Entry",
                TRACE_DATA_END_PARMS);
    }

    // Delete all global references obtained previously.
    if (jwolaInterruptObjectBridgeIface != NULL) {
        (*env)->DeleteGlobalRef(env, jwolaInterruptObjectBridgeIface);
        jwolaInterruptObjectBridgeIface = NULL;
    }

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(7),
                    "server_wola_services_jni.javaEnvironmentDeregistration. Exit",
                    TRACE_DATA_END_PARMS);
    }

    return JNI_OK;
}

/**
 * JNI routine to attach to the wola group shared memory area (anchored by BBOASHR).
 *
 * @param jwolaGroup - The WOLA group (byte[8], in EBCDIC)
 * @param jregistryToken - Output - A 64-byte area to contain the registry token.
 * @param jreturnCodeArea - Output - A 16-byte area to contain return codes, if there was an error.
 *
 * @return 0 if all is well; otherwise check jreturnCodeArea for return codes from the various
 *         authorized services that are invoked under this routine.
 */
JNIEXPORT jint JNICALL
ntv_attachToBboashr(JNIEnv* env, jobject jobj, jbyteArray jwolaGroup, jbyteArray jregistryToken, jbyteArray jreturnCodeArea) {
    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(1),
                    "ntv_attachToBboashr entry",
                    TRACE_DATA_END_PARMS);
    }

    jbyte* wola_group = NULL;
    jsize  wola_group_len = 0;
    jsize  registry_token_len = 0;

    int return_code = 0;
    int iean4rt_rc = 0;
    int iean4cr_rc = 0;
    int registry_rc = 0;
    int composite_rc = 0;

    pc_attachToBboashr_parms parms;
    memset(&parms, 0, sizeof(pc_attachToBboashr_parms));

    JNI_try {

        // Load the auth stubs
        const server_authorized_function_stubs* auth_stubs_p = getServerAuthorizedFunctionStubs();
        if (auth_stubs_p == NULL) {
            JNI_throwNullPointerException(env, "auth_stubs_p is null. Failed to load authorized PC routines");
        }

        // Pull parm data from Java
        JNI_GetByteArrayElements(wola_group,    env, jwolaGroup,    "jwolaGroup is null", NULL);
        JNI_GetArrayLength(wola_group_len,      env, jwolaGroup,    "jwolaGroup is null", 0);
        JNI_GetArrayLength(registry_token_len,  env, jregistryToken, "jregistryToken is null", 0);

        if (TraceActive(trc_level_detailed)) {
             TraceRecord(trc_level_detailed,
                         TP(3),
                         "JNI parms",
                         TRACE_DATA_RAWDATA(wola_group_len, wola_group, "wola_group"),
                         TRACE_DATA_INT(registry_token_len, "registry_token_len"),
                         TRACE_DATA_END_PARMS);
        }

        // Validate parms
        if (registry_token_len != sizeof(RegistryToken)) {
            JNI_throwIllegalArgumentException(env, "registry_token must be 64 bytes long");
        } else if (wola_group_len != 8) {
            JNI_throwIllegalArgumentException(env, "wola_group must be 8 bytes long");
        }

        // Setup the PC routine parms
        memcpy(&parms.wola_group, wola_group, 8);
        parms.registry_token_p = &parms.registry_token;
        parms.return_code_p = &return_code;
        parms.iean4rt_rc_p = &iean4rt_rc;
        parms.iean4cr_rc_p = &iean4cr_rc;
        parms.registry_rc_p = &registry_rc;

        // Invoke the PC routine
        int pc_rc = auth_stubs_p->pc_attachToBboashr(&parms);
        if (pc_rc != 0) {
            JNI_throwPCRoutineFailedException(env, "pc_attachToBboashr", pc_rc);
        }

        composite_rc = return_code | iean4rt_rc | iean4cr_rc | registry_rc;

        // Copy the retrieved user token into Java byte[] output parm
        if (composite_rc == 0) {
            JNI_SetByteArrayRegion(env, jregistryToken, 0, sizeof(RegistryToken), (jbyte*) &parms.registry_token);
        } else {
            // Copy back the return codes.
            int return_code_area_len;
            JNI_GetArrayLength(return_code_area_len,  env, jreturnCodeArea, "jreturnCodeArea is null", 0);

            if (return_code_area_len < 16) {
                JNI_throwIllegalArgumentException(env, "jreturnCodeArea must be 16 bytes long");
            }

            JNI_SetByteArrayRegion(env, jreturnCodeArea, 0, sizeof(int), (jbyte*) &return_code);            // return_code in first 4 bytes
            JNI_SetByteArrayRegion(env, jreturnCodeArea, sizeof(int), sizeof(int), (jbyte*) &iean4rt_rc);   // iean4rt_rc in second 4 bytes
            JNI_SetByteArrayRegion(env, jreturnCodeArea, sizeof(int)*2, sizeof(int), (jbyte*) &iean4cr_rc); // iean4cr_rc in third 4 bytes
            JNI_SetByteArrayRegion(env, jreturnCodeArea, sizeof(int)*3, sizeof(int), (jbyte*) &registry_rc); // registry_rc in fourth 4 bytes
        }
    }
    JNI_catch(env);

    // Release Java parms
    JNI_ReleaseByteArrayElements(env, jwolaGroup,  wola_group, NULL);

    // If there's a pending Java exception, it will be raised here and control
    // will immediately return to the Java caller.
    JNI_reThrowAndReturn(env);

    if (TraceActive(trc_level_detailed)) {
      TraceRecord(trc_level_detailed,
                  TP(2),
                  "ntv_attachToBboashr exit",
                  TRACE_DATA_INT(return_code,"return_code"),
                  TRACE_DATA_INT(iean4rt_rc,"iean4rt_rc"),
                  TRACE_DATA_INT(iean4cr_rc,"iean4cr_rc"),
                  TRACE_DATA_INT(registry_rc,"registry_rc"),
                  TRACE_DATA_INT(composite_rc,"composite_rc"),
                  TRACE_DATA_RAWDATA(sizeof(RegistryToken), &parms.registry_token, "registry token"),
                  TRACE_DATA_END_PARMS);
    }

    return (jint) composite_rc;
}

/**
 * @param jregistryToken - The registry token previously obtained from a call to ntv_attachToBboashr.
 *
 * @return 0 if all is well; otherwise a non-zero return code from util_registry.
 */
JNIEXPORT jint JNICALL
ntv_detachFromBboashr(JNIEnv* env, jobject jobj, jbyteArray jregistryToken) {
    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(11),
                    "ntv_detachFromBboashr entry",
                    TRACE_DATA_END_PARMS);
    }

    jbyte* registry_token = NULL;
    jsize  registry_token_len = 0;

    int registry_rc = 0;

    pc_attachToBboashr_parms parms;
    memset(&parms, 0, sizeof(pc_attachToBboashr_parms));

    JNI_try {

        // Load the auth stubs
        const server_authorized_function_stubs* auth_stubs_p = getServerAuthorizedFunctionStubs();
        if (auth_stubs_p == NULL) {
            JNI_throwNullPointerException(env, "auth_stubs_p is null. Failed to load authorized PC routines");
        }

        // Pull parm data from Java
        JNI_GetByteArrayElements(registry_token,    env, jregistryToken,    "jregistryToken is null", NULL);
        JNI_GetArrayLength(registry_token_len,  env, jregistryToken, "jregistryToken is null", 0);

        if (TraceActive(trc_level_detailed)) {
             TraceRecord(trc_level_detailed,
                         TP(13),
                         "JNI parms",
                         TRACE_DATA_RAWDATA(registry_token_len, registry_token, "registry_token"),
                         TRACE_DATA_END_PARMS);
        }

        // Validate parms
        if (registry_token_len != sizeof(RegistryToken)) {
            JNI_throwIllegalArgumentException(env, "registry_token must be 64 bytes long");
        }

        // Setup the PC routine parms
        memcpy(&parms.registry_token, registry_token, sizeof(RegistryToken));
        parms.registry_rc_p = &registry_rc;

        // Invoke the PC routine
        int pc_rc = auth_stubs_p->pc_detachFromBboashr(&parms);
        if (pc_rc != 0) {
            JNI_throwPCRoutineFailedException(env, "pc_detachFromBboashr", pc_rc);
        }
    }
    JNI_catch(env);

    // Release Java parms
    JNI_ReleaseByteArrayElements(env, jregistryToken,  registry_token, NULL);

    // If there's a pending Java exception, it will be raised here and control
    // will immediately return to the Java caller.
    JNI_reThrowAndReturn(env);

    if (TraceActive(trc_level_detailed)) {
      TraceRecord(trc_level_detailed,
                  TP(12),
                  "ntv_detachFromBboashr exit",
                  TRACE_DATA_INT(registry_rc,"registry_rc"),
                  TRACE_DATA_END_PARMS);
    }

    return (jint) registry_rc;
}

/**
 * Advertise the server's presence to WOLA clients by obtaining an ENQ using the server's 
 * WOLA 3-part name.  The client will scan for the ENQ and obtain the server's STOKEN
 * from the ENQ.  With the STOKEN, the client can PC into authorized code in order to attach
 * to the server's WOLA shared memory area.
 *
 * @param jwolaGroup - byte[8] containing the WOLA group in EBCDIC
 * @param jwolaName2 - byte[8] containing the wola 2nd name in EBCDIC, blank-padded
 * @param jwolaName3 - byte[8] containing the wola 3rd name in EBCDIC, blank-padded
 * @param jregistryToken - output - A registry token that indirect refs the ENQ used to advertise the server's presence
 * @param jreturnCodeArea - output - byte[12] to contain RC info in the event of a failure
 * 
 * @return 0 if all is well; non-zero otherwise (check jreturnCodeArea for RCs).
 */
JNIEXPORT jint JNICALL
ntv_advertiseWolaServer(JNIEnv* env, jobject jobj, jbyteArray jwolaGroup, jbyteArray jwolaName2, jbyteArray jwolaName3, jbyteArray jloadModuleName, jbyteArray jregistryToken, jbyteArray jreturnCodeArea) {

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(21),
                    "ntv_advertiseWolaServer entry",
                    TRACE_DATA_END_PARMS);
    }

    jbyte* wola_group = NULL;
    jsize  wola_group_len = 0;
    jbyte* wola_name2 = NULL;
    jsize  wola_name2_len = 0;
    jbyte* wola_name3 = NULL;
    jsize  wola_name3_len = 0;
    jbyte* bboacall_module_name = NULL;
    jsize  bboacall_module_name_len = 0;
    jsize  registry_token_len = 0;

    int return_code = 0;
    int getIPT_TToken_rc = 0;
    int registry_rc = 0;
    int composite_rc = 0;
    int load_bboacall_rc = 0;

    pc_advertise_parms parms;
    memset(&parms, 0, sizeof(pc_advertise_parms));

    JNI_try {

        // Load the auth stubs
        const server_authorized_function_stubs* auth_stubs_p = getServerAuthorizedFunctionStubs();
        if (auth_stubs_p == NULL) {
            JNI_throwNullPointerException(env, "auth_stubs_p is null. Failed to load authorized PC routines");
        }

        // Pull parm data from Java
        JNI_GetByteArrayElements(wola_group,    env, jwolaGroup,    "jwolaGroup is null", NULL);
        JNI_GetArrayLength(wola_group_len,      env, jwolaGroup,    "jwolaGroup is null", 0);

        JNI_GetByteArrayElements(wola_name2,    env, jwolaName2,    "jwolaName2 is null", NULL);
        JNI_GetArrayLength(wola_name2_len,      env, jwolaName2,    "jwolaName2 is null", 0);

        JNI_GetByteArrayElements(wola_name3,    env, jwolaName3,    "jwolaName3 is null", NULL);
        JNI_GetArrayLength(wola_name3_len,      env, jwolaName3,    "jwolaName3 is null", 0);

        JNI_GetByteArrayElements(bboacall_module_name,    env, jloadModuleName,    "jloadModuleName is null", NULL);
        JNI_GetArrayLength(bboacall_module_name_len,      env, jloadModuleName,    "jloadModuleName is null", 0);


        JNI_GetArrayLength(registry_token_len,  env, jregistryToken, "jregistryToken is null", 0);

        if (TraceActive(trc_level_detailed)) {
             TraceRecord(trc_level_detailed,
                         TP(23),
                         "JNI parms",
                         TRACE_DATA_RAWDATA(wola_group_len, wola_group, "wola_group"),
                         TRACE_DATA_RAWDATA(wola_name2_len, wola_name2, "wola_name2"),
                         TRACE_DATA_RAWDATA(wola_name3_len, wola_name3, "wola_name3"),
                         TRACE_DATA_RAWDATA(bboacall_module_name_len, bboacall_module_name, "bboacall_module_name"),
                         TRACE_DATA_INT(registry_token_len, "registry_token_len"),
                         TRACE_DATA_END_PARMS);
        }

        // Validate parms
        if (registry_token_len != sizeof(RegistryToken)) {
            JNI_throwIllegalArgumentException(env, "registry_token must be 64 bytes long");
        } else if (wola_group_len != 8) {
            JNI_throwIllegalArgumentException(env, "wola_group must be 8 bytes long");
        } else if (wola_name2_len != 8) {
            JNI_throwIllegalArgumentException(env, "wola_name2 must be 8 bytes long");
        } else if (wola_name3_len != 8) {
            JNI_throwIllegalArgumentException(env, "wola_name3 must be 8 bytes long");
        }

        // Setup the PC routine parms
        memcpy(&parms.wola_group, wola_group, wola_group_len);
        memcpy(&parms.wola_name2, wola_name2, wola_name2_len);
        memcpy(&parms.wola_name3, wola_name3, wola_name3_len);
        memcpy(&parms.bboacall_module_name, bboacall_module_name, bboacall_module_name_len);

        parms.registry_token_p = &parms.registry_token;
        parms.return_code_p = &return_code;
        parms.getIPT_TToken_rc_p = &getIPT_TToken_rc;
        parms.registry_rc_p = &registry_rc;
        parms.load_bboacall_rc_p = &load_bboacall_rc;

        // Invoke the PC routine
        int pc_rc = auth_stubs_p->pc_advertiseWolaServer(&parms);
        if (pc_rc != 0) {
              JNI_throwPCRoutineFailedException(env, "pc_advertiseWolaServer", pc_rc);
        }
        composite_rc = return_code | getIPT_TToken_rc | registry_rc | load_bboacall_rc;

        // Copy the retrieved user token into Java byte[] output parm
        if (composite_rc == 0) {
            JNI_SetByteArrayRegion(env, jregistryToken, 0, sizeof(RegistryToken), (jbyte*) &parms.registry_token);
        } else {
            // Copy back the return codes.
            int return_code_area_len;
            JNI_GetArrayLength(return_code_area_len,  env, jreturnCodeArea, "jreturnCodeArea is null", 0);

            if (return_code_area_len < 16) {
                JNI_throwIllegalArgumentException(env, "jreturnCodeArea must be 16 bytes long");
            }

            JNI_SetByteArrayRegion(env, jreturnCodeArea, 0, sizeof(int), (jbyte*) &return_code);            // return_code in first 4 bytes
            JNI_SetByteArrayRegion(env, jreturnCodeArea, sizeof(int), sizeof(int), (jbyte*) &getIPT_TToken_rc);   // getIPT_TToken_rc in second 4 bytes
            JNI_SetByteArrayRegion(env, jreturnCodeArea, sizeof(int)*2, sizeof(int), (jbyte*) &registry_rc); // registry_rc in third 4 bytes
            JNI_SetByteArrayRegion(env, jreturnCodeArea, sizeof(int)*3, sizeof(int), (jbyte*) &load_bboacall_rc); // load_bboacall_rc in next 4 bytes
        }
    }
    JNI_catch(env);

    // Release Java parms
    JNI_ReleaseByteArrayElements(env, jwolaGroup,  wola_group, NULL);
    JNI_ReleaseByteArrayElements(env, jwolaName2,  wola_name2, NULL);
    JNI_ReleaseByteArrayElements(env, jwolaName3,  wola_name3, NULL);

    // If there's a pending Java exception, it will be raised here and control
    // will immediately return to the Java caller.
    JNI_reThrowAndReturn(env);

    if (TraceActive(trc_level_detailed)) {
      TraceRecord(trc_level_detailed,
                  TP(22),
                  "ntv_advertiseWolaServer exit",
                  TRACE_DATA_INT(return_code,"return_code"),
                  TRACE_DATA_INT(getIPT_TToken_rc,"getIPT_TToken_rc"),
                  TRACE_DATA_INT(registry_rc,"registry_rc"),
                  TRACE_DATA_INT(load_bboacall_rc,"load_bboacall_rc"),
                  TRACE_DATA_INT(composite_rc,"composite_rc"),
                  TRACE_DATA_RAWDATA(sizeof(RegistryToken), &parms.registry_token, "registry token"),
                  TRACE_DATA_END_PARMS);
    }

    return (jint) composite_rc;
}

/**
 * Deadvertise the presence of the WOLA server by release the ENQ we previoulsy obtained via
 * ntv_advertiseWolaServer.  The ENQ is tucked inside the given registry token.
 *
 * @param jregistryToken - The registry token that indirectly refers to the ENQ that we want to release
 * @param jreturnCodeArea - Output - a byte[12] to contain RCs, in the event of a failure.
 *
 * @return 0 if all's well; non-zero otherwise (check jreturnCodeArea for RCs).
 */
JNIEXPORT jint JNICALL
ntv_deadvertiseWolaServer(JNIEnv* env, jobject jobj, jbyteArray jregistryToken, jbyteArray jreturnCodeArea) {

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(31),
                    "ntv_deadvertiseWolaServer entry",
                    TRACE_DATA_END_PARMS);
    }

    jbyte* registry_token = NULL;
    jsize  registry_token_len = 0;

    int return_code = 0;
    int getIPT_TToken_rc = 0;
    int registry_rc = 0;
    int composite_rc = 0;

    pc_advertise_parms parms;
    memset(&parms, 0, sizeof(pc_advertise_parms));

    JNI_try {

        // Load the auth stubs
        const server_authorized_function_stubs* auth_stubs_p = getServerAuthorizedFunctionStubs();
        if (auth_stubs_p == NULL) {
            JNI_throwNullPointerException(env, "auth_stubs_p is null. Failed to load authorized PC routines");
        }

        // Pull parm data from Java
        JNI_GetByteArrayElements(registry_token,    env, jregistryToken,    "jregistryToken is null", NULL);
        JNI_GetArrayLength(registry_token_len,  env, jregistryToken, "jregistryToken is null", 0);

        if (TraceActive(trc_level_detailed)) {
             TraceRecord(trc_level_detailed,
                         TP(33),
                         "JNI parms",
                         TRACE_DATA_RAWDATA(registry_token_len, registry_token, "registry_token"),
                         TRACE_DATA_END_PARMS);
        }

        // Validate parms
        if (registry_token_len != sizeof(RegistryToken)) {
            JNI_throwIllegalArgumentException(env, "registry_token must be 64 bytes long");
        } 

        // Setup the PC routine parms
        memcpy(&parms.registry_token, registry_token, sizeof(RegistryToken));
        parms.return_code_p = &return_code;
        parms.getIPT_TToken_rc_p = &getIPT_TToken_rc;
        parms.registry_rc_p = &registry_rc;

        // Invoke the PC routine
        int pc_rc = auth_stubs_p->pc_deadvertiseWolaServer(&parms);
        if (pc_rc != 0) {
            JNI_throwPCRoutineFailedException(env, "pc_deadvertiseWolaServer", pc_rc);
        }

        composite_rc = getIPT_TToken_rc | registry_rc;

        // Copy the retrieved user token into Java byte[] output parm
        if (composite_rc != 0) {
            // Copy back the return codes.
            int return_code_area_len;
            JNI_GetArrayLength(return_code_area_len,  env, jreturnCodeArea, "jreturnCodeArea is null", 0);

            if (return_code_area_len < 12) {
                JNI_throwIllegalArgumentException(env, "jreturnCodeArea must be 12 bytes long");
            }

            JNI_SetByteArrayRegion(env, jreturnCodeArea, 0, sizeof(int), (jbyte*) &return_code);            // return_code in first 4 bytes
            JNI_SetByteArrayRegion(env, jreturnCodeArea, sizeof(int), sizeof(int), (jbyte*) &getIPT_TToken_rc);   // getIPT_TToken_rc in second 4 bytes
            JNI_SetByteArrayRegion(env, jreturnCodeArea, sizeof(int)*2, sizeof(int), (jbyte*) &registry_rc); // registry_rc in third 4 bytes
        }
    }
    JNI_catch(env);

    // Release Java parms
    JNI_ReleaseByteArrayElements(env, jregistryToken,  registry_token, NULL);

    // If there's a pending Java exception, it will be raised here and control
    // will immediately return to the Java caller.
    JNI_reThrowAndReturn(env);

    if (TraceActive(trc_level_detailed)) {
      TraceRecord(trc_level_detailed,
                  TP(32),
                  "ntv_deadvertiseWolaServer exit",
                  TRACE_DATA_INT(return_code,"return_code"),
                  TRACE_DATA_INT(getIPT_TToken_rc,"getIPT_TToken_rc"),
                  TRACE_DATA_INT(registry_rc,"registry_rc"),
                  TRACE_DATA_INT(composite_rc,"composite_rc"),
                  TRACE_DATA_END_PARMS);
    }

    return (jint) composite_rc;
}

/**
 * Activate the WOLA registration (BBOARGE) for the server identified by the given WOLA 3-part name.
 *
 * If a BBOARGE does not already exist for this server, it is created.
 *
 * @param jwolaGroup - byte[8] containing the wola group name in EBCDIC, blank-padded
 * @param jwolaName2 - byte[8] containing the wola 2nd name in EBCDIC, blank-padded
 * @param jwolaName3 - byte[8] containing the wola 3rd name in EBCDIC, blank-padded
 * @param juseCicsTaskUserId - boolean set to true if we'll allow CICS to pass us an alternate ACEE
 * @param jregistryToken - Output - byte[64] to contain the registry token for the BBOARGE pointer
 * @param jreturnCodeAarea - Output - byte[16] to contain RCs in the event of failure
 *
 * @return 0 if all is well; non-zero otherwise (check the jreturnCodeArea).
 */
JNIEXPORT jint JNICALL
ntv_activateWolaRegistration(JNIEnv* env, jobject jobj, jbyteArray jwolaGroup, jbyteArray jwolaName2, jbyteArray jwolaName3, jboolean juseCicsTaskUserId, jbyteArray jregistryToken, jbyteArray jreturnCodeArea) {

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(41),
                    "ntv_activateWolaRegistration entry",
                    TRACE_DATA_END_PARMS);
    }

    jbyte* wola_group = NULL;
    jsize  wola_group_len = 0;
    jbyte* wola_name2 = NULL;
    jsize  wola_name2_len = 0;
    jbyte* wola_name3 = NULL;
    jsize  wola_name3_len = 0;
    jsize  registry_token_len = 0;

    int return_code = 0;
    int registry_rc = 0;
    int iean4rt_rc = 0;
    int cell_pool_rc = 0;
    int composite_rc = 0;

    pc_activateWolaRegistration_parms parms;
    memset(&parms, 0, sizeof(pc_activateWolaRegistration_parms));

    JNI_try {

        // Load the auth stubs
        const server_authorized_function_stubs* auth_stubs_p = getServerAuthorizedFunctionStubs();
        if (auth_stubs_p == NULL) {
            JNI_throwNullPointerException(env, "auth_stubs_p is null. Failed to load authorized PC routines");
        }

        // Pull parm data from Java
        JNI_GetByteArrayElements(wola_group,    env, jwolaGroup,    "jwolaGroup is null", NULL);
        JNI_GetArrayLength(wola_group_len,      env, jwolaGroup,    "jwolaGroup is null", 0);

        JNI_GetByteArrayElements(wola_name2,    env, jwolaName2,    "jwolaName2 is null", NULL);
        JNI_GetArrayLength(wola_name2_len,      env, jwolaName2,    "jwolaName2 is null", 0);

        JNI_GetByteArrayElements(wola_name3,    env, jwolaName3,    "jwolaName3 is null", NULL);
        JNI_GetArrayLength(wola_name3_len,      env, jwolaName3,    "jwolaName3 is null", 0);

        JNI_GetArrayLength(registry_token_len,  env, jregistryToken, "jregistryToken is null", 0);

        if (TraceActive(trc_level_detailed)) {
             TraceRecord(trc_level_detailed,
                         TP(43),
                         "JNI parms",
                         TRACE_DATA_RAWDATA(wola_group_len, wola_group, "wola_group"),
                         TRACE_DATA_RAWDATA(wola_name2_len, wola_name2, "wola_name2"),
                         TRACE_DATA_RAWDATA(wola_name3_len, wola_name3, "wola_name3"),
                         TRACE_DATA_BOOLEAN(juseCicsTaskUserId, "Use CICS task user ID"),
                         TRACE_DATA_INT(registry_token_len, "registry_token_len"),
                         TRACE_DATA_END_PARMS);
        }

        // Validate parms
        if (registry_token_len != sizeof(RegistryToken)) {
            JNI_throwIllegalArgumentException(env, "registry_token must be 64 bytes long");
        } else if (wola_group_len != 8) {
            JNI_throwIllegalArgumentException(env, "wola_group must be 8 bytes long");
        } else if (wola_name2_len != 8) {
            JNI_throwIllegalArgumentException(env, "wola_name2 must be 8 bytes long");
        } else if (wola_name3_len != 8) {
            JNI_throwIllegalArgumentException(env, "wola_name3 must be 8 bytes long");
        }

        // Setup the PC routine parms
        memcpy(&parms.wola_group, wola_group, wola_group_len);
        memcpy(&parms.wola_name2, wola_name2, wola_name2_len);
        memcpy(&parms.wola_name3, wola_name3, wola_name3_len);

        parms.useCicsTaskUserId = juseCicsTaskUserId;
        parms.registry_token_p = &parms.registry_token;
        parms.return_code_p = &return_code;
        parms.iean4rt_rc_p = &iean4rt_rc;
        parms.cell_pool_rc_p = &cell_pool_rc;
        parms.registry_rc_p = &registry_rc;

        // Invoke the PC routine
        int pc_rc = auth_stubs_p->pc_activateWolaRegistration(&parms);
        if (pc_rc != 0) {
            JNI_throwPCRoutineFailedException(env, "pc_activateWolaRegistration", pc_rc);
        }

        composite_rc = return_code | iean4rt_rc | cell_pool_rc | registry_rc;

        // Copy the retrieved user token into Java byte[] output parm
        if (composite_rc == 0) {
            JNI_SetByteArrayRegion(env, jregistryToken, 0, sizeof(RegistryToken), (jbyte*) &parms.registry_token);
        } else {
            // Copy back the return codes.
            int return_code_area_len;
            JNI_GetArrayLength(return_code_area_len,  env, jreturnCodeArea, "jreturnCodeArea is null", 0);

            if (return_code_area_len < 16) {
                JNI_throwIllegalArgumentException(env, "jreturnCodeArea must be 16 bytes long"); 
            }

            JNI_SetByteArrayRegion(env, jreturnCodeArea, 0, sizeof(int), (jbyte*) &return_code);                // return_code in first 4 bytes
            JNI_SetByteArrayRegion(env, jreturnCodeArea, sizeof(int), sizeof(int), (jbyte*) &iean4rt_rc);       // iean4rt in second 4 bytes
            JNI_SetByteArrayRegion(env, jreturnCodeArea, 2*sizeof(int), sizeof(int), (jbyte*) &cell_pool_rc);   // cell_pool_rc in third 4 bytes
            JNI_SetByteArrayRegion(env, jreturnCodeArea, 3*sizeof(int), sizeof(int), (jbyte*) &registry_rc);    // registry_rc in fourth 4 bytes
        }
    }
    JNI_catch(env);

    // Release Java parms
    JNI_ReleaseByteArrayElements(env, jwolaGroup,  wola_group, NULL);
    JNI_ReleaseByteArrayElements(env, jwolaName2,  wola_name2, NULL);
    JNI_ReleaseByteArrayElements(env, jwolaName3,  wola_name3, NULL);

    // If there's a pending Java exception, it will be raised here and control
    // will immediately return to the Java caller.
    JNI_reThrowAndReturn(env);

    if (TraceActive(trc_level_detailed)) {
      TraceRecord(trc_level_detailed,
                  TP(42),
                  "ntv_activateWolaRegistration exit",
                  TRACE_DATA_INT(return_code,"return_code"),
                  TRACE_DATA_INT(iean4rt_rc, "iean4rt_rc"),
                  TRACE_DATA_INT(cell_pool_rc, "cell_pool_rc"),
                  TRACE_DATA_INT(registry_rc,"registry_rc"),
                  TRACE_DATA_INT(composite_rc,"composite_rc"),
                  TRACE_DATA_RAWDATA(sizeof(RegistryToken), &parms.registry_token, "registry token"),
                  TRACE_DATA_END_PARMS);
    }

    return (jint) composite_rc;
}

/**
 * Deactivate the WOLA registration (BBOARGE) referenced by the given jregistryToken.
 *
 * @param jregistryToken - byte[64] containing the registry token previously returned by ntv_activateWolaRegistration.
 *
 * @return zero if all is well; non-zero otherwise.
 */
JNIEXPORT jint JNICALL
ntv_deactivateWolaRegistration(JNIEnv* env, jobject jobj, jbyteArray jregistryToken) {

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(51),
                    "ntv_deactivateWolaRegistration entry",
                    TRACE_DATA_END_PARMS);
    }

    jbyte* registry_token = NULL;
    jsize  registry_token_len = 0;

    int registry_rc = 0;

    pc_activateWolaRegistration_parms parms;
    memset(&parms, 0, sizeof(pc_activateWolaRegistration_parms));

    JNI_try {

        // Load the auth stubs
        const server_authorized_function_stubs* auth_stubs_p = getServerAuthorizedFunctionStubs();
        if (auth_stubs_p == NULL) {
            JNI_throwNullPointerException(env, "auth_stubs_p is null. Failed to load authorized PC routines");
        }

        // Pull parm data from Java
        JNI_GetByteArrayElements(registry_token,    env, jregistryToken,    "jregistryToken is null", NULL);
        JNI_GetArrayLength(registry_token_len,  env, jregistryToken, "jregistryToken is null", 0);

        if (TraceActive(trc_level_detailed)) {
             TraceRecord(trc_level_detailed,
                         TP(53),
                         "JNI parms",
                         TRACE_DATA_RAWDATA(registry_token_len, registry_token, "registry_token"),
                         TRACE_DATA_END_PARMS);
        }

        // Validate parms
        if (registry_token_len != sizeof(RegistryToken)) {
            JNI_throwIllegalArgumentException(env, "registry_token must be 64 bytes long");
        }

        // Setup the PC routine parms
        memcpy(&parms.registry_token, registry_token, sizeof(RegistryToken));
        parms.registry_rc_p = &registry_rc;

        // Invoke the PC routine
        int pc_rc = auth_stubs_p->pc_deactivateWolaRegistration(&parms);
        if (pc_rc != 0) {
            JNI_throwPCRoutineFailedException(env, "pc_deactivateWolaRegistration", pc_rc);
        }
    }
    JNI_catch(env);

    // Release Java parms
    JNI_ReleaseByteArrayElements(env, jregistryToken,  registry_token, NULL);

    // If there's a pending Java exception, it will be raised here and control
    // will immediately return to the Java caller.
    JNI_reThrowAndReturn(env);

    if (TraceActive(trc_level_detailed)) {
      TraceRecord(trc_level_detailed,
                  TP(52),
                  "ntv_deactivateWolaRegistration exit",
                  TRACE_DATA_INT(registry_rc,"registry_rc"),
                  TRACE_DATA_END_PARMS);
    }

    return (jint) registry_rc;
}

/**
 * Retrieve the localcomm connection handle for the client who has registered
 * with the given WOLA registration name and who is hosting the given serviceName.
 * 
 * @param jwolaGroup - byte[8] containing the wola group name in EBCDIC, blank-padded
 * @param registerName - byte[16] containing the registration name of the WOLA client (in EBCDIC, blank-padded)
 * @param serviceName - byte[<=256] containing the service hosted by the WOLA client (in EBCDIC bytes)
 * @param clientConnHandle - Output - byte[16] is populated with the localcomm client connection handle.
 * @param waiterToken - A token we can use if we have to create a waiter, so others can find it.
 * @param jreturnCodeAarea - Output - byte[12] to contain RCs in the event of failure
 * 
 * @return 0 if all is well, non-zero otherwise (check returnCodeArea).
 */
JNIEXPORT jint JNICALL
ntv_getClientService(JNIEnv* env, jobject jobj, 
                     jbyteArray jwolaGroup, 
                     jbyteArray jregisterName, 
                     jbyteArray jserviceName, 
                     jint jtimeout_s,
                     jbyteArray jclientConnHandle,
                     jlong waiterToken,
                     jbyteArray jreturnCodeArea) {

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(61),
                    "ntv_getClientService entry",
                    TRACE_DATA_END_PARMS);
    }

    jbyte* wola_group = NULL;
    jsize  wola_group_len = 0;
    jbyte* registerName = NULL;
    jsize  registerName_len = 0;
    jbyte* serviceName = NULL;
    jsize  serviceName_len = 0;

    // Output fields from the PC routine.
    LocalCommClientConnectionHandle_t client_conn_handle;   
    int return_code = 0;
    int iean4rt_rc = 0;
    int getClientService_rc = 0;
    int composite_rc = 0;

    pc_wolaServiceQueueParms parms;
    memset(&parms, 0, sizeof(parms));

    JNI_try {

        // Load the auth stubs
        const server_authorized_function_stubs* auth_stubs_p = getServerAuthorizedFunctionStubs();
        if (auth_stubs_p == NULL) {
            JNI_throwNullPointerException(env, "auth_stubs_p is null. Failed to load authorized PC routines");
        }

        // Pull parm data from Java
        JNI_GetByteArrayElements(wola_group,    env, jwolaGroup,    "jwolaGroup is null", NULL);
        JNI_GetArrayLength(wola_group_len,      env, jwolaGroup,    "jwolaGroup is null", 0);

        JNI_GetByteArrayElements(registerName,  env, jregisterName,    "jregisterName is null", NULL);
        JNI_GetArrayLength(registerName_len,    env, jregisterName,    "jregisterName is null", 0);

        JNI_GetByteArrayElements(serviceName,   env, jserviceName,     "jserviceName is null", NULL);
        JNI_GetArrayLength(serviceName_len,     env, jserviceName,     "jserviceName is null", 0);

        if (TraceActive(trc_level_detailed)) {
             TraceRecord(trc_level_detailed,
                         TP(63),
                         "JNI parms",
                         TRACE_DATA_RAWDATA(wola_group_len, wola_group, "wola_group"),
                         TRACE_DATA_RAWDATA(registerName_len, registerName, "registerName"),
                         TRACE_DATA_RAWDATA(serviceName_len, serviceName, "serviceName"),
                         TRACE_DATA_RAWDATA(sizeof(jlong), &waiterToken, "Waiter token"),
                         TRACE_DATA_END_PARMS);
        }

        // Validate parms 
        if (wola_group_len != 8) {
            JNI_throwIllegalArgumentException(env, "wola_group must be 8 bytes long");
        } else if (registerName_len != 16 ) { 
            JNI_throwIllegalArgumentException(env, "registerName must be 16 bytes long");
        } else if (serviceName_len > 256) {
            JNI_throwIllegalArgumentException(env, "serviceName must be <= 256 bytes long");
        } 

        // Setup the PC routine parms
        memcpy(&parms.wolaGroup, wola_group, wola_group_len);
        memcpy(&parms.registration, registerName, registerName_len);
        memcpy(&parms.serviceName, serviceName, serviceName_len);
        parms.timeout_s = jtimeout_s;
        parms.return_code_p = &return_code;
        parms.iean4rt_rc_p = &iean4rt_rc;
        parms.getClientService_rc_p = &getClientService_rc;
        parms.client_conn_handle_p = &client_conn_handle;
        parms.waiterToken = waiterToken;

        // Invoke the PC routine
        int pc_rc = auth_stubs_p->pc_getClientService(&parms);
        if (pc_rc != 0) {
            JNI_throwPCRoutineFailedException(env, "pc_getClientService", pc_rc);
        }

        composite_rc = return_code | iean4rt_rc | getClientService_rc;

        // Copy the retrieved user token into Java byte[] output parm
        if (return_code == 0) {
            JNI_SetByteArrayRegion(env, jclientConnHandle, 0, sizeof(client_conn_handle), (jbyte*) &client_conn_handle);
        } else {
            // Copy back the return codes.
            int return_code_area_len;
            JNI_GetArrayLength(return_code_area_len,  env, jreturnCodeArea, "jreturnCodeArea is null", 0);

            if (return_code_area_len < 12) {
                JNI_throwIllegalArgumentException(env, "jreturnCodeArea must be 12 bytes long"); 
            }

            // !! NOTE: The return code area must be kept in sync with GetClientServiceReturnCodeArea in Java.
            JNI_SetByteArrayRegion(env, jreturnCodeArea, 0, sizeof(int), (jbyte*) &return_code);                // return_code in first 4 bytes
            JNI_SetByteArrayRegion(env, jreturnCodeArea, sizeof(int), sizeof(int), (jbyte*) &iean4rt_rc);       // iean4rt in second 4 bytes
            JNI_SetByteArrayRegion(env, jreturnCodeArea, sizeof(int) * 2, sizeof(int), (jbyte*) &getClientService_rc);  // getClientService rc in third 4 bytes
        }
    }
    JNI_catch(env);

    // Release Java parms
    JNI_ReleaseByteArrayElements(env, jwolaGroup,  wola_group, NULL);
    JNI_ReleaseByteArrayElements(env, jregisterName, registerName, NULL);
    JNI_ReleaseByteArrayElements(env, jserviceName,  serviceName, NULL);

    // If there's a pending Java exception, it will be raised here and control
    // will immediately return to the Java caller.
    JNI_reThrowAndReturn(env);

    if (TraceActive(trc_level_detailed)) {
      TraceRecord(trc_level_detailed,
                  TP(62),
                  "ntv_getClientService exit",
                  TRACE_DATA_INT(return_code,"return_code"),
                  TRACE_DATA_INT(iean4rt_rc,"iean4rt_rc"),
                  TRACE_DATA_INT(getClientService_rc,"getClientService_rc"),
                  TRACE_DATA_RAWDATA(sizeof(client_conn_handle), &client_conn_handle, "client conn handle"),
                  TRACE_DATA_END_PARMS);
    }

    return (jint) composite_rc;
}

/**
* Open a connetion to IMS and pass back the returned anchor
*
*
* @param GroupName - byte[8] containing the XCF group name in EBCDIC, blank padded
* @param MemberName - byte[16] containing the XCF server member name in EBCDIC, blank-padded)
* @param PartnerName - byte[16] containing the XCF client member name in EBCDIC, blank-padded)
* @param anchor - Output - byte[8] is populated with the returned connection anchor
* @param returnCodeArea - Output - int[5] return area for RC and RSN codes
*
* @return 0 if all is well; non-zero otherwise (check returnCodeArea)
*/
JNIEXPORT jint JNICALL
ntv_OpenOTMAConnection(JNIEnv* env, jobject jobj,
                       jbyteArray jGroupName,
                       jbyteArray jMemberName,
                       jbyteArray jPartnerName,
                       jbyteArray janchor,
                       jintArray jreturnCodeArea) {

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(70),
                    "ntv_OpenOTMAConnection entry",
                    TRACE_DATA_END_PARMS);
    }

    jbyte* group_name = NULL;
    jsize  group_name_len = 0;
    jbyte* member_name = NULL;
    jsize  member_name_len = 0;
    jbyte* partner_name = NULL;
    jsize  partner_name_len = 0;

    int return_code = 0;

    JNI_try {

        // Load the auth stubs
        const server_authorized_function_stubs* auth_stubs_p = getServerAuthorizedFunctionStubs();
        if (auth_stubs_p == NULL) {
            JNI_throwNullPointerException(env, "auth_stubs_p is null. Failed to load authorized PC routines");
        }

        // Load the unauth stubsgetServerUnauthorizedFunctionStubs
        const server_unauthorized_function_stubs* unauth_stubs_p = getServerUnauthorizedFunctionStubs();
        if (unauth_stubs_p == NULL) {
            JNI_throwNullPointerException(env, "unauth_stubs_p is null. Failed to load unauthorized PC routines");
        }

        // Pull parm data from Java
        JNI_GetByteArrayElements(group_name,    env, jGroupName,    "jwolaGroup is null", NULL);
        JNI_GetArrayLength(group_name_len,      env, jGroupName,    "jwolaGroup is null", 0);

        JNI_GetByteArrayElements(member_name,  env, jMemberName,    "jregisterName is null", NULL);
        JNI_GetArrayLength(member_name_len,    env, jMemberName,    "jregisterName is null", 0);

        JNI_GetByteArrayElements(partner_name,   env, jPartnerName,     "jserviceName is null", NULL);
        JNI_GetArrayLength(partner_name_len,     env, jPartnerName,     "jserviceName is null", 0);

        if (TraceActive(trc_level_detailed)) {
             TraceRecord(trc_level_detailed,
                         TP(72),
                         "JNI parms",
                         TRACE_DATA_RAWDATA(group_name_len, group_name, "group_name"),
                         TRACE_DATA_RAWDATA(member_name_len, member_name, "member_name"),
                         TRACE_DATA_RAWDATA(partner_name_len, partner_name, "partner_name"),
                         TRACE_DATA_END_PARMS);
        }

        // Validate parms
        if (group_name_len != 8) {
            JNI_throwIllegalArgumentException(env, "group_name must be 8 bytes long");
        } else if (member_name_len != 16 ) {
            JNI_throwIllegalArgumentException(env, "member_name must be 16 bytes long");
        } else if (partner_name_len != 16) {
            JNI_throwIllegalArgumentException(env, "partner_name must be 16 bytes long");
        }

        // Copy parms below the bar
        OpenOTMAParms* parms_p = __malloc31(sizeof(OpenOTMAParms));
        if (parms_p == NULL) {
            JNI_throwNullPointerException(env, "The pointer returned from __malloc31 is NULL");
        }

        memset(parms_p, 0, sizeof(OpenOTMAParms));

        parms_p->otma_retrsn.ret = -1;
        parms_p->otma_retrsn.rsn[0] = -1;
        parms_p->otma_retrsn.rsn[1] = -1;
        parms_p->otma_retrsn.rsn[2] = -1;
        parms_p->otma_retrsn.rsn[3] = -1;

        memcpy(&(parms_p->otma_group_name), group_name, group_name_len);
        memcpy(&(parms_p->otma_member_name), member_name, member_name_len);
        memcpy(&(parms_p->otma_partner_name), partner_name, partner_name_len);
        memcpy(&(parms_p->tpipe_prfx), "BBOA", 4);
        parms_p->sessions = 10;

        // Assign pointer values
        parms_p->otma_anchor_p = &(parms_p->otma_anchor);
        parms_p->otma_retrsn_p = &(parms_p->otma_retrsn);
        parms_p->ecb_p = &(parms_p->ecb);
        parms_p->otma_group_name_p = &(parms_p->otma_group_name);
        parms_p->otma_member_name_p = &(parms_p->otma_member_name);
        parms_p->otma_partner_name_p = &(parms_p->otma_partner_name);
        parms_p->sessions_p = &(parms_p->sessions);
        parms_p->tpipe_prfx_p = &(parms_p->tpipe_prfx);

        // Mark the last parm with a high order bit
        parms_p->tpipe_prfx_p = (tpipe_prfx_t* __ptr32) ((unsigned int) parms_p->tpipe_prfx_p | 0X80000000U);

        // Invoke the PC routine
        return_code = unauth_stubs_p->openOTMAConnection(parms_p);

        // Copy the retrieved anchor into Java byte[] output parm
        JNI_SetByteArrayRegion(env, janchor, 0, 8, (jbyte*) parms_p->otma_anchor_p);
        (*env)->SetIntArrayRegion(env, jreturnCodeArea, 0, 5, (jint*) parms_p->otma_retrsn_p);

        //Add anchor to server process data if open worked
        if(parms_p->otma_retrsn.ret == 0){

            //Create the parms
            struct pc_addOTMAAnchorToSPD_parms pc_parms;
            struct pc_addOTMAAnchorToSPD_parms* pc_parms_p = &pc_parms;
            memset(pc_parms_p, 0, sizeof(pc_addOTMAAnchorToSPD_parms));

            // Setup the PC routine parms
            memcpy(&(pc_parms_p->otma_group_name), group_name, group_name_len);
            memcpy(&(pc_parms_p->otma_member_name), member_name, member_name_len);
            memcpy(&(pc_parms_p->otma_partner_name), partner_name, partner_name_len);
            memcpy(&(pc_parms_p->otma_anchor), parms_p->otma_anchor_p, sizeof(otma_anchor_t));

            // Invoke the PC routine
            int pc_rc = auth_stubs_p->pc_addOTMAAnchorToSPD(pc_parms_p);

            if (pc_rc != 0) {
                JNI_throwPCRoutineFailedException(env, "pc_addOTMAAnchorToSPD", pc_rc);
            }
        }

        free(parms_p);
    }
    JNI_catch(env);

    // Release Java parms
    JNI_ReleaseByteArrayElements(env, jGroupName, group_name, NULL);
    JNI_ReleaseByteArrayElements(env, jMemberName, member_name, NULL);
    JNI_ReleaseByteArrayElements(env, jPartnerName, partner_name, NULL);

    // If there's a pending Java exception, it will be raised here and control
    // will immediately return to the Java caller.
    JNI_reThrowAndReturn(env);

    if (TraceActive(trc_level_detailed)) {
      TraceRecord(trc_level_detailed,
                  TP(73),
                  "ntv_OpenOTMAConnection exit",
                  TRACE_DATA_INT(jreturnCodeArea,"return codes"),
                  TRACE_DATA_RAWDATA(8, &janchor, "anchor"),
                  TRACE_DATA_END_PARMS);
    }

    return (jint) return_code;
}

/**
 * Send a message via otma_send_receive and copy back any response data.
 *
 * @param janchor - byte[8], obtained from otma_open
 * @param jsendSegmentList - int[] with segment count and segment lengths
 * @param jsendSegmentData - byte[] containing data to send
 * @param jsendDataLength - int, length of segment data
 * @param jsyncLevel - 0 or 1 (for SyncLevel1)
 * @param jrecvSegmentNum - int, max segment count for response
 * @param jrecvSegmentList - int[] to hold segment list for response
 * @param jrecvSegmentData - byte[] to hold response data
 * @param jrecvDataLength - int, max response size
 * @param jreturnReasonCodes - int[] to hold return and reason codes
 * @param jerrorMessage - byte[120] to hold error messages from IMS
 * @param jinterruptObjectBridge - The interface to the ODI for request interrupt
 * @return 0 if successful, additional return/reason codes in ret/rsn code area
 */
JNIEXPORT jint JNICALL
ntv_otmaSendReceive(JNIEnv* env, jobject jobj,
                    jbyteArray janchor,
                    jintArray jsendSegmentList,
                    jbyteArray jsendSegmentData,
                    jint jsendDataLength,
                    jint jsyncLevel,
                    jint jrecvSegmentNum,
                    jintArray jrecvSegmentList,
                    jbyteArray jrecvSegmentData,
                    jint jrecvDataLength,
                    jintArray jreturnReasonCodes,
                    jbyteArray jerrorMessage,
                    jobject jinterruptObjectBridge) {
    jint rc = 0;
    jsize anchor_len = 0;
    jsize retrsn_len = 0;
    jsize send_seg_len = 0;
    jsize send_data_len = 0;
    jsize recv_seg_len = 0;
    jsize recv_data_len = 0;
    jsize err_msg_len = 0;
    int segments = 0;
    otma_alloc_parms* alloc_parms_p;
    otma_sendrcv_parms* send_parms_p;
    otma_free_parms* free_parms_p;

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(81),
                    "ntv_OtmaSendReceive entry",
                    TRACE_DATA_END_PARMS);
    }

    const server_unauthorized_function_stubs* unauth_stubs_p;

    JNI_try {
        unauth_stubs_p = getServerUnauthorizedFunctionStubs();
        if (unauth_stubs_p == NULL) {
            JNI_throwNullPointerException(env, "Failed to get a reference to unauthorized function stubs");
        }

        // Verify Java parms
        JNI_GetArrayLength(anchor_len, env, janchor, "janchor is null", 0);
        JNI_GetArrayLength(retrsn_len, env, jreturnReasonCodes, "jreturnReasonCodes is null", 0);
        JNI_GetArrayLength(send_seg_len, env, jsendSegmentList, "jsendSegmentList is null", 0);
        JNI_GetArrayLength(send_data_len, env, jsendSegmentData, "jsendSegmentData is null", 0);
        JNI_GetArrayLength(recv_seg_len, env, jrecvSegmentList, "jrecvSegmentList is null", 0);
        JNI_GetArrayLength(recv_data_len, env, jrecvSegmentData, "jrecvSegmentData is null", 0);
        JNI_GetArrayLength(err_msg_len, env, jerrorMessage, "jerrorMessage is null", 0);
        (*env)->GetIntArrayRegion(env, jsendSegmentList, 0, 1, &segments);

        if (anchor_len != 8) {
            JNI_throwIllegalArgumentException(env, "OTMA anchor must be 8 bytes");
        } else if (retrsn_len != 5) {
            JNI_throwIllegalArgumentException(env, "OTMA return/reason code array must be 5 integers");
        } else if (jsyncLevel != 0 && jsyncLevel != 1) {
            JNI_throwIllegalArgumentException(env, "OTMA sync level value must be 0 or 1");
        } else if (send_seg_len != (segments + 1)) {
            JNI_throwIllegalArgumentException(env, "Send segment list size does not match segment count");
        } else if (send_data_len != jsendDataLength) {
            JNI_throwIllegalArgumentException(env, "Declared send buffer size does not match array size");
        } else if (recv_seg_len != (jrecvSegmentNum + 1)) {
            JNI_throwIllegalArgumentException(env, "Receive segment list size does not match segment count");
        } else if (recv_data_len != jrecvDataLength) {
            JNI_throwIllegalArgumentException(env, "Declared receive buffer size does not match array size");
        } else if (err_msg_len != OTMA_ERROR_SIZE) {
            JNI_throwIllegalArgumentException(env, "Error message buffer size must be 120 bytes");
        }
    } JNI_catch(env);
    JNI_reThrowAndReturn(env);

    // Step 1: Call DFSYALOC (otma_alloc) to get a session handle
    JNI_try {
        alloc_parms_p = __malloc31(sizeof(otma_alloc_parms));
        if (alloc_parms_p == NULL) {
            JNI_throwNullPointerException(env, "Malloc for otma_alloc parms failed");
        }

        rc = callOtmaAllocate(env, janchor, jreturnReasonCodes, jsyncLevel, jsendSegmentData, alloc_parms_p, unauth_stubs_p);

        // Verify RC from alloc
        if (rc != 0) {
            // Copy ret/rsn codes and return to Java caller
            (*env)->SetIntArrayRegion(env, jreturnReasonCodes, 0, retrsn_len, (int*) &(alloc_parms_p->retrsn));
            free(alloc_parms_p);
            return rc;
        }
    } JNI_catch2(env);
    JNI_reThrowAndReturn(env); // Quit here if we failed to malloc

    // Step 2: Call DFSYSEND (otma_send_receive) using session handle from alloc
    JNI_try {
        send_parms_p = __malloc31(sizeof(otma_sendrcv_parms));
        if (send_parms_p == NULL) {
            JNI_throwNullPointerException(env, "Malloc for otma_send_receive parms failed");
        }

        send_parms_p->send_buffer_p = __malloc31(jsendDataLength +                      // send buffer size
                                                (sizeof(int) * (segments + 1)) +        // send segment list (segment count + 1 per segment)
                                                jrecvDataLength +                       // recv buffer size
                                                (sizeof(int) * (jrecvSegmentNum + 1))); // recv segment list (segment count + 1 per segment)
        if (send_parms_p->send_buffer_p == NULL) {
            free(send_parms_p);
            JNI_throwNullPointerException(env, "Malloc for otma_send_receive buffers failed");
        }

        rc = callOtmaSendReceive(env, janchor, jsendSegmentList, segments, jsendSegmentData, jsendDataLength,
                                 jrecvSegmentNum, jrecvDataLength, jinterruptObjectBridge, alloc_parms_p->session_handle_p, send_parms_p, unauth_stubs_p);

    } JNI_catch2(env); // Don't re-throw and return yet: need to call otma_free

    // Step 3: Call DFSYFREE (otma_free) to free the session handle regardless of DFSYSEND result.
    //         There is a race condition where, if the request is cancelled, the cancel may have
    //         already successfully freed the session just as the session completed on its own.  We
    //         are relying on OTMA C/I to detect this condition and prevent a double session free.
    JNI_try {
        free_parms_p = __malloc31(sizeof(otma_free_parms));
        if (free_parms_p == NULL) {
            JNI_throwNullPointerException(env, "Malloc for otma_free failed");
        }

        callOtmaFree(env, janchor, alloc_parms_p->session_handle_p, free_parms_p, unauth_stubs_p);
    } JNI_catch2(env);

    // Free parm structs we no longer need
    free(alloc_parms_p);
    if (free_parms_p != NULL) {
        free(free_parms_p);
    }

    // Check if we failed to malloc for send
    if (send_parms_p == NULL) {
        JNI_reThrowAndReturn(env);
    } else if (send_parms_p->send_buffer_p == NULL) {
        free(send_parms_p);
        JNI_reThrowAndReturn(env);
    }

    // Convert our internal cancel to a real OTMA C/I cancel.  It is very unlikely that we will
    // post the ECB with our own post code, OTMA C/I should do it first, but just in case.
    if (send_parms_p->postCode == OTMA_CANCEL_INTERNAL_POST_CODE) {
        send_parms_p->retrsn.ret = 16; // OTMA value for cancelled.
    }

    // Check RC from send, and copy back either error info or response data
    if (rc != 0) {
        (*env)->SetIntArrayRegion(env, jreturnReasonCodes, 0, retrsn_len, (int*) send_parms_p->retrsn_p);
        (*env)->SetByteArrayRegion(env, jerrorMessage, 0, OTMA_ERROR_SIZE, send_parms_p->error_message_ptr);
    } else {
        (*env)->SetIntArrayRegion(env, jreturnReasonCodes, 0, retrsn_len, (int*) send_parms_p->retrsn_p);
        (*env)->SetIntArrayRegion(env, jrecvSegmentList, 0, send_parms_p->recv_seg_list_p->seg_count + 1, (int*) send_parms_p->recv_seg_list_p);
        (*env)->SetByteArrayRegion(env, jrecvSegmentData, 0, send_parms_p->recv_length, (signed char*) send_parms_p->recv_buffer_p);
    }

    free(send_parms_p->send_buffer_p);
    free(send_parms_p);

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(85),
                    "ntv_OtmaSendReceive exit",
                    TRACE_DATA_END_PARMS);
    }

    return rc;
}

/**
 * Set up below-the-bar parameter area and call otma_alloc.
 *
 * @return 0 if successful
 */
int callOtmaAllocate(JNIEnv* env,
              jbyteArray janchor,
              jintArray jreturnReasonCodes,
              jint jsyncLevel,
              jbyteArray jsendSegmentData,
              otma_alloc_parms* parms_p,
              server_unauthorized_function_stubs* unauth_stubs_p) {

    // Copy parms below the bar
    parms_p->anchor_p = &(parms_p->anchor);
    parms_p->retrsn_p = &(parms_p->retrsn);
    parms_p->session_handle_p = &(parms_p->session_handle);

    /* OTMA C/I requires that either the tran name is in the data stream and blanks here      */
    /* or supplied here and not in the message data stream.  We require that the tranid       */
    /* be supplied in the datastream as : <TRANID>   LLZZ<DATA>, so we insert blanks here.    */
    strncpy((char *)parms_p->transaction, "        ", sizeof(tran_name_t));
    parms_p->transaction_p = &(parms_p->transaction);
    parms_p->options_p = &(parms_p->options);
    parms_p->user_id_p = &(parms_p->user_id);
    parms_p->user_grp_p = &(parms_p->user_grp);

    (*env)->GetByteArrayRegion(env, janchor, 0, sizeof(otma_anchor_t), (signed char*) parms_p->anchor_p);

    parms_p->retrsn.ret = -1;
    parms_p->retrsn.rsn[0] = -1;
    parms_p->retrsn.rsn[1] = -1;
    parms_p->retrsn.rsn[2] = -1;
    parms_p->retrsn.rsn[3] = -1;

    memset(parms_p->session_handle_p, 0, sizeof(sess_handle_t) + sizeof(otma_profile_t));
    parms_p->options.SyncLevel1 = jsyncLevel;



    // We are not authorized, so don't provide user info - IMS will take it off the TCB
    memset(parms_p->user_id_p, 0, sizeof(racf_uid_t) + sizeof(racf_prf_t));

    // Set high order bit to mark last parameter
    parms_p->user_grp_p = (racf_prf_t * __ptr32)
            ((unsigned int) parms_p->user_grp_p | 0X80000000U);

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(
          trc_level_detailed,
          TP(82),
          "Calling DFSYALOC OTMA Allocate",
          TRACE_DATA_RAWDATA(sizeof(otma_anchor_t), parms_p->anchor_p, "OTMA Anchor data"),
          TRACE_DATA_RAWDATA(sizeof(otma_retrsn_t), parms_p->retrsn_p, "OTMA Return Reason data"),
          TRACE_DATA_RAWDATA(sizeof(sess_handle_t), parms_p->session_handle_p, "OTMA Session Handle data"),
          TRACE_DATA_RAWDATA(sizeof(otma_profile_t), parms_p->options_p, "OTMA Options data"),
          TRACE_DATA_RAWDATA(sizeof(tran_name_t), parms_p->transaction_p, "OTMA Transaction Name data"),
          TRACE_DATA_RAWDATA(sizeof(racf_uid_t), parms_p->user_id_p, "OTMA User Name data"),
          TRACE_DATA_RAWDATA(sizeof(racf_prf_t), parms_p->user_grp_p, "OTMA User Group data"),
          TRACE_DATA_PTR32(parms_p->anchor_p, "OTMA Anchor pointer"),
          TRACE_DATA_PTR32(parms_p->retrsn_p, "OTMA Return Reason pointer"),
          TRACE_DATA_PTR32(parms_p->session_handle_p, "OTMA Session Handle pointer"),
          TRACE_DATA_PTR32(parms_p->options_p, "OTMA Options pointer"),
          TRACE_DATA_PTR32(parms_p->transaction_p, "OTMA Transaction Name pointer"),
          TRACE_DATA_PTR32(parms_p->user_id_p, "OTMA User Name pointer"),
          TRACE_DATA_PTR32(parms_p->user_grp_p, "OTMA User Group pointer"),
          TRACE_DATA_END_PARMS);
    }

    // Call otma_alloc
    return unauth_stubs_p->otmaAllocate(parms_p);
}

/**
 * Set up below-the-bar parameter area and call otma_send_receive.
 *
 * @return 0 if successful
 */
int callOtmaSendReceive(JNIEnv* env,
                jbyteArray janchor,
                jintArray jsendSegmentList,
                int segments,
                jbyteArray jsendSegmentData,
                jint jsendDataLength,
                jint jrecvSegmentNum,
                jint jrecvDataLength,
                jobject jinterruptObjectBridge,
                sess_handle_t* session_handle_p,
                otma_sendrcv_parms* parms_p,
                server_unauthorized_function_stubs* unauth_stubs_p) {

    // Set up pointers
    parms_p->anchor_p = &(parms_p->anchor);
    parms_p->retrsn_p = &(parms_p->retrsn);
    parms_p->ecb_p = &(parms_p->ecb);
    parms_p->session_handle_p = &(parms_p->session_handle);
    parms_p->lterm_p = &(parms_p->lterm);
    parms_p->modname_p = &(parms_p->modname);
    // parms_p->send_buffer_p was set up by caller
    parms_p->send_length_p = &(parms_p->send_length);
    parms_p->send_seg_list_p = (otma_seg_list*) (((char *) parms_p->send_buffer_p) + jsendDataLength);
    parms_p->recv_buffer_p = (char*) parms_p->send_seg_list_p + (sizeof(int) * (segments + 1));
    parms_p->recv_length_max_p = &(parms_p->recv_length_max);
    parms_p->recv_length_p = &(parms_p->recv_length);
    parms_p->recv_seg_list_p = (otma_seg_list*)(((char *) parms_p->recv_buffer_p) + jrecvDataLength);
    parms_p->context_id_p = &(parms_p->context_id);
    parms_p->error_message_ptr = &(parms_p->error_message);
    parms_p->error_message_ptr_ptr = &(parms_p->error_message_ptr);
    //parms_p->otma_userdata_p = &(parms_p->otma_userdata);

    parms_p->otma_userdata_p = NULL;
    parms_p->postCode = 0;

    // Copy parms below the bar
    (*env)->GetByteArrayRegion(env, janchor, 0, sizeof(otma_anchor_t), (signed char*) parms_p->anchor_p);

    parms_p->retrsn.ret = -1;
    parms_p->retrsn.rsn[0] = -1;
    parms_p->retrsn.rsn[1] = -1;
    parms_p->retrsn.rsn[2] = -1;
    parms_p->retrsn.rsn[3] = -1;

    parms_p->ecb = 0;

    memcpy(parms_p->session_handle_p, session_handle_p, sizeof(sess_handle_t));

    memset(parms_p->lterm_p, 0, sizeof(lterm_name_t) + sizeof(mod_name_t)); // not using lterm or modname

    (*env)->GetByteArrayRegion(env, jsendSegmentData, 0, jsendDataLength, (signed char*) parms_p->send_buffer_p);

    parms_p->send_length = jsendDataLength;

    (*env)->GetIntArrayRegion(env, jsendSegmentList, 0, segments + 1, (int*) parms_p->send_seg_list_p);

    // set recv buf?

    parms_p->recv_length_max = jrecvDataLength;

    parms_p->recv_length = 0;

    parms_p->recv_seg_list_p->seg_count = jrecvSegmentNum;

    memset(parms_p->context_id_p, 0, sizeof(context_t) + OTMA_ERROR_SIZE); // not using context, zero out error buffer


    // Set high order bit to mark last parameter
    parms_p->error_message_ptr_ptr = (char * __ptr32)
            ((unsigned int) parms_p->error_message_ptr_ptr | 0X80000000U);

    // Register the ODI if appropriate.
    jobject jodiToken = NULL;
    if (jinterruptObjectBridge != NULL) {
        jint jecbPtr = (jint)(parms_p->ecb_p);
        jlong jsessionHandle = 0L;
        jlong janchor = 0L;
        memcpy(&jsessionHandle, session_handle_p, sizeof(jsessionHandle));
        memcpy(&janchor, parms_p->anchor_p, sizeof(janchor));
        jodiToken = (*env)->CallObjectMethod(env, jinterruptObjectBridge, jregisterOtmaMethod, janchor, jsessionHandle, jecbPtr);

        // The java code tries to ignore any exceptions.  If something leaks thru, we want to draw
        // attention to it, but also continue with the request.
        if ((*env)->ExceptionCheck(env) == JNI_TRUE) {
            (*env)->ExceptionDescribe(env);
            (*env)->ExceptionClear(env);
        }
    }

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(
          trc_level_detailed,
          TP(83),
          "Calling DFSYSEND OTMA Send/Receive",
          TRACE_DATA_RAWDATA(sizeof(otma_anchor_t), parms_p->anchor_p, "OTMA Anchor data"),
          TRACE_DATA_RAWDATA(sizeof(otma_retrsn_t), parms_p->retrsn_p, "OTMA Return Reason data"),
          TRACE_DATA_INT(parms_p->ecb, "OTMA ECB data"),
          TRACE_DATA_RAWDATA(sizeof(sess_handle_t), parms_p->session_handle_p, "OTMA Session Handle data"),
          TRACE_DATA_RAWDATA(sizeof(lterm_name_t), parms_p->lterm_p, "OTMA Lterm data"),
          TRACE_DATA_RAWDATA(sizeof(mod_name_t), parms_p->modname_p, "OTMA Modname data"),
          TRACE_DATA_RAWDATA(parms_p->send_length, parms_p->send_buffer_p, "OTMA Send Buffer data"),
          TRACE_DATA_INT(parms_p->send_length, "OTMA Send Buffer Length data"),
          TRACE_DATA_RAWDATA(sizeof(int) * (segments + 1), parms_p->send_seg_list_p, "OTMA Send Segment List data"),
          TRACE_DATA_RAWDATA(parms_p->recv_length, parms_p->recv_buffer_p, "OTMA Recv Buffer Length Raw"),
          TRACE_DATA_INT(parms_p->recv_length_max, "OTMA Recv Buffer Length data max"),
          TRACE_DATA_INT(parms_p->recv_length, "OTMA Recv Actual Length data int"),
          TRACE_DATA_RAWDATA(sizeof(int) * (jrecvSegmentNum + 1), parms_p->recv_seg_list_p, "OTMA Recv Segment List data"),
          TRACE_DATA_RAWDATA(sizeof(context_t), parms_p->context_id_p, "OTMA Context data"),
          TRACE_DATA_RAWDATA(OTMA_ERROR_SIZE, parms_p->error_message_ptr, "OTMA Error Message pointer"),
           TRACE_DATA_PTR32(parms_p->anchor_p, "OTMA Anchor pointer"),
          TRACE_DATA_PTR32(parms_p->retrsn_p, "OTMA Return Reason pointer"),
          TRACE_DATA_PTR32(parms_p->ecb_p, "OTMA ECB pointer"),
          TRACE_DATA_PTR32(parms_p->session_handle_p, "OTMA Session Handle pointer"),
          TRACE_DATA_PTR32(parms_p->lterm_p, "OTMA Lterm pointer"),
          TRACE_DATA_PTR32(parms_p->modname_p, "OTMA Modname pointer"),
          TRACE_DATA_PTR32(parms_p->send_buffer_p, "OTMA Send Buffer pointer"),
          TRACE_DATA_PTR32(parms_p->send_length_p, "OTMA Send Buffer Length pointer"),
          TRACE_DATA_PTR32(parms_p->send_seg_list_p, "OTMA Send Segment List pointer"),
          TRACE_DATA_PTR32(parms_p->recv_buffer_p, "OTMA Recv Buffer pointer"),
          TRACE_DATA_PTR32(parms_p->recv_length_max_p, "OTMA Recv Buffer Length pointer"),
          TRACE_DATA_PTR32(parms_p->recv_length_p, "OTMA Recv Actual Length pointer"),
          TRACE_DATA_PTR32(parms_p->recv_seg_list_p, "OTMA Recv Segment List pointer"),
          TRACE_DATA_PTR32(parms_p->context_id_p, "OTMA Context pointer"),
          TRACE_DATA_PTR32(parms_p->error_message_ptr, "OTMA Error Message pointer"),
          TRACE_DATA_PTR32(parms_p->error_message_ptr_ptr, "OTMA Error Message pointer pointer"),
          TRACE_DATA_END_PARMS);
    }

    // Call otma_send_receive
    int stubRc = unauth_stubs_p->otmaSendReceive(parms_p);

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(
          trc_level_detailed,
          TP(86),
          "DFSYSEND OTMA Send/Receive return",
          TRACE_DATA_RAWDATA(sizeof(otma_retrsn_t), parms_p->retrsn_p, "OTMA Return Reason data"),
          TRACE_DATA_INT(parms_p->postCode, "Post code"),
          TRACE_DATA_END_PARMS);
    }

    // Deregister the ODI
    if ((jinterruptObjectBridge != NULL) && (jodiToken != NULL)) {
        (*env)->CallVoidMethod(env, jinterruptObjectBridge, jderegisterMethod, jodiToken);

        // The java code tries to ignore any exceptions.  If something leaks thru, we want to draw
        // attention to it, but also continue with the request.
        if ((*env)->ExceptionCheck(env) == JNI_TRUE) {
            (*env)->ExceptionDescribe(env);
            (*env)->ExceptionClear(env);
        }
    }

    // Once the call from otma_send_receivex and the call to deregister ODI returns, it is
    // safe to free the ECB (we do not free it here, the caller does).
    return stubRc;
}

/**
 * Set up below-the-bar parameter area and call otma_free.
 *
 * @return 0 if successful
 */
int callOtmaFree(JNIEnv* env,
                 jbyteArray janchor,
                 sess_handle_t* session_handle_p,
                 otma_free_parms* parms_p,
                 server_unauthorized_function_stubs* unauth_stubs_p) {
    // Set up pointers
    parms_p->anchor_p = &(parms_p->anchor);
    parms_p->retrsn_p = &(parms_p->retrsn);
    parms_p->session_handle_p = &(parms_p->session_handle);

    // Copy parms below the bar
    (*env)->GetByteArrayRegion(env, janchor, 0, sizeof(otma_anchor_t), (signed char *) parms_p->anchor_p);

    parms_p->retrsn.ret = -1;
    parms_p->retrsn.rsn[0] = -1;
    parms_p->retrsn.rsn[1] = -1;
    parms_p->retrsn.rsn[2] = -1;
    parms_p->retrsn.rsn[3] = -1;

    memcpy(parms_p->session_handle_p, session_handle_p, sizeof(sess_handle_t));

    // Set high order bit to mark last parameter
    parms_p->session_handle_p = (sess_handle_t * __ptr32)
            ((unsigned int) parms_p->session_handle_p | 0X80000000U);

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(
          trc_level_detailed,
          TP(84),
          "Calling DFSYFREE OTMA Free",
          TRACE_DATA_RAWDATA(sizeof(otma_anchor_t), parms_p->anchor_p, "OTMA Anchor data"),
          TRACE_DATA_RAWDATA(sizeof(otma_retrsn_t), parms_p->retrsn_p, "OTMA Return Reason data"),
          TRACE_DATA_RAWDATA(sizeof(sess_handle_t), parms_p->session_handle_p, "OTMA Session Handle data"),
          TRACE_DATA_PTR32(parms_p->anchor_p, "OTMA Anchor pointer"),
          TRACE_DATA_PTR32(parms_p->retrsn_p, "OTMA Return Reason pointer"),
          TRACE_DATA_PTR32(parms_p->session_handle_p, "OTMA Session Handle pointer"),
          TRACE_DATA_END_PARMS);
    }

    // Call otma_free
    return unauth_stubs_p->otmaFree(parms_p, NULL, NULL);
}

/**
 * Close an open OTMA connection.
 *
 * @param janchor - byte[8] containing the anchor for the open connection
 * @param jreturnCodeArea - Output - int[5] to store ret/rsn codes from OTMA
 *
 * @return 0 for success, non-zero otherwise (additional ret/rsn in returnCodeArea)
 */
JNIEXPORT jint JNICALL
ntv_closeOtmaConnection(JNIEnv* env, jobject jobj, jbyteArray janchor, jintArray jreturnReasonCodes) {
    const server_unauthorized_function_stubs* unauth_stubs_p = NULL;
    jint rc = 0;
    jsize anchor_len = 0;
    jsize retrsn_len = 0;

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(91),
                    "ntv_closeOtmaConnection entry",
                    TRACE_DATA_END_PARMS);
    }

    JNI_try {

        // Load the auth stubs
        const server_authorized_function_stubs* auth_stubs_p = getServerAuthorizedFunctionStubs();
        if (auth_stubs_p == NULL) {
            JNI_throwNullPointerException(env, "auth_stubs_p is null. Failed to load authorized PC routines");
        }


        unauth_stubs_p = getServerUnauthorizedFunctionStubs();

        if (unauth_stubs_p) {

            // Verify Java parms
            JNI_GetArrayLength(anchor_len, env, janchor, "janchor is null", 0);
            JNI_GetArrayLength(retrsn_len, env, jreturnReasonCodes, "jreturnReasonCodes is null", 0);

            if (anchor_len != 8) {
                JNI_throwIllegalArgumentException(env, "OTMA anchor must be 8 bytes");
            } else if (retrsn_len != 5) {
                JNI_throwIllegalArgumentException(env, "OTMA return/reason code array must be 5 integers");
            }

            // Copy parms below the bar
            otma_close_parms* parms_p = __malloc31(sizeof(otma_close_parms));

            if (parms_p == NULL) {
                JNI_throwNullPointerException(env, "Failed to obtain 31-bit storage for otma_close parms");
            }

            (*env)->GetByteArrayRegion(env, janchor, 0, anchor_len, (signed char*) &(parms_p->anchor));

            parms_p->retrsn.ret = -1;
            parms_p->retrsn.rsn[0] = -1;
            parms_p->retrsn.rsn[1] = -1;
            parms_p->retrsn.rsn[2] = -1;
            parms_p->retrsn.rsn[3] = -1;

            parms_p->anchor_p = &(parms_p->anchor);
            parms_p->retrsn_p = &(parms_p->retrsn);

            // Set high order bit to mark last parameter
            parms_p->retrsn_p = (otma_retrsn_t * __ptr32)
                    ((unsigned int) parms_p->retrsn_p | 0X80000000U);

            if (TraceActive(trc_level_detailed)) {
                TraceRecord(
                  trc_level_detailed,
                  TP(92),
                  "Calling DFSYCLSE OTMA Close",
                  TRACE_DATA_RAWDATA(sizeof(otma_anchor_t), &(parms_p->anchor), "OTMA Anchor data"),
                  TRACE_DATA_RAWDATA(sizeof(otma_retrsn_t), &(parms_p->retrsn), "OTMA Return Reason data"),
                  TRACE_DATA_PTR32(parms_p->anchor_p, "OTMA Anchor pointer"),
                  TRACE_DATA_PTR32(parms_p->retrsn_p, "OTMA Return Reason pointer"),
                  TRACE_DATA_END_PARMS);
            }

            //Create and add anchor to SPD parms so we can remove it from spd after the connection is closed
            struct pc_addOTMAAnchorToSPD_parms pc_parms;
            struct pc_addOTMAAnchorToSPD_parms* pc_parms_p = &pc_parms;
            memset(pc_parms_p, 0, sizeof(pc_addOTMAAnchorToSPD_parms));

            // Add anchor to the PC routine parms (the other parameters are not needed)
            memcpy(&(pc_parms_p->otma_anchor), parms_p->anchor_p, sizeof(otma_anchor_t));

            // Call otma_close
            rc = unauth_stubs_p->closeOtmaConnection(parms_p);

            // Copy ret/rsn codes
            (*env)->SetIntArrayRegion(env, jreturnReasonCodes, 0, retrsn_len, (int*) &(parms_p->retrsn));


            // Remove the anchor from SPD
            int pc_rc = auth_stubs_p->pc_removeOTMAAnchorFromSPD(pc_parms_p);
            if (pc_rc != 0) {
                JNI_throwPCRoutineFailedException(env, "pc_removeOTMAAnchorFromSPD", pc_rc);
            }

            free(parms_p);
        }
    } JNI_catch(env);

    JNI_reThrowAndReturn(env);

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(93),
                    "ntv_closeOtmaConnection exit",
                    TRACE_DATA_END_PARMS);
    }

    return rc;
}

/**
 * Cancel an OTMA connection
 *
 * @param jotmaAnchor The OTMA anchor that was used to obtain the session ID.
 * @param jotmaSession The OTMA session ID to cancel.
 * @param jecbPtr The ECB that the OTMA send_receivex is waiting on.
 *
 * @return The return code from otma_free.
 */
void ntv_cancelOtmaRequest(JNIEnv* env, jobject jobj, jlong jotmaAnchor, jlong jotmaSession, jint jecbPtr) {
    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(8),
                    "ntv_cancelOtmaRequest entry",
                    TRACE_DATA_RAWDATA(sizeof(jlong), &jotmaSession, "OTMA Session ID"),
                    TRACE_DATA_PTR((void*)jecbPtr, "ECB Pointer"),
                    TRACE_DATA_RAWDATA(sizeof(jint), (void*)jecbPtr, "ECB Data"),
                    TRACE_DATA_END_PARMS);
    }

    const server_unauthorized_function_stubs* unauth_stubs_p = getServerUnauthorizedFunctionStubs();
    if (unauth_stubs_p == NULL) {
        if (TraceActive(trc_level_exception)) {
            TraceRecord(
              trc_level_exception,
              TP(16),
              "ntv_cancelOtmaRequest could not get unauth fcn ptr",
              TRACE_DATA_END_PARMS);
        }

        return;
    }

    // Set up the ECB that we'll post.
    void* ecb_p = (void*)jecbPtr;
    int postCode = OTMA_CANCEL_INTERNAL_POST_CODE;

    // The thread which made the OTMA request is either still off in IMS, or is back
    // and has called up to Java to deregister the ODI.  That call will block because
    // we hold the monitor (synchronized method) that it needs.  We are free to touch
    // the ECB here.
    //
    // First we're going to call otma_free to cancel the OTMA session.  Once we call
    // free, OTMA C/I will not touch the ECB.  At that point, we'll POST the ECB with
    // a post code which indicates we cancelled the request.
    otma_free_parms* free_parms_p = __malloc31(sizeof(otma_free_parms));
    if (free_parms_p == NULL) {
        if (TraceActive(trc_level_exception)) {
            TraceRecord(
              trc_level_exception,
              TP(17),
              "ntv_cancelOtmaRequest out of memory obtaining OTMA parm area",
              TRACE_DATA_INT(sizeof(otma_free_parms), "Bytes"),
              TRACE_DATA_END_PARMS);
        }

        return;
    }

    // Set up pointers
    free_parms_p->anchor_p = &(free_parms_p->anchor);
    free_parms_p->retrsn_p = &(free_parms_p->retrsn);
    free_parms_p->session_handle_p = &(free_parms_p->session_handle);

    // Copy parms below the bar
    memset(free_parms_p->retrsn_p, 0xFF, sizeof(free_parms_p->retrsn));
    memcpy(free_parms_p->anchor_p, &jotmaAnchor, sizeof(free_parms_p->anchor));
    memcpy(free_parms_p->session_handle_p, &jotmaSession, sizeof(free_parms_p->session_handle));

    // Set high order bit to mark last parameter
    free_parms_p->session_handle_p = (sess_handle_t * __ptr32)
            ((unsigned int) free_parms_p->session_handle_p | 0X80000000U);

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(
          trc_level_detailed,
          TP(14),
          "ntv_cancelOtmaRequest DFSYFREE OTMA Free call",
          TRACE_DATA_RAWDATA(sizeof(otma_anchor_t), free_parms_p->anchor_p, "OTMA Anchor data"),
          TRACE_DATA_RAWDATA(sizeof(otma_retrsn_t), free_parms_p->retrsn_p, "OTMA Return Reason data"),
          TRACE_DATA_RAWDATA(sizeof(sess_handle_t), free_parms_p->session_handle_p, "OTMA Session Handle data"),
          TRACE_DATA_PTR(ecb_p, "ECB Ptr"),
          TRACE_DATA_INT(postCode, "Post code"),
          TRACE_DATA_END_PARMS);
    }

    // Call otma_free
    int rc = unauth_stubs_p->otmaFree(free_parms_p, ecb_p, &postCode);

    enum trc_level responseTraceLevel = (rc == 0) ? trc_level_detailed : trc_level_exception;
    if (TraceActive(responseTraceLevel)) {
        TraceRecord(
          responseTraceLevel,
          TP(15),
          "ntv_cancelOtmaRequest DFSYFREE OTMA Free return",
          TRACE_DATA_INT(rc, "Return Code"),
          TRACE_DATA_RAWDATA(sizeof(otma_retrsn_t), free_parms_p->retrsn_p, "OTMA Return Reason data"),
          TRACE_DATA_END_PARMS);
    }

    free(free_parms_p);

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(9),
                    "ntv_cancelOtmaRequest exit",
                    TRACE_DATA_END_PARMS);
    }
}

/**
 * This method is called when the Java code detects a hung request, and request interrupts
 * are enabled.  We'll try to remove this particular waiter from the queue.
 *
 * There is no return value.  We will do our best to find and cancel the waiter, but in
 * the end this is driven from an ODI which is a best-effort sort of thing.
 *
 * @param wolaGroupBytes The WOLA group name, padded to 8 bytes
 * @param registerNameBytes The WOLA registration name, padded to 16 bytes
 * @param waiterToken The unique waiter token to look for an cancel.
 */
void ntv_cancelWolaClientWaiter(JNIEnv* env, jobject obj,
                                jbyteArray jwolaGroup,
                                jbyteArray jregisterName,
                                jlong waiterToken) {
    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(18),
                    "ntv_cancelWolaClientWaiter",
                    TRACE_DATA_END_PARMS);
    }

    jbyte* wola_group = NULL;
    jsize  wola_group_len = 0;
    jbyte* registerName = NULL;
    jsize  registerName_len = 0;

    pc_wolaCancelClientService_parms parms;
    memset(&parms, 0, sizeof(parms));

    JNI_try {

        // Load the auth stubs
        const server_authorized_function_stubs* auth_stubs_p = getServerAuthorizedFunctionStubs();
        if (auth_stubs_p == NULL) {
            JNI_throwNullPointerException(env, "auth_stubs_p is null. Failed to load authorized PC routines");
        }

        // Pull parm data from Java
        JNI_GetByteArrayElements(wola_group,    env, jwolaGroup,    "jwolaGroup is null", NULL);
        JNI_GetArrayLength(wola_group_len,      env, jwolaGroup,    "jwolaGroup is null", 0);

        JNI_GetByteArrayElements(registerName,  env, jregisterName,    "jregisterName is null", NULL);
        JNI_GetArrayLength(registerName_len,    env, jregisterName,    "jregisterName is null", 0);

        if (TraceActive(trc_level_detailed)) {
             TraceRecord(trc_level_detailed,
                         TP(19),
                         "JNI parms",
                         TRACE_DATA_RAWDATA(wola_group_len, wola_group, "wola_group"),
                         TRACE_DATA_RAWDATA(registerName_len, registerName, "registerName"),
                         TRACE_DATA_RAWDATA(sizeof(jlong), &waiterToken, "Waiter token"),
                         TRACE_DATA_END_PARMS);
        }

        // Validate parms
        if (wola_group_len != 8) {
            JNI_throwIllegalArgumentException(env, "wola_group must be 8 bytes long");
        } else if (registerName_len != 16 ) {
            JNI_throwIllegalArgumentException(env, "registerName must be 16 bytes long");
        }

        // Setup the PC routine parms
        memcpy(&parms.wolaGroup, wola_group, wola_group_len);
        memcpy(&parms.registration, registerName, registerName_len);
        parms.waiterToken = waiterToken;

        // Invoke the PC routine
        int pc_rc = auth_stubs_p->pc_cancelClientService(&parms);
        if (pc_rc != 0) {
            JNI_throwPCRoutineFailedException(env, "pc_cancelClientService", pc_rc);
        }
    }
    JNI_catch(env);

    // Release Java parms
    JNI_ReleaseByteArrayElements(env, jwolaGroup,  wola_group, NULL);
    JNI_ReleaseByteArrayElements(env, jregisterName, registerName, NULL);

    // If there's a pending Java exception, it will be raised here and control
    // will immediately return to the Java caller.
    JNI_reThrowAndReturn(env);

    if (TraceActive(trc_level_detailed)) {
      TraceRecord(trc_level_detailed,
                  TP(20),
                  "ntv_getClientService exit",
                  TRACE_DATA_END_PARMS);
    }
}
