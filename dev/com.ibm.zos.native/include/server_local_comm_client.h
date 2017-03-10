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
#ifndef _BBOZ_SERVER_LOCAL_COMM_CLIENT_H
#define _BBOZ_SERVER_LOCAL_COMM_CLIENT_H

#include "common_defines.h"
#include "petvet.h"
#include "server_local_comm_api.h"
#include "server_local_comm_cleanup.h"
#include "server_local_comm_data_store.h"
#include "server_local_comm_queue.h"
#include "server_local_comm_shared_memory.h"
#include "stack_services.h"

/** Eyecatcher for local comm client server pair. */
#define BBGZLSCL_EYE "BBGZLSCL"
#define RELEASED_BBGZLSCL_EYE "LSCLBBGZ"

/** Detach system affinity is broken right now. */
#define SKIP_DETACH_SYSTEM_AFF 1

#pragma pack(1)
/** A PLO maintained change control area. */
typedef struct localComPairPLO_CS_Area {
    unsigned long long ploSequenceNumber;                            /* 0x000*/

    /** Flags. */
    struct {
        /** Client-side of connection will issue detach from the LDAT storage shortly. */
        unsigned int clientLDAT_detach : 1,
        /** Server-side of connection will issue detach from the LDAT storage shortly. */
                     serverLDAT_detach : 1,

        /** Available flag space. */
                     _available : 30;
    } flags;                                                         /* 0x008*/
    char _reserved1[4];                                              /* 0x00C*/
} LocalComPairPLO_CS_Area_t;                                         /* 0x010*/

/**
 * Control block representing a client/server pair.  These control blocks are
 * chained in the local comm client anchor, and are allocated from a cell pool
 * in the local comm client anchor.  They point to the first data store used
 * by the client server pair.
 */
typedef struct localCommClientServerPair {
    /**
     * WARNING / WARNING / WARNING
     * This structure is mapped from Java in the introspection code.  If the
     * "length" offsets and/or offsets to anchors of other structures pointed
     * to from this structure change, then updates will neeed to be made to
     * the introspection code (ex., com.ibm.ws.zos.channel.local.queuing.internal.IntrospectHelper.java).
     */

    /** Eyecatcher 'BBGZLSCL' */
    unsigned char eyecatcher[8];                                     /* 0x000*/

    /** Version of this control block. */
    unsigned short version;                                          /* 0x008*/

    /** Size of this control block. */
    unsigned short length;                                           /* 0x00A*/

    char reserved1[4];                                               /* 0x00C*/

    /** Stoken for the server we are connected to. */
    SToken serverStoken;                                             /* 0x010*/

    /** Pointer to the first data store for this pair. */
    void* firstDataStore_p;                                          /* 0x018*/

    /** PLO Area used for serialization on updates to this Pair */
    LocalComPairPLO_CS_Area_t lscl_PLO_CS_Area;                      /* 0x020*/

    /**
     * Pointer to the first/Last connection handle(s) in use by this pair.
     * Note: maintained with PLO. Must be quadword aligned.
     * */
    struct localCommConnectionHandle* firstInUseConnHdl_p;           /* 0x030*/
    struct localCommConnectionHandle* lastInUseConnHdl_p;            /* 0x038*/

    /** Pointer to the BBGZLOCL control block for the client. */
    struct localCommClientAnchor* localCommClientControlBlock_p;     /* 0x040*/

    /** Pointer to the next BBGZLSCL. */
    struct localCommClientServerPair* nextLSCL_p;                    /* 0x048*/

    /** Pointer to the server's BBGZLOCL control block. */
    struct localCommClientAnchor* serverLOCL_p;                      /* 0x050*/

    /**
     * The client process data token for the client -> server bind.  This
     * disambiguates the serverStoken when the server has restarted as BPXAS.
     */
    unsigned long long clientProcessDataToken;                       /* 0x058*/

    /** Available for future use. */
    unsigned char _available[416];                                   /* 0x060*/
} LocalCommClientServerPair_t;                                       /* 0x200*/

/** Eyecatcher for local comm client anchor. */
#define BBGZLOCL_EYE "BBGZLOCL"
#define RELEASED_BBGZLOCL_EYE "LOCLBBGZ"

/** The name token name for the BBGZLOCL control block. */
#define BBGZLOCL_NAMETOKEN_NAME "BBGZLOCL"

