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
 * Functions related to tracing native code in the server process.
 */
#include <ieac.h>
#include <jni.h>
#include <pthread.h>
#include <stdarg.h>
#include <stdlib.h>

#include "include/bbgztrgoo.h"
#include "include/ieantc.h"
#include "include/mvs_psw.h"
#include "include/ras_tracing.h"
#include "include/server_jni_method_manager.h"
#include "include/server_logging_jni.h"
#include "include/server_trgoo_services.h"
#include "include/stack_services.h"

// TODO: Restructure unit test tracing and remove this
#include "common_tracing_functions_c.c"

static const iea_release_code go_away_release_code = "END";

bbgztrgoo* global_trgoo_p = 0;

//---------------------------------------------------------------------
// The initial value needs to be set at the highest level.
//
// Since the Java NativeTraceHandler is responsible for tracking this
// value at least one trace call needs to be handled by the native
// trace handler which will kick off a trace component registration
// that will result in deriving the highest active trace level and
// then calling down into native ntv_setTraceLevel to set this value
// based on what was specified in the trace.specification.
//---------------------------------------------------------------------
/**
 * Aggregate trace level.  This is the highest level of trace that's enabled for
 * the server.  This is used by the TraceActive check to avoid the overhead of
 * building a trace record when the requested level of trace is not enabled.
 */
unsigned char RAS_aggregate_trace_level = trc_level_detailed;

/**
 * This is the trace level used by the unit test environment.
 * If it is configured the value would be from 0 to 3.
 */
extern int RAS_unittest_trace_level;

/**
 * This is the fully qualified path of the location of the z unit
 * test native trace log.
 */
extern char* RAS_unittest_trace_filename;

/**
 * The file descriptor for the unit test log file.
 */
extern int RAS_unittest_trace_filedesc;

/**
 * The name of the name token used to store the address of the
 * RAS_aggregate_trace_level, which is referenced by the metal C stack
 * prefix.  Note that this name is also referenced in macros/BBGZDAPI.
 */
#define RAS_AGGREGATE_TRACE_LEVEL_TOKEN_NAME "BBGZ_RAS_LVL_PTR"

//---------------------------------------------------------------------
// Callback methods for the NativeTraceHandler
//---------------------------------------------------------------------
int
nativeTraceHandlerRegistration(JNIEnv* env, jclass clazz, jobjectArray extraInfo);

int
nativeTraceHandlerDeregistration(JNIEnv* env, jclass clazz, jobjectArray extraInfo);

//---------------------------------------------------------------------
// JNI function declaration and export
//---------------------------------------------------------------------
#pragma export(ntv_getThreadData)
JNIEXPORT jlong JNICALL
ntv_getThreadData(JNIEnv* env, jobject jobj);

#pragma export(ntv_getTraces)
JNIEXPORT jbyteArray JNICALL
ntv_getTraces(JNIEnv* env, jobject jobj, jlong threadData_p);

#pragma export(ntv_stopListeningForTraces)
JNIEXPORT jint JNICALL
ntv_stopListeningForTraces(JNIEnv* env, jobject jobj, jlong threadData_p);

#pragma export(ntv_traceWritten)
JNIEXPORT jint JNICALL
ntv_traceWritten(JNIEnv* env, jobject jobj, jlong threadData_p);

#pragma export(ntv_setTraceLevel)
JNIEXPORT void JNICALL
ntv_setTraceLevel(JNIEnv* env, jobject jobj, jint traceLevel);

#pragma export(ntv_getNativeTraceComponents)
JNIEXPORT jobjectArray JNICALL
ntv_getNativeTraceComponents(JNIEnv* env, jobject jobj);

//---------------------------------------------------------------------
// JNI native method structure for the NativeTraceHandler methods
//---------------------------------------------------------------------
#pragma convert("ISO8859-1")
static const JNINativeMethod nativeTraceHandlerMethods[] = {
    { "ntv_getThreadData",
      "()J",
      (void *) ntv_getThreadData },
    { "ntv_getTraces",
      "(J)[B",
      (void *) ntv_getTraces },
    { "ntv_stopListeningForTraces",
      "(J)I",
      (void *) ntv_stopListeningForTraces },
    { "ntv_traceWritten",
      "(J)I",
      (void *) ntv_traceWritten },
    { "ntv_setTraceLevel",
      "(I)V",
      (void *) ntv_setTraceLevel },
    { "ntv_getNativeTraceComponents",
      "()[Lcom/ibm/ws/zos/logging/internal/NativeTraceHandler$NativeTraceComponentDefinition;",
      (void *) ntv_getNativeTraceComponents }
};
#pragma convert(pop)

//---------------------------------------------------------------------
// NativeMethodDescriptor for the NativeTraceHandler
//---------------------------------------------------------------------
#pragma export(zJNI_com_ibm_ws_zos_logging_internal_NativeTraceHandler)
NativeMethodDescriptor zJNI_com_ibm_ws_zos_logging_internal_NativeTraceHandler = {
   .registrationFunction = nativeTraceHandlerRegistration,
   .deregistrationFunction = nativeTraceHandlerDeregistration,
   .nativeMethodCount = sizeof(nativeTraceHandlerMethods) / sizeof(nativeTraceHandlerMethods[0]),
   .nativeMethods = nativeTraceHandlerMethods
};

