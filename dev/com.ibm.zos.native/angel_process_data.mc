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

#include "include/angel_process_data.h"
#include "include/angel_task_data.h"
#include "include/common_defines.h"
#include "include/heap_management.h"
#include "include/ieantc.h"
#include "include/mvs_cell_pool_services.h"
#include "include/mvs_estae.h"
#include "include/mvs_storage.h"
#include "include/mvs_utils.h"
#include "include/stack_services.h"

#include "include/gen/ihapsa.h"
#include "include/gen/ikjtcb.h"
#include "include/gen/ihastcb.h"
#include "include/gen/ihaascb.h"
#include "include/gen/ihaassb.h"

#ifdef ANGEL_COMPILE
#define ANGEL_PROCESS_DATA_EYE_CATCHER "BBGZAPD_"

#define ANGEL_MALLOC_STORAGE_SUBPOOL 249 //!< Subpool to use when doing a storage obtain.
#define ANGEL_MALLOC_STORAGE_KEY 2 //!< Key to use when doing a storage obtain

angel_process_data* createAngelProcessData(bbgzsgoo* sgoo_p, unsigned char type)
{
    // TODO: Make sure 'type' is a valid type.

    // -----------------------------------------------------------------------
    // If we were passed an SGOO, we will want to fill in the instance count
    // in the angel process data.
    // -----------------------------------------------------------------------
    cs_t new_angel_process_data_count = (cs_t) 0;

    if (sgoo_p != NULL) {
        cs_t old_angel_process_data_count = sgoo_p->bbgzsgoo_angel_process_data_count;
        int csResult;

        do {
          new_angel_process_data_count = old_angel_process_data_count + 1;
          csResult = __cs1(&old_angel_process_data_count,
                           &(sgoo_p->bbgzsgoo_angel_process_data_count),
                           &new_angel_process_data_count);
        }
        while (csResult);
    }

    // -----------------------------------------------------------------------
    // Get storage for the angel process data from the cell pool in the SGOO
    // -----------------------------------------------------------------------
    angel_process_data* apd_p = getCellPoolCell(sgoo_p->bbgzsgoo_angel_process_data_cellpool_id);

    if (apd_p != NULL) {
        char apd_name[16];
        char apd_name2[16];
        char apd_token[16];

        memset(apd_p, 0, sizeof(angel_process_data));
        memcpy(apd_p->eyecatcher, ANGEL_PROCESS_DATA_EYE_CATCHER,
               sizeof(apd_p->eyecatcher));
        apd_p->version = 1;
        apd_p->length = sizeof(angel_process_data);
        apd_p->instance_num = new_angel_process_data_count;
        apd_p->bbgzsgoo_p = sgoo_p;
        apd_p->as_type = type;

        memcpy(&(apd_p->clientBindArea.serverStoken), &(((assb*)(((ascb*)(((psa*)0)->psaaold))->ascbassb))->assbstkn), sizeof(SToken));

        // -------------------------------------------------------------------
        // Hang the angel_process_data off of a pair of name tokens.
        //
        // The first is scoped at the address space level and is used by the
        // server when creating its task level prefix area.
        //
        // The second is scoped at the system level and also contains the
        // STOKEN for the address space.  It is used by the RESMGR to clean
        // up the angel_process_data after the address space terminates.
        // -------------------------------------------------------------------
        memset(apd_name, 0, sizeof(apd_name));
        memcpy(apd_name, ANGEL_PROCESS_DATA_TOKEN_NAME,
               strlen(ANGEL_PROCESS_DATA_TOKEN_NAME));

        memset(apd_token, 0, sizeof(apd_token));
        memcpy(apd_token, &apd_p, sizeof(apd_p));

        int home_nametoken_rc;
        iean4cr(IEANT_HOME_LEVEL,
                apd_name,
                apd_token,
                IEANT_NOPERSIST,
                &home_nametoken_rc);

        if (home_nametoken_rc == 0) {
            psa* psa_p = NULL;
            ascb* ascb_p = psa_p->psaaold;
            assb* assb_p = ascb_p->ascbassb;

            memcpy(apd_name2, apd_name, sizeof(apd_name2));
            memcpy(&(apd_name2[8]), &(assb_p->assbstkn),
                   sizeof(assb_p->assbstkn));

            int system_nametoken_rc;
            iean4cr(IEANT_SYSTEM_LEVEL,
                    apd_name2,
                    apd_token,
                    IEANT_PERSIST,
                    &system_nametoken_rc);

            if (system_nametoken_rc != 0) {
                freeCellPoolCell(sgoo_p->bbgzsgoo_angel_process_data_cellpool_id, apd_p);
                apd_p = NULL;
            }
        } else {
            freeCellPoolCell(sgoo_p->bbgzsgoo_angel_process_data_cellpool_id, apd_p);
            apd_p = NULL;
        }
    }

    return apd_p;
}

