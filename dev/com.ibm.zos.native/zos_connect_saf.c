/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2015
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */

/**
 * @file
 *
 * Native Core Utilities in the server process.
 */
#include <jni.h>

#include "include/security_saf_registry.h"
#include "include/server_jni_method_manager.h"

/**
 * Get the groups associated with a single user.
 *
 * @param env the JNI environment reference provided by the JVM.
 * @param jobj the reference to the object that invoked this method.
 * @param jname the user whose groups we want.
 * @param list the list to be populated with the user&apos;s groups.
 *
 * @return the given java.util.List populated with EBCDIC strings representing
 *         groups associated with the given user.
 */
#pragma export(ntv_zos_connect_saf_getGroupsForUser)
JNIEXPORT jobject JNICALL
ntv_zos_connect_saf_getGroupsForUser(JNIEnv* env, jobject jobj, jbyteArray jname, jobject list);

/**
 * JNI native method signatures for ZosConnectSafServiceImpl.
 */
#pragma convert("ISO8859-1")
static JNINativeMethod zosConnectSafServiceMethods[] = {
    { "ntv_zos_connect_saf_getGroupsForUser",
      "([BLjava/util/List;)Ljava/util/List;",
      (void *) ntv_zos_connect_saf_getGroupsForUser }
};
#pragma convert(pop)

/**
 * NativeMethodDescriptor for ZosConnectSafServiceImpl.
 */
#pragma export(zJNI_com_ibm_ws_zos_connect_saf_internal_ZosConnectSafServiceImpl)
NativeMethodDescriptor zJNI_com_ibm_ws_zos_connect_saf_internal_ZosConnectSafServiceImpl = {
    .registrationFunction = NULL,
    .deregistrationFunction = NULL,
    .nativeMethodCount = sizeof(zosConnectSafServiceMethods) / sizeof(zosConnectSafServiceMethods[0]),
    .nativeMethods = zosConnectSafServiceMethods
};


//---------------------------------------------------------------------
// Get the list of groups that the specified user is a member of.
//---------------------------------------------------------------------
JNIEXPORT jobject JNICALL
ntv_zos_connect_saf_getGroupsForUser(JNIEnv* env, jobject jobj, jbyteArray jname, jobject list) {

    return ntv_getGroupsForUser(env, jobj, jname, list);
}

