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

#include <ctype.h>
#include <ieac.h>
#include <limits.h>
#include <metal.h>
#include <string.h>
#include <stdio.h>
#include <stdlib.h>

#include "include/bpx_ipt.h"
#include "include/common_defines.h"
#include "include/ieantc.h"
#include "include/mvs_abend.h"
#include "include/mvs_aio_services.h"
#include "include/mvs_cell_pool_services.h"
#include "include/mvs_iarv64.h"
#include "include/mvs_plo.h"
#include "include/mvs_stimerm.h"
#include "include/mvs_utils.h"
#include "include/mvs_wto.h"
#include "include/petvet.h"
#include "include/ras_tracing.h"
#include "include/server_process_data.h"
#include "include/server_kernel_common.h"


#define RAS_MODULE_CONST  RAS_MODULE_MVS_AIO_SERVICES

// ----------------------------------------------------------------------------
// Asynchronous I/O service calls that require key 0-7 or supervisor state.
// ----------------------------------------------------------------------------


//---------------------------------------------------------------------
// Error codes.
//---------------------------------------------------------------------
#define MVS_AIOSERVICES_CANCELAIO_CALLID      (RAS_MODULE_MVS_AIO_SERVICES + 1)

#define _TP_MVS_AIOSERVICES_AIOCALL_ENTER           1
#define _TP_MVS_AIOSERVICES_AIOCALL_SINGLE_RW       2
#define _TP_MVS_AIOSERVICES_AIOCALL_READV           3
#define _TP_MVS_AIOSERVICES_AIOCALL_WRITEV          4
#define _TP_MVS_AIOSERVICES_BPX4AIO_ENTER           5
#define _TP_MVS_AIOSERVICES_BPX4AIO_RETURN          6

#define _TP_MVS_AIOSERVICES_PREPARE2_CONNINFO       10
#define _TP_MVS_AIOSERVICES_PREPARE2_REGTOKEN       11
#define _TP_MVS_AIOSERVICES_DISPOSE_ENTER           12
#define _TP_MVS_AIOSERVICES_GETIOEV2_WAITER_STACK   14
#define _TP_MVS_AIOSERVICES_GETIOEV2_BEFORE_PAUSE   15
#define _TP_MVS_AIOSERVICES_GETIOEV2_AFTER_RELEASE  16
#define _TP_MVS_AIOSERVICES_GETIOEV2_AIOCD_ERROR    17
#define _TP_MVS_AIOSERVICES_AIOINIT_ENTER           18
#define _TP_MVS_AIOSERVICES_DISPOSE_CONNECTION      19
#define _TP_MVS_AIOSERVICES_DISPOSE_WITHIO          20
#define _TP_MVS_AIOSERVICES_AIOCALL_CANCELAIO_ENTER 21
#define _TP_MVS_AIOSERVICES_AIOCALL_CANCELAIO_EXIT  22
#define _TP_MVS_AIOSERVICES_GETAIOSRBSTORAGE        23
#define _TP_MVS_AIOSERVICES_BUILDAIOSRBCELLPOOL     24
#define _TP_MVS_AIOSERVICES_GETIOEV2_NO_AIOCD       25
#define _TP_MVS_AIOSERVICES_PUSHHANDLER_PLO_LOOP    26
#define _TP_MVS_AIOSERVICES_PUSHHANDLER_PLO_LOOP2   27
#define _TP_MVS_AIOSERVICES_PUSHHANDLER_PLO_LOOP3   28
#define _TP_MVS_AIOSERVICES_PUSHHANDLER_PLO_LOOP4   29
#define _TP_MVS_AIOSERVICES_PUSHHANDLER_PLO_LOOP5   30
#define _TP_MVS_AIOSERVICES_PUSHHANDLER_PLO_LOOP6   31
#define _TP_MVS_AIOSERVICES_PUSHHANDLER_PLO_LOOP7   32
#define _TP_MVS_AIOSERVICES_PUSHHANDLER_PLO_LOOP8   33
#define _TP_MVS_AIOSERVICES_PUSHHANDLER_PLO_LOOP9   34
#define _TP_MVS_AIOSERVICES_PUSHHANDLER_PLO_LOOP10  35
#define _TP_MVS_AIOSERVICES_PUSHHANDLER_PLO_LOOP11  36
#define _TP_MVS_AIOSERVICES_PUSHHANDLER_PLO_LOOP12  37
#define _TP_MVS_AIOSERVICES_PUSHHANDLER_PLO_LOOP13  38
#define _TP_MVS_AIOSERVICES_PUSHHANDLER_PLO_LOOP14  39
#define _TP_MVS_AIOSERVICES_PUSHHANDLER_PLO_LOOP15  40
#define _TP_MVS_AIOSERVICES_PUSHHANDLER_PLO_LOOP16  41
#define _TP_MVS_AIOSERVICES_PUSHHANDLER_PLO_LOOP17  42
#define _TP_MVS_AIOSERVICES_PUSHHANDLER_PLO_LOOP18  43
#define _TP_MVS_AIOSERVICES_PUSHHANDLER_PLO_LOOP19  44
#define _TP_MVS_AIOSERVICES_AIOSHUTDOWN_ENTER       45
#define _TP_MVS_AIOSERVICES_AIOSHUTDOWN_EXIT        46
#define _TP_MVS_AIOSERVICES_SHUTDOWNHANDLERS_PLO_LOOP1  47
#define _TP_MVS_AIOSERVICES_SHUTDOWNHANDLERS_PLO_LOOP2  48
#define _TP_MVS_AIOSERVICES_SHUTDOWNHANDLERS_PLO_LOOP3  49
#define _TP_MVS_AIOSERVICES_DESTROYAIOELEMENTCELLPOOL   50
#define _TP_MVS_AIOSERVICES_DESTROYAIOCONNCELLPOOL      51
#define _TP_MVS_AIOSERVICES_DESTROYAIOSRBCELLPOOL       52
#define _TP_MVS_AIOSERVICES_AIOINIT_EXIT                53
#define _TP_MVS_AIOSERVICES_DESTROYAIOCELLPOOLS_ENTRY   54
#define _TP_MVS_AIOSERVICES_DESTROYAIOCELLPOOLS_EXIT    55
#define _TP_MVS_AIOSERVICES_AIOCLOSEPORT_ENTER          56
#define _TP_MVS_AIOSERVICES_AIOCLOSEPORT_EXIT           57
#define _TP_MVS_AIOSERVICES_GETSOCKET_DESCR             58
#define _TP_MVS_AIOSERVICES_CANCELAIO_CALLID            59
#define _TP_MVS_AIOSERVICES_FREEAIOCESTORAGECELLPOOLSTORAGE    60
#define _TP_MVS_AIOSERVICES_FREEAIOCONNSTORAGECELLPOOLSTORAGE  61
#define _TP_MVS_AIOSERVICES_CHECKANDEXPANDPOOLS_ENTRY          62
#define _TP_MVS_AIOSERVICES_CHECKANDEXPANDPOOLS_EXIT           63
#define _TP_MVS_AIOSERVICES_CHECKANDEXPANDPOOLS_FAILEDTOEXPAND 64


#define AIO_COMPLETION_ELEMENT_CELL_POOL_NAME  "ZAIOCECP" //!< The name of the aio completion element's cell pool.
#define AIOCONN_CELL_POOL_NAME                 "ZAIOCNCP" //!< The name of the aioconn's cell pool.
#define AIOCONN_CELL_POOL_EXTENT_SIZE          446464      //!< The amount of storage to obtain each time the AIOCONN cell pool expands.
#define AIOCE_CELL_POOL_EXTENT_SIZE            64*1024      //!< The amount of storage to obtain each time the AIO Completion Element cell pool expands.
#define AIOSRB_CELL_POOL_NAME                  "AIOSRBCP" //!< The name of the AIO SRB's cell pool.
#define AIOSRB_CELL_POOL_CELL_SIZE             1048576    //!< AIO SRB cell pool cellsize (1Meg).
#define AIOSRB_CELL_POOL_EXTENT_CELLS          8          //!< AIO SRB number of cells in Extent
#define AIOSRB_CELL_POOL_STORAGE_SIZE_MEG      AIOSRB_CELL_POOL_EXTENT_CELLS*AIOSRB_CELL_POOL_CELL_SIZE //!< AIO SRB cell pool initial allocation and extent size (50Meg).
#define AIO_RELEASE_FORSHUTDOWN                "SHD"      //!< AIO handler shutdown release code.
#define AIO_RELEASE_FORTIMEOUT                 "TMO"      //!< AIO handler released for timeout.


#define CALL_BPX4AIO    1
#define CLEANUP_AIOCB   2
#define MAX_AIOCBS 32768
#define USERKEY8 0x80

// We use this if/when our task-level RESMGR, serverAuthorizedTaskCleanup, drives our
// registered hardFailureRegisteredCleanupRtn routine.  This is done during server startup code
// when a Java thread is started and marked it (std_p->taskFlags.cleanupForHardFailure).
// This call disables trace on the calling thread to avoid hangs in tracing code
// during a hard failure.
extern int disableTraceLevelInServerTaskData(void);

// Handler (getioev2 or getioev3 returns)
 static IOCDEntry SHUTDOWN_IOCDE = { 0, -1, 99, -1 };
 static IOCDEntry OOM_IOCDE = { 0, -1, 100, -1 };
 static IOCDEntry NOPET_IOCDE = { 0, -1, 101, -1 };
 static IOCDEntry TIMEOUT_IOCDE = { 0, -1, ASNYC_TIMEOUT_RETURNCODE, -1};
 static IOCDEntry HARDFAILURE_IOCDE = { 0, -1, ASYNC_SERVERHARDFAILURE_RETURNCODE, -1};

//---------------------------------------------------------------------
// IAsyncProvider.java interface defines additional capabilities of
// the Async provider.
//---------------------------------------------------------------------
// Flag indicating that the provider supports jit buffers on reads.
#define IAsyncProvider_CAP_JIT_BUFFERS 0x00000004
// Provider has implemented the batching APIs
#define IAsyncProvider_CAP_BATCH_IO 0x00000008


// We build a local prototype for BPX4AIO here because we are unable to
// include aio.h in a metal C part.
#pragma linkage(BPX4AIO , OS)
void BPX4AIO(const unsigned int aiocb_length, void* aiocb_p, int* rv_p, int* rc_p, int* rsn_p);

//---------------------------------------------------------------------
// Internal function prototypes
//---------------------------------------------------------------------
long long buildAIOConnCellPool(void);
long long buildAIOElementCellPool(void);
int buildAIOSRBCellPool(AsyncIOCompletionData* aiocd_p);
AsyncIOCompletionData* buildAsyncIOCompletionData(void);
void destroyAIOCellPools(AsyncIOCompletionData* aiocd);
long long destroyAIOSRBCellPool(long long cellPoolToken);
void deregisterAioHardFailureRtn(void);
void enqueueIO_PLO(AsyncIOCompletionData* aiocd, aiocb *aiocb_p);
int expandAIOSRBCellPool(AsyncIOCompletionData* aiocd);
static int getAioConnFromRegistry(RegistryToken* aioconnRegistryToken_p, aioconn** aioconn_pp);
AsyncIOCompletionData* getAsyncIOCompletionData(void);
void handlerTimeoutRtn(void* inParms_p);
void pushHandler_PLO(AsyncIOCompletionData* aiocd, PetVet* mypetvet, AIO_IoevParms *ioev2Parms);
void registerAioHardFailureRtn(void);
void shutdownHandlers_PLO(AsyncIOCompletionData* aiocd_p);


#ifdef AIOCD_PLO_DEBUG
void writeDiagnosticsFromAIOCD(AsyncIOCompletionData* aiocd_p);
#endif

/**
 * AIO exit routine.
 *
 * Note that this routine runs as an SRB.  Currently, tracing isn't
 * supported under an SRB.  The linkage has been modified to no retrieve
 * the TraceLevel setting for performance reasons.
 */

#pragma prolog(aio_exit_routine,"SAIOPRL")
#pragma epilog(aio_exit_routine,"SAIOEPL")
void aio_exit_routine(aiocb *aiocb_p, char *workptr, int *worklen) {
    // Establish Metal C environment, save current R12 to restore (probably not needed)
    void* oldR12_p = getenvfromR12();
    server_process_data* spd = getServerProcessData();
    setenvintoR12(spd->auth_metalc_env_p);

    // Get the Async IO Completion queues pointer
    AsyncIOCompletionData* aiocd = (AsyncIOCompletionData*) spd->asyncio_completion_data_p;

    // build the queueElement with the aiorc, aiorv  and callId values from aiocb structure and address of the Token
    enqueueIO_PLO(aiocd, aiocb_p);

    // Restore R12 (probably not needed)
    setenvintoR12(oldR12_p);
}

static int csSetInt(int* intTarget_Ptr, int newInt)
{
  int oldInt;

  oldInt = *intTarget_Ptr;
  do {
  } while(cs((cs_t *) &oldInt,
             (cs_t *) intTarget_Ptr,
             (cs_t)   newInt
             ));

  return newInt;

} // end, csIntInc
static int csIntInc(int* intTarget_Ptr, int max)
{
  int oldInt, newInt;

  if (max == 0)
      max = INT_MAX;

  oldInt = *intTarget_Ptr;
  do
  {
    newInt = oldInt + 1;
    if (newInt == max) {
        newInt = 0;
    }
  } while(cs((cs_t *) &oldInt,
             (cs_t *) intTarget_Ptr,
             (cs_t)   newInt
             ));

  return newInt;

} // end, csIntInc
static int csIntDec(int* intTarget_Ptr)
{

  int oldInt, newInt;

  oldInt = *intTarget_Ptr;
  do
  {
    newInt = oldInt - 1;
  } while(cs((cs_t *) &oldInt,
             (cs_t *) intTarget_Ptr,
             (cs_t)   newInt
             ));

  return newInt;
} // end, csIntDec

