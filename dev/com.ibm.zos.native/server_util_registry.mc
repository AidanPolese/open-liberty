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
/**@file
 * A registry to keep stuff in (and get it back!).
 *
 * The registry will be kept in key 2 non-fetch-protected storage.  Thus,
 * you can only modify the registry from authorized code but anyone can see
 * the stuff you keep in here.  The registry is in private storage so it is
 * not visible from other servers.
 */

#include <stdlib.h>
#include <string.h>

#include "include/mvs_cell_pool_services.h"
#include "include/mvs_storage.h"
#include "include/ras_tracing.h"
#include "include/server_process_data.h"
#include "include/util_registry.h"

//---------------------------------------------------------------------
// Private defines.
//---------------------------------------------------------------------
#define RAS_MODULE_CONST RAS_MODULE_SERVER_UTIL_REGISTRY

//---------------------------------------------------------------------
// Error codes.
//---------------------------------------------------------------------
#define UTIL_REGISTRY_RC_NO_MEMORY      (RAS_MODULE_SERVER_UTIL_REGISTRY + 1)
#define UTIL_REGISTRY_RC_PENDING_FREE   (RAS_MODULE_SERVER_UTIL_REGISTRY + 2)
#define UTIL_REGISTRY_RC_BAD_TOKEN      (RAS_MODULE_SERVER_UTIL_REGISTRY + 3)
#define UTIL_REGISTRY_RC_BAD_DECREMENT  (RAS_MODULE_SERVER_UTIL_REGISTRY + 4)

#define REGISTRY_CELL_POOL_NAME            "BBGZREGP" //!< The name of the registry's cell pool.
#define REGISTRY_CELL_POOL_EXTENT_SIZE     65536      //!< The amount of storage to obtain each time the cell pool expands.
#define REGISTRY_ELEMENT_EYECATCHER    "BBGZREGE"     //!< The eyecatcher for the @c RegistryElement.
#define REGISTRY_CELL_POOL_STORAGE_KEY     2          //!< The storage key for the registry's cell pool.
#define REGISTRY_CELL_POOL_STORAGE_SUBPOOL 249        //!< The storage subpool for the registry's cell pool.

//---------------------------------------------------------------------
// Private types and variables.
//---------------------------------------------------------------------
/**
 * A double-word control structure for the RegistryElement that can be
 * used in a compare double and swap.
 */
#pragma pack(packed)
typedef struct {
    int          isFreed:1,       //!< Element is free
                 isFreePending:1, //!< Free is pending
                 reserved:14;     //!< Reserved bits
    short        useCount;        //!< In use counter
    int          instanceCounter; //!< Instance counter - must match counter in element
} RegistryElementControlBlock;
#pragma pack(reset)

/**
 * A registry entry.  Must be double-word aligned for CDS on control structure to work
*/
#pragma pack(packed)
typedef struct {
    unsigned char               eyecatcher[8]; //!< Eyecatcher @c REGISTRY_ELEMENT_EYECATCHER
    int                         version;       //!< Version
    int                         length;        //!< Element Length
    RegistryElementControlBlock controlBlock;  //!< Control structure
    int                         type;          //!< Type of data in element
    RegistryDataArea            data;          //!< The data
} RegistryElement;
#pragma pack(reset)

/**
 * Array of elementDestroyed_t function pointers used to drive a callback when
 * an element is destroyed, in case the data area of the element references
 * other objects that need to be cleaned up at the same time.
 */
elementDestroyed_t* destroyFunctions[REGISTRY_NUM_DATA_TYPES] = {
    NULL,
    &destroySAFNSCDataArea,
    NULL,
    NULL,
    NULL,
    NULL,
    NULL,
    NULL,
    &destroyAIOCONNDataArea
};

//---------------------------------------------------------------------
// Private function declarations.
//---------------------------------------------------------------------

