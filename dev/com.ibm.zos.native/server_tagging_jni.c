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

#include <_Ccsid.h>
#include <errno.h>
#include <fcntl.h>
#include <jni.h>
#include <jvmti.h>
#include <pthread.h>
#include <stdlib.h>
#include <string.h>
#include <sys/stat.h>

#include "include/server_jni_method_manager.h"

//---------------------------------------------------------------------
// File tagging JNI function declaration and exports
//---------------------------------------------------------------------
#pragma export(ntv_setFileTag)
JNIEXPORT jint JNICALL
ntv_setFileTag(JNIEnv* env, jobject jthis, jstring jpath, jint codesetId);

#pragma export(ntv_getCcsid)
JNIEXPORT jint JNICALL
ntv_getCcsid(JNIEnv* env, jclass clazz, jstring encoding);

//---------------------------------------------------------------------
// Native method declarations for file tagging
//---------------------------------------------------------------------
#pragma convert("ISO8859-1")
static const JNINativeMethod taggedOutputStreamMethods[] = {
    { "ntv_setFileTag",
      "(Ljava/lang/String;I)I",
      (void *) ntv_setFileTag },
    { "ntv_getCcsid",
      "(Ljava/lang/String;)I",
      (void *) ntv_getCcsid }
};
#pragma convert(pop)

//---------------------------------------------------------------------
// NativeMethodDescriptor for file tagging
//---------------------------------------------------------------------
#pragma export(zJNIBOOT_com_ibm_ws_kernel_boot_logging_TaggedFileOutputStream)
NativeMethodDescriptor zJNIBOOT_com_ibm_ws_kernel_boot_logging_TaggedFileOutputStream = {
    .registrationFunction = NULL,
    .deregistrationFunction = NULL,
    .nativeMethodCount = sizeof(taggedOutputStreamMethods) / sizeof(taggedOutputStreamMethods[0]),
    .nativeMethods = taggedOutputStreamMethods
};

//---------------------------------------------------------------------
// Callback methods for the JVMTI based thread tracker
//---------------------------------------------------------------------
int threadTrackerRegistration(JNIEnv* env, jclass clazz, jobjectArray extraInfo);

int threadTrackerDeregistration(JNIEnv* env, jclass clazz, jobjectArray extraInfo);

void onThreadStart(jvmtiEnv* jvmti, JNIEnv* env, jthread thread);

void onThreadEnd(jvmtiEnv* jvmti, JNIEnv* env, jthread thread);

//---------------------------------------------------------------------
// NativeMethodDescriptor for JVMTI based thread tracker
//---------------------------------------------------------------------
#pragma export(zJNI_com_ibm_ws_zos_core_thread_internal_ThreadTracker)
NativeMethodDescriptor zJNI_com_ibm_ws_zos_core_thread_internal_ThreadTracker = {
    .registrationFunction = threadTrackerRegistration,
    .deregistrationFunction = threadTrackerDeregistration,
    .nativeMethodCount = 0,
    .nativeMethods = NULL
};

//---------------------------------------------------------------------
// Module scoped constants
//---------------------------------------------------------------------
/**
 * The documented maximum length of the pthread tag string.
 */
#define MAX_TAG_LENGTH 65

//---------------------------------------------------------------------
// Module scoped functions
//---------------------------------------------------------------------
static jint setThreadTag(JNIEnv* env, jobject thread);

//---------------------------------------------------------------------
// Module scoped data
//---------------------------------------------------------------------
/**
 * The JVMTI environment for our "agent".  This is obtained in the tracker
 * registration callback and released in the deregistration callback.
 */
static jvmtiEnv* jvmti = NULL;

/**
 * The @c ThreadTracker @c threadStarted method ID.
 */
static jmethodID threadStartedMethodId = NULL;

/**
 * The @c ThreadTracker @c threadTerminating method ID.
 */
static jmethodID threadTerminatingMethodId = NULL;

/**
 * The @c ThreadTracker instance that events are delegated to.
 */
static jobject threadTracker = NULL;

/**
 * The @c java.lang.Thread @c getId method ID.
 */
static jmethodID threadGetIdMethodId = NULL;

/**
 * The @c java.lang.Thread @c getName method ID.
 */
static jmethodID threadGetNameMethodId = NULL;

//---------------------------------------------------------------------
// Function implementations
//---------------------------------------------------------------------

/**
 * Callback used to wire ourselves into JVMTI thread events.
 *
 * @param jvm a reference to the hosting JVM
 * @param reserved the reserved work from JNI_OnLoad
 *
 * @return JNI_OK on success, JNI_ERR on failure
 */
