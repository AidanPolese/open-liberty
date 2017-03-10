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
 * Native functions in support of SAFRegistry (com.ibm.ws.security.registry.saf).
 */
#include <errno.h>
#include <grp.h>
#include <jni.h>
#include <pwd.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>

#include "include/gen/ihapsa.h"
#include "include/gen/cvt.h"

#include "include/ras_tracing.h"
#include "include/security_saf_authentication.h"
#include "include/security_saf_authorization.h"
#include "include/server_jni_method_manager.h"
#include "include/server_native_service_tracker.h"
#include "include/util_jni.h"

//---------------------------------------------------------------------
// defined the ecvt structure because it gives an error that
// identifier ecvt has already been defined in stdlib.h
//---------------------------------------------------------------------
#define ecvt struct_ecvt
#include "include/gen/ihaecvt.h"


#ifndef __CERTIFICATE_AUTHENTICATE
#define __CERTIFICATE_AUTHENTICATE 4
#endif

//---------------------------------------------------------------------
// JNI function declaration and export
//---------------------------------------------------------------------

/**
 * Verify the given user and password.
 *
 * @param env the JNI environment reference provided by the JVM
 * @param jobj the reference to the class defining this native method
 * @param juser the name of the user in EBCDIC
 * @param jpwd  the password of the user in EBCDIC
 * @param japplid the application identifier
 * @param jpasswdResult Output parm - pass back errno/errno2
 *
 * @returns JNI_TRUE if the password is correct; JNI_FALSE otherwise
 */
#pragma export(ntv_checkPassword)
JNIEXPORT jboolean JNICALL
ntv_checkPassword(JNIEnv* env, 
                  jobject jobj, 
                  jbyteArray juser, 
                  jbyteArray jpwd, 
                  jstring japplid, 
                  jbyteArray jpasswdResult);

/**
 * Retrieve the realm name from the APPLDATA associated with the
 * SAFDFLT profile in the REALM class.
 *
 * @param env the JNI environment reference provided by the JVM
 * @param jobj the reference to the SAFRegistry object this invoked this native method
 *
 * @returns the realm from the RACROUTE EXTRACT service call
 */
#pragma export(ntv_getRealm)
JNIEXPORT jbyteArray JNICALL
ntv_getRealm(JNIEnv* env, jobject jobj);

/**
 * Validate user against SAF registry using USS services.
 *
 * @param env the JNI environment reference provided by the JVM
 * @param jobj the reference to the SAFRegistry object that invoked this method
 * @param juser the user
 *
 * @return JNI_TRUE if user exists; otherwise JNI_FALSE
 */
#pragma export(ntv_isValidUser)
JNIEXPORT jboolean JNICALL
ntv_isValidUser(JNIEnv* env, jobject jobj, jbyteArray juser);

/**
 * Validate group against SAF registry using USS services.
 *
 * @param env the JNI environment reference provided by the JVM
 * @param jobj the reference to the SAFRegistry object that invoked this method
 * @param jgroup the group
 *
 * @return JNI_TRUE if group exists; otherwise JNI_FALSE
 */
#pragma export(ntv_isValidGroup)
JNIEXPORT jboolean JNICALL
ntv_isValidGroup(JNIEnv* env, jobject jobj, jbyteArray jgroup);

/**
 * Map certficate.
 *
 * @param env the JNI environment reference provided by the JVM
 * @param jobj the reference to the SAFRegistry object that invoked this method
 * @param jcert the certificate
 * @param length the certificate length
 *
 * @return
 */
#pragma export(ntv_mapCertificate)
JNIEXPORT jbyteArray JNICALL
ntv_mapCertificate(JNIEnv* env, jobject jobj, jbyteArray jcert, jint length);

/**
 * Get the users associated with a single group from the SAF registry.
 *
 * @param env the JNI environment reference provided by the JVM.
 * @param jobj the reference to the SAFRegistry object that invoked this method.
 * @param jname the group whose users we want.
 * @param list the list to be populated with the group&apos;s users.
 *
 * @return the given java.util.List populated with EBCDIC strings representing
 *         users associated with the given group.
 */
#pragma export(ntv_getUsersForGroup)
JNIEXPORT jobject JNICALL
ntv_getUsersForGroup(JNIEnv* env, jobject jobj, jbyteArray jname, jobject list);

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
#pragma export(ntv_getGroupsForUser)
JNIEXPORT jobject JNICALL
ntv_getGroupsForUser(JNIEnv* env, jobject jobj, jbyteArray jname, jobject list);

/**
 * Rewind and open the SAF registry&apos;s groups database.  A lock is held in Java.
 *
 * @param env the JNI environment reference provided by the JVM.
 * @param jobj the reference to the SAFRegistry object that invoked this method.
 *
 * @return JNI_TRUE on success, JNI_FALSE otherwise.
 */
#pragma export(ntv_resetGroupsCursor)
JNIEXPORT jboolean JNICALL
ntv_resetGroupsCursor(JNIEnv* env, jobject jobj);

/**
 * Get the next group from the SAF registry.  A lock is held in Java.
 *
 * @param env the JNI environment reference provided by the JVM.
 * @param jobj the reference to the SAFRegistry object that invoked this method.
 *
 * @return JNI_TRUE on success, JNI_FALSE otherwise.
 */
#pragma export(ntv_getNextGroup)
JNIEXPORT jbyteArray JNICALL
ntv_getNextGroup(JNIEnv* env, jobject jobj);


