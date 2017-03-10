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
 * Functions in support of com.ibm.ws.zos.core.NativeServiceTracker.
 */
#include <stdlib.h>
#include <string.h>
#include <unistd.h>

#include "include/angel_dynamic_replaceable_module.h"
#include "include/angel_process_data.h"
#include "include/angel_server_pc_stub.h"
#include "include/ieantc.h"
#include "include/ras_tracing.h"
#include "include/server_authorized_function_module.h"
#include "include/server_jni_method_manager.h"
#include "include/server_native_service_tracker.h"
#include "include/server_process_data.h"

//---------------------------------------------------------------------
// JNI function declaration and export
//---------------------------------------------------------------------

/**
 * Load the unauthorized metal C function module.
 *
 * @param env the JNI environment reference provided by the JVM.
 * @param jthis the object instance this method was invoked against.
 * @param jmodulePath the path to the unauthorized metal C load module.
 *
 * @returns the service results from loadHFS (@c BPX4LOD)
 */
#pragma export(ntv_loadUnauthorized)
JNIEXPORT jobject JNICALL
ntv_loadUnauthorized(JNIEnv* env, jobject jthis, jstring jmodulePath);

/**
 * Call the angel registration interface required to load the authorized
 * code module and perform permission checking for enabled functions.
 *
 * @param env the JNI environment reference provided by the JVM.
 * @param jthis the object instance this method was invoked against.
 * @param jAuthorizedModulePath the path to the authorized metal C load module.
 * @param jAngelName The name of the angel to connect to.
 *
 * @returns the return code from angel registration
 */
#pragma export(ntv_registerServer)
JNIEXPORT jint JNICALL
ntv_registerServer(JNIEnv* env, jobject jthis, jstring jAuthorizedModulePath, jstring jAngelName);

/**
 * Call tha angel deregistration interface.  This will disconnect us
 * from the angel infrastructure.
 *
 * @param env the JNI environment reference provided by the JVM.
 * @param jthis the object instance this method was invoked against.
 */
#pragma export(ntv_deregisterServer)
JNIEXPORT jint JNICALL
ntv_deregisterServer(JNIEnv* env, jobject jthis);

/**
 * Populate the @c permitted and @c denied lists with the names of services
 * declared in the authorized service vector table that have been enabled
 * or disabled.
 *
 * @param env the JNI environment reference provided by the JVM.
 * @param jthis the object instance this method was invoked against.
 * @param permittedServices a java @c List<String> of services this server is
 *        allowed to use.  Each entry uses two slots.  The first is the service
 *        name and the second is the authorization profile.
 * @param deniedServices a java @c List<String> of services this server is not
 *        allowed to use.  Each entry uses two slots.  The first is the service
 *        name and the second is the authorization profile.
 * @param permittedClientServices a java @c List<String> of services that clients are
 *        allowed to use.  Each entry uses two slots.  The first is the service
 *        name and the second is the authorization profile.
 * @param deniedClientServices a java @c List<String> of services that clients are not
 *        allowed to use.  Each entry uses two slots.  The first is the service
 *        name and the second is the authorization profile.
 *
 * @return the number of services in the vector table
 */
#pragma export(ntv_getNativeServiceEntries)
JNIEXPORT jint JNICALL
ntv_getNativeServiceEntries(JNIEnv* env, jobject jthis, jobject permittedServices, jobject deniedServices, jobject permittedClientServices, jobject deniedClientServices);

/**
 * Get the version of the angel DRM.
 *
 * @return The version of the angel DRM, or -1 if unable to discover it.
 */
#pragma export (ntv_getAngelVersion)
JNIEXPORT jint JNICALL
ntv_getAngelVersion(JNIEnv* env, jobject this);

//---------------------------------------------------------------------
// RAS related constants
//---------------------------------------------------------------------
#define RAS_MODULE_CONST RAS_MODULE_SERVER_NATIVE_SERVICE_TRACKER
#define TP_LOADHFS_CALL                        1
#define TP_LOADHFS_RETURN                      2
#define TP_ANGEL_REGISTER_CALL                 3
#define TP_ANGEL_REGISTER_RETURN               4
#define TP_ANGEL_DEREGISTER_CALL               5
#define TP_ANGEL_DEREGISTER_RETURN             6
#define TP_PGOO_AUTHORIZED_FUNCTIONS           7

//---------------------------------------------------------------------
// Native method manager related infrastructure
//---------------------------------------------------------------------

