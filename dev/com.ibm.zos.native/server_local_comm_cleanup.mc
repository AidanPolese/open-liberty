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

#include "include/ieantc.h"
#include "include/mvs_abend.h"
#include "include/mvs_cell_pool_services.h"
#include "include/mvs_enq.h"
#include "include/mvs_estae.h"
#include "include/mvs_iarv64.h"
#include "include/mvs_plo.h"
#include "include/mvs_user_token_manager.h"

#include "include/server_local_comm_cleanup.h"
#include "include/server_local_comm_client.h"
#include "include/server_local_comm_footprint.h"

#include "include/server_local_comm_queue.h"

#include "include/server_process_data.h"
#include "include/stack_services.h"
#include "include/ras_tracing.h"


#define RAS_MODULE_CONST  RAS_MODULE_SERVER_LCOM_CLEANUP


#define TP_SERVER_LCOM_QUEUE_REGCC_QFAIL                       1
#define TP_SERVER_LCOM_QUEUE_DREGCC_DQOK                       2
#define TP_SERVER_LCOM_QUEUE_DREGCC_DQFAIL                     3
#define TP_SERVER_LCOM_CLIENT_CLEANUPSERVERLOCL_NMTKN_FAILED   4
#define TP_SERVER_LCOM_CLIENT_CLEANUPSERVERLOCL_SLD            5
#define TP_SERVER_LCOM_CLIENT_CLEANUPSERVERLOCL_ENQR           6
#define TP_SERVER_LCOM_CLIENT_CLEANUPSERVERLOCL_EXIT           7

/**
 * Client Cleanup Queue related methods.
 *
 * A Client cleanup queue is maintained for the purpose of cleaning up client
 * resources related to a server that terminated abnormally.
 *
 * An entry is added to the client cleanup queue when the server attaches to a
 * client's set of Shared Memory Objects for local comm functions.
 *
 * An entry is removed when the Local Comm Channel comm advises that the last
 * local comm connection has closed between a client and server pairing.
 *
 * This queue is "processed" in the server during termination.  Normal termination
 * would have shutdown the channel and thus driven the disconnect local comm
 * which drives the removal of a client cleanup element.   If any elements are
 * found on the client cleanup queue during the serverAuthorizedProcessCleanup
 * then resources related to the client/server pairing are cleaned up; this entails
 * closing connections, releasing waiting client threads, releasing related storage,
 * and detaching from the Shared Memory objects.
 *
 */

/**
 * Add the input client cleanup queue element to the queue.
 *
 * @param targetQueue Pointer to the client cleanup queue.
 * @param newElement Pointer the client cleanup queue element to add to the queue.
 * @return 0.
 */
static int ccq_enqueue(LocalCommClientCleanupQueue_t* targetQueue, LocalCommClientCleanupQueueElement_t* newElement) {

    PloCompareAndSwapAreaQuadWord_t swapArea;
    PloStoreAreaQuadWord_t          storeArea1;  // The Header
    PloStoreAreaQuadWord_t          storeArea2;  // The Last_Element->Next or not used
    ElementDT                       header;
    int                             ploRC;

    LocalCommClientCleanup_PLO_CS_Area_t  localReplacePLO;

    swapArea.compare_p     = &(targetQueue->ploCS_Area);
    memcpy(&(swapArea.expectedValue), swapArea.compare_p, sizeof(swapArea.expectedValue));

    // Set the new header as target update area.
    storeArea1.storeLocation_p =  &(targetQueue->queueHeader);

    do {
        // Build new SwapArea
        memcpy(&localReplacePLO, &(swapArea.expectedValue), sizeof(localReplacePLO));
        localReplacePLO.ploSequenceNumber+=1;
        memcpy(&(swapArea.replaceValue), &localReplacePLO, sizeof(swapArea.replaceValue));

        // Grab current queue header info
        memcpy(&(header), &(targetQueue->queueHeader), sizeof(header));

        // If the queue is currently empty...
        if (header.element_next_p == NULL) {
            clearPtrs(&(newElement->queueElement));

            // Set the head and tail to this element
            header.element_next_p = (ElementDT*) newElement;
            header.element_prev_p = (ElementDT*) newElement;

            // Set the new header.
            memcpy(&(storeArea1.storeValue), &header, sizeof(storeArea1.storeValue));

            ploRC = ploCompareAndSwapAndStoreQuadWord(swapArea.compare_p, &swapArea, &storeArea1);
        }
        else {
            // Add the new element to the end of the list
            newElement->queueElement.element_next_p = NULL;                        // next = null
            newElement->queueElement.element_prev_p = header.element_prev_p;       // prev = tail

            // Set the current tail->next = new element
            storeArea2.storeLocation_p = &(((LocalCommClientCleanupQueueElement_t*)(header.element_prev_p))->queueElement.element_next_p);
            memcpy(&(storeArea2.storeValue), storeArea2.storeLocation_p, sizeof(storeArea2.storeValue));
            ((ElementDT*) &(storeArea2.storeValue))->element_next_p = (ElementDT*) newElement;

            // Set the tail = new element
            header.element_prev_p = (ElementDT*) newElement;
            memcpy(&(storeArea1.storeValue), &header, sizeof(storeArea1.storeValue));

            ploRC = ploCompareAndSwapAndDoubleStoreQuadWord(swapArea.compare_p, &swapArea, &storeArea1, &storeArea2);
        }

    } while (ploRC);

    return 0;
}   // end, ccq_enqueue

