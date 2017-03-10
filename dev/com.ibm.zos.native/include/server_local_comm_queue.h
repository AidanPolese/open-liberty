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
#ifndef _BBOZ_SERVER_LOCAL_COMM_QUEUE_H
#define _BBOZ_SERVER_LOCAL_COMM_QUEUE_H

#include <ieac.h>
#include "server_lcom_services.h"
#include "server_local_comm_data_store.h"
#include "stack_services.h"


/** A queue with a 'read pending' flag (blue queue). */
#define LOCAL_COMM_READ_PENDING_QUEUE_TYPE 1

/** A queue with a pause element (black queue). */
#define LOCAL_COMM_PAUSE_ELEMENT_QUEUE_TYPE 2

/** Initial queue state. */
#define LOCAL_COMM_QUEUE_STATE_INITIAL 0

/** Ready queue state. */
#define LOCAL_COMM_QUEUE_STATE_READY 1

/** Quiescing queue state. */
#define LOCAL_COMM_QUEUE_STATE_QUIESCING 2

/** Terminating queue state. */
#define LOCAL_COMM_QUEUE_STATE_TERMINATING 3


/** Add queue return code indicating no further action is required. */
#define LOCAL_COMM_ADD_QUEUE_OK 0

/** Add queue return code indicating we need to release the black queue. */
#define LOCAL_COMM_ADD_QUEUE_NOTIFY_PET 1

/** Add queue return code indicating that we could not obtain a queue element cell */
#define LOCAL_COMM_ADD_QUEUE_NO_CELL -4

/** Add queue return code indicating that the queue is full. */
#define LOCAL_COMM_ADD_QUEUE_FULL -3

/** Add queue return code indicating that the queue state prevents the add. */
#define LOCAL_COMM_ADD_QUEUE_BAD_STATE -2

/** Add queue return code indicating an unknown error has occurred. */
#define LOCAL_COMM_ADD_QUEUE_UNKNOWN_ERROR -1

/**
 * Adds a piece of work to the queue.  The caller may need to take some action
 * depending on the type of queue this is.  The action will be described by
 * the return code.
 *
 * @param queue_p The queue to be operated on.
 * @param work_p The work to add to the queue.
 *
 * @return A return code describing the action required by the caller.  One of:
 *         LOCAL_COMM_ADD_QUEUE_OK - success
 *         LOCAL_COMM_ADD_QUEUE_NOTIFY_PET - This is a blue queue and the PET of
 *                                           the corresponding black queue needs
 *                                           to be notified.
 *         LOCAL_COMM_ADD_QUEUE_FULL - The queue is full and the caller must
 *                                     try to add the work element later.
 *         LOCAL_COMM_ADD_QUEUE_BAD_STATE - The queue is shutting down and the
 *                                          add could not be processed.
 *         LOCAL_COMM_ADD_QUEUE_UNKNOWN_ERROR - An unknown error occurred.
 */
//int addToQueue(LocalCommMessageQueue_t* queue_p, void* work_p);

/** Remove queue return code indicating work was successfully obtained. */
#define LOCAL_COMM_REMOVE_QUEUE_OK 0

/** Remove queue return code indicating no work was available.  The caller should
 *  attept to retrieve work from the corresponding black queue.
 */
#define LOCAL_COMM_REMOVE_QUEUE_EMPTY 1

/** Remove queue return code indicating no work was available.  The work_p pointer
 *  is set to point to a pause element token which will be released when work is
 *  available.
 */
#define LOCAL_COMM_REMOVE_QUEUE_EMPTY_PET 2

/** Remove queue return code indicating that the queue state prevents the remove. */
#define LOCAL_COMM_REMOVE_QUEUE_BAD_STATE -2

/** Remove queue return code indicating that an unknown error has occurred. */
#define LOCAL_COMM_REMOVE_QUEUE_UNKNOWN_ERROR -1

/**
 * The size of the rawData field in the REQUESTTYPE_FFDC work queue element
 * requestSpecificParms.
 */
#define REQUESTTYPE_FFDC_RAWDATA_SIZE 68


/*********************************************************************************************/
/* Work queue constructs                                                                     */
/*********************************************************************************************/


#pragma pack(1)
/**
 * The Work Queue Element structure for the server.
 *
 * This is the BBGZLWQE area.
 *
 * WARNING: This structure is also mapped in Java:
 *  (com/ibm/ws/zos/channel/local/queuing/internal/NativeWorkRequest.java)
 */
