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

#ifndef SERVER_LOCAL_COMM_CLEANUP_H_
#define SERVER_LOCAL_COMM_CLEANUP_H_
#include "stack_services.h"

/**
 * Local Comm Cleanup constructs and functions
 */

/*********************************************************************************************/
/* Queue of Clients to a server that need cleanup actions.                                   */
/*********************************************************************************************/
#define SERVER_LCOM_CCQE_EYECATCHER_INUSE  "BBGZCCQE"
#define SERVER_LCOM_CCQE_EYECATCHER_POOLED  "CCQEBBGZ"

#pragma pack(1)
/** A PLO maintained change control area. */
typedef struct localCommClientCleanup_PLO_CS_Area {
    unsigned long long ploSequenceNumber;                            /* 0x000 */

    /** Flags. */
    struct {
        /** Available flag space. */
       unsigned int _available : 32;
    } flags;                                                         /* 0x008 */

    /** Pad out to quadword. */
    char _available2[4];                                             /* 0x00C */
} LocalCommClientCleanup_PLO_CS_Area_t;                              /* 0x010 */

/** Header of Client Cleanup queue */
typedef struct  localCommClientCleanupQ_PLO_Header {
    ElementDT*                 head_p;
    ElementDT*                 tail_p;
} LocalCommClientCleanupQ_PLO_Header_t;

/** Client Cleanup queue element */
typedef struct localCommClientCleanupQueueElement {
    /**
     * Chain pointers.
     */
    ElementDT                queueElement;                           /* 0x000 */

    /**
     * The eye catcher for this control block ("BBGZCCQE")
     */
    unsigned char            eyecatcher[8];                          /* 0x010 */

    /**
     * Creation Time
     */
    unsigned long long       createStck;

    /**
     * Pointer to the client LOCL
     */
    struct localCommClientAnchor* inClientBBGZLOCL_p;

    /**
     * Pointer to the Client LDAT
     */
    struct localCommClientDataStore* inClientBBGZLDAT_p;

    /**
     * Pointer to the client LSCL and also the shared memory usertoken for the Client's
     * LOCL and LDAT
     */
    struct localCommClientServerPair* inClientBBGZLSCL_p;

} LocalCommClientCleanupQueueElement_t;

/**
 * Local Comm Client cleanup queue element pool
 */
typedef struct localCommClientCleanupQueueElementPool {
    LocalCommClientCleanupQueueElement_t*   queueElementPool_p;      /* 0x000 */
    long long                               queueElementPoolCnt;     /* 0x008 */
} LocalCommClientCleanupQueueElementPool_t;                          /* 0x010 */

/**
 * Local Comm Client cleanup queue.
 */
typedef struct localCommClientCleanupQueue {

    LocalCommClientCleanupQ_PLO_Header_t        queueHeader;         /* 0x000 */

    LocalCommClientCleanup_PLO_CS_Area_t        ploCS_Area;          /* 0x010 */

    LocalCommClientCleanupQueueElementPool_t    queueElementPool;    /* 0x020 */

} LocalCommClientCleanupQueue_t;                                     /* 0x030 */
#pragma pack(reset)

#define LCOM_REGCLIENTCLEANUP_RC_OK          0
#define LCOM_REGCLIENTCLEANUP_RC_NO_ELEM     8
#define LCOM_REGCLIENTCLEANUP_RC_QUEFAILED  12
int registerClientCleanup(struct localCommClientAnchor*     serverLOCL_p,
                          struct localCommClientAnchor*     inClientBBGZLOCL_p,
                          struct localCommClientDataStore*  inClientBBGZLDAT_p,
                          struct localCommClientServerPair* inClientBBGZLSCL_p);

#define LCOM_DREGCLIENTCLEANUP_RC_NO_ELEM      8
#define LCOM_DREGCLIENTCLEANUP_RC_DQUEFAILED  12
int deregisterClientCleanup(struct localCommClientAnchor*     serverLOCL_p,
                            struct localCommClientAnchor*     inClientBBGZLOCL_p,
                            struct localCommClientDataStore*  inClientBBGZLDAT_p,
                            struct localCommClientServerPair* inClientBBGZLSCL_p);

/**
 * Cleanup clients in the client cleanup queue.  Called from server address space in task resmgr (I hope).
 */
int cleanupClients_ServerGone(void* serverProcessData_p);

/*********************************************************************************************/
/* Common Local comm client routines.                                                        */
/*********************************************************************************************/


/**
 * Cleanup the Server's LOCL and related resources
 */
#define LCOM_CLEANUPSERVERLOCL_RSN_NO_SPD         64
#define LCOM_CLEANUPSERVERLOCL_RSN_BBGZLOCL_BUSY  65
#define LCOM_CLEANUPSERVERLOCL_RSN_NO_BBGZLOCL    66
#define LCOM_CLEANUPSERVERLOCL_RSN_ABENDED        67
#define LCOM_CLEANUPSERVERLOCL_RSN_NOESTAE        68
int cleanupServerLOCL(int* outRSN);


/**
 * Cleanup client resources related to a bind break to a server.
 */
int cleanupClient_ClientTerm(unsigned long long serverToken);

#endif /* SERVER_LOCAL_COMM_CLEANUP_H_ */
