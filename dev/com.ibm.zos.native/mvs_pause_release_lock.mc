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

#include "include/mvs_pause_release_lock.h"

#include <metal.h>
#include <stdlib.h>
#include <string.h>

#include "include/common_defines.h"
#include "include/mvs_cell_pool_services.h"
#include "include/mvs_storage.h"
#include "include/mvs_utils.h"

/** Number of bytes to obtain when growing the waiter cell pool. */
#define GROW_WAITER_CELL_POOL_BYTES 1024

/** Subpool to get the storage for the waiter cell pool from, for authorized callers. */
#define GROW_WAITER_CELL_POOL_AUTH_SUBPOOL 249 // Jobstep, high private, not fetch prot.

/** Subpool to get the storage for the waiter cell pool from, for unauthorized callers. */
#define GROW_WAITER_CELL_POOL_UNAUTH_SUBPOOL 132 // Jobstep, low private, not fetch prot.

/** The string identifying the cell pool for the waiters. */
#define PAUSE_RELEASE_LOCK_WAITER_CELL_POOL_NAME "BBGZPRLP"

/**
 * Gets storage for the waiter cell pool.
 */
static void* getWaiterCellPoolStorage(long long* bytes_allocated_p, long long cell_pool_id) {
    bbgz_psw psw;
    extractPSW(&psw);
    int key = psw.key;
    int subpool = (psw.pbm_state == 1) ? GROW_WAITER_CELL_POOL_UNAUTH_SUBPOOL : GROW_WAITER_CELL_POOL_AUTH_SUBPOOL;
    void* storage_p = storageObtain(GROW_WAITER_CELL_POOL_BYTES, subpool, key, NULL);
    if (storage_p != NULL) {
        *bytes_allocated_p = GROW_WAITER_CELL_POOL_BYTES;
    }
    return storage_p;
}

// Create a lock.
int createPauseReleaseLock(PauseReleaseLock_t* lock_p) {
    int rc = -1;
    long long storageSize = 0;

    // -----------------------------------------------------------------------
    // Get some storage for a cell pool of waiters.  We use the same method to
    // get storage that the cell pool will use later to grow the pool.
    // -----------------------------------------------------------------------
    void* cellPoolStorage_p = getWaiterCellPoolStorage(&storageSize, 0);
    if (cellPoolStorage_p != NULL) {
        buildCellPoolFlags flags;
        memset(&flags, 0, sizeof(flags));
        flags.autoGrowCellPool = 1;
        flags.skipInitialCellAllocation = 1;
        long long cellPoolId = buildCellPool(cellPoolStorage_p, storageSize, sizeof(PauseReleaseLockWaiterStackNode_t), PAUSE_RELEASE_LOCK_WAITER_CELL_POOL_NAME, flags);
        if (cellPoolId != 0L) {
            setCellPoolAutoGrowFunction(cellPoolId, getWaiterCellPoolStorage);

            // ---------------------------------------------------------------
            // Initialize the lock control block.
            // ---------------------------------------------------------------
            memcpy(lock_p->eyecatcher, PAUSE_RELEASE_LOCK_EYECATCHER, sizeof(lock_p->eyecatcher));
            lock_p->waiterCellPoolId = cellPoolId;
            lock_p->waiterStack.headCount = 0;
            lock_p->waiterStack.head_p = NULL;
            rc = 0;
        } else {
            // TODO: Release storage for cell pool.
            rc = -2;
        }
    }

    return rc;
}

// Function used to free the memory associated with the waiter cell pool.
// See comments in mvs_cell_pool_services.mc.  Since we skipped the initial allocatation,
// and are using auto-grow, we want to free the anchor and the cell storage only.
static void freeWaiterCellPoolStorage(unsigned char storageType, void* storage_p, long long id) {
    if (storage_p != NULL) {
        if ((storageType == CELL_POOL_CELL_STORAGE_TYPE) ||
            (storageType == CELL_POOL_ANCHOR_STORAGE_TYPE)) {
            bbgz_psw psw;
            extractPSW(&psw);
            int key = psw.key;
            int subpool = (psw.pbm_state == 1) ? GROW_WAITER_CELL_POOL_UNAUTH_SUBPOOL : GROW_WAITER_CELL_POOL_AUTH_SUBPOOL;
            storageRelease(storage_p, GROW_WAITER_CELL_POOL_BYTES, subpool, key);
        }
    }
}

// Destroys the lock.
void destroyPauseReleaseLock(PauseReleaseLock_t* lock_p) {
    destroyCellPool(lock_p->waiterCellPoolId, freeWaiterCellPoolStorage);
}

