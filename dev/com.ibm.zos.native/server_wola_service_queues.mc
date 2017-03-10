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

#include <metal.h>
#include <stdlib.h>
#include <string.h>
#include <stdio.h>

#include "include/mvs_utils.h"
#include "include/ras_tracing.h"
#include "include/server_wola_wait_service.h"
#include "include/server_wola_avail_service.h"
#include "include/server_process_data.h"
#include "include/server_wola_shared_memory_anchor.h"
#include "include/server_wola_registration.h"
#include "include/server_wola_service_queues.h"
#include "include/server_wola_message.h"
#include "include/mvs_plo.h"
#include "include/mvs_stimerm.h"

#define RAS_MODULE_CONST  RAS_MODULE_SERVER_WOLA_SERVICE_QUEUES

/**
 * strnlen isn't available on MVS it appears.
 */
int strnlen(const char * str, int maxLen) {
    char * nullTerm = memchr(str, '\0', maxLen);
    return (nullTerm == NULL) ? maxLen : (nullTerm - str);
}

/**
 * Compare a string against a pattern possibly containing wildcards.
 *
 * @param string - The string to match against the pattern
 * @param pattern - The pattern that may include wildcards
 * @return 0 if a match is found
 */
int isMatch(const char* string, const char* pattern){
    int rc = 0;

    const char* remainingString = string;

    //Duplicate pattern to preserve original content after strtok call
    // Note: declaring var length char arrays on the stack like this is allowed by ISO C99 compilers.
    char remainingPattern[strlen(pattern)+1];
    memcpy(remainingPattern, pattern, strlen(pattern)+1);

    //Strtok will replace our wildcards with null terminators
    //so we need to reference the original pattern pointer with
    //this current location index.
    int remainingPatternIndex = 0;

    //Get first token from pattern split by delimiter
    char* saveRemainingPattern;
    char* token = strtok_r(remainingPattern,"*", &saveRemainingPattern);

    while(token != NULL && rc == 0){
        char* tokenInName = strstr(remainingString, token);

        if(tokenInName == NULL){
            //Token was not found in serviceName
            rc = -1;
        }else{
           if(pattern[remainingPatternIndex] != '*' && tokenInName != remainingString){
               //No wildcard was found at the current location yet the found token
               //did not start at the beginning of the remaining name. There are
               //leading characters that are not accounted for.
               rc = -2;
           }else{
               //Adjust the remaining name and the current index of the remaining pattern
               remainingString = tokenInName + strlen(token);
               remainingPatternIndex = (token + strlen(token)) - remainingPattern;
           }
        }

        //Get the next token
        token = strtok_r(NULL, "*", &saveRemainingPattern);
    }

    //Check for trailing wildcard
    if(rc == 0 && pattern[remainingPatternIndex] != '*' && strlen(remainingString) != 0){
        //The pattern did not end with a wildcard but there are
        //trailing characters that are not accounted for.
        rc = -3;
    }

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(5),
                    "isMatch",
                    TRACE_DATA_STRING(string, "string"),
                    TRACE_DATA_STRING(pattern, "pattern"),
                    TRACE_DATA_INT(rc, "rc (0 == match)"),
                    TRACE_DATA_END_PARMS);
    }

    return rc;
}

/**
 * Find an available client service by a given name in a given registration.
 *
 * @param serviceName - The name of the available service to find
 * @param reg_p - The registration in which to find the available service
 *
 * @return availServ_p - The found available service or NULL if one was not found
 */
AvailableService_t * findClientAvailableServiceByName(unsigned char* serviceName, struct wolaRegistration * reg_p){
    struct availableService* availServ_p = reg_p->availServiceFirst_p;

    while(availServ_p != NULL && isMatch(serviceName, availServ_p->serviceName) != 0){
        availServ_p = availServ_p->nextService;
    }

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(2),
                    "findClientAvailableServiceByName finished search for a matching available service",
                    TRACE_DATA_RAWDATA(strnlen(serviceName, BBOA_REQUEST_SERVICE_NAME_MAX), serviceName, "service name"),
                    TRACE_DATA_RAWDATA( ((availServ_p != NULL) ? sizeof(struct availableService) : 0), availServ_p, "availableService element"),
                    TRACE_DATA_END_PARMS);
    }

    return availServ_p;
}

/**
 * Find an available service by local comm connection handle.  This is called by
 * client code when a service is added to the available queue, but the subsequent
 * local comm read fails.  We need to remove the service from the queue before we
 * return to the caller.
 * @return availServ_p - The found available service or NULL if one was not found
 */
static AvailableService_t * findClientAvailableServiceByConnHdl(OpaqueClientConnectionHandle_t* localCommHandle_p, struct wolaRegistration * reg_p) {
    struct availableService* availServ_p = reg_p->availServiceFirst_p;

    while((availServ_p != NULL) &&
          (memcmp(localCommHandle_p, &(availServ_p->client_conn_handle), sizeof(*localCommHandle_p)) != 0)) {
        availServ_p = availServ_p->nextService;
    }

    return availServ_p;
}

/**
 * Find a wait service in the given client registration that matches the 
 * given serviceName.
 *
 * The wait service represents a server-side thread waiting for the given
 * serviceName in the given client to become available.
 *
 * This method is called by the client (in putClientService) when it has a
 * new service it wants to make available.  If a server-side thread is already
 * waiting for that service, its waitService entry will be returned (and 
 * then released by putClientService).
 *
 * @param serviceName - The name of the newly available service. The serviceName 
 *                      may be wildcarded.  The first waitService name that matches
 *                      is returned.
 * @param reg_p - The client's WOLA registration, in which to find the wait service
 *
 * @return waitServ_p - The found wait service or NULL if one was not found
 */
