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
#ifndef _BBOZ_MVS_SETLOCK_H
#define _BBOZ_MVS_SETLOCK_H

/** The size of the save area supplied to getLocalLock and releaseLocalLock. */
#define LOCAL_LOCK_SAVE_AREA_SIZE 72

/**
 * Gets the local lock unconditionally.  This function will switch to key 0
 * to do the SETLOCK, and then back to the callers key.
 *
 * @param saveArea_p A pointer to a 72 byte save area which is allocated below
 *                   the bar.
 *
 * @return 0 if the lock was obtained successfully, 4 if the lock was already
 *         held by this task.  Any other return value indicates the lock was
 *         not obtained.
 */
int getLocalLock(void* saveArea_p);

/**
 * Releases the local lock unconditionally.  This function will switch to key 0
 * to do the SETLOCK, and then back to the caller's key.
 *
 * @param saveArea_p A pointer to a 72 byte save area which is allocated below
 *                   the bar.
 *
 * @return 0 if the lock was released successfully.  Any other value indicates
 *         the lock was not released.
 */
int releaseLocalLock(void* saveArea_p);

#endif
