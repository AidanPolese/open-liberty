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
#ifndef _BBOZ_ANGEL_SGOO_SERVICES_H
#define _BBOZ_ANGEL_SGOO_SERVICES_H

#include "angel_bgvt_services.h"

#include "bbgzsgoo.h"

/**@file
 * Define Functions for the Server level GOO
 */

/**
 * Creates an SGOO (and a CGOO) for this system.
 *
 * @param bgvt_p A pointer to the master BGVT control block for the system.
 * @param new_control_blocks If set to TRUE, the existing CGOO and any control
 *                           blocks pointed to by the CGOO (including the SGOO)
 *                           will be orphaned, and new control blocks will be
 *                           created.  This is associated with the COLD=Y
 *                           parameter on the angel start command.  A cold start
 *                           will cause the CGOO and SGOO to be re-created.
 * @param angel_name The null-terminated angel name.
 *
 * @return A pointer to the new SGOO control block, or NULL on error.
 */
bbgzsgoo* createSGOO(bgvt* bgvt_p, unsigned char new_control_blocks, char* angel_name);

/**
 * Gets a reference to the SGOO via the BGVT.
 *
 * @return A pointer to the SGOO control block, or NULL if none.
 */
bbgzsgoo* getDefaultSGOO(void);

/**
 * Gets a reference to the SGOO for the named angel.
 *
 * @return A pointer to the SGOO control block for the named angel, or NULL if none.
 */
bbgzsgoo* getSGOO(char* angel_name);

/**
 * Calculate the angel anchor instance number.  The instance number is a short
 * identifier that uniquely identifies the angel instance on the system.
 *
 * @return The angel identifier.
 */
unsigned short getAngelAnchorInstanceNumber(AngelAnchor_t* angelAnchor_p);

/**
 * Allocates part of the shared memory managed by the SGOO.  The memory
 * cannot be returned to the SGOO, the caller owns it until the system is
 * IPLed.
 *
 * @param sgoo_p A pointer to the SGOO control block.
 * @param bytes The size of the storage to allocate.
 *
 * @return A pointer to the allocated storage, or NULL if no storage is
 *         available.
 */
void* allocateSGOOsharedStorage(bbgzsgoo* sgoo_p, int bytes);

/**
 * Auto-grow function for cell pools using the shared memory segment occupied
 * by the SGOO.
 *
 * @see setCellPoolAutoGrowFunction
 */
void* getStorageToGrowSharedMemoryCellPool(long long* size_p, long long cell_pool_id);

/**
 * Dumps information about the SGOO shared memory cell pools to the MVS console.
 */
void writeSgooCellPoolStatusToOperator(char* angel_name);

/*
 * Allocate a new Angel anchor set and initialize
 */
AngelAnchorSet_t* getNewAngelAnchorSet(AngelAnchorSet_t* prev_aas_p, bbgzcgoo* cgoo_p, bbgzsgoo* sgoo_p, char* angelName);

#endif
