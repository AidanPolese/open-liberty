/*
 * security_saf_registry.h
 *
 *  Created on: Jun 11, 2015
 *      Author: spewak
 */

#ifndef SECURITY_SAF_REGISTRY_H_
#define SECURITY_SAF_REGISTRY_H_

/**
 * Get the groups associated with a single user from the SAF registry.
 *
 * @param env the JNI environment reference provided by the JVM.
 * @param jobj the reference to the SAFRegistry object that invoked this method.
 * @param jname the user whose groups we want.
 * @param list the list to be populated with the user&apos;s groups.
 *
 * @return the given java.util.List populated with EBCDIC strings representing
 *         groups associated with the given user.
 */
JNIEXPORT jobject JNICALL
ntv_getGroupsForUser(JNIEnv* env, jobject jobj, jbyteArray jname, jobject list);

#endif /* SECURITY_SAF_REGISTRY_H_ */