WaitService_t * findWaitServiceByName(unsigned char* serviceName, struct wolaRegistration * reg_p){
    struct waitService* waitServ_p = reg_p->waitServiceFirst_p;

    while(waitServ_p != NULL && isMatch(waitServ_p->serviceName, serviceName) != 0){
        waitServ_p = waitServ_p->nextService;
    }

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(3),
                    "findWaitServiceByName finished search for a matching wait service",
                    TRACE_DATA_RAWDATA(strnlen(serviceName, BBOA_REQUEST_SERVICE_NAME_MAX), serviceName, "service name"),
                    TRACE_DATA_RAWDATA( ((waitServ_p != NULL) ? sizeof(struct waitService) : 0), waitServ_p, "waitService element"),
                    TRACE_DATA_END_PARMS);
    }

    return waitServ_p;
}

/**
 * Find a wait service in the given client registration that matches the
 * given unique token (provided when the waiter was created).
 *
 * The wait service represents a server-side thread waiting for the given
 * serviceName in the given client to become available.
 *
 * This method is called by the STIMERM exit during cancel, and by the Java code
 * when a hung request is detected and the waiter must be cancelled.
 *
 * @param uniqueToken - The unique token that was provided when the waiter was created.
 * @param reg_p - The client's WOLA registration, in which to find the wait service
 *
 * @return waitServ_p - The found wait service or NULL if one was not found
 */
WaitService_t * findWaitServiceByToken(long long uniqueToken, struct wolaRegistration * reg_p){
    struct waitService* waitServ_p = reg_p->waitServiceFirst_p;

    while((waitServ_p != NULL) && (waitServ_p->uniqueToken != uniqueToken)) {
        waitServ_p = waitServ_p->nextService;
    }

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(3),
                    "findWaitServiceByToken finished search for a matching wait service",
                    TRACE_DATA_RAWDATA(sizeof(long long), &uniqueToken, "Unique token"),
                    TRACE_DATA_RAWDATA( ((waitServ_p != NULL) ? sizeof(struct waitService) : 0), waitServ_p, "waitService element"),
                    TRACE_DATA_END_PARMS);
    }

    return waitServ_p;
}

/**
 * Remove a given available service from a given registration's chain.
 *
 * An availableService represents a client-side thread waiting to be invoked 
 * by the server.  The availableService is removed by the server when a 
 * server-side thread is ready to invoke it (see getClientService).
 *
 * @param reg_p - The client registration owning the available service chain
 * @param availServ_p - The available service to be removed
 */
int removeAvailableServiceFromChain(PloCompareAndSwapAreaDoubleWord_t* swapArea, struct wolaRegistration* reg_p, struct availableService* availServ_p){
    int ploRC = 0;

    PloStoreAreaDoubleWord_t storeArea1;
    PloStoreAreaDoubleWord_t storeArea2;
    PloStoreAreaDoubleWord_t storeArea3;
    memset(&storeArea1, 0, sizeof(storeArea1));
    memset(&storeArea2, 0, sizeof(storeArea2));
    memset(&storeArea3, 0, sizeof(storeArea3));

    struct availableService* tempAvailServ_p = availServ_p;

    if(tempAvailServ_p != NULL){
        struct availableService* tempNextService = tempAvailServ_p->nextService;
        struct availableService* tempPreviousService = tempAvailServ_p->previousService;

        //Remove from queue
        //Check for null values to maintain head/tail and registration pointer values for first and last
        if(tempPreviousService == NULL && tempNextService != NULL){
            //We are the first service in the list.
            storeArea1.storeLocation_p = &(tempNextService->previousService);
            storeArea1.storeValue = (unsigned long long) NULL;

            storeArea2.storeLocation_p = &(reg_p->availServiceFirst_p);
            storeArea2.storeValue = (unsigned long long) tempNextService;

         }else if(tempNextService == NULL && tempPreviousService != NULL){
            //We are the last service in the list
            storeArea1.storeLocation_p = &(tempPreviousService->nextService);
            storeArea1.storeValue = (unsigned long long) NULL;

            storeArea2.storeLocation_p = &(reg_p->availServiceLast_p);
            storeArea2.storeValue = (unsigned long long) tempPreviousService;

        }else if(tempNextService != NULL && tempPreviousService != NULL){
            //We are in the middle of the list
            storeArea1.storeLocation_p = &(tempPreviousService->nextService);
            storeArea1.storeValue = (unsigned long long)tempNextService;

            storeArea2.storeLocation_p = &(tempNextService->previousService);
            storeArea2.storeValue = (unsigned long long) tempPreviousService;

        }else{
            //We are the only member in the list
            storeArea1.storeLocation_p = &(reg_p->availServiceFirst_p);
            storeArea1.storeValue = (unsigned long long) NULL;

            storeArea2.storeLocation_p = &(reg_p->availServiceLast_p);
            storeArea2.storeValue = (unsigned long long) NULL;
        }

        //Keep track of service count
        storeArea3.storeLocation_p = &(reg_p->availServiceCount);
        storeArea3.storeValue = reg_p->availServiceCount - 1;

        ploRC = ploCompareAndSwapAndTripleStoreDoubleWord(swapArea->compare_p, swapArea, &storeArea1, &storeArea2, &storeArea3);

    }else{
        //The available service to remove is NULL and most likely has been removed by someone else.
        ploRC = -1;
    }

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(4),
                    ((ploRC == 0) ? "removeAvailableServiceFromChain has removed a service" : "removeAvailableServiceFromChain could not yet perform locked operation"),
                    TRACE_DATA_RAWDATA(sizeof(reg_p->availServiceCount), reg_p->availServiceCount, "available service count"),
                    TRACE_DATA_END_PARMS);
    }

    return ploRC;
}

