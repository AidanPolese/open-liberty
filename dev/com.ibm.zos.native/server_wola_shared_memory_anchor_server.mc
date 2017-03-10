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

/**
 * @file
 *
 * Assorted server-side authorized routines used by the WOLA code for 
 * building/accessing the BBOASHR.
 *
 */

#include <metal.h>
#include <stdlib.h>
#include <string.h>

#include "include/mvs_utils.h"
#include "include/mvs_cell_pool_services.h"
#include "include/ras_tracing.h"
#include "include/ieantc.h"
#include "include/server_wola_connection_handle.h"
#include "include/server_wola_shared_memory_anchor_server.h"
#include "include/util_registry.h"
#include "include/common_defines.h"
#include "include/server_wola_avail_service.h"
#include "include/server_wola_wait_service.h"

#define RAS_MODULE_CONST  RAS_MODULE_SERVER_WOLA_SHARED_MEMORY_ANCHOR_SERVER

#define BBOASHR_OUTER_CELL_POOL_CELL_SIZE 1024 * 1024

/**
 *
 * IEAN4CR: http://pic.dhe.ibm.com/infocenter/zos/v2r1/index.jsp?topic=%2Fcom.ibm.zos.v2r1.ieaa200%2Fiean4cr.htm
 *
 * @param wola_group - the WOLA group name
 * @param bboashr_p - the shared memory area address
 *
 * @return The rc from iean4cr.
 */
int createBboashrNameToken( char * wola_group, void * bboashr_p ) {

    char token_name[16];
    getBboashrTokenName( token_name, wola_group );

    struct name_token_map name_token;

    name_token.bboashr_p = bboashr_p;
    name_token.unused = 0L;

    int rc = -1;
    iean4cr(IEANT_SYSTEM_LEVEL,
            token_name,
            (char *)&name_token,
            IEANT_PERSIST,
            &rc);

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(41),
                    "createBboashrNameToken",
                    TRACE_DATA_INT(rc, "iean4cr rc"),
                    TRACE_DATA_RAWDATA(16, token_name, "token_name (name)"),
                    TRACE_DATA_RAWDATA(16, &name_token, "name_token (value)"),
                    TRACE_DATA_END_PARMS);
    }

    return rc;
}

/**
 * TODO: Can't use autoGrow since the cell pools are accesse by both the client
 *       and server address spaces.  Only the server address space (which set up
 *       the autogrow function) would be able to call the autoGrow function.
 *       The client would ABEND if it tried.
 *
 * Auto-grow function for BBOASHR "inner" cell pools.
 *
 * This function obtains the next extent for the inner cell pool by grabbing
 * another 1MB cell from the BBOASHR "outer" cell pool.
 *
 * @param size_p - Output - The size of the new extent
 * @param cell_pool_id - The id of the inner cell pool to be grown
 *
 * @return the addr of the new extent.
 */
void * autoGrowInnerCellPool(long long * size_p, long long cell_pool_id) {

    // Get the bboashr_p from the cell pool user data.
    WolaSharedMemoryAnchor_t * bboashr_p = (WolaSharedMemoryAnchor_t *) getCellPoolUserData(cell_pool_id);

    void * inner_cp_extent = getCellPoolCell( bboashr_p->outerCellPoolID );
    *size_p = BBOASHR_OUTER_CELL_POOL_CELL_SIZE;

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(81),
                    "autoGrowInnerCellPool",
                    TRACE_DATA_PTR(bboashr_p, "bboashr_p"),
                    TRACE_DATA_RAWDATA(64, (void *)cell_pool_id, "cell_pool_id"),
                    TRACE_DATA_PTR(inner_cp_extent, "inner_cp_extent"),
                    TRACE_DATA_LONG(*size_p, "*size_p"),
                    TRACE_DATA_END_PARMS);
    }

    return inner_cp_extent;
}

/**
 * Build an "inner" cell pool for the BBOASHR storage area.  
 *
 * The BBOASHR storage area is managed using two levels of cell pools - an "outer" cell 
 * pool, that carves up the entire storage area into large 1MB cells, and several "inner" 
 * cell pools, each of which carves up a single cell of the outer pool.  
 *
 * This function obtains a 1MB cell from the outer pool and builds an inner cell pool 
 * within the 1MB cell.  The cell size and name of the inner pool is given by the parms.
 *
 * The inner cell pool is set to auto-grow, using the autoGrowInnerCellPool function, 
 * which simply obtains another 1MB outer cell for the next extent.
 *
 * @param bboashr_p
 * @param cell_size - The cell size for the newly created inner cell pool.
 * @param name - The name for the inner cell pool.
 *
 * @return the inner cell pool ID
 */