/**
 * Close the groups database.  A lock is held in Java.
 *
 * @param env the JNI environment reference provided by the JVM.
 * @param jobj the reference to the SAFRegistry object that invoked this method.
 *
 * @return JNI_TRUE on success, JNI_FALSE otherwise.
 */
#pragma export(ntv_closeGroupsDB)
JNIEXPORT jboolean JNICALL
ntv_closeGroupsDB(JNIEnv* env, jobject jobj);

/**
 * Rewind the users database.  A lock is held in Java.
 *
 * @param env the JNI environment reference provided by the JVM.
 * @param jobj the reference to the SAFRegistry object that invoked this method.
 *
 * @return JNI_TRUE on success, JNI_FALSE otherwise.
 */
#pragma export(ntv_resetUsersCursor)
JNIEXPORT jboolean JNICALL
ntv_resetUsersCursor(JNIEnv* env, jobject jobj);

/**
 * Get the next user from the SAF registry.  A lock is held in Java.
 *
 * @param env the JNI environment reference provided by the JVM.
 * @param jobj the reference to the SAFRegistry object that invoked this method.
 *
 * @return JNI_TRUE on success, JNI_FALSE otherwise.
 */
#pragma export(ntv_getNextUser)
JNIEXPORT jbyteArray JNICALL
ntv_getNextUser(JNIEnv* env, jobject jobj);

/**
 * Close the users database.  A lock is held in Java.
 *
 * @param env the JNI environment reference provided by the JVM.
 * @param jobj the reference to the SAFRegistry object that invoked this method.
 *
 * @return JNI_TRUE on success, JNI_FALSE otherwise.
 */
#pragma export(ntv_closeUsersDB)
JNIEXPORT jboolean JNICALL
ntv_closeUsersDB(JNIEnv* env, jobject jobj);

/**
 * Retrieve the PLEX name from the ecvt
 *
 * @param env the JNI environment reference provided by the JVM
 * @param jobj the reference to the SAFRegistry object this invoked this native method
 *
 * @returns the plex name
 */
#pragma export(ntv_getPlexName)
JNIEXPORT jbyteArray JNICALL
ntv_getPlexName(JNIEnv* env, jobject jobj);

/**
 * Call RACROUTE EXTRACT to extract data from the given class/profile/field.
 *
 * @param env the JNI environment reference provided by the JVM
 * @param jobj the reference to the java object that contains this JNI method
 * @param jclassName the SAF class (in EBCDIC bytes)
 * @param jprofileName the SAF profile within the given class (in EBCDIC bytes)
 * @param jfieldName the field to extract within the given profile (in EBCDIC bytes)
 * @param jsafServiceResult output parm for copying back SAF RC info in the event of error
 *
 * @return the extracted data
 */
#pragma export(ntv_racrouteExtract)
JNIEXPORT jbyteArray JNICALL
ntv_racrouteExtract(JNIEnv* env, 
                    jobject jobj, 
                    jbyteArray jclassName, 
                    jbyteArray jprofileName, 
                    jbyteArray jfieldName, 
                    jbyteArray jsafServiceResult);

//---------------------------------------------------------------------
// RAS related Trace point definitions
//---------------------------------------------------------------------
#define RAS_MODULE_CONST RAS_MODULE_SECURITY_SAF_REGISTRY
#define _TP_IS_VALID_GROUP                          1
#define _TP_IS_VALID_USER                           2
#define _TP_CHECK_PASSWORD                          3
#define _TP_MAP_CERTIFICATE                         4
#define _TP_GET_REALM                               6
#define _TP_GET_GROUPS_FOR_USER_CALLED              7
#define _TP_GOT_GROUP_FOR_USER                      8
#define _TP_GET_GROUPS_FOR_USER_RETURN              9
#define _TP_RESET_GROUPS_CURSOR_CALLED             10
#define _TP_RESET_GROUPS_CURSOR_RETURN             11
#define _TP_GET_NEXT_GROUP_CALLED                  12
#define _TP_GOT_GROUP_ENTRY                        13
#define _TP_GET_NEXT_GROUP_RETURNING               14
#define _TP_CLOSE_GROUPS_DB_CALLED                 15
#define _TP_CLOSE_GROUPS_DB_RETURN                 16
#define _TP_RESET_USERS_CURSOR_CALLED              17
#define _TP_RESET_USERS_CURSOR_RETURN              18
#define _TP_GET_NEXT_USER_CALLED                   19
#define _TP_GOT_USER_ENTRY                         20
#define _TP_GET_NEXT_USER_RETURNING                21
#define _TP_CLOSE_USERS_DB_CALLED                  22
#define _TP_CLOSE_USERS_DB_RETURN                  23
#define _TP_GET_GROUPS_FOR_USER_COUNT_FAILURE      25
#define _TP_GET_GROUPS_FOR_USER_FAILURE            26
#define _TP_GET_GROUPS_FOR_USER_MALLOC_FAILURE     27
#define _TP_GET_PLEX_NAME_CALLED                   28
#define _TP_GET_PLEX_NAME_RETURN                   29
#define _TP_GET_USERS_FOR_GROUP_FAILURE            30
#define _TP_GET_USERS_FOR_GROUP_CALLED             31

