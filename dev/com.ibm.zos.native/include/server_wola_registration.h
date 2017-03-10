/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2013
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */

/** @file
 * Defines the WOLA registration control block and common (client+server) 
 * registration functions.
 *
 * Server-side registration functions are defined in server_wola_registration_server.h.
 */

#ifndef _BBOZ_SERVER_WOLA_REGISTRATION_H
#define _BBOZ_SERVER_WOLA_REGISTRATION_H

#include "gen/ihaascb.h"

#include "angel_client_pc_stub.h"
#include "server_wola_shared_memory_anchor.h"

#define BBOARGE_EYE "BBOARGE "

/** tWAS registration version. */
#define BBOARGE_VERSION_1 1

/**
 * Version for WolaRegistration_t
 * Important notes about versioning -- version 1 is used by tWAS, version 2 is used by
 * Liberty.  Code in the assembler stubs bboas* and bboatrue use the version number to
 * determine which set of stubs to branch to.
 *
 * As time goes by, it may become necessary to increment the version number on either
 * the tWAS or the Liberty version of WOLA.  The stubs would need to be updated at
 * that point to be able to detect the new version and branch to the appropriate
 * place.  It might be a good idea at that point to add a flag to the RGE that can
 * identify a tWAS vs Liberty RGE, so that both runtimes can use a version 3 RGE
 * (even though they are formated differently).
 */
#define BBOARGE_VERSION_2 2

/** Connection pool state constants */
#define BBOARGE_CONNPOOL_READY      0
#define BBOARGE_CONNPOOL_QUIESCING  1
#define BBOARGE_CONNPOOL_DESTROYING 2

/** Service queue state constants */
#define BBOARGE_SERVICE_QUEUE_READY      0
#define BBOARGE_SERVICE_QUEUE_QUIESCING  1
#define BBOARGE_SERVICE_QUEUE_DESTROYING 2

typedef struct wolaRegistrationFlags {
    /** Flag indicating daemon registration.  Not used in Liberty.   */
    unsigned int daemonRegistration:1,

    /** Flag indicating server registration. */
                 serverRegistration:1,

    /** Flag indicating client registration. */
                 clientRegistration:1,

    /** Flag indicating unregister has been called (BBOA1URG). */
                 unregisterCalled:1,

    /**
     * This flag used to be used to indicate security information is
     * propagated from WebSphere to CICS on outbound WOLA calls, to be
     * consumed by the link server.  Now, the user ID is always
     * propagated (if available), and CICS will consume it if the link
     * server is set up to consume it.
     */
                  _twas_rsvd1:1,

    /** Flag indicating WLM propagation.  Not currently used. */
                  wlmFlag:1,

    /** Flag indicating transactions are supported. */
                  transactionFlag:1,

    /** Flag indicating this registration is active. */
                  active:1,

    /** Available for flags. */
                  _rsvd2:4,

    /** Flag indicating RRS UR propagation via OTMA is supported. */
                  rrsOtmaTranProp:1,

    /** Flag indicating RRS UR propagation is supported. */
                  rrsTranProp:1,

    /** Flag indicating client side local comm read timer is supported. */
                  clientSideReadTimer:1,

    /**
     * Flag indicating that under CICS, the ACEE provided by CICS via the
     * task related user exit (TRUE) should be propagated to WebSphere on
     * inbound requests, instead of the ACEE on the thread making the call.
     * This is inheritly insecure since CICS runs key 8, and so this must
     * be explicitly enabled in the server before the client will be
     * allowed to do it.
     */
                  propAceeFromTrueIntoServer:1,

                  _rsvd3:16;

    unsigned int  _rsvd4:32;
} WolaRegistrationFlags_t;

