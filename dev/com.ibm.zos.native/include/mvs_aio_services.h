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


#ifndef MVS_AIO_SERVICES_H_
#define MVS_AIO_SERVICES_H_

#include <ieac.h>
#include "common_defines.h"
#include "gen/bpxyaio.h"
#include "mvs_aio_common.h"
#include "mvs_storage.h"
#include "stack_services.h"
#include "util_queue_services.h"
#include "util_registry.h"

typedef struct aio aiocb;


// --------------------------------------------------------------------
// Asynchronous I/O Connection data - this contains Read/Write
// AIOCBs, the storage key value of each (2/8), pointers to the I/O
// vectors for read/write, and the read/write call ID. This also
// contains the associated read/write AIOCB index values, and for
// authorized mode, a copy of the pause element token (PET).
// --------------------------------------------------------------------
typedef struct {
    aiocb* readAiocb_p;                                    /* 0x000 */
    iovec* readIovec_p;                                    /* 0x008 */
    char   reserved1[4];                                   /* 0x010 */
    int    readIovecLength;                                /* 0x014 */
    long   readCallId;                                     /* 0x018 */
    aiocb* writeAiocb_p;                                   /* 0x020 */
    iovec* writeIovec_p;                                   /* 0x028 */
    char   reserved2[4];                                   /* 0x030 */
    int    writeIovecLength;                               /* 0x034 */
    long   writeCallId;                                    /* 0x038 */
    // Warning: macro SAIOPRL has offset dependency on the following field
    struct server_process_data* pgoo;                  /* DEP 0x040 */
    RegistryToken aioConn_RegToken;                        /* 0x048 */
} aioconn;                                                 /* 0x088 */



// The following defines data collected for AsynIO debugging.
//
// NOTE: To enable the diag/debug related to the following structure
// uncomment the following #define in mvs_aio_common.h and re-compile.
//  #define AIOCD_PLO_DEBUG
//
// Also, may want to consider enabling some messages to help with
// diag/debug (again in mvs_aio_common.h):
//  #define AIOCD_PLO_DEBUG_MSGS
typedef struct AsyncIODebugData {

    int addIOQueEl;                                        /* 0x000 */
    int removeQueEl;                                       /* 0x004 */
    int enQPLOFailed;                                      /* 0x008 */
    int deQPLOFailed;                                      /* 0x00C */

    int srbcount;                                          /* 0x010 */
    int hndlrStackDepth;                                   /* 0x014 */
    int hndlrPushPLOFailed;                                /* 0x018 */
    int hndlrPopPLOFailed;                                 /* 0x01C */

    int totalPLOcalls;                                     /* 0x020 */
    int displayInterval;                                   /* 0x024 */
    int buildQueEleError;                                  /* 0x028 */
    int buildStackEleError;                                /* 0x02C */

    int highestBatchValue;                                 /* 0x030 */
    int highestBatchValueInstances;                        /* 0x034 */
    int hndlrTimeoutRtn;                                   /* 0x038 */
    int hndlrTimedOut;                                     /* 0x03C */

    int totalHandlerCalls;                                 /* 0x040 */
    int _rsvd[3];
} AsyncIODebugData;                                        /* 0x050 */

typedef struct AsyncIO_aiocdFlags {
    int  _reserved1: 30,

         // Setting of flag "owns" expanding the SRB Cellpool
         aiocdFlagsExpanding_aiosrb_cellpool:1,

         // Flag set by AsyncIO Srb Exit when it detects that no
         // more cells are available.  Reset, by various asyncIO
         // metal C routines that detect and expand the cellpool.
         // WARNING: offset/bit mask dependency (SAIOPRL).
         aiocdFlagsExpand_aiosrb_cellpool:1;
} AsyncIO_aiocdFlags;
#define SAIOPRL_AIOCDFLAGSEXPAND_AIOSRB_CELLPOOL    0x00000001 // Bit 32
#define SAIOPRL_AIOCDFLAGSEXPANDING_AIOSRB_CELLPOOL 0x00000002 // Bit 31

/**
 * The AsyncIOCompletionData is hung off the server PGOO at asyncio_completionq_p.
 *
 */
