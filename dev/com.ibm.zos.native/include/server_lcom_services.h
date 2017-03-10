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
/*
 * server_lcom_services.h
 *
 *  Created on: Jun 4, 2012
 *      Author: ginnick
 */

#ifndef SERVER_LCOM_SERVICES_H_
#define SERVER_LCOM_SERVICES_H_

#include "server_local_comm_api.h"

/*-------------------------------------------------------------------*/
/* Drive Local Comm Initialization                                   */
/*                                                                   */
/* Parms:                                                            */
/*   outrc,outrsn -- OUTPUT, return and reason code.                 */
/*-------------------------------------------------------------------*/
#define LOCAL_COMM_INIT_RC_ABENDED        64
#define LOCAL_COMM_INIT_RC_NOESTAE        65
typedef struct LCOM_InitParms LCOM_InitParms;
struct LCOM_InitParms {
    int  * outRC;
    int  * outRSN;
};
void lcom_init(LCOM_InitParms* parms);

/*-------------------------------------------------------------------*/
/* Drive Local Comm Uninitialization                                 */
/*                                                                   */
/* Parms:                                                            */
/*   outrc,outrsn -- OUTPUT, return and reason codes                 */
/*                                                                   */
/*-------------------------------------------------------------------*/
#define LOCAL_COMM_UNINIT_RC_ABENDED        64
#define LOCAL_COMM_UNINIT_RC_NOESTAE        65
#define LOCAL_COMM_UNINIT_RC_BBGZLOCL_BUSY  66
typedef struct LCOM_UninitParms LCOM_UninitParms;
struct LCOM_UninitParms {
    int  * outRC;
    int  * outRSN;
};
void lcom_uninit(LCOM_UninitParms* parms);


/*-------------------------------------------------------------------*/
/* Grab the current Work Request Queue from BBGZLCOM                 */
/*                                                                   */
/* Parms:                                                            */
/*   otherWorkToDo -- input boolean indicating not to wait.          */
/*   outWRQE_Ptr -- pointer to a list of Work Request Elements       */
/*   outrc,outrsn -- OUTPUT, return and reason codes                 */
/*                                                                   */
/*-------------------------------------------------------------------*/
#define LOCAL_COMM_GETWRQ_RC_ABENDED        64
#define LOCAL_COMM_GETWRQ_RC_NOESTAE        65
typedef struct LCOM_GetWRQParms LCOM_GetWRQParms;
struct LCOM_GetWRQParms {
    long long otherWorkToDo;
    long long* outWRQE_Ptr;
    int  * outRC;
    int  * outRSN;
};
void lcom_getWRQ(LCOM_GetWRQParms* parms);

/*-------------------------------------------------------------------*/
/* Free the previously returned Work Request Queue from BBGZLCOM     */
/*                                                                   */
/* Parms:                                                            */
/*   outrc,outrsn -- OUTPUT, return and reason codes                 */
/*                                                                   */
/*-------------------------------------------------------------------*/
#define LOCAL_COMM_FREEWRQES_RC_ABENDED        64
#define LOCAL_COMM_FREEWRQES_RC_NOESTAE        65
typedef struct LCOM_FreeWRQEsParms LCOM_FreeWRQEsParms;
struct LCOM_FreeWRQEsParms {
    int  * outRC;
    int  * outRSN;
};
void lcom_freeWRQEs(LCOM_FreeWRQEsParms* parms);

/*-------------------------------------------------------------------*/
/* Release the Work Request Queue Listener thread to stop.           */
/*                                                                   */
/* Parms:                                                            */
/*   outrc,outrsn -- OUTPUT, return and reason codes                 */
/*                                                                   */
/*-------------------------------------------------------------------*/
#define LOCAL_COMM_STOPLIST_RC_ABENDED        64
#define LOCAL_COMM_STOPLIST_RC_NOESTAE        65
typedef struct LCOM_StopListeningOnWRQParms LCOM_StopListeningOnWRQParms;
struct LCOM_StopListeningOnWRQParms {
    int  * outRC;
    int  * outRSN;
};
void lcom_stopListeningOnWRQ(LCOM_StopListeningOnWRQParms* parms);

/*-------------------------------------------------------------------*/
/* Drive a connect response to the target connection.                */
/*                                                                   */
/* Parms:                                                            */
/*   outrc,outrsn -- OUTPUT, return and reason codes.                */
/*                                                                   */
/*-------------------------------------------------------------------*/
#define LOCAL_COMM_CONNRESP_RC_ABENDED        64
#define LOCAL_COMM_CONNRESP_RC_NOESTAE        65
typedef struct LCOM_ConnectResponseParms LCOM_ConnectResponseParms;
struct LCOM_ConnectResponseParms {
    OpaqueClientConnectionHandle_t inConnHandle;
    int*               outRC;
    int*               outRSN;
};
void lcom_connectResponse(LCOM_ConnectResponseParms* parms);

