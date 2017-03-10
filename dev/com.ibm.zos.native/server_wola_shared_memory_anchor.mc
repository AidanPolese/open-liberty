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

/**
 * @file
 *
 * Assorted authorized routines used by the WOLA code for accessing the BBOASHR.
 *
 * These routines are shared by WOLA clients and servers.
 *
 */

#include <metal.h>
#include <stdlib.h>
#include <string.h>

#include "include/mvs_enq.h"
#include "include/mvs_iarv64.h"
#include "include/mvs_user_token_manager.h"
#include "include/ieantc.h"
#include "include/ras_tracing.h"
#include "include/server_common_function_module.h"
#include "include/server_wola_shared_memory_anchor.h"

#define RAS_MODULE_CONST  RAS_MODULE_SERVER_WOLA_SHARED_MEMORY_ANCHOR


/**
 * Finds the WOLA shared memory attachment info for the input shared memory address.
 * The caller should hold ENQ CLIENT_WOLA_ATTACH_SHMEM_ENQ_RNAME.
 */
static WolaClientSharedMemoryAttachmentInfo_t* findSharedMemoryAttachInfo(
    struct wolaSharedMemoryAnchor* anchor_p, ServerCommonFunctionModuleProcessData_t* processData_p) {

    WolaClientSharedMemoryAttachmentInfo_t* curInfo_p = processData_p->wolaSharedMemoryAttachHead_p;

    while ((curInfo_p != NULL) && (curInfo_p->wolaAnchor_p != anchor_p)) {
        curInfo_p = curInfo_p->next_p;
    }

    return curInfo_p;
}


/**
 * Connect a client to some shared memory owned by a WOLA group.
 *
 * @param wolaGroup An 8 character WOLA group name.
 *
 * @return A pointer to the requested shared memory anchor.
 */
struct wolaSharedMemoryAnchor* clientConnectToWolaSharedMemoryAnchor(char* wolaGroup) {
    int iean4rt_rc = -1;

    // Figure out what the address of the WOLA shared memory is for this group.
    struct wolaSharedMemoryAnchor* anchor_p = (struct wolaSharedMemoryAnchor*)
        getBboashrForWolaGroup(wolaGroup, &iean4rt_rc);

    ServerCommonFunctionModuleProcessData_t* processData_p =
        getServerCommonFunctionModuleProcessData();

    if ((anchor_p != NULL) && (processData_p != NULL)) {

        // Get a reference to our list of shared memory attachments.
        // TODO: tWAS set ARR to release ENQ on error.
        enqtoken enqToken;
        get_enq_exclusive_step(BBGZ_ENQ_QNAME, CLIENT_WOLA_ATTACH_SHMEM_ENQ_RNAME, &enqToken);

        // See if we've already attached to this shared memory.  If so, bump the count.
        // If not, create a new attachment info.
        WolaClientSharedMemoryAttachmentInfo_t* info_p = findSharedMemoryAttachInfo(anchor_p, processData_p);
        if (info_p != NULL) {
            info_p->attachCount = info_p->attachCount + 1;
        } else {
            info_p = malloc(sizeof(*info_p));
            if (info_p != NULL) {
                // Access the shared memory.
                accessSharedAbove(anchor_p, processData_p->clientProcessDataToken);

                // Create the info object and chain it.
                memset(info_p, 0, sizeof(*info_p));
                memcpy(info_p->eyecatcher, BBOASMA_EYE, sizeof(info_p->eyecatcher));
                info_p->version = 1;
                info_p->size = sizeof(*info_p);
                info_p->wolaAnchor_p = anchor_p;
                info_p->attachCount = 1;
                info_p->next_p = processData_p->wolaSharedMemoryAttachHead_p;
                processData_p->wolaSharedMemoryAttachHead_p = info_p;
            } else {
                anchor_p = NULL;
            }
        }

        // Release the ENQ
        release_enq(&enqToken);
    } else {
        anchor_p = NULL;
    }

    return anchor_p;
}


/**
 * Disconnect a client from some shared memory owned by a WOLA group.
 *
 * @param anchor_p A pointer to the shared memory anchor.
 *
 * @return 0 if the detach was successful, non-zero if not.
 */
int clientDisconnectFromWolaSharedMemoryAnchor(struct wolaSharedMemoryAnchor* anchor_p) {
    int rc = -1;

    ServerCommonFunctionModuleProcessData_t* processData_p =
        getServerCommonFunctionModuleProcessData();

    if (processData_p != NULL) {

        // Get a reference to our list of shared memory attachments.
        // TODO: tWAS set ARR to release ENQ on error.
        enqtoken enqToken;
        get_enq_exclusive_step(BBGZ_ENQ_QNAME, CLIENT_WOLA_ATTACH_SHMEM_ENQ_RNAME, &enqToken);

        // See if we've already attached to this shared memory.  If so, bump the count.
        // If not, create a new attachment info.
        WolaClientSharedMemoryAttachmentInfo_t* info_p = findSharedMemoryAttachInfo(anchor_p, processData_p);
        if (info_p != NULL) {
            info_p->attachCount = info_p->attachCount - 1;
            rc = 0;

            // If we decreased the attachment count to zero, remove the
            // element and detach from the shared memory.
            if (info_p->attachCount == 0) {
                WolaClientSharedMemoryAttachmentInfo_t* prev_p = NULL;
                WolaClientSharedMemoryAttachmentInfo_t* cur_p = processData_p->wolaSharedMemoryAttachHead_p;
                while (cur_p != info_p) {
                    prev_p = cur_p;
                    cur_p = cur_p->next_p;
                }

                if (prev_p == NULL) {
                    processData_p->wolaSharedMemoryAttachHead_p = cur_p->next_p;
                } else {
                    prev_p->next_p = cur_p->next_p;
                }

                free(cur_p);

                // Detach from the shared memory.
                detachSharedAbove(anchor_p, processData_p->clientProcessDataToken, FALSE);
            }
        }

        // Release the ENQ
        release_enq(&enqToken);
    }

    return rc;
}

/**
 * Clean up any remaining attachments to shared memory.  This function is
 * invoked when the bbgzscfm is being unloaded from memory, after the last
 * client unbind, or on client termination.
 */
void cleanupClientWolaSharedMemoryAttachments(void) {
    // There can be no other client threads in here when this method is invoked.
    // So no need for serialization.  Just iterate the list and remove the
    // attachments.  If there is no process data, we are not running in the client
    // address space, so there is nothing to clean up.
    ServerCommonFunctionModuleProcessData_t* processData_p =
        getServerCommonFunctionModuleProcessData();

    if (processData_p != NULL) {
        // Get a reference to our list of shared memory attachments.
        while (processData_p->wolaSharedMemoryAttachHead_p != NULL) {
            WolaClientSharedMemoryAttachmentInfo_t* cur_p = processData_p->wolaSharedMemoryAttachHead_p;
            detachSharedAbove(cur_p->wolaAnchor_p, processData_p->clientProcessDataToken, FALSE);
            processData_p->wolaSharedMemoryAttachHead_p = cur_p->next_p;
            free(cur_p);
        }
    }
}

