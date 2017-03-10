/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2012, 2017
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
#include <pthread.h>
#include <stdarg.h>
#include <stdlib.h>
#include <unistd.h>   // for getpid
#include <sys/stat.h>


#include "include/ras_tracing.h"
#include "include/server_jni_method_manager.h"
#include "include/server_native_service_tracker.h"
#include "include/util_jni.h"
#include "include/gen/cvt.h"
#include "include/gen/ihapsa.h"
#include "include/gen/ihastcb.h"
#include "include/gen/ikjtcb.h"

/**
 * RAS related constants.
 */
#define RAS_MODULE_CONST  RAS_MODULE_SERVER_UTILS_JNI

#define TP_GET_STCK_ENTRY                                                 1
#define TP_GET_STCK_EXIT                                                  2
#define TP_GET_TASK_ID_ENTRY                                              3
#define TP_GET_TASK_ID_EXIT                                               4
#define TP_GET_PID_ENTRY                                                  5
#define TP_GET_PID_EXIT                                                   6
#define TP_GET_SMF_DATA_ENTRY                                             7
#define TP_GET_SMF_DATA_EXIT                                              8
#define TP_GET_TIMEUSED_DATA_EXIT                                         9
#define TP_GET_UMASK_ENTRY                                               10
#define TP_GET_UMASK_EXIT                                                11

/**
 * Gets the current STCK value.
 *
 * @return The current STCK value.
 */
#pragma export(ntv_getStck)
JNIEXPORT jlong JNICALL
ntv_getStck(JNIEnv* env, jclass jobj);

/**
 * Gets the current task ID using pthread_self
 *
 * @return The current task ID.
 */
#pragma export(ntv_getTaskId)
JNIEXPORT jlong JNICALL
ntv_getTaskId(JNIEnv* env, jclass jobj);

/**
 * Gets the servers process ID using getpid()
 *
 * @return The server process id.
 */
#pragma export(ntv_getPid)
JNIEXPORT jint JNICALL
ntv_getPid(JNIEnv* env, jclass jobj);

/**
 * Get SMF data.
 *
 * @return The SMF data.
 */
#pragma export(ntv_getSmfData)
JNIEXPORT jbyteArray JNICALL
ntv_getSmfData(JNIEnv* env, jclass jobj);

/**
 * Get TIMEUSED data
 *
 * @return The TIMEUSED data
 */
#pragma export (ntv_getTimeusedData)
JNIEXPORT jbyteArray JNICALL
ntv_getTimeusedData(JNIEnv* env, jobject this);

/**
 * Create a @c DirectByteBuffer that maps storage at the requested address.
 */
#pragma export(ntv_mapDirectByteBuffer)
JNIEXPORT jobject JNICALL
ntv_mapDirectByteBuffer(JNIEnv* env, jobject obj, jlong address, jint size);

/**
 * Gets the servers process UMASK.
 *
 * @return The server process UMASK.
 */
#pragma export(ntv_getUmask)
JNIEXPORT jint JNICALL
ntv_getUmask(JNIEnv* env, jclass jobj);

/**
 * JNI native method signatures for NativeUtilsImpl.
 */
#pragma convert("ISO8859-1")
static JNINativeMethod nativeUtilsMethods[] = {
    { "ntv_getStck",
      "()J",
      (void *) ntv_getStck },
    { "ntv_mapDirectByteBuffer",
      "(JI)Ljava/nio/ByteBuffer;",
      (void *) ntv_mapDirectByteBuffer },
    { "ntv_getTaskId",
      "()J",
      (void *) ntv_getTaskId },
    { "ntv_getPid",
      "()I",
      (void *) ntv_getPid },
    { "ntv_getSmfData",
      "()[B",
      (void *) ntv_getSmfData },
    { "ntv_getTimeusedData",
      "()[B",
      (void*) ntv_getTimeusedData },
    { "ntv_getUmask",
      "()I",
      (void*) ntv_getUmask }
};
#pragma convert(pop)

/**
 * NativeMethodDescriptor for NativeUtilsImpl.
 */
#pragma export(zJNI_com_ibm_ws_zos_core_utils_internal_NativeUtilsImpl)
NativeMethodDescriptor zJNI_com_ibm_ws_zos_core_utils_internal_NativeUtilsImpl = {
    .registrationFunction = NULL,
    .deregistrationFunction = NULL,
    .nativeMethodCount = sizeof(nativeUtilsMethods) / sizeof(nativeUtilsMethods[0]),
    .nativeMethods = nativeUtilsMethods
};

