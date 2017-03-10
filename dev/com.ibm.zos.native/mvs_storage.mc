/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2011,2013
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */

/**
 * @file
 * Defines functions for obtaining and releasing storage using the MVS STORAGE macro.
 */
#include <metal.h>
#include <stdlib.h>

#include "include/mvs_storage.h"

/* Storage obtain */
#pragma noinline(storageObtain) // Compiler workaround for PMR 91539,999,000
void*
storageObtain(int length, int subpool, int key, int* rc_p) {
    int rc = -1;
    int shifted_key = (key << 4);
    void* addr = NULL;

    __asm(" STORAGE OBTAIN,LENGTH=(%2),ADDR=(%0),SP=(%3),KEY=(%4),STARTBDY=4,COND=YES\n"
        " ST 15,%1":
          "=r"(addr),"=m"(rc) :
          "r"(length),"r"(subpool),"r"(shifted_key) :
          "r0","r1","r14","r15");

    /* Give caller the return code */
    if (rc_p != NULL) *rc_p = rc;

    /* If storage obtain failed, make sure the pointer is NULL */
    if (rc != 0) addr = NULL;

    return addr;
}

/* Storage release */
#pragma noinline(storageRelease) // Compiler workaround for PMR 91539,999,000
int
storageRelease(void* addr, int length, int subpool, int key) {
    int shifted_key = (key << 4);
    int rc = -1;

    __asm(" STORAGE RELEASE,LENGTH=(%1),ADDR=(%2),SP=(%3),KEY=(%4),COND=YES\n"
        " ST 15,%0" :
          "=m"(rc) :
          "r"(length),"r"(addr),"r"(subpool),"r"(shifted_key) :
          "r0","r1","r14","r15");

    return rc;
}

/* Storage release with input TCB */
#pragma noinline(storageReleaseTcb) // Compiler workaround for PMR 91539,999,000
int
storageReleaseTcb(void* addr, int length, int subpool, int key, void * tcbAddr) {
    int shifted_key = (key << 4);
    int rc = -1;

    __asm(" STORAGE RELEASE,LENGTH=(%1),ADDR=(%2),SP=(%3),KEY=(%4),TCBADDR=(%5),COND=YES\n"
        " ST 15,%0" :
          "=m"(rc) :
          "r"(length),"r"(addr),"r"(subpool),"r"(shifted_key),"r"(tcbAddr) :
          "r0","r1","r14","r15");

    return rc;
}
