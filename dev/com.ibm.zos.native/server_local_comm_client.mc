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

#include <metal.h>
#include <stdio.h>

#include "include/server_local_comm_client.h"

#include "include/ieantc.h"
#include "include/mvs_cell_pool_services.h"
#include "include/mvs_enq.h"
#include "include/mvs_iarv64.h"
#include "include/mvs_plo.h"
#include "include/mvs_user_token_manager.h"
#include "include/ras_tracing.h"
#include "include/security_saf_authorization.h"
#include "include/server_common_function_module.h"
#include "include/server_local_comm_data_store.h"
#include "include/server_local_comm_footprint.h"
#include "include/server_local_comm_global_lock.h"
#include "include/server_local_comm_shared_memory.h"
#include "include/server_process_data.h"

#include "include/gen/ihapsa.h"
#include "include/gen/ihaascb.h"
#include "include/gen/ihaassb.h"
#include "include/gen/isgyquaa.h"

#define RAS_MODULE_CONST  RAS_MODULE_SERVER_LCOM_CLIENT

#define TP_SERVER_LCOM_CLIENT_FREELHDL_ENTRY                   1
#define TP_SERVER_LCOM_CLOSE_ENTRY                             2
#define TP_SERVER_LCOM_CLOSE_EXIT                              3
#define TP_SERVER_LCOM_CLOSE_PRESTATECHANGE                    4
#define TP_SERVER_LCOM_CLOSE_STALEHANDLE                       5
#define TP_SERVER_LCOM_CLOSE_EXIT2                             6



/** Number of segments (megabytes) to allocate for BBGZLOCL. */
#define BBGZLOCL_SEGMENTS 32

/** Number of segments (megabytes) to reserve as 'HIDDEN' in the BBGZLOCL. */
#define BBGZLOCL_HIDDEN_SEGMENTS 30

/** Name of connection handle cell pool. */
#define BBGZLHDL_POOL_NAME "LHDLPOOL"

/** Number of connection handle cells per cell pool extent. */
#define BBGZLHDL_CELLS_PER_EXTENT 128

/** Name of the queue element cell pool. */
#define LHDLQE_POOL_NAME "LCQEPOOL"

/** Number of queue element cells per cell pool extent. */
#define LHDLQE_CELLS_PER_EXTENT 128

/** Name of the client server pair cell pool. */
#define BBGZLSCL_POOL_NAME "LSCLPOOL"

/** Number of client server pair cells per cell pool extent. */
#define BBGZLSCL_CELLS_PER_EXTENT 128

/**
 * Forward declares
 */
LocalCommClientAnchor_t* getLOCL_address(SToken* stoken_p);
int addHandle(LocalCommClientServerPair_t* pair_p, LocalCommConnectionHandle_t* connHandle_p);
int removeHandle(LocalCommClientServerPair_t* pair_p, LocalCommConnectionHandle_t* connHandle_p);
int anyValidConnections(LocalCommClientServerPair_t* pair_p);
int isHealthy(LocalCommClientServerPair_t* targetPair_p);
LocalCommConnectionHandle_t* getConnectionHandleFromPool(LocalCommClientAnchor_t* anchor_p);
int initializeHandle(LocalCommConnectionHandle_t* connHandle_p, LocalCommClientServerPair_t* pair_p, LocalCommClientConnectionHandle_t* clientHandle);
int cleanupHandle(LocalCommClientAnchor_t* inLOCL_p,LocalCommConnectionHandle_t* connHandle_p);

#define LCOM_STARTHANDLECLOSING_OTHERSIDE_CLOSING  8
#define LCOM_STARTHANDLECLOSING_ALREADY_CLOSING   12
int startHandleClosing(LocalCommConnectionHandle_t* connHandle_p);

#define LCOM_MARKHANDLECLOSED_OTHERSIDE_CLOSED 9
int markHandleClosed(LocalCommConnectionHandle_t* connHandle_p);

int createLocalCommClientServerPair(SToken* serverStoken_p, unsigned long long clientProcessDataToken, LocalCommClientAnchor_t* anchor_p, LocalCommClientServerPair_t** pair_p_p, LocalComLOCL_PLO_CS_Area_t* lsclRequiredState_p);
int checkSetLSCL_Detach(LocalCommConnectionHandle_t* connHandle_p, int weOwnHandleCleanup);
int addLSCL(LocalCommClientAnchor_t* locl, LocalCommClientServerPair_t* bbgzlscl_p, LocalComLOCL_PLO_CS_Area_t* requiredState);
LocalCommDataCell_t * initializeMessageCell(LocalCommConnectionHandle_t *connHandle_p,  char* data_p, unsigned long long dataLen, unsigned char dataKey);
int notifyOtherSideClosing(LocalCommConnectionHandle_t* connHandle_p);

int clientConnectCBIND(acee* inACEE_p,
                       char* inClientSafProfileString,
                       int* outRSN_p,
                       int* outSAFRC_p,
                       int* outRACFRC_p,
                       int* outRACFRSN_p);
/**
 * Validate the referenced Connection Handle is still the instance that the caller expects.
 * @param clientConnHandle_p
 * @return 0 if all OK, otherwise non-zero.
 */
int validateClientConnectionHandle(struct localCommClientConnectionHandle* clientConnHandle_p) {
    int localRC = LOCAL_COMM_STALE_HANDLE;

    if (clientConnHandle_p->handle_p &&
        (clientConnHandle_p->instanceCount == clientConnHandle_p->handle_p->instanceCount) ) {
        localRC = 0;
    }

    return localRC;
}

/**
 * Build a cell pool out of shared memory obtained from the LOCL.
 *
 * @param anchor_p A pointer to the LOCL
 * @param cellSize The size of the cells in the cell pool.
 * @param poolName The 8 character name of the pool
 *
 * @return The ID of the cell pool, or 0 if a pool could not be created.
 */
static long long buildCellPoolFromLOCL(LocalCommClientAnchor_t* anchor_p, long long cellSize, char* poolName)
{
    long long cellPoolAnchor = 0L;
    long long bytesForCellPoolAnchor = computeCellPoolStorageRequirement(0, cellSize);
    void* storageForCellPoolAnchor_p = getLocalCommSharedStorage(&(anchor_p->info), bytesForCellPoolAnchor);

    if (storageForCellPoolAnchor_p != NULL) {
        buildCellPoolFlags poolFlags;
        memset(&poolFlags, 0, sizeof(poolFlags));
        poolFlags.skipInitialCellAllocation = 1;

        cellPoolAnchor = buildCellPool(storageForCellPoolAnchor_p, bytesForCellPoolAnchor, cellSize, poolName, poolFlags);
    }

    return cellPoolAnchor;
}

/**
 * Get some shared memory from the current LOCL.  This routine is passed to other
 * routines which need to obtain memory in a way that is compatible with malloc.
 */
static void* getStorageFromCurrentLOCL(size_t size) {
    void* storage_p = NULL;

    LocalCommClientAnchor_t* currentLOCL_p = getCurrentLOCL_address();
    if (currentLOCL_p != NULL) {
        storage_p = getLocalCommSharedStorage(&(currentLOCL_p->info), (unsigned long long)size);
    }

    return storage_p;
}

/**
 * Get the current Local Comm Anchor (LOCL)
 */
LocalCommClientAnchor_t* getCurrentLOCL_address() {

    struct {
        unsigned char name[8];
        unsigned long long nulls;
    } nameTokenName;

    struct {
        LocalCommClientAnchor_t* ptr;
        unsigned long long nulls;
    } nameTokenToken;

    // -----------------------------------------------------------------------
    // See if the LOCL already exists.
    // -----------------------------------------------------------------------
    memcpy(nameTokenName.name, BBGZLOCL_NAMETOKEN_NAME, sizeof(nameTokenName.name));
    nameTokenName.nulls = 0L;
    memset(&nameTokenToken, 0, sizeof(nameTokenToken));

    int nameTokenReturnCode;
    iean4rt(IEANT_HOMEAUTH_LEVEL, (char*)&nameTokenName, (char*)&nameTokenToken, &nameTokenReturnCode);

    return nameTokenToken.ptr;
}

/**
 * Get the address of the BBGZLOCL control block.  If one does not exist,
 * create it.
 *
 * This function assumes that the BBGZLOCL will not be deallocated unless the
 * client address space is terminating.
 *
 * @param create If set to TRUE, a BBGZLOCL will be created if one does not
 *               already exist.
 *
 * @return A pointer to the BBGZLOCL control block, or NULL if one could not
 *         be found or created.
 */
static LocalCommClientAnchor_t* getLocalCommClientAnchor(void) {

    LocalCommClientAnchor_t* anchor_p = getCurrentLOCL_address();

    // -----------------------------------------------------------------------
    // If it does not already exist, try to make one.
    // -----------------------------------------------------------------------
    if (anchor_p == NULL) {
        // NOTE: Shared memory cleanup utility depends on the user token being the STOKEN.
        long long userToken = getAddressSpaceSupervisorStateUserToken();
        void* sharedMemory_p = getSharedAbove(BBGZLOCL_SEGMENTS, FALSE, userToken);

        if (sharedMemory_p != NULL) {
            unsigned char cleanupSharedMem = FALSE;
            int iarv64_rc = 0, iarv64_rsn = 0;
            void* startOfHidden_p = (void*)(((char*)sharedMemory_p) +
                ((BBGZLOCL_SEGMENTS - BBGZLOCL_HIDDEN_SEGMENTS) * (1024 * 1024)));
            iarv64_rc = changeSharedAccessLevel(startOfHidden_p, BBGZLOCL_HIDDEN_SEGMENTS,
                                                IARV64_SHARED_HIDDEN, &iarv64_rsn);
            if (iarv64_rc == 0) {
                accessSharedAbove(sharedMemory_p, userToken);

                anchor_p = (LocalCommClientAnchor_t*)sharedMemory_p;
                memset(anchor_p, 0, sizeof(LocalCommClientAnchor_t));
                memcpy(anchor_p->eyecatcher, BBGZLOCL_EYE, sizeof(anchor_p->eyecatcher));
                anchor_p->version = 1;
                anchor_p->length = sizeof(LocalCommClientAnchor_t);
                memcpy(&(anchor_p->creatorStoken),
                       &(((assb*)(((ascb*)(((psa*)0)->psaaold))->ascbassb))->assbstkn),
                       sizeof(anchor_p->creatorStoken));
                anchor_p->info.baseAddress_p = anchor_p;
                anchor_p->info.numSegmentsAllocated = BBGZLOCL_SEGMENTS;
                anchor_p->info.numSegmentsGuard = BBGZLOCL_HIDDEN_SEGMENTS;
                anchor_p->info.nextFreeByte_p = anchor_p + 1;
                anchor_p->numConnHandlesPerExtent = BBGZLHDL_CELLS_PER_EXTENT;
                anchor_p->numQueueElementsPerExtent = LHDLQE_CELLS_PER_EXTENT;
                anchor_p->numClientServerPairsPerExtent = BBGZLSCL_CELLS_PER_EXTENT;

                // ---------------------------------------------------------------
                // Set up the client's PETVET.  If this is actually a server, this
                // will get swapped out with the PETVET hung off of the server
                // process data later.  The storage for the PET cache is obtained
                // from the LOCL because the client can have more than one heap
                // when the client connects to multiple address spaces.
                // ---------------------------------------------------------------
                anchor_p->spca_p = &(anchor_p->clientSpcaBranch);

                // ---------------------------------------------------------------
                // Create the anchor for the connection handle pool.
                // ---------------------------------------------------------------
                anchor_p->connHandlePool = buildCellPoolFromLOCL(anchor_p, sizeof(LocalCommConnectionHandle_t), BBGZLHDL_POOL_NAME);
                if (anchor_p->connHandlePool != 0L) {

                    // -------------------------------------------------------
                    // Create the anchor for the client server pair (LSCL)
                    // element pool.
                    // -------------------------------------------------------
                    anchor_p->clientServerPairPool = buildCellPoolFromLOCL(anchor_p, sizeof(LocalCommClientServerPair_t), BBGZLSCL_POOL_NAME);
                    if (anchor_p->clientServerPairPool != 0L) {

                        // -------------------------------------------------------
                        // Create the anchor for the queue element pool.
                        // TODO: This pool needs to auto-expand because it'll be
                        //       used by both client & server inside the queue.
                        // -------------------------------------------------------
                        anchor_p->queueElementPool = buildCellPoolFromLOCL(anchor_p, sizeof(LocalCommCommonQueueElement), LHDLQE_POOL_NAME);
                        if (anchor_p->queueElementPool != 0L) {

                            // -----------------------------------------------------
                            // Create footprint table
                            // -----------------------------------------------------
                            anchor_p->footprintTable = createLocalCommFootprintTable(&(anchor_p->info));
                            if (anchor_p->footprintTable != 0L) {

                                // -----------------------------------------------
                                // Try to set the name token.
                                // -----------------------------------------------
                                struct {
                                    unsigned char name[8];
                                    unsigned long long nulls;
                                } nameTokenName;

                                struct {
                                    LocalCommClientAnchor_t* ptr;
                                    unsigned long long nulls;
                                } nameTokenToken;
                                int nameTokenReturnCode;

                                memcpy(nameTokenName.name, BBGZLOCL_NAMETOKEN_NAME, sizeof(nameTokenName.name));
                                nameTokenName.nulls = 0L;

                                nameTokenToken.ptr = anchor_p;
                                nameTokenToken.nulls = 0L;

                                iean4cr(IEANT_HOME_LEVEL, (char*)&nameTokenName, (char*)&nameTokenToken, IEANT_NOPERSIST, &nameTokenReturnCode);

                                if (nameTokenReturnCode == IEANT_OK) {
                                    // Initialize the PETVET after the name token is created,  The storage
                                    // routine needs the name token to be able to find the LOCL.
                                    initializePetVet(anchor_p->spca_p, 100, getStorageFromCurrentLOCL); /* Room for 100 PETs */
                                } else {
                                    /* TODO: Cleanup. */
                                    cleanupSharedMem = TRUE;
                                    memset(&nameTokenToken, 0, sizeof(nameTokenToken));
                                    if (nameTokenReturnCode == IEANT_DUP_NAME) {
                                        iean4rt(IEANT_HOMEAUTH_LEVEL, (char*)&nameTokenName, (char*)&nameTokenToken, &nameTokenReturnCode);
                                        anchor_p = nameTokenToken.ptr;
                                    }
                                }
                            } else {
                                cleanupSharedMem = TRUE;
                            }
                        } else {
                            cleanupSharedMem = TRUE;
                        }
                    } else {
                        cleanupSharedMem = TRUE;
                    }
                } else {
                    cleanupSharedMem = TRUE;
                }
            } else {
                cleanupSharedMem = TRUE;
            }

            if (cleanupSharedMem == TRUE) {
                detachSharedAbove(sharedMemory_p, userToken, FALSE);
            }
        }
    }

    return anchor_p;
}