/** A PLO maintained change control area. */
typedef struct localComLOCL_PLO_CS_Area {
    unsigned int ploSequenceNumber;                                  /* 0x000*/

    /** Flags. */
    struct {
        /**
        * This BBGZLOCL was created and owned by a Server process
        */
        unsigned int serverCreated : 1,

        /**
         * Server has driven the cleanup of the Local Comm channel.
         */
                     serverShutdown: 1,

        /**
         * Client broke its last bind to a server's SCFM.  Upon cleanup
         * the LSCL chain was empty; so, we took the opportunity to cleanup the
         * LOCL.
         */
                     clientCleaningup: 1,

        /** Available flag space. */
                     _available : 29;
    } flags;                                                         /* 0x004*/
} LocalComLOCL_PLO_CS_Area_t;                                        /* 0x008*/

/**
 * Control block anchoring the local comm structures in the client and server
 * address spaces.  1 per local comm address space.
 *
 * The connection handle pool and list of data stores is kept here.
 */
typedef struct localCommClientAnchor {
    /**
     * WARNING / WARNING / WARNING
     * This structure is mapped from Java in the introspection code.  If the
     * "length" offsets and/or offsets to anchors of other structures pointed
     * to from this structure change, then updates will neeed to be made to
     * the introspection code (ex., to com.ibm.ws.zos.channel.local.queuing.internal.IntrospectHelper.java).
     */

    /** Eyecatcher 'BBGZLOCL' */
    unsigned char eyecatcher[8];                                     /* 0x000*/

    /** Version of this control block. */
    unsigned short version;                                          /* 0x008*/

    /** Size of this control block. */
    unsigned short length;                                           /* 0x00A*/

    /** Available for future use. */
    char           _available1[12];                                  /* 0x00C*/

    /** PLO Area used for serialization on updates to this LOCL */
    LocalComLOCL_PLO_CS_Area_t locl_PLO_CS_Area;                     /* 0x018*/

    /** STOKEN of the client-side of connection. */
    SToken creatorStoken;                                            /* 0x020*/

    /**
     * Pointer to the first LSCL control block.
     * Note: Maintained with PLO.  DoubleWord alignment.
     */
    LocalCommClientServerPair_t* firstLSCL_p;                        /* 0x028*/

    struct {
        /**
         * Server's Work Queue PLO CS area.
         * Note: quadword alignment.
         */
        LocalComWQE_PLO_CS_Area_t serverAsClientWorkQ_PLO_CS_Area;   /* 0x030*/

        /**
        * Server Inbound Work Queue.  Also the server as client
        * Inbound Work Queue
        */
        LocalCommWorkQueue       serverAsClientWorkQ;                /* 0x040*/

        /**
        * Enq Token returned from initialization code obtaining an Enq
        * for Local Comm.
        */
        char                     serverIdentityEnqToken[32];         /* 0x0A0 */

        /**
        * TTOKEN of thread owning the Enq for Local Comm.
        */
        char                     serverIdentityEnqTokenOwningTToken[16]; /* 0x0C0 */

        /**
         * Client Cleanup queue.  Used in server during abnormal termination.
         */
        LocalCommClientCleanupQueue_t  serverClientCleanupQueue;     /* 0x0D0*/
    } serverSpecificInfo;

    /** Connection handle cell pool anchor. */
    // TODO: If we put the conn handle pool in the LDAT, then we don't need to
    //       worry about using the wrong version conn handle with a server.
    unsigned long long connHandlePool;                               /* 0x100*/

    /** Pointer to the PetVet used by this process. */
    PetVet* spca_p;                                                  /* 0x108*/

    /** Shared memory book-keeping. */
    LocalCommSharedMemoryInfo_t info;                                /* 0x110*/

    /** Data queue element cell pool anchor. */
    long long queueElementPool;                                      /* 0x128*/

    /** Number of connection handles to allocate per extent. */
    unsigned short numConnHandlesPerExtent;                          /* 0x130*/

    /** Number of queue elements to allocate per extent. */
    unsigned short numQueueElementsPerExtent;                        /* 0x132*/

    /** Number of client server pairs to allocate per extent. */
    unsigned short numClientServerPairsPerExtent;                    /* 0x134*/

    /** Available for future use. */
    unsigned char _available2[2];                                    /* 0x136*/

    /** Client server pair control block cell pool anchor. */
    unsigned long long clientServerPairPool;                         /* 0x138*/

    /** Footprint table pointer */
    void * footprintTable;                                           /* 0x140*/

    /** Available for future use. */
    unsigned char _available3[8];                                    /* 0x148*/

    /** Storage for the PetVet used by a client process.  Quad aligned. */
    PetVet clientSpcaBranch;                                         /* 0x150*/

    /** Available for future use. */
    unsigned char _available4[136];                                  /* 0x178*/
} LocalCommClientAnchor_t;                                           /* 0x200*/