void aio_call (AIO_CallParms* parms) {
    // input parms contain start of IO buffer, number of buffers to read, bytes requested, read or write, forcequeue to force asyncIO,
    // from the token ptr, do registry lookup to get aioconn, then get the aiocb
    // based on read/write, set the read/writeCallId, set the aio_cmd
    // configure AIOCB for a single buffer read or write ioev[6] and ioev[7]
    // do the above 2 steps for multiread/multiwrite
    // call BPX4AIO
    // based on return sync/async , return to LE C, the individual values

    int numOfBuff, ioType;
    RegistryToken* callerToken_p;  // key 8 token ptr
    RegistryToken token;           // actual token in key 2
    RegistryToken* token_p = &token;
    aiocb* multiIocb;
    aioconn* local_aioconn_p;
    int rc=0;
    int rsn=0;
    int rv=0;
    iovec* iov;

    numOfBuff = parms->count;
    ioType    = parms->isRead;
    int buffLen = numOfBuff*2;

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
        TP(_TP_MVS_AIOSERVICES_AIOCALL_ENTER),
        "aio_call:  parameter values passed from LE C",
        TRACE_DATA_INT(numOfBuff, "numOfBuff"),
        TRACE_DATA_INT(buffLen, "Calculated Length of IOBuffer"),
        TRACE_DATA_INT(ioType, "ioType (1=READ/0=WRITE)"),
        TRACE_DATA_INT(parms->forceQueue, "forceQueue"),
        TRACE_DATA_LONG(parms->bytesRequested, "bytesRequested"),
        TRACE_DATA_END_PARMS);
    }

    //Note:  assuming this value is ptr to token, this needs to be changed later when interface changes to take the entire token
    // first entry(8 bytes) of ioBuffer points to key 8 token ptr
    callerToken_p = (RegistryToken*)parms->token;
    // move the token (key8) into key 2
    memcpy_sk(token_p, callerToken_p, sizeof(*token_p), 8);

    // Validate the token using cell pool services and get AIOCONN from registry
    rc = getAioConnFromRegistry(token_p, &local_aioconn_p);
    if (rc == 0) {

        // save the iov entries into aioconn cell
        if (ioType == 0) { // write
            memcpy_sk(local_aioconn_p->writeIovec_p, parms->iov_p, buffLen * sizeof(long), 8);
            iov = (iovec*)local_aioconn_p->writeIovec_p;
        } else { // read
            memcpy_sk(local_aioconn_p->readIovec_p, parms->iov_p, buffLen * sizeof(long), 8);
            iov = (iovec*)local_aioconn_p->readIovec_p;
        }

        if (numOfBuff == 1) {
            if (ioType == 0) { // single write
                multiIocb = local_aioconn_p->writeAiocb_p;
                local_aioconn_p->writeCallId = parms->callId;
                multiIocb->aiocmd = aio_write;

            } else { // single read
                multiIocb = local_aioconn_p->readAiocb_p;
                local_aioconn_p->readCallId = parms->callId;
                multiIocb->aiocmd = aio_read;
            }
            // configure AIOCB for a single buffer read or write
            multiIocb->aiobuffptr64 = (void*) iov[0].iov_base;       // aio_buf
            multiIocb->aiobuffsize =  (int) iov[0].iov_len ;  //aio_nbytes
            if (TraceActive(trc_level_detailed)) {
                TraceRecord(trc_level_detailed,
                            TP(_TP_MVS_AIOSERVICES_AIOCALL_SINGLE_RW),
                            "aio_multiIO3:  Single READ/WRITE request",
                            TRACE_DATA_PTR(multiIocb, "AIOCB multiIocb"),
                            TRACE_DATA_INT(multiIocb->aiocmd, "aiocmd"),
                            TRACE_DATA_INT(ioType, "ioType (1=READ/0=WRITE)"),
                            TRACE_DATA_HEX_LONG(parms->callId, "callid"),
                            TRACE_DATA_INT(multiIocb->aiobuffsize, "aiobuffsize"),
                            TRACE_DATA_PTR(multiIocb->aiobuffptr64, "aiobuffptr64"),
                            TRACE_DATA_END_PARMS);
            }
        } else {
            if (ioType == 0) { // multi write
                multiIocb = local_aioconn_p->writeAiocb_p;
                local_aioconn_p->writeCallId = parms->callId;
                multiIocb->aiocmd = aio_writev;


                multiIocb->aiobuffptr64 = (void*) local_aioconn_p->writeIovec_p; //aio_buf
                multiIocb->aiobuffsize = numOfBuff;  //aio_nbytes

                if (TraceActive(trc_level_detailed)) {
                    TraceRecord(trc_level_detailed,
                                TP(_TP_MVS_AIOSERVICES_AIOCALL_WRITEV),
                                "aio_multiIO3: WRITEV request",
                                TRACE_DATA_PTR(multiIocb, "AIOCB multiIocb"),
                                TRACE_DATA_INT(multiIocb->aiocmd, "aiocmd"),
                                TRACE_DATA_INT(local_aioconn_p->writeIovecLength, "local_aioconn_p->writeIovecLength"),
                                TRACE_DATA_PTR(multiIocb->aiobuffptr64, "AIOCB buffptr64(Iovec_p)"),
                                TRACE_DATA_INT(multiIocb->aiobuffsize, "AIOCB aiobuffsize"),
                                TRACE_DATA_RAWDATA(BBGZ_min(multiIocb->aiobuffsize, 4096),
                                                   multiIocb->aiobuffptr64,
                                                   "AIO buffer contents (max 4096 bytes)"),
                                                   TRACE_DATA_END_PARMS);
                }
            } else { // multi read
                multiIocb = local_aioconn_p->readAiocb_p;
                local_aioconn_p->readCallId = parms->callId;
                multiIocb->aiocmd = aio_readv;

                multiIocb->aiobuffptr64 = (void*) local_aioconn_p->readIovec_p; //(void *) aio_buf
                multiIocb->aiobuffsize = numOfBuff; //aio_nbytes

                if (TraceActive(trc_level_detailed)) {
                    TraceRecord(trc_level_detailed,
                                TP(_TP_MVS_AIOSERVICES_AIOCALL_READV),
                                "aio_multiIO3: READV request",
                                TRACE_DATA_PTR(multiIocb, "AIOCB multiIocb"),
                                TRACE_DATA_INT(multiIocb->aiocmd, "aiocmd"),
                                TRACE_DATA_INT(local_aioconn_p->readIovecLength, "local_aioconn_p->readIovecLength"),
                                TRACE_DATA_PTR(multiIocb->aiobuffptr64, "AIOCB buffptr64(Iovec_p)"),
                                TRACE_DATA_INT(multiIocb->aiobuffsize, "AIOCB aiobuffsize"),
                                TRACE_DATA_RAWDATA(BBGZ_min(multiIocb->aiobuffsize, 4096),
                                                   multiIocb->aiobuffptr64,
                                                   "AIO buffer contents (max 4096 bytes)"),
                                                   TRACE_DATA_END_PARMS);
                }
            }
        }

        // Check and set Force option and Immediate Read requests.
        multiIocb->aiocflags = 0;
        if (parms->forceQueue == 0) {
            if ((parms->bytesRequested == 0) && (parms->isRead == 1)) {
                // Immediate read request
                multiIocb->aiocflags = 0 | aiosync;
            } else {
                multiIocb->aiocflags = 0 | aiook2compimd;  //AIO_OK2COMPIMD
            }
        }

        // Support for Immediate read request (bytesRequested==0).  It would have been more
        // convenient to issue an IOCTL call here, in authorized code, rather than having the LE
        // code detect to issue it prior to calling this method.  However, Metal C does not have
        // an IOCTL library function.
        //
        // I investigated the assembler API to drive IOCTL (ESASMI).  Turns out we can not use it
        // if we drive USS socket services in the same process (we do and I am sure Java does as well).

        // Now call the BPX4AIO
        multiIocb->aioexitptr64 = (void *)&aio_exit_routine;  // aio_exitptr

        // Calling z/OS USS BPX4AIO service here.  We call this routine and
        // specify that we are passing the request buffer in user key (8) and
        // getting the response buffer also in key 8.  This keeps us from having
        // to make a copy of this buffer in this program (which is running in key 2
        // supervisor).  The code that sets this up is in our  caller, where
        // the aio flags are setup. The byte aio_flags2 has AIO_USERKEY (0x80) and
        // AIO_USEUSERKEY (0x08) specified.
        if (TraceActive(trc_level_detailed)) {
            TraceRecord(trc_level_detailed,
                        TP(_TP_MVS_AIOSERVICES_BPX4AIO_ENTER),
                        "Parameter list to BPX4AIO call",
                        TRACE_DATA_INT(sizeof(aiocb), "aiocb_length"),
                        TRACE_DATA_PTR(multiIocb->aioexitptr64, "aio_exit_routine address"),
                        TRACE_DATA_END_PARMS);
        }
        BPX4AIO(sizeof(aiocb), multiIocb, &rv, &rc, &rsn);

        if (TraceActive(trc_level_detailed)) {
            TraceRecord(trc_level_detailed,
                        TP(_TP_MVS_AIOSERVICES_BPX4AIO_RETURN),
                        "Back from BPX4AIO call",
                        TRACE_DATA_STRING(((ioType== 1)? "READ": "WRITE"), "ioType"),
                        TRACE_DATA_HEX_INT(multiIocb->aiocflags,"multiIocb->aiocflags"),
                        TRACE_DATA_HEX_INT(multiIocb->aiocflags2,"multiIocb->aiocflags2"),
                        TRACE_DATA_HEX_INT(rc, "RC"),
                        TRACE_DATA_HEX_INT(rsn, "RSN"),
                        TRACE_DATA_HEX_INT(rv, "RV async(0) / sync(1)"),
                        TRACE_DATA_INT(multiIocb->aiorv, "bytes completed"),
                        TRACE_DATA_RAWDATA(BBGZ_min(((rv==1 && numOfBuff==1)?multiIocb->aiorv:0), 4096),
                                           multiIocb->aiobuffptr64,
                                           "AIO buffer contents (max 4096 bytes)"),
                        TRACE_DATA_RAWDATA(BBGZ_min(((rv==1 && numOfBuff>1)?((iovec*)multiIocb->aiobuffptr64)[0].iov_len:0), 4096),
                                           ((iovec*)multiIocb->aiobuffptr64)[0].iov_base,
                                           "AIO buffer contents (max 4096 bytes, 1st buff)"),
                        TRACE_DATA_RAWDATA(BBGZ_min(((rv==1 && numOfBuff>1)?((iovec*)multiIocb->aiobuffptr64)[1].iov_len:0), 4096),
                                           ((iovec*)multiIocb->aiobuffptr64)[1].iov_base,
                                           "AIO buffer contents (max 4096 bytes, 2nd buff)"),
                        TRACE_DATA_END_PARMS);
        }

        // Use copy with destination key (MVCDK) to copy return data back to key 8
        memcpy_dk(parms->AIO_call_rc_p,&rc,sizeof(int),8);
        memcpy_dk(parms->AIO_call_rsn_p,&rsn,sizeof(int),8);
        memcpy_dk(parms->AIO_call_rv_p,&rv,sizeof(int),8);

        if (rv == 1 ) { // Call completed synchronous
            // Copy the number of bytes in aiorv to call (key8)
            memcpy_dk(parms->AIO_aiorv, &(multiIocb->aiorv), sizeof(int),8);

            // Reset outstanding callid
            if (ioType == 0) {
                local_aioconn_p->writeCallId = -1;

            } else {
                local_aioconn_p->readCallId = -1;
            }
        }

        //unlock and decrement the usecount of the token
        registrySetUnused(token_p, TRUE);
    } else {
        rv = -1;
        // rc contains the validation failure return code.
        memcpy_dk(parms->AIO_call_rc_p, &rc, sizeof(int), 8);
        memcpy_dk(parms->AIO_call_rsn_p, &rsn, sizeof(int), 8);
        memcpy_dk(parms->AIO_call_rv_p, &rv, sizeof(int), 8);
    }

#ifdef AIOCD_PLO_DEBUG_INTERVAL_MSG
    // PLO diagnostics--start
    AsyncIOCompletionData* aiocd_p = getAsyncIOCompletionData();

    if(aiocd_p != NULL) {
        int displayInterval = csIntInc(&aiocd_p->aiodebug_data.displayInterval, 10000);
        if (displayInterval == 0) {  // Hit max and flipped to 0.
            writeDiagnosticsFromAIOCD(aiocd_p);
        }
    }
    // PLO diagnostic s end
#endif

}

/**
 * Gets a AIO Connection token from the native registry.
 *
 * @param aioconnRegistryToken_p A pointer to the registry token used to look up the AioConn token.
 * @param aioconnToken_pp A pointer to pointer which contains the address of the AioConn token ptr, which will be filled in.
 *
 * @return The return code from registry services or validation failures.  0 if success.
 */
static int getAioConnFromRegistry(RegistryToken* aioconnRegistryToken_p, aioconn** aioconn_pp) {
    RegistryDataArea dataArea;

    // registryGetAndSetUsed drives incrementUseCount() which verifies that the RegistryElement is within the
    // Registry's cellpool.
    //
    //Note: is this a performance killer, doing verify in every io call.
    //Note: the unlock and decrement RegistryToken happens on callers control (ex. after the IO completes in aio_call()).

    int localRC = registryGetAndSetUsed(aioconnRegistryToken_p, &dataArea);
    if (localRC == 0) {
        // Return the aioconn ptr to caller
        memcpy(aioconn_pp, &dataArea, sizeof(aioconn_pp));
    }

    return localRC;
}

/**
 * Adds a AIO Connection token to the native registry.
 *
 * @param aioconnToken The AIOConn token to add to the registry.
 * @param aioconnRegistryToken_p A pointer to the registry token which will be
 *                           filled in and used to look up the AIOConn token in
 *                           the registry.
 *
 * @return The return code from the registry service.  0 indicates success.
 */
static int addAioConnTokenToRegistry(aioconn* aioconn_p, RegistryToken* aioconnRegistryToken_p) {
    RegistryDataType dataType = AIOCONN;
    RegistryDataArea dataArea;
    memset(&dataArea, 0, sizeof(dataArea));
    // The size of the RegistryDataArea is only 36 bytes, aioconn structure does not fit within it.
    // Put the address of aioconn into the RegistryDataArea instead of the entire aioconn object.
    memcpy(&dataArea, &aioconn_p, sizeof(aioconn_p));
    return registryPut(dataType, &dataArea, aioconnRegistryToken_p);
}

void setCompletionDataInPGOO(server_process_data* spd, AsyncIOCompletionData* aiocd) {
    spd->asyncio_completion_data_p = aiocd;
}

AsyncIOCompletionData* buildAsyncIOCompletionData() {
    server_process_data* spd = getServerProcessData();
    AsyncIOCompletionData* aiocd = (AsyncIOCompletionData*) spd->asyncio_completion_data_p;

    if (aiocd == NULL) {
       // Create the AsyncIOCompletionData.
       aiocd = (AsyncIOCompletionData*) malloc(sizeof(AsyncIOCompletionData));
       if (aiocd !=NULL) {
           memset(aiocd, 0, sizeof(AsyncIOCompletionData));

           // Initialize the QueHeader and HandlerWaiterStackHeader
           initializeQueue(&(aiocd->asyncIOCompletionQ));
           // InitializeStack(&(aiocd->handlerWaiterStack));
           aiocd->handlerWaiterStack.stack_header.first = NULL;

           // Get an owning TTOKEN for storage
           if (getIPT_TToken(&aiocd->aio_TToken) != 0) {
               getJobstepTToken(&aiocd->aio_TToken);
           }

           // Set anchor of CompletionData into PGOO.
           setCompletionDataInPGOO(spd, aiocd);
       }
    }
    return aiocd;
}

// ---------------------------------------------------------------------
// getAsyncIOCompletionData
//
// ---------------------------------------------------------------------
AsyncIOCompletionData* getAsyncIOCompletionData() {

    server_process_data* spd = getServerProcessData();
    AsyncIOCompletionData* aiocd = (AsyncIOCompletionData*) spd->asyncio_completion_data_p;

    return aiocd;
}

// Note: there are explicit instructions for how to delete the cell pool storage by 
// passed storage_type in mvs_cell_pool_services.h in the prolog for freeCellPoolStorage_t definition.
void* getAIOCEStorage(long long* size_p, long long cell_pool_id) {
    void *storage = (void*) malloc(AIOCE_CELL_POOL_EXTENT_SIZE);
    if (storage != NULL) {
        *size_p = AIOCE_CELL_POOL_EXTENT_SIZE;
    }

#ifdef AIOCD_PLO_DEBUG_MSGS
          char zbuf[150];
          snprintf(zbuf, sizeof(zbuf), "getAIOCEStorage  storage: %p size: %d",
                   storage, AIOCE_CELL_POOL_EXTENT_SIZE);
          write_to_programmer(zbuf);
#endif
    return storage;
}
static void freeAIOCEStorageCellPoolStorage(unsigned char storageType, void* storage_p, long long id) {
    int type = storageType;
    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(_TP_MVS_AIOSERVICES_FREEAIOCESTORAGECELLPOOLSTORAGE),
                    "freeAIOCEStorageCellPoolStorage:  ",
                    TRACE_DATA_HEX_LONG(id,"cellpool id"),
                    TRACE_DATA_PTR(storage_p, "storage_p"),
                    TRACE_DATA_INT(type, "storageType in integer field"),
                    TRACE_DATA_END_PARMS);
    }

    if (storage_p != NULL) {
        if ((storageType == CELL_POOL_ANCHOR_STORAGE_TYPE) ||
            (storageType == CELL_POOL_CELL_STORAGE_TYPE)) {
            free(storage_p);
        }
    }
}