/**
 * Remove a given wait service from a given registration's chain.
 *
 * A waitService represents a server-side thread waiting to invoke
 * a client-hosted service.  This method is called by the client when
 * it's ready to make the service available. 
 *
 * Note: The waitService can be removed by either the client or by a stimer
 * set by the server. There's no code in here to verify the given waitServ_p
 * is actually still in the chain for the given reg_p.  Perhaps we should
 * add some?
 *
 * @param reg_p - The client registration owning the wait service chain
 * @param waitServ_p - The wait service to be removed
 */
int removeWaitServiceFromChain(PloCompareAndSwapAreaDoubleWord_t* swapArea, struct wolaRegistration* reg_p, struct waitService* waitServ_p){
    int ploRC = 0;

    PloStoreAreaDoubleWord_t storeArea1;
    PloStoreAreaDoubleWord_t storeArea2;
    PloStoreAreaDoubleWord_t storeArea3;
    memset(&storeArea1, 0, sizeof(storeArea1));
    memset(&storeArea2, 0, sizeof(storeArea2));
    memset(&storeArea3, 0, sizeof(storeArea3));

    struct waitService* tempWaitServ_p = waitServ_p;

    if(tempWaitServ_p != NULL){
        struct waitService* tempNextService = tempWaitServ_p->nextService;
        struct waitService* tempPreviousService = tempWaitServ_p->previousService;

        //Remove from queue
        //Check for null values to maintain head/tail and registration pointer values for first and last
        if(tempPreviousService == NULL && tempNextService!= NULL){
            //We are the first service in the list.
            storeArea1.storeLocation_p = &(tempNextService->previousService);
            storeArea1.storeValue = (unsigned long long) NULL;

            storeArea2.storeLocation_p = &(reg_p->waitServiceFirst_p);
            storeArea2.storeValue = (unsigned long long) tempNextService;

         }else if(tempNextService == NULL && tempPreviousService != NULL){
            //We are the last service in the list
            storeArea1.storeLocation_p = &(tempPreviousService->nextService);
            storeArea1.storeValue = (unsigned long long) NULL;

            storeArea2.storeLocation_p = &(reg_p->waitServiceLast_p);
            storeArea2.storeValue = (unsigned long long) tempPreviousService;

        }else if(tempNextService != NULL && tempPreviousService != NULL){
            //We are in the middle of the list
            storeArea1.storeLocation_p = &(tempPreviousService->nextService);
            storeArea1.storeValue = (unsigned long long)tempNextService;

            storeArea2.storeLocation_p = &(tempNextService->previousService);
            storeArea2.storeValue = (unsigned long long) tempPreviousService;

        }else{
            //We are the only member in the list
            storeArea1.storeLocation_p = &(reg_p->waitServiceFirst_p);
            storeArea1.storeValue = (unsigned long long) NULL;

            storeArea2.storeLocation_p = &(reg_p->waitServiceLast_p);
            storeArea2.storeValue = (unsigned long long) NULL;
        }

        //Keep track of service count
        storeArea3.storeLocation_p = &(reg_p->waitServiceCount);
        storeArea3.storeValue = reg_p->waitServiceCount - 1;

        ploRC = ploCompareAndSwapAndTripleStoreDoubleWord(swapArea->compare_p, swapArea, &storeArea1, &storeArea2, &storeArea3);

    }else{
        //The wait service to remove is NULL and most likely has been removed by someone else.
        ploRC = -1;
    }

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(4),
                    ((ploRC == 0) ? "removeWaitServiceFromChain has removed a service" : "removeWaitServiceFromChain could not yet perform locked operation"),
                    TRACE_DATA_RAWDATA(sizeof(reg_p->waitServiceCount), reg_p->waitServiceCount, "wait service count"),
                    TRACE_DATA_END_PARMS);
    }

    return ploRC;
}

/**
 * Initialize the given waitService_p storage area.
 *
 * @param waitService_p - The wait service to init.
 * @param pet - The PEToken to be added to the wait service
 * @param serviceName - The service name
 * @param reg_p - The registration to be added as the wait service parent
 * @param waiterToken - The unique token to use when creating this waiter.
 *
 * @return waitService_p 
 */
WaitService_t * initializeWaitService(WaitService_t * waitService_p, 
                                      iea_PEToken pet, 
                                      unsigned char* serviceName, 
                                      struct wolaRegistration* reg_p,
                                      long long waiterToken) {

    memset(waitService_p, 0, sizeof(struct waitService)); //clear memory

    memcpy(waitService_p->serviceName, serviceName, strlen(serviceName));
    waitService_p->serviceNameLength = strlen(serviceName);
    memcpy(waitService_p->eye, BBOAWSRV_EYE, sizeof(BBOAWSRV_EYE));
    waitService_p->thisRegistration = reg_p;
    memcpy(waitService_p->pet, pet, sizeof(pet));
    waitService_p->uniqueToken = waiterToken;

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(6),
                    "initializeWaitService",
                    TRACE_DATA_RAWDATA(sizeof(*waitService_p), waitService_p, "waitService_p"),
                    TRACE_DATA_RAWDATA(strnlen(serviceName, BBOA_REQUEST_SERVICE_NAME_MAX), serviceName, "service name"),
                    TRACE_DATA_END_PARMS);
    }

    return waitService_p;
}

/**
 * Create a wait service given the input parameters.
 *
 * A wait service represents a server thread waiting to invoke a client-hosted service.
 * The server calls this method when the client-side service that it wants to invoke
 * is not yet available.  The server thread is paused via the waitService and eventually
 * gets posted/released once the client-side service becomes available. 
 *
 * @param pet - The PEToken to be added to the wait service
 * @param serviceName - The service name
 * @param reg_p - The registration to be added as the wait service parent
 * @param waiterToken - A unique token (on this system) which identifies the waiter.
 *
 * @return newWaitService_p - The newly created wait service
 */