//---------------------------------------------------------------------
// JNI function declaration and export NativeLibraryTraceHelper
//---------------------------------------------------------------------

#pragma export(ntv_initTraceForUnitTest)
JNIEXPORT jint JNICALL
ntv_initTraceForUnitTest(JNIEnv *env, jclass clazz, jint traceLevel, jstring logFileName);

#pragma export(ntv_resetTraceForUnitTest)
JNIEXPORT jint JNICALL
ntv_resetTraceForUnitTest(JNIEnv *env, jclass clazz);

//---------------------------------------------------------------------
// JNI native method structure for the NativeLibraryTraceHelper
//---------------------------------------------------------------------
#pragma convert("ISO8859-1")
static const JNINativeMethod nativeLibraryTraceHelperMethods[] = {
    { "ntv_initTraceForUnitTest",
      "(ILjava/lang/String;)I",
      (void *) ntv_initTraceForUnitTest },
    { "ntv_resetTraceForUnitTest",
      "()I",
      (void *) ntv_resetTraceForUnitTest }
};
#pragma convert(pop)

//---------------------------------------------------------------------
// NativeMethodDescriptor for the NativeLibraryTraceHelper
//---------------------------------------------------------------------
#pragma export(zJNI_test_common_zos_NativeLibraryTraceHelper)
NativeMethodDescriptor zJNI_test_common_zos_NativeLibraryTraceHelper = {
   .registrationFunction = NULL,
   .deregistrationFunction = NULL,
   .nativeMethodCount = sizeof(nativeLibraryTraceHelperMethods) / sizeof(nativeLibraryTraceHelperMethods[0]),
   .nativeMethods = nativeLibraryTraceHelperMethods
};


//---------------------------------------------------------------------
// Module scoped utility methods
//---------------------------------------------------------------------
static JNIEnv* getJavaEnv();

static unsigned long long getStoreClockTimeStamp();

static jstring createJavaString(JNIEnv* env, const char* ebcdicString);

static void debug(const char* format, ...);

//---------------------------------------------------------------------
// Module scoped data related to the trace handler
//---------------------------------------------------------------------

/**
 * The cached reference to the JVM that loaded the handler code.
 */
JavaVM* javaVM = NULL;

/**
 * Reference to the native trace handler.
 */
jobject handlerInstance  = NULL;

/**
 * Cached reference to the trace handler callback method.
 */
jmethodID callbackMethodID = NULL;

/**
 * Deletes the address space level name token which the metal C code used to
 * reference the RAS_aggregate_trace_level variable.
 */
static void
deleteRasAggregateTraceLevelNameToken() {
    int returnCode = 0;
    char nameTokenName[16];
    memcpy(nameTokenName, RAS_AGGREGATE_TRACE_LEVEL_TOKEN_NAME, sizeof(nameTokenName));

    iean4dl(IEANT_HOME_LEVEL, nameTokenName, &returnCode);
}

/**
 * Creates an address space level name token which the metal C code will use to
 * reference the RAS_aggregate_trace_level variable.
 *
 * @return 0 on success.
 */
static int
createRasAggregateTraceLevelNameToken() {
    int returnCode = 0;

    //---------------------------------------------------------------------
    // Get the address of the trace level byte, and create a name token for
    // it.  The code that creates the metal C stack prefix area will look
    // up the name token and set the address of the trace level byte into
    // it.
    //---------------------------------------------------------------------
    unsigned char* RAS_aggregate_trace_level_p = &RAS_aggregate_trace_level;
    char nameTokenName[16];
    char nameTokenToken[16];
    memcpy(nameTokenName, RAS_AGGREGATE_TRACE_LEVEL_TOKEN_NAME, sizeof(nameTokenName));
    memset(nameTokenToken, 0, 8);
    memcpy(&nameTokenToken[8], &RAS_aggregate_trace_level_p, sizeof(unsigned char*));

    iean4cr(IEANT_HOME_LEVEL, nameTokenName, nameTokenToken, IEANT_NOPERSIST, &returnCode);

    //---------------------------------------------------------------------
    // If the name token already exists, we may be in a situation where
    // the address space was re-used, and the previous server instance did
    // not get shut down cleanly.  In this case we should replace the name
    // token.
    //---------------------------------------------------------------------
    if (returnCode == IEANT_DUP_NAME) {
        iean4dl(IEANT_HOME_LEVEL, nameTokenName, &returnCode);

        if (returnCode == IEANT_OK) {
            iean4cr(IEANT_HOME_LEVEL, nameTokenName, nameTokenToken, IEANT_NOPERSIST, &returnCode);
        }
    }

    return returnCode;
}

/**
 * Deletes the address space level name token which the metal C code used to
 * reference the trace goo.
 */
static void
deleteTraceGooNameToken() {
    int returnCode = 0;
    char nameTokenName[16];
    memcpy(nameTokenName, BBGZTRGOO_TOKEN_NAME, sizeof(nameTokenName));

    iean4dl(IEANT_HOME_LEVEL, nameTokenName, &returnCode);
}

