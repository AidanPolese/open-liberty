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

#include <metal.h>
#include <string.h>

#include "include/gen/ihaacee.h"
#include "include/gen/ihapsa.h"
#include "include/gen/ikjtcb.h"

#include "include/mvs_cell_pool_services.h"
#include "include/mvs_plo.h"
#include "include/mvs_stimerm.h"
#include "include/mvs_utils.h"
#include "include/server_local_comm_api.h"
#include "include/server_process_data.h"
#include "include/server_wola_client.h"
#include "include/server_wola_connection_handle.h"
#include "include/server_wola_connection_pool.h"
#include "include/server_wola_registration.h"
#include "include/server_wola_shared_memory_anchor.h"

#define DEFAULT_LOCAL_COMM_CONNECT_WAIT_TIME_SECONDS 60

// "WOLA.x.y.z"
// strlen("WOLA") + Three 8 character names from Client + 3 "Dot" separators and a null terminator.
#define MAX_SAFPROFILESUFFIX_LENGTH   4 +          \
		                              (3 * 8) +    \
		                              3 + 1
/*
 * Build a SAF compatible profile suffix string with the 3 part WOLA names from the Client register
 * call.  Any padded blanks will need to go and folded to upper case.
 * "WOLA.x.y.z"
 */
static buildSafProfileSuffix(char* result, const char* firstPart, const char* secondPart, const char* thirdPart) {

    // The 3 parts are blank padded

    char* currPos = result + strlen("WOLA.");
    strcpy(result, "WOLA.");
    int i;

    // Copy first part
    for (i = 0;  (i < 8) && (firstPart[i] != ' ') && (firstPart[i] != '\0'); i++) {
        *currPos = firstPart[i];
        currPos++;
    }

    *currPos = '.';
    currPos++;

    // Add Second part
    for (i = 0;  (i < 8) && (secondPart[i] != ' ') && (secondPart[i] != '\0'); i++) {
        *currPos = secondPart[i];
        currPos++;
    }

    *currPos = '.';
    currPos++;

    // Add Third part
    for (i = 0;  (i < 8) && (thirdPart[i] != ' ') && (thirdPart[i] != '\0'); i++) {
        *currPos = thirdPart[i];
        currPos++;
    }

    *currPos = '\0';
}

/*
 * Forward declares
 */
int addConnectionToList(WolaRegistration_t * RGE_p, unsigned long long * ploCountOld_p,
                        WolaConnectionHandle_t * newHandle_p, WolaConnectionHandle_t ** listHead_p);
int changeConnHandleState(WolaConnectionHandle_t * handle_p, unsigned long long * ploCountOld_p,
                          unsigned long long oldState, unsigned long long newState);
int changeConnPoolState(WolaRegistration_t * RGE_p, unsigned long long * ploCountOld_p,
                        unsigned long long newState);
void destroyRGE(WolaRegistration_t * RGE_p, unsigned long long oldSTCK);
int insertIntoWaiterQueue(WolaRegistration_t * RGE_p, unsigned long long * ploCountOld_p,
                          WolaConnectionHandleWaiter_t * waiter_p);
int loadFromRGE(WolaRegistration_t * RGE_p, unsigned long long oldSTCK,
                unsigned long long * loadSource_p, unsigned long long * loadDest_p);
int loadFromHandle(WolaConnectionHandle_t * handle_p, unsigned long long * ploCountOld_p,
                   unsigned long long * loadSource_p, unsigned long long * loadDest_p);
int popFromFreeQueue(WolaRegistration_t * RGE_p, unsigned long long * ploCountOld_p,
                     WolaConnectionHandle_t * handleToPop_p);
int popFromWaiterQueue(WolaRegistration_t * RGE_p, unsigned long long * ploCountOld_p,
                       WolaConnectionHandleWaiter_t * waiter_p);
int releaseWaiter(WolaConnectionHandleWaiter_t * waiter_p, unsigned long long oldState, unsigned long long newState,
                  char * releaseCode, WolaClientConnectionHandle_t * clientHandle_p);
int removeConnectionFromList(WolaRegistration_t * RGE_p, unsigned long long * ploCountOld_p,
                             WolaConnectionHandle_t * removeHandle_p, WolaConnectionHandle_t * prevHandle_p);
int updateActiveConnCount(WolaRegistration_t * RGE_p, unsigned long long * ploCountOld_p,
                          unsigned long long newCount);
void waiterTimeout(void* inParm_p);

/**
 * Get a WOLA connection handle, either from the set of free connection handles on the registration
 * or from a newly created connection.
 *
 * @param clientRGE_p The registration which owns the connection
 * @param clientSTCK The last-known STCK value for the registration
 * @param waitTime Time to wait for a connection to become available, in seconds
 * @param inClientHandle_p A pointer to storage for a wolaClientConnectionHandle, which will be
 *                         filled with the connection handle info if the call is successful
 * @return 0 if successful, see header for other values
 */
int getPooledConnection(WolaRegistration_t * clientRGE_p, unsigned long long clientSTCK,
                        unsigned int waitTime, WolaClientConnectionHandle_t * inClientHandle_p,
                        struct bboapc1p* cicsParms_p)
{
    WolaConnectionHandle_t * wolaHandle_p;
    WolaClientConnectionHandle_t wolaClientHandle;
    unsigned long long ploCountOld;
    unsigned long long instanceCountOld;
    unsigned long long handleSTCK;
    int ploCC = 1;
    int newConnCount;
    unsigned int connPoolState;

