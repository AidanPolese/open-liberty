#include "include/client_dynamic_area_cell_pool.h"

#include <stdlib.h>
#include <string.h>

#include "include/mvs_cell_pool_services.h"
#include "include/mvs_iarv64.h"

/**
 * Creates a client dynamic area cell pool.
 *
 * @param info_p A pointer to an area which is filled in with information
 *               about the cell pool.
 * @param rec_p Address of a pointer where the address of storage obtained by
 *              malloc can be stored temporarily, and freed by recovery code,
 *              in the event that an abend occurs in this method.  The caller
 *              should check this pointer during its recovery code, and free
 *              it if it is set.
 * @param ttoken_p The TToken who should own storage obtained by iarv64.
 *
 * @return The cell pool identifier, or 0 if the cell pool could not be
 *         created
 */
long long createClientDynamicAreaCellPool(ClientDynamicAreaCellPoolInfo_t* info_p, void** rec_p, TToken* ttoken_p) {
     // -------------------------------------------------------------------
     // Create a dynamic area cell pool which clients can use to get a
     // dynamic area during invoke.
     // -------------------------------------------------------------------
     long long clientDynamicAreaCellPool = 0L;
     buildCellPoolFlags poolFlags;
     memset(&poolFlags, 0, sizeof(poolFlags));
     poolFlags.skipInitialCellAllocation = 1;
     long long poolSize = computeCellPoolStorageRequirement(0, 0);
     void* poolStorage_p = malloc(poolSize);
     if (poolStorage_p != NULL) {
         if (rec_p != NULL) {
             *rec_p = poolStorage_p;
         }

         // ---------------------------------------------------------------
         // It's unfortunate that we need to make these cells the same size
         // as the cells used by the server for code that runs in the
         // angel.  It might be a lot of memory for the client.
         // ---------------------------------------------------------------
         clientDynamicAreaCellPool = buildCellPool(poolStorage_p, poolSize, 1048576, "BBGZCDYN", poolFlags);
         if (rec_p != NULL) {
             *rec_p = NULL;
         }

         if (clientDynamicAreaCellPool == 0L) {
             free(poolStorage_p);
         } else {
             info_p->cellPool = clientDynamicAreaCellPool;
             memcpy(&(info_p->storageOwner), ttoken_p, sizeof(info_p->storageOwner));
             memset(info_p->_available, 0, sizeof(info_p->_available));
         }
     }

     return clientDynamicAreaCellPool;
}

/**
 * Expands the client dynamic area cell pool.
 *
 * @param info_p A pointer to the information returned by
 *               createClientDynamicAreaCellPool.
 *
 * @return 0 if the cell pool was successfully expanded, non-zero if not.
 */
int growClientDynamicAreaCellPool(ClientDynamicAreaCellPoolInfo_t* info_p) {
    // See if we still need to grow.
    int expandSuccessful = -1;
    void* storage_p = getCellPoolCell(info_p->cellPool);
    if (storage_p == NULL) {
        long long numCellsPerExtent = 32;
        long long extentSize = computeCellPoolExtentStorageRequirement(numCellsPerExtent);
        void* extentStorage_p = malloc(extentSize);
        if (extentStorage_p != NULL) {
            // ---------------------------------------------------------------
            // The cells need to be allocated on a megabyte boundary, so we
            // use IARV64 to allocate them.
            // ---------------------------------------------------------------
            long long cellSize = getCellPoolCellSize(info_p->cellPool);
            long long cellStorageSize = numCellsPerExtent * cellSize;
            int iarvRC = 0, iarvRSN = 0;
            void* cellStorage_p = obtain_iarv64(numCellsPerExtent + 1, 1, &(info_p->storageOwner), &iarvRC, &iarvRSN);
            if (cellStorage_p != NULL) {
                growCellPool(info_p->cellPool, numCellsPerExtent, extentStorage_p, extentSize, cellStorage_p, cellStorageSize);
                expandSuccessful = 0;
            } else {
                free(extentStorage_p);
            }
        }
    } else {
        freeCellPoolCell(info_p->cellPool, storage_p);
        expandSuccessful = 0;
    }

    return expandSuccessful;
}

/**
 * Function used to free the client dynamic area cell pool.  This is called by cell pool
 * services.
 *
 * @param storageType The type of storage being freed.  Either cell storage,
 *                    extent storage, or anchor storage.
 * @param storage_p A pointer to the storage being freed.
 * @param id The cell pool owning the storage being freed.  We use this to obtain the
 *           TToken of the owner of the IARV64 storage, which was set in the cell
 *           pool user data just before this call.
 */
static void freeClientDynamicAreaCellPoolStorage(unsigned char storageType, void* storage_p, long long id) {
    if (storage_p != NULL) {
        // -------------------------------------------------------------------
        // The cells were allocated using IARV64 on a megabyte boundary.
        // Everything else was allocated using malloc.
        // -------------------------------------------------------------------
        if (storageType == CELL_POOL_CELL_STORAGE_TYPE) {
            int iarvRC = 0, iarvRSN = 0;
            TToken* storageOwner_p = (TToken*) getCellPoolUserData(id);
            if (storageOwner_p != NULL) {
                release_iarv64(storage_p, storageOwner_p, &iarvRC, &iarvRSN);
            }
        } else {
            free(storage_p);
        }
    }
}


/**
 * Destroys the client dynamic area cell pool.
 *
 * @param info_p A pointer to the information returned by
 *               createClientDynamicAreaCellPool.
 */
void destroyClientDynamicAreaCellPool(ClientDynamicAreaCellPoolInfo_t* info_p) {
    if (info_p->cellPool != 0L) {
        setCellPoolUserData(info_p->cellPool, &(info_p->storageOwner));
        destroyCellPool(info_p->cellPool, freeClientDynamicAreaCellPoolStorage);
        info_p->cellPool = 0L;
    }
}
