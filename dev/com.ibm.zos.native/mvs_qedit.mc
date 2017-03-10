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

#include "include/mvs_qedit.h"

int free_cib_from_chain(iezcom* com_ptr, void* cib_to_free_p)
{
  int rc = -1;

  __asm(" QEDIT ORIGIN=%1,BLOCK=(%2)\n"
        " ST 15,%0" :
        "=m"(rc) :
        "m"(com_ptr->comcibpt), "r"(cib_to_free_p) :
        "r0","r1","r14","r15");

  return rc;
}

int set_cib_limit(iezcom* com_ptr, int limit)
{
  int rc = -1;

  __asm(" LLGT 2,%2\n"
        " QEDIT ORIGIN=%1,CIBCTR=(2)\n"
        " ST 15,%0" :
        "=m"(rc) :
        "m"(com_ptr->comcibpt),"m"(limit) :
        "r0","r1","r2","r14","r15");

  return rc;
}