WaitService_t * createWaitService(iea_PEToken pet, unsigned char* serviceName, struct wolaRegistration* reg_p, long long waiterToken){

    //Create a new wait service and populate
    struct waitService* newWaitService_p = getCellPoolCell(reg_p->wolaAnchor_p->waitServiceCellPoolID);
    if(newWaitService_p != NULL){

        initializeWaitService( newWaitService_p, pet, serviceName, reg_p, waiterToken );

    } else {
        // TODO: FFDC candidate.  
    }

    return newWaitService_p;
}

/**
 * Initialize the given newAvailService.
 *
 * @param newAvailService - the available service to init.
 * @param clientConnHandle - The local comm connection handle for this service
 * @param serviceName - The service name - may be wildcarded.
 * @param reg_p - The registration to be added as the available service parent
 *
 * @return newAvailService.
 */
AvailableService_t * initializeAvailableService(AvailableService_t * newAvailService, 
                                                struct localCommClientConnectionHandle* clientConnHandle, 
                                                unsigned char* serviceName, 
                                                struct wolaRegistration* reg_p) {

    memset(newAvailService, 0, sizeof(struct availableService)); //clear memory

    memcpy(newAvailService->serviceName, serviceName, strlen(serviceName));
    newAvailService->serviceNameLength = strlen(serviceName);
    memcpy(newAvailService->eye, BBOAASRV_EYE, sizeof(BBOAASRV_EYE));
    newAvailService->thisRegistration = reg_p;
    memcpy(&newAvailService->client_conn_handle, clientConnHandle, sizeof(struct localCommClientConnectionHandle));

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                TP(7),
                "initializeAvailableService",
                TRACE_DATA_RAWDATA(strnlen(serviceName, BBOA_REQUEST_SERVICE_NAME_MAX), serviceName, "service name"),
                TRACE_DATA_RAWDATA( sizeof(struct availableService), newAvailService, "availService element"),
                TRACE_DATA_END_PARMS);
    }

    return newAvailService;
}

/**
 * Create an available service given the input parameters.
 *
 * An availableService represents a client-side thread hosting a client service,
 * waiting to be invoked by the server.  This method is called by the client when
 * it's making a service available but there's currently no waitService waiting
 * for it (i.e. currently no server-side thread waiting to invoke the service).
 *
 * @param clientConnHandle - The local comm connection handle for this service
 * @param serviceName - The service name - may be wildcarded.
 * @param reg_p - The registration to be added as the available service parent
 *
 * @return newAvailService - The newly created available service
 */
AvailableService_t * createAvailableService(struct localCommClientConnectionHandle* clientConnHandle, 
                                            unsigned char* serviceName, 
                                            struct wolaRegistration* reg_p) {

    //Create a new wait service and populate
    struct availableService* newAvailService = (struct availableService*) getCellPoolCell(reg_p->wolaAnchor_p->availServiceCellPoolID);

    if(newAvailService != NULL){
        initializeAvailableService(newAvailService, clientConnHandle, serviceName, reg_p);
    } else {
        // TODO
    }

    return newAvailService;
}

/**
 * Add a given wait service to a given registration's wait chain.
 *
 * @param reg_p - The wolaRegistration owning the wait service chain
 * @param newWaitService_p - The new wait service to add to the chain
 */
int addWaitServiceToChain(PloCompareAndSwapAreaDoubleWord_t* swapArea, struct wolaRegistration* reg_p, struct waitService* newWaitService_p){

    int ploRC = 0;

    PloStoreAreaDoubleWord_t storeArea1;
    PloStoreAreaDoubleWord_t storeArea2;
    PloStoreAreaDoubleWord_t storeArea3;
    memset(&storeArea1, 0, sizeof(storeArea1));
    memset(&storeArea2, 0, sizeof(storeArea2));
    memset(&storeArea3, 0, sizeof(storeArea3));

    struct waitService* tempWaitServiceLast_p = reg_p->waitServiceLast_p;

    //Add to end of wait chain
    //Check for null values to maintain head/tail and registration pointer values for first and last
    if(tempWaitServiceLast_p != NULL){
        //We have wait services in the list

        //We can set this value without a lock since it does not affect existing queue members
        newWaitService_p->previousService = reg_p->waitServiceLast_p;

        storeArea1.storeLocation_p = &(tempWaitServiceLast_p->nextService);
        storeArea1.storeValue = (unsigned long long) newWaitService_p;

        storeArea2.storeLocation_p = &(reg_p->waitServiceLast_p);
        storeArea2.storeValue = (unsigned long long) newWaitService_p;

    }else{
        //No wait services are in the list yet. Set the first and last to the new waiting service.

        storeArea1.storeLocation_p = &(reg_p->waitServiceFirst_p);
        storeArea1.storeValue = (unsigned long long) newWaitService_p;

        storeArea2.storeLocation_p = &(reg_p->waitServiceLast_p);
        storeArea2.storeValue = (unsigned long long) newWaitService_p;
    }

    //Keep track of service count
    storeArea3.storeLocation_p = &(reg_p->waitServiceCount);
    storeArea3.storeValue = reg_p->waitServiceCount+1;

    ploRC = ploCompareAndSwapAndTripleStoreDoubleWord(swapArea->compare_p, swapArea, &storeArea1, &storeArea2, &storeArea3);

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(8),
                    ((ploRC == 0) ? "addWaitServiceToChain has added a service" : "addWaitServiceToChain could not yet perform locked operation"),
                    TRACE_DATA_RAWDATA(sizeof(reg_p->waitServiceCount), reg_p->waitServiceCount, "wait service count"),
                    TRACE_DATA_END_PARMS);
    }

    return ploRC;
}

/**
 * Add a given available service to a given registration's available chain.
 *
 * @param reg_p - The wolaRegistration owning the available service chain
 * @param newAvailService - The new available service to add to the chain
 */