//-----------------------------------------------------------------------------
// Gets the current STCK value.
//-----------------------------------------------------------------------------
JNIEXPORT jlong JNICALL
ntv_getStck(JNIEnv* env, jclass jobj) {
    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(TP_GET_STCK_ENTRY),
                    "ntv_getStck Entry",
                    TRACE_DATA_END_PARMS);
    }

    unsigned long long currentStck;
    __stck(&currentStck);

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(TP_GET_STCK_EXIT),
                    "ntv_getStck Exit",
                    TRACE_DATA_RAWDATA(sizeof(long long), &currentStck, "currentStck"),
                    TRACE_DATA_END_PARMS);
    }

    return currentStck;
}

//-----------------------------------------------------------------------------
// Gets the current task ID using pthread_self
//-----------------------------------------------------------------------------
JNIEXPORT jlong JNICALL
ntv_getTaskId(JNIEnv* env, jclass jobj) {
    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(TP_GET_TASK_ID_ENTRY),
                    "ntv_getTaskId Entry",
                    TRACE_DATA_END_PARMS);
    }

    jlong returnTaskId;
    pthread_t currentTaskId = pthread_self();
    memcpy(&returnTaskId, &currentTaskId, sizeof(returnTaskId));

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(TP_GET_TASK_ID_EXIT),
                    "ntv_getTaskId Exit",
                    TRACE_DATA_RAWDATA(sizeof(currentTaskId), &currentTaskId, "current task id"),
                    TRACE_DATA_RAWDATA(sizeof(returnTaskId), &returnTaskId, "return task id"),
                    TRACE_DATA_END_PARMS);
    }

    return returnTaskId;
}

//-----------------------------------------------------------------------------
// Gets the servers process ID using getpid()
//-----------------------------------------------------------------------------
JNIEXPORT jint JNICALL
ntv_getPid(JNIEnv* env, jclass jobj) {
    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(TP_GET_PID_ENTRY),
                    "ntv_getPid Entry",
                    TRACE_DATA_END_PARMS);
    }

    pid_t pid = getpid();

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(TP_GET_PID_EXIT),
                    "ntv_getPid Exit",
                    TRACE_DATA_RAWDATA(sizeof(pid), &pid, "pid"),
                    TRACE_DATA_END_PARMS);
    }

    return (jint) pid;
}

//-----------------------------------------------------------------------------
// Gets SMF data
//-----------------------------------------------------------------------------
JNIEXPORT jbyteArray JNICALL
ntv_getSmfData(JNIEnv* env, jclass jobj) {
    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(TP_GET_SMF_DATA_ENTRY),
                    "ntv_getSmfData Entry",
                    TRACE_DATA_END_PARMS);
    }

    jbyteArray smfData = NULL;
    psa*  psa_p  = NULL;
    tcb* tcb_p = psa_p->psatold;
    stcb* stcb_p = (stcb*)(((tcb*)tcb_p)->tcbstcb);
    cvt*  cvt_p  = (cvt* __ptr32) psa_p->flccvt;
    cvtxtnt2 * cvtext2_p = (cvtxtnt2 * __ptr32) cvt_p->cvtext2a;
    pthread_t currentTaskId = pthread_self();
    JNI_try {
        JNI_NewByteArray(smfData, env, sizeof(psa_p->psatold) + sizeof(stcb_p->stcbttkn) + sizeof(currentTaskId) + sizeof(cvtext2_p->cvtldto));
        JNI_SetByteArrayRegion(env,
                               smfData,
                               0,
                               sizeof(psa_p->psatold),
                               (jbyte*) &(psa_p->psatold));
        JNI_SetByteArrayRegion(env,
                               smfData,
                               sizeof(psa_p->psatold),
                               sizeof(stcb_p->stcbttkn),
                               (jbyte*) &(stcb_p->stcbttkn));
        JNI_SetByteArrayRegion(env,
                               smfData,
                               sizeof(psa_p->psatold) + sizeof(stcb_p->stcbttkn),
                               sizeof(currentTaskId),
                               (jbyte*) &(currentTaskId));
        JNI_SetByteArrayRegion(env,
                               smfData,
                               sizeof(psa_p->psatold) + sizeof(stcb_p->stcbttkn) + sizeof(currentTaskId),
                               sizeof(cvtext2_p->cvtldto),
                               (jbyte*) &(cvtext2_p->cvtldto));
    }
    JNI_catch(env);
    // JNI_reThrowAndReturn(env);


    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(TP_GET_SMF_DATA_EXIT),
                    "ntv_getSmfData Exit",
                    TRACE_DATA_RAWDATA(sizeof(psa_p->psatold), &(psa_p->psatold), "psatold"),
                    TRACE_DATA_RAWDATA(sizeof(stcb_p->stcbttkn), &(stcb_p->stcbttkn), "ttoken"),
                    TRACE_DATA_RAWDATA(sizeof(pthread_t), &currentTaskId, "pthread_self"),
                    TRACE_DATA_RAWDATA(sizeof(cvtext2_p->cvtldto), &(cvtext2_p->cvtldto), "cvtldto"),
                    TRACE_DATA_END_PARMS);
    }

    return smfData;
}

