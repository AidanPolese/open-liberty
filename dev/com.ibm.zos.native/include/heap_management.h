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
#ifndef _BBGZ_HEAP_MANAGEMENT_H
#define _BBGZ_HEAP_MANAGEMENT_H

#include <stdlib.h>

/** @file
 * This part contains the heap management code used by the metal C environments
 * created for the Liberty server.  The heap is divided into fixed-length cells
 * of various sizes.  When storage is requested, it is allocated from one of the
 * pools which is closest in size to (but not smaller than) the requested size.
 * If no pool is large enough to fill the request, no storage is returned.
 *
 * The heap is split into above-the-bar and below-the-bar sections.  The storage
 * for the below-the-bar heap pools is obtained via the STORAGE macro.  The
 * storage is allocated in the key of the caller at the time the heap is
 * created.  The subpool used on the STORAGE macro depends on the authorization
 * level of the caller.  If authorized, storage is obtained from subpool 249,
 * otherwise subpool 132 is used.  Both pools are owned by the job-step task.
 * The storage for the above-the-bar heap pools is obtained via the IARV64
 * macro.  Storage is owned by the job-step task.
 *
 * The heap can optionally manage a task level cache of heap cells for each task
 * which uses the heap manager.  The heap manager assumes that the task level
 * cache will be stored in the task data control block in R13.  The address
 * in R13 is rounded down to the nearest megabyte to obtain the task control
 * block.  An offset provided when the heap is created is then added to the
 * task control block address and the task level cache is stored here.  It is
 * assumed that there is one cache area for each pool in the heap, and therefore
 * there must be enough space at this offset to support this.  It is suggested
 * that the caller reserve an array of type ThreadLevelHeapCache_t to store the
 * cache.  The cache is not serialized and it is assumed that only the calling
 * thread will use the cache.  If the cache offset is set to -1, a task level
 * cache will not be created.  The task level cache can be disabled for any
 * request by also providing the offset to a byte of storage which, when
 * non-zero, will disable the task level cache for that request.  This byte of
 * storage is also referenced off of the task control block.
 *
 * When using a task level cache, the heap will clean up the task level cache
 * when a task ends by returning any cached cells to their owning heap pool.
 * The heap can also optionally clean up cached cells for other tasks when asked
 * to, if a routine is provided to get the address of the task control block
 * for an arbitrary TCB.  This routine, if provided, is called with the local
 * lock held and should return what would be in R13 if that task were currently
 * dispatched.
 */

#define BBGZ_HEAP_ANGEL_AUTH 1 //!< Heap is for authorized angel owned code.
#define BBGZ_HEAP_SERVER_AUTH 2 //!< Heap is for authorized server owned code (BBGZSAFM).
#define BBGZ_HEAP_SERVER_UNAUTH 3 //!< Heap is for unauthorized server owned code (BBGZSUFM).
#define BBGZ_HEAP_ANGEL_CLIENT_AUTH 4 //!< Heap is for authorized angel owned client code.
#define BBGZ_HEAP_SERVER_CLIENT_AUTH 5 //!< Heap is for authorized server owned client code (BBGZSCFM).

/**
 * Prefix area for storage returned from a heap pool.  This struct occupies the
 * first bytes of storage returned from an allocation request, and is used when the
 * storage is returned to put it in the correct pool.  While the storage is in
 * the pool, it is used to chain the elements in the pool (stack).
 */
typedef struct heapCell {
    union {
        struct heapCell* nextFree_p; //!< When on a free chain, points to the next free element.
        struct {
            union {
                int size; //!< Size of the storage, when storage obtained.
                unsigned char poolNum; //!< The pool number to return the storage to, when obtained from a pool.
            };
            int _available    : 30, //!< Available flags for use.
                storageObtain : 1,  //!< Storage was allocated using storage obtain.
                inUse         : 1;  //!< Storage is not on a free chain, and is allocated to some task.
        } flags;
    };
    void* _available1; //!< Available for future use, aligns to quad word boundary.
} HeapCell_t;

/**
 * Structure which can be used by tasks to keep a thread-level cache for a heap pool.
 */
typedef struct threadLevelHeapCache {
    HeapCell_t* head_p; //!< First free storage on the chain.
    unsigned int count; //!< Count of elements on the chain.
    unsigned int aboveTheBar : 1,  //!< Elements are above the bar in memory
                 poolNum     : 4,  //!< The pool number.
                 elementSize : 27; //!< Size of the elements on the chain, including storage header, in bytes.
} ThreadLevelHeapCache_t;

/** A default recommended small pool size. */
#define BBGZ_HEAP_SMALL_POOL_SIZE 128L

