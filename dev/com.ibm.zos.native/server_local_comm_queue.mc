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

/**
 * @file
 *
 * Assorted routines that interface with z/OS Local Communication Queuing
 * structures.
 *
 */
#include <ctype.h>
#include <ieac.h>
#include <metal.h>
#include <stdlib.h>
#include <stdio.h>
#include <string.h>

#include "include/common_defines.h"
#include "include/common_mc_defines.h"

#include "include/gen/ihapsa.h"
#include "include/gen/ihaascb.h"
#include "include/gen/ihaassb.h"

#include "include/mvs_abend.h"
#include "include/mvs_cell_pool_services.h"
#include "include/mvs_iarv64.h"
#include "include/mvs_plo.h"
#include "include/mvs_stimerm.h"
#include "include/server_local_comm_client.h"
#include "include/server_local_comm_footprint.h"
#include "include/server_local_comm_global_lock.h"
#include "include/server_local_comm_queue.h"
#include "include/server_local_comm_stimer.h"
#include "include/server_process_data.h"
#include "include/stack_services.h"
#include "include/ras_tracing.h"


#define RAS_MODULE_CONST  RAS_MODULE_SERVER_LCOM_QUEUE

#define TP_SERVER_LCOM_QUEUE_GETWRQES_ENTRY             1
#define TP_SERVER_LCOM_QUEUE_GETWRQES_EXIT              2
#define TP_SERVER_LCOM_QUEUE_FREE_WRQE                  5
#define TP_SERVER_LCOM_QUEUE_RELWRQ_PET_ENTRY           6
#define TP_SERVER_LCOM_QUEUE_RELWRQ_PET_EXIT            7
#define TP_SERVER_LCOM_QUEUE_INITWRQ_EXIT               8
#define TP_SERVER_LCOM_QUEUE_ADDTOWQUE_ENTRY            9
#define TP_SERVER_LCOM_QUEUE_ADDTOWQUE_PUSHRC          10
#define TP_SERVER_LCOM_QUEUE_ADDTOWQUE_RELPET          11
#define TP_SERVER_LCOM_QUEUE_ADDTOWQUE_EXIT            12
#define TP_SERVER_LCOM_QUEUE_FREE_DRQE                 13
#define TP_SERVER_LCOM_QUEUE_ADDTODQUE_ENTRY           14
#define TP_SERVER_LCOM_QUEUE_ADDTODQUE_PUSHRC          15
#define TP_SERVER_LCOM_QUEUE_ADDTODQUE_FAILED_RR       16
#define TP_SERVER_LCOM_QUEUE_ADDTODQUE_EXIT            17
#define TP_SERVER_LCOM_QUEUE_GETWRQE_EXIT              18
#define TP_SERVER_LCOM_QUEUE_FREEWRQE_ENTRY            19
#define TP_SERVER_LCOM_QUEUE_GETWRQE_GETSTOR           20
#define TP_SERVER_LCOM_QUEUE_GETWRQE_GROWING           21
#define TP_SERVER_LCOM_QUEUE_FREELREAD_ENTRY           22
#define TP_SERVER_LCOM_QUEUE_FREELREAD_EXIT            23
#define TP_SERVER_LCOM_QUEUE_DATAAVAIL_ENTRY           24
#define TP_SERVER_LCOM_QUEUE_DATAAVAIL_EXIT            25
#define TP_SERVER_LCOM_QUEUE_GETWRQES_NOPET            26
#define TP_SERVER_LCOM_QUEUE_INITIATE_SERVER_CLOSE     27
#define TP_SERVER_LCOM_QUEUE_ISSUERR_FORCE_ASYNC       28
#define TP_SERVER_LCOM_QUEUE_INIT_WRQ_FLAGS_ENTRY      29
#define TP_SERVER_LCOM_QUEUE_INIT_WRQ_FLAGS_EXIT       30

// Forward declares
int addToDataQueue(LocalCommDirectionalQueue* sendQ_p, LocalCommDataQueueElement* dataQE_p);
int addToWorkQueue(LocalCommDirectionalQueue* targetDirectionalQ_p, LocalCommWorkQueueElement* work_p);
LocalCommWorkQueueElement * buildConnectRequest(LocalCommClientConnectionHandle_t* connHandle_p);
LocalCommWorkQueueElement * buildReadReadyRequest(LocalCommDirectionalQueue* queue_p);
LocalCommDataQueueElement * buildSendRequest(LocalCommConnectionHandle_t* connHandle_p);
int cleanupWorkQueueElement(LocalCommWorkQueue* workQueue_p, LocalCommWorkQueueElement* inWRQE_p);
int initializeDataQueue(LocalCommDataQueue* dataQueue_p, LocalCommClientAnchor_t* LOCL_p, int inboundQueue);
void freeLComWorkQueueElement(LocalCommWorkQueue* workQueue_p, LocalCommWorkQueueElement* inWRQE_p);
LocalCommWorkQueueElement* getLComWorkQueueElement(LocalCommWorkQueue* workQueue_p);
LocalCommCommonQueueElement* getLComCommonQueueElement(LocalCommClientAnchor_t* LOCL_p);
void freeLComDataQueueElement(LocalCommDataQueue* dataQueue_p, LocalCommDataQueueElement* inDRQE_p);
int cleanupDataQueueElement(LocalCommDataQueue* dataQueue_p, LocalCommDataQueueElement* inDRQE_p);
unsigned long long dataAvailableOnDataQueue(LocalCommDirectionalQueue* readQ_p, unsigned char setReadPending);
int cleanupWorkQueueElement(LocalCommWorkQueue* workQueue_p, LocalCommWorkQueueElement* inWRQE_p);
int buildAndQueueCloseRequest(LocalCommConnectionHandle_t * localConnHandle_p);
void waitOnWorkQueueTimeoutRtn(void* timeoutParms_p);
int timedWaitOnWorkQueue(LocalCommDirectionalQueue* readQ_p, int timeToWait);

// We use this if/when our task-level RESMGR, serverAuthorizedTaskCleanup, drives our
// registered hardFailureRegisteredCleanupRtn routine.  This is done during server startup code
// when a Java thread is started and marked it (std_p->taskFlags.cleanupForHardFailure).
// This call disables trace on the calling thread to avoid hangs in tracing code
// during a hard failure.
extern int disableTraceLevelInServerTaskData(void);

/**
 * Retrieve Work Request Queue Element(s) anchored in the BBGZLOCL or BBGZLHDL
 *
 * Either get the entire list of available queue elements or a single request.
 * Caller will be paused waiting on work if none is available.
 *
 * An optional timeToWait value and time out routine indicate if the wait is timed.
 * If so, a stimer is started to drive the passed time out routine (timeoutRtn_p)
 * if no work has appeared for the specified wait time (timeToWait).
 *
 * @param workQueue_p Pointer to the Local Work queue to obtain requests.
 * @param outWRQE_p Pointer to a list of Work requests or a single work request based on
 * the setting of the singleRequestDesired parameter or NULL.
 * @param timeToWait Time, in seconds, to wait for a WRQE to arrive.  A value of 0 indicates
 * an untimed wait.
 * @param timeoutRtn_p Timeout routine to drive under a stimer after timeToWait seconds.
 * @param timeoutRtnParms_p Pointer to parameters to pass to the timeoutRtn_p when invoked.
 * @param singleRequestDesired Indication that only a single request is desired.
 * @param otherWorkToDo Nonzero if the caller has other things to do and we should skip the pause.
 * @return LCOM_WRQ_WAITONWORK_RC_OK if successful (outWRQE_p should be non-null),
 * LCOM_WRQ_WAITONWORK_RC_TIMEDOUT if we timed out waiting for work to arrive,
 * LCOM_WRQ_WAITONWORK_RC_BADSTATE if the connection is closing,
 * LCOM_WRQ_WAITONWORK_RC_PAUSEFAILED if pause for work failed,
 * LCOM_WRQ_WAITONWORK_RC_PAUSEFAILED2 if pause for stimer cancel failed,
 * LCOM_WRQ_WAITONWORK_RC_STOPLIS if the channel is stopping,
 * LCOM_WRQ_WAITONWORK_RC_UNKWN_REL if the pause release code was unknown,
 * LCOM_WRQ_WAITONWORK_RC_SKIP_PAUSE if we skipped the pause because the caller told us to,
 * LCOM_WRQ_WAITONWORK_RC_SERVERHARD_FAILURE if the server is terminating from a hard failure.
 *
 */
int waitOnWork(LocalCommWorkQueue* workQueue_p, LocalCommWorkQueueElement** outWRQE_p, int timeToWait, void* timeoutRtn_p, LocalCommStimerParms_t* timeoutRtnParms_p, int singleRequestDesired, long long otherWorkToDo) {

    int localRC;

    LocalCommWorkQueueElement * newWRQEs = NULL;

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(TP_SERVER_LCOM_QUEUE_GETWRQES_ENTRY),
                    "waitOnWork, entry",
                    TRACE_DATA_PTR(outWRQE_p, "outWRQE_p"),
                    TRACE_DATA_INT(timeToWait, "timeToWait"),
                    TRACE_DATA_END_PARMS);
    }

    PloCompareAndSwapAreaQuadWord_t swapArea;
    PloStoreAreaQuadWord_t          storeArea1;  // The Queue
    PloStoreAreaQuadWord_t          storeArea2;  // The PET
    int                             ploRC;

    LocalComWQE_PLO_CS_Area_t       localReplacePLO;
    LocalCommWRQ_PLO_Area_t*        newWRQ_p = (LocalCommWRQ_PLO_Area_t*) &(storeArea1.storeValue);

    char                            nullPET[16] = {{0}};
    char                            localPET[16];
    char                            newPET[16];

    swapArea.compare_p     = workQueue_p->wrqPLO_CS_p;
    memcpy(&(swapArea.expectedValue), swapArea.compare_p, sizeof(swapArea.expectedValue));

    // Setup store area 1 & 2 information
    storeArea1.storeLocation_p = &(workQueue_p->wrqPLO_Area);
    storeArea2.storeLocation_p = &(workQueue_p->wrq_pet);

    do {
        localRC  = LCOM_WRQ_WAITONWORK_RC_OK;
        newWRQEs = (LocalCommWorkQueueElement *) NULL;

        // Build new SwapArea
        memcpy(&localReplacePLO, &(swapArea.expectedValue), sizeof(localReplacePLO));
        localReplacePLO.ploSequenceNumber+=1;
        memcpy(&(swapArea.replaceValue), &localReplacePLO, sizeof(swapArea.replaceValue));

        // Grab current queue/stack info for new StoreArea1
        //*newWRQ_p = localWorkQueue_p->wrqPLO_Area;
        memcpy(newWRQ_p, storeArea1.storeLocation_p, sizeof(storeArea1.storeValue));

        // Check state of queue
        if (newWRQ_p->flags.closingWorkQueue) {
            localRC = LCOM_WRQ_WAITONWORK_RC_BADSTATE;
            break;
        }

        // If the queue is empty, wait for work
        if (newWRQ_p->wrqHead_p == NULL) {
            if (otherWorkToDo != 0L) {
                localRC = LCOM_WRQ_WAITONWORK_RC_SKIP_PAUSE;
                break;
            }

            // Use a current PET or get a new one
            memcpy(localPET, workQueue_p->wrq_pet, sizeof(localPET));
            if (memcmp(localPET, nullPET, sizeof(localPET)) == 0) {
                // Get new pet
                int pickupRC = pickup(workQueue_p->LOCL_p->spca_p, &newPET);

                if (pickupRC != 0) {
                    long long longRC = pickupRC;
                    abend_with_data(ABEND_TYPE_SERVER,
                                    KRSN_SERVER_LCOM_QUEUE_NO_PET,
                                    workQueue_p->LOCL_p->spca_p,
                                    (void*)&longRC);
                }

                memcpy(localPET, newPET, sizeof(localPET));
            }

            // Make sure the PET we are pausing on is available to be released.
            memcpy(&(storeArea2.storeValue), localPET, sizeof(storeArea2.storeValue));

            // Attempt to make the work queue updates (updating the PET or just storing existing back)
            ploRC = ploCompareAndSwapAndStoreQuadWord(swapArea.compare_p, &swapArea, &storeArea2);
            if (ploRC == 0) {
                // Start a stimer unless directed to wait forever.
                MvsTimerID_t stimerID = 0;

                if (timeToWait) {
                    // Start stimerm for caller supplied timeout routine and parms.  We turn off tracing
                    // in the STIMERM exit - you won't get any.  The server does not use the STIMERM exit,
                    // and the client does not support any tracing anyway.
                    // ------------------------------------------------------------------------------------

                    // Finish building parms to pass through to local comm's timer routine(driven from STIMERM exit)
                    memcpy(timeoutRtnParms_p->workQueuePet, localPET,sizeof(timeoutRtnParms_p->workQueuePet));
                    timeoutRtnParms_p->bbgzlocl_p   = workQueue_p->LOCL_p;

                    int stimerRC = setTimer((setTimerExitFunc_t*)timeoutRtn_p, timeoutRtnParms_p, timeToWait, workQueue_p->LOCL_p->spca_p, TRUE, &stimerID);

                    if (stimerRC != 0) {
                        localRC = LCOM_WRQ_WAITONWORK_RC_SETTIMER_FAILED;
                        break; // leave loop
                    }
                }

                // Wait for work or timeout
                iea_return_code pauseRc;
                iea_auth_type pauseAuthType = IEA_AUTHORIZED;
                iea_release_code releaseCode;
                unsigned char currentKey = switchToKey0();
                iea4pse(&pauseRc, pauseAuthType, localPET, localPET, releaseCode);
                switchToSavedKey(currentKey);

                // Drive Cancel of Stimer if we started one (doesn't matter if it popped--still need to drive cancel to cleanup).
                if (stimerID) {
                    int cancelRC = cancelTimer(&stimerID);
                }

                // Check if we made it to the paused service (ie. didn't fail the pause call itself)
                if (pauseRc != 0) {
                    localRC = LCOM_WRQ_WAITONWORK_RC_PAUSEFAILED;
                    break; // leave loop
                } else {
                    server_process_data* spd_p = getServerProcessData();
                    if (spd_p->serializedFlags.serverHardFailureDetected) {
                        // Disable native trace on this thread...its hopefully going to end when it gets back to an LE
                        // Environment
                        disableTraceLevelInServerTaskData();
                    }

                    // Take new PET to the vet
                    board(workQueue_p->LOCL_p->spca_p, localPET);

                    // Check the reason we woke up
                    if ((memcmp(releaseCode, LCOM_WRQ_PET_CHECK_QUEUE, sizeof(releaseCode)) == 0) ||
                        (memcmp(releaseCode, LCOM_WRQ_PET_CLOSING_INBOUND, sizeof(releaseCode)) == 0) ) {
                        // For LCOM_WRQ_PET_CHECK_QUEUE and LCOM_WRQ_PET_CLOSING_INBOUND we'll loop around to
                        // either remove the new WRQE or see the "closing" flag and exit.

                        // Loop around to check for work
                        ploRC = 4;
                    }
                    else if(memcmp(releaseCode, LCOM_WRQ_PET_TIMEDOUT, sizeof(releaseCode)) == 0) {
                        // TIMED OUT...Set RC and bail loop.
                        localRC = LCOM_WRQ_WAITONWORK_RC_TIMEDOUT;
                        break;
                    } else if (memcmp(releaseCode, LCOM_WRQ_PET_STOP_LISTENER, sizeof(releaseCode)) == 0) {
                        if (spd_p->serializedFlags.serverHardFailureDetected) {
                            // Server took hard failure
                            localRC = LCOM_WRQ_WAITONWORK_RC_SERVERHARD_FAILURE;
                        } else {
                            // STOP listening
                            localRC = LCOM_WRQ_WAITONWORK_RC_STOPLIS;
                        }
                        break;
                    } else {
                        // Unknown reason...bail
                        localRC = LCOM_WRQ_WAITONWORK_RC_UNKWN_REL;
                        break;
                    }
                }   // end if, pause OK
            } else {
                // Release the new PET if we obtained one, then loop around to try again.
                if (memcmp(newPET, nullPET, sizeof(newPET)) != 0) {
                    board(workQueue_p->LOCL_p->spca_p, newPET);
                }
            }
        } else {
            // Remove first element if caller is requesting a "single" element
            if (singleRequestDesired == LCOM_WRQ_WAITONWORK_SINGLEREQUEST) {
                newWRQEs = (LocalCommWorkQueueElement*) newWRQ_p->wrqHead_p;
                newWRQ_p->wrqHead_p = newWRQEs->stackElement_header.element_prev_p;
                newWRQ_p->wrqElementCount--;
            } else {
                // Grab entire stack
                newWRQEs = (LocalCommWorkQueueElement*) newWRQ_p->wrqHead_p;
                newWRQ_p->wrqHead_p = NULL;
                newWRQ_p->wrqElementCount = 0;
            }

            // Attempt to make the work queue
            ploRC = ploCompareAndSwapAndStoreQuadWord(swapArea.compare_p, &swapArea, &storeArea1);
        }
    } while (ploRC);  // end do while, no work


    if (newWRQEs != NULL) {

        if (singleRequestDesired == LCOM_WRQ_WAITONWORK_SINGLEREQUEST) {
            clearPtrs(&(newWRQEs->stackElement_header));
        }

        // Anchor retrieved list in Work Queue to free later if a multi-request.
        if (workQueue_p->currentWRQE_List == NULL) {
            if (singleRequestDesired == LCOM_WRQ_WAITONWORK_MULTREQUEST) {
                workQueue_p->currentWRQE_List = newWRQEs;
            }
        } else {
            // Error!!!! Should NOT be a list of processed requests that are not cleaned up.
            abend_with_data(ABEND_TYPE_SERVER,
                            KRSN_SERVER_LCOM_QUEUE_EXISTING_CL,
                            workQueue_p->currentWRQE_List,
                            newWRQEs);
        }
    }

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(TP_SERVER_LCOM_QUEUE_GETWRQES_EXIT),
                    "waitOnWork, exit",
                    TRACE_DATA_RAWDATA(((newWRQEs != NULL) ? sizeof(*newWRQEs): 0),
                                       newWRQEs,
                                       "newWRQEs"),
                    TRACE_DATA_END_PARMS);
    }

    *outWRQE_p = newWRQEs;

    return localRC;
}

