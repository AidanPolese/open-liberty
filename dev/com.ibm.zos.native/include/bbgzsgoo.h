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
#ifndef _BBOZ_BBGZSGOO_H
#define _BBOZ_BBGZSGOO_H
/**@file
 * System Common Area GOO
 */
#include "bbgzarmv.h"
#include "angel_fixed_shim_module.h"
#include "mvs_user_token_manager.h"

#pragma pack(1)

/**
 * A small temporary dynamic area in shared above the bar storage, used by
 * clients who are already connected to the SGOO to obtain a larger dynamic
 * area.
 */
typedef struct angelClientPreDynArea {
    char dynArea[192];
} AngelClientPreDynArea_t;

#define ANGEL_PC_PARM_AREA_EYE "BBGZPLPA"
#define ANGEL_PC_PARM_AREA_VER1 1

/**
 * A latent parm area passed to the PC routine which points to the SGOO.
 * There is free space to point to other things as necessary.
 * Some PC entry linkage (CPCPROL) had dependencies on some of the offsets
 * in here, which are marked with DEP.
 */
typedef struct angelPcParmArea {
    /**
     * Eye catcher for validation.
     */
    unsigned char          eyecatcher[8];            /* 0x000*/

    /**
     * Version number for validation.
     */
    unsigned short         version;                  /* 0x008*/

    /**
     * Control block length.
     */
    unsigned short         length;                   /* 0x00A*/

    /**
     * Pointer to the angel anchor control block, which holds the
     * angel name and has a pointer to the SGOO.
     */
    struct angelAnchor* __ptr32 angelAnchor_p;       /* 0x00C*/

    /**
     * Pointer to the SGOO control block.
     */
    struct bbgzsgoo*       sgoo_p;               /* DEP 0x010*/

    /**
     * Available for use.
     */
    unsigned char          _available2[232];         /* 0x018*/
} AngelPCParmArea_t;                                 /* 0x100*/

/**
 * Flags used to indicate angel status.  Update with compare-and-swap.
 */
typedef struct angelStatusFlags {
    /**
     * The angel is active.  If this flag is not set, the
     * PC routines should not be used.
     */
    int                  angel_active  :  1;

    /**
     * Available flags for use.
     */
    int                  _reservedBits : 31;
} AngelStatusFlags_t;

/**
 * Information used to connect to a named angel.
 */
typedef struct angelAnchor {
    unsigned char eye[8];                           /* 0x000 */
    short version;                                  /* 0x008 */
    short length;                                   /* 0x00A */
    AngelStatusFlags_t flags;                       /* 0x00C */
    long long sgoo_p;                               /* 0x010 */
    struct angelAnchorSet* __ptr32 anchorSet_p;     /* 0x018 */
    int pclx;                                       /* 0x01C */
    unsigned char name[55]; /* Null-terminated */   /* 0x020 */
    unsigned char avail[41];                        /* 0x057 */
} AngelAnchor_t;                                    /* 0x080 */

#define ANGEL_ANCHOR_COUNT_PER_SET 31

/**
 * A logical extension to the CGOO which contains information about a set of
 * named angel processes.
 */
typedef struct angelAnchorSet {
    unsigned char eye[8];                           /* 0x000 */
    short version;                                  /* 0x008 */
    short length;                                   /* 0x00A */
    struct angelAnchorSet* __ptr32 next_p;          /* 0x00C */
    unsigned short setNumber;                       /* 0x010 */
    unsigned char nextAvailableSlot;                /* 0x012 */
    unsigned char avail1[13];                       /* 0x013 */
    struct angelAnchor namedAngelInfo[ANGEL_ANCHOR_COUNT_PER_SET]; /* 0x020 */
    unsigned char avail2[96];                       /* 0xFA0 */
} AngelAnchorSet_t;                                 /* 0x1000 */

