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
#include <stdlib.h>
#include <string.h>

#include "include/common_defines.h"
#include "include/heap_management.h"

#include "include/mvs_iarv64.h"
#include "include/mvs_pause_release_lock.h"
#include "include/mvs_resmgr.h"
#include "include/mvs_storage.h"
#include "include/mvs_utils.h"
#include "include/ras_tracing.h"

#include "include/gen/ihapsa.h"

#ifdef ANGEL_COMPILE
#include "include/angel_task_data.h"
#define TASK_DATA angel_task_data
#define GET_TASK_DATA getAngelTaskData
#define TASK_FIRST_BELOW freeHeapCellsBelow
#define TASK_FIRST_ABOVE freeHeapCellsAbove
#define TASK_CACHE_DISABLED noTaskLevelHeapCache
#elif SERVER_COMPILE
#include "include/server_task_data.h"
#define TASK_DATA server_task_data
#define GET_TASK_DATA getServerTaskData
#define TASK_FIRST_BELOW freeHeapCellsBelow
#define TASK_FIRST_ABOVE freeHeapCellsAbove
#else
#warning "Compiling in an unsupported environment"
#endif

/** RAS module idendifier. */
#define RAS_MODULE_CONST  RAS_MODULE_HEAP_MANAGEMENT

#define _TP_HEAP_MANAGER_EXPAND_EXISTING_IARV64        1
#define _TP_HEAP_MANAGER_EXPAND_NEW                    2
#define _TP_HEAP_MANAGER_EXPAND_UTIL                   3
#define _TP_HEAP_MANAGER_NEW_HEAP                      4

/**
 * Structure representing a cell storage area used by a heap pool.
 */
typedef struct heapCellPoolStorageNode {
    unsigned long long size; //!< Size of the cell storage.  For below the bar, in bytes, for above the bar, in segments.
    unsigned long long usedSize; //!< Size being used.  For above the bar, the unused size is guard pages.
    struct heapCellPoolStorageNode* next_p; //!< Pointer to the next storage node.
    void* cellStorage_p; //!< Pointer to the memory used for the cells.
} HeapCellPoolStorageNode_t;

/** CDSG area for the heap cell pool node. */
typedef struct heapCellPoolNodeCDSG {
    unsigned char poolNum;   //!< The pool number.  Note this is in the CDSG area, but does not need to be.
    unsigned char _reserved[3]; //!< Reserved for flags in the CDSG area.
    unsigned int cdsgCount;  //!< Counter for push/pop from chain.
    HeapCell_t* firstFree_p; //!< Pointer to the first free cell in the pool.
} HeapCellPoolNodeCDSG_t;

/**
 * Structure representing a heap cell pool chain.  This struct needs to be
 * allocated on a quad word boundary, and its length should also be a multiple
 * of 16.
 */
typedef struct heapCellPoolNode {
    unsigned long long cellSize; //!< The cell size.  Cached here for speed.
    HeapCellPoolStorageNode_t* firstStorageNode_p; //!< Pointer to the first block of memory used by cell pool.
    PauseReleaseLock_t expansionLock; //!< Lock used when expanding the pool.
    HeapCellPoolNodeCDSG_t cdsgArea; //!< Area to use for CDSG (pool push/pop).
} HeapCellPoolNode_t;

/**
 * Structure representing a chain of memory blocks which can be used for allocating
 * memory used by the heap anchor.  This includes cell pool extents.  Each block has
 * a bytes available field and a pointer field.  Storage is allocated by CDSG,
 * decreasing the bytes available and incrementing the pointer by the same
 * amount.  On a successful CDSG, the caller can use the memory from the old pointer
 * value to the new pointer value.  If there is not sufficient storage here to
 * satisfy the request, a new memory block is allocated and pushed onto the heap
 * anchor using CSG.  Since values are only added to this stack, there is no need
 * for a count field and CDSG.  This new block can now be used to allocate memory.
 * When the heap is destroyed, this memory chain is freed.
 *
 * This structure should be allocated on a quad word bounday due to the CDSG.
 */
typedef struct heapMemoryChainNode {
    char eyecatcher[8];            //!< Eye catcher.
    struct heapMemoryChainNode* next_p; //!< Next memory block (all used up).
    long long bytesAvailable;      //!< Bytes available in this memory block.
    void* nextAvailable_p;         //!< Next free location in this memory block.
    void* firstByte_p;             //!< Pointer to the begining of the memory object.
    char _available1[8];           //!< Available for use (align to quad word).
} HeapMemoryChainNode_t;

/** Heap utility memory eye catcher. */
#define BBGZ_HEAP_MEMORY_EYE "BBGZHMEM"

/** Size of a batch of heap elements (in bytes). */
#define HEAP_BATCH_SIZE_BYTES 16384

/**
 * Structure representing the anchor passed to the metal C environment which
 * contains pointers to the various heap cell pools.  This structure should
 * be allocated on a quad word boundary.
 *
 * This structure is followed immediately (on a quad word boundary) by the
 * stacks of free heap elements.  The below-the-bar stacks occur first,
 * in increasing size, followed by the above-the-bar stacks, in increasing size.
 */