/**
 * Time out routine,  driven from STIMER Exit, to cover waiting for a WRQE to Arrive.
 *
 * @param timeoutParms_p Pointer to parameter data for time out routine.
 */
static void waitOnWorkQueueTimeoutRtn(void* inParms_p) {
    LocalCommStimerParms_t* timeoutParms_p = (LocalCommStimerParms_t*) inParms_p;
    LocalCommClientConnectionHandle_t * connectionHandleToken_p = (LocalCommClientConnectionHandle_t *)&(timeoutParms_p->workQConnectionHandleToken);
    int statusFlags = 0;
    const int CONNECTION_VALIDATED    = 0x00000001;
    const int SAME_WRQ_PET            = 0x00000002;
    const int DIFFERENT_WRQ_PET       = 0x00000004;
    const int CONNECTION_IS_INVALID   = 0x00000008;

    // Make sure that connection is still the same one.
    if (validateClientConnectionHandle(connectionHandleToken_p) == 0) {
        statusFlags += CONNECTION_VALIDATED;

        // Make sure that the PET that we were waiting on for a WRQE to arrive is still the current PET in the queue.
        PloCompareAndSwapAreaQuadWord_t swapArea;
        PloStoreAreaQuadWord_t          storeArea1;  // The work queue PET
        int                             ploRC;
        LocalComWQE_PLO_CS_Area_t       localReplacePLO;

        char                            localPET[16];

        // Target work queue is the inbound work queue (client work queue)
        LocalCommConnectionHandle_t* connHandle_p = connectionHandleToken_p->handle_p;
        LocalCommWorkQueue* workQueue_p = connHandle_p->clientInboundQ.workQueue_p;

        swapArea.compare_p     = workQueue_p->wrqPLO_CS_p;
        memcpy(&(swapArea.expectedValue), swapArea.compare_p, sizeof(swapArea.expectedValue));

        // Setup store area 1 information
        storeArea1.storeLocation_p = &(workQueue_p->wrq_pet);
        memset(&(storeArea1.storeValue), 0, sizeof(storeArea1.storeValue));

        do {
            // Build new SwapArea
            memcpy(&localReplacePLO, &(swapArea.expectedValue), sizeof(localReplacePLO));
            localReplacePLO.ploSequenceNumber+=1;
            memcpy(&(swapArea.replaceValue), &localReplacePLO, sizeof(swapArea.replaceValue));

            // Grab current PET
            memcpy(localPET, workQueue_p->wrq_pet, sizeof(localPET));

            // Is it still the same PET?
            if (memcmp(timeoutParms_p->workQueuePet, localPET, sizeof(timeoutParms_p->workQueuePet)) == 0) {

                // Attempt to make the work queue updates (zeroing the current PET and making sure we got a consistent view of the data)
                ploRC = ploCompareAndSwapAndStoreQuadWord(swapArea.compare_p, &swapArea, &storeArea1);
                if (ploRC == 0) {
                    statusFlags += SAME_WRQ_PET;

                    // Release the WRQ PET.
                    iea_return_code releaseRc;
                    iea_auth_type authType = IEA_AUTHORIZED;
                    unsigned char currentKey = switchToKey0();
                    iea4rls(&releaseRc, authType, localPET, (char *) LCOM_WRQ_PET_TIMEDOUT);
                    switchToSavedKey(currentKey);
                }
            } else {
                // WRQ PET has changed.  Work has arrived in time or closing/shutting down.
                statusFlags += DIFFERENT_WRQ_PET;
                break;
            }
        } while (ploRC);

    } else {
        // Timer exit driven on "old" connection.
        statusFlags += CONNECTION_IS_INVALID;
    }


    // Footprint to LOCL
    createLocalCommFPEntry_WaitForDataTO_Rtn(((CF_FootprintTable*)((LocalCommClientAnchor_t*)timeoutParms_p->bbgzlocl_p)->footprintTable),
                                             timeoutParms_p,
                                             statusFlags);

    return;
}

#define LCOM_TIMEDWAITONWORKQ_RC_DATA_ARRIVED     0
#define LCOM_TIMEDWAITONWORKQ_RC_CLOSERECEIVED    8
#define LCOM_TIMEDWAITONWORKQ_TIMEDOUT           12
#define LCOM_TIMEDWAITONWORKQ_UNKNOWN_WRQE       16
#define LCOM_TIMEDWAITONWORKQ_NO_WRQE            20
#define LCOM_TIMEDWAITONWORKQ_BADQSTATE          24
#define LCOM_TIMEDWAITONWORKQ_UNKN_WOW_RC        28
#define LCOM_TIMEDWAITONWORKQ_CONRESP_OK         32
#define LCOM_TIMEDWAITONWORKQ_CONRESP_FAIL       36
/**
 * Wait on the work queue.
 *
 * @param readQ_p Pointer a LocalCommDirectionQueue containing the work queue to wait on.
 * @param timeToWait Time, in seconds, to wait for a request to arrive.
 * @return LCOM_TIMEDWAITONWORKQ_RC_DATA_ARRIVED if notified that data is available,
 * LCOM_TIMEDWAITONWORKQ_RC_CLOSERECEIVED if notified that the connection is closing,
 * LCOM_TIMEDWAITONWORKQ_TIMEDOUT if notified that timed expired while waiting for a WRQE to arrive,
 * LCOM_TIMEDWAITONWORKQ_UNKNOWN_WRQE if notified with an unexpected WRQE,
 * LCOM_TIMEDWAITONWORKQ_NO_WRQE if notified without any WRQE,
 * LCOM_TIMEDWAITONWORKQ_BADQSTATE if we detected a incorrect state of the connection prior to waiting,
 * LCOM_TIMEDWAITONWORKQ_UNKN_WOW_RC if we received an unknown return code from the common waitOnWork routine.
 * LCOM_TIMEDWAITONWORKQ_CONRESP_OK if we received a connect response with a 0 rc.
 *
 */
static int timedWaitOnWorkQueue(LocalCommDirectionalQueue* readQ_p, int timeToWait) {
    int localRC;
    LocalCommStimerParms_t stimerParms = {{0}};
    LocalCommClientConnectionHandle_t* targetConnectionHandleToken_p = (LocalCommClientConnectionHandle_t*) &stimerParms.workQConnectionHandleToken;

    // Build Connection Handle token for stimer exit to use to validate that connection is still good prior
    // to acting on it.
    __stck(&(stimerParms.startTime));
    targetConnectionHandleToken_p->handle_p = (LocalCommConnectionHandle_t*) readQ_p->bbgzlhdl_p;
    targetConnectionHandleToken_p->instanceCount = targetConnectionHandleToken_p->handle_p->instanceCount;

    // Wait for a wrqe to arrive
    LocalCommWorkQueueElement* result = NULL;
    int waitRC = waitOnWork(readQ_p->workQueue_p,
                            &result,
                            timeToWait,
                            (void*)&waitOnWorkQueueTimeoutRtn,
                            &stimerParms,
                            LCOM_WRQ_WAITONWORK_SINGLEREQUEST,
                            0L); /* Always wait */

    switch(waitRC) {
        case LCOM_WRQ_WAITONWORK_RC_OK: {
            if (result != NULL) {
                // Extract needed info from WQE then free it.
                short localRequestType = result->requestType;

                // Process WQE.
                if (localRequestType == REQUESTTYPE_READREADY) {
                    // Data has arrived.
                    localRC = LCOM_TIMEDWAITONWORKQ_RC_DATA_ARRIVED;
                } else if (localRequestType == REQUESTTYPE_DISCONNECT) {
                    // Connection close has been initiated by the other side of the connection.  Tell the caller.
                    localRC = LCOM_TIMEDWAITONWORKQ_RC_CLOSERECEIVED;
                } else if (localRequestType == REQUESTTYPE_CONNECTRESPONSE) {
                    // Connection Response.
                    if (result->requestSpecificParms.connectResponseParms.failureCode == REQUESTTYPE_CONNECTRESPONSE_OK) {
                        localRC = LCOM_TIMEDWAITONWORKQ_CONRESP_OK;
                    } else {
                        localRC = LCOM_TIMEDWAITONWORKQ_CONRESP_FAIL;
                    }
                } else {
                    localRC = LCOM_TIMEDWAITONWORKQ_UNKNOWN_WRQE;
                }

                freeLComWorkQueueElement(readQ_p->workQueue_p, result);
            } else {
                localRC = LCOM_TIMEDWAITONWORKQ_NO_WRQE;
            }
            break;
        }
        case LCOM_WRQ_WAITONWORK_RC_BADSTATE: {
            localRC = LCOM_TIMEDWAITONWORKQ_BADQSTATE;
            break;
        }
        case LCOM_WRQ_WAITONWORK_RC_TIMEDOUT: {
            localRC = LCOM_TIMEDWAITONWORKQ_TIMEDOUT;
            break;
        }
        default:
            localRC = LCOM_TIMEDWAITONWORKQ_UNKN_WOW_RC;
    }

    return localRC;
}

 /**
  * Release storage for a LocalCommWorkQueueElement
  */
 static void freeLComWorkQueueElement(LocalCommWorkQueue* workQueue_p, LocalCommWorkQueueElement* inWRQE_p) {

     if (workQueue_p != NULL && inWRQE_p != NULL) {
         if (TraceActive(trc_level_detailed)) {
             TraceRecord(trc_level_detailed,
                         TP(TP_SERVER_LCOM_QUEUE_FREE_WRQE),
                         "freeLComWorkQueueElement, freeing this WRQE",
                         TRACE_DATA_RAWDATA(sizeof(*inWRQE_p),
                                            inWRQE_p,
                                            "inWRQE_p"),
                         TRACE_DATA_END_PARMS);
         }

         // Cleanup other resources allocated to WRQE
         memcpy(inWRQE_p->eyecatcher,
                SERVER_LCOM_WRQE_EYECATCHER_FREE,
                sizeof(inWRQE_p->eyecatcher));

         freeCellPoolCell(workQueue_p->LOCL_p->queueElementPool,
                          inWRQE_p);
     }
 }   // end, freeLComWorkQueueElement

/**
 * Cleanup the list of Work Request Queue Elements returned previously.
 */