/*-------------------------------------------------------------------*/
/* Connect to Clients' shared memory.                                */
/*                                                                   */
/* Parms:                                                            */
/*   inConnHandle,  -- Connection handle.                            */
/*   inBBGZLDAT_p, -- Pointer to client BBGZLDAT.                    */
/*   inBBGZLOCL_p, -- Pointer to client BBGZLOCL.                    */
/*   inSharingUserToken -- User token for accessing client shared    */
/*                    memory objects.                                */
/*   outrc,outrsn -- OUTPUT, return and reason codes.                */
/*                                                                   */
/*-------------------------------------------------------------------*/
#define LOCAL_COMM_CONSHAREDMEM_RC_LSCL_BADEYE    33
#define LOCAL_COMM_CONSHAREDMEM_RC_LOCL_BADEYE    34
#define LOCAL_COMM_CONSHAREDMEM_RC_LDAT_BADEYE    35
#define LOCAL_COMM_CONSHAREDMEM_RC_ABENDED        64
#define LOCAL_COMM_CONSHAREDMEM_RC_NOESTAE        65
typedef struct LCOM_ConnectClientSharedMemoryParms LCOM_ConnectClientSharedMemoryParms;
struct LCOM_ConnectClientSharedMemoryParms {
    OpaqueClientConnectionHandle_t inConnHandle;
    unsigned long long inBBGZLDAT_p;
    unsigned long long inBBGZLOCL_p;
    unsigned long long inSharingUserToken;
    int*               outRC;
    int*               outRSN;
};
void lcom_connectClientSharedMemory(LCOM_ConnectClientSharedMemoryParms* parms);

/*-------------------------------------------------------------------*/
/* Disconnect from Clients' shared memory.                           */
/*                                                                   */
/* Parms:                                                            */
/*   inConnHandle,  -- Connection handle.                            */
/*   inBBGZLDAT_p, -- Pointer to client BBGZLDAT.                    */
/*   inBBGZLOCL_p, -- Pointer to client BBGZLOCL.                    */
/*   inSharingUserToken -- User token for accessing client shared    */
/*                    memory objects.                                */
/*   outrc,outrsn -- OUTPUT, return and reason codes.                */
/*                                                                   */
/*-------------------------------------------------------------------*/
#define LOCAL_COMM_DISSHAREDMEM_RC_LSCL_BADEYE    33
#define LOCAL_COMM_DISSHAREDMEM_RC_LOCL_BADEYE    34
#define LOCAL_COMM_DISSHAREDMEM_RC_LDAT_BADEYE    35
#define LOCAL_COMM_DISCCLNTSHARED_RC_ABENDED_CL   64
#define LOCAL_COMM_DISCCLNTSHARED_RC_NOESTAE      65
#define LOCAL_COMM_DISCCLNTSHARED_RC_ABENDED_DS   66
#define LOCAL_COMM_DISCCLNTSHARED_RC_ABENDED_VE   67
#define LOCAL_COMM_DISCCLNTSHARED_RC_ABENDED_DCC  68
typedef struct LCOM_DisconnectClientSharedMemoryParms LCOM_DisconnectClientSharedMemoryParms;
struct LCOM_DisconnectClientSharedMemoryParms {
    OpaqueClientConnectionHandle_t inConnHandle;
    unsigned long long inBBGZLDAT_p;
    unsigned long long inBBGZLOCL_p;
    unsigned long long inSharingUserToken;
    int*               outRC;
    int*               outRSN;
};
void lcom_disconnectClientSharedMemory(LCOM_DisconnectClientSharedMemoryParms* parms);

/**
 * Structure desribing a data block returned by lcom_read.
 */
typedef struct LCOM_ReadDataBlock {
    void*              dataCellPointer_p; /* Opaque pointer to the BBGZLMSG. */
    char*              data_p; /* Pointer to the start of the data to read. */
    unsigned long long dataSize; /* Size of data to read. */
} LCOM_ReadDataBlock;

/**
 * Structure returned by lcom_read and supplied on lcom_releaseDataMessage which
 * describes the data that is available to the caller.
 */
typedef struct LCOM_AvailableDataVector {
    unsigned int blockCount; /* The number of data blocks in the vector. */
    unsigned int _filler; /* Available for use. */
    unsigned long long totalDataSize; /* The total data size of all blocks. */
    /* LCOM_ReadDataBlock array follows... */
} LCOM_AvailableDataVector;