static int ccq_dequeue(LocalCommClientCleanupQueue_t* targetQueue_p, LocalCommClientCleanupQueueElement_t* targetElement_p) {

    PloCompareAndSwapAreaQuadWord_t swapArea;
    PloStoreAreaQuadWord_t          storeArea1;  // The Header or Previous_Element->Next
    PloStoreAreaQuadWord_t          storeArea2;  // The Next_Element->Prev or Previous_Element->Next or not used
    ElementDT                       header;
    int                             ploRC;

    LocalCommClientCleanup_PLO_CS_Area_t localReplacePLO;

    swapArea.compare_p     = &(targetQueue_p->ploCS_Area);
    memcpy(&(swapArea.expectedValue), swapArea.compare_p, sizeof(swapArea.expectedValue));

    do {
        // Build new SwapArea
        memcpy(&localReplacePLO, &(swapArea.expectedValue), sizeof(localReplacePLO));
        localReplacePLO.ploSequenceNumber+=1;
        memcpy(&(swapArea.replaceValue), &localReplacePLO, sizeof(swapArea.replaceValue));

        // Grab current queue header info
        memcpy(&(header), &(targetQueue_p->queueHeader), sizeof(header));

        // Check if the target element is the only element in the queue (if so, we can use the CompareSwap and single store)
        if ((header.element_next_p == header.element_prev_p) && (header.element_prev_p == (ElementDT*) targetElement_p)) {
            // Set the queue to be empty.
            storeArea1.storeLocation_p = &(targetQueue_p->queueHeader);
            memset(&(storeArea1.storeValue), 0, sizeof(storeArea1.storeValue));

            ploRC = ploCompareAndSwapAndStoreQuadWord(swapArea.compare_p, &swapArea, &storeArea1);
        }
        else {
            // We'll need to use the CompareSwap and double store for the other cases.

            // If the target element is the FIRST but not only
            if (header.element_next_p == (ElementDT*) targetElement_p) {
                // Set the head = target.next
                header.element_next_p = targetElement_p->queueElement.element_next_p;
                storeArea1.storeLocation_p = &(targetQueue_p->queueHeader);
                memcpy(&(storeArea1.storeValue), &header, sizeof(storeArea1.storeValue));

                // Zero the next elements previous pointer
                storeArea2.storeLocation_p = &(targetElement_p->queueElement);
                memcpy(&(storeArea2.storeValue), storeArea2.storeLocation_p, sizeof(storeArea2.storeValue));
                ((ElementDT*) &(storeArea2.storeValue))->element_prev_p = NULL;
            } else if (header.element_prev_p == (ElementDT*) targetElement_p) {
                // else if its the LAST element, but not only

                // Set the tail to target element's previous
                header.element_prev_p = targetElement_p->queueElement.element_prev_p;
                storeArea1.storeLocation_p = &(targetQueue_p->queueHeader);
                memcpy(&(storeArea1.storeValue), &header, sizeof(storeArea1.storeValue));

                // Zero the previous element's next pointer
                storeArea2.storeLocation_p = &(((LocalCommClientCleanupQueueElement_t*)(targetElement_p->queueElement.element_prev_p))->queueElement);
                memcpy(&(storeArea2.storeValue), storeArea2.storeLocation_p, sizeof(storeArea2.storeValue));
                ((ElementDT*) &(storeArea2.storeValue))->element_next_p = NULL;
            } else {
                // its a MIDDLE element, not either a head or tail

                // Set the previous element's next pointer = targetElement.next
                storeArea1.storeLocation_p = &(((LocalCommClientCleanupQueueElement_t*)(targetElement_p->queueElement.element_prev_p))->queueElement);
                memcpy(&(storeArea1.storeValue), storeArea1.storeLocation_p, sizeof(storeArea1.storeValue));
                ((ElementDT*) &(storeArea1.storeValue))->element_next_p = targetElement_p->queueElement.element_next_p;

                // Set the next element's previous pointer = targetElement.prev
                storeArea2.storeLocation_p = &(((LocalCommClientCleanupQueueElement_t*)(targetElement_p->queueElement.element_next_p))->queueElement);
                memcpy(&(storeArea2.storeValue), storeArea2.storeLocation_p, sizeof(storeArea2.storeValue));
                ((ElementDT*) &(storeArea2.storeValue))->element_prev_p = targetElement_p->queueElement.element_prev_p;
            }

            ploRC = ploCompareAndSwapAndDoubleStoreQuadWord(swapArea.compare_p, &swapArea, &storeArea1, &storeArea2);
        }

    } while (ploRC);

    clearPtrs(&(targetElement_p->queueElement));

    return 0;
}   // end, ccq_dequeue

static LocalCommClientCleanupQueueElement_t* ccq_findElement(LocalCommClientCleanupQueue_t* targetQueue_p, LocalCommClientAnchor_t* targetClientLOCL_p) {
    LocalCommClientCleanupQueueElement_t* returnElement_p;


    PloCompareAndSwapAreaQuadWord_t swapArea;
    int                             ploRC;

    LocalCommClientCleanup_PLO_CS_Area_t localReplacePLO;

    swapArea.compare_p     = &(targetQueue_p->ploCS_Area);
    memcpy(&(swapArea.expectedValue), swapArea.compare_p, sizeof(swapArea.expectedValue));

    do {
        returnElement_p = NULL;

        // Build new SwapArea
        memcpy(&localReplacePLO, &(swapArea.expectedValue), sizeof(localReplacePLO));
        localReplacePLO.ploSequenceNumber+=1;
        memcpy(&(swapArea.replaceValue), &localReplacePLO, sizeof(swapArea.replaceValue));

        // Scan queue for target element
        for (LocalCommClientCleanupQueueElement_t* currentElement_p = (LocalCommClientCleanupQueueElement_t*) targetQueue_p->queueHeader.head_p;
            currentElement_p != NULL;
            currentElement_p = (LocalCommClientCleanupQueueElement_t*) currentElement_p->queueElement.element_next_p) {
            if (currentElement_p->inClientBBGZLOCL_p == targetClientLOCL_p) {
                returnElement_p = currentElement_p;
                break;
            }
        }

        ploRC = ploCompareAndSwapQuadWord(swapArea.compare_p, &swapArea);
    } while (ploRC);

    return returnElement_p;
}   // end, ccq_findElement


static LocalCommClientCleanupQueueElement_t* ccq_getQueueElement(LocalCommClientCleanupQueue_t* targetQueue) {
    LocalCommClientCleanupQueueElement_t* returnElement_p;

    // Pop a pooled element from the free pool or allocate a new one.
    PloCompareAndSwapAreaQuadWord_t swapArea;
    PloStoreAreaQuadWord_t          storeArea1;               // The QueueElementPool
    LocalCommClientCleanupQueueElementPool_t localQueuePool;
    int                             ploRC;

    LocalCommClientCleanup_PLO_CS_Area_t  localReplacePLO;

    swapArea.compare_p     = &(targetQueue->ploCS_Area);
    memcpy(&(swapArea.expectedValue), swapArea.compare_p, sizeof(swapArea.expectedValue));

    do {
        returnElement_p = NULL;

        // Build new SwapArea
        memcpy(&localReplacePLO, &(swapArea.expectedValue), sizeof(localReplacePLO));
        localReplacePLO.ploSequenceNumber+=1;
        memcpy(&(swapArea.replaceValue), &localReplacePLO, sizeof(swapArea.replaceValue));

        // Grab current queue element pool info
        memcpy(&(localQueuePool), &(targetQueue->queueElementPool), sizeof(localQueuePool));

        // If the queue is currently empty...
        if (localQueuePool.queueElementPool_p == NULL) {
            // Return NULL -- need to fluff one up.
            ploRC = ploCompareAndSwapQuadWord(swapArea.compare_p, &swapArea);
        }
        else {
            // Set the queue element pool as target update area.
            storeArea1.storeLocation_p = &(targetQueue->queueElementPool);

            // Update the QueueElementPool to remove the first element
            localQueuePool.queueElementPoolCnt--;
            returnElement_p = localQueuePool.queueElementPool_p;
            localQueuePool.queueElementPool_p = (LocalCommClientCleanupQueueElement_t*) returnElement_p->queueElement.element_next_p;

            // Set the placement area for the PLO
            memcpy(&(storeArea1.storeValue), &localQueuePool, sizeof(storeArea1.storeValue));

            ploRC = ploCompareAndSwapAndStoreQuadWord(swapArea.compare_p, &swapArea, &storeArea1);
        }

    } while (ploRC);

    if (returnElement_p == NULL) {
        returnElement_p = malloc(sizeof(*returnElement_p));
    }

    return returnElement_p;
}   // end, ccq_getQueueElement