/** Eyecatcher for local comm connection handle. */
#define BBGZLHDL_EYE "BBGZLHDL"
#define RELEASED_BBGZLHDL_EYE "LHDLBBGZ"

/** Version definitions for connection handle */
#define BBGZLHDL_INITIAL_VERSION 1

typedef struct  LocalCommHandle_PLO_Area {
    /**
     * Warning this storage is shared with WRQ code and mapped by LocalComWQE_PLO_CS_Area.
     */
    unsigned int       ploSequenceNumber;                            /* 0X000 */

    /**
     * Inbound Data Queue current count of elements
     */
    unsigned int       dataQInboundElementCount;                     /* 0x004 */

    /**
     * Outbound Data Queue current count of elements
     */
    unsigned int       dataQOutboundElementCount;                    /* 0x008 */

    /** Flags. */
    struct {
        // Handle Specific flags
        /** Client-side of connection has requested close processing.  */
        unsigned int   clientInitiatedClosing : 1,
        /** Server-side of connection has requested close processing.  */
                       serverInitiatedClosing : 1,
        /** Client-side of close is finished "closed". */
                       clientClosed: 1,
        /** Servser-side of close is finished "closed".*/
                       serverClosed: 1,

        /** Available flag space. */
                       _available1 : 12,

        // Inbound Data Queue Flags
        /**
         * Receiving side needs to get poked when data is added to this queue.
         */
                       inboundDataQ_readPending : 1,

        /** A close connection request has been issued.  */
                       inboundDataQ_closingDataQueue : 1,

        /** A close connection request has been issued and the queue has been drained. */
                       inboundDataQ_closedDrainedDataQueue : 1,

        /**
         * Available flags for Inbound Data Queue
         */
                       _available2 : 5,

        // Outbound Data Queue Flags
        /**
         * Receiving side needs to get poked when data is added to this queue.
         */
                       outboundDataQ_readPending : 1,

        /** A close connection request has been issued.  */
                       outboundDataQ_closingDataQueue : 1,

        /** A close connection request has been issued and the queue has been drained. */
                       outboundDataQ_closedDrainedDataQueue : 1,

        /**
         * Available flags for Outbound Data Queue
         */
                       _available3 : 5;
    } flags;                                                         /* 0x00C */
} LocalCommHandle_PLO_Area_t;

/**
 * Local comm connection handle.
 */
typedef struct localCommConnectionHandle {
    /**
     * WARNING / WARNING / WARNING
     * This structure is mapped from Java in the introspection code.  If the
     * "length" offsets and/or offsets to anchors of other structures pointed
     * to from this structure change, then updates will neeed to be made to
     * the introspection code (ex., com.ibm.ws.zos.channel.local.queuing.internal.IntrospectHelper.java).
     */

    /**
     * Next in-use connection handle in LSCL chain.
     * Note: Target of PLO operations.  Must be Quadword aligned.
     */
    struct localCommConnectionHandle* nextHandle_p;                  /* 0x000*/
    struct localCommConnectionHandle* prevHandle_p;                  /* 0x008*/

    /** Eyecatcher 'BBGZLHDL' */
    unsigned char eyecatcher[8];                                     /* 0x010*/

    /** Version of this control block. */
    unsigned short version;                                          /* 0x018*/

    /** Size of this control block. */
    unsigned short length;                                           /* 0x01A*/

    /** Client ASID (asid that initiated this connection) */
    unsigned short clientASID;                                       /* 0x01C*/

    unsigned char _available1[2];                                    /* 0x01E*/
    
    /**
     * The PLO serialized area of Handle (SEQ#, Flags for handle and data queues).
     * Note: Must be on Quadword boundary and 16 Bytes.
     *
     * This includes the Data queues and the Client Work Queue.  This is used
     * as the serialization address (PLO PLT) as well.
     */
    LocalCommHandle_PLO_Area_t  handlePLO_CS;                        /* 0x020 */

    /** Instance counter for this handle.  Incremented when returned to pool.*/
    unsigned int instanceCount;                                      /* 0x030*/

    unsigned char _available2[4];                                    /* 0x034*/

    /** Client/Server pair information */
    LocalCommClientServerPair_t * bbgzlscl_p;                        /* 0x038*/

    /** Client Inbound Queues */
    LocalCommDirectionalQueue clientInboundQ;                        /* 0x040*/

    /** Client Outbound Queues */
    LocalCommDirectionalQueue clientOutboundQ;                       /* 0x060*/

    //TODO: We may want to investigate separating the queues to individual cache
    // lines.  To eliminate cache invalidation.

    /** Data Queue headers */
    LocalCommDataQueue localDataQueues[2];                           /* 0x080*/

    /** Work Queue headers
     *
     *  Note: The server-side of a work resides in the BBGZLOCL of the Server.
     *  Also, if the client-side of the connection is also a server then its
     *  clientIN_WorkQ resides in that server's BBGZLOCL.  So, we need at most
     *  1 localWorkQueue in the Handle; used for the raw client side. */
    LocalCommWorkQueue localWorkQueue;                               /* 0x0E0*/

    /** Footprint table pointer */
    void * footprintTable;                                           /* 0x140*/

    /** Available for use. */
    unsigned char _available3[24];                                   /* 0x148*/
} LocalCommConnectionHandle_t;                                       /* 0x160*/