typedef struct LocalCommWorkQueueElement {
    ElementDT                stackElement_header;                    /* 0x000 */

    /**
     * The eye catcher for this control block ("BBGZLWQE")
     */
    unsigned char            eyecatcher[8];                          /* 0x010 */

    /**
     * The version number of this control block.
     */
    short                    version;                                /* 0x018 */

    /**
     * The length of the storage allocated for this control block.
     */
    short                    length;                                 /* 0x01A */

    /**
     * Work Request Type (see definitions below)
     */
    short                    requestType;                            /* 0x01C */

    /**
     * Miscellaneous flags.
     */
    short                    requestFlags;                           /* 0x01E */

    /**
     * Creation Time of work request
     */
    unsigned long long       createStck;                             /* 0x020 */

    /**
     * Local Comm Client Connection Handle
     */
    unsigned char            clientConnHandle[16];                   /* 0x028 */

    /**
     * Additional request specific parms
     */
    union {                                                          /* 0x038 */
        // REQUESTTYPE_CONNECT Additional Parms              
        struct {
            // Client Shared Memory Objects to attach
            void* bbgzldat_p;                                        
            void* bbbzlocl_p;
            // User token to use when accessing Client Shared memory objects (LSCL Address)
            long long sharedMemoryUserToken;
        } connectParms;

        // REQUESTTYPE_CONNECTRESPONSE Additional Parms
        struct {
            short failureCode;
        } connectResponseParms;

        // REQUESTTYPE_FFDC additional parms
        struct {
            int tp;
            char rawData[REQUESTTYPE_FFDC_RAWDATA_SIZE];
        } ffdcParms;

        // Full requestSpecificParms area
        char requestSpecificParmsCharArea[72];                       /* 0x038 */
    } requestSpecificParms;

} LocalCommWorkQueueElement;                                         /* 0x080 */
#pragma pack(reset)

#define SERVER_LCOM_WRQE_EYECATCHER_INUSE "BBGZLWQE"
#define SERVER_LCOM_WRQE_EYECATCHER_FREE  "LWQEBBGZ"

#define LCOMWORKQUEUEELEMENT_CURRENT_VER    1
#define LCOM_SERVER_BLACKQUEUE_LIMIT        1024
#define LCOM_WRQ_CELLPOOL_GROWTH_SIZE       32 * 1024

/**
 * LComWorkQueueElement "requestType" definitions.
 * WARNING: These work request types are also mapped in Java:
 * com.ibm.ws.zos.channel.local.queuing.NativeWorkRequestType.java
 */
#define REQUESTTYPE_CONNECT                 1
#define REQUESTTYPE_CONNECTRESPONSE         2
#define REQUESTTYPE_DISCONNECT              3
#define REQUESTTYPE_SEND                    4
#define REQUESTTYPE_READREADY               5
#define REQUESTTYPE_FFDC                    6

#define REQUESTTYPE_CONNECTRESPONSE_OK            0
#define REQUESTTYPE_CONNECTRESPONSE_FAIL_STOPPING 1

#pragma pack(1)

/** Local Comm Work Queue Compare area. */
typedef struct LocalComWQE_PLO_CS_Area {
    /**
     * WARNING.  A dependency exists between this area and the mapping in LocalCommHandle_PLO_Area.
     * The reason is that both the WRQ code and Handle related updates use the same Compare area.  The
     * WORK queue code only needs the "ploSequenceNumber", but the Handle code maps and uses the entire
     * 16 bytes.  So, the "ploSequenceNumber" needs to be the same size and NO further bits can be used
     * in the WRQ code on this Compare area.
     */
    unsigned int ploSequenceNumber;

    char _filler[12];
} LocalComWQE_PLO_CS_Area_t;

typedef struct  LocalCommWRQ_PLO_Area {
    ElementDT*         wrqHead_p;                                /* 0x000 */

    int                wrqElementCount;                          /* 0x008 */
    /** Flags. */
    struct {
        /** A close connection request has been issued.  */
        int closingWorkQueue : 1;

        /** A close connection request has been issued and the queue has been drained. */
        int closedDrainedWorkQueue : 1;

        /** Available flag space. */
        int _available : 30;
    } flags;                                                     /* 0x00C */
} LocalCommWRQ_PLO_Area_t;

