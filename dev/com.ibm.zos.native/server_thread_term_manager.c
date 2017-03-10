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
 * JNI code in support of the thread termination manager.
 */
#include <jni.h>
#include <pthread.h>
#include <stdio.h>
#include <stdlib.h>

#include "include/common_defines.h"
#include "include/ras_tracing.h"
#include "include/server_jni_method_manager.h"
#include "include/server_native_service_tracker.h"

#define RAS_MODULE_CONST  RAS_MODULE_SERVER_THREAD_TERM_MGR

#define TP_REGISTRATION_ENTRY                  1
#define TP_REGISTRATION_EXIT                   2
#define TP_DEREGISTRATION_ENTRY                3
#define TP_DEREGISTRATION_EXIT                 4
#define TP_REGISTRATION_OUT_OF_MEMORY          5
#define TP_REG_THRED_FOR_TERM_OUT_OF_MEMORY    6

//-----------------------------------------------------------------------------
// Macro definitions.
//-----------------------------------------------------------------------------
#define NO_RETURN
#define CHECK_JAVA_EXCEPTION(env, ret_stmt) \
    if ((*env)->ExceptionCheck(env) == JNI_TRUE) { \
        (*env)->ExceptionDescribe(env); \
        ret_stmt; \
    }

//---------------------------------------------------------------------
// Data structures
//---------------------------------------------------------------------
/** Structure describing a terminated thread. */
typedef struct terminated_thread {
    jobject threadRef;
    struct terminated_thread* next_p;
} terminated_thread;

//---------------------------------------------------------------------
// Registration and deregistration callback methods.
//---------------------------------------------------------------------

/**
 * Registration callback used to resolve java object references.
 *
 * @param env The JNI environment for the calling thread.
 * @param clazz The class for which deregistration is taking place.
 * @param extraInfo The context information from the caller.
 *
 * @return JNI_OK on success; JNI_ERR or on error
 */
int threadTermManagerJavaEnvironmentRegistration(JNIEnv* env, jclass clazz, jobjectArray extraInfo);

/**
 * Deregistration callback used java object reference cleanup.
 *
 * @param env The calling thread's JNI environment.
 * @param clazz The class for which deregistration is taking place.
 * @param extraInfo The context provided to the registration function.
 *
 * @return JNI_OK
 */
int threadTermManagerJavaEnvironmentDeregistration(JNIEnv* env, jclass clazz, jobjectArray extraInfo);

//---------------------------------------------------------------------
// JNI function declaration and export
//---------------------------------------------------------------------
#pragma export(ntv_activateThreadTermMonitor)
JNIEXPORT jint JNICALL
ntv_activateThreadTermMonitor(JNIEnv* env, jobject mgr);

#pragma export(ntv_deactivateThreadTermMonitor)
JNIEXPORT void JNICALL
ntv_deactivateThreadTermMonitor(JNIEnv* env, jobject mgr);

#pragma export(ntv_registerThreadForTermination)
JNIEXPORT int JNICALL
ntv_registerThreadForTermination(JNIEnv* env, jobject mgr, jobject thread);

//---------------------------------------------------------------------
// JNI native method structure for the CommandProcessor methods
//---------------------------------------------------------------------
#pragma convert("ISO8859-1")
static const JNINativeMethod terminationManagerMethods[] = {
    { "ntv_activateThreadTermMonitor",
      "()I",
      (void *) ntv_activateThreadTermMonitor },
    { "ntv_deactivateThreadTermMonitor",
      "()V",
      (void *) ntv_deactivateThreadTermMonitor },
    { "ntv_registerThreadForTermination",
      "(Ljava/lang/Thread;)I",
      (void *) ntv_registerThreadForTermination },
};
#pragma convert(pop)

//---------------------------------------------------------------------
// NativeMethodDescriptor for the thread termination manager
//---------------------------------------------------------------------
#pragma export(zJNI_com_ibm_ws_zos_thread_term_internal_TerminationManagerImpl)
NativeMethodDescriptor zJNI_com_ibm_ws_zos_thread_term_internal_TerminationManagerImpl = {
    .registrationFunction = threadTermManagerJavaEnvironmentRegistration,
    .deregistrationFunction = threadTermManagerJavaEnvironmentDeregistration,
    .nativeMethodCount = sizeof(terminationManagerMethods) / sizeof(terminationManagerMethods[0]),
    .nativeMethods = terminationManagerMethods
};