    // Outer loop - do one of [pop from free chain|create new connection|create waiter] each iteration
    while (ploCC != 0) {
        wolaHandle_p = NULL;
        wolaClientHandle.handle_p = NULL;
        wolaClientHandle.instanceCount = 0;

        // Get the conn pool state, verify the RGE is still valid using the STCK, and load the RGE's conn pool PLO counter
        connPoolState = clientRGE_p->connPoolState;
        // PLO: compare input STCK value to current RGE STCK, load connection pool PLO count
        ploCC = loadFromRGE(clientRGE_p, clientSTCK, &(clientRGE_p->connPoolPLOCounter), &ploCountOld);

        if (ploCC != 0) {
            // STCK mismatch, registration is no longer valid for us to use
            return GetConn_RC_RgeStckChanged;
        }

        // Make sure connection pool is in a valid state
        if (connPoolState != BBOARGE_CONNPOOL_READY) {
            return GetConn_RC_ConnPoolNotReady;
        }

        // Get a connection handle from the free chain, if available
        if (clientRGE_p->freeConnHandlePoolHead_p != NULL) {
            wolaHandle_p = clientRGE_p->freeConnHandlePoolHead_p;
            wolaClientHandle.handle_p = wolaHandle_p;
            instanceCountOld = wolaHandle_p->ploArea.instanceCount;

            // PLO: compare and swap on RGE PLO count, store pointer to new head of free chain
            ploCC = popFromFreeQueue(clientRGE_p, &ploCountOld, wolaHandle_p);

            if (ploCC == 0) {
                // Verify the connection is still valid for the server registration using STCK
                // PLO: compare client vs. conn handle instance count, load server STCK from handle
                ploCC = loadFromHandle(wolaHandle_p, &instanceCountOld, &(wolaHandle_p->wolaServerRegistrationSTCK), &handleSTCK);

                if (ploCC == 0 &&
                    memcmp(&handleSTCK, &(clientRGE_p->serverRegistration_p->stckLastStateChange), sizeof(handleSTCK)) == 0) {

                    // PLO: compare and swap on handle instance count, then compare and swap on handle state to make it ready
                    ploCC = changeConnHandleState(wolaHandle_p, &instanceCountOld, BBOAHDL_POOLED, BBOAHDL_READY);

                    if (ploCC == 0) {
                        // Connection we popped is good
                        wolaClientHandle.instanceCount = wolaHandle_p->ploArea.instanceCount;
                        memcpy(inClientHandle_p, &wolaClientHandle, sizeof(WolaClientConnectionHandle_t));
                        return GetConn_RC_PooledConnectionOK;
                    }
                }
                // If we made it here, then one of the two PLO checks above failed, or the server registration STCK did not match the connection.
                // Since we should be the only ones modifying the handle after popping it, this means something went wrong, so we'll destroy the connection.
                int rc = destroyPooledConnection(clientRGE_p, clientSTCK, wolaHandle_p);
                if (rc == DestroyConn_RC_DestroyedRGE) {
                    return GetConn_RC_DestroyedRGE;
                }
                ploCC = 1;
            }
        } // end get from free chain

        // No free connections; get a new local comm connection, create a WOLA connection handle for it within the cell pool, and add it to the active chain
        else if (clientRGE_p->activeConnCount < clientRGE_p->maxConns) {

            // PLO: compare and swap RGE PLO count, store incremented active connection count
            ploCC = updateActiveConnCount(clientRGE_p, &ploCountOld, clientRGE_p->activeConnCount + 1);

            if (ploCC == 0) {
                // to hold return handle from local comm connect
                char lcommHandle[16];
                memset(lcommHandle, 0, sizeof(lcommHandle));

                // Build a null-terminated string of "WOLA.x.y.z" where x y and z are
                // the three parts of the WOLA three part name provided on the BBOA1REG call (WOLA register).
                // -x y and z should be trimmed of any white space, and should be folded to upper case.
                char localSafProfileString[MAX_SAFPROFILESUFFIX_LENGTH];
                buildSafProfileSuffix(localSafProfileString,
                                      clientRGE_p->wolaAnchor_p->wolaGroup,
                                      clientRGE_p->serverRegistration_p->serverNameSecondPart,
                                      clientRGE_p->serverRegistration_p->serverNameThirdPart);

                // See if we were passed an ACEE from CICS.  If we were, and this client has security
                // enabled, use that ACEE.  We assume that the server is set up to accept an alternate
                // ACEE because the client register request would have failed if security was specified
                // but the server's WOLA configuration would not allow it (which is the default).
                // Remember that the unauthorized caller is providing the ACEE pointer, it is not
                // obtained from a trusted chain.
                acee* targetACEE_p = NULL; /* Default is TCBSENV, or 'main' ACEE if not set */
                if ((clientRGE_p->flags.propAceeFromTrueIntoServer == 1) && (cicsParms_p != NULL) && (cicsParms_p->bboapc1p_acee != 0)) {
                    // Make sure the caller can read the ACEE that they passed.  Copy it into
                    // local storage.  This is what tWAS did - not sure if RACF required the
                    // ACEE to be in our key, or if this was just to check if the caller
                    // could read it.  Note that our security services expect the ACEE to
                    // exist in below the bar storage.
                    bbgz_psw pswFromLinkageStack;
                    extractPSWFromLinkageStack(&pswFromLinkageStack);
                    void* tempAcee_p = (void*)((cicsParms_p->bboapc1p_acee) & 0x7FFFFFFF);
                    targetACEE_p = __malloc31(sizeof(acee));
                    // TODO: allocation check.
                    memcpy_sk(targetACEE_p, tempAcee_p, sizeof(acee), pswFromLinkageStack.key);
                }

                // Compute a wait time, regarless of what the caller asked for.  Why?  The server
                // has a habit of losing connect requests under certain circumstances, such as
                // server shutdown and/or failure.  By specifying a timeout, we can be more confident
                // that if something went wrong, we would not hang indefinitely.
                unsigned int localWaitTime = waitTime;
                if ((localWaitTime > DEFAULT_LOCAL_COMM_CONNECT_WAIT_TIME_SECONDS) ||
                    (localWaitTime == 0)) {
                    localWaitTime = DEFAULT_LOCAL_COMM_CONNECT_WAIT_TIME_SECONDS;
                }

                // create new local comm connection.
                int rc, rsn, safRC, racfRC, racfRSN;
                rc = localCommClientConnect((SToken*)clientRGE_p->serverRegistration_p->stoken,
                                            LCOM_CLIENTID_WOLA,
                                            localWaitTime,
                                            targetACEE_p,
                                            localSafProfileString,
                                            lcommHandle,
                                            &rsn,
                                            &safRC,
                                            &racfRC,
                                            &racfRSN);

                if (targetACEE_p != NULL) {
                    free(targetACEE_p);
                }

                if (rc > 0) {
                    // no local comm connection
                    ploCC = 1;
                    while (ploCC != 0) {
                        // PLO: compare and swap RGE PLO count, store decremented active connection count
                        connPoolState = clientRGE_p->connPoolState;
                        newConnCount = clientRGE_p->activeConnCount - 1;
                        ploCC = updateActiveConnCount(clientRGE_p, &ploCountOld, newConnCount);
                    }
                    // If conn pool is tearing down and we're the last one out, destroy the RGE
                    if (connPoolState == BBOARGE_CONNPOOL_DESTROYING &&
                        newConnCount == 0) {
                        destroyRGE(clientRGE_p, clientSTCK);
                        return GetConn_RC_DestroyedRGE;
                    }
                    if (rc == LCOM_CLIENTCONNECT_RC_SAF) {
                        return GetConn_RC_LocalCommSAFError;
                    } else {
                        return GetConn_RC_LocalCommConnectError;
                    }
                }

                // if we did get a local comm connection, wrap it in a WOLA connection handle stored in the cell pool
                wolaHandle_p = getCellPoolCell(clientRGE_p->wolaAnchor_p->connectionHandleCellPoolID);
                if (wolaHandle_p == NULL) {
                    // couldn't get storage in cell pool
                    localCommClose(lcommHandle);
                    ploCC = 1;
                    while (ploCC != 0) {
                        // PLO: compare and swap RGE PLO count, store decremented active connection count
                        connPoolState = clientRGE_p->connPoolState;
                        newConnCount = clientRGE_p->activeConnCount - 1;
                        ploCC = updateActiveConnCount(clientRGE_p, &ploCountOld, newConnCount);
                    }
                    // If conn pool is tearing down and we're the last one out, destroy the RGE
                    if (connPoolState == BBOARGE_CONNPOOL_DESTROYING &&
                        newConnCount == 0) {
                        destroyRGE(clientRGE_p, clientSTCK);
                        return GetConn_RC_DestroyedRGE;
                    }
                    return GetConn_RC_CellPoolError;
                }

                // zero out except for the instance count
                unsigned int instanceTemp = wolaHandle_p->ploArea.instanceCount;
                memset(wolaHandle_p, 0, sizeof(WolaConnectionHandle_t));
                wolaHandle_p->ploArea.instanceCount = instanceTemp;

                // put local comm handle in wola handle
                memcpy(wolaHandle_p->localCommConnectionHandle, lcommHandle, sizeof(wolaHandle_p->localCommConnectionHandle));

                // set additional wola connection handle fields
                memcpy(wolaHandle_p->eye, BBOAHDL_EYE, sizeof(wolaHandle_p->eye));
                wolaHandle_p->version = BBOAHDL_VERSION_2;
                wolaHandle_p->size = sizeof(WolaConnectionHandle_t);
                wolaHandle_p->registration_p = clientRGE_p;
                wolaHandle_p->state = BBOAHDL_READY;

                ploCC = 1;
                // Try adding our new connection to the list of all connections until we succeed or the conn pool changes state
                while (ploCC != 0) {
                    if (clientRGE_p->connPoolState == BBOARGE_CONNPOOL_READY) {
                        // do these here since what we load from the RGE may change if the PLO count changes
                        memcpy(wolaHandle_p->wolaGroup, clientRGE_p->wolaAnchor_p->wolaGroup, sizeof(wolaHandle_p->wolaGroup));
                        memcpy(&(wolaHandle_p->wolaRegistrationSTCK), &(clientRGE_p->stckLastStateChange), sizeof(wolaHandle_p->wolaRegistrationSTCK));
                        memcpy(&(wolaHandle_p->wolaServerRegistrationSTCK), &(clientRGE_p->serverRegistration_p->stckLastStateChange), sizeof(wolaHandle_p->wolaServerRegistrationSTCK));

                        // add handle to chain of all connections
                        wolaHandle_p->nextHandle_p = clientRGE_p->allConnHandleListHead_p;
                        // PLO: compare and swap RGE PLO count, store our working handle as the new head of the list
                        ploCC = addConnectionToList(clientRGE_p, &ploCountOld, wolaHandle_p, (WolaConnectionHandle_t**) &(clientRGE_p->allConnHandleListHead_p));
                        if (ploCC == 0) {
                            // successfully added to list of all connections
                            wolaClientHandle.handle_p = wolaHandle_p;
                            wolaClientHandle.instanceCount = wolaHandle_p->ploArea.instanceCount;
                            memcpy(inClientHandle_p, &wolaClientHandle, sizeof(WolaClientConnectionHandle_t));
                            return GetConn_RC_PooledConnectionOK;
                        }
                    } else {
                        // conn pool is being torn down
                        localCommClose(lcommHandle);
                        freeCellPoolCell(clientRGE_p->wolaAnchor_p->connectionHandleCellPoolID, wolaHandle_p);
                        wolaHandle_p = NULL;
                        ploCC = 1;
                        while (ploCC != 0) {
                            // PLO: compare and swap RGE PLO count, store decremented active connection count
                            connPoolState = clientRGE_p->connPoolState;
                            newConnCount = clientRGE_p->activeConnCount - 1;
                            ploCC = updateActiveConnCount(clientRGE_p, &ploCountOld, newConnCount);
                        }
                        // If conn pool is tearing down and we're the last one out, destroy the RGE
                        if (connPoolState == BBOARGE_CONNPOOL_DESTROYING &&
                            newConnCount == 0) {
                            destroyRGE(clientRGE_p, clientSTCK);
                            return GetConn_RC_DestroyedRGE;
                        }
                        return GetConn_RC_ConnPoolNotReady;
                    }
                }
            }

        } // end create new connection

        else {
            // no free connections and max connection count reached, create a waiter for the next available connection
            WolaConnectionHandleWaiter_t* waiter_p = getCellPoolCell(clientRGE_p->wolaAnchor_p->connectionWaiterCellPoolID);
            if (waiter_p == NULL) {
                return GetConn_RC_CellPoolError;
            }
            memset(waiter_p, 0, sizeof(WolaConnectionHandleWaiter_t));

            memcpy(waiter_p->eye, BBOAHDLW_EYE, sizeof(waiter_p->eye));
            waiter_p->cdsgArea.state = BBOAHDLW_WAITING;
            waiter_p->next_p = NULL;

            unsigned char waiterSTCK[8];
            __stck((unsigned long long*)waiterSTCK);
            memcpy(waiter_p->cdsgArea.stck, waiterSTCK, sizeof(waiter_p->cdsgArea.stck));

            // create a local PetVet for our PETs
            struct PetVet myPetVet;
            memset(&myPetVet, 0, sizeof(myPetVet));
            iea_PEToken waiterPET = {0};
            int petvetRC = pickup(&myPetVet, &waiterPET);

            if (petvetRC != 0) {
                freeCellPoolCell(clientRGE_p->wolaAnchor_p->connectionWaiterCellPoolID, waiter_p);
                return GetConn_RC_PetVetFailure;
            }
            memcpy(waiter_p->pauseElementToken, &waiterPET, sizeof(waiter_p->pauseElementToken));

            // set STIMERM based on waitTime
            MvsTimerID_t timerID = 0;
            if (waitTime > 0) {
                setTimerExitFunc_t* exitFunc = waiterTimeout;
                struct wolaConnectionHandleWaiterExitParms exitParms;
                exitParms.waiter_p = waiter_p;
                memcpy(exitParms.oldSTCK, waiterSTCK, sizeof(exitParms.oldSTCK));
                int timerRC = setTimer(exitFunc, &exitParms, waitTime, &myPetVet, TRUE, &timerID);
                if (timerRC != 0) {
                    // setting the stimer failed, so we'll wait indefinitely. this seems strange, but it's consistent with tWAS
                    timerID = 0;
                }
            }

            // add this waiter to the tail of the queue so it can be found when a connection becomes available
            ploCC = insertIntoWaiterQueue(clientRGE_p, &ploCountOld, waiter_p);
            if (ploCC == 0) {

                // PAUSE
                iea_return_code pauseRC;
                iea_auth_type pauseAuthType = IEA_AUTHORIZED;
                iea_release_code releaseCode;
                unsigned char currentKey = switchToKey0();
                iea4pse(&pauseRC, pauseAuthType, waiter_p->pauseElementToken, waiter_p->pauseElementToken, releaseCode);
                switchToSavedKey(currentKey);

                // when unpaused: cancel timer and return PET
                if (timerID) {
                    int cancelRC = cancelTimer(&timerID);
                }
                board(&myPetVet, waiterPET);

                // if the pause service failed, mark the waiter as failed and we'll clean it up later
                if (pauseRC != 0) {
                    struct wolaConnectionHandleWaiterCDSG oldCDSG;
                    memcpy(oldCDSG.stck, waiter_p->cdsgArea.stck, sizeof(oldCDSG.stck));
                    oldCDSG.state = BBOAHDLW_WAITING;

                    struct wolaConnectionHandleWaiterCDSG newCDSG;
                    memcpy(newCDSG.stck, waiter_p->cdsgArea.stck, sizeof(newCDSG.stck));
                    newCDSG.state = BBOAHDLW_PAUSE_FAILED;

                    __cdsg(&oldCDSG, &(waiter_p->cdsgArea), &newCDSG);

                    return GetConn_RC_WaiterPauseFailed;
                }

                // if pause completed and we have a good connection, use it, and free the waiter struct
                if (memcmp(releaseCode, BBOAHDLW_RELEASE_GOTCONN, sizeof(releaseCode)) == 0 &&
                    memcmp(waiterSTCK, waiter_p->cdsgArea.stck, sizeof(waiterSTCK)) == 0 &&
                    waiter_p->cdsgArea.state == BBOAHDLW_CONN_OK) {

                    memcpy(inClientHandle_p, &(waiter_p->clientConnectionHandle), sizeof(WolaClientConnectionHandle_t));
                    freeCellPoolCell(clientRGE_p->wolaAnchor_p->connectionWaiterCellPoolID, waiter_p);
                    return GetConn_RC_PooledConnectionOK;
                }

                // else if pause completed by timing out or conn pool teardown, just return, waiter struct was already freed
                else if (memcmp(releaseCode, BBOAHDLW_RELEASE_TIMEOUT, sizeof(releaseCode)) == 0) {
                    return GetConn_RC_ExceededMaxConnections;
                }
                else if (memcmp(releaseCode, BBOAHDLW_RELEASE_DESTROY, sizeof(releaseCode)) == 0) {
                    return GetConn_RC_ConnPoolNotReady;
                }
                else if (memcmp(releaseCode, BBOAHDLW_RELEASE_RETRY, sizeof(releaseCode)) == 0){
                    ploCC = 1; // back to the outer loop
                }
            } else {
                // PLO value for the conn pool changed, so there might be a connection available now.
                // Undo the waiter we created and loop back again.
                if (timerID) {
                    int cancelRC = cancelTimer(&timerID);
                }
                board(&myPetVet, waiterPET);
                freeCellPoolCell(clientRGE_p->wolaAnchor_p->connectionWaiterCellPoolID, waiter_p);
            }
        }
    } // end outer loop