void* getAIOCONNStorage(long long* size_p, long long cell_pool_id) {
    void *storage = (void*) malloc(AIOCONN_CELL_POOL_EXTENT_SIZE);
    if (storage != NULL) {
        *size_p = AIOCONN_CELL_POOL_EXTENT_SIZE;
    }

#ifdef AIOCD_PLO_DEBUG_MSGS
          char zbuf[150];
          snprintf(zbuf, sizeof(zbuf), "getAIOCONNStorage  storage: %p size: %d",
                   storage, AIOCONN_CELL_POOL_EXTENT_SIZE);
          write_to_programmer(zbuf);
#endif
    return storage;
}
static void freeAIOCONNStorageCellPoolStorage(unsigned char storageType, void* storage_p, long long id) {
    int type = storageType;
    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(_TP_MVS_AIOSERVICES_FREEAIOCONNSTORAGECELLPOOLSTORAGE),
                    "freeAIOCONNStorageCellPoolStorage:  ",
                    TRACE_DATA_HEX_LONG(id,"cellpool id"),
                    TRACE_DATA_PTR(storage_p, "storage_p"),
                    TRACE_DATA_INT(type, "storageType in integer field"),
                    TRACE_DATA_END_PARMS);
    }

    if (storage_p != NULL) {
        if ((storageType == CELL_POOL_ANCHOR_STORAGE_TYPE) ||
            (storageType == CELL_POOL_CELL_STORAGE_TYPE)) {
            free(storage_p);
        }
    }
}
long long buildAIOElementCellPool() {
    buildCellPoolFlags flags;
    memset(&flags, 0, sizeof(flags));
    flags.autoGrowCellPool = 1;
    flags.skipInitialCellAllocation = 1;
    long long size = computeCellPoolStorageRequirement(0, 0);
    void* initialStorage = malloc(size);

    // CompletionStackElement and CompletionQElement share this cellpool.
    long long cellPoolToken = buildCellPool(initialStorage,
                                            size,
                                            AIO_MAX(sizeof(CompletionQElement),sizeof(CompletionStackElement)),
                                            AIO_COMPLETION_ELEMENT_CELL_POOL_NAME,
                                            flags);
    setCellPoolAutoGrowFunction(cellPoolToken, &(getAIOCEStorage));

    return cellPoolToken;
}
long long destroyAIOElementCellPool(long long cellPoolToken) {

    destroyCellPool(cellPoolToken, freeAIOCEStorageCellPoolStorage);

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(_TP_MVS_AIOSERVICES_DESTROYAIOELEMENTCELLPOOL),
                    "destroyAIOElementCellPool: after destroyCellPool ",
                    TRACE_DATA_HEX_LONG(cellPoolToken,"cellpooltoken"),
                    TRACE_DATA_END_PARMS);
    }

    return cellPoolToken;
}

long long buildAIOConnCellPool() {
    buildCellPoolFlags flags;
    memset(&flags, 0, sizeof(flags));
    flags.autoGrowCellPool = 1;
    flags.skipInitialCellAllocation = 1;
    long long size = computeCellPoolStorageRequirement(0, 0);
    void* initialStorage = malloc(size);

    // Aioconn and aiocb(read and write aiocb) together will take 664 bytes in size
    // include read and write iovec vector sizes(120 for each) as part of the cell
    long long cellSize = sizeof(aioconn) + 2*sizeof(aiocb) + 240*sizeof(iovec);
    long long cellPoolToken = buildCellPool(initialStorage, size, cellSize, AIOCONN_CELL_POOL_NAME, flags);
    setCellPoolAutoGrowFunction(cellPoolToken, &(getAIOCONNStorage));

    return cellPoolToken;
}

long long destroyAIOConnCellPool(long long cellPoolToken) {

    destroyCellPool(cellPoolToken, freeAIOCONNStorageCellPoolStorage);

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(_TP_MVS_AIOSERVICES_DESTROYAIOCONNCELLPOOL),
                    "destroyAIOConnCellPool: after destroyCellPool ",
                    TRACE_DATA_HEX_LONG(cellPoolToken,"cellpooltoken"),
                    TRACE_DATA_END_PARMS);
    }

    return cellPoolToken;
}

void* getAIOSRBExtentStorage(AsyncIOCompletionData* aiocd, long long* size_p) {
    int rc = 0;
    int rsn = 0;
    int totalSegmentsToAllocate = AIOSRB_CELL_POOL_EXTENT_CELLS;

    void* storage = (void*)obtain_iarv64(totalSegmentsToAllocate, 0, &aiocd->aio_TToken, &rc, &rsn);
    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(_TP_MVS_AIOSERVICES_GETAIOSRBSTORAGE),
                    "getAIOSRBExtentStorage: ",
                    TRACE_DATA_PTR(storage,"storage"),
                    TRACE_DATA_END_PARMS);
    }
    if (storage != NULL) {
        *size_p = AIOSRB_CELL_POOL_STORAGE_SIZE_MEG;
    }

#ifdef AIOCD_PLO_DEBUG_MSGS
          char zbuf[150];
          snprintf(zbuf, sizeof(zbuf), "getAIOSRBExtentStorage storage: %p size: %ld",
                   storage, *size_p);
          write_to_programmer(zbuf);
#endif

    return storage;
}

/**
 * Function used to free the AIO cell pool.  This is called by cell pool
 * services.
 *
 * @param storageType The type of storage being freed.  Either cell storage,
 *                    extent storage, or anchor storage.
 * @param storage_p A pointer to the storage being freed.
 * @param id The cell pool owning the storage being freed.  We use this to obtain the
 *           TToken of the owner of the IARV64 storage, which was set in the cell
 *           pool user data just before this call.
 */
static void freeAIOSRBStorageCellPoolStorage(unsigned char storageType, void* storage_p, long long id) {
    if (storage_p != NULL) {

        if ((storageType == CELL_POOL_ANCHOR_STORAGE_TYPE) ||
            (storageType == CELL_POOL_EXTENT_STORAGE_TYPE)) {
            free(storage_p);
        }

        // -------------------------------------------------------------------
        // The cells were allocated using IARV64 on a megabyte boundary.
        // -------------------------------------------------------------------
        if (storageType == CELL_POOL_CELL_STORAGE_TYPE) {
            int iarvRC = 0, iarvRSN = 0;
            TToken* storageOwner_p = (TToken*) getCellPoolUserData(id);
            if (storageOwner_p != NULL) {
                release_iarv64(storage_p, storageOwner_p, &iarvRC, &iarvRSN);
            }
        }
    }
}


int buildAIOSRBCellPool(AsyncIOCompletionData* aiocd) {
    long long size = computeCellPoolStorageRequirement(0, 0);
    void* initialStorage = malloc(size);  // Storage for Anchor
    int   rc = 0;

    buildCellPoolFlags flags;
    memset(&flags, 0, sizeof(flags));
    flags.autoGrowCellPool = 0;
    flags.skipInitialCellAllocation = 1;
    long cellsize = AIOSRB_CELL_POOL_CELL_SIZE;  // 1M
    // Each cell is 1M for the dynamic area usage.
    long long cellPoolToken = buildCellPool(initialStorage, size, cellsize, AIOSRB_CELL_POOL_NAME, flags);

    aiocd->aiosrb_cellpool_id = cellPoolToken;

    // Build the first extent and with cell storage.
    if (expandAIOSRBCellPool(aiocd) != 0) {
        // Failed to get initial extent.  Free what we have so far and return error.
        destroyAIOSRBCellPool(aiocd->aiosrb_cellpool_id);

        aiocd->aiosrb_cellpool_id = 0;
        rc = -1;
    }

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(_TP_MVS_AIOSERVICES_BUILDAIOSRBCELLPOOL),
                    "buildAIOSRBCellPool: after buildAIOSRBcellpool ",
                    TRACE_DATA_HEX_LONG(cellPoolToken,"cellpooltoken"),
                    TRACE_DATA_HEX_INT(rc, "return code"),
                    TRACE_DATA_END_PARMS);
    }
    return rc;
}

long long destroyAIOSRBCellPool(long long cellPoolToken) {

    destroyCellPool(cellPoolToken, freeAIOSRBStorageCellPoolStorage);

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(_TP_MVS_AIOSERVICES_DESTROYAIOSRBCELLPOOL),
                    "destroyAIOSRBCellPool: after destroyCellPool ",
                    TRACE_DATA_HEX_LONG(cellPoolToken,"cellpooltoken"),
                    TRACE_DATA_END_PARMS);
    }

    return cellPoolToken;
}

int expandAIOSRBCellPool(AsyncIOCompletionData* aiocd) {
    // Extents are malloc and the cell storage is IARV64.
    long  numCells  = AIOSRB_CELL_POOL_EXTENT_CELLS;
    long long size  = computeCellPoolExtentStorageRequirement(numCells);
    void* extentAddr_p = malloc(size);  // Storage for Extent
    long long extentLen = size;
    int   rc        = 0;

    void* cellAddr_p = getAIOSRBExtentStorage(aiocd, &size);
    if (cellAddr_p != NULL) {
        long long cellLen = size;
        growCellPool(aiocd->aiosrb_cellpool_id, numCells, extentAddr_p, extentLen, cellAddr_p, cellLen);
    } else {
        free(extentAddr_p);
        rc = -1;
    }
    return rc;
}


int buildAIOCellPools(AsyncIOCompletionData* aiocd) {
    int rc = 0;

    // Build the cellpools for aioconn and the completion elements
    // aioconn cell includes aioconn and the aiocb objects(read and write), allocate for maybe 100 connections
    // completion cell includes StackElement/QueueElement and IOCDEntry
    long long aioconn_cpool_id = buildAIOConnCellPool();
    long long aioelement_cpool_id = buildAIOElementCellPool();
    // save the aioconn_cellpool_id and the completion element cell pool (not in pgoo)
    aiocd->aioconn_cellpool_id = aioconn_cpool_id;
    aiocd->aioelement_cellpool_id = aioelement_cpool_id;

    // Build a cell pool for the SRB exit routine to be used in the epilog/prolog
    if (buildAIOSRBCellPool(aiocd) != 0) {
        destroyAIOCellPools(aiocd);
        rc = -1;
    }

    return rc;
}

void destroyAIOCellPools(AsyncIOCompletionData* aiocd) {
    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(_TP_MVS_AIOSERVICES_DESTROYAIOCELLPOOLS_ENTRY),
                    "destroyAIOCellPools: Entry",
                    TRACE_DATA_END_PARMS);
    }

    // Release cell pool for the SRB exit routine used in the epilog/prolog
    if (aiocd->aiosrb_cellpool_id) {
        destroyAIOSRBCellPool(aiocd->aiosrb_cellpool_id);
        aiocd->aiosrb_cellpool_id = 0;
    }

    // Release cell pool for the completion elements / handler elements
    if (aiocd->aioelement_cellpool_id) {
        destroyAIOElementCellPool(aiocd->aioelement_cellpool_id);
        aiocd->aioelement_cellpool_id = 0;
    }

    // Release cell pool used for Connections
    if (aiocd->aioconn_cellpool_id) {
        destroyAIOConnCellPool(aiocd->aioconn_cellpool_id);
        aiocd->aioconn_cellpool_id = 0;
    }

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(_TP_MVS_AIOSERVICES_DESTROYAIOCELLPOOLS_EXIT),
                    "destroyAIOCellPools: Exit",
                    TRACE_DATA_END_PARMS);
    }
}

void checkAndExpandPools(AsyncIOCompletionData* aiocd) {
    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(_TP_MVS_AIOSERVICES_CHECKANDEXPANDPOOLS_ENTRY),
                    "checkAndExpandPools: Entry",
                    TRACE_DATA_HEX_INT(*((int*)&aiocd->aiocdFlags), "aiocd->aiocdFlags"),
                    TRACE_DATA_END_PARMS);
    }

    int expand = 0;

    // Is the "need to expand SRB cellpool" flag on?
    if (aiocd->aiocdFlags.aiocdFlagsExpand_aiosrb_cellpool) {
        int oldFlags;
        int newFlags;

        expand = 1;
        oldFlags = *(int*)(&aiocd->aiocdFlags);
        do {
            if ((oldFlags & SAIOPRL_AIOCDFLAGSEXPAND_AIOSRB_CELLPOOL) != SAIOPRL_AIOCDFLAGSEXPAND_AIOSRB_CELLPOOL ||
                (oldFlags & SAIOPRL_AIOCDFLAGSEXPANDING_AIOSRB_CELLPOOL) == SAIOPRL_AIOCDFLAGSEXPANDING_AIOSRB_CELLPOOL) {
                expand = 0;
                break;
            }

            newFlags = oldFlags | SAIOPRL_AIOCDFLAGSEXPANDING_AIOSRB_CELLPOOL;
        } while(cs((cs_t *) &oldFlags,
                   (cs_t *) (&aiocd->aiocdFlags),
                   (cs_t)   newFlags
                   ));

        if (expand) {
            if (expandAIOSRBCellPool(aiocd) != 0) {
                if (TraceActive(trc_level_exception)) {
                    TraceRecord(trc_level_exception,
                                TP(_TP_MVS_AIOSERVICES_CHECKANDEXPANDPOOLS_FAILEDTOEXPAND),
                                "checkAndExpandPools: Expand failed",
                                TRACE_DATA_END_PARMS);
                }
            }

            // Expanded.  Turn off the "Need to Expand" and the "I'm expanding"
            // Flags.
            do {
                newFlags = oldFlags & ~(SAIOPRL_AIOCDFLAGSEXPAND_AIOSRB_CELLPOOL+
                                        SAIOPRL_AIOCDFLAGSEXPANDING_AIOSRB_CELLPOOL);
            } while(cs((cs_t *) &oldFlags,
                       (cs_t *) (&aiocd->aiocdFlags),
                       (cs_t)   newFlags
                       ));
        }
    }

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(_TP_MVS_AIOSERVICES_CHECKANDEXPANDPOOLS_EXIT),
                    "checkAndExpandPools: Exit",
                    TRACE_DATA_HEX_INT(*((int*)&aiocd->aiocdFlags), "aiocd->aiocdFlags"),
                    TRACE_DATA_INT(expand, "expanded SRB cellpool"),
                    TRACE_DATA_END_PARMS);
    }
}

// ---------------------------------------------------------------------
// aio_initPGOO
//
// ---------------------------------------------------------------------
void aio_initPGOO(AIO_InitParms* initParms) {
    int rc = 0;
    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(_TP_MVS_AIOSERVICES_AIOINIT_ENTER),
                    "aio_initPGOO: before build all cellpools",
                    TRACE_DATA_END_PARMS);
    }

    AsyncIOCompletionData* aiocd = buildAsyncIOCompletionData();
    if ( aiocd == NULL ) {
        rc = -1;
    } else {
        if (buildAIOCellPools(aiocd) == 0) {

            // "Register" an AIO hard failure cleanup routine.
            registerAioHardFailureRtn();

            // Set capabilities to indicate we support batch operations (ex. getCompletionData3/aio_getioev3)
            //rc += (IAsyncProvider_CAP_BATCH_IO + IAsyncProvider_CAP_JIT_BUFFERS);
            rc += IAsyncProvider_CAP_BATCH_IO;

        } else {
            rc = -2;
        }
    }

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(_TP_MVS_AIOSERVICES_AIOINIT_EXIT),
                    "aio_initPGOO: after build all cellpools",
                    TRACE_DATA_INT(rc, "rc"),
                    TRACE_DATA_END_PARMS);
    }


    // copy back the return code
    memcpy_dk(initParms->returnCode, &rc, sizeof(int), 8);
}