int addAvailableServiceToChain(PloCompareAndSwapAreaDoubleWord_t* swapArea, struct wolaRegistration* reg_p, struct availableService* newAvailService_p){

    int ploRC = 0;

    PloStoreAreaDoubleWord_t storeArea1;
    PloStoreAreaDoubleWord_t storeArea2;
    PloStoreAreaDoubleWord_t storeArea3;
    memset(&storeArea1, 0, sizeof(storeArea1));
    memset(&storeArea2, 0, sizeof(storeArea2));
    memset(&storeArea3, 0, sizeof(storeArea3));

    struct availableService* tempAvailServiceLast_p = reg_p->availServiceLast_p;

    //Add to end of available chain
    //Check for null values to maintain head/tail and registration pointer values for first and last
    if(tempAvailServiceLast_p != NULL){
        //We have an available service in the list

        //We can set this value without a lock since it does not affect existing queue members
        newAvailService_p->previousService = reg_p->availServiceLast_p;

        storeArea1.storeLocation_p = &(tempAvailServiceLast_p->nextService);
        storeArea1.storeValue = (unsigned long long) newAvailService_p;

        storeArea2.storeLocation_p = &(reg_p->availServiceLast_p);
        storeArea2.storeValue = (unsigned long long) newAvailService_p;

    }else{
        //No available services are in the list yet. Set the first and last to the new available service.

        storeArea1.storeLocation_p = &(reg_p->availServiceFirst_p);
        storeArea1.storeValue = (unsigned long long) newAvailService_p;

        storeArea2.storeLocation_p = &(reg_p->availServiceLast_p);
        storeArea2.storeValue = (unsigned long long) newAvailService_p;
    }

    //Keep track of service count
    storeArea3.storeLocation_p = &(reg_p->availServiceCount);
    storeArea3.storeValue = reg_p->availServiceCount+1;

    ploRC = ploCompareAndSwapAndTripleStoreDoubleWord(swapArea->compare_p, swapArea, &storeArea1, &storeArea2, &storeArea3);

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(9),
                    ((ploRC == 0) ? "addAvailableServiceToChain has added a service" : "addAvailableServiceToChain could not yet perform locked operation"),
                    TRACE_DATA_RAWDATA(sizeof(reg_p->availServiceCount), reg_p->availServiceCount, "available service count"),
                    TRACE_DATA_END_PARMS);
    }

    return ploRC;
}

/**
 * Pause the given waitService. 
 *
 * This function is used to pause a server-side thread that has 
 * created a waitService to wait for a particular client-side service
 * to become available.
 *
 * @param waitService_p - The waitService to pause (contains the pet)
 *
 * @return pauseRc - the return code of iea4pse
 */
int pauseWaitService(WaitService_t * waitService_p){
    //Pause
    iea_return_code pauseRc;
    iea_auth_type pauseAuthType = IEA_AUTHORIZED;
    iea_release_code releaseCode;

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(10),
                    "pauseOnWaitService is pausing",
                    TRACE_DATA_RAWDATA(sizeof(struct waitService), waitService_p, "waitService_p"),
                    TRACE_DATA_RAWDATA(sizeof(waitService_p->pet), waitService_p->pet, "PEToken"),
                    TRACE_DATA_END_PARMS);
    }

    unsigned char oldKey = switchToKey0();
    iea4pse(&pauseRc, pauseAuthType, waitService_p->pet, waitService_p->pet, releaseCode);
    switchToSavedKey(oldKey);

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(1),
                    "pauseOnWaitService return from pause",
                    TRACE_DATA_INT(pauseRc, "Return code"),
                    TRACE_DATA_RAWDATA(sizeof(releaseCode), &releaseCode, "Release code"),
                    TRACE_DATA_RAWDATA(sizeof(waitService_p->pet), waitService_p->pet, "New PET"),
                    TRACE_DATA_END_PARMS);
    }

    if(pauseRc != 0){
        //The pause failed
        return WOLA_SERVICE_QUEUES_RC_PAUSEFAILED;

    }else if((memcmp(releaseCode, WOLA_SERVICE_PET_UNREGISTERING, sizeof(releaseCode)) == 0)){
        //We are being unregistered
        return WOLA_SERVICE_QUEUES_RC_UNREGISTERING;

    }else if((memcmp(releaseCode, WOLA_SERVICE_PET_TIMEOUT, sizeof(releaseCode)) == 0)){
        //We timed out
        return WOLA_SERVICE_QUEUES_RC_PAUSE_TIMEOUT;
    }else if ((memcmp(releaseCode, WOLA_SERVICE_PET_INTERRUPT, sizeof(releaseCode)) == 0)) {
        // We were interrupted by requestInterrupt-1.0
        return WOLA_SERVICE_QUEUES_RC_PAUSE_INTERRUPTED;
    }else{
        return WOLA_SERVICE_QUEUES_RC_OK;
    }
}

/**
 * Release the PET associated with the given waitService.
 *
 * This function is used to wake up a server-side thread that has 
 * been waiting for a particular client-side service to become available.
 *
 * @param waitServ_p the waitService to release (contains the PET)
 * @param releaseCode 
 *
 * @return iea4rls rc
 */
int releaseWaitService(struct waitService* waitServ_p, const char* releaseCode) {
    iea_return_code releaseRc;
    iea_auth_type authType = IEA_AUTHORIZED;

    unsigned char oldKey = switchToKey0();
    iea4rls(&releaseRc, authType, waitServ_p->pet, (char *) releaseCode);
    switchToSavedKey(oldKey);

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(11),
                    "releaseWaitService released PEToken",
                    TRACE_DATA_RAWDATA(sizeof(struct waitService), waitServ_p, "waitService"),
                    TRACE_DATA_RAWDATA(sizeof(waitServ_p->pet), waitServ_p->pet, "PEToken"),
                    TRACE_DATA_STRING(((releaseCode != NULL) ? releaseCode : "null"), "releaseCode"),
                    TRACE_DATA_HEX_INT(releaseRc, "releaseRc"),
                    TRACE_DATA_END_PARMS);
    }

    if(releaseRc == 0){
        return 0;
    }else{
        return WOLA_SERVICE_QUEUES_RC_RELEASEFAILED;
    }
}