    // We shouldn't be here... if we got a good connection we should have returned, and if not we should be looping or waiting
    return GetConn_RC_UnexpectedError;
}

/**
 * Return a WOLA connection to the free queue for its registration.
 *
 * @param clientRGE_p The registration which owns the connection
 * @param inClientHandle_p The client connection handle for the connection to be returned
 * @return 0 if successful, see header for other values
 */
int freePooledConnection(WolaRegistration_t * clientRGE_p, WolaClientConnectionHandle_t * inClientHandle_p) {

    WolaConnectionHandle_t * freeHandle_p = inClientHandle_p->handle_p;
    unsigned long long clientPLOCount = inClientHandle_p->instanceCount;
    unsigned long long handleState;
    unsigned long long rgeSTCK;
    memcpy(&rgeSTCK, &(clientRGE_p->stckLastStateChange), sizeof(rgeSTCK));
    int ploCC = 1;

    // Make sure the connection is ready to be freed
    while (ploCC != 0) {
        ploCC = loadFromHandle(freeHandle_p, &clientPLOCount, &(freeHandle_p->state), &handleState);
        if (ploCC != 0) {
            // Client handle's instance count didn't match the connection's
            return FreeConn_RC_InvalidClientHandle;
        } else if (handleState != BBOAHDL_READY) { //TODO check for transaction on the connection
            // Free was called when connection wasn't ready
            int rc = destroyPooledConnection(clientRGE_p, rgeSTCK, freeHandle_p);
            if (rc == DestroyConn_RC_DestroyedRGE) {
                return FreeConn_RC_DestroyedRGE;
            }
            return FreeConn_RC_ConnectionStateError;
        }
        // Handle is in the correct state, change it to pooled state and proceed if successful
        ploCC = changeConnHandleState(freeHandle_p, &clientPLOCount, BBOAHDL_READY, BBOAHDL_POOLED);
    }

    // changeConnHandleState updates the connection instance count
    // create a client connection handle with the updated instance count.
    WolaClientConnectionHandle_t localWolaClientConnectionHandle;
    localWolaClientConnectionHandle.handle_p = inClientHandle_p->handle_p;
    localWolaClientConnectionHandle.instanceCount = clientPLOCount;
    void* message_p = 0;
    void* contexts_p = 0;
    // if the message or context pointers are still set free them
    if (getMessageAndContextAreas(&localWolaClientConnectionHandle, &message_p, &contexts_p) == 0 ) {
        if (message_p != 0) {
            free(message_p);
        }
        if (contexts_p != 0) {
            free(contexts_p);
        }
    }

    // Return the connection to the free queue, or hand off to a waiter if available
    ploCC = 1;
    unsigned long long ploCountOld;
    while (ploCC != 0) {
        ploCC = loadFromRGE(clientRGE_p, rgeSTCK, &(clientRGE_p->connPoolPLOCounter), &ploCountOld);
        if (ploCC != 0) {
            // RGE STCK has changed, nothing we can do
            return FreeConn_RC_RgeStckChanged;
        }
        ploCC = 1;
        WolaConnectionHandleWaiter_t * firstWaiter_p = clientRGE_p->connWaitListHead_p;

        // If there waiter queue isn't empty, pop the first waiter and give it the freed connection
        if (clientRGE_p->connPoolState == BBOARGE_CONNPOOL_READY &&
            firstWaiter_p != NULL) {
            ploCC = popFromWaiterQueue(clientRGE_p, &ploCountOld, firstWaiter_p);
            if (ploCC == 0) {
                // Release the waiter and give it the connection
                int rc = releaseWaiter(firstWaiter_p, BBOAHDLW_WAITING, BBOAHDLW_CONN_OK, BBOAHDLW_RELEASE_GOTCONN, inClientHandle_p);
                // Waiter will be freed after the released thread reads the connection
                if (rc != 0) {
                    // The waiter wasn't in a waiting state, which means it previously timed out or failed. Clean it up and try again.
                    freeCellPoolCell(clientRGE_p->wolaAnchor_p->connectionWaiterCellPoolID, firstWaiter_p);
                    ploCC = 1;
                }
            }

        } else if (clientRGE_p->connPoolState == BBOARGE_CONNPOOL_READY ||
                   clientRGE_p->connPoolState == BBOARGE_CONNPOOL_QUIESCING){
            // No waiters available, put it on the free chain
            freeHandle_p->nextFreeHandle_p = clientRGE_p->freeConnHandlePoolHead_p;
            ploCC = addConnectionToList(clientRGE_p, &ploCountOld, freeHandle_p, (WolaConnectionHandle_t **)&(clientRGE_p->freeConnHandlePoolHead_p));
            // If ploCC is nonzero, any of the conditions we checked could have changed, so we'll loop back to the beginning
        } else {
            // Conn pool is tearing down, destroy the connection
            int rc = destroyPooledConnection(clientRGE_p, clientRGE_p->stckLastStateChange, freeHandle_p);
            if (rc == DestroyConn_RC_DestroyedRGE) {
                return FreeConn_RC_DestroyedRGE;
            }
            ploCC = 0;
        }
    }
    return FreeConn_RC_FreeConnectionOK;
}