/**
 * Function passed to the cell pool utilities, invoked to allocate storage when
 * the cell pool needs to expand.
 *
 * @param bytesAllocated A reference used by the function to store the number of bytes that were
 *                         actually allocated.
 *
 * @return A pointer to the storage that was allocated.
 */
void* getRegistryStorage(long long* sizePtr, long long cell_pool_id);

/**
 * Builds the initial cell pool for the registry.
 *
 * @return A token that can be used for future operations on the cell pool, such as to
 *           allocate a cell or free a cell.
 */
long long buildRegistryCellPool(void);

/**
 * Get the cell pool token for the registry, building the initial cell pool for the registry if it
 * doesn't already exist.
 *
 * @return A token that can be used for future operation on the cell pool, such as to
 *           allocate a cell or free a cell.
 */
long long getRegistryCellPoolToken(void);

/**
 * Frees a registry element.  No checking is done to determine whether or not the element is in
 * an appropriate state to be freed.  The data area is handed to the deletion function for the
 * element type, the @c instanceNumber of the element is incremented, and the cell is returned
 * to the pool.
 *
 * @param element The @c RegistryElement to be freed.
 */
void freeRegistryElement(RegistryElement* element);

/**
 * Increments the use count for the @c RegistryElement referenced by @c token.
 *
 * @param token A token that references a @c RegistryElement in the registry.
 *
 * @return 0 if the use count was successfully incremented, 
 *         an error code if the token was bad,
 *         an error code if the use count could not be incremented due to a free or a
 *         pending free
 */
int incrementUseCount(RegistryToken* token);

/**
 * Decrements the use count for the @c RegistryElement referenced by @c token.
 *
 * @param token A token that references a @c RegistryElement in the registry.
 * @parm alreadyVerified indication that a verify has already been done.
 *
 * @return 0 if the use count was successfully decremented, 
 *         an error code if the token was bad,
 *         an error code the token was good but the element is out of sync (either the
 *         element was already freed, or the use count is not positive)
 */
int decrementUseCount(RegistryToken* token, unsigned char alreadyVerified);

//---------------------------------------------------------------------
// Public function implementations.
//---------------------------------------------------------------------

/*
 * Documentation is in the header.
 */
int registryPut(RegistryDataType dataType, RegistryDataArea* dataAreaPtr, RegistryToken* outputTokenPtr) {
    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(1),
                    TRACE_DESC_FUNCTION_ENTRY,
                    TRACE_DATA_INT(dataType, "data type"),
                    TRACE_DATA_RAWDATA(sizeof(RegistryDataArea),
                                       dataAreaPtr,
                                       "data area"),
                    TRACE_DATA_END_PARMS);
    }

    int rc = 0;
    RegistryElement* element = (RegistryElement*) getCellPoolCell(getRegistryCellPoolToken());

    if (element != NULL) {
        memcpy(&(element->eyecatcher), REGISTRY_ELEMENT_EYECATCHER, 8);
        element->version = REGISTRY_VERSION;
        element->length = sizeof(RegistryElement);
        element->type = dataType;
        element->controlBlock.isFreed = 0;
        element->controlBlock.isFreePending = 0;
        element->controlBlock.useCount = 0;
        memcpy(&(element->data), dataAreaPtr, sizeof(RegistryDataArea));

        memset(outputTokenPtr, 0, sizeof(RegistryToken));
        memcpy(&(outputTokenPtr->eyecatcher), "BBGZREGT", 8);
        outputTokenPtr->version = 1;
        outputTokenPtr->length = sizeof(RegistryToken);
        outputTokenPtr->elementPtr = (char*) element;
        outputTokenPtr->instanceCounter = element->controlBlock.instanceCounter;
        outputTokenPtr->type = dataType;
    } else {
        rc = UTIL_REGISTRY_RC_NO_MEMORY;
    }

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(2),
                    TRACE_DESC_FUNCTION_EXIT,
                    TRACE_DATA_INT(rc, "return code"),
                    TRACE_DATA_RAWDATA((element == NULL ? 0 : sizeof(RegistryElement)),
                                       element,
                                       "element"),
                    TRACE_DATA_RAWDATA(sizeof(RegistryToken),
                                       outputTokenPtr,
                                       "token"),
                    TRACE_DATA_END_PARMS);
    }

    return rc;
}

