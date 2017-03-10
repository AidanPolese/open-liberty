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
#include <stdlib.h>

#include "include/server_authorized_function_module.h"

#include "include/common_defines.h"
#include "include/common_task_data_anchor.h"
#include "include/mvs_iarv64.h"
#include "include/mvs_storage.h"
#include "include/mvs_user_token_manager.h"
#include "include/mvs_utils.h"
#include "include/server_local_comm_cleanup.h"
#include "include/server_process_data.h"
#include "include/server_task_data.h"
#include "include/server_wola_registration_server.h"
#include "include/server_wola_unauth_services.h"
#include "include/tx_authorized_rrs_services.h"

#include "include/gen/ihapsa.h"
#include "include/gen/ihastcb.h"
#include "include/gen/ikjtcb.h"

#define SERVER_AUTHORIZED_CSYSENV_SUBPOOL 249
#define SERVER_AUTHORIZED_CSYSENV_KEY 2

extern int disableTraceLevelInServerTaskData(void);

/**
 * This is the method the angel will call during registration processing.
 * It is responsible for setting up the process data and the metal C environment
 * that tasks will use during invoke processing.
 *
 * This routine can be called more than once if the server deregisters and then
 * registers again.
 *
 * @param token IGNORE
 *
 * @return 0 on success, nonzero on failure.
 */
#pragma prolog(serverAuthorizedProcessInitialization, "SAUTHPRL")
#pragma epilog(serverAuthorizedProcessInitialization, "SAUTHEPL")
int serverAuthorizedProcessInitialization(unsigned long long token) {
    int init_rc = 0;
    server_process_data* spd_p = NULL;

    // -----------------------------------------------------------------------
    // Create the metal C environment that we'll use in this process.
    // -----------------------------------------------------------------------
    struct __csysenv_s* auth_csysenv_p = NULL;
    long long usertoken = getAddressSpaceSupervisorStateUserToken();

    int storage_obtain_rc = 0;
    auth_csysenv_p = storageObtain(sizeof(*auth_csysenv_p),
                                   SERVER_AUTHORIZED_CSYSENV_SUBPOOL,
                                   SERVER_AUTHORIZED_CSYSENV_KEY,
                                   &storage_obtain_rc);

    if (auth_csysenv_p != NULL) {
        // -------------------------------------------------------------------
        // Create the heap for authorized callers.
        // -------------------------------------------------------------------
        unsigned long long belowSizes[4] = BBGZ_HEAP_DEFAULT_POOL_SIZES;
        unsigned long long aboveSizes[4] = BBGZ_HEAP_DEFAULT_POOL_SIZES;
        getTaskControlBlockForTCB_t* getTaskDataFcn_p = (getTaskControlBlockForTCB_t*) getServerTaskDataFromAlternateTCB;
        void* heapAnchor_p = buildHeap(BBGZ_HEAP_SERVER_AUTH, belowSizes, aboveSizes, getTaskDataFcn_p, TRUE, FALSE);
        if (heapAnchor_p != NULL) {
            void* auth_cenv_p = initenv(auth_csysenv_p, usertoken, heapAnchor_p);

            // -------------------------------------------------------------------
            // Make sure the process data structure has been created.
            // -------------------------------------------------------------------
            spd_p = createServerProcessData(auth_csysenv_p, auth_cenv_p);

            /*-------------------------------------------------------------------*/
            /* Free the metal C environment if it's not cached in the process    */
            /* data.  Remember, the heap we created is attached to the metal C   */
            /* environment, and gets cleaned up in termenv().                    */
            /*-------------------------------------------------------------------*/
            if ((spd_p == NULL) || (spd_p->auth_metalc_env_p != auth_cenv_p)) {
                termenv();
                storageRelease(auth_csysenv_p, sizeof(*auth_csysenv_p),
                               SERVER_AUTHORIZED_CSYSENV_SUBPOOL,
                               SERVER_AUTHORIZED_CSYSENV_KEY);
            }
        } else {
            storageRelease(auth_csysenv_p, sizeof(*auth_csysenv_p),
                           SERVER_AUTHORIZED_CSYSENV_SUBPOOL,
                           SERVER_AUTHORIZED_CSYSENV_KEY);
        }
    }

    if (spd_p == NULL) init_rc = -1;

    return init_rc;
}

/**
 * This is the invoke tie, called by the angel to process an invoke call.  The
 * tie is responsible for setting up the environment for the invoke call, and
 * calling the invokable service.
 *
 * @param fcn_ptr A pointer to the function to invoke.  The angel extracts this
 *                pointer from the server authorized function module (SAFM)
 *                control block.
 * @param struct_p The parameter data for the routine to be invoked.
 * @param token IGNORE
 */