void prepareConnection(AIO_ConnectionParms* parms) {
    RegistryToken localAioConnToken = {{0}};
    aioconn* mptr = NULL;

    // initialize the aioconn struct values
    AsyncIOCompletionData* aiocd_p = getAsyncIOCompletionData();

    // get a cell from the cell pool for each connection
    // each cell in this cellpool contains storage for aioconn + read aiocb + write aiocb + read iov + write iov
    mptr = (aioconn*)getCellPoolCell(aiocd_p->aioconn_cellpool_id);
    aioconn* connection = (aioconn*) mptr;
    memset(connection, 0, sizeof(aioconn));
    // read aiocb storage starts immediately after the aioconn object
    connection->readAiocb_p = (aiocb*)((char*)mptr + sizeof(aioconn));
    memset(connection->readAiocb_p, 0, sizeof(aiocb));
    // write aiocb storage is after the read aiocb
    connection->writeAiocb_p = (aiocb*)((char*)mptr + sizeof(aioconn) + sizeof(aiocb));
    memset(connection->writeAiocb_p, 0, sizeof(aiocb));

    // read iov entries(max of 120) after write aiocb
    connection->readIovec_p = (iovec*)((char*)mptr + sizeof(aioconn) + sizeof(aiocb) + sizeof(aiocb));
    memset(connection->readIovec_p, 0, sizeof(iovec));

    // write iov entries(max of 120) after read iov
    connection->writeIovec_p = (iovec*)((char*)mptr + sizeof(aioconn) + 2*sizeof(aiocb) + 120*sizeof(iovec));
    memset(connection->writeIovec_p, 0, sizeof(iovec));

    connection->readIovecLength = 0;
    connection->writeIovecLength = 0;

    connection->readCallId = -1;
    connection->writeCallId = -1;

    connection->readAiocb_p->aiofd = parms->socketHandle; //aio_fildes
    connection->writeAiocb_p->aiofd = parms->socketHandle;

    connection->readAiocb_p->aionotifytype = aio_mvs;  //AIO_MVS;
    connection->writeAiocb_p->aionotifytype = aio_mvs;  //AIO_MVS;

    memcpy(&(connection->readAiocb_p->aioexitdata), &connection, 8);
    memcpy(&(connection->writeAiocb_p->aioexitdata), &connection, 8);

    connection->readAiocb_p->aiocflags = 0 | aiook2compimd;  //AIO_OK2COMPIMD
    connection->writeAiocb_p->aiocflags = 0 | aiook2compimd; //AIO_OK2COMPIMD

    connection->readAiocb_p->aiocflags2 = USERKEY8;  // Set AIO_USERKEY to key8
    connection->writeAiocb_p->aiocflags2 = USERKEY8; // Set AIO_USERKEY to key8

    // set the bit that tells to use Key8 (userkey) to access the databuffers
    connection->readAiocb_p->aiocflags2 = connection->readAiocb_p->aiocflags2 | aiouseuserkey;   //AIO_USEUSERKEY;
    connection->writeAiocb_p->aiocflags2 = connection->writeAiocb_p->aiocflags2 | aiouseuserkey; //AIO_USEUSERKEY;

    if (TraceActive(trc_level_detailed)) {
        int l_read_aiocflags = connection->readAiocb_p->aiocflags;
        int l_read_aiocflags2 = connection->readAiocb_p->aiocflags2;
        int l_write_aiocflags = connection->writeAiocb_p->aiocflags;
        int l_write_aiocflags2 = connection->writeAiocb_p->aiocflags2;

        TraceRecord(trc_level_detailed,
                    TP(_TP_MVS_AIOSERVICES_PREPARE2_CONNINFO),
                    "aio_prepare2: AIOCB info",
                    TRACE_DATA_PTR(connection, "connection"),
                    TRACE_DATA_PTR(connection->readAiocb_p->aioexitdata, "aiocb exitdata"),
                    TRACE_DATA_INT(connection->readAiocb_p->aionotifytype, "AIO_MVS"),
                    TRACE_DATA_INT(connection->readAiocb_p->aiofd, "aio_fildes"),
                    TRACE_DATA_INT(connection->writeAiocb_p->aiofd, "aio_fildes"),
                    TRACE_DATA_PTR(connection->writeAiocb_p, "connection->writeAiocb_p"),
                    TRACE_DATA_PTR(connection->readAiocb_p, "connection->readAiocb_p"),
                    TRACE_DATA_HEX_INT(l_read_aiocflags, "connection->readAiocb_p->aiocflags"),
                    TRACE_DATA_HEX_INT(l_read_aiocflags2, "connection->readAiocb_p->aiocflags2"),
                    TRACE_DATA_HEX_INT(l_write_aiocflags, "connection->writeAiocb_p->aiocflags"),
                    TRACE_DATA_HEX_INT(l_write_aiocflags2, "connection->writeAiocb_p->aiocflags2"),
                    TRACE_DATA_INT(connection->readAiocb_p->aiorv, "readaiocb AIORV"),
                    TRACE_DATA_INT(connection->writeAiocb_p->aiorv, "writeaiocb AIORV"),
                    TRACE_DATA_END_PARMS);
    }

    // map connection to registry token and return back token ptr
    addAioConnTokenToRegistry(connection, &localAioConnToken);

    // save the token in the aioconn
    connection->aioConn_RegToken = localAioConnToken;

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(_TP_MVS_AIOSERVICES_PREPARE2_REGTOKEN),
                    "prepareConn: before getServerProcessData",
                    TRACE_DATA_RAWDATA(sizeof(RegistryToken), &localAioConnToken, "Registry Token"),
                    TRACE_DATA_END_PARMS);
    }
    // save the address of the PGOO
    connection->pgoo = getServerProcessData();

    // copy the key2 aioconn token to key8
    memcpy_dk(parms->outputToken, &localAioConnToken, sizeof(localAioConnToken), 8);

}

void getioev2(AIO_IoevParms *ioev2Parms) {

    server_process_data* spd = getServerProcessData(); // get the PGoo*
    PetVet* mypetvet = (PetVet*)&(spd->petvet[0]);

    AsyncIOCompletionData* aiocd_p = (AsyncIOCompletionData*) spd->asyncio_completion_data_p;

    // If aiocd is not null,
    if(aiocd_p != NULL) {
        checkAndExpandPools(aiocd_p);

        pushHandler_PLO(aiocd_p, mypetvet, ioev2Parms);
    } else {
        // error
        if (TraceActive(trc_level_detailed)) {
            TraceRecord(trc_level_detailed,
                        TP(_TP_MVS_AIOSERVICES_GETIOEV2_NO_AIOCD),
                        "getioev2: create aiocd_p failed",
                        TRACE_DATA_END_PARMS);
        }
    }
}

void disposeConnection(AIO_DisposeParms* parms) {

    RegistryToken* callerToken_p;   // key 8 token ptr
    RegistryToken  token;           // actual token in key 2
    RegistryToken* token_p = &token;
    aioconn* local_aioconn_p;

    //Note:  assuming this value is ptr to token, this needs to be changed when the interface changes to take entire token
    callerToken_p = parms->inputToken;                          //  key 8 token ptr
    memcpy_sk(token_p, callerToken_p, sizeof(*token_p), 8);      // move the token (key8) into key 2

    // Verify token and retrieve Connection address
    int localRC = getAioConnFromRegistry(token_p, &local_aioconn_p);
    if (localRC == 0) {

        // get the original handle which is in aiocb->aiofd and return to java
        long handle = local_aioconn_p->readAiocb_p->aiofd; //since the aiofd is the same in both read and write aiocb,
        memcpy_dk(parms->outputHandle, &handle, sizeof(long), 8);

        if (TraceActive(trc_level_detailed)) {
            TraceRecord(trc_level_detailed,
                        TP(_TP_MVS_AIOSERVICES_DISPOSE_CONNECTION),
                        "Entered Dispose",
                        TRACE_DATA_PTR(callerToken_p, "key8 token ptr in dispose"),
                        TRACE_DATA_PTR(token_p, "key2 token ptr in dispose"),
                        TRACE_DATA_INT(local_aioconn_p->readAiocb_p->aiofd, "read aiofd"),
                        TRACE_DATA_HEX_LONG(handle, "read aiofd"),
                        TRACE_DATA_HEX_LONG(*(parms->outputHandle), "write aiofd"),
                        TRACE_DATA_END_PARMS);
        }

        //add the destroy routine which does verify aioconn obj  in cellpool, frees the iovec, free cell pool
        registryFree(token_p,TRUE); // we want the token to be verified in the registry cell pool (done above under getAioConnFromRegistry)
        registrySetUnused(token_p,TRUE); // to decrement the usecount
    }

    // Pass back the return code
    memcpy_dk(parms->returnCode, &localRC, sizeof(int), 8);
}

void destroyAIOCONNDataArea(RegistryDataArea dataArea) {

    aioconn* local_aioconn_p = *((aioconn**)&dataArea);

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(_TP_MVS_AIOSERVICES_DISPOSE_ENTER),
                    "destroyAIOCONNDataArea",
                    TRACE_DATA_HEX_LONG(local_aioconn_p->writeCallId, "local_aioconn_p->writeCallId"),
                    TRACE_DATA_HEX_LONG(local_aioconn_p->readCallId, "local_aioconn_p->readCallId"),
                    TRACE_DATA_END_PARMS);
    }
    if (local_aioconn_p->readCallId != -1 || local_aioconn_p->writeCallId != -1) {
        if (TraceActive(trc_level_detailed)) {
            TraceRecord(trc_level_detailed,
                        TP(_TP_MVS_AIOSERVICES_DISPOSE_WITHIO),
                        "Dispose Called during Outstanding I/O!",
                        TRACE_DATA_HEX_LONG(local_aioconn_p->writeCallId, "local_aioconn_p->writeCallId"),
                        TRACE_DATA_HEX_LONG(local_aioconn_p->readCallId, "local_aioconn_p->readCallId"),
                        TRACE_DATA_END_PARMS);
        }
    }

    AsyncIOCompletionData* aiocd_p = getAsyncIOCompletionData();
    freeCellPoolCell(aiocd_p->aioconn_cellpool_id, local_aioconn_p);
}

void cancelAIO(AIO_CancelParms* parms) {
    aiocb cancelIocb;
    aioconn* local_aioconn_p;
    RegistryToken* callerToken_p;  // key 8 token ptr
    RegistryToken token;           // actual token in key 2
    RegistryToken* token_p = &token;
    long callid;
    int cancelRv = 0;
    int cancelRc = 0;
    int cancelRsn = 0;

    callerToken_p = parms->inputToken;                          //  key 8 token ptr
    memcpy_sk(token_p, callerToken_p, sizeof(*token_p), 8);      // move the token (key8) into key 2

    // Verify token and retrieve Connection address
    cancelRv = getAioConnFromRegistry(token_p, &local_aioconn_p);
    if (cancelRv == 0) {
        callid = parms->callId;
        if (TraceActive(trc_level_detailed)) {
            TraceRecord(trc_level_detailed,
                        TP(_TP_MVS_AIOSERVICES_AIOCALL_CANCELAIO_ENTER),
                        "Entered cancelAIO ",
                        TRACE_DATA_HEX_LONG(callid, "callid"),
                        TRACE_DATA_HEX_LONG(local_aioconn_p->writeCallId, "write callid"),
                        TRACE_DATA_HEX_LONG(local_aioconn_p->readCallId, "read callid"),
                        TRACE_DATA_END_PARMS);
        }

        memset(&cancelIocb, 0, sizeof(cancelIocb));

        if(callid == local_aioconn_p->writeCallId) {
            cancelIocb.aiobuffptr64 = local_aioconn_p->writeAiocb_p;
            cancelIocb.aiofd = local_aioconn_p->writeAiocb_p->aiofd;
        } else if(callid == local_aioconn_p->readCallId) {
            cancelIocb.aiobuffptr64 = local_aioconn_p->readAiocb_p;
            cancelIocb.aiofd = local_aioconn_p->readAiocb_p->aiofd;
        } else {
            // Hmm.  The caller is confused, the callid does not match the current read or write call id?
            if (TraceActive(trc_level_detailed)) {
                TraceRecord(trc_level_detailed,
                            TP(_TP_MVS_AIOSERVICES_CANCELAIO_CALLID),
                            "cancelAIO callid not found",
                            TRACE_DATA_HEX_LONG(callid, "callid"),
                            TRACE_DATA_HEX_LONG(local_aioconn_p->writeCallId, "write callid"),
                            TRACE_DATA_HEX_LONG(local_aioconn_p->readCallId, "read callid"),
                            TRACE_DATA_END_PARMS);
            }

            cancelRv = MVS_AIOSERVICES_CANCELAIO_CALLID;
        }

        if (cancelRv == 0) {
            // Complete the AIOCB setup for a cancel.
            cancelIocb.aiocmd = aio_cancel;
            cancelIocb.aiocflags = 0 | aiosync;

            BPX4AIO(sizeof(aiocb), &cancelIocb, &cancelRv, &cancelRc, &cancelRsn);
            if(cancelRv == 1) { // BPX4AIO called for cancel completed successfully, check the aiorv value

                // Pass back cancel results
                cancelRv = cancelIocb.aiorv;

                // Should I clear callid after successful cancel? Leaving a non -1 value
                // causes a subsequent dispose() call to report that IO was in progress.  However, if I
                // did clear it then a cancel'd IO request causes an SRB to drop a -1, ECANCELLED back
                // to a Handler.  It wouldn't be able to associated this completed IO to the cancelled read/write
                // if we cleared the callid now.  So, leave it.
            } else {
                // Error in bpx4aio call
                if (TraceActive(trc_level_detailed)) {
                    TraceRecord(trc_level_detailed,
                                TP(_TP_MVS_AIOSERVICES_AIOCALL_CANCELAIO_EXIT),
                                "Call got an error ",
                                TRACE_DATA_HEX_INT(cancelRv, "cancelRV"),
                                TRACE_DATA_HEX_INT(cancelRc, "cancelRc"),
                                TRACE_DATA_HEX_INT(cancelRsn, "cancelRsn"),
                                TRACE_DATA_END_PARMS);
                }
            }

#ifdef AIOCD_PLO_DEBUG_MSGS
            char zbuf[150];
            snprintf(zbuf, sizeof(zbuf), "cancelIocb.aiobuffptr64: %p aiorv: %d cmd: %d flg: %X flg2: %X rv: %d rc: %d rsn: %d cId: %lX wId: %lX rId: %lX",
                     cancelIocb.aiobuffptr64, cancelIocb.aiorv, cancelIocb.aiocmd, cancelIocb.aiocflags, cancelIocb.aiocflags2,
                     cancelRv, cancelRc, cancelRsn, callid, local_aioconn_p->writeCallId, local_aioconn_p->readCallId);
            write_to_programmer(zbuf);
#endif
        }

        // unlock and decrement the usecount of the token
        registrySetUnused(token_p, TRUE);
    }

    // Copy back results to caller.
    memcpy_dk(parms->rv, &(cancelRv), sizeof(int), 8);
}

