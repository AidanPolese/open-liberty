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

// this define statement is required in order to include non-posix structures and prototypes
// from aio.h
#define _AIO_OS390 1

#include <aio.h>
#include <assert.h>
#include <dlfcn.h>
#include <errno.h>
#include <jni.h>
#include <signal.h>
#include <stdlib.h>
#include <sys/ioctl.h>
#include <sys/uio.h>
#include "include/mvs_aio_common.h"
#include "include/ras_tracing.h"
#include "include/server_function_module_stub.h"
#include "include/server_jni_method_manager.h"
#include "include/server_native_service_tracker.h"
#include "include/util_jni.h"

#define RAS_MODULE_CONST  RAS_MODULE_SERVER_ASYNC_IO

//---------------------------------------------------------------------
// Error codes.
//---------------------------------------------------------------------
#define SERVER_ASYNC_IO_MULTIIO3_BADCOUNT      (RAS_MODULE_SERVER_ASYNC_IO + 1)


#define _TP_SERVER_ASYNCIO_ENTER_INIT               1
#define _TP_SERVER_ASYNCIO_ENTER_SHUTDOWN           2
#define _TP_SERVER_ASYNCIO_ENTER_NEWCOMPPORT        3
#define _TP_SERVER_ASYNCIO_ENTER_PREPARE2           4
#define _TP_SERVER_ASYNCIO_ENTER_DISPOSE            5
#define _TP_SERVER_ASYNCIO_ENTER_INITIOCB           6
#define _TP_SERVER_ASYNCIO_ENTER_TERMIOCB           7
#define _TP_SERVER_ASYNCIO_ENTER_MULTIIO3           8
#define _TP_SERVER_ASYNCIO_ENTER_CANCEL2            9
#define _TP_SERVER_ASYNCIO_ENTER_GETERROR           10
#define _TP_SERVER_ASYNCIO_ENTER_GETIOEV2           11
#define _TP_SERVER_ASYNCIO_GETIOEV2_AIOCALL_RETURN  12
#define _TP_SERVER_ASYNCIO_ENTER_GETIOEV3           13
#define _TP_SERVER_ASYNCIO_MULTIIO3_WRITE           14
#define _TP_SERVER_ASYNCIO_MULTIIO3_READ            15
#define _TP_SERVER_ASYNCIO_MULTIIO3_WRITEV          16
#define _TP_SERVER_ASYNCIO_MULTIIO3_READV           17
#define _TP_SERVER_ASYNCIO_MULTIIO3_AIOCALL         18
#define _TP_SERVER_ASYNCIO_MULTIIO3_AIOCALL_RETURN  19
#define _TP_SERVER_ASYNCIO_MULTIIO3_IOV             20
#define _TP_SERVER_ASYNCIO_MULTIIO3_AIOCALL_ERROR   21
#define _TP_SERVER_ASYNCIO_PREPARE2_CONNINFO        22
#define _TP_SERVER_ASYNCIO_ENTER_CLOSEPORT2         23
#define _TP_SERVER_ASYNCIO_EXIT_SHUTDOWN            24
#define _TP_SERVER_ASYNCIO_MULTIIO3_COUNT           25
#define _TP_SERVER_ASYNCIO_EXIT_CLOSEPORT2          26
#define _TP_SERVER_ASYNCIO_GETIOEV3_GETIOEV2_RETURN 27
#define _TP_SERVER_ASYNCIO_GETIOEV3_AIOCALL_RETURN  28
#define _TP_SERVER_ASYNCIO_GETIOEV3_OUTARRAY        29
#define _TP_SERVER_ASYNCIO_MULTIIO3_AIOCALL_EWOULDBLOCK  30
#define _TP_SERVER_ASYNCIO_MULTIIO3_AIOCALL_GETSOCK      31
#define _TP_SERVER_ASYNCIO_GETIOEV3_RETURN_ZERO          32
#define _TP_SERVER_ASYNCIO_GETIOEV2_TIMEOUT              33
#define _TP_SERVER_ASYNCIO_GETIOEV3_GETIOEV2_TIMEOUT     34
#define _TP_SERVER_ASYNCIO_GETIOEV2_HARDFAILURE          35

// NOTE: To turn on some debug code without tracing un-comment the
//   following #define statement in mvs_aio_common.h
//   #define AIOCD_PLO_DEBUG

//---------------------------------------------------------------------
// JNI function declaration and export
//---------------------------------------------------------------------
#pragma export(aio_init)
JNIEXPORT jint JNICALL aio_init(JNIEnv* env, jclass thisClass, jint cacheSize, jclass errorClazz);

#pragma export(aio_shutdown)
JNIEXPORT void JNICALL aio_shutdown(JNIEnv* env, jclass thisClass);

#pragma export(aio_newCompletionPort)
JNIEXPORT jlong JNICALL aio_newCompletionPort(JNIEnv* env, jclass thisClass);

#pragma export(aio_closeport2)
JNIEXPORT void JNICALL aio_closeport2(JNIEnv* env, jclass thisClass, jlong completionPort);

#pragma export(aio_prepare2)
JNIEXPORT jlong JNICALL aio_prepare2(JNIEnv* env, jclass thisClass, jlong handle, jlong epoll_fd1);

#pragma export(aio_dispose)
JNIEXPORT jlong JNICALL aio_dispose(JNIEnv* env, jclass thisClass, jlong handle);

#pragma export(aio_initIOCB)
JNIEXPORT void JNICALL aio_initIOCB(JNIEnv* env, jclass thisClass, jlong iocbAddr);

#pragma export(aio_termIOCB)
JNIEXPORT void JNICALL aio_termIOCB(JNIEnv* env, jclass thisClass, jlong iocbAddr);

#pragma export(aio_multiIO3)
JNIEXPORT jboolean JNICALL aio_multiIO3(
    JNIEnv*  env,
    jclass   thisClass,
    jlong    iocb,           // Address of start of IOCB
    jlong    position,       // Position in the file to start read/write
    jint     count,          // Count of buffers to read/write
    jboolean isRead,         // Flag indicating a Read (true) or Write (false)
    jboolean forceQueue,     // boolean indicating callers knowledge of the readiness of the fd
    jlong    bytesRequested,
    jboolean useJITBuffer);

#pragma export(aio_cancel2)
JNIEXPORT jint JNICALL aio_cancel2(JNIEnv* env, jclass thisClass, jlong handle, jlong id);

#pragma export(aio_getErrorString)
JNIEXPORT jint JNICALL aio_getErrorString(JNIEnv* env, jclass thisClass, jint errorCode, jbyteArray javaArray);