/**
 * Creates an address space level name token which the metal C code will use to
 * reference the trace goo.
 *
 * @return 0 on success.
 */
static int
createTraceGooNameToken() {
    int returnCode = 0;
    char nameTokenName[16];
    char userToken[16];
    iea_auth_type auth = IEA_UNAUTHORIZED;

    memcpy(nameTokenName, BBGZTRGOO_TOKEN_NAME, sizeof(nameTokenName));
    memset(userToken, 0, sizeof(userToken));

    bbgztrgoo* trgoo_p = malloc(sizeof(bbgztrgoo) + sizeof(TraceThreadProcessingData));
    if (trgoo_p == NULL) {
        perror("malloc of bbgztrgoo failed");
        return JNI_ERR;
    }

    memset(trgoo_p, 0, sizeof(bbgztrgoo) + sizeof(TraceThreadProcessingData));
    memcpy(trgoo_p->bbgztrgoo_eyecatcher, BBGZTRGOO_EYE, sizeof(trgoo_p->bbgztrgoo_eyecatcher));
    trgoo_p->bbgztrgoo_version = BBGZTRGOO_VERSION_1;
    trgoo_p->bbgztrgoo_length = sizeof(bbgztrgoo);


    TraceThreadProcessingData* traceThreadProcessingData_p = (TraceThreadProcessingData*) ( ((void *) trgoo_p) + sizeof(bbgztrgoo));
    memcpy(&traceThreadProcessingData_p->traceThreadProcessingData_eyecatcher,
           TRACETHREADPROCESSINGDATA_EYE,
           sizeof(traceThreadProcessingData_p->traceThreadProcessingData_eyecatcher));

    // Allocate pet for Trace Thread data processing
    iea4ape(&returnCode, auth, traceThreadProcessingData_p->traceThreadProcessingData_pet);
    if (returnCode) {
        // Disable trace if we couldn't get a PET
        traceThreadProcessingData_p = NULL;
    }

    trgoo_p->bbgztrgoo_trace_thread_data_p = traceThreadProcessingData_p;

    // Expose the new TRGOO
    global_trgoo_p = trgoo_p;
    memcpy(userToken, &global_trgoo_p, 8);

    iean4cr(IEANT_HOME_LEVEL, nameTokenName, userToken, IEANT_NOPERSIST, &returnCode);

    //---------------------------------------------------------------------
    // If the name token already exists, we may be in a situation where
    // the address space was re-used, and the previous server instance did
    // not get shut down cleanly.  In this case we should replace the name
    // token.
    //---------------------------------------------------------------------
    if (returnCode == IEANT_DUP_NAME) {
        iean4dl(IEANT_HOME_LEVEL, nameTokenName, &returnCode);

        if (returnCode == IEANT_OK) {
            iean4cr(IEANT_HOME_LEVEL, nameTokenName, userToken, IEANT_NOPERSIST, &returnCode);
        }
    }

    return returnCode;
}

/**
 * Deletes the address space level name token which the metal C code used to
 * reference the RAS_unittest_trace_level variable.
 */
static void
deleteRasUnitTestTraceLevelNameToken() {
    int returnCode = 0;
    char nameTokenName[16];
    memcpy(nameTokenName, RAS_UNITTEST_TRACE_LEVEL_TOKEN_NAME, sizeof(nameTokenName));

    iean4dl(IEANT_HOME_LEVEL, nameTokenName, &returnCode);
}

/**
 * Creates an address space level name token which the metal C code will use to
 * reference the RAS_unittest_trace_level and RAS_unittest_trace_filedesc.
 *
 * @return 0 on success.
 */
static int
createRasUnitTestTraceLevelNameToken() {
    int returnCode = 0;

    //---------------------------------------------------------------------
    // Create a name token to contain the unittest variables for
    // RAS_unittest_trace_filedesc and RAS_unittest_trace_level.
    //---------------------------------------------------------------------

    char nameTokenName[16];
    char nameTokenToken[16];
    memcpy(nameTokenName, RAS_UNITTEST_TRACE_LEVEL_TOKEN_NAME, sizeof(nameTokenName));
    memcpy(&(nameTokenToken[0]), &RAS_unittest_trace_filedesc, 4);
    memcpy(&(nameTokenToken[4]), &RAS_unittest_trace_level, 4);

    iean4cr(IEANT_HOME_LEVEL, nameTokenName, nameTokenToken, IEANT_NOPERSIST, &returnCode);

    //---------------------------------------------------------------------
    // If the name token already exists, we may be in a situation where
    // the address space was re-used, and the previous server instance did
    // not get shut down cleanly.  In this case we should replace the name
    // token.
    //---------------------------------------------------------------------
    if (returnCode == IEANT_DUP_NAME) {
        iean4dl(IEANT_HOME_LEVEL, nameTokenName, &returnCode);

        if (returnCode == IEANT_OK) {
            iean4cr(IEANT_HOME_LEVEL, nameTokenName, nameTokenToken, IEANT_NOPERSIST, &returnCode);
        }
    }

    return returnCode;
}