/**
 * JNI native method structure for the NativeServiceTracker methods.
 */
#pragma convert("ISO8859-1")
static const JNINativeMethod serviceTrackerMethods[] = {
    { "ntv_loadUnauthorized",
      "(Ljava/lang/String;)Lcom/ibm/ws/zos/core/internal/NativeServiceTracker$ServiceResults;",
      (void *) ntv_loadUnauthorized },
    { "ntv_registerServer",
      "(Ljava/lang/String;Ljava/lang/String;)I",
      (void *) ntv_registerServer },
    { "ntv_deregisterServer",
      "()I",
      (void *) ntv_deregisterServer },
    { "ntv_getNativeServiceEntries",
      "(Ljava/util/List;Ljava/util/List;Ljava/util/List;Ljava/util/List;)I",
      (void *) ntv_getNativeServiceEntries },
    { "ntv_getAngelVersion",
      "()I",
      (void*) ntv_getAngelVersion }
};
#pragma convert(pop)

/**
 * NativeMethodDescriptor for the NativeServiceTracker.
 */
#pragma export(zJNI_com_ibm_ws_zos_core_internal_NativeServiceTracker)
NativeMethodDescriptor zJNI_com_ibm_ws_zos_core_internal_NativeServiceTracker = {
    .registrationFunction = NULL,
    .deregistrationFunction = NULL,
    .nativeMethodCount = sizeof(serviceTrackerMethods) / sizeof(serviceTrackerMethods[0]),
    .nativeMethods = serviceTrackerMethods
};

/**
 * NativeMethodDescriptor for NativeLibraryUtils in the unit test environment.
 */
#pragma export(zJNI_test_common_zos_NativeLibraryUtils)
NativeMethodDescriptor zJNI_test_common_zos_NativeLibraryUtils = {
    .registrationFunction = NULL,
    .deregistrationFunction = NULL,
    .nativeMethodCount = sizeof(serviceTrackerMethods) / sizeof(serviceTrackerMethods[0]),
    .nativeMethods = serviceTrackerMethods
};

//---------------------------------------------------------------------
// Module scoped utility functions
//---------------------------------------------------------------------

/**
 * Add the specified objec to the specified list.
 *
 * @param env the JNI environment reference provided by the JVM.
 * @param collection the collection to add to.
 * @param string the string to add to the collection.
 */
static void addToCollection(JNIEnv* env, jobject collection, jstring string);

/**
 * Load the server authorized function module locally and copy the entry
 * table so disabled, mock services can be registered when the angel isn't
 * available.
 *
 * @return malloc'd storage containing a copy of the vector table from the
 *         authorized function module
 */
static struct bbgzsafm* getAuthorizedFunctionTableUnauth();

/**
 * Load the client (common) authorized function module locally and copy the entry
 * table so disabled, mock services can be registered when the angel isn't
 * available.
 *
 * @return malloc'd storage containing a copy of the vector table from the
 *         authorized client function module
 */
static struct bbgzasvt_header* getAuthorizedClientFunctionTableUnauth();

//---------------------------------------------------------------------
// Module scoped references related to the non-LE stubs
//---------------------------------------------------------------------

/**
 * The angel anchor used by this server (or null if using the default
 * angel).
 */
AngelAnchor_t* angelAnchor_p = NULL;

/**
 * Absolute path to authorized function DLL.
 */
char* authorizedModulePath = NULL;

/**
 * Cached reference to the stubs we loaded before attempting server
 * registration with the angel.
 */
const server_function_stubs* serverFunctions = NULL;

/**
 * Cached reference to the server authorized stubs we loaded before
 * attempting server registration with the angel.
 */
const server_authorized_function_stubs* serverAuthorizedFunctions = NULL;

/**
 * Cached reference to the server unauthorized stubs we loaded before
 * attempting server registration with the angel.
 */
const server_unauthorized_function_stubs* serverUnauthorizedFunctions = NULL;

//---------------------------------------------------------------------
// Prototypes for the USS 'loadHFS' and 'deleteHFS' functions
//---------------------------------------------------------------------
#pragma linkage(BPX4LOD, OS)
void BPX4LOD(int filenameLength, char* filename,
             int flags,
             int libpathLength, char* libpath,
             void* entryPoint,
             int* returnValue, int* returnCode, int* reasonCode);