//---------------------------------------------------------------------
// Module scoped data
//---------------------------------------------------------------------

/** The condition variable for the pthread wait calls. */
static pthread_cond_t threadTerminationCond = {0x0};

/** The mutex (lock) for thread termination synchronization. */
static pthread_mutex_t threadTerminationMutex = PTHREAD_MUTEX_INITIALIZER;

/** The key for pthread setspecific. */
static pthread_key_t* threadTerminationKey_p = NULL;

/** The thread termination monitor thread. */
static pthread_t threadTerminationMonitorThread;

/** The current running state of the thread termination monitor thread. */
static unsigned char threadTerminationMonitorThreadActive = FALSE;

/** The stack of terminated threads, serialized by threadTerminationLock. */
static terminated_thread* threadTerminationStack = NULL;

/** A global reference to the current thread termination manager instance. */
static jobject threadTerminationManager = NULL;

/** The termination notification routine for the thread termination manager. */
static jmethodID threadTerminationManagerNotificationMethodID = NULL;

/** Status of thread initialization. */
typedef enum {
    INCOMPLETE    = 0, //!< Thread is still initializing.
    SUCCESSFUL    = 1, //!< Thread initialized successfully.
    UNSUCCESSFUL  = 2  //!< Thread did not initialize successfully.
} ThreadInitializationStatus;

/** Parms to pass to the monitor thread run routine. */
typedef struct monitor_thread_run_parms {
    JavaVM* jvm_p; //!< Reference to the JVM for this process.
    pthread_cond_t* threadInitializationCompleteCond_p; //!< Init complete condition, posted when complete.
    pthread_mutex_t* threadInitializationCompleteMutex_p; //!< Init complete mutex, posted when complete.
    ThreadInitializationStatus* status_p; //!< The status of the monitor thread initialization.
} monitor_thread_run_parms;

//---------------------------------------------------------------------
//---------------------------------------------------------------------
/**
 * The main processing loop for the termination monitor thread.  The code pulls
 * terminated thread structures off of a stack, and notifies the JVM of the
 * terminations.
 *
 * @param data_p Initialization parameters passed to this new thread during
 *               initialization.
 */