typedef struct AsyncIOCompletionData {
    // use for PLO compare area
    int sequenceNumber;                                    /* 0x000 */
    // reserve to make it double word(8 bytes) aligned
    char reserved1[12];                                    /* 0x004 */
    // Stack of Handlers waiting for completed IO events
    Stack handlerWaiterStack;                              /* 0x010 */
    char restricted[8];                                    /* 0x018 */
    // Queue of completed IO events for Handlers to process
    Que asyncIOCompletionQ;                                /* 0x020 */

    char reserved3[4];                                     /* 0x030 */

    // Serialized Flags...managed with compare and swap not
    // part of the PLO managed structures above.
    volatile AsyncIO_aiocdFlags aiocdFlags;              /*DEP 0x34 */


    // Cellpool for native Connections (aioconn)
    long long aioconn_cellpool_id;                         /* 0x038 */
    // Cellpool for Queue and Stack Elements used for handling
    // completed IO from the async SRB
    long long aioelement_cellpool_id;                      /* 0x040 */
    // Cells must be on 1Meg boundaries for use as Stack storage
    // Warning: Offset dependency on SRB cellpool in SAIOPRL macro
    long long aiosrb_cellpool_id;                       /*DEP 0x048 */

    // TToken for storage ownership.  (IPT or jobstep).
    // Warning: Offset dependency on SRB cellpool in SAIOPRL macro
    TToken aio_TToken;                                  /*DEP 0x050 */
#ifdef AIOCD_PLO_DEBUG
    AsyncIODebugData aiodebug_data;     /* 0x060 - 0x0B0 */
#endif
} AsyncIOCompletionData;                                   /* 0x060 */

AsyncIOCompletionData* getAsyncIOCompletionData();
void aio_exit_routine( aiocb *aiocb_p,char  *workptr,int   *worklen );

/**
 * A completion queue element.
 *
 * Note: The CompletionQElement and the CompletionStackElement shared the same
 * cellpool (AsyncIOCompletionData.aioelement_cellpool_id).
 */
typedef struct CompletionQElement {
    // QueueElement needs to be the first entry here because during push, we are pushing QueueElement ptr, and
    // during pop, we are casting to CompletionQueueElement ptr, if StackElement is the first entry, it can find the address correctly.
    ElementDT queueElement;                                /* 0x000 */
    char eyeCatcher[8];                                    /* 0x010 */
    char reserved[16];                                     /* 0x018 */
    IOCDEntry ioCompletedInfo;                             /* 0x028 */
    char reserved2[8];                                     /* 0x048 */
} CompletionQElement;                                      /* 0x050 */

/**
 * A Completion Stack element.
 *
 * Note: The CompletionQElement and the CompletionStackElement shared the same
 * cellpool (AsyncIOCompletionData.aioelement_cellpool_id).
 */
typedef struct CompletionStackElement {
    // StackElement needs to be the first entry here because during push, we are pushing StackElement ptr, and
    // during pop, we are casting to CompletionStackElement ptr, if StackElement is the first entry, it can find the address correctly.
    ElementDT stackElement;                                /* 0x000 */
    char eyeCatcher[8];                                    /* 0x010 */
    // current active PET 16 bytes
    iea_PEToken active_pauseElement;                       /* 0x018 */
    IOCDEntry ioCompletedInfo;                             /* 0x028 */
    int stackElementErrorFlag;                             /* 0x048 */
    char reserved2[4];                                     /* 0x04C */
} CompletionStackElement;                                  /* 0x050 */

/**
 * Parmlist for Stimer related to Java Handler threads coming native
 * to retrieve completed IO.
 */
typedef struct  AsyncIOStimerParms {
    // PET of Handler to wakeup at time of stimer set.
    char               handlerPet[16];                     /* 0x000 */

    /** STCK when started ticking. */
    unsigned long long startTime;                          /* 0x010 */

    /** Pointer to target AIOCD. */
    AsyncIOCompletionData*  aiocd_p;                       /* 0x018 */

} AsyncIOStimerParms;                                      /* 0x020 */

#endif /* MVS_AIO_SERVICES_H_ */