// Obtain a lock.
int obtainPauseReleaseLock(PauseReleaseLock_t* lock_p, PauseReleaseLockToken_t* token_p) {
    int rc = -1;

    if ((lock_p != NULL) && (memcmp(lock_p->eyecatcher, PAUSE_RELEASE_LOCK_EYECATCHER, sizeof(lock_p->eyecatcher)) == 0)) {
        // -------------------------------------------------------------------
        // Make a waiter element.  We're not necessarily going to allocate the
        // PET and wait.  We'll only do that if the stack of waiters is not
        // empty.
        // -------------------------------------------------------------------
        psa* psa_p = 0;
        tcb* tcb_p = (tcb*) psa_p->psatold;
        PauseReleaseLockWaiterStackNode_t* node_p = getCellPoolCell(lock_p->waiterCellPoolId);
        if (node_p != NULL) {
            node_p->next_p = NULL;
            node_p->waiter.tcb_p = tcb_p;
            memset(node_p->waiter.pauseToken, 0, sizeof(node_p->waiter.pauseToken));

            // ---------------------------------------------------------------
            // Try to get the lock.  Since we're using CDSG to push ourselves
            // onto a stack, this may require several attempts.
            // ---------------------------------------------------------------
            unsigned char allocatedPET = FALSE;
            unsigned char gotLock = FALSE;
            unsigned char errorCase = FALSE;
            while ((gotLock == FALSE) && (errorCase == FALSE)) {
                PauseReleaseLockWaiterStack_t oldStackHead, newStackHead;
                memcpy(&oldStackHead, &(lock_p->waiterStack), sizeof(oldStackHead));
                newStackHead.headCount = oldStackHead.headCount + 1;
                newStackHead.head_p = node_p;
                node_p->next_p = oldStackHead.head_p;

                // -----------------------------------------------------------
                // If there's no stack head, we can try to get the lock
                // without allocating a pause element.  Otherwise, allocate
                // a pause element.
                // -----------------------------------------------------------
                if ((oldStackHead.head_p != NULL) && (allocatedPET == FALSE)) {
                    iea_return_code petRc;
                    iea_auth_type petAuthType = IEA_UNAUTHORIZED;
                    iea4ape(&petRc, petAuthType, node_p->waiter.pauseToken);
                    allocatedPET = (petRc == 0);
                    errorCase = !allocatedPET;
                }

                // -----------------------------------------------------------
                // If we were able to get a PET, or if we didn't need one,
                // push ourselves onto the waiter stack.
                // -----------------------------------------------------------
                if (errorCase == FALSE) {
                    if (__cdsg(&oldStackHead, &(lock_p->waiterStack), &newStackHead) == 0) {
                        // ---------------------------------------------------
                        // Pause if we need to.
                        // ---------------------------------------------------
                        if (oldStackHead.head_p != NULL) {
                            iea_return_code pauseRc;
                            iea_auth_type pauseAuthType = IEA_UNAUTHORIZED;
                            iea_release_code releaseCode;
                            iea4pse(&pauseRc, pauseAuthType, node_p->waiter.pauseToken, node_p->waiter.pauseToken, releaseCode);
                            errorCase = (pauseRc != 0);
                        }

                        if (errorCase == FALSE) {
                            gotLock = TRUE;
                            memcpy(token_p->eyecatcher, PAUSE_RELEASE_LOCK_TOKEN_EYECATCHER, sizeof(token_p->eyecatcher));
                            token_p->waiter_p = &(node_p->waiter);
                            rc = 0;
                        }

                        // ----------------------------------------------------
                        // Free the pause element if we need to.  This is
                        // either a PET we didn't need because it turned out
                        // there was no one else waiting, or the new PET
                        // returned by pause.
                        // ----------------------------------------------------
                        if (allocatedPET == TRUE) {
                            iea_return_code dpetRc;
                            iea_auth_type dpetAuthType = IEA_UNAUTHORIZED;
                            iea4dpe(&dpetRc, dpetAuthType, node_p->waiter.pauseToken);
                        }
                    }
                }
            }
        } else {
            rc = -2;
        }
    }

    return rc;
}