#pragma prolog(invokeService,"SAUTHPRL")
#pragma epilog(invokeService,"SAUTHEPL")
void invokeService(void (* fcn_ptr)(void*), void* struct_p, unsigned long long token) {
    // -----------------------------------------------------------------------
    // Finish initializing the task data if necessary.
    // -----------------------------------------------------------------------
    if (isServerTaskDataSetInTrustedChain() == FALSE) {
        server_task_data* std_p = getServerTaskData();
        if (std_p->spd_p == NULL) {
            server_process_data* spd_p = getServerProcessData();
            std_p->spd_p = spd_p;
        }

        setServerTaskDataIntoTrustedChain();
    }

    // -----------------------------------------------------------------------
    // Attach our metal C environment.
    // -----------------------------------------------------------------------
    server_task_data* std_p = getServerTaskData();
    server_process_data* spd_p = std_p->spd_p;
    setenvintoR12(spd_p->auth_metalc_env_p);

    // -----------------------------------------------------------------------
    // Call the requested service.
    // -----------------------------------------------------------------------
    fcn_ptr(struct_p);

    // -----------------------------------------------------------------------
    // Get out.
    // -----------------------------------------------------------------------
    setenvintoR12(NULL);
}

/**
 * This is the cleanup routine, called when BBGZSAFM is about to be deleted.
 * We can clean up any process related 'stuff' here.
 *
 * @param token IGNORE
 */
#pragma prolog(serverAuthorizedProcessCleanup,"SAUTHPRL")
#pragma epilog(serverAuthorizedProcessCleanup,"SAUTHEPL")
void serverAuthorizedProcessCleanup(unsigned long long token) {
    // -----------------------------------------------------------------------
    // Destroy the server process data, the heap and the metal C environment.
    // -----------------------------------------------------------------------
    server_process_data* spd_p = getServerProcessData();
    if (spd_p != NULL) {
        if (spd_p->auth_metalc_env_p != NULL) {
            // Make our metal C environment available to cleanup routines
            struct __csysenv_s* cenvParms_p = spd_p->auth_metalc_env_parms_p;
            void* cenv_p = spd_p->auth_metalc_env_p;
            setenvintoR12(cenv_p);

            // Disable native tracing for this thread.
            disableTraceLevelInServerTaskData();

            // Local Comm cleanup: run the client cleanup queue
            cleanupClients_ServerGone(spd_p);

            // Unhook the WOLA RGE from the server process data, if it wasn't done already.
            removeBboargeFromSpd(spd_p);

            // Call OTMA close on any OMTA anchors in the server process data
            cleanupOTMAAnchors(spd_p);

            // Perform RRS cleanup.
            cleanupRRS(spd_p);

            destroyServerProcessData(spd_p);

            // Destroy our metal C environment
            termenv();
            setenvintoR12(NULL);

            storageRelease(cenvParms_p,
                           sizeof(*cenvParms_p),
                           SERVER_AUTHORIZED_CSYSENV_SUBPOOL,
                           SERVER_AUTHORIZED_CSYSENV_KEY);
        }
    }
}

/**
 * This is the task cleanup routine.
 *
 * @param tcb_p A pointer to the TCB for the task being cleaned up.  If NULL,
 *              the current task is being cleaned up.  If non-NULL, the local
 *              lock is held and this parameter points to the TCB for the task
 *              which is being cleaned up.
 */
