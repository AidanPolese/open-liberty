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

/** @file
 * Defines the WOLA shared memory manager, which manages the storage of the
 * WOLA shared memory anchor.
 */

#ifndef _BBOZ_SERVER_WOLA_SHARED_MEMORY_MANAGER_H
#define _BBOZ_SERVER_WOLA_SHARED_MEMORY_MANAGER_H

/** WOLA shared memory manager. */
typedef struct wolaSharedMemoryManager {
    /** Eye catcher */
    unsigned char eye[8];                                      /* 0x000 */

    /** Version */
    unsigned short version;                                    /* 0x008 */

    /** Size of this control block. */
    unsigned short size;                                       /* 0x00A */

    /** Number of memory pools being managed. */
    unsigned char numberOfPools;                               /* 0x00C */

    /** Flags */
    unsigned char flags[2];                                    /* 0x00D */

    /** Pad */
    unsigned char pad;                                         /* 0x00F */

    /** Next available memory address. */
    void* nextAvailableMemory_p;                               /* 0x010 */

    /** Last available memory address. */
    void* lastAvailableMemory_p;                               /* 0x018 */

    /* Following is an array of memory pools.  The number of pools is   */
    /* based on the numberOfPools variable.  This must start on a QUAD  */
    /* word boundary.                                          /* 0x020 */
} WolaSharedMemoryManager_t;                                   /* 0x??? */


/** A WOLA shared memory cell pool. */
typedef struct wolaSharedMemoryManagerPool {
    /** Size of cells managed by this pool. */
    unsigned long long cellSize;                               /* 0x000 */

    /** Number of cells that have been created. */
    unsigned int numberCellsCreated;                           /* 0x008 */

    /** Pad */
    unsigned char pad[4];                                      /* 0x00C */

    /** CDSG - Counter for pointer to next available cell. */
    unsigned long long count;                                  /* 0x010 */

    /** CDSG - Pointer to next available cell. */
    void* nextCell_p;                                          /* 0x018 */
} WolaSharedMemoryManagerPool_t;


/** A WOLA shared memory cell. */
typedef struct wolaSharedMemoryCell {
    /** Eye catcher */
    unsigned char eye[8];                                      /* 0x000 */

    /** Version */
    unsigned short version;                                    /* 0x008 */

    /** Allocated flag */
    unsigned int allocated:1,                                  /* 0x00A */

    /** Remainder of flag byte 1. */
                 flagByte1:7;                                  /* 0x00A */

    /** Flag byte 2 */
    unsigned char flagByte2;                                   /* 0x00B */

    /** Pad */
    unsigned char pad[4];                                      /* 0x00C */

    /** Total size of this cell, including this header. */
    unsigned long long size;                                   /* 0x010 */

    /** STCK from obtain. */
    unsigned long long obtainTime;                             /* 0x018 */

    /** STCK from release. */
    unsigned long long releaseTime;                            /* 0x020 */

    /** Pointer to the pool which allocated this cell. */
    WolaSharedMemoryManagerPool_t* pool_p;                     /* 0x028 */

    /** Pointer to the next free cell. */
    struct wolaSharedMemoryCell* nextFreeCell_p;               /* 0x030 */

    /** Available for use. */
    unsigned char _rsvd1[24];                                  /* 0x038 */

    /* Data follows immediately.  Data size included in 'size'. */
} WolaSharedMemoryCell_t;

#endif