void* pthread_monitor_thread_run(void* data_p) {
    monitor_thread_run_parms* parms = (monitor_thread_run_parms*) data_p;

#pragma convert("ISO8859-1")
    char* threadName = "z/OS Thread Termination Monitor Thread";
#pragma convert(pop)

    // ----------------------------------------------------------------------
    // Attach ourselves to the JVM.
    // ----------------------------------------------------------------------
    ThreadInitializationStatus status = UNSUCCESSFUL;
    JavaVM* jvm_p = parms->jvm_p;
    JNIEnv* env = NULL;
    JavaVMAttachArgs jthreadArgs = {
        .version = JNI_VERSION_1_4,
        .name = threadName,
        .group = NULL
    };

    if ((*jvm_p)->AttachCurrentThreadAsDaemon(jvm_p, (void**)&env, &jthreadArgs) == JNI_OK) {
        status = SUCCESSFUL;
    }

    // ----------------------------------------------------------------------
    // Notify our parent thread of our initialization status.
    // ----------------------------------------------------------------------
    if (pthread_mutex_lock(parms->threadInitializationCompleteMutex_p) != 0) {
        perror("server_thread_term_manager.pthread_monitor_thread_run pthread_mutex_lock");
        (*jvm_p)->DetachCurrentThread(jvm_p);
        pthread_exit(NULL);
    }

    *(parms->status_p) = status;

    if (pthread_cond_signal(parms->threadInitializationCompleteCond_p) != 0) {
        perror("server_thread_term_manager.pthread_monitor_thread_run pthread_cond_signal");
        (*jvm_p)->DetachCurrentThread(jvm_p);
        pthread_exit(NULL);
    }

    if (pthread_mutex_unlock(parms->threadInitializationCompleteMutex_p) != 0) {
        perror("server_thread_term_manager.pthread_monitor_thread_run pthread_mutex_unlock");
    }

    if (status != SUCCESSFUL) {
        pthread_exit(NULL);
    }

    // ----------------------------------------------------------------------
    // Start processing loop.
    // ----------------------------------------------------------------------
    if (pthread_mutex_lock(&threadTerminationMutex) == 0) {

        // ------------------------------------------------------------------
        // Loop until we're told to stop.
        // ------------------------------------------------------------------
        while (threadTerminationMonitorThreadActive == TRUE) {
            // --------------------------------------------------------------
            // Process any terminated threads.
            // --------------------------------------------------------------
            while (threadTerminationStack != NULL) {
                terminated_thread* curThread_p = threadTerminationStack;
                threadTerminationStack = curThread_p->next_p;

                // ----------------------------------------------------------
                // Notify the thread termination manager, if there is one.
                // ----------------------------------------------------------
                if (threadTerminationManager != NULL) {
                    (*env)->CallVoidMethod(env,
                                           threadTerminationManager,
                                           threadTerminationManagerNotificationMethodID,
                                           curThread_p->threadRef);
                    if ((*env)->ExceptionCheck(env) == JNI_TRUE) {
                        (*env)->ExceptionDescribe(env);
                        (*env)->ExceptionClear(env);
                    }
                }

                // ----------------------------------------------------------
                // Cleanup the storage used to store the terminated thread
                // information.
                // ----------------------------------------------------------
                (*env)->DeleteGlobalRef(env, curThread_p->threadRef);
                free(curThread_p);
            }

            // --------------------------------------------------------------
            // Wait for another terminated thread, or exit signal.
            // --------------------------------------------------------------
            if (pthread_cond_wait(&threadTerminationCond, &threadTerminationMutex) != 0) {
                perror("server_thread_term_manager.pthread_monitor_thread_run pthread_cond_wait");
                threadTerminationMonitorThreadActive = FALSE;
            }
        }

        // ------------------------------------------------------------------
        // Unlock -- we are done.
        // ------------------------------------------------------------------
        if (pthread_mutex_unlock(&threadTerminationMutex) != 0) {
            perror("server_thread_term_manager.pthread_monitor_thread_run pthread_mutex_unlock");
        }
    } else {
        perror("server_thread_term_manager pthread_mutex_lock");
    }

    // ----------------------------------------------------------------------
    // Detach from the JVM
    // ----------------------------------------------------------------------
    (*jvm_p)->DetachCurrentThread(jvm_p);
}

//---------------------------------------------------------------------
//---------------------------------------------------------------------
/**
 * This is the destructor provided on pthread_setspecific.  It is driven
 * when a thread terminates.  We use this routine to place an entry on the
 * terminated threads stack and post the terminated thread monitor thread.
 *
 * @param data_p Information about the terminated thread.
 */
void pthread_terminated_exit_routine(void* data_p) {
    terminated_thread* term_p = (terminated_thread*) data_p;

    // ----------------------------------------------------------------------
    // Get the mutex so that we can see if there's anything for us to do.
    // If we can't get the mutex for some reason, we can't process this
    // terminated thread, so clean it up.
    // ----------------------------------------------------------------------
    if (pthread_mutex_lock(&threadTerminationMutex) != 0) {
        perror("server_thread_term_manager.pthread_terminated_exit_routine pthread_mutex_lock");
        free(term_p);
        return;
    }

    // ----------------------------------------------------------------------
    // If there is a thread termination monitor running, and if there is a
    // current thread termination manager, then tell it about this failed
    // thread by placing the terminated_thread on a stack of terminated
    // threads, and notifying the monitor thread.
    // -----------------------------------------------------------------------
    if ((threadTerminationManager != NULL) && (threadTerminationMonitorThreadActive == TRUE)) {
        term_p->next_p = threadTerminationStack;
        threadTerminationStack = term_p;

        if (pthread_cond_signal(&threadTerminationCond) != 0) {
            perror("server_thread_term_manager.pthread_terminated_exit_routine pthread_cond_signal");
        }
    } else {
        free(term_p);
    }

    // ----------------------------------------------------------------------
    // Release the mutex
    // ----------------------------------------------------------------------
    if (pthread_mutex_unlock(&threadTerminationMutex) != 0) {
        perror("server_thread_term_manager.pthread_terminated_exit_routine pthread_mutex_unlock");
    }
}