static int ccq_freeQueueElement(LocalCommClientCleanupQueue_t* targetQueue, LocalCommClientCleanupQueueElement_t* inElement) {
    // Push a client cleanup queue element to the free pool.
    PloCompareAndSwapAreaQuadWord_t swapArea;
    PloStoreAreaQuadWord_t          storeArea1;               // The QueueElementPool
    LocalCommClientCleanupQueueElementPool_t localQueuePool;
    int                             ploRC;

    LocalCommClientCleanup_PLO_CS_Area_t  localReplacePLO;

    swapArea.compare_p     = &(targetQueue->ploCS_Area);
    memcpy(&(swapArea.expectedValue), swapArea.compare_p, sizeof(swapArea.expectedValue));

    // Not using the prev pointer while in the free pool
    inElement->queueElement.element_prev_p = NULL;

    do {
        // Build new SwapArea
        memcpy(&localReplacePLO, &(swapArea.expectedValue), sizeof(localReplacePLO));
        localReplacePLO.ploSequenceNumber+=1;
        memcpy(&(swapArea.replaceValue), &localReplacePLO, sizeof(swapArea.replaceValue));

        // Grab current queue element pool info
        memcpy(&(localQueuePool), &(targetQueue->queueElementPool), sizeof(localQueuePool));

        // Set the queue element pool as target update area.
        storeArea1.storeLocation_p = &(targetQueue->queueElementPool);

        // Update the QueueElementPool to add to front
        localQueuePool.queueElementPoolCnt++;
        inElement->queueElement.element_next_p = (ElementDT*) localQueuePool.queueElementPool_p;
        localQueuePool.queueElementPool_p      = inElement;

        // Set the placement area for the PLO
        memcpy(&(storeArea1.storeValue), &localQueuePool, sizeof(storeArea1.storeValue));

        ploRC = ploCompareAndSwapAndStoreQuadWord(swapArea.compare_p, &swapArea, &storeArea1);

    } while (ploRC);

    return 0;
}   // end, ccq_freeQueueElement

/**
 * Build and queue a Client cleanup element.
 *
 * Use to cleanup client related resources during an abnormal server end.  More
 * specifically, during the task-level resmgr for the IPT task of the server;  We
 * will drive cleanup activities for the associated client's using the information
 * in the queue elements.
 *
 * @param serverLOCL_p Server's LOCL pointer.
 * @param inClientBBGZLOCL_p Client's LOCL pointer.
 * @param inClientBBGZLDAT_p Client's LDAT pointer.
 * @param inClientBBGZLSCL_p Client's LSCL pointer.
 * @return 0 if all went well, 8 if we couldn't obtain a new queue element, 12 if we failed.
 * to queue the new element.
 */
int registerClientCleanup(LocalCommClientAnchor_t*     serverLOCL_p,
                          LocalCommClientAnchor_t*     inClientBBGZLOCL_p,
                          LocalCommClientDataStore_t*  inClientBBGZLDAT_p,
                          LocalCommClientServerPair_t* inClientBBGZLSCL_p) {

    int localRC = LCOM_REGCLIENTCLEANUP_RC_OK;

    LocalCommClientCleanupQueue_t* clientCleanQ_p = &(serverLOCL_p->serverSpecificInfo.serverClientCleanupQueue);

    // Get a queue element
    LocalCommClientCleanupQueueElement_t* newElement_p = ccq_getQueueElement(clientCleanQ_p);

    if (newElement_p != NULL) {

        // Fill it in
        clearPtrs(&newElement_p->queueElement);
        memcpy(newElement_p->eyecatcher, SERVER_LCOM_CCQE_EYECATCHER_INUSE, sizeof(newElement_p->eyecatcher));

        newElement_p->inClientBBGZLOCL_p = inClientBBGZLOCL_p;
        newElement_p->inClientBBGZLDAT_p = inClientBBGZLDAT_p;
        newElement_p->inClientBBGZLSCL_p = inClientBBGZLSCL_p;

        __stck(&(newElement_p->createStck));

        // Queue it.
        int qRC = ccq_enqueue(clientCleanQ_p, newElement_p);

        if (qRC != 0) {
            if (TraceActive(trc_level_exception)) {
                TraceRecord(trc_level_exception,
                            TP(TP_SERVER_LCOM_QUEUE_REGCC_QFAIL),
                            "registerClientCleanup, failed to queue",
                            TRACE_DATA_HEX_INT(qRC, "ccq_enqueue rc"),
                            TRACE_DATA_END_PARMS);
            }

            localRC = LCOM_REGCLIENTCLEANUP_RC_QUEFAILED;
        }
    } else {
        localRC = LCOM_REGCLIENTCLEANUP_RC_NO_ELEM;
    }

    return localRC;
}   // end, registerClientCleanup

/**
 * Remove a client cleanup queue element from the Server's queue.
 * @param serverLOCL_p Server's LOCL pointer.
 * @param inClientBBGZLOCL_p Client's LOCL pointer.
 * @param inClientBBGZLDAT_p Client's LDAT pointer.
 * @param inClientBBGZLSCL_p Client's LSCL pointer.
 * @return 0 all went well, 8 queue element not found, 12 dequeue failed.
 */
int deregisterClientCleanup(LocalCommClientAnchor_t*     serverLOCL_p,
                            LocalCommClientAnchor_t*     inClientBBGZLOCL_p,
                            LocalCommClientDataStore_t*  inClientBBGZLDAT_p,
                            LocalCommClientServerPair_t* inClientBBGZLSCL_p) {

    int localRC = LCOM_REGCLIENTCLEANUP_RC_OK;

    LocalCommClientCleanupQueue_t* clientCleanQ_p = &(serverLOCL_p->serverSpecificInfo.serverClientCleanupQueue);

    // Find the cleanup queue element
    LocalCommClientCleanupQueueElement_t* targetElement_p = ccq_findElement(clientCleanQ_p, inClientBBGZLOCL_p);

    if (targetElement_p != NULL) {

        // Queue it.
        int dqRC = ccq_dequeue(clientCleanQ_p, targetElement_p);

        if (dqRC == 0) {
            if (TraceActive(trc_level_detailed)) {
                TraceRecord(trc_level_detailed,
                            TP(TP_SERVER_LCOM_QUEUE_DREGCC_DQOK),
                            "deregisterClientCleanup, dequeued",
                            TRACE_DATA_RAWDATA(sizeof(*targetElement_p),
                                               targetElement_p,
                                               "targetElement"),
                            TRACE_DATA_END_PARMS);
            }

            // Free the element (put into pool)
            ccq_freeQueueElement(clientCleanQ_p, targetElement_p);
        } else {
            if (TraceActive(trc_level_exception)) {
                TraceRecord(trc_level_exception,
                            TP(TP_SERVER_LCOM_QUEUE_DREGCC_DQFAIL),
                            "deregisterClientCleanup, failed to dequeue",
                            TRACE_DATA_HEX_INT(dqRC, "ccq_dequeue rc"),
                            TRACE_DATA_END_PARMS);
            }

            localRC = LCOM_DREGCLIENTCLEANUP_RC_DQUEFAILED;
        }
    } else {
        localRC = LCOM_DREGCLIENTCLEANUP_RC_NO_ELEM;
    }

    return localRC;
}   // end, deregisterClientCleanup


