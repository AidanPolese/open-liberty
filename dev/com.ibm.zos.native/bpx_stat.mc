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
#include <string.h>

#include "include/bpx_stat.h"

int stat(char* pathname, struct stat* info_p)
{
  int pathl = 0;
  int statusl = sizeof(struct stat);
  int rv = 0;
  int rc = 0;
  int rsn = 0;
  char plist[128];

  pathl = strlen(pathname);

  __asm(" CALL BPX4STA,(%3,%4,%5,%6,%0,%1,%2),MF=(E,%7)" :
        "=m"(rv),"=m"(rc),"=m"(rsn) :
        "m"(pathl),"m"(*pathname),"m"(statusl),"m"(*info_p),"m"(plist) :
        "r0","r1","r14","r15");

  if (rv != -1)
  {
    rc = 0;
  }

  return rc;
}
