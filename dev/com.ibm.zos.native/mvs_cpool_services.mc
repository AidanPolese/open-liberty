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
#include "include/mvs_cpool_services.h"

#include "include/mvs_utils.h"

#include <stdlib.h>
#include <string.h>

__asm(" CPOOL BUILD,MF=L" : "DS"(build_list_form));

//---------------------------------------------------------------------
//---------------------------------------------------------------------
mvs_cpool_id
mvs_cpool_build(int primaryCellCount,
                int secondaryCellCount,
                int cellSize,
                int subpool,
                int key,
                const char* header) {

    unsigned int poolId = 0;

    struct parm31 {
        int primaryCellCount;
        int secondaryCellCount;
        int cellSize;
        int subpool;
        int key;
        unsigned int poolId;
        char header[24];
        char build_dynamic[sizeof(build_list_form)];
    };

    struct parm31* parm_p = __malloc31(sizeof(struct parm31));
    if (parm_p == NULL) {
        return 0;
    }

    memcpy(parm_p->build_dynamic, &build_list_form, sizeof(build_list_form));
    parm_p->primaryCellCount = primaryCellCount;
    parm_p->secondaryCellCount = secondaryCellCount;
    parm_p->cellSize = cellSize;
    parm_p->subpool = subpool;
    parm_p->key = key;
    memcpy(parm_p->header, header, sizeof(parm_p->header));
    parm_p->poolId = 0;

    __asm(" SAM31\n"
          " SYSSTATE PUSH\n"
          " SYSSTATE AMODE64=NO,OSREL=ZOSV1R6\n"
          " CPOOL BUILD,PCELLCT=(%1),SCELLCT=(%2),CSIZE=(%3),SP=(%4),KEY=(%5),LOC=(ANY,ANY),HDR=%6,CPID=%0,LINKAGE=SYSTEM,MF=(E,(%7))\n"
          " SYSSTATE POP\n"
          " SAM64":
          "=m"(parm_p->poolId) :
          "r"(parm_p->primaryCellCount),
              "r"(parm_p->secondaryCellCount),
              "r"(parm_p->cellSize),
              "r"(parm_p->subpool),
              "r"(parm_p->key),
              "m"(parm_p->header),
              "r"(parm_p->build_dynamic) :
          "r0", "r1", "r14" ,"r15");

    poolId = parm_p->poolId;

    free(parm_p);

    return poolId;
}

//---------------------------------------------------------------------
//---------------------------------------------------------------------
void*
mvs_cpool_get(mvs_cpool_id poolId) {
    void* cell_p = NULL;

    struct parm31 {
        void* __ptr32 cell_p;
        mvs_cpool_id poolId;
    };

    struct parm31* parm_p = __malloc31(sizeof(struct parm31));
    if (parm_p == NULL) {
        return NULL;
    }

    parm_p->cell_p = NULL;
    parm_p->poolId = poolId;

    __asm(" SAM31\n"
          " SYSSTATE PUSH\n"
          " SYSSTATE AMODE64=NO,OSREL=ZOSV1R6\n"
          " CPOOL GET,UNCOND,CPID=%1,REGS=SAVE,CELL=%0,LINKAGE=SYSTEM\n"
          " SYSSTATE POP\n"
          " SAM64":
          "=m"(parm_p->cell_p) :
          "m"(parm_p->poolId) :
          "r0", "r1", "r14" ,"r15");

    cell_p = parm_p->cell_p;

    free(parm_p);

    return cell_p;
}

//---------------------------------------------------------------------
//---------------------------------------------------------------------
void
mvs_cpool_free(mvs_cpool_id poolId, void* cell_p) {
    struct parm31 {
        mvs_cpool_id poolId;
        void* __ptr32 cell_p;
    };

    struct parm31* parm_p = __malloc31(sizeof(struct parm31));
    if (parm_p == NULL) {
        return;
    }

    parm_p->poolId = poolId;
    parm_p->cell_p = cell_p;

    __asm(" SAM31\n"
          " SYSSTATE PUSH\n"
          " SYSSTATE AMODE64=NO,OSREL=ZOSV1R6\n"
          " CPOOL FREE,CPID=%0,REGS=SAVE,CELL=(%1)\n"
          " SYSSTATE POP\n"
          " SAM64":
          : // No output
          "m"(parm_p->poolId), "r"(parm_p->cell_p) :
          "r0", "r1", "r14" ,"r15");

    free(parm_p);
}

//---------------------------------------------------------------------
//---------------------------------------------------------------------
void
mvs_cpool_delete(mvs_cpool_id poolId) {
    struct parm31 {
        mvs_cpool_id poolId;
    };

    struct parm31* parm_p = __malloc31(sizeof(struct parm31));
    if (parm_p == NULL) {
        return;
    }

    parm_p->poolId = poolId;

    __asm(" SAM31\n"
          " SYSSTATE PUSH\n"
          " SYSSTATE AMODE64=NO,OSREL=ZOSV1R6\n"
          " CPOOL DELETE,CPID=%0,LINKAGE=SYSTEM\n"
          " SYSSTATE POP\n"
          " SAM64" :
          : // No output
          "m"(parm_p->poolId) :
          "r0", "r1", "r14" ,"r15");

    free(parm_p);
}