long long buildInnerCellPool( WolaSharedMemoryAnchor_t * bboashr_p, long long cell_size, char * name ) {

    void * inner_cp_addr = getCellPoolCell( bboashr_p->outerCellPoolID );

    buildCellPoolFlags flags;
    memset(&flags, 0, sizeof(buildCellPoolFlags));

    // TODO: can't use autoGrow since the cell pool is accessed by different
    //       address spaces (the client and the server).
    // flags.autoGrowCellPool = 1;
    long long cell_pool_id = buildCellPool(inner_cp_addr,
                                           BBOASHR_OUTER_CELL_POOL_CELL_SIZE,
                                           cell_size,
                                           name,
                                           flags);  

    // Setup auto-grow.
    // setCellPoolAutoGrowFunction(cell_pool_id, &(autoGrowInnerCellPool) );
    // setCellPoolUserData(cell_pool_id, (void *)bboashr_p);

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(91),
                    "buildInnerCellPool",
                    TRACE_DATA_PTR(bboashr_p, "bboashr_p"),
                    TRACE_DATA_RAWDATA(64, (void *)cell_pool_id, "cell_pool_id"),
                    TRACE_DATA_PTR(inner_cp_addr, "inner_cp_addr"),
                    TRACE_DATA_LONG(cell_size, "cell_size"),
                    TRACE_DATA_STRING(name, "name"),
                    TRACE_DATA_END_PARMS);
    }

    return cell_pool_id;
}

/**
 * @param fromHere - begin search for next quad word from this pointer
 *
 * @return a pointer aligned to the next quad word after the given fromHere pointer.
 */
void * nextQuadWord( void * fromHere ) {
    return (void *) ( (long)((char *)fromHere+15) & ~0x0FL);
}

/**
 * @param bboashr_p
 * @param outer_cp_size_p - Output - to contain the size of the outer cell pool storage area
 *
 * @return the address of the outer cell pool.
 */
void * getOuterCellPoolAddrAndSize( WolaSharedMemoryAnchor_t * bboashr_p, long long * outer_cp_size_p ) {

    void * outer_cp_addr = nextQuadWord( (void *) (((char *)bboashr_p) + sizeof(WolaSharedMemoryAnchor_t)) ); 
    *outer_cp_size_p = bboashr_p->anchorRequestedSize - ( (char *)outer_cp_addr - (char *)bboashr_p );
    
    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(101),
                    "getOuterCellPoolAddrAndLen",
                    TRACE_DATA_PTR(bboashr_p, "bboashr_p"),
                    TRACE_DATA_PTR(outer_cp_addr, "outer_cp_addr"),
                    TRACE_DATA_HEX_LONG(*outer_cp_size_p, "*outer_cp_size_p (dereferenced)"),
                    TRACE_DATA_END_PARMS);
    }

    return outer_cp_addr;
}

/**
 * The BBOASHR shared memory storage is managed via a "cell pool of cell pools".
 *
 * There's an "outer" cell pool that covers the entire storage area, carving it up
 * into big cells of 1MB each.  Each of those cells is managed by its own "inner"
 * cell pool, which carves up the 1MB chunk into smaller cells.  The inner cell pools
 * have varying cell sizes, depending on what type of data they hold. For example
 * there's an inner cell pool for BBOARGE elements, one for small-ish elements, others
 * for medium-ish and large-ish elements, and possibly others for whatever else we need.
 *
 * Each inner cell pool can auto-grow by obtaining another 1MB cell from the outer
 * cell pool (assuming there are outer cells available).
 *
 */
static void buildCellPools( WolaSharedMemoryAnchor_t * bboashr_p ) {

    long long outer_cp_size;
    void * outer_cp_addr = getOuterCellPoolAddrAndSize( bboashr_p, &outer_cp_size );

    buildCellPoolFlags flags ;
    memset(&flags, 0, sizeof(buildCellPoolFlags));

    // TODO: tailor the cell size to fit the available storage.  using 1MB ends up leaving a lot of unused space.
    // TODO: copy back RCs.
    bboashr_p->outerCellPoolID = buildCellPool(outer_cp_addr,
                                               outer_cp_size,
                                               BBOASHR_OUTER_CELL_POOL_CELL_SIZE,
                                               "BBGZWASP",
                                               flags);

    bboashr_p->registationCellPoolID = buildInnerCellPool( bboashr_p, sizeof(WolaRegistration_t), "BBGZWRGP" );
    bboashr_p->availServiceCellPoolID = buildInnerCellPool( bboashr_p, sizeof(struct availableService), "BBGZASQP");
    bboashr_p->waitServiceCellPoolID = buildInnerCellPool( bboashr_p, sizeof(struct waitService), "BBGZWSQP");

    bboashr_p->connectionHandleCellPoolID = buildInnerCellPool( bboashr_p, sizeof(WolaConnectionHandle_t), "BBGZHDLP");
    bboashr_p->connectionWaiterCellPoolID = buildInnerCellPool( bboashr_p, sizeof(WolaConnectionHandleWaiter_t), "BBGZWTRP");

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(11),
                    "buildCellPools",
                    TRACE_DATA_HEX_LONG(bboashr_p->outerCellPoolID, "bboashr_p->outerCellPoolID"),
                    TRACE_DATA_HEX_LONG(bboashr_p->registationCellPoolID, "bboashr_p->registationCellPoolID"),
                    TRACE_DATA_HEX_LONG(bboashr_p->availServiceCellPoolID, "bboashr_p->availServiceCellPoolID"),
                    TRACE_DATA_HEX_LONG(bboashr_p->waitServiceCellPoolID, "bboashr_p->waitServiceCellPoolID"),
                    TRACE_DATA_HEX_LONG(bboashr_p->connectionHandleCellPoolID, "bboashr_p->connectionHandleCellPoolID"),
                    TRACE_DATA_HEX_LONG(bboashr_p->connectionWaiterCellPoolID, "bboashr_p->connectionWaiterCellPoolID"),
                    TRACE_DATA_END_PARMS);
    }
}