/** Struct of the client service indexes stored in the registration. */
typedef struct wolaClientPCIndexes{
    unsigned short wola_register; //!< Register BBOA1REG                      0x000
    unsigned short wola_unregister; //!< Unregister BBOA1URG                  0x002
    unsigned short wola_getConnection; //!< Get connection BBOA1CNG           0x004
    unsigned short wola_releaseConnection; //!< Release connection BBOA1CNR   0x006
    unsigned short wola_sendRequest; //!< Send request BBOA1SRQ               0x008
    unsigned short wola_sendResponse; //!< Send response BBOA1SRP             0x00A
    unsigned short wola_sendResponseException; //!< Send exception BBOA1SRX   0x00C
    unsigned short wola_receiveRequestAny; //!< Receive request BBOA1RCA      0x00E
    unsigned short wola_receiveRequestSpecific; //!< Receive request BBOA1RCS 0x010
    unsigned short wola_receiveResponseLength; //!< Receive length BBOA1RCL   0x012
    unsigned short wola_getData; //!< Get data BBOA1GET                       0x014
    unsigned short wola_invokeService; //!< Invoke BBOA1INV                   0x016
    unsigned short wola_hostService; //!< Host BBOA1SRV                       0x018
    unsigned short wola_getInformation; //!< Get information BBOA1INF         0x01A
    unsigned short wola_getContext; //!< Get context (CICS) BBOA1GTX          0x01C
    unsigned short wola_unused[17]; //!< Reserved for future                  0x01E
} WolaClientPCIndexes_t; //                                                   0x040

/**
 * Structure representing a WOLA registration.  A WOLA registration exists for each
 * server participating in WOLA, and for each client registration (via call to
 * client service BBOA1REG).
 */
typedef struct wolaRegistration
{
    /** Eye catcher 'BBOARGE ' */
    unsigned char eye[8];                                   /* 0x000 */

    /**
     * Version.  Note this field is referenced in assembler code, do
     * not change the offset.
     */
    unsigned short version;                                 /* 0x008 */

    /** Available for use. */
    unsigned short _rsvd1;                                  /* 0x00A */

    /** Size of this control block. */
    unsigned int size;                                      /* 0x00C */

    /** Flags */
    WolaRegistrationFlags_t flags;                          /* 0x010 */

    /** Pointer to this control block. */
    struct wolaRegistration* this_p;                        /* 0x018 */

    /** Registration name provided by caller.  Blank padded. */
    unsigned char registrationName[16];                     /* 0x020 */

    union {
        /** Client job name. */
        unsigned char clientJobName[8];                     /* 0x030 */

        /** For servers, third part of WOLA name */
        unsigned char serverNameThirdPart[8];               /* 0x030 */
    };

    /** Reserved for client job number - not currently used. */
    unsigned int clientJobNumber;                           /* 0x038 */

    /** ASCB@ of client, or server. */
    struct ascb* __ptr32 ascb_p;                            /* 0x03C */

    /** Un-available until the RGE version is incremented. */
    unsigned char _rsvd2[0x10];                             /* 0x040 */

    /** For servers, the second part of WOLA name */
    unsigned char serverNameSecondPart[8];                  /* 0x050 */

    /** Minimum initial number of connections for this RGE. */
    unsigned int minConns;                                  /* 0x058 */

    /** Maximum number of connections for this RGE. */
    unsigned int maxConns;                                  /* 0x05C */

    /** Pointer to the client stub vector table BBOAVEC. */
    void* __ptr32 clientVector_p;                           /* 0x060 */

    /** Native WOLA trace level 0/1/2. */
    unsigned int  traceLevel;                               /* 0x064 */

    /**
     * The STCK of the last RGE state change.  This is updated when
     * the RGE gets returned to the free pool, and when it's
     * re-allocated.  It is used by PLO to load other fields out of
     * the RGE.  For example, if a client wishes to get a connection
     * handle, a PLO is performed comparing this STCK with the one that
     * existed when the client found the RGE, to make sure the RGE is
     * still allocated to the client.
     */
    unsigned long long stckLastStateChange;                 /* 0x068 */

    /** Pointer to the related server registration. */
    struct wolaRegistration* serverRegistration_p;          /* 0x070 */

    /** Pointer to the WOLA shared memory anchor. */
    struct wolaSharedMemoryAnchor* wolaAnchor_p;            /* 0x078 */

    union {
        /** Address space level RESMGR token. */
        unsigned int addressSpaceResmgrToken;               /* 0x080 */

        /** Client outbound transaction inactivity timeout. */
        unsigned int outboundTxInactivityTimeout;           /* 0x080 */
    };

    /** CMRO task RESMGR token. */
    unsigned int CMRO_taskResmgrToken;                      /* 0x084 */

    /** STOKEN for the address space making this registration. */
    unsigned char stoken[8];                                /* 0x088 */

    /** Pointer to the previous RGE in the chain of RGEs. */
    struct wolaRegistration* previousRegistration_p;        /* 0x090 */

    /** Pointer to the next RGE in the chain of RGEs. */
    struct wolaRegistration* nextRegistration_p;            /* 0x098 */

    /** Available service queue - first service. */
    struct availableService* availServiceFirst_p;           /* 0x0A0 */

    /** Available service queue - last service. */
    struct availableService* availServiceLast_p;            /* 0x0A8 */

    /** Waiting service queue - first waiter. */
    struct waitService* waitServiceFirst_p;                 /* 0x0B0 */

    /** Waiting service queue - last service. */
    struct waitService* waitServiceLast_p;                  /* 0x0B8 */

    /** Available service queue service count */
    unsigned long long availServiceCount;                   /* 0x0C0 */

    /** Waiting service queue service count */
    unsigned long long waitServiceCount;                    /* 0x0C8 */

    /** In tWAS used for service Q latch.  In Liberty TBD for lock.  */
    unsigned char serviceQ_LockArea[8];                     /* 0x0D0 */

    /** The STCK time that the last service was matched.  Red robin. */
    unsigned char stckLastServiceMatch[8];                  /* 0x0D8 */

    /** Number of services matched to this RGE. Used by Red robin.  */
    unsigned int serviceMatchCount;                         /* 0x0E0 */

    /** Available for use */
    unsigned char _rsvd3[4];                                /* 0x0E4 */

    /** Connection handle pool counter (for PLO). */
    unsigned long long connPoolPLOCounter;                  /* 0x0E8 */

    /** Pointer to the head of the free handle pool */
    struct wolaConnectionHandle* freeConnHandlePoolHead_p;  /* 0x0F0 */

    /** Pointer to the head of the connection wait list. */
    struct wolaConnectionHandleWaiter* connWaitListHead_p;  /* 0x0F8 */

    /** Pointer to the tail of the connection wait list. */
    struct wolaConnectionHandleWaiter* connWaitListTail_p;  /* 0x100 */

    /** Pointer to the head of the list of all connections. */
    struct wolaConnectionHandle* allConnHandleListHead_p;   /* 0x108 */

    /** Connection pool state. */
    unsigned long long connPoolState;                       /* 0x110 */

    /** Service queues state. */
    unsigned long long serviceQueuesState;                  /* 0x118 */

    /** Number of active connection handles. */
    unsigned long long activeConnCount;                     /* 0x120 */

    /** In a client RGE, the STCK when the server started. */
    unsigned long long serverStartSTCK;                     /* 0x128 */

    /** Un-available until the RGE version is incremented. */
    unsigned char _rsvd5[0x40];                             /* 0x130 */

    /** Service Queues counter (for PLO). */
    unsigned long long serviceQueuesPLOCounter;             /* 0x170 */

    /** Available for use */
    unsigned char _rsvd6[8];                                /* 0x178 */

} WolaRegistration_t;                                       /* 0x180 */
//
// !!! NOTE NOTE !!!
// if you change the size of this structure, be sure to update
// server_wola_registration_test.mc with the size that you think it is,
// and the unit test will tell you if you're wrong, or if you've broken
// any of the word-alignment variables.
//

