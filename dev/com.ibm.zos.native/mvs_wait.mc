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

#include "include/mvs_wait.h"

int wait(void* __ptr32 wait_ecb_p)
{
  struct
  {
    int wait_bit : 1;
    int post_bit : 1;
    int comp_code : 30;
  } ecb_data;

  __asm(" WAIT ECB=(%0)" : : "r"(wait_ecb_p) :
        "r0","r1","r14","r15");

  memcpy(&ecb_data, wait_ecb_p, sizeof(ecb_data));
  return ecb_data.comp_code;
}

int waitlist(void* __ptr32 wait_ecb_list_p)
{
  int rc = 0;

  __asm(" WAIT ECBLIST=(%1)\n"
        " ST   15,%0":
        "=m"(rc) :
        "r"(wait_ecb_list_p) :
        "r0","r1","r14","r15");


  return rc;
}

int post(void* __ptr32 post_ecb_p, int comp_code)
{
  int post_rc;

  __asm(" LLGT 2,%1\n"
        " LLGT 11,%2\n"
        " POST (2),(11)\n"
        " ST   15,%0" :
        "=m"(post_rc) :
        "m"(post_ecb_p),"m"(comp_code):
        "r0","r1","r2","r11","r14","r15");

  return post_rc;
}

