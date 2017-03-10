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
#include <metal.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#include "include/angel_sgoo_services.h"
#include "include/ieantc.h"
#include "include/mvs_cell_pool_services.h"
#include "include/mvs_iarv64.h"
#include "include/mvs_storage.h"
#include "include/mvs_user_token_manager.h"
#include "include/mvs_utils.h"
#include "include/mvs_wto.h"
#include "include/ras_tracing.h"

#define RAS_MODULE_CONST RAS_MODULE_ANGEL_SGOO_SERVICES
#define _TP_CREATE_SGOO_ENTRY                  1
#define _TP_CREATE_SGOO_ACCESS_SHMEM           2
#define _TP_CREATE_SGOO_ACCESS_SHMEM_RETURN    3
#define _TP_CREATE_SGOO_ALLOCATE_FAIL          4
#define _TP_CREATE_SGOO_ABANDON_CONTROL_BLOCKS 5
#define _TP_CREATE_SGOO_ALLOCATE_FAIL2         6
#define _TP_CREATE_SGOO_EXCEED_MAX_ANGELS      7
#define _TP_CREATE_SGOO_FAIL_ANGEL_ANCHOR_SET  8


#define MAX_SLOTS 31
#define MAX_SEGMENTS 132

/* Gets a reference to the default SGOO */
bbgzsgoo* getDefaultSGOO(void)
{
    bbgzsgoo* sgoo_p = NULL;
    bgvt* bgvt_p = findOrCreateBGVT();
    if (bgvt_p != NULL) {
        bbgzcgoo* cgoo_p = bgvt_p->bbodbgvt_bbgzcgoo;
        if (cgoo_p != NULL) {
            sgoo_p = (bbgzsgoo*)(cgoo_p->bbgzcgoo_sgoo_p);
        }
    }

    return sgoo_p;
}

static unsigned char isNamedAngel(char* angel_name) {
    int angel_name_len = strlen(angel_name);
    return ((angel_name != NULL) && (angel_name_len > 0));
}

/* Gets a reference to the SGOO for the named angel. */
bbgzsgoo* getSGOO(char* angel_name) {
    if (isNamedAngel(angel_name) == FALSE) {
        return getDefaultSGOO();
    }

    bgvt* bgvt_p = findOrCreateBGVT();
    if (bgvt_p == NULL) {
        return NULL;
    }

    bbgzcgoo* cgoo_p = bgvt_p->bbodbgvt_bbgzcgoo;
    if (cgoo_p == NULL) {
        return NULL;
    }

    AngelAnchorSet_t* angelAnchorSet_p = cgoo_p->firstAaSet_p;
    if (angelAnchorSet_p == NULL) {
        return NULL;
    }

    bbgzsgoo* sgoo_p = NULL;
    while(angelAnchorSet_p != NULL) {
      for (int x = 0; x < angelAnchorSet_p->nextAvailableSlot; x++) {
          if (strcmp(angel_name, angelAnchorSet_p->namedAngelInfo[x].name) == 0) {
              sgoo_p = (bbgzsgoo*)(angelAnchorSet_p->namedAngelInfo[x].sgoo_p);
              break;
          }
      }
      if(sgoo_p != NULL)
         break;
      angelAnchorSet_p = angelAnchorSet_p->next_p;
    }

    return sgoo_p;
}

/* Get the angel anchor instance number */
unsigned short getAngelAnchorInstanceNumber(AngelAnchor_t* angelAnchor_p) {
    AngelAnchorSet_t* anchorSet_p = angelAnchor_p->anchorSet_p;

    return (anchorSet_p->setNumber * ANGEL_ANCHOR_COUNT_PER_SET) + (((unsigned long long)angelAnchor_p) - ((unsigned long long)(anchorSet_p->namedAngelInfo))) / sizeof(AngelAnchor_t);
}

#define ANGEL_SGOO_SERVICES_OLD_CGOO_NAME "BBGZ_OLD_CGOO_PT"