/*-------------------------------------------------------------------*/
/* Read data from the Inbound data queue                             */
/*                                                                   */
/* Parms:                                                            */
/*   inConnHandle -- BBGZLHDL token.                                 */
/*   outDataVector_p -- Pointer to a pointer area to return the      */
/*                      pointer to a LMSG block array.  The storage  */
/*                      returned is allocated by the authorized code */
/*                      and is freed on the call to                  */
/*                      lcom_releaseDataMessage.                     */
/*   outrc,outrsn -- OUTPUT, return and reason codes.                */
/*                                                                   */
/*-------------------------------------------------------------------*/
#define LOCAL_COMM_READ_RC_PC_FAILED        32
#define LOCAL_COMM_READ_RC_NO_MEMORY        33
#define LOCAL_COMM_READ_RC_RELMSG_FAILED    34
#define LOCAL_COMM_READ_RC_ABENDED          64
#define LOCAL_COMM_READ_RC_NOESTAE          65
typedef struct LCOM_ReadParms LCOM_ReadParms;
struct LCOM_ReadParms {
    OpaqueClientConnectionHandle_t inConnHandle;
    unsigned long long  inForceAsync;
    LCOM_AvailableDataVector** outDataVector_p;
    int*                outRC;
    int*                outRSN;
};
void lcom_read(LCOM_ReadParms* parms);

/*-------------------------------------------------------------------*/
/* Release data read by caller (BBGZLMSG).                           */
/*                                                                   */
/* Parms:                                                            */
/*   inConnHandle -- BBGZLHDL token.                                 */
/*   inDataVector_p -- Pointer to the LMSG array returned by         */
/*                     lcom_read.  This storage will be freed.       */
/*   outrc,outrsn -- OUTPUT, return and reason codes.                */
/*                                                                   */
/*-------------------------------------------------------------------*/
#define LOCAL_COMM_RELLMSG_RC_PC_FAILED   32
#define LOCAL_COMM_RELLMSG_RC_ABENDED     64
#define LOCAL_COMM_RELLMSG_RC_NOESTAE     65
typedef struct LCOM_ReleaseDataMessageParms LCOM_ReleaseDataMessageParms;
struct LCOM_ReleaseDataMessageParms {
    OpaqueClientConnectionHandle_t inConnHandle;
    LCOM_AvailableDataVector*      inDataVector_p;
    int*                outRC;
    int*                outRSN;
};
void lcom_releaseDataMessage(LCOM_ReleaseDataMessageParms* parms);

/*-------------------------------------------------------------------*/
/* Send data from the Inbound data queue                             */
/*                                                                   */
/* Parms:                                                            */
/*   inConnHandle -- Pointer to BBGZLHDL.                            */
/*   inData_p     -- Pointer to data to send.                        */
/*   inDataSize   -- size of data to send.                           */
/*   outrc,outrsn -- OUTPUT, return and reason codes.                */
/*                                                                   */
/*-------------------------------------------------------------------*/
#define LOCAL_COMM_WRITE_RC_PC_FAILED        32
#define LOCAL_COMM_WRITE_RC_NODATA           33
#define LOCAL_COMM_WRITE_RC_ABENDED          64
#define LOCAL_COMM_WRITE_RC_NOESTAE          65
typedef struct LCOM_WriteParms LCOM_WriteParms;
struct LCOM_WriteParms {
    OpaqueClientConnectionHandle_t inConnHandle;
    char*               inData_p;
    unsigned long long  inDataSize;
    int*                outRC;
    int*                outRSN;
};
void lcom_write(LCOM_WriteParms* parms);

/*-------------------------------------------------------------------*/
/* Close a Connection                                                */
/*                                                                   */
/* Parms:                                                            */
/*   inConnHandle -- Pointer to BBGZLHDL.                            */
/*   outrc,outrsn -- OUTPUT, return and reason codes.                */
/*                                                                   */
/*-------------------------------------------------------------------*/
#define LOCAL_COMM_CLOSE_RC_PC_FAILED        32
#define LOCAL_COMM_CLOSE_RC_ABENDED          64
#define LOCAL_COMM_CLOSE_RC_NOESTAE          65
typedef struct LCOM_CloseParms LCOM_CloseParms;
struct LCOM_CloseParms {
    OpaqueClientConnectionHandle_t inConnHandle;
    int*                outRC;
    int*                outRSN;
};
void lcom_close(LCOM_CloseParms* parms);

/*-------------------------------------------------------------------*/
/* Reset the work request queue closure flags                        */
/*                                                                   */
/* Parms:                                                            */
/*   outrc,outrsn -- OUTPUT, return and reason codes                 */
/*                                                                   */
/*-------------------------------------------------------------------*/
#define LOCAL_COMM_INITWRQFLAGS_RC_ABENDED        64
#define LOCAL_COMM_INITWRQFLAGS_RC_NOESTAE        65
typedef struct LCOM_InitWRQFlagsParms LCOM_InitWRQFlagsParms;
struct LCOM_InitWRQFlagsParms {
    int  * outRC;
    int  * outRSN;
};
void lcom_initWRQFlags(LCOM_InitWRQFlagsParms* parms);

#endif /* SERVER_LCOM_SERVICES_H_ */