//---------------------------------------------------------------------
//---------------------------------------------------------------------
/**
 * This initialization routine is driven when the OSGi service registers its
 * native methods.  The terminated thread monitor thread is started and static
 * variables are initialized.
 *
 * @param env A pointer to the JNI environment for this thread.
 * @param myClazz A pointer to the caller's class object.
 * @param extraInfo Extra information passed by the Java caller, such as the
 *                  classloader for the caller's class.
 *
 * @return JNI_OK on success, all other return values indicate an error.
 */
int threadTermManagerJavaEnvironmentRegistration(JNIEnv* env, jclass myClazz, jobjectArray extraInfo) {

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(TP_REGISTRATION_ENTRY),
                    "server_thread_term_manager javaEnvironmentRegistration entry",
                    TRACE_DATA_PTR(env, "JNIEnv*"),
                    TRACE_DATA_PTR(myClazz, "myClazz"),
                    TRACE_DATA_PTR(extraInfo, "extraInfo"),
                    TRACE_DATA_END_PARMS);
    }

    // -----------------------------------------------------------------------
    // The object array extraInfo passed to this method contains:
    //  1. ClassLoader for the bundle
    // -----------------------------------------------------------------------

    // -----------------------------------------------------------------------
    // Get a reference to the JVM -- the monitor thread will need it to get
    // its java environment.
    // -----------------------------------------------------------------------
    JavaVM* jvm_p = NULL;
    if ((*env)->GetJavaVM(env, &jvm_p) != 0) {
        return JNI_ERR;
    }

    // -----------------------------------------------------------------------
    // Obtain the thread termination mutex so that we can start the thread
    // termination monitor.
    // -----------------------------------------------------------------------
    if (pthread_mutex_lock(&threadTerminationMutex) != 0) {
        perror("server_thread_term_manager.javaEnvironmentRegistration pthread_mutex_lock");
        return JNI_ERR;
    }

    // -----------------------------------------------------------------------
    // If we haven't created a pthread key yet, do it.  Also create the
    // pthread condition because PTHREAD_COND_INITIALIZER is broken.
    // -----------------------------------------------------------------------
    if (threadTerminationKey_p == NULL) {
        if (pthread_cond_init(&threadTerminationCond, NULL) != 0) {
            pthread_mutex_unlock(&threadTerminationMutex);
            return -1;
        }

        pthread_key_t* key_p = malloc(sizeof(pthread_key_t));
        if (key_p != NULL) {
            if (pthread_key_create(key_p, pthread_terminated_exit_routine) == 0) {
                threadTerminationKey_p = key_p;
            } else {
                perror("server_thread_term_manager.javaEnvironmentRegistration pthread_key_create");
                free(key_p);
                pthread_cond_destroy(&threadTerminationCond);
                pthread_mutex_unlock(&threadTerminationMutex);
                return -1;
            }
        } else {
            pthread_cond_destroy(&threadTerminationCond);
            pthread_mutex_unlock(&threadTerminationMutex);

            if (TraceActive(trc_level_exception)) {
                TraceRecord(trc_level_exception,
                            TP(TP_REGISTRATION_OUT_OF_MEMORY),
                            "server_thread_term_manager javaEnvironmentRegistration. Failed to allocate memory for pthread_key_t",
                            TRACE_DATA_END_PARMS);
            }

            return -1;
        }
    }

    // ----------------------------------------------------------------------
    // We are going to create a condition that we can wait on, so that we can
    // be notified when the termination monitor has started successfully.
    // ----------------------------------------------------------------------
    pthread_cond_t threadInitializationCompleteCond = {0x0};
    pthread_mutex_t threadInitializationCompleteMutex = PTHREAD_MUTEX_INITIALIZER;

    if (pthread_cond_init(&threadInitializationCompleteCond, NULL) != 0) {
        perror("server_thread_term_manager.javaEnvironmentRegistration pthread_cond_init");
        pthread_mutex_unlock(&threadTerminationMutex);
        return -1;
    }

    if (pthread_mutex_lock(&threadInitializationCompleteMutex) != 0) {
        perror("server_thread_term_manager.javaEnvironmentRegistration pthread_mutex_lock");
        pthread_mutex_unlock(&threadTerminationMutex);
        return -1;
    }

    // -----------------------------------------------------------------------
    // Start up the thread termination monitor.
    // -----------------------------------------------------------------------
    ThreadInitializationStatus threadStatus = INCOMPLETE;
    monitor_thread_run_parms thread_parms = {
        .jvm_p = jvm_p,
        .threadInitializationCompleteCond_p = &threadInitializationCompleteCond,
        .threadInitializationCompleteMutex_p = &threadInitializationCompleteMutex,
        .status_p = &threadStatus
    };

    threadTerminationMonitorThreadActive = TRUE;
    if (pthread_create(&threadTerminationMonitorThread, NULL, pthread_monitor_thread_run, &thread_parms) != 0) {
        perror("server_thread_term_manager.javaEnvironmentRegistration pthread_create");
        threadTerminationMonitorThreadActive = FALSE;
        pthread_mutex_unlock(&threadInitializationCompleteMutex);
        pthread_mutex_unlock(&threadTerminationMutex);
        return JNI_ERR;
    }

    // -----------------------------------------------------------------------
    // Wait for the termination monitor thread to finish initialization.
    // -----------------------------------------------------------------------
    while (threadStatus == INCOMPLETE) {
        if (pthread_cond_wait(&threadInitializationCompleteCond, &threadInitializationCompleteMutex) != 0) {
            perror("server_thread_term_manager.javaEnvironmentRegistration pthread_cond_wait");
            pthread_mutex_unlock(&threadInitializationCompleteMutex);
            pthread_mutex_unlock(&threadTerminationMutex);
            // TODO: What to do about the thread?
            return JNI_ERR;
        }
    }

    // -----------------------------------------------------------------------
    // Release the thread initialization mutex
    // -----------------------------------------------------------------------
    if (pthread_mutex_unlock(&threadInitializationCompleteMutex) != 0) {
        perror("server_thread_term_manager.javaEnvironmentRegistration pthread_mutex_unlock");
    }

    // -----------------------------------------------------------------------
    // Make sure initialization went OK.
    // -----------------------------------------------------------------------
    if (threadStatus == UNSUCCESSFUL) {
        threadTerminationMonitorThreadActive = FALSE;
        pthread_mutex_unlock(&threadTerminationMutex);
        return JNI_ERR;
    }

    // -----------------------------------------------------------------------
    // Cleanup threadInitializationCompleteCond. If destroy fails, print the
    // error and move on.
    // -----------------------------------------------------------------------
    if (pthread_cond_destroy(&threadInitializationCompleteCond) != 0) {
        perror("server_thread_term_manager.javaEnvironmentRegistration pthread_cond_destroy");
    }

    // -----------------------------------------------------------------------
    // Release the thread termination mutex
    // -----------------------------------------------------------------------
    if (pthread_mutex_unlock(&threadTerminationMutex) != 0) {
        perror("server_thread_term_manager.javaEnvironmentRegistration mutex_unlock");
    }

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(TP_REGISTRATION_EXIT),
                    "server_thread_term_manager javaEnvironmentRegistration exit",
                    TRACE_DATA_END_PARMS);
    }

    return JNI_OK;
}

