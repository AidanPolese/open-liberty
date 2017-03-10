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

#include "include/mvs_abend.h"

void abend(short comp, int reason)
{
  __asm(" ABEND (%0),REASON=(%1),DUMP,STEP,SYSTEM" : :
        "r"(comp),"r"(reason));
}

void abend_with_data(short comp, int reason, void* data1, void* data2)
{
  __asm(" LGR 2,%2\n"
        " LGR 3,%3\n"
        " ABEND (%0),REASON=(%1),DUMP,STEP,SYSTEM" : :
        "r"(comp),"r"(reason),"r"(data1),"r"(data2) :
        "r0","r1","r2","r3","r15");
        /* Note that control never returns, but we don't */
        /* want the compiler to use r2 or r3 for the     */
        /* parameters.  Also the macro clobbers r0, r1   */
        /* and r15.                                      */
}