/*********************************************************************************************/
/* Cleanup Routines.                                                                         */
/*********************************************************************************************/


/**
 * Common Server LOCL cleanup routine.
 *
 * Called during normal server termination from the ntv_uninit path.  Also,
 * called from abnormal termination from the in server_authorized_function_module's
 * serverAuthorizedProcessCleanup routine.
 *
 * @param outRSN Pointer to an integer to contain the reason code.
 * @return 0 if successful, -1 if something out of the ordinary occurred.
 */
int cleanupServerLOCL(int* outRSN) {
    int localRC, localRSN;
    // -----------------------------------------------------------------------
    // Establish some recovery in case of an abend.
    // -----------------------------------------------------------------------
    int estaex_rc  = -1;
    int estaex_rsn = -1;
    volatile struct {
                 int tryToSetESTAE : 1,
                     setESTAE : 1,
                     abendedAndRetried: 1,
                     haltRecovery : 1,

                     tryToObtainLOCL: 1,
                     obtainedLOCL: 1,
                     tryToReleaseLOCL_Nametoken : 1,
                     releasedLOCL_Nametoken : 1,

                     failedReleasingLOCL_NameToken : 1,
                     tryToReleaseENQ : 1,
                     releasedENQ : 1,
                     setShutdownFlags : 1,

                     sharedDetachFailed: 1,
                     systemAffinityDetachFailed : 1,
                     detachedFromSharedMem : 1,
                     _available : 17;
    } retryFootprints;


    memset((void*)&retryFootprints, 0, sizeof(retryFootprints));
    struct retry_parms retryParms;
    memset(&retryParms, 0, sizeof(retryParms));

    // ----------------------------------------------------------------
    // Uninitialize:
    //   -Drop LOCL nametoken
    //   -Drop the ENQ obtained that allows new clients to find us
    //   -Unhook the BBGZLOCL structure from the PGOO
    //      -Mark it as shutting down, mark workq
    //      -detach from it (including System Affinity)
    //
    // ----------------------------------------------------------------
    SET_RETRY_POINT(retryParms);
    if (retryFootprints.tryToSetESTAE == 0) {
        retryFootprints.tryToSetESTAE = 1;
        establish_estaex_with_retry(&retryParms, &estaex_rc, &estaex_rsn);
        retryFootprints.setESTAE = (estaex_rc == 0);
    }

    if (retryFootprints.setESTAE == 1) {
        LocalCommClientAnchor_t* bbgzlocl_p = NULL;

        // -----------------------------------------------------------------------
        // Delete the nametoken for our LOCL
        // -----------------------------------------------------------------------
        SET_RETRY_POINT(retryParms);
        if (retryFootprints.tryToReleaseLOCL_Nametoken == 0) {
            retryFootprints.tryToReleaseLOCL_Nametoken = 1;

            struct {
                unsigned char name[8];
                unsigned long long nulls;
            } nameTokenName;

            memcpy(nameTokenName.name, BBGZLOCL_NAMETOKEN_NAME, sizeof(nameTokenName.name));
            nameTokenName.nulls = 0L;

            int nameTokenReturnCode = 0;
            iean4dl(IEANT_HOME_LEVEL, (char*) &nameTokenName, &nameTokenReturnCode);
            if (nameTokenReturnCode == IEANT_OK) {
                retryFootprints.releasedLOCL_Nametoken = 1;
            } else {
                retryFootprints.failedReleasingLOCL_NameToken = 1;

                if (TraceActive(trc_level_exception)) {
                    TraceRecord(trc_level_exception,
                                TP(TP_SERVER_LCOM_CLIENT_CLEANUPSERVERLOCL_NMTKN_FAILED),
                                "cleanupServerLOCL, failed LOCL nametoken delete",
                                TRACE_DATA_RAWDATA(sizeof(nameTokenName),
                                                   &nameTokenName,
                                                   "nameTokenName"),
                                TRACE_DATA_HEX_INT(nameTokenReturnCode,
                                                   "nameTokenReturnCode"),
                                TRACE_DATA_END_PARMS);
                }
            }
        } else {
            retryFootprints.abendedAndRetried = 1;
            retryFootprints.failedReleasingLOCL_NameToken = 1;
        }


        // -----------------------------------------------------------------------
        // Unhook LOCL from the server_process_data
        // -----------------------------------------------------------------------
        SET_RETRY_POINT(retryParms);
        if (retryFootprints.tryToObtainLOCL == 0) {
            retryFootprints.tryToObtainLOCL = 1;

            // Get PGOO
            server_process_data* spd_p = getServerProcessData();
            if (spd_p == NULL) {
                localRC  = -1;
                localRSN = LCOM_CLEANUPSERVERLOCL_RSN_NO_SPD;

                retryFootprints.haltRecovery = 1;
            } else {
                // Get BBGZLCOM anchored from PGOO
                bbgzlocl_p = (LocalCommClientAnchor_t*) spd_p->lcom_BBGZLOCL_p;
                long long newValue = 0;
                if (__cds1(&bbgzlocl_p, &(spd_p->lcom_BBGZLOCL_p), &newValue) != 0) {
                    localRC  = -1;
                    localRSN = LCOM_CLEANUPSERVERLOCL_RSN_BBGZLOCL_BUSY;

                    retryFootprints.haltRecovery = 1;
                } else {
                    if (bbgzlocl_p == NULL) {
                        localRC  = -1;
                        localRSN = LCOM_CLEANUPSERVERLOCL_RSN_NO_BBGZLOCL;

                        retryFootprints.haltRecovery = 1;
                    } else {
                        retryFootprints.obtainedLOCL = 1;

                        if (TraceActive(trc_level_detailed)) {
                            TraceRecord(trc_level_detailed,
                                        TP(TP_SERVER_LCOM_CLIENT_CLEANUPSERVERLOCL_SLD),
                                        "cleanupServerLOCL, BBGZLCOM before cleanup",
                                        TRACE_DATA_RAWDATA(sizeof(*bbgzlocl_p),
                                                           bbgzlocl_p,
                                                           "BBGZLOCL"),
                                        TRACE_DATA_END_PARMS);
                        }
                    }
                }
            }
        } else {
            // Abended and retried
            retryFootprints.abendedAndRetried = 1;
            retryFootprints.haltRecovery = 1;
        }

        // ----------------------------------------------------------------
        // -Drop the ENQ obtained that allows new clients to find us
        // -Mark LOCL as shutting down and mark the LOCL's workq for no more work
        // -Detach from it (including System Affinity)
        // ----------------------------------------------------------------
        SET_RETRY_POINT(retryParms);
        if (retryFootprints.haltRecovery == 0) {

            if (retryFootprints.tryToReleaseENQ == 0) {
                retryFootprints.tryToReleaseENQ = 1;

                // Drop the Local Comm Enq
                enqtoken localEnqToken;
                TToken localEnqOwningTToken;
                memcpy(&localEnqToken,
                       &(bbgzlocl_p->serverSpecificInfo.serverIdentityEnqToken),
                       sizeof(localEnqToken));
                memcpy(&localEnqOwningTToken,
                       &(bbgzlocl_p->serverSpecificInfo.serverIdentityEnqTokenOwningTToken),
                       sizeof(localEnqOwningTToken));

                memset(&(bbgzlocl_p->serverSpecificInfo.serverIdentityEnqToken),
                       0,
                       sizeof(bbgzlocl_p->serverSpecificInfo.serverIdentityEnqToken));

                memset(&(bbgzlocl_p->serverSpecificInfo.serverIdentityEnqTokenOwningTToken),
                       0,
                       sizeof(bbgzlocl_p->serverSpecificInfo.serverIdentityEnqTokenOwningTToken));

                release_enq_owning(&localEnqToken,
                                   (char*)&localEnqOwningTToken);

                retryFootprints.releasedENQ = 1;

                if (TraceActive(trc_level_detailed)) {
                    TraceRecord(trc_level_detailed,
                                TP(TP_SERVER_LCOM_CLIENT_CLEANUPSERVERLOCL_ENQR),
                                "cleanupServerLOCL, released LCOM ENQ",
                                TRACE_DATA_RAWDATA(sizeof(localEnqToken),
                                                   &localEnqToken,
                                                   "localEnqToken"),
                                TRACE_DATA_RAWDATA(sizeof(localEnqOwningTToken),
                                                   &localEnqOwningTToken,
                                                   "localEnqOwningTToken"),
                                TRACE_DATA_END_PARMS);
                }

                // Mark the LOCL as shutting down.
                setServerShutdownFlag(bbgzlocl_p);

                // Mark the server's inbound Work Queue so "no more work" can be queued to it (the WRQE's are in
                // the LOCL Shared Memory Object).
                setNoMoreWork(&(bbgzlocl_p->serverSpecificInfo.serverAsClientWorkQ));


                retryFootprints.setShutdownFlags = 1;


                // Detach from LOCL Shared Memory Object (including the System Affinity)
                prepareLoclForSystemDetach(bbgzlocl_p);
                memcpy(bbgzlocl_p->eyecatcher, RELEASED_BBGZLOCL_EYE, sizeof(bbgzlocl_p->eyecatcher));

                long long userToken = getAddressSpaceSupervisorStateUserToken();
                int       detRC, detRSN;

                detRC = detachSharedAboveConditional(bbgzlocl_p, userToken, FALSE, &detRSN);
                if (detRC != 0) {
                    retryFootprints.sharedDetachFailed = 1;
                }

                detRC = detachSharedAboveConditional(bbgzlocl_p, userToken, TRUE, &detRSN);
                if (detRC != 0) {
                    retryFootprints.systemAffinityDetachFailed = 1;
                }

                retryFootprints.detachedFromSharedMem = 1;

                localRC  = 0;
                localRSN = 0;

            } else {
                retryFootprints.abendedAndRetried = 1;
            }
        }

        // -----------------------------------------------------------------------
        // Remove the ESTAE.
        // -----------------------------------------------------------------------
        remove_estaex(&estaex_rc, &estaex_rsn);


        if ((localRC == 0) && (retryFootprints.abendedAndRetried == 1)) {
            localRC  = -1;
            localRSN = LCOM_CLEANUPSERVERLOCL_RSN_ABENDED;
        }
    } else {
        // Couldn't establish an ESTAE
        localRC = -1;
        localRSN = LCOM_CLEANUPSERVERLOCL_RSN_NOESTAE;
    }

    if (TraceActive(trc_level_detailed)) {
         TraceRecord(trc_level_detailed,
                     TP(TP_SERVER_LCOM_CLIENT_CLEANUPSERVERLOCL_EXIT),
                     "cleanupServerLOCL, exiting",
                     TRACE_DATA_HEX_INT(*((int*) &retryFootprints),
                                        "retryFootprints"),
                     TRACE_DATA_END_PARMS);
     }

    *outRSN = localRSN;
    return localRC;
}