/* Creates the SGOO for this angel, and the CGOO for the system */
bbgzsgoo* createSGOO(bgvt* bgvt_p, unsigned char new_control_blocks, char* angel_name)
{
    if (TraceActive(trc_level_basic)) {
        TraceRecord(trc_level_basic,
                TP(_TP_CREATE_SGOO_ENTRY),
                "angel_main createSGOO called",
                TRACE_DATA_PTR(bgvt_p, "BGVT"),
                TRACE_DATA_INT(new_control_blocks, "Replace existing control blocks"),
                TRACE_DATA_STRING(angel_name, "Angel name"),
                TRACE_DATA_END_PARMS);
    }

    //-----------------------------------------------------------------
    // Get a reference to the Server GOO.  Create one if not already
    // created.
    //-----------------------------------------------------------------
    long long access_sgoo_user_token = getAddressSpaceSupervisorStateUserToken();

    bbgzsgoo* sgoo_p = NULL;
    bbgzcgoo* cgoo_p = bgvt_p->bbodbgvt_bbgzcgoo;

    /*-----------------------------------------------------------------------*/
    /* If we were asked to start over and make a new CGOO/SGOO, trace the    */
    /* old addresses and make a name token for them so that we can find      */
    /* them in a dump.  We only save the most recently abandonded CGOO.      */
    // TODO: Need more checks around cold start.  can't be any angels
    //       up on the system.
    /*-----------------------------------------------------------------------*/
    if (new_control_blocks != 0) {
        if (TraceActive(trc_level_exception)) {
            TraceRecord(trc_level_exception,
                    TP(_TP_CREATE_SGOO_ABANDON_CONTROL_BLOCKS),
                    "angel_main createSGOO cold start, abandoning old control blocks",
                    TRACE_DATA_PTR(cgoo_p, "CGOO"),
                    TRACE_DATA_PTR((void*)(cgoo_p->bbgzcgoo_sgoo_p), "SGOO"),
                    TRACE_DATA_END_PARMS);
        }

        int iea_rc = 0;
        char token[16];
        memset(token, 0, sizeof(token));
        memcpy(token, &cgoo_p, sizeof(cgoo_p));
        iean4dl(IEANT_SYSTEM_LEVEL,
                ANGEL_SGOO_SERVICES_OLD_CGOO_NAME,
                &iea_rc);
        iean4cr(IEANT_SYSTEM_LEVEL,
                ANGEL_SGOO_SERVICES_OLD_CGOO_NAME,
                token,
                IEANT_PERSIST,
                &iea_rc);

        cgoo_p = NULL;
    }

    int so_rc = -1;

    if (cgoo_p == NULL) {
        /* Get some common storage */
        cgoo_p = storageObtain(sizeof(bbgzcgoo), 241, 2, &so_rc);

        if (cgoo_p != NULL) {
          memset(cgoo_p, 0, sizeof(*cgoo_p));
          memcpy(cgoo_p->bbgzcgoo_eyecatcher, "BBGZCGOO", 8);
          cgoo_p->bbgzcgoo_version = 1;
          cgoo_p->bbgzcgoo_length = sizeof(bbgzcgoo);

          bgvt_p->bbodbgvt_bbgzcgoo = cgoo_p;
        }
    }

    if (cgoo_p != NULL) {
      /*-----------------------------------------------------------------*/
      /* If we're using the default angel name, we'll use the SGOO hung  */
      /* off the CGOO.  But if we're using a named angel, we'll need to  */
      /* search the angel set for the correct one.                       */
      /*-----------------------------------------------------------------*/
      sgoo_p = getSGOO(angel_name);
      if (sgoo_p != NULL) {
        /*---------------------------------------------------------------*/
        /* Connect to the shared above-the-bar storage.                  */
        /*---------------------------------------------------------------*/
        void* env;
        __asm(" LGR %0,12" : "=r"(env));

        if (TraceActive(trc_level_detailed)) {
            TraceRecord(trc_level_detailed,
                    TP(_TP_CREATE_SGOO_ACCESS_SHMEM),
                    "Accessing SGOO storage",
                    TRACE_DATA_RAWDATA(sizeof(access_sgoo_user_token), &access_sgoo_user_token, "usertoken"),
                    TRACE_DATA_PTR(env, "env"),
                    TRACE_DATA_END_PARMS);
        }

        accessSharedAbove(sgoo_p, access_sgoo_user_token);

        if (TraceActive(trc_level_detailed)) {
            TraceRecord(trc_level_detailed,
                    TP(_TP_CREATE_SGOO_ACCESS_SHMEM_RETURN),
                    "Accessed SGOO storage",
                    TRACE_DATA_RAWDATA(sizeof(*sgoo_p), sgoo_p, "sgoo_p"),
                    TRACE_DATA_END_PARMS);
        }
      } else {
        /*---------------------------------------------------------------*/
        /* Need to create the Server GOO.  Need a place to put it.       */
        /* Get some shared above the bar storage.                        */
        /*---------------------------------------------------------------*/
        long long create_sgoo_user_token = getSystemSupervisorStateUserToken();
        unsigned int shmem_segment_count = 4; /* 4 Megabytes */
        void* shrmem_p = (void *) getSharedAbove((long long)shmem_segment_count,
                                                 0,  /* Not fetch protected */
                                                 create_sgoo_user_token);

        /*---------------------------------------------------------------*/
        /* If we got some storage, take the first part for the SGOO      */
        /* and use the rest for a cell pool.                             */
        /*---------------------------------------------------------------*/
        if (shrmem_p != NULL) {
            accessSharedAbove(shrmem_p, access_sgoo_user_token);

            sgoo_p = (bbgzsgoo*)shrmem_p;
            shrmem_p = (((char*)shrmem_p) + sizeof(bbgzsgoo));

            /*---------------------------------------------------------------*/
            /* We are defining this as an "auto-grow" cell pool, so that it  */
            /* will grow if someone requests a cell but there are no cells   */
            /* left.  Since the cell pool is used by multiple address        */
            /* spaces, the grow function must reside in shared storage.  We  */
            /* put the grow function in the dynamic replaceable module,      */
            /* which is not loaded yet, so we don't set the grow function    */
            /* until we load the dynamic replaceable module.                 */
            /*---------------------------------------------------------------*/
            buildCellPoolFlags cellPoolFlags;
            memset(&cellPoolFlags, 0, sizeof(cellPoolFlags));
            cellPoolFlags.autoGrowCellPool = 1;

            int initial_cell_pool_allocation_bytes = 32 * 1024; /* 32 K */
            long long cpool_id = buildCellPool(shrmem_p,
                                               initial_cell_pool_allocation_bytes,
                                               1024L, /* Cell size 1K */
                                               "SGOOPOOL",
                                               cellPoolFlags);
            shrmem_p = (((char*)shrmem_p) + initial_cell_pool_allocation_bytes);

            /*-------------------------------------------------------------*/
            /* If the cell pool was created successfully, set up the       */
            /* SGOO.                                                       */
            /*-------------------------------------------------------------*/
            if (cpool_id != 0L) {
                memset(sgoo_p, 0, sizeof(*sgoo_p));
                memcpy(sgoo_p->bbgzsgoo_eyecatcher, "BBGZSGOO", sizeof(sgoo_p->bbgzsgoo_eyecatcher));
                sgoo_p->bbgzsgoo_version = 1;
                sgoo_p->bbgzsgoo_angel_process_data_cellpool_id = cpool_id;
                sgoo_p->bbgzsgoo_shmem_seg_count = shmem_segment_count;
                sgoo_p->bbgzsgoo_shmem_origin = sgoo_p;
                sgoo_p->bbgzsgoo_shmem_next_avail = shrmem_p;

                /*-----------------------------------------------------------*/
                /* Place the SGOO into the CGOO for the default angel, or    */
                /* create a new angel anchor if it's a named angel.          */
                /*-----------------------------------------------------------*/
                if (isNamedAngel(angel_name) == FALSE) {
                    cgoo_p->bbgzcgoo_sgoo_p = (long long)sgoo_p;
                } else {
                    AngelAnchorSet_t* aas_p = cgoo_p->firstAaSet_p;
                    if (aas_p == NULL) {
                        aas_p = getNewAngelAnchorSet(NULL, cgoo_p, sgoo_p, angel_name);
                        if(aas_p == NULL) {
                            if (TraceActive(trc_level_exception)) {
                                TraceRecord(
                                    trc_level_exception,
                                    TP(_TP_CREATE_SGOO_FAIL_ANGEL_ANCHOR_SET),
                                    "Storage Obtain failed for new angel anchor set",
                                    TRACE_DATA_END_PARMS);
                            }
                            shrmem_p = ((void *)sgoo_p);
                            sgoo_p = NULL;
                        }

                    } else {
                        int slot = aas_p->nextAvailableSlot;
                        if( slot < MAX_SLOTS ) {
                            aas_p->nextAvailableSlot = slot + 1;
                            AngelAnchor_t* aa_p = &(aas_p->namedAngelInfo[slot]);
                            memcpy(aa_p->eye, "BBGZAA__", 8);
                            aa_p->version = 1;
                            aa_p->length = sizeof(*aa_p);
                            aa_p->sgoo_p = (long long) sgoo_p;
                            aa_p->anchorSet_p = aas_p;
                            strcpy(aa_p->name, angel_name);
                            sgoo_p->bbgzsgoo_angelAnchor_p = aa_p;
                        } else if( aas_p->setNumber < MAX_SEGMENTS ) {
                            aas_p = getNewAngelAnchorSet( aas_p, cgoo_p, sgoo_p, angel_name );
                            if( aas_p == NULL) {
                              if (TraceActive(trc_level_exception)) {
                                  TraceRecord(
                                      trc_level_exception,
                                      TP(_TP_CREATE_SGOO_FAIL_ANGEL_ANCHOR_SET),
                                      "Storage Obtain failed for new angel anchor set",
                                      TRACE_DATA_END_PARMS);
                              }
                              shrmem_p = ((void *)sgoo_p);
                              sgoo_p = NULL;
                            }
                        } else {
                            // Maxed out on angels
                            // can't create a new one
                            if (TraceActive(trc_level_exception)) {
                                TraceRecord(
                                    trc_level_exception,
                                    TP(_TP_CREATE_SGOO_EXCEED_MAX_ANGELS),
                                    "angel_main createSGOO exceeded max number of Angels",
                                    TRACE_DATA_INT(aas_p->nextAvailableSlot, "Next Avialable slot"),
                                    TRACE_DATA_INT(aas_p->setNumber, "Set Number"),
                                    TRACE_DATA_END_PARMS);
                            }
                            shrmem_p = ((void *)sgoo_p);
                            sgoo_p = NULL;
                        }
                    }
                }
            }
        }
      }

      // ---------------------------------------------------------------
      // We want to pass a pointer to the SGOO as a latent parm on the
      // PC routines that we're going to set up, in preparation for
      // multi-angel support.  However the latent parms are 31 bit
      // only, and the SGOO is shared above the bar (64 bit), so
      // allocate a piece of common storage to store the pointer into.
      // This storage lifecycle is the same as the SGOO and thus it is
      // never freed.
      // ---------------------------------------------------------------
      if ((sgoo_p != NULL) && (sgoo_p->bbgzsgoo_pcLatentParmArea_p == NULL)) {
        AngelPCParmArea_t* __ptr32 latentParmArea_p = storageObtain(sizeof(AngelPCParmArea_t), 241, 2, &so_rc);

        if (latentParmArea_p != NULL) {
            memset(latentParmArea_p, 0, sizeof(AngelPCParmArea_t));
            memcpy(latentParmArea_p->eyecatcher, ANGEL_PC_PARM_AREA_EYE, sizeof(latentParmArea_p->eyecatcher));
            latentParmArea_p->version = ANGEL_PC_PARM_AREA_VER1;
            latentParmArea_p->length = sizeof(AngelPCParmArea_t);
            latentParmArea_p->sgoo_p = sgoo_p;
            latentParmArea_p->angelAnchor_p = sgoo_p->bbgzsgoo_angelAnchor_p; // May not be set if default angel.
            sgoo_p->bbgzsgoo_pcLatentParmArea_p = latentParmArea_p;
        } else {
            if (TraceActive(trc_level_exception)) {
                TraceRecord(
                    trc_level_exception,
                    TP(_TP_CREATE_SGOO_ALLOCATE_FAIL2),
                    "angel_main createSGOO could not allocate storage for latent parm area",
                    TRACE_DATA_INT(so_rc, "Storage Obtain RC"),
                    TRACE_DATA_END_PARMS);
            }

            sgoo_p = NULL;
        }
      }
    } else {
        if (TraceActive(trc_level_exception)) {
            TraceRecord(
                trc_level_exception,
                TP(_TP_CREATE_SGOO_ALLOCATE_FAIL),
                "angel_main createSGOO could not allocate storage for CGOO",
                TRACE_DATA_INT(so_rc, "Storage Obtain RC"),
                TRACE_DATA_END_PARMS);
        }
    }

    return sgoo_p;
}