//---------------------------------------------------------------------
// JNI native method structure for the SAF user registry methods
//---------------------------------------------------------------------
#pragma convert("ISO8859-1")
static const JNINativeMethod safUserRegistryMethods[] = {
    { "ntv_checkPassword",
      "([B[BLjava/lang/String;[B)Z",
      (void *) ntv_checkPassword },
    { "ntv_getRealm",
      "()[B",
      (void *) ntv_getRealm },
    { "ntv_isValidUser",
      "([B)Z",
      (void *) ntv_isValidUser },
    { "ntv_isValidGroup",
      "([B)Z",
      (void *) ntv_isValidGroup },
    { "ntv_mapCertificate",
      "([BI)[B",
      (void *) ntv_mapCertificate },
    { "ntv_getUsersForGroup",
      "([BLjava/util/List;)Ljava/util/List;",
      (void *) ntv_getUsersForGroup },
    { "ntv_getGroupsForUser",
      "([BLjava/util/List;)Ljava/util/List;",
      (void *) ntv_getGroupsForUser },
    { "ntv_resetGroupsCursor",
      "()Z",
      (void *) ntv_resetGroupsCursor },
    { "ntv_getNextGroup",
      "()[B",
      (void *) ntv_getNextGroup },
    { "ntv_closeGroupsDB",
      "()Z",
      (void *) ntv_closeGroupsDB },
    { "ntv_resetUsersCursor",
      "()Z",
      (void *) ntv_resetUsersCursor },
    { "ntv_getNextUser",
      "()[B",
      (void *) ntv_getNextUser },
    { "ntv_closeUsersDB",
      "()Z",
      (void *) ntv_closeUsersDB },
    { "ntv_getPlexName",
      "()[B",
      (void *) ntv_getPlexName }
};

static const JNINativeMethod safDelegationProviderMethods[] = {
    { "ntv_racrouteExtract",
      "([B[B[B[B)[B",
      (void *) ntv_racrouteExtract }
};
#pragma convert(pop)

//---------------------------------------------------------------------
// NativeMethodDescriptor(s) for the SAFRegistry and SAFDelegationProvider
//---------------------------------------------------------------------
#pragma export(zJNI_com_ibm_ws_security_registry_saf_internal_SAFRegistry)
NativeMethodDescriptor zJNI_com_ibm_ws_security_registry_saf_internal_SAFRegistry = {
    .registrationFunction = NULL,
    .deregistrationFunction = NULL,
    .nativeMethodCount = sizeof(safUserRegistryMethods) / sizeof(safUserRegistryMethods[0]),
    .nativeMethods = safUserRegistryMethods
};

#pragma export(zJNI_com_ibm_ws_security_authorization_saf_internal_SAFDelegationProvider)
NativeMethodDescriptor zJNI_com_ibm_ws_security_authorization_saf_internal_SAFDelegationProvider = {
    .registrationFunction = NULL,
    .deregistrationFunction = NULL,
    .nativeMethodCount = sizeof(safDelegationProviderMethods) / sizeof(safDelegationProviderMethods[0]),
    .nativeMethods = safDelegationProviderMethods 
};


//---------------------------------------------------------------------
// Convert a given char* to a java byte array and add it to the 
// provided java/util/List
//---------------------------------------------------------------------
static char
addToList(JNIEnv* env, jobject list, char* estr) {
    // Convert java method names and parms to ASCII for use with GetMethodID
#pragma convert("ISO8859-1")
    static const char* addMethodName = "add";
    static const char* addMethodParms = "(Ljava/lang/Object;)Z";
    static const char* c_listClassName = "java/util/List";
#pragma convert(pop)

    jclass     clazz     = NULL;
    jmethodID  addMethod = NULL;
    jbyteArray jarr      = NULL;
    int        len       = 0;

    // Find the List interface class
    clazz = (*env)->FindClass(env, c_listClassName);
    if (clazz == NULL) {
        return -1;
    }

    // Find the add method
    addMethod = (*env)->GetMethodID(env, clazz, addMethodName, addMethodParms);
    if (addMethod == NULL) {
        return -1;
    }

    // convert input string to java byte array
    len = strlen(estr);
    jarr = (*env)->NewByteArray(env, len);
    (*env)->SetByteArrayRegion(env, jarr, 0, len, (jbyte *)estr);

    // Add the java byte array to the provided List
    (*env)->CallBooleanMethod(env, list, addMethod, jarr);

    return 0;
}

