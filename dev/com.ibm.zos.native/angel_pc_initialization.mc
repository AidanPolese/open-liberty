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
#include <stdlib.h>
#include <stdio.h>
#include <string.h>

#include "include/angel_pc_initialization.h"
#include "include/angel_process_data.h"
#include "include/mvs_utils.h"

#include "include/angel_fixed_shim_module.h"

/* List form of LXRES macro */
__asm(" LXRES SYSTEM=YES,MF=L" : "DS:16"(listlxrs));

/* List form of ETCON macro */
__asm(" ETCON MF=L" : "DS:16"(listetco));

/*-------------------------------------------------------------------*/
/* Define the table used by ETDEF.  This will build the table in the */
/* static area.  Later we'll copy it to dynamic area and fill in the */
/* rest of the fields (like the real routine address).               */
/*-------------------------------------------------------------------*/
__asm("         ETDEF TYPE=INITIAL\n"
      "         ETDEF TYPE=ENTRY,ROUTINE=0,AKM=8\n"
      "         ETDEF TYPE=ENTRY,ROUTINE=0,AKM=8\n"
      "         ETDEF TYPE=ENTRY,ROUTINE=0,AKM=8\n"
      "         ETDEF TYPE=ENTRY,ROUTINE=0,AKM=8\n"
      "         ETDEF TYPE=ENTRY,ROUTINE=0,AKM=8\n"
      "         ETDEF TYPE=ENTRY,ROUTINE=0,AKM=8\n"
      "         ETDEF TYPE=FINAL" : "DS:384"(listetdf));