typedef struct heapAnchor {
    char eyecatcher[8];  //!< Eye catcher.
    char environment;    //!< The environment that the heap is serving (unauth/auth, angel/server).
    unsigned char numBelowPools; //!< The number of below-the-bar heap pools in the heap.
    unsigned char numAbovePools; //!< The number of above-the-bar heap pools in the heap.
    unsigned char useTaskLevelCache; //!< The heap should create task level caches for each task.
    unsigned char checkTaskLevelCacheDisabled; //!< The heap should check if the task level cache is disabled for a task.
    char _available1[3]; //!< Available for use.
    HeapMemoryChainNode_t* utilityMemoryHead_p; //!< Memory to use for heap-related structures.  Update with CSG.
    getTaskControlBlockForTCB_t* getTaskControlBlockFcn_p;  //!< Routine to call to get task control block.
    HeapCellPoolNode_t* firstBelowBarPool_p; //!< Pointer to the first (smallest) below-the-bar storage pool.
    HeapCellPoolNode_t* firstAboveBarPool_p; //!< Pointer to the first (smallest) above-the-bar storage pool.
    unsigned char _available2[16]; //!< Available for use (align to quad word).
} HeapAnchor_t;

#define BBGZ_HEAP_ANCHOR_EYE "BBGZHEAP" //!< Heap anchor eye catcher.

#define BBGZ_HEAP_CELLS_PER_EXTENT 1024 //!< The number of cells per extent.
#define BBGZ_HEAP_XL_SUBPOOL_UNAUTH 132 //!< Subpool to use when doing a storage obtain from unauthorized code.
#define BBGZ_HEAP_XL_SUBPOOL_AUTH 249 //!< Subpool to use when doing a storage obtain from authorized code.
#define BBGZ_IARV64_SEG_SIZE 1048576 //!< The size of a segment of memory when using IARV64.

/** Macro to round something to the nearest 16 bytes (quad word). */
#define BBGZ_ROUND_TO_QUAD_WORD(size) ((((size) + 15) / 16) * 16)

/**
 * Allocates memory from the heap utility memory pool.
 *
 * @param anchor_p A pointer to the heap anchor.
 * @param size The number of bytes requested.  This will be rounded up to the
 *             nearest quad-word.
 *
 * @return A pointer to the storage requested, or NULL if no storage is available.
 */
static void* allocateUtilityMemory(HeapAnchor_t* anchor_p, int size) {
    void* storage_p = NULL;
    unsigned char errorCase = FALSE;
    int roundedSize = BBGZ_ROUND_TO_QUAD_WORD(size);

    typedef struct {
        long long storageSize;
        char* storage_p;
    } Storage_t;

    while ((storage_p == NULL) && (errorCase == FALSE)) {
        HeapMemoryChainNode_t* utilStorage_p = anchor_p->utilityMemoryHead_p;
        Storage_t oldStorage, newStorage;
        memcpy(&oldStorage, &(utilStorage_p->bytesAvailable), sizeof(oldStorage));
        if (oldStorage.storageSize >= roundedSize) {
            // ---------------------------------------------------------------
            // We can try to get the memory from the utility area.
            // ---------------------------------------------------------------
            newStorage.storageSize = oldStorage.storageSize - roundedSize;
            newStorage.storage_p = oldStorage.storage_p + roundedSize;
            if (__cdsg(&oldStorage, &(utilStorage_p->bytesAvailable), &newStorage) == 0) {
                storage_p = oldStorage.storage_p;
            }
        } else {
            // ---------------------------------------------------------------
            // Add a new utility area
            // ---------------------------------------------------------------
            if (TraceActive(trc_level_detailed)) {
                TraceRecord(trc_level_detailed,
                            TP(_TP_HEAP_MANAGER_EXPAND_UTIL),
                            "Expanding utility memory area",
                            TRACE_DATA_PTR(anchor_p, "Heap anchor"),
                            TRACE_DATA_INT(anchor_p->environment, "Environment"),
                            TRACE_DATA_INT(size, "Size request that caused the expansion"),
                            TRACE_DATA_END_PARMS);
            }

            TToken jobstepTToken;
            if (getJobstepTToken(&jobstepTToken) != 0) {
                errorCase = TRUE;
            } else {
                int rc = 0, rsn = 0;
                HeapMemoryChainNode_t* newUtilStorage_p = obtain_iarv64(2, 1, &jobstepTToken, &rc, &rsn);
                if (anchor_p == NULL) {
                    errorCase = TRUE;
                } else {
                    memcpy(newUtilStorage_p->eyecatcher, BBGZ_HEAP_MEMORY_EYE, sizeof(newUtilStorage_p->eyecatcher));
                    newUtilStorage_p->next_p = utilStorage_p;
                    newUtilStorage_p->bytesAvailable = BBGZ_IARV64_SEG_SIZE - sizeof(HeapMemoryChainNode_t);
                    newUtilStorage_p->nextAvailable_p = ((char*)newUtilStorage_p) + sizeof(HeapMemoryChainNode_t);
                    newUtilStorage_p->firstByte_p = newUtilStorage_p;
                    memset(newUtilStorage_p->_available1, 0, sizeof(newUtilStorage_p->_available1));
                    if (__csg(&utilStorage_p, &(anchor_p->utilityMemoryHead_p), &(newUtilStorage_p)) != 0) {
                        release_iarv64(newUtilStorage_p, &jobstepTToken, &rc, &rsn);
                    }
                }
            }
        }
    }

    return storage_p;
}

/**
 * Creates a heap pool chain.
 *
 * @param anchor_p A pointer to the location where the first chain should be allocated.
 *                 The chains will be allocated in sequence starting at this location.
 * @param sizes_p A pointer to an array of double words containing the sizes of
 *                the cells in the heap pools.  The pools will be allocated in
 *                the order of the sizes in the array, so the smallest sizes
 *                should be first.
 * @param startingPoolNum The pool number to use for the first chain.  Subsequent
 *                        chains will increment this number by one.
 *
 * @return 0 on success, nonzero on failure.
 */
