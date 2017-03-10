/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2013
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */

#include <metal.h>
#include <stdio.h>
#include <stdlib.h>
#include <ctype.h>

#include "include/mvs_enq.h"
#include "include/mvs_iarv64.h"
#include "include/mvs_psw.h"
#include "include/mvs_user_token_manager.h"
#include "include/server_local_comm_client.h"
#include "include/server_local_comm_data_store.h"

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

// Function type for freeing a control block.
typedef void freeControlBlockFcn_t(void* memStartAddr_p);

// Free a LOCL
static void processLOCL(void* memStartAddr_p) {
    LocalCommClientAnchor_t* lcAnchor_p = (LocalCommClientAnchor_t*)memStartAddr_p;
    long long userToken = 0L;
    memcpy(&userToken, &(lcAnchor_p->creatorStoken), sizeof(userToken));

    int rc = 0, rsn = 0;
    rc = detachSharedAboveConditional(memStartAddr_p, userToken, TRUE, &rsn);

    if (rc != 0) {
        char msg[120];
        snprintf(msg, sizeof(msg),
                 "  Error calling IARV64 to free LOCL with user token %llx, RC = %x RSN = %x\n",
                 userToken, rc, rsn);
        printString(msg);
    }
}

// Free an LDAT
static void processLDAT(void* memStartAddr_p) {
    LocalCommClientDataStore_t* dataStore_p = (LocalCommClientDataStore_t*) memStartAddr_p;
    void* anchor_p = dataStore_p->localCommClientControlBlock_p;
    char msg[120];

    // Try to connect to the LOCL.  We need to connect to the LOCL to get
    // the user token that we'll use to detach the system affinity of the
    // LDAT.
    long long myUserToken = getAddressSpaceSupervisorStateUserToken();
    int rc = 0, rsn = 0;
    snprintf(msg, sizeof(msg),
             "  Trying to attach to LOCL at %p ...\n",
             anchor_p, rc, rsn);
    printString(msg);
    rc = accessSharedAboveConditional(anchor_p, myUserToken, &rsn);

    if (rc == 0) {
        LocalCommClientAnchor_t* lcAnchor_p = (LocalCommClientAnchor_t*)anchor_p;
        long long ldatUserToken = 0L;
        memcpy(&ldatUserToken, &(lcAnchor_p->creatorStoken), sizeof(ldatUserToken));

        // Try to detach from the LDAT.
        snprintf(msg, sizeof(msg),
                 "  Trying to free LDAT at address %p with user token %llx ...\n",
                 memStartAddr_p, ldatUserToken);
        printString(msg);

        rc = detachSharedAboveConditional(memStartAddr_p, ldatUserToken, TRUE, &rsn);
        if (rc != 0) {
            snprintf(msg, sizeof(msg),
                     "  Error calling IARV64 to free LDAT with user token %llx, RC = %x RSN = %x\n",
                     ldatUserToken, rc, rsn);
            printString(msg);
        }

        detachSharedAbove(anchor_p, myUserToken, FALSE);
    } else {
        snprintf(msg, sizeof(msg),
                 "  Error calling IARV64 to attach to LOCL at %p, RC = %x RSN = %x\n",
                 anchor_p, rc, rsn);
        printString(msg);
    }
}


// Free a Large Data Message
static void processLGMG(void* memStartAddr_p) {
    LargeDataMessageHeader_t* largeMsg_p = (LargeDataMessageHeader_t*) memStartAddr_p;
    long long userToken = 0L;
    memcpy(&userToken, &(largeMsg_p->largeData.owningUserToken), sizeof(userToken));

    int rc = 0, rsn = 0;
    rc = detachSharedAboveConditional(memStartAddr_p, userToken, TRUE, &rsn);

    if (rc != 0) {
        char msg[120];
        snprintf(msg, sizeof(msg),
                 "  Error calling IARV64 to free LGMG with user token %llx, RC = %x RSN = %x\n",
                 userToken, rc, rsn);
        printString(msg);
    }
}