/**
 * Called during Server abnormal termination to cleanup client resources related to
 * this Server.
 *
 * @param currentCCQE_p Pointer to the Client cleanup queue element representing the
 * client to cleanup server related resources.
 * @return an integer representing the cleanup activities.
 */
#define LCOM_CLEANUPCLIENT_SG_RC_ABENDED  0x20000000
static int cleanupClient_ServerGone(LocalCommClientCleanupQueueElement_t* currentCCQE_p) {

    int localRC;
    // -----------------------------------------------------------------------
    // Establish some recovery in case of an abend.
    // -----------------------------------------------------------------------
    int estaex_rc  = -1;
    int estaex_rsn = -1;
    volatile struct {
                 int tryToSetESTAE : 1,
                     setESTAE : 1,
                     abendedAndRetried: 1,          // Bit mapped by LCOM_CLEANUPCLIENT_SG_RC_ABENDED above
                     failedSettingUpESTAE: 1,

                     tryToExtractClientInfo: 1,
                     failedExtactingClientInfo: 1,
                     tryToCloseClientConnections: 1,
                     failedClosingConnections: 1,

                     clientCleaningLSCL: 1,
                     closedAtLeastOneConnection: 1,
                     tryToDetachLOCL_LDAT: 1,
                     failedDetachingLOCL_LDAT: 1,

                     detachFailedForLOCL: 1,
                     detachFailedForLDAT: 1,
                     detachedFromClientSharedMem: 1,

                     _available : 17;
    } retryFootprints;

    memset((void*)&retryFootprints, 0, sizeof(retryFootprints));
    struct retry_parms retryParms;
    memset(&retryParms, 0, sizeof(retryParms));

    // ----------------------------------------------------------------
    // -Drive close on all the connections found in the LSCL for the
    // associated client.
    //
    // -Detach from the client's Shared Memory.
    // ----------------------------------------------------------------
    SET_RETRY_POINT(retryParms);
    if (retryFootprints.tryToSetESTAE == 0) {
        retryFootprints.tryToSetESTAE = 1;
        establish_estaex_with_retry(&retryParms, &estaex_rc, &estaex_rsn);
        retryFootprints.setESTAE = (estaex_rc == 0);
    }

    if (retryFootprints.setESTAE == 1) {
        // -----------------------------------------------------------------------
        // Extract Client info
        // -----------------------------------------------------------------------
        SET_RETRY_POINT(retryParms);
        if (retryFootprints.tryToExtractClientInfo == 0) {
            retryFootprints.tryToExtractClientInfo = 1;

            LocalCommClientAnchor_t*     clientLOCL_p = currentCCQE_p->inClientBBGZLOCL_p;
            LocalCommClientServerPair_t* clientLSCL_p = currentCCQE_p->inClientBBGZLSCL_p;
            LocalCommClientDataStore_t*  clientLDAT_p = currentCCQE_p->inClientBBGZLDAT_p;

            // -----------------------------------------------------------------------
            // For each Connection in the Client's LSCL
            // -----------------------------------------------------------------------
            SET_RETRY_POINT(retryParms);
            if (retryFootprints.tryToCloseClientConnections == 0) {
                retryFootprints.tryToCloseClientConnections = 1;

                LocalCommConnectionHandle_t* currentHandle_p = clientLSCL_p->firstInUseConnHdl_p;
                do {
                    LocalCommClientConnectionHandle_t currentHandleToken;
                    memset(&currentHandleToken, 0, sizeof(currentHandleToken));

                    if (currentHandle_p != NULL) {
                        if (currentHandle_p->handlePLO_CS.flags.serverInitiatedClosing == 0) {
                            // Build Connection handle token
                            currentHandleToken.handle_p      = currentHandle_p;
                            currentHandleToken.instanceCount = currentHandle_p->instanceCount;
                        } else {
                            // Skip this connection.
                            currentHandle_p = currentHandle_p->nextHandle_p;
                            continue;
                        }

                        // Bail if the LSCL is not in a reasonable state
                        if ((clientLSCL_p->lscl_PLO_CS_Area.flags.clientLDAT_detach == 1) ||
                            (memcmp(&(clientLSCL_p->serverStoken),
                                    &(((assb*)(((ascb*)(((psa*)0)->psaaold))->ascbassb))->assbstkn),
                                    sizeof(clientLSCL_p->serverStoken)) != 0)) {

                            retryFootprints.clientCleaningLSCL = 1;
                            break;
                        }

                        // Drive close on the connection (will do nothing with a zeroed connection token)
                        localCommClose(&currentHandleToken);

                        retryFootprints.closedAtLeastOneConnection = 1;

                        // Start at the beginning, client could've removed connection.
                        currentHandle_p = clientLSCL_p->firstInUseConnHdl_p;
                    }
                } while (currentHandle_p != NULL);
            } else {
                retryFootprints.abendedAndRetried        = 1;
                retryFootprints.failedClosingConnections = 1;
            }

            // -----------------------------------------------------------------------
            // Detach from Client's Shared Memory objects (LOCL and LDAT)
            // -----------------------------------------------------------------------
            SET_RETRY_POINT(retryParms);
            if (retryFootprints.tryToDetachLOCL_LDAT == 0) {
                retryFootprints.tryToDetachLOCL_LDAT = 1;

                int       detRC, detRSN;

                // Detach from client LOCL Shared Memory Object
                detRC = detachSharedAboveConditional(clientLOCL_p, (long long) clientLSCL_p, FALSE, &detRSN);
                if (detRC != 0) {
                    retryFootprints.detachFailedForLOCL = 1;
                }

                // Detach from the client LDAT Shared Memory Object
                detRC = detachSharedAboveConditional(clientLDAT_p, (long long) clientLSCL_p, FALSE, &detRSN);
                if (detRC != 0) {
                    retryFootprints.detachFailedForLDAT = 1;
                }

                retryFootprints.detachedFromClientSharedMem = 1;
            } else {
                retryFootprints.abendedAndRetried = 1;
                retryFootprints.failedDetachingLOCL_LDAT = 1;
            }
        } else {
            // Abended extracting client info from Client Cleanup element
            retryFootprints.abendedAndRetried         = 1;
            retryFootprints.failedExtactingClientInfo = 1;
        }

        // -----------------------------------------------------------------------
        // Remove the ESTAE.
        // -----------------------------------------------------------------------
        remove_estaex(&estaex_rc, &estaex_rsn);
    } else {
        // Couldn't establish an ESTAE
        retryFootprints.failedSettingUpESTAE = 1;
    }

    return *((int*) &retryFootprints);
}   // end, cleanupClient_ServerGone