//---------------------------------------------------------------------
//---------------------------------------------------------------------
/**
 * Cleanup routine is driven when the OSGi bundle is stopped.  The monitor
 * thread is stopped.
 *
 * @param env A pointer to the JNI environment for this thread.
 * @param myClazz A pointer to the caller's class object.
 * @param extraInfo Extra information passed by the Java caller, such as the
 *                  classloader for the caller's class.
 *
 * @return JNI_OK on success, all other return values indicate an error.
 */
int threadTermManagerJavaEnvironmentDeregistration(JNIEnv* env, jclass clazz, jobjectArray extraInfo) {

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(TP_DEREGISTRATION_ENTRY),
                    "server_thread_term_manager javaEnvironmentDeregistration entry",
                    TRACE_DATA_PTR(env, "JNIEnv*"),
                    TRACE_DATA_PTR(clazz, "clazz"),
                    TRACE_DATA_PTR(extraInfo, "extraInfo"),
                    TRACE_DATA_END_PARMS);
    }

    // -----------------------------------------------------------------------
    // Obtain the mutex so that we can stop the thread termination monitor.
    // -----------------------------------------------------------------------
    if (pthread_mutex_lock(&threadTerminationMutex) != 0) {
        perror("server_thread_term_manager.javaEnvironmentDeregistration pthread_mutex_lock");
        return JNI_ERR;
    }

    // -----------------------------------------------------------------------
    // Post the thread termination monitor.  It will see that it should be
    // done now, and will stop.
    // -----------------------------------------------------------------------
    threadTerminationMonitorThreadActive = FALSE;
    if (pthread_cond_signal(&threadTerminationCond) != 0) {
        perror("server_thread_term_manager.javaEnvironmentDeregistration pthread_cond_signal");
    }

    // -----------------------------------------------------------------------
    // Release the mutex.
    // -----------------------------------------------------------------------
    if (pthread_mutex_unlock(&threadTerminationMutex) != 0) {
        perror("server_thread_term_manager.javaEnvironmentDeregistration pthread_mutex_unlock");
    }

    // -----------------------------------------------------------------------
    // Wait for the thread to stop.
    // -----------------------------------------------------------------------
    if (pthread_join(threadTerminationMonitorThread, NULL) != 0) {
        perror("server_thread_term_manager.javaEnvironmentDeregistration pthread_join");
    }

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(TP_DEREGISTRATION_EXIT),
                    "server_thread_term_manager javaEnvironmentDeregistration exit",
                    TRACE_DATA_END_PARMS);
    }

    return JNI_OK;
}