//---------------------------------------------------------------------
// Determine if the user/password combination is valid.
//
// http://www-01.ibm.com/support/knowledgecenter/SSLTBW_1.12.0/com.ibm.zos.r12.bpxbd00/rpassw.htm%23rpassw
//  If unsuccessful, __passwd() returns -1 and sets errno to one of the following values:
//  
//  Error Code      Description
//  EACCES          The oldpass is not authorized.
//  EINVAL          The username, oldpass, newpass, or applid argument is invalid.
//  EMVSERR         The specified function is not supported in an address space where a load was done from an uncontrolled library.
//  EMVSEXPIRE      The oldpass has expired and no newpass has been provided.
//  EMVSPASSWORD    The newpass is not valid, or does not meet the installation-exit requirements.
//  EMVSSAF2ERR     Internal processing error.
//  EMVSSAFEXTRERR  An internal SAF/RACF extract error has occurred. A possible reason is that the username access has been revoked. 
//                  errno2 contains the BPX1PWD reason code. For more information, see z/OS UNIX System Services Programming: Assembler 
//                  Callable Services Reference.
//  ESRCH           The username provided is not defined to the security product or does not have an OMVS segment defined.
// 
//---------------------------------------------------------------------
JNIEXPORT jboolean JNICALL
ntv_checkPassword(JNIEnv* env, 
                  jobject jobj, 
                  jbyteArray juser, 
                  jbyteArray jpwd, 
                  jstring japplid,
                  jbyteArray jpasswdResult) {
    jbyte* euser = NULL;
    jbyte* epwd = NULL;
    int isValid = 0;

    JNI_try {
        JNI_GetByteArrayElements(euser, env, juser, "juser is null", NULL);
        JNI_GetByteArrayElements(epwd,  env, jpwd,  "jpwd is null", NULL);

        // Clear errno/errno2 before the call
        errno = 0;
        __err2ad();

        isValid = (__passwd_applid((const char*)euser, (const char*)epwd, NULL, NULL) == 0);

        int errno2 = __errno2();

        if (TraceActive(trc_level_basic)) {
            TraceRecord(trc_level_basic,
                        TP(_TP_CHECK_PASSWORD),
                        "check password return",
                        TRACE_DATA_FUNCTION,
                        TRACE_DATA_STRING((const char*)euser, "user id"),
                        TRACE_DATA_INT(isValid, "isValid"),
                        TRACE_DATA_INT(errno, "errno"),
                        TRACE_DATA_HEX_INT(errno2, "errno2"),
                        TRACE_DATA_END_PARMS);
        }

        if ( ! isValid  && jpasswdResult != NULL) {
            // Fail! Copy the errnos back to java
            JNI_SetByteArrayRegion(env, jpasswdResult, 0, sizeof(int), (jbyte*) &errno);
            JNI_SetByteArrayRegion(env, jpasswdResult, sizeof(int), sizeof(int), (jbyte*) &errno2);
        }

    } JNI_catch(env);

    JNI_ReleaseByteArrayElements(env, juser, euser, NULL);
    JNI_ReleaseByteArrayElements(env, jpwd,  epwd,  NULL);

    JNI_reThrowAndReturn(env);

    return (isValid) ? JNI_TRUE : JNI_FALSE;
}

//---------------------------------------------------------------------
// Retrieve the realm setting from the SAF product.  The realm is pulled
// from the APPLDATA field in the SAFDFLT profile of the REALM class.  
// Use RACROUTE EXTRACT to extract the data.  The RACROUTE macro must 
// be called by authorized code.
//---------------------------------------------------------------------
JNIEXPORT jbyteArray JNICALL 
ntv_getRealm( JNIEnv* env, jobject jobj)
{
    jbyteArray              jrealm = NULL;
    racf_extract_results    extractResults;

    SAFServiceResult safServiceResult = {
        .wasReturnCode = SECURITY_AUTH_RC_UNAUTHORIZED,
        .safReturnCode = -1,
        .racfReturnCode = -1,
        .racfReasonCode = -1 
    };

    JNI_try {

        const server_authorized_function_stubs* auth_stubs_p = getServerAuthorizedFunctionStubs();

        if (auth_stubs_p == NULL) {
            JNI_throwNullPointerException(env, "auth_stubs_p is null");
        }

        // Prepare the extractResults
        memset(&extractResults, 0, sizeof(racf_extract_results));

        SafExtractRealmParms serp = {
            .extractResults = &extractResults,
            .safServiceResult = &safServiceResult
        };

        int rc = auth_stubs_p->safExtractRealm(&serp);

        if (rc != 0) {
            JNI_throwPCRoutineFailedException(env, "safExtractRealm", rc);
        }
    
        if (TraceActive(trc_level_basic)) {
            TraceRecord(trc_level_basic,
                        TP(_TP_GET_REALM),
                        "returned from safExtractRealm",
                        TRACE_DATA_FUNCTION,
                        TRACE_DATA_INT(rc, "safExtractRealm rc"),
                        TRACE_DATA_RAWDATA( sizeof(SAFServiceResult), &safServiceResult, "safServiceResult"),
                        TRACE_DATA_RAWDATA( (sizeof(extractResults.length) + extractResults.length), &extractResults, "extractResults"),
                        TRACE_DATA_END_PARMS);
        }

        if (rc == 0 && safServiceResult.safReturnCode == 0 && extractResults.length > 0) {
            // All good.  Push the extracted realm up into Java.
            JNI_NewByteArray(jrealm, env, extractResults.length);
            JNI_SetByteArrayRegion(env, jrealm, 0, extractResults.length, (jbyte *)&extractResults.data);
        }
    } JNI_catch(env);

    JNI_reThrowAndReturn(env);

    return jrealm;
}

//---------------------------------------------------------------------
// Determine if the given user is valid (i.e. defined in the SAF product).
// This method uses the unauthorized USS service getpwnam_r.  Note that
// getpwnam_r also returns information for revoked users.  If you wish to
// validate that the user has not been revoked, you must follow this call
// with an attempt to create a credential for the user.
//---------------------------------------------------------------------
JNIEXPORT jboolean JNICALL
ntv_isValidUser(JNIEnv* env, jobject jobj, jbyteArray juser) {
    const size_t    c_GETPW_R_SIZE_MAX = 24576;
    char            r_buffer[c_GETPW_R_SIZE_MAX];
    struct passwd   r_pwent;
    struct passwd*  pwent  = NULL;
    jbyte*          euser  = NULL;
    jboolean        isValid = JNI_FALSE;

    JNI_try {
        JNI_GetByteArrayElements(euser, env, juser, "juser is null", NULL);

        // Find the user entry.
        int rc = getpwnam_r((const char*)euser, &r_pwent, r_buffer, c_GETPW_R_SIZE_MAX, &pwent);

        // pwent != NULL means the user is found.
        isValid = (rc == 0 && pwent != NULL) ? JNI_TRUE : JNI_FALSE;

        if (TraceActive(trc_level_basic)) {
            TraceRecord(trc_level_basic,
                        TP(_TP_IS_VALID_USER),
                        "isValidUser return",
                        TRACE_DATA_FUNCTION,
                        TRACE_DATA_STRING((const char*)euser, "user name"),
                        TRACE_DATA_INT(isValid, "isValid"),
                        TRACE_DATA_INT(rc, "rc"),
                        TRACE_DATA_INT(((rc != 0) ? errno : 0), "errno"),
                        TRACE_DATA_HEX_INT(((rc != 0) ? __errno2() : 0), "errno2"),
                        TRACE_DATA_END_PARMS);
        }
    } JNI_catch(env);

    JNI_ReleaseByteArrayElements(env, juser, euser, NULL);

    JNI_reThrowAndReturn(env);

    return isValid;
}

