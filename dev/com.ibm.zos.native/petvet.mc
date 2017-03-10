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
#include <metal.h>
#include <string.h>
#include "include/common_defines.h"
#include "include/mvs_storage.h"
#include "include/mvs_utils.h"
#include "include/petvet.h"
#include "include/ras_tracing.h"
#include "include/server_process_data.h"

#define RAS_MODULE_CONST  RAS_MODULE_SERVER_PETVET

#define PETVET_CELL_POOL_NAME                  "PETVETCP" //!< The name of the PETVET cell pool.

static void* getPETVETStorage(int limit, long long* size_p, petVetStorageFcn* storageFcn) {

    long long poolSize = computeCellPoolStorageRequirement(limit, sizeof(PetVetStackElement));
    void* storage = storageFcn((size_t)poolSize);
    if (storage != NULL) {
        *size_p = poolSize;
    }

    return storage;
}

static long long buildPetvetCellPool(int limit, petVetStorageFcn* storageFcn) {
    long long size;
    long long cellPoolToken = 0;
    void* initialStorage = getPETVETStorage(limit, &size, storageFcn);

    if (initialStorage != NULL) {
        buildCellPoolFlags flags;
        memset(&flags, 0, sizeof(flags));
        cellPoolToken = buildCellPool(initialStorage, size, sizeof(PetVetStackElement), PETVET_CELL_POOL_NAME, flags);
    }

    return cellPoolToken;
}

void initializePetVet(PetVet* petvet, int limit, petVetStorageFcn* storageFcn){

    if (storageFcn == NULL) {
        storageFcn = malloc;
    }

    initializeSerializedStack(&(petvet->petvetStack), limit);
    petvet->petvetCellpoolAnchor = buildPetvetCellPool(limit, storageFcn);
}

// create a PetVetStackElement
static PetVetStackElement* createPetVetStackElement(PetVet* petvet, iea_PEToken pauseElement) {
    int rc = 0;

    PetVetStackElement* petStackElement = 0;

    if (petvet->petvetCellpoolAnchor != 0) {
        petStackElement = (PetVetStackElement*)getCellPoolCell(petvet->petvetCellpoolAnchor);
    }

    if (petStackElement != 0) {
        memcpy(petStackElement->pauseElement, pauseElement, sizeof(iea_PEToken));
        memcpy(petStackElement->eyeCatcher,"ZPETVETE",8);
        if (TraceActive(trc_level_detailed)) {
            TraceRecord(trc_level_detailed,
                        TP(1),
                        "petvet: createPetVetStackElement",
                        TRACE_DATA_RAWDATA(sizeof(PetVetStackElement), petStackElement, "pet stack element"),
                        TRACE_DATA_END_PARMS);
        }
    }

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(2),
                    "petvet: exit createPetVetStackElement",
                    TRACE_DATA_PTR(petStackElement, "pet stack element ptr"),
                    TRACE_DATA_END_PARMS);
    }
    return petStackElement;
}

// See header for description.
void board(PetVet* petvet, iea_PEToken pauseElement) {

    PetVetStackElement* petStackElement;
    petStackElement = createPetVetStackElement(petvet, pauseElement);

    if (petStackElement != 0) {
        // push into PetVet stack
        if( pushOnSerializedStack( &(petvet->petvetStack), &(petStackElement->stackElement) ) != 0 ) {
            if (TraceActive(trc_level_detailed)) {
                TraceRecord(trc_level_detailed,
                            TP(3),
                            "petvet: board - stack full",
                            TRACE_DATA_END_PARMS);
            }
            // if PetVet stack is full, delete the pauseElement
            euthanise(pauseElement);
            freeCellPoolCell(petvet->petvetCellpoolAnchor, petStackElement);
        }
    } else {
        if (TraceActive(trc_level_detailed)) {
            TraceRecord(trc_level_detailed,
                        TP(4),
                        "petvet: board - could not create a stack element",
                        TRACE_DATA_END_PARMS);
        }
        euthanise(pauseElement);
    }
    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(5),
                    "petvet: board - done",
                    TRACE_DATA_END_PARMS);
    }
}

// See header for description.
int pickup(PetVet* petvet, iea_PEToken* outPET_p) {
    PetVetStackElement* targetPetStackElement;
    iea_PEToken allocatedPauseElement;
    iea_return_code petRc = 0;

    //pop from stack
    targetPetStackElement = (PetVetStackElement*)popOffSerializedStack(&(petvet->petvetStack));
    if(targetPetStackElement == NULL) {
         //allocate a PET
         iea_auth_type petAuthType = IEA_AUTHORIZED;
         //should be in key0 and supervisor state to use the service
         unsigned char oldKey = switchToKey0();
         iea4ape(&petRc, petAuthType, allocatedPauseElement);
         //switch back to previous key 2
         switchToSavedKey(oldKey);

         if (TraceActive(trc_level_detailed)) {
             TraceRecord(trc_level_detailed,
                         TP(6),
                         "petvet: pickup - allocated a PET(iea4ape)",
                         TRACE_DATA_RAWDATA(sizeof(iea_PEToken), &allocatedPauseElement, "pause element"),
                         TRACE_DATA_INT(petRc, "iea4ape return code"),
                         TRACE_DATA_END_PARMS);
         }
    } else {
        if (TraceActive(trc_level_detailed)) {
            TraceRecord(trc_level_detailed,
                        TP(7),
                        "petvet: pickup - popped an element",
                        TRACE_DATA_RAWDATA(sizeof(*targetPetStackElement), targetPetStackElement, "stack element"),
                        TRACE_DATA_INT(petRc, "iea4ape return code"),
                        TRACE_DATA_END_PARMS);
        }
        memcpy(allocatedPauseElement, targetPetStackElement->pauseElement, sizeof(iea_PEToken));
        // release storage of PetVetStackElement
        freeCellPoolCell(petvet->petvetCellpoolAnchor, targetPetStackElement);
    }
    // copy pet to callers area
    memcpy(outPET_p, &allocatedPauseElement, sizeof(*outPET_p));

    return petRc;
}

// See header for description.
int euthanise(iea_PEToken pauseElement) {
    // deallocate the PET
    iea_return_code deallocateRc;
    iea_auth_type petAuthType = IEA_AUTHORIZED;
    //should be in key0 and supervisor state to use the service
    unsigned char oldKey = switchToKey0();
    iea4dpe(&deallocateRc, petAuthType, pauseElement);
    //switch back to previous key 2
    switchToSavedKey(oldKey);
    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(8),
                    "petvet: euthanise - iea4dpe)",
                    TRACE_DATA_INT(deallocateRc, "iea4dpe return code"),
                    TRACE_DATA_RAWDATA(sizeof(iea_PEToken),pauseElement,"pause element"),
                    TRACE_DATA_END_PARMS);
    }
    return deallocateRc;
}

// See header for description.
void destroyPetVetPool(PetVet* petvet) {
    // call euthanise on all the pause elements in the PetVet
    PetVetStackElement* removedPetStackElement;
    removedPetStackElement = (PetVetStackElement*)popOffSerializedStack(&(petvet->petvetStack));
    while(removedPetStackElement != NULL) {
        euthanise(removedPetStackElement->pauseElement);
        // release storage of PetVetStackElement
        freeCellPoolCell(petvet->petvetCellpoolAnchor, removedPetStackElement);
        removedPetStackElement = (PetVetStackElement*)popOffSerializedStack(&(petvet->petvetStack));
    }
}

