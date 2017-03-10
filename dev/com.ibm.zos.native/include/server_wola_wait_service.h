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
 * Defines the WOLA wait service. Wait services are created by the
 * WOLA server when no available service is found. Basically the
 * server is waiting for an available client service in order to
 * get the client's connection handle. These wait services contain
 * a PET (pause element token) on which the server pauses. When a
 * client becomes available, the correct wait service is found, the
 * connection handle is updated, and the server is unpaused to continue
 * its previous task with the new conn handle.
 */

#ifndef SERVER_WOLA_WAIT_SERVICE_H_
#define SERVER_WOLA_WAIT_SERVICES_H_

#define BBOAWSRV_EYE "BBOAWSRV"
#include "mvs_stimerm.h"
#include "server_local_comm_client.h"

typedef struct waitService{

    /** Eye catcher 'BBOAWSRV' */
    unsigned char eye[8];                                             /* 0x000 */

    /** Previous available service */
    struct waitService* previousService;                              /* 0x008 */

    /** Next available service */
    struct waitService* nextService;                                  /* 0x010 */

    /** Associated registration */
    struct wolaRegistration* thisRegistration;                        /* 0x018 */

    /** Wait service PET */
    char pet[16];                                                     /* 0x020 */
    
    /** 16 bytes Connection handle */
    struct localCommClientConnectionHandle client_conn_handle;        /* 0x030 */

    /** Service name length */
    unsigned int serviceNameLength;                                   /* 0x040 */

    /** Service name */
    unsigned char serviceName[256];                                   /* 0x044 */
    
    /** !!!! Need to ensure the serviceName is null-term'ed. */
    unsigned char nullTerm;                                           /* 0x144 */

    /** Available space */
    unsigned char _available1[3];                                     /* 0x145 */

    /** MvsTimerID for STIMER.  MUST BE DWORD ALIGNED!! */            
    MvsTimerID_t mvsTimerID;                                          /* 0x148 */

    /**
     * Unique token for this waiter (on this system).  This is passed down from
     * the Java code, and the Java code will use this token to locate this
     * waiter in the event the request needs to be cancelled.
     */
    long long uniqueToken;                                            /* 0x150 */

    /** Available space */
    unsigned char _available2[40];                                    /* 0x158 */

} WaitService_t;                                                      /* 0x180 */

#endif /* SERVER_WOLA_AVAIL_SERVICES_H_ */

