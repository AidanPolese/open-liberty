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
 * JNI code in support of command processing.
 */
#include <assert.h>
#include <dlfcn.h>
#include <jni.h>
#include <stdio.h>
#include <stdlib.h>

#include "include/ras_tracing.h"
#include "include/server_command_functions.h"
#include "include/server_jni_method_manager.h"
#include "include/server_native_service_tracker.h"

#define RAS_MODULE_CONST  RAS_MODULE_SERVER_COMMAND_JNI

//---------------------------------------------------------------------
// JNI function declaration and export
//---------------------------------------------------------------------
#pragma export(ntv_getIEZCOMReference)
JNIEXPORT jlong JNICALL
ntv_getIEZCOMReference(JNIEnv* env, jclass jobj);

#pragma export(ntv_getCommand)
JNIEXPORT jbyteArray JNICALL
ntv_getCommand(JNIEnv* env, jclass jobj, jlong iezcom_ptr);

#pragma export(ntv_stopListeningForCommands)
JNIEXPORT void JNICALL
ntv_stopListeningForCommands(JNIEnv* env, jclass jobj);

#pragma export(ntv_issueCommandResponse)
JNIEXPORT jint JNICALL
ntv_issueCommandResponse(JNIEnv* env, jclass jobj, jbyteArray msg, jlong cart, jint consid);

//---------------------------------------------------------------------
// JNI native method structure for the CommandProcessor methods
//---------------------------------------------------------------------
#pragma convert("ISO8859-1")
static const JNINativeMethod commandProcessorMethods[] = {
    { "ntv_getIEZCOMReference",
      "()J",
      (void *) ntv_getIEZCOMReference },
    { "ntv_getCommand",
      "(J)[B",
      (void *) ntv_getCommand },
    { "ntv_stopListeningForCommands",
      "()V",
      (void *) ntv_stopListeningForCommands },
    { "ntv_issueCommandResponse",
      "([BJI)I",
      (void *) ntv_issueCommandResponse }
};
#pragma convert(pop)

//---------------------------------------------------------------------
// NativeMethodDescriptor for the CommandProcessor
//---------------------------------------------------------------------
#pragma export(zJNI_com_ibm_ws_zos_command_processing_internal_CommandProcessor)
NativeMethodDescriptor zJNI_com_ibm_ws_zos_command_processing_internal_CommandProcessor = {
    .registrationFunction = NULL,
    .deregistrationFunction = NULL,
    .nativeMethodCount = sizeof(commandProcessorMethods) / sizeof(commandProcessorMethods[0]),
    .nativeMethods = commandProcessorMethods
};

//---------------------------------------------------------------------
// Module scoped data
//---------------------------------------------------------------------
static unsigned int* commandStopListenECB_Ptr = NULL;


//---------------------------------------------------------------------
//---------------------------------------------------------------------
JNIEXPORT jlong JNICALL
ntv_getIEZCOMReference(JNIEnv* env, jclass jobj) {
    iezcom* com_ptr = NULL;
    const server_unauthorized_function_stubs* unauth_stubs_p = NULL;

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                TP(1),
                "CommandProcessor.ntv_getIEZCOMReference, entry",
                TRACE_DATA_END_PARMS);
    }

    //-------------------------------------------------------------------------
    // Clear Stop Listening ECB??
    //-------------------------------------------------------------------------
    commandStopListenECB_Ptr = __malloc31(sizeof(unsigned int *));
    memset(commandStopListenECB_Ptr,0,sizeof(unsigned int *));


    //-------------------------------------------------------------------------
    // Drive routine to get IEZCOM area
    //-------------------------------------------------------------------------
    unauth_stubs_p = getServerUnauthorizedFunctionStubs();

    if (unauth_stubs_p) {
        GetIEZCOM_referenceParms getComParms = {
            .outCom_ptr = &com_ptr
        };
        unauth_stubs_p->getIEZCOM_reference(&getComParms);
    }

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                TP(2),
                "CommandProcessor.ntv_getIEZCOM_reference, exit",
                TRACE_DATA_PTR(com_ptr, "com_ptr"),
                TRACE_DATA_END_PARMS);
    }

    return (jlong) com_ptr;
}

