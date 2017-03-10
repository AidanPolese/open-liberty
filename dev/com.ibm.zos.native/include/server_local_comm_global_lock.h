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
#ifndef _BBOZ_SERVER_LOCAL_COMM_GLOBAL_LOCK_H
#define _BBOZ_SERVER_LOCAL_COMM_GLOBAL_LOCK_H

/*
 * This is a temporary lock to be used to serialize the first pass of local
 * comm code.  The intention is to go back and replace the global lock with
 * finer scoped locking (ie compare-and-swap, PLO, or pause-release locking)
 * after a first pass of local comm is complete.
 *
 * Places that need to be serialized:
 *   - Adding LSCLs to LOCL chain
 *   - Adding connection handles to LSCL
 *   - Updating queues in connection handles and on server
 *   - others...
 */

/**
 * Obtain the local comm global lock.  A name token will be created on the
 * caller's task which will be required when the lock is freed.
 */
void obtainLocalCommGlobalLock(void);

/**
 * Release the local comm global lock.  The name token which was created on
 * the caller's task by obtainLocalCommGlobalLock() will be required to
 * release the lock.
 */
void releaseLocalCommGlobalLock(void);

#endif
