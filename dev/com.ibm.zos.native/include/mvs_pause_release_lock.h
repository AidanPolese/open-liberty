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

/** @file
 * A lock that uses compare and swap, and MVS pause/release, to act as a latch
 * and allow a single task to be executing at any one time.
 *
 * Lock ownership is controlled by a stack, with a sequence number and pointer,
 * modified via compare double and swap grande (CDSG).  To request access to
 * the lock, the caller pushes himself onto the stack.  The furthest entry down
 * the stack is the lock owner.  Therefore, the first person to push himself
 * onto the stack has the lock and no further action is required.  The lock
 * owner receives a token which must be supplied when releasing the lock.
 *
 * If there is already someone on the stack, you request ownership by pushing
 * yourself onto the stack.  You allocate a pause element and place the token
 * into the entry that you push onto the stack.  Multiple people can do this
 * concurrently so CDSG is used.  Note that there is no reason to create a
 * pause element if the stack is empty.
 *
 * When the lock owner wants to release the lock, he traverses the stack,
 * finding the next-to-last element in the stack (the one previous to himself).
 * He then then does two things -- removes himself from the stack, and releases
 * the pause element for the next-to-last element (now the last element).  The
 * lock owner is allowed to modify the portion of the stack below the head of
 * the stack, because only the lock owner is allowed to modify this portion of
 * the stack.  After releasing the pause element, the paused task now owns the
 * lock.  If the lock owner is the only element on the stack, he removes himself
 * from the stack using CDSG, and no one owns the lock (the stack is empty).
 *
 * Storage for the waiter element on the stack is obtained from a cell pool,
 * whose storage is obtained from storageObtain.  This was chosen because the
 * first user of this lock was the code which provided serialization around the
 * metal C malloc/malloc31 and free services.  Therefore, malloc could not be
 * used to obtain the storage for the lock, since that call to malloc would not
 * be serialized.
 *
 * The requirement for the token on release is only a 'belt-and-suspenders'
 * check, and can be removed at some point in the future when the code is
 * deemed stable.  The token is only used to ensure that the last element in
 * the stack is the lock owner.
 */

#ifndef BBGZ_MVS_PAUSE_RELEASE_LOCK_H
#define BBGZ_MVS_PAUSE_RELEASE_LOCK_H

#include <ieac.h>

#include "gen/ihapsa.h"
#include "gen/ikjtcb.h"

/** Eye catcher for the lock. */
#define PAUSE_RELEASE_LOCK_EYECATCHER "BBGZPRL_"

/** Eye catcher for the lock token. */
#define PAUSE_RELEASE_LOCK_TOKEN_EYECATCHER "BBGZPRLT"

/**
 * Data structure describing a waiter.
 */
typedef struct pauseReleaseLockWaiter {
    iea_PEToken pauseToken; //!< The PET that the waiter is paused on.
    tcb* tcb_p;             //!< The task that is waiting.
} PauseReleaseLockWaiter_t;

/**
 * Data structure describing a node in the waiter stack.
 */
typedef struct pauseReleaseLockWaiterStackNode {
    struct pauseReleaseLockWaiterStackNode* next_p; //!< The next waiter.
    PauseReleaseLockWaiter_t waiter; //!< The actual waiter.
} PauseReleaseLockWaiterStackNode_t;

/**
 * Structure representing the head of the waiter stack.
 */
typedef struct pauseReleaseLockWaiterStack {
    long long headCount;        //!< CDSG counter for the head.
    PauseReleaseLockWaiterStackNode_t* head_p; //!< Head of the waiter stack.
} PauseReleaseLockWaiterStack_t;

/**
 * The data structure for the lock.  Structure must be quad word alligned.
 */
typedef struct pauseReleaseLock {
    char eyecatcher[8];         //!< Eye catcher
    long long waiterCellPoolId; //!< The cell pool used to make waiter nodes.
    PauseReleaseLockWaiterStack_t waiterStack; //!< The stack of waiters.
} PauseReleaseLock_t;

/**
 * Token returned when the lock is obtained.  The token must be provided when
 * the lock is released.
 */
typedef struct pauseReleaseLockToken {
    char eyecatcher[8];                 //!< Eye catcher
    PauseReleaseLockWaiter_t* waiter_p; //!< Pointer to the waiter element
} PauseReleaseLockToken_t;

/**
 * Create the pause release lock.
 *
 * @param lock_p A pointer to a PauseReleaseLock_t structure that will be filled
 *               in with the information needed to obtain and release the lock.
 *
 * @return 0 if the lock was created successfully, nonzero if error.
 */
int createPauseReleaseLock(PauseReleaseLock_t* lock_p);

/**
 * Destroys the pause release lock.  The caller should ensure that no one is using
 * the lock when it is destroyed.
 *
 * @param lock_p A pointer to the PauseReleaseLock_t that will be freed.
 */
void destroyPauseReleaseLock(PauseReleaseLock_t* lock_p);

/**
 * Obtain the pause release lock.  The caller will be blocked until the lock is
 * obtained or an unrecoverable error occurs.
 *
 * @param lock_p A pointer to the lock returned by createPauseReleaseLock.
 * @param token_p A pointer to the lock token which is filled in if the lock
 *                is obtained (rc = 0).
 *
 * @return 0 if the lock was obtained, nonzero if not.
 */
int obtainPauseReleaseLock(PauseReleaseLock_t* lock_p, PauseReleaseLockToken_t* token_p);

/**
 * Release the pause release lock.
 *
 * @param lock_p A pointer to the lock to release.
 * @param token_p A pointer to the lock token returned by obtainPauseReleaseLock.
 *
 * @return 0 if the lock was released successfully, nonzero if not.
 */
int releasePauseReleaseLock(PauseReleaseLock_t* lock_p, PauseReleaseLockToken_t* token_p);

#endif