//---------------------------------------------------------------------
// Determine if the given group is valid (i.e. defined).
//---------------------------------------------------------------------
JNIEXPORT jboolean JNICALL
ntv_isValidGroup(JNIEnv* env, jobject jobj, jbyteArray jgroup) {
    const size_t   c_GETGR_R_SIZE_MAX = 24576;
    char           r_buffer[c_GETGR_R_SIZE_MAX];
    struct group   r_grent;
    struct group*  grent  = NULL;
    jbyte*         egroup = NULL;
    jboolean       isValid = JNI_FALSE;

    JNI_try {

        JNI_GetByteArrayElements(egroup, env, jgroup, "jgroup is null", NULL);

        // Find the group entry.
        int rc = getgrnam_r((const char*)egroup, &r_grent, r_buffer, c_GETGR_R_SIZE_MAX, &grent);

        // grent != NULL means the group is found.
        isValid = (rc == 0 && grent != NULL ) ? JNI_TRUE : JNI_FALSE;

        if (TraceActive(trc_level_basic)) {
            TraceRecord(trc_level_basic,
                        TP(_TP_IS_VALID_GROUP),
                        "isValidGroup return",
                        TRACE_DATA_FUNCTION,
                        TRACE_DATA_STRING((const char*)egroup, "egroup"),
                        TRACE_DATA_INT(isValid, "isValid"),
                        TRACE_DATA_INT(rc, "rc"),
                        TRACE_DATA_INT(((rc != 0) ? errno : 0), "errno"),
                        TRACE_DATA_HEX_INT(((rc != 0) ? __errno2() : 0), "errno2"),
                        TRACE_DATA_END_PARMS);
        }
    } JNI_catch(env);

    JNI_ReleaseByteArrayElements(env, jgroup, egroup, NULL);

    JNI_reThrowAndReturn(env);

    return isValid;
}

//---------------------------------------------------------------------
// Map a certificate to a user.
//---------------------------------------------------------------------
JNIEXPORT jbyteArray JNICALL
ntv_mapCertificate(JNIEnv* env, jobject jobj, jbyteArray jcert, jint length) {
    jbyteArray     juserId = NULL;
    jbyte*         cert = NULL;
    char           userId[1024] = { '\0' };

    JNI_try {

        JNI_GetByteArrayElements(cert, env, jcert, "jcert is null", NULL);

        int rc = __certificate(__CERTIFICATE_AUTHENTICATE,
                               length,
                               (char*) cert,
                               sizeof(userId),
                               userId);

        // Push the user ID back into to Java.
        if (rc == 0) {
            int len = strlen(userId);
            JNI_NewByteArray(juserId, env, len);
            JNI_SetByteArrayRegion(env, juserId, 0, len, (jbyte*) userId);
        }

        if (TraceActive(trc_level_basic)) {
            TraceRecord(trc_level_basic,
                        TP(_TP_MAP_CERTIFICATE),
                        "mapCertificate return",
                        TRACE_DATA_RAWDATA(length, cert, "certificate"),
                        TRACE_DATA_PTR(juserId, "juserId"),
                        TRACE_DATA_STRING(userId, "user id"),
                        TRACE_DATA_INT(rc, "rc"),
                        TRACE_DATA_INT(((rc != 0) ? errno : 0), "errno"),
                        TRACE_DATA_HEX_INT(((rc != 0) ? __errno2() : 0), "errno2"),
                        TRACE_DATA_END_PARMS);
        }
    } JNI_catch(env);

    JNI_ReleaseByteArrayElements(env, jcert, cert, NULL);

    JNI_reThrowAndReturn(env);

    return juserId;
}