//---------------------------------------------------------------------
//---------------------------------------------------------------------
/**
 * Thread monitor service activation routine, which attaches the monitor thread
 * to the current thread monitor service instance.
 *
 * @param env A pointer to the JNI environment for this thread.
 * @param mgr The termination manager object instance which is being activated.
 *
 * @return 0 on success, nonzero on failure.
 */
JNIEXPORT int JNICALL
ntv_activateThreadTermMonitor(JNIEnv* env, jobject mgr) {

#pragma convert("ISO8859-1")
    char* notificationMethodName = "notifyThreadTerminated";
    char* notificationMethodParms = "(Ljava/lang/Thread;)V";
#pragma convert(pop)

    // -----------------------------------------------------------------------
    // Obtain the mutex.
    // -----------------------------------------------------------------------
    if (pthread_mutex_lock(&threadTerminationMutex) != 0) {
        perror("server_thread_term_manager.ntv_activateThreadTermMonitor pthread_mutex_lock");
        return -1;
    }

    // -----------------------------------------------------------------------
    // Get a global reference to the current thread termination manager.
    // -----------------------------------------------------------------------
    threadTerminationManager = (*env)->NewGlobalRef(env, mgr);
    if ((*env)->ExceptionCheck(env) == JNI_TRUE) {
        (*env)->ExceptionDescribe(env);
        pthread_mutex_unlock(&threadTerminationMutex);
        return -1;
    }

    if (threadTerminationManager == NULL) {
        pthread_mutex_unlock(&threadTerminationMutex);
        return -1;
    }

    // -----------------------------------------------------------------------
    // Get a reference to the notification method on the termination manager.
    // -----------------------------------------------------------------------
    jclass notificationManagerClass = (*env)->GetObjectClass(env, mgr);
    if (notificationManagerClass == NULL) {
        (*env)->DeleteGlobalRef(env, threadTerminationManager);
        threadTerminationManager = NULL;
        pthread_mutex_unlock(&threadTerminationMutex);
        return -1;
    }

    threadTerminationManagerNotificationMethodID =
        (*env)->GetMethodID(env, notificationManagerClass, notificationMethodName, notificationMethodParms);
    if ((*env)->ExceptionCheck(env) == JNI_TRUE) {
        (*env)->ExceptionDescribe(env);
        (*env)->DeleteGlobalRef(env, threadTerminationManager);
        threadTerminationManager = NULL;
        pthread_mutex_unlock(&threadTerminationMutex);
        return -1;
    }

    // -----------------------------------------------------------------------
    // Release the mutex.
    // -----------------------------------------------------------------------
    if (pthread_mutex_unlock(&threadTerminationMutex) != 0) {
        perror("server_thread_term_manager.ntv_activateThreadTermMonitor pthread_mutex_unlock");
    }

    return 0;
}

