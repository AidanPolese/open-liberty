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
#ifndef _BBOZ_MVS_CPOOL_SERVICES_H
#define _BBOZ_MVS_CPOOL_SERVICES_H

/**
 * Type definition of the CPOOL CPID.
 */
typedef unsigned int mvs_cpool_id;

/**
 * Build and MVS CPOOL with with requested attributes.
 *
 * @param primaryCellCount the number of cells expected in the initial extent.
 * @param secondaryCellCount the number of cells per each secondary extent.
 * @param cellSize the size of each cell in the pool.  If the size is a multiple
 *        of 4, the cells will be on a word boundary; if the size is a multiple
 *        of 8, the cells will be on a double word boundary.
 * @param subpool the subpool to allocate backing storage from
 * @param key the key to use when allocating the backing storage
 * @param header a 24 byte area placed at the beginning of each cpool extent.
 *        This header can be used to help when debugging from a dump.
 */
mvs_cpool_id mvs_cpool_build(int primaryCellCount,
                             int secondaryCellCount,
                             int cellSize,
                             int subpool,
                             int key,
                             const char* header);

/**
 * Get a cell from the cell pool.
 *
 * @param poolId The ID returned from mvs_cpool_build
 *
 * @return A pointer to the allocated cell, or NULL if no cells available.
 */
void* mvs_cpool_get(mvs_cpool_id poolId);

/**
 * Frees a cell from the cell pool
 *
 * @param poolId The ID returned from mvs_cpool_build
 * @param cell_p The cell to return to the pool.
 */
void mvs_cpool_free(mvs_cpool_id poolId, void* cell_p);

/**
 * Destroys a cell pool.
 *
 * @param poolId The ID returned from mvs_cpool_build.
 */
void mvs_cpool_delete(mvs_cpool_id poolId);

#endif