typedef struct LocalCommWorkQueue {
    /**
     * The LOCL containing the cell pool id used to allocate LComWorkQueueElements.
     */
    struct localCommClientAnchor* LOCL_p;                            /* 0x000 */

    /**
     * PLO Compare Area for Work Queue (contains PLO Sequence #).  This is used
     * as the serialization address (PLO PLT) as well.  For Clients this points
     * into the Handle for the connection.  For a server work queue this points
     * into the LOCL where the work queue resides.
     */
    LocalComWQE_PLO_CS_Area_t* wrqPLO_CS_p;                          /* 0x008 */

    /**
     * The PLO serialized stack of WRQEs and Work Queue Flags.
     * Note: Must be on Quadword boundary and 16 Bytes.
     */
    LocalCommWRQ_PLO_Area_t    wrqPLO_Area;                          /* 0x010 */

    /**
     * Maximum allowable concurrent work elements
     */
    int                        wrqeLimit;                            /* 0x020 */
    char                       _reserved1[4];                        /* 0x024 */

    /**
     * Previously returned list of Work Requests taken from the WRQ.
     * This is the current list of Work Requests being processed by the Local
     * comm java services.
     */
    LocalCommWorkQueueElement* currentWRQE_List;                     /* 0x028 */

    /**
     * Work Request Pause Element Token (MVS PET). Note: Quadword alignment.
     * Also, any updates to the wrq_pet need to be made by PLO.
     */
    char                       wrq_pet[16];                          /* 0x030 */

    char                       _reserved2[32];                       /* 0x040 */
} LocalCommWorkQueue;                                                /* 0x060 */
#pragma pack(reset)

/**
 * Retrieve Work Request Queue Element(s) anchored in either the BBGZLOCL (ie.
 * get list of new work or a single piece) for the server, OR, the BBGZLHDL for the client.
 */
struct LocalCommStimerParms;
int waitOnWork(LocalCommWorkQueue* workQueue_p, LocalCommWorkQueueElement** outWRQE_p, int timeToWait, void* timeoutRtn_p, struct LocalCommStimerParms* timeoutRtnParms_p, int singleRequestDesired, long long otherWorkToDo);
static int LCOM_WRQ_WAITONWORK_SINGLEREQUEST = 1;
static int LCOM_WRQ_WAITONWORK_MULTREQUEST   = 2;

#define LCOM_WRQ_WAITONWORK_RC_OK                  0
#define LCOM_WRQ_WAITONWORK_RC_BADSTATE            8
#define LCOM_WRQ_WAITONWORK_RC_PAUSEFAILED        12   // Failed issuing PAUSE to wait for work
#define LCOM_WRQ_WAITONWORK_RC_SETTIMER_FAILED    16
#define LCOM_WRQ_WAITONWORK_RC_TIMEDOUT           20
#define LCOM_WRQ_WAITONWORK_RC_STOPLIS            24   // Channel shutting down while we were waiting (Note: Check in Java.  Must keep in sync with NativeRequestHandler)
#define LCOM_WRQ_WAITONWORK_RC_UNKWN_REL          28   // Unknown release code received after PAUSE
#define LCOM_WRQ_WAITONWORK_RC_SKIP_PAUSE         32   // Skipped pause because caller had other things to do.
#define LCOM_WRQ_WAITONWORK_RC_SERVERHARD_FAILURE 36   // Hard Failure detected by resmgr, server dying.

/**
 * Cleanup the list of Work Request Queue Elements anchored in the BBGZLCOM as
 * returned previously.
 */
void releaseReturnedWRQEs(LocalCommWorkQueue* workQueue_p);

/**
 * Release Listener PET with code
 */
static const char LCOM_WRQ_PET_CHECK_QUEUE[3]     = "CHK";
static const char LCOM_WRQ_PET_STOP_LISTENER[3]   = "STP";
static const char LCOM_WRQ_PET_CLOSING_INBOUND[3] = "CLS";
static const char LCOM_WRQ_PET_TIMEDOUT[3]        = "TMO";
int releaseWRQ_PET(LocalCommWorkQueue* workQueue_p, const char * releaseCode);


/**
 * Initialize the Work Queue ("Black Queue").
 *
 */
int initializeWorkQueue(LocalCommWorkQueue* workQueue_p,
                        struct localCommClientAnchor* LOCL_p,
                        LocalComWQE_PLO_CS_Area_t* ploCS_Area_p);

/**
 * Set flag to stop work from being queued to this Work Queue
 */
int setNoMoreWork(LocalCommWorkQueue* targetWorkQ_p);

/*********************************************************************************************/
/* Data Queue constructs                                                                     */
/*********************************************************************************************/

/**
 * The Work Queue Element structure for the server.
 *
 * This is the BBGZLDQE area.
 */