/*
 * Documentation is in the header.
 */
int registryGetAndSetUsed(RegistryToken* token, RegistryDataArea* dataAreaPtr) {
    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(3),
                    TRACE_DESC_FUNCTION_ENTRY,
                    TRACE_DATA_RAWDATA(sizeof(RegistryToken),
                                       token,
                                       "token"),
                    TRACE_DATA_END_PARMS);
    }

    int rc = incrementUseCount(token);
    if (rc == 0) {
        RegistryElement* element = (RegistryElement*) token->elementPtr;
        memcpy(dataAreaPtr, &(element->data), sizeof(RegistryDataArea));
    }

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(4),
                    TRACE_DESC_FUNCTION_EXIT,
                    TRACE_DATA_INT(rc, "return code"),
                    TRACE_DATA_RAWDATA(sizeof(RegistryDataArea),
                                       dataAreaPtr,
                                       "data area"),
                    TRACE_DATA_END_PARMS);
    }

    return rc;
}

/*
 * Documentation is in the header.
 */
int registrySetUnused(RegistryToken* token, unsigned char alreadyVerified) {
    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(5),
                    TRACE_DESC_FUNCTION_ENTRY,
                    TRACE_DATA_BOOLEAN(alreadyVerified,
                                       "alreadyVerified"),
                    TRACE_DATA_RAWDATA(sizeof(RegistryToken),
                                       token,
                                       "token"),
                    TRACE_DATA_END_PARMS);
    }

    int rc = decrementUseCount(token, alreadyVerified);

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(6),
                    TRACE_DESC_FUNCTION_EXIT,
                    TRACE_DATA_INT(rc, "return code"),
                    TRACE_DATA_END_PARMS);
    }

    return rc;
}

/*
 * Documentation is in the header.
 */
int registryFree(RegistryToken* token, unsigned char alreadyVerified) {
    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(7),
                    TRACE_DESC_FUNCTION_ENTRY,
                    TRACE_DATA_RAWDATA(sizeof(RegistryToken),
                                       token,
                                       "token"),
                    TRACE_DATA_END_PARMS);
    }

    int rc = 0;
    int instance = token->instanceCounter;
    RegistryElement* element = (RegistryElement*) token->elementPtr;

    if (alreadyVerified || (verifyCellInPool(getRegistryCellPoolToken(), (void*) element, NULL) == 1)) {
        RegistryElementControlBlock oldControlBlock;
        RegistryElementControlBlock newControlBlock;
        memcpy(&oldControlBlock, &(element->controlBlock), sizeof(RegistryElementControlBlock));

        do {
            if (oldControlBlock.instanceCounter != instance) {
                break; // the element has been freed already, which is basically fine, it just
                       //   means that we have a stale token
            }
            else {
                memcpy(&newControlBlock, &oldControlBlock, sizeof(RegistryElementControlBlock));
                newControlBlock.isFreePending = 1;
                rc = __cds1((cds_t*) &oldControlBlock, (cds_t*) &(element->controlBlock), (cds_t*) &newControlBlock);
            }
        } while (rc == 1);  // CDS returns 0 on success, 1 if OLD != CURRENT (so NEW couldn't be swapped in).

        if ((rc == 0) && (newControlBlock.useCount == 0)) {
            // if rc is 1, that means we successfully swapped on the isFreePending bit which prevents
            //   anyone from incrementing the use count...  if the use count is 0, we can go ahead and
            //   free the element now, otherwise it will get freed when the count is decremented to 0
            freeRegistryElement(element);
        }
    } else {
        rc = UTIL_REGISTRY_RC_BAD_TOKEN;
    }

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(8),
                    TRACE_DESC_FUNCTION_EXIT,
                    TRACE_DATA_INT(rc, "return code"),
                    TRACE_DATA_END_PARMS);
    }

    return rc;
}