/**
 * Initialize the BBOASHR structure.
 *
 * This method should only be called upon first creating the WOLA shared memory area
 * and BBOASHR anchor.
 *
 * @param bboashr_p - The shared memory area
 * @param wola_group - The wola group name.
 */
void initializeBboashr( void * bboashr_p, char * wola_group ) {

    WolaSharedMemoryAnchor_t * anchor_p = (WolaSharedMemoryAnchor_t *) bboashr_p;
    memset(anchor_p, 0, sizeof(WolaSharedMemoryAnchor_t));

    memcpy( anchor_p->eye, BBOASHR_EYE, 8 );
    anchor_p->version = BBOASHR_VERSION;      
    anchor_p->size = sizeof(WolaSharedMemoryAnchor_t);
    anchor_p->self_p = bboashr_p;
    memcpy( anchor_p->wolaGroup, wola_group, 8);
    anchor_p->anchorRequestedSize = WOLA_SMA_SIZE_MB * 1024 * 1024;

    buildCellPools( bboashr_p );
    
    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(61),
                    "initializeBboashr",
                    TRACE_DATA_RAWDATA(8, wola_group, "wola_group"),
                    TRACE_DATA_RAWDATA(sizeof(WolaSharedMemoryAnchor_t), anchor_p, "BBOASHR"),
                    TRACE_DATA_END_PARMS);
    }
}

/**
 * Map out the RegistryDataArea to take just the bboashr_p.
 */
struct RegistryDataArea_BboashrMap {
    void * bboashr_p;
    char unused[sizeof(RegistryDataArea)-sizeof(void *)];
};

/**
 * Allocate a RegistryToken for the given bboashr_p.  The RegistryToken is
 * passed back to the Java caller for later use under pc_detachFromBboashr.
 * We can't pass back the actual address of bboashr_p, because then the detach
 * routine would take the pointer, and we'd effectively be giving the Java caller
 * access to an authorized routine that they could use to detach from any old
 * piece of shared memory.  Not kosher.
 *
 * @param bboashr_p - The pointer to BBOASHR
 * @param registry_token - Output - The RegistryToken
 *
 * @return the RC from util_registry::registryPut
 */
int allocateRegistryTokenForBboashr( void * bboashr_p, RegistryToken * registry_token ) {

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(31),
                    "allocateRegistryTokenForBboashr entry",
                    TRACE_DATA_PTR(bboashr_p, "bboashr_p"),
                    TRACE_DATA_END_PARMS);
    }

    struct RegistryDataArea_BboashrMap dataArea;
    memset(&dataArea, 0, sizeof(RegistryDataArea));
    dataArea.bboashr_p = bboashr_p;

    // Put it in there.
    int rc = registryPut(WOLATKN, (RegistryDataArea *)&dataArea, registry_token);

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(32),
                    "allocateRegistryTokenForBboashr exit",
                    TRACE_DATA_INT(rc, "return code"),
                    TRACE_DATA_RAWDATA(sizeof(RegistryToken), registry_token, "registry token"),
                    TRACE_DATA_END_PARMS);
    }

    return rc;
}

/**
 * Get the bboashr_p from the given registry token.
 *
 * @param registry_token - The registry token
 * @param rc - Output - the rc from util_registry::registryGetAndSetUsed
 *
 * @return the bboashr_p from the registry token.
 */
void * getBboashrFromRegistryToken(RegistryToken * registry_token, int * rc) {

    struct RegistryDataArea_BboashrMap dataArea;

    *rc = registryGetAndSetUsed(registry_token, (RegistryDataArea *)&dataArea);

    int setUnused_rc = -1;

    if (*rc == 0) {
        // Note: not checking RC.  No need.  It gets traced, just in case.
        setUnused_rc = registrySetUnused(registry_token, 0);
    }

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(71),
                    "getBboashrFromRegistryToken",
                    TRACE_DATA_INT(*rc, "registryGetAndSetUsed rc"),
                    TRACE_DATA_INT(setUnused_rc, "registrySetUnused rc"),
                    TRACE_DATA_RAWDATA(sizeof(struct RegistryDataArea_BboashrMap), &dataArea, "RegistryDataArea"),
                    TRACE_DATA_PTR(dataArea.bboashr_p, "bboashr_p (in RegistryDataArea)"),
                    TRACE_DATA_END_PARMS);
    }

    if (*rc == 0) {
        return dataArea.bboashr_p;
    } else {
        return NULL;
    }
}