#pragma pack(1)
typedef struct LocalCommDataQueueElement {
    ElementDT                stackElement_header;                    /* 0x000 */

    /**
     * The eye catcher for this control block ("BBGZLDQE")
     */
    unsigned char            eyecatcher[8];                          /* 0x010 */

    /**
     * The version number of this control block.
     */
    short                    version;                                /* 0x018 */

    /**
     * The length of the storage allocated for this control block.
     */
    short                    length;                                 /* 0x01A */

    /**
     * Work Request Type (see definitions below)
     */
    short                    requestType;                            /* 0x01C */

    /**
     * Miscellaneous flags.
     */
    short                    requestFlags;                           /* 0x01E */

    /**
     * Creation Time of work request
     */
    unsigned long long       createStck;                             /* 0x020 */

    /**
     * Pointer to the "data".  Actually a LocalCommDataCell_t ('BBGZLMSG')
     */
    LocalCommDataCell_t*     bbgzlmsg_p;                             /* 0x028 */


    /**
     * Sharing a cellpool with Work Queue Elements.  The cellsizes are
     * 128 bytes.
     */
    char                     _reserved2[80];                         /* 0x030 */
} LocalCommDataQueueElement;                                         /* 0x080 */
#pragma pack(reset)


#define SERVER_LCOM_DRQE_EYECATCHER_INUSE "BBGZLDQE"
#define SERVER_LCOM_DQE_EYECATCHER_FREE  "LDQEBBGZ"

#define LCOMDATAQUEUEELEMENT_CURRENT_VER    1

#pragma pack(1)
typedef struct LocalCommCommonQueueElement {
    union {                                                          /* 0x000 */
        LocalCommWorkQueueElement _workQueueElement;                 /* 0x000 */
        LocalCommDataQueueElement _dataQueueElement;                 /* 0x000 */

        /**
        * Minimum Queue element size (128-byte elements)
        */
        char                     _reserved[128];                     /* 0x000 */
    };
} LocalCommCommonQueueElement;                                       /* 0x080 */
#pragma pack(reset)


#define LCOM_SERVER_BLUEQUEUE_LIMIT         1024

#pragma pack(1)

typedef struct  LocalCommDataQ_PLO_Header {
    ElementDT*                 head_p;
    ElementDT*                 tail_p;
} LocalCommDataQ_PLO_Header_t;


typedef struct LocalCommDataQueue {
    /**
     * The PLO serialized queue of Data Queue Elements (DQEs).
     * Note: Must be on Quadword boundary and 16 Bytes.
     */
    LocalCommDataQ_PLO_Header_t dataQPLO_header;                     /* 0x000 */

    /**
     * The LOCL containing the cell pool id used to allocate LComWorkQueueElements.
     */
    struct localCommClientAnchor* LOCL_p;                            /* 0x010 */

    /**
     * Maximum allowable concurrent data elements
     */
    int                        dataQLimit;                           /* 0x018 */

    struct {
        /**
         * "ON" if this is an Inbound data queue. "OFF" indicates an Outbound
         * data queue.
         */
        int inboundDataQueue : 1;

        /** Available flag space. */
        int _available : 31;
    } flags;                                                         /* 0x01C */

    char                       _reserved2[16];                       /* 0x020 */
} LocalCommDataQueue;                                                /* 0x030 */

/*********************************************************************************************/
/* DirectionalQueue -- collective of data and work queue representing a direction.  Either   */
/* "inbound" or "outbound" relative to client.                                               */
/*********************************************************************************************/

/**
 * Local Comm Directional queue.  This is either an Inbound or Outbound Queue comprised of
 * a Work Queue ("Black Queue") and a Data Queue ("Blue Queue").
 */
typedef struct LocalCommDirectionalQueue {
    LocalCommWorkQueue*        workQueue_p;    /* Work Queue ("Black Queue") 0x000 */

    LocalCommDataQueue*        dataQueue_p;    /* Data Queue ("Blue Queue") 0x008 */

    /**
     * Associated Local Comm Handle.
     */
    void*                      bbgzlhdl_p;                           /* 0x010 */

    char                       _reserved1[8];                        /* 0x018 */
} LocalCommDirectionalQueue;                                         /* 0x020 */
#pragma pack(reset)


struct localCommConnectionHandle;
struct localCommClientConnectionHandle;

/**
 * Initialize a Local Comm Directional queue.  This is either an Inbound or Outbound Queue comprised of
 * a Work Queue ("Black Queue") and a Data Queue ("Blue Queue").
 *
 * @param targetQueue The Direction queue to be initialized.
 * @param connHandle_p The associated Connection handle.
 * @param workQueue_p The Work Queue to be associated with this directional queue.
 * @param workQueueCellpoolLOCL_p The LOCL containing the Work Queue element cellpool anchor.
 * @param dataQueue_p The Data Queue to be associated with this directional queue.
 * @param dataQueueCellpoolLOCL_p The LOCL containing the Data Queue element cellpool anchor.
 */