static int createHeapPoolChain(void* anchor_p, unsigned long long* sizes_p, unsigned char startingPoolNum) {
    // We are done when we hit the zero-size pool.
    if ((*sizes_p) == 0) {
        return 0;
    }

    // Allocate this pool.
    HeapCellPoolNode_t* node_p = (HeapCellPoolNode_t*)anchor_p;
    node_p->cellSize = BBGZ_ROUND_TO_QUAD_WORD(*sizes_p);
    node_p->firstStorageNode_p = NULL;
    int lockRC = createPauseReleaseLock(&(node_p->expansionLock));
    node_p->cdsgArea.poolNum = startingPoolNum;
    node_p->cdsgArea.cdsgCount = 0;
    node_p->cdsgArea.firstFree_p = NULL;

    if (lockRC != 0) {
        return -1;
    }

    // Allocate the rest of the pools
    return createHeapPoolChain(node_p + 1, sizes_p + 1, startingPoolNum + 1);
}

/**
 * Carves up a chunk of storage into cells and adds them to the heap pool.
 *
 * @param pool_p The pool where the cells are to be added.
 * @param cellStorage_p The storage that is to be carved into cells.
 * @param cellsToAllocate The number of cells to make.  The parameter cellStorage_p must
 *                        point to enough memory to support creating this many cells
 *                        based on the cell size specified in pool_p.
 */
static void addBatchOfCells(HeapCellPoolNode_t* pool_p, void* cellStorage_p, unsigned long long cellsToAllocate) {
    // Chain all the cells together so we can add them all at once.
    HeapCell_t* curCell_p = (HeapCell_t*)(((char*)cellStorage_p) + (pool_p->cellSize));
    HeapCell_t* prevCell_p = (HeapCell_t*)cellStorage_p;
    for (long long x = 0; x < (cellsToAllocate - 1); x++) {
        prevCell_p->nextFree_p = curCell_p;
        prevCell_p = curCell_p;
        curCell_p = (HeapCell_t*)(((char*)prevCell_p) + (pool_p->cellSize));
    }

    // At the end of the for loop, the last valid cell will be in prevCell_p;
    HeapCellPoolNodeCDSG_t oldArea, newArea;
    memcpy(&oldArea, &(pool_p->cdsgArea), sizeof(oldArea));
    do {
        memcpy(&newArea, &oldArea, sizeof(newArea));
        newArea.cdsgCount = oldArea.cdsgCount + 1;
        newArea.firstFree_p = cellStorage_p;
        prevCell_p->nextFree_p = oldArea.firstFree_p;
    } while (__cdsg(&oldArea, &(pool_p->cdsgArea), &newArea) != 0);
}

/**
 * Expands a heap pool if it's empty.
 *
 * @param anchor_p A pointer to the heap anchor.
 * @param pool_p The pool to expand.
 * @param aboveTheBar Set to TRUE (1) if cell storage should be allocated above
 *                    the bar using IARV64, otherwise below the bar using
 *                    STORAGE OBTAIN.
 *
 * @return 0 If the heap pool has elements available when this method exits.  The
 *           elements could have been made available through heap expansion or by
 *           some other event on another task which resulted in elements being
 *           added to the heap pool.
 */