#pragma export(aio_getioev2)
JNIEXPORT jboolean JNICALL aio_getioev2(
    JNIEnv* env,
    jclass thisClass,
    jlong bufferAddress,
    jint timeout,
    jlong completionPort);

#pragma export(aio_getioev3)
JNIEXPORT jint JNICALL aio_getioev3(
    JNIEnv* env,
    jclass thisClass,
    jlongArray jlArray,
    jint numBatch,
    jint timeout,
    jlong epoll_fd);

// ---------------------------------------------------------------------
// JNI native method structure for the CommandProcessor methods
// ---------------------------------------------------------------------
#pragma convert("ISO8859-1")
static const JNINativeMethod asyncLibraryMethods[] = {
    { "aio_init",
      "(ILjava/lang/Class;)I",
      (void*) aio_init },
    { "aio_shutdown",
      "()V",
      (void*) aio_shutdown },
    { "aio_newCompletionPort",
      "()J",
      (void*) aio_newCompletionPort },
    { "aio_closeport2",
      "(J)V",
      (void*) aio_closeport2 },
    { "aio_prepare2",
      "(JJ)J",
      (void*) aio_prepare2 },
    { "aio_dispose",
      "(J)J",
      (void*) aio_dispose },
    { "aio_initIOCB",
      "(J)V",
      (void*) aio_initIOCB },
    { "aio_termIOCB",
      "(J)V",
      (void*) aio_termIOCB },
    { "aio_multiIO3",
      "(JJIZZJZ)Z",
      (void*) aio_multiIO3 },
    { "aio_cancel2",
      "(JJ)I",
      (void*) aio_cancel2 },
    { "aio_getErrorString",
      "(I[B)I",
      (void*) aio_getErrorString },
    { "aio_getioev2",
      "(JIJ)Z",
      (void*) aio_getioev2 },
    { "aio_getioev3",
      "([JIIJ)I",
      (void*) aio_getioev3 }
};
#pragma convert(pop)

// ---------------------------------------------------------------------
// NativeMethodDescriptor for the CommandProcessor
// ---------------------------------------------------------------------
#pragma export(zJNI_com_ibm_io_async_AsyncLibrary)
NativeMethodDescriptor zJNI_com_ibm_io_async_AsyncLibrary = {
    .registrationFunction = NULL,
    .deregistrationFunction = NULL,
    .nativeMethodCount = sizeof(asyncLibraryMethods) / sizeof(asyncLibraryMethods[0]),
    .nativeMethods = asyncLibraryMethods
};

/**
 * Initialize the library
 *
 * Return:    void
 * Class:     com_ibm_io_async_AsyncLibrary
 * Method:    aio_init
 * Signature: (ILjava/lang/Class;)J
 *
 * Note: Synchronized by AsyncLibrary.createInstance()
 */
JNIEXPORT jint JNICALL
aio_init(JNIEnv* env, jclass thisClass, jint cacheSize, jclass errorClazz) {
    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(_TP_SERVER_ASYNCIO_ENTER_INIT),
                    "Entered aio_init()",
                    TRACE_DATA_INT(cacheSize, "cacheSize"),
                    TRACE_DATA_END_PARMS);
    }

    int rc = 0;
    AIO_InitParms aioInitParms = {
                .returnCode     = &rc
    };
    // call to authorized code to setup for AsyncIO (create storage pools, AIOCD, ...)
    const struct server_authorized_function_stubs* auth_stubs_p =
            (server_authorized_function_stubs*) getServerAuthorizedFunctionStubs();

    auth_stubs_p->aio_initPGOO(&aioInitParms);

    if (rc < 0) {
        // Build and throw exception
        char zbuf[150];
        snprintf(zbuf, sizeof(zbuf), "Failure to obtain native storage for Asynchronous I/O in %s, rc x%x",
                             "aio_initPGOO", rc);
        setNewExceptionByName(env,"com/ibm/io/async/AsyncException", zbuf);
    }
    return rc;
}

/**
 * Shutdown the library and free any OS resources held.
 *
 * Returns:   void
 * Class:     com_ibm_io_async_AsyncLibrary
 * Method:    aio_shutdown
 * Signature: ()V
 */
JNIEXPORT void JNICALL
aio_shutdown(JNIEnv* env, jclass thisClass) {
    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(_TP_SERVER_ASYNCIO_ENTER_SHUTDOWN),
                    "Entered aio_shutdown()",
                    TRACE_DATA_END_PARMS);
    }

    int rc = 0;
    AIO_ShutdownParms aioShutdownParms = {
        .returnCode = &rc
    };

    const struct server_authorized_function_stubs* auth_stubs_p =
             (server_authorized_function_stubs*) getServerAuthorizedFunctionStubs();

    auth_stubs_p->shutdownAIO(&aioShutdownParms);

    if (TraceActive(trc_level_detailed)) {
            TraceRecord(trc_level_detailed,
                        TP(_TP_SERVER_ASYNCIO_EXIT_SHUTDOWN),
                        "Exit aio_shutdown()",
                        TRACE_DATA_HEX_INT(rc, "returnCode"),
                        TRACE_DATA_END_PARMS);
    }

    return;
}

/**
 * Set up a new epoll fd for async operations.
 * Comment:  Essentially an epoll fd on Linux equals a CompletionPort handle on Windows/AIX
 *
 * Return:    jlong  New CompletionHandler is returned or an exception is thrown.
 * Class:     com_ibm_io_async_AsyncLibrary
 * Method:    aio_newCompletionPort
 * Signature: ()J
 */
JNIEXPORT jlong JNICALL
aio_newCompletionPort(JNIEnv* env, jclass thisClass) {
    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(_TP_SERVER_ASYNCIO_ENTER_NEWCOMPPORT),
                    "Entered newCompletionPort()",
                    TRACE_DATA_END_PARMS);
    }

    // I believe this is used as a grouping identifier in Java.  I don't believe we need to
    // support more than 1?
    //
    // Set to "NATVPORT" in EBCDIC because we're z.
    return 0xD5C1E3E5D7D6D9E3L;
}

/**
 * This is called prior to shutdown.  Release the handlers that are waiting for completed IO events from the
 * AsyncIO SRB exit.  We release with a special code to inform them that the channel is stopping/cleaning up/going away.
 */