void closeportAIO(AIO_CloseportParms* parms) {
    int rc = 0;
    if (TraceActive(trc_level_detailed)) {
                    TraceRecord(trc_level_detailed,
                    TP(_TP_MVS_AIOSERVICES_AIOCLOSEPORT_ENTER),
                    "closeportAIO: entry",
                    TRACE_DATA_END_PARMS);
    }

    server_process_data* spd = getServerProcessData();
    AsyncIOCompletionData* aiocd_p = (AsyncIOCompletionData*) spd->asyncio_completion_data_p;

    // "DeRegister" the AIO server Hard failure cleanup routine.
    deregisterAioHardFailureRtn();

    if ( aiocd_p != NULL ) {
#ifdef AIOCD_PLO_DEBUG
        writeDiagnosticsFromAIOCD(aiocd_p);
#endif

        // Releases any handlers that are currently waiting for IO Completions.
        shutdownHandlers_PLO(aiocd_p);
    } else {
        rc = -1;
    }

    if (TraceActive(trc_level_detailed)) {
                    TraceRecord(trc_level_detailed,
                    TP(_TP_MVS_AIOSERVICES_AIOCLOSEPORT_EXIT),
                    "closeportAIO: exit",
                    TRACE_DATA_INT(rc, "rc"),
                    TRACE_DATA_END_PARMS);
    }

    // copy back the return code
    memcpy_dk(parms->returnCode, &rc, sizeof(int), 8);
}


void shutdownAIO(AIO_ShutdownParms* parms) {
    int rc = 0;
    if (TraceActive(trc_level_detailed)) {
                    TraceRecord(trc_level_detailed,
                    TP(_TP_MVS_AIOSERVICES_AIOSHUTDOWN_ENTER),
                    "shutdownAIO: entry",
                    TRACE_DATA_END_PARMS);
    }

    server_process_data* spd = getServerProcessData();
    AsyncIOCompletionData* aiocd_p = (AsyncIOCompletionData*) spd->asyncio_completion_data_p;

    if ( aiocd_p != NULL ) {
        // Unhook AIOCD from SPD
        AsyncIOCompletionData* clearPtr = NULL;
        setCompletionDataInPGOO(spd, clearPtr);

        // Release storage pools
        destroyAIOCellPools(aiocd_p);

        // Release AIOCD storage
        free(aiocd_p);
    } else {
        rc = -1;
    }

    if (TraceActive(trc_level_detailed)) {
                    TraceRecord(trc_level_detailed,
                    TP(_TP_MVS_AIOSERVICES_AIOSHUTDOWN_EXIT),
                    "shutdownAIO: exit",
                    TRACE_DATA_INT(rc, "rc"),
                    TRACE_DATA_END_PARMS);
    }

    // copy back the return code
    memcpy_dk(parms->returnCode, &rc, sizeof(int), 8);
}


/* Add element in the queue at end
if que is empty,
   increment newArea.seqnumber
   head and tail ptrs pointing to newElement
   (need CSSTX (func code 15)-- single store of header with 2 8byte ptrs )
else
   increment newArea.seqnumber
   newElement.prev to lastElement and lastElement.next to newElement
   tail pointing to newElement
   (need CSDSTX (func code 19), store of header, store of element ptrs(2 - 8byte ptrs) )
*/
void enqueueIO_PLO(AsyncIOCompletionData* aiocd_p, aiocb *aiocb_p) {

    int ploRC = 0;

    CompletionQElement* completedQueueElement_p = NULL;

    AsyncIOCompletionData oldCompareArea;

    PloCompareAndSwapAreaQuadWord_t swapArea;
    PloStoreAreaQuadWord_t          storeArea1;  // The Stack or Queue updates
    PloStoreAreaQuadWord_t          storeArea2;  // The existing Element updates for Stack or Queue

    swapArea.compare_p = aiocd_p;
    AsyncIOCompletionData* replaceArea_p = (AsyncIOCompletionData*)&(swapArea.replaceValue);

    Stack*  replaceStack_p = (Stack*) &(storeArea1.storeValue);
    ElementDT* replaceStackElement_p = (ElementDT*) &(storeArea2.storeValue);

    Que* replaceQ_p = (Que*) &(storeArea1.storeValue);
    ElementDT* replaceQElement_p = (ElementDT*) &(storeArea2.storeValue);

#ifdef AIOCD_PLO_DEBUG
    // increment the srbcount  for debug
    csIntInc(&aiocd_p->aiodebug_data.srbcount, 0);
#endif

    do {
        // Make a local copy of all serialized stuff (SEQ #, stack and queue)
        memcpy(&oldCompareArea, aiocd_p, sizeof(AsyncIOCompletionData));

        // Get PLO Compare area
        memcpy(&(swapArea.expectedValue), &oldCompareArea, sizeof(swapArea.expectedValue));

        // Build new SwapArea
        memcpy(replaceArea_p, &(swapArea.expectedValue), sizeof(swapArea.replaceValue));
        replaceArea_p->sequenceNumber+=1;

        // Clear storage for PLO new values
        memset(&storeArea1.storeValue, 0, sizeof(storeArea1.storeValue));
        memset(&storeArea2.storeValue, 0, sizeof(storeArea2.storeValue));

        // Grab current stack
        memcpy(replaceStack_p, &(oldCompareArea.handlerWaiterStack), sizeof(*replaceStack_p));
        if (!isStackEmpty(replaceStack_p)) { // stack not empty
            // pop the HandlerStackElement
            ElementDT* firstStackElement_p = replaceStack_p->stack_header.first;
            ElementDT* nextStackElement_p = firstStackElement_p->element_next_p;

            // replaceStack_p is using storeArea1.storeValue
            // replaceStackElement_p is using storeArea2.storeValue
            storeArea1.storeLocation_p = &(aiocd_p->handlerWaiterStack);
            if (nextStackElement_p == NULL) {   //only 1 element
                replaceStack_p->stack_header.first = NULL;

                ploRC = ploCompareAndSwapAndStoreQuadWord(swapArea.compare_p, &swapArea, &storeArea1);
            } else { // if more than 1 element

                *replaceStackElement_p = *nextStackElement_p; // Copy element
                replaceStackElement_p->element_prev_p = NULL;
                replaceStack_p->stack_header.first = nextStackElement_p;

                storeArea2.storeLocation_p = nextStackElement_p;
                ploRC = ploCompareAndSwapAndDoubleStoreQuadWord(swapArea.compare_p, &swapArea, &storeArea1, &storeArea2);
            }

#ifdef AIOCD_PLO_DEBUG
            // increment the PLO call -- for debug
            csIntInc(&aiocd_p->aiodebug_data.totalPLOcalls, 0);
#endif

            if (ploRC == 0) {
#ifdef AIOCD_PLO_DEBUG
                // decrement the hndlrstack depth -- for debug
                csIntDec(&aiocd_p->aiodebug_data.hndlrStackDepth);
#endif
                aioconn* connection_p;
                CompletionStackElement* topStackElement_p;

                // get the aioconn from aiocb
                memcpy(&connection_p,&(aiocb_p->aioexitdata),8);
                topStackElement_p = (CompletionStackElement*)firstStackElement_p;

                // Clear the removed elements ptrs
                clearPtrs(firstStackElement_p);

                // If we already built an IOCDEntry for this completed IO, then we already updated the Connection
                // information too--we cant do it again.  So, we need to transfer the previously built IOCDEntry
                // for a CompletionQElement to the CompletionStackElement we just removed from the Handler stack.
                if (completedQueueElement_p != NULL) {
                    memcpy(&(topStackElement_p->ioCompletedInfo),
                           &(completedQueueElement_p->ioCompletedInfo),
                           sizeof(topStackElement_p->ioCompletedInfo));

                    // Release the previously build CompletionQElement
                    freeCellPoolCell(aiocd_p->aioelement_cellpool_id, completedQueueElement_p);
                    completedQueueElement_p = NULL;
                }  else {
                    //put IOCompletedInfo into the topStackElement_p,
                    topStackElement_p->ioCompletedInfo.iocde_ReturnCode = aiocb_p->aiorc;
                    topStackElement_p->ioCompletedInfo.iocde_BytesAffected = aiocb_p->aiorv;
                    topStackElement_p->ioCompletedInfo.iocde_ChannelIdentifier = (long)&(connection_p->aioConn_RegToken);

                    // check if this IO is read or write
                    if ( (aiocb_p->aiocmd == aio_read) || (aiocb_p->aiocmd == aio_readv) ) {
                        topStackElement_p->ioCompletedInfo.iocde_CallId = connection_p->readCallId;
                        connection_p->readCallId = -1;
                    } else {
                        topStackElement_p->ioCompletedInfo.iocde_CallId = connection_p->writeCallId;
                        connection_p->writeCallId = -1;
                    }
                }

                // release the active PET
                iea_return_code releaseRc;
                iea_auth_type authType = IEA_AUTHORIZED;
                iea_release_code releaseCode;
                memset(&releaseCode, 0, sizeof(releaseCode));

                //switch to key 0 since we are using IEA_AUTHORIZED and after the call to release , switch it back to Key 2
                unsigned char oldKey = switchToKey0();
                iea4rls(&releaseRc, authType, topStackElement_p->active_pauseElement, releaseCode);
                switchToSavedKey(oldKey);

                //if release failed, add a flag into the stack element and loop back as if plo failed.
                if(releaseRc != 0) {
                    topStackElement_p->stackElementErrorFlag = topStackElement_p->stackElementErrorFlag | 1;
                    ploRC = 1;
                    continue;
                }

            } else {
#ifdef AIOCD_PLO_DEBUG
                // increment the hndlrPopPLOFailed -- for debug
                csIntInc(&aiocd_p->aiodebug_data.hndlrPopPLOFailed, 0);
#endif
            }

        } else { // stack empty,
            // Build the CompletionQElement
            //
            // Note: We need to update the state of the connection before queuing this Element.  Once queued the Connection can
            // have another read/write issued against it.  This presents a problem if the PLO fails to add this new QElement.  When
            // we loop around for another try, if a Handler is on the stack now, we dont need this QElement, but we have updated the
            // Connection read/write callid.  So, we must transfer the IOCDEntry to the Stack Element in that scenario to make sure 
            // the Handler has the correct callid when trying to match the completed IO to the future from the read/write.
            if (completedQueueElement_p == NULL) {
                completedQueueElement_p = (CompletionQElement*)getCellPoolCell(aiocd_p->aioelement_cellpool_id);
                if (completedQueueElement_p != NULL) {
                    memset(completedQueueElement_p, 0, sizeof(CompletionQElement));
                    // set the CompletionQElement EyeCatcher
                    memcpy(completedQueueElement_p->eyeCatcher,"ZAIOCEQE",8);

                    // get the aioconn from aiocb
                    aioconn* connection_p;
                    memcpy(&connection_p,&(aiocb_p->aioexitdata),8);

                    // fill in the CompletionQElement with completedIOInfo
                    completedQueueElement_p->ioCompletedInfo.iocde_ReturnCode = aiocb_p->aiorc;
                    completedQueueElement_p->ioCompletedInfo.iocde_BytesAffected = aiocb_p->aiorv;
                    completedQueueElement_p->ioCompletedInfo.iocde_ChannelIdentifier = (long)&(connection_p->aioConn_RegToken);

                    // check if this IO is read or write
                    if ( (aiocb_p->aiocmd == aio_read) || (aiocb_p->aiocmd == aio_readv) ) {
                        completedQueueElement_p->ioCompletedInfo.iocde_CallId = connection_p->readCallId;
                        connection_p->readCallId = -1;
                    } else {
                        completedQueueElement_p->ioCompletedInfo.iocde_CallId = connection_p->writeCallId;
                        connection_p->writeCallId = -1;
                    }
                } else {
#ifdef AIOCD_PLO_DEBUG
                    //error obtaining qelement from pool
                    csIntInc(&aiocd_p->aiodebug_data.buildQueEleError, 0);
#endif
                    // Nothing to do but ABEND.  Saving the aiocb for the completed IO and the AsyncIO CB address.
                    abend_with_data(ABEND_TYPE_SERVER,
                                    KRSN_MVS_AIO_SERVICES_NO_ELEMENT_CELL,
                                    (void*)aiocd_p,
                                    (void*)aiocb_p);
                }
            }

            ElementDT* newQElement_p = &(completedQueueElement_p->queueElement);
            //Place the CompletionQElement to the end of Que
            memcpy(replaceQ_p, &(oldCompareArea.asyncIOCompletionQ), sizeof(Que));

            // replaceQ_p is using storeArea1.storeValue
            // replaceQElement_p is using storeArea2.storeValue
            storeArea1.storeLocation_p = &(aiocd_p->asyncIOCompletionQ);

            //check if que is empty or not
            if (!isQueueEmpty(replaceQ_p)){ //not empty
                ElementDT* lastQElement_p = (ElementDT* ) replaceQ_p->queue_header.tail;
                newQElement_p->element_prev_p = lastQElement_p;
                newQElement_p->element_next_p = NULL;
                *replaceQElement_p = *lastQElement_p;
                replaceQElement_p->element_next_p = newQElement_p;

                replaceQ_p->queue_header.tail = newQElement_p;

                storeArea2.storeLocation_p = lastQElement_p;
                ploRC = ploCompareAndSwapAndDoubleStoreQuadWord(swapArea.compare_p, &swapArea, &storeArea1, &storeArea2);
            } else { //que empty
                replaceQ_p->queue_header.head = newQElement_p;
                replaceQ_p->queue_header.tail = newQElement_p;

                ploRC = ploCompareAndSwapAndStoreQuadWord(swapArea.compare_p, &swapArea, &storeArea1);
            }
#ifdef AIOCD_PLO_DEBUG
            // increment the PLO call -- for debug
            csIntInc(&aiocd_p->aiodebug_data.totalPLOcalls, 0);
            if(ploRC == 0) {
                // increment the ComIOQueDepth -- for debug
                csIntInc(&aiocd_p->aiodebug_data.addIOQueEl, 0);
            } else {
                // increment the ComIOQueDepth -- for debug
                csIntInc(&aiocd_p->aiodebug_data.enQPLOFailed, 0);
            }
#endif
        }
    } while(ploRC);

}

