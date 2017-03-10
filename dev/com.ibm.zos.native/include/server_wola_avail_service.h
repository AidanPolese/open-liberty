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
 * Defines the WOLA available service. These client services are found by the
 * WOLA server to perform certain requested tasks based on this service's
 * local comm connection handle. Services are linked together and anchored in
 * the client's registration.
 *
 * An available service represents a client-side thread hosting a client-side 
 * service.  The thread is waiting for the server to invoke it.
 */

#ifndef SERVER_WOLA_AVAIL_SERVICE_H_
#define SERVER_WOLA_AVAIL_SERVICE_H_

#define BBOAASRV_EYE "BBOAASRV"

#include "server_local_comm_client.h"

typedef struct availableService{

    /** Eye catcher 'BBOAASRV' */
    unsigned char eye[8];                                              /* 0x000 */

    /** Previous available service */
    struct availableService* previousService;                          /* 0x008 */

    /** Next available service */
    struct availableService* nextService;                              /* 0x010 */

    /** Associated registration */
    struct wolaRegistration* thisRegistration;                         /* 0x018 */

    /** Server stoken */
    unsigned char stoken[8];                                           /* 0x020 */

    /** Unique token */
    unsigned char token[8];                                            /* 0x028 */

    /** 16 bytes Connection handle */
    struct localCommClientConnectionHandle client_conn_handle;        /* 0x030 */

    /** Service name length */
    unsigned int serviceNameLength;                                    /* 0x040 */
    
    /** Service name */
    unsigned char serviceName[256];                                    /* 0x044 */

    /** !!!! Need to ensure the serviceName is null-term'ed. */
    unsigned char nullTerm;                                            /* 0x144 */

    /** Available space */
    unsigned char _available[59];                                      /* 0x145 */

} AvailableService_t;                                                  /* 0x180 */

#endif /* SERVER_WOLA_AVAIL_SERVICE_H_ */