/**
 * Gets a LSCL cell from the pool.
 *
 * @param anchor_p A pointer to the BBGZLOCL.
 *
 * @return A pointer to a new LSCL, or NULL if one could not be obtained.
 */
static LocalCommClientServerPair_t* getClientServerPairFromPool(LocalCommClientAnchor_t* anchor_p) {
    LocalCommClientServerPair_t* pair_p = NULL;
    unsigned char tryAgain = TRUE;

    while ((pair_p == NULL) && (tryAgain == TRUE)) {
        // Try to get a cell from the cell pool.  If no cells, grow the pool.
        pair_p = getCellPoolCell(anchor_p->clientServerPairPool);
        if (pair_p == NULL) {
            long long extentSize = computeCellPoolExtentStorageRequirement(anchor_p->numClientServerPairsPerExtent);
            long long cellSize = (anchor_p->numClientServerPairsPerExtent) * sizeof(LocalCommClientServerPair_t);
            void* cellStorage = getLocalCommSharedStorage(&(anchor_p->info), extentSize + cellSize);
            void* extentStorage = (void*)(((char*)cellStorage) + cellSize);
            if (cellStorage != NULL) {
                growCellPool(anchor_p->clientServerPairPool, anchor_p->numClientServerPairsPerExtent, extentStorage, extentSize, cellStorage, cellSize);
            } else {
                tryAgain = FALSE;
            }
        }
    }

    return pair_p;
}

/**
 * Get or create the BBGZLOCL control block for a Server process.
 *
 *
 * @return A pointer to the BBGZLOCL control block, or NULL if one could not
 *         be created.
 */
LocalCommClientAnchor_t* createServerLocalCommClientAnchor(void) {
    LocalCommClientAnchor_t* anchor_p = NULL;

    // Get BBGZLOCL anchored from PGOO if available
    server_process_data* spd_p = getServerProcessData();
    if (spd_p != NULL) {
        anchor_p = (LocalCommClientAnchor_t *) spd_p->lcom_BBGZLOCL_p;

        if (anchor_p == NULL) {

            // Create the BBGZLOCL and anchor into PGOO.  Driving common initialization for now.
            //TODO: most likely need own initialization routine for Server or pass parm.  The following routine
            // creates a HOME_LEVEL nametoken for it.
            anchor_p = getLocalCommClientAnchor();

            // Initialize Server specific information
            if (anchor_p != NULL) {
                anchor_p->locl_PLO_CS_Area.flags.serverCreated = 1;
                anchor_p->spca_p = (PetVet*)(spd_p->petvet); // Use the server's PETVET.

                initializeWorkQueue(&(anchor_p->serverSpecificInfo.serverAsClientWorkQ),
                                    anchor_p,
                                    &(anchor_p->serverSpecificInfo.serverAsClientWorkQ_PLO_CS_Area));

                //TODO: need serialization...at least make it match other places (cs).
                spd_p->lcom_BBGZLOCL_p = anchor_p;
            }
        }
    }

    return anchor_p;
}

/**
 * Set LOCL flag to indicate that the server has begin and will continue to shutdown this LOCL.
 * Note: The server most likely is terminating, but it may just be stopping the Local Comm
 * channel.  In which case a subsequent re-init of the Local Comm Channel could cause another LOCL
 * instance to be created.
 *
 * @param serverLOCL_p Pointer to the Server LOCL to update.
 * @return 0 if all went well, 1 if not.
 */
int setServerShutdownFlag(LocalCommClientAnchor_t* serverLOCL_p) {
    PloCompareAndSwapAreaDoubleWord_t swapArea;

    int                               ploRC;
    LocalComLOCL_PLO_CS_Area_t        localReplacePLO;

    swapArea.compare_p = &(serverLOCL_p->locl_PLO_CS_Area);
    memcpy(&(swapArea.expectedValue), swapArea.compare_p, sizeof(swapArea.expectedValue));

    do {
        // Build new SwapArea
        memcpy(&localReplacePLO, &(swapArea.expectedValue), sizeof(localReplacePLO));
        localReplacePLO.ploSequenceNumber+=1;

        if ((localReplacePLO.flags.serverCreated == 1) && (localReplacePLO.flags.serverShutdown == 0)) {
            localReplacePLO.flags.serverShutdown = 1;
            memcpy(&(swapArea.replaceValue), &localReplacePLO, sizeof(swapArea.replaceValue));

            ploRC = ploCompareAndSwapDoubleWord(swapArea.compare_p, &swapArea);
        } else {
            ploRC = 99;
            break;
        }

    } while (ploRC);

    return ((ploRC == 99) ? 1 : 0);

}

/**
 * Set LOCL flag to indicate that the client has begun and will continue to shutdown this LOCL.
 * Note: The client may be terminating, but it may just be breaking its bind with a server.
   In which case a subsequent re-bind and connect request will cause another LOCL
 * instance to be created.
 *
 * @param clientLOCL_p Pointer to the Client LOCL to update.
 * @return 0 if all went well, 1 if not.
 */
int setClientCleaningupFlag(LocalCommClientAnchor_t* clientLOCL_p) {
    PloCompareAndSwapAreaDoubleWord_t swapArea;

    int                               ploRC;
    LocalComLOCL_PLO_CS_Area_t        localReplacePLO;

    swapArea.compare_p = &(clientLOCL_p->locl_PLO_CS_Area);
    memcpy(&(swapArea.expectedValue), swapArea.compare_p, sizeof(swapArea.expectedValue));

    do {
        // Build new SwapArea
        memcpy(&localReplacePLO, &(swapArea.expectedValue), sizeof(localReplacePLO));
        localReplacePLO.ploSequenceNumber+=1;

        if ((localReplacePLO.flags.serverCreated == 0) && (localReplacePLO.flags.clientCleaningup == 0)) {
            localReplacePLO.flags.clientCleaningup = 1;
            memcpy(&(swapArea.replaceValue), &localReplacePLO, sizeof(swapArea.replaceValue));

            ploRC = ploCompareAndSwapDoubleWord(swapArea.compare_p, &swapArea);
        } else {
            ploRC = 99;
            break;
        }

    } while (ploRC);

    return ((ploRC == 99) ? 1 : 0);

}
/**
 * Add a new Connection handle to the list.
 * @param pair_p Pointer to LSCL representing a connection pair.
 * @param connHandle_p Pointer to new connection handle to add.
 * @return 0 if added to the LSCL list or 8 if the LSCL is not in a good mood.
 */
static int addHandle(LocalCommClientServerPair_t* pair_p,
                     LocalCommConnectionHandle_t* connHandle_p) {
    const int RC_addHandle_OK             =  0;
    const int RC_addHandle_LSCL_BAD_STATE =  8;

    PloCompareAndSwapAreaQuadWord_t swapArea;
    PloStoreAreaQuadWord_t          storeArea1;  // The Header
    PloStoreAreaQuadWord_t          storeArea2;  // The Last_Element->Next or not used
    ElementDT                       header;
    int                             ploRC;
    int                             rc = RC_addHandle_OK;

    LocalComPairPLO_CS_Area_t       localReplacePLO;

    swapArea.compare_p     = &(pair_p->lscl_PLO_CS_Area);
    memcpy(&(swapArea.expectedValue), swapArea.compare_p, sizeof(swapArea.expectedValue));

    // Set the new header as target update area.
    storeArea1.storeLocation_p = &(pair_p->firstInUseConnHdl_p);

    do {
        // Build new SwapArea
        memcpy(&localReplacePLO, &(swapArea.expectedValue), sizeof(localReplacePLO));
        localReplacePLO.ploSequenceNumber+=1;
        memcpy(&(swapArea.replaceValue), &localReplacePLO, sizeof(swapArea.replaceValue));

        // Check the state of the LSCL
        if ((localReplacePLO.flags.clientLDAT_detach == 1) ||
            (localReplacePLO.flags.serverLDAT_detach == 1)) {
            rc = RC_addHandle_LSCL_BAD_STATE;
            break;
        }


        // Grab current queue header info
        memcpy(&(header), &(pair_p->firstInUseConnHdl_p), sizeof(header));


        // If the queue is currently empty...
        if (header.element_next_p == NULL) {
            clearPtrs((ElementDT*) &(connHandle_p->nextHandle_p));

            // Set the head and tail to this element
            header.element_next_p = (ElementDT*) connHandle_p;
            header.element_prev_p = (ElementDT*) connHandle_p;

            // Set the new header.
            memcpy(&(storeArea1.storeValue), &header, sizeof(storeArea1.storeValue));

            ploRC = ploCompareAndSwapAndStoreQuadWord(swapArea.compare_p, &swapArea, &storeArea1);
        }
        else {
            // Add the new element to the end of the list
            connHandle_p->nextHandle_p = NULL;                        // next = null
            connHandle_p->prevHandle_p = (LocalCommConnectionHandle_t*) header.element_prev_p;       // prev = tail

            // Set the current tail->next = new element
            storeArea2.storeLocation_p = &(((LocalCommConnectionHandle_t*)(header.element_prev_p))->nextHandle_p);
            memcpy(&(storeArea2.storeValue), storeArea2.storeLocation_p, sizeof(storeArea2.storeValue));
            ((ElementDT*) &(storeArea2.storeValue))->element_next_p = (ElementDT*) connHandle_p;

            // Set the tail = new element
            header.element_prev_p = (ElementDT*) connHandle_p;
            memcpy(&(storeArea1.storeValue), &header, sizeof(storeArea1.storeValue));

            ploRC = ploCompareAndSwapAndDoubleStoreQuadWord(swapArea.compare_p, &swapArea, &storeArea1, &storeArea2);
        }

    } while (ploRC);

    return rc;
}

/**
 * Remove a Connection handle from the active connection list.
 * @param pair_p Pointer to LSCL representing a connection pair.
 * @param connHandle_p Pointer to connection handle to remove.
 * @return 1 queue is now empty or 0 queue still has at least one connection.
 */
