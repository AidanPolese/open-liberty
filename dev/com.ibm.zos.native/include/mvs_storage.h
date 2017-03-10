/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2011,2013
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
#ifndef _BBOZ_MVS_STORAGE_H
#define _BBOZ_MVS_STORAGE_H

/**
 * Invokes the STORAGE OBTAIN macro to obtain storage.
 *
 * @param length The length of storage to obtain.
 * @param subpool The subpool to obtain the storage from.
 * @param key The key that the storage should be allocated in.
 * @param rc_p A pointer to an integer representing the return code from
 *             the STORAGE macro.
 *
 * @return A pointer to the storage, or NULL if an error occurred.
 */
void* storageObtain(int length, int subpool, int key, int* rc_p);

/**
 * Releases storage obtained from storageObtain.
 *
 * @param addr A pointer to the storage obtained by storageObtain.
 * @param length The length of the storage
 * @param subpool The subpool which the storage was obtained from.
 * @param key The key that the storage is in
 *
 * @return The return code from the STORAGE macro.  0 is success.
 */
int storageRelease(void* addr, int length, int subpool, int key);

/**
 * Releases storage obtained from storageObtain.
 *
 * @param addr A pointer to the storage obtained by storageObtain.
 * @param length The length of the storage
 * @param subpool The subpool which the storage was obtained from.
 * @param key The key that the storage is in
 * @param tcbAddr The storage owning TCB address.
 *
 * @return The return code from the STORAGE macro.  0 is success.
 */
int storageReleaseTcb(void* addr, int length, int subpool, int keym , void* tcbAddr);
#endif