// Process an entry, see if it's ours, free the system affinity.
static void processSharedMemoryEntry(v64waentry* curEntry_p, char* targetEye_p, freeControlBlockFcn_t* targetFcn_p) {
    void* memStartAddr_p;
    memcpy(&memStartAddr_p, curEntry_p->v64wastart64, sizeof(memStartAddr_p));

    // Local Comm shared memory areas fold its eyecatcher during cleanup.
    // For example, BBGZLDAT becomes LDATBBGZ. We should check for both.
    char reverseFoldTargetEye[8];
    if (targetEye_p) {
    	memcpy(&(reverseFoldTargetEye[4]), targetEye_p, 4);
    	memcpy(&(reverseFoldTargetEye[0]), targetEye_p+4, 4);
    } else {
    	reverseFoldTargetEye[0] = '\0';
    }

    // If we still have a system affinity, connect to the shared memory.
    char msg[120];
    if (((curEntry_p->v64waflag & v64washared) == v64washared) &&    // Shared
        ((curEntry_p->v64waflag1 & v64wasysaff) == v64wasysaff) &&   // SYS affinity
        ((curEntry_p->v64waflag & v64wakey) == 0x20)) {              // Key 2
        snprintf(msg, sizeof(msg),
                 "Processing shared memory segment at address %p for %s or %.8s ...\n",
                 memStartAddr_p, targetEye_p, reverseFoldTargetEye);
        printString(msg);

        long long userToken = getAddressSpaceSupervisorStateUserToken();
        accessSharedAbove(memStartAddr_p, userToken); // ABEND if failure.

        // See about an eye catcher.
        char eye[8];
        memcpy(eye, memStartAddr_p, sizeof(eye));

        if ((targetEye_p == NULL) || (targetFcn_p == NULL)) {
            // Just print the eye catcher (first 16 bytes).
            char firstSixteenBytes[16];
            memcpy(firstSixteenBytes, memStartAddr_p, 16);
            for (int x = 0; x < 16; x++) {
                if (isprint(firstSixteenBytes[x]) == 0) {
                    firstSixteenBytes[x] = '.';
                }
            }
            snprintf(msg, sizeof(msg), " %8.8X %8.8X %8.8X %8.8X   %.16s\n",
                     *((int*)memStartAddr_p),
                     *(((int*)memStartAddr_p) + 1),
                     *(((int*)memStartAddr_p) + 2),
                     *(((int*)memStartAddr_p) + 3),
                     firstSixteenBytes);
            printString(msg);
        } else if ((memcmp(eye, targetEye_p, sizeof(eye)) == 0) ||
                   (memcmp(reverseFoldTargetEye, targetEye_p, sizeof(reverseFoldTargetEye)) == 0)) {
            snprintf(msg, sizeof(msg),
                     " Freeing system affinity, eye catcher %.8s\n",
                     eye);
            printString(msg);
            targetFcn_p(memStartAddr_p);
        }

        detachSharedAbove(memStartAddr_p, userToken, FALSE);
    }
}