//---------------------------------------------------------------------
//---------------------------------------------------------------------
JNIEXPORT jbyteArray JNICALL
ntv_getCommand(JNIEnv* env, jclass jobj, jlong iezcom_ptr) {
    iezcom* com_ptr = (iezcom *) iezcom_ptr;
    jbyteArray cmdInfoAreaByteArray = NULL;
    CommandInfoArea cmdInfoArea = {{0}};
    const server_unauthorized_function_stubs* unauth_stubs_p = NULL;

    // Be a boyscout and prepare for failure
    cmdInfoArea.cia_commandType =  CIA_COMMANDTYPE_ERROR;

    //-------------------------------------------------------------------------
    // Call unauth service to retrieve a Console Command (waits until one
    // arrives or posted to stop waiting)
    //-------------------------------------------------------------------------
    unauth_stubs_p = getServerUnauthorizedFunctionStubs();
    if (unauth_stubs_p) {
        if (com_ptr != 0) {
            GetConsoleCommandParms getCmdParms = {
                .inCom_ptr = com_ptr,
                .inStopListenECB_ptr = commandStopListenECB_Ptr,
                .outCommandInfoArea_ptr = &cmdInfoArea
            };
            unauth_stubs_p->getConsoleCommand(&getCmdParms);
        }
    } else {
        cmdInfoArea.cia_errorCode = CIA_ERRCODE_NO_ACCESS_SUFM1;
    }


    cmdInfoAreaByteArray = (*env)->NewByteArray(env, sizeof(cmdInfoArea));
    (*env)->SetByteArrayRegion(env, cmdInfoAreaByteArray, 0, sizeof(cmdInfoArea), (jbyte*)&(cmdInfoArea));

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(3),
                    "CommandProcessor.ntv_getCommand",
                    TRACE_DATA_HEX_INT(cmdInfoArea.cia_commandType, "cia_commandType"),
                    TRACE_DATA_HEX_INT(cmdInfoArea.cia_consoleID, "cia_consoleID"),
                    TRACE_DATA_RAWDATA(sizeof(cmdInfoArea.cia_consoleName), cmdInfoArea.cia_consoleName, "cia_consoleName"),
                    TRACE_DATA_LONG(*((long *)(&cmdInfoArea.cia_commandCART[0])), "cia_commandCART"),
                    TRACE_DATA_HEX_INT(cmdInfoArea.cia_commandRestOfCommandLength, "cia_commandRestOfCommandLength"),
                    TRACE_DATA_STRING(cmdInfoArea.cia_commandRestOfCommand, "cia_commandRestOfCommand"),
                    TRACE_DATA_END_PARMS);
    }

    return cmdInfoAreaByteArray;
}


//---------------------------------------------------------------------
//---------------------------------------------------------------------
JNIEXPORT void JNICALL
ntv_stopListeningForCommands(JNIEnv* env, jclass jobj) {

    const server_unauthorized_function_stubs* unauth_stubs_p = NULL;

    //-------------------------------------------------------------------------
    // Post the Stop Listening ECB
    //-------------------------------------------------------------------------
    unauth_stubs_p = getServerUnauthorizedFunctionStubs();

    if (unauth_stubs_p) {

        int postRC = 0;
        StopCommandListeningParms stopCmdListeningParms = {
            .inECB_ptr = commandStopListenECB_Ptr,
            .outPostRC_ptr = &postRC
        };
        unauth_stubs_p->stopCommandListening(&stopCmdListeningParms);

        if (TraceActive(trc_level_detailed)) {
            TraceRecord(trc_level_detailed,
                    TP(4),
                    "CommandProcessor.ntv_stopListeningForCommands",
                    TRACE_DATA_HEX_INT(postRC, "postRC"),
                    TRACE_DATA_END_PARMS);
        }
    }
}

JNIEXPORT jint JNICALL
ntv_issueCommandResponse(JNIEnv* env, jclass jobj, jbyteArray jmsg, jlong cart, jint consid) {
    const server_unauthorized_function_stubs* unauth_stubs_p = NULL;
    char* msg = NULL;
    int msglen = 0;
    int rc = 0;

    // Message coverted to native code page in Java
    if (!jmsg) {
        return -1;
    }

    msglen = (*env)->GetArrayLength(env, jmsg);
    msg = malloc(msglen+1);
    (*env)->GetByteArrayRegion(env, jmsg, 0, msglen, (jbyte*)(msg));
    msg[msglen] = '\0';

    // msg = (char *) (*env)->GetStringUTFChars(env, jmsg, NULL);
    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                TP(5),
                "CommandProcessor.ntv_issueCommandResponse",
                TRACE_DATA_RAWDATA(strlen(msg), msg, "msg"),
                TRACE_DATA_INT(consid,"console id"),
                TRACE_DATA_LONG(cart, "cart"),
                TRACE_DATA_END_PARMS);
    }

    //-------------------------------------------------------------------------
    // Issue the WTO with the CART value.
    //-------------------------------------------------------------------------
    unauth_stubs_p = getServerUnauthorizedFunctionStubs();

    if (unauth_stubs_p)    {
        unauth_stubs_p->write_to_operator_response(msg, (const long*)&cart, &consid, &rc);

        if (TraceActive(trc_level_detailed)) {
            TraceRecord(trc_level_detailed,
                    TP(6),
                    "CommandProcessor.ntv_issueCommandResponse",
                    TRACE_DATA_INT(rc, "rc"),
                    TRACE_DATA_END_PARMS);
        }
    }

    return rc;
}