void releaseReturnedWRQEs(LocalCommWorkQueue* workQueue_p) {

    if (workQueue_p != NULL && workQueue_p->currentWRQE_List != NULL) {
        LocalCommWorkQueueElement* nextWRQE_p;
        LocalCommWorkQueueElement* currentWRQE_p = workQueue_p->currentWRQE_List;
        workQueue_p->currentWRQE_List = NULL;

        for (;
             currentWRQE_p != NULL;
             currentWRQE_p = nextWRQE_p) {
            nextWRQE_p = (LocalCommWorkQueueElement*) (currentWRQE_p->stackElement_header.element_prev_p);
            freeLComWorkQueueElement(workQueue_p, currentWRQE_p);
        }
    }

}   // end, releaseReturnedWRQEs

/**
 * Obtain storage for a work or data queue element.
 *
 * @param LOCL_p The LOCL containing the pool ID and the shared memory info object.
 *
 * @return A cell to use, or NULL if no cell was available.
 */
static LocalCommCommonQueueElement* getLComCommonQueueElement(LocalCommClientAnchor_t* LOCL_p) {
    LocalCommCommonQueueElement* element_p = getCellPoolCell(LOCL_p->queueElementPool);
    if (element_p == NULL) {
        void* newStorage_p = getLocalCommSharedStorage(&(LOCL_p->info),
                                                       LCOM_WRQ_CELLPOOL_GROWTH_SIZE);
        if (TraceActive(trc_level_detailed)) {
            TraceRecord(trc_level_detailed,
                        TP(TP_SERVER_LCOM_QUEUE_GETWRQE_GETSTOR),
                        "getLComCommonQueueElement, getLocalCommSharedStorage",
                        TRACE_DATA_PTR(newStorage_p,
                                       "newStorage_p"),
                        TRACE_DATA_END_PARMS);
        }

        if (newStorage_p != NULL) {
            void* extentAddr_p;
            void* cellAddr_p;
            long long extentLen, cellLen, numCells;
            computeExtentDetailsFromSingleAddress(LOCL_p->queueElementPool, newStorage_p, LCOM_WRQ_CELLPOOL_GROWTH_SIZE, &numCells, &extentAddr_p, &extentLen, &cellAddr_p, &cellLen);

            if (numCells > 0) {
                if (TraceActive(trc_level_detailed)) {
                    TraceRecord(trc_level_detailed,
                                TP(TP_SERVER_LCOM_QUEUE_GETWRQE_GROWING),
                                "getLComCommonQueueElement, before growCellPool",
                                TRACE_DATA_HEX_LONG(LOCL_p->queueElementPool,
                                               "LOCL_p->queueElementPool"),
                                TRACE_DATA_PTR(extentAddr_p,
                                               "extentAddr_p"),
                                TRACE_DATA_HEX_LONG(extentLen,
                                                "extentLen"),
                                TRACE_DATA_PTR(newStorage_p,
                                               "newStorage_p"),
                                TRACE_DATA_HEX_LONG(numCells,
                                               "numCells"),
                                TRACE_DATA_HEX_LONG(sizeof(LocalCommCommonQueueElement),
                                               "sizeof(LocalCommCommonQueueElement)"),
                                TRACE_DATA_END_PARMS);
                }
                growCellPool(LOCL_p->queueElementPool, numCells, extentAddr_p, extentLen, cellAddr_p, cellLen);

                element_p = getCellPoolCell(LOCL_p->queueElementPool);
            }
        }
    }

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(TP_SERVER_LCOM_QUEUE_GETWRQE_EXIT),
                    "getLComCommonQueueElement, exit",
                    TRACE_DATA_RAWDATA(sizeof(*element_p),
                                       element_p,
                                       "element_p"),
                    TRACE_DATA_END_PARMS);
    }

    return element_p;
}

/**
 * Obtain storage for a LocalCommWorkQueueElement
 */
static LocalCommWorkQueueElement* getLComWorkQueueElement(LocalCommWorkQueue* workQueue_p) {
    LocalCommCommonQueueElement* element_p = getLComCommonQueueElement(workQueue_p->LOCL_p);
    return (element_p != NULL) ? &(element_p->_workQueueElement) : NULL;
}   // end, getLComWorkQueueElement


/**
 * Release Listener PET with code
 * @param workQueue_p Pointer to Work Queue to release PET
 * @param releaseCode Release code to provide on release of PET
 * @return 0 successfully released PET, 4 no PET to release, 8 failed to release current PET.
 */
int releaseWRQ_PET(LocalCommWorkQueue* workQueue_p, const char* releaseCode) {
    int localRC;

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(TP_SERVER_LCOM_QUEUE_RELWRQ_PET_ENTRY),
                    "releaseWRQ_PET, entry",
                    TRACE_DATA_PTR(workQueue_p, "Work queue address"),
                    TRACE_DATA_RAWDATA(sizeof(iea_release_code),
                                       releaseCode,
                                       "releaseCode"),
                    TRACE_DATA_END_PARMS);
    }

    PloCompareAndSwapAreaQuadWord_t swapArea;
    PloStoreAreaQuadWord_t          storeArea1;  // The PET
    int                             ploRC;
    LocalComWQE_PLO_CS_Area_t       localReplacePLO;

    char                            nullPET[16] = {{0}};
    char                            localPET[16];

    swapArea.compare_p     = workQueue_p->wrqPLO_CS_p;
    memcpy(&(swapArea.expectedValue), swapArea.compare_p, sizeof(swapArea.expectedValue));

    // Setup store area 1 information
    storeArea1.storeLocation_p = &(workQueue_p->wrq_pet);
    memset(&(storeArea1.storeValue), 0, sizeof(storeArea1.storeValue));

    do {
        localRC = 0;

        // Build new SwapArea
        memcpy(&localReplacePLO, &(swapArea.expectedValue), sizeof(localReplacePLO));
        localReplacePLO.ploSequenceNumber+=1;
        memcpy(&(swapArea.replaceValue), &localReplacePLO, sizeof(swapArea.replaceValue));

        // Grab current PET
        memcpy(localPET, workQueue_p->wrq_pet, sizeof(localPET));

        // Attempt to make the work queue updates (zeroing the PET and making sure we got a consistent view of the data)
        ploRC = ploCompareAndSwapAndStoreQuadWord(swapArea.compare_p, &swapArea, &storeArea1);
        if (ploRC == 0) {
            // If there's a PET release it.
            if (memcmp(localPET, nullPET, sizeof(localPET)) != 0) {
                iea_return_code releaseRc;
                iea_auth_type authType = IEA_AUTHORIZED;
                unsigned char currentKey = switchToKey0();
                iea4rls(&releaseRc, authType, localPET, (char *) releaseCode);
                switchToSavedKey(currentKey);
                if (releaseRc != 0) {
                    localRC = 8;
                }
            } else {
                localRC = 4;
            }
        }
    } while (ploRC);


    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(TP_SERVER_LCOM_QUEUE_RELWRQ_PET_EXIT),
                    "releaseWRQ_PET, exit",
                    TRACE_DATA_HEX_INT(localRC,
                                       "localRC"),
                    TRACE_DATA_END_PARMS);
    }

    return localRC;
}   // end, releaseWRQ_PET



/**
 * Initialize the Work Queue ("Black Queue")
 * @param workQueue_p The Queue to be initialized.
 * @param LOCL_p The LOCL containing the cell pool id to use to obtain/release queue elements for this.
 * @param ploCS_Area_p Pointer to the area to use as the PLO Compare and Swap area (contains sequence number).
 * queue.
 */
int initializeWorkQueue(LocalCommWorkQueue* workQueue_p,
                        LocalCommClientAnchor_t* LOCL_p,
                        LocalComWQE_PLO_CS_Area_t* ploCS_Area_p) {

    memset(workQueue_p, 0, sizeof(*workQueue_p));

    workQueue_p->wrqeLimit = BOUNDED_UNSERIALIZED_STACK_LIMIT_DEFAULT;

    //workQueue_p->currentWRQE_List = null;

    workQueue_p->wrqPLO_CS_p = ploCS_Area_p;
    workQueue_p->LOCL_p = LOCL_p;

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(TP_SERVER_LCOM_QUEUE_INITWRQ_EXIT),
                    "initializeWorkQueue, exit",
                    TRACE_DATA_END_PARMS);
    }
}  // end, initializeWorkQueue


#define LOCAL_COMM_RESET_DQ_READ_PENDING 41

/**
 * Adds a piece of work to the queue.  The caller may need to take some action
 * depending on the type of queue this is.  The action will be described by
 * the return code.
 *
 * @param targetDirectionalQ_p Pointer to Local Comm directional queue containing the target work queue and
 * associated data queue.
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
static int addToWorkQueue(LocalCommDirectionalQueue* targetDirectionalQ_p, LocalCommWorkQueueElement* work_p) {

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(TP_SERVER_LCOM_QUEUE_ADDTOWQUE_ENTRY),
                    "addToWorkQueue, entry",
                    TRACE_DATA_PTR(targetDirectionalQ_p, "targetDirectionalQ_p"),
                    TRACE_DATA_PTR(work_p, "work_p"),
                    TRACE_DATA_END_PARMS);
    }

    PloCompareAndSwapAreaQuadWord_t swapArea;
    PloStoreAreaQuadWord_t          storeArea1;  // The Queue
    PloStoreAreaQuadWord_t          storeArea2;  // The PET
    int                             ploRC;

    LocalComWQE_PLO_CS_Area_t       localReplacePLO;
    LocalCommWorkQueue*             localWorkQueue_p = targetDirectionalQ_p->workQueue_p;
    LocalCommWRQ_PLO_Area_t*        newWRQ_p = (LocalCommWRQ_PLO_Area_t*) &(storeArea1.storeValue);
    int                             rc = LOCAL_COMM_ADD_QUEUE_OK;

    char                            nullPET[16] = {{0}};
    char                            localPET[16];

    swapArea.compare_p     = localWorkQueue_p->wrqPLO_CS_p;
    memcpy(&(swapArea.expectedValue), swapArea.compare_p, sizeof(swapArea.expectedValue));

    // Setup store area 1 & 2 information
    storeArea1.storeLocation_p = &(localWorkQueue_p->wrqPLO_Area);

    storeArea2.storeLocation_p = &(localWorkQueue_p->wrq_pet);
    memcpy(&(storeArea2.storeValue), nullPET, sizeof(storeArea2.storeValue));

    do {
        // Build new SwapArea
        memcpy(&localReplacePLO, &(swapArea.expectedValue), sizeof(localReplacePLO));
        localReplacePLO.ploSequenceNumber+=1;
        memcpy(&(swapArea.replaceValue), &localReplacePLO, sizeof(swapArea.replaceValue));

        // Grab current queue/stack info for new StoreArea1
        //*newWRQ_p = localWorkQueue_p->wrqPLO_Area;
        memcpy(newWRQ_p, storeArea1.storeLocation_p, sizeof(storeArea1.storeValue));

        // Check state of queue
        if (newWRQ_p->flags.closedDrainedWorkQueue) {
            rc = LOCAL_COMM_ADD_QUEUE_BAD_STATE;
            break;
        }

        // Prepare updated stack
        if (newWRQ_p->wrqElementCount < localWorkQueue_p->wrqeLimit) {
            newWRQ_p->wrqElementCount++;
            work_p->stackElement_header.element_prev_p = newWRQ_p->wrqHead_p;
            newWRQ_p->wrqHead_p = (ElementDT *) work_p;

            // I don't think I need to copy the PET at the same time as the wrqPLO_Area because changes made to the PET should be
            // made by updating the ploSequenceNumber related to this Work Queue.  So, if the PET changes then the SEQ # should've
            // changed too.
            memcpy(localPET, localWorkQueue_p->wrq_pet, sizeof(localPET));

            ploRC = ploCompareAndSwapAndDoubleStoreQuadWord(swapArea.compare_p, &swapArea, &storeArea1, &storeArea2);

            if (ploRC == 0)
                break;
        } else {
            rc = LOCAL_COMM_ADD_QUEUE_FULL;
            break;
        }

    } while (ploRC);

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(TP_SERVER_LCOM_QUEUE_ADDTOWQUE_PUSHRC),
                    "addToWorkQueue, push results",
                    TRACE_DATA_HEX_INT(rc, "push rc"),
                    TRACE_DATA_END_PARMS);
    }

    if (rc == LOCAL_COMM_ADD_QUEUE_OK) {
        // If necessary, release the PET
        if (memcmp(nullPET, localPET, sizeof(nullPET)) != 0) {
            if (TraceActive(trc_level_detailed)) {
                TraceRecord(trc_level_detailed,
                            TP(TP_SERVER_LCOM_QUEUE_ADDTOWQUE_RELPET),
                            "addToWorkQueue, releasing pet",
                            TRACE_DATA_RAWDATA(sizeof(localPET),localPET, "wrq_pet"),
                            TRACE_DATA_END_PARMS);
            }

            iea_return_code releaseRc = 0;
            iea_auth_type authType = IEA_AUTHORIZED;
            unsigned char currentKey = switchToKey0();
            iea4rls(&releaseRc, authType, localPET, (char *)LCOM_WRQ_PET_CHECK_QUEUE);
            switchToSavedKey(currentKey);

            // TODO: Check return code.
        }
    }

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(TP_SERVER_LCOM_QUEUE_ADDTOWQUE_EXIT),
                    "addToWorkQueue, exit",
                    TRACE_DATA_HEX_INT(rc, "rc"),
                    TRACE_DATA_END_PARMS);
    }

    return rc;
}

/**
 * Mark the work queue so that no more work will be queued.  Take the work that is on the
 * queue and dispose of it.  Reply to any outstanding connect requests.  Ignore the rest.
 *
 * @param
 * @return
 */