static int removeHandle(LocalCommClientServerPair_t* pair_p,
                        LocalCommConnectionHandle_t* connHandle_p) {

    int                             localRC;   // Assume queue is still non-empty

    PloCompareAndSwapAreaQuadWord_t swapArea;
    PloStoreAreaQuadWord_t          storeArea1;  // The Header or Previous_Element->Next
    PloStoreAreaQuadWord_t          storeArea2;  // The Next_Element->Prev or Previous_Element->Next or not used
    ElementDT                       header;
    ElementDT                       targetElement;
    int                             ploRC;

    LocalComPairPLO_CS_Area_t       localReplacePLO;

    swapArea.compare_p     = &(pair_p->lscl_PLO_CS_Area);
    memcpy(&(swapArea.expectedValue), swapArea.compare_p, sizeof(swapArea.expectedValue));

    do {
        localRC=0;  // Assume queue is still non-empty

        // Build new SwapArea
        memcpy(&localReplacePLO, &(swapArea.expectedValue), sizeof(localReplacePLO));
        localReplacePLO.ploSequenceNumber+=1;
        memcpy(&(swapArea.replaceValue), &localReplacePLO, sizeof(swapArea.replaceValue));

        // Grab current queue header info
        memcpy(&(header), &(pair_p->firstInUseConnHdl_p), sizeof(header));

        // Grab the target element's pointer info
        memcpy(&(targetElement), &(connHandle_p->nextHandle_p), sizeof(targetElement));


        // Check if the target handle is the only handle in the queue (if so, we can use the CompareSwap and single store)
        if ((header.element_next_p == header.element_prev_p) && (header.element_prev_p == (ElementDT*) connHandle_p)) {
            // Set the queue to be empty.
            storeArea1.storeLocation_p = &(pair_p->firstInUseConnHdl_p);
            memset(&(storeArea1.storeValue), 0, sizeof(storeArea1.storeValue));

            localRC = 1;  // Queue is going to be empty

            ploRC = ploCompareAndSwapAndStoreQuadWord(swapArea.compare_p, &swapArea, &storeArea1);
        }
        else {
            // We'll need to use the CompareSwap and double store for the other cases.

            // If the target element is the FIRST but not only
            if (header.element_next_p == (ElementDT*) connHandle_p) {
                // Set the head = target.next
                header.element_next_p = targetElement.element_next_p;
                storeArea1.storeLocation_p = &(pair_p->firstInUseConnHdl_p);
                memcpy(&(storeArea1.storeValue), &header, sizeof(storeArea1.storeValue));

                // Zero the next elements previous pointer
                storeArea2.storeLocation_p = &(((LocalCommConnectionHandle_t*)(targetElement.element_next_p))->nextHandle_p);
                memcpy(&(storeArea2.storeValue), storeArea2.storeLocation_p, sizeof(storeArea2.storeValue));
                ((ElementDT*) &(storeArea2.storeValue))->element_prev_p = NULL;
            } else if (header.element_prev_p == (ElementDT*) connHandle_p) {
                // else if its the LAST element, but not only

                // Set the tail to target element's previous
                header.element_prev_p = targetElement.element_prev_p;
                storeArea1.storeLocation_p = &(pair_p->firstInUseConnHdl_p);
                memcpy(&(storeArea1.storeValue), &header, sizeof(storeArea1.storeValue));

                // Zero the previous element's next pointer
                storeArea2.storeLocation_p = &(((LocalCommConnectionHandle_t*)(targetElement.element_prev_p))->nextHandle_p);
                memcpy(&(storeArea2.storeValue), storeArea2.storeLocation_p, sizeof(storeArea2.storeValue));
                ((ElementDT*) &(storeArea2.storeValue))->element_next_p = NULL;
            } else {
                // its a MIDDLE element, not either a head or tail

                // Set the previous element's next pointer = targetElement.next
                storeArea1.storeLocation_p = &(((LocalCommConnectionHandle_t*)(targetElement.element_prev_p))->nextHandle_p);
                memcpy(&(storeArea1.storeValue), storeArea1.storeLocation_p, sizeof(storeArea1.storeValue));
                ((ElementDT*) &(storeArea1.storeValue))->element_next_p = targetElement.element_next_p;

                // Set the next element's previous pointer = targetElement.prev
                storeArea2.storeLocation_p = &(((LocalCommConnectionHandle_t*)(targetElement.element_next_p))->nextHandle_p);
                memcpy(&(storeArea2.storeValue), storeArea2.storeLocation_p, sizeof(storeArea2.storeValue));
                ((ElementDT*) &(storeArea2.storeValue))->element_prev_p = targetElement.element_prev_p;
            }

            ploRC = ploCompareAndSwapAndDoubleStoreQuadWord(swapArea.compare_p, &swapArea, &storeArea1, &storeArea2);
        }

    } while (ploRC);

    clearPtrs((ElementDT*) &(connHandle_p->nextHandle_p));

    return localRC;
}

/**
 * Return an indication that a valid connection handle exists in the LSCL
 * @param pair_p Pointer the LSCL
 * @return 1 if a valid connection exists, otherwise 0.
 */
static int anyValidConnections(LocalCommClientServerPair_t* pair_p) {
    int localRC = 0;
    unsigned long long currentPLO_Seq = pair_p->lscl_PLO_CS_Area.ploSequenceNumber;

    // Grab the plo sequence # before starting scan...At each loop need to re-check it, if it changes start over.
    for (LocalCommConnectionHandle_t* currentConnHandle_p = pair_p->firstInUseConnHdl_p;
        currentConnHandle_p != NULL;
        ) {

        // If something changed with the LSCL, then let's start over
        if (currentPLO_Seq != pair_p->lscl_PLO_CS_Area.ploSequenceNumber) {
            currentPLO_Seq = pair_p->lscl_PLO_CS_Area.ploSequenceNumber;
            currentConnHandle_p = pair_p->firstInUseConnHdl_p;
            continue;
        }

        if ((currentConnHandle_p->handlePLO_CS.flags.clientInitiatedClosing == 0) &&
            (currentConnHandle_p->handlePLO_CS.flags.serverInitiatedClosing == 0)) {
            localRC = 1;
            break;
        }

        currentConnHandle_p = currentConnHandle_p->nextHandle_p;
    }

    return localRC;
}

/**
 * Gets a connection handle from the connection handle pool.
 *
 * @param anchor_p A pointer to the BBGZLOCL.
 *
 * @return A pointer to a new connection handle, or NULL if one could not be obtained.
 */
static LocalCommConnectionHandle_t* getConnectionHandleFromPool(LocalCommClientAnchor_t* anchor_p) {
    LocalCommConnectionHandle_t* connHdl_p = NULL;
    unsigned char tryAgain = TRUE;

    while ((connHdl_p == NULL) && (tryAgain == TRUE)) {
        // Try to get a cell from the cell pool.  If no cells, grow the pool.
        connHdl_p = getCellPoolCell(anchor_p->connHandlePool);
        if (connHdl_p == NULL) {
            long long extentSize = computeCellPoolExtentStorageRequirement(anchor_p->numConnHandlesPerExtent);
            long long cellSize = (anchor_p->numConnHandlesPerExtent) * sizeof(LocalCommConnectionHandle_t);
            void* cellStorage = getLocalCommSharedStorage(&(anchor_p->info), extentSize + cellSize);
            void* extentStorage = (void*)(((char*)cellStorage) + cellSize);
            if (cellStorage != NULL) {
                growCellPool(anchor_p->connHandlePool, anchor_p->numConnHandlesPerExtent, extentStorage, extentSize, cellStorage, cellSize);
            } else {
                tryAgain = FALSE;
            }
        }
    }

    return connHdl_p;
}

static void freeConnectionHandleToPool(LocalCommClientAnchor_t* inLOCL_p, LocalCommConnectionHandle_t* connHandle_p) {
    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(TP_SERVER_LCOM_CLIENT_FREELHDL_ENTRY),
                    "freeConnectionHandleToPool",
                    TRACE_DATA_PTR(inLOCL_p,
                                   "target BBGZLOCL ptr"),
                    TRACE_DATA_PTR(connHandle_p,
                                  "BBGZLHDL ptr"),
                    TRACE_DATA_END_PARMS);
    }

    // Flip eyecatcher
    memcpy(connHandle_p->eyecatcher, RELEASED_BBGZLHDL_EYE, sizeof(connHandle_p->eyecatcher));

    freeCellPoolCell(inLOCL_p->connHandlePool, connHandle_p);
}

/**
 * Initialize a connection Handle and add it to the LSCL list
 *
 * @param connHandle_p pointer to the connection handle to initialize
 * @param pair_p pointer to the client/server pair (LSCL)
 * @param clientHandle pointer to client connection handle
 * @return 0 if a new handle was created and added to the LSCL, 8 if not.
 */
static int initializeHandle(LocalCommConnectionHandle_t* connHandle_p,
                            LocalCommClientServerPair_t* pair_p,
                            LocalCommClientConnectionHandle_t* clientHandle) {
    int localRC = 0;

    // Initialize storage if this is not a released connection handle.
    // Keep the instance count (just use whatever was there in storage).
    if (memcmp(connHandle_p->eyecatcher, RELEASED_BBGZLHDL_EYE, sizeof(connHandle_p->eyecatcher)) != 0) {
        unsigned int instanceCount = connHandle_p->instanceCount ? connHandle_p->instanceCount : 10;
        memset(connHandle_p, 0, sizeof(LocalCommConnectionHandle_t));
        connHandle_p->instanceCount = instanceCount;
    }

    // Build Connection handle token for client references
    clientHandle->handle_p = connHandle_p;
    clientHandle->instanceCount = connHandle_p->instanceCount;
    memset(clientHandle->_available, 0, sizeof(clientHandle->_available));

    memcpy(connHandle_p->eyecatcher, BBGZLHDL_EYE, sizeof(connHandle_p->eyecatcher));
    connHandle_p->version = BBGZLHDL_INITIAL_VERSION;
    connHandle_p->length = sizeof(LocalCommConnectionHandle_t);

    // Preserve Seq# clear the rest (flags, queue counts, )
    unsigned int oldSeqNum = connHandle_p->handlePLO_CS.ploSequenceNumber;
    memset(&(connHandle_p->handlePLO_CS), 0, sizeof(connHandle_p->handlePLO_CS));
    connHandle_p->handlePLO_CS.ploSequenceNumber = oldSeqNum;


    connHandle_p->clientASID = ((ascb*)(((psa*)0)->psaaold))->ascbasid;
    connHandle_p->bbgzlscl_p = pair_p;

    // Initialize Inbound queues
    initializeDirectionalQueue(&(connHandle_p->clientInboundQ),
                               connHandle_p,
                               &(connHandle_p->localWorkQueue),
                               pair_p->localCommClientControlBlock_p,
                               &(connHandle_p->localDataQueues[0]),
                               pair_p->localCommClientControlBlock_p
                               );

    // Initialize Outbound Queues
    initializeDirectionalQueue(&(connHandle_p->clientOutboundQ),
                               connHandle_p,
                               &(pair_p->serverLOCL_p->serverSpecificInfo.serverAsClientWorkQ),
                               NULL,
                               &(connHandle_p->localDataQueues[1]),
                               pair_p->localCommClientControlBlock_p
                               );

    // Create footprint table if needed
    if (connHandle_p->footprintTable == NULL) {
        createLocalCommConnectionFootprintTable(connHandle_p);
    }

    // Add new connection to the LSCL connection handle chain
    localRC = addHandle(pair_p, connHandle_p);

    if (localRC != 0) {
        localRC = 8;
    }

    return localRC;
}

/**
 * Cleanup a Connection Handle
 * @param connHandle_p Pointer to the Connection Handle to cleanup.
 * @return 0
 */
static int cleanupHandle(LocalCommClientAnchor_t* inLOCL_p,LocalCommConnectionHandle_t* connHandle_p) {
    // Release resources...

    // Reuse the footprintTable

    // Bump the instance counter to invalidate the client's connection handle.
    connHandle_p->instanceCount = connHandle_p->instanceCount + 1;

    // Return the handle to the cell pool.
    freeConnectionHandleToPool(inLOCL_p, connHandle_p);
    return 0;
}

/**
 * Return indication if the current address space is the client-side of a connection.
 * @param connHandle_p Pointer to the Connection Handle.
 * @return 1 if we in the client-side of a connection, otherwise 0.
 */
int amIClientSide(LocalCommConnectionHandle_t* connHandle_p) {
    int localRC = 0;
    if (connHandle_p->clientASID == ((ascb*)(((psa*)0)->psaaold))->ascbasid) {
        localRC = 1;
    }
    return localRC;
}

/**
 * Set the Closing initiated flag.
 * @param connHandle_p Pointer to the Connection Handle.
 * @return 0 if set, 8 if the otherside of the connection had already set its closing flag,
 * 12 if the connection was already marked for closure.
 */
static int startHandleClosing(LocalCommConnectionHandle_t* connHandle_p) {
    int localRC;

    PloCompareAndSwapAreaQuadWord_t swapArea;
    int                             ploRC;

    LocalCommHandle_PLO_Area_t*     localReplacePLO_p = (LocalCommHandle_PLO_Area_t*)&(swapArea.replaceValue);

    swapArea.compare_p = &(connHandle_p->handlePLO_CS);
    memcpy(&(swapArea.expectedValue), swapArea.compare_p, sizeof(swapArea.expectedValue));


    do {
        localRC = 0;

        //TODO: Validate handle here ...

        // Build new SwapArea
        memcpy(localReplacePLO_p, &(swapArea.expectedValue), sizeof(*localReplacePLO_p));
        localReplacePLO_p->ploSequenceNumber+=1;

        // Mark Connection closing by this side of the connection unless the other side beat us to it.
        if (amIClientSide(connHandle_p)) {
            if (localReplacePLO_p->flags.clientInitiatedClosing == 0) {
                if (localReplacePLO_p->flags.serverInitiatedClosing == 1) {
                    localRC = LCOM_STARTHANDLECLOSING_OTHERSIDE_CLOSING;
                } else {
                    localReplacePLO_p->flags.clientInitiatedClosing = 1;
                }
            } else {
                localRC = LCOM_STARTHANDLECLOSING_ALREADY_CLOSING;
            }
        } else if (localReplacePLO_p->flags.serverInitiatedClosing == 0) {
            if (localReplacePLO_p->flags.clientInitiatedClosing == 1) {
                localRC = LCOM_STARTHANDLECLOSING_OTHERSIDE_CLOSING;
            } else {
                localReplacePLO_p->flags.serverInitiatedClosing = 1;
            }
        } else {
            localRC = LCOM_STARTHANDLECLOSING_ALREADY_CLOSING;
        }

        ploRC = ploCompareAndSwapQuadWord(swapArea.compare_p, &swapArea);

    } while (ploRC);

    return localRC;
}

