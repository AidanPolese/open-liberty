/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2011
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
#ifndef _BBOZ_MVS_CELL_POOL_SERVICES_H
#define _BBOZ_MVS_CELL_POOL_SERVICES_H

/**
 * Flags that can be set when building a cell pool.
 */
struct buildCellPoolFlags {
    int autoGrowCellPool : 1; /**< Automatically get more storage and allocate
                                   new cells if a request to get a new cell
                                   fails.  The caller must also specify a
                                   function which will return the storage to
                                   use to grow the cell pool. */
    int skipInitialCellAllocation : 1; /**< Start with 0 available cells. */
    int available : 30;       /**< Available for future use. */
};
typedef struct buildCellPoolFlags buildCellPoolFlags;

/**
 * Function type used to obtain new storage when autoGrowCellPool is enabled.
 *
 * @param bytes_allocated_p A pointer to a double word which is filled in by
 *                          the implementing code with the number of bytes that
 *                          were allocated and returned.
 * @param cell_pool_id - The ID of the cell pool to be grown.
 *
 * @return A pointer to the storage allocated.
 */
typedef void* getGrowCellPoolStorage_t(long long* bytes_allocated_p, long long cell_pool_id);

/** Cell pool anchor storage.  Used on freeCellPoolStorage_t. */
#define CELL_POOL_ANCHOR_STORAGE_TYPE 1
/** Extent anchor storage.  Used on freeCellPoolStorage_t. */
#define CELL_POOL_EXTENT_STORAGE_TYPE 2
/** Cell storage.  Used on freeCellPoolStorage_t. */
#define CELL_POOL_CELL_STORAGE_TYPE 3

/**
 * Function type used to free the storage used by a cell pool when the cell
 * pool is destroyed.  The storage being freed can be from a cell pool anchor,
 * an extent anchor, or cell storage.  The function's job is to free the
 * storage back to the system.  For example, if the storage was allocated using
 * malloc, the function implementation would call free.
 *
 * In order to figure out what needs to be done for the various storage types,
 * you must know how the cell pool was built.
 *  - Cell pool anchor storage was provided on the buildCellPool call.
 *  - Extent anchor storage was provided on the growCellPool call.  If the
 *    cell pool is using auto-grow, this storage should not be freed.
 *  - Cell storage was provided on the growCellPool call.  If the cell pool is
 *    using auto-grow, this is the storage provided by the auto-grow function.
 *  - If you did not set the flag for 'skip initial allocation', the first
 *    extent will be allocated in the anchor.  It is not safe to delete
 *    the extents or cell storage in this case, unless you do not allow the cell
 *    pool to grow at all.  If this is true, all of the storage can be freed by
 *    freeing the anchor.
 *
 * @param storageType The type of storage which is currently being processed.
 * @param storage_p A pointer to the storage currently being processed.
 * @param id The ID of the cell pool being destroyed.
 */
typedef void freeCellPoolStorage_t(unsigned char storageType, void* storage_p, long long id);

/**
 * Computes the number of bytes of storage required to build a cell pool extent
 * with the given number of cells.
 *
 * @param numberOfCells The number of cells that are desired.  This should be
 *                      a multiple of 8.  If it is not, it will be rounded up
 *                      to the nearest multiple of 8.
 *
 * @return The number of bytes of storage which should be passed to
 *         growCellPool for extent storage.  This does not include the size of
 *         storage required for the actual cells, only the extent data structure.
 */
long long computeCellPoolExtentStorageRequirement(long long numberOfCells);

/**
 * Divide a single storage area into extent and cell portions.  Figure out
 * how many cells can be allocated into the cell portion.
 *
 * @param id The cell pool in which the extent will be allocated.
 * @param storage_p A pointer to the storage to be used.
 * @param storageLen The length of the storage, in bytes.
 * @param numCells_p A pointer to a double word which will be filled in with
 *                   the number of cells which will be contained in the extent.
 * @param extentStorageAddr_p A pointer to a double word which will be filled in with
 *                            the address within storage_p where the extent will start.
 * @param extentStorageLen_p A pointer to a double word which will be filled in with
 *                           the length of the extent to be created.
 * @param cellStorageAddr_p A pointer to a double word which will be filled in with
 *                          the address within storage_p where the cells will start.
 * @param cellStorageLen_p A pointer to a double word which will be filled in with
 *                         the length of the cell storage.
 */
void computeExtentDetailsFromSingleAddress(long long id, void* storage_p, long long storageLen, long long* numCells_p, void** extentStorageAddr_p, long long* extentStorageLen_p, void** cellStorageAddr_p, long long* cellStorageLen_p);

/**
 * Computes the number of bytes of storage required to build a cell pool with
 * the given number of cells and the size of the cells.
 *
 * @param numberOfCells The number of cells that are desired.  This should be
 *                      a multiple of 8.  If it is not, it will be rounded up
 *                      to the nearest multiple of 8.
 * @param cellSize The size of each individual cell, in bytes.
 *
 * @return The number of bytes of storage which should be passed to
 *         buildCellPool in order to build the desired cell pool.
 */
long long computeCellPoolStorageRequirement(long long numberOfCells, long long cellSize);

/**
 * Builds a cell pool.  The provided storage is carved into cells of the
 * specified size.  Some storage is used as overhead to store structures.
 *
 * @param storage_p The storage to be used
 * @param storage_len The length of the storage supplied by storage_p
 * @param cell_size The size in bytes that the cells should be
 * @param name_p The name of the cell pool.  This should be an 8 character
 *               string.
 * @param flags Flags that describe what options to use when creating the cell
 *              pool.
 *
 * @return a double word token to be used on future calls to the cell pool,
 *         or 0 if the cell pool build failed.
 */