int setNoMoreWork(LocalCommWorkQueue* targetWorkQ_p) {
    int localRC = 0;

    PloCompareAndSwapAreaQuadWord_t swapArea;
    PloStoreAreaQuadWord_t          storeArea1;  // The Queue state
    int                             ploRC;

    LocalComWQE_PLO_CS_Area_t       localReplacePLO;
    LocalCommWRQ_PLO_Area_t*        newWRQ_p = (LocalCommWRQ_PLO_Area_t*) &(storeArea1.storeValue);
    LocalCommWorkQueueElement*      newWRQEs = NULL;



    swapArea.compare_p = targetWorkQ_p->wrqPLO_CS_p;
    memcpy(&(swapArea.expectedValue), swapArea.compare_p, sizeof(swapArea.expectedValue));

    // Setup store area information
    storeArea1.storeLocation_p = &(targetWorkQ_p->wrqPLO_Area);

    do {
        // Build new SwapArea
        memcpy(&localReplacePLO, &(swapArea.expectedValue), sizeof(localReplacePLO));
        localReplacePLO.ploSequenceNumber+=1;
        memcpy(&(swapArea.replaceValue), &localReplacePLO, sizeof(swapArea.replaceValue));

        // Grab current queue/stack info for new StoreArea1
        //*newWRQ_p = localWorkQueue_p->wrqPLO_Area;
        memcpy(newWRQ_p, storeArea1.storeLocation_p, sizeof(storeArea1.storeValue));

        // Set state of queue to "no more work"
        newWRQ_p->flags.closedDrainedWorkQueue = 1;

        // Take any remaining work on the queue.
        newWRQEs = (LocalCommWorkQueueElement*) newWRQ_p->wrqHead_p;
        newWRQ_p->wrqHead_p = NULL;
        newWRQ_p->wrqElementCount = 0;

        ploRC = ploCompareAndSwapAndStoreQuadWord(swapArea.compare_p, &swapArea, &storeArea1);
    } while (ploRC);

    // Clean up anything that was on the queue when we drained it.  I use the term clean up
    // loosely here.  We're only replying to connect requests that were not taken by the
    // server.  For other request types, there should be a connection on the server's queue
    // that got closed.  Probably some more windows here that will need to get closed later.
    while (newWRQEs != NULL) {
        if (newWRQEs->requestType == REQUESTTYPE_CONNECT) {

            void* storageToConnect = newWRQEs->requestSpecificParms.connectParms.bbbzlocl_p;
            long long userToken = newWRQEs->requestSpecificParms.connectParms.sharedMemoryUserToken;

            if (accessSharedAboveConditional(storageToConnect, userToken, NULL) == 0) {
                buildAndQueueConnectResponse((LocalCommClientConnectionHandle_t *)(newWRQEs->clientConnHandle),
                                             REQUESTTYPE_CONNECTRESPONSE_FAIL_STOPPING);

                detachSharedAbove(storageToConnect, userToken, FALSE);
            }
        }

        // Free the work queue element and move on to the next one.
        LocalCommWorkQueueElement* toFree_p = newWRQEs;
        newWRQEs = (LocalCommWorkQueueElement*) toFree_p->stackElement_header.element_next_p;
        freeLComWorkQueueElement(targetWorkQ_p, toFree_p);
    }

    return localRC;
}

static int cleanupWorkQueueElement(LocalCommWorkQueue* workQueue_p, LocalCommWorkQueueElement* inWRQE_p) {

     // Hmm...need to investigate if some of these Work Queue elements have resources hung off of
     // them that need to be cleaned up as well.  For now ... I didn't see anything obvious.

     freeLComWorkQueueElement(workQueue_p, inWRQE_p);
     return 0;
}

/************************************************************************************************/
/***********************************************************************************************/
/**
 * Initialize the Data Queue ("Blue Queue")
 * @param dataQueue_p The Queue to be initialized.
 * @param LOCL_p The LOCL containing the cell pool id to use to obtain/release queue elements for this
 * @param inboundQueue If non-zero indicates inbound direction
 * queue.
 */
static int initializeDataQueue(LocalCommDataQueue* dataQueue_p, LocalCommClientAnchor_t* LOCL_p, int inboundQueue) {
    memset(dataQueue_p, 0, sizeof(*dataQueue_p));

    dataQueue_p->dataQLimit = LCOM_SERVER_BLUEQUEUE_LIMIT;
    dataQueue_p->LOCL_p     = LOCL_p;
    dataQueue_p->flags.inboundDataQueue = (inboundQueue ? 1 : 0);

}  // end, initializeDataQueue

/**
 * Release storage for a LocalCommDataQueueElement
 */
static void freeLComDataQueueElement(LocalCommDataQueue* dataQueue_p, LocalCommDataQueueElement* inDRQE_p) {

    if (dataQueue_p != NULL && inDRQE_p != NULL) {
        if (TraceActive(trc_level_detailed)) {
            TraceRecord(trc_level_detailed,
                        TP(TP_SERVER_LCOM_QUEUE_FREE_DRQE),
                        "freeLComDataQueueElement, freeing this DRQE",
                        TRACE_DATA_RAWDATA(sizeof(*inDRQE_p),
                                           inDRQE_p,
                                           "inDRQE_p"),
                        TRACE_DATA_END_PARMS);
        }

        // Cleanup other resources allocated to DRQE
        memcpy(inDRQE_p->eyecatcher,
               SERVER_LCOM_DQE_EYECATCHER_FREE,
               sizeof(inDRQE_p->eyecatcher));

        freeCellPoolCell(dataQueue_p->LOCL_p->queueElementPool,
                         inDRQE_p);
    }
}   // end, freeLComDataQueueElement

static int cleanupDataQueueElement(LocalCommDataQueue* dataQueue_p, LocalCommDataQueueElement* inDRQE_p) {
    // If this data queue element is pointing to shared memory that outside of the LDAT pools, then we need to clean it up
    // now.
    if (inDRQE_p->bbgzlmsg_p->readInfo.flags.dataAreaNotInCellpools) {
        int localRC, localRSN;
        LargeDataMessageHeader_t* largeMsg_p = (LargeDataMessageHeader_t*)
                                               ((char*)(inDRQE_p->bbgzlmsg_p->dataAreaPtr)-sizeof(LargeDataMessageHeader_t));

        memcpy(largeMsg_p->eyecatcher, RELEASED_BBGZLMSG_LARGEDATA_EYE, sizeof(largeMsg_p->eyecatcher));

        // Detach this address space from the shared memory.
        localRC = detachSharedAboveConditional(largeMsg_p,
                                               inDRQE_p->bbgzlmsg_p->largeData.sharingUserToken,
                                               FALSE,
                                               &localRSN);
        // Remove the system affinity to the shared memory.
        localRC = detachSharedAboveConditional(largeMsg_p,
                                               inDRQE_p->bbgzlmsg_p->largeData.owningUserToken,
                                               TRUE,
                                               &localRSN);
        inDRQE_p->bbgzlmsg_p->dataAreaPtr = NULL;
    }

    // Release the LMSG then release the data queue element
    freeLocalCommDataStoreCell(inDRQE_p->bbgzlmsg_p);
    inDRQE_p->bbgzlmsg_p = NULL;

    freeLComDataQueueElement(dataQueue_p, inDRQE_p);

    return 0;
}


/**
 * Obtain storage for a LocalCommDataQueueElement
 */
LocalCommDataQueueElement* getLComDataQueueElement(LocalCommDataQueue* dataQueue_p) {
    LocalCommCommonQueueElement* element_p = getLComCommonQueueElement(dataQueue_p->LOCL_p);
    return (element_p != NULL) ? &(element_p->_dataQueueElement) : NULL;
}   // end, getLComDataQueueElement


/**
 * Add to the specified data queue.
 */
static int addToDataQueue(LocalCommDirectionalQueue* sendQ_p, LocalCommDataQueueElement* dataQE_p) {

    // Add data queue element to target send queue
    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(TP_SERVER_LCOM_QUEUE_ADDTODQUE_ENTRY),
                    "addToDataQueue, entry",
                    TRACE_DATA_PTR(sendQ_p, "sendQ_p"),
                    TRACE_DATA_PTR(dataQE_p, "dataQE_p"),
                    TRACE_DATA_END_PARMS);
    }

    // Track status and activities for debugging.
    unsigned int statusFlags;
    const int INBOUND_DATAQ                           = 0x00000001;
    const int READPENDING_WAS_ON                      = 0x00000002;
    const int QUEUE_WAS_EMPTY                         = 0x00000004;
    const int PLO_SUCCESSFUL                          = 0x00000008;
    const int FAILED_READREADY_QUEUEWORK              = 0x00000010;
    const int BUILT_AND_QUEUED_READREADY              = 0x00000020;
    const int FAILED_READREADY_BUILD                  = 0x00000040;

    PloCompareAndSwapAreaQuadWord_t swapArea;    // SEQ#, flags, cnt
    PloStoreAreaQuadWord_t          storeArea1;  // The Queue Header
    PloStoreAreaQuadWord_t          storeArea2;  // The current tail element

    int                             ploRC;

    LocalCommHandle_PLO_Area_t*     localReplacePLO_p = (LocalCommHandle_PLO_Area_t*)&(swapArea.replaceValue);  // SEQ#, flags, cnt
    LocalCommDataQueue*             localDataQueue_p = sendQ_p->dataQueue_p;
    LocalCommDataQ_PLO_Header_t*    newDataQHeader_p = (LocalCommDataQ_PLO_Header_t*) &(storeArea1.storeValue);
    int                             rc = LOCAL_COMM_ADD_QUEUE_OK;
    int                             needReadReadyWorkRequest;

    ElementDT*                      localDataQE_p = (ElementDT*) &(dataQE_p->stackElement_header);

    LocalCommConnectionHandle_t* connHandle_p = (LocalCommConnectionHandle_t*) sendQ_p->bbgzlhdl_p;

    swapArea.compare_p = &(connHandle_p->handlePLO_CS);
    memcpy(&(swapArea.expectedValue), swapArea.compare_p, sizeof(swapArea.expectedValue));

    storeArea1.storeLocation_p = &(localDataQueue_p->dataQPLO_header);

    do {
        statusFlags              = 0;
        needReadReadyWorkRequest = 0;

        // Build new SwapArea
        memcpy(localReplacePLO_p, &(swapArea.expectedValue), sizeof(*localReplacePLO_p));
        localReplacePLO_p->ploSequenceNumber+=1;

        // Grab current queue/stack info for new StoreArea
        *newDataQHeader_p = localDataQueue_p->dataQPLO_header;

        // Check state of queue
        if (localDataQueue_p->flags.inboundDataQueue) {
            statusFlags += INBOUND_DATAQ;
            // "Inbound" data queue.
            if (localReplacePLO_p->flags.clientInitiatedClosing || localReplacePLO_p->flags.serverInitiatedClosing) {
                rc = LOCAL_COMM_ADD_QUEUE_BAD_STATE;
            } else if (localReplacePLO_p->dataQInboundElementCount >= localDataQueue_p->dataQLimit) {
                rc = LOCAL_COMM_ADD_QUEUE_FULL;
            } else {
                // Prepare new count
                localReplacePLO_p->dataQInboundElementCount++;

                // Check/reset "ReadPending" Flag
                if (localReplacePLO_p->flags.inboundDataQ_readPending) {
                    statusFlags += READPENDING_WAS_ON;
                    localReplacePLO_p->flags.inboundDataQ_readPending = 0;

                    // We need to queue a "Read Ready" work request to wake up a waiter reader
                    needReadReadyWorkRequest = 1;
                }
            }
        } else {
            // "Outbound" data queue.
            if (localReplacePLO_p->flags.clientInitiatedClosing || localReplacePLO_p->flags.serverInitiatedClosing) {
                rc = LOCAL_COMM_ADD_QUEUE_BAD_STATE;
            } else if (localReplacePLO_p->dataQOutboundElementCount >= localDataQueue_p->dataQLimit) {
                rc = LOCAL_COMM_ADD_QUEUE_FULL;
            } else {
                // Prepare new count
                localReplacePLO_p->dataQOutboundElementCount++;

                // Check/reset "ReadPending" Flag
                if (localReplacePLO_p->flags.outboundDataQ_readPending) {
                    statusFlags += READPENDING_WAS_ON;
                    localReplacePLO_p->flags.outboundDataQ_readPending = 0;

                    // We need to queue a "Read Ready" work request to wake up a waiter reader
                    needReadReadyWorkRequest = 1;
                }
            }
        }

        // Bail if we can't add the element to the queue.
        if (rc != LOCAL_COMM_ADD_QUEUE_OK)
            break;

        // Data Queue is added to end and removed from front
        if (newDataQHeader_p->head_p == NULL) {
            statusFlags += QUEUE_WAS_EMPTY;
            // Adding to empty queue
            clearPtrs(localDataQE_p);

            newDataQHeader_p->head_p = localDataQE_p;
            newDataQHeader_p->tail_p = localDataQE_p;

            ploRC = ploCompareAndSwapAndStoreQuadWord(swapArea.compare_p, &swapArea, &storeArea1);
        } else {
            // Adding to end
            localDataQE_p->element_next_p = NULL;
            localDataQE_p->element_prev_p = newDataQHeader_p->tail_p;

            // Set the current tail->next = new element
            storeArea2.storeLocation_p = &(((LocalCommDataQueueElement*)(newDataQHeader_p->tail_p))->stackElement_header);
            memcpy(&(storeArea2.storeValue), storeArea2.storeLocation_p, sizeof(storeArea2.storeValue));
            ((ElementDT*) &(storeArea2.storeValue))->element_next_p = (ElementDT*) localDataQE_p;

            // Set the tail = new element
            newDataQHeader_p->tail_p = localDataQE_p;

            ploRC = ploCompareAndSwapAndDoubleStoreQuadWord(swapArea.compare_p, &swapArea, &storeArea1, &storeArea2);
        }
    } while (ploRC);

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(TP_SERVER_LCOM_QUEUE_ADDTODQUE_PUSHRC),
                    "addToDataQueue, push results",
                    TRACE_DATA_HEX_INT(rc, "push rc"),
                    TRACE_DATA_HEX_INT(needReadReadyWorkRequest, "needReadReadyWorkRequest"),
                    TRACE_DATA_END_PARMS);
    }

    if (rc == LOCAL_COMM_ADD_QUEUE_OK) {
        statusFlags += PLO_SUCCESSFUL;
        // If the read pending bit was set, a 'read ready' request must be placed
        // on the work queue, posting the PET if necessary.
        if (needReadReadyWorkRequest) {
            // Put a  'read ready' queue entry must be placed on the client's outbound work queue
            // (server's inbound work queue), posting the PET if necessary.
            LocalCommWorkQueueElement* readReadyQE_p = buildReadReadyRequest(sendQ_p);

            if (readReadyQE_p != NULL) {

                // Add the Read Ready work request to the work queue and reset pending read flag on the data queue.
                int addRC = addToWorkQueue(sendQ_p, readReadyQE_p);

                if (addRC != LOCAL_COMM_ADD_QUEUE_OK) {
                    statusFlags += FAILED_READREADY_QUEUEWORK;
                    if (TraceActive(trc_level_exception)) {
                        TraceRecord(trc_level_exception,
                                    TP(TP_SERVER_LCOM_QUEUE_ADDTODQUE_FAILED_RR),
                                    "addToDataQueue, add read ready failed",
                                    TRACE_DATA_HEX_INT(addRC, "addRC"),
                                    TRACE_DATA_END_PARMS);
                    }

                    freeLComWorkQueueElement(sendQ_p->workQueue_p, readReadyQE_p);
                } else {
                    statusFlags += BUILT_AND_QUEUED_READREADY;
                }
            } else {
                statusFlags += FAILED_READREADY_BUILD;
            }

        }
    }

    // Footprint addToDataQueue
    createLocalCommFPEntry_addToDataQueue(((CF_FootprintTable*)connHandle_p->footprintTable),
                                          dataQE_p,
                                          localReplacePLO_p,
                                          statusFlags,
                                          rc);

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(TP_SERVER_LCOM_QUEUE_ADDTODQUE_EXIT),
                    "addToDataQueue, entry",
                    TRACE_DATA_HEX_INT(rc, "rc"),
                    TRACE_DATA_END_PARMS);
    }
    return rc;
}