/**
 * Mark the connection handle as closed, return an indication if caller should
 * own the rest of the cleanup for this connection handle.
 *
 * @param connHandle_p Pointer the Connection Handle.
 * @return LCOM_MARKHANDLECLOSED_OTHERSIDE_CLOSED if caller owns rest of cleanup.
 */
static int markHandleClosed(LocalCommConnectionHandle_t* connHandle_p) {
    int localRC;

    PloCompareAndSwapAreaQuadWord_t swapArea;
    int                             ploRC;

    LocalCommHandle_PLO_Area_t*     localReplacePLO_p = (LocalCommHandle_PLO_Area_t*)&(swapArea.replaceValue);

    swapArea.compare_p = &(connHandle_p->handlePLO_CS);
    memcpy(&(swapArea.expectedValue), swapArea.compare_p, sizeof(swapArea.expectedValue));

    do {
        localRC = 0;

        //TODO: Validate handle here ...

        // Build new SwapArea
        memcpy(localReplacePLO_p, &(swapArea.expectedValue), sizeof(*localReplacePLO_p));
        localReplacePLO_p->ploSequenceNumber+=1;

        // Mark Connection closed on this side of the connection.
        if (amIClientSide(connHandle_p)) {
            localReplacePLO_p->flags.clientClosed = 1;

            if (localReplacePLO_p->flags.serverClosed == 1) {
                localRC = LCOM_MARKHANDLECLOSED_OTHERSIDE_CLOSED;
            }
        } else {
            localReplacePLO_p->flags.serverClosed = 1;

            if (localReplacePLO_p->flags.clientClosed == 1) {
                localRC = LCOM_MARKHANDLECLOSED_OTHERSIDE_CLOSED;
            }
        }

        ploRC = ploCompareAndSwapQuadWord(swapArea.compare_p, &swapArea);

    } while (ploRC);

    return localRC;
}

// detach from LDAT and LOCL.

/**
 * Detach from LDAT and LOCL shared memory objects
 * @param otherSideLOCL_p Pointer to the BBGZLOCL to detach
 * @param dataStore_p Pointer to the BBGZLDAT to detach
 * @param sharedMemoryUserToken User token value to use in detach
 * @return 0
 */
int detachFromLDATandLOCL(LocalCommClientAnchor_t* otherSideLOCL_p, LocalCommClientDataStore_t* dataStore_p, long long sharedMemoryUserToken) {
    int localRC = 0, localRSN = 0;

    // Detach from Client LDAT
    if (dataStore_p) {
        SToken ldatServerStoken __attribute__((aligned(8))) = dataStore_p->serverStoken;

        // Detach System Affinity.  Watch the usertoken.
        //
        // Only detach system affinity from side that created the shared memory object (client).
        if (memcmp(&(ldatServerStoken), &(((assb*)(((ascb*)(((psa*)0)->psaaold))->ascbassb))->assbstkn), sizeof(ldatServerStoken)) != 0) {
            long long allocationUserToken = getAddressSpaceSupervisorStateUserToken();
            localRC = detachSharedAboveConditional(dataStore_p, allocationUserToken, TRUE, &localRSN);

            // Flip eyecatcher.
            memcpy(dataStore_p->eyecatcher, RELEASED_BBGZLDAT_EYE, sizeof(dataStore_p->eyecatcher));
        }

        // Try to detach our access to the LDAT.
        localRC = detachSharedAboveConditional(dataStore_p, sharedMemoryUserToken, FALSE, &localRSN);


    }


    // Detach from other side of connection's LOCL
    localRC = detachSharedAboveConditional(otherSideLOCL_p, sharedMemoryUserToken, FALSE, &localRSN);
    //pair_p->serverLOCL_p = NULL;


    return 0;
}

/**
 * Cleanup anything attached to the LOCL so that it's system affinity can be removed.
 */
void prepareLoclForSystemDetach(LocalCommClientAnchor_t* locl_p) {
    // Free the PETs in the PETVET attached to the LOCL.  Note that the
    // spca_p might not point to the PETVET in the LOCL.  We only want to
    // clean up the one in the LOCL.
    destroyPetVetPool(&(locl_p->clientSpcaBranch));
}

/**
 * Get the address of the BBGZLSCL control block between this client and the
 * specified server.
 *
 * This function returns the state of LSCL/LOCL (lsclCurrentState) when the search concludes.
 * The lsclCurrentState must be supplied on the add LSCL function call, if the
 * state has changed then the add function will be failed.  The callers must re-examine LSCL chain by
 * reissuing a call to this routine.
 *
 * @param clientProcessDataToken The token generated by the angel for this bind between client and
 *        server.  This token disambiguates two server instances which have shared the same STOKEN
 *        (one which has gone down but we're still bound to it, the other just came up and we bound
 *        to it already).
 * @param anchor_p A pointer to the local comm client anchor.
 * @param out_lsclCurrentState_p Pointer to an output area to contain a sequence number representing the
 * current state of the LSCL/LOCL when search finished.
 *
 * @return A pointer to the BBGZLSCL control block, or NULL if one is not found.
 */
LocalCommClientServerPair_t* getLocalCommClientServerPair(unsigned long long clientProcessDataToken, LocalCommClientAnchor_t* anchor_p, LocalComLOCL_PLO_CS_Area_t* out_lsclCurrentState_p) {
    LocalCommClientServerPair_t* targetPair_p = NULL;
    LocalCommClientServerPair_t* cur_p;

    LocalComLOCL_PLO_CS_Area_t localCurrentState = anchor_p->locl_PLO_CS_Area;

    cur_p = anchor_p->firstLSCL_p;  // Make sure grab after getting the Seq#.

    // Grab the plo sequence # before starting scan...At each loop need to re-check it, if it changes start over.
    while ((targetPair_p == NULL) && (cur_p != NULL)) {

        // If something changed with the LSCL, then let's start over
        if (localCurrentState.ploSequenceNumber != anchor_p->locl_PLO_CS_Area.ploSequenceNumber) {
            localCurrentState = anchor_p->locl_PLO_CS_Area;
            cur_p = anchor_p->firstLSCL_p;
            continue;
        }

        if (clientProcessDataToken == cur_p->clientProcessDataToken) {
            targetPair_p = cur_p;
        } else {
            cur_p = cur_p->nextLSCL_p;
        }
    }

    *out_lsclCurrentState_p = localCurrentState;

    return targetPair_p;
}

/**
 * Indicates if the LSCL is still healthy.
 *
 * @param targetPair_p Pointer to a LSCL
 * @return 1 if all is fine, 0 if the LSCL is being cleaned up or ??
 */
static int isHealthy(LocalCommClientServerPair_t* targetPair_p) {
    // Check eyecatcher too?
    return ((targetPair_p->lscl_PLO_CS_Area.flags.clientLDAT_detach || targetPair_p->lscl_PLO_CS_Area.flags.serverLDAT_detach) ? 0 : 1);
}

#define CREATE_LSCL_RC_QUEUE_MAYHAVECHANGED    8
#define CREATE_LSCL_RC_LDAT_NO_STORAGE        12
#define CREATE_LSCL_RC_LSCL_NO_STORAGE        16
#define CREATE_LSCL_RC_SERVER_LOCL_NOTFOUND   20
/**
 * Create a new BBGZLSCL control block between this client and the specified server,
 * and its associated BBGZLDAT message store.  The new BBGZLSCL will be added to the
 * chain hung off of the BBGZLOCL.
 *
 * The caller also must verify that a BBGZLSCL does not exist between this client
 * and the specified server.  To accomplish this, the caller must obtained the current
 * state of the LOCL/LSCL (locl_PLO_CS_Area), then scan the LSCL queue.  If no LSCL
 * was found on the queue then the state (locl_PLO_CS_Area) should be provided on the
 * call to this routine.
 *
 * @param serverStoken_p The stoken of the server.
 * @param clientProcessDataToken The token generated by the angel for this client->server bind.
 *                               This disambiguates the stoken when the server is a BPXAS.
 * @param anchor_p A pointer to the local comm client anchor.
 * @param pair_p_p Pointer to return the pointer to the new BBGZLSCL or NULL.
 * @param lsclRequiredState_p Pointer to a PLO CS Area representing a required state prior to updates.
 *
 * @return 0 if a new LSCL was created and returned (pair_p_p),
 *         8 if the current state of the LOCL/LSCL queue has changed when compared using lsclCurrentState_p,
 *         12 if LDAT storage could not be obtained,
 *         16 if LSCL storage could not be obtained,
 *         20 if the Server LOCL could not be found.
 */
static int createLocalCommClientServerPair(SToken* serverStoken_p,
                                           unsigned long long clientProcessDataToken,
                                           LocalCommClientAnchor_t* anchor_p,
                                           LocalCommClientServerPair_t** pair_p_p,
                                           LocalComLOCL_PLO_CS_Area_t* lsclRequiredState_p) {
    int localRC = 0;

    // NOTE: Local comm shared memory cleanup utility depends on this user token being the STOKEN.
    long long allocationUserToken = getAddressSpaceSupervisorStateUserToken();
    long long sharingUserToken;  // Using LSCL Address

    LocalCommClientServerPair_t* pair_p = NULL;

    // Create the LSCL and chain to the LDAT that we just made.
    pair_p = getClientServerPairFromPool(anchor_p);

    if (pair_p != NULL) {
        // Using LSCL to attach to Server's LOCL
        sharingUserToken = (long long) pair_p;

        LocalCommClientDataStore_t* dataStore_p = createLocalCommDataStore(serverStoken_p, anchor_p, allocationUserToken, sharingUserToken);

        if (dataStore_p != NULL) {
            memset(pair_p, 0, sizeof(LocalCommClientServerPair_t));
            memcpy(pair_p->eyecatcher, BBGZLSCL_EYE, sizeof(pair_p->eyecatcher));
            pair_p->version = 1;
            pair_p->length = sizeof(LocalCommClientServerPair_t);
            memcpy(&(pair_p->serverStoken), serverStoken_p, sizeof(pair_p->serverStoken));
            pair_p->clientProcessDataToken = clientProcessDataToken;
            pair_p->firstDataStore_p = dataStore_p;
            pair_p->localCommClientControlBlock_p = anchor_p;

            // Look up the target server's LOCL buffer and connect to it.  Note that we're
            // not using the clientProcessDataToken here, we're just going to get the active LOCL.
            pair_p->serverLOCL_p = getLOCL_address(serverStoken_p);
            if (pair_p->serverLOCL_p != NULL) {
                // Attach to the Server's LOCL using this LSCL Address as the user token.
                accessSharedAbove(pair_p->serverLOCL_p, sharingUserToken);

                // Chain up the new data store and client/server pair.
                // if queue changed then we need to free LSCL and return the indication..so caller can loop and try again.
                localRC = addLSCL(anchor_p, pair_p, lsclRequiredState_p);

                if (localRC != 0) {
                    // Detach from Server's LOCL
                    detachSharedAbove(pair_p->serverLOCL_p, sharingUserToken, FALSE);

                    // Free LSCL
                    freeCellPoolCell(anchor_p->clientServerPairPool, pair_p);
                    pair_p = NULL;

                    // Detach from LDAT
                    detachSharedAbove(dataStore_p, sharingUserToken, FALSE);
                    // Detach System Affinity too. Watch the usertoken.
                    detachSharedAbove(dataStore_p, allocationUserToken, TRUE);
                    dataStore_p = NULL;

                    localRC = CREATE_LSCL_RC_QUEUE_MAYHAVECHANGED;
                }
            } else {
                // Free LSCL
                freeCellPoolCell(anchor_p->clientServerPairPool, pair_p);
                pair_p = NULL;

                // Detach from LDAT
                detachSharedAbove(dataStore_p, sharingUserToken, FALSE);
                // Detach System Affinity too. Watch the usertoken.
                detachSharedAbove(dataStore_p, allocationUserToken, TRUE);
                dataStore_p = NULL;

                localRC = CREATE_LSCL_RC_SERVER_LOCL_NOTFOUND;
            }
        } else {
            // Free LSCL
            freeCellPoolCell(anchor_p->clientServerPairPool, pair_p);
            pair_p = NULL;

            localRC = CREATE_LSCL_RC_LDAT_NO_STORAGE;
        }
    } else {
        localRC = CREATE_LSCL_RC_LSCL_NO_STORAGE;
    }


    *pair_p_p = pair_p;

    return localRC;
}

