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
#include "include/mvs_abend.h"
#include "include/mvs_cell_pool_services.h"

#include <stdlib.h>
#include <stdio.h>
#include <string.h>

// -----------------------------------------------------------
// Note - these structures are duplicated in utility modules:
//  angel_client_bind_report.mc
// -----------------------------------------------------------
struct cellpool_extent {
  char eyecatcher[8];
  void* extent_p;
  void* cells_p;
  long long num_cells;
  struct cellpool_extent* next_p;
  struct cellpool* pool_p;
};

struct cellpool {
  char eyecatcher[8];
  void* anchor_p;
  struct cellpool_extent* extent_p;
  long long cell_size;
  getGrowCellPoolStorage_t* auto_grow_func;
  struct {
      int auto_grow : 1;
      int _availableflags : 31;
  } flags;
  char _available[12];
  void * user_data;
};

static int wrap_CSRC4BLD(void* addr, char* name, long long cellsize);
static int wrap_CSRC4EXP(void* anchor_addr, void* extent_addr,
                  long long extent_size, void* area_addr,
                  long long area_size, int* extent_num);
static int wrap_CSRC4GT1(void* anchor_addr, void** cell_addr);
static int wrap_CSRC4GT2(void* anchor_addr, void** cell_addr, void** extent_addr);
static int wrap_CSRC4FR1(void* anchor_addr, void* cell_addr);
static int wrap_CSRC4FR2(void* anchor_addr, void* cell_addr, void* extent_addr);
static int wrap_CSRC4QCL(void* anchor_addr, void* cell_addr, long long* allocated_p);
static int wrap_CSRC4QPL(void* anchor_addr, CellPoolStatus_t* status_p);

/**
 * Divide a single storage area into extent and cell portions.  Figure out
 * how many cells can be allocated into the cell portion.
 *
 * @param id The cell pool in which the extent will be allocated.
 * @param storage_p A pointer to the storage to be used.
 * @param storageLen The length of the storage, in bytes.
 * @param numCells_p A pointer to a double word which will be filled in with
 *                   the number of cells which will be contained in the extent.
 * @param extentStorageAddr_p A pointer to a double word which will be filled in with
 *                            the address within storage_p where the extent will start.
 * @param extentStorageLen_p A pointer to a double word which will be filled in with
 *                           the length of the extent to be created.
 * @param cellStorageAddr_p A pointer to a double word which will be filled in with
 *                          the address within storage_p where the cells will start.
 * @param cellStorageLen_p A pointer to a double word which will be filled in with
 *                         the length of the cell storage.
 */
void computeExtentDetailsFromSingleAddress(long long id, void* storage_p, long long storageLen, long long* numCells_p, void** extentStorageAddr_p, long long* extentStorageLen_p, void** cellStorageAddr_p, long long* cellStorageLen_p) {
    long long numCells = 0L;
    void* extentAddr_p = NULL;
    long long extentLen = 0L;
    void* cellAddr_p = NULL;
    long long cellLen = 0L;
    struct cellpool* pool_p = (struct cellpool*)id;

    // Our cell pool extent struct is a fixed size.  Subtract that to start.
    long long storageLeft = storageLen - sizeof(struct cellpool_extent);

    // Now figure out how many cells we can fit.
    if (storageLeft > 0) {
        int extentBaseLen;
        __asm(" LGHI %0,CSRC4_EXTENT_BASE" : "=r"(extentBaseLen));

        long long blockSize = ((pool_p->cell_size) * 8) + 1;
        long long blocksToAllocate = (storageLeft - extentBaseLen) / blockSize;

        if (blocksToAllocate > 0) {
            numCells = blocksToAllocate * 8;
            cellLen = numCells * pool_p->cell_size;
            cellAddr_p = storage_p;
            extentLen = sizeof(struct cellpool_extent) + extentBaseLen + blocksToAllocate;
            extentAddr_p = ((char*)storage_p) + cellLen;
        }
    }

    *numCells_p = numCells;
    *extentStorageAddr_p = extentAddr_p;
    *extentStorageLen_p = extentLen;
    *cellStorageAddr_p = cellAddr_p;
    *cellStorageLen_p = cellLen;
}