void deleteAngelProcessDataNameTokens(angel_process_data* apd_p, SToken* stoken_p) {
    int ieant_rc;

    char apd_name[16];
    char apd_name2[16];

    memset(apd_name, 0, sizeof(apd_name));
    memcpy(apd_name, ANGEL_PROCESS_DATA_TOKEN_NAME,
           strlen(ANGEL_PROCESS_DATA_TOKEN_NAME));

    // ---------------------------------------------------------------
    // Delete the system level name token that we created.
    // ---------------------------------------------------------------
    void* local_stoken_p = stoken_p;

    if (local_stoken_p == NULL) {
      psa* psa_p = NULL;
      ascb* ascb_p = psa_p->psaaold;
      assb* assb_p = ascb_p->ascbassb;
      local_stoken_p = &(assb_p->assbstkn);
    }

    memcpy(apd_name2, apd_name, sizeof(apd_name2));
    memcpy(&(apd_name2[8]), local_stoken_p, 8);

    iean4dl(IEANT_SYSTEM_LEVEL, apd_name2, &ieant_rc);

    // ---------------------------------------------------------------
    // Delete the address space level name token.  Everyone should
    // have this.
    // ---------------------------------------------------------------
    iean4dl(IEANT_HOME_LEVEL, apd_name, &ieant_rc);
}

void destroyAngelProcessData(angel_process_data* apd_p)
{
    bbgzsgoo* sgoo_p = apd_p->bbgzsgoo_p;

    // ---------------------------------------------------------------
    // Clean up the heap anchored in the angel process data.
    // ---------------------------------------------------------------
    // TODO: Destroy the heap.  Be careful here because this method is
    //       called in both the IPT task termination RESMGR and the
    //       address space RESMGR.  And we can only clean up the heap
    //       in task termination since address space termination runs
    //       in the MASTER address space.

    // ---------------------------------------------------------------
    // Finally, free the angel process data storage.
    // ---------------------------------------------------------------
    freeCellPoolCell(sgoo_p->bbgzsgoo_angel_process_data_cellpool_id, apd_p);

}


angel_process_data* getAngelProcessDataByStoken(void* stoken_p)
{
    angel_process_data* apd_p = NULL;

    // ---------------------------------------------------------------
    // Fill in the name token for lookup by stoken.
    // ---------------------------------------------------------------
    char apd_name[16];
    char apd_token[16];

    memset(apd_name, 0, sizeof(apd_name));
    memcpy(apd_name, ANGEL_PROCESS_DATA_TOKEN_NAME, strlen(ANGEL_PROCESS_DATA_TOKEN_NAME));
    memcpy(&(apd_name[8]), stoken_p, 8);

    int apd_name_token_rc;
    iean4rt(IEANT_SYSTEM_LEVEL,
            apd_name,
            apd_token,
            &apd_name_token_rc);

    if (apd_name_token_rc == 0) {
        memcpy(&apd_p, apd_token, sizeof(apd_p));
    }

    return apd_p;
}

// Increments the client bind count.
int incrementBindCount(angel_process_data* apd_p, SToken* stoken_p) {
    int rc = -1;
    unsigned char errorCase = FALSE;

    AngelClientBindCount_t oldCount, newCount;
    memcpy(&oldCount, &(apd_p->clientBindArea), sizeof(oldCount));
    while ((rc != 0) && (errorCase == FALSE)) {
        if ((oldCount.noMoreBinds != 0) ||
            (memcmp(&(oldCount.serverStoken), stoken_p, sizeof(oldCount.serverStoken)) != 0)) {
            errorCase = TRUE;
        } else {
            memcpy(&newCount, &oldCount, sizeof(newCount));
            newCount.count = oldCount.count + 1;
            rc = __cdsg(&oldCount, &(apd_p->clientBindArea), &newCount);
        }
    }

    return rc;
}