/**
 *
 * @param chain - The head of the BBOARGE chain
 * @param wola_name2 - The 2nd WOLA name
 * @param wola_name3 - The 3rd WOLA name
 *
 * @return the bboarge in the chain for the given wola_name2/wola_name3
 */
WolaRegistration_t * findServerBboargeInChain( WolaRegistration_t * chain, char * wola_name2, char * wola_name3) ;

/**
 * Adds an RGE to the RGE chain hung off of the BBOASHR (server_wola_shared_memory_anchor).
 *
 * @param bboashr_p Pointer to the BBOASHR
 * @param bboarge_p Pointer to the registration which should be added to the chain.
 */
void addBboargeToChain( struct wolaSharedMemoryAnchor * bboashr_p, WolaRegistration_t * bboarge_p ) ;

/**
 * Removes an RGE from the RGE chain hung off of the BBOASHR (server_wola_shared_memory_anchor).
 *
 * @param bboashr_p Pointer to the BBOASHR
 * @param bboarge_p Pointer to the registration which should be removed from the chain.
 *
 * @return zero if the BBOARGE was successfully removed; non-zero otherwise.
 */
int removeBboargeFromChain( struct wolaSharedMemoryAnchor * bboashr_p, WolaRegistration_t * bboarge_p );


#endif
