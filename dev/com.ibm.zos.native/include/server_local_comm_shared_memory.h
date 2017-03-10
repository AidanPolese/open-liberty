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
#ifndef _BBOZ_SERVER_LOCAL_COMM_SHARED_MEMORY_H
#define _BBOZ_SERVER_LOCAL_COMM_SHARED_MEMORY_H

#include "common_defines.h"

typedef struct localCommSharedMemoryInfo {
    /** The number of segments allocated when this control block was created. */
    unsigned int numSegmentsAllocated;                               /* 0x000*/

    /** The number of segments reserved as guard pages. */
    unsigned int numSegmentsGuard;                                   /* 0x004*/

    /** The base address of the shared memory area. */
    void* baseAddress_p;                                             /* 0x008*/

    /** The next free byte of space available in this control block. */
    void* nextFreeByte_p;                                            /* 0x010*/
} LocalCommSharedMemoryInfo_t;                                       /* 0x018*/

/**
 * Get a block of shared memory from this control block.  The storage will be
 * aligned to a quad word boundary.
 *
 * @param info_p The shared memory info object from the shared memory area where
 *               the storage is to be obtained.
 * @param bytes The number of bytes requested.
 *
 * @return A pointer to the memory requested, or NULL if it could not be allocated.
 */
void* getLocalCommSharedStorage(LocalCommSharedMemoryInfo_t* info_p, unsigned long long bytes);

#endif