void pushHandler_PLO(AsyncIOCompletionData* aiocd_p, PetVet* mypetvet, AIO_IoevParms *ioev2Parms) {

    int ploRC = 0;
    AsyncIOCompletionData oldCompareArea;

    ElementDT* firstStackElement_p;
    ElementDT* newStackElement_p;

    ElementDT* firstQElement_p;
    ElementDT* nextQElement_p;

    CompletionStackElement* handlerWaiterStackElement_p = NULL;
    CompletionQElement* topCompletionQueueElement_p;
    iea_PEToken pauseElement;
    unsigned char allocatedPauseToken = FALSE;

    PloCompareAndSwapAreaQuadWord_t swapArea;
    PloStoreAreaQuadWord_t          storeArea1;  // The Stack or Queue updates
    PloStoreAreaQuadWord_t          storeArea2;  // The existing Element updates for Stack or Queue

    swapArea.compare_p = aiocd_p;
    AsyncIOCompletionData* replaceArea_p = (AsyncIOCompletionData*)&(swapArea.replaceValue);


    Stack*  replaceStack_p = (Stack*) &(storeArea1.storeValue);
    ElementDT* replaceStackElement_p = (ElementDT*) &(storeArea2.storeValue);

    Que* replaceQ_p = (Que*) &(storeArea1.storeValue);
    ElementDT* replaceQElement_p = (ElementDT*) &(storeArea2.storeValue);

    // Setup reference to first output IOCDE.
    IOCDEntry* currentIOCDE_p;
    int numberIOCDEsCompleted = 0;

#ifdef AIOCD_PLO_DEBUG
            // Increment the Total number of Handler calls for IO -- for debug
            csIntInc(&aiocd_p->aiodebug_data.totalHandlerCalls, 0);
#endif


    do {
        // Make a local copy of all serialized stuff (SEQ #, stack and queue)
        memcpy(&oldCompareArea, aiocd_p, sizeof(AsyncIOCompletionData));

        // Get PLO Compare area
        memcpy(&(swapArea.expectedValue), &oldCompareArea, sizeof(swapArea.expectedValue));

        // Build new SwapArea
        memcpy(replaceArea_p, &(swapArea.expectedValue), sizeof(swapArea.replaceValue));
        replaceArea_p->sequenceNumber+=1;

        // Clear storage for PLO new values
        memset(&storeArea1.storeValue, 0, sizeof(storeArea1.storeValue));
        memset(&storeArea2.storeValue, 0, sizeof(storeArea2.storeValue));

        memcpy_sk(&currentIOCDE_p, &(ioev2Parms->iocde_p[numberIOCDEsCompleted]), sizeof(currentIOCDE_p), 8);

        memcpy(replaceQ_p, &(oldCompareArea.asyncIOCompletionQ), sizeof(Que));
        if (TraceActive(trc_level_detailed)) {
                                        TraceRecord(trc_level_detailed,
                                        TP(_TP_MVS_AIOSERVICES_PUSHHANDLER_PLO_LOOP),
                                        "after copying to replaceQ",
                                        TRACE_DATA_RAWDATA(sizeof(Que), replaceQ_p, "replaceQ"),
                                        TRACE_DATA_PTR(currentIOCDE_p, "currentIOCDE_p"),
                                        TRACE_DATA_END_PARMS);
        }

        if( !(isQueueEmpty(replaceQ_p)) ) { //completionQ not empty
            //free the handlerstackelement storage you created ,
            //this happens when creating hndlrstackElement, but plo failed , and when trying agian,
            //if there is already a IOCompleted event in the queue in the meantime , no need to put the hndlr as wiating , instead continue with processing the CompletionQ
            if( handlerWaiterStackElement_p != NULL ) {
                freeCellPoolCell(aiocd_p->aioelement_cellpool_id, handlerWaiterStackElement_p);
                handlerWaiterStackElement_p = NULL;
            }

            // replaceQ_p is using storeArea1.storeValue
            storeArea1.storeLocation_p = &(aiocd_p->asyncIOCompletionQ);

            // dequeue the completedIO
            ElementDT* firstQElement_p = replaceQ_p->queue_header.head;

            if(replaceQ_p->queue_header.head == replaceQ_p->queue_header.tail) { //only 1 element in the que
                replaceQ_p->queue_header.head = NULL;
                replaceQ_p->queue_header.tail = NULL;
                if (TraceActive(trc_level_detailed)) {
                                                TraceRecord(trc_level_detailed,
                                                TP(_TP_MVS_AIOSERVICES_PUSHHANDLER_PLO_LOOP2),
                                                "deque completedIO before CSSTX plo",
                                                TRACE_DATA_INT(replaceArea_p->sequenceNumber, "replaceArea.sequenceNum"),
                                                TRACE_DATA_INT(oldCompareArea.sequenceNumber, "oldCompareArea.sequenceNum"),
                                                TRACE_DATA_END_PARMS);
                }

                ploRC = ploCompareAndSwapAndStoreQuadWord(swapArea.compare_p, &swapArea, &storeArea1);
                if (TraceActive(trc_level_detailed)) {
                    TraceRecord(trc_level_detailed,
                                TP(_TP_MVS_AIOSERVICES_PUSHHANDLER_PLO_LOOP3),
                                "after PLO CSSTX  deq compIOQ only 1 ele in Q",
                                TRACE_DATA_INT(ploRC, "ploRC"),
                                TRACE_DATA_RAWDATA(sizeof(AsyncIOCompletionData), aiocd_p,"OP2 aiocd contents"),
                                TRACE_DATA_RAWDATA(sizeof(ElementDT),(ElementDT*)(aiocd_p->asyncIOCompletionQ.queue_header.head),"aiocdQueElement head should be null"),
                                TRACE_DATA_END_PARMS);
                }
            } else { // more than 1 element in the que
                nextQElement_p = firstQElement_p->element_next_p;
                if (nextQElement_p == NULL) {
                    continue;
                }

                *replaceQElement_p = *nextQElement_p;
                replaceQElement_p->element_prev_p = NULL;
                replaceQ_p->queue_header.head = nextQElement_p;

                if (TraceActive(trc_level_detailed)) {
                    TraceRecord(trc_level_detailed,
                                TP(_TP_MVS_AIOSERVICES_PUSHHANDLER_PLO_LOOP4),
                                "deque completedIO before CSDSTX plo",
                                TRACE_DATA_INT(replaceArea_p->sequenceNumber, "replaceArea.sequenceNum"),
                                TRACE_DATA_INT(oldCompareArea.sequenceNumber, "oldCompareArea.sequenceNum"),
                                TRACE_DATA_PTR(nextQElement_p,"nextElement ptr"),
                                TRACE_DATA_RAWDATA(sizeof(ElementDT),replaceQElement_p,"replaceQElement ptr"),
                                TRACE_DATA_END_PARMS);
                }

                storeArea2.storeLocation_p = nextQElement_p;
                ploRC = ploCompareAndSwapAndDoubleStoreQuadWord(swapArea.compare_p, &swapArea, &storeArea1, &storeArea2);

                if (TraceActive(trc_level_detailed)) {
                    TraceRecord(trc_level_detailed,
                                TP(_TP_MVS_AIOSERVICES_PUSHHANDLER_PLO_LOOP5),
                                "after PLO CSDSTX  deq compIOQ >1 ele in Q",
                                TRACE_DATA_INT(ploRC, "ploRC"),
                                TRACE_DATA_RAWDATA(sizeof(AsyncIOCompletionData), aiocd_p,"OP2 aiocd contents"),
                                TRACE_DATA_PTR(nextQElement_p,"nextElement ptr"),
                                TRACE_DATA_RAWDATA(sizeof(ElementDT),(ElementDT*)(aiocd_p->asyncIOCompletionQ.queue_header.head),"aiocdQueElement head should be next element ptr"),
                                TRACE_DATA_END_PARMS);
                }
            }

#ifdef AIOCD_PLO_DEBUG
            // increment the PLO call -- for debug
            csIntInc(&aiocd_p->aiodebug_data.totalPLOcalls, 0);
#endif

            if (ploRC == 0) {
                // Clear the removed elements ptrs
                clearPtrs(firstQElement_p);

                topCompletionQueueElement_p = (CompletionQElement*)firstQElement_p;

                // Copy the values to return to caller
                memcpy_dk(currentIOCDE_p, &topCompletionQueueElement_p->ioCompletedInfo, sizeof(*currentIOCDE_p), 8);
                numberIOCDEsCompleted++;

                if (TraceActive(trc_level_detailed)) {
                    TraceRecord(trc_level_detailed,
                                TP(_TP_MVS_AIOSERVICES_PUSHHANDLER_PLO_LOOP6),
                                "getioev2 - pushHandlerPLO: Processing firstElement IOCompletedInfo from Que",
                                TRACE_DATA_RAWDATA(sizeof(CompletionQElement), topCompletionQueueElement_p,"CompletionQElement"),
                                TRACE_DATA_HEX_LONG(topCompletionQueueElement_p->ioCompletedInfo.iocde_ChannelIdentifier,
                                                    "ChannelIdentifier"),
                                TRACE_DATA_HEX_LONG(topCompletionQueueElement_p->ioCompletedInfo.iocde_CallId,
                                                    "CallId           "),
                                TRACE_DATA_HEX_LONG(topCompletionQueueElement_p->ioCompletedInfo.iocde_ReturnCode,
                                                    "ReturnCode       "),
                                TRACE_DATA_HEX_LONG(topCompletionQueueElement_p->ioCompletedInfo.iocde_BytesAffected,
                                                    "BytesAffected    "),
                                TRACE_DATA_END_PARMS);
                }

                // at this point i release the storage
                freeCellPoolCell(aiocd_p->aioelement_cellpool_id, topCompletionQueueElement_p);
                topCompletionQueueElement_p = NULL;

#ifdef AIOCD_PLO_DEBUG
                // decrement the ComIOQueDepth -- for debug
                csIntInc(&aiocd_p->aiodebug_data.removeQueEl, 0);
#endif
            } else {
#ifdef AIOCD_PLO_DEBUG
                // increment the deQPLOFailed -- for debug
                csIntInc(&aiocd_p->aiodebug_data.deQPLOFailed, 0);
#endif
            }
        } else { // completedIO queue empty
            if (TraceActive(trc_level_detailed)) {
                                            TraceRecord(trc_level_detailed,
                                            TP(_TP_MVS_AIOSERVICES_PUSHHANDLER_PLO_LOOP7),
                                            "empty compIO_Q,",
                                            TRACE_DATA_END_PARMS);
            }

            // Bail out, don't wait, if we have got at least 1 completed event
            if (numberIOCDEsCompleted > 0) {
                // Free the waiter Stackelement if we previously obtained one
                if (handlerWaiterStackElement_p != NULL) {
                    // Release storage of the stackElement
                    freeCellPoolCell(aiocd_p->aioelement_cellpool_id, handlerWaiterStackElement_p);
                    handlerWaiterStackElement_p = NULL;
                }

                break;
            }

            //get a PET from PetVet( allocate if not there)
            if (allocatedPauseToken == FALSE) {
                if (TraceActive(trc_level_detailed)) {
                                                TraceRecord(trc_level_detailed,
                                                TP(_TP_MVS_AIOSERVICES_PUSHHANDLER_PLO_LOOP8),
                                                "empty compIO_Q, before pickup petvet",
                                                TRACE_DATA_END_PARMS);
                }

                int petRC = pickup(mypetvet, &pauseElement);

                if (petRC == 0) {
                    allocatedPauseToken = TRUE;
                } else {
                    // Fillin IOCDEntry for error return
                    memcpy_dk(currentIOCDE_p, &NOPET_IOCDE, sizeof(*currentIOCDE_p), 8);
                    numberIOCDEsCompleted++;

                    break;
                }
            }
            // create a HandlerWaiterStackElement
            if (handlerWaiterStackElement_p == NULL) {
                if (TraceActive(trc_level_detailed)) {
                    TraceRecord(trc_level_detailed,
                                TP(_TP_MVS_AIOSERVICES_PUSHHANDLER_PLO_LOOP9),
                                "empty compIO_Q before getCell for handlerStackEle",
                                TRACE_DATA_END_PARMS);
                }

                handlerWaiterStackElement_p = (CompletionStackElement*) getCellPoolCell(aiocd_p->aioelement_cellpool_id);
                if (handlerWaiterStackElement_p != NULL) {
                    memset(handlerWaiterStackElement_p, 0, sizeof(*handlerWaiterStackElement_p));
                    // set the HandlerWaiterStackElement eyeCatcher
                    memcpy(handlerWaiterStackElement_p->eyeCatcher, "ZAIOHWSE", sizeof(handlerWaiterStackElement_p->eyeCatcher));

                    // put the stackElement containing PET on the top of the stack
                    memcpy(handlerWaiterStackElement_p->active_pauseElement, pauseElement, sizeof(iea_PEToken));
                } else {
                    // Error obtaining stackelement from the pool

                    // Fillin IOCDEntry for error return
                    memcpy_dk(currentIOCDE_p, &OOM_IOCDE, sizeof(*currentIOCDE_p), 8);
                    numberIOCDEsCompleted++;

                    break;
#ifdef AIOCD_PLO_DEBUG
                    csIntInc(&aiocd_p->aiodebug_data.buildStackEleError, 0);
#endif
                }
            }
            newStackElement_p = &(handlerWaiterStackElement_p->stackElement);

            // put the stackElement containing PET on the top of the stack
            memcpy(replaceStack_p, &(oldCompareArea.handlerWaiterStack), sizeof(*replaceStack_p));

            if (TraceActive(trc_level_detailed)) {
                TraceRecord(trc_level_detailed,
                            TP(_TP_MVS_AIOSERVICES_PUSHHANDLER_PLO_LOOP10),
                            "before check isStackEmpty(replaceStack)",
                            TRACE_DATA_RAWDATA(sizeof(AsyncIOCompletionData), aiocd_p,"aiocd contents"),
                            TRACE_DATA_RAWDATA(sizeof(AsyncIOCompletionData), &oldCompareArea,"oldCompareArea contents"),
                            TRACE_DATA_RAWDATA(sizeof(storeArea1.storeValue), replaceStack_p,"replacestack contents"),
                            TRACE_DATA_END_PARMS);
            }

            // replaceStack_p is using storeArea1.storeValue
            // replaceStackElement_p is using storeArea2.storeValue
            storeArea1.storeLocation_p = &(aiocd_p->handlerWaiterStack);
            if(!isStackEmpty(replaceStack_p)) { //stack not empty
                if (TraceActive(trc_level_detailed)) {
                    TraceRecord(trc_level_detailed,
                                TP(_TP_MVS_AIOSERVICES_PUSHHANDLER_PLO_LOOP11),
                                "empty compIO_Q, hdlrStack not empty",
                                TRACE_DATA_END_PARMS);
                }

                newStackElement_p->element_next_p = replaceStack_p->stack_header.first;
                newStackElement_p->element_prev_p = NULL;
                firstStackElement_p = replaceStack_p->stack_header.first;
                *replaceStackElement_p = *firstStackElement_p;
                replaceStackElement_p->element_prev_p = newStackElement_p;
                replaceStack_p->stack_header.first = newStackElement_p;

                if (TraceActive(trc_level_detailed)) {
                    TraceRecord(trc_level_detailed,
                                TP(_TP_MVS_AIOSERVICES_PUSHHANDLER_PLO_LOOP12),
                                "hndlrstack notempty, before CSDSTX parmlist",
                                TRACE_DATA_PTR(newStackElement_p,"newStackElement"),
                                TRACE_DATA_PTR( firstStackElement_p,"prev firstStackElemenet, should be different from newStackEle"),
                                TRACE_DATA_PTR( replaceStack_p->stack_header.first,"replaceStack.stack_header.first, should be same as newStackElement"),
                                TRACE_DATA_END_PARMS);
                }

                storeArea2.storeLocation_p = firstStackElement_p;
                ploRC = ploCompareAndSwapAndDoubleStoreQuadWord(swapArea.compare_p, &swapArea, &storeArea1, &storeArea2);

                if (TraceActive(trc_level_detailed)) {
                    TraceRecord(trc_level_detailed,
                                TP(_TP_MVS_AIOSERVICES_PUSHHANDLER_PLO_LOOP13),
                                "after PLO CSDSTX , hndlr stack not empty",
                                TRACE_DATA_INT(ploRC, "ploRC"),
                                TRACE_DATA_RAWDATA(sizeof(AsyncIOCompletionData), aiocd_p,"OP2 aiocd contents"),
                                TRACE_DATA_END_PARMS);
                }
            } else { //stack empty
                if (TraceActive(trc_level_detailed)) {
                                                TraceRecord(trc_level_detailed,
                                                TP(_TP_MVS_AIOSERVICES_PUSHHANDLER_PLO_LOOP14),
                                                "empty compIO_Q, hndlrstack empty insert ele",
                                                TRACE_DATA_PTR(newStackElement_p, "newStackElement"),
                                                TRACE_DATA_RAWDATA(sizeof(newStackElement_p),newStackElement_p," contents of newStackElement"),
                                                TRACE_DATA_END_PARMS);
                }

                replaceStack_p->stack_header.first = newStackElement_p;
                newStackElement_p->element_next_p = NULL;
                newStackElement_p->element_prev_p = NULL;

                if (TraceActive(trc_level_detailed)) {
                    TraceRecord(trc_level_detailed,
                                TP(_TP_MVS_AIOSERVICES_PUSHHANDLER_PLO_LOOP15),
                                "hndlrstack empty, before PLO CSSTX ",
                                TRACE_DATA_RAWDATA(sizeof(AsyncIOCompletionData), aiocd_p,"OP2 aiocd contents"),
                                TRACE_DATA_RAWDATA(sizeof(Stack), replaceStack_p,"replaceStack contents"),
                                TRACE_DATA_PTR(newStackElement_p,"newStackElement"),
                                TRACE_DATA_RAWDATA(sizeof(Stack),&(aiocd_p->handlerWaiterStack),"aiocd-s stack before PLO 1st ele"),
                                TRACE_DATA_END_PARMS);
                }

                ploRC = ploCompareAndSwapAndStoreQuadWord(swapArea.compare_p, &swapArea, &storeArea1);

                if (TraceActive(trc_level_detailed)) {
                    TraceRecord(trc_level_detailed,
                                TP(_TP_MVS_AIOSERVICES_PUSHHANDLER_PLO_LOOP16),
                                "after PLO CSSTX ",
                                TRACE_DATA_INT(ploRC, "ploRC"),
                                TRACE_DATA_RAWDATA(sizeof(AsyncIOCompletionData), aiocd_p,"OP2 aiocd contents"),
                                TRACE_DATA_RAWDATA(sizeof(AsyncIOCompletionData),  &oldCompareArea,"old CompareAre contents"),
                                TRACE_DATA_END_PARMS);
                }
            }

#ifdef AIOCD_PLO_DEBUG
            // increment the PLO call -- for debug
            csIntInc(&aiocd_p->aiodebug_data.totalPLOcalls, 0);
#endif

            if (ploRC == 0) {
#ifdef AIOCD_PLO_DEBUG
                // increment the hndlrstack depth -- for debug
                csIntInc(&aiocd_p->aiodebug_data.hndlrStackDepth, 0);
#endif

                // ---------------------------------------------------------------------------------------------------
                // Warning:  if shutting down, the shutdownHandlers_PLO routine will free the Handler's CompletionStackElement
                // and start cleaning up the AIO Cellpools.  So, if we're released from shutdown we can't reference
                // our CompletionStackElement/handlerWaiterStackElement_p.
                // ----------------------------------------------------------------------------------------------------

                // Before pausing, check and bail if the server took a hard failure
                server_process_data* spd_p = getServerProcessData();
                if (spd_p->serializedFlags.serverHardFailureDetected) {
                    memcpy_dk(currentIOCDE_p, &HARDFAILURE_IOCDE, sizeof(*currentIOCDE_p), 8);

                    numberIOCDEsCompleted++;
                    break;
                }

                // Start a stimer unless directed to wait forever.
                MvsTimerID_t stimerID = 0;
                AsyncIOStimerParms timeoutRtnParm = {{0}};

                if (ioev2Parms->timeout) {
                    // stimer is using seconds...so convert from milli to secs and roundup.
                    int timeToWaitSec = (ioev2Parms->timeout + 999) / 1000;

                    // Start stimerm for caller supplied timeout routine and parms
                    // ------------------------------------------------------------------------------------

                    // Finish building parms to pass through to AsyncIO's timer routine(driven from STIMERM exit)
                    memcpy(timeoutRtnParm.handlerPet, pauseElement, sizeof(timeoutRtnParm.handlerPet));
                    timeoutRtnParm.aiocd_p = aiocd_p;
                    __stck(&timeoutRtnParm.startTime);  // OK to grab time here.  "timeout" is a time to wait value. Currently unused.

                    int stimerRC = setTimer((setTimerExitFunc_t*)handlerTimeoutRtn, &timeoutRtnParm, timeToWaitSec, mypetvet, FALSE, &stimerID);

                    if (stimerRC != 0) {
                        // Hmm. I think for now we'll go un-timed.  We already added ourself to the Handler stack.  So, we
                        // would have to remove ourself and return to the caller...which will drive us again most likely.
                        //
                        // Perhaps we should treat this path as an ABEND.  This should never happen...if it does would we like
                        // to know about it????
                    }
                }

                // Reset flag indicating that we used an obtained PET.
                allocatedPauseToken = FALSE;

                // Pause on the PET
                iea_return_code pauseRc;
                iea_release_code releaseCode;
                memset(&releaseCode, 0, sizeof(releaseCode));
                iea_auth_type petAuthType = IEA_AUTHORIZED;
                if (TraceActive(trc_level_detailed)) {
                    TraceRecord(trc_level_detailed,
                                TP(_TP_MVS_AIOSERVICES_PUSHHANDLER_PLO_LOOP17),
                                "getioev2: before iea4pse",
                                TRACE_DATA_RAWDATA(sizeof(releaseCode), &releaseCode, "releaseCode"),
                                TRACE_DATA_END_PARMS);
                }

                //should be in key0 and supervisor state to use the service
                unsigned char oldKey = switchToKey0();
                iea4pse(&pauseRc, petAuthType, pauseElement, pauseElement, releaseCode);
                //switch back to previous key 2
                switchToSavedKey(oldKey);

                if (spd_p->serializedFlags.serverHardFailureDetected) {
                    // Disable native trace on this thread...its hopefully going to end when it gets back to an LE
                    // Environment.
                    disableTraceLevelInServerTaskData();
                }

                if (TraceActive(trc_level_detailed)) {
                    TraceRecord(trc_level_detailed,
                                TP(_TP_MVS_AIOSERVICES_PUSHHANDLER_PLO_LOOP18),
                                "getioev2: after iea4pse",
                                TRACE_DATA_INT(pauseRc, "pauseRc of iea4pse"),
                                TRACE_DATA_RAWDATA(sizeof(releaseCode), &releaseCode, "releaseCode of iea4pse"),
                                TRACE_DATA_END_PARMS);
                }

                // Drive Cancel of Stimer if we started one (doesn't matter if it popped--still need to drive cancel to cleanup).
                if (stimerID) {
                    int cancelRC = cancelTimer(&stimerID);
                }

                // at this point SRB done, PET released, handler woke up
                // return the released PE to PetVet
                board(mypetvet, pauseElement);

                // If we are not shutting down gather completed IO information
                if (memcmp(&releaseCode, AIO_RELEASE_FORSHUTDOWN, sizeof(releaseCode)) != 0) {
                    // Copy the values to return to caller
                    memcpy_dk(currentIOCDE_p, &handlerWaiterStackElement_p->ioCompletedInfo, sizeof(*currentIOCDE_p), 8);
                    numberIOCDEsCompleted++;

                    if (TraceActive(trc_level_detailed)) {
                        TraceRecord(trc_level_detailed,
                                    TP(_TP_MVS_AIOSERVICES_PUSHHANDLER_PLO_LOOP19),
                                    "getioev2: Processing IOCompletedInfo from HandlerWaiterStack",
                                    TRACE_DATA_RAWDATA(sizeof(CompletionStackElement), handlerWaiterStackElement_p,"CompletionStackElement"),
                                    TRACE_DATA_HEX_LONG(handlerWaiterStackElement_p->ioCompletedInfo.iocde_ChannelIdentifier,
                                                        "ChannelIdentifier"),
                                    TRACE_DATA_HEX_LONG(handlerWaiterStackElement_p->ioCompletedInfo.iocde_CallId,
                                                        "CallId           "),
                                    TRACE_DATA_HEX_LONG(handlerWaiterStackElement_p->ioCompletedInfo.iocde_ReturnCode,
                                                        "ReturnCode       "),
                                    TRACE_DATA_HEX_LONG(handlerWaiterStackElement_p->ioCompletedInfo.iocde_BytesAffected,
                                                        "BytesAffected    "),
                                    TRACE_DATA_END_PARMS);
                    }

                    // release storage of the stackElement
                    freeCellPoolCell(aiocd_p->aioelement_cellpool_id, handlerWaiterStackElement_p);
                    handlerWaiterStackElement_p = NULL;

                    // If we received a TIMEOUT, then break out and return...the API back to Java can't handle a TIMEOUT and returned IOCDEs
                    if (currentIOCDE_p->iocde_ReturnCode == ASNYC_TIMEOUT_RETURNCODE) {
                        break;
                    }
                } else {
                    // Fill in IOCDEntry for handler under shutdown or hard failure
                    // Note: can't reference handlerWaiterStackElement_p, may be released by caller.
                    if (spd_p->serializedFlags.serverHardFailureDetected) {
                        memcpy_dk(currentIOCDE_p, &HARDFAILURE_IOCDE, sizeof(*currentIOCDE_p), 8);
                    } else {
                        memcpy_dk(currentIOCDE_p, &SHUTDOWN_IOCDE, sizeof(*currentIOCDE_p), 8);
                    }
                    numberIOCDEsCompleted++;
                    break;
                }
            } else {
#ifdef AIOCD_PLO_DEBUG
                // increment the ComIOQueDepth -- for debug
                csIntInc(&aiocd_p->aiodebug_data.hndlrPushPLOFailed, 0);
#endif
            }
        }
    } while(numberIOCDEsCompleted < ioev2Parms->iocdeMax);

    // Pass back the number of completed IO events.
    memcpy_dk(ioev2Parms->iocdeReturned, &numberIOCDEsCompleted, sizeof(*ioev2Parms->iocdeReturned), 8);


#ifdef AIOCD_PLO_DEBUG
    if (numberIOCDEsCompleted > aiocd_p->aiodebug_data.highestBatchValue) {
        csSetInt(&aiocd_p->aiodebug_data.highestBatchValue, numberIOCDEsCompleted);
        csSetInt(&aiocd_p->aiodebug_data.highestBatchValueInstances, 1);
    } else if (numberIOCDEsCompleted == aiocd_p->aiodebug_data.highestBatchValue) {
        csIntInc(&aiocd_p->aiodebug_data.highestBatchValueInstances, 0);
    }
#endif
}