//---------------------------------------------------------------------
// Private function implementations.
//---------------------------------------------------------------------

/*
 * Documentation is in the declaration.
 */
void* getRegistryStorage(long long* size_p, long long cell_pool_id) {
    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(9),
                    TRACE_DESC_FUNCTION_ENTRY,
                    TRACE_DATA_END_PARMS);
    }

    int rc = 0;
    void *storage = (void*) storageObtain(REGISTRY_CELL_POOL_EXTENT_SIZE, REGISTRY_CELL_POOL_STORAGE_SUBPOOL, REGISTRY_CELL_POOL_STORAGE_KEY, &rc);

    if (storage != NULL) {
        *size_p = REGISTRY_CELL_POOL_EXTENT_SIZE;
    }

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(10),
                    TRACE_DESC_FUNCTION_EXIT,
                    TRACE_DATA_PTR(storage, "storage pointer"),
                    TRACE_DATA_END_PARMS);
    }

    return storage;
}

/*
 * Documentation is in the declaration.
 */
long long buildRegistryCellPool(void) {
    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(11),
                    TRACE_DESC_FUNCTION_ENTRY,
                    TRACE_DATA_END_PARMS);
    }

    long long size;
    void* initialStorage = getRegistryStorage(&size, 0);
    buildCellPoolFlags flags;
    flags.autoGrowCellPool = 1;
    long long cellPoolToken = buildCellPool(initialStorage, size, sizeof(RegistryElement), REGISTRY_CELL_POOL_NAME, flags);
    setCellPoolAutoGrowFunction(cellPoolToken, &(getRegistryStorage));

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(12),
                    TRACE_DESC_FUNCTION_EXIT,
                    TRACE_DATA_LONG(cellPoolToken, "cell pool token"),
                    TRACE_DATA_END_PARMS);
    }

    return cellPoolToken;
}

/*
 * Documentation is in the declaration.
 */
long long getRegistryCellPoolToken(void) {
    long long cellPoolToken;
    int rc = 0;
    server_process_data* spd = getServerProcessData();
    if (spd != NULL) {
        cellPoolToken = spd->registry_cell_pool_token;
        if (cellPoolToken == 0) {
            long long newCellPoolToken = buildRegistryCellPool();
            rc = __cds1(&cellPoolToken, &(spd->registry_cell_pool_token), &newCellPoolToken);
            if (rc == 1) {
                // Someone else created the registry cell pool first. Free the one we just created.
                storageRelease((void*)newCellPoolToken, REGISTRY_CELL_POOL_EXTENT_SIZE,
                               REGISTRY_CELL_POOL_STORAGE_SUBPOOL, REGISTRY_CELL_POOL_STORAGE_KEY);
            }
            // Whether or not this thread or another thread did a successful cds, the spd will have a token now.
            cellPoolToken = spd->registry_cell_pool_token;
        }
    }
    return cellPoolToken;
}

/*
 * Documentation is in the declaration.
 */
void freeRegistryElement(RegistryElement* element) {
    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(13),
                    TRACE_DESC_FUNCTION_ENTRY,
                    TRACE_DATA_RAWDATA(sizeof(RegistryElement),
                                       element,
                                       "element"),
                    TRACE_DATA_END_PARMS);
    }

    element->controlBlock.instanceCounter++;
    element->controlBlock.isFreed = 1;

    elementDestroyed_t* destroyFunction = destroyFunctions[element->type];
    if (destroyFunction != NULL) {
        destroyFunction(element->data);
    }

    freeCellPoolCell(getRegistryCellPoolToken(), (void*) element);

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(14),
                    TRACE_DESC_FUNCTION_EXIT,
                    TRACE_DATA_END_PARMS);
    }
}

