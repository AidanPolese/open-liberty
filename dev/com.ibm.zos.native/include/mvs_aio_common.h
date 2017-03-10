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

#ifndef MVS_AIO_COMMON_H_
#define MVS_AIO_COMMON_H_

#include "util_registry.h"

/*-------------------------------------------------------------------*/
/* AIO services                                                      */
/*-------------------------------------------------------------------*/
#define CALL_BPX4AIO    1
#define CLEANUP_AIOCB   2
#define AUTHORIZED      0
#define UNAUTHORIZED    1


// NOTE: To enable the diag/debug related code un-comment the
// following #define
//#define AIOCD_PLO_DEBUG

// To enable diag/debug code that issues messages/traces un-comment the
// following #define's
//#define AIOCD_PLO_DEBUG_MSGS

// To enable code that displays diag/debug counts at an interval
// (currently 10,000 BPX4AIO calls) un-comment the following #define
// Note: must have AIOCD_PLO_DEBUG enabled too.
//#define AIOCD_PLO_DEBUG_INTERVAL_MSG


#define AIO_MAX(a,b) (((a) > (b)) ? (a) : (b))


/**
 * Basic IO Vector structure.
 */
typedef struct {
    void* iov_base;
    long iov_len;
} iovec;


/**
 * Parameter structure used by @c aio_multiIO3 routine.
 * @param parms A @c AIO_CallParms.
 *
 */
typedef struct AIO_CallParms AIO_CallParms;
struct AIO_CallParms
{
    RegistryToken*  token;      //!< Input  - channel identifier which is the RegistryToken
    long callId;                //!< Input  - call id
    int  count;                 //!< Input  - Number of Buffers
    int  isRead;                //!< Input  - Read/Write operation
    int  forceQueue;            //!< Input  - Force async
    long long bytesRequested;   //!< Input  - number of bytes requested from IO
    iovec* iov_p;               //!< Input  - I/O vector
    int* AIO_call_rv_p;         //!< Output  - Return value from BPX4AIO call
    int* AIO_call_rc_p;         //!< Output  - Return code from BPX4AIO call
    int* AIO_call_rsn_p;        //!< Output  - Reason code from BPX4AIO call
    int* AIO_aiorv;             //!< Output  - number of bytes of IO completed
};

/**
 * Perform I/O operation.
 * @param parms A @c AIO_CallParms structure containing parameters for calling BPX4AIO.
 *              .
 */
void aio_call(AIO_CallParms* parms);

/**
 * Parameter structure used by the @c aio_prepare2 routine.
 */
typedef struct {
    long long      socketHandle;  //!< Input  - Socket descriptor for the IO operation
    RegistryToken*  outputToken;  //!< Output - A token associated with the aio connection.
} AIO_ConnectionParms;

/**
 * Prepare the given file/socket channel handle for async operations.
 * Initialize the AioConn .
 * @param parms A @c PrepareConnectionParms structure containing socket handle.
 *              .
 */
void prepareConnection(AIO_ConnectionParms* parms);

/**
 * Parameter structure used by the @c aio_init routine.
 */
typedef struct {
    int* returnCode;  //!< Output  - returncode for the init operation
} AIO_InitParms;

/**
 * Initialize the PGOO for AsyncIO
 * @param parms A @c AIO_InitParms structure containing socket handle.
 *              .
 */
void aio_initPGOO(AIO_InitParms* parms);

/**
 * A IO Completed Data entry related to the getIOEV2 and getIOEV3
 * output structure.  This is defined and mapped by the
 * com.ibm.io.async.CompletionKey java class.
 */
typedef struct IOCDEntry {
    long iocde_ChannelIdentifier;                          /* 0x000 */
    long iocde_CallId;                                     /* 0x008 */
    long iocde_ReturnCode;                                 /* 0x010 */
    long iocde_BytesAffected;                              /* 0x018 */
} IOCDEntry;                                               /* 0x020 */

/**
 * Parameter structure used by the @c getioev routines.
 */
typedef struct {
    struct IOCDEntry** iocde_p;           //!< Output  - Array of pointers to IOCDEs
    int iocdeMax;                         //!< Input   - Maximum number of IOCDEs in iocde_p
    int timeout;                          //!< Input   - Time to wait for Completed IO (Milliseconds)
    int* iocdeReturned;                   //!< Output  - Number of returned IOCDEs
} AIO_IoevParms;

#define ASNYC_TIMEOUT_RETURNCODE 102
#define ASYNC_SERVERHARDFAILURE_RETURNCODE 103

/**
 * Pause the thread to process IO and return info after the async IO completed.
 * Process the completed async IO.
 * @param parms A @c AIO_IoevParms structure containing return values.
 *              .
 */
void getioev2(AIO_IoevParms* parms);

/**
 * Parameter structure used by the @c disposeConnection routine.
 */
typedef struct {
    RegistryToken*  inputToken;  //!< Input - A ptr to token associated with the aio connection.
    long* outputHandle;          //!< Output - the original handle referring the channel id
    int* returnCode;             //!< Output - return code
} AIO_DisposeParms;

/**
 * Dispose the Connection
 * @param parms A @c DisposeParms structure containing ptr to Connection Token.
 *
 */
void disposeConnection(AIO_DisposeParms* parms);

/**
 * Parameter structure used by the @c aio_cancel2 routine.
 */
typedef struct {
    RegistryToken*  inputToken;  //!< Input - A token associated with the aio connection.
    long callId;                     //!< Input - call id
    int* rv;                     //!< Output - return value
} AIO_CancelParms;

/**
 * Perform cancel I/O operation.
 * @param parms A @c cancelParms structure containing parameters for calling cancel on BPX4AIO.
 *
 */
void cancelAIO(AIO_CancelParms* parms);

/**
 * Parameter structure used by the @c closeportAIO routine.
 */
typedef struct {
    int* returnCode;             //!< Output - return code
} AIO_CloseportParms;

/**
 * Perform closeport actions for AIO.
 * @param parms A @c AIO_CloseportParms structure containing parameters for calling closeport2 of AIO.
 *
 */
void closeportAIO(AIO_CloseportParms* parms);

/**
 * Parameter structure used by the @c shutdownAIO routine.
 */
typedef struct {
    int* returnCode;             //!< Output - return code
} AIO_ShutdownParms;

/**
 * Perform shutdown actions for AIO.
 * @param parms A @c AIO_ShutdownParms structure containing parameters for calling shutdown of AIO.
 *
 */
void shutdownAIO(AIO_ShutdownParms* parms);

/**
 * Parameter structure used by the @c aioGetSocketDescriptor routine.
 */
typedef struct {
    RegistryToken*  token;  //!< Input - A ptr to token associated with the aio connection.
    long* socketInfo_p;       //!< Output - the original Socket descriptor for the connection.
    int* rc_p;              //!< Output - return code.
} AIO_GetSocketDescriptor;

/**
 * Retrieve the socket descriptor assoicated with a connection.  Used on immediate read request (ie. bytesRequested==0).
 * @param parms A @c AIO_GetSocketDescriptor structure containing parameters for making the call.
 *
 */
void aioGetSocketDescriptor(AIO_GetSocketDescriptor* parms);

#endif /* MVS_AIO_COMMON_H_ */
