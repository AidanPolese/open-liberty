/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2016
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */

#include <stdlib.h>
#include <dlfcn.h>
#include <jni.h>
#include <stdio.h>

#include "include/common_defines.h"
#include "include/ras_tracing.h"
#include "include/server_function_module_stub.h"
#include "include/server_jni_method_manager.h"

/**
 * @file
 *
 * Methods for managing a native bytebuffer
 *
 */

#define RAS_MODULE_CONST  RAS_MODULE_SERVER_BYTEBUFFER
#define _TP_SERVER_BYTEBUFFER_ALLOCATE_BYTE_BUFFER_ENTRY        1
#define _TP_SERVER_BYTEBUFFER_ALLOCATE_BYTE_BUFFER_EXIT         2
#define _TP_SERVER_BYTEBUFFER_RELEASE_BYTE_BUFFER               3


//---------------------------------------------------------------------
// JNI function declaration and export
//---------------------------------------------------------------------
#pragma export(allocateDirectByteBuffer)
JNIEXPORT jobject JNICALL allocateDirectByteBuffer(JNIEnv* env, jclass thisClass, jlong size);

#pragma export(releaseDirectByteBuffer)
JNIEXPORT void JNICALL releaseDirectByteBuffer(JNIEnv* env, jclass thisClass, jobject directBuffer);

// ---------------------------------------------------------------------
// JNI native method structure for the ByteBuffer methods
// ---------------------------------------------------------------------
#pragma convert("ISO8859-1")
static const JNINativeMethod byteBufferLibraryMethods[] = {
    { "allocateDirectByteBuffer",
      "(J)Ljava/nio/ByteBuffer;",
      (void*) allocateDirectByteBuffer },
    { "releaseDirectByteBuffer",
      "(Ljava/nio/ByteBuffer;)V",
      (void*) releaseDirectByteBuffer }
};
#pragma convert(pop)

// ---------------------------------------------------------------------
// NativeMethodDescriptor for the ByteBuffer
// ---------------------------------------------------------------------
#pragma export(zJNI_com_ibm_ws_bytebuffer_internal_ZOSWsByteBufferPoolManagerImpl)
NativeMethodDescriptor zJNI_com_ibm_ws_bytebuffer_internal_ZOSWsByteBufferPoolManagerImpl = {
    .registrationFunction = NULL,
    .deregistrationFunction = NULL,
    .nativeMethodCount = sizeof(byteBufferLibraryMethods) / sizeof(byteBufferLibraryMethods[0]),
    .nativeMethods = byteBufferLibraryMethods
};




/**
 * Routine to allocate a direct bytebuffer.
 * @param
 * @return
 *
 */
JNIEXPORT jobject JNICALL
allocateDirectByteBuffer(JNIEnv* env, jclass thisClass, jlong size) {
    char* address = NULL;
    jobject directBuffer = NULL;
    if (TraceActive(trc_level_detailed)) {
         TraceRecord(trc_level_detailed,
             TP(_TP_SERVER_BYTEBUFFER_ALLOCATE_BYTE_BUFFER_ENTRY),
             "Entered allocateDirectByteBuffer",
             TRACE_DATA_LONG(size, "Size"),
             TRACE_DATA_END_PARMS);
    }
    // allocate off the stack
    address = (char*)malloc(size);
    directBuffer = (*env)->NewDirectByteBuffer(env, address, size);
    if ((*env)->ExceptionOccurred(env)) {
            return NULL;
    }
    if (TraceActive(trc_level_detailed)) {
         TraceRecord(trc_level_detailed,
             TP(_TP_SERVER_BYTEBUFFER_ALLOCATE_BYTE_BUFFER_EXIT),
             "Allocated DirectByteBuffer",
             TRACE_DATA_LONG(size, "Size"),
             TRACE_DATA_PTR(address,"allocated address"),
             TRACE_DATA_END_PARMS);
    }

    return directBuffer;
}

/**
 * Routine to free the direct bytebuffer.
 *
 * @param inputStack_p    concurrent_stack to push on to.
 * @return
 */
JNIEXPORT void JNICALL
releaseDirectByteBuffer(JNIEnv* env, jclass thisClass, jobject directBuffer) {
    // Get native storage pointer

    void* nativePtr = (*env)->GetDirectBufferAddress(env, directBuffer);
    if (nativePtr != NULL ) {
        if (TraceActive(trc_level_detailed))
            TraceRecord(trc_level_detailed,
            TP(_TP_SERVER_BYTEBUFFER_RELEASE_BYTE_BUFFER),
            "Deleting ByteBuffer storage",
            TRACE_DATA_PTR(nativePtr, "native buffer pointer"),
            TRACE_DATA_END_PARMS);

        free(nativePtr);
    }
}