/** A default recommended medium pool size. */
#define BBGZ_HEAP_MEDIUM_POOL_SIZE 1024L

/** A default recommended large pool size. */
#define BBGZ_HEAP_LARGE_POOL_SIZE 8192L

/** A set of default recommended pool sizes. */
#define BBGZ_HEAP_DEFAULT_POOL_SIZES {BBGZ_HEAP_SMALL_POOL_SIZE, BBGZ_HEAP_MEDIUM_POOL_SIZE, BBGZ_HEAP_LARGE_POOL_SIZE, 0L}

/**
 * Function type used to obtain the task control block for an arbitrary TCB.
 * See the description of this service in the file level documentation for more
 * information about how it is used.
 *
 * This function is driven with the local lock held.
 *
 * @param tcb_p A pointer to the TCB representing the task whose task level
 *              control block should be returned.
 */
typedef void* getTaskControlBlockForTCB_t(void* tcb_p);

/**
 * Builds a heap for use by a metal C environment.  See the file level documentation
 * for a description of how the heap is constructed.
 *
 * @param environment The environment for which the heap is being created.  This
 *                    can be one of BBGZ_HEAP_ANGEL_AUTH, BBGZ_HEAP_SERVER_AUTH,
 *                    or BBGZ_HEAP_SERVER_UNAUTH.
 * @param belowSizes_p A pointer to an array of double words containing the sizes of
 *                     the heap pools that should contain cells below-the-bar.  The
 *                     array is terminated by a double word containing zero.
 * @param aboveSizes_p A pointer to an array of double words containing the sizes of
 *                     the heap pools that should contain cells above-the-bar.  The
 *                     array is terminated by a double word containing zero.
 * @param getTaskControlBlockFcn_p A pointer to a function which will return the task
 *                                 control block for an arbitrary TCB.  This function
 *                                 is used to clean up cached heap cells for a task
 *                                 which is not currently dispatched.  If this function
 *                                 is not provided, the heap will not attempt to clean
 *                                 up heap cells cached at the task level for tasks
 *                                 which are not currently dispatched.
 * @param useTaskLevelCache If set to TRUE, the heap will keep a task level cache
 *                          hung off of the task data in R13.
 * @param checkForTaskLevelCacheDisabled If set to TRUE, a heap obtain or release
 *                                       request will check to see if the task level
 *                                       cache is disabled for this task by looking
 *                                       at a byte off of the task data in R13.
 *
 * @return A pointer to the heap anchor, or NULL if the heap anchor could not be
 *         created.
 */
void* buildHeap(int environment, unsigned long long* belowSizes_p, unsigned long long* aboveSizes_p, getTaskControlBlockForTCB_t* getTaskControlBlockFcn_p, unsigned char useTaskLevelCache, unsigned char checkForTaskLevelCacheDisabled);

/**
 * Destroys a heap created by buildHeap.  After this call returns, all cells in
 * the heap will be deallocated and cannot be referenced.
 *
 * This method is not thread-safe, and it is not guaranteed to complete
 * successfully unless no other tasks are using the heap, or its allocated
 * cells.
 *
 * @param anchor_p A pointer to the heap anchor.
 */
void destroyHeap(void* anchor_p);

/**
 * Obtains above the bar storage from the heap.
 *
 * @param bytes The number of bytes to obtain.
 *
 * @return A pointer to storage satisfying the requested size, or NULL if no
 *         storage is available.  If storage is returned, it will be quad
 *         word aligned.
 */
void* obtainHeapStorage(size_t bytes);

/**
 * Obtains below the bar storage from the heap.
 *
 * @param bytes The number of bytes to obtain.
 *
 * @return A pointer to storage satisfying the requested size, or NULL if no
 *         storage is available.  If storage is returned, it will be double
 *         word aligned.
 */
void* obtainHeapStorage31(size_t bytes);

/**
 * Frees storage obtained by obtainHeapStorage or obtainHeapStorage31.
 *
 * @param storage_p The storage pointer returned from obtainHeapStorage or
 *                  obtainHeapStorage31.
 */
void freeHeapStorage(void* storage_p);

/**
 * This routine will call out the task level cleanup routine provided by the
 * heap creator.
 *
 * @param heapAnchor_p A pointer to the heap anchor to be cleaned up.
 * @param tcb_p A pointer to the TCB representing the task which is being cleaned
 *              up.  If TCB is NULL, the current task is being cleaned up.  If
 *              TCB is non-NULL, then the local lock is held and this parameter
 *              points to the TCB representing the task which is being cleaned up.
 */
void taskLevelHeapCleanup(void* heapAnchor_p, void* tcb_p);

#endif