//---------------------------------------------------------------------
// Get the list of users that are members of the specified group.
//---------------------------------------------------------------------
JNIEXPORT jobject JNICALL
ntv_getUsersForGroup(JNIEnv* env, jobject jobj, jbyteArray jname, jobject list) {
    struct group*  grp       = NULL;
    unsigned char* ename     = NULL;
    char**         curr;

    // Get the name into a local array allocated off the stack
    jsize jnameLength = (*env)->GetArrayLength(env, jname);
    ename = alloca(jnameLength);

    (*env)->GetByteArrayRegion(env, jname, 0, jnameLength, (jbyte *) ename);

    if ((*env)->ExceptionOccurred(env)) {
        (*env)->ExceptionDescribe(env);
        (*env)->ExceptionClear(env);
        return NULL;
    }

    if (TraceActive(trc_level_basic)) {
        TraceRecord(trc_level_basic,
                    TP(_TP_GET_USERS_FOR_GROUP_CALLED),
                    "getUsersForGroup called",
                    TRACE_DATA_STRING(ename, "group name"),
                    TRACE_DATA_PTR(list, "user list"),
                    TRACE_DATA_END_PARMS);
    }

    if ((grp = getgrnam(ename)) == NULL) {
        // There are no documented errnos for getgrnam()
        if (TraceActive(trc_level_basic)) {
            TraceRecord(trc_level_basic,
                        TP(_TP_GET_USERS_FOR_GROUP_FAILURE),
                        "getgrnam was unsuccessful or no group entry was found",
                        TRACE_DATA_STRING(ename, "requested group name"),
                        TRACE_DATA_END_PARMS);
        }
    } else {
        // Add the users to the list
        for (curr=grp->gr_mem; (*curr) != NULL; curr++) {
            if (addToList(env, list, *curr)) {
                list = NULL;
                break;
            }
        }
    }

    return list;
}

//---------------------------------------------------------------------
// Get the list of groups that the specified user is a member of.
//---------------------------------------------------------------------
JNIEXPORT jobject JNICALL
ntv_getGroupsForUser(JNIEnv* env, jobject jobj, jbyteArray jname, jobject list) {
    struct group*  grent = NULL;
    gid_t*         gids  = NULL;
    unsigned char* ename = NULL;
    int            count = -1;

    // Reset errno
    errno = 0;

    // Get the name into a local array allocated off the stack
    jsize jnameLength = (*env)->GetArrayLength(env, jname);
    ename = alloca(jnameLength);
    (*env)->GetByteArrayRegion(env, jname, 0, jnameLength, (jbyte *) ename);

    // Failures in JNI will show up as runtime exceptions in Java
    if ((*env)->ExceptionOccurred(env)) {
        return NULL;
    }

    if (TraceActive(trc_level_basic)) {
        TraceRecord(trc_level_basic,
                    TP(_TP_GET_GROUPS_FOR_USER_CALLED),
                    "getGroupsForUser called",
                    TRACE_DATA_STRING(ename, "user name"),
                    TRACE_DATA_PTR(list, "group list"),
                    TRACE_DATA_END_PARMS);
    }


    // Find out how many supplemental groups there are
    count = getgroupsbyname(ename, 0, NULL);
    if (count < 0) {
        if (TraceActive(trc_level_exception)) {
            TraceRecord(trc_level_exception,
                        TP(_TP_GET_GROUPS_FOR_USER_COUNT_FAILURE),
                        "getgroupsbyname failed",
                        TRACE_DATA_INT(errno, "errno"),
                        TRACE_DATA_HEX_INT(((errno != 0) ? __errno2() : 0), "errno2"),
                        TRACE_DATA_END_PARMS);
        }

        return NULL;
    }

    // Get storage for group IDs (allow for 0 groups)
    gids = malloc((count + 1) * sizeof(gid_t));
    if (gids == NULL) {
        if (TraceActive(trc_level_exception)) {
            TraceRecord(trc_level_exception,
                        TP(_TP_GET_GROUPS_FOR_USER_MALLOC_FAILURE),
                        "gids malloc failed",
                        TRACE_DATA_END_PARMS);
        }

        return NULL;
    }

    // We have the storage, get the supplemental groups
    int groupCount = getgroupsbyname(ename, count, gids);
    if (groupCount < 0) {
        free(gids);
        return NULL;
    }

    // Add the group entries to the list
    for (int i = 0; i < groupCount; i++) {
        grent = getgrgid(gids[i]);

        if (grent == NULL || grent->gr_name == NULL) {
            continue;
        }

        if (addToList(env, list, grent->gr_name)) {
            list = NULL;
            break;
        }
    }

    // Release the temporary storage
    free(gids);
    gids = NULL;

    return list;
}

//---------------------------------------------------------------------
//---------------------------------------------------------------------
JNIEXPORT jboolean JNICALL
ntv_resetGroupsCursor(JNIEnv* env, jobject jobj) {
    if (TraceActive(trc_level_basic)) {
        TraceRecord(trc_level_basic,
                    TP(_TP_RESET_GROUPS_CURSOR_CALLED),
                    "resetGroupsCursor called",
                    TRACE_DATA_END_PARMS);
    }

    errno = 0;
    setgrent();

    return (errno == 0) ? JNI_TRUE : JNI_FALSE;
}

//---------------------------------------------------------------------
//---------------------------------------------------------------------
JNIEXPORT jbyteArray JNICALL
ntv_getNextGroup(JNIEnv* env, jobject jobj) {
    struct group* grent = NULL;
    jbyteArray jarr = NULL;

    if (TraceActive(trc_level_basic)) {
        TraceRecord(trc_level_basic,
                    TP(_TP_GET_NEXT_GROUP_CALLED),
                    "getNextGroup called",
                    TRACE_DATA_END_PARMS);
    }

    errno = 0;
    if ((grent = getgrent()) != NULL) {
        if (TraceActive(trc_level_detailed)) {
            TraceRecord(trc_level_detailed,
                        TP(_TP_GOT_GROUP_ENTRY),
                        "got group entry",
                        TRACE_DATA_STRING(grent->gr_name, "group name"),
                        TRACE_DATA_RAWDATA(sizeof(grent->gr_gid), &grent->gr_gid,"gid"),
                        TRACE_DATA_END_PARMS);
        }

        // convert group name string to java byte array
        int len = strlen(grent->gr_name);
        jarr = (*env)->NewByteArray(env, len);
        if (jarr != NULL) {
            (*env)->SetByteArrayRegion(env, jarr, 0, len, (jbyte *) grent->gr_name);
        }
    }

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(_TP_GET_NEXT_GROUP_RETURNING),
                    "getNextGroup return",
                    TRACE_DATA_FUNCTION,
                    TRACE_DATA_PTR(jarr, "group name jbyteArray"),
                    TRACE_DATA_END_PARMS);
    }

    return jarr;
}