//---------------------------------------------------------------------
//---------------------------------------------------------------------
/**
 * Thread monitor service deactivation routine.  The thread monitor thread is
 * detached from the thread monitor service instance.
 *
 * @param env A pointer to the JNI environment for this thread.
 * @param mgr The current thread monitor service instance.
 */
JNIEXPORT void JNICALL
ntv_deactivateThreadTermMonitor(JNIEnv* env, jobject mgr) {

    // -----------------------------------------------------------------------
    // Obtain the mutex.
    // -----------------------------------------------------------------------
    if (pthread_mutex_lock(&threadTerminationMutex) != 0) {
        perror("server_thread_term_manager.ntv_deactivateThreadTermMonitor pthread_mutex_lock");
        return;
    }

    // -----------------------------------------------------------------------
    // Delete the global reference to the thread termination manager.
    // -----------------------------------------------------------------------
    (*env)->DeleteGlobalRef(env, threadTerminationManager);
    threadTerminationManager = NULL;

    // -----------------------------------------------------------------------
    // Release the mutex.
    // -----------------------------------------------------------------------
    if (pthread_mutex_unlock(&threadTerminationMutex) != 0) {
        perror("server_therad_term_manager.ntv_deactivateThreadTermMonitor pthread_mutex_unlock");
    }
}

//---------------------------------------------------------------------
//---------------------------------------------------------------------
/**
 * This routine is called when the thread monitor service wants to register the
 * current task for termination.
 *
 * @param env A pointer to the JNI environment for this thread.
 * @param mgr The current thread monitor service object instance.
 * @param thread The java/lang/Thread object representing the current thread.
 *
 * @return 0 on success, nonzero on failure.
 */
JNIEXPORT jint JNICALL
ntv_registerThreadForTermination(JNIEnv* env, jobject mgr, jobject thread) {
    jint rc = 0;

    // -----------------------------------------------------------------------
    // If this thread does not already have a destructor registered, then
    // register one.
    // -----------------------------------------------------------------------
    void* pthread_value = NULL;
    if (pthread_getspecific(*threadTerminationKey_p, &pthread_value) != 0) {
        perror("server_thread_term_manager.ntv_registerThreadForTermination pthread_getspecific");
        return -1;
    }

    if (pthread_value == NULL) {
        terminated_thread* term_p = malloc(sizeof(terminated_thread));
        if (term_p != NULL) {
            term_p->next_p = NULL;
            term_p->threadRef = (*env)->NewGlobalRef(env, thread);
            if ((*env)->ExceptionCheck(env) == JNI_TRUE) {
                (*env)->ExceptionDescribe(env);
                free(term_p);
                term_p = NULL;
                rc = -1;
            } else if (term_p->threadRef == NULL) {
                free(term_p);
                term_p = NULL;
                rc = -1;
            } else {
                // -----------------------------------------------------------
                // Set the data into the pthread specific field.  This
                // registers us to be notified when the thread terminates.
                // -----------------------------------------------------------
                if (pthread_setspecific(*threadTerminationKey_p, term_p) != 0) {
                    perror("server_thread_term_manager.ntv_registerThreadForTermination pthread_setspecific");
                    (*env)->DeleteGlobalRef(env, term_p->threadRef);
                    free(term_p);
                    term_p = NULL;
                    rc = -1;
                }
            }
        } else {
            if (TraceActive(trc_level_exception)) {
                TraceRecord(trc_level_exception,
                            TP(TP_REG_THRED_FOR_TERM_OUT_OF_MEMORY),
                            "server_thread_term_manager ntv_registerThreadForTermination. Failed to allocate memory for terminated_thread",
                            TRACE_DATA_END_PARMS);
            }

            rc = -1;
        }
    }

    return rc;
}
