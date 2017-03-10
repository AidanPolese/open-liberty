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
#include <string.h>

#include "include/mvs_extract.h"
#include "include/mvs_utils.h"

//---------------------------------------------------------------------
// List form of EXTRACT macro
//---------------------------------------------------------------------
__asm(" EXTRACT MF=L" : "DS"(listextr));

//---------------------------------------------------------------------
// Extract the command scheduler communications list from the TCB
//---------------------------------------------------------------------
iezcom*
extract_comm(void) {
    iezcom* com_ptr = NULL;

    struct parms31 {
        char buffextr[sizeof(listextr)];
        iezcom* __ptr32 com_ptr;
    };

    struct parms31* parms_p = __malloc31(sizeof(struct parms31));
    if (parms_p == NULL) {
        return NULL;
    }

    memcpy(parms_p->buffextr, &listextr, sizeof(listextr));

    __asm(" SAM31\n"
          " SYSSTATE AMODE64=NO\n"
          " EXTRACT (%0),'S',FIELDS=COMM,MF=(E,(%1))\n"
          " SYSSTATE AMODE64=YES\n"
          " SAM64" : :
          "r"(&(parms_p->com_ptr)), "r"(parms_p->buffextr) :
          "r0","r1","r14","r15");

    com_ptr = parms_p->com_ptr;
    free(parms_p);

    return com_ptr;
}