#pragma export(zJNI_OnLoad1)
JNIEXPORT jint JNICALL
zJNI_OnLoad1(JavaVM* jvm, void* reserved) {
    JNIEnv* env = NULL;

    // Get a JNI environment from the JVM
    (*jvm)->GetEnv(jvm, (void **) &env, JNI_VERSION_1_6);
    if (env == NULL) {
        return JNI_ERR;
    }

    // Get a reference to the thread class
#pragma convert("ISO8859-1")
    jclass threadClass = (*env)->FindClass(env, "java/lang/Thread");
#pragma convert(pop)
    if (threadClass == NULL) {
        return JNI_ERR;
    }

    // Resolve the thread methods we'll need
#pragma convert("ISO8859-1")
    threadGetIdMethodId = (*env)->GetMethodID(env, threadClass, "getId", "()J");
    threadGetNameMethodId = (*env)->GetMethodID(env, threadClass, "getName", "()Ljava/lang/String;");
#pragma convert(pop)

    if (threadGetIdMethodId == NULL || threadGetNameMethodId == NULL) {
        return JNI_ERR;
    }

    // Get a JVMTI environment
    if ((*jvm)->GetEnv(jvm, (void**) &jvmti, JVMTI_VERSION_1_1) != JNI_OK) {
        return JNI_ERR;
    }

    // Get the current thread and attempt to tag it
    jthread jcurrentThread = NULL;
    (*jvmti)->GetCurrentThread(jvmti, &jcurrentThread);
    if (jcurrentThread != NULL) {
        setThreadTag(env, jcurrentThread);
    }

    // Create an event callback structure
    jvmtiEventCallbacks eventCallbacks = { NULL };
    eventCallbacks.ThreadStart = onThreadStart;
    eventCallbacks.ThreadEnd = onThreadEnd;

    // Set the event callback functions into our environment
    jvmtiError error = (*jvmti)->SetEventCallbacks(jvmti, &eventCallbacks, sizeof(eventCallbacks));
    if (error != JVMTI_ERROR_NONE) {
        return JNI_ERR;
    }

    // Enable the thread start event
    error = (*jvmti)->SetEventNotificationMode(jvmti, JVMTI_ENABLE, JVMTI_EVENT_THREAD_START, NULL);
    if (error != JVMTI_ERROR_NONE) {
        return JNI_ERR;
    }

    // Enable the thread end event
    error = (*jvmti)->SetEventNotificationMode(jvmti, JVMTI_ENABLE, JVMTI_EVENT_THREAD_END, NULL);
    if (error != JVMTI_ERROR_NONE) {
        return JNI_ERR;
    }

    return JNI_OK;
}

/**
 * Callback used to clean up our JVMTI registration.
 *
 * @param jvm a reference to the hosting JVM
 * @param reserved the reserved work from JNI_OnLoad
 */
#pragma export(zJNI_OnUnload1)
JNIEXPORT void JNICALL
zJNI_OnUnload1(JavaVM* jvm, void* reserved) {
    (*jvmti)->DisposeEnvironment(jvmti);

    // Clear our references
    threadGetIdMethodId = NULL;
    threadGetNameMethodId = NULL;
    jvmti = NULL;
}

/**
 * Callback from the native method manager that we use to setup for JVMTI
 * thread events.
 *
 * @param env the JNI environment for this thread
 * @param clazz the that's being registered
 * @param extraInfo extra information from the caller
 *
 * @return JNI_OK on success; JNI_ERR on error
 */
JNIEXPORT int JNICALL
threadTrackerRegistration(JNIEnv* env, jclass clazz, jobjectArray extraInfo) {
    JavaVM* jvm = NULL;

    // Grab a reference to the thread tracker instance passed in extraInfo
    threadTracker = (*env)->GetObjectArrayElement(env, extraInfo, 0);
    threadTracker = (*env)->NewGlobalRef(env, threadTracker);
    if (threadTracker == NULL) {
        return JNI_ERR;
    }

    // Get the callback methods
#pragma convert("ISO8859-1")
    threadStartedMethodId = (*env)->GetMethodID(env, clazz, "threadStarted", "()V");
    threadTerminatingMethodId = (*env)->GetMethodID(env, clazz, "threadTerminating", "()V");
#pragma convert(pop)
    if (threadStartedMethodId == NULL || threadTerminatingMethodId == NULL) {
        return JNI_ERR;
    }

    return JNI_OK;
}

/**
 * Callback from the native method manager that we use to cleanup from JVMTI
 * thread events.
 *
 * @param env the JNI environment for this thread
 * @param clazz the that's being registered
 * @param extraInfo extra information from the caller
 *
 * @return JNI_OK on success; JNI_ERR on error
 */
JNIEXPORT int JNICALL
threadTrackerDeregistration(JNIEnv* env, jclass clazz, jobjectArray extraInfo) {

    (*env)->DeleteGlobalRef(env, threadTracker);
    threadTracker = NULL;
    threadStartedMethodId = NULL;
    threadTerminatingMethodId = NULL;

    return JNI_OK;
}