/**
 * Destroys a WOLA connection by removing it from the registration's list of connections and returning its
 * storage to the connection handle cell pool.
 *
 * @param clientRGE_p The registration which owns the connection
 * @param clientSTCK The last-known STCK value for the registration
 * @param destroyHandle_p The connection handle to be destroyed
 * @return 0 if successful, see header for other values
 */
int destroyPooledConnection(WolaRegistration_t * clientRGE_p, unsigned long long clientSTCK, WolaConnectionHandle_t * destroyHandle_p) {

    WolaConnectionHandle_t * prevHandle_p;
    WolaConnectionHandle_t * curHandle_p = clientRGE_p->allConnHandleListHead_p;
    unsigned long long ploCountOld;
    int ploCC = 1;
    int calledRemove = 0;

    while (ploCC != 0 &&
        destroyHandle_p != NULL) {
        // Verify the STCK value while grabbing the PLO counter
        ploCC = loadFromRGE(clientRGE_p, clientSTCK, &(clientRGE_p->connPoolPLOCounter), &ploCountOld);
        if (ploCC != 0) {
            // RGE STCK has changed
            return DestroyConn_RC_RgeStckChanged;
        }

        // Iterate the list of all connections until we find the one to destroy
        prevHandle_p = NULL;
        curHandle_p = clientRGE_p->allConnHandleListHead_p;
        ploCC = 1;
        calledRemove = 0;
        while (calledRemove == 0) {
            if (curHandle_p == NULL) {
                // Handle to destroy was not found in the list of all connections
                return DestroyConn_RC_ConnectionNotOnRGE;
            }
            else if (curHandle_p == destroyHandle_p) {
                calledRemove = 1;
                ploCC = removeConnectionFromList(clientRGE_p, &ploCountOld, curHandle_p, prevHandle_p);
            } else {
                prevHandle_p = curHandle_p;
                curHandle_p = prevHandle_p->nextHandle_p;
            }
        }

        // Out of the loop means we tried to remove something; check the result
        if (ploCC == 0) {
            // Handle is now removed from the list, continue with destroy
            destroyHandle_p->ploArea.instanceCount++;
            // If message buffer exists free it
            void * message_p = destroyHandle_p->cachedMessage_p;
            if (message_p != 0) {
                destroyHandle_p->cachedMessage_p = 0;
                free(message_p);                
            }
            // If context buffer exists free it
            void * contexts_p = destroyHandle_p->cachedContexts_p;
            if (contexts_p != 0) {
                destroyHandle_p->cachedContexts_p = 0;
                free(contexts_p);                
            }
            localCommClose(destroyHandle_p->localCommConnectionHandle);
            freeCellPoolCell(clientRGE_p->wolaAnchor_p->connectionHandleCellPoolID, destroyHandle_p);

            unsigned long long connPoolState, newConnCount;
            ploCC = 1;
            while (ploCC != 0) {
                connPoolState = clientRGE_p->connPoolState;
                newConnCount = clientRGE_p->activeConnCount - 1;
                WolaConnectionHandleWaiter_t * waiter_p = clientRGE_p->connWaitListHead_p;

                // If this was the last connection but the pool is active, we may have waiters that need to be released,
                // since they rely on existing connections to be freed
                if (connPoolState == BBOARGE_CONNPOOL_READY &&
                    newConnCount == 0 &&
                    waiter_p != NULL) {
                    ploCC = popFromWaiterQueue(clientRGE_p, &ploCountOld, waiter_p);
                    if (ploCC == 0) {
                        releaseWaiter(waiter_p, BBOAHDLW_WAITING, BBOAHDLW_RETRY, BBOAHDLW_RELEASE_RETRY, NULL);
                        freeCellPoolCell(clientRGE_p->wolaAnchor_p->connectionWaiterCellPoolID, waiter_p);
                        ploCC = 1;
                    }
                } else {
                    // PLO: compare and swap RGE PLO count, store decremented active connection count
                    ploCC = updateActiveConnCount(clientRGE_p, &ploCountOld, newConnCount);
                }
            }
            // If conn pool is tearing down and we're the last one out, destroy the RGE
            if (connPoolState == BBOARGE_CONNPOOL_DESTROYING &&
                newConnCount == 0) {
                destroyRGE(clientRGE_p, clientSTCK);
                return DestroyConn_RC_DestroyedRGE;
            }
        }
        // If it wasn't successfully removed, PLO counter probably changed, so loop all the way back to check conditions again
    }
    return DestroyConn_RC_DestroyConnectionOK;
}