#pragma linkage(BPX4DEL, OS)
void BPX4DEL(void* entryPoint,
             int* returnValue, int* returnCode, int* reasonCode);

//---------------------------------------------------------------------
//---------------------------------------------------------------------
const server_function_stubs* getServerFunctionStubs() {
    return serverFunctions;
}

//---------------------------------------------------------------------
//---------------------------------------------------------------------
const server_authorized_function_stubs* getServerAuthorizedFunctionStubs() {
    return serverAuthorizedFunctions;
}

//---------------------------------------------------------------------
//---------------------------------------------------------------------
const server_unauthorized_function_stubs* getServerUnauthorizedFunctionStubs() {
    return serverUnauthorizedFunctions;
}

//---------------------------------------------------------------------
//---------------------------------------------------------------------
JNIEXPORT jobject JNICALL
ntv_loadUnauthorized(JNIEnv* env, jobject jthis, jstring jmodulePath) {
#pragma convert("ISO8859-1")
    const char* serviceResultsClassName = "com/ibm/ws/zos/core/internal/NativeServiceTracker$ServiceResults";
    const char* serviceResultsCtorName = "<init>";
    const char* serviceResultsCtorSig = "(III)V";
#pragma convert(pop)

    char* modulePath = NULL; // allocated with alloca
    char* libPath = "";

    // Convert module path to EBCDIC
    const char* utfModulePath = (*env)->GetStringUTFChars(env, jmodulePath, NULL);
    if (utfModulePath != NULL) {
        modulePath = alloca(strlen(utfModulePath) + 1);
        strcpy(modulePath, utfModulePath);
        __atoe(modulePath);
        (*env)->ReleaseStringUTFChars(env, jmodulePath, utfModulePath);
    }

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(TP_LOADHFS_CALL),
                    "load unauthorized from HFS call",
                    TRACE_DATA_STRING(modulePath, "modulePath"),
                    TRACE_DATA_STRING(libPath, "libPath"),
                    TRACE_DATA_END_PARMS);
    }

    // Load the module
    int returnValue = 0, returnCode = 0, reasonCode = 0;
    BPX4LOD(strlen(modulePath),   // module path length
            modulePath,           // module
            0,                    // flags
            strlen(libPath),      // libpath length
            libPath,              // libpath
            &serverFunctions,     // entry point
            &returnValue,
            &returnCode,
            &reasonCode);

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(TP_LOADHFS_RETURN),
                    "load unauthorized from HFS returned",
                    TRACE_DATA_INT(returnValue, "returnValue"),
                    TRACE_DATA_INT(returnCode, "returnCode"),
                    TRACE_DATA_INT(reasonCode, "reasonCode"),
                    TRACE_DATA_RAWDATA(
                        sizeof(*serverFunctions),
                        serverFunctions,
                        "serverFunctions"),
                    TRACE_DATA_END_PARMS);
    }

    // If the load was successful, save references to the function tables
    if (returnValue == 0) {
        serverFunctions = (server_function_stubs*) (((long long) serverFunctions) & ~1LL);
        if (serverFunctions != NULL) {
            serverAuthorizedFunctions = serverFunctions->authorized_p;
            serverUnauthorizedFunctions = serverFunctions->unauthorized_p;
        }
    }

    // Create an instance of the services results.  If an exception occurs here,
    // leave it pending so it shows up in Java
    jobject serviceResults = NULL;
    jclass serviceResultsClass = (*env)->FindClass(env, serviceResultsClassName);
    jmethodID serviceResultsCtorMethodID = (*env)->GetMethodID(env,
                                                               serviceResultsClass,
                                                               serviceResultsCtorName,
                                                               serviceResultsCtorSig);
    if (serviceResultsClass && serviceResultsCtorMethodID) {
        serviceResults = (*env)->NewObject(env,
                                           serviceResultsClass,
                                           serviceResultsCtorMethodID,
                                           returnValue,
                                           returnCode,
                                           reasonCode);
    }

    return serviceResults;
}