// Release a lock.
int releasePauseReleaseLock(PauseReleaseLock_t* lock_p, PauseReleaseLockToken_t* token_p) {
    int rc = -1;

    if ((lock_p != NULL) && (memcmp(lock_p->eyecatcher, PAUSE_RELEASE_LOCK_EYECATCHER, sizeof(lock_p->eyecatcher)) == 0)) {
        if ((token_p != NULL) && (memcmp(token_p->eyecatcher, PAUSE_RELEASE_LOCK_TOKEN_EYECATCHER, sizeof(token_p->eyecatcher)) == 0)) {
            // ---------------------------------------------------------------
            // We need to hand off the lock to the next waiter, unless we're
            // the only element on the stack, in which case we're done.
            // ---------------------------------------------------------------
            unsigned char releasedLock = FALSE;
            unsigned char errorCase = FALSE;
            while ((releasedLock == FALSE) && (errorCase == FALSE)) {
                PauseReleaseLockWaiterStackNode_t* prev_p = NULL;
                PauseReleaseLockWaiterStackNode_t* cur_p = lock_p->waiterStack.head_p;

                if (cur_p != NULL) {
                    // -------------------------------------------------------
                    // If we're the only element on the waiter stack, we just
                    // compare and swap ourselves off the stack and we're done.
                    // -------------------------------------------------------
                    if (&(cur_p->waiter) == token_p->waiter_p) {
                        PauseReleaseLockWaiterStack_t oldStackHead, newStackHead;
                        memcpy(&oldStackHead, &(lock_p->waiterStack), sizeof(oldStackHead));
                        newStackHead.headCount = oldStackHead.headCount + 1;
                        newStackHead.head_p = NULL;
                        releasedLock = (__cdsg(&oldStackHead, &(lock_p->waiterStack), &newStackHead) == 0);
                        if (releasedLock) {
                            freeCellPoolCell(lock_p->waiterCellPoolId, cur_p);
                            rc = 0;
                        }
                    } else {
                        // ---------------------------------------------------
                        // If we're not the only element on the stack, we need
                        // to find ourselves down the stack, and the previous
                        // element, who will become the new lock owner.
                        // ---------------------------------------------------
                        while ((cur_p != NULL) && (&(cur_p->waiter) != token_p->waiter_p)) {
                            prev_p = cur_p;
                            cur_p = prev_p->next_p;
                        }

                        // ---------------------------------------------------
                        // Hopefully we found ourselves... alter the previous
                        // element (the next lock owner) whose next pointer
                        // will now point to NULL, and release it's PET.
                        // ---------------------------------------------------
                        if (cur_p != NULL) {
                            prev_p->next_p = NULL;
                            iea_return_code releaseRc;
                            iea_auth_type authType = IEA_UNAUTHORIZED;
                            iea_release_code releaseCode;
                            memset(releaseCode, 0, sizeof(releaseCode));
                            iea4rls(&releaseRc, authType, prev_p->waiter.pauseToken, releaseCode);
                            if (releaseRc == 0) {
                                // --------------------------------------------
                                // If the release went well, we're done.
                                // --------------------------------------------
                                freeCellPoolCell(lock_p->waiterCellPoolId, cur_p);
                                releasedLock = TRUE;
                                rc = 0;
                            } else {
                                // --------------------------------------------
                                // If the release did not go well, we have a
                                // problem.  Somehow the waiter is not waiting.
                                // At least we'll assume that's the case.  We
                                // will try to remove this waiter from the
                                // stack and loop around to try the next waiter.
                                // --------------------------------------------
                                prev_p->next_p = cur_p;  // Re-establish the chain
                                if (prev_p == lock_p->waiterStack.head_p) {
                                    // ----------------------------------------
                                    // If the bad waiter is at the head of the
                                    // stack, we need to use CDSG to get rid
                                    // of it.  That also means that there will
                                    // be no lock owner when we're done.
                                    // ----------------------------------------
                                    PauseReleaseLockWaiterStack_t oldStackHead, newStackHead;
                                    memcpy(&oldStackHead, &(lock_p->waiterStack), sizeof(oldStackHead));
                                    newStackHead.headCount = oldStackHead.headCount + 1;
                                    newStackHead.head_p = NULL;
                                    releasedLock = (__cdsg(&oldStackHead, &(lock_p->waiterStack), &newStackHead) == 0);
                                    if (releasedLock) {
                                        freeCellPoolCell(lock_p->waiterCellPoolId, cur_p);
                                        freeCellPoolCell(lock_p->waiterCellPoolId, prev_p);
                                        rc = 0;
                                    }
                                } else {
                                    // ----------------------------------------
                                    // If the bad waiter is not at the head of
                                    // the stack, we need to scan the stack
                                    // again, to find the previous element, and
                                    // modify its next element to skip the bad
                                    // waiter.
                                    // ----------------------------------------
                                    PauseReleaseLockWaiterStackNode_t* target_p = prev_p;
                                    prev_p = NULL;
                                    cur_p = lock_p->waiterStack.head_p;
                                    while ((cur_p != NULL) && (cur_p != target_p)) {
                                        prev_p = cur_p;
                                        cur_p = prev_p->next_p;
                                    }
                                    if (cur_p != NULL) {
                                        prev_p->next_p = cur_p->next_p;
                                    } else {
                                        errorCase = TRUE;
                                        rc = -5;
                                    }
                                }
                            }
                        } else {
                            errorCase = TRUE;
                            rc = -4;
                        }
                    }
                } else {
                    errorCase = TRUE;
                    rc = -3;
                }
            }

        } else {
            rc = -2;
        }
    }

    return rc;
}
