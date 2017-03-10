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

#include <limits.h>
#include <metal.h>
#include <stdio.h>

#include "include/server_local_comm_client.h"
#include "include/server_local_comm_data_store.h"

#include "include/mvs_cell_pool_services.h"
#include "include/mvs_iarv64.h"
#include "include/mvs_user_token_manager.h"


/** Number of segments (megabytes) to allocate for BBGZDATD. */
#define BBGZDATD_SEGMENTS 64

/** Name of message cell cell pool. */
#define BBGZLMSG_POOL_NAME "LMSGPOOL"

/** Sizes of cell pools */
#define BBGZDATD_MAXIMUM_CELL_POOL_SIZE 1048576
#define BBGZDATD_CELL_POOL_SIZES {4096, 8192, 32768, 131072, BBGZDATD_MAXIMUM_CELL_POOL_SIZE}

/** MAX definition */
#define LC_MAX(a,b) (((a) > (b)) ? (a) : (b))

/** Two megabytes */
#define TWO_MEGS 2097152L

/** Maximum allowed local comm send data length */
#define LCOM_MAXIMUM_SUPPORTED_DATALENGTH    INT_MAX

/**
 * Allocate an initialize a data store control block.
 *
 * @param serverStoken_p Pointer to the STOKEN of the server the client is connecting to.
 * @param anchor_p Pointer to the client's LOCL.
 * @param allocationUserToken The user token to use when allocating shared memory.
 * @param sharingUserToken The user token to use when accessing a shared memory object.
 *
 * @return A pointer to the data store, or NULL if the data store could not be created.
 */
LocalCommClientDataStore_t* createLocalCommDataStore(SToken* serverStoken_p, LocalCommClientAnchor_t* anchor_p, long long allocationUserToken, long long sharingUserToken) {
    LocalCommClientDataStore_t* dataStore_p = NULL;

    // Get storage for the new data store.  We allocate a new piece of shared
    // storage for the data store, because it is only to be used between this
    // client and the target server.  The server has no need to iterate the
    // data stores hung off of the BBGZLOCL.
    void* sharedMemory_p = getSharedAbove(BBGZDATD_SEGMENTS, FALSE, allocationUserToken);

    if (sharedMemory_p != NULL) {
        accessSharedAbove(sharedMemory_p, sharingUserToken);
        dataStore_p = (LocalCommClientDataStore_t*)sharedMemory_p;
        memset(dataStore_p, 0, sizeof(LocalCommClientDataStore_t));
        memcpy(dataStore_p->eyecatcher, BBGZLDAT_EYE, sizeof(dataStore_p->eyecatcher));
        dataStore_p->version = 1;
        dataStore_p->length = sizeof(LocalCommClientDataStore_t);
        dataStore_p->info.numSegmentsAllocated = BBGZDATD_SEGMENTS;
        dataStore_p->info.nextFreeByte_p = dataStore_p + 1;
        dataStore_p->info.baseAddress_p = dataStore_p;
        memcpy(&(dataStore_p->serverStoken), serverStoken_p, sizeof(*serverStoken_p));
        dataStore_p->localCommClientControlBlock_p = anchor_p;

        // Create the anchors for the different sized message cell pools.
        buildCellPoolFlags poolFlags;
        memset(&poolFlags, 0, sizeof(poolFlags));
        poolFlags.skipInitialCellAllocation = 1;
        long long poolSizes[] = BBGZDATD_CELL_POOL_SIZES;
        unsigned long long* curPoolAnchor = &(dataStore_p->anchor4K);
        unsigned char allocationFailure = FALSE;

        for (int x = 0; x < (sizeof(poolSizes) / sizeof(poolSizes[0])); x++) {
            long long bytesForCellPoolAnchor = computeCellPoolStorageRequirement(0, poolSizes[x]);
            void* storageForCellPoolAnchor_p = getLocalCommSharedStorage(&(dataStore_p->info), bytesForCellPoolAnchor);
            if (storageForCellPoolAnchor_p != NULL) {
                *curPoolAnchor = buildCellPool(storageForCellPoolAnchor_p, bytesForCellPoolAnchor, poolSizes[x], BBGZLMSG_POOL_NAME, poolFlags);
            }

            allocationFailure = (*curPoolAnchor == 0L) ? TRUE : allocationFailure;
            curPoolAnchor++;
        }
    }

    return dataStore_p;
}


/**
 * Obtain a new Shared Memory Object
 * @param datalen Length of data needed to include in allocated memory (in bytes).
 * @param allocationUserToken User token associated with the creation of the memory object.
 * @param sharingUserToken User token associated with the caller's use of the memory object.
 * @return a pointer to the allocated Shared Memory Object.
 *
 * @note Local comm shared memory cleanup utility depends on <code>allocationUserToken</code> using the
 * creating STOKEN.
 */