static int expandHeapPoolIfEmpty(HeapAnchor_t* anchor_p, HeapCellPoolNode_t* pool_p, unsigned char aboveTheBar) {
    int rc = 0;

    // ------------------------------------------------------------------------
    // First lock the pool.  We'll check the pool again to see if it's empty after
    // we get the lock, but the caller should have checked first to see if it was
    // empty before calling us.
    // ------------------------------------------------------------------------
    PauseReleaseLockToken_t lockToken;
    if (obtainPauseReleaseLock(&(pool_p->expansionLock), &lockToken) == 0) {
        if (pool_p->cdsgArea.firstFree_p == NULL) {
            // ----------------------------------------------------------------
            // If there is already a block of storage, see if we can use it.
            // Otherwise we need to allocate more storage.
            // ----------------------------------------------------------------
            if ((pool_p->firstStorageNode_p != NULL) &&
                ((pool_p->firstStorageNode_p->size) > (pool_p->firstStorageNode_p->usedSize))) {

                if (TraceActive(trc_level_detailed)) {
                    TraceRecord(trc_level_detailed,
                                TP(_TP_HEAP_MANAGER_EXPAND_EXISTING_IARV64),
                                "Expanding existing IARV64 heap cell allocation",
                                TRACE_DATA_INT(pool_p->cdsgArea.poolNum, "Pool number"),
                                TRACE_DATA_LONG(pool_p->cellSize, "Cell size"),
                                TRACE_DATA_LONG(pool_p->firstStorageNode_p->size, "Current segment size"),
                                TRACE_DATA_LONG(pool_p->firstStorageNode_p->usedSize, "Current used segment size"),
                                TRACE_DATA_END_PARMS);
                }

                int segmentsPerAllocation = (((pool_p->cellSize) - 1) / BBGZ_IARV64_SEG_SIZE) + 1;
                unsigned long long segmentsLeft = pool_p->firstStorageNode_p->size - pool_p->firstStorageNode_p->usedSize;
                if (segmentsLeft >= segmentsPerAllocation) {
                    int iarv64_rc, iarv64_rsn;
                    void* cellStorage_p = ((char*)pool_p->firstStorageNode_p->cellStorage_p) + ((pool_p->firstStorageNode_p->usedSize) * BBGZ_IARV64_SEG_SIZE);
                    pool_p->firstStorageNode_p->usedSize = pool_p->firstStorageNode_p->usedSize + segmentsPerAllocation;
                    convertGuardToActive_iarv64(pool_p->firstStorageNode_p->cellStorage_p, segmentsPerAllocation, &iarv64_rc, &iarv64_rsn);
                    if (iarv64_rc == 0) {
                        unsigned long long cellsToAllocate = (segmentsPerAllocation * BBGZ_IARV64_SEG_SIZE) / pool_p->cellSize;
                        addBatchOfCells(pool_p, cellStorage_p, cellsToAllocate);
                    } else {
                        rc = -1; // IARV64 changeguard failed.
                    }
                } else {
                    rc = -1; // Should never happen - we allocate in blocks.
                }
            } else {
                HeapCellPoolStorageNode_t* newExtent_p = (HeapCellPoolStorageNode_t*)allocateUtilityMemory(anchor_p, sizeof(HeapCellPoolStorageNode_t));
                if (newExtent_p != NULL) {
                    unsigned long long cellsToAllocate = BBGZ_HEAP_CELLS_PER_EXTENT;
                    unsigned long long cellStorageSize = (pool_p->cellSize) * cellsToAllocate;
                    unsigned long long usedStorageSize = cellStorageSize;
                    void* cellStorage_p = NULL;

                    if (TraceActive(trc_level_detailed)) {
                        TraceRecord(trc_level_detailed,
                                    TP(_TP_HEAP_MANAGER_EXPAND_NEW),
                                    "Expanding heap pool",
                                    TRACE_DATA_INT(pool_p->cdsgArea.poolNum, "Pool number"),
                                    TRACE_DATA_LONG(pool_p->cellSize, "Cell size"),
                                    TRACE_DATA_INT(aboveTheBar, "Above the bar"),
                                    TRACE_DATA_END_PARMS);
                    }

                    if (aboveTheBar == 0) {
                        // ----------------------------------------------------
                        // Our below-the-bar cell storage allocation is pretty
                        // simple -- just get enough storage for a default
                        // allocation size.
                        // ----------------------------------------------------
                        bbgz_psw psw;
                        extractPSW(&psw);
                        int subpool = (psw.pbm_state) ? BBGZ_HEAP_XL_SUBPOOL_UNAUTH : BBGZ_HEAP_XL_SUBPOOL_AUTH;
                        cellStorage_p = storageObtain(cellStorageSize, subpool, psw.key, NULL);
                    } else {
                        TToken jobstepTToken;
                        if (getJobstepTToken(&jobstepTToken) != 0) {
                            rc = -1;
                        } else {
                            // ------------------------------------------------
                            // For above the bar we'll try to be a little more
                            // clever.  Figure out how many cells we can fit
                            // into a segment (if the cell size is bigger than
                            // a segment then figure out how many we can fit
                            // into X segments).  Then allocate 8 times this
                            // much storage, using 1 of those for the cells and
                            // the other 7 for guard pages, which can be
                            // converted into real cells later.
                            // ------------------------------------------------
                            int segmentsPerAllocation = (((pool_p->cellSize) - 1) / BBGZ_IARV64_SEG_SIZE) + 1;
                            int totalSegmentsToAllocate = segmentsPerAllocation * 8;
                            cellsToAllocate = (segmentsPerAllocation * BBGZ_IARV64_SEG_SIZE) / pool_p->cellSize;
                            cellStorageSize = totalSegmentsToAllocate;
                            usedStorageSize = segmentsPerAllocation;
                            cellStorage_p = obtain_iarv64(totalSegmentsToAllocate, totalSegmentsToAllocate - 1, &jobstepTToken, NULL, NULL);
                        }
                    }

                    // -------------------------------------------------------
                    // Assuming we got some storage, initialize the cells and
                    // add them to the pool.
                    // -------------------------------------------------------
                    if (cellStorage_p != NULL) {
                        newExtent_p->size = cellStorageSize;
                        newExtent_p->usedSize = usedStorageSize;
                        newExtent_p->cellStorage_p = cellStorage_p;
                        newExtent_p->next_p = pool_p->firstStorageNode_p;
                        pool_p->firstStorageNode_p = newExtent_p;

                        // Chain all the cells together so we can add them all at once.
                        addBatchOfCells(pool_p, cellStorage_p, cellsToAllocate);
                    } else {
                        rc = -1;
                    }
                } else {
                    rc = -1;
                }
            }
        }

        releasePauseReleaseLock(&(pool_p->expansionLock), &lockToken);
    } else {
        rc = -1;
    }

    return rc;
}