/**
 * Do some data queue book keeping related to a previous read.
 *
 * A call sequence for the read is:
 *      call issueReadRequest -- which returns a ptr to the data to move and length.
 *      ...the caller then does what it wants with the data.  Once securely processed it then
 *      calls to freeLastReadData.
 *
 * If the caller had exhausted the data within the current data queue element, then the element is
 * removed from the data queue and the related storage is released (back to pools).
 *
 * @return 0 if successful, non-zero otherwise.
 */
unsigned short freeLastReadData(LocalCommConnectionHandle_t* connHandle_p, LocalCommDataCell_t* bbgzlmsg_p) {

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(TP_SERVER_LCOM_QUEUE_FREELREAD_ENTRY),
                    "freeLastReadData, entry",
                    TRACE_DATA_PTR(connHandle_p, "connHandle_p"),
                    TRACE_DATA_HEX_INT(connHandle_p->instanceCount, "handle instance"),
                    TRACE_DATA_PTR(bbgzlmsg_p, "bbgzlmsg_p"),
                    TRACE_DATA_END_PARMS);
    }

    unsigned short             localRC;
    LocalCommDirectionalQueue* readQ_p = NULL;
    // If we are client-side issuing the read
    if (amIClientSide(connHandle_p)) {
        readQ_p = &(connHandle_p->clientInboundQ);
    } else {
        // Server-side issuing a READ request
        readQ_p = &(connHandle_p->clientOutboundQ);
    }


    PloCompareAndSwapAreaQuadWord_t swapArea;    // SEQ#, flags, cnt
    PloStoreAreaQuadWord_t          storeArea1;  // The Queue Header
    PloStoreAreaQuadWord_t          storeArea2;  // The current 2nd element
    int                             ploRC;

    LocalCommHandle_PLO_Area_t*     localReplacePLO_p = (LocalCommHandle_PLO_Area_t*)&(swapArea.replaceValue);  // SEQ#, flags, cnt
    LocalCommDataQueue*             localDataQueue_p = readQ_p->dataQueue_p;
    LocalCommDataQ_PLO_Header_t*    newDataQHeader_p = (LocalCommDataQ_PLO_Header_t*) &(storeArea1.storeValue);
    LocalCommDataQueueElement*      dataQE_p;
    ReadInfo_PLO_Area_t             newLMSGReadInfo;  // Just looking at it...


    swapArea.compare_p = &(connHandle_p->handlePLO_CS);
    memcpy(&(swapArea.expectedValue), swapArea.compare_p, sizeof(swapArea.expectedValue));

    storeArea1.storeLocation_p = &(localDataQueue_p->dataQPLO_header);

    do {
        localRC = LCOM_FREELREAD_RC_OK;

        // Build new SwapArea
        memcpy(localReplacePLO_p, &(swapArea.expectedValue), sizeof(*localReplacePLO_p));
        localReplacePLO_p->ploSequenceNumber+=1;

        // Grab current queue header info for new StoreArea
        memcpy(newDataQHeader_p, storeArea1.storeLocation_p, sizeof(storeArea1.storeValue));

        // Grab first Element pointer
        dataQE_p = (LocalCommDataQueueElement* ) newDataQHeader_p->head_p;
        
        // For now.   We're assuming that the input LMSG MUST be the first (oldest) data queue element on the queue.
        if (dataQE_p != NULL) {
            if (dataQE_p->bbgzlmsg_p == bbgzlmsg_p) {
                // Get the "Read" information from the LMSG.
                newLMSGReadInfo = bbgzlmsg_p->readInfo;
                
                // If the reader has read all the data from this data element
                if (newLMSGReadInfo.bytesAlreadyRead == bbgzlmsg_p->dataAreaSize) {
                    // -------------------------------------------------------------------
                    // Remove this data queue element
                    // -------------------------------------------------------------------
                    
                    // Prepare queue flags and count
                    if (localDataQueue_p->flags.inboundDataQueue) {
                        // "Inbound" data queue.
                        if (localReplacePLO_p->dataQInboundElementCount > 0) {
                           localReplacePLO_p->dataQInboundElementCount--;
                        }
                    } else {
                        // "Outbound" data queue.
                        if (localReplacePLO_p->dataQOutboundElementCount > 0) {
                           localReplacePLO_p->dataQOutboundElementCount--;
                        } 
                    }
                    
                    // If only 1 element on queue or Empty queue
                    if (newDataQHeader_p->head_p == newDataQHeader_p->tail_p) {
                        newDataQHeader_p->head_p = NULL;
                        newDataQHeader_p->tail_p = NULL;

                        ploRC = ploCompareAndSwapAndStoreQuadWord(swapArea.compare_p, &swapArea, &storeArea1);
                    } else {
                        // Set the 2nd element->prev = NULL
                        ElementDT* current2ndElement = dataQE_p->stackElement_header.element_next_p;

                        storeArea2.storeLocation_p = &(((LocalCommDataQueueElement*)(current2ndElement))->stackElement_header);
                        memcpy(&(storeArea2.storeValue), storeArea2.storeLocation_p, sizeof(storeArea2.storeValue));
                        ((ElementDT*) &(storeArea2.storeValue))->element_prev_p = NULL;

                        // Set the head = 2nd Element ptr
                        newDataQHeader_p->head_p = current2ndElement;

                        ploRC = ploCompareAndSwapAndDoubleStoreQuadWord(swapArea.compare_p, &swapArea, &storeArea1, &storeArea2);
                    }   
                    
                    if (ploRC == 0) {
                        // Release data queue element and bbgzlmsg
                        cleanupDataQueueElement(localDataQueue_p, dataQE_p);
                    }
                } else {
                    localRC = LCOM_FREELREAD_RC_MORE_TO_READ;
                    ploRC = 0;
                }
            } else {
                localRC = LCOM_FREELREAD_RC_LMSG_NOT_MATCHED;
                ploRC = 0;
            }
        } else {
            localRC = LCOM_FREELREAD_RC_LMSG_NOTFOUND;
            ploRC = 0;
        }
    } while (ploRC);


    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(TP_SERVER_LCOM_QUEUE_FREELREAD_EXIT),
                    "freeLastReadData, exit",
                    TRACE_DATA_INT(localRC, "return code"),
                    TRACE_DATA_END_PARMS);
    }
    return localRC;
}

/**
 * Return the amount of data available to read at this time.
 *
 * @param readQ_p Pointer to the directional queue
 * @param setReadPending is an indicator to set the "Read Pending" flag if their is currently no data to read
 * @return number of bytes available to read from the "Blue Queue"
 * TODO: Need to update this method when we implement returning a list of LMSGs.
 */
static unsigned long long dataAvailableOnDataQueue(LocalCommDirectionalQueue* readQ_p, unsigned char setReadPending) {
    unsigned long long dataAvailable;

      // Remove a data queue element from the target data queue
      if (TraceActive(trc_level_detailed)) {
          TraceRecord(trc_level_detailed,
                      TP(TP_SERVER_LCOM_QUEUE_DATAAVAIL_ENTRY),
                      "dataAvailableOnDataQueue, entry",
                      TRACE_DATA_PTR(readQ_p,         "readQ_p       "),
                      TRACE_DATA_CHAR(setReadPending, "setReadPending"),
                      TRACE_DATA_END_PARMS);
      }

      PloCompareAndSwapAreaQuadWord_t swapArea;    // SEQ#, flags, cnt
      PloStoreAreaQuadWord_t          storeArea;  // The Queue Header

      int                             ploRC;

      LocalCommHandle_PLO_Area_t*     localReplacePLO_p = (LocalCommHandle_PLO_Area_t*)&(swapArea.replaceValue);  // SEQ#, flags, cnt
      LocalCommDataQueue*             localDataQueue_p = readQ_p->dataQueue_p;
      LocalCommDataQ_PLO_Header_t     newDataQHeader;

      LocalCommConnectionHandle_t*    connHandle_p = (LocalCommConnectionHandle_t*) readQ_p->bbgzlhdl_p;

      swapArea.compare_p = &(connHandle_p->handlePLO_CS);
      memcpy(&(swapArea.expectedValue), swapArea.compare_p, sizeof(swapArea.expectedValue));

      do {
          dataAvailable = 0;

          // Build new SwapArea
          memcpy(localReplacePLO_p, &(swapArea.expectedValue), sizeof(*localReplacePLO_p));
          localReplacePLO_p->ploSequenceNumber+=1;

          // Grab current queue/stack info for new StoreArea
          newDataQHeader = localDataQueue_p->dataQPLO_header;

          // Check state of queue
          if (localDataQueue_p->flags.inboundDataQueue) {
              if (localReplacePLO_p->flags.inboundDataQ_closedDrainedDataQueue) {
                  break;
              }
          } else {
              if (localReplacePLO_p->flags.outboundDataQ_closedDrainedDataQueue) {
                  break;
              }
          }

          // Check queue for data
          LocalCommDataQueueElement* firstDataQE_p = (LocalCommDataQueueElement*) newDataQHeader.head_p;
          if ((firstDataQE_p != NULL) &&
              (firstDataQE_p->bbgzlmsg_p != NULL)) {

              // Return the data length from the first message on the "Blue Queue"
              dataAvailable = firstDataQE_p->bbgzlmsg_p->dataAreaSize - firstDataQE_p->bbgzlmsg_p->readInfo.bytesAlreadyRead;

              // If there's no data because its all been read
              if (firstDataQE_p->bbgzlmsg_p->readInfo.bytesAlreadyRead >= firstDataQE_p->bbgzlmsg_p->dataAreaSize) {
                  // Something is wrong.  Caller should've driven the freeLastReadData() to free this element prior
                  // to driving preview.
                  abend_with_data(ABEND_TYPE_SERVER,
                                  KRSN_SERVER_LCOM_QUEUE_DATAAVAIL_INVALID,
                                  firstDataQE_p->bbgzlmsg_p,
                                  (void*)dataAvailable);
              }
          } else {
              if (setReadPending) {
                 if (localDataQueue_p->flags.inboundDataQueue) {
                      localReplacePLO_p->flags.inboundDataQ_readPending = 1;
                  } else {
                      localReplacePLO_p->flags.outboundDataQ_readPending = 1;
                  }
              }
          }

          ploRC = ploCompareAndSwapQuadWord(swapArea.compare_p, &swapArea);

      } while (ploRC);

      if (TraceActive(trc_level_detailed)) {
          TraceRecord(trc_level_detailed,
                      TP(TP_SERVER_LCOM_QUEUE_DATAAVAIL_EXIT),
                      "dataAvailableOnDataQueue, exit",
                      TRACE_DATA_HEX_LONG(dataAvailable, "dataAvailable"),
                      TRACE_DATA_END_PARMS);
      }
      return dataAvailable;
}

/**
 * Initialize a Local Comm Directional queue.  This is either an Inbound or Outbound Queue comprised of
 * a Work Queue ("Black Queue") and a Data Queue ("Blue Queue").
 *
 * @param targetQueue The Direction queue to be initialized.
 * @param connHandle_p The associated Connection handle.
 * @param workQueue_p The Work Queue to be associated with this directional queue.
 * @param workQueueCellpoolLOCL_p LOCL containing the Work Queue element cellpool anchor.
 * @param dataQueue_p The Data Queue to be associated with this directional queue.
 * @param dataQueueCellpoolLOCL_p LOCL containing the Data Queue element cellpool anchor.
 */
int initializeDirectionalQueue(LocalCommDirectionalQueue *  targetQueue,
                               LocalCommConnectionHandle_t* connHandle_p,
                               LocalCommWorkQueue* workQueue_p,
                               LocalCommClientAnchor_t* workQueueCellpoolLOCL_p,
                               LocalCommDataQueue* dataQueue_p,
                               LocalCommClientAnchor_t* dataQueueCellpoolLOCL_p) {

    int inboundQueue = 0;

    // Initialize the Work queue ("Black Queue") if not already done
    if (workQueue_p != NULL && (workQueueCellpoolLOCL_p != NULL)) {
        inboundQueue = 1;
        initializeWorkQueue(workQueue_p,
                            workQueueCellpoolLOCL_p,
                            (LocalComWQE_PLO_CS_Area_t*)&(connHandle_p->handlePLO_CS));
    }

    // Initialize the Data queue ("Blue Queue") if not already done
    if (dataQueue_p != NULL && (dataQueueCellpoolLOCL_p != NULL)) {
        initializeDataQueue(dataQueue_p, dataQueueCellpoolLOCL_p, inboundQueue);
    }

    targetQueue->workQueue_p = workQueue_p;
    targetQueue->dataQueue_p = dataQueue_p;
    targetQueue->bbgzlhdl_p  = connHandle_p; // TODO: Should  be a token instead of ptr????
}

/**
 * Common routine for initializing the given workElement_p with the given data.
 *
 * @param workElement_p - The work element to init (must not be NULL)
 * @param requestType - work element request type
 * @param clientConnHandle_p - client connection handle associated with this work
 *
 * @return workElement_p
 */