int initializeDirectionalQueue(LocalCommDirectionalQueue *  targetQueue,
                               struct localCommConnectionHandle* connHandle_p,
                               LocalCommWorkQueue* workQueue_p,
                               struct localCommClientAnchor* workQueueCellpoolLOCL_p,
                               LocalCommDataQueue* dataQueue_p,
                               struct localCommClientAnchor* dataQueueCellpoolLOCL_p);


/*********************************************************************************************/
/* Non-primitive level local comm methods                                                    */
/*********************************************************************************************/


#define LCOM_ISSUECONNECTREQUEST_RC_OK                      0
#define LCOM_ISSUECONNECTREQUEST_RC_ADDTOWORK_FAILED        8
#define LCOM_ISSUECONNECTREQUEST_RC_UNKNOWN_ERR            16
#define LCOM_ISSUECONNECTREQUEST_RC_TIMEDOUT               20
#define LCOM_ISSUECONNECTREQUEST_RC_BUILDCONN_WRQE_FAILED  24
int issueConnectRequest(struct localCommClientConnectionHandle* clientConnHandle_p, int timeToWait);

int buildAndQueueConnectResponse(struct localCommClientConnectionHandle * localClientConnHandle_p, short failureCode);

void freeConnectResults(struct localCommConnectionHandle* connHandle_p, LocalCommWorkQueueElement * connResults);

int issueSendRequest(struct localCommConnectionHandle* connHandle_p, LocalCommDataCell_t* msgCell_p);


#define LCOM_ISSUEREAD_RC_ASYNC                      0
#define LCOM_ISSUEREAD_RC_DATATOMOVE                 1
// Don't use -8...used by one of the callers.
#define LCOM_ISSUEREAD_RC_BADPARM_REQUESTDATALEN   -12
#define LCOM_ISSUEREAD_RC_DATAQ_INVALIDSTATE       -16
#define LCOM_ISSUEREAD_RC_CLIENT_NODATA            -20
#define LCOM_ISSUEREAD_RC_FAILED_SHR_ACCESS        -24
#define LCOM_ISSUEREAD_RC_FORCE_ASYNC_WORK_FAIL    -28
#define LCOM_ISSUEREAD_RC_FORCE_ASYNC_BUILD_FAIL   -32
int issueReadRequest(struct localCommConnectionHandle* connHandle_p,
                     unsigned long long forceAsync,
                     unsigned long long requestDataLen,
                     LCOM_AvailableDataVector** returnDataVector_p);

#define LCOM_FREELREAD_RC_OK                  0
#define LCOM_FREELREAD_RC_MORE_TO_READ        8
#define LCOM_FREELREAD_RC_LMSG_NOT_MATCHED   12
#define LCOM_FREELREAD_RC_LMSG_NOTFOUND      16
unsigned short freeLastReadData(struct localCommConnectionHandle* connHandle_p, LocalCommDataCell_t* bbgzlmsg_p);

#define LCOM_ISSUEPREVIEW_RC_OK               0
#define LCOM_ISSUEPREVIEW_RC_CLOSERECEIVED    8
#define LCOM_ISSUEPREVIEW_RC_TIMEDOUT        20
#define LCOM_ISSUEPREVIEW_RC_BADSTATE        24
#define LCOM_ISSUEPREVIEW_RC_ERROR           28
int issuePreviewRequest(struct localCommConnectionHandle* connHandle_p,
                        unsigned char waitForData,
                        int timeToWait,
                        unsigned long long* dataLen_p);

int issueCloseRequest(struct localCommConnectionHandle* connHandle_p);
#define LCOM_CLOSINGCLIENTQUEUES_CLOSED 37
int initiateClosingClientQueues(struct localCommConnectionHandle* connHandle_p);
void initiateClosingServerQueue(LocalCommWorkQueue* localWorkQueuePtr);

/**
 * Build and queue an FFDC Request to the outbound work queue for delivery to the server.
 *
 * @param connHandle_p Pointer to the client's connection handle 
 * @param tp The trace point identifying the FFDC record
 * @param rawData Raw data for the FFDC record
 *
 * @return 0 if all went well;
 *         8 if a workElement could not be allocated;
 *         otherwise the failing RC from addToWorkQueue
 */
int issueFFDCRequest(struct localCommClientConnectionHandle * clientConnHandle_p, int tp, char rawData[REQUESTTYPE_FFDC_RAWDATA_SIZE]) ;

/**
 * Reset the flags indicating the work queue has closed
 * @param localWorkQueuePtr the pointer to the work queue
 */
void initializeWRQFlags(LocalCommWorkQueue* localWorkQueuePtr);

#endif