// Build the heap.
void* buildHeap(int environment, unsigned long long* belowSizes_p, unsigned long long* aboveSizes_p, getTaskControlBlockForTCB_t* getTaskControlBlockFcn_p, unsigned char useTaskLevelCache, unsigned char checkForTaskLevelCacheDisabled) {
    // -----------------------------------------------------------------------
    // Obtain some working storage.
    // -----------------------------------------------------------------------
    TToken jobstepTToken;
    if (getJobstepTToken(&jobstepTToken) != 0) {
        return NULL;
    }

    int rc = 0, rsn = 0;
    HeapAnchor_t* anchor_p = obtain_iarv64(2, 1, &jobstepTToken, &rc, &rsn);
    if (anchor_p == NULL) {
        return NULL;
    }

    // -----------------------------------------------------------------------
    // Count the number of pools that we're going to create.
    // -----------------------------------------------------------------------
    int belowPoolCount = 0;
    for (unsigned long long* curPool = belowSizes_p; (*curPool) != 0; curPool = curPool + 1) {
        belowPoolCount = belowPoolCount + 1;
    }

    int abovePoolCount = 0;
    for (unsigned long long* curPool = aboveSizes_p; (*curPool) != 0; curPool = curPool + 1) {
        abovePoolCount = abovePoolCount + 1;
    }

    // -----------------------------------------------------------------------
    // Initialize our anchor.
    // -----------------------------------------------------------------------
    int anchorLen = BBGZ_ROUND_TO_QUAD_WORD(sizeof(HeapAnchor_t));
    int utilMemOffset = anchorLen + ((abovePoolCount + belowPoolCount) * sizeof(HeapCellPoolNode_t));
    memcpy(anchor_p->eyecatcher, BBGZ_HEAP_ANCHOR_EYE, sizeof(anchor_p->eyecatcher));
    anchor_p->environment = environment;
    anchor_p->numBelowPools = belowPoolCount;
    anchor_p->numAbovePools = abovePoolCount;
    anchor_p->useTaskLevelCache = useTaskLevelCache;
    anchor_p->checkTaskLevelCacheDisabled = checkForTaskLevelCacheDisabled;
    memset(anchor_p->_available1, 0, sizeof(anchor_p->_available1));
    anchor_p->utilityMemoryHead_p = (HeapMemoryChainNode_t*)(((char*)anchor_p) + utilMemOffset);
    memcpy(anchor_p->utilityMemoryHead_p->eyecatcher, BBGZ_HEAP_MEMORY_EYE, sizeof(anchor_p->utilityMemoryHead_p->eyecatcher));
    anchor_p->utilityMemoryHead_p->next_p = NULL;
    anchor_p->utilityMemoryHead_p->bytesAvailable = BBGZ_IARV64_SEG_SIZE - utilMemOffset - sizeof(HeapMemoryChainNode_t);
    anchor_p->utilityMemoryHead_p->nextAvailable_p = ((char*)anchor_p->utilityMemoryHead_p) + sizeof(HeapMemoryChainNode_t);
    anchor_p->utilityMemoryHead_p->firstByte_p = anchor_p;
    memset(anchor_p->utilityMemoryHead_p->_available1, 0, sizeof(anchor_p->utilityMemoryHead_p->_available1));
    anchor_p->firstBelowBarPool_p = (HeapCellPoolNode_t*)(((char*)anchor_p) + anchorLen);
    anchor_p->firstAboveBarPool_p = (anchor_p->firstBelowBarPool_p) + belowPoolCount;
    anchor_p->getTaskControlBlockFcn_p = getTaskControlBlockFcn_p;

    // -----------------------------------------------------------------------
    // Create the cell pools.
    // -----------------------------------------------------------------------
    createHeapPoolChain(anchor_p->firstBelowBarPool_p, belowSizes_p, 0);
    createHeapPoolChain(anchor_p->firstAboveBarPool_p, aboveSizes_p, belowPoolCount);

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(_TP_HEAP_MANAGER_NEW_HEAP),
                    "Built a new heap pool",
                    TRACE_DATA_PTR(anchor_p, "Anchor"),
                    TRACE_DATA_INT(anchor_p->environment, "Environment"),
                    TRACE_DATA_END_PARMS);
    }

    return anchor_p;
}

// A routine to destroy the heap and free all storage obtained.
void destroyHeap(void* anchor_p) {
    HeapAnchor_t* heapAnchor_p = (HeapAnchor_t*)anchor_p;
    if (heapAnchor_p != NULL) {
        bbgz_psw psw;
        extractPSW(&psw);
        int subpool = (psw.pbm_state) ? BBGZ_HEAP_XL_SUBPOOL_UNAUTH : BBGZ_HEAP_XL_SUBPOOL_AUTH;

        // -------------------------------------------------------------------
        // First delete the cell storage.
        // -------------------------------------------------------------------
        HeapCellPoolNode_t* curPool_p = heapAnchor_p->firstBelowBarPool_p;
        for (int x = 0; x < heapAnchor_p->numBelowPools; x++) {
            HeapCellPoolStorageNode_t* curNode_p = curPool_p->firstStorageNode_p;
            while(curNode_p != NULL) {
                storageRelease(curNode_p->cellStorage_p, curNode_p->size, subpool, psw.key);
                curNode_p = curNode_p->next_p;
            }
            destroyPauseReleaseLock(&(curPool_p->expansionLock));
            curPool_p = curPool_p + 1;
        }

        TToken jobstepTToken;
        if (getJobstepTToken(&jobstepTToken) == 0) {
            for (int x = 0; x < heapAnchor_p->numAbovePools; x++) {
                HeapCellPoolStorageNode_t* curNode_p = curPool_p->firstStorageNode_p;
                while (curNode_p != NULL) {
                    release_iarv64(curNode_p->cellStorage_p, &jobstepTToken, NULL, NULL);
                    curNode_p = curNode_p->next_p;
                }
                destroyPauseReleaseLock(&(curPool_p->expansionLock));
                curPool_p = curPool_p + 1;
            }

            // -------------------------------------------------------------------
            // Then delete the utility storage.  Node that when we delete the last
            // node, the pool anchor itself is deleted since it was part of the
            // first utility storage node (which is now at the end of the list).
            // -------------------------------------------------------------------
            HeapMemoryChainNode_t* curUtilMem_p = heapAnchor_p->utilityMemoryHead_p;
            while (curUtilMem_p != NULL) {
                void* stgToFree = curUtilMem_p->firstByte_p;
                curUtilMem_p = curUtilMem_p->next_p;
                release_iarv64(stgToFree, &jobstepTToken, NULL, NULL);
            }
        }
    }
}