JNIEXPORT void JNICALL
aio_closeport2(JNIEnv* env, jclass thisClass, jlong completionPort) {
    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(_TP_SERVER_ASYNCIO_ENTER_CLOSEPORT2),
                    "Entered aio_closeport2",
                    TRACE_DATA_HEX_LONG(completionPort, "completionPort"),
                    TRACE_DATA_END_PARMS);
    }

    int rc = 0;
    AIO_CloseportParms aioCloseportParms = {
        .returnCode = &rc
    };

    const struct server_authorized_function_stubs* auth_stubs_p =
             (server_authorized_function_stubs*) getServerAuthorizedFunctionStubs();

    auth_stubs_p->closeportAIO(&aioCloseportParms);

    if (TraceActive(trc_level_detailed)) {
            TraceRecord(trc_level_detailed,
                        TP(_TP_SERVER_ASYNCIO_EXIT_CLOSEPORT2),
                        "Exit aio_closeport2()",
                        TRACE_DATA_HEX_INT(rc, "returnCode"),
                        TRACE_DATA_END_PARMS);
    }
}

/**
 * Prepare the given file/socket channel handle for async operations.
 *
 * Return:    jlong (Returns the address of a data structure representing the file handle and ongoing operations
 * Class:     com_ibm_io_async_AsyncLibrary
 * Method:    aio_prepare2
 * Signature: (JJ)J
 */
JNIEXPORT jlong JNICALL
aio_prepare2(JNIEnv* env, jclass thisClass, jlong handle, jlong completionPort) {

    if (TraceActive(trc_level_detailed)) {
            TraceRecord(trc_level_detailed,
            TP(_TP_SERVER_ASYNCIO_ENTER_PREPARE2),
            "Entered aio_prepare2",
            TRACE_DATA_HEX_LONG(handle, "original prepare handle"),
            TRACE_DATA_HEX_LONG(completionPort, "completionPort"),
            TRACE_DATA_END_PARMS);
    }
    const struct server_authorized_function_stubs* auth_stubs_p =
                (server_authorized_function_stubs*) getServerAuthorizedFunctionStubs();

    int dontblock = 0;
    ioctl(handle, FIONBIO, (char*) &dontblock);

    //Note: need to change this to allocate ByteArray the size of RegistryToken instead of ptr when additional
    //      java support is added to support carting around the token instead of 8-bytes.
    RegistryToken* tokenPtr; // 8 bytes token ptr instead of 64bytes token
    tokenPtr = malloc(sizeof(RegistryToken));

    AIO_ConnectionParms aioConnParms = {
            .socketHandle     = (long long)handle,
            .outputToken      = tokenPtr
    };

    auth_stubs_p->prepareConnection(&aioConnParms);

    if (TraceActive(trc_level_detailed)) {
                TraceRecord(trc_level_detailed,
                TP(_TP_SERVER_ASYNCIO_ENTER_PREPARE2),
                "Exit aio_prepare2",
                TRACE_DATA_PTR(tokenPtr, "token ptr as handle"),
                TRACE_DATA_LONG(tokenPtr, "token long as handle"),
                TRACE_DATA_END_PARMS);
    }
    //Note: need to change this when the interface changes to take 64 bytes token instead of token ptr
    return (long)tokenPtr;
}

/**
 * Dispose the data structures associated with a Channel
 *
 * Returns:   original socket handle value
 * Class:     com_ibm_io_async_AsyncLibrary
 * Method:    aio_dispose
 * Signature: (J)V
 */
JNIEXPORT jlong JNICALL
aio_dispose(JNIEnv* env, jclass thisClass, jlong handle) {

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(_TP_SERVER_ASYNCIO_ENTER_DISPOSE),
                    "Entered aio_dispose()",
                    TRACE_DATA_PTR(handle, "Dispose handle"),
                    TRACE_DATA_END_PARMS);
    }

    long origHandle = 0;
    int  rc         = 0;
    RegistryToken* token = (RegistryToken*)handle;
    AIO_DisposeParms aioDisposeParms = {
        .inputToken   = token,
        .outputHandle = &origHandle,
        .returnCode   = &rc
    };

    const struct server_authorized_function_stubs* auth_stubs_p =
             (server_authorized_function_stubs*) getServerAuthorizedFunctionStubs();

    auth_stubs_p->disposeConnection(&aioDisposeParms);

    if (TraceActive(trc_level_detailed)) {
            TraceRecord(trc_level_detailed,
                        TP(_TP_SERVER_ASYNCIO_ENTER_DISPOSE),
                        "Exit aio_dispose()",
                        TRACE_DATA_HEX_LONG(origHandle, "original socket handle"),
                        TRACE_DATA_INT(rc, "return code"),
                        TRACE_DATA_END_PARMS);
    }

    // Dispose of the key 8 storage allocated for the RegisteryToken in the prepareConn.
    if (rc == 0) {
        free(token);
    }

    return origHandle;
}

/**
 * Prepare a DirectCompletionKey (IOCB) structure for use by asynchronous IO operations
 *
 * Return:      void
 * Class:       com_ibm_io_async_AsyncLibrary
 * Method:      aio_initIOCB
 * Signature    (J)V
 */
JNIEXPORT void JNICALL
aio_initIOCB(JNIEnv* env, jclass thisClass, jlong iocbAddr) {
    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(_TP_SERVER_ASYNCIO_ENTER_INITIOCB),
                    "Entered aio_initIOCB()",
                    TRACE_DATA_PTR(iocbAddr, "IOCB address"),
                    TRACE_DATA_END_PARMS);
    }

    // Note: I modified AsyncLibrary's PrivLoadLibrary class to skip calls to this method.
}

/**
 * Clean up an DirectCompletionKey (IOCB) structure when no longer required for asynchronous IO operations
 *
 * Return:      void
 * Class:       com_ibm_io_async_AsyncLibrary
 * Method:      aio_termIOCB
 * Signature    (J)V
 */
JNIEXPORT void JNICALL
aio_termIOCB(JNIEnv* env, jclass thisClass, jlong iocbAddr) {
    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(_TP_SERVER_ASYNCIO_ENTER_TERMIOCB),
                    "Entered aio_termIOCB()",
                    TRACE_DATA_PTR(iocbAddr, "IOCB address"),
                    TRACE_DATA_END_PARMS);
    }

    // Note: I modified AsyncLibrary's PrivLoadLibrary class to skip calls to this method.
}

// Note: Used for debugging with counts and such...trying to get accurate numbers
static int csIntInc(int* intTarget_Ptr, int increment, int max)
{
  int oldInt, newInt;

  oldInt = *intTarget_Ptr;
  do
  {
    newInt = oldInt + increment;
    if (max > 0 && newInt == max) {
        newInt = 0;
    }
  } while(cs((cs_t *) &oldInt,
             (cs_t *) intTarget_Ptr,
             (cs_t)   newInt
             ));

  return newInt;

} // end, csIntInc

/**
 * Perform an asynchronous multi-read/write operation on the given channel (Version 3)
 * This version uses an IOCB that carries most of the parameters in a DirectByteBuffer
 *
 * LINUX implementation
 *
 * Returns:   boolean
 * Class:     com_ibm_io_async_AsyncLibrary
 * Method:    aio_multiIO3
 * Signature: (JJIZZJZ)Z
 */