long long computeCellPoolExtentStorageRequirement(long long numberOfCells) {
    // ----------------------------------------------------------------------
    // The cell pool functions allocate cells in multiples of 8, so make sure
    // the number of cells requested is a multiple of 8.  If not, round up.
    // ----------------------------------------------------------------------
    long long num_cells = numberOfCells;
    if ((num_cells % 8) != 0) {
        num_cells = ((num_cells + 8) / 8) * 8;
    }

    // ----------------------------------------------------------------------
    // Compute the size of the structures required.
    // ----------------------------------------------------------------------
    int extent_base_len;
    __asm(" LGHI %0,CSRC4_EXTENT_BASE" : "=r"(extent_base_len));

    long long extent_storage = (num_cells != 0) ? (sizeof(struct cellpool_extent) + // Our extent header
                                                   ((num_cells / 8) + extent_base_len)) // CSR extent
                                                : 0;

    return extent_storage;
}

long long computeCellPoolStorageRequirement(long long numberOfCells, long long cellSize) {
    // ----------------------------------------------------------------------
    // The cell pool functions allocate cells in multiples of 8, so make sure
    // the number of cells requested is a multiple of 8.  If not, round up.
    // ----------------------------------------------------------------------
    long long num_cells = numberOfCells;
    if ((num_cells % 8) != 0) {
        num_cells = ((num_cells + 8) / 8) * 8;
    }

    // ----------------------------------------------------------------------
    // Compute the size of the structures required.
    // ----------------------------------------------------------------------
    int anchor_len;
    __asm(" LGHI %0,CSRC4_ANCHOR_LENGTH" : "=r"(anchor_len));

    long long extent_storage = (num_cells != 0) ? (computeCellPoolExtentStorageRequirement(num_cells) + // Extent
                                                   (num_cells * cellSize)) // Cell storage
                                                : 0;

    long long req_storage = sizeof(struct cellpool) + // Our cellpool header
                            anchor_len +              // CSR anchor
                            extent_storage;           // Extent size

    return req_storage;
}

long long
buildCellPool(void* storage_p,
              long long storage_len,
              long long cell_size,
              char* name_p,
              buildCellPoolFlags flags) {
  long long pool_id = 0L;
  long long storage_left = storage_len;

  char* next_storage_p = (char*)storage_p;
  struct cellpool* pool_p = NULL;
  int anchor_len = 0;
  int csrc4_rc = 0;

  /*-----------------------------------------------------------------*/
  /* Ensure passed storage is starting on a quadword boundary.       */
  /* The cellpool services are exposed to alignment related          */
  /* specification abends.                                           */
  /*-----------------------------------------------------------------*/
  if ((unsigned long long )storage_p !=
      (((unsigned long long )storage_p >> 4 ) << 4)) {  // Shift out the last nibble
      abend_with_data(ABEND_TYPE_SERVER,
                      KRSN_SERVER_MVSCELLSERVICES_BAD_STORAGE_PTR,
                      storage_p,
                      *((void**)name_p));
  }


  /*-----------------------------------------------------------------*/
  /* Partition the storage into the pool header and anchor.          */
  /*-----------------------------------------------------------------*/
  storage_left = storage_len - sizeof(struct cellpool);
  pool_p = (struct cellpool*)storage_p;
  next_storage_p = next_storage_p + sizeof(struct cellpool);

  if (storage_left > 0) {
    __asm(" LGHI %0,CSRC4_ANCHOR_LENGTH" : "=r"(anchor_len));

    memset(pool_p, 0, sizeof(struct cellpool));
    memcpy(pool_p->eyecatcher, "BBGZCELP", 8);
    pool_p->cell_size = cell_size;
    pool_p->flags.auto_grow = flags.autoGrowCellPool;

    storage_left = storage_left - anchor_len;
    pool_p->anchor_p = next_storage_p;
    next_storage_p = next_storage_p + anchor_len;

    if (storage_left >= 0) {
      csrc4_rc = wrap_CSRC4BLD(pool_p->anchor_p,
                               name_p,
                               cell_size);

      if (csrc4_rc == 0) {
        pool_id = (long long)pool_p;
        if (flags.skipInitialCellAllocation == 0) {
            void* extentAddr_p;
            void* cellAddr_p;
            long long extentLen, cellLen, numCells;
            computeExtentDetailsFromSingleAddress(pool_id, (void*)next_storage_p, storage_left, &numCells, &extentAddr_p, &extentLen, &cellAddr_p, &cellLen);
            growCellPool(pool_id, numCells, extentAddr_p, extentLen, cellAddr_p, cellLen);
        }
      }
    }
  }

  return pool_id;
}