/**
 * Initiates teardown of the connection pool. Destroys all free connections and all existing waiters.
 *
 * @param clientRGE_p The registration which contains the connection pool
 * @param clientSTCK The last known STCK value for the registration
 * @return 0 if connection pool was fully cleaned up
 */
int cleanupConnectionPool(WolaRegistration_t * clientRGE_p, unsigned long long clientSTCK) {
    //TODO We load some fields from the RGE directly in here. That should generally be okay since only one thread can be in this path
    //     at once, but it's possible to have a race between unregister and unregister force, so we should serialize them.

    // Set the pool state to indicate we're tearing down.
    unsigned long long ploCountOld;
    int ploCC = loadFromRGE(clientRGE_p, clientSTCK, &(clientRGE_p->connPoolPLOCounter), &ploCountOld);
    if (ploCC != 0) {
        return Cleanup_RC_RgeStckChanged;
    }
    ploCC = 1;
    while (ploCC != 0) {
        if (clientRGE_p->connPoolState == BBOARGE_CONNPOOL_READY) {
            ploCC = changeConnPoolState(clientRGE_p, &ploCountOld, BBOARGE_CONNPOOL_QUIESCING);
        } else {
            //return not in correct state?
        }
    }
    // We set the pool state to quiescing, so there'll be no new connections or waiters. Take care of any existing waiters.
    while (clientRGE_p->connWaitListHead_p != NULL) {
        WolaConnectionHandleWaiter_t* waiter_p = clientRGE_p->connWaitListHead_p;
        clientRGE_p->connWaitListHead_p = waiter_p->next_p;
        releaseWaiter(waiter_p, BBOAHDLW_WAITING, BBOAHDLW_DESTROYING, BBOAHDLW_RELEASE_DESTROY, NULL);
        freeCellPoolCell(clientRGE_p->wolaAnchor_p->connectionWaiterCellPoolID, waiter_p);
    }
    // Waiters are gone, we must advance the state to destroying before dealing with free connections.
    WolaConnectionHandle_t * handle_p, * nextHandle_p;
    int connCount;
    ploCountOld = clientRGE_p->connPoolPLOCounter;
    ploCC = 1;
    while (ploCC != 0) {
        handle_p = clientRGE_p->freeConnHandlePoolHead_p;
        connCount = clientRGE_p->activeConnCount;
        ploCC = changeConnPoolState(clientRGE_p, &ploCountOld, BBOARGE_CONNPOOL_DESTROYING);
    }
    // If there are no connections at this point, go ahead and destroy the registration.
    if (handle_p == NULL && connCount == 0) {
        destroyRGE(clientRGE_p, clientSTCK);
        return Cleanup_RC_CleanupOK;
    }
    // If we still have connections, start by destroying any connections on the free chain.
    while (handle_p != NULL) {
        nextHandle_p = handle_p->nextFreeHandle_p;
        int rc = destroyPooledConnection(clientRGE_p, clientSTCK, handle_p);
        if (rc == DestroyConn_RC_DestroyedRGE) {
            return Cleanup_RC_CleanupOK;
        } else {
            handle_p = nextHandle_p;
        }
    }
    // If we destroyed all the free connections and still have connections remaining somewhere, we can't do anything more here.
    // When the active connections come back to be freed, they'll be destroyed, and once the last one comes in we destroy the RGE.
    return Cleanup_RC_ActiveConnsRemaining;
}

/**
 * Forcibly tears down the connection pool if active connections are keeping the pool alive.
 *
 * @param clientRGE_p The registration which contains the connection pool
 * @param clientSTCK The last known STCK value for the registration
 * @return 0 if connection pool was fully cleaned up
 */