JNIEXPORT jboolean JNICALL
aio_multiIO3(
    JNIEnv* env, jclass thisClass,
    jlong    iobuffer,       // Address of start of IO buffer
    jlong    position,       // Position in the file to start read/write
    jint     count,          // Count of buffers to read/write
    jboolean isRead,         // Flag indicating a Read (true) or Write (false)
    jboolean forceQueue,     // boolean indicating callers knowledge of the readiness of the fd
    jlong    bytesRequested, // Min bytes requested (read), need to support immediate read (=0)
    jboolean useJITBuffer
) {
    // Problems so far:
    //   - bytesRequested seems to always be 1 for a read, and -1 for a write.  However added support
    //     to Immediate read (bytesRequested==0).
    //
    //   - Java code in AbstractAsyncChannel.multiIO interprets the return value true
    //     of this function as "pending", i.e. if we return true from this function
    //     then AbstractAsyncChannel.multiIO interprets this as I/O still pending...
    //     however, the javadoc for the native aio_multiIO3() function in AsyncLibrary
    //     says to return true if the I/O completed immediately or false otherwise, which
    //     is the reverse...  for now I have coded the return value to work properly,
    //     which is the opposite of what the javadoc says I should do.

    // the iobuffer is a pointer to an array of longs, some of which are input values and some of which
    // are output values, as described here by the javadoc in AsyncLibrary.aio_multiIO3() which I
    // edited a bit because some of it was incorrect:
    //
    //   [0] - the channel identifier
    //   [1] - the call identifier
    //   [2, 3] used for OUTPUT only
    //   [4] - native data structure address
    //   [5] - not listed in Javadoc but contains some kind of return status?
    //   [6] - address of start of first buffer
    //   [7] - length of first buffer
    //   [8...] - addresses & lengths of second and subsequent buffers
    //
    //   Contains the following data if the operation completes immediately:
    //
    //   [0] - the channel identifier
    //   [1] - the call identifier
    //   [2] - the error code for a failed IO operation, or 0 if successful
    //   [3] - the number of bytes affected by a successful IO operation
    jlong* ioev = (jlong*) iobuffer;
    iovec* mptr = NULL;

    // print all of the input so i can tell what's going on...
    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(_TP_SERVER_ASYNCIO_ENTER_MULTIIO3),
                    "Enter aio_multiIO3",
                    TRACE_DATA_PTR(ioev, "IOEV address"),
                    TRACE_DATA_HEX_LONG(ioev[0], "Channel ID (ioev[0])"),
                    TRACE_DATA_HEX_LONG(ioev[1], "Call ID (ioev[1])"),
                    TRACE_DATA_HEX_LONG(ioev[2], "returnCode (ioev[2])"),
                    TRACE_DATA_HEX_LONG(ioev[3], "bytesAffected (ioev[3])"),
                    TRACE_DATA_HEX_LONG(ioev[4], "nativeStructure (ioev[4])"),
                    TRACE_DATA_HEX_LONG(ioev[5], "returnStatus (ioev[5])"),
                    TRACE_DATA_HEX_LONG(ioev[6], "IO Buffer address (ioev[6])"),
                    TRACE_DATA_HEX_LONG(ioev[7], "IO Buffer length (ioev[7])"),
                    TRACE_DATA_INT(count,   "IO Buffer count)"),
                    TRACE_DATA_INT(isRead,   "isRead)"),
                    TRACE_DATA_INT(forceQueue,   "forceQueue)"),
                    TRACE_DATA_LONG(bytesRequested, "bytesRequested"),
                    TRACE_DATA_END_PARMS);
    }

    // Less than 1 should never happen.  Java code telling us it is
    // passing a negative number of buffers
    if (count < 1) {
        // Return the failure to caller.
        ioev[2] = SERVER_ASYNC_IO_MULTIIO3_BADCOUNT;
        ioev[3] = 0;

        if (TraceActive(trc_level_detailed)) {
            TraceRecord(trc_level_detailed,
                        TP(_TP_SERVER_ASYNCIO_MULTIIO3_COUNT),
                        "aio_multiIO3: count = 0",
                        TRACE_DATA_PTR(ioev,         "IOEV address               "),
                        TRACE_DATA_HEX_LONG(ioev[0], "Channel ID (ioev[0])       "),
                        TRACE_DATA_HEX_LONG(ioev[1], "Call ID (ioev[1])          "),
                        TRACE_DATA_HEX_LONG(ioev[2], "returnCode (ioev[2])       "),
                        TRACE_DATA_HEX_LONG(ioev[3], "bytesAffected (ioev[3])    "),
                        TRACE_DATA_HEX_LONG(ioev[4], "nativeStructure (ioev[4])  "),
                        TRACE_DATA_HEX_LONG(ioev[5], "returnStatus (ioev[5])     "),
                        TRACE_DATA_PTR(ioev[6],      "IO Buffer address (ioev[6])"),
                        TRACE_DATA_HEX_LONG(ioev[7], "IO Buffer length (ioev[7]) "),
                        TRACE_DATA_INT(count,        "IO Buffer count)           "),
                        TRACE_DATA_INT(isRead,       "isRead)                    "),
                        TRACE_DATA_END_PARMS);
        }

        return JNI_FALSE;
    }

    if (TraceActive(trc_level_detailed)) {
        if (count == 1) {
            if (isRead == 0) { // single write
                TraceRecord(trc_level_detailed,
                            TP(_TP_SERVER_ASYNCIO_MULTIIO3_WRITE),
                            "aio_multiIO3: WRITE request",
                            TRACE_DATA_RAWDATA(BBGZ_min(ioev[7], 4096),
                                               ioev[6],
                                               "AIO buffer contents"),
                                               TRACE_DATA_END_PARMS);
            } else { // single read
                TraceRecord(trc_level_detailed,
                            TP(_TP_SERVER_ASYNCIO_MULTIIO3_READ),
                            "aio_multiIO3: READ request",
                            TRACE_DATA_END_PARMS);
            }
        } else {
            if (isRead == 0) { // multi-write
                TraceRecord(trc_level_detailed,
                            TP(_TP_SERVER_ASYNCIO_MULTIIO3_WRITEV),
                            "aio_multiIO3: WRITEV request",
                            TRACE_DATA_RAWDATA(BBGZ_min(ioev[7], 4096),
                                               ioev[6],
                                               "AIO buffer 1 contents"),
                            TRACE_DATA_RAWDATA(BBGZ_min(ioev[9], 4096),
                                               ioev[8],
                                               "AIO buffer 2 contents"),
                                               TRACE_DATA_END_PARMS);
            } else { // multi-read
                TraceRecord(trc_level_detailed,
                            TP(_TP_SERVER_ASYNCIO_MULTIIO3_READV),
                            "aio_multiIO3: READV request",
                            TRACE_DATA_END_PARMS);
            }
        }
    }

    RegistryToken* token = (RegistryToken*)ioev[0];
    long callid = ioev[1];
    mptr = (iovec*)((char*)ioev + (6 * sizeof(long))); // starting address of the array element that contains the iovec values

    // Invoke BPX4AIO here to perform the I/O operation
    int rv = 0;
    int rc = 0;
    int rsn = 0;
    int aiorv = 0; // number of bytes of completed IO

    long originalHandle = 0;  // Original Socket Handle from prepare call for Immediate Reads

    const struct server_authorized_function_stubs* auth_stubs_p =
        (server_authorized_function_stubs*) getServerAuthorizedFunctionStubs();

    // Check for an Immediate read (bytesRequested==0). If so, we need to modify the AIOCB and the Socket(FD) to perform
    // a Synchronous non-blocking read.  The socket is modified here...this AIOCB is modified in aio_call().
    if (bytesRequested == 0 && isRead == 1) {
        int localRC = 0;
        // Call to authorized code to retrieve the socket descriptor
        AIO_GetSocketDescriptor aioGetSocketDescriptor = {
            .token           = token,
            .socketInfo_p    = &originalHandle,
            .rc_p            = &localRC
        };

        auth_stubs_p->aioGetSocketDescriptor(&aioGetSocketDescriptor);

        if (localRC == 0) {
            // Do some immediate read setup by making the subsequent call non-blocking.
            int dontblock = 1;
            ioctl(originalHandle, FIONBIO, (char*) &dontblock);
        } else {
            // Return the failure to caller.
            ioev[2] = localRC;
            ioev[3] = 0;

            if (TraceActive(trc_level_detailed)) {
               TraceRecord(trc_level_detailed,
                             TP(_TP_SERVER_ASYNCIO_MULTIIO3_AIOCALL_GETSOCK),
                             "failure return from aioGetSocketDescriptor() ",
                             TRACE_DATA_HEX_LONG(ioev[2], "returnCode (ioev[2])       "),
                             TRACE_DATA_HEX_LONG(ioev[3], "bytesAffected (ioev[3])    "),
                             TRACE_DATA_END_PARMS);
            }
            return JNI_FALSE;
        }
    }

    AIO_CallParms aioCallParms = {
        .token               = token,
        .callId              = callid,
        .count               = count,
        .isRead              = (int)isRead,
        .forceQueue          = (int)forceQueue,
        .bytesRequested      = bytesRequested,
        .iov_p               = mptr,
        .AIO_aiorv           = &aiorv,
        .AIO_call_rv_p       = &rv,
        .AIO_call_rc_p       = &rc,
        .AIO_call_rsn_p      = &rsn
    };

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(_TP_SERVER_ASYNCIO_MULTIIO3_AIOCALL),
                    "Invoking aio_call() to drive BPX4AIO",
                    TRACE_DATA_PTR(auth_stubs_p, "aio_call() auth_stubs_p"),
                    TRACE_DATA_HEX_LONG(callid, "callid"),
                    TRACE_DATA_LONG(bytesRequested, "bytesRequested"),
                    TRACE_DATA_RAWDATA(sizeof(aioCallParms),
                                       &aioCallParms,
                                       "aioCallParms parameter data"),
                    TRACE_DATA_END_PARMS);
    }

    // ---------------------------------------------------------------------
    // Calling native routine aio_call() to drive BPX4AIO here.
    // ---------------------------------------------------------------------
    auth_stubs_p->aio_call(&aioCallParms);

    if (TraceActive(trc_level_detailed)) {
       TraceRecord(trc_level_detailed,
                     TP(_TP_SERVER_ASYNCIO_MULTIIO3_AIOCALL_RETURN),
                     "Return from aio_call() ",
                     TRACE_DATA_HEX_INT(rc, "aio_call() rc"),
                     TRACE_DATA_HEX_INT(rsn, "aio_call() rsn"),
                     TRACE_DATA_HEX_INT(rv, "aio_call() rv"),
                     TRACE_DATA_INT(aiorv, "aio_call() aiorv"),
                     TRACE_DATA_END_PARMS);
    }

    // ---------------------------------------------------------------------
    // Restore modifications made for a Immediate Read request, if any.
    // ---------------------------------------------------------------------
    if (bytesRequested == 0 && isRead == 1) {
        // Restore immediate read setup by allowing the socket to block.
        int dontblock = 0;
        ioctl(originalHandle, FIONBIO, (char*) &dontblock);
    }

    // ---------------------------------------------------------------------
    // Process BPX4AIO call results
    // ---------------------------------------------------------------------

    // (rv == 1) means the I/O operation completed synchronously
    if (rv == 1) {
        ioev[2] = 0;
        ioev[3] = aiorv;

        // Check for Sync with 0-bytes requested (caller is just checking for data).
        // Note: supported this like cWAS defect 349622 (dup'd of 350015).

        return JNI_FALSE;
    } else if (rv == 0) {
        if (TraceActive(trc_level_detailed)) {
            static int multi3_AsyncRtns = 0;
            multi3_AsyncRtns++;
               TraceRecord(trc_level_detailed,
                    TP(_TP_SERVER_ASYNCIO_MULTIIO3_AIOCALL_ERROR),
                    "aio_multiIO3, BPX4AIO Returned async",
                    TRACE_DATA_HEX_INT(rc, "rc"),
                    TRACE_DATA_HEX_INT(multi3_AsyncRtns, "multi3_AsyncRtns"),
                    TRACE_DATA_END_PARMS);
        }
#ifdef AIOCD_PLO_DEBUG_MSGS
          static int debug_multi3_AsyncRtns_read = 0;
          static int debug_multi3_AsyncRtns_write = 0;
          if (isRead) {
              csIntInc(&debug_multi3_AsyncRtns_read, 1, 0);
              printf("multi3_AsyncRtns for read %d\n", debug_multi3_AsyncRtns_read);
          } else {
              csIntInc(&debug_multi3_AsyncRtns_write, 1, 0);
              printf("multi3_AsyncRtns for write %d\n", debug_multi3_AsyncRtns_write);
          }
          fflush(stdout);
#endif

        // (rv == 0) means the I/O operation went async
        return JNI_TRUE;
    } else {
        // (rv < 0) means an error occurred...most times

        // Check for Sync with 0-bytes requested (caller is just checking for data).
        if (bytesRequested == 0  && isRead == 1 && rc == EWOULDBLOCK) {
            // Tell caller that there is currently no data.
            rc = 0;

            // Immediate read was issued for which no data was available.  The RV=1 and RC=EWOULDBLOCK,
            // indicates that the caller said it could NOT be blocked to wait for data.
            if (TraceActive(trc_level_detailed)) {
                   TraceRecord(trc_level_detailed,
                        TP(_TP_SERVER_ASYNCIO_MULTIIO3_AIOCALL_EWOULDBLOCK),
                        "aio_multiIO3, Immediate read, no data available",
                        TRACE_DATA_END_PARMS);
            }
        } else {
            if (TraceActive(trc_level_detailed)) {
                   TraceRecord(trc_level_detailed,
                        TP(_TP_SERVER_ASYNCIO_MULTIIO3_AIOCALL_ERROR),
                        "aio_multiIO3, BPX4AIO error! Return JNI_FALSE",
                        TRACE_DATA_HEX_INT(rc, "rc"),
                        TRACE_DATA_END_PARMS);
            }
        }

        ioev[2] = rc;
        ioev[3] = 0;
        return JNI_FALSE;
    }
}

