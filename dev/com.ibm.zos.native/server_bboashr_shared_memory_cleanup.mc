/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2014
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 *
 */

#include <metal.h>
#include <stdio.h>
#include <stdlib.h>

#include "include/mvs_enq.h"
#include "include/mvs_psw.h"
#include "include/mvs_user_token_manager.h"
#include "include/server_wola_shared_memory_anchor.h"

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

// Entry point
int main(int argc, char** argv) {
    // -----------------------------------------------------------------------
    // Get into supervisor state.  This utility will run in supervisor
    // state key 2.
    // -----------------------------------------------------------------------
    bbgz_psw current_PSW;
    memset(&current_PSW, 0x00, sizeof(current_PSW));
    extractPSW(&current_PSW);

    if (current_PSW.key != 2) {
        printString("This program must be started in key 2. Exiting...\n");
        return -1;
    }

    // -----------------------------------------------------------------------
    // Note: We're not using the switchToSupervisorState() in mvs_utils
    // because it requires us to drag in the heap manager right now.
    // -----------------------------------------------------------------------
    __asm(" MODESET MODE=SUP" : : : "r0","r1","r14","r15");

    memset(&current_PSW, 0x00, sizeof(current_PSW));
    extractPSW(&current_PSW);

    if (current_PSW.pbm_state == TRUE) {
        printString("This program could not switch to supervisor state. Exiting...\n");
        return -1;
    }

    // Tell the metal C runtime library what parameters it should use if it has
    // to obtain storage on our behalf.
    struct __csysenv_s mysysenv;
    memset(&mysysenv, 0x00, sizeof(struct __csysenv_s));
    mysysenv.__cseversion = __CSE_VERSION_2;

    mysysenv.__csesubpool = 0;
    mysysenv.__cseheap64usertoken = getAddressSpaceSupervisorStateUserToken();

    /* Create a Metal C environment. */
    myenvtkn = (void * ) __cinit(&mysysenv);

    char * wolaGroup = NULL;

    // Skip arg 0 (the program name)
    if (argc >= 2) {
        wolaGroup = argv[1];
    }

    if (wolaGroup == NULL ) {
        printString("WOLA group name was not provided. Exiting...\n");
        __cterm((__csysenv_t) myenvtkn);
        return -1;        
    } 

    char msg[240];
    // Make sure angel process is not up.
    enqtoken angelProcessEnqToken;
    int enq_rc = get_enq_exclusive_system_conditional(BBGZ_ENQ_QNAME, ANGEL_PROCESS_RNAME, &angelProcessEnqToken);
    if (enq_rc != 0) {
        printString("Stop the angel before running this program. Exiting...\n");
        __cterm((__csysenv_t) myenvtkn);
        return -1;
    }

    int rc;
    void* bboashr_p = getBboashrForWolaGroup(wolaGroup, &rc);

    int returnCode = 0;

    if (bboashr_p == NULL) {
        snprintf(msg, sizeof(msg),
                 "Shared memory anchor not found for WOLA group %s. Exiting...\n",
                 wolaGroup);
        printString(msg);
        returnCode = -1;
    } else {
        // Construct the MVS name token name.
        char token_name[16];
        getBboashrTokenName( token_name, wolaGroup );
        // delete name token
        int iean4dl_rc;
        iean4dl(IEANT_SYSTEM_LEVEL,
                token_name,
                &iean4dl_rc);
        if (iean4dl_rc == 0) {
            // Update token_name with the prefix used for a saved bboashr
            memcpy(token_name, BBGZ_BBOASHR_NAME_TOKEN_SAVE_USER_NAME_PREIFX, strlen(BBGZ_BBOASHR_NAME_TOKEN_SAVE_USER_NAME_PREIFX));

            struct name_token_map name_token;

            name_token.bboashr_p = bboashr_p;
            name_token.unused = 0L;

            // save old bboashr
            int iean4cr_rc = -1;
            iean4cr(IEANT_SYSTEM_LEVEL,
                    token_name,
                    (char *)&name_token,
                    IEANT_PERSIST,
                    &iean4cr_rc);
            if (iean4cr_rc == 0) {
                snprintf(msg, sizeof(msg),
                         "Shared memory anchor %p for WOLA group %s removed and saved.\n",
                         bboashr_p, wolaGroup);
                printString(msg);
            } else {
                snprintf(msg, sizeof(msg),
                         "Shared memory anchor %p for WOLA group %s removed but was not saved. iean4cr return code is %i.\n",
                         bboashr_p, wolaGroup, iean4cr_rc);
                printString(msg);
            }
        } else {
            snprintf(msg, sizeof(msg),
                     "Name token delete failed for WOLA group %s. iean4dl return code is %i. Exiting...\n",
                     wolaGroup, iean4dl_rc);
            printString(msg);
            returnCode -1;
        }
    }

    // free angel enq
    release_enq(&angelProcessEnqToken);

    // ----------------------------------------------------------------
    // Destroy the metal C environment.
    // ----------------------------------------------------------------
    __cterm((__csysenv_t) myenvtkn);

    return returnCode;
}