#pragma noinline(shutdownHandlers_PLO)
void shutdownHandlers_PLO(AsyncIOCompletionData* aiocd_p) {
    static IOCDEntry SHUTDOWN_IOCDE = { 0, -1, 99, -1 };

    int ploRC = 0;
    AsyncIOCompletionData oldCompareArea;

    CompletionStackElement* currentWaiterStackElement_p = NULL;
    CompletionStackElement* nextWaiterStackElement_p = NULL;

    PloCompareAndSwapAreaQuadWord_t swapArea;
    PloStoreAreaQuadWord_t          storeArea1;  // Stack updates

    swapArea.compare_p = aiocd_p;
    AsyncIOCompletionData* replaceArea_p = (AsyncIOCompletionData*)&(swapArea.replaceValue);

    Stack*  replaceStack_p = (Stack*) &(storeArea1.storeValue);
    storeArea1.storeLocation_p = &(aiocd_p->handlerWaiterStack);

    do {
        // Make a local copy of all serialized stuff (SEQ #, stack and queue)
        memcpy(&oldCompareArea, aiocd_p, sizeof(AsyncIOCompletionData));

        // Get PLO Compare area
        memcpy(&(swapArea.expectedValue), &oldCompareArea, sizeof(swapArea.expectedValue));

        // Build new SwapArea
        memcpy(replaceArea_p, &(swapArea.expectedValue), sizeof(swapArea.replaceValue));
        replaceArea_p->sequenceNumber+=1;

        // Clear storage for PLO new values
        memset(&storeArea1.storeValue, 0, sizeof(storeArea1.storeValue));

        // If handlers
        if(!isStackEmpty(&(oldCompareArea.handlerWaiterStack))) {

            // Get stack of handlers
            memset(replaceStack_p, 0, sizeof(*replaceStack_p));
            currentWaiterStackElement_p = oldCompareArea.handlerWaiterStack.stack_header.first;

            if (TraceActive(trc_level_detailed)) {
                TraceRecord(trc_level_detailed,
                            TP(_TP_MVS_AIOSERVICES_SHUTDOWNHANDLERS_PLO_LOOP1),
                            "grab stack -- before PLO CSSTX",
                            TRACE_DATA_RAWDATA(sizeof(AsyncIOCompletionData), aiocd_p,"aiocd contents"),
                            TRACE_DATA_RAWDATA(sizeof(AsyncIOCompletionData), &oldCompareArea,"oldCompareArea contents"),
                            TRACE_DATA_RAWDATA(sizeof(storeArea1.storeValue), replaceStack_p,"replacestack contents"),
                            TRACE_DATA_END_PARMS);
            }

            ploRC = ploCompareAndSwapAndStoreQuadWord(swapArea.compare_p, &swapArea, &storeArea1);

#ifdef AIOCD_PLO_DEBUG
            // increment the PLO call -- for debug
            csIntInc(&aiocd_p->aiodebug_data.totalPLOcalls, 0);
#endif

            if (TraceActive(trc_level_detailed)) {
                TraceRecord(trc_level_detailed,
                            TP(_TP_MVS_AIOSERVICES_SHUTDOWNHANDLERS_PLO_LOOP2),
                            "grab stack -- after PLO CSSTX ",
                            TRACE_DATA_INT(ploRC, "ploRC"),
                            TRACE_DATA_RAWDATA(sizeof(AsyncIOCompletionData), aiocd_p,"OP2 aiocd contents"),
                            TRACE_DATA_RAWDATA(sizeof(AsyncIOCompletionData),  &oldCompareArea,"old CompareAre contents"),
                            TRACE_DATA_END_PARMS);
            }

            if (ploRC == 0) {
                do {

#ifdef AIOCD_PLO_DEBUG
                    // decrement the hndlrstack depth -- for debug
                    csIntDec(&aiocd_p->aiodebug_data.hndlrStackDepth);
#endif

                    // Fill in IOCompletedInfo for handler under shutdown
                    memcpy(&currentWaiterStackElement_p->ioCompletedInfo, &SHUTDOWN_IOCDE, sizeof(currentWaiterStackElement_p->ioCompletedInfo));

                    if (TraceActive(trc_level_detailed)) {
                        TraceRecord(trc_level_detailed,
                                    TP(_TP_MVS_AIOSERVICES_SHUTDOWNHANDLERS_PLO_LOOP3),
                                    "shutdownHandlers: About to release handler",
                                    TRACE_DATA_RAWDATA(sizeof(*currentWaiterStackElement_p), currentWaiterStackElement_p, "currentWaiterStackElement_p"),
                                    TRACE_DATA_HEX_LONG(currentWaiterStackElement_p->ioCompletedInfo.iocde_ChannelIdentifier,
                                                        "ChannelIdentifier"),
                                    TRACE_DATA_HEX_LONG(currentWaiterStackElement_p->ioCompletedInfo.iocde_CallId,
                                                        "CallId           "),
                                    TRACE_DATA_HEX_LONG(currentWaiterStackElement_p->ioCompletedInfo.iocde_ReturnCode,
                                                        "ReturnCode       "),
                                    TRACE_DATA_HEX_LONG(currentWaiterStackElement_p->ioCompletedInfo.iocde_BytesAffected,
                                                        "BytesAffected    "),
                                    TRACE_DATA_END_PARMS);
                    }

                    // Grab next pointer before releasing handler
                    nextWaiterStackElement_p = (CompletionStackElement*) currentWaiterStackElement_p->stackElement.element_next_p;

                    // Release the handler
                    iea_return_code releaseRc;
                    iea_auth_type authType = IEA_AUTHORIZED;
                    iea_release_code releaseCode;
                    memcpy(&releaseCode, AIO_RELEASE_FORSHUTDOWN, sizeof(releaseCode));

                    // Switch to key 0 since we are using IEA_AUTHORIZED and after the call to release , switch it back to Key 2
                    unsigned char oldKey = switchToKey0();
                    iea4rls(&releaseRc, authType, currentWaiterStackElement_p->active_pauseElement, releaseCode);
                    switchToSavedKey(oldKey);

                    // If release failed, add a flag into the stack element
                    if(releaseRc != 0) {
                        currentWaiterStackElement_p->stackElementErrorFlag = currentWaiterStackElement_p->stackElementErrorFlag | 2;
                    }

                    // Release storage of the Handler stackElement
                    freeCellPoolCell(aiocd_p->aioelement_cellpool_id, currentWaiterStackElement_p);

                    currentWaiterStackElement_p = nextWaiterStackElement_p;

                } while(currentWaiterStackElement_p != NULL);

            } else {
#ifdef AIOCD_PLO_DEBUG
                // increment the hndlrPopPLOFailed -- for debug
                csIntInc(&aiocd_p->aiodebug_data.hndlrPopPLOFailed, 0);
#endif
            }
        } else {
            // Bail, no handlers
            ploRC = 0;
        }
    } while(ploRC);
}

