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
 * Functions in support of hosting Java native methods for multiple bundles in
 * a single DLL.
 */
#include <assert.h>
#include <dlfcn.h>
#include <errno.h>
#include <jni.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#include "include/server_jni_method_manager.h"
#include "include/server_nls_messages.h"
#include "include/gen/native_messages.h"

/**
 * Implementation of a mechanism to load and link native method entry points that
 * live in the server DLL.
 *
 * @param env the JNI environment reference from the JVM
 * @param myClazz the jclass reference to the NativeMethodHelper class
 * @param clazz the jclass reference to the class with native methods to be registered
 * @param descriptorName the name of the NativeMethodDescriptor structure to resolve
 * @param extraInfo extra information provided by the caller
 *
 * @returns -1 on error
 * @returns 0 when descriptor name was not found
 * @returns DLL handle when the library was successfully registered
 */
#pragma export(ntv_registerNatives)
JNIEXPORT jlong JNICALL
ntv_registerNatives(JNIEnv* env, jclass myClazz, jclass clazz, jstring descriptorName, jobjectArray extraInfo);

/**
 * Implementation of a mechanism to notify native code that the host bundle has
 * stopped and native infrastructure should be cleaned up.
 *
 * @param env the JNI environment reference from the JVM
 * @param myClazz the jclass reference to the NativeMethodHelper calss
 * @param dllHandle the DLL handle returned from dlopen at registration
 * @param clazz the jclass reference to the class with native method to be registered
 * @param descriptorName the name of the NativeMethodDescriptor structure to resolve
 * @param extraInfo extra information provided by the caller at registration
 */
#pragma export(ntv_deregisterNatives)
JNIEXPORT jlong JNICALL
ntv_deregisterNatives(JNIEnv* env, jclass myClazz, jlong dllHandle, jclass clazz, jstring descriptorName, jobjectArray extraInfo);

/**
 * Distribute the zJNI_OnLoad<em>n</em> callbacks.
 *
 * @param jvm the host JVM
 * @param reserved the reserved word passed to @c JNI_OnLoad
 *
 * @return JNI_OK on success, JNI_ERR on failure
 */
static jint distributeOnLoadCallbacks(JavaVM* jvm, void* reserved);

/**
 * Distribute the zJNI_OnUnload<em>n</em> callbacks.
 *
 * @param jvm the host JVM
 * @param reserved the reserved word passed to @c JNI_OnLoad
 */
static void distributeOnUnloadCallbacks(JavaVM* jvm, void* reserved);

//---------------------------------------------------------------------
// Data required for the local call to RegisterNatives
//---------------------------------------------------------------------
#pragma convert("ISO8859-1")
static const char* nativeMethodHelperClassName =
    "com/ibm/ws/kernel/boot/delegated/zos/NativeMethodHelper";

static const char* nativeUnittestMethodHelperClassName =
    "test/common/zos/NativeLibraryUtils";

static const JNINativeMethod nativeMethodHelperMethods[] = {
    { "ntv_registerNatives",
      "(Ljava/lang/Class;Ljava/lang/String;[Ljava/lang/Object;)J",
      (void *) ntv_registerNatives },
    { "ntv_deregisterNatives",
      "(JLjava/lang/Class;Ljava/lang/String;[Ljava/lang/Object;)J",
      (void *) ntv_deregisterNatives }
};

static const int nativeMethodHelperMethodCount =
    sizeof(nativeMethodHelperMethods) /
    sizeof(nativeMethodHelperMethods[0]);

static const char* libraryNameFieldName = "libraryName";

static const char* libraryNameFieldSig = "Ljava/lang/String;";
#pragma convert(pop)

//---------------------------------------------------------------------
// Cached reference to the hosting library's name in EBCDIC
//---------------------------------------------------------------------
static char* libraryName = NULL;

//---------------------------------------------------------------------
// Handle to our hosting dll so we can resolve zJNI_OnLoadx functions
//---------------------------------------------------------------------
static void* onLoadDllHandle = NULL;