//---------------------------------------------------------------------
//---------------------------------------------------------------------
JNIEXPORT jbyteArray JNICALL
ntv_getTimeusedData(JNIEnv* env, jobject jthis) {
    jbyteArray data = NULL;
    TimeusedData timeusedData;
    memset(&timeusedData, 0, sizeof(timeusedData));
    const server_unauthorized_function_stubs* stubs_p = getServerUnauthorizedFunctionStubs();
    if (stubs_p) {
        int rc = stubs_p->getTimeusedData(&timeusedData);
        if (rc == 0) {
            JNI_try {
                JNI_NewByteArray(data, env, sizeof(timeusedData));
                JNI_SetByteArrayRegion(env, data, 0, sizeof(timeusedData), (jbyte*) &(timeusedData));
            }
            JNI_catch(env);
            // JNI_reThrowAndReturn(env);
        }
    }
    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(TP_GET_TIMEUSED_DATA_EXIT),
                    "ntv_getTimeusedData Exit",
                    TRACE_DATA_RAWDATA(sizeof(timeusedData), &timeusedData, "timeused Data"),
                    TRACE_DATA_END_PARMS);
    }
    return data;
}

//-----------------------------------------------------------------------------
// Gets the servers process UMASK.
//
// Structure mode_t octal values:
//
// S_ISUID  0x0800 (04000)
// S_ISGID  0x0400 (02000)
// S_ISVTX  0x0200 (01000)
//
// S_IRUSR  0x0100 (00400)
// S_IWUSR  0x0080 (00200)
// S_IXUSR  0x0040 (00100)
// S_IRWXU  0x01C0 (00700)

// S_IRGRP  0x0020 (00040)
// S_IWGRP  0x0010 (00020)
// S_IXGRP  0x0008 (00010)
// S_IRWXG  0x0038 (00070)

// S_IROTH  0x0004 (00004)
// S_IWOTH  0x0002 (00002)
// S_IXOTH  0x0001 (00001)
// S_IRWXO  0x0007 (00007)
//-----------------------------------------------------------------------------
JNIEXPORT jint JNICALL
ntv_getUmask(JNIEnv* env, jclass jobj) {
    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(TP_GET_UMASK_ENTRY),
                    "ntv_getUmask Entry",
                    TRACE_DATA_END_PARMS);
    }

    // Get the current umask by setting it to zero temporarily.
    mode_t currentUmask = umask((mode_t) 0);

    // Set the umask value we retrieved back.
    mode_t tmpUmask = umask(currentUmask);

    // Grab mode_t's 12 right most bits (4 octets : Special, UserMask, GroupMask, OtherMask) and convert it to
    // an integer value that we can return.
    // Use Decimal value 4095 (Hex: 0xFFF, Octal: 07777, Binary: 111 111 111 111) as base.
    int umask = currentUmask & 4095;

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(TP_GET_UMASK_EXIT),
                    "ntv_getUmask Exit",
                    TRACE_DATA_RAWDATA(sizeof(mode_t), &currentUmask, "Current UMASK"),
                    TRACE_DATA_RAWDATA(sizeof(mode_t), &tmpUmask, "Temporary UMASK"),
                    TRACE_DATA_INT(umask, "Return UMASK"),
                    TRACE_DATA_END_PARMS);
    }

    return (jint) umask;
}


/**
 * Create a @c DirectByteBuffer that maps storage at the requested address.
 *
 * @param  env the JNI environment reference provided by the JVM.
 * @param  obj the object instance this method was invoked against.
 * @param  address the pointer to the storage to map.
 * @param  size the size of the area to map.
 *
 * @return a @c DirectByteBuffer that maps the requested area or null if an
 *     exception is pending in the JVM.
 */
JNIEXPORT jobject JNICALL
ntv_mapDirectByteBuffer(JNIEnv* env, jobject obj, jlong address, jint size) {
    // WARNING:  Do not add a trace point to this method.  It is called BY the code
    //           that WRITES native traces, creating an infinite loop.  If you want to
    //           trace the parameters to this function, use the trace point in this
    //           function's Java caller, NativeUtilsImpl.mapDirectByteBuffer(...).

    return (*env)->NewDirectByteBuffer(env, (void*) address, size);
}