/* Allocates some shared above the bar storage */
void* allocateSGOOsharedStorage(bbgzsgoo* sgoo_p, int bytes)
{
    /*-----------------------------------------------------------------------*/
    /* Since we have no way to return storage (only allocate), we'll compare */
    /* and swap the next available byte count to allocate storage.           */
    /*-----------------------------------------------------------------------*/
    int csg_rc = -1;
    void* allocated_storage_p = NULL;
    void* old_next_storage_p = sgoo_p->bbgzsgoo_shmem_next_avail;
    void* first_unavailable_storage_p = ((char*)sgoo_p->bbgzsgoo_shmem_origin) +
                                        (sgoo_p->bbgzsgoo_shmem_seg_count * 1024 * 1024);

    while (csg_rc != 0) {
        void* new_next_storage_p = ((char*)old_next_storage_p) + bytes;
        if (((unsigned long long)new_next_storage_p) <= ((unsigned long long)first_unavailable_storage_p)) {
            csg_rc = __csg(&old_next_storage_p, &(sgoo_p->bbgzsgoo_shmem_next_avail), &new_next_storage_p);
            if (csg_rc == 0) {
                allocated_storage_p = old_next_storage_p;
            }
        } else {
            csg_rc = 0;
        }
    }

    return allocated_storage_p;
}

