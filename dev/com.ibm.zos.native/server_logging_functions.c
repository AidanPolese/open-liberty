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
 * Functions related to native logging.
 */
#include <jni.h>
#include <jvmri.h>
#include <stdlib.h>
#include <stdio.h>
#include <errno.h>
#include <string.h>

#include "include/gen/ihapsa.h"
#include "include/gen/ihaascb.h"
#include "include/mvs_wto.h"
#include "include/ras_tracing.h"
#include "include/server_jni_method_manager.h"
#include "include/server_native_service_tracker.h"
#include "include/server_util_tiot.h"


//---------------------------------------------------------------------
// JNI function declaration and export for ZosLoggingBundleActivator methods
//---------------------------------------------------------------------
#pragma export(ntv_WriteToOperatorConsole)
JNIEXPORT jint JNICALL
ntv_WriteToOperatorConsole(JNIEnv* env, jobject obj, jbyteArray msg);

#pragma export(ntv_isLaunchContextShell)
JNIEXPORT jboolean JNICALL
ntv_isLaunchContextShell(JNIEnv* env, jobject obj);

#pragma export(ntv_isMsgLogDDDefined)
JNIEXPORT jboolean JNICALL
ntv_isMsgLogDDDefined(JNIEnv* env, jobject obj);

#pragma export(ntv_openFile)
JNIEXPORT jlong JNICALL
ntv_openFile(JNIEnv* env, 
             jobject obj, 
             jbyteArray jfileName, 
             jbyteArray jerrorCodes);

#pragma export(ntv_writeFile)
JNIEXPORT jint JNICALL
ntv_writeFile(JNIEnv* env, 
              jobject obj, 
              jlong jfile_p, 
              jbyteArray jmsg, 
              jbyteArray jerrorCodes);

#pragma export(ntv_closeFile)
JNIEXPORT jint JNICALL
ntv_closeFile(JNIEnv* env, 
              jobject obj, 
              jlong jfile_p, 
              jbyteArray jerrorCodes);

//---------------------------------------------------------------------
// JNI native method structure for the ZosLoggingBundleActivator methods
//---------------------------------------------------------------------
#pragma convert("iso8859-1")
static JNINativeMethod nativeZosLoggingBundleActivatorMethods[] = {
    { "ntv_WriteToOperatorConsole",
      "([B)I",
      (void *) ntv_WriteToOperatorConsole },
    { "ntv_isLaunchContextShell",
      "()Z",
      (void *) ntv_isLaunchContextShell },
    { "ntv_openFile",
      "([B[B)J",
      (void *) ntv_openFile},
    { "ntv_closeFile",
      "(J[B)I",
      (void *) ntv_closeFile},
    { "ntv_writeFile",
      "(J[B[B)I",
      (void *) ntv_writeFile},
    { "ntv_isMsgLogDDDefined",
      "()Z",
      (void *) ntv_isMsgLogDDDefined }
};
#pragma convert(pop)

//---------------------------------------------------------------------
// NativeMethodDescriptor for the ZosLoggingBundleActivator methods
//---------------------------------------------------------------------
#pragma export(zJNI_com_ibm_ws_zos_logging_internal_ZosLoggingBundleActivator)
NativeMethodDescriptor zJNI_com_ibm_ws_zos_logging_internal_ZosLoggingBundleActivator = {
   .registrationFunction = NULL,
   .deregistrationFunction = NULL,
   .nativeMethodCount = sizeof(nativeZosLoggingBundleActivatorMethods) / sizeof(nativeZosLoggingBundleActivatorMethods[0]),
   .nativeMethods = nativeZosLoggingBundleActivatorMethods
};



//---------------------------------------------------------------------
// JNI function declaration and export for LoggingHardcopyLogHandler methods
//---------------------------------------------------------------------
#pragma export(ntv_WriteToOperatorProgrammerAndHardcopy)
JNIEXPORT jint JNICALL
ntv_WriteToOperatorProgrammerAndHardcopy(JNIEnv* env, jobject obj, jbyteArray msg);

//---------------------------------------------------------------------
// JNI native method structure for the LoggingHardcopyLogHandler methods
//---------------------------------------------------------------------
#pragma convert("iso8859-1")
static JNINativeMethod nativeLoggingHardcopyLogHandlerMethods[] = {
    { "ntv_WriteToOperatorProgrammerAndHardcopy",
      "([B)I",
      (void *) ntv_WriteToOperatorProgrammerAndHardcopy }
};
#pragma convert(pop)