/**
 * {@inheritDoc}
 *
 */
void unregisterServiceQueues(struct wolaRegistration* reg_p){
    //Prevent all future service queue functions.
    int ploRC = 0;

    PloCompareAndSwapAreaDoubleWord_t swapArea;

    unsigned long long* counter_p = ((unsigned long long*)(&(reg_p->serviceQueuesPLOCounter)));

    swapArea.expectedValue = *counter_p;
    swapArea.compare_p = counter_p;

    PloStoreAreaDoubleWord_t storeArea;
    memset(&storeArea, 0, sizeof(storeArea));

    storeArea.storeLocation_p = &(reg_p->serviceQueuesState);
    storeArea.storeValue = BBOARGE_SERVICE_QUEUE_QUIESCING;

    do{
        swapArea.replaceValue = swapArea.expectedValue + 1;

        ploRC = ploCompareAndSwapAndStoreDoubleWord(swapArea.compare_p, &swapArea, &storeArea);

    }while(ploRC != 0);

    //Iterate through wait services
    struct waitService* waitServ_p = reg_p->waitServiceFirst_p;
    while(waitServ_p != NULL){

        //Save the next service pointer value so we do not lose it when the cell is freed.
        struct waitService* waitServHelper_p = waitServ_p->nextService;

        //Release the service. Freeing the cell and boarding the PEToken
        //is done by the released getClientService call.
        int releaseRC = releaseWaitService(waitServ_p, WOLA_SERVICE_PET_UNREGISTERING);

        waitServ_p = waitServHelper_p;

    }

    //Iterate through available services
    struct availableService* availServ_p = reg_p->availServiceFirst_p;
    while(availServ_p != NULL){

        struct availableService* availServHelper_p = availServ_p->nextService;

        //Close connection
        int localRC = localCommClose((void *)& (availServ_p->client_conn_handle));

        //Free cell pool
        freeCellPoolCell(reg_p->wolaAnchor_p->availServiceCellPoolID, availServ_p);

        availServ_p = availServHelper_p;
    }
}

/**
 * Remove an available service from the client's queue by connection handle.
 * This is used by the client receive request code, to clean up after a failed local comm read.
 *
 * @param localCommHandle_p A pointer to the local comm connection handle provided to us when the
 *        available service entry was created.
 * @param reg_p A pointer to the WOLA registration hosting the available service.
 */
void removeAvailableServiceByHandle(OpaqueClientConnectionHandle_t* localCommHandle_p, volatile WolaRegistration_t* reg_p) {
     int ploRC = 0;
     PloCompareAndSwapAreaDoubleWord_t swapArea;

     do{
         swapArea.expectedValue = reg_p->serviceQueuesPLOCounter;
         swapArea.compare_p = (void*)(&(reg_p->serviceQueuesPLOCounter));

         // Try to find the available service entry that we want to remove.
         AvailableService_t* service_p = findClientAvailableServiceByConnHdl(localCommHandle_p, (WolaRegistration_t*)reg_p);
         if (service_p != NULL) {
             swapArea.replaceValue = swapArea.expectedValue + 1;
             ploRC = removeAvailableServiceFromChain(&swapArea, (WolaRegistration_t*)reg_p, service_p);
             if(ploRC == 0){
                 freeCellPoolCell(reg_p->wolaAnchor_p->availServiceCellPoolID, service_p);
             }
         } else {
             ploRC = 0; // Not found, already removed.
         }
     } while(ploRC != 0);
}

/**
 * setTimer exit routine.  This function is called when the wait service
 * timer pops.  Release the wait service with a TIMEOUT release code.
 *
 * @param waitService_p - the wait service to wake up
 *
 */
void stimermExitForWaitService( void * waitService_p ) {

   //  remove the waitServer element from the chain.
    struct waitService* waitServ_p = (WaitService_t *) waitService_p;
    int ploRC = 0;
    PloCompareAndSwapAreaDoubleWord_t swapArea;

    WolaRegistration_t * reg_p = (WolaRegistration_t*)(waitServ_p->thisRegistration);

    do{
        unsigned long long* counter_p = ((unsigned long long*)(&(reg_p->serviceQueuesPLOCounter)));
        swapArea.expectedValue = *counter_p;
        swapArea.compare_p = counter_p;

        //check if this specific waitService element is still there in the RGE iterating thru the waitService queue
        struct waitService* foundService = findWaitServiceByToken(waitServ_p->uniqueToken,reg_p);
        if(foundService != NULL) {
            swapArea.replaceValue = swapArea.expectedValue + 1;
            ploRC = removeWaitServiceFromChain(&swapArea, reg_p, foundService);
            if(ploRC == 0){
                releaseWaitService(foundService, WOLA_SERVICE_PET_TIMEOUT );
            }
        }else {
            ploRC = 0;
        }
    } while(ploRC != 0);

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(21),
                    "stimermExitForWaitService",
                    TRACE_DATA_RAWDATA(sizeof(WaitService_t), waitService_p, "waitService_p"),
                    TRACE_DATA_END_PARMS);
    }
}

/**
 * Start a STIMERM timer to cover and potentially timeout a
 * call to pauseWaitService.
 *
 * @param waitService_p - the wait service that will soon be paused
 * @param timeout_s - the stimerm timeout (in seconds)
 * @param petvet_p - A PETVET to provide to setTimer/STIMERM
 *
 * @return the rc from setTimer/STIMERM
 */