/**
 * Initializes the task level heap chains.
 *
 * @param heapAnchor_p A pointer to the heap anchor.  The task level heap chain
 *                     sizes will be taken from here.
 * @param start_p A pointer to the location in memory where the first task
 *                level heap chain should be created.
 */
static void initializeTaskHeapChains(HeapAnchor_t* heapAnchor_p, ThreadLevelHeapCache_t* start_p) {
    ThreadLevelHeapCache_t* threadLevelCache_p = start_p;

    HeapCellPoolNode_t* curChain_p = heapAnchor_p->firstBelowBarPool_p;
    for (int x = 0; x < heapAnchor_p->numBelowPools; x++) {
        memset(threadLevelCache_p, 0, sizeof(ThreadLevelHeapCache_t));
        threadLevelCache_p->elementSize = curChain_p->cellSize;
        threadLevelCache_p->poolNum = curChain_p->cdsgArea.poolNum;
        threadLevelCache_p->aboveTheBar = 0;
        threadLevelCache_p = threadLevelCache_p + 1;
        curChain_p = curChain_p + 1;
    }

    curChain_p = heapAnchor_p->firstAboveBarPool_p;
    for (int x = 0; x < heapAnchor_p->numAbovePools; x++) {
        memset(threadLevelCache_p, 0, sizeof(ThreadLevelHeapCache_t));
        threadLevelCache_p->elementSize = curChain_p->cellSize;
        threadLevelCache_p->poolNum = curChain_p->cdsgArea.poolNum;
        threadLevelCache_p->aboveTheBar = 1;
        threadLevelCache_p = threadLevelCache_p + 1;
        curChain_p = curChain_p + 1;
    }
}

/**
 * Free a heap cell to the heap pool from where it came.
 *
 * @param anchor_p A pointer to the heap anchor.
 * @param cell_p A pointer to the cell to return.
 */
static void releaseHeapStorage(HeapAnchor_t* heapAnchor_p, HeapCell_t* cell_p) {
    // If we got the cell from a cell pool, put it back there.
    if ((cell_p->flags.inUse == 1) && (cell_p->flags.storageObtain == 0)) {
        HeapCellPoolNode_t* pool_p = (heapAnchor_p->firstBelowBarPool_p) + cell_p->flags.poolNum;
        HeapCellPoolNodeCDSG_t oldArea, newArea;
        memcpy(&oldArea, &(pool_p->cdsgArea), sizeof(oldArea));
        do {
            memcpy(&newArea, &oldArea, sizeof(newArea));
            newArea.cdsgCount = oldArea.cdsgCount + 1;
            newArea.firstFree_p = cell_p;
            cell_p->nextFree_p = oldArea.firstFree_p;
        } while (__cdsg(&oldArea, &(pool_p->cdsgArea), &newArea) != 0);
    }
}

/**
 * Return a batch of cells from a thread level cache to the heap.
 *
 * @param heapAnchor_p A pointer to the heap anchor.
 * @param batchSize The number of cells to transfer from the thread level cache
 *                  to the heap.
 * @param threadLevelCache_p The thread level cache to take the cells from.
 */
static void releaseBatchToHeap(HeapAnchor_t* heapAnchor_p, int batchSize, ThreadLevelHeapCache_t* threadLevelCache_p) {
    for (int x = 0; x < batchSize; x++) {
        HeapCell_t* storageHeader_p = threadLevelCache_p->head_p;
        threadLevelCache_p->head_p = storageHeader_p->nextFree_p;
        threadLevelCache_p->count = threadLevelCache_p->count - 1;
        storageHeader_p->flags.inUse = 1;
        storageHeader_p->flags.poolNum = threadLevelCache_p->poolNum;
        releaseHeapStorage(heapAnchor_p, storageHeader_p);
    }
}

/**
 * Obtains a cell from the heap.
 *
 * @param heapAnchor_p A pointer to the heap anchor.
 * @param chain_p A pointer to the heap pool from which to get the cell.
 * @param aboveTheBar Set to 1 if the storage can be allocated above the bar, or
 *                    0 if below the bar.
 *
 * @return A pointer to a cell from the heap pool, or NULL if no storage is
 *         available.
 */
static HeapCell_t* getStorageFromHeapPool(HeapAnchor_t* heapAnchor_p, HeapCellPoolNode_t* chain_p, unsigned char aboveTheBar) {
    HeapCell_t* storage_p = NULL;
    unsigned char errorCase = FALSE;

    while ((storage_p == NULL) && (errorCase == FALSE)) {
        HeapCellPoolNodeCDSG_t oldArea, newArea;
        memcpy(&oldArea, &(chain_p->cdsgArea), sizeof(oldArea));
        if (oldArea.firstFree_p != NULL) {
            memcpy(&newArea, &oldArea, sizeof(newArea));
            newArea.cdsgCount = oldArea.cdsgCount + 1;
            newArea.firstFree_p = oldArea.firstFree_p->nextFree_p;
            if (__cdsg(&oldArea, &(chain_p->cdsgArea), &newArea) == 0) {
                storage_p = oldArea.firstFree_p;
                storage_p->nextFree_p = NULL; // Clear header
                storage_p->flags.poolNum = chain_p->cdsgArea.poolNum;
                storage_p->flags.inUse = 1;
            }
        } else {
            expandHeapPoolIfEmpty(heapAnchor_p, chain_p, aboveTheBar);
        }
    }

    return storage_p;
}

