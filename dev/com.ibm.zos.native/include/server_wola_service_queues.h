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

#include "server_local_comm_client.h"
#include "server_wola_registration.h"

/**
 * Authorized services needed by the WOLA channel.
 */
#ifndef SERVER_WOLA_SERVICE_QUEUES_H_
#define SERVER_WOLA_SERVICE_QUEUES_H_

#define WOLA_SERVICE_QUEUES_RC_OK                0
#define WOLA_SERVICE_QUEUES_RC_UNREGISTERING     4
#define WOLA_SERVICE_QUEUES_RC_PET_PICKUPFAILED  8
#define WOLA_SERVICE_QUEUES_RC_PAUSEFAILED      12     // Failed issuing PAUSE on wait service
#define WOLA_SERVICE_QUEUES_RC_RELEASEFAILED    16     // Failed issuing RELEASE on wait service
#define WOLA_SERVICE_QUEUES_RC_PAUSE_TIMEOUT    20     // The PAUSE timed out
#define WOLA_SERVICE_QUEUES_RC_STIMERM_FAILED   24     // The call to STIMERM failed.
#define WOLA_SERVICE_QUEUES_RC_PAUSE_INTERRUPTED 28    // The wait was interrupted (ODI)

static const char WOLA_SERVICE_PET_UNREGISTERING[3] = "UNR";
static const char WOLA_SERVICE_PET_TIMEOUT[3] = "TIM";      // iea4rls release code for a timeout
static const char WOLA_SERVICE_PET_INTERRUPT[3] = "INT";    // iea4rls release code for an interrupt

/**
 * Called by the server when it wants to invoke a client-hosted service.
 *
 * First search the client's available services queue (in the client's 
 * wolaRegistration) for the service name. If found, return the client's 
 * LocalCommClientConnectionHandle and remove the available service from the queue. 
 *
 * If not found, create a wait service, add the service to the wait services queue 
 * (in the client's wolaRegistration), and pause the thread.  The thread will be
 * woken up when the client makes the service available.
 *
 * @param regPtr - The registration element
 * @param serviceName - The name of this client service - must be null term'ed.
 * @param timeout_s - The time to wait (in seconds) for the service to become available before giving up
 * @param waiterToken - If we have to make a waiter, the unique token that will identify it.
 * @param clientConnHandle - OUTPUT - The localcomm connection handle of the client hosting the service
 * 
 * @return 0 for success
 *         4 if unregistering
 *         8 if pet pickup failed
 *         12 if pause failed
 *         20 if pause timed out
 *         24 if STIMERM failed
 *         28 if interrupted
 */
int getClientService(struct wolaRegistration * reg_p, 
                     unsigned char* serviceName, 
                     int timeout_s,
                     long long waiterToken,
                     struct localCommClientConnectionHandle* clientConnHandle);

/**
 * Called by a client when it wants to make a client-hosted service availble
 * to be invoked by the server.
 *
 * First search the wait services queue (in the client's wolaRegistration) for 
 * anyone waiting on the given service name.  If found, remove the wait service,
 * hand it the client's localcomm conn handle, and release the waiting thread. 
 *
 * If not found in the wait services queue, create an available service and add 
 * it to the available service queue. The client will then issue a read and wait
 * for the server to invoke the service.
 *
 * @param clientConnHandle - A pointer to the client connection handle that will be set into the service
 * @param regPtr - The registration element
 * @param serviceName - The name of this client service. May be wild-carded. Must be null term'ed.
 *
 * @return 0 if it worked;
 *         1 if the async flag is set and no waiter found
 *         8 if the service queues weren't ready
 */
int putClientService(struct localCommClientConnectionHandle * clientConnHandle, struct wolaRegistration * reg_p, unsigned char* serviceName, unsigned int async);

/**
 * Called by the server when it wants to cancel a waiter for a client-hosted service.
 *
 * This can happen when the server detects a hung request, and request interrupts (ODIs)
 * are enabled.  There is a similar path involving the STIMERM that is set for the
 * WOLA-specific conneciton wait timeout.
 *
 * @param regPtr - The registration element
 * @param waiterToken - The unique token that identifies the waiter to cancel.
 */
void cancelClientService(struct wolaRegistration * reg_p,
                         long long waiterToken);

/**
 * Called during unregistration of the wola registration block.
 *
 * All remaining wait services are released with appropriate release codes
 * and all remaining available services' connection handles are closed.
 *
 * @param reg_p - The registration block that is being unregistered
 */
void unregisterServiceQueues(struct wolaRegistration* reg_p);

/**
 * Remove an available service from the client's queue by connection handle.
 * This is used by the client receive request code, to clean up after a failed local comm read.
 *
 * @param localCommHandle_p A pointer to the local comm connection handle provided to us when the
 *        available service entry was created.
 * @param reg_p A pointer to the WOLA registration hosting the available service.
 */
void removeAvailableServiceByHandle(OpaqueClientConnectionHandle_t* localCommHandle_p, volatile WolaRegistration_t* reg_p);

#endif /* SERVER_WOLA_SERVICE_QUEUES_H_ */