/**
 * Cleanup Client Local comm constructs related to a Server that is terminating.
 *
 * Intended to be invoked from the resmgr established for the IPT task termination
 * of a local comm Server.
 *
 * This method will be invoked for each Client that the terminating server is
 * currently attached to the client's shared memory.
 *
 * @param spd_p Pointer to the Server's server_process_data.
 * @return 0
 */

int cleanupClients_ServerGone(void* in_spd_p) {
    server_process_data* spd_p = (server_process_data*) in_spd_p;
    int localRC = 0, localRSN = 0;
    LocalCommClientAnchor_t* serverLOCL_p = (LocalCommClientAnchor_t*) spd_p->lcom_BBGZLOCL_p;

    if (serverLOCL_p) {
        // ---------------------------------------------------------------------------------------------
        // Cleanup client stuff
        // ---------------------------------------------------------------------------------------------
        int abendCnt = 0;
        for (LocalCommClientCleanupQueueElement_t* currentCCQE_p = (LocalCommClientCleanupQueueElement_t*) serverLOCL_p->serverSpecificInfo.serverClientCleanupQueue.queueHeader.head_p;
             (currentCCQE_p != NULL) && (abendCnt < 5);
             currentCCQE_p = (LocalCommClientCleanupQueueElement_t*) currentCCQE_p->queueElement.element_next_p) {

            // Cleanup local comm client resources related to this server.
            int statusFlags = cleanupClient_ServerGone(currentCCQE_p);

            if (statusFlags & LCOM_CLEANUPCLIENT_SG_RC_ABENDED) {
                abendCnt++;
            }

            // Footprint, cleanup client results
            createLocalCommFPEntry_CleanupClientServerGone(serverLOCL_p->footprintTable, currentCCQE_p, statusFlags);
        }

        // ---------------------------------------------------------------------------------------------
        // Cleanup Server LOCL related resources
        // Note: Common cleanup routine between here and server_lcom_services's uninit routine.
        // ---------------------------------------------------------------------------------------------
        localRC = cleanupServerLOCL(&localRSN);

    }   // end if, have a Server LOCL
}

/**
 * Cleanup client resources related to a bind break to a server.
 *
 * @param serverToken Stoken of server that client has broken a bind.
 * @return 0.
 */