/**
 * Cancel an asynchronous IO operation
 *
 * Return:      void
 * Class:       com_ibm_io_async_AsyncLibrary
 * Method:      aio_cancel2
 * Signature    (JJ)I
 */
JNIEXPORT jint JNICALL
aio_cancel2(JNIEnv* env, jclass thisClass, jlong handle, jlong id)
{
    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(_TP_SERVER_ASYNCIO_ENTER_CANCEL2),
                    "Entered aio_cancel2()",
                    TRACE_DATA_PTR(handle, "Handle"),
                    TRACE_DATA_HEX_LONG(id, "id"),
                    TRACE_DATA_END_PARMS);
    }

    int rc,rv;
    RegistryToken* token = (RegistryToken*)handle;
    AIO_CancelParms aioCancelParms = {
            .inputToken  = token,
            .callId      = id,
            .rv          = &rv
    };

    // call to mc  get the Server Process Data (PGOO) ptr
    const struct server_authorized_function_stubs* auth_stubs_p =
                 (server_authorized_function_stubs*) getServerAuthorizedFunctionStubs();

    auth_stubs_p->cancelAIO(&aioCancelParms);

    if(rv == 1 || rv == 3){ //AIO_CANCELED (1), AIO_ALLDONE (3)
        rc = 0;
    } else {
        rc = 1;
    }

