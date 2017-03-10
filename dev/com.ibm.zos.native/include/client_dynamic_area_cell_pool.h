#ifndef _BBOZ_CLIENT_DYN_AREA_CELL_POOL_H
#define _BBOZ_CLIENT_DYN_AREA_CELL_POOL_H

#include <metal.h>

#include "common_defines.h"

/**
 * Details about the client dynamic area cell pool.
 */
typedef struct clientDynamicAreaCellPoolInfo {
    /**
     * The actual cell pool.
     */
    long long cellPool;                                   /* 0x000 */

    /**
     * The TToken which storage allocated for cells should be assigned to.
     */
    TToken storageOwner;                                  /* 0x008 */

    /**
     * Available for use.
     */
    char _available[8];                                   /* 0x018 */
} ClientDynamicAreaCellPoolInfo_t;                        /* 0x020 */

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
long long createClientDynamicAreaCellPool(ClientDynamicAreaCellPoolInfo_t* info_p, void** rec_p, TToken* ttoken_p);

/**
 * Expands the client dynamic area cell pool.
 *
 * @param info_p A pointer to the information returned by
 *               createClientDynamicAreaCellPool.
 *
 * @return 0 if the cell pool was successfully expanded, non-zero if not.
 */
int growClientDynamicAreaCellPool(ClientDynamicAreaCellPoolInfo_t* info_p);

/**
 * Destroys the client dynamic area cell pool.
 *
 * @param info_p A pointer to the information returned by
 *               createClientDynamicAreaCellPool.
 */
void destroyClientDynamicAreaCellPool(ClientDynamicAreaCellPoolInfo_t* info_p);

#endif