/** Client connection handle.  This is 16 bytes opaque data to the client. */
typedef struct localCommClientConnectionHandle {
    /** A pointer to the actual connection handle. */
    LocalCommConnectionHandle_t* handle_p;                           /* 0x000*/

    /** Instance counter for validation with the actual handle. */
    unsigned int instanceCount;                                      /* 0x008*/

    /** Available. */
    unsigned char _available[4];                                     /* 0x00C*/
} LocalCommClientConnectionHandle_t;                                 /* 0x010*/
#pragma pack(reset)

#define LOCAL_COMM_STALE_HANDLE -8
int validateClientConnectionHandle(struct localCommClientConnectionHandle* clientConnHandle_p);

int amIClientSide(struct localCommConnectionHandle* connHandle_p);


LocalCommClientServerPair_t* getLocalCommClientServerPair(unsigned long long clientProcessDataToken, LocalCommClientAnchor_t* anchor_p, LocalComLOCL_PLO_CS_Area_t* outLSCLState_p);

/**
 * Get the current Local Comm Anchor (LOCL)
 */
LocalCommClientAnchor_t* getCurrentLOCL_address(void);

/**
 * Get or create the BBGZLOCL control block for a Server process.
 */
LocalCommClientAnchor_t* createServerLocalCommClientAnchor(void);

/**
 * Set LOCL flag to indicate that the server has begun and will continue to shutdown this LOCL.
 * Note: The server most likely is terminating, but it may just be stopping the Local Comm
 * channel.  In which case a subsequent re-init of the Local Comm Channel could cause another LOCL
 * instance to be created.
 */
int setServerShutdownFlag(LocalCommClientAnchor_t* serverLOCL_p);

/**
 * Set LOCL flag to indicate that the client has begun and will continue to shutdown this LOCL.
 * Note: The client may be terminating, but it may just be breaking its bind with a server.
   In which case a subsequent re-bind and connect request will cause another LOCL
 * instance to be created.
 */
int setClientCleaningupFlag(LocalCommClientAnchor_t* serverLOCL_p);

/**
 * Detach from LOCL and LDAT.
 */
int detachFromLDATandLOCL(LocalCommClientAnchor_t* otherSideLOCL_p, LocalCommClientDataStore_t* dataStore_p, long long sharedMemoryUserToken);

/**
 * Cleanup then pool the specified LSCL
 */
int cleanupLSCL(LocalCommClientAnchor_t* locl, LocalCommClientServerPair_t* bbgzlscl_p);

/**
 * Remove the LSCL from the LOCL chain
 */
int removeLSCL(LocalCommClientAnchor_t* locl, LocalCommClientServerPair_t* pair_p, LocalComLOCL_PLO_CS_Area_t* requiredState_p);

/**
 * Prepare to detach the system affinity from a LOCL.
 */
void prepareLoclForSystemDetach(LocalCommClientAnchor_t* locl_p);

#endif