/* Allocate shared storage to grow a shared memory cell pool. */
void* getStorageToGrowSharedMemoryCellPool(long long* size_p, long long cell_pool_id)
{
  /*-------------------------------------------------------------------------*/
  /* We'd like some shared storage.  Unfortunately we can't get new shared   */
  /* storage because we'd have to connect all of the address spaces which    */
  /* are currently connected to the existing shared storage.  Instead we     */
  /* have to carve a chunk out of the existing shared storage.               */
  /*-------------------------------------------------------------------------*/
  void* new_storage_p = NULL;
  int new_storage_len = 1024 * 32; /* 32 KB */

  /*-------------------------------------------------------------------------*/
  /* Get a reference to our SGOO, it should be set in the cell pool user     */
  /*-------------------------------------------------------------------------*/
  bbgzsgoo* sgoo_p = (bbgzsgoo*) getCellPoolUserData(cell_pool_id);
  if (sgoo_p != NULL)
  {
    new_storage_p = allocateSGOOsharedStorage(sgoo_p, new_storage_len);
    if (new_storage_p != NULL)
    {
      *size_p = new_storage_len;
    }
  }

  return new_storage_p;
}
AngelAnchorSet_t* getNewAngelAnchorSet(AngelAnchorSet_t* prev_aas_p, bbgzcgoo* cgoo_p, bbgzsgoo* sgoo_p, char* angelName) {

    AngelAnchorSet_t* newaas_p = NULL;
    int so_rc = -1;

    newaas_p = storageObtain(sizeof(AngelAnchorSet_t), 241, 2, &so_rc);
    if( newaas_p != NULL ) {
        memset(newaas_p, 0, sizeof(*newaas_p));
        memcpy(newaas_p->eye, "BBGZAAS_", 8);
        newaas_p->version = 1;
        newaas_p->length = sizeof(*newaas_p);
        newaas_p->nextAvailableSlot = 1;
        memcpy(newaas_p->namedAngelInfo[0].eye, "BBGZAA__", 8);
        newaas_p->namedAngelInfo[0].version = 1;
        newaas_p->namedAngelInfo[0].length = sizeof(newaas_p->namedAngelInfo[0]);
        newaas_p->namedAngelInfo[0].sgoo_p = (long long) sgoo_p;
        newaas_p->namedAngelInfo[0].anchorSet_p = newaas_p;
        strcpy(newaas_p->namedAngelInfo[0].name, angelName);
        sgoo_p->bbgzsgoo_angelAnchor_p = &(newaas_p->namedAngelInfo[0]);
        if (prev_aas_p == NULL) {
           cgoo_p->firstAaSet_p = newaas_p;
        } else {
            prev_aas_p->next_p = newaas_p;
            newaas_p->next_p = NULL;
        }
    }

    return newaas_p;

}
#define DUMP_CELL_POOL_TO_OPERATOR(name, desc) {                   \
    int rc = getCellPoolStatistics(sgoo_p->name, &cellPoolStatus); \
    write_to_operator(desc, NULL);                                 \
    if (rc == 0) {                                                 \
        snprintf(msg, 120, "%.8s %lli %lli/%lli %i", cellPoolStatus.poolName, cellPoolStatus.cellSize, cellPoolStatus.availableCells, cellPoolStatus.totalCells, cellPoolStatus.numberOfExtents); \
    } else {                                                       \
        snprintf(msg, 120, " ** ERROR **   RC = %i", rc);          \
    }                                                              \
    write_to_operator(msg, NULL); }

/* Report on the status of the cell pools anchored in the SGOO. */
void writeSgooCellPoolStatusToOperator(char* angel_name) {
    bbgzsgoo* sgoo_p = getSGOO(angel_name);
    CellPoolStatus_t cellPoolStatus;
    memset(&cellPoolStatus, 0, sizeof(cellPoolStatus));

    char msg[120];
    write_to_operator("SGOO Cell Pool Statistics", NULL);
    write_to_operator("-------------------------", NULL);

    DUMP_CELL_POOL_TO_OPERATOR(bbgzsgoo_angel_process_data_cellpool_id, "Angel PGOO cell pool");
    DUMP_CELL_POOL_TO_OPERATOR(bbgzsgoo_angelClientDataPool, "Angel Client PGOO cell pool");
    DUMP_CELL_POOL_TO_OPERATOR(bbgzsgoo_clientBindDataPool, "Angel Client Bind Data Pool");
    DUMP_CELL_POOL_TO_OPERATOR(bbgzsgoo_clientBindDataNodePool, "Angel Client Bind Data Node Pool");
    DUMP_CELL_POOL_TO_OPERATOR(bbgzsgoo_clientPreDynamicAreaPool, "Angel Client Pre-dynamicarea Pool");
}