void 
growCellPool(long long id, long long numCells, void* extentStorage_p, long long extentStorageLen, void* cellStorage_p, long long cellStorageLen) {
    struct cellpool* pool_p = (struct cellpool*)id;

    // -----------------------------------------------------------------
    // Make an extent header and extent.
    // -----------------------------------------------------------------
    int extent_base_len = 0;
    __asm(" LGHI %0,CSRC4_EXTENT_BASE" : "=r"(extent_base_len));
    struct cellpool_extent* extent_p = (struct cellpool_extent*) extentStorage_p;
    long long csrExtentLen = extentStorageLen - sizeof(struct cellpool_extent);
    if (csrExtentLen >= (extent_base_len + (numCells / 8))) {
        memset(extent_p, 0, sizeof(struct cellpool_extent));
        memcpy(extent_p->eyecatcher, "BBGZCELE", 8);
        extent_p->pool_p = pool_p;
        extent_p->extent_p = ((char*)extentStorage_p) + sizeof(struct cellpool_extent);
        extent_p->num_cells = numCells;

        if (cellStorageLen >= (numCells * (pool_p->cell_size))) {
            int extent_num = 0, csrc4_rc = 0;
            extent_p->cells_p = cellStorage_p;
            csrc4_rc = wrap_CSRC4EXP(pool_p->anchor_p,
                                     extent_p->extent_p,
                                     csrExtentLen,
                                     extent_p->cells_p,
                                     cellStorageLen,
                                     &extent_num);

            if (csrc4_rc == 0) {
                // TODO: Serialize
                extent_p->next_p = pool_p->extent_p;
                pool_p->extent_p = extent_p;
            }
        }
    }
}

long long 
getCellPoolTotalCells(long long id) {
  struct cellpool* pool_p = (struct cellpool*)id;
  struct cellpool_extent* extent_p = pool_p->extent_p;

  long long num_cells = 0;
  while (extent_p != NULL) {
    num_cells += extent_p->num_cells;
    extent_p = (struct cellpool_extent*)(extent_p->next_p);
  }

  return num_cells;
}

long long 
getCellPoolCellSize(long long id) {
  struct cellpool* pool_p = (struct cellpool*)id;
  return pool_p->cell_size;
}

void* 
getCellPoolCell(long long id) {
  struct cellpool* pool_p = (struct cellpool*)id;
  void* cell_p = NULL;

  int csrc4_rc = wrap_CSRC4GT1(pool_p->anchor_p, &cell_p);

  if (csrc4_rc != 0) {
    /*-----------------------------------------------------------------------*/
    /* If there are no cells left (rc = 8) and we can auto-grow, do it.      */
    /*-----------------------------------------------------------------------*/
    if ((csrc4_rc == 8) && (pool_p->flags.auto_grow == 1) && (pool_p->auto_grow_func != NULL))   {
      long long storage_len = 0L;
      void* new_storage_p = pool_p->auto_grow_func(&storage_len, id);
      if ((new_storage_p != NULL) && (storage_len > 0)) {
        void* extentAddr_p;
        void* cellAddr_p;
        long long extentLen, cellLen, numCells;
        computeExtentDetailsFromSingleAddress(id, new_storage_p, storage_len, &numCells, &extentAddr_p, &extentLen, &cellAddr_p, &cellLen);
        growCellPool(id, numCells, extentAddr_p, extentLen, cellAddr_p, cellLen);

        csrc4_rc = wrap_CSRC4GT1(pool_p->anchor_p, &cell_p);
      }
    }

    if (csrc4_rc != 0) {
      cell_p = NULL;
    }
  }

  return cell_p;
}