int cleanupConnectionPoolForce(WolaRegistration_t * clientRGE_p, unsigned long long clientSTCK) {
    //TODO We load some fields from the RGE directly in here. That should generally be okay since only one thread can be in this path
    //     at once, but it's possible to have a race between unregister and unregister force, so we should serialize them.

    unsigned long long connPoolState;
    int ploCC = loadFromRGE(clientRGE_p, clientSTCK, &(clientRGE_p->connPoolState), &connPoolState);
    if (ploCC == 0 &&
        connPoolState == BBOARGE_CONNPOOL_DESTROYING) {
        // Take connections off the list of all connections and destroy them
        WolaConnectionHandle_t * handle_p;
        ploCC = 1;
        while (ploCC != 0) {
            ploCC = loadFromRGE(clientRGE_p, clientSTCK, (unsigned long long*)&(clientRGE_p->allConnHandleListHead_p), (unsigned long long*)&handle_p);
            if (ploCC == 0 &&
                handle_p != NULL) {
                // Got a connection. Destroy it, then check if there are any left
                int rc = destroyPooledConnection(clientRGE_p, clientSTCK, handle_p);
                if (rc == DestroyConn_RC_DestroyedRGE) {
                    return CleanupForce_RC_CleanupOK;
                } else {
                    // keep looping
                    ploCC = 1;
                }
            }
            // TODO: Infinite loop here if PLO != 0?
        }
    } else {
        // Either the RGE isn't being destroyed, or it was gone before we got there
        return CleanupForce_RC_CleanupErr;
    }
}


/*
 * Internal functions
 */

/**
 * Insert a connection handle as the new head of the specified list.
 *
 * @param RGE_p The registration which contains the connection handle list
 * @param ploCountOld_p The PLO counter of the registration
 * @param newHandle_p The connection handle to be inserted
 * @param listHead_p A pointer to the pointer representing the head of the list
 * @return 0 if successful, nonzero if the PLO compare-and-swap fails
 */
int addConnectionToList(WolaRegistration_t * RGE_p, unsigned long long * ploCountOld_p,
                        WolaConnectionHandle_t * newHandle_p, WolaConnectionHandle_t ** listHead_p) {
    PloCompareAndSwapAreaDoubleWord_t ploCompareArea;
    ploCompareArea.compare_p = &(RGE_p->connPoolPLOCounter);
    ploCompareArea.expectedValue = *ploCountOld_p;
    ploCompareArea.replaceValue = *ploCountOld_p + 1;

    PloStoreAreaDoubleWord_t ploStoreArea;
    ploStoreArea.storeLocation_p = listHead_p;
    ploStoreArea.storeValue = (unsigned long long)newHandle_p;

    int rc = ploCompareAndSwapAndStoreDoubleWord(&(RGE_p->connPoolPLOCounter), &ploCompareArea, &ploStoreArea);
    if (rc != 0) {
        memcpy(ploCountOld_p, &(ploCompareArea.expectedValue), sizeof(unsigned long long));
    } else {
        memcpy(ploCountOld_p, &(ploCompareArea.replaceValue), sizeof(unsigned long long));
    }
    return rc;
}

/**
 * Change the state of the given connection handle using a double compare-and-swap. If the handle is
 * not in the expected state, an error code is returned.
 *
 * @param handle_p The connection handle to modify
 * @param ploCountOld_p The connection handle's PLO counter
 * @param oldState The state the handle is expected to be in
 * @param newState The state the handle is to be changed to
 * @return 0 if successful, nonzero if the PLO compare-and-swap fails
 */
int changeConnHandleState(WolaConnectionHandle_t * handle_p, unsigned long long * ploCountOld_p,
                          unsigned long long oldState, unsigned long long newState) {
    PloCompareAndSwapAreaDoubleWord_t ploCompareCountArea;
    ploCompareCountArea.compare_p = &(handle_p->ploArea);
    ploCompareCountArea.expectedValue = *ploCountOld_p;
    ploCompareCountArea.replaceValue = *ploCountOld_p + 1;

    PloCompareAndSwapAreaDoubleWord_t ploCompareStateArea;
    ploCompareStateArea.compare_p = &(handle_p->state);
    ploCompareStateArea.expectedValue = oldState;
    ploCompareStateArea.replaceValue = newState;

    int rc = ploDoubleCompareAndSwapDoubleWord(&(handle_p->ploArea), &ploCompareCountArea, &ploCompareStateArea);
    if (rc != 0) {
        memcpy(ploCountOld_p, &(ploCompareCountArea.expectedValue), sizeof(unsigned long long));
    } else {
        memcpy(ploCountOld_p, &(ploCompareCountArea.replaceValue), sizeof(unsigned long long));
    }
    return rc;
}

/**
 *
 * @param RGE_p
 * @param ploCountOld_p
 * @param newState
 * @return
 */
int changeConnPoolState(WolaRegistration_t * RGE_p, unsigned long long * ploCountOld_p,
                        unsigned long long newState) {
    PloCompareAndSwapAreaDoubleWord_t ploCompareArea;
    ploCompareArea.compare_p = &(RGE_p->connPoolPLOCounter);
    ploCompareArea.expectedValue = *ploCountOld_p;
    ploCompareArea.replaceValue = *ploCountOld_p + 1;

    PloStoreAreaDoubleWord_t ploStoreArea;
    ploStoreArea.storeLocation_p = &(RGE_p->connPoolState);
    ploStoreArea.storeValue = newState;

    int rc = ploCompareAndSwapAndStoreDoubleWord(&(RGE_p->connPoolPLOCounter), &ploCompareArea, &ploStoreArea);
    if (rc != 0) {
        memcpy(ploCountOld_p, &(ploCompareArea.expectedValue), sizeof(unsigned long long));
    } else {
        memcpy(ploCountOld_p, &(ploCompareArea.replaceValue), sizeof(unsigned long long));
    }
    return rc;
}

/**
 * Destroys a WOLA registration by destroying the name token, freeing the storage, and removing its attachment
 * to the shared memory area.
 *
 * @param RGE_p The registration to destroy
 * @param oldSTCK The last known STCK value for this registration
 */
void destroyRGE(WolaRegistration_t * RGE_p, unsigned long long oldSTCK) {
    WolaSharedMemoryAnchor_t * bboashr_p = RGE_p->wolaAnchor_p;

    // Invalidate the RGE so it can't be used while we're destroying it
    PloCompareAndSwapAreaDoubleWord_t ploCompareArea;
    ploCompareArea.compare_p = &(RGE_p->stckLastStateChange);
    ploCompareArea.expectedValue = oldSTCK;
    __stck(&(ploCompareArea.replaceValue));
    int ploCC = ploCompareAndSwapDoubleWord(&(RGE_p->stckLastStateChange), &ploCompareArea);
    if (ploCC == 0) {
        // No one else can modify the RGE at this point. Continue with the destroy unserialized.
        removeBboargeFromChain(bboashr_p, RGE_p);

        // Delete the RGE name token
        int rc;
        char tokenName[16];
        getRegisterTokenName(tokenName, RGE_p->registrationName);
        iean4dl(IEANT_PRIMARY_LEVEL, tokenName, &rc);

        // Free the storage and unattach from the shared memory area
        freeCellPoolCell(bboashr_p->registationCellPoolID, RGE_p);
        clientDisconnectFromWolaSharedMemoryAnchor(bboashr_p);
    }
}

/**
 * Inserts a waiter element onto the tail of the registration's queue of waiters.
 *
 * @param RGE_p The registration containing the waiter
 * @param ploCountOld_p The expected value of the registration's PLO counter
 * @param waiter_p The waiter to be put on the queue
 * @return 0 if successful, nonzero if the PLO compare-and-swap fails
 */