LocalCommWorkQueueElement * initializeWorkQueueElement(LocalCommWorkQueueElement * workElement_p, 
                                                       short requestType,
                                                       LocalCommClientConnectionHandle_t * clientConnHandle_p) {

    memset(workElement_p, 0, sizeof(LocalCommWorkQueueElement));
    memcpy(workElement_p->eyecatcher, SERVER_LCOM_WRQE_EYECATCHER_INUSE, sizeof(workElement_p->eyecatcher));
    workElement_p->version = LCOMWORKQUEUEELEMENT_CURRENT_VER;
    workElement_p->length = sizeof(LocalCommWorkQueueElement);
    workElement_p->requestType = requestType;
    __stck(&(workElement_p->createStck));
    memcpy(workElement_p->clientConnHandle, clientConnHandle_p, sizeof(workElement_p->clientConnHandle));

    return workElement_p;
}

/**
 * Build and queue a Connect Response work request back to the client
 *
 * @param localConnHandle_p Reference to the Connection Handle.
 * @param failureCode the return code from connect.  0 if successful, non-zero if failed.
 *
 * @return refer to return codes from function addToWorkQueue.  May also
 * receive a LOCAL_COMM_ADD_QUEUE_NO_CELL if a work element cell could not be obtained.
 * May also receive a LOCAL_COMM_STALE_HANDLE is the handle is stale.
 */
int buildAndQueueConnectResponse(LocalCommClientConnectionHandle_t * localClientConnHandle_p, short failureCode) {
    int localRC;

    if (validateClientConnectionHandle(localClientConnHandle_p) != 0)  {
        return LOCAL_COMM_STALE_HANDLE;
    }

    LocalCommConnectionHandle_t* localConnHandle_p = localClientConnHandle_p->handle_p;

   // Get a Work queue element from Client's work queue element pool
    //TODO: Need some serialization during the cell obtain and referencing through the LOCL off of the workQueue_p
    LocalCommWorkQueueElement * connResponseQE_p = getLComWorkQueueElement(localConnHandle_p->clientInboundQ.workQueue_p);

    if (connResponseQE_p != NULL) {

        initializeWorkQueueElement(connResponseQE_p, REQUESTTYPE_CONNECTRESPONSE, localClientConnHandle_p);
        connResponseQE_p->requestSpecificParms.connectResponseParms.failureCode = failureCode;

        localRC = addToWorkQueue(&(localConnHandle_p->clientInboundQ), connResponseQE_p);
    } else {
        // Error
        localRC = LOCAL_COMM_ADD_QUEUE_NO_CELL;
    }

    return localRC;
}

/**
 * Build connect work request
 *
 * @param connHandle_p Pointer to the Local Comm Connection handle (BBGZLHDL)
 *
 * @return pointer to WRQE representing a Connect request or NULL.
 */
LocalCommWorkQueueElement * buildConnectRequest(LocalCommClientConnectionHandle_t* clientConnHandle_p) {
    LocalCommWorkQueueElement* connectWorkElement_p = NULL;
    if (validateClientConnectionHandle(clientConnHandle_p) == 0) {

        // Get a queue element and fill it in with the connect request.
        connectWorkElement_p = getLComWorkQueueElement(clientConnHandle_p->handle_p->clientOutboundQ.workQueue_p);

        if (connectWorkElement_p != NULL) {

            initializeWorkQueueElement(connectWorkElement_p, REQUESTTYPE_CONNECT, clientConnHandle_p);

            // CONNECT-specific data
            connectWorkElement_p->requestSpecificParms.connectParms.bbbzlocl_p = clientConnHandle_p->handle_p->bbgzlscl_p->localCommClientControlBlock_p;
            connectWorkElement_p->requestSpecificParms.connectParms.bbgzldat_p = clientConnHandle_p->handle_p->bbgzlscl_p->firstDataStore_p;
            connectWorkElement_p->requestSpecificParms.connectParms.sharedMemoryUserToken = (long long)clientConnHandle_p->handle_p->bbgzlscl_p;
        }
    }

    return connectWorkElement_p;
}



/**
 * @param ffdcWorkElement_p - The workElement storage (must not be NULL)
 * @param clientConnHandle_p - client connection handle associated with this work
 * @param tp - The trace point 
 * @param rawData Raw data for the FFDC record
 *
 * @return ffdcWorkElement_p
 */
LocalCommWorkQueueElement * initializeFFDCWorkQueueElement(LocalCommWorkQueueElement * ffdcWorkElement_p, 
                                                           LocalCommClientConnectionHandle_t* clientConnHandle_p,
                                                           int tp,
                                                           char rawData[REQUESTTYPE_FFDC_RAWDATA_SIZE]) {
                                                           
    initializeWorkQueueElement(ffdcWorkElement_p, REQUESTTYPE_FFDC, clientConnHandle_p);

    // FFDC-specific data
    ffdcWorkElement_p->requestSpecificParms.ffdcParms.tp = tp;
    if (rawData != NULL) {
        memcpy(ffdcWorkElement_p->requestSpecificParms.ffdcParms.rawData, rawData, REQUESTTYPE_FFDC_RAWDATA_SIZE);
    }

    return ffdcWorkElement_p;
}

/**
 * Build an FFDC work request element.
 *
 * @param connHandle_p Pointer to the Local Comm Connection handle (BBGZLHDL)
 * @param tp The trace point identifying the FFDC record
 * @param rawData Raw data for the FFDC record
 *
 * @return pointer to WRQE representing the FFDC request;
 *         NULL if the client conn handle is invalid;
 *         NULL if storage could not be allocated
 */
LocalCommWorkQueueElement * buildFFDCRequest(LocalCommClientConnectionHandle_t* clientConnHandle_p,
                                             int tp,
                                             char rawData[REQUESTTYPE_FFDC_RAWDATA_SIZE]) {

    if (validateClientConnectionHandle(clientConnHandle_p) != 0) {
        return NULL;    // Error. Bail.
    }

    // Make sure the connection is not closing.  We may have detached from the server's LOCL.
    // TODO: We need a broader solution for the problem when the client has closed the last
    //       connection and detached from the server's LOCL and client's LDAT, but the server
    //       has not closed the connection and invalidated the handle instance count yet.
    //       This same problem exists in other parts of the code.
    if (amIClientSide(clientConnHandle_p->handle_p)) {
        if (clientConnHandle_p->handle_p->handlePLO_CS.flags.clientInitiatedClosing == 1) {
            return NULL;
        }
    } else {
        if (clientConnHandle_p->handle_p->handlePLO_CS.flags.serverInitiatedClosing == 1) {
            return NULL;
        }
    }

    LocalCommWorkQueueElement* ffdcWorkElement_p = getLComWorkQueueElement(clientConnHandle_p->handle_p->clientOutboundQ.workQueue_p);
    if (ffdcWorkElement_p == NULL) {
        return NULL;    // Error. Bail.
    }

    return initializeFFDCWorkQueueElement(ffdcWorkElement_p, clientConnHandle_p, tp, rawData);
}

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
int issueFFDCRequest(LocalCommClientConnectionHandle_t* clientConnHandle_p, int tp, char rawData[REQUESTTYPE_FFDC_RAWDATA_SIZE]) {

    // Build work request
    LocalCommWorkQueueElement* workElement_p = buildFFDCRequest(clientConnHandle_p, tp, rawData);
    if (workElement_p == NULL) {
        return 8; // Error. Bail.
    }

    // Queue it.
    int workqRC = addToWorkQueue(&(clientConnHandle_p->handle_p->clientOutboundQ), workElement_p);
    if (workqRC != LOCAL_COMM_ADD_QUEUE_OK) {
        // Free failed work element
        freeLComWorkQueueElement(clientConnHandle_p->handle_p->clientOutboundQ.workQueue_p, workElement_p);

        // TODO: log a footprint record
        // createLocalCommFPEntry_issueConnectRequest_AddToWorkQFailed(clientConnHandle_p->handle_p->bbgzlscl_p->localCommClientControlBlock_p->footprintTable,
        //                                                             clientConnHandle_p,
        //                                                             &(clientConnHandle_p->handle_p->bbgzlscl_p->serverStoken),
        //                                                             workqRC,
        //                                                             localRC);
    }

    return workqRC;
}

/**
 * Build send data request
 *
 * @param connHandle_p Pointer to the Local Comm Connection handle (BBGZLHDL)
 *
 * @return pointer to DQE representing a SEND request or NULL.
 */
static LocalCommDataQueueElement * buildSendRequest(LocalCommConnectionHandle_t* connHandle_p) {

    // Get a queue element and fill it in with the send request.  Note: both inbound and outbound data queues use the
    // same cellpool.  So here we don't have to now which direction the send is going.
    LocalCommDataQueueElement* sendDataElement_p = getLComDataQueueElement(connHandle_p->clientOutboundQ.dataQueue_p);
    if (sendDataElement_p != NULL) {
        memset(sendDataElement_p, 0, sizeof(LocalCommDataQueueElement));
        memcpy(sendDataElement_p->eyecatcher, SERVER_LCOM_DRQE_EYECATCHER_INUSE,
               sizeof(sendDataElement_p->eyecatcher));
        sendDataElement_p->version = LCOMDATAQUEUEELEMENT_CURRENT_VER;
        sendDataElement_p->length = sizeof(LocalCommDataQueueElement);
        sendDataElement_p->requestType = REQUESTTYPE_SEND;
        __stck(&(sendDataElement_p->createStck));


    } else {
        //TODO: go boom!!! Issue error!  something.
    }

    return sendDataElement_p;
}

/**
 * Build a read ready work request
 *
 * @param queue_p Pointer to the Local Comm Directional queue
 *
 * @return pointer to WRQE representing a Read ready request
 */
static LocalCommWorkQueueElement * buildReadReadyRequest(LocalCommDirectionalQueue* queue_p) {

    // TODO: When adding serialization here it may be necessary to pass the client connection
    //       handle (or at least the instance count) in here rather than building it on the fly.
    LocalCommConnectionHandle_t* handle_p = (LocalCommConnectionHandle_t*)(queue_p->bbgzlhdl_p);
    LocalCommClientConnectionHandle_t clientConnHandle = {
        .handle_p = handle_p,
        .instanceCount = handle_p->instanceCount,
        ._available = {0x00, 0x00, 0x00, 0x00}
    };

    // Get a queue element and fill it in with the read ready request.

    LocalCommWorkQueueElement* readReadyWorkElement_p = getLComWorkQueueElement(queue_p->workQueue_p);
    if (readReadyWorkElement_p != NULL) {

        initializeWorkQueueElement(readReadyWorkElement_p, REQUESTTYPE_READREADY, &clientConnHandle);

    } else {
        //TODO: go boom!!! Issue error!  something.
    }

    return readReadyWorkElement_p;
}

static int buildAndQueueCloseRequest(LocalCommConnectionHandle_t * localConnHandle_p) {
    int localRC;
    LocalCommDirectionalQueue* targetQ_p;

    //TODO: validate the handle

    // If we are client-side issuing the close
    if (amIClientSide(localConnHandle_p)) {
        targetQ_p = &(localConnHandle_p->clientOutboundQ);
    } else {
        // Server-side issuing a Close request
        targetQ_p = &(localConnHandle_p->clientInboundQ);
    }

    // Get a Work queue element from other-sides work queue element pool
    LocalCommWorkQueueElement * closeRequestQE_p = getLComWorkQueueElement(targetQ_p->workQueue_p);

    if (closeRequestQE_p != NULL) {
        // TODO: When adding serialization here it may be necessary to pass the client connection
        //       handle (or at least the instance count) in here rather than building it on the fly.
        LocalCommClientConnectionHandle_t clientConnHandle = {
            .handle_p = localConnHandle_p,
            .instanceCount = localConnHandle_p->instanceCount,
            ._available = {0x00, 0x00, 0x00, 0x00}
        };

        initializeWorkQueueElement(closeRequestQE_p, REQUESTTYPE_DISCONNECT, &clientConnHandle);

        // Queue the close request.
        localRC = addToWorkQueue(targetQ_p, closeRequestQE_p);

    } else {
        // Error
        localRC = LOCAL_COMM_ADD_QUEUE_NO_CELL;
    }


    return localRC;
}

/**
 * Build and queue a Connect Request to the outbound work queue for delivery to the server.
 *
 * @param connHandle_p Pointer to the connection handle that Client has initialized.
 * @param timeToWait an Integer number of seconds to wait for a connect response.  A value of
 *             zero indicates to wait indefinitely until a response is received.
 * @return LCOM_ISSUECONNECTREQUEST_RC_OK if the connect was successful.
 *         LCOM_ISSUECONNECTREQUEST_RC_ADDTOWORK_FAILED if we couldn't add the connect request to
 *             the server queue.
 *         LCOM_ISSUECONNECTREQUEST_RC_UNKNOWN_ERR if the return from waiting for a response did
 *             not make sense.
 *         LCOM_ISSUECONNECTREQUEST_RC_TIMEDOUT if we didn't receive a response from the server within
 *             the time specified by parameter timeToWait.
 *         LCOM_ISSUECONNECTREQUEST_RC_BUILDCONN_WRQE_FAILED if we couldn't build the connect request.
 */