#ifdef AIOCD_PLO_DEBUG_MSGS
              static int debug_cancel2Completions = 0;
              csIntInc(&debug_cancel2Completions, 1, 0);
              printf("debug_cancel2Completions %d, rv: %d\n", debug_cancel2Completions, rv);
              fflush(stdout);
#endif

    if (TraceActive(trc_level_detailed)) {
                TraceRecord(trc_level_detailed,
                            TP(_TP_SERVER_ASYNCIO_ENTER_CANCEL2),
                            "Exit aio_cancel2()",
                            TRACE_DATA_INT(rv, "rv AIO_CANCELLED(1) / AIO_ALLDONE (3) "),
                            TRACE_DATA_INT(rc, "rc"),
                            TRACE_DATA_END_PARMS);
    }

    return rc;
}

/**
 * Find a message text for a native error code.
 *
 * @param env  the JNI environment reference provided by the JVM.
 * @param thisClass the object instance this method was invoked against.
 * @param errorCode the numeric error code for which a text string is desired.
 * @param javaArray the output byte array that will contain the error code text, if found.
 *
 * @return the size of the text message for this error code. 0 if no message is found
 *
 * Return:      int
 * Class:       com_ibm_io_async_AsyncLibrary
 * Method:      aio_getErrorString
 * Signature    (IB)I
 *
 */
JNIEXPORT jint JNICALL
aio_getErrorString(JNIEnv* env, jclass thisClass, jint errorCode, jbyteArray javaArray)
{
    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(_TP_SERVER_ASYNCIO_ENTER_GETERROR),
                    "Entered aio_getErrorString()",
                    TRACE_DATA_INT(errorCode, "errorCode"),
                    TRACE_DATA_END_PARMS);
    }

    // TODO: - here's where we can define and return error text for any error numbers that we return from
    //           aio_multiIO3()

    return 0;
}

/**
 * Class:     com_ibm_io_async_AsyncLibrary
 * Method:    aio_getioev2
 * Signature: (JIJ)Z
 */