//---------------------------------------------------------------------
//---------------------------------------------------------------------
JNIEXPORT jint JNICALL
ntv_registerServer(JNIEnv* env, jobject jthis, jstring jAuthorizedModulePath, jstring jAngelName) {

    int registerReturnCode = -1;

    // Convert module path and angel name to EBCDIC
    const char* utfAuthorizedModulePath = (*env)->GetStringUTFChars(env, jAuthorizedModulePath, NULL);
    if (utfAuthorizedModulePath != NULL) {
        authorizedModulePath = strdup(utfAuthorizedModulePath);
        __atoe(authorizedModulePath);
        (*env)->ReleaseStringUTFChars(env, jAuthorizedModulePath, utfAuthorizedModulePath);
    }

    char* angelName = NULL;
    if (jAngelName != NULL) {
        const char* utfAngelName = (*env)->GetStringUTFChars(env, jAngelName, NULL);
        if (utfAngelName != NULL) {
            angelName = strdup(utfAngelName);
            __atoe(angelName);
            (*env)->ReleaseStringUTFChars(env, jAngelName, utfAngelName);
        }
    }

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(TP_ANGEL_REGISTER_CALL),
                    "angel register pc call",
                    TRACE_DATA_STRING(authorizedModulePath, "authorizedModulePath"),
                    TRACE_DATA_STRING(angelName, "angelName"),
                    TRACE_DATA_END_PARMS);
    }

    // Look up the angel anchor for this angel name (if there is one).
    if (angelName != NULL) {
        angelAnchor_p = serverUnauthorizedFunctions->findAngelAnchor(angelName);
    }

    // If we could not find the angel anchor, we can't register.
    // Report this back to the caller.
    if ((angelName != NULL) && (angelAnchor_p == NULL)) {
        registerReturnCode = ANGEL_REGISTER_ANGEL_NAME_NOT_EXIST;
    } else {
        registerReturnCode = serverUnauthorizedFunctions->angel_register_pc_client_stub(authorizedModulePath, angelAnchor_p);
    }

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(TP_ANGEL_REGISTER_RETURN),
                    "angel register pc return",
                    TRACE_DATA_INT(registerReturnCode, "registerReturnCode"),
                    TRACE_DATA_RAWDATA(sizeof(*angelAnchor_p), angelAnchor_p, "Angel anchor"),
                    TRACE_DATA_END_PARMS);
    }

	if (angelName != NULL) {
        free(angelName);
    }

    return registerReturnCode;
}

//---------------------------------------------------------------------
//---------------------------------------------------------------------
JNIEXPORT jint JNICALL
ntv_deregisterServer(JNIEnv* env, jobject jthis) {

    int deregisterReturnCode = -1;

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(TP_ANGEL_DEREGISTER_CALL),
                    "angel deregister pc call",
                    TRACE_DATA_END_PARMS);
    }

    deregisterReturnCode = serverUnauthorizedFunctions->angel_deregister_pc_client_stub(angelAnchor_p);

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(TP_ANGEL_DEREGISTER_RETURN),
                    "angel deregister pc return",
                    TRACE_DATA_INT(deregisterReturnCode, "deregisterReturnCode"),
                    TRACE_DATA_END_PARMS);
    }

    // Cleanup the copy of the authorized module path
    free(authorizedModulePath);
    authorizedModulePath = NULL;
    angelAnchor_p = NULL;

    return deregisterReturnCode;
}

//---------------------------------------------------------------------
//---------------------------------------------------------------------
static angel_process_data*
getAngelProcessData() {
    angel_process_data* apd_p = NULL;
    int returnCode = -1;
    char nameTokenName[16];
    char nameTokenToken[16];

    memset(nameTokenName, 0, sizeof(nameTokenName));
    memcpy(nameTokenName, ANGEL_PROCESS_DATA_TOKEN_NAME, strlen(ANGEL_PROCESS_DATA_TOKEN_NAME));
    memset(nameTokenToken, 0, sizeof(nameTokenToken));

    iean4rt(IEANT_HOME_LEVEL, nameTokenName, nameTokenToken, &returnCode);

    if (returnCode == 0) {
        memcpy(&apd_p, nameTokenToken, sizeof(apd_p));
    }

    return apd_p;
}

//---------------------------------------------------------------------
//---------------------------------------------------------------------
static server_process_data*
getServerProcessData() {
    server_process_data* spd_p = NULL;

    char spd_name[16];
    char spd_token[16];

    memset(spd_name, 0, sizeof(spd_name));
    memcpy(spd_name, SERVER_PROCESS_DATA_TOKEN_NAME,
           strlen(SERVER_PROCESS_DATA_TOKEN_NAME));
    int spd_name_token_rc;

    int level = IEANT_HOME_LEVEL;

    iean4rt(level,
            spd_name,
            spd_token,
            &spd_name_token_rc);

    // ---------------------------------------------------------------
    // If the name token exists, we can get the server process data
    // from it.  Otherwise, no server process data.
    // ---------------------------------------------------------------
    if (spd_name_token_rc == 0) {
        memcpy(&spd_p, spd_token, sizeof(spd_p));
    }

    return spd_p;
}