#define LCOM_LSCL_CHECKSET_NOCLEANUP     0x00000000
#define LCOM_LSCL_CHECKSET_DETACHSHARED  0x00000040
#define LCOM_LSCL_CHECKSET_LSCLCLEANUP   0x00000080
/**
 * See if we need to cleanup the LSCL and detach from the Shared memory (LDAT and LOCL).
 * We need to detach from the LDAT if all existing connection handles are mark "closed" AND we successfully set
 * the LSCL Detach flag.
 *
 * If we own the connection handle cleanup then we need to remove it from the chain under this serialized method.
 * The reason is that we may not end up owning the LSCL cleanup.  So, we can't let the other side cleanup the LSCL
 * if we still need to de-chain the handle from the LSCL.
 *
 * Note: We could have multiple closes happening at the same time from the same side. So, the LSCL handle chain would look
 * all closed for both...need to serialized on the setting of the LDAT flag to solve that.  Also, if this side of close
 * issue the LDAT_Detach last, then it also owns the LSCL cleanup.
 *
 * @param connHandle_p Pointer to connection handle being closed.
 * @param weOwnHandleCleanup Indication that the caller owns the connection handle cleanup.
 * @return LCOM_LSCL_CHECKSET_NOCLEANUP if no additional cleanup responsibility assigned;
 * LCOM_LSCL_CHECKSET_DETACHSHARED if the caller is assigned responsibility of detaching from the from LDAT and other-side LOCL;
 * LCOM_LSCL_CHECKSET_LSCLCLEANUP if the caller is assigned responsibility of further cleanup on the LSCL (remove from chain and pool).
 *
 * @note: Multiple return responsibilities may be assigned.  (ex. both LCOM_LSCL_CHECKSET_DETACHSHARED and LCOM_LSCL_CHECKSET_LSCLCLEANUP
 * may be set on the same return.
 */
static int checkSetLSCL_Detach(LocalCommConnectionHandle_t* connHandle_p, int weOwnHandleCleanup) {
    int localRC = LCOM_LSCL_CHECKSET_NOCLEANUP;

    // Mark this volatile because someone else can modify it while we're in here.
    LocalCommClientServerPair_t volatile * pair_p = connHandle_p->bbgzlscl_p;

    int  emptyHandleQueue;
    if (weOwnHandleCleanup) {
        // Remove handle from LSCL chain
        removeHandle(connHandle_p->bbgzlscl_p, connHandle_p);
    }

    // Mark this volatile too because PLO can change the expected value on us.
    PloCompareAndSwapAreaQuadWord_t volatile swapArea;
    int                             ploRC;

    int                             callerToDetachSharedMem,
                                    callerOwnsLSCL_Cleanup;

    LocalComPairPLO_CS_Area_t       localReplacePLO;

    swapArea.compare_p = (void* volatile) &(pair_p->lscl_PLO_CS_Area);
    memcpy((void*)&(swapArea.expectedValue), swapArea.compare_p, sizeof(swapArea.expectedValue));

    do {
        emptyHandleQueue        = 1;
        callerToDetachSharedMem = 0;
        callerOwnsLSCL_Cleanup  = 0;

        // Copy SwapArea (referencing the PLO Seq# below prior to updating to new value)
        memcpy(&localReplacePLO, (void*)&(swapArea.expectedValue), sizeof(localReplacePLO));

        // Scan the Handle queue for a "Good" handle.
        // At each loop check the PLO Seq#, if it changes start over.
        for (LocalCommConnectionHandle_t* currentConnHandle_p = pair_p->firstInUseConnHdl_p;
            currentConnHandle_p != NULL;
            ) {

            // If something changed with the LSCL, then let's start over
            if (localReplacePLO.ploSequenceNumber != pair_p->lscl_PLO_CS_Area.ploSequenceNumber) {
                currentConnHandle_p = pair_p->firstInUseConnHdl_p;

                // Need to replace both expected and replace values since the replace
                // value is initialized outside of the for loop that we are continuing on.
                memcpy((void*)&(swapArea.expectedValue), swapArea.compare_p, sizeof(swapArea.expectedValue));
                memcpy(&localReplacePLO, (void*)&(swapArea.expectedValue), sizeof(localReplacePLO));
                continue;
            }

            // Check if the Connection is not going through close processing for this side of the connection
            if (amIClientSide(connHandle_p)) {
                if (currentConnHandle_p->handlePLO_CS.flags.clientInitiatedClosing == 0) {
                    emptyHandleQueue = 0;
                    break;
                }
            } else {
                // Server-side driven close processing
                if (currentConnHandle_p->handlePLO_CS.flags.serverInitiatedClosing == 0) {
                    emptyHandleQueue = 0;
                    break;
                }
            }

            currentConnHandle_p = currentConnHandle_p->nextHandle_p;
        }

        // If the Pairing no longer has any "Good" connections, time to cleanup the LSCL...(well, at least
        // let the caller know its time :-))
        if (emptyHandleQueue) {
            callerToDetachSharedMem = 1;

            // Update the appropriate LSCL flags based on which side of the connection we are.
            if (amIClientSide(connHandle_p)) {
                localReplacePLO.flags.clientLDAT_detach  = 1;

                if (localReplacePLO.flags.serverLDAT_detach == 1) {
                    callerOwnsLSCL_Cleanup = 1;
                }
            } else {
                localReplacePLO.flags.serverLDAT_detach = 1;

                if (localReplacePLO.flags.clientLDAT_detach == 1) {
                    callerOwnsLSCL_Cleanup = 1;
                }
            }

            // Create the new replace area
            localReplacePLO.ploSequenceNumber+=1;
            memcpy((void*)&(swapArea.replaceValue), &localReplacePLO, sizeof(swapArea.replaceValue));

            ploRC = ploCompareAndSwapQuadWord(swapArea.compare_p, (struct ploCompareAndSwapAreaQuadWord*)&swapArea);
        } else {
            // Nothing to cleanup...LSCL still in use.
            break;
        }
    } while (ploRC);

    if (callerOwnsLSCL_Cleanup == 1) {
        localRC += LCOM_LSCL_CHECKSET_LSCLCLEANUP;
    }

    if (callerToDetachSharedMem == 1) {
        localRC += LCOM_LSCL_CHECKSET_DETACHSHARED;
    }

    return localRC;
}

/**
 * Cleanup then pool the specified LSCL
 * @param bbgzlscl_p Pointer to LSCL to cleanup and pool.
 * @return 0 if removed and cleaned up.  1 if not found.
 */
int cleanupLSCL(LocalCommClientAnchor_t* locl, LocalCommClientServerPair_t* bbgzlscl_p) {
    LocalCommClientServerPair_t*      prevElement;

    PloCompareAndSwapAreaDoubleWord_t swapArea;
    PloStoreAreaDoubleWord_t          storeArea;  // Head of queue or Previous_Element
    int                               ploRC;
    LocalComLOCL_PLO_CS_Area_t        localReplacePLO;
    int                               foundLSCL;

    swapArea.compare_p = &(locl->locl_PLO_CS_Area);
    memcpy(&(swapArea.expectedValue), swapArea.compare_p, sizeof(swapArea.expectedValue));

    do {
        foundLSCL = 0;

        // Build new SwapArea
        memcpy(&localReplacePLO, &(swapArea.expectedValue), sizeof(localReplacePLO));
        localReplacePLO.ploSequenceNumber+=1;
        memcpy(&(swapArea.replaceValue), &localReplacePLO, sizeof(swapArea.replaceValue));

        prevElement = locl->firstLSCL_p;

        if (prevElement == NULL) {
            return 1;
        }

        if (prevElement == bbgzlscl_p) {
            // Matched first element, remove.
            storeArea.storeLocation_p = &(locl->firstLSCL_p);
            memcpy(&(storeArea.storeValue), &(prevElement->nextLSCL_p), sizeof(storeArea.storeValue));

            ploRC = ploCompareAndSwapAndStoreDoubleWord(swapArea.compare_p, &swapArea, &storeArea);
        } else {
            // Scan queue for target LSCL
            for (LocalCommClientServerPair_t* currentElement = prevElement->nextLSCL_p;
                 currentElement != NULL;
                 currentElement = currentElement->nextLSCL_p) {
                if (currentElement == bbgzlscl_p) {
                    foundLSCL = 1;
                    storeArea.storeLocation_p = &(prevElement->nextLSCL_p);
                    memcpy(&(storeArea.storeValue), &(currentElement->nextLSCL_p), sizeof(storeArea.storeValue));

                    ploRC = ploCompareAndSwapAndStoreDoubleWord(swapArea.compare_p, &swapArea, &storeArea);
                    break;
                } else {
                    prevElement = currentElement;
                }
            }

            // If we didn't find the target LSCL, make sure the queue didn't change during our scan.  If the queue
            // did NOT change then the LSCL was not in the queue.  Otherwise, the queue changed and we need to
            // reexamine the queue.
            if (foundLSCL == 0) {
                PloLoadAreaDoubleWord_t loadArea;
                loadArea.loadLocation_p = locl;   // Picking any Double to load.

                ploRC = ploCompareAndLoadDoubleWord(swapArea.compare_p, &swapArea, &loadArea);
                if (ploRC != 0) {
                    continue;
                }
            }
        }
    } while (ploRC);

    // Free related resources...

    if ((foundLSCL == 1) && (memcmp(bbgzlscl_p->eyecatcher, BBGZLSCL_EYE, sizeof(bbgzlscl_p->eyecatcher)) == 0)) {
        // Flip eyecatcher
        memcpy(bbgzlscl_p->eyecatcher, RELEASED_BBGZLSCL_EYE, sizeof(bbgzlscl_p->eyecatcher));

        // Free LSCL storage.
        freeCellPoolCell(locl->clientServerPairPool, bbgzlscl_p);
    }

    return 0;
}

/**
 * Add a LSCL to the LOCL chain
 * @param locl Pointer to the client LOCL
 * @param bbgzlscl_p Pointer to LSCL to add to the chain.
 * @param requiredState_p Pointer to a PLO CS Area representing a required state prior to updates.
 * @return 0 if LSCL was added to queue, non-zero if the state of things has changed.
 */
static int addLSCL(LocalCommClientAnchor_t* locl, LocalCommClientServerPair_t* pair_p, LocalComLOCL_PLO_CS_Area_t* requiredState_p) {
    PloCompareAndSwapAreaDoubleWord_t swapArea;
    PloStoreAreaDoubleWord_t          storeArea;  // Head of queue
    int                               ploRC;
    LocalComLOCL_PLO_CS_Area_t        localReplacePLO;

    swapArea.compare_p = &(locl->locl_PLO_CS_Area);
    memcpy(&(swapArea.expectedValue), requiredState_p, sizeof(swapArea.expectedValue));

    storeArea.storeLocation_p = &(locl->firstLSCL_p);

    // Build new SwapArea
    memcpy(&localReplacePLO, &(swapArea.expectedValue), sizeof(localReplacePLO));
    localReplacePLO.ploSequenceNumber+=1;
    memcpy(&(swapArea.replaceValue), &localReplacePLO, sizeof(swapArea.replaceValue));

    // Add new LSCL to head of queue
    pair_p->nextLSCL_p = locl->firstLSCL_p;

    memcpy(&(storeArea.storeValue), &(pair_p), sizeof(storeArea.storeValue));

    ploRC = ploCompareAndSwapAndStoreDoubleWord(swapArea.compare_p, &swapArea, &storeArea);


    return ploRC;
}

/**
 * Remove the LSCL from the LOCL chain
 * @param locl Pointer to the client LOCL
 * @param bbgzlscl_p Pointer to LSCL to remove from the chain.
 * @param requiredState_p Pointer to a PLO CS Area representing a required state prior to updates.
 * @return 0 if LSCL was removed from the queue, non-zero if the state of things has changed.
 */
int removeLSCL(LocalCommClientAnchor_t* locl, LocalCommClientServerPair_t* bbgzlscl_p, LocalComLOCL_PLO_CS_Area_t* requiredState_p) {

    PloCompareAndSwapAreaDoubleWord_t swapArea;
    PloStoreAreaDoubleWord_t          storeArea;  // Head of queue
    int                               ploRC;
    LocalComLOCL_PLO_CS_Area_t        localReplacePLO;
    LocalCommClientServerPair_t*      prevElement;

    memcpy(&(swapArea.expectedValue), requiredState_p, sizeof(swapArea.expectedValue));

    swapArea.compare_p = &(locl->locl_PLO_CS_Area);

    // Build new SwapArea
    memcpy(&localReplacePLO, &(swapArea.expectedValue), sizeof(localReplacePLO));
    localReplacePLO.ploSequenceNumber+=1;
    memcpy(&(swapArea.replaceValue), &localReplacePLO, sizeof(swapArea.replaceValue));

    prevElement = locl->firstLSCL_p;
    if (prevElement == NULL) {
        return 1;
    }

    if (prevElement == bbgzlscl_p) {
        // Matched first element, remove.
        storeArea.storeLocation_p = &(locl->firstLSCL_p);
        memcpy(&(storeArea.storeValue), &(prevElement->nextLSCL_p), sizeof(storeArea.storeValue));

        ploRC = ploCompareAndSwapAndStoreDoubleWord(swapArea.compare_p, &swapArea, &storeArea);
    } else {
        // Scan queue for target LSCL
        int foundLSCL = 0;
        for (LocalCommClientServerPair_t* currentElement = prevElement->nextLSCL_p;
             currentElement != NULL;
             currentElement = currentElement->nextLSCL_p) {
            if (currentElement == bbgzlscl_p) {
                foundLSCL = 1;
                storeArea.storeLocation_p = &(prevElement->nextLSCL_p);
                memcpy(&(storeArea.storeValue), &(currentElement->nextLSCL_p), sizeof(storeArea.storeValue));

                ploRC = ploCompareAndSwapAndStoreDoubleWord(swapArea.compare_p, &swapArea, &storeArea);
                break;
            } else {
                prevElement = currentElement;
            }
        }

        // If we didn't find the target LSCL, make sure the queue didn't change during our scan.  If the queue
        // did NOT change then the LSCL was not in the queue.  Otherwise, the queue changed and the call may need to
        // reexamine the queue.
        if (foundLSCL == 0) {
            PloLoadAreaDoubleWord_t loadArea;
            loadArea.loadLocation_p = locl;   // Picking any Double to load.

            ploRC = ploCompareAndLoadDoubleWord(swapArea.compare_p, &swapArea, &loadArea);
        }
    }

    return ploRC;
}