// TODO: Combine with the non-token version
void* 
getCellPoolCellWithToken(long long id, long long* token_p) {
    struct cellpool* pool_p = (struct cellpool*)id;
    void* cell_p = NULL;
    long long* extentAddr_p = NULL;

    int csrc4_rc = wrap_CSRC4GT2(pool_p->anchor_p, &cell_p, (void**)(&extentAddr_p));
    *token_p = (long long)extentAddr_p;

    if (csrc4_rc != 0) {
      /*-----------------------------------------------------------------------*/
      /* If there are no cells left (rc = 8) and we can auto-grow, do it.      */
      /*-----------------------------------------------------------------------*/
      if ((csrc4_rc == 8) && (pool_p->flags.auto_grow == 1) && (pool_p->auto_grow_func != NULL)) {
        long long storage_len = 0L;
        void* new_storage_p = pool_p->auto_grow_func(&storage_len, id);
        if ((new_storage_p != NULL) && (storage_len > 0)) {
          void* extentAddr_p;
          void* cellAddr_p;
          long long extentLen, cellLen, numCells;
          computeExtentDetailsFromSingleAddress(id, new_storage_p, storage_len, &numCells, &extentAddr_p, &extentLen, &cellAddr_p, &cellLen);
          growCellPool(id, numCells, extentAddr_p, extentLen, cellAddr_p, cellLen);

          csrc4_rc = wrap_CSRC4GT2(pool_p->anchor_p, &cell_p, &extentAddr_p);
          *token_p = (long long)extentAddr_p;
        }
      }

      if (csrc4_rc != 0) {
        cell_p = NULL;
      }
    }

    return cell_p;
}

void 
freeCellPoolCell(long long id, void* cell_addr) {
  struct cellpool* pool_p = (struct cellpool*)id;

  int csrc4_rc = wrap_CSRC4FR1(pool_p->anchor_p,
                               cell_addr);
}

int
freeCellPoolCellWithToken(long long id, void* cell_addr, long long extent_token) {
    struct cellpool* pool_p = (struct cellpool*)id;

    return wrap_CSRC4FR2(pool_p->anchor_p, cell_addr, (void*)extent_token);
}

int 
verifyCellInPool(long long id, void* cell_addr, long long* allocated_p) {
  struct cellpool* pool_p = (struct cellpool*)id;

  int isInCellPool = wrap_CSRC4QCL(pool_p->anchor_p, cell_addr, allocated_p);

  return isInCellPool;
}

void 
setCellPoolAutoGrowFunction(long long id, getGrowCellPoolStorage_t* impl_p) {
  struct cellpool* pool_p = (struct cellpool*)id;

  if (pool_p->flags.auto_grow == 1) {
    pool_p->auto_grow_func = impl_p;
  }
}

void 
setCellPoolUserData(long long id, void * user_data) {
  struct cellpool* pool_p = (struct cellpool*)id;
  pool_p->user_data = user_data;
}

void * 
getCellPoolUserData(long long id) {
  struct cellpool* pool_p = (struct cellpool*)id;
  return pool_p->user_data;
}

long long 
getNumberOfExtentsInCellPool(long long id) {
  struct cellpool* pool_p = (struct cellpool*)id;
  long long num_extents = 0L;

  struct cellpool_extent* extent_p = pool_p->extent_p;
  while (extent_p != NULL) {
    num_extents = num_extents + 1;
    extent_p = extent_p->next_p;
  }

  return num_extents;
}

// Destroy the cell pool.
void destroyCellPool(long long id, freeCellPoolStorage_t* freeFcn_p) {
    if ((id != 0L) && (freeFcn_p != NULL)) {
        struct cellpool* pool_p = (struct cellpool*)id;
        struct cellpool_extent* curExtent_p = pool_p->extent_p;
        while (curExtent_p != NULL) {
            struct cellpool_extent* prevExtent_p = curExtent_p;
            void* prevCellStorage_p = curExtent_p->cells_p;
            curExtent_p = prevExtent_p->next_p;
            freeFcn_p(CELL_POOL_CELL_STORAGE_TYPE, prevCellStorage_p, id);
            freeFcn_p(CELL_POOL_EXTENT_STORAGE_TYPE, prevExtent_p, id);
        }

        freeFcn_p(CELL_POOL_ANCHOR_STORAGE_TYPE, pool_p, id);
    }
}

// Query the cell pool.
int getCellPoolStatistics(long long id, CellPoolStatus_t* status_p) {
    struct cellpool* pool_p = (struct cellpool*)id;

    return wrap_CSRC4QPL(pool_p->anchor_p, status_p);
}