int issueConnectRequest(LocalCommClientConnectionHandle_t* clientConnHandle_p, int timeToWait) {
    int localRC = LCOM_ISSUECONNECTREQUEST_RC_OK, workqRC = 0;

    // Build connect work request
    LocalCommWorkQueueElement* connectQE_p = buildConnectRequest(clientConnHandle_p);

    if (connectQE_p != NULL) {

        // Add connect request to server's work queue
        workqRC = addToWorkQueue(&(clientConnHandle_p->handle_p->clientOutboundQ), connectQE_p);

        if (workqRC == LOCAL_COMM_ADD_QUEUE_OK) {
            // Wait for the connect response on client's work queue.
            workqRC = timedWaitOnWorkQueue(&(clientConnHandle_p->handle_p->clientInboundQ), timeToWait);

            switch (workqRC) {
                case LCOM_TIMEDWAITONWORKQ_CONRESP_OK: {
                    localRC = LCOM_ISSUECONNECTREQUEST_RC_OK;
                    break;
                }
                case LCOM_TIMEDWAITONWORKQ_TIMEDOUT: {
                    localRC = LCOM_ISSUECONNECTREQUEST_RC_TIMEDOUT;
                    break;
                }
                default : {
                    localRC = LCOM_ISSUECONNECTREQUEST_RC_UNKNOWN_ERR;
                }
            }
        } else {
            // Free failed Connect request
            freeLComWorkQueueElement(clientConnHandle_p->handle_p->clientOutboundQ.workQueue_p, connectQE_p);

            localRC = LCOM_ISSUECONNECTREQUEST_RC_ADDTOWORK_FAILED;
        }
    } else {
        localRC = LCOM_ISSUECONNECTREQUEST_RC_BUILDCONN_WRQE_FAILED;
    }

    if (localRC != LCOM_ISSUECONNECTREQUEST_RC_OK) {
        createLocalCommFPEntry_issueConnectRequest_AddToWorkQFailed(clientConnHandle_p->handle_p->bbgzlscl_p->localCommClientControlBlock_p->footprintTable,
                                                                    clientConnHandle_p,
                                                                    &(clientConnHandle_p->handle_p->bbgzlscl_p->serverStoken),
                                                                    workqRC,
                                                                    localRC);
    }

    return localRC;
}

void freeConnectResults(LocalCommConnectionHandle_t* connHandle_p, LocalCommWorkQueueElement * connResults) {
    // Free queue element
    freeLComWorkQueueElement(connHandle_p->clientInboundQ.workQueue_p, connResults);
}

/**
 * Build and queue a Close request to the "Black Queue" of the other side of the connection.
 * @param connHandle_p Pointer to the Connection handle (bbgzlhdl).
 * @return 0 if all went well, non-zero othewise.
 */
int issueCloseRequest(LocalCommConnectionHandle_t* connHandle_p) {
    int localRC = buildAndQueueCloseRequest(connHandle_p);

    return localRC;
}

void initiateClosingServerQueue(LocalCommWorkQueue* localWorkQueuePtr) {

    PloCompareAndSwapAreaQuadWord_t swapArea;
    PloStoreAreaQuadWord_t          storeArea1;  // The Inbound Work Queue
    int                             ploRC;

    LocalCommHandle_PLO_Area_t*     localReplacePLO_p = (LocalCommHandle_PLO_Area_t*)&(swapArea.replaceValue);
    LocalCommWRQ_PLO_Area_t*        newInWorkQ_p = (LocalCommWRQ_PLO_Area_t*) &(storeArea1.storeValue);

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(TP_SERVER_LCOM_QUEUE_INITIATE_SERVER_CLOSE),
                    "initiateClosingServerQueue, entry",
                    TRACE_DATA_PTR(localWorkQueuePtr, "Work Queue Ptr"),
                    TRACE_DATA_END_PARMS);
    }

    swapArea.compare_p = localWorkQueuePtr->wrqPLO_CS_p;
    memcpy(&(swapArea.expectedValue), swapArea.compare_p, sizeof(swapArea.expectedValue));

    storeArea1.storeLocation_p = &(localWorkQueuePtr->wrqPLO_Area);

    do {
        // Build new SwapArea
        memcpy(localReplacePLO_p, &(swapArea.expectedValue), sizeof(*localReplacePLO_p));
        localReplacePLO_p->ploSequenceNumber+=1;

        // Grab current stack info for new StoreAreas (the work queue)
        memcpy(newInWorkQ_p, storeArea1.storeLocation_p, sizeof(storeArea1.storeValue));

        // Mark the server work queue as closing.  If the server is currently processing
        // requests, there will not be a PET to release, and so this flag will tell the
        // reader thread when it comes back down to read, that the queue has closed.
        newInWorkQ_p->flags.closingWorkQueue  = 1;

        ploRC = ploCompareAndSwapAndStoreQuadWord(swapArea.compare_p, &swapArea, &storeArea1);

    } while (ploRC);

    // If there is a PET, release it.  I guess we could have taken the PET during the previous
    // PLO, but we already had a method to release it so I figured, just call that.
    releaseWRQ_PET(localWorkQueuePtr, LCOM_WRQ_PET_STOP_LISTENER);
}

/**
 * Mark the client's inbound and outbound queues for closure.
 * @param connHandle_p Pointer to the Connection handle (bbgzlhdl).
 * @return LCOM_CLOSINGCLIENTQUEUES_CLOSED if successfully progressed to "CLOSED" not just "CLOSING".
 */
int initiateClosingClientQueues(LocalCommConnectionHandle_t* connHandle_p) {
    // TODO: For now client cant wait for draining of queues...so return indication that all are drained.
    int localRC = LCOM_CLOSINGCLIENTQUEUES_CLOSED;

    // Cleanup any remaining elements on the client queues
//    for (LocalCommDataQueueElement* currentQE_p = (LocalCommDataQueueElement*) popEntireUnSerializedStack(&(connHandle_p->clientInboundQ.dataQueue_p->stackHeader_DQEs));
//         currentQE_p != NULL;
//         currentQE_p = (LocalCommDataQueueElement*) currentQE_p->stackElement_header.element_prev_p) {
//        cleanupDataQueueElement(connHandle_p->clientInboundQ.dataQueue_p, currentQE_p);
//    }
//    for (LocalCommDataQueueElement* currentQE_p = (LocalCommDataQueueElement*) popEntireUnSerializedStack(&(connHandle_p->clientOutboundQ.dataQueue_p->stackHeader_DQEs));
//         currentQE_p != NULL;
//         currentQE_p = (LocalCommDataQueueElement*) currentQE_p->stackElement_header.element_prev_p) {
//        cleanupDataQueueElement(connHandle_p->clientOutboundQ.dataQueue_p, currentQE_p);
//    }
//    for (LocalCommWorkQueueElement* currentQE_p = (LocalCommWorkQueueElement*) popEntireUnSerializedStack(&(connHandle_p->clientInboundQ.workQueue_p->stackHeader_WRQEs));
//         currentQE_p != NULL;
//         currentQE_p = (LocalCommWorkQueueElement*) currentQE_p->stackElement_header.element_prev_p) {
//        cleanupWorkQueueElement(connHandle_p->clientInboundQ.workQueue_p, currentQE_p);
//    }

    // All the client queues use the same PLT and Sequence # area.  So, we can update the closing flags at the
    // same time.
    PloCompareAndSwapAreaQuadWord_t swapArea;
    PloStoreAreaQuadWord_t          storeArea1;  // The Inbound Work Queue
    int                             ploRC;

    LocalCommHandle_PLO_Area_t*     localReplacePLO_p = (LocalCommHandle_PLO_Area_t*)&(swapArea.replaceValue);
    LocalCommWRQ_PLO_Area_t*        newInWorkQ_p = (LocalCommWRQ_PLO_Area_t*) &(storeArea1.storeValue);

    swapArea.compare_p = &(connHandle_p->handlePLO_CS);
    memcpy(&(swapArea.expectedValue), swapArea.compare_p, sizeof(swapArea.expectedValue));

    storeArea1.storeLocation_p = &(connHandle_p->clientInboundQ.workQueue_p->wrqPLO_Area);


    do {
        // Build new SwapArea
        memcpy(localReplacePLO_p, &(swapArea.expectedValue), sizeof(*localReplacePLO_p));
        localReplacePLO_p->ploSequenceNumber+=1;

        // Grab current stack info for new StoreAreas (the work queue)
        memcpy(newInWorkQ_p, storeArea1.storeLocation_p, sizeof(storeArea1.storeValue));

        // Mark each client queue as closing.
        localReplacePLO_p->flags.inboundDataQ_closingDataQueue = 1;
        localReplacePLO_p->flags.outboundDataQ_closingDataQueue = 1;
        newInWorkQ_p->flags.closingWorkQueue  = 1;   // Cant do this in the serverAsClient path.

        // Mark each client queue as closed if possible.
        if (connHandle_p->clientInboundQ.dataQueue_p->dataQPLO_header.head_p == NULL) {
            localReplacePLO_p->flags.inboundDataQ_closedDrainedDataQueue = 1;
        }
        if (connHandle_p->clientOutboundQ.dataQueue_p->dataQPLO_header.head_p == NULL) {
            localReplacePLO_p->flags.outboundDataQ_closedDrainedDataQueue = 1;
        }
        if (newInWorkQ_p->wrqHead_p == NULL) {
            newInWorkQ_p->flags.closedDrainedWorkQueue = 1;    // Cant do this in the serverAsClient path.
        }

        ploRC = ploCompareAndSwapAndStoreQuadWord(swapArea.compare_p, &swapArea, &storeArea1);

    } while (ploRC);

    // Wake up any waiting reader (it will see the new state of the queue, drained, and abandon its read).
    releaseWRQ_PET(connHandle_p->clientInboundQ.workQueue_p, LCOM_WRQ_PET_CLOSING_INBOUND);

    return localRC;
}

/**
 * Build and queue a Send data request for the "Blue Queue".
 *
 * @param connHandle_p Pointer to the Connection handle (bbgzlhdl).
 * @param msgCell_p Pointer to the data to send.
 * @return 0 on success, -1 on failure.
 */
int issueSendRequest(LocalCommConnectionHandle_t* connHandle_p, LocalCommDataCell_t * msgCell_p) {
    int localRC = -1;

    // Build send data request
    LocalCommDataQueueElement* sendQE_p = buildSendRequest(connHandle_p);

    if (sendQE_p != NULL) {
        // Finish building send request
        sendQE_p->bbgzlmsg_p = msgCell_p;

        LocalCommDirectionalQueue * sendQ_p = NULL;

        // If we are client-side issuing the send
        if (amIClientSide(connHandle_p)) {
            sendQ_p = &(connHandle_p->clientOutboundQ);

        } else {
            // Server-side issuing a SEND request to client-side
            sendQ_p = &(connHandle_p->clientInboundQ);
        }
        // Add send request to client's Outbound data queue if sending from the client-side Or add to
        // to the client's Inbound data queue if sending from the server-side of the connection.
        int addRC = addToDataQueue(sendQ_p, sendQE_p);

        if (addRC == LOCAL_COMM_ADD_QUEUE_OK) {
            localRC = 0;
        } else {
            // Return error.
        }
    }

    return localRC;
}

/**
 * Get a data from the "Blue Queue".
 *
 * The caller supplies the requestDataLen.  Examine the available data.  Return
 * the pointer to the shared memory of the available data to move.  The
 * queue will be updated to reflect the returned "read" data.  The message element(s)
 * will remain on the data queue until all the data is read from it/them.
 *
 * The caller of this routine will drive freeLastReadData() routine after copying/processing
 * the returned data.  The freeLastReadData() routine will remove the data element(s)
 * from the data queue when all available data represented by the data element(s) has
 * been read.
 *
 * If forceAsync is specified, the read will be forced async even if it could be
 * completed synchronously.  This only applies to the server, and is accomplished by
 * adding a work queue (black queue) element regardless of the state of the data queue.
 *
 * @param connHandle_p Pointer to the Connection handle (bbgzlhdl).
 * @param forceAsync Set to 1 if the request must complete asynchronously
 * @param requestDataLen Requested amount of data to read or 0 for whatever is available.
 * @param returnDataVector_p Pointer to a pointer of a data vector, which this function will
 *                           allocate and fill in describing the data that is available.
 *
 * @return > 0 there is data,  0 read went async, or < 0 read failed.
 */