/**
 * Respond to the registerNatives call performed out of bundle activation and
 * setup the required native infrastructure.  This method will resolve and cache
 * the method ID's for the trace handler code, save a reference to the JavaVM
 * that loaded this code, and prepare to manage thread-specific references to
 * JNI environments.
 *
 * @param env the JNI environment for the calling thread.
 * @param clazz the class that native registration is occurring for.
 * @param extraInfo context information from the caller.  For the native trace
 *        handler, the first element is the handler instance and the second
 *        element is the name of the trace callback method.
 *
 * @return JNI_OK on success; JNI_ERR or on error.
 */
int
nativeTraceHandlerRegistration(JNIEnv* env, jclass clazz, jobjectArray extraInfo) {
    // Cache a reference to our JVM
    int returnCode = (*env)->GetJavaVM(env, &javaVM);
    if (returnCode != JNI_OK) {
        javaVM = NULL;
        return returnCode;
    }

    returnCode = createTraceGooNameToken();
    if (returnCode != 0) {
        return JNI_ERR;
    }

    // Get the callback instance from extra info
    jobject handler = (*env)->GetObjectArrayElement(env, extraInfo, 0);
    handlerInstance = (*env)->NewGlobalRef(env, handler);

    // Get the name of the callback function from extra info
    jstring callbackName = (jstring) (*env)->GetObjectArrayElement(env, extraInfo, 1);
    const char* utfCallbackName = (*env)->GetStringUTFChars(env, callbackName, NULL);
#pragma convert("iso8859-1")
    const char* signature = "(IIJJIII)I";
#pragma convert(pop)

    // Get the callback method ID
    callbackMethodID = (*env)->GetMethodID(env, clazz, utfCallbackName, signature);
    (*env)->ReleaseStringUTFChars(env, callbackName, utfCallbackName);

    // Bail if the java stuff didn't work
    if (callbackMethodID == NULL || handlerInstance == NULL) {
        return JNI_ERR;
    }

    // Liberty is embedded in a number of environments.  One of which is a CICS environment in which they manage their
    // own tasks and they own the pthread create and destroy.  They attach/detach these tasks over and over to the JVM.
    // The consequence is that when the related Java thread ends, the native pthread exit/destroy functions are
    // **NOT** driven.  So, any cached info using pthread services survive multiple Java Threads using the native
    // tasks.
    //
    // We can't use pthread_setspecific and pthreads_getspecific for the above reason.


    // Set references to the LE static area that the metal C code will use
    returnCode = createRasAggregateTraceLevelNameToken();
    if (returnCode != 0) {
        return JNI_ERR;
    }

    return JNI_OK;
}

/**
 * Respond to the host bundle stop by performing any necessary cleanup.
 *
 * @param env the calling thread's JNI environment.
 * @param clazz the class that deregistration is occurring for.
 * @param extraInfo context provided to the registration function.
 *
 * @return JNI_OK.
 */
int
nativeTraceHandlerDeregistration(JNIEnv* env, jclass clazz, jobjectArray extraInfo) {
    javaVM = NULL;

    (*env)->DeleteGlobalRef(env, handlerInstance);
    handlerInstance = NULL;
    callbackMethodID = NULL;

    // Remove references to the LE static area that the metal C code used
    deleteRasAggregateTraceLevelNameToken();
    deleteTraceGooNameToken();

    return JNI_OK;
}

/**
 * Main trace formatting routine.
 *
 * @param usr_level the trace level requester by the trace.
 * @param trace_point the trace point of the trace.
 * @param event_desc the description of the trace.
 * @param usr_var_list the valist containing the data to be traced.
 */
void
TraceWriteV(enum trc_level usr_level, int trace_point, char* event_desc, va_list usr_var_list) {

    int tcbAddress = *((int*) 0x21c);
    // get state and key
    bbgz_psw psw;
    extractThePSW(&psw);
    int executionState = psw.pbm_state ? 1 : 0;
    int key = psw.key;

    // If unittest trace level is specified then write the trace records using native facilities only.
    if (RAS_unittest_trace_level > 0) {
        if (usr_level > 0 && usr_level <= RAS_unittest_trace_level) {
            TraceWriteNativeV(usr_level, trace_point, event_desc, usr_var_list, tcbAddress, executionState, key, RAS_unittest_trace_filedesc);
        }
        return;
    }

    if (javaVM == NULL) {
        debug("(NO JVM) Trace description=%s\n", event_desc);
        return;
    }

    JNIEnv* env = getJavaEnv();
    if (env == NULL) {
        debug("(NO JAVA ENV) Trace description=%s\n", event_desc);
        return;
    }

    if (handlerInstance == NULL || callbackMethodID == NULL) {
        debug("(NO LOGGING OBJECT) Trace description=%s\n", event_desc);
        return;
    }

    // Call up to java to write the native trace
    (*env)->CallIntMethod(env,
                          handlerInstance,
                          callbackMethodID,
                          usr_level,
                          trace_point,
                          usr_var_list,
                          getStoreClockTimeStamp(),
                          tcbAddress,
                          psw.pbm_state,
                          psw.key);
}

/**
 * Data returned from attempt to get trace Stack elements
 */
typedef struct returnData {
    int                 returnDataReturnCode;
    int                 iea4pse_rc;
    int                 iea4rls_rc;
    int                 iea4dpe_rc;
    traceStackElement*  returnDataTraceStackElement_p;
} returnData;