/**
 * Get the address of the BBGZLOCL for a Liberty server.
 *
 * @param stoken_p The stoken of the Liberty server whose BBGZLOCL we want
 *                 to find.
 *
 * @return The address of the BBGZLOCL, or NULL if we could not find one.
 */
static LocalCommClientAnchor_t* getLOCL_address(SToken* stoken_p) {
    // TODO: This is a temporary way of finding the LOCL.  In the end, we'll just map the
    //       STOKEN to an LOCL address somehow.  Right now, we'll do a GQSCAN to get the
    //       ENQs of all servers which are advertising local comm, and pick the one whose
    //       STOKEN matches, and ignoring the local comm identity name that the server is
    //       advertising.
    LocalCommClientAnchor_t* serverLOCL_p = NULL;
    char clientEnqRname[255];
    strncpy(clientEnqRname, SERVER_LCOM_READY_ENQ_RNAME_QUERY, sizeof(clientEnqRname)); // Will pad with NULLs.

    int enqRc, enqRsn;
    isgyquaahdr* enqData_p = scan_enq_system(BBGZ_ENQ_QNAME, clientEnqRname, &enqRc, &enqRsn);

    // If we got some ENQs back, iterate over them starting with the first.
    if (enqData_p != NULL) {
        isgyquaars* serverEnq_p = (isgyquaars*) enqData_p->isgyquaahdrfirstrecord31;

        // Iterate while there are more ENQs to look at and we haven't yet found serverLOCL_p
        while (serverEnq_p != NULL && serverLOCL_p == NULL) {
            isgyquaarq* serverEnqRQ_p = (isgyquaarq*) serverEnq_p->isgyquaarsfirstrq31;
            isgyquaarqx* serverEnqRQX_p = (isgyquaarqx*) serverEnqRQ_p->isgyquaarqrqx31;

            // If this ENQ matches the STOKEN that we were looking for, parse the RNAME and
            // extract the address of the LCOM.
            if (memcmp(stoken_p, serverEnqRQX_p->isgyquaarqxstoken, sizeof(*stoken_p)) == 0) {
                char currentRname[BBGZ_ENQ_MAX_RNAME_LEN + 1];
                memcpy(currentRname, serverEnq_p->isgyquaarsrname31, serverEnq_p->isgyquaarsrnamelen);
                currentRname[serverEnq_p->isgyquaarsrnamelen] = 0;
                if (sscanf(currentRname, SERVER_LCOM_READY_ENQ_RNAME_PATTERN, &serverLOCL_p) == 1) {
                    // -rx- serverEnq_p = NULL; // We found it and filled in the target server's BBGZLOCL address.
                } else {
                    serverLOCL_p = NULL; // Be sure we didn't fill something in here.
                }
            } 
            
            // Advance to the next ENQ if necessary.
            serverEnq_p = (isgyquaars*) serverEnq_p->isgyquaarsnext31;
        }

        // ENQ scan obtains storage for us, so free it here.
        free(enqData_p);
    }

    return serverLOCL_p;
}

/**
 * Issue a CBIND check against "BBG.<inSafProfileString>"
 *
 * @param inACEE_p
 * @param inSafProfileString
 * @param outRSN_p
 * @param outSAFRC_p
 * @param outRACFRC_p
 * @param outRACFRSN_p
 * @return 0 if all is well,
 * 8 if we failed trying to make the AUTH call (outRSN_p contains return code from checkAuthorizationFast),
 * 12 if we failed trying to make the AUTH call after a needed raclist (outRSN_p contains return code from checkAuthorizationFast),
 * 16 if we failed trying to raclist the CBIND class (outRSN_p has the return code from calling raclist),
 * 20 if the SAF profile entity name built with "inSafProfileString" exceeded the maximum allowable,
 * -1 if SAF returned non-zero return and reason codes (excluding the 4,4 return and reason).
 */
#define LCOM_CONNECT_CBIND_RC_CHKAUTH_FAILED     8
#define LCOM_CONNECT_CBIND_RC_RECHKAUTH_FAILED  12
#define LCOM_CONNECT_CBIND_RC_RACLIST_FAILED    16
#define LCOM_CONNECT_CBIND_RC_ENTITYX_TOOBIG    20
#define LCOM_MAX_SAF_PROFILE_LENGTH                   240     // I could define a profile anywhere near this big, but it may be restricted on vicom
static int clientConnectCBIND(acee* inACEE_p,
                              char* inSafProfileString,
                              int*  outRSN_p,
                              int*  outSAFRC_p,
                              int*  outRACFRC_p,
                              int*  outRACFRSN_p) {
    int localRC = 0;
    int localRSN;

    // Check length of profile name
    int racfProfileNameLen = strlen("BBG.") + strlen(inSafProfileString);
    if (racfProfileNameLen <= LCOM_MAX_SAF_PROFILE_LENGTH) {
        // Build RACF Profile name
        char racfProfileName[racfProfileNameLen + 1];
        strcpy(racfProfileName, "BBG.");
        strcat(racfProfileName, inSafProfileString);

        // The RACROUTE REQUEST=AUTH defaults to use the TCBSENV if the ACEE is not passed.
        // However, if the TCBSENV is NULL it will use the ASXBSENV.  I assume this is OK so
        // no need to check the value of the passed ACEE pointer.

        saf_results safResults;
        localRSN = checkAuthorizationFast(&safResults,
                                              0,                          // Suppress messages
                                              ASIS,                       // Log option
                                              "BBGZSRV",                  // Requestor
                                              NULL,                       // RACO_CB
                                              inACEE_p,                   // ACEE
                                              READ,                       // Access level
                                              "BBGZSRV",                  // Application name
                                              SAF_CBIND_CLASS,            // Class
                                              (const char*)&racfProfileName);    // Profile

        if (localRSN == 0) {
            // SAF/RACF return and reason codes are only valid if we called SAF.  Check if we need to raclist.
            if (safResults.safReturnCode == 4
                && safResults.racfReturnCode == 4) {
                // 4/4/x possibly means the class is not RACLISTed.  RACLIST it, then re-run
                // the authorization check.
                localRSN = raclist(NULL, NULL, CREATE, SAF_CBIND_CLASS);

                if (localRSN == 0) {
                    // RE-Perform the FASTAUTH check.
                    localRSN = checkAuthorizationFast(&safResults,
                                                      0,                          // Suppress messages
                                                      ASIS,                       // Log option
                                                      "BBGZSRV",                  // Requestor
                                                      NULL,                       // RACO_CB
                                                      inACEE_p,                   // ACEE
                                                      READ,                       // Access level
                                                      "BBGZSRV",                  // Application name
                                                      SAF_CBIND_CLASS,            // Class
                                                      (const char*)&racfProfileName);    // Profile
                    if (localRSN == 0) {
                        // SAF/RACF return and reason codes are only valid if we called SAF.
                        *outSAFRC_p   = safResults.safReturnCode;
                        *outRACFRC_p  = safResults.racfReturnCode;
                        *outRACFRSN_p = safResults.racfReasonCode;
                        if (safResults.safReturnCode > 4) {
                            localRC   = -1;
                        }
                    } else {
                        localRC = LCOM_CONNECT_CBIND_RC_RECHKAUTH_FAILED;
                        // localRSN has RC from re-issuing checkAuthorizationFast.
                    }
                } else {
                    localRC = LCOM_CONNECT_CBIND_RC_RACLIST_FAILED;
                    // localRSN has RC from "raclist".
                }
            } else {
                *outSAFRC_p   = safResults.safReturnCode;
                *outRACFRC_p  = safResults.racfReturnCode;
                *outRACFRSN_p = safResults.racfReasonCode;
                if (safResults.safReturnCode > 4) {
                    localRC   = -1;
                }
            }
        } else {
            localRC       = LCOM_CONNECT_CBIND_RC_CHKAUTH_FAILED;
            // localRSN has CHKAUTH RC.
        }
    } else {
        localRC  = LCOM_CONNECT_CBIND_RC_ENTITYX_TOOBIG;
        localRSN = racfProfileNameLen;
    }

    *outRSN_p = localRSN;

    return localRC;
}