// Cleans up the client function module.
void cleanupSCFM(angel_process_data* apd_p) {
    // -----------------------------------------------------------------------
    // Since this function has recovery, and is called from both the client and
    // server ARR (which have different recovery area mappings), we'll make
    // our own ESTAE.
    // -----------------------------------------------------------------------
    int estaeRC, estaeRSN;
    retry_parms scfmRetryParms;
    memset(&scfmRetryParms, 0, sizeof(retry_parms));
    scfmRetryParms.setrp_opts.nodump = 1;

    volatile unsigned char triedToUnload = FALSE;
    volatile unsigned char triedToStorageRelease = FALSE;

    establish_estaex_with_retry(&scfmRetryParms, &estaeRC, &estaeRSN);
    if (estaeRC == 0) {
        SET_RETRY_POINT(scfmRetryParms);
        if (triedToUnload == FALSE) {
            triedToUnload = TRUE;
            loadhfs_details scfmDetails;
            scfmDetails.mod_len = apd_p->scfmModuleLength;
            scfmDetails.mod_p = apd_p->scfmModule_p;
            scfmDetails.entry_p = NULL;
            memcpy(scfmDetails.delete_token, apd_p->scfmModuleDeleteToken, sizeof(scfmDetails.delete_token));
            unload_from_hfs(&scfmDetails);
        }

        SET_RETRY_POINT(scfmRetryParms);
        if (triedToStorageRelease == FALSE) {
            triedToStorageRelease = TRUE;
            if (apd_p->scfm_function_table_p != NULL) {
                storageRelease(apd_p->scfm_function_table_p,
                               apd_p->scfm_function_table_size,
                               LOCAL_SCFM_SUBPOOL,
                               LOCAL_SCFM_KEY);
            }
        }

        remove_estaex(&estaeRC, &estaeRSN);
    }
}

// Decrement the client bind count.  Clean up if necessary.
int decrementBindCount(angel_process_data* apd_p, SToken* stoken_p) {
    int rc = -1;
    unsigned char errorCase = FALSE;

    AngelClientBindCount_t oldCount, newCount;
    memcpy(&oldCount, &(apd_p->clientBindArea), sizeof(oldCount));
    while ((rc != 0) && (errorCase == FALSE)) {
        if (memcmp(&(oldCount.serverStoken), stoken_p, sizeof(oldCount.serverStoken)) != 0) {
            errorCase = TRUE;
        } else {
            memcpy(&newCount, &oldCount, sizeof(newCount));
            newCount.count = oldCount.count - 1;
            rc = __cdsg(&oldCount, &(apd_p->clientBindArea), &newCount);
        }
    }

    // -----------------------------------------------------------------------
    // If we decremented, see if we need to clean up the process data.
    // We are responsible for setting the all clients unbound flag, because
    // the no more binds bit was set when we decremented the count to zero.
    // -----------------------------------------------------------------------
    if ((rc == 0) && (newCount.count == 0) && (newCount.noMoreBinds == 1)) {
        AngelProcessInvokeCount_t oldInvokeCount, newInvokeCount;
        memcpy(&oldInvokeCount, &(apd_p->invokecount), sizeof(oldInvokeCount));
        int invokeCountRC = -1;
        while (invokeCountRC != 0) {
            memcpy(&newInvokeCount, &oldInvokeCount, sizeof(newInvokeCount));
            newInvokeCount.allClientsUnbound = 1;
            invokeCountRC = __cs1(&oldInvokeCount, &(apd_p->invokecount), &newInvokeCount);
        }

        // -------------------------------------------------------------------
        // The caller who sets the 'allClientsUnbound' flag must free the
        // SCFM.
        // -------------------------------------------------------------------
        cleanupSCFM(apd_p);

        // -------------------------------------------------------------------
        // If we were the ones who set the last bit for cleanup, we need to
        // destroy the angel process data if the RESMGR has already run.
        // -------------------------------------------------------------------
        if (newInvokeCount.iptOrAsResmgrFinished == 1) {
            destroyAngelProcessData(apd_p);
        }
    }

    return rc;
}
#endif

angel_process_data* getAngelProcessDataFromNameToken(void)
{
    angel_process_data* apd_p = NULL;

    // ---------------------------------------------------------------
    // Find the angel process data using the name token.
    // ---------------------------------------------------------------
    char apd_name[16];
    char apd_token[16];

    memset(apd_name, 0, sizeof(apd_name));
    memcpy(apd_name, ANGEL_PROCESS_DATA_TOKEN_NAME, strlen(ANGEL_PROCESS_DATA_TOKEN_NAME));
    int apd_name_token_rc;

    int level = IEANT_HOMEAUTH_LEVEL;

    iean4rt(level,
            apd_name,
            apd_token,
            &apd_name_token_rc);

    // -----------------------------------------------------------------------
    // If the name token exists, we can get the angel process data from it.
    // Otherwise, no luck.
    // -----------------------------------------------------------------------
    if (apd_name_token_rc == 0) {
        memcpy(&apd_p, apd_token, sizeof(apd_p));
    }

    return apd_p;
}