/**
 * Gets a batch of cells from the heap.  Returns one cell for immediate use and
 * places the rest of the cells onto the specified thread level cache.
 *
 * @param batchSize The number of cells to obtain from the heap.
 * @param heapAnchor_p A pointer to the heap anchor.
 * @param threadLevelCache_p A pointer to the thread level cache where the extra
 *                           heap cells should be stored.
 * @param chain_p A pointer to the heap pool from where the cells should be
 *                obtained.
 * @param aboveTheBar Set to TRUE if the storage obtained from the heap should be
 *                    allocated above the bar, FALSE if below the bar.
 *
 * @return The address of one heap cell for immediate use.
 */
static HeapCell_t* getBatchFromHeap(int batchSize, HeapAnchor_t* heapAnchor_p, ThreadLevelHeapCache_t* threadLevelCache_p, HeapCellPoolNode_t* chain_p, unsigned char aboveTheBar) {
    HeapCell_t* storageHeader_p = NULL;
    for (int x = 0; x < batchSize; x++) {
        // Put back the previous cell (if any) and get another one.
        if (storageHeader_p != NULL) {
            memset(storageHeader_p, 0, sizeof(*storageHeader_p));
            storageHeader_p->nextFree_p = threadLevelCache_p->head_p;
            threadLevelCache_p->head_p = storageHeader_p;
            threadLevelCache_p->count = threadLevelCache_p->count + 1;
        }
        storageHeader_p = getStorageFromHeapPool(heapAnchor_p, chain_p, aboveTheBar);
    }

    return storageHeader_p;
}

/**
 * Gets a heap element from a thread level cache.
 *
 * @param threadLevelCache_p A pointer to the thread level cache.
 *
 * @return The heap element to use.
 */
static HeapCell_t* getStorageFromThreadLevelCache(ThreadLevelHeapCache_t* threadLevelCache_p) {
    HeapCell_t* storage_p = threadLevelCache_p->head_p;
    threadLevelCache_p->head_p = storage_p->nextFree_p;
    threadLevelCache_p->count = threadLevelCache_p->count - 1;
    storage_p->flags.inUse = 1;
    storage_p->flags.poolNum = threadLevelCache_p->poolNum;
    return storage_p;
}

/**
 * Gets a heap element using STORAGE OBTAIN.
 *
 * @param size The size of the element to obtain.
 *
 * @return The heap element to use.
 */
static HeapCell_t* getStorageFromStorageObtain(unsigned long long size) {
    bbgz_psw psw;
    extractPSW(&psw);
    int subpool = (psw.pbm_state) ? BBGZ_HEAP_XL_SUBPOOL_UNAUTH : BBGZ_HEAP_XL_SUBPOOL_AUTH;

    HeapCell_t* storageHeader_p = storageObtain(size, subpool, psw.key, NULL);
    if (storageHeader_p != NULL) {
        memset(storageHeader_p, 0, sizeof(*storageHeader_p));
        storageHeader_p->flags.inUse = 1;
        storageHeader_p->flags.storageObtain = 1;
        storageHeader_p->flags.size = size;
    }

    return storageHeader_p;
}

/**
 * Finds a task level cache to get a cell from.
 *
 * @param heapAnchor_p A pointer to the heap anchor.
 * @param poolNum The pool number that the storage will be obtained from.
 *
 * @return A pointer to a task level cache of heap cells that can be used to
 *         get heap storage, or NULL if no task level cache is available.
 */
static ThreadLevelHeapCache_t* findThreadLevelHeapCache(HeapAnchor_t* heapAnchor_p, unsigned char poolNum) {
    if (heapAnchor_p->useTaskLevelCache == FALSE) {
        return NULL;
    }

    TASK_DATA* taskData_p = GET_TASK_DATA();

#ifdef TASK_CACHE_DISABLED
    if ((heapAnchor_p->checkTaskLevelCacheDisabled != FALSE) && (taskData_p->TASK_CACHE_DISABLED != 0)) {
        return NULL;
    }
#endif

    ThreadLevelHeapCache_t* firstHeapCache_p = taskData_p->TASK_FIRST_BELOW;
    if (firstHeapCache_p->elementSize == 0) {
        initializeTaskHeapChains(heapAnchor_p, firstHeapCache_p);
    }

    return firstHeapCache_p + poolNum;
}

// Task level cleanup routine.
void taskLevelHeapCleanup(void* heapAnchor_p, void* tcb_p) {
    HeapAnchor_t* anchor_p = (HeapAnchor_t*)heapAnchor_p;
    if (anchor_p != NULL) {
        ThreadLevelHeapCache_t* threadLevelCache_p = NULL;
        if (tcb_p == NULL) {
            threadLevelCache_p = findThreadLevelHeapCache(anchor_p, 0);
        } else if (anchor_p->getTaskControlBlockFcn_p != NULL) {
            TASK_DATA* threadControlBlock_p = (TASK_DATA*)anchor_p->getTaskControlBlockFcn_p(tcb_p);
            if (threadControlBlock_p != NULL) {
                threadLevelCache_p = threadControlBlock_p->TASK_FIRST_BELOW;
            }
        }

        if (threadLevelCache_p != NULL) {
            for (int x = 0; x < ((anchor_p->numBelowPools) + (anchor_p->numAbovePools)); x++) {
                while (threadLevelCache_p->head_p != NULL) {
                    HeapCell_t* cell_p = threadLevelCache_p->head_p;
                    threadLevelCache_p->count = threadLevelCache_p->count -1;
                    threadLevelCache_p->head_p = cell_p->nextFree_p;
                    memset(&(cell_p->flags), 0, sizeof(cell_p->flags));
                    cell_p->flags.poolNum = threadLevelCache_p->poolNum;
                    cell_p->flags.inUse = 1;
                    releaseHeapStorage(anchor_p, cell_p);
                }
                threadLevelCache_p = threadLevelCache_p + 1;
            }
        }
    }
}