/**
 * Set indication that the Trace Thread is waiting for new traces.
 */
static void traceThreadNeedsRelease(bbgztrgoo * bbgztrgoo_p) {
    unsigned int oldInt, newInt = 1;
    unsigned int* intTarget_Ptr = &(bbgztrgoo_p->bbgztrgoo_threadThreadReleaseLock);

    oldInt = *intTarget_Ptr;
    do {
    } while(cs((cs_t *) &oldInt,
               (cs_t *) intTarget_Ptr,
               (cs_t)   newInt
               ));

    return;
}

/**
 * Reset indication that the Trace Thread is waiting for new traces.
 *
 * @return 1 if successfully reset from set value, 0 otherwise.
 */
static int traceThreadNeedsReleaseReset(bbgztrgoo * bbgztrgoo_p) {
    unsigned int oldInt, newInt = 0;
    unsigned int* intTarget_Ptr = &(bbgztrgoo_p->bbgztrgoo_threadThreadReleaseLock);

    oldInt = *intTarget_Ptr;
    do {
        if (oldInt == 0) {
            return 0;
        }
    } while(cs((cs_t *) &oldInt,
               (cs_t *) intTarget_Ptr,
               (cs_t)   newInt
               ));

    return 1;
}

#define DATA_RETURN_CODE_GOT_TRACE_ELEMENT  1
#define DATA_RETURN_CODE_GO_AWAY            2
#define DATA_RETURN_CODE_IEA4PSE_FAILURE    5
#define DATA_RETURN_CODE_IEA4RLS_FAILURE    6
#define DATA_RETURN_CODE_IEA4PSE_FAILURE2   7
#define DATA_RETURN_CODE_NO_TRGOO           8

/**
 * Get traces form the stack.
 *
 * @param traceThreadProcessingData_p processing data for this thread.
 *
 * @return returnData area containing various return codes and a pointer to a trace stack element.
 */
returnData getTraceFromStack(TraceThreadProcessingData* traceThreadProcessingData_p) {
    returnData dataToReturn;

    memset(&dataToReturn, 0, sizeof(dataToReturn));

    iea_auth_type auth = IEA_UNAUTHORIZED;
    int rc;
    iea_release_code release_code;

    // If there are no trace elements left from the last time we got some
    // go get some more.
    if (is_stack_empty(&traceThreadProcessingData_p->traceThreadProcessingData_traceStack)) {
        bbgztrgoo* trgoo_p = global_trgoo_p;

        /* If there is a process trace goo */
        if (trgoo_p != NULL) {
            /* try to get some traces. pause if there is none */
            while (dataToReturn.returnDataReturnCode == 0) {
                /* get stack of trace elements */
                concurrent_stack_element * TraceStackElement_p = get_entire_stack(&(trgoo_p->bbgztrgoo_trace_stack));
                concurrent_stack_element * nextTraceStackElement_p;
                /* If we got some trace elements exit loop */
                if (TraceStackElement_p != NULL) {

                    while (dataToReturn.returnDataTraceStackElement_p == NULL) {
                        /* If only one element on the stack or we are at the last element return it */
                        if (TraceStackElement_p->stack_element_next_p == 0) {
                            dataToReturn.returnDataTraceStackElement_p = (traceStackElement *) TraceStackElement_p;
                            dataToReturn.returnDataReturnCode = DATA_RETURN_CODE_GOT_TRACE_ELEMENT;
                        } else {
                            /* else more than one element push newest trace on stack so we write the oldest trace first */
                            nextTraceStackElement_p = TraceStackElement_p->stack_element_next_p;
                            push_on_stack(&(traceThreadProcessingData_p->traceThreadProcessingData_traceStack),
                                          TraceStackElement_p);
                            TraceStackElement_p = nextTraceStackElement_p;
                        }
                    } // End while there are trace stack elements to flip
                } else { // else no traces on the stack
                    // Tell trace producers that I need to be released for a new trace.
                    traceThreadNeedsRelease(trgoo_p);

                    /* if trace stack is still empty pause */
                    if (is_stack_empty(&(trgoo_p->bbgztrgoo_trace_stack))) {

                        /* pause */
                        iea_PEToken tempPet;
                        iea4pse(&rc, auth, traceThreadProcessingData_p->traceThreadProcessingData_pet, tempPet, release_code);

                        if (rc) {
                            /* pause failed */
                            dataToReturn.returnDataReturnCode = DATA_RETURN_CODE_IEA4PSE_FAILURE;
                            dataToReturn.iea4pse_rc = rc;
                        } else {
                            /* see if we were released to go away */
                            if (memcmp(release_code, go_away_release_code, sizeof(release_code)) == 0) {
                                dataToReturn.returnDataReturnCode = DATA_RETURN_CODE_GO_AWAY;
                                iea4dpe(&rc, auth, tempPet);
                                if (rc) {
                                    /* deallocate pause element failed */
                                    dataToReturn.iea4dpe_rc = rc;
                                }
                            } else { /* save pet returned from pause */
                                memcpy(traceThreadProcessingData_p->traceThreadProcessingData_pet, tempPet, sizeof(traceThreadProcessingData_p->traceThreadProcessingData_pet));
                            }
                        }
                    } // End trace element stack empty
                    else /* else trace stack no longer empty. reset release indicator so we do not hang */
                    {
                        int reset = traceThreadNeedsReleaseReset(trgoo_p);

                        if (reset) {
                            // Just loop around pickup new trace(s).
                            continue;
                        } else {
                            /*
                            Someone else must have removed us so pause hopefully
                            briefly since they should have released us */
                            iea_PEToken tempPet;
                            iea4pse(&rc, auth, traceThreadProcessingData_p->traceThreadProcessingData_pet, tempPet, release_code);
                            if (rc) {
                                /* pause failed */
                                dataToReturn.returnDataReturnCode = DATA_RETURN_CODE_IEA4PSE_FAILURE2;
                                dataToReturn.iea4pse_rc = rc;
                            } else { // else pause worked
                                /* see if we were released to go away */
                                if (memcmp(release_code, go_away_release_code, sizeof(release_code)) == 0) {
                                    dataToReturn.returnDataReturnCode = DATA_RETURN_CODE_GO_AWAY;
                                    iea4dpe(&rc, auth, tempPet);
                                    if (rc) {
                                        /* deallocate pause element failed */
                                        dataToReturn.iea4dpe_rc = rc;
                                    }
                                } else { /* save pet returned from pause */
                                    memcpy(traceThreadProcessingData_p->traceThreadProcessingData_pet, tempPet, sizeof(traceThreadProcessingData_p->traceThreadProcessingData_pet));
                                } // end do not go away
                            } // end pause worked
                        }   // end else, someone should be releasing us.
                    } // end trace stack no longer empty
                } // end no traces on the stack
            } // end while no trace data
        } else {  // else no trgoo
            dataToReturn.returnDataReturnCode = DATA_RETURN_CODE_NO_TRGOO;
        } // end no trgoo
    } else { // else trace elements left. pop one off the stack
        dataToReturn.returnDataTraceStackElement_p = (traceStackElement *)
            pop_off_stack(&(traceThreadProcessingData_p->traceThreadProcessingData_traceStack));
        dataToReturn.returnDataReturnCode = DATA_RETURN_CODE_GOT_TRACE_ELEMENT;
    } // end trace elements left.

    return dataToReturn;

} /* end getTraceFromStack */