#define ISSUE_READ_REQUEST_MAX_READ_BLOCKS 10
int issueReadRequest(LocalCommConnectionHandle_t* connHandle_p,
                     unsigned long long forceAsync,
                     unsigned long long requestDataLen,
                     LCOM_AvailableDataVector** returnDataVector_p) {
    int localRC;
    LocalCommDirectionalQueue* readQ_p = NULL;

    // -----------------------------------------------------------------------------
    // Make space on the stack to read ten BBGZLMSG data blocks.  If there are more,
    // we'll have to do another read.  After we're done reading, we'll copy this
    // into another area which we can return to the caller.
    // -----------------------------------------------------------------------------
    struct {
        LCOM_AvailableDataVector dataVector;
        LCOM_ReadDataBlock dataBlockArray[ISSUE_READ_REQUEST_MAX_READ_BLOCKS];
        unsigned char dataBlockNeedToAttach[ISSUE_READ_REQUEST_MAX_READ_BLOCKS];
    } temporaryReadArea __attribute__((aligned(16)));
    memset(&temporaryReadArea, 0, sizeof(temporaryReadArea));

    // -----------------------------------------------------------------------------
    // Retrieve a data reference from the client's Inbound data queue if reading from
    // the client-side, otherwise read from client's Outbound data queue if reading
    // from the server-side of the connection.
    // -----------------------------------------------------------------------------
    int treatAsASyncRead; // Set "Read Pending" flag if no data on server-side reads

    // If we are client-side issuing the read
    if (amIClientSide(connHandle_p)) {
        readQ_p = &(connHandle_p->clientInboundQ);
        treatAsASyncRead = 0;
    } else {
        // Server-side issuing a READ request
        readQ_p = &(connHandle_p->clientOutboundQ);
        treatAsASyncRead = 1;
    }

    // Sets the "Read Pending" Flag if no data.  This is OK for a server read, but not a client.
    // Server calls wants async reads (ie. wants the "Read Pending" set when no data), client
    // will use preview if it wants to wait for data.
    PloCompareAndSwapAreaQuadWord_t swapArea;    // SEQ#, flags, cnt
    PloStoreAreaQuadWord_t          storeArea1;  // The BBGZLMSG.readInfo
    int                             ploRC;

    LocalCommHandle_PLO_Area_t*     localReplacePLO_p = (LocalCommHandle_PLO_Area_t*)&(swapArea.replaceValue);  // SEQ#, flags, cnt
    LocalCommDataQueue*             localDataQueue_p = readQ_p->dataQueue_p;
    LocalCommDataQueueElement*      dataQE_p;
    ReadInfo_PLO_Area_t*            newLMSGReadInfo_p = (ReadInfo_PLO_Area_t*) &(storeArea1.storeValue);

    swapArea.compare_p = &(connHandle_p->handlePLO_CS);
    memcpy(&(swapArea.expectedValue), swapArea.compare_p, sizeof(swapArea.expectedValue));

    unsigned long long              bytesLeftToRead = requestDataLen;

    for (unsigned char keepReading = TRUE; keepReading == TRUE;) {
        // Initialize the block we're currently trying to read.
        int currentReadBlock = temporaryReadArea.dataVector.blockCount;
        memset(&(temporaryReadArea.dataBlockArray[currentReadBlock]), 0, sizeof(LCOM_ReadDataBlock));
        temporaryReadArea.dataBlockNeedToAttach[currentReadBlock] = 0;

        // Build new SwapArea
        memcpy(localReplacePLO_p, &(swapArea.expectedValue), sizeof(*localReplacePLO_p));
        localReplacePLO_p->ploSequenceNumber+=1;

        // Find the first data cell to read from.  If this is our first read attempt, it will
        // be the head.  Otherwise, it will be whatever element (if any) comes next.
        if (currentReadBlock == 0) {
            LocalCommDataQ_PLO_Header_t newDataQHeader = localDataQueue_p->dataQPLO_header;
            dataQE_p = (LocalCommDataQueueElement*)newDataQHeader.head_p;
        } else {
            dataQE_p = (LocalCommDataQueueElement*)dataQE_p->stackElement_header.element_next_p;
        }

        // Is there any data?
        if (dataQE_p) {
            // If we wanted to force an async read, we'll write to the work queue instead of
            // reading from the data queue.
            if (forceAsync == 1L) {
                // Put a  'read ready' queue entry must be placed on the server's inbound work queue
                // (server's inbound work queue), posting the PET if necessary.
                LocalCommWorkQueueElement* readReadyQE_p = buildReadReadyRequest(readQ_p);

                if (readReadyQE_p != NULL) {

                    // Add the Read Ready work request to the work queue and reset pending read flag on the data queue.
                    int addRC = addToWorkQueue(readQ_p, readReadyQE_p);

                    if (addRC != LOCAL_COMM_ADD_QUEUE_OK) {
                        if (TraceActive(trc_level_exception)) {
                            TraceRecord(trc_level_exception,
                                        TP(TP_SERVER_LCOM_QUEUE_ISSUERR_FORCE_ASYNC),
                                        "issueRead, forceAsync add read ready failed",
                                        TRACE_DATA_HEX_INT(addRC, "addRC"),
                                        TRACE_DATA_END_PARMS);
                        }

                        freeLComWorkQueueElement(readQ_p->workQueue_p, readReadyQE_p);
                        localRC = LCOM_ISSUEREAD_RC_FORCE_ASYNC_WORK_FAIL;
                    } else {
                        localRC = LCOM_ISSUEREAD_RC_ASYNC;
                    }
                } else {
                    localRC = LCOM_ISSUEREAD_RC_FORCE_ASYNC_BUILD_FAIL;
                }

                keepReading = FALSE;
            } else {
                // Grab LMSG partial read info
                LocalCommDataCell_t* currentLMSG_p = dataQE_p->bbgzlmsg_p;
                *newLMSGReadInfo_p = currentLMSG_p->readInfo;

                // Check LMSG
                unsigned long long currentDataAvailable = currentLMSG_p->dataAreaSize - newLMSGReadInfo_p->bytesAlreadyRead;
                if (currentDataAvailable > 0) {
                    // TODO: This is currently set up for two kinds of callers:
                    //       1) Servers who always read as much as they can.
                    //       2) Clients who call 'preview' first to get the read data length.
                    // The preview function only checks the first buffer, so it's impossible to
                    // get here with a read length greater than the size of the first block.  When
                    // preview is updated to return the total length available spanning multiple
                    // blocks, the code below will need to be updated.
                    unsigned long long bytesToReadThisRound = (treatAsASyncRead == 1) ? currentDataAvailable : bytesLeftToRead;
                    if (bytesToReadThisRound <= currentDataAvailable) {
                        // Calculate address to data available to caller
                        temporaryReadArea.dataBlockArray[currentReadBlock].data_p =
                            (char*)currentLMSG_p->dataAreaPtr + newLMSGReadInfo_p->bytesAlreadyRead;

                        // Check the LMSG to see if it needs special handling.
                        if (newLMSGReadInfo_p->flags.dataAreaNotInCellpools && (newLMSGReadInfo_p->flags.readerObtainedAccess == 0)) {
                            newLMSGReadInfo_p->flags.readerObtainedAccess = 1;
                            temporaryReadArea.dataBlockNeedToAttach[currentReadBlock] = 1;
                        }

                        // Adjust the bytes read
                        newLMSGReadInfo_p->bytesAlreadyRead = newLMSGReadInfo_p->bytesAlreadyRead + bytesToReadThisRound;

                        // Return "there's data"
                        localRC = LCOM_ISSUEREAD_RC_DATATOMOVE;

                        storeArea1.storeLocation_p = &(currentLMSG_p->readInfo);
                        ploRC = ploCompareAndSwapAndStoreQuadWord(swapArea.compare_p, &swapArea, &storeArea1);

                        if (ploRC == 0) {
                            temporaryReadArea.dataBlockArray[currentReadBlock].dataCellPointer_p = currentLMSG_p;
                            temporaryReadArea.dataBlockArray[currentReadBlock].dataSize = bytesToReadThisRound;
                            temporaryReadArea.dataVector.blockCount += 1;
                            temporaryReadArea.dataVector.totalDataSize += bytesToReadThisRound;
                            swapArea.expectedValue = swapArea.replaceValue;

                            // Stop reading if we're out of room in our temporary vector.
                            if (temporaryReadArea.dataVector.blockCount == ISSUE_READ_REQUEST_MAX_READ_BLOCKS) {
                                keepReading = FALSE;
                            }
                        } else if (temporaryReadArea.dataVector.blockCount > 0) {
                            // If we've got something, just go with that.
                            keepReading = FALSE;
                        }
                    } else {
                        localRC = LCOM_ISSUEREAD_RC_BADPARM_REQUESTDATALEN;
                        keepReading = FALSE;
                    }
                } else {
                    // All data from the data queue elment was read, but it wasn't removed from the queue prior
                    // to driving a subsequent read.  Bad..bad..client...hopefully.
                    localRC = LCOM_ISSUEREAD_RC_DATAQ_INVALIDSTATE;
                    keepReading = FALSE;
                }
            }
        } else {
            // If we already read something, then this is fine, just go with that.
            if (temporaryReadArea.dataVector.blockCount == 0) {
                // Turn on the "Read Pending" flag for Server-side reads.  (Clients don't do async reads).
                if (treatAsASyncRead) {
                    // "Outbound" data queue for server read.  Set the "Read Pending" flag.
                    localReplacePLO_p->flags.outboundDataQ_readPending = 1;
                    ploRC = ploCompareAndSwapQuadWord(swapArea.compare_p, &swapArea);

                    if (ploRC == 0) {
                        localRC = LCOM_ISSUEREAD_RC_ASYNC;
                        keepReading = FALSE;
                    }
                } else {
                    // Client issued a read with no data available...Client should always PREVIEW before read. So, this is an error.
                    localRC = LCOM_ISSUEREAD_RC_CLIENT_NODATA;
                    keepReading = FALSE;
                }
            } else {
                keepReading = FALSE;
            }
        }
    }

    // If we found data, see if we need to Attach to a LMSG's shared memory outside of the LDAT pools
    if (localRC == LCOM_ISSUEREAD_RC_DATATOMOVE) {
        for (int x = 0; x < temporaryReadArea.dataVector.blockCount; x++) {
            if (temporaryReadArea.dataBlockNeedToAttach[x]) {
                int rc, rsn;
                LocalCommDataCell_t* currentLMSG_p = (LocalCommDataCell_t*)(temporaryReadArea.dataBlockArray[x].dataCellPointer_p);
                void * sharedMem_p = (void*) ((char*)(currentLMSG_p->dataAreaPtr)-sizeof(LargeDataMessageHeader_t));
                rc = accessSharedAboveConditional(sharedMem_p, currentLMSG_p->largeData.sharingUserToken, &rsn);
                if (rc != 0) {
                    localRC = LCOM_ISSUEREAD_RC_FAILED_SHR_ACCESS;
                }

                // The reader will detach from "this" piece of data after consuming it.
            }
        }
    }

    // Set output parms.
    if (localRC > 0) {
        *returnDataVector_p = malloc((sizeof(LCOM_AvailableDataVector)) +
                                     (sizeof(LCOM_ReadDataBlock) * temporaryReadArea.dataVector.blockCount));
        memcpy(*returnDataVector_p, &(temporaryReadArea.dataVector), sizeof(LCOM_AvailableDataVector));
        for (int x = 0; x < temporaryReadArea.dataVector.blockCount; x++) {
            LCOM_ReadDataBlock* cur_p = ((LCOM_ReadDataBlock*)((*returnDataVector_p) + 1)) + x;
            memcpy(cur_p, &(temporaryReadArea.dataBlockArray[x]), sizeof(LCOM_ReadDataBlock));
        }
    } else {
        *returnDataVector_p = NULL;
    }

    return localRC;
}   // issueReadRequest


/**
 * Preview data request on the "Blue Queue".
 *
 * @param connHandle_p Pointer to the Connection handle (bbgzlhdl).
 * @param waitForData Non-zero indicates that we will wait until data arrives.
 * @param timeToWait A maximum amount of seconds to wait for data to arrive. A zero value indicates to wait indefinitely.
 * @param dataLen_p Pointer to field to contain the available bytes to be read.
 * @return LCOM_ISSUEPREVIEW_RC_OK if all went well,
 * LCOM_ISSUEPREVIEW_RC_CLOSERECEIVED if the connection was marked for closing while we were waiting for data,
 * LCOM_ISSUEPREVIEW_RC_ERROR if we received an unknown return code from timedWaitOnWorkQueue while waiting for data,
 * LCOM_ISSUEPREVIEW_RC_TIMEDOUT if we timed out while waiting for data.
 *
 * TODO: currently returning a single queue element worth of data length. In future may support list.
 */
int issuePreviewRequest(LocalCommConnectionHandle_t* connHandle_p,
                        unsigned char waitForData,
                        int timeToWait,
                        unsigned long long* dataLen_p) {

    int localRC = LCOM_ISSUEPREVIEW_RC_OK;
    LocalCommDirectionalQueue* readQ_p = NULL;
    unsigned long long dataAvailable = 0;

    // -----------------------------------------------------------------------------
    // Examine a data element from the client's Inbound data queue if reading from
    // the client-side, otherwise read from client's Outbound data queue if reading
    // from the server-side of the connection.
    // -----------------------------------------------------------------------------

    // If we are client-side issuing the read
    if (amIClientSide(connHandle_p)) {
        readQ_p = &(connHandle_p->clientInboundQ);
    } else {
        // Server-side issuing a READ request
        readQ_p = &(connHandle_p->clientOutboundQ);
    }

    // Get the data available to read
    while (dataAvailable == 0) {

        // Check on data, set the "Read Pending" indicator if we plan to wait for data below.
        dataAvailable = dataAvailableOnDataQueue(readQ_p, waitForData);

        // Should we wait for Data?
        if ((waitForData != 0) &&
            (dataAvailable == 0)) {

            // Wait for data to arrive
            int waitRC = timedWaitOnWorkQueue(readQ_p, timeToWait);

            if (waitRC == LCOM_TIMEDWAITONWORKQ_RC_DATA_ARRIVED) {
                continue;
            } else if (waitRC == LCOM_TIMEDWAITONWORKQ_TIMEDOUT) {
                localRC = LCOM_ISSUEPREVIEW_RC_TIMEDOUT;
                break;
            } else if (waitRC == LCOM_TIMEDWAITONWORKQ_RC_CLOSERECEIVED) {
                localRC = LCOM_ISSUEPREVIEW_RC_CLOSERECEIVED;
                break;
            }  else if (waitRC == LCOM_TIMEDWAITONWORKQ_BADQSTATE) {
                localRC = LCOM_ISSUEPREVIEW_RC_BADSTATE;
                break;
            } else {
                localRC = LCOM_ISSUEPREVIEW_RC_ERROR;
                break;
            }
        } else {
            // Done.
            localRC = LCOM_ISSUEPREVIEW_RC_OK;
            break;
        }
        // loop back around.
    }

    *dataLen_p = dataAvailable;

    return localRC;
}   // issuePreviewRequest

void initializeWRQFlags(LocalCommWorkQueue* localWorkQueuePtr) {

    PloCompareAndSwapAreaQuadWord_t swapArea;
    PloStoreAreaQuadWord_t          storeArea1;  // The Inbound Work Queue
    int                             ploRC;

    LocalCommHandle_PLO_Area_t*     localReplacePLO_p = (LocalCommHandle_PLO_Area_t*)&(swapArea.replaceValue);
    LocalCommWRQ_PLO_Area_t*        newInWorkQ_p = (LocalCommWRQ_PLO_Area_t*) &(storeArea1.storeValue);

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(TP_SERVER_LCOM_QUEUE_INIT_WRQ_FLAGS_ENTRY),
                    "initializeWRQFlags, entry",
                    TRACE_DATA_PTR(localWorkQueuePtr, "Work Queue Ptr"),
                    TRACE_DATA_END_PARMS);
    }

    swapArea.compare_p = localWorkQueuePtr->wrqPLO_CS_p;
    memcpy(&(swapArea.expectedValue), swapArea.compare_p, sizeof(swapArea.expectedValue));

    storeArea1.storeLocation_p = &(localWorkQueuePtr->wrqPLO_Area);

    do {
        // Build new SwapArea
        memcpy(localReplacePLO_p, &(swapArea.expectedValue), sizeof(*localReplacePLO_p));
        localReplacePLO_p->ploSequenceNumber+=1;

        // Grab current stack info for new StoreAreas (the work queue)
        memcpy(newInWorkQ_p, storeArea1.storeLocation_p, sizeof(storeArea1.storeValue));

        // Mark the queue as open once again
        newInWorkQ_p->flags.closingWorkQueue = 0;
        newInWorkQ_p->flags.closedDrainedWorkQueue = 0;

        ploRC = ploCompareAndSwapAndStoreQuadWord(swapArea.compare_p, &swapArea, &storeArea1);

    } while (ploRC);

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(TP_SERVER_LCOM_QUEUE_INIT_WRQ_FLAGS_EXIT),
                    "initializeWRQFlags, exit",
                    TRACE_DATA_END_PARMS);
    }
}

#pragma insert_asm(" CVT DSECT=YES")