void aioGetSocketDescriptor(AIO_GetSocketDescriptor* parms) {

    RegistryToken* callerToken_p;   // key 8 token ptr
    RegistryToken  token;           // actual token in key 2
    RegistryToken* token_p = &token;
    aioconn* local_aioconn_p;

    //Note:  assuming this value is ptr to token, this needs to be changed when the interface changes to take entire token
    callerToken_p = parms->token;                          //  key 8 token ptr
    memcpy_sk(token_p, callerToken_p, sizeof(*token_p), 8);      // move the token (key8) into key 2

    // Verify token and retrieve Connection address
    int localRC = getAioConnFromRegistry(token_p, &local_aioconn_p);
    if (localRC == 0) {
        // Get the original handle and return to caller
        long handle = local_aioconn_p->readAiocb_p->aiofd;
        memcpy_dk(parms->socketInfo_p, &handle, sizeof(long), 8);

        if (TraceActive(trc_level_detailed)) {
            int l_read_aiocflags = local_aioconn_p->readAiocb_p->aiocflags;
            TraceRecord(trc_level_detailed,
                        TP(_TP_MVS_AIOSERVICES_GETSOCKET_DESCR),
                        "Called getSocketDescriptor",
                        TRACE_DATA_RAWDATA(sizeof(RegistryToken), token_p, "passed registry token"),
                        TRACE_DATA_HEX_LONG(handle, "original prepare handle"),
                        TRACE_DATA_END_PARMS);
        }

        // Unlock our use of the registry for this connection call
        registrySetUnused(token_p,TRUE); // to decrement the usecount
    }

    memcpy_dk(parms->rc_p, &localRC, sizeof(int), 8);
}

/**
 * Time out routine,  driven from STIMER Exit, to cover Handlers waiting for completed IO.
 *
 * @param timeoutParms_p Pointer to parameter data for time out routine.
 */
static void handlerTimeoutRtn(void* inParms_p) {
    AsyncIOStimerParms* timeoutParms_p = (AsyncIOStimerParms*) inParms_p;

    // Find and remove the handler from the aiocd.stack (use PET to find)
    //   if found then
    //      release with timeout release code

    int ploRC = 0;
    AsyncIOCompletionData oldCompareArea;

    CompletionStackElement* currentWaiterStackElement_p = NULL;
    ElementDT* previousWaiterStackElement_p = NULL;

    PloCompareAndSwapAreaQuadWord_t swapArea;
    PloStoreAreaQuadWord_t          storeArea1;  // Stack updates
    PloStoreAreaQuadWord_t          storeArea2;  // Stack updates

    swapArea.compare_p = timeoutParms_p->aiocd_p;
    AsyncIOCompletionData* replaceArea_p = (AsyncIOCompletionData*)&(swapArea.replaceValue);

    Stack*  replaceStack_p = (Stack*) &(storeArea1.storeValue);
    storeArea1.storeLocation_p = &(timeoutParms_p->aiocd_p->handlerWaiterStack);

    ElementDT* replaceStackElement_p = (ElementDT*) &(storeArea2.storeValue);
    ElementDT* replaceStackElement2_p;

    do {
        // Make a local copy of all serialized stuff (SEQ #, stack and queue)
        memcpy(&oldCompareArea, timeoutParms_p->aiocd_p, sizeof(AsyncIOCompletionData));

        // Get PLO Compare area
        memcpy(&(swapArea.expectedValue), &oldCompareArea, sizeof(swapArea.expectedValue));

        // Build new SwapArea
        memcpy(replaceArea_p, &(swapArea.expectedValue), sizeof(swapArea.replaceValue));
        replaceArea_p->sequenceNumber+=1;

        // Clear storage for PLO new values
        memset(&storeArea1.storeValue, 0, sizeof(storeArea1.storeValue));

        // If handlers
        if(!isStackEmpty(&(oldCompareArea.handlerWaiterStack))) {

            // Get stack of handlers
            currentWaiterStackElement_p = oldCompareArea.handlerWaiterStack.stack_header.first;

            previousWaiterStackElement_p = NULL;

            // Find target handler by matching the PET value
            int handlerFound = 0;
            do {
                // Is current element the Target Handler?
                if (memcmp(timeoutParms_p->handlerPet, currentWaiterStackElement_p->active_pauseElement, sizeof(timeoutParms_p->handlerPet)) == 0) {

                        handlerFound = 1;

                } else {
                    previousWaiterStackElement_p = (ElementDT*) currentWaiterStackElement_p;
                    currentWaiterStackElement_p  = (CompletionStackElement*) currentWaiterStackElement_p->stackElement.element_next_p;
                }
            } while (handlerFound == 0 && currentWaiterStackElement_p != NULL);


            // Remove the handler from the Stack
            if (handlerFound == 1) {
                // Grab next element ptr
                ElementDT* nextStackElement_p = currentWaiterStackElement_p->stackElement.element_next_p;

                if (currentWaiterStackElement_p == oldCompareArea.handlerWaiterStack.stack_header.first) {
                    // Target Handler is first on stack

                    // replaceStack_p is using storeArea1.storeValue
                    // replaceStackElement_p is using storeArea2.storeValue
                    storeArea1.storeLocation_p = &(timeoutParms_p->aiocd_p->handlerWaiterStack);
                    if (nextStackElement_p == NULL) {   //only 1 element
                        replaceStack_p->stack_header.first = NULL;

                        ploRC = ploCompareAndSwapAndStoreQuadWord(swapArea.compare_p, &swapArea, &storeArea1);
                    } else { // if more than 1 element

                        *replaceStackElement_p = *nextStackElement_p; // Copy  next element
                        replaceStackElement_p->element_prev_p = NULL;
                        replaceStack_p->stack_header.first = nextStackElement_p;

                        storeArea2.storeLocation_p = nextStackElement_p;
                        ploRC = ploCompareAndSwapAndDoubleStoreQuadWord(swapArea.compare_p, &swapArea, &storeArea1, &storeArea2);
                    }
                } else {
                    // Target Handler is nth on stack

                    if (previousWaiterStackElement_p == NULL) {
                        // Stack moved while we were following Element ptrs...need to loop around and start
                        // again.
                        continue;
                    }

                    // replaceStack_p is using storeArea1.storeValue
                    // replaceStackElement_p is using storeArea2.storeValue

                    // If the target Handler is the last element
                    if (nextStackElement_p == NULL) {   // Last element, update prev

                        storeArea2.storeLocation_p = previousWaiterStackElement_p;
                        *replaceStackElement_p = *previousWaiterStackElement_p; // Copy  prev element
                        replaceStackElement_p->element_next_p = NULL;

                        ploRC = ploCompareAndSwapAndStoreQuadWord(swapArea.compare_p, &swapArea, &storeArea2);
                    } else { // stack element is in the middle of the stack

                        storeArea2.storeLocation_p = nextStackElement_p;
                        *replaceStackElement_p = *nextStackElement_p; // Copy  next element
                        replaceStackElement_p->element_prev_p = previousWaiterStackElement_p;

                        storeArea1.storeLocation_p = previousWaiterStackElement_p;
                        replaceStackElement2_p = (ElementDT*) &(storeArea1.storeValue);
                        *replaceStackElement2_p = *previousWaiterStackElement_p; // Copy  prev element
                        replaceStackElement2_p->element_next_p = nextStackElement_p;

                        ploRC = ploCompareAndSwapAndDoubleStoreQuadWord(swapArea.compare_p, &swapArea, &storeArea1, &storeArea2);
                    }
                }

#ifdef AIOCD_PLO_DEBUG
                // increment the PLO call -- for debug
                csIntInc(&timeoutParms_p->aiocd_p->aiodebug_data.totalPLOcalls, 0);
#endif

                // If found and removed
                if (ploRC == 0) {
#ifdef AIOCD_PLO_DEBUG
                    // decrement the hndlrstack depth -- for debug
                    csIntDec(&timeoutParms_p->aiocd_p->aiodebug_data.hndlrStackDepth);

                    // Bump cnt for Handler Timedout
                    csIntInc(&timeoutParms_p->aiocd_p->aiodebug_data.hndlrTimedOut, 0);
#endif
                    // Clear the removed elements ptrs
                    clearPtrs(&(currentWaiterStackElement_p->stackElement));

                    // Release Handler with specific TIMEOUT release code
                    memcpy(&currentWaiterStackElement_p->ioCompletedInfo, &TIMEOUT_IOCDE, sizeof(currentWaiterStackElement_p->ioCompletedInfo));

                    iea_return_code releaseRc;
                    iea_auth_type authType = IEA_AUTHORIZED;
                    iea_release_code releaseCode;
                    memcpy(&releaseCode, AIO_RELEASE_FORTIMEOUT, sizeof(releaseCode));

                    // Switch to key 0 since we are using IEA_AUTHORIZED and after the call to release , switch it back to Key 2
                    unsigned char oldKey = switchToKey0();
                    iea4rls(&releaseRc, authType,  currentWaiterStackElement_p->active_pauseElement, releaseCode);
                    switchToSavedKey(oldKey);
                }
            } else {
                // Handler not found on stack
                break;
            }
        } else {
            // Bail, no handlers
            break;
        }
    } while(ploRC);


#ifdef AIOCD_PLO_DEBUG
    // Bump the count of timeouts for Handlers -- for debug
    csIntInc(&timeoutParms_p->aiocd_p->aiodebug_data.hndlrTimeoutRtn, 0);
#endif

    return;
}


/**
 * "Register" the AIO hard failure cleanup routine to enable it to be called on
 * a hard failure for the server.
 */
void registerAioHardFailureRtn(void) {
    server_process_data* spd = getServerProcessData();

    if (spd != NULL) {
        spd->hardFailureRegisteredCleanupRtn[HARDFAILURE_REGISTRY_SLOT_AIO] = hardFailureRegisteredCleanupRtn;
    }
}

/**
 * "DeRegister" the AIO hard failure cleanup routine.
 */
void deregisterAioHardFailureRtn(void) {
    server_process_data* spd = getServerProcessData();

    if (spd != NULL) {
        spd->hardFailureRegisteredCleanupRtn[HARDFAILURE_REGISTRY_SLOT_AIO] = NULL;
    }
}

/**
 * Cleanup routine driven from resmgr for a Task termination of a specially
 * marked thread.  This thread was started from Java during server startup to
 * cover a hard failure of the server (ex. a "kill -9") which bypassed normal
 * server shutdown.
 *
 * This routine will check for and release the any ResultHandler thread that
 * is currently in an MVS PAUSED state which will prevent LE from terminating the
 * server (hung server).
 *
 */
static void hardFailureRegisteredCleanupRtn(void) {

    server_process_data* spd = getServerProcessData();
    AsyncIOCompletionData* aiocd_p = (AsyncIOCompletionData*) spd->asyncio_completion_data_p;

    // Just shutdown the handlers.  They will check the server_process_data.serverHardFailureDetected
    // and perform the appropriate action.
    shutdownHandlers_PLO(aiocd_p);

    return;
}

#ifdef AIOCD_PLO_DEBUG
void writeDiagnosticsFromAIOCD(AsyncIOCompletionData* aiocd_p) {
    if ( aiocd_p != NULL ) {
        // PLO diagnostics--start
        char zbuf1[100];
        char zbuf2[100];
        char zbuf3[100];
        char zbuf4[100];
        char zbuf5[100];
        char zbuf6[100];
        char zbuf7[100];
        char zbuf8[100];
        char zbuf9[100];

        AsyncIODebugData debugData;
        memcpy(&debugData, &(aiocd_p->aiodebug_data), sizeof(debugData));

        snprintf(zbuf1, sizeof(zbuf1), "\nStart Diagnostics  ******* totalPLOcalls: %d ", debugData.totalPLOcalls);
        snprintf(zbuf2, sizeof(zbuf2), "insertQueDepth: %d removeQueDepth: %d ", debugData.addIOQueEl, debugData.removeQueEl);
        snprintf(zbuf3, sizeof(zbuf3), "enQPLOFail: %d deQPLOFail: %d ", debugData.enQPLOFailed, debugData.deQPLOFailed);
        snprintf(zbuf4, sizeof(zbuf4), "srbcnt: %d hndlrStckDepth: %d ", debugData.srbcount, debugData.hndlrStackDepth);
        snprintf(zbuf5, sizeof(zbuf5), "hndlrPushPLOFail: %d hndlrPopPLOFail: %d ", debugData.hndlrPushPLOFailed, debugData.hndlrPopPLOFailed);
        snprintf(zbuf6, sizeof(zbuf6), "buildQEError: %d buildSEError: %d ", debugData.buildQueEleError, debugData.buildStackEleError);

        snprintf(zbuf7, sizeof(zbuf7), "highestBatchValue: %d highestBatchValueInstances: %d ", debugData.highestBatchValue, debugData.highestBatchValueInstances);
        snprintf(zbuf8, sizeof(zbuf8), "hndlrTimeoutRtn: %d hndlrTimedOut: %d ", debugData.hndlrTimeoutRtn, debugData.hndlrTimedOut);
        snprintf(zbuf9, sizeof(zbuf9), "totalHandlerCalls: %d \n", debugData.totalHandlerCalls);

        write_to_programmer(zbuf1);
        write_to_programmer(zbuf2);
        write_to_programmer(zbuf3);
        write_to_programmer(zbuf4);
        write_to_programmer(zbuf5);
        write_to_programmer(zbuf6);
        write_to_programmer(zbuf7);
        write_to_programmer(zbuf8);
        write_to_programmer(zbuf9);
        // PLO diagnostics--end
    }
}
#endif


#pragma insert_asm(" IHAPSA")
#pragma insert_asm(" IKJTCB")
#pragma insert_asm(" IHASTCB")
#pragma insert_asm(" IHAASCB")
#pragma insert_asm(" IEANTASM")