void* getLocalCommNewSharedStorage(unsigned long long datalen, long long allocationUserToken, long long sharingUserToken) {

    const unsigned long long oneMeg = 1048576L;
    int segmentsForAllocation = ((datalen - 1 + oneMeg) / oneMeg);
    void* sharedMemory_p = getSharedAbove(segmentsForAllocation, FALSE, allocationUserToken);

    if (sharedMemory_p != NULL) {
        accessSharedAbove(sharedMemory_p, sharingUserToken);
    }

    return sharedMemory_p;
}


/**
 * Figure out which cell pool to use to get a cell from, based on size.
 *
 * @param dataStore_p A pointer to the data store.
 * @param datalen The size, in bytes, of the cell required, not including
 *                the size of the LocalCommDataCell_t header.
 *
 * @return The cell pool to use to get a cell that can hold the required amount of data,
 *         or 0L if no pool was found.
 */
static unsigned long long selectCellPoolByMessageSize(LocalCommClientDataStore_t* dataStore_p, unsigned long long datalen) {
    unsigned long long effectiveLen = datalen + sizeof(LocalCommDataCell_t);
    long long poolSizes[] = BBGZDATD_CELL_POOL_SIZES;
    unsigned long long* curPoolAnchor = &(dataStore_p->anchor4K);
    unsigned long long poolToUse = 0L;

    // Find what pool to use.
    for (int x = 0; x < (sizeof(poolSizes) / sizeof(poolSizes[0])); x++) {
        long long curPoolCellSize = getCellPoolCellSize(*curPoolAnchor);
        if (effectiveLen <= curPoolCellSize) {
            poolToUse = *curPoolAnchor;
        } else {
            curPoolAnchor++;
        }
    }

    return poolToUse;
}

/**
 * Grow an LDAT cell pool.  We will grow the pool in 2 segment (2 MB) chunks with a minimum of
 * 8 cells.  IE a 1 MB cell size would mean 8 MB segment (minimum 8 cells), but a 4 KB cell size
 * would mean 2 MB segment.
 *
 * If we obtain some storage to grow the cell pool, we'll use the first part of the storage to
 * hold the cells, and the part after the cells to hold the extent data.  This keeps the cell
 * addresses quad word aligned (since the storage we get will be quad word aligned).
 *
 * @param info_p The shared memory info struct for the data store
 * @param poolID The cell pool ID
 *
 * @return 0 if the grow was successful.
 */
static int growLocalCommDataStoreCellPool(LocalCommSharedMemoryInfo_t* info_p, unsigned long long poolID) {
    unsigned long long cellSize = getCellPoolCellSize(poolID);
    unsigned long long numberOfCellsThatWillFitInTwoMegs = TWO_MEGS / cellSize;
    unsigned long long numberOfCells = LC_MAX(numberOfCellsThatWillFitInTwoMegs, 8);
    unsigned long long extentLen = computeCellPoolExtentStorageRequirement(numberOfCells);
    unsigned long long cellAreaLen = numberOfCells * cellSize;

    void* sharedMemory_p = getLocalCommSharedStorage(info_p, extentLen + cellAreaLen);
    if (sharedMemory_p != NULL) {
        void* extentStart_p = (void*)(((char*)sharedMemory_p) + cellAreaLen);
        growCellPool(poolID, numberOfCells, extentStart_p, extentLen, sharedMemory_p, cellAreaLen);
    }

    return (sharedMemory_p != NULL) ? 0 : -1;
}

/**
 * Returns 1 if we can handle the input data length.
 *
 * @param dataLen Length of the data to for a LMSG.
 * @return 1 if OK, 0 if not.
 */
int dataLengthSupported(unsigned long long dataLen) {
    // Do we support this size?
    return (dataLen <= (LCOM_MAXIMUM_SUPPORTED_DATALENGTH - sizeof(LocalCommDataCell_t)) ) ? 1 : 0;

}

/**
 * Gets a cell from the data store.
 *
 * @param dataStore_p The data store
 * @param datalen The size of the cell, in bytes.
 *
 * @return A pointer to the cell, or NULL if no cell was available.
 */
