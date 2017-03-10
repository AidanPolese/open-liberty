/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2012
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */

/**
 * @file
 *
 * Functions related to native diagnostics.
 */
#include <jni.h>
#include <jvmri.h>
#include <stdlib.h>

#include "include/ras_tracing.h"
#include "include/server_jni_method_manager.h"
#include "include/server_native_service_tracker.h"

#define RAS_MODULE_CONST  RAS_MODULE_SERVER_DIANOSTICS_FUNCTIONS

//---------------------------------------------------------------------
// JNI function declaration and export for SvcdumpCommandHandler methods
//---------------------------------------------------------------------
#pragma export(ntv_takeSvcDump)
JNIEXPORT jint JNICALL
ntv_takeSvcDump(JNIEnv* env, jobject obj, jstring id);

//---------------------------------------------------------------------
// JNI native method structure for the SvcdumpCommandHandler methods
//---------------------------------------------------------------------
#pragma convert("iso8859-1")
static JNINativeMethod nativeSvcdumpCommandHandlerMethods[] = {
    { "ntv_takeSvcDump",
      "(Ljava/lang/String;)I",
      (void *) ntv_takeSvcDump }
};
#pragma convert(pop)

//---------------------------------------------------------------------
// NativeMethodDescriptor for the SvcdumpCommandHandler methods
//---------------------------------------------------------------------
#pragma export(zJNI_com_ibm_ws_diagnostics_zos_svcdump_SvcdumpCommandHandler)
NativeMethodDescriptor zJNI_com_ibm_ws_diagnostics_zos_svcdump_SvcdumpCommandHandler = {
   .registrationFunction = NULL,
   .deregistrationFunction = NULL,
   .nativeMethodCount = sizeof(nativeSvcdumpCommandHandlerMethods) / sizeof(nativeSvcdumpCommandHandlerMethods[0]),
   .nativeMethods = nativeSvcdumpCommandHandlerMethods
};

//---------------------------------------------------------------------
// JNI function declaration and export for TdumpCommandHandler methods
//---------------------------------------------------------------------
#pragma export(ntv_takeTDump)
JNIEXPORT jint JNICALL
ntv_takeTDump(JNIEnv* env, jobject obj);

//---------------------------------------------------------------------
// JNI native method structure for the TdumpCommandHandler methods
//---------------------------------------------------------------------
#pragma convert("iso8859-1")
static JNINativeMethod nativeTdumpCommandHandlerMethods[] = {
    { "ntv_takeTDump",
      "()I",
      (void *) ntv_takeTDump }
};
#pragma convert(pop)

//---------------------------------------------------------------------
// NativeMethodDescriptor for the TdumpCommandHandler methods
//---------------------------------------------------------------------
#pragma export(zJNI_com_ibm_ws_diagnostics_zos_tdump_TdumpCommandHandler)
NativeMethodDescriptor zJNI_com_ibm_ws_diagnostics_zos_tdump_TdumpCommandHandler = {
   .registrationFunction = NULL,
   .deregistrationFunction = NULL,
   .nativeMethodCount = sizeof(nativeTdumpCommandHandlerMethods) / sizeof(nativeTdumpCommandHandlerMethods[0]),
   .nativeMethods = nativeTdumpCommandHandlerMethods
};

/**
 * Take a svcdump.
 *
 * @param  env the JNI environment reference provided by the JVM.
 * @param  obj the object instance this method was invoked against.
 * @param  id  a null terminated id of the requester. Only the first 56 bytes will end up in the dump title.
 *
 * @return a return code. 0 success, < 0 internal error, > 0 return code returned by sdumpx.
 */
JNIEXPORT jint JNICALL
ntv_takeSvcDump(JNIEnv* env, jobject obj, jstring id) {

    const server_authorized_function_stubs* auth_stubs_p = getServerAuthorizedFunctionStubs();
    int invokeReturnCode = 0;
    int returnCode = 0;
    char* dumpId = NULL; // allocated with alloca

    if (auth_stubs_p) {
        SvcDumpParms svcDumpParms = {
            .outRC = &returnCode,
        };

        // Convert module path to EBCDIC
        const char* utfDumpId = (*env)->GetStringUTFChars(env, id, NULL);
        if (utfDumpId != NULL) {
            dumpId = alloca(strlen(utfDumpId) + 1);
            strcpy(dumpId, utfDumpId);
            __atoe(dumpId);
            (*env)->ReleaseStringUTFChars(env, id, utfDumpId);
        } else {
            dumpId = "SVCDUMP";
        }
        svcDumpParms.id = dumpId;
        svcDumpParms.idLength = strlen(dumpId);
        invokeReturnCode = auth_stubs_p->takeSvcDumpAuthorizedPc(&svcDumpParms);
        if (invokeReturnCode) {
            returnCode = SVCDUMP_INVOKE_PC_ERROR;
        }
    } else {
        returnCode = SVCDUMP_NO_AUTH_STUBS;
    }

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                TP(1),
                TRACE_DESC_FUNCTION_EXIT,
                TRACE_DATA_INT(invokeReturnCode, "invoke return code"),
                TRACE_DATA_INT(returnCode, "return code"),
                TRACE_DATA_END_PARMS);
    }

    return returnCode;
}

/**
 * Take a transaction dump.
 *
 * @param  env the JNI environment reference provided by the JVM.
 * @param  obj the object instance this method was invoked against.
 *
 * @return a return code. 0 success, < 0 return code returned by InitiateSystemDump, > 0 internal error.
 */
JNIEXPORT jint JNICALL
ntv_takeTDump(JNIEnv* env, jobject obj) {

    JavaVM* javaVM = NULL;
    DgRasInterface*  jvmri_intf = NULL;
    int returnCode = 0;

    if ((*env)->GetJavaVM(env, &javaVM) == JNI_OK) {
        if ((*javaVM)->GetEnv(javaVM, (void **) &jvmri_intf, JVMRAS_VERSION_1_3) == JNI_OK) {
            returnCode = jvmri_intf->InitiateSystemDump(env);
        } else {
            returnCode = 100;
        }
    } else {
        returnCode = 200;
    }

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                TP(2),
                TRACE_DESC_FUNCTION_EXIT,
                TRACE_DATA_INT(returnCode, "return code"),
                TRACE_DATA_END_PARMS);
    }

    return returnCode;
}