/*
 * Documentation is in the declaration.
 */
int incrementUseCount(RegistryToken* token) {
    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(15),
                    TRACE_DESC_FUNCTION_ENTRY,
                    TRACE_DATA_RAWDATA(sizeof(RegistryToken),
                                       token,
                                       "token"),
                    TRACE_DATA_END_PARMS);
    }

    int rc = 0;
    int instance = token->instanceCounter;
    RegistryElement* element = (RegistryElement*) token->elementPtr;

    if (verifyCellInPool(getRegistryCellPoolToken(), (void*) element, NULL) == 1) {
        RegistryElementControlBlock oldControlBlock;
        RegistryElementControlBlock newControlBlock;
        memcpy(&oldControlBlock, &(element->controlBlock), sizeof(RegistryElementControlBlock));

        do {
            if ((oldControlBlock.instanceCounter != instance) ||
                (oldControlBlock.isFreePending == 1)) {
                rc = UTIL_REGISTRY_RC_PENDING_FREE; // element was freed or a free is pending, so we can't use it
            }
            else {
                memcpy(&newControlBlock, &oldControlBlock, sizeof(RegistryElementControlBlock));
                newControlBlock.useCount++;
                rc = __cds1((cds_t*) &oldControlBlock, (cds_t*) &(element->controlBlock), (cds_t*) &newControlBlock);
            }
        } while (rc == 1);  // CDS returns 0 on success, 1 if OLD != CURRENT (so NEW couldn't be swapped in).
    } else {
        rc = UTIL_REGISTRY_RC_BAD_TOKEN;
    }

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(16),
                    TRACE_DESC_FUNCTION_EXIT,
                    TRACE_DATA_INT(rc, "return code"),
                    TRACE_DATA_END_PARMS);
    }

    return rc;
}

/*
 * Documentation is in the declaration.
 */
int decrementUseCount(RegistryToken* token, unsigned char alreadyVerified) {
    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(17),
                    TRACE_DESC_FUNCTION_ENTRY,
                    TRACE_DATA_BOOLEAN(alreadyVerified,
                                       "alreadyVerified"),
                    TRACE_DATA_RAWDATA(sizeof(RegistryToken),
                                       token,
                                       "token"),
                    TRACE_DATA_END_PARMS);
    }

    int rc = 0;
    int instance = token->instanceCounter;
    RegistryElement* element = (RegistryElement*) token->elementPtr;

    if (alreadyVerified || (verifyCellInPool(getRegistryCellPoolToken(), (void*) element, NULL) == 1)) {
        RegistryElementControlBlock oldControlBlock;
        RegistryElementControlBlock newControlBlock;
        memcpy(&oldControlBlock, &(element->controlBlock), sizeof(RegistryElementControlBlock));

        do {
            if ((oldControlBlock.instanceCounter != instance) ||
                (oldControlBlock.useCount <= 0)) {
                rc = UTIL_REGISTRY_RC_BAD_DECREMENT; // this is really bad...  there's a code bug somewhere
            }
            else {
                memcpy(&newControlBlock, &oldControlBlock, sizeof(RegistryElementControlBlock));
                newControlBlock.useCount--;
                rc = __cds1((cds_t*) &oldControlBlock, (cds_t*) &(element->controlBlock), (cds_t*) &newControlBlock);
            }
        } while (rc == 1);  // CDS returns 0 on success, 1 if OLD != CURRENT (so NEW couldn't be swapped in).

        if ((rc == 0) &&
            (newControlBlock.useCount == 0) &&
            (newControlBlock.isFreePending == 1)) {
            freeRegistryElement(element);
        }
    } else {
        rc = UTIL_REGISTRY_RC_BAD_TOKEN;
    }

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(18),
                    TRACE_DESC_FUNCTION_EXIT,
                    TRACE_DATA_INT(rc, "return code"),
                    TRACE_DATA_END_PARMS);
    }

    return rc;
}