/**
 * Establish a new local comm connection.
 *
 * @param inServerStoken_p A pointer to the stoken for the server to connect to.
 * @param inClientID The client identifier.  This is a constant that represents
 *                 the "channel" which handed the request to local comm.  The
 *                 value will be used on the server side during discrimination
 *                 to determine which channel will receive the data sent by the
 *                 client.
 * @param inTimeToWait A maximum amount of seconds to wait for data to arrive. A zero value indicates to wait indefinitely.
 * @param inACEE_p A pointer to the ACEE to extract identity from or NULL (will be pulled from current TCB).
 * @param inClientSafString A string containing the SAF profile component needed for a CBIND check ("BBG.<inClientSafString>").
 * @param outClientHandle_p A pointer to a quad word where the connection handle will be
 *                 copied.
 * @param outRSN_p Pointer to an int to contain the reason code from processing the connect request.
 * @param outSAFRC_p Pointer to an int to contain the SAF return code from the CBIND security check.
 * @param outRACFRC_p Pointer to an int to contain the RACF return code from the CBIND security check.
 * @param outRACFRSN_p Pointer to an int to contain the RACF reason code from the CBIND security check.
 *
 * @return 0 on success.  8 on failure. 12 if timed out waiting for a server response.
*/
int localCommClientConnect(SToken*        inServerStoken_p,
                           unsigned int   inClientID,
                           int            inTimeToWait,
                           acee*          inACEE_p,
                           char*          inClientSafString,
                           void*          outClientHandle_p,
                           int*           outRSN_p,
                           int*           outSAFRC_p,
                           int*           outRACFRC_p,
                           int*           outRACFRSN_p) {

    /* -------------------------------------------------------------------- */
    /* Some random notes about the local comm implementation...             */
    /*  * A client binds to a specific server.  This code lives in the      */
    /*    server common function module.  The only server we could connect  */
    /*    to is the one that we are bound to.  So the inServerStoken_p      */
    /*    parameter is somewhat meaningless.  We only use this to find the  */
    /*    server's LOCL, because we have to disambiguate the ENQ during the */
    /*    GQSCAN by using the stoken.  We could have done this another way. */
    /*                                                                      */
    /*  * Remember that server stoken is not unique, it can be re-used if   */
    /*    the server is running in a BPXAS.  The stoken is not appropriate  */
    /*    for use during cleanup, as there is no way to resolve connections */
    /*    that were made to the old server instance vs. connections to the  */
    /*    new server instance by just using the stoken.                     */
    /*                                                                      */
    /*  * The caller of this routine (local comm connect) was driven by the */
    /*    angel invoke process.  The angel passes a unique 8 byte token     */
    /*    representing this bind to the server code, which in turn stashes  */
    /*    it away in the server client task data.  This token is useful to  */
    /*    local comm for cleanup.  Since that token represents a single     */
    /*    server instance, it functions like stoken for local comm.         */
    /*    Unfortunately, this token means nothing to the server, so it      */
    /*    is not so useful for debugging.                                   */
    /*                                                                      */
    /*  * Since the server loads this code, we can make changes to both the */
    /*    server and client sides of the local comm code, and be confident  */
    /*    that there won't be a code level mismatch.  We do need to be      */
    /*    careful that we don't modify any shared control blocks in a way   */
    /*    that would prevent downlevel servers from being able to work.     */
    /*    For example, the client LOCL is shared among all client binds,    */
    /*    and the client needs to be able to run the LSCL chain.            */
    /* -------------------------------------------------------------------- */

    // Keep track of cleanup responsibilities and some debugging interests.
    unsigned int statusFlags = 0;
    const int RC_localCommClientConnect_OK                =  0x00000001;
    const int RC_localCommClientConnect_FailuresLine      =  0x0000FFFF;
    const int RC_localCommClientConnect_NO_LOCL           =  0x00010000;
    const int RC_localCommClientConnect_CONNECT_FAILED    =  0x00020000;
    const int RC_localCommClientConnect_CONNECT_NORESULTS =  0x00040000;
    const int RC_localCommClientConnect_INITHANDLE_FAILED =  0x00080000;
    const int RC_localCommClientConnect_NO_HANDLE         =  0x00100000;
    const int RC_localCommClientConnect_NO_LSCL           =  0x00200000;
    const int RC_localCommClientConnect_Timedout          =  0x00400000;
    const int RC_localCommClientConnect_SEC_FAILED        =  0x00800000;
    const int RC_localCommclientConnect_NOT_IN_CLIENT     =  0x01000000;

    int localRC;

    // Set default return values
    localRC       = 0;
    memset(outClientHandle_p, 0, sizeof(LocalCommClientConnectionHandle_t));
    *outRSN_p     = 0;
    *outSAFRC_p   = 0;
    *outRACFRC_p  = 0;
    *outRACFRSN_p = 0;

    // The client process data token represents our bind to this specific server instance.
    // The STOKEN can be re-used if the server is running in a BPXAS.  So the other token
    // provides uniqueness.  Unfortnately the other token is not useful for debugging.
    ServerCommonFunctionModuleProcessData_t* scfmpd_p = getServerCommonFunctionModuleProcessData();

    // Get a reference to our anchor control block, and create a data area for use by
    // us and the server we want to connect to.
    LocalCommClientAnchor_t* clientAnchor_p = NULL;
    if (scfmpd_p != NULL) {
        clientAnchor_p = getLocalCommClientAnchor();
    } else {
        statusFlags += RC_localCommclientConnect_NOT_IN_CLIENT;
    }

    LocalCommClientServerPair_t* pair_p = NULL;

    if (clientAnchor_p != NULL) {
        // Footprint client connect (can't footprint before bbgzlocl is created--just above)
        //TODO: Most likely need to pass a PLO_CS area for the LOCL to the footprint methods.  It would use PLO to
        // compare and check the state in the LOCL and update the footprint table (currently uses Compare and Swap).
        createLocalCommFPEntry_ClientConnectEntry(clientAnchor_p->footprintTable, inServerStoken_p, inClientID);

        //TODO: Need to use the inClientID parm to choose the correct connection
        int createLSCL_RC;
        do {
            LocalComLOCL_PLO_CS_Area_t out_lsclCurrentState;
            createLSCL_RC = 0;

            // Find existing LSCL
            pair_p = getLocalCommClientServerPair(scfmpd_p->clientProcessDataToken, clientAnchor_p, &out_lsclCurrentState);
            if ((pair_p == NULL) || !isHealthy(pair_p)) {
                createLSCL_RC = createLocalCommClientServerPair(inServerStoken_p, scfmpd_p->clientProcessDataToken, clientAnchor_p, &pair_p, &out_lsclCurrentState);

                if (createLSCL_RC > 8) {
                    // Footprint...the warning/failure.
                    createLocalCommFPEntry_ClientConnectCreateLSCL(clientAnchor_p->footprintTable, clientAnchor_p, inServerStoken_p, &out_lsclCurrentState, createLSCL_RC);
                }
            }
        } while ((pair_p == NULL) && (createLSCL_RC <= 8));
    } else {
        statusFlags += RC_localCommClientConnect_NO_LOCL;
    }

    //TODO: Another thread could issue a connect/close here and the LSCL we just created/found is gone.  Causing the addHandle
    // within the initializeHandle below to fail. Be careful when fixing; need to bail if we can't find the target Server.

    // If we got a data store to use, get a new connection handle.
    LocalCommConnectionHandle_t* connHandle_p = NULL;
    if (pair_p != NULL) {
        connHandle_p = getConnectionHandleFromPool(clientAnchor_p);

        if (connHandle_p != NULL) {
            LocalCommClientConnectionHandle_t clientHandle = {{0}};

            // Initialize the handle and add to LSCL list
            localRC = initializeHandle(connHandle_p, pair_p, &clientHandle);

            if (localRC == 0) {

                // Issue Security call
                localRC = clientConnectCBIND(inACEE_p,
                                             inClientSafString,
                                             outRSN_p,
                                             outSAFRC_p,
                                             outRACFRC_p,
                                             outRACFRSN_p);

                if (localRC == 0) {
                    // Security call was successful, try to connect to server.
                    localRC = issueConnectRequest(&clientHandle, inTimeToWait);

                    if (localRC == LCOM_ISSUECONNECTREQUEST_RC_OK) {
                        statusFlags += RC_localCommClientConnect_OK;

                        // Copy the connection handle token back to the caller.
                        memcpy(outClientHandle_p, &clientHandle, sizeof(clientHandle));
                    } else {
                        if (localRC == LCOM_ISSUECONNECTREQUEST_RC_TIMEDOUT) {
                            statusFlags += RC_localCommClientConnect_Timedout;
                        } else {
                            statusFlags += RC_localCommClientConnect_CONNECT_FAILED;
                        }
                    }
                } else {
                    // Security check failed.
                    statusFlags += RC_localCommClientConnect_SEC_FAILED;

                    // Footprint failed connect return and reason codes.
                    createLocalCommFPEntry_ClientConnectFailedSec(clientAnchor_p->footprintTable,
                                                                  connHandle_p,
                                                                  statusFlags,
                                                                  localRC,
                                                                  *outRSN_p,
                                                                  *outSAFRC_p,
                                                                  *outRACFRC_p,
                                                                  *outRACFRSN_p);                    
                }

                if (localRC != 0) {
                    // We failed somewhere after creating the connection handle, cleanup the handle.
                    localRC = localCommClose(&clientHandle);
                }
            } else {
                //TODO: Related to TODO above about LSCL going down.  We should cleanup and LOOP around to find a good LSCL or
                // create one, then fall back into the initializeHandle path.
                statusFlags += RC_localCommClientConnect_INITHANDLE_FAILED;
            }
        } else {
            statusFlags += RC_localCommClientConnect_NO_HANDLE;
        }
    } else {
        statusFlags += RC_localCommClientConnect_NO_LSCL;
    }

    // Footprint client connect exit
    createLocalCommFPEntry_ClientConnectExit(clientAnchor_p->footprintTable, inServerStoken_p, inClientID, connHandle_p, statusFlags);

    // Figure out the RC to pass back
    if (statusFlags & RC_localCommClientConnect_OK) {
        localRC = LCOM_CLIENTCONNECT_RC_OK;
    } else if (statusFlags & RC_localCommClientConnect_Timedout) {
        localRC = LCOM_CLIENTCONNECT_RC_TIMEDOUT;
    } else if (statusFlags & RC_localCommClientConnect_SEC_FAILED) {
        localRC = LCOM_CLIENTCONNECT_RC_SAF;
    } else {
        localRC = LCOM_CLIENTCONNECT_RC_FAILED;
    }

    return localRC;
}

static LocalCommDataCell_t * initializeMessageCell(LocalCommConnectionHandle_t *connHandle_p,  char* data_p, unsigned long long dataLen, unsigned char dataKey) {
    // TODO: Handle multiple LDAT case (LDAT expansion)
    LocalCommDataCell_t* msgCell_p = getLocalCommDataStoreCell(
        (LocalCommClientDataStore_t*)(connHandle_p->bbgzlscl_p->firstDataStore_p), dataLen);

    // Copy caller data with key int msg cell
    if (msgCell_p) {
        if (dataKey) {

            //TODO: Need a loop of 2Gig calls to memcpy, move with key takes an int not a unsigned long long.
            memcpy_sk(msgCell_p->dataAreaPtr,
                      data_p,
                      dataLen,
                      dataKey);
        } else {
            memcpy(msgCell_p->dataAreaPtr,
                   data_p,
                   dataLen);
        }

        // Detach from the Large Data Shared Memory if obtained for data (we copied sending data).  The reader
        // side is done with it.
        if (msgCell_p->readInfo.flags.dataAreaNotInCellpools) {
            int localRC, localRSN;
            void * sharedMem_p = (void*) ((char*)(msgCell_p->dataAreaPtr)-sizeof(LargeDataMessageHeader_t));
            localRC = detachSharedAboveConditional(sharedMem_p, msgCell_p->largeData.sharingUserToken, FALSE, &localRSN);
        }
    }
    return msgCell_p;
}

/**
 * Send data on a local comm connection.
 *
 * @param handle_p A pointer to the connection handle, returned on connect.
 * @param dataLen The length of the data to send.
 * @param data_p A pointer to the data, in the caller's key.
 * @param dataKey The key the data is in.
 *
 * @return 0 on success. 8 if no available message cells to transport data.  12 if we could not
 * build and deliver the send request to the target. 16 if the input dataLen is too big for local comm.
 *
 * @note Called by both client and server-sides of the connection.
 */
int localCommSend(void* handle_p, unsigned long long dataLen, void* data_p, unsigned char dataKey) {
    int localRC = 0;
    LocalCommDataCell_t * msgCell_p = NULL;

    localRC = validateClientConnectionHandle((LocalCommClientConnectionHandle_t*)handle_p);
    LocalCommConnectionHandle_t* connHandle_p = ((LocalCommClientConnectionHandle_t*)handle_p)->handle_p;

    if ((localRC == 0) && (dataLen > 0)) {

        if (dataLengthSupported(dataLen)) {

            // Get Msg Cell for caller data
            msgCell_p = initializeMessageCell(connHandle_p,  data_p, dataLen, dataKey);

            if (msgCell_p) {
                // Build a Send Data Queue Element
                int sendRC = issueSendRequest(connHandle_p, msgCell_p);

                if (sendRC != 0) {
                    freeLocalCommDataStoreCell(msgCell_p);

                    localRC = LCOM_SEND_RC_BUILDREQ_FAILED;
                }

            } else {
                localRC = LCOM_SEND_RC_NOMSGCELL;
            }

        } else {
            localRC = LCOM_SEND_RC_MSGTOOBIG;
        }
    }

    // Footprint send
    createLocalCommFPEntry_SendExit(((CF_FootprintTable*)connHandle_p->footprintTable),
                                    data_p,
                                    dataLen,
                                    dataKey,
                                    msgCell_p,
                                    localRC);

    return localRC;
}

/**
 * Receive data synchronously on a local comm connection.
 *
 * @param handle_p A pointer to the connection handle token returned on connect.
 * @param dataLen The number of bytes to receive into the buffer pointed to by <param>data_p</param>.
 * @param data_p A pointer to the buffer where the data should be copied.
 * @param dataKey The key the buffer is in (specified by data_p).
 *
 * @return 0 read went async (only for server reads), less than zero if failed,  otherwise the amount of data read.
 */
int localCommReceive(void* handle_p, unsigned long long dataLen, void* data_p, unsigned char dataKey) {
    int localRC;
    LocalCommConnectionHandle_t* connHandle_p = ((LocalCommClientConnectionHandle_t*) handle_p)->handle_p;
    unsigned long long returnDataSize = 0;
    unsigned short     freeLastRC     = 0;

    if (validateClientConnectionHandle((LocalCommClientConnectionHandle_t*) handle_p) != 0)  {
        localRC = LOCAL_COMM_STALE_HANDLE;
    } else {
        LCOM_AvailableDataVector* dataVector_p = NULL;

        localRC  = issueReadRequest(connHandle_p, 0L, dataLen, &dataVector_p);

        if ((localRC > 0) && (dataVector_p != NULL)) {
            char* cur_p = data_p;
            for (int x = 0; x < dataVector_p->blockCount; x++) {
                LCOM_ReadDataBlock* curBlock_p = ((LCOM_ReadDataBlock*)(dataVector_p + 1)) + x;

                //TODO: I wonder if we should check the key == to mykey and skip mvcdk and do a memcpy instead.
                // All the header reads, and some context reads would be improved.

                // Copy message to caller supplied storage with storage key
                memcpy_dk(cur_p,
                          curBlock_p->data_p,
                          curBlock_p->dataSize,
                          dataKey);

                cur_p += curBlock_p->dataSize;

                // Release returned data LMSG (may free the LMSG or just prepare for next read).
                freeLastRC = freeLastReadData(connHandle_p, (LocalCommDataCell_t*)(curBlock_p->dataCellPointer_p));
            }

            // Pass the data read back...note that its an "int" and were dealing with "unsigned long long" lengths.
            returnDataSize = dataVector_p->totalDataSize;
            localRC = dataVector_p->totalDataSize;
            free(dataVector_p);
        }
    }

    // Footprint receive
    createLocalCommFPEntry_Receive(((CF_FootprintTable*)connHandle_p->footprintTable),
                                   connHandle_p,
                                   returnDataSize,
                                   dataLen,
                                   localRC,
                                   freeLastRC);

    return localRC;
}

/**
 * See if data is available to receive on this connection.  The call can be asynchronous or synchronous
 * depending on the value of waitForData.  If synchronous, the call will block until at least one byte
 * of data is available or the time limit has been exceeded as specified by timeToWait.
 *
 * @param handle_p A pointer to the connection handle, returned on connect.
 * @param waitForData Set to 0 if we should return immediately with an answer, non-zero if we should wait until at least 1 byte is available.
 * @param timeToWait A maximum amount of seconds to wait for data to arrive. A zero value indicates to wait indefinitely.
 * @param dataLen_p A pointer to a double word where the number of bytes available is stored.
 * Only applicable if "waitForData" is non-zero.
 *
 * @return LCOM_PREVIEW_RC_OK on success, LCOM_PREVIEW_RC_TIMEDOUT if we timed out waiting for data, or
 * LCOM_PREVIEW_RC_PREVIEWREQ_FAILED otherwise.
 */