long long buildCellPool(void* storage_p, long long storage_len, long long cell_size, char* name_p, buildCellPoolFlags flags);

/**
 * Adds storage to a cell pool, allowing more cells to be allocated.
 *
 * @param id The cell pool token returned on buildCellPool.
 * @param numCells The number of cells to put in the extent.
 * @param extentStorage_p The storage to use for the extent.
 * @param extentStorageLen The length of extentStorage_p, in bytes.
 * @param cellStorage_p The storage to use for the cells.
 * @param cellStorageLen The length of cellStorage_p.
 */
void growCellPool(long long id, long long numCells, void* extentStorage_p, long long extentStorageLen, void* cellStorage_p, long long cellStorageLen);

/**
 * Gets a cell from the cell pool
 *
 * @param id The cell pool token returned on buildCellPool.
 *
 * @return A pointer to the allocated cell, or NULL if no cells left.
 */
void* getCellPoolCell(long long id);

/**
 * Gets a cell from the cell pool.  The cell obtained can be freed more quickly
 * than a cell obtained from getCellPoolCell() by providing the token to
 * freeCellPoolCellWithToken().
 *
 * @param id The cell pool token returned on buildCellPool.
 * @param token_p A pointer to a double word field where a token will be copied
 *                which should be supplied on the freeCellPoolCellWithToken()
 *                call.
 *
 * @return A pointer to the allocated cell, or NULL if no cells left.
 */
void* getCellPoolCellWithToken(long long id, long long* token_p);

/**
 * Returns a cell pool cell to the pool.
 *
 * @param id The cell pool token returned on buildCellPool.
 * @param cell_addr A pointer to the cell to be returned to the pool.
 */
void freeCellPoolCell(long long id, void* cell_addr);

/**
 * Returns a cell pool cell to the pool.
 *
 * @param id The cell pool token returned on buildCellPool.
 * @param cell_addr A pointer to the cell to be returned to the pool.
 * @param extent_token The double word token obtained from getCellPoolCellWithToken().
 *
 * @return 0 if the free was successful, nonzero if not.  This is the return
 *         code from the CSRC4FR2 service.
 */
int freeCellPoolCellWithToken(long long id, void* cell_addr, long long extent_token);

/**
 * Checks to see if a cell belongs in this cell pool.
 *
 * @param id The cell pool token returned on buildCellPool.
 * @param cell_addr A pointer to the cell to be checked.
 * @param A pointer to a double word.  If the pointer is not null and the return
 *        code is 1, the double word will be set to 0 if the cell is not
 *        currently allocated, or set to 1 if the cell is currently allocated.
 *
 * @return 1 if the cell belongs in the pool, 0 if not, -1 if some
 *         other error occurred while checking.
 */
int verifyCellInPool(long long id, void* cell_addr, long long* allocated_p);

/**
 * Sets the auto-grow function for this cell pool, when auto-grow is enabled.
 *
 * @param id The cell pool token returned on buildCellPool.
 * @param impl_p A pointer to the grow function
 */
void setCellPoolAutoGrowFunction(long long id, getGrowCellPoolStorage_t* impl_p);

/**
 * Associate the given user data with the given cell pool.
 *
 * The user data can be retrieved via getCellPoolUserData.  The user data is useful
 * for passing parm data to the cell pool's auto-grow function.
 *
 * @param id The cell pool token returned on buildCellPool.
 * @param user_data The user data to associate with the cell pool.
 */
void setCellPoolUserData(long long id, void * user_data);

/**
 * Returns the user data assocaited with the given cell pool, as previously set
 * via setCellPoolUserData.
 *
 * @param id The cell pool token returned on buildCellPool.
 *
 * @return the user_data associated with the given cell pool.
 */
void * getCellPoolUserData(long long id) ;

/**
 * Destroys a cell pool.
 *
 * @param id The ID of the cell pool to destroy.
 * @param freeFcn_p A pointer to the function that should be used to free
 *                  the storage used by this cell pool.  The function will be
 *                  called once for each block of storage which must be
 *                  freed.
 */
void destroyCellPool(long long id, freeCellPoolStorage_t* freeFcn_p);

/**
 * Gets the size of the individual cells in the cell pool.
 *
 * @param id The cell pool token returned on buildCellPool.
 *
 * @return The size of the individual cells in the cell pool, in bytes.
 */
long long getCellPoolCellSize(long long id);

/**
 * Gets the number of cells in the cell pool.
 *
 * @param id The cell pool token returned on buildCellPool.
 *
 * @return The number of cells in the cell pool.
 */
long long getCellPoolTotalCells(long long id);

/**
 * Returns the number of extents in the cell pool.
 *
 * @param id The cell pool token returned on buildCellPool.
 *
 * @return The number of extents in the cell pool.
 */
long long getNumberOfExtentsInCellPool(long long id);

/** Structure returned by getCellPoolStatus. */
typedef struct cellPoolStatus {
    char poolName[8]; //!< The name of the cell pool.
    long long totalCells; //!< The number of cells in the cell pool.
    long long availableCells; //!< The number of available cells in the cell pool.
    long long cellSize; //!< The size of a cell in the cell pool.
    int numberOfExtents; //!< The number of extents in the cell pool.
} CellPoolStatus_t;

/**
 * Returns statistics about the cell pool.
 *
 * @param id The cell pool ID to query.
 * @param status_p A pointer to a status block to fill in.
 *
 * @return 0 if the cell pool status was queried successfully.
 */
int getCellPoolStatistics(long long id, CellPoolStatus_t* status_p);

#endif