//---------------------------------------------------------------------
// Callback from the JVM used to perform DLL specific initialization.
//---------------------------------------------------------------------
#pragma export(JNI_OnLoad)
JNIEXPORT jint JNICALL
JNI_OnLoad(JavaVM* jvm, void* reserved) {
    int         returnCode              = 0;
    JNIEnv*     env                     = NULL;
    jclass      nativeMethodHelperClazz = NULL;
    const char* helperClassName         = nativeMethodHelperClassName;

    (*jvm)->GetEnv(jvm, (void **) &env, JNI_VERSION_1_6);
    assert(env);

    // See if we're running in a unit test environment
    if (getenv("ZOS_NATIVE_UNIT_TEST_ENV")) {
        helperClassName = nativeUnittestMethodHelperClassName;
    }

    // Find the class for method registration
    nativeMethodHelperClazz = (*env)->FindClass(env, helperClassName);
    assert(nativeMethodHelperClazz != NULL);

    returnCode = (*env)->RegisterNatives(
            env,
            nativeMethodHelperClazz,
            nativeMethodHelperMethods,
            nativeMethodHelperMethodCount);
    if (returnCode != JNI_OK) {
        return JNI_ERR;
    }

    // Resolve the field containing the name used during load
    jfieldID libraryNameFieldID = (*env)->GetStaticFieldID(
            env,
            nativeMethodHelperClazz,
            libraryNameFieldName,
            libraryNameFieldSig);
    if (libraryNameFieldID == NULL) {
        return JNI_ERR;
    }

    jstring jLibraryName = (jstring) (*env)->GetStaticObjectField(
            env,
            nativeMethodHelperClazz,
            libraryNameFieldID);
    if (jLibraryName == NULL) {
        return JNI_ERR;
    }

    const char* utfLibraryName = (*env)->GetStringUTFChars(env, jLibraryName, NULL);
    if (utfLibraryName != NULL) {
        // Convert from UTF8 to EBCDIC
        libraryName = strdup(utfLibraryName);
        if (libraryName) {
            __atoe(libraryName);
        }
    }
    (*env)->ReleaseStringUTFChars(env, jLibraryName, utfLibraryName);

    // Distribute the zJNI_OnLoad calls
    if (distributeOnLoadCallbacks(jvm, reserved) != JNI_OK) {
        return JNI_ERR;
    }

    return JNI_VERSION_1_6;
}

//---------------------------------------------------------------------
// Callback from the JVM when the class loader responsible for loading
// the DLL has been garbage collected.  This can be used for cleanup.
//---------------------------------------------------------------------
#pragma export(JNI_OnUnload)
JNIEXPORT void  JNICALL
JNI_OnUnload(JavaVM* jvm, void* reserved) {
    // Distribute the zJNI_OnUnload calls
    distributeOnUnloadCallbacks(jvm, reserved);

    // We should be loaded by the framework or application loader so this
    // probably won't happen.
    if (libraryName != NULL) {
        free(libraryName);
        libraryName = NULL;
    }
}

//---------------------------------------------------------------------
// Attempt to resolve zJNI_OnLoadx functions and call them
//---------------------------------------------------------------------
jint
distributeOnLoadCallbacks(JavaVM* jvm, void* reserved) {
    // Load and resolve the DLL
    onLoadDllHandle = dlopen(libraryName, RTLD_LAZY | RTLD_LOCAL);
    if (onLoadDllHandle == NULL) {
        char* msg = getTranslatedMessageById(SERVER_JNI_01, "Failed to open %s: %s\n");
        fprintf(stderr, msg, libraryName, dlerror());
        fprintf(stderr, "\n");
        return JNI_ERR;
    }

    int maxSymbolLength = strlen("zJNI_OnLoad") + 4; // 1-999 + null
    char symbol[maxSymbolLength];
    zJNI_OnLoadFunction onLoadFunction = NULL;

    // Look for zJNI_OnLoad functions and call them
    for (int i = 1; i < 1000; onLoadFunction = NULL, i++) {
        snprintf(symbol, maxSymbolLength, "zJNI_OnLoad%d", i);
        onLoadFunction = (zJNI_OnLoadFunction) dlsym(onLoadDllHandle, symbol);
        if (onLoadFunction == NULL) {
            break;
        }

        // Call the function; errors are terminal
        if (onLoadFunction(jvm, reserved) != JNI_OK) {
            // message
            return JNI_ERR;
        }
    }

    return JNI_OK;
}

//---------------------------------------------------------------------
// Attempt to resolve zJNI_OnUnloadx functions and call them
//---------------------------------------------------------------------
void
distributeOnUnloadCallbacks(JavaVM* jvm, void* reserved) {
    if (onLoadDllHandle == NULL) {
        return;
    }

    int maxSymbolLength = strlen("zJNI_OnUnload") + 4; // 1-999 + null
    char symbol[maxSymbolLength];
    zJNI_OnUnloadFunction onUnloadFunction = NULL;

    // Look for zJNI_OnUnload functions and call them
    for (int i = 1; i < 1000; onUnloadFunction = NULL, i++) {
        snprintf(symbol, maxSymbolLength, "zJNI_OnUnload%d", i);
        onUnloadFunction = (zJNI_OnUnloadFunction) dlsym(onLoadDllHandle, symbol);
        if (onUnloadFunction == NULL) {
            break;
        }

        // Call the function; errors are terminal
        onUnloadFunction(jvm, reserved);
    }

    // Try to clean up our on load reference
    dlclose(onLoadDllHandle);
    onLoadDllHandle = NULL;
}