//---------------------------------------------------------------------
//---------------------------------------------------------------------
static int
parseNativeServiceEntries(bbgzasvt_header* header_p, unsigned char authorized, JNIEnv* env, jobject permittedServices, jobject deniedServices) {
    int functionEntryCount = header_p->num_entries;
    bbgzasve* entry = (bbgzasve*) (header_p + 1);
    for (int i = 0; i < functionEntryCount; i++) {
        const int MAX_NAME_LENGTH = sizeof(entry[i].bbgzasve_name);

        char serviceName[sizeof(entry[i].bbgzasve_name) + 1];
        memcpy(serviceName, entry[i].bbgzasve_name, sizeof(entry[i].bbgzasve_name));
        serviceName[sizeof(entry[i].bbgzasve_name)] = '\0';

        __etoa(serviceName);
        jstring jserviceName = (*env)->NewStringUTF(env, serviceName);

        char profileName[sizeof(entry[i].bbgzasve_auth_name) + 1];
        memcpy(profileName, entry[i].bbgzasve_auth_name, sizeof(entry[i].bbgzasve_auth_name));
        profileName[sizeof(entry[i].bbgzasve_auth_name)] = '\0';

        __etoa(profileName);
        jstring jprofileName = (*env)->NewStringUTF(env, profileName);

        // If we have an angel we can use the bits, otherwise they're denied
        if ((authorized) && (entry[i].bbgzasve_runtime_bits.authorized_to_use)) {
            addToCollection(env, permittedServices, jserviceName); // Service first
            addToCollection(env, permittedServices, jprofileName); // Authorization group second
        } else {
            addToCollection(env, deniedServices, jserviceName); // Service first
            addToCollection(env, deniedServices, jprofileName); // Authorization group second
        }
    }

    return functionEntryCount;
}

//---------------------------------------------------------------------
//---------------------------------------------------------------------
JNIEXPORT jint JNICALL
ntv_getNativeServiceEntries(JNIEnv* env, jobject jthis, jobject permittedServices, jobject deniedServices, jobject permittedClientServices, jobject deniedClientServices) {

    // Get a reference to the angel process data.
    angel_process_data* apd_p = getAngelProcessData();
    server_process_data* spd_p = getServerProcessData();

    // Look for the server authorized function module
    struct bbgzsafm* safm_p = NULL;
    struct bbgzasvt_header* scfm_p = NULL;
    unsigned char authorizedSafm = TRUE;
    unsigned char authorizedScfm = TRUE;

    if (apd_p) {
        safm_p = (struct bbgzsafm*) apd_p->safm_function_table_p;
    }

    if (spd_p) {
        scfm_p = spd_p->localScfmHeader_p;
    }

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(TP_PGOO_AUTHORIZED_FUNCTIONS),
                    "authorized function vector",
                    TRACE_DATA_RAWDATA(
                        sizeof(struct bbgzsafm),
                        apd_p ? apd_p->safm_function_table_p : NULL,
                        "authorized function vector table"),
                    TRACE_DATA_PTR(
                        apd_p ? apd_p->scfm_function_table_p : NULL,
                        "authorized client function vector table ptr"),
                    TRACE_DATA_END_PARMS);
    }

    // Attempt to perform an unauthorized load so we can populate the
    // service information with disabled entries.
    if (safm_p == NULL) {
        safm_p = getAuthorizedFunctionTableUnauth();
        authorizedSafm = FALSE;
    }

    if (scfm_p == NULL) {
        scfm_p = getAuthorizedClientFunctionTableUnauth();
        authorizedScfm = FALSE;
    }

    // Still no luck so there's not much we can do...
    if ((safm_p == NULL) || (scfm_p == NULL)) {
        return 0;
    }

    // Go process the authorized services for the server.
    int functionEntryCount = parseNativeServiceEntries(&(safm_p->header), authorizedSafm, env, permittedServices, deniedServices);

    // We loaded the authorized module so we should unload it
    if (authorizedSafm == FALSE) {
        free(safm_p);
        safm_p = NULL;
    }

    // Now parse the client module
    functionEntryCount += parseNativeServiceEntries(scfm_p, authorizedScfm, env, permittedClientServices, deniedClientServices);

    // We loaded the authorized client module so we should unload it
    if (authorizedScfm == FALSE) {
        free(scfm_p);
        scfm_p = NULL;
    }

    return functionEntryCount;
}

