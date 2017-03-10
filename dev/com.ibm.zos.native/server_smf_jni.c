/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2014
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */

/**
 * @file
 *
 * SMF Services in the server process.
 */
#include <jni.h>
#include <stdarg.h>
#include <stdlib.h>
#include <unistd.h>
#include <errno.h>

#include "include/ras_tracing.h"
#include "include/server_jni_method_manager.h"
#include "include/util_jni.h"

/**
 * RAS related constants.
 */
#define RAS_MODULE_CONST  RAS_MODULE_SERVER_SMF_JNI

#define TP_WRITE_SMFT120S11_ENTRY                                                 1
#define TP_WRITE_SMFT120S11_EXIT                                                  2
#define TP_WRITE_SMFT120S11_DATA                                                  3
#define TP_WRITE_SMFT120S12_ENTRY                                                 4
#define TP_WRITE_SMFT120S12_EXIT                                                  5
#define TP_WRITE_SMFT120S12_DATA                                                  6


/**
 * Gets Write an SMF 120-11 Record
 *
 * @param data the SMF record
 * @return The SMF Return COde
 */
#pragma export(ntv_SmfRecordT120S11Write)
JNIEXPORT jint JNICALL
ntv_SmfRecordT120S11Write(JNIEnv* env, jclass jobj,jbyteArray data);

/**
 * Gets Write an SMF 120-12 Record
 *
 * @param data the SMF record
 * @return The SMF Return COde
 */
#pragma export(ntv_SmfRecordT120S12Write)
JNIEXPORT jint JNICALL
ntv_SmfRecordT120S12Write(JNIEnv* env, jclass jobj,jbyteArray data);

/**
 * JNI native method signatures for NativeUtilsImpl.
 */
#pragma convert("ISO8859-1")
static JNINativeMethod nativeSmfMethods[] = {
    { "ntv_SmfRecordT120S11Write",
      "([B)I",
      (int *) ntv_SmfRecordT120S11Write },
      { "ntv_SmfRecordT120S12Write",
        "([B)I",
        (int *) ntv_SmfRecordT120S12Write }
};
#pragma convert(pop)

/**
 * NativeMethodDescriptor for NativeUtilsImpl.
 */
#pragma export(zJNI_com_ibm_ws_zos_core_utils_internal_SmfImpl)
NativeMethodDescriptor zJNI_com_ibm_ws_zos_core_utils_internal_SmfImpl = {
    .registrationFunction = NULL,
    .deregistrationFunction = NULL,
    .nativeMethodCount = sizeof(nativeSmfMethods) / sizeof(nativeSmfMethods[0]),
    .nativeMethods = nativeSmfMethods
};

//-----------------------------------------------------------------------------
// Write an SMF 120-11 Record
//-----------------------------------------------------------------------------
JNIEXPORT jint JNICALL
ntv_SmfRecordT120S11Write(JNIEnv* env, jclass jobj, jbyteArray data) {
    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(TP_WRITE_SMFT120S11_ENTRY),
                    "ntv_SmfRecordT120S11Write Entry",
                    TRACE_DATA_END_PARMS);
    }


    int rc = 0;
    int err = 0;
    int errJr = 0;
    jbyte* smfData          = NULL;
    jsize  smfDataLength    = 0;

    const int type = 120;
    const int subtype = 11;

    JNI_try {

        JNI_GetByteArrayElements(smfData,env, data,"SMF data is null", NULL);
        JNI_GetArrayLength(smfDataLength,env, data,"SMF data is null", 0);

        if (TraceActive(trc_level_detailed)) {
            TraceRecord(trc_level_detailed,
                        TP(TP_WRITE_SMFT120S11_DATA),
                        "ntv_SmfRecordT120S11Write Data",
                        TRACE_DATA_RAWDATA(smfDataLength,smfData,"SMF Data"),
                        TRACE_DATA_END_PARMS);
        }


        rc = __smf_record(type,subtype,smfDataLength,(char *)smfData);
        if (rc!=0) {
            // Should really return these up to Java..rc is just -1 which isn't that helpful
            err = errno;
            errJr = __errno2();
        }

    }
    JNI_catch(env);

    JNI_ReleaseByteArrayElements(env, data,  smfData, NULL);

    JNI_reThrowAndReturn(env);

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(TP_WRITE_SMFT120S11_EXIT),
                    "ntv_SmfRecordT120S11Write Exit",
                    TRACE_DATA_INT(rc, "return code"),
                    TRACE_DATA_INT(err,"errno"),
                    TRACE_DATA_INT(errJr, "errno2"),
                    TRACE_DATA_END_PARMS);
    }

    return rc;
}

//-----------------------------------------------------------------------------
// Write an SMF 120-12 Record
//-----------------------------------------------------------------------------
JNIEXPORT jint JNICALL
ntv_SmfRecordT120S12Write(JNIEnv* env, jclass jobj, jbyteArray data) {
    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(TP_WRITE_SMFT120S12_ENTRY),
                    "ntv_SmfRecordT120S12Write Entry",
                    TRACE_DATA_END_PARMS);
    }


    int rc = 0;
    int err = 0;
    int errJr = 0;
    jbyte* smfData          = NULL;
    jsize  smfDataLength    = 0;

    const int type = 120;
    const int subtype = 12;

    JNI_try {

        JNI_GetByteArrayElements(smfData,env, data,"SMF data is null", NULL);
        JNI_GetArrayLength(smfDataLength,env, data,"SMF data is null", 0);

        if (TraceActive(trc_level_detailed)) {
            TraceRecord(trc_level_detailed,
                        TP(TP_WRITE_SMFT120S12_DATA),
                        "ntv_SmfRecordT120S12Write Data",
                        TRACE_DATA_RAWDATA(smfDataLength,smfData,"SMF Data"),
                        TRACE_DATA_END_PARMS);
        }


        rc = __smf_record(type,subtype,smfDataLength,(char *)smfData);
        if (rc!=0) {
            // Should really return these up to Java..rc is just -1 which isn't that helpful
            err = errno;
            errJr = __errno2();
        }

    }
    JNI_catch(env);

    JNI_ReleaseByteArrayElements(env, data,  smfData, NULL);

    JNI_reThrowAndReturn(env);

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(TP_WRITE_SMFT120S12_EXIT),
                    "ntv_SmfRecordT120S12Write Exit",
                    TRACE_DATA_INT(rc, "return code"),
                    TRACE_DATA_INT(err,"errno"),
                    TRACE_DATA_INT(errJr, "errno2"),
                    TRACE_DATA_END_PARMS);
    }

    return rc;
}