JNIEXPORT jboolean JNICALL
aio_getioev2(
    JNIEnv* env,
    jclass thisClass,
    jlong bufferAddress,
    jint timeout,
    jlong completionPort) {

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(_TP_SERVER_ASYNCIO_ENTER_GETIOEV2),
                    "Entered aio_getioev2()",
                    TRACE_DATA_PTR(bufferAddress, "bufferAddress"),
                    TRACE_DATA_INT(timeout, "timeout value"),
                    TRACE_DATA_HEX_LONG(completionPort, "completionPort"),
                    TRACE_DATA_END_PARMS);
    }


    // The "bufferAddress" passed to us is a native storage address of at least 32 bytes, and the
    // Java code treats it as an array of longs...  so to return information about a completed I/O
    // event, we can treat this pointer like an array of longs and fill in the first 4 slots, as
    // shown here (copied from AsyncLibrary.aio_getioev2() Javadoc):
    //
    //   ioev[0] - the channel identifier (connection info)
    //   ioev[1] - the call identifier
    //   ioev[2] - the error code for a failed IO operation, or 0 if successful
    //   ioev[3] - the number of bytes affected by a successful IO operation
    //
    // This completed IO structure is mapped as IOCDEntry and is used by the aio_getioev3 API as well.

    JNI_try {

        IOCDEntry* singleIOCDE_Array[1] = { (IOCDEntry*) bufferAddress };
        jint completedIOCDEs = 0;


        const struct server_authorized_function_stubs* auth_stubs_p =
            (server_authorized_function_stubs*) getServerAuthorizedFunctionStubs();

        AIO_IoevParms aioIoevParms = {
            .iocde_p       = (struct IOCDEntry**) &singleIOCDE_Array[0],  // Expects an Array of IOCDE ptrs.
            .iocdeMax      = 1,
            .timeout       = timeout,
            .iocdeReturned = &completedIOCDEs
        };

        // ---------------------------------------------------------------------
        // Calling native routine getioev2 to get the completed asyncIO results
        // ---------------------------------------------------------------------
        auth_stubs_p->getioev2(&aioIoevParms);

        if ((*(aioIoevParms.iocde_p))->iocde_ReturnCode == ASYNC_SERVERHARDFAILURE_RETURNCODE) {
            // Build and throw a RuntimeError?  We can NOT.  On a hard failure JVM and LE services
            // are hosed.
            //JNI_throwByName(env,"java/lang/RuntimeException", "Severe server failure detected");

            // Do NOT return to Java...the entire path may be all JIT'd and will just call back down.
            // We need to end the thread now to prevent termination hangs.
            //
            // Beware: using pthread_exit(nn) is generally not a good idea.  It could fool LE to
            // think this is a "normal" thread termination and may lead them to commit a current
            // transaction that may be marked for rollback.  We've seen this problems in WASt.
            pthread_exit((void *)ASYNC_SERVERHARDFAILURE_RETURNCODE);
        } else {
            // Check for a timeout.  For a timeout the java code expects a return value of FALSE.
            if ((*(aioIoevParms.iocde_p))->iocde_ReturnCode == ASNYC_TIMEOUT_RETURNCODE) {
                if (TraceActive(trc_level_detailed)) {
                    TraceRecord(trc_level_detailed,
                                TP(_TP_SERVER_ASYNCIO_GETIOEV2_TIMEOUT),
                                "aio_getioev2: Return from getioev2 with a timeout",
                                TRACE_DATA_INT(completedIOCDEs, "number of IOCDEs returned"),
                                TRACE_DATA_HEX_LONG((*(aioIoevParms.iocde_p))->iocde_ChannelIdentifier, "getioev2 ChannelIdentifier"),
                                TRACE_DATA_HEX_LONG((*(aioIoevParms.iocde_p))->iocde_CallId, "getioev2 CallId"),
                                TRACE_DATA_HEX_LONG((*(aioIoevParms.iocde_p))->iocde_ReturnCode, "getioev2 ReturnCode"),
                                TRACE_DATA_HEX_LONG((*(aioIoevParms.iocde_p))->iocde_BytesAffected, "getioev2 BytesAffected"),
                                TRACE_DATA_END_PARMS);
                }

                // Set timeout indicator for Java
                return JNI_FALSE;
            }
        }

        if (TraceActive(trc_level_detailed)) {
            static int ioev2Completions = 0;
            ioev2Completions++;
            TraceRecord(trc_level_detailed,
                        TP(_TP_SERVER_ASYNCIO_GETIOEV2_AIOCALL_RETURN),
                        "aio_getioev2: Return from getioev2",
                        TRACE_DATA_INT(completedIOCDEs, "number of IOCDEs returned"),
                        TRACE_DATA_HEX_LONG((*(aioIoevParms.iocde_p))->iocde_ChannelIdentifier, "getioev2 ChannelIdentifier"),
                        TRACE_DATA_HEX_LONG((*(aioIoevParms.iocde_p))->iocde_CallId, "getioev2 CallId"),
                        TRACE_DATA_HEX_LONG((*(aioIoevParms.iocde_p))->iocde_ReturnCode, "getioev2 ReturnCode"),
                        TRACE_DATA_HEX_LONG((*(aioIoevParms.iocde_p))->iocde_BytesAffected, "getioev2 BytesAffected"),
                        TRACE_DATA_HEX_INT(ioev2Completions, "ioev2Completions"),
                        TRACE_DATA_END_PARMS);
        }

#ifdef AIOCD_PLO_DEBUG_MSGS
        static int debug_ioev2Completions = 0;
        //debug_ioev2Completions++;
        csIntInc(&debug_ioev2Completions, 1, 0);
        printf("ioev2Completions:%d, rc: %lX, completedIOCDEs:%d\n",
               debug_ioev2Completions, (*(aioIoevParms.iocde_p))->iocde_ReturnCode, completedIOCDEs);
        fflush(stdout);
#endif


        //Note: need to change to return actual token instead of ptr to token when we change the interface
        // depends on what we return in prepare2
        //Note: java side doesn't use the "Channel Identifier", ioev[0], to find the channel/caller instance.  It uses
        // the callid.  The callid is made up of a channel index and Future index which are used to find the associated
        // listener/waiter of the IO.
        //    ioev[0] = *((long*)&connRegistryTokenPtr);
        //
        //    ioev[1] = callId;
        //    ioev[2] = rc;
        //    ioev[3] = rv;

    }
    JNI_catch(env);

    JNI_reThrowAndReturn(env);

    return JNI_TRUE;
}

/**
 * Returns a completion status from an earlier async call, or times out if none available
 * after the given number of milliseconds.
 *
 * Upon successful dequeueing, the ioev array contains for each completed event:
 *  ioev[0] - the channel identifier
 *  ioev[1] - the call identifier
 *  ioev[2] - the error code for a failed IO operation, or 0 if successful
 *  ioev[3] - the number of bytes affected by a successful IO operation
 *
 * Returns:   the number of completed events.
 * Throws:    Throwable (user defined subclass) if the dequeueing failed for a reason other
 *            than a timeout.
 * Class:     com_ibm_io_async_AsyncLibrary
 * Method:    aio_getioev3
 * Signature: ([JIIJ)I
 */