/*-------------------------------------------------------------------*/
/* Cell pool services                                                */
/*-------------------------------------------------------------------*/
extern void CSRC4BLD(int* alet, void* anchor, char* name, long long* cell_size, int* rc);

int 
wrap_CSRC4BLD(void* addr, char* name, long long cellsize) {
  int rc = -1;
  int alet = 0;

  CSRC4BLD(&alet, &addr, name, &cellsize, &rc);

  return rc;
}

extern void 
CSRC4EXP(int* alet, void* anchor, void* extent_addr,
                     long long* extent_size, void* area_addr,
                     long long* area_size, int* extent_num, int* rc);

int 
wrap_CSRC4EXP(void* anchor_addr, void* extent_addr,
                  long long extent_size, void* area_addr,
                  long long area_size, int* extent_num) {
  int rc = -1;
  int alet = 0;

  CSRC4EXP(&alet, &anchor_addr, &extent_addr, &extent_size, &area_addr, &area_size, extent_num, &rc);

  return rc;
}

extern void 
CSRC4GT1(int* alet, void* anchor, void** cell,
                     int* rc, void* save_area);

int 
wrap_CSRC4GT1(void* anchor_addr, void** cell_addr) {
  int rc = -1;
  int alet = 0;
  char save_area[216];

  CSRC4GT1(&alet, &anchor_addr, cell_addr, &rc, (void*)(&(save_area[0])));

  return rc;
}

extern void 
CSRC4GT2(int* alet, void* anchor, void** cell,
         int* rc, void* save_area, void** extent);

int 
wrap_CSRC4GT2(void* anchor_addr, void** cell_addr, void** extent_addr) {
    int rc = -1, alet = 0;
    char save_area[216];

    CSRC4GT2(&alet, &anchor_addr, cell_addr, &rc, (void*)save_area, extent_addr);

    return rc;
}

extern void 
CSRC4FR1(int* alet, void* anchor, void* cell,
         int* rc, void* save_area);

int 
wrap_CSRC4FR1(void* anchor_addr, void* cell_addr) {
  int rc = -1;
  int alet = 0;
  char save_area[216];

  CSRC4FR1(&alet, &anchor_addr, &cell_addr, &rc, (void*)(&(save_area[0])));

  return rc;
}

extern void 
CSRC4FR2(int* alet, void* anchor, void* cell, int* rc, void* save_area, void* extent_addr);

int 
wrap_CSRC4FR2(void* anchor_addr, void* cell_addr, void* extent_addr) {
    int rc = -1;
    int alet = 0;
    char save_area[216];

    CSRC4FR2(&alet, &anchor_addr, &cell_addr, &rc, (void*)save_area, &extent_addr);

    return rc;
}

extern void 
CSRC4QCL(int* alet, void* anchor, void* cell,
         long long* avail, int* extent_num, int* rc);

int 
wrap_CSRC4QCL(void* anchor_addr, void* cell_addr, long long* allocated_p) {
  int rc = -1;
  int alet = 0;

  long long avail = -1;
  int extent_num = -1;

  CSRC4QCL(&alet, &anchor_addr, &cell_addr, &avail, &extent_num, &rc);

    if (rc == 0) {
        rc = 1;     // Cell is in cell pool
    } else if (rc == 84)  {
        rc = 0;     // Cell not in cell pool
    } else {
        rc = -1;    // Some other problem
  }

  // If the caller wants to know if the cell is allocated, tell them.
  if (allocated_p != NULL) {
      *allocated_p = avail;
  }

  return rc;
}

#pragma linkage(CSRC4QPL,OS_NOSTACK)
void CSRC4QPL(int* alet, void* anchor, char* name, long long* cell_size, long long* total_cells, long long* available_cells, int* num_extents, int* rc);

int wrap_CSRC4QPL(void* anchor_addr, CellPoolStatus_t* status_p) {
    int rc = -1;
    int alet = 0;

    CSRC4QPL(&alet, &anchor_addr, status_p->poolName, &(status_p->cellSize), &(status_p->totalCells), &(status_p->availableCells), &(status_p->numberOfExtents), &rc);

    return rc;
}

#pragma insert_asm(" CSRC4ASM")