/**
 * @c JVMTI_EVENT_THREAD_START event handler.  This handler will call the
 * thread tracker to properly handle the notification.
 *
 * @param jvmti the JVMTI environment
 * @param env the JNI environment
 * @param thread the java thread that's being initialized
 */
JNIEXPORT void JNICALL
onThreadStart(jvmtiEnv* jvmti, JNIEnv* env, jthread thread) {
    // Tag the thread
    setThreadTag(env, thread);

    if (threadTracker && threadStartedMethodId) {
        (*env)->CallVoidMethod(env, threadTracker, threadStartedMethodId);
    }
    (*env)->ExceptionClear(env);
}

/**
 * @c JVMTI_EVENT_THREAD_END event handler.  This handler will call the
 * thread tracker to properly handle the notification.
 *
 * @param jvmti the JVMTI environment
 * @param env the JNI environment
 * @param thread the java thread that's ending
 */
JNIEXPORT void JNICALL
onThreadEnd(jvmtiEnv* jvmti, JNIEnv* env, jthread thread) {
    if (threadTracker && threadTerminatingMethodId) {
        (*env)->CallVoidMethod(env, threadTracker, threadTerminatingMethodId);
    }
    (*env)->ExceptionClear(env);
}

/**
 * Tag the current thread with the specified diagnostic string.
 * The diagnostic string is displayed in the output of
 * @code D OMVS,PID=${pid} @endcode .
 *
 * @param env the current JNI environment
 * @param thread the thread that was created
 *
 * @return 0 on success, non-zero on error
 */
jint
setThreadTag(JNIEnv* env, jobject thread) {
    char newTag[MAX_TAG_LENGTH + 1] = "";
    int returnCode = -1;
    char* threadName = NULL;

    jlong jthreadId = (*env)->CallLongMethod(env, thread, threadGetIdMethodId);
    jstring jthreadName = (*env)->CallObjectMethod(env, thread, threadGetNameMethodId);

    // If there's no name, there's no tag
    if (jthreadName == NULL) {
        return -2;
    }

    // Get the thread name into EBCDIC
    const char* utfThreadName = (*env)->GetStringUTFChars(env, jthreadName, NULL);
    if (utfThreadName) {
        threadName = alloca(strlen(utfThreadName) + 1);
        strcpy(threadName, utfThreadName);
        __atoe(threadName);
        (*env)->ReleaseStringUTFChars(env, jthreadName, utfThreadName);
    }

    // If we didn't get the name in EBCDIC, no tag
    if (threadName == NULL) {
        return -3;
    }

    // Format the tag and set it
    snprintf(newTag, sizeof(newTag), "(%ld) %s", (long long) jthreadId, threadName);
    returnCode = pthread_tag_np(newTag, NULL);
    if (returnCode != 0) {
        returnCode = errno;
    }

    return returnCode;
}

/**
 * Tag the specified text file with the specified code page.
 *
 * @param env the current JNI environment
 * @param jthis the object instance used to invoke this method
 * @param jpath the path to the text file to tag
 * @param jcodepage the codepage to associate with the text file
 *
 * @return 0 on success, non zero on error
 */
JNIEXPORT jint JNICALL
ntv_setFileTag(JNIEnv* env, jobject jthis, jstring jpath, jint jccsid) {
    __ccsid_t codesetId = (__ccsid_t) jccsid;
    char* path = NULL;

    const char* utfPath = (*env)->GetStringUTFChars(env, jpath, NULL);
    if (utfPath) {
        path = alloca(strlen(utfPath) + 1);
        strcpy(path, utfPath);
        __atoe(path);
        (*env)->ReleaseStringUTFChars(env, jpath, utfPath);
    }

    // Setup the extended attributes
    attrib_t attributes;
    memset(&attributes, 0, sizeof(attributes));
    attributes.att_filetagchg = 1;
    attributes.att_filetag.ft_ccsid = codesetId;
    attributes.att_filetag.ft_txtflag = 1;

    // Attempt to set the attributes
    if (__chattr(path, &attributes, sizeof(attributes)) != 0) {
        return errno;
    }

    return 0;
}

/**
 * Get the native ccsid for the specified encoding.
 *
 * @param env the JNI environment
 * @param clazz the class that this native method is linked to
 * @param jencoding the encoding alias from the JVM
 *
 * @return the code set identifier or 0 if the ccsid could not be determined
 */
JNIEXPORT jint JNICALL
ntv_getCcsid(JNIEnv* env, jclass clazz, jstring jencoding) {
    char* encoding = NULL;

    const char* utfEncoding = (*env)->GetStringUTFChars(env, jencoding, NULL);
    if (utfEncoding) {
        encoding = alloca(strlen(utfEncoding) + 1);
        strcpy(encoding, utfEncoding);
        __atoe(encoding);
        (*env)->ReleaseStringUTFChars(env, jencoding, utfEncoding);
    }

    __ccsid_t codesetId = __toCcsid(encoding);

    return (jint) codesetId;
}