//---------------------------------------------------------------------
// NativeMethodDescriptor for the LoggingHardcopyLogHandler methods
//---------------------------------------------------------------------
#pragma export(zJNI_com_ibm_ws_zos_logging_internal_LoggingHardcopyLogHandler)
NativeMethodDescriptor zJNI_com_ibm_ws_zos_logging_internal_LoggingHardcopyLogHandler = {
   .registrationFunction = NULL,
   .deregistrationFunction = NULL,
   .nativeMethodCount = sizeof(nativeLoggingHardcopyLogHandlerMethods) / sizeof(nativeLoggingHardcopyLogHandlerMethods[0]),
   .nativeMethods = nativeLoggingHardcopyLogHandlerMethods
};



/**
 * Write input message to programmer and hardcopy log.
 *
 * @param  env the JNI environment reference provided by the JVM.
 * @param  obj the object instance this method was invoked against.
 * @param  msg message to be written to programmer and hardcopy log.
 *
 * @return a return code. 0 success, non 0 failure.
 */
JNIEXPORT jint JNICALL
ntv_WriteToOperatorProgrammerAndHardcopy(JNIEnv* env, jobject obj, jbyteArray msg) {
    jbyte* msg_p = NULL;
    const server_unauthorized_function_stubs* unauth_stubs_p = NULL;
    int returnCode = 0;
    unauth_stubs_p = getServerUnauthorizedFunctionStubs();
    if (unauth_stubs_p) {
        if (msg != NULL) {
            msg_p = (*env)->GetByteArrayElements(env, msg, NULL);
            if (msg_p != NULL) {
                WtoLocation location = WTO_PROGRAMMER_HARDCOPY;
                returnCode = unauth_stubs_p->write_to_operator_unauthorized_routine((char*)msg_p, &location);
                (*env)->ReleaseByteArrayElements(env, msg, msg_p, 0);
            } else {
                returnCode = -100;
            }
        } else {
            returnCode = -102;
        }
    } else {
        returnCode = -101;
    }
    return returnCode;
    
}

/**
 * Write input message to operator console.
 *
 * @param  env the JNI environment reference provided by the JVM.
 * @param  obj the object instance this method was invoked against.
 * @param  msg message to be written to the operator console.
 *
 * @return a return code. 0 success, non 0 failure.
 */
JNIEXPORT jint JNICALL
ntv_WriteToOperatorConsole(JNIEnv* env, jobject obj, jbyteArray msg) {
    jbyte* msg_p = NULL;
    const server_unauthorized_function_stubs* unauth_stubs_p = NULL;
    int returnCode = 0;
    unauth_stubs_p = getServerUnauthorizedFunctionStubs();
    if (unauth_stubs_p) {
        if (msg != NULL) {
            msg_p = (*env)->GetByteArrayElements(env, msg, NULL);
            if (msg_p != NULL) {
                WtoLocation location = WTO_OPERATOR_CONSOLE;
                returnCode = unauth_stubs_p->write_to_operator_unauthorized_routine((char*)msg_p, &location);
                (*env)->ReleaseByteArrayElements(env, msg, msg_p, 0);
            } else {
                returnCode = -100;
            }
        } else {
            returnCode = -102;
        }
    } else {
        returnCode = -101;
    }
    return returnCode;
    
}

/**
 * Determine whether the server was started in Unix shell or not.
 *
 * For started tasks, ascbjbni will be zero. For forked/spawned JVMs
 * it will be BPXAS.
 *
 * @param  env the JNI environment reference provided by the JVM.
 * @param  obj the object instance this method was invoked against.
 *
 * @return true if the launch context is forked/spawned, false for started tasks
 */
JNIEXPORT jboolean JNICALL
ntv_isLaunchContextShell(JNIEnv* env, jobject obj) {
    psa* psa_p = NULL;
    ascb* ascb_p = psa_p->psaaold;
    return ((ascb_p->ascbjbni != NULL) ? JNI_TRUE : JNI_FALSE);
}

/**
 *
 * @param  env the JNI environment reference provided by the JVM.
 * @param  obj the object instance this method was invoked against.
 *
 * @return true if the MSGLOG DD card is defined.
 */
JNIEXPORT jboolean JNICALL
ntv_isMsgLogDDDefined(JNIEnv* env, jobject obj) {
    return ( tiot_isDDDefined(tiot_getTiot(), "MSGLOG") ) ? JNI_TRUE : JNI_FALSE;
}