// Entry point
int main() {
    // -----------------------------------------------------------------------
    // Get into supervisor state.  This utility will run in supervisor
    // state key 2.
    // -----------------------------------------------------------------------
    bbgz_psw current_PSW;
    memset(&current_PSW, 0x00, sizeof(current_PSW));
    extractPSW(&current_PSW);

    if (current_PSW.key != 2) {
        printString("This program must be started in key 2.  Exiting...\n");
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
        printString("This program could not switch to supervisor state.  Exiting...\n");
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

    // Make sure no servers up and connected to the angel process.
    char msg[120];
    int isgquery_rc = 0, isgquery_rsn = 0;
    isgyquaahdr* server_enqs_p = scan_enq_system(
        BBGZ_ENQ_QNAME, ANGEL_PROCESS_SERVER_ENQ_RNAME_QUERY,
        &isgquery_rc, &isgquery_rsn);

    if ((server_enqs_p != NULL) && (server_enqs_p->isgyquaahdrnumrecords > 0)) {
        snprintf(msg, sizeof(msg),
                 "Detected %i Liberty servers still connected to the angel.  Processing terminated.\n",
                 server_enqs_p->isgyquaahdrnumrecords);
        printString(msg);
        free(server_enqs_p);
        __cterm((__csysenv_t) myenvtkn);
        return -1;
    }
    free(server_enqs_p);

    // In pass 1 we'll free LDATs.
    printString("Calling IARV64 to list shared memory control blocks (pass 1) ...\n");

    v64waheader* sharedMemoryHeader_p = listSharedAbove();
    if (sharedMemoryHeader_p != NULL) {
        int returnCode = sharedMemoryHeader_p->v64wareturncode;
        if ((returnCode == v64warc_ok) || (returnCode == v64warc_changed) ||
            (returnCode == v64warc_notfound)) {
            snprintf(msg, sizeof(msg), "Returned %i memory areas.\n",
                     sharedMemoryHeader_p->v64wanumdataareas);
            printString(msg);

            // First process the LDATs since they have to connect to LOCLs.
            v64waentry* curEntry_p = (v64waentry*)(sharedMemoryHeader_p + 1);
            for (int x = 0; x < sharedMemoryHeader_p->v64wanumdataareas; x++) {
                processSharedMemoryEntry(curEntry_p, BBGZLDAT_EYE, processLDAT);
                curEntry_p = curEntry_p + 1;
            }
        } else {
            snprintf(msg, sizeof(msg),
                     "Could not list shared memory, IARV64 return code %i (decimal)\n",
                     returnCode);
            printString(msg);
        }

        free(sharedMemoryHeader_p);
    } else {
        printString("Could not obtain shared memory list from IARV64.\n");
    }

    // In pass 2 we'll process LOCLs.
    printString("Calling IARV64 to list shared memory control blocks (pass 2) ...\n");

    sharedMemoryHeader_p = listSharedAbove();
    if (sharedMemoryHeader_p != NULL) {
        int returnCode = sharedMemoryHeader_p->v64wareturncode;
        if ((returnCode == v64warc_ok) || (returnCode == v64warc_changed) ||
            (returnCode == v64warc_notfound)) {
            snprintf(msg, sizeof(msg), "Returned %i memory areas.\n",
                     sharedMemoryHeader_p->v64wanumdataareas);
            printString(msg);

            // Then process LOCLs.
            v64waentry* curEntry_p = (v64waentry*)(sharedMemoryHeader_p + 1);
            for (int x = 0; x < sharedMemoryHeader_p->v64wanumdataareas; x++) {
                processSharedMemoryEntry(curEntry_p, BBGZLOCL_EYE, processLOCL);
                curEntry_p = curEntry_p + 1;
            }
        } else {
            snprintf(msg, sizeof(msg),
                     "Could not list shared memory, IARV64 return code %i (decimal)\n",
                     returnCode);
            printString(msg);
        }

        free(sharedMemoryHeader_p);
    } else {
        printString("Could not obtain shared memory list from IARV64.\n");
    }

    // In pass 3, cleanup Local Comm Large Data Shared Memory objects ("BBGZLGMG")
    printString("Calling IARV64 to list shared memory control blocks (pass 3) ...\n");

    sharedMemoryHeader_p = listSharedAbove();
    if (sharedMemoryHeader_p != NULL) {
        int returnCode = sharedMemoryHeader_p->v64wareturncode;
        if ((returnCode == v64warc_ok) || (returnCode == v64warc_changed) ||
            (returnCode == v64warc_notfound)) {
            snprintf(msg, sizeof(msg), "Returned %i memory areas.\n",
                     sharedMemoryHeader_p->v64wanumdataareas);
            printString(msg);

            // Then process LGMGs.
            v64waentry* curEntry_p = (v64waentry*)(sharedMemoryHeader_p + 1);
            for (int x = 0; x < sharedMemoryHeader_p->v64wanumdataareas; x++) {
                processSharedMemoryEntry(curEntry_p, BBGZLMSG_LARGEDATA_EYE, processLGMG);
                curEntry_p = curEntry_p + 1;
            }
        } else {
            snprintf(msg, sizeof(msg),
                     "Could not list shared memory, IARV64 return code %i (decimal)\n",
                     returnCode);
            printString(msg);
        }

        free(sharedMemoryHeader_p);
    } else {
        printString("Could not obtain shared memory list from IARV64.\n");
    }

    // In pass 4 we'll dump the eye catcher for anything left.
    printString("Calling IARV64 to list shared memory control blocks (pass 4) ...\n");

    sharedMemoryHeader_p = listSharedAbove();
    if (sharedMemoryHeader_p != NULL) {
        int returnCode = sharedMemoryHeader_p->v64wareturncode;
        if ((returnCode == v64warc_ok) || (returnCode == v64warc_changed) ||
            (returnCode == v64warc_notfound)) {
            snprintf(msg, sizeof(msg), "Returned %i memory areas.\n",
                     sharedMemoryHeader_p->v64wanumdataareas);
            printString(msg);

            // Print out information for whatever's left.
            v64waentry* curEntry_p = (v64waentry*)(sharedMemoryHeader_p + 1);
            for (int x = 0; x < sharedMemoryHeader_p->v64wanumdataareas; x++) {
                processSharedMemoryEntry(curEntry_p, NULL, NULL);
                curEntry_p = curEntry_p + 1;
            }
        } else {
            snprintf(msg, sizeof(msg),
                     "Could not list shared memory, IARV64 return code %i (decimal)\n",
                     returnCode);
            printString(msg);
        }

        free(sharedMemoryHeader_p);
    } else {
        printString("Could not obtain shared memory list from IARV64.\n");
    }

    // ----------------------------------------------------------------
    // Destroy the metal C environment.
    // ----------------------------------------------------------------
    __cterm((__csysenv_t) myenvtkn);

    return 0;
}