/**
 * The CGOO control block is a system-wide control block hung off of the
 * BGVT.  It is allocated in common storage below the bar.  It is used
 * by clients who want to connect to the Angel.  It contains a pointer
 * to the SGOO, which is allocated in shared above the bar storage.
 */
typedef struct bbgzcgoo
{
  /**
   * Eye catcher for storage validation "BBGZCGOO".
   */
  unsigned char            bbgzcgoo_eyecatcher[8];   /* 0x000*/

  /**
   * Version number for control block validation.
   */
  short                    bbgzcgoo_version;         /* 0x008*/

  /**
   * Length of the control block.
   */
  short                    bbgzcgoo_length;          /* 0x00A*/

  /**
   * The LX reserved for the angel PC routines.
   */
  int                      bbgzcgoo_rsvd_lx;         /* 0x00C*/

  /**
   * A pointer to the SGOO control block.
   */
  long long                bbgzcgoo_sgoo_p;          /* 0x010*/

  /**
   * Flags.  Update with compare-and-swap.
   */
  AngelStatusFlags_t       bbgzcgoo_flags;           /* 0x018*/

  /**
   * Information for the first set of named angels.
   */
  AngelAnchorSet_t* __ptr32 firstAaSet_p;            /* 0x01C*/

  /**
   * Available for use.
   */
  unsigned char            _reserved1[32];           /* 0x020*/
} bbgzcgoo;                                          /* 0x040*/

/**
 * The SGOO is a system-wide control block hung off the CGOO.  It is
 * allocated in shared above the bar storage.  When registering with
 * the Angel, the storage will be shared with the registering address
 * space.
 */