int localCommClientPreview(void* handle_p, unsigned char waitForData, int timeToWait, unsigned long long* dataLen_p) {

    int previewRC;
    int localRC = LCOM_PREVIEW_RC_OK;

    LocalCommConnectionHandle_t* connHandle_p = ((LocalCommClientConnectionHandle_t*) handle_p)->handle_p;

    previewRC = validateClientConnectionHandle((LocalCommClientConnectionHandle_t*) handle_p);

    if (previewRC == 0) {

        // Examine queues and wait for data if requested.
        previewRC = issuePreviewRequest(connHandle_p, waitForData, timeToWait, dataLen_p);

        if (previewRC != 0) {
            // Propagate the "Timed out" RC
            if (previewRC == LCOM_ISSUEPREVIEW_RC_TIMEDOUT) {
                localRC = LCOM_PREVIEW_RC_TIMEDOUT;
            } else {
                localRC = LCOM_PREVIEW_RC_PREVIEWREQ_FAILED;
            }

            // We need to drive close if the other side issued close.
            if (previewRC == LCOM_ISSUEPREVIEW_RC_CLOSERECEIVED) {
                localCommClose(handle_p);
            }
        }
    }

    // Footprint client preview
    createLocalCommFPEntry_ClientPreview(((CF_FootprintTable*)connHandle_p->footprintTable),
                                         connHandle_p,
                                         waitForData,
                                         timeToWait,
                                         dataLen_p,
                                         previewRC);

    return localRC;
}

// Notify other side of the connection to start CLOSING the connection.
static int notifyOtherSideClosing(LocalCommConnectionHandle_t* connHandle_p) {

    //TODO: We can't blindly do this.   If the client issued a send and followed it with a close, then its possible that
    // the close request will be driven before the presently sent data.
    //
    // So, if the other-side has the "read pending" flag
    // on, then we can send the close request (its waiting for something on this connection).  If the other-side doesn't
    // have the "read pending" flag on, we don't queue the close request...The other-side will "discover" the closed state
    // when it next attempts to use the connection.
    int localRC = issueCloseRequest(connHandle_p);

    return localRC;
}



/**
 * Terminate a local comm connection.
 *
 * @param handle_p A pointer to the connection handle token, returned on connect.
 *
 * @return 0 on success. -8 if the connection was stale (failed validation).  -12 if the connection was already
 * driven for close.
 */
int localCommClose(void* handle_p) {
    int closeRC = 0, localRC = 0;
    LocalCommConnectionHandle_t* connHandle_p = ((LocalCommClientConnectionHandle_t*) handle_p)->handle_p;

    // Keep track of cleanup responsibilities and some debugging interests.
    int statusFlags = 0;
    const int DRIVING_CLOSE_FROM_CLIENT_SIDE          = 0x00000001;
    const int CALLER_OWNS_HANDLE_CLEANUP              = 0x00000002;
    const int NOTIFIED_OTHERSIDE_TOSTARTCLOSING       = 0x00000004;
    const int DRAINED_QUEUES_MARKED_CLOSED            = 0x00000008;
    const int MARKED_HANDLE_CLOSED                    = 0x00000010;
    const int DRAINED_QUEUES_FAILED                   = 0x00000020;
    const int AFTER_POTENTIAL_HANDLEREMOVE_CHECKLSCL  = 0x00000040;
    const int CALLER_CLEANEDUP_HANDLE                 = 0x00000080;
    const int CALLER_OWNS_LSCL_CLEANUP                = 0x00000100;
    const int CALLER_CLEANEDUP_LSCL                   = 0x00000200;
    const int CALLER_NEEDS_TO_DETACH_LDATnLOCL        = 0x00000400;
    const int CALLER_DETACHED_LDAT_LOCL               = 0x00000800;
    const int CONNECTION_ALREADY_CLOSING              = 0x00001000;

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(TP_SERVER_LCOM_CLOSE_ENTRY),
                    "localCommClose, entry",
                    TRACE_DATA_RAWDATA(sizeof(LocalCommClientConnectionHandle_t), handle_p,   "Handle Token"),
                    TRACE_DATA_END_PARMS);
    }

    // Good connection?
    if (validateClientConnectionHandle((LocalCommClientConnectionHandle_t*) handle_p) == 0) {

        LocalCommClientServerPair_t* pair_p = connHandle_p->bbgzlscl_p; // TODO: Need method to extract data from Handle

        // Using LSCL to attach to local comm shared memory
        long long sharedMemoryUserToken = (long long) pair_p;

        LocalCommClientAnchor_t* currentLOCL_p;
        LocalCommClientAnchor_t* otherSideLOCL_p;

        LocalCommClientAnchor_t* clientLOCL_p = pair_p->localCommClientControlBlock_p;
        LocalCommClientAnchor_t* serverLOCL_p = pair_p->serverLOCL_p;


        LocalCommClientDataStore_t* ldat_p = pair_p->firstDataStore_p;  // for logging/tracing

        if (amIClientSide(connHandle_p) == 1) {
            statusFlags += DRIVING_CLOSE_FROM_CLIENT_SIDE;

            currentLOCL_p   = clientLOCL_p;
            otherSideLOCL_p = serverLOCL_p;
        } else {
            currentLOCL_p   = serverLOCL_p;
            otherSideLOCL_p = clientLOCL_p;
        }

        if (TraceActive(trc_level_detailed)) {
            TraceRecord(trc_level_detailed,
                        TP(TP_SERVER_LCOM_CLOSE_PRESTATECHANGE),
                        "localCommClose, entry",
                        TRACE_DATA_PTR(connHandle_p,   "BBGZLHDL ptr"),
                        TRACE_DATA_PTR(currentLOCL_p,  "BBGZLOCL ptr (current)"),
                        TRACE_DATA_PTR(otherSideLOCL_p,"BBGZLOCL ptr (other)"),
                        TRACE_DATA_PTR(clientLOCL_p,   "BBGZLOCL ptr (client) "),
                        TRACE_DATA_PTR(pair_p,         "BBGZLSCL ptr"),
                        TRACE_DATA_PTR(ldat_p,         "BBGZLDAT ptr"),
                        TRACE_DATA_HEX_INT(statusFlags,"statusFlags "),
                        TRACE_DATA_END_PARMS);
        }

        // LOCL -- Footprint close start
        createLocalCommFPEntry_CloseEntry(((CF_FootprintTable*)currentLOCL_p->footprintTable),
                                          connHandle_p,
                                          statusFlags,
                                          ldat_p,
                                          pair_p);
        // ConnectionHandle -- Footprint close start
        createLocalCommFPEntry_CloseEntry(((CF_FootprintTable*)connHandle_p->footprintTable),
                                          currentLOCL_p,
                                          statusFlags,
                                          ldat_p,
                                          pair_p);

        // Mark Connection closing by this side of the connection.
        localRC = startHandleClosing(connHandle_p);

        if (localRC == LCOM_STARTHANDLECLOSING_ALREADY_CLOSING) {
            statusFlags += CONNECTION_ALREADY_CLOSING;

            // ConnectionHandle -- Footprint close exit (footprint here before potentially pooling Handle)
            createLocalCommFPEntry_CloseExit(((CF_FootprintTable*)connHandle_p->footprintTable),
                                             connHandle_p,
                                             statusFlags,
                                             ldat_p,
                                             pair_p);

            // LOCL -- Footprint close exit (using our own LOCL)
            createLocalCommFPEntry_CloseExit(((CF_FootprintTable*)currentLOCL_p->footprintTable),
                                             connHandle_p,
                                             statusFlags,
                                             ldat_p,
                                             pair_p);

            closeRC = LCOM_CLOSE_RC_ALREADYCLOSING;

            if (TraceActive(trc_level_detailed)) {
                TraceRecord(trc_level_detailed,
                            TP(TP_SERVER_LCOM_CLOSE_EXIT2),
                            "localCommClose, exit",
                            TRACE_DATA_PTR(connHandle_p,   "BBGZLHDL ptr"),
                            TRACE_DATA_HEX_INT(statusFlags,"statusFlags "),
                            TRACE_DATA_INT(closeRC,        "closeRC     "),
                            TRACE_DATA_END_PARMS);
            }

            return closeRC;
        }

        if (localRC != LCOM_STARTHANDLECLOSING_OTHERSIDE_CLOSING) {
            // Notify other side of the connection to start CLOSING the connection.
            notifyOtherSideClosing(connHandle_p);

            statusFlags += NOTIFIED_OTHERSIDE_TOSTARTCLOSING;

            // If issued from the server-side then we are putting an entry on the client's inbound work queue.  I wonder when it will/should
            // notice it.  If its sending and receiving ...
        }

        // Mark client's queues as closing.
        localRC = initiateClosingClientQueues(connHandle_p);  // TODO: Must make sure that they are CLOSED state before returning...otherwise we would need a wait scheme or responsibility passing.

        if (localRC == LCOM_CLOSINGCLIENTQUEUES_CLOSED) {
            statusFlags += DRAINED_QUEUES_MARKED_CLOSED;

            // Update the Connection handle state to CLOSED.
            localRC = markHandleClosed(connHandle_p);

            statusFlags +=MARKED_HANDLE_CLOSED;

            if (localRC == LCOM_MARKHANDLECLOSED_OTHERSIDE_CLOSED) {
                // We own the cleanup of the Connection handle since the other side of the connection has already closed.
                statusFlags +=CALLER_OWNS_HANDLE_CLEANUP;
            }
        } else {
            // Oh no...didn't expect this...ABEND???
            statusFlags += DRAINED_QUEUES_FAILED;
        }

        // See if we need to cleanup the LSCL and detach from the Shared memory (LDAT and LOCL).
        // We need to detach from the LDAT if all existing connection handles are mark "closed" AND we successfully set
        // the LSCL Detach flag.
        //
        // If we own the connection handle cleanup then we need to remove it from the chain under this serialized method.
        // The reason is that we may not end up owning the LSCL cleanup.  So, we can't let the other side cleanup the LSCL
        // if we still need to de-chain the handle from the LSCL.
        //
        // Note: We could have multiple closes happening at the same time from the same side. So, the LSCL handle chain would look
        // all closed for both...need to serialized on the setting of the LDAT flag to solve that.  Also, if this side of close
        // issue the LDAT_Detach last, then it also owns the LSCL cleanup.
        localRC = checkSetLSCL_Detach(connHandle_p, (statusFlags & CALLER_OWNS_HANDLE_CLEANUP));
        statusFlags += AFTER_POTENTIAL_HANDLEREMOVE_CHECKLSCL;

        // ConnectionHandle -- Footprint close exit (footprint here before potentially pooling Handle)
        createLocalCommFPEntry_CloseExit(((CF_FootprintTable*)connHandle_p->footprintTable),
                                         connHandle_p,
                                         statusFlags,
                                         ldat_p,
                                         pair_p);

        if (statusFlags & CALLER_OWNS_HANDLE_CLEANUP) {
            cleanupHandle(clientLOCL_p, connHandle_p);
            statusFlags += CALLER_CLEANEDUP_HANDLE;
        }

        if (localRC & LCOM_LSCL_CHECKSET_LSCLCLEANUP) {
            if (amIClientSide(connHandle_p) == 1) {
                statusFlags += CALLER_OWNS_LSCL_CLEANUP;
                cleanupLSCL(clientLOCL_p, pair_p);
                statusFlags += CALLER_CLEANEDUP_LSCL;
            } else {
                // Server side cleans up the LSCL during its Shared Memory detach controlled by the channel code
            }
        }

        if (localRC & LCOM_LSCL_CHECKSET_DETACHSHARED) {
            if (amIClientSide(connHandle_p) == 1) {
                statusFlags += CALLER_NEEDS_TO_DETACH_LDATnLOCL;
                // detach from client LDAT and the other side's LOCL.
                detachFromLDATandLOCL(otherSideLOCL_p, ldat_p, sharedMemoryUserToken);

                statusFlags += CALLER_DETACHED_LDAT_LOCL;
            } else {
                // Server shared memory attach/detach are controlled by the channel code
            }
        }


        // LOCL -- Footprint close exit (using our own LOCL)
        createLocalCommFPEntry_CloseExit(((CF_FootprintTable*)currentLOCL_p->footprintTable),
                                         connHandle_p,
                                         statusFlags,
                                         ldat_p,
                                         pair_p);
    } else {
        // Stale connection
        if (TraceActive(trc_level_exception)) {
            TraceRecord(trc_level_exception,
                        TP(TP_SERVER_LCOM_CLOSE_STALEHANDLE),
                        "localCommClose, stale handle",
                        TRACE_DATA_RAWDATA(sizeof(LocalCommClientConnectionHandle_t), handle_p,   "Handle Token"),
                        TRACE_DATA_END_PARMS);
        }
        closeRC = LOCAL_COMM_STALE_HANDLE;
    }


    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(TP_SERVER_LCOM_CLOSE_EXIT),
                    "localCommClose, exit",
                    TRACE_DATA_PTR(connHandle_p,   "BBGZLHDL ptr"),
                    TRACE_DATA_HEX_INT(statusFlags,"statusFlags "),
                    TRACE_DATA_INT(closeRC,        "closeRC     "),
                    TRACE_DATA_END_PARMS);
    }

    return closeRC;
}