int startTimerForWaitService(WaitService_t * waitService_p,
                             int timeout_s,
                             PetVet * petvet_p) {

    int rc = setTimer(stimermExitForWaitService, 
                      waitService_p, 
                      timeout_s, 
                      petvet_p, 
                      FALSE,
                      &waitService_p->mvsTimerID);

    if (rc != 0) {
        // RC is a failure code from STIMERM.
        // TODO: good place for a native FFDC reporting mechanism
    }

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(31),
                    "startTimerForWaitService",
                    TRACE_DATA_RAWDATA(sizeof(WaitService_t), waitService_p, "waitService_p"),
                    TRACE_DATA_INT(timeout_s, "timeout_s"),
                    TRACE_DATA_INT(rc, "setTimer rc"),
                    TRACE_DATA_END_PARMS);
    }

    return (rc == 0) ? 0 : WOLA_SERVICE_QUEUES_RC_STIMERM_FAILED;
}

/**
 * Cancel the STIMERM timer for the given wait service
 *
 * @param waitService_p - contains the MvsTimerID to cancel
 *
 * @return the rc from cancelTimer.
 */
int cancelTimerForWaitService(WaitService_t * waitService_p ) {

    int rc = cancelTimer( &waitService_p->mvsTimerID );

    if (rc != 0 && rc != 1) {
        // RC is a failure code from STIMERM.
        // TODO: good place for a native FFDC reporting mechanism
    }

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(41),
                    "cancelTimerForWaitService",
                    TRACE_DATA_RAWDATA(sizeof(WaitService_t), waitService_p, "waitService_p"),
                    TRACE_DATA_INT(rc, "rc"),
                    TRACE_DATA_END_PARMS);
    }

    return rc;
}

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
 *         24 if stimer failed
 *         28 if interrupted
 */
int getClientService(struct wolaRegistration * reg_p, 
                     unsigned char* serviceName, 
                     int timeout_s,
                     long long waiterToken,
                     struct localCommClientConnectionHandle* clientConnHandle){

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(12),
                    "getClientService entry",
                    TRACE_DATA_RAWDATA(sizeof(WolaRegistration_t), reg_p, "Wola registration element"),
                    TRACE_DATA_RAWDATA(BBOA_REQUEST_SERVICE_NAME_MAX, serviceName, "Service name"),
                    TRACE_DATA_INT(reg_p->serviceQueuesState, "serviceQueueState"),
                    TRACE_DATA_INT(timeout_s, "timeout_s"),
                    TRACE_DATA_RAWDATA(sizeof(long long), &waiterToken, "Waiter token"),
                    TRACE_DATA_END_PARMS);
    }

    int localRC = -1;

    if(reg_p->serviceQueuesState == BBOARGE_SERVICE_QUEUE_READY){

        struct waitService* newWaitService_p = NULL;
        iea_PEToken currentPET = {{0}};
        server_process_data* spd_p = getServerProcessData();
        int ploRC = 0;

        PloCompareAndSwapAreaDoubleWord_t swapArea;

        unsigned long long* counter_p = ((unsigned long long*)(&(reg_p->serviceQueuesPLOCounter)));

        swapArea.expectedValue = *counter_p;
        swapArea.compare_p = counter_p;

        do {

            swapArea.replaceValue = swapArea.expectedValue + 1;

            //If our wait service pointer is not null, that means our PLO call failed and
            //we need to cleanup the wait service work from the previous attempt: free the
            //cell pool, reset the pointer, and board the PEToken.
            if(newWaitService_p != NULL){

                freeCellPoolCell(reg_p->wolaAnchor_p->waitServiceCellPoolID, newWaitService_p);
                newWaitService_p = NULL;
                board((PetVet*)spd_p->petvet, currentPET);
            }

            //Check to see if unregistration is occuring. If so, stop search.
            if(reg_p->serviceQueuesState != BBOARGE_SERVICE_QUEUE_QUIESCING){

                //Find the service
                struct availableService* availServ_p = findClientAvailableServiceByName(serviceName, reg_p);

                if(availServ_p != NULL){

                    //The service was available. Return the connection handle, remove the service from the queue, and free the cell.
                    ploRC = removeAvailableServiceFromChain(&swapArea, reg_p, availServ_p);

                    if(ploRC == 0){
                        memcpy(clientConnHandle, &availServ_p->client_conn_handle, sizeof(struct localCommClientConnectionHandle));
                        freeCellPoolCell(reg_p->wolaAnchor_p->availServiceCellPoolID, availServ_p);
                        localRC = 0;
                    }
                }else{

                    //The service was not available. Run wait routine: get PEToken, create a wait service, add service to wait queue.
                    int petvetRC = 0;
                    petvetRC = pickup((PetVet*)spd_p->petvet, &currentPET);

                    if(petvetRC == 0){

                        newWaitService_p = createWaitService(currentPET, serviceName, reg_p, waiterToken);

                        ploRC = addWaitServiceToChain(&swapArea, reg_p, newWaitService_p);

                        //Pause
                        if(ploRC == 0){

                            int returnState = startTimerForWaitService( newWaitService_p, timeout_s, (PetVet *)spd_p->petvet); 

                            if ( returnState == 0) {
                            
                                returnState = pauseWaitService(newWaitService_p);

                                cancelTimerForWaitService( newWaitService_p ); 
                            } 

                            if(returnState == WOLA_SERVICE_QUEUES_RC_OK) {
                                //We've been unpasued with no problems. Return the connection handle.
                                memcpy(clientConnHandle, &newWaitService_p->client_conn_handle, sizeof(struct localCommClientConnectionHandle));
                            }
                            localRC = returnState;

                            //Cleanup is done whether we failed the pause, are unregistering, or have been release ok
                            memcpy(currentPET, newWaitService_p->pet, sizeof(currentPET));
                            freeCellPoolCell(reg_p->wolaAnchor_p->waitServiceCellPoolID, newWaitService_p);
                            board((PetVet*)spd_p->petvet, currentPET);
                        }
                    }else{
                        localRC = WOLA_SERVICE_QUEUES_RC_PET_PICKUPFAILED;
                    }
                }
            }else{
                //We are being unregistered
                ploRC = 0;
            }

        } while (ploRC != 0);

    }else{
        if (TraceActive(trc_level_detailed)) {
            TraceRecord(trc_level_detailed,
                        TP(13),
                        "The service queues are not in the ready state.",
                        TRACE_DATA_END_PARMS);
        }
    }

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(14),
                    "getClientService exit",
                    TRACE_DATA_RAWDATA(sizeof(LocalCommClientConnectionHandle_t), clientConnHandle, "Client connection handle"),
                    TRACE_DATA_HEX_INT(localRC, "getClientService return code"),
                    TRACE_DATA_END_PARMS);
    }

    return localRC;

}