typedef struct bbgzsgoo
{
  /**
   * Eye catcher for storage validation 'BBGZSGOO'.
   */
  unsigned char      bbgzsgoo_eyecatcher[8];                        /* 0x000*/

  /**
   * Version number of the control block for validation.
   */
  short              bbgzsgoo_version;                              /* 0x008*/

  /**
   * The length of the control block in bytes.
   */
  short              bbgzsgoo_length;                               /* 0x00A*/

  /**
   * A running count of the number of angel process data control blocks
   * that have been created.  The count is incremented each time an
   * angel process data is created and the count is stored in the angel
   * process data control block.
   */
  unsigned int       bbgzsgoo_angel_process_data_count;             /* 0x00C*/

  /**
   * The length of the fixed shim module loaded by the angel.
   */
  long long          bbgzsgoo_fsm_len;                              /* 0x010*/

  /**
   * The starting address of the fixed shim module loaded by the angel, as
   * reported by BPX4LDX.
   */
  void*              bbgzsgoo_fsm_mod_p;                            /* 0x018*/

  /**
   * The entry point of the fixed shim module loaded by the angel, as reported
   * by BPX4LDX.  The entry point is the vector of functions which are
   * registered as PC routines for use by Liberty servers.
   */
  struct bbgzafsm*   bbgzsgoo_fsm;                                  /* 0x020*/

  /**
   * The token received by CSVDYLPA which is used later to tell contents
   * supervisor that the module will be deleted from memory.
   */
  unsigned char      bbgzsgoo_fsm_del_token[8];                     /* 0x028*/

  /**
   * The ID for the cell pool used to allocate angel process data control
   * blocks.  User Data: SGOO_P.
   */
  long long          bbgzsgoo_angel_process_data_cellpool_id;       /* 0x030*/

  /**
   * A pointer to the current ARMV control block.
   */
  long long          bbgzsgoo_armv;                                 /* 0x038*/

  /**
   * The origin (starting address) of the shared above the bar storage allocated
   * for Liberty.
   */
  void*              bbgzsgoo_shmem_origin;                         /* 0x040*/

  /**
   * The next available address of the shared above the bar storage allocation
   * starting at bbgzsgoo_shmem_origin.
   */
  void*              bbgzsgoo_shmem_next_avail;                     /* 0x048*/

  /**
   * The number of segments of shared memory whose origin is
   * bbgzsgoo_shmem_origin.  Each segment is 1 MB.  This length can be used
   * along with bbgzsgoo_shmem_origin and bbgzsgoo_shmem_next_avail to determine
   * how much shared above the bar storage is available to Liberty servers and
   * the angel.
   */
  unsigned int       bbgzsgoo_shmem_seg_count;                      /* 0x050*/

  /**
   * The size of the cells in the bbgzsgoo_angelClientDataPool.
   */
  unsigned short     bbgzsgoo_angelClientDataPoolCellSize;          /* 0x054*/

  /**
   * The size of the cells in the bbgzsgoo_clientBindDataPool.
   */
  unsigned short     bbgzsgoo_clientBindDataPoolCellSize;           /* 0x056*/

  /**
   * The cell pool ID that is used to allocate ARMV control blocks.
   */
  long long          bbgzsgoo_armv_cellpool_id;                     /* 0x058*/

  /**
   * The starting address used to create the ARMV cell pool.
   */
  void*              bbgzsgoo_armv_cellpool_stg;                    /* 0x060*/

  /**
   * The length of the storage used to create the ARMV cell pool.
   */
  long long          bbgzsgoo_armv_cellpool_len;                    /* 0x068*/

  /**
   * The STOKEN of the active angel process.
   */
  long long          bbgzsgoo_angel_stoken;                         /* 0x070*/

  /**
   * The cell pool used for angel client process data control blocks.
   * User Data: SGOO_P.
   */
  long long          bbgzsgoo_angelClientDataPool;                  /* 0x078*/

  /**
   * The cell pool used for client/server bind data.
   * User Data: SGOO_P.
   */
  long long          bbgzsgoo_clientBindDataPool;                   /* 0x080*/

  /**
   * The cell pool used for chaining together client bind data in the
   * angel process data and the angel client process data.
   * User Data: SGOO_P.
   */
  long long          bbgzsgoo_clientBindDataNodePool;               /* 0x088*/

  /**
   * The size of the cells in the bbgzsgoo_clientBindDataNodePool.
   */
  unsigned short     bbgzsgoo_clientBindDataNodePoolCellSize;       /* 0x090*/

  /**
   * The size of the cells in the bbgzsgoo_clientPreDynamicAreaPool.
   */
  unsigned short     bbgzsgoo_clientPreDynamicAreaPoolCellSize;     /* 0x092*/

  /**
   * An instance counter used to uniquely identify client bind data control
   * blocks generated from this SGOO.  This field is updated with compare and
   * swap to generate unique instance numbers for each bind data.
   */
  unsigned int       clientBindDataInstanceCounter;                 /* 0x094*/

  /**
   * The cell pool used by the client to get a small dynamic area which can
   * be used to get a larger dynamic area from some other place.  For
   * example, this might provide enough space to create the parameter list
   * to call name token lookup, or to verify the cell in a cell pool using
   * cell pool query.
   * User Data: SGOO_P.
   */
  long long          bbgzsgoo_clientPreDynamicAreaPool;             /* 0x098*/

  /**
   * Pointer to the latent parm area passed to the PC routines.
   */
  void* __ptr32      bbgzsgoo_pcLatentParmArea_p;                   /* 0x0A0*/

  /**
   * Pointer to the angel anchor for this SGOO, if a named angel.  If not
   * a named angel, this pointer will be NULL.
   */
  AngelAnchor_t* __ptr32 bbgzsgoo_angelAnchor_p;                    /* 0x0A4*/

  /**
   * Available for use.
   */
  unsigned char      _reserved1[344];                               /* 0x0A8*/
} bbgzsgoo;                                                         /* 0x200*/

#pragma pack(reset)

/**
 * The bias that a client uses when connecting to the SGOO.  Defined in angel_fixed_shim_module.mc.
 */
extern const UserTokenBias_t CLIENT_SGOO_BIAS;

#endif
