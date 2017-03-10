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

#ifndef _BBOZ_SERVER_WOLA_CONNECTION_HANDLE_H
#define _BBOZ_SERVER_WOLA_CONNECTION_HANDLE_H

#include "common_defines.h"

/** @file
 * Defines WOLA connection handle related structures.
 */

#define BBOAHDL_EYE "BBOAHDL "
#define BBOAHDL_VERSION_2 2

#define BBOAHDLW_EYE "BBOAHDLW"

/**
 * The JCA connection ID.  This ID is used for outbound requests from
 * WebSphere to a subsystem.  The ID is generated in Java and
 * propagated to the connection handle.  It is used to tie together
 * transactional requests in the subsystem.  For example, if WebSphere
 * starts a global transaction and gets a managed connection, it will
 * generate a JCA connection ID.  Then on the first request in-tran the
 * ID will get copied to the connection handle.  On the next request
 * in-tran, the JCA connection ID in the handle is matched to the
 * managed connection so that the second request is processed by
 * the same connection handle, even if another connection handle
 * could service the request based on the service name.
 */
typedef struct wolaJcaConnectionID {
    /** Stoken of server address space. */
    SToken serverStoken;                                    /* 0x000 */

    /** Connection ID (from managed connection). */
    unsigned int managedConnectionID;                       /* 0x008 */
} WolaJcaConnectionID_t;                                    /* 0x00C */

/**
 * Defines the server-side WOLA connection handle.  The WOLA connection
 * handle has a 1-1 mapping with a local comm connection handle.  It
 * is stateful and can only handle one request/response at a time.
 */
typedef struct wolaConnectionHandle
{
    /** Eye catcher 'BBOAHDL ' */
    unsigned char  eye[8];                                  /* 0x000 */

    /** Version of this control block. */
    unsigned short version;                                 /* 0x008 */

    /** Available for future use. */
    unsigned short _rsvd1;                                  /* 0x00A */

    /** Size of this control block. */
    unsigned int size;                                      /* 0x00C */

    /** Available flag bits. */
    unsigned int   ahdlflags:31;                            /* 0x010 */

    /** Flag indicating this connection has a tran affinity. */
    unsigned int   tranAffinity:1;                        /* 0x013.8 */

    /** Handle identifier / key.  This is the WOLA group. */
    unsigned char  wolaGroup[8];                            /* 0x014 */

    /** OLA API vector address. */
    void* __ptr32  clientVector_p;                          /* 0x01C */

    /** PC number to use to invoke client authorized services. */
    unsigned int   clientAuthPC;                            /* 0x020 */

    /** In tWAS this was the BACB pointer. TBD in Liberty. */
    void* __ptr32  bacb_p;                                  /* 0x024 */

    /** In tWAS this was the local comm session CB.  TBD in Liberty. */
    void* __ptr32  lscb_p;                                  /* 0x028 */

    /** In tWAS this was the local comm session handle.  TBD.        */
    void* __ptr32  sessionHandle_p;                         /* 0x02C */

    /** Pointer to the registration owning this connection. */
    struct wolaRegistration* registration_p;                /* 0x030 */

    /** The STCK from the owning RGE.  Use for validation. */
    unsigned long long wolaRegistrationSTCK;                /* 0x038 */

    /** Next free connection handle in the pool. */
    struct wolaConnectionHandle* nextFreeHandle_p;          /* 0x040 */

    /** The STCK from the server RGE that we are connected to. */
    unsigned long long wolaServerRegistrationSTCK;          /* 0x048 */

    /**
     * Next connection handle in the chain of all handles for this
     * registration.
     */
    struct wolaConnectionHandle* nextHandle_p;              /* 0x050 */

    /** PLO area */
    struct {
        /** Reserved for PLO with next field. */
        unsigned char _rsvd2[4];                            /* 0x058 */

        /**
         * Connection instance count.  Each time the connection is
         * removed from the free pool, the instance count is
         * incremented, and its value is copied into the client's
         * connection handle.  The client's handle is valid so long as
         * its instance count matches this instance count.
         *
         * When the client reads a field from the handle, it uses a
         * PLO compare-and-load to do so, comparing on the instance
         * count.  This is necessary because the server can force
         * the cleanup of the connection handle at any point.
         */
        unsigned int instanceCount;                         /* 0x05C */
    } ploArea;

    /** Connection handle state. */
    unsigned long long state;                               /* 0x060 */

    /**
     * Pointer to a cached BBOAMSG for the current request.  The
     * message is cached if we needed to read the headers or contexts
     * but have not been asked to read the actual message yet (or we
     * read the header but not the contexts yet).
     */
    void* cachedMessage_p;                                  /* 0x068 */

    /** Cached BBOAMSG contexts. */
    void* cachedContexts_p;                                 /* 0x070 */

    /**
     * JCA connection ID for the transaction currently associated with
     * this connection.  If there is a tran affinity, this will be
     * set, and is used by the Liberty server to send the next
     * request in-tran to this connection.
     */
    struct wolaJcaConnectionID jcaConnectionID;             /* 0x078 */

    /** 
     * The requestId for the current request.  This is used for associating 
     * server-to-client outbound requests (via JCA) with their responses.  
     * The requestId is assigned in the server, placed in the WolaMessage request, 
     * cached temporarily in the connection handle after the client reads the 
     * request, then is copied by the client into the response WolaMessage.  
     * Server-to-client requests are single-threaded (per connection), so there 
     * are no concurrency issues with caching the requestId in the connection handle.
     */
    unsigned int requestId;                                 /* 0x084 */

    /** Local comm connection handle. */
    unsigned char localCommConnectionHandle[16];            /* 0x088 */

    /** Available for future use. */
    unsigned char _rsvd4[40];                               /* 0x098 */
} WolaConnectionHandle_t;                                   /* 0x0C0 */