#pragma prolog(serverAuthorizedTaskCleanup," SAUTHPRL ABENDINU=NO")
#pragma epilog(serverAuthorizedTaskCleanup," SAUTHEPL ABENDINU=NO")
void serverAuthorizedTaskCleanup(void* tcb_p) {
    // -----------------------------------------------------------------------
    // Look for our server process data off of the server task data.  If this
    // task did anything interesting, it will have process data hung off the
    // task data.
    // -----------------------------------------------------------------------
    server_task_data* std_p = (tcb_p == NULL) ? getServerTaskData() : getServerTaskDataFromAlternateTCB(tcb_p);
    server_process_data* spd_p = (std_p != NULL) ? std_p->spd_p : NULL;
    if (spd_p != NULL) {
        // -------------------------------------------------------------------
        // Attach our metal C environment.
        // -------------------------------------------------------------------
        setenvintoR12(spd_p->auth_metalc_env_p);

        // -------------------------------------------------------------------
        //   ** Important **
        // When we are called for another task (tcb_p is non-NULL), we're
        // cleaning up someone else's server task data.  If that task ends,
        // the task data may get freed out from underneath us.  We're relying
        // on the fact that the caller has an ESTAE set up and can retry if we
        // die a horrible death in here.  We can't set up our own ESTAE because
        // we would need to get below-the-bar storage, which we can't get
        // since we hold the local lock when tcb_p is non-NULL.
        // --------------------------------------------------------------------

        // --------------------------------------------------------------------
        // Clean up related to a hard server failure (ex., "kill -9")...some
        // failure that prevents normal server shutdown.  The targeted cleanup
        // is any cleanup needed to allow the server to terminate.
        // --------------------------------------------------------------------
        if (((std_p->taskFlags & taskFlags_cleanupForHardFailure) == taskFlags_cleanupForHardFailure)) {
            kernel_cleanupForHardFailure();
        }

        // --------------------------------------------------------------------
        // Clean up the task level heap pools.
        // --------------------------------------------------------------------
        struct __csysenvtoken_s* cenv_p = (struct __csysenvtoken_s*)(spd_p->auth_metalc_env_p);
        if (cenv_p != NULL) {
            void* heapAnchor_p = (void*)(cenv_p->__csetheapuserdata);
            if (heapAnchor_p != NULL) {
                taskLevelHeapCleanup(heapAnchor_p, tcb_p);
            }
        }

        // -------------------------------------------------------------------
        // If we're cleaning up someone else's task, free their server task
        // data.
        // -------------------------------------------------------------------
        if (tcb_p != NULL) {
            stcb* stcb_p = (stcb*)(((tcb*)tcb_p)->tcbstcb);

            // ---------------------------------------------------------------
            // If it happens that the other task is ours, we will be freeing
            // the task data in the exit linkage.
            // ---------------------------------------------------------------
            if (stcb_p != NULL) {
                if (tcb_p != (((psa*)0)->psatold)) {
                    TToken* ttoken_p = (TToken*)(stcb_p->stcbttkn);
                    release_iarv64(std_p, ttoken_p, NULL, NULL);
                }

                common_task_data_anchor* ctda_p = (common_task_data_anchor*)stcb_p->stcbbcba;
                if (ctda_p != NULL) {
                    ctda_p->server_task_data_p = NULL;
                }
            }
        }

        // -------------------------------------------------------------------
        // Get out.
        // -------------------------------------------------------------------
        setenvintoR12(NULL);
    }
}

const struct bbgzsafm BBGZSAFM = {
  .header = {
    .module_name    = "BBGZSAFM",
    .eyecatcher     = BBGZASVT_EYE,
    .version        = BBGZASVT_VERSION_1,
    .flags.generateClientToken = 0,
    .flags._available = 0,
    .size           = sizeof(BBGZSAFM) - sizeof(BBGZSAFM.header.module_name),
    .num_entries    = (sizeof(BBGZSAFM) - sizeof(BBGZSAFM.header) - sizeof(BBGZSAFM.end_eyecatcher)) / sizeof(bbgzasve),
    .version_string = BUILD_LEVEL,
    .process_initialization_routine_ptr = serverAuthorizedProcessInitialization,
    .setupEnvironmentAndCallInvokableService = invokeService,
    .process_cleanup_routine_ptr = serverAuthorizedProcessCleanup,
    .task_cleanup_routine_ptr = serverAuthorizedTaskCleanup
  },
#define AUTH_DEF(svc_name, auth_name, impl_name, arg_type) \
  .##impl_name = {                                                      \
    .bbgzasve_name      = svc_name,                       /* +0x00 */   \
    .bbgzasve_auth_name = auth_name,                      /* +0x08 */   \
    .bbgzasve_fcn_ptr   = (void(* const)())impl_name,     /* +0x10 */   \
    .bbgzasve_err_ptr   = 0,                              /* +0x18 */   \
    .bbgzasve_loadmod_bits._available = 0,                /* +0x20 */   \
    .bbgzasve_runtime_bits_zeros = 0,                     /* +0x22 */   \
    .bbgzasve_rsvd1     = ""                              /* +0x24 */   \
  },                                                      /* +0x30 */
#include "include/server_authorized_functions.def"
#undef AUTH_DEF
  .end_eyecatcher = BBGZASVT_EYE_END
};

#pragma insert_asm(" IHAPSA")
#pragma insert_asm(" IKJTCB")
#pragma insert_asm(" IHASTCB")
#pragma insert_asm(" IEANTASM")
