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
#ifndef _BBOZ_MVS_IARV64_H
#define _BBOZ_MVS_IARV64_H

#include "mvs_utils.h" // TToken
#include "gen/iaxv64wa.h" // LIST mappings

/**
 * Gets shared avove the bar storage using the IARV64 macro.
 *
 * @param megs The number of megabytes of storage to obtain.
 * @param fetchProtected 1 if the storage should be fetch protected, 0 if not
 * @param userToken The 8 byte user token to provide on the IARV64 macro
 *
 * @return A pointer to the storage requested.  The storage should be accessed
 *         before it's used by calling accessSharedAbove from each address
 *         space using the storage (including the address space calling
 *         getSharedAbove).
 */
void* getSharedAbove(long long megs,
                     unsigned char fetchProtected,
                     long long userToken);

/**
 * Accesses storage obtained by getSharedAbove.
 *
 * @param storage_p A pointer to the storage returned by getSharedAbove.
 * @param userToken The 8 byte user token to provide on the IARV64 macro.
 */
void accessSharedAbove(void* storage_p, long long userToken);

/**
 * Accesses storage obtained by getSharedAbove.  Does not abend on failure,
 * but returns RC / RSN.
 *
 * @param storage_p A pointer to the storage returned by getSharedAbove.
 * @param userToken The 8 byte user token to provide on the IARV64 macro.
 * @param rsn_p A pointer to storage where the reason code will be stored.
 *
 * @return The return code from IARV64.
 */
int accessSharedAboveConditional(void* storage_p, long long userToken, int* rsn_p);

/**
 * Detaches this address space from the shared storage.  This is the opposite
 * of accessSharedAbove.
 *
 * @param storage_p A pointer to the storage returned by getSharedAbove
 * @param userToken The 8 byte user token to provide on the IARV64 macro.
 * @param systemAffinity 0 to remove an address space's connection to the
 *                       shared storage, 1 to remove the system affinity.
 *                       Once all address space affinities and the system
 *                       affinity are gone, the storage is returned to the
 *                       system.  The address space requesting the storage
 *                       has ownership of the system affinity.
 */
void detachSharedAbove(void* storage_p,
                       long long userToken,
                       unsigned char systemAffinity);

/**
 * Performs the same function as detachedSharedAbove, but returns a RC/RSN
 * from IARV64 rather than abend if an error is detected.
 *
 * @param storage_p A pointer to the storage returned by getSharedAbove
 * @param userToken The 8 byte user token to provide on the IARV64 macro.
 * @param systemAffinity 0 to remove an address space's connection to the
 *                       shared storage, 1 to remove the system affinity.
 *                       Once all address space affinities and the system
 *                       affinity are gone, the storage is returned to the
 *                       system.  The address space requesting the storage
 *                       has ownership of the system affinity.
 * @param rsn_p A pointer to the reason code returned by IARV64 if an
 *              error is encountered.
 *
 * @return The return code from IARV64.
 */
int detachSharedAboveConditional(void* storage_p,
                                 long long userToken,
                                 unsigned char systemAffinity,
                                 int* rsn_p);

#define IARV64_SHARED_READONLY 0
#define IARV64_SHARED_READWRITE 1
#define IARV64_SHARED_HIDDEN 2
/**
 * Change access to a region of shared memory from one access level to another.
 * The valid access levels are:
 *  IARV64_SHARED_READONLY - Read only access to the region
 *  IARV64_SHARED_READWRITE - Normal read/write access
 *  IARV64_SHARED_HIDDEN - You will program check if you access the memory.
 *
 * @param storage_p A pointer to the start of the area to change access level.
 *                  Per the IARV64 doc, this must be on a segment boundary.
 * @param numSegments The number of segments (megs) to change access level.
 * @param access The access level desired.
 * @param rsn_p A pointer to the reason code returned by IARV64 if an
 *              error is encountered.
 *
 * @return The return code from IARV64.
 */
int changeSharedAccessLevel(void* storage_p,
                            long long numSegments,
                            unsigned int access,
                            int* rsn_p);

/**
 * List shared memory segments that are allocated on the entire system.
 *
 * @return A pointer to storage that contains a list of shared memory object
 *         information, and a return code.  This storage was obtained on the
 *         caller's behalf using malloc31(), and should be freed using free().
 *         If the pointer is NULL, the list of memory objects could not be
 *         obtained.
 */
v64waheader* listSharedAbove(void);

/**
 * Obtains non-shared storage using IARV64.  The storage is allocated in the
 * caller's key and is not fetch protected.
 *
 * @param segments The number of segments to obtain.  One segment is 1 megabyte.
 * @param guardSegments The number of guard segments to obtain.  One segment is
 *                      1 megabyte.  Guard segments are allocated at the end of
 *                      the memory segment (HIGH).
 * @param ttoken_p A pointer to the TToken which will own the storage.
 * @param rc_p A pointer to a full word where the return code from IARV64 is
 *             stored.  If this parameter is NULL, the return code is not
 *             returned.
 * @param rsn_p A pointer to a full word where the reason code from IARV64 is
 *              stored.  If this parameter is NULL, the reason code is not
 *              returned.
 *
 * @return A pointer to the allocated storage, or NULL if not available.
 */
void* obtain_iarv64(int segments, int guardSegments, TToken* ttoken_p, int* rc_p, int* rsn_p);

/**
 * Releases non-shared storage obtained by IARV64.
 *
 * @param storage_p A pointer to the storage to free.
 * @param ttoken_p A pointer to the TToken which will own the storage, or NULL
 *                 if a TTOKEN was not supplied on the IARV64 obtain call.
 * @param rc_p A pointer to an int which is populated with the return code
 *             from IARV64.
 * @param rsn_p A pointer to an int which is populated with the reason code
 *              from IARV64.
 */
void release_iarv64(void* storage_p, TToken* ttoken_p, int* rc_p, int* rsn_p);

/**
 * Converts guard pages from a memory object created by obtain_iarv64 into active
 * pages.  Pages are converted from the begining of the object.  For example, if you
 * created a memory object of 8 segments with 7 segments of guard page, the first
 * segment would be useable and the last 7 would be guard.  Calling this method
 * to remove 1 guard segment would result in the first 2 segments useable and
 * the last 6 guard.
 *
 * @param storageStart_p A pointer to the storage to modify.  This pointer was
 *                       returned on the call to obtain_iarv64.
 * @param segmentsToRemove The number of segments of guard pages to convert to
 *                         useable storage.
 * @param rc_p A pointer to an int which is populated with the return code
 *             from IARV64.
 * @param rsn_p A pointer to an int which is populated with the reason code
 *              from IARV64.
 */
void
convertGuardToActive_iarv64(void* storageStart_p, int segmentsToRemove, int* rc_p, int* rsn_p);

#endif
