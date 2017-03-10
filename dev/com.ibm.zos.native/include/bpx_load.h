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
#ifndef _BBOZ_BPX_LOAD_H
#define _BBOZ_BPX_LOAD_H

/**
 * Structure mapping the return values from an HFS load.
 */
typedef struct loadhfs_details loadhfs_details;
struct loadhfs_details
{
  int mod_len;
  void* __ptr32 mod_p;
  void* __ptr32 entry_p;
  char delete_token[8];
};

/**
 * Load a load module from the HFS using BPX4LDX, into common storage.
 *
 * @param pathname The pathname to the load module in the HFS.
 *
 * @return A struct containing the entry point, length, and delete token
 *         for the loaded module.  The caller owns this storage which
 *         must be freed using the free() function.
 */
loadhfs_details* load_from_hfs(char* pathname);

/**
 * Load a load module from the HFS using BPX4LDX into private storage.
 *
 * @param pathname The pathname to the load module in the HFS.
 *
 * @return A struct containing the entry point and delete token for
 *         the load module.  The length and module pointers will be
 *         empty.  The caller owns this storage which must be freed
 *         using the free() function.
 */
loadhfs_details* load_from_hfs_private(char* pathname);

/**
 * Removes a module from memory.
 *
 * @param details A pointer to the structure returned by load_from_hfs
 *                or load_from_hfs_private.
 */
void unload_from_hfs(loadhfs_details* details);

/**
 * Removes a module from private storage (when loaded with load_from_hfs_private).
 *
 * @param entrypt_p The entry point to the loaded module.
 * @param rc_p A pointer to a field where the return code from BPX4DEL is stored.
 *             This field is only valid if the return value is -1.
 * @param rsn_p A pointer to a field where the reason code from BPX4DEL is stored.
 *              This field is only valid if the return value is -1.
 * @param rv_p A pointer to a field where the return value from BPX4DEL is stored.
 *             This field will contain 0 on success and -1 on failure.
 */
void unload_from_hfs_private(void* entrypt_p, int* rc_p, int* rsn_p, int* rv_p);

#endif