/**
 * Get a tracing thread processing data.
 *
 * @param  env the JNI environment reference provided by the JVM.
 * @param  jobj the object instance this method was invoked against.
 *
 * @return a pointer to the thread processing data as a jlong.
 */
JNIEXPORT jlong JNICALL
ntv_getThreadData(JNIEnv* env, jobject jobj) {

    TraceThreadProcessingData* traceThreadProcessingData_p = NULL;

    bbgztrgoo* trgoo_p = global_trgoo_p;

    /* If there is a process trace goo */
    if (trgoo_p != NULL) {
        traceThreadProcessingData_p = trgoo_p->bbgztrgoo_trace_thread_data_p;
    }

    return (jlong) traceThreadProcessingData_p;
}

#pragma pack(1)
typedef struct OutputParms OutputParms;
struct OutputParms {
    int                      output_trace_level;  // 0x00
    int                      output_trace_point;  // 0x04
    jlong                    output_valist_ptr;   // 0x08
    unsigned long long       output_create_time;  // 0x10
    int                      output_create_tcb;   // 0x18
    int                      output_create_state; // 0x1c
    int                      output_create_key;   // 0x20
};
#pragma pack(reset)

/**
 * Get native traces.
 *
 * @param env the JNI environment reference provided by the JVM.
 * @param jobj the object instance this method was invoked against.
 * @param threadData_p pointer to the thread data
 *
 * @return a pointer to the trace data that is to be written as a jlong.
 */
JNIEXPORT jbyteArray JNICALL
ntv_getTraces(JNIEnv* env, jobject jobj, jlong threadData_p) {
    TraceThreadProcessingData* traceThreadProcessingData_p = (TraceThreadProcessingData *) threadData_p;
    jbyteArray outputByteArray = 0;
    OutputParms outputParmsArea = {{0}};

    returnData outputData = getTraceFromStack(traceThreadProcessingData_p);


    if (outputData.returnDataReturnCode == DATA_RETURN_CODE_GOT_TRACE_ELEMENT) {
        outputParmsArea.output_trace_level = outputData.returnDataTraceStackElement_p->traceStackElement_traceLevel;
        outputParmsArea.output_trace_point = outputData.returnDataTraceStackElement_p->traceStackElement_tracePoint;
        outputParmsArea.output_valist_ptr = (jlong) outputData.returnDataTraceStackElement_p->traceStackElement_tracedata_p;
        outputParmsArea.output_create_time = outputData.returnDataTraceStackElement_p->traceStackElement_createTime;
        outputParmsArea.output_create_tcb = outputData.returnDataTraceStackElement_p->traceStackElement_createTcb;
        outputParmsArea.output_create_state = outputData.returnDataTraceStackElement_p->traceStackElement_createState;
        outputParmsArea.output_create_key = outputData.returnDataTraceStackElement_p->traceStackElement_createKey;

        memcpy(traceThreadProcessingData_p->traceThreadProcessingData_requestor_pet,
               outputData.returnDataTraceStackElement_p->traceStackElement_pet,
               sizeof(iea_PEToken));
    }

    // TODO: check rc if some kind of error call up to java and put return codes on a thread local
    //       do not forget about the go away rc maybe put in byteArray

    outputByteArray = (*env)->NewByteArray(env, sizeof(outputParmsArea));
    if (outputByteArray == NULL ) {
        return NULL;
    }

    (*env)->SetByteArrayRegion(env, outputByteArray, 0, sizeof(outputParmsArea), (jbyte*)&(outputParmsArea));
    if ((*env)->ExceptionOccurred(env)) {
        return NULL;
    }

    return outputByteArray;
}

