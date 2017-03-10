/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2016
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 *
 *
 *
 * If you change this program, be sure to update the angel fat and stick this guy here: com.ibm.ws.zos.angel_zfat/publish/files/bbgzchkt
 *
 *
 */
#include <stdio.h>
#include <metal.h>
#include <stdlib.h>
#include <string.h>

#include "../include/angel_bgvt_services.h"
#include "../include/angel_check_main.h"
#include "../include/angel_check_module.h"
#include "../include/bpx_load.h"
#include "../include/mvs_user_token_manager.h"
#include "../include/mvs_utils.h"

#define NO_BGVT             8
#define NO_BGVT_ANGEL_CHECK 9

register void* myenvtkn __asm("r12");

#pragma linkage(BPX4WRT,OS_NOSTACK)
void BPX4WRT(int filedes, void* buffer, int alet, int bufsize, int* retval, int* retcode, int* rsnval);

/* Print a string */
static void printString(const char* message_p) {
    int retval = 0;
    int retcode = 0;
    int rsnval = 0;
    int filedes = 1; // stdout

    BPX4WRT(filedes, &message_p, 0, strlen(message_p), &retval, &retcode, &rsnval);
}


/**
 * Use this to check whether an angel set the angel_check 
 * function pointer in the bgvt, and what version it is. 
 * Ideally, a test would call this before any angel 
 * has started, after an angel has started, and after
 * another version of the check_angel function has been loaded.
 */
int main(int argc, char** argv) {

    // Tell the metal C runtime library what parameters it should use if it has
    // to obtain storage on our behalf.
    struct __csysenv_s mysysenv;
    memset(&mysysenv, 0x00, sizeof(struct __csysenv_s));
    mysysenv.__cseversion = __CSE_VERSION_2;

    mysysenv.__csesubpool = 0;
    mysysenv.__cseheap64usertoken = getAddressSpaceSupervisorStateUserToken();

    /* Create a Metal C environment. */
    myenvtkn = (void * ) __cinit(&mysysenv);

    int rc = -1;
    char buffer[256];

    __asm(" BBGZAACK ANAME=%0\n"
          " ST 15,%1" :
          "=m"(argv[1]),"=m"(rc) : :
          "r0","r1","r14","r15");

    snprintf(buffer, sizeof(buffer), "Exiting angel_check_test with rc = (%d).\n", rc);
    printString(buffer);
    // ----------------------------------------------------------------
    // Destroy the metal C environment.
    // ----------------------------------------------------------------
    __cterm((__csysenv_t) myenvtkn);

    return rc;
}