int insertIntoWaiterQueue(WolaRegistration_t * RGE_p, unsigned long long * ploCountOld_p,
                          WolaConnectionHandleWaiter_t * waiter_p) {
    PloCompareAndSwapAreaDoubleWord_t ploCompareArea;
    ploCompareArea.compare_p = &(RGE_p->connPoolPLOCounter);
    ploCompareArea.expectedValue = *ploCountOld_p;
    ploCompareArea.replaceValue = *ploCountOld_p + 1;

    PloStoreAreaDoubleWord_t ploStoreArea1;
    if (RGE_p->connWaitListTail_p == NULL) {
        // If there are no other elements on the list, put ourselves as both the head and the tail pointers.
        waiter_p->prev_p = NULL;
        ploStoreArea1.storeLocation_p = &(RGE_p->connWaitListHead_p);
    } else {
        // If there is a tail, we set the current tail's next pointer to us and then become the new tail.
        waiter_p->prev_p = RGE_p->connWaitListTail_p;
        ploStoreArea1.storeLocation_p = &(RGE_p->connWaitListTail_p->next_p);
    }
    ploStoreArea1.storeValue = (unsigned long long)waiter_p;

    PloStoreAreaDoubleWord_t ploStoreArea2;
    ploStoreArea2.storeLocation_p = &(RGE_p->connWaitListTail_p);
    ploStoreArea2.storeValue = (unsigned long long)waiter_p;

    int rc = ploCompareAndSwapAndDoubleStoreDoubleWord(&(RGE_p->connPoolPLOCounter), &ploCompareArea, &ploStoreArea1, &ploStoreArea2);
    if (rc != 0) {
        memcpy(ploCountOld_p, &(ploCompareArea.expectedValue), sizeof(unsigned long long));
    } else {
        memcpy(ploCountOld_p, &(ploCompareArea.replaceValue), sizeof(unsigned long long));
    }
    return rc;
}

/**
 * Load a double word field from the input RGE, using PLO compare-and-load to verify the input STCK
 * against the RGE's current STCK.
 *
 * @param RGE_p The registration to load from
 * @param oldSTCK The last known STCK value for this registration
 * @param loadSource_p A pointer to a double word area containing the data to be loaded
 * @param loadDest_p A pointer to a double word area where the data will be copied to
 * @return 0 if successful, nonzero if the PLO compare-and-swap fails
 */
int loadFromRGE(WolaRegistration_t * RGE_p, unsigned long long oldSTCK,
                unsigned long long * loadSource_p, unsigned long long * loadDest_p ) {
    PloCompareAndSwapAreaDoubleWord_t ploCompareArea;
    ploCompareArea.compare_p = &(RGE_p->stckLastStateChange);
    ploCompareArea.expectedValue = oldSTCK;

    PloLoadAreaDoubleWord_t ploLoadArea;
    ploLoadArea.loadLocation_p = loadSource_p;

    int rc = ploCompareAndLoadDoubleWord(&(RGE_p->stckLastStateChange), &ploCompareArea, &ploLoadArea);
    if (rc == 0) {
        memcpy(loadDest_p, &(ploLoadArea.loadValue), sizeof(*loadDest_p));
    }
    return rc;
}

/**
 * Load a double word area from a connection handle, using PLO compare-and-load to verify
 * the handle's current PLO counter.
 *
 * @param handle_p The connection handle to load from
 * @param ploCountOld_p The expected value of the PLO counter
 * @param loadSource_p A pointer to a double word area containing the data to be loaded
 * @param loadDest_p A pointer to a double word area where the data will be copied to
 * @return 0 if successful, nonzero if the PLO compare-and-swap fails
 */
int loadFromHandle(WolaConnectionHandle_t * handle_p, unsigned long long * ploCountOld_p,
                   unsigned long long * loadSource_p, unsigned long long * loadDest_p) {
    PloCompareAndSwapAreaDoubleWord_t ploCompareArea;
    ploCompareArea.compare_p = &(handle_p->ploArea);
    ploCompareArea.expectedValue = *ploCountOld_p;

    PloLoadAreaDoubleWord_t ploLoadArea;
    ploLoadArea.loadLocation_p = loadSource_p;

    int rc = ploCompareAndLoadDoubleWord(&(handle_p->ploArea), &ploCompareArea, &ploLoadArea);
    if (rc == 0) {
        memcpy(loadDest_p, &(ploLoadArea.loadValue), sizeof(*loadDest_p));
    } else {
        memcpy(ploCountOld_p, &(ploCompareArea.expectedValue), sizeof(unsigned long long));
    }
    return rc;
}


/**
 * Remove a connection handle from the free queue so that it can be used as an active connection.
 *
 * @param RGE_p The registration containing the connection
 * @param ploCountOld_p The expected value of the registration's PLO counter
 * @param handleToPop_p The handle to be removed from the free queue
 * @return 0 if successful, nonzero if the PLO compare-and-swap fails
 */
int popFromFreeQueue(WolaRegistration_t * RGE_p, unsigned long long * ploCountOld_p,
                     WolaConnectionHandle_t * handleToPop_p) {
    PloCompareAndSwapAreaDoubleWord_t ploCompareArea;
    ploCompareArea.compare_p = &(RGE_p->connPoolPLOCounter);
    ploCompareArea.expectedValue = *ploCountOld_p;
    ploCompareArea.replaceValue = *ploCountOld_p + 1;

    PloStoreAreaDoubleWord_t ploStoreArea;
    ploStoreArea.storeLocation_p = &(RGE_p->freeConnHandlePoolHead_p);
    ploStoreArea.storeValue = (unsigned long long)handleToPop_p->nextFreeHandle_p;

    int rc = ploCompareAndSwapAndStoreDoubleWord(&(RGE_p->connPoolPLOCounter), &ploCompareArea, &ploStoreArea);
    if (rc != 0) {
        memcpy(ploCountOld_p, &(ploCompareArea.expectedValue), sizeof(unsigned long long));
    } else {
        memcpy(ploCountOld_p, &(ploCompareArea.replaceValue), sizeof(unsigned long long));
    }
    return rc;
}

/**
 * Pop a connection handle waiter element from the registration's queue of waiters.
 *
 * @param RGE_p The registration containing this waiter
 * @param ploCountOld_p The expected value of the registration's PLO counter
 * @param waiter_p Pointer to the head of the waiter queue
 * @return 0 if successful, nonzero if the PLO compare-and-swap fails
 */
int popFromWaiterQueue(WolaRegistration_t * RGE_p, unsigned long long * ploCountOld_p,
                       WolaConnectionHandleWaiter_t * waiter_p) {
    PloCompareAndSwapAreaDoubleWord_t ploCompareArea;
    ploCompareArea.compare_p = &(RGE_p->connPoolPLOCounter);
    ploCompareArea.expectedValue = *ploCountOld_p;
    ploCompareArea.replaceValue = *ploCountOld_p + 1;

    WolaConnectionHandleWaiter_t * nextWaiter_p = waiter_p->next_p;

    PloStoreAreaDoubleWord_t ploStoreArea1;
    ploStoreArea1.storeLocation_p = &(RGE_p->connWaitListHead_p);
    ploStoreArea1.storeValue = (unsigned long long)nextWaiter_p;

    PloStoreAreaDoubleWord_t ploStoreArea2;
    if (nextWaiter_p == 0) {
        ploStoreArea2.storeLocation_p = &(RGE_p->connWaitListTail_p);
    } else {
        ploStoreArea2.storeLocation_p = &(nextWaiter_p->prev_p);
    }
    ploStoreArea2.storeValue = (unsigned long long)NULL;

    int rc = ploCompareAndSwapAndDoubleStoreDoubleWord(&(RGE_p->connPoolPLOCounter), &ploCompareArea, &ploStoreArea1, &ploStoreArea2);
    if (rc != 0) {
        memcpy(ploCountOld_p, &(ploCompareArea.expectedValue), sizeof(unsigned long long));
    } else {
        memcpy(ploCountOld_p, &(ploCompareArea.replaceValue), sizeof(unsigned long long));
    }
    return rc;
}

