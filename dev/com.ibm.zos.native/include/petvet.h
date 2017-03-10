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
 *
 */
#ifndef _BBOZ_PETVET_H
#define _BBOZ_PETVET_H

#include <ieac.h>
#include "mvs_cell_pool_services.h"
#include "stack_services.h"

/**
 * A PetVet Stack element.
 */
typedef struct PetVetStackElement {
    ElementDT stackElement;      // 16 bytes
    char eyeCatcher[8];
    iea_PEToken pauseElement;    // 16 bytes
} PetVetStackElement;

/**
 * A Pool of Authorized PETs implemented as a stack
 */
typedef struct PetVet {
  SerializedStack petvetStack;
  long long       petvetCellpoolAnchor;
} PetVet;                       // Size 0x28

/**
 * Storage function used when creating the PET cell pool.
 */
typedef void* petVetStorageFcn(size_t size);

/**
 * Routine to initialize a PetVet.
 *
 * @param petvet PetVet to initialize.
 * @param limit  Maximum number of pause element tokens allowed in the PetVet.
 * @param storageFcn A function which obtains storage.  If null, malloc() is used.
 *
 */
void initializePetVet(PetVet* petvet, int limit, petVetStorageFcn* storageFcn);

/**
 * Routine to board a pet.
 *
 * @param petvet PetVet to add a pause element token to.
 * @param pet  pause element token to be added to the PetVet.
 *
 */
void board(PetVet* petvet, iea_PEToken pet);

/**
 * Routine to pickup a pause element token.
 *
 * @param petvet PetVet to get a pause element token from.
 * @param outPET_p  Output area that gets a pause element token.
 *
 * @return iea4ape return code.
 *
 */
int pickup(PetVet* petvet, iea_PEToken* outPET_p);

/**
 * Routine to euthanise a pause element token.
 *
 * @param pet Pause element token to euthanise.
 *
 * @return Return code from iea4dpe.
 *
 */
int euthanise(iea_PEToken pet);              // deallocate a pet

/**
 * Routine to destroy the PetVet.
 *
 * @param petvet PetVet to destroy.
 *
 */
void destroyPetVetPool(PetVet* petvet);

#endif /* _BBOZ_PETVET_H */