/**
 * Set given error codes into the given jerrorCodes byte array and return the given rc.
 *
 * @return rc 
 */
static long setErrorCodes(long rc, JNIEnv* env, jbyteArray jerrorCodes, int err1, int err2) {

    if ( jerrorCodes != NULL && (*env)->GetArrayLength(env, jerrorCodes) >= 2 * sizeof(int)) {
        (*env)->SetByteArrayRegion(env, jerrorCodes, 0, sizeof(int), (jbyte*) &err1);
        (*env)->SetByteArrayRegion(env, jerrorCodes, sizeof(int), sizeof(int), (jbyte*) &err2);
    }

    return rc;
}

/**
 * Open the given file.
 *
 * @param  env the JNI environment reference provided by the JVM.
 * @param  obj the object instance this method was invoked against.
 * @param  jfileName - null-term'ed
 * @param  jerrorCodes - output parm - errno and errno2, if the file could not be opened.
 *
 * @return the FILE *, or 0 if the file could not be opened (jerrorCodes set).
 */
JNIEXPORT jlong JNICALL
ntv_openFile(JNIEnv* env, jobject obj, jbyteArray jfileName, jbyteArray jerrorCodes) {

    if (jfileName == NULL) {
        return setErrorCodes( 0, env, jerrorCodes, -1, -1);
    }

    jbyte* fileName_p = (*env)->GetByteArrayElements(env, jfileName, NULL);

    if (fileName_p == NULL) {
        return setErrorCodes( 0, env, jerrorCodes, -2, -2);
    }

    // Clear errno/errno2 before the call
    errno = 0;
    __err2ad();

    FILE * file_p = fopen( (const char *) fileName_p, "a" );
    (*env)->ReleaseByteArrayElements(env, jfileName, fileName_p, 0);

    if (file_p == NULL) {
        return setErrorCodes( 0, env, jerrorCodes, errno, __errno2());
    }

    return (jlong) file_p;
}

/**
 * Close the given file.
 *
 * @param  env the JNI environment reference provided by the JVM.
 * @param  obj the object instance this method was invoked against.
 * @param  jfile_p - the file to close
 * @param  jerrorCodes - output parm - errno and errno2, if the file could not be opened.
 *
 * @return 0 if all is well; non-zero otherwise (jerrorCodes set).
 */
JNIEXPORT jint JNICALL
ntv_closeFile(JNIEnv* env, jobject obj, jlong jfile_p, jbyteArray jerrorCodes) {

    // Clear errno/errno2 before the call
    errno = 0;
    __err2ad();

    int rc = fclose((FILE *) jfile_p);

    return (rc == 0) ? 0 : (jint) setErrorCodes( rc, env, jerrorCodes, errno, __errno2() ) ;
}

/**
 * Write to the given file.
 *
 * @param  env the JNI environment reference provided by the JVM.
 * @param  obj the object instance this method was invoked against.
 * @param  jfile_p FILE * to DD:MSGLOG.  If NULL, the file is opened.  
 * @param  jmsg message to be written (null-term'ed)
 * @param  jerrorCodes - output parm - errno and errno2
 *
 * @return a return code. 0 success, non 0 failure (jerrorCodes set)
 */
JNIEXPORT jint JNICALL
ntv_writeFile(JNIEnv* env, 
              jobject obj, 
              jlong jfile_p, 
              jbyteArray jmsg, 
              jbyteArray jerrorCodes) {

    if (jmsg == NULL) {
        return (jint) setErrorCodes( -1, env, jerrorCodes, -1, -1);
    }

    if (jfile_p == 0) {
        return (jint) setErrorCodes( -2, env, jerrorCodes, -2, -2);
    }

    jbyte* msg_p = (*env)->GetByteArrayElements(env, jmsg, NULL);
    int msgLen = strlen((const char *)msg_p);

    if (msg_p == NULL) {
        return (jint) setErrorCodes( -3, env, jerrorCodes, -3, -3);
    }

    // Clear errno/errno2 before the call
    errno = 0;
    __err2ad();

    int rc = fwrite( (void *) msg_p, 1, msgLen, (FILE *) jfile_p );

    (*env)->ReleaseByteArrayElements(env, jmsg, msg_p, 0);

    return (rc == msgLen) ? 0 : (jint) setErrorCodes((long)rc, env, jerrorCodes, errno, __errno2()) ;
}