/**
 * Tell logging thread to stop waiting for traces.
 *
 * @param env the JNI environment reference provided by the JVM.
 * @param jobj the object instance this method was invoked against.
 * @param threadData_p pointer to the thread processing data.
 *
 * @return the release return code.
 */
JNIEXPORT jint JNICALL
ntv_stopListeningForTraces(JNIEnv* env, jobject jobj, jlong threadData_p) {
    TraceThreadProcessingData* traceThreadProcessingData_p = (TraceThreadProcessingData*) threadData_p;
    iea_release_code release_code;
    jint returnCode = 0;

    /* TODO @tj pop thread offstack ? */
    /*
     *      pop every one looking for the one release it with go away
     *      what about the ones that might be in flight?  set a flag where release storage
     *      need to work on who will free the thread data.
     */

    memcpy(release_code, go_away_release_code, sizeof(iea_release_code));
    iea4rls(&returnCode,
            IEA_UNAUTHORIZED,
            traceThreadProcessingData_p->traceThreadProcessingData_pet,
            release_code);

    return returnCode;
}

/**
 * Release the thread that requested the trace.
 *
 * @param env the JNI environment reference provided by the JVM.
 * @param jobj the object instance this method was invoked against.
 * @param threadData_p pointer to the thread processing data.
 *
 * @return release return code.
 */
JNIEXPORT jint JNICALL
ntv_traceWritten(JNIEnv* env, jobject jobj, jlong threadData_p) {
    TraceThreadProcessingData* traceThreadProcessingData_p = (TraceThreadProcessingData*) threadData_p;
    iea_release_code release_code;
    jint returnCode = 0;

    memset(release_code, 0, sizeof(iea_release_code));
    iea4rls(&returnCode,
            IEA_UNAUTHORIZED,
            traceThreadProcessingData_p->traceThreadProcessingData_requestor_pet,
            release_code);

    memset(traceThreadProcessingData_p->traceThreadProcessingData_requestor_pet,
           0,
           sizeof(traceThreadProcessingData_p->traceThreadProcessingData_requestor_pet));

    return returnCode;
}

/**
 * Set the aggregate trace level as determined in Java.
 *
 * @param env the JNI environment reference provided by the JVM.
 * @param jobj the object instance this method was invoked against.
 * @param traceLevel native trace level.
 */
JNIEXPORT void JNICALL
ntv_setTraceLevel(JNIEnv* env, jobject jobj, jint traceLevel) {
    RAS_aggregate_trace_level = (unsigned char) traceLevel;
}

/**
 * Return an array of Java TraceDefinition objects.
 *
 * @param env the JNI environment reference provided by the JVM.
 * @param obj the object instance this method was invoked against.
 *
 * @return Object array of TraceDefinition instances
 */
JNIEXPORT jobjectArray JNICALL
ntv_getNativeTraceComponents(JNIEnv* env, jobject obj) {
#pragma convert("iso8859-1")
    const char *clazzName = "com/ibm/ws/zos/logging/internal/NativeTraceHandler$NativeTraceComponentDefinition";
    const char *methodName = "<init>";
    const char *signature = "(ILjava/lang/String;Ljava/lang/String;)V";
#pragma convert(pop)

    jclass clazz = (*env)->FindClass(env, clazzName);
    if (clazz == NULL) {
        return NULL;
    }

    jmethodID constructor = (*env)->GetMethodID(env, clazz, methodName, signature);
    if (constructor == NULL) {
        return NULL;
    }

    jobjectArray traceDefs = (*env)->NewObjectArray(env, NUM_DEFINITIONS, clazz, NULL);
    if (traceDefs == NULL) {
        return NULL;
    }

    for (int i = 0; i < NUM_DEFINITIONS; i++) {
        jint id = trace_definitions[i].moduleId;
        jstring name = createJavaString(env, trace_definitions[i].name);
        if (name == NULL) {
            return NULL;
        }

        jstring groups = createJavaString(env, trace_definitions[i].groups);
        if (groups == NULL) {
            return NULL;
        }

        jobject traceDef = (*env)->NewObject(env, clazz, constructor, id, name, groups);
        if (traceDef == NULL) {
            return NULL;
        }

        (*env)->SetObjectArrayElement(env, traceDefs, i, traceDef);
        if ((*env)->ExceptionCheck(env)) {
            return NULL;
        }
    }

    return traceDefs;
}