/**
 * Common for malloc31 and malloc.
 *
 * @param size The size of storage requested.
 * @param aboveTheBar TRUE if storage can be above the bar, FALSE if not.
 *
 * @return The storage we obtained, or NULL if no storage is available.
 */
#pragma inline(mallocCommon)
static void* mallocCommon(size_t size, unsigned char aboveTheBar) {
    // Get the heap anchor off of R12.
    HeapAnchor_t* heapAnchor_p = ((struct __csysenvtoken_s*)getenvfromR12())->__csetheapuserdata;

    // See if we can get the storage from a heap pool.
    // Find the heap pool with the correct sized cells
    HeapCellPoolNode_t* chain_p = (aboveTheBar == 0) ? heapAnchor_p->firstBelowBarPool_p : heapAnchor_p->firstAboveBarPool_p;
    unsigned int numPoolsToCheck = (aboveTheBar == 0) ? heapAnchor_p->numBelowPools : heapAnchor_p->numAbovePools;
    unsigned long long effectiveSize = size + sizeof(HeapCell_t);
    while ((numPoolsToCheck > 0) && (chain_p->cellSize < effectiveSize)) {
        chain_p = chain_p + 1;
        numPoolsToCheck = numPoolsToCheck - 1;
    }

    if (numPoolsToCheck == 0) {
        chain_p = NULL;
    }

    // If we found a suitably sized pool, see if we can get the storage from
    // our thread level cache for that pool.
    ThreadLevelHeapCache_t* threadLevelCache_p = (chain_p != NULL) ?
        findThreadLevelHeapCache(heapAnchor_p, chain_p->cdsgArea.poolNum) :
        NULL;

    // If we got a thread level cache, try to get the storage from it.
    HeapCell_t* storage_p;
    if (threadLevelCache_p != NULL) {
        if (threadLevelCache_p->head_p != NULL) {
            storage_p = getStorageFromThreadLevelCache(threadLevelCache_p);
        } else {
            storage_p = getBatchFromHeap((HEAP_BATCH_SIZE_BYTES / threadLevelCache_p->elementSize), heapAnchor_p, threadLevelCache_p, chain_p, aboveTheBar);
        }
    } else {
        storage_p = NULL;
    }

    // If we could not get storage from the cache, try to get it from the heap.
    if ((storage_p == NULL) && (chain_p != NULL)) {
        storage_p = getStorageFromHeapPool(heapAnchor_p, chain_p, aboveTheBar);
    }

    // If we could not get storage from the heap, do a storage obtain.
    if (storage_p == NULL) {
        storage_p = getStorageFromStorageObtain(effectiveSize);
    }

    // Advance past the header.
    return (storage_p != NULL) ? (storage_p + 1) : NULL;
}

// Obtain above-the-bar storage
void* obtainHeapStorage(size_t bytes) {
    return mallocCommon(bytes, TRUE);
}

// Obtain below-the-bar storage
void* obtainHeapStorage31(size_t bytes) {
    return mallocCommon(bytes, FALSE);
}

// Temporary free
void freeHeapStorage(void* storage_p) {
    if (storage_p != NULL) {
        // Get the heap anchor off of R12.
        struct __csysenvtoken_s* cenv_p = (struct __csysenvtoken_s*) getenvfromR12();
        HeapAnchor_t* heapAnchor_p = cenv_p->__csetheapuserdata;
        HeapCell_t* storageHeader_p = ((HeapCell_t*)storage_p) - 1;

        if (storageHeader_p->flags.storageObtain == 0) {
            // Try to put the storage back on our thread level cache (if there's room).
            ThreadLevelHeapCache_t* threadLevelCache_p = findThreadLevelHeapCache(heapAnchor_p, storageHeader_p->flags.poolNum);
            if (threadLevelCache_p != NULL) {
                // Only put the element back on the stack if there are less than two times the batch
                // size elements on the stack already.  If more than this, we should try to obtain a
                // batch from the task and free them back to the heap.
                int batchSize = HEAP_BATCH_SIZE_BYTES / threadLevelCache_p->elementSize;
                if (threadLevelCache_p->count < (batchSize * 2)) {
                    storageHeader_p->nextFree_p = threadLevelCache_p->head_p;
                    threadLevelCache_p->head_p = storageHeader_p;
                    threadLevelCache_p->count = threadLevelCache_p->count + 1;
                } else {
                    releaseHeapStorage(heapAnchor_p, storageHeader_p);
                    releaseBatchToHeap(heapAnchor_p, batchSize - 1, threadLevelCache_p);
                }
            } else {
                releaseHeapStorage(heapAnchor_p, storageHeader_p);
            }
        } else {
            bbgz_psw psw;
            extractPSW(&psw);
            int subpool = (psw.pbm_state) ? BBGZ_HEAP_XL_SUBPOOL_UNAUTH : BBGZ_HEAP_XL_SUBPOOL_AUTH;
            storageRelease(storageHeader_p, storageHeader_p->flags.size, subpool, psw.key);
        }
    }
}