//---------------------------------------------------------------------
// Load and link the native methods for the specified class using the
// information in the named descriptor.
//---------------------------------------------------------------------
JNIEXPORT jlong JNICALL
ntv_registerNatives(JNIEnv* env, jclass myClazz, jclass clazz, jstring jdescriptorName, jobjectArray extraInfo) {

    char* descriptorName = NULL; // Allocated on stack w/ alloca
    NativeMethodDescriptor* desc = NULL;

    const char* utfDescriptorName = (*env)->GetStringUTFChars(env, jdescriptorName, NULL);
    if (utfDescriptorName != NULL) {
        descriptorName = alloca(strlen(utfDescriptorName) + 1);
        strcpy(descriptorName, utfDescriptorName);
        __atoe(descriptorName);
        (*env)->ReleaseStringUTFChars(env, jdescriptorName, utfDescriptorName);
    }

    // Load and resolve the DLL
    void* dllHandle = dlopen(libraryName, RTLD_LAZY | RTLD_LOCAL);
    if (dllHandle == NULL) {
        char* msg = getTranslatedMessageById(SERVER_JNI_01, "Failed to open %s: %s\n");
        fprintf(stderr, msg, libraryName, dlerror());
        fprintf(stderr, "\n");
        return -1;
    }

    // Find the structure called out by the caller
    desc = (NativeMethodDescriptor*) dlsym(dllHandle, descriptorName);
    if (!desc) {
        dlclose(dllHandle);
        return 0;
    }

    // Drive the registration callback.  If the callback returns with an error,
    // close the DLL and return a bad return code.
    if (desc->registrationFunction) {
        jint rc = (*desc->registrationFunction)(env, clazz, extraInfo);
        if (rc != JNI_OK) {
            char* msg = getTranslatedMessageById(SERVER_JNI_02, "Method registration function in descriptor \"%s\" returned %s\n");
            char buffer[20];
            sprintf(buffer, "%d", rc);
            fprintf(stderr, msg, descriptorName, buffer);
            fprintf(stderr, "\n");
            dlclose(dllHandle);
            return -1;
        };
    }

    if (desc->nativeMethods) {
        jint rc = (*env)->RegisterNatives(env, clazz, desc->nativeMethods, desc->nativeMethodCount);
        if (rc != JNI_OK) {
            char* msg = getTranslatedMessageById(SERVER_JNI_03, "RegisterNatives for descriptor \"%s\" failed with %s\n");
            char buffer[20];
            sprintf(buffer, "%d", rc);
            fprintf(stderr, msg, descriptorName, buffer);
            fprintf(stderr, "\n");
            dlclose(dllHandle);
            return -1;
        }
    }

    return (jlong) dllHandle;
}

//---------------------------------------------------------------------
// Drive the deregistration callback for a class to notify it that the
// host bundle has stopped.  The DLL handle associated with the class
// will also be closed.
//---------------------------------------------------------------------
JNIEXPORT jlong JNICALL
ntv_deregisterNatives(JNIEnv* env, jclass myClazz, jlong jdllHandle, jclass clazz, jstring jdescriptorName, jobjectArray extraInfo) {

    void* dllHandle = (void*) jdllHandle;
    char* descriptorName = NULL; // Allocated on stack w/ alloca
    NativeMethodDescriptor* desc = NULL;

    const char* utfDescriptorName = (*env)->GetStringUTFChars(env, jdescriptorName, NULL);
    if (utfDescriptorName != NULL) {
        descriptorName = alloca(strlen(utfDescriptorName) + 1);
        strcpy(descriptorName, utfDescriptorName);
        __atoe(descriptorName);
        (*env)->ReleaseStringUTFChars(env, jdescriptorName, utfDescriptorName);
    }

    // Find the structure called out by the caller.  If we don't find it on
    // deregister, soemthing is wrong.
    desc = (NativeMethodDescriptor*) dlsym(dllHandle, descriptorName);
    if (!desc) {
        dlclose(dllHandle);
        return -1;
    }

    // Drive the deregistration callback.  If the callback returns with an error,
    // close the DLL anyway but raise a bad return code.
    if (desc->registrationFunction) {
        jint rc = (*desc->deregistrationFunction)(env, clazz, extraInfo);
        if (rc != JNI_OK) {
            char* msg = getTranslatedMessageById(SERVER_JNI_04, "Method de-registration function in descriptor \"%s\" returned %s\n");
            char buffer[20];
            sprintf(buffer, "%d", rc);
            fprintf(stderr, msg, libraryName, buffer);
            fprintf(stderr, "\n");
            dlclose(dllHandle);
            return -1;
        };
    }

    // Language environment likes to return a bad return code from dlclose
    // when the DLL was logically deleted but not physically deleted.  As
    // this is a normal occurrence, we don't want to call attention to it
    // if possible.  Unfortunately, I can't find documentation that defines
    // the errno/errno2 that gets set so I'm using what we've observed at
    // runtime as the filter
    const int LOGICALLY_DELETED_ERRNO = 103567;     // 0x1948f
    const int LOGICALLY_DELETED_ERRNO2 = 86180152;   // 0x5230138

    int rc = dlclose(dllHandle);
    if (rc != 0 && errno != LOGICALLY_DELETED_ERRNO && __errno2() != LOGICALLY_DELETED_ERRNO2) {
        char* msg = getTranslatedMessageById(SERVER_JNI_05, "Failed to close %s: %s\n");
        fprintf(stderr, msg, libraryName, dlerror());
        fprintf(stderr, "\n");
        return -1;
    }

    return 0;
}