//---------------------------------------------------------------------
//---------------------------------------------------------------------
static void
addToCollection(JNIEnv* env, jobject collection, jstring string) {
#pragma convert("iso8859-1")
    const char* interfaceName = "java/util/Collection";
    const char* methodName = "add";
    const char* methodDesc = "(Ljava/lang/Object;)Z";
#pragma convert(pop)

    // Find the java.util.Collection interface
    jclass collectionClazz = (*env)->FindClass(env, interfaceName);
    if (collectionClazz == NULL) {
        return;
    }

    // Find the boolean add(java.lang.Object) method
    jmethodID addMethod = (*env)->GetMethodID(env, collectionClazz, methodName, methodDesc);
    if (addMethod == NULL) {
        return;
    }

    (*env)->CallBooleanMethod(env, collection, addMethod, string);
}

//---------------------------------------------------------------------
//---------------------------------------------------------------------
static struct bbgzsafm*
getAuthorizedFunctionTableUnauth() {
    struct bbgzsafm* safmTable = NULL;

    int returnValue = 0;
    int returnCode = 0;
    int reasonCode = 0;
    struct bbgzsafm* entryPoint = NULL;

    BPX4LOD(strlen(authorizedModulePath), // module path length
            authorizedModulePath,         // module path
            0,                            // flags
            0,                            // libpath length
            "",                           // libpath
            &entryPoint,                  // entry point
            &returnValue,
            &returnCode,
            &reasonCode);

    // Bail if we couln't load the module
    if (returnValue != 0 || entryPoint == NULL) {
        return NULL;
    }

    // Cast the entry point to a function module and get working
    // storage to copy the table into
    entryPoint = (struct bbgzsafm*) (((long long) entryPoint) & ~1LL);
    long safmSize = entryPoint->header.size + sizeof(entryPoint->header.module_name);
    safmTable = malloc(safmSize);

    // Copy the table and release the module
    if (safmTable != NULL) {
        memcpy(safmTable, entryPoint, safmSize);
        BPX4DEL(entryPoint, &returnValue, &returnCode, &reasonCode);
    }

    return safmTable;
}

static struct bbgzasvt_header*
getAuthorizedClientFunctionTableUnauth() {
    struct bbgzasvt_header* scfmTable = NULL;

    int returnValue = 0;
    int returnCode = 0;
    int reasonCode = 0;
    struct bbgzasvt_header* entryPoint = NULL;

    // Need to replace the 'a' in SAFM with a 'c' for SCFM.  Note that the
    // same transformation happens in angel_server_pc.mc.
    char* scfmName = strdup(authorizedModulePath);
    if (scfmName == NULL) {
        return NULL;
    }

    int scfmNameLen = strlen(scfmName);
    *(scfmName + (scfmNameLen - 3)) = 'c';

    BPX4LOD(strlen(scfmName),             // module path length
            scfmName,                     // module path
            0,                            // flags
            0,                            // libpath length
            "",                           // libpath
            &entryPoint,                  // entry point
            &returnValue,
            &returnCode,
            &reasonCode);

    // Bail if we couln't load the module
    if (returnValue != 0 || entryPoint == NULL) {
        free(scfmName);
        return NULL;
    }

    // Cast the entry point to a function module and get working
    // storage to copy the table into
    entryPoint = (struct bbgzasvt_header*) (((long long) entryPoint) & ~1LL);
    long scfmSize = entryPoint->size + sizeof(entryPoint->module_name);
    scfmTable = malloc(scfmSize);

    // Copy the table and release the module
    if (scfmTable != NULL) {
        memcpy(scfmTable, entryPoint, scfmSize);
        BPX4DEL(entryPoint, &returnValue, &returnCode, &reasonCode);
    }

    free(scfmName);

    return scfmTable;
}

//---------------------------------------------------------------------
//---------------------------------------------------------------------
JNIEXPORT jint JNICALL
ntv_getAngelVersion(JNIEnv* env, jobject jthis) {
    const server_unauthorized_function_stubs* stubs_p = getServerUnauthorizedFunctionStubs();
    return stubs_p->getAngelVersion(angelAnchor_p);
}
