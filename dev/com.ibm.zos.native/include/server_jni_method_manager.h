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

#include <jni.h>

/**
 * Prototype for a registration/initialization method that users can implement
 * to perform any actions prior to method registration.  This callback is similar
 * to the @c JNI_OnLoad callback driven by the JVM.
 *
 * This function will be driven before the framework registers the native methods
 * pointed to by the @c NativeMethodDescriptor structure.
 *
 * @param env the @c JNIEnv reference for the thread performing registration
 * @param extraInfo the object array reference specified at the time
 *     @c registerNatives was called in Java
 *
 * @return JNI_OK on success; any other value for failure
 */
typedef jint (JNICALL* RegistrationFunction)(JNIEnv* env, jclass clazz, jobjectArray extraInfo);

/**
 * Prototype for a deregistration/cleanup method that users can implement to
 * perform any cleanup/tear-down actions required when the bundle hosting the
 * associated class is stopped.  This callback is similar to the @c JNI_OnUnload
 * callback driven by the JVM but is called long before the hosting class loader
 * is garbage collected.
 *
 * @param env the @c JNIEnv reference for the thread that's stopping the bundle
 * @param extraInfo the object array reference presented at the time
 *     @c registerNatives was called in Java
 *
 * @return JNI_OK on success; any other value for failure
 */
typedef jint (JNICALL* DeregistrationFunction)(JNIEnv* env, jclass clazz, jobjectArray extraInfo);

/**
 * Prototype for functions that should be called with native library is loaded
 * during bootstrap.  These functions are equivalent to the @c JNI_OnLoad callbacks
 * driven by the JVM but to prevent duplicate exported symbols, these functions
 * must be exported as <tt>zJNI_OnLoad<em>n</em></tt> where <em>n</em> is a number.
 *
 * The driving code will look for exported symbols of that form starting with n = 1
 * and continuing until the first unresolved symbol is encountered.
 *
 * @param jvm the host JVM
 * @param reserved field as declared by the JNI spec
 *
 * @return JNI_OK on success, JNI_ERR on failures
 */
typedef jint (JNICALL* zJNI_OnLoadFunction)(JavaVM* jvm, void* reserved);

/**
 * Prototype for functions that should be called with native library is unloaded
 * These functions are equivalent to the @c JNI_OnUnload callbacks
 * driven by the JVM but to prevent duplicate exported symbols, these functions
 * must be exported as <tt>zJNI_OnLoad<em>n</em></tt> where <em>n</em> is a number.
 *
 * The driving code will look for exported symbols of that form starting with n = 1
 * and continuing until the first unresolved symbol is encountered.
 *
 * @param jvm the host JVM
 * @param reserved field as declared by the JNI spec
 *
 * @return JNI_OK on success, JNI_ERR on failures
 */
typedef jint (JNICALL* zJNI_OnUnloadFunction)(JavaVM* jvm, void* reserved);

/**
 * Structure used to define information about native method registration
 * callbacks and/or the native method implementations to associate with a
 * class.
 */
typedef struct {
    /**
     * The optional registration/initialization callback for the Java class.
     */
    const RegistrationFunction registrationFunction;

    /**
     * The optional deregistration/cleanup callback for the Java class.
     */
    const DeregistrationFunction deregistrationFunction;

    /**
     * The number of native methods in the @c nativeMethods table.  This may
     * be zero if there are no native methods to register or if method
     * registration was done by the registration function.
     */
    const jint nativeMethodCount;

    /**
     * The native method information to provide to @c RegisterNatives.
     */
    const JNINativeMethod* nativeMethods;
} NativeMethodDescriptor;