/**
 * The client side WOLA connection handle.  This is opaque to the
 * client (just a 12 byte data area).
 *
 * The instance count is used to validate the handle.  The server side
 * handle also has the instance count.  If the client's instance count
 * does not match the server-side instance count, then the client's
 * handle is not valid.  This check is necessary when reading from the
 * server side handle because the server side handle can be forced to
 * clean up at any point.  Usually a PLO compare-and-load is used to
 * read fields from the handle.
 */
#pragma pack(1)
typedef struct wolaClientConnectionHandle {
    /** Pointer to the real connection handle.  */
    WolaConnectionHandle_t* handle_p;                       /* 0x000 */

    /** Instance counter. */
    unsigned int instanceCount;                             /* 0x008 */
} WolaClientConnectionHandle_t;                             /* 0x00C */
#pragma pack(reset)


/** The CDSG area in WolaConnectionHandleWaiter_t. */
typedef struct wolaConnectionHandleWaiterCDSG {
    /** STCK when this was created. */
    unsigned char stck[8];                                  /* 0x000 */

    /** State of the waiter. */
    unsigned long long state;                               /* 0x008 */
} WolaConnectionHandleWaiterCDSG_t;                         /* 0x010 */


/**
 * Structure representing a handle waiter.  A client task trying to
 * get a connection when one is not available in the free pool (and
 * one cannot be created) will be assigned a waiter.  If a handle
 * becomes available, the waiter will be removed and a handle will
 * be provided to the caller.
 */
typedef struct wolaConnectionHandleWaiter {
    /** Eye catcher 'BBOAHDLW' */
    unsigned char eye[8];                                   /* 0x000 */

    /** Previous waiter in the waiting chain. */
    struct wolaConnectionHandleWaiter* prev_p;              /* 0x008 */

    /** Next waiter in the waiting chain. */
    struct wolaConnectionHandleWaiter* next_p;              /* 0x010 */

    /** Available for use */
    unsigned char _rsvd1[8];                                /* 0x018 */

    /** CDSG area.  TODO BBOAHDLW_CDSG */
    WolaConnectionHandleWaiterCDSG_t cdsgArea;              /* 0x020 */

    /** Client connection handle.  TODO: BBOACHLD */
    struct wolaClientConnectionHandle clientConnectionHandle;
                                                            /* 0x030 */

    /** Available for use */
    unsigned char _rsvd2[4];                                /* 0x03C */

    /** PET for waiting task. */
    unsigned char pauseElementToken[16];                    /* 0x040 */
} WolaConnectionHandleWaiter_t;                             /* 0x050 */

/**
 * Structure to hold the parms required for the STIMERM exit routine.
 */
#pragma pack(1)
typedef struct wolaConnectionHandleWaiterExitParms {
    /** The waiter to be released. */
    struct wolaConnectionHandleWaiter* waiter_p;            /* 0x000 */

    /** Saved value of the stck for the waiter before pausing. */
    unsigned char oldSTCK[8];                               /* 0x008 */
} WolaConnectionHandleWaiterExitParms_t;                    /* 0x010 */
#pragma pack(reset)

/**
 * Connection handle states.
 */
#define BBOAHDL_POOLED          0
#define BBOAHDL_READY           1
#define BBOAHDL_REQUEST_SENT    2
#define BBOAHDL_RESPONSE_RCVD   3
#define BBOAHDL_REQUEST_RCVD    4
#define BBOAHDL_DATA_RCVD       5
#define BBOAHDL_ERROR           6

/**
 * Connection handle waiter states and release codes.
 */
#define BBOAHDLW_WAITING        0
#define BBOAHDLW_CONN_OK        1
#define BBOAHDLW_PAUSE_FAILED   2
#define BBOAHDLW_TIMED_OUT      3
#define BBOAHDLW_DESTROYING     4
#define BBOAHDLW_RETRY          5

#define BBOAHDLW_RELEASE_GOTCONN "CON"
#define BBOAHDLW_RELEASE_TIMEOUT "TMO"
#define BBOAHDLW_RELEASE_DESTROY "DST"
#define BBOAHDLW_RELEASE_RETRY   "RTR"

#endif