int cleanupClient_ClientTerm(unsigned long long serverToken) {

    int localRC = 0;
    // -----------------------------------------------------------------------
    // Establish some recovery in case of an abend.
    // -----------------------------------------------------------------------
    int estaex_rc  = -1;
    int estaex_rsn = -1;
    volatile struct {
                 int tryToSetESTAE : 1,
                     setESTAE : 1,
                     abendedAndRetried: 1,
                     failedSettingUpESTAE: 1,

                     tryToFindLSCLforServer: 1,
                     noLOCLtoCleanup: 1,
                     failedFindingLSCL: 1,
                     foundTargetLSCL: 1,

                     tryToCleanupLSCL: 1,
                     tryToCloseConnections: 1,
                     failedClosingConnections: 1,
                     closedAtLeastOneConnection: 1,

                     tryToFindAndDequeueLSCL: 1,
                     failedFindAndDequeueLSCL: 1,
                     tryToReleaseLSCL: 1,
                     failedReleasingLSCL_LDAT: 1,

                     tryToDetachLDAT_SysAff: 1,
                     detachFailedLDAT_SysAff: 1,
                     detachFailedLDAT_Client: 1,
                     failedCleaningupLSCL: 1,

                     tryToCleanupLOCL: 1,
                     failedCleaningupLOCL: 1,
                     tryToReleaseLOCL_Nametoken: 1,
                     releasedLOCL_Nametoken: 1,

                     failedReleasingLOCL_NameToken: 1,
                     tryToDetachLOCL: 1,
                     detachFailedLOCL_SysAff: 1,
                     detachFailedLOCL_Client: 1,

                     detachedFromLOCL: 1,
                     failedDetachLOCL: 1,
                     _available : 2;

                 int abendCnt: 16;
                 int connectionCloseCnt: 16;
    } retryFootprints;

    memset((void*)&retryFootprints, 0, sizeof(retryFootprints));
    struct retry_parms retryParms;
    memset(&retryParms, 0, sizeof(retryParms));

    // ----------------------------------------------------------------
    // -Drive close on all the connections found in the LSCL for the
    // associated server.
    // ----------------------------------------------------------------
    SET_RETRY_POINT(retryParms);
    if (retryFootprints.tryToSetESTAE == 0) {
        retryFootprints.tryToSetESTAE = 1;
        establish_estaex_with_retry(&retryParms, &estaex_rc, &estaex_rsn);
        retryFootprints.setESTAE = (estaex_rc == 0);
    }

    if (retryFootprints.setESTAE == 1) {
        // -----------------------------------------------------------------------
        // Find Client LOCL
        //
        // Cleanup LSCL for related Server.
        //   -Find LSCL
        //      -Cleanup connections.
        //   -Remove LSCL
        //
        // Note: WOLA clients should've driven unregister which should've driven close connections ...
        //
        //   -Mark the Client LOCL as "Client Terminating"??
        //
        //   -Run the LSCL to cleanup each connection and the LSCL, detach from the LDAT's (System Affinity too).
        // -----------------------------------------------------------------------

        LocalCommClientAnchor_t* currentLOCL_p = getCurrentLOCL_address();

        SET_RETRY_POINT(retryParms);
        if (retryFootprints.tryToFindLSCLforServer == 0) {
            retryFootprints.tryToFindLSCLforServer = 1;


            if (currentLOCL_p != NULL) {
                LocalCommClientServerPair_t* lscl_p;

                // -----------------------------------------------------------------------
                // Find existing LSCL for the target Server
                // -----------------------------------------------------------------------
                LocalComLOCL_PLO_CS_Area_t out_lsclCurrentState;
                lscl_p = getLocalCommClientServerPair(serverToken, currentLOCL_p, &out_lsclCurrentState);

                // -----------------------------------------------------------------------
                // Start cleanup of LSCL...close connections
                // -----------------------------------------------------------------------
                SET_RETRY_POINT(retryParms);
                if (retryFootprints.tryToCleanupLSCL == 0) {
                    retryFootprints.tryToCleanupLSCL = 1;

                    if (lscl_p != NULL) {
                        retryFootprints.foundTargetLSCL = 1;

                        // Mark LSCL as terminating
                        setClientCleaningupFlag(currentLOCL_p);

                        // -----------------------------------------------------------------------
                        // Loop through Connections trying to close them.
                        // -----------------------------------------------------------------------
                        unsigned short connectionCloseAbends = 0;
                        SET_RETRY_POINT(retryParms);
                        do {
                            if (retryFootprints.tryToCloseConnections == 0) {
                                retryFootprints.tryToCloseConnections = 1;

                                LocalCommConnectionHandle_t* currentHandle_p = lscl_p->firstInUseConnHdl_p;
                                do {
                                    LocalCommClientConnectionHandle_t currentHandleToken;
                                    memset(&currentHandleToken, 0, sizeof(currentHandleToken));

                                    if (currentHandle_p != NULL) {
                                        if (currentHandle_p->handlePLO_CS.flags.clientClosed == 0) {
                                            // Build Connection handle token
                                            currentHandleToken.handle_p      = currentHandle_p;
                                            currentHandleToken.instanceCount = currentHandle_p->instanceCount;
                                        } else {
                                            // Skip this connection.
                                            currentHandle_p = currentHandle_p->nextHandle_p;
                                            continue;
                                        }

                                        // Drive close on the connection (will do nothing with a zeroed connection token)
                                        retryFootprints.connectionCloseCnt++;
                                        localCommClose(&currentHandleToken);
                                        retryFootprints.closedAtLeastOneConnection = 1;

                                        // Start at the beginning, client could've removed connection.
                                        currentHandle_p = lscl_p->firstInUseConnHdl_p;
                                    }
                                } while (currentHandle_p != NULL);

                                // No more connections
                                break;
                            } else {
                                // Abended Closing a connection ?
                                retryFootprints.abendCnt++;
                                retryFootprints.abendedAndRetried         = 1;

                                connectionCloseAbends++;

                                // Should we keep closing connections?
                                if (connectionCloseAbends <= 4) {
                                    // Reset retry flag.
                                    retryFootprints.tryToCloseConnections = 0;
                                } else {
                                    retryFootprints.failedClosingConnections  = 1;
                                    break;
                                }
                            }
                        } while (connectionCloseAbends <= 4);

                        // -----------------------------------------------------------------------
                        // Find and dequeue LSCL to server that we unbound from.
                        // -----------------------------------------------------------------------
                        SET_RETRY_POINT(retryParms);
                        if (retryFootprints.tryToFindAndDequeueLSCL == 0) {
                            retryFootprints.tryToFindAndDequeueLSCL = 1;
                            int foundLSCL = 0;
                            do {
                                lscl_p = NULL;

                                // Find existing LSCL for the target Server
                                LocalComLOCL_PLO_CS_Area_t out_lsclCurrentState;
                                lscl_p = getLocalCommClientServerPair(serverToken, currentLOCL_p, &out_lsclCurrentState);
                                if (lscl_p != NULL) {
                                    // Remove from chain
                                    int removeRC = removeLSCL(currentLOCL_p, lscl_p, &out_lsclCurrentState);
                                    if (removeRC == 0) {
                                        foundLSCL = 1;
                                    }
                                } else {
                                    break;
                                }
                            } while (!foundLSCL);
                        } else {
                            // Abended finding or cleaning up the LSCL
                            retryFootprints.abendCnt++;
                            retryFootprints.abendedAndRetried         = 1;
                            retryFootprints.failedFindAndDequeueLSCL  = 1;
                        }

                        // -----------------------------------------------------------------------
                        // Free cleaned up LSCL (may already be released connection close
                        // processing).
                        // -----------------------------------------------------------------------
                        SET_RETRY_POINT(retryParms);
                        if (retryFootprints.tryToReleaseLSCL == 0) {
                            retryFootprints.tryToReleaseLSCL = 1;
                            if (lscl_p != NULL) {
                                LocalCommClientDataStore_t* dataStore_p = lscl_p->firstDataStore_p;

                                // Detach from LDAT
                                if ((lscl_p->lscl_PLO_CS_Area.flags.clientLDAT_detach == 0) && dataStore_p) {
                                    SToken ldatServerStoken __attribute__((aligned(8))) = dataStore_p->serverStoken;
                                    int detRSN;

                                    retryFootprints.tryToDetachLDAT_SysAff = 1;

                                    // Detach System Affinity.  Watch the usertoken.
                                    long long allocationUserToken = getAddressSpaceSupervisorStateUserToken();
                                    int detRC = detachSharedAboveConditional(dataStore_p, allocationUserToken, TRUE, &detRSN);

                                    if (detRC != 0) {
                                        retryFootprints.detachFailedLDAT_SysAff = 1;
                                    }

                                    // Flip eyecatcher.
                                    memcpy(dataStore_p->eyecatcher, RELEASED_BBGZLDAT_EYE, sizeof(dataStore_p->eyecatcher));

                                    // Try to detach our access to the LDAT.
                                    detRC = detachSharedAboveConditional(dataStore_p, (long long) lscl_p, FALSE, &detRSN);

                                    if (detRC != 0) {
                                        retryFootprints.detachFailedLDAT_Client = 1;
                                    }
                                }

                                // Return LSCL to pool
                                cleanupLSCL(currentLOCL_p, lscl_p);
                            }
                        } else {
                            // Abended cleaning up the LSCL or LDAT
                            retryFootprints.abendCnt++;
                            retryFootprints.abendedAndRetried         = 1;
                            retryFootprints.failedReleasingLSCL_LDAT  = 1;
                        }
                    } else {
                        // No LSCL ...
                    }
                } else {
                    // Abended finding or cleaning up the LSCL
                    retryFootprints.abendCnt++;
                    retryFootprints.abendedAndRetried         = 1;
                    retryFootprints.failedCleaningupLSCL      = 1;
                }
            }   // end if, have LOCL
            else {
                retryFootprints.noLOCLtoCleanup = 1;
            }
        } else {
            // Abended finding or cleaning up the LSCL
            retryFootprints.abendCnt++;
            retryFootprints.abendedAndRetried         = 1;
            retryFootprints.failedFindingLSCL         = 1;
        }

        // -----------------------------------------------------------------------
        // Can free the Client LOCL if we can detect that we're running in the client (we wouldn't have found the LOCL using the
        // HOME_LEVEL nametoken if we were not in the client space) AND the IPT Thread is dying or gone??  OR No Connections?
        //TODO: Need a better condition/checking for Client LOCL cleanup.
        // -----------------------------------------------------------------------

        if (currentLOCL_p) {
            // free Client LOCL...
            SET_RETRY_POINT(retryParms);
            if (retryFootprints.tryToCleanupLOCL == 0) {
                retryFootprints.tryToCleanupLOCL = 1;

                // If this client has NO connections to any server
                if (currentLOCL_p->firstLSCL_p == NULL) {
                    // -----------------------------------------------------------------------
                    // Delete the nametoken for our LOCL
                    // -----------------------------------------------------------------------
                    SET_RETRY_POINT(retryParms);
                    if (retryFootprints.tryToReleaseLOCL_Nametoken == 0) {
                        retryFootprints.tryToReleaseLOCL_Nametoken = 1;

                        struct {
                            unsigned char name[8];
                            unsigned long long nulls;
                        } nameTokenName;

                        memcpy(nameTokenName.name, BBGZLOCL_NAMETOKEN_NAME, sizeof(nameTokenName.name));
                        nameTokenName.nulls = 0L;

                        int nameTokenReturnCode = 0;
                        iean4dl(IEANT_HOME_LEVEL, (char*) &nameTokenName, &nameTokenReturnCode);
                        if (nameTokenReturnCode == IEANT_OK) {
                            retryFootprints.releasedLOCL_Nametoken = 1;
                        } else {
                            retryFootprints.failedReleasingLOCL_NameToken = 1;
                        }
                    } else {
                        retryFootprints.abendCnt++;
                        retryFootprints.abendedAndRetried = 1;
                        retryFootprints.failedReleasingLOCL_NameToken = 1;
                    }

                    // Footprint cleanup activity thus far into the LOCL footprint table
                    createLocalCommFPEntry_CleanupClientClientTerm(currentLOCL_p->footprintTable,
                                                                   serverToken,
                                                                   (unsigned long long*)& retryFootprints);

                    // -----------------------------------------------------------------------
                    // Detach from the LOCL Shared Memory
                    // -----------------------------------------------------------------------
                    // Detach from LOCL Shared Memory Object (including the System Affinity)
                    SET_RETRY_POINT(retryParms);
                    if (retryFootprints.tryToDetachLOCL == 0) {
                        retryFootprints.tryToDetachLOCL = 1;

                        // Do other cleanup before detach.
                        prepareLoclForSystemDetach(currentLOCL_p);
                        memcpy(currentLOCL_p->eyecatcher, RELEASED_BBGZLOCL_EYE, sizeof(currentLOCL_p->eyecatcher));

                        long long userToken = getAddressSpaceSupervisorStateUserToken();
                        int       detRC, detRSN;

                        detRC = detachSharedAboveConditional(currentLOCL_p, userToken, FALSE, &detRSN);
                        if (detRC != 0) {
                            retryFootprints.detachFailedLOCL_Client = 1;
                        }

                        detRC = detachSharedAboveConditional(currentLOCL_p, userToken, TRUE, &detRSN);
                        if (detRC != 0) {
                            retryFootprints.detachFailedLOCL_SysAff = 1;
                        }

                        retryFootprints.detachedFromLOCL = 1;

                    } else {
                        retryFootprints.abendCnt++;
                        retryFootprints.abendedAndRetried  = 1;
                        retryFootprints.failedDetachLOCL   = 1;
                    }

                } else {
                    // Footprint cleanup activity thus far into the LOCL footprint table
                    createLocalCommFPEntry_CleanupClientClientTerm(currentLOCL_p->footprintTable,
                                                                   serverToken,
                                                                   (unsigned long long*)& retryFootprints);
                }
            } else {
                retryFootprints.abendCnt++;
                retryFootprints.abendedAndRetried         = 1;
                retryFootprints.failedCleaningupLOCL      = 1;
            }
        }   // end if, we have a LOCL

        // -----------------------------------------------------------------------
        // Remove the ESTAE.
        // -----------------------------------------------------------------------
        remove_estaex(&estaex_rc, &estaex_rsn);
    } else {
        // Couldn't establish an ESTAE
        retryFootprints.failedSettingUpESTAE = 1;
    }

    return localRC;
}

///**
// * Local Comm cleanup routine meant to be driven from a resmgr established by the
// * client on the server it attached to...
// *
// * @param clientStoken
// * @param serverToken
// * @return
// */
//#define LCOM_CLEANUP_SERVERGONE_RC_BADPARMS    8
//int cleanupClient_ServerGone(SToken clientStoken, SToken serverToken) {
//    SToken nullToken;
//    memset(&nullToken, 0, sizeof(nullToken));
//
//    if ( (memcmp(&clientStoken, &nullToken, sizeof(nullToken)) == 0) ||
//         (memcmp(&serverToken, &nullToken, sizeof(nullToken)) == 0) ) {
//        return LCOM_CLEANUP_SERVERGONE_RC_BADPARMS;
//
//    }
//
//    // Find Client LOCL and attach to it (shared memory object).
//
//        // The nametoken Doc (iean4rt) has restrictions on calling from a RESMGR.
//        // The target nametoken may have to be a system-level for it to work for us.
//
//        // There may be a client ENQ we can gqscan for....client bind perhaps? Nah, its STEP scoped.
//
//
//
//
//    // Find LSCL for the terminated Server
//
//        // Attach LDAT prior to cleanup calls on Connections/Queues
//}