/**
 * {@inheritDoc}
 *
 */
int putClientService(struct localCommClientConnectionHandle* clientConnHandle, struct wolaRegistration* reg_p, unsigned char* serviceName, unsigned int async){

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(16),
                    "putClientService entry",
                    TRACE_DATA_RAWDATA(sizeof(LocalCommClientConnectionHandle_t), clientConnHandle, "Client connection handle element"),
                    TRACE_DATA_RAWDATA(sizeof(WolaRegistration_t), reg_p, "Wola registration element"),
                    TRACE_DATA_RAWDATA(strnlen(serviceName, BBOA_REQUEST_SERVICE_NAME_MAX), serviceName, "service name"),
                    TRACE_DATA_END_PARMS);
    }

    int rc = 0;

    if(reg_p->serviceQueuesState == BBOARGE_SERVICE_QUEUE_READY){

        struct availableService* newAvailService_p = NULL;
        int ploRC = 0;

        PloCompareAndSwapAreaDoubleWord_t swapArea;

        unsigned long long* counter_p = ((unsigned long long*)(&(reg_p->serviceQueuesPLOCounter)));

        swapArea.expectedValue = *counter_p;
        swapArea.compare_p = counter_p;

        do {

            swapArea.replaceValue = swapArea.expectedValue + 1;

            //If our available service pointer is not null, that means our PLO call failed and
            //we need to cleanup the available service work from the previous attempt: free the
            //cell pool and reset the pointer
            if(newAvailService_p != NULL){
                freeCellPoolCell(reg_p->wolaAnchor_p->availServiceCellPoolID, newAvailService_p);
                newAvailService_p = NULL;
            }

            //Check to see if unregistration is occuring. If so, stop putting the service.
            if(reg_p->serviceQueuesState != BBOARGE_SERVICE_QUEUE_QUIESCING){

                //See if a service is waiting
                struct waitService* waitServ_p = findWaitServiceByName(serviceName, reg_p);

                if(waitServ_p != NULL){

                    //A service was waiting. Add the connection handle, remove the service from the queue, and release the waiting service.
                    ploRC = removeWaitServiceFromChain(&swapArea, reg_p, waitServ_p);
                    if(ploRC == 0){
                        memcpy(&waitServ_p->client_conn_handle, clientConnHandle, sizeof(struct localCommClientConnectionHandle));

                        releaseWaitService(waitServ_p, WOLA_SERVICE_QUEUES_RC_OK);
                    }
                }else{
                    if(async == 1){
                       // no service match, return back
                       return 1;
                    }else {
                        //No service was waiting for us. Create an available service, add service to available queue.
                        newAvailService_p = createAvailableService(clientConnHandle, serviceName, reg_p);

                        if(newAvailService_p != NULL){

                            ploRC = addAvailableServiceToChain(&swapArea, reg_p, newAvailService_p);
                        }
                    }
                }
            }else{
                //We are being unregistered
                ploRC = 0;
            }

        } while(ploRC != 0);

    }else{
        if (TraceActive(trc_level_detailed)) {
            TraceRecord(trc_level_detailed,
                        TP(17),
                        "The service queues are not in the ready state.",
                        TRACE_DATA_END_PARMS);
        }

        rc = 8;
    }

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(18),
                    "putClientService exit",
                    TRACE_DATA_INT(rc, "rc"),
                    TRACE_DATA_END_PARMS);
    }

    return rc;
}

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
                         long long waiterToken) {
    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(19),
                    "cancelClientService entry",
                    TRACE_DATA_RAWDATA(sizeof(WolaRegistration_t), reg_p, "Wola registration element"),
                    TRACE_DATA_RAWDATA(sizeof(long long), &waiterToken, "Waiter token"),
                    TRACE_DATA_END_PARMS);
    }

    if(reg_p->serviceQueuesState == BBOARGE_SERVICE_QUEUE_READY){
        int ploRC = 0;
        PloCompareAndSwapAreaDoubleWord_t swapArea;

        do{
            unsigned long long* counter_p = ((unsigned long long*)(&(reg_p->serviceQueuesPLOCounter)));
            swapArea.expectedValue = *counter_p;
            swapArea.compare_p = counter_p;

            //check if this specific waitService element is still there in the RGE iterating thru the waitService queue
            struct waitService* foundService = findWaitServiceByToken(waiterToken, reg_p);
            if(foundService != NULL) {
                swapArea.replaceValue = swapArea.expectedValue + 1;
                ploRC = removeWaitServiceFromChain(&swapArea, reg_p, foundService);
                if(ploRC == 0){
                    releaseWaitService(foundService, WOLA_SERVICE_PET_INTERRUPT );
                }
            }else {
                ploRC = 0; // Not in the queue anymore.
            }
        } while(ploRC != 0);
    } else {
        if (TraceActive(trc_level_detailed)) {
            TraceRecord(trc_level_detailed,
                        TP(20),
                        "The service queues are not in the ready state.",
                        TRACE_DATA_END_PARMS);
        }
    }

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(22),
                    "cancelClientService exit",
                    TRACE_DATA_END_PARMS);
    }
}