/**
 * Set the unittest traceLevel and logFileName.
 *
 * @param env the JNI environment reference provided by the JVM.
 * @param clazz the class this method was invoked against.
 * @param traceLevel native trace level.
 * @param jlogFileName cannonical name of the unittest native log file.
 *
 * @return jint 0 if successful > 0 if not successful
 */
JNIEXPORT jint JNICALL
ntv_initTraceForUnitTest(JNIEnv* env, jclass clazz, jint traceLevel, jstring jlogFileName) {
    jint rc = 0;
    char* logFileName;

    // Set the trace level for the guards
    RAS_aggregate_trace_level = (unsigned char) traceLevel;

    //this is used to determine if we are in the unittest environment and what level
    //of trace is enabled in that environment.
    RAS_unittest_trace_level = traceLevel;
    RAS_aggregate_trace_level = (unsigned char) traceLevel;
    debug("Set RAS_unittest_trace_level=%d\n", RAS_unittest_trace_level);

    const char* utfLogFileName = (*env)->GetStringUTFChars(env, jlogFileName, NULL);
    if (utfLogFileName == NULL) {
        return 1;
    }

    logFileName = alloca(strlen(utfLogFileName) + 1);
    strcpy(logFileName, utfLogFileName);
    __atoe(logFileName);
    (*env)->ReleaseStringUTFChars(env, jlogFileName, utfLogFileName);

    //global used to store the log file name
    RAS_unittest_trace_filename = logFileName;
    debug("Set RAS_unittest_trace_filename=%s\n", RAS_unittest_trace_filename);

    if (RAS_unittest_trace_level > 0) {
        RAS_unittest_trace_filedesc = openLogFile();
        if (RAS_unittest_trace_filedesc <= 0) {
            return 2;
        }

        //create the name token so the metal environment will see the configured trace level
        rc = createRasAggregateTraceLevelNameToken();
        if (rc > 0) {
            return 3;
        }

        //create name tokens for RAS_unittest_trace_level and RAS_unittest_trace_filename
        rc = createRasUnitTestTraceLevelNameToken();
        if (rc > 0) {
            return 4;
        }
    }

    return rc;
}

/**
 * Reset the unittest trace environment.
 *
 * @param env the JNI environment reference provided by the JVM.
 * @param clazz the class this method was invoked against.
 *
 * @return jint 0 if successful 1 if not
 */
JNIEXPORT jint JNICALL
ntv_resetTraceForUnitTest(JNIEnv* env, jclass clazz) {

    jint rc = 0;

    if (RAS_unittest_trace_level > 0) {

        if (RAS_unittest_trace_filedesc > 0)
            closeLogFile(RAS_unittest_trace_filedesc);

        deleteRasAggregateTraceLevelNameToken();
        deleteRasUnitTestTraceLevelNameToken();
    }

    RAS_aggregate_trace_level = (unsigned char) 3;
    RAS_unittest_trace_level  =  -1;

    return rc;
}

/**
 * Get the JNI environment associated with the current thread of execution.
 *
 * @return the current thread's JNI environment or NULL if the thread is not
 *         attached to the JVM.
 */
static JNIEnv*
getJavaEnv() {
    JNIEnv* env = NULL;
    int returnCode = 0;

    // No cached reference, try to get from the JVM
    if (javaVM) {
        returnCode = (*javaVM)->GetEnv(javaVM, (void**) &env, JNI_VERSION_1_6);
        if (returnCode != JNI_OK) {
            debug("Unable to get the JNI environment: rc=%d\n", returnCode);
            return NULL;
        }
    }

    // Liberty is embedded in a number of environments.  One of which is a CICS environment in which they manage their
    // own tasks and they own the pthread create and destroy.  They attach/detach these tasks over and over to the JVM.
    // The consequence is that when the related Java thread ends, the native pthread exit/destroy functions are
    // **NOT** driven.  So, any cached info using pthread services survive multiple Java Threads using the native
    // tasks.
    //
    // We can't use pthread_setspecific and pthreads_getspecific for the above reason.

    return env;
}

/**
 * Create a Java string from a C string in EBCDIC.
 *
 * @param env the JNI environment for this thread
 * @param ebcdicString the C string to render in Java
 *
 * @return the Java string version of ebcdicString
 */
static jstring
createJavaString(JNIEnv* env, const char* ebcdicString) {
    char* asciiString = alloca(strlen(ebcdicString) + 1);
    strcpy(asciiString, ebcdicString);
    __etoa(asciiString);

    return (*env)->NewStringUTF(env, asciiString);
}

/**
 * Get a store clock style time stamp.
 *
 * return the value set by the STCK instruction
 */
static unsigned long long
getStoreClockTimeStamp() {
    unsigned long long timestamp = 0;
    __stck(&timestamp);
    return timestamp;
}

/**
 * Write debug message to the standard error stream if the
 * module is compiled with LIBERTY_DEBUG set.
 *
 * @param format the @c printf format string
 */
static void
debug(const char* format, ...) {
#ifdef LIBERTY_DEBUG
    va_list arg_list;

    va_start(arg_list, format);
    vfprintf(stderr, format, arg_list);
    va_end(arg_list);
#endif
}