//---------------------------------------------------------------------
//---------------------------------------------------------------------
JNIEXPORT jboolean JNICALL
ntv_closeGroupsDB(JNIEnv* env, jobject jobj) {
    if (TraceActive(trc_level_basic)) {
        TraceRecord(trc_level_basic,
                    TP(_TP_CLOSE_GROUPS_DB_CALLED),
                    "closeGroupsDB call",
                    TRACE_DATA_END_PARMS);
    }

    // Close the database
    errno = 0;
    endgrent();

    if (TraceActive(trc_level_basic)) {
        TraceRecord(trc_level_basic,
                    TP(_TP_CLOSE_GROUPS_DB_RETURN),
                    "closeGroupsDB return",
                    TRACE_DATA_INT(errno, "errno"),
                    TRACE_DATA_HEX_INT(((errno != 0) ? __errno2() : 0), "errno2"),
                    TRACE_DATA_END_PARMS);
    }

    return (errno == 0) ? JNI_TRUE : JNI_FALSE;
}

//---------------------------------------------------------------------
//---------------------------------------------------------------------
JNIEXPORT jboolean JNICALL
ntv_resetUsersCursor(JNIEnv* env, jobject jobj) {
    if (TraceActive(trc_level_basic)) {
        TraceRecord(trc_level_basic,
                    TP(_TP_RESET_USERS_CURSOR_CALLED),
                    "resetUsersCursor called",
                    TRACE_DATA_END_PARMS);
    }

    // Open the database and rewind.
    errno = 0;
    setpwent();

    if (TraceActive(trc_level_basic)) {
        TraceRecord(trc_level_basic,
                    TP(_TP_RESET_USERS_CURSOR_RETURN),
                    "resetUsersCursor return",
                    TRACE_DATA_INT(errno, "errno"),
                    TRACE_DATA_HEX_INT(((errno != 0) ? __errno2() : 0), "errno2"),
                    TRACE_DATA_END_PARMS);
    }

    return (errno == 0) ? JNI_TRUE : JNI_FALSE;
}

//---------------------------------------------------------------------
//---------------------------------------------------------------------
JNIEXPORT jbyteArray JNICALL
ntv_getNextUser(JNIEnv* env, jobject jobj) {
    struct passwd* pwent = NULL;
    jbyteArray jarr = NULL;

    if (TraceActive(trc_level_basic)) {
        TraceRecord(trc_level_basic,
                    TP(_TP_GET_NEXT_USER_CALLED),
                    "getNextUser called",
                    TRACE_DATA_END_PARMS);
    }

    errno = 0;
    if ((pwent = getpwent()) != NULL) {
        if (TraceActive(trc_level_detailed)) {
            TraceRecord(trc_level_detailed,
                        TP(_TP_GOT_USER_ENTRY),
                        "got user entry",
                        TRACE_DATA_STRING(pwent->pw_name, "user name"),
                        TRACE_DATA_RAWDATA(sizeof(pwent->pw_uid), &pwent->pw_uid, "uid"),
                        TRACE_DATA_END_PARMS);
        }

        // convert user name string to java byte array
        int len = strlen(pwent->pw_name);
        jarr = (*env)->NewByteArray(env, len);
        (*env)->SetByteArrayRegion(env, jarr, 0, len, (jbyte *) pwent->pw_name);
    }

    if (TraceActive(trc_level_basic)) {
        TraceRecord(trc_level_basic,
                    TP(_TP_GET_NEXT_USER_RETURNING),
                    "getNextUser return",
                    TRACE_DATA_PTR(jarr, "user name as jbyteArray"),
                    TRACE_DATA_INT(errno, "errno"),
                    TRACE_DATA_HEX_INT(((errno != 0) ? __errno2() : 0), "errno2"),
                    TRACE_DATA_END_PARMS);
    }

    return jarr;
}

//---------------------------------------------------------------------
//---------------------------------------------------------------------
JNIEXPORT jboolean JNICALL
ntv_closeUsersDB(JNIEnv* env,
                 jobject jobj) {
    if (TraceActive(trc_level_basic)) {
        TraceRecord(trc_level_basic,
                    TP(_TP_CLOSE_USERS_DB_CALLED),
                    "closeUsersDB called",
                    TRACE_DATA_END_PARMS);
    }

    // Close the database
    errno = 0;
    endpwent();

    if (TraceActive(trc_level_basic)) {
        TraceRecord(trc_level_basic,
                    TP(_TP_CLOSE_USERS_DB_RETURN),
                    "",
                    TRACE_DATA_FUNCTION,
                    TRACE_DATA_INT(errno, "errno"),
                    TRACE_DATA_HEX_INT(((errno != 0) ? __errno2() : 0), "errno2"),
                    TRACE_DATA_END_PARMS);
    }

    return (errno == 0) ? JNI_TRUE : JNI_FALSE;
}