/**
 * Release a paused waiter element after changing its state.
 *
 * @param waiter_p The waiter to be released
 * @param oldState The state we expect the waiter to be in
 * @param newState The state to release the waiter with
 * @param releaseCode The release code indicating why the waiter is being released
 * @param clientHandle_p The client connection handle for the waiter's thread to use (if newState = BBOAHDLW_CONN_OK)
 * @return 0 if successful, nonzero if CDSG fails
 */
int releaseWaiter(WolaConnectionHandleWaiter_t * waiter_p, unsigned long long oldState, unsigned long long newState,
                  char * releaseCode, WolaClientConnectionHandle_t * clientHandle_p) {
    struct wolaConnectionHandleWaiterCDSG oldCDSG;
    memcpy(oldCDSG.stck, waiter_p->cdsgArea.stck, sizeof(oldCDSG.stck));
    oldCDSG.state = oldState;

    struct wolaConnectionHandleWaiterCDSG newCDSG;
    memcpy(newCDSG.stck, oldCDSG.stck, sizeof(newCDSG.stck));
    newCDSG.state = newState;

    int cdsgCC = __cdsg(&oldCDSG, &(waiter_p->cdsgArea), &newCDSG);

    if (cdsgCC == 0) {
        // State change succeeded, so we know the waiter is good. Give it the connection (if applicable) and release it.
        if (newState == BBOAHDLW_CONN_OK &&
            clientHandle_p != NULL) {
            memcpy(&(waiter_p->clientConnectionHandle), clientHandle_p, sizeof(waiter_p->clientConnectionHandle));
            waiter_p->clientConnectionHandle.instanceCount = waiter_p->clientConnectionHandle.handle_p->ploArea.instanceCount;
        }

        iea_return_code releaseRc;
        iea_auth_type authType = IEA_AUTHORIZED;

        unsigned char currentKey = switchToKey0();
        iea4rls(&releaseRc, authType, waiter_p->pauseElementToken, releaseCode);
        switchToSavedKey(currentKey);
    }
    return cdsgCC;
}

/**
 * Remove the specified connection handle from the list of all connections.
 *
 * @param RGE_p The registration containing this connection
 * @param ploCountOld_p The expected value of the registration's PLO counter
 * @param removeHandle_p The handle to be removed
 * @param prevHandle_p The previous handle in the list of all connections, or NULL
 *                     if the handle being removed is the head of the list
 * @return 0 if successful, nonzero if the PLO compare-and-swap fails
 */
int removeConnectionFromList(WolaRegistration_t * RGE_p, unsigned long long * ploCountOld_p,
                             WolaConnectionHandle_t * removeHandle_p, WolaConnectionHandle_t * prevHandle_p) {
    PloCompareAndSwapAreaDoubleWord_t ploCompareArea;
    ploCompareArea.compare_p = &(RGE_p->connPoolPLOCounter);
    ploCompareArea.expectedValue = *ploCountOld_p;
    ploCompareArea.replaceValue = *ploCountOld_p + 1;

    PloStoreAreaDoubleWord_t ploStoreArea;
    if (prevHandle_p == NULL) {
        ploStoreArea.storeLocation_p = &(RGE_p->allConnHandleListHead_p);
    } else {
        ploStoreArea.storeLocation_p = &(prevHandle_p->nextHandle_p);
    }
    ploStoreArea.storeValue = (unsigned long long)removeHandle_p->nextHandle_p;

    int rc = ploCompareAndSwapAndStoreDoubleWord(&(RGE_p->connPoolPLOCounter), &ploCompareArea, &ploStoreArea);
    if (rc != 0) {
        memcpy(ploCountOld_p, &(ploCompareArea.expectedValue), sizeof(unsigned long long));
    } else {
        memcpy(ploCountOld_p, &(ploCompareArea.replaceValue), sizeof(unsigned long long));
    }
    return rc;
}



/**
 * Change the specified registration's active connection count to the new value given.
 *
 * @param RGE_p The registration to be modified
 * @param ploCountOld_p The expected value of the registration's PLO counter
 * @param newCount The new value of the registration's activeConnCount
 * @return 0 if successful, nonzero if the PLO compare-and-swap fails
 */
int updateActiveConnCount(WolaRegistration_t * RGE_p, unsigned long long * ploCountOld_p,
                          unsigned long long newCount) {
    PloCompareAndSwapAreaDoubleWord_t ploCompareArea;
    ploCompareArea.compare_p = &(RGE_p->connPoolPLOCounter);
    ploCompareArea.expectedValue = *ploCountOld_p;
    ploCompareArea.replaceValue = *ploCountOld_p + 1;

    PloStoreAreaDoubleWord_t ploStoreArea;
    ploStoreArea.storeLocation_p = &(RGE_p->activeConnCount);
    ploStoreArea.storeValue = newCount;

    int rc = ploCompareAndSwapAndStoreDoubleWord(&(RGE_p->connPoolPLOCounter), &ploCompareArea, &ploStoreArea);
    if (rc != 0) {
        memcpy(ploCountOld_p, &(ploCompareArea.expectedValue), sizeof(unsigned long long));
    } else {
        memcpy(ploCountOld_p, &(ploCompareArea.replaceValue), sizeof(unsigned long long));
    }
    return rc;
}

/**
 * Method called by STIMERM service when a connection handle waiter times out.
 *
 * @param inParm_p
 */
void waiterTimeout(void* inParm_p) {
    WolaConnectionHandleWaiterExitParms_t* exitParms_p = (WolaConnectionHandleWaiterExitParms_t*)inParm_p;

    // Mark the waiter as timed out.
    struct wolaConnectionHandleWaiterCDSG oldCDSG;
    memcpy(oldCDSG.stck, exitParms_p->oldSTCK, sizeof(oldCDSG.stck));
    oldCDSG.state = BBOAHDLW_WAITING;

    struct wolaConnectionHandleWaiterCDSG newCDSG;
    memcpy(newCDSG.stck, exitParms_p->oldSTCK, sizeof(newCDSG.stck));
    newCDSG.state = BBOAHDLW_TIMED_OUT;

    int rc = __cdsg(&oldCDSG, &(exitParms_p->waiter_p->cdsgArea), &newCDSG);

    // If the CDSG succeeded, we marked the waiter as timed out and can now release it.
    // If it failed, we assume someone else is handling the waiter and do nothing.
    if (rc == 0) {
        // resume using PET, release code = timed out
        iea_return_code releaseRc;
        iea_auth_type authType = IEA_AUTHORIZED;

        unsigned char currentKey = switchToKey0();
        iea4rls(&releaseRc, authType, exitParms_p->waiter_p->pauseElementToken, BBOAHDLW_RELEASE_TIMEOUT);
        switchToSavedKey(currentKey);
    }
}