/* Creates PC routines */
void createPC(bbgzafsm* fsm_p, bbgzcgoo* cgoo_p, void* __ptr32 latentParm_p) {

    // So much of this module needs to be in 31 bit, we'll just put
    // all of the "dynamic area" below the bar.
    struct parm31 {
      short axvalue;
      struct {
        int lxcount;
        int lxvalue;
      } lxl;

      struct {
        int tkcount;
        int tkvalue;
      } tkl;

      char etdstg[sizeof(listetdf)];
      char bufflxrs[sizeof(listlxrs)];
      char buffetco[sizeof(listetco)];
    };

    struct parm31* parm_p = __malloc31(sizeof(struct parm31));

    if (parm_p != NULL)
    {
      memset(parm_p, 0, sizeof(*parm_p));

      /* Set the authorization index to 1 */
      parm_p->axvalue = 1;

      __asm(" SAM31\n"
            " SYSSTATE PUSH\n"
            " SYSSTATE AMODE64=NO,OSREL=ZOSV1R6\n"
            " AXSET AX=%0\n"
            " SYSSTATE POP\n"
            " SAM64" : : "m"(parm_p->axvalue): "r0","r1","r14","r15");

      /* Get an LX */
      parm_p->lxl.lxcount = 1;
      parm_p->lxl.lxvalue = 0;

      /*-----------------------------------------------------------------*/
      /* For the un-named (Default) angel, we store the LX in the CGOO.  */
      /* Named angels store theirs in an angel anchor.                   */
      /*-----------------------------------------------------------------*/
      angel_process_data* apd_p = getAngelProcessData();
      bbgzsgoo* sgoo_p = apd_p->bbgzsgoo_p;
      AngelAnchor_t* aa_p = sgoo_p->bbgzsgoo_angelAnchor_p;
      int* lx_p = (aa_p != NULL) ? (&(aa_p->pclx)) : (&(cgoo_p->bbgzcgoo_rsvd_lx));

      if ((*lx_p) != 0) {
        parm_p->lxl.lxvalue = (*lx_p);
      } else {
        memcpy(parm_p->bufflxrs, &listlxrs, sizeof(parm_p->bufflxrs));

        __asm(" SAM31\n"
              " SYSSTATE PUSH\n"
              " SYSSTATE AMODE64=NO,OSREL=ZOSV1R6\n"
              " LXRES LXLIST=%0,MF=(E,(%1))\n"
              " SYSSTATE POP\n"
              " SAM64": :
              "m"(parm_p->lxl),"r"(parm_p->bufflxrs) :
              "r0","r1","r14","r15");

        *lx_p = parm_p->lxl.lxvalue;
      }

      /* Set up the ETDEF */
      memcpy(parm_p->etdstg, &listetdf, sizeof(parm_p->etdstg));

      /*-----------------------------------------------------------------*/
      /* This code assumes that all PC routine entry points follow the   */
      /* first one at 8 byte increments.                                 */
      /*-----------------------------------------------------------------*/
      __asm(" SAM31\n"
            " SYSSTATE PUSH\n"
            " SYSSTATE AMODE64=NO,OSREL=ZOSV1R6\n"
            " LA 2,%1\n"
            " USING ETDSECT,2\n"
            " ETDEF TYPE=SET,HEADER=AUTOETD,NUMETE=6\n"
            " LG 4,0(%2)\n"
            " ETDEF TYPE=SET,ETEADR=ETD1,ROUTINE=(4),ARR=(%3),ASCMODE=PRIMARY,"
            "SSWITCH=NO,STATE=SUPERVISOR,AKM=8,EKM=0:15,EK=2,PKM=REPLACE,"
            "RAMODE=64,PARM1=(%4)\n"
            " LG 4,8(%2)\n"
            " ETDEF TYPE=SET,ETEADR=ETD2,ROUTINE=(4),ARR=(%3),ASCMODE=PRIMARY,"
            "SSWITCH=NO,STATE=SUPERVISOR,AKM=8,EKM=0:15,EK=2,PKM=REPLACE,"
            "RAMODE=64,PARM1=(%4)\n"
            " LG 4,16(%2)\n"
            " ETDEF TYPE=SET,ETEADR=ETD3,ROUTINE=(4),ARR=(%3),ASCMODE=PRIMARY,"
            "SSWITCH=NO,STATE=SUPERVISOR,AKM=8,EKM=0:15,EK=2,PKM=REPLACE,"
            "RAMODE=64,PARM1=(%4)\n"
            " LG 4,24(%2)\n"
            " ETDEF TYPE=SET,ETEADR=ETD4,ROUTINE=(4),ARR=(%3),ASCMODE=PRIMARY,"
            "SSWITCH=NO,STATE=SUPERVISOR,AKM=8,EKM=0:15,EK=2,PKM=REPLACE,"
            "RAMODE=64,PARM1=(%4)\n"
            " LG 4,32(%2)\n"
            " ETDEF TYPE=SET,ETEADR=ETD5,ROUTINE=(4),ARR=(%3),ASCMODE=PRIMARY,"
            "SSWITCH=NO,STATE=SUPERVISOR,AKM=8,EKM=0:15,EK=2,PKM=REPLACE,"
            "RAMODE=64,PARM1=(%4)\n"
            " LG 4,40(%2)\n"
            " ETDEF TYPE=SET,ETEADR=ETD6,ROUTINE=(4),ARR=(%3),ASCMODE=PRIMARY,"
            "SSWITCH=NO,STATE=SUPERVISOR,AKM=8,EKM=0:15,EK=2,PKM=REPLACE,"
            "RAMODE=64,PARM1=(%4)\n"
            " ETCRE ENTRIES=AUTOETD\n"
            " ST 0,%0\n"
            " DROP 2\n"
            " SYSSTATE POP\n"
            " SAM64":
            "=m"(parm_p->tkl.tkvalue) :
            "m"(parm_p->etdstg),"r"(&(fsm_p->register_pc_stub)),"r"(fsm_p->associatedRecoveryRoutine),"r"(latentParm_p) :
            "r0","r2","r4");

      parm_p->tkl.tkcount = 1;

      memcpy(parm_p->buffetco, &listetco, sizeof(parm_p->buffetco));

      __asm(" SAM31\n"
            " SYSSTATE PUSH\n"
            " SYSSTATE AMODE64=NO,OSREL=ZOSV1R6\n"
            " ETCON LXLIST=%0,TKLIST=%1,MF=(E,(%2))\n"
            " SYSSTATE POP\n"
            " SAM64": :
            "m"(parm_p->lxl),"m"(parm_p->tkl),"r"(parm_p->buffetco) :
            "r0","r1","r14","r15");

      free(parm_p);
    }
}

/*-------------------------------------------------------------------*/
/* Define a DSECT which is identical to the ETDEF table created in   */
/* the static area.  We map this DSECT on top of the dynamic area    */
/* storage so that we can pass the labels to the ETDEF macro and     */
/* fill in the actual values for the parameters (like ROUTINE).      */
/*-------------------------------------------------------------------*/
#pragma insert_asm("ETDSECT  DSECT")
#pragma insert_asm("AUTOETD  ETDEF TYPE=INITIAL")
#pragma insert_asm("ETD1     ETDEF TYPE=ENTRY,ROUTINE=0,AKM=8")
#pragma insert_asm("ETD2     ETDEF TYPE=ENTRY,ROUTINE=0,AKM=8")
#pragma insert_asm("ETD3     ETDEF TYPE=ENTRY,ROUTINE=0,AKM=8")
#pragma insert_asm("ETD4     ETDEF TYPE=ENTRY,ROUTINE=0,AKM=8")
#pragma insert_asm("ETD5     ETDEF TYPE=ENTRY,ROUTINE=0,AKM=8")
#pragma insert_asm("ETD6     ETDEF TYPE=ENTRY,ROUTINE=0,AKM=8")
#pragma insert_asm("         ETDEF TYPE=FINAL")