LocalCommDataCell_t* getLocalCommDataStoreCell(LocalCommClientDataStore_t* dataStore_p, unsigned long long datalen) {

    LocalCommDataCell_t* msgCell_p = NULL;
    unsigned long long poolToUse   = 0;

    // Will it fit within the LDAT cellpools.
    if (datalen <= (BBGZDATD_MAXIMUM_CELL_POOL_SIZE-sizeof(LocalCommDataCell_t)) ) {
        // Figure out which cell pool to use.
        poolToUse = selectCellPoolByMessageSize(dataStore_p, datalen);
        if (poolToUse != 0L) {

            // Get a cell.  Grow the pool if necessary.
            for (int growRC = 0; ((growRC == 0) && (msgCell_p == NULL));) {
                msgCell_p = getCellPoolCell(poolToUse);
                if (msgCell_p == NULL) {
                    growRC = growLocalCommDataStoreCellPool(&(dataStore_p->info), poolToUse);
                }
            }

            // If we got a cell, initialize it.
            if (msgCell_p != NULL) {
                memset(msgCell_p, 0, sizeof(LocalCommDataCell_t));
                memcpy(msgCell_p->eyecatcher, BBGZLMSG_EYE, sizeof(msgCell_p->eyecatcher));
                msgCell_p->version = BBGZLMSGL_INITIAL_VERSION;
                msgCell_p->length = sizeof(LocalCommDataCell_t);
                msgCell_p->owningPoolID = poolToUse;
                msgCell_p->dataAreaSize = datalen;
                msgCell_p->dataAreaPtr  = (char*) msgCell_p + msgCell_p->length;
            }
        }
    } else {
        // Allocate a chunk of shared memory for the data outside of the set of LDAT cellpools.
        unsigned long long effectiveLen = datalen + sizeof(LargeDataMessageHeader_t);

        // Is it too big to support?   At the time of writing this code, the entire LDAT allocated storage was 64 Segments
        // (64 Meg).
        if (effectiveLen <= LCOM_MAXIMUM_SUPPORTED_DATALENGTH) {
            // Get a cell from the small pool to use for the LMSG prefix only.  Grow the pool if necessary.
            poolToUse = dataStore_p->anchor4K;
            for (int growRC = 0; ((growRC == 0) && (msgCell_p == NULL));) {
                msgCell_p = getCellPoolCell(poolToUse);
                if (msgCell_p == NULL) {
                    growRC = growLocalCommDataStoreCellPool(&(dataStore_p->info), poolToUse);
                }
            }

            if (msgCell_p) {
                // Set up the data header (LMSG) for a large data request.
                memset(msgCell_p, 0, sizeof(LocalCommDataCell_t));
                memcpy(msgCell_p->eyecatcher, BBGZLMSG_EYE, sizeof(msgCell_p->eyecatcher));
                msgCell_p->version = BBGZLMSGL_INITIAL_VERSION;
                msgCell_p->length = sizeof(LocalCommDataCell_t);
                msgCell_p->owningPoolID = poolToUse;
                msgCell_p->dataAreaSize = datalen;
                msgCell_p->dataAreaPtr  = NULL;

                // Get the large chunk of Shared Memory outside of the LDAT pools.
                long long allocationUserToken = getAddressSpaceSupervisorStateUserToken();
                void * largeDataAreaPtr       = getLocalCommNewSharedStorage(effectiveLen, allocationUserToken, (long long) dataStore_p);
                if (largeDataAreaPtr) {
                    msgCell_p->readInfo.flags.dataAreaNotInCellpools = 1;
                    msgCell_p->dataAreaPtr                  = largeDataAreaPtr + sizeof(LargeDataMessageHeader_t);

                    msgCell_p->largeData.owningUserToken    = allocationUserToken;
                    msgCell_p->largeData.sharingUserToken   = (long long) dataStore_p;

                    // Build Large Message header into newly Obtained Shared Memory Object
                    LargeDataMessageHeader_t* largeDataHeader_p = (LargeDataMessageHeader_t*) largeDataAreaPtr;
                    memset(largeDataHeader_p, 0, sizeof(LargeDataMessageHeader_t));
                    memcpy(largeDataHeader_p->eyecatcher, BBGZLMSG_LARGEDATA_EYE, sizeof(largeDataHeader_p->eyecatcher));
                    largeDataHeader_p->largeData = msgCell_p->largeData;
                    largeDataHeader_p->length = sizeof(LargeDataMessageHeader_t);
                    largeDataHeader_p->version = BBGZLMSG_LARGEDATA_CUR_VER;
                } else {
                    // Free LMSG.
                    freeLocalCommDataStoreCell(msgCell_p);
                    msgCell_p = NULL;
                }
            }
        } else {
            // Error, data too big.  Caller should've checked if a supported size.
        }
    }

    return msgCell_p;
}

/**
 * Frees a cell from the data store.
 *
 * @param dataStore_p The data store
 * @return None.
 */
 void freeLocalCommDataStoreCell(LocalCommDataCell_t* msgCell_p) {
     freeCellPoolCell(msgCell_p->owningPoolID, msgCell_p);
}