JNIEXPORT jint JNICALL
aio_getioev3(
    JNIEnv* env,
    jclass thisClass,
    jlongArray jlArray,
    jint numBatch,
    jint timeout,
    jlong completionPort
) {

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(_TP_SERVER_ASYNCIO_ENTER_GETIOEV3),
                    "Entered aio_getioev3()",
                    TRACE_DATA_INT(numBatch, "maximum completed events for this call"),
                    TRACE_DATA_INT(timeout, "timeout value"),
                    TRACE_DATA_HEX_LONG(completionPort, "completionPort"),
                    TRACE_DATA_END_PARMS);
    }

    jint completedIOCDEs = 0;

    int handlerTimedout = 0;

    // The "jlArray" passed to us is an array of native addresses that each point to native storage of
    // 32 bytes elements.  Each element is treated by the Java code (ResultHandler) as a completed IO event.
    //
    // The Java code treats each completed IO event as an array of longs...  so to return information
    // about a completed I/O event, we can treat this pointer like an array of longs and fill in the
    // first 4 slots, as shown here (copied from AsyncLibrary.aio_getioev3() Javadoc):
    //
    //   ioev[0] - the channel identifier (connection info)
    //   ioev[1] - the call identifier
    //   ioev[2] - the error code for a failed IO operation, or 0 if successful
    //   ioev[3] - the number of bytes affected by a successful IO operation


    JNI_try {
        jint localNumBatch;
        JNI_GetArrayLength(localNumBatch, env, jlArray, "jlArray is null", 0);
        if (localNumBatch <= 0) {
            // error
            JNI_throwNullPointerException(env, "jlArray is null");
        }

        jlong dynLongArray[localNumBatch];

        JNIGetxxxArrayRegion(Long, env, jlArray, 0, localNumBatch, dynLongArray, "jlArray is null");

        IOCDEntry** multiIOCDE_Array = (IOCDEntry**) dynLongArray;

        if (TraceActive(trc_level_detailed)) {
            TraceRecord(trc_level_detailed,
                        TP(_TP_SERVER_ASYNCIO_GETIOEV3_OUTARRAY),
                        "Entered aio_getioev3()",
                        TRACE_DATA_PTR(jlArray, "jlArray"),
                        TRACE_DATA_RAWDATA(sizeof(dynLongArray), dynLongArray, "jlArray copied storage"),
                        TRACE_DATA_END_PARMS);
        }

        const struct server_authorized_function_stubs* auth_stubs_p =
            (server_authorized_function_stubs*) getServerAuthorizedFunctionStubs();

        AIO_IoevParms aioIoevParms = {
            .iocde_p       = (struct IOCDEntry**) &multiIOCDE_Array[0],  // Expects an Array of IOCDE ptrs.
            .iocdeMax      = numBatch,
            .timeout       = timeout,
            .iocdeReturned = &completedIOCDEs
        };

        // ---------------------------------------------------------------------
        // Calling native routine getioev2 to get the completed asyncIO results
        // ---------------------------------------------------------------------
        auth_stubs_p->getioev2(&aioIoevParms);


        if (multiIOCDE_Array[0]->iocde_ReturnCode == ASYNC_SERVERHARDFAILURE_RETURNCODE) {
            // Build and throw a RuntimeError?  We can NOT.  On a hard failure JVM and LE services
            // are hosed.
            //JNI_throwByName(env,"java/lang/RuntimeException", "Severe server failure detected");

            // Do NOT return to Java...the entire path may be all JIT'd and will just call back down.
            // We need to end the thread now to prevent termination hangs.
            pthread_exit((void *)ASYNC_SERVERHARDFAILURE_RETURNCODE);
        } else {
            // Check for a timeout.  For a timeout the java code expects a return value of 0.
            if (multiIOCDE_Array[0]->iocde_ReturnCode == ASNYC_TIMEOUT_RETURNCODE) {
                if (TraceActive(trc_level_detailed)) {
                    TraceRecord(trc_level_detailed,
                                TP(_TP_SERVER_ASYNCIO_GETIOEV3_GETIOEV2_TIMEOUT),
                                "aio_getioev3: Return from getioev2 with a timeout",
                                TRACE_DATA_INT(completedIOCDEs, "number of IOCDEs returned"),
                                TRACE_DATA_HEX_LONG(multiIOCDE_Array[0]->iocde_ChannelIdentifier, "getioev2 ChannelIdentifier"),
                                TRACE_DATA_HEX_LONG(multiIOCDE_Array[0]->iocde_CallId, "getioev2 CallId"),
                                TRACE_DATA_HEX_LONG(multiIOCDE_Array[0]->iocde_ReturnCode, "getioev2 ReturnCode"),
                                TRACE_DATA_HEX_LONG(multiIOCDE_Array[0]->iocde_BytesAffected, "getioev2 BytesAffected"),
                                TRACE_DATA_END_PARMS);
                }

                // Set timeout indicator for Java
                handlerTimedout = 1;
                completedIOCDEs = 0;
            }
        }

        if (TraceActive(trc_level_detailed)) {
            for (int i = 0; i < completedIOCDEs; i++) {
                TraceRecord(trc_level_detailed,
                            TP(_TP_SERVER_ASYNCIO_GETIOEV3_GETIOEV2_RETURN),
                            "aio_getioev3: Return from getioev2",
                            TRACE_DATA_INT(completedIOCDEs, "number of IOCDEs returned"),
                            TRACE_DATA_INT(i, "IOCDE instance"),
                            TRACE_DATA_HEX_LONG(multiIOCDE_Array[i]->iocde_ChannelIdentifier, "getioev2 ChannelIdentifier"),
                            TRACE_DATA_HEX_LONG(multiIOCDE_Array[i]->iocde_CallId, "getioev2 CallId"),
                            TRACE_DATA_HEX_LONG(multiIOCDE_Array[i]->iocde_ReturnCode, "getioev2 ReturnCode"),
                            TRACE_DATA_HEX_LONG(multiIOCDE_Array[i]->iocde_BytesAffected, "getioev2 BytesAffected"),
                            TRACE_DATA_END_PARMS);
            }
        }

        if (TraceActive(trc_level_detailed)) {
            static int ioev3Completions = 0;
            ioev3Completions += completedIOCDEs;
            TraceRecord(trc_level_detailed,
                        TP(_TP_SERVER_ASYNCIO_GETIOEV3_AIOCALL_RETURN),
                        "aio_getioev3: Return from getioev3",
                        TRACE_DATA_INT(completedIOCDEs, "number of IOCDEs returned"),
                        TRACE_DATA_HEX_INT(ioev3Completions, "ioev3Completions"),
                        TRACE_DATA_END_PARMS);
        }

#ifdef AIOCD_PLO_DEBUG_MSGS
          static int debug_ioev3Completions = 0;
          //debug_ioev3Completions += completedIOCDEs;
          csIntInc(&debug_ioev3Completions, completedIOCDEs, 0);
          printf("ioev3Completions %d\n", debug_ioev3Completions);
          fflush(stdout);
#endif

    }
    JNI_catch(env);

    JNI_reThrowAndReturn(env);

#ifdef AIOCD_PLO_DEBUG_MSGS
    if (completedIOCDEs == 0 && !handlerTimedout) {
        printf("***aio_getioev3: Return from getioev3 with zero completed***\n");
        fflush(stdout);
    }
#endif

    if (TraceActive(trc_level_detailed) && completedIOCDEs==0) {
        TraceRecord(trc_level_detailed,
                    TP(_TP_SERVER_ASYNCIO_GETIOEV3_RETURN_ZERO),
                    "aio_getioev3: Return from getioev3 with zero completed",
                    TRACE_DATA_END_PARMS);
    }

    //Note: need to change to return actual token instead of ptr to token when we change the interface
    // depends on what we return in prepare2
    //Note: java side doesn't use the "Channel Identifier", ioev[0], to find the channel/caller instance.  It uses
    // the callid.  The callid is made up of a channel index and Future index which are used to find the associated
    // listener/waiter of the IO.
    //    ioev[0] = *((long*)&connRegistryTokenPtr);
    //
    //    ioev[1] = callId;
    //    ioev[2] = rc;
    //    ioev[3] = rv;

    return completedIOCDEs;
}