//---------------------------------------------------------------------
//---------------------------------------------------------------------
JNIEXPORT jbyteArray JNICALL
ntv_getPlexName(JNIEnv* env,
                 jobject jobj) {

    jbyteArray     jplexName = NULL;

    if (TraceActive(trc_level_basic)) {
        TraceRecord(trc_level_basic,
                    TP(_TP_GET_PLEX_NAME_CALLED),
                    TRACE_DESC_FUNCTION_ENTRY,
                    TRACE_DATA_END_PARMS);
    }

    JNI_try {
        char plexName[9];
        psa* psa_p = 0;
        cvt* cvt_p = (cvt*) psa_p->flccvt;
        struct ecvt* ecvt_p = (struct ecvt*) cvt_p->cvtecvt;

        memset(plexName, 0, sizeof(plexName));
        memcpy(plexName, ecvt_p->ecvtsplx, sizeof(plexName));

        strtok(plexName," ");
        int len = strlen(plexName);
        JNI_NewByteArray(jplexName, env, len);
        JNI_SetByteArrayRegion(env, jplexName, 0, len, (jbyte *) plexName);

        if (TraceActive(trc_level_basic)) {
            TraceRecord(trc_level_basic,
                        TP(_TP_GET_PLEX_NAME_RETURN),
                        TRACE_DESC_FUNCTION_EXIT,
                        TRACE_DATA_STRING((const char*)plexName, "plex name"),
                        TRACE_DATA_END_PARMS);
        }

    } JNI_catch(env);
        JNI_reThrowAndReturn(env);

    return jplexName;
}

/**
 * call RACROUTE EXTRACT and return the extracted data.
 */
JNIEXPORT jbyteArray JNICALL
ntv_racrouteExtract(JNIEnv* env, 
                    jobject jobj, 
                    jbyteArray jclassName, 
                    jbyteArray jprofileName, 
                    jbyteArray jfieldName,
                    jbyteArray jsafServiceResult) {

    jbyteArray retMe = NULL;

    jbyte* className = NULL;
    jbyte* profileName = NULL;
    jbyte* fieldName = NULL;

    JNI_try {

        // Copy data from java to native
        JNI_GetByteArrayElements(className, env, jclassName, "jclassName is null", NULL);
        JNI_GetByteArrayElements(profileName,  env, jprofileName,  "jprofileName is null", NULL);
        JNI_GetByteArrayElements(fieldName,  env, jfieldName,  "jfieldName is null", NULL);

        // obtain ref to authz services
        const server_authorized_function_stubs* auth_stubs_p = getServerAuthorizedFunctionStubs();

        if (auth_stubs_p == NULL) {
            JNI_throwNullPointerException(env, "auth_stubs_p is null");
        }

        // Setup parms for PC routine
        racf_extract_results racfExtractResults; 
        memset(&racfExtractResults, 0, sizeof(racf_extract_results));

        SAFServiceResult safServiceResult = {
            .wasReturnCode = SECURITY_AUTH_RC_UNAUTHORIZED,
            .safReturnCode = -1,
            .racfReturnCode = -1,
            .racfReasonCode = -1 
        };

        SafRacrouteExtractParms pcParms = {
            .className          = (char*) className,
            .classNameLen       = strlen((char*)className),
            .profileName        = (char*) profileName,
            .profileNameLen     = strlen((char*)profileName),
            .fieldName          = (char*) fieldName,
            .fieldNameLen       = strlen((char*)fieldName),
            .racfExtractResults = &racfExtractResults,
            .safServiceResult   = &safServiceResult
         };
        
        // Invoke PC routine 
        int rc = auth_stubs_p->safRacrouteExtract(&pcParms);  

        if (rc != 0) {
            JNI_throwPCRoutineFailedException(env, "safRacrouteExtract", rc);
        }
    
        if (TraceActive(trc_level_basic)) {
            TraceRecord(trc_level_basic,
                        TP(30),
                        "returned from safRacrouteExtract",
                        TRACE_DATA_FUNCTION,
                        TRACE_DATA_INT(rc, "safRacrouteExtract rc"),
                        TRACE_DATA_STRING((const char *)className, "className"),
                        TRACE_DATA_STRING((const char *)profileName, "profileName"),
                        TRACE_DATA_STRING((const char *)fieldName, "fieldName"),
                        TRACE_DATA_RAWDATA( sizeof(SAFServiceResult), &safServiceResult, "safServiceResult"),
                        TRACE_DATA_RAWDATA( (sizeof(racfExtractResults.length) + racfExtractResults.length), &racfExtractResults, "racfExtractResults"),
                        TRACE_DATA_END_PARMS);
        }

        // Process the response data
        if (rc == 0 && safServiceResult.safReturnCode == 0 && racfExtractResults.length > 0) {
            // All good.  Push the extracted data into Java.
            JNI_NewByteArray(retMe, env, racfExtractResults.length);
            JNI_SetByteArrayRegion(env, retMe, 0, racfExtractResults.length, (jbyte *)&racfExtractResults.data);
        } else {
            // Copy back the error info.
            if (jsafServiceResult != NULL) {
                JNI_SetByteArrayRegion(env, jsafServiceResult, 0, sizeof(SAFServiceResult), (jbyte*) &safServiceResult);
            }
        }

    } JNI_catch(env);

    JNI_ReleaseByteArrayElements(env, jclassName, className, NULL);
    JNI_ReleaseByteArrayElements(env, jprofileName,  profileName,  NULL);
    JNI_ReleaseByteArrayElements(env, jfieldName,  fieldName,  NULL);

    JNI_reThrowAndReturn(env);

    return retMe;
}

