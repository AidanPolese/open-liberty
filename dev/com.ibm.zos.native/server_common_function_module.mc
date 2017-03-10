/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2012
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
#include <metal.h>
#include <stdlib.h>
#include <string.h>

#include "include/bpx_ipt.h"
#include "include/heap_management.h"
#include "include/ieantc.h"
#include "include/mvs_cell_pool_services.h"
#include "include/mvs_iarv64.h"
#include "include/mvs_storage.h"
#include "include/mvs_user_token_manager.h"
#include "include/mvs_utils.h"
#include "include/server_common_function_module.h"
#include "include/server_local_comm_cleanup.h"
#include "include/server_task_data.h"
#include "include/server_wola_shared_memory_anchor.h"

#include "include/common_defines.h"

/** Mapping of the name for the process data name token. */
typedef struct {
    char name[8];
    unsigned long long uniqueToken;
} ServerCommonFunctionModuleProcessDataName_t;

/** Mapping of the token for the process data name token. */
typedef struct {
    ServerCommonFunctionModuleProcessData_t* processData_p;
    unsigned char _available[8];
} ServerCommonFunctionModuleProcessDataToken_t;

/** Constant string used in the metal C env name. */
#define SCFMPD_NAME_STRING "BBGZCMPD"

/** Constant describing the subpool to get the metal C env parms from. */
#define SERVER_COMMON_AUTHORIZED_CSYSENV_SUBPOOL 249

/** Constant describing the key to get the metal C env parms in. */
#define SERVER_COMMON_AUTHORIZED_CSYSENV_KEY 2

/**
 * Create process level data structure for code running under this server common
 * function module, and store the metal C environment for future use.
 *
 * @param clientToken The token used as part of the name token caching this
 *                    process data.
 * @param envParms_p A pointer to the parameters used to create the metal C
 *                   environment.
 * @param env_p A pointer to the metal C environment.
 *
 * @return 0 on success.  -1 if storage could not be obtained.  Other return
 *         code is return code from IEAN4CR service.
 */
static int createServerCommonFunctionModuleProcessData(unsigned long long clientToken, struct __csysenv_s* envParms_p, struct __csysenvtoken_s* env_p) {
    int rc = -1;

    // -----------------------------------------------------------
    // Figure out which TToken should own storage used for the
    // dynamic area.  If we have an IPT, we want to use that, but
    // if we don't, we should use the jobstep task.
    // -----------------------------------------------------------
    TToken storageOwner;
    ascb* ascb_p = ((psa*)0)->psaaold;
    tcb* ipt_p = getIPTandVerifyCallerIsRelated();
    tcb* storageOwnerTCB_p = (ipt_p != NULL) ? ipt_p : ascb_p->ascbxtcb;
    memcpy(&storageOwner, ((stcb*)(storageOwnerTCB_p->tcbstcb))->stcbttkn, sizeof(storageOwner));

    // -----------------------------------------------------------
    // Create the process data control block.  The metal C env is
    // already set up on this thread, so use malloc().
    // -----------------------------------------------------------
    ServerCommonFunctionModuleProcessData_t* processData_p =
        malloc(sizeof(ServerCommonFunctionModuleProcessData_t));

    if (processData_p != NULL) {
        // -----------------------------------------------------------
        // Fill it in.
        // -----------------------------------------------------------
        memset(processData_p, 0, sizeof(*processData_p));
        memcpy(processData_p->eyecatcher, SCFMPD_NAME_STRING,
               sizeof(processData_p->eyecatcher));
        processData_p->version = 1;
        processData_p->length = sizeof(*processData_p);
        processData_p->auth_metalc_env_parms_p = envParms_p;
        processData_p->auth_metalc_env_p = env_p;
        processData_p->clientProcessDataToken = clientToken;
        processData_p->clientDynAreaPool = createClientDynamicAreaCellPool(&(processData_p->clientDynAreaPoolInfo),
                                                                           NULL,
                                                                           &storageOwner);

        if (processData_p->clientDynAreaPool != 0L) {
            // -----------------------------------------------------------
            // Make a name token to keep the address of the process data
            // -----------------------------------------------------------
            ServerCommonFunctionModuleProcessDataName_t name;
            ServerCommonFunctionModuleProcessDataToken_t token;

            memcpy(name.name, SCFMPD_NAME_STRING, strlen(SCFMPD_NAME_STRING));
            name.uniqueToken = clientToken;

            token.processData_p = processData_p;
            memset(token._available, 0, sizeof(token._available));

            iean4cr(IEANT_HOME_LEVEL,
                    (char*)&name,
                    (char*)&token,
                    IEANT_NOPERSIST,
                    &rc);
        } else {
            free(processData_p);
            processData_p = NULL;
        }
    }

    return rc;
}

/**
 * Retrieve the process data for this common function module, from the name token.
 *
 * @param clientToken The token used to create the process data.
 *
 * @return A pointer to the process data.
 */
static ServerCommonFunctionModuleProcessData_t* getServerCommonFunctionModuleProcessDataFromNameToken(unsigned long long clientToken) {
    ServerCommonFunctionModuleProcessData_t* processData_p = NULL;
    ServerCommonFunctionModuleProcessDataName_t name;
    ServerCommonFunctionModuleProcessDataToken_t token;

    memcpy(name.name, SCFMPD_NAME_STRING, strlen(SCFMPD_NAME_STRING));
    name.uniqueToken = clientToken;

    int name_token_rc = 0;

    iean4rt(IEANT_HOMEAUTH_LEVEL,
            (char*)&name,
            (char*)&token,
            &name_token_rc);

    // ---------------------------------------------------------------
    // If the name token exists, we can get the process data from it.
    // ---------------------------------------------------------------
    if (name_token_rc == 0) {
        processData_p = token.processData_p;
    }

    return processData_p;
}

/**
 * Remove the common function module process data name token.
 */
static void removeServerCommonFunctionModuleProcessDataNameToken(ServerCommonFunctionModuleProcessData_t* processData_p) {
    ServerCommonFunctionModuleProcessDataName_t name;
    unsigned long long clientToken = processData_p->clientProcessDataToken;

    memcpy(name.name, SCFMPD_NAME_STRING, strlen(SCFMPD_NAME_STRING));
    name.uniqueToken = clientToken;

    int ieant_rc = 0;
    iean4dl(IEANT_HOME_LEVEL, (char*)&name, &ieant_rc);
}

/**
 * This is the method that gets called when the bind is first established
 * between the client and a server.  This code runs in the client address space.
 * It is responsible for setting up the process data and the metal C environment
 * that tasks will use during invoke processing.
 *
 * @param token An instance token which must be used when this module creates
 *              process level data (ie. a name token).  This is required
 *              because a client can connect to many server at many different
 *              levels of code, and they must be independent.
 *
 * @return 0 on success, nonzero on failure.
 */
#pragma prolog(commonServerAuthorizedProcessInitialization, " SAUTHPRL CLIENT=YES")
#pragma epilog(commonServerAuthorizedProcessInitialization, " SAUTHEPL CLIENT=YES")
static int commonServerAuthorizedProcessInitialization(unsigned long long token) {
    int init_rc = 0;

    // -----------------------------------------------------------------------
    // Create the metal C environment that we'll use in this process.
    // -----------------------------------------------------------------------
    struct __csysenv_s* auth_csysenv_p = NULL;
    long long usertoken = getAddressSpaceSupervisorStateUserToken();

    int storage_obtain_rc = 0;
    auth_csysenv_p = storageObtain(sizeof(*auth_csysenv_p),
                                   SERVER_COMMON_AUTHORIZED_CSYSENV_SUBPOOL,
                                   SERVER_COMMON_AUTHORIZED_CSYSENV_KEY,
                                   &storage_obtain_rc);

    if (auth_csysenv_p != NULL) {
        // -------------------------------------------------------------------
        // Create the heap for authorized callers.
        // -------------------------------------------------------------------
        unsigned long long belowSizes[4] = BBGZ_HEAP_DEFAULT_POOL_SIZES;
        unsigned long long aboveSizes[4] = BBGZ_HEAP_DEFAULT_POOL_SIZES;
        void* heapAnchor_p = buildHeap(BBGZ_HEAP_SERVER_CLIENT_AUTH, belowSizes, aboveSizes, NULL, FALSE, FALSE);
        if (heapAnchor_p != NULL) {
            struct __csysenvtoken_s* auth_cenv_p = (struct __csysenvtoken_s*) initenv(auth_csysenv_p, usertoken, heapAnchor_p);

            // ---------------------------------------------------------------
            // Make sure the process data structure has been created.
            // ---------------------------------------------------------------
            if (createServerCommonFunctionModuleProcessData(token, auth_csysenv_p, auth_cenv_p) != 0) {
                termenv();
                storageRelease(auth_csysenv_p, sizeof(*auth_csysenv_p),
                               SERVER_COMMON_AUTHORIZED_CSYSENV_SUBPOOL,
                               SERVER_COMMON_AUTHORIZED_CSYSENV_KEY);
                init_rc = -1;
            }
        } else {
            storageRelease(auth_csysenv_p, sizeof(*auth_csysenv_p),
                           SERVER_COMMON_AUTHORIZED_CSYSENV_SUBPOOL,
                           SERVER_COMMON_AUTHORIZED_CSYSENV_KEY);
            init_rc = -2;
        }
    } else {
        init_rc = -3;
    }

    return init_rc;
}

/**
 * This is the client invoke tie, called by the angel to process a client invoke
 * call.  The tie is responsible for setting up the environment for the invoke
 * call, and calling the invokable service.
 *
 * @param fcn_ptr A pointer to the function to invoke.  The angel extracts this
 *                pointer from the server common function module (SCFM)
 *                control block.
 * @param struct_p The parameter data for the routine to be invoked.
 * @param token An instance token which must be used when this module creates
 *              or retrieves any process level data (ie. a name token).  This is
 *              required because a client can connect to many server at many
 *              different levels of code, and they must be independent.
 */
#pragma prolog(commonInvokeService," SAUTHPRL CLIENT=YES,TOKENPRM=3")
#pragma epilog(commonInvokeService," SAUTHEPL CLIENT=YES")
static void commonInvokeService(void (* fcn_ptr)(void*), void* struct_p, unsigned long long token) {
    struct __csysenvtoken_s* env_p = NULL;

    // -----------------------------------------------------------------------
    // Grab the metal C environment.
    // -----------------------------------------------------------------------
    ServerCommonFunctionModuleProcessData_t* processData_p =
        getServerCommonFunctionModuleProcessDataFromNameToken(token);

    if (processData_p != NULL) {
        server_task_data* std_p = getServerTaskData();
        std_p->scfmProcessData_p = processData_p;

        env_p = processData_p->auth_metalc_env_p;
        setenvintoR12(env_p);
        fcn_ptr(struct_p);

        // -------------------------------------------------------------------
        // See if the dynamic area cell pool needs to be expanded.  The entry
        // linkage will have set a bit in the server task data if this is
        // necessary.
        // -------------------------------------------------------------------
        if (std_p->expandDynAreaCellPool != 0) {
            growClientDynamicAreaCellPool(&(processData_p->clientDynAreaPoolInfo));
            std_p->expandDynAreaCellPool = 0;
        }

        setenvintoR12(NULL);

        std_p->scfmProcessData_p = NULL;
    }
}

/**
 * This is the cleanup routine, called when the last unbind between the client
 * and server is broken, or when the server is going away and there are no
 * client binds left to this server.
 *
 * Note that there is no guarantee that this method will be driven to clean up
 * when the server comes down.  It is expected that any cleanup would happen
 * when the last client bind is removed (even if the server is still up), since
 * this method will be invoked when the last client bind is removed.
 *
 * @param token An instance token which must be used when this module creates
 *              or retrieves any process level data (ie. a name token).  This is
 *              required because a client can connect to many server at many
 *              different levels of code, and they must be independent.  In the
 *              cleanup routine, any data created with this token should be
 *              destroyed.
 */
#pragma prolog(commonServerAuthorizedProcessCleanup," SAUTHPRL CLIENT=YES")
#pragma epilog(commonServerAuthorizedProcessCleanup," SAUTHEPL CLIENT=YES")
static void commonServerAuthorizedProcessCleanup(unsigned long long token) {
    // -----------------------------------------------------------------------
    // Destroy the cached process data.
    // -----------------------------------------------------------------------
    struct __csysenv_s* auth_csysenv_p = NULL;
    struct __csysenvtoken_s* env_p = NULL;

    ServerCommonFunctionModuleProcessData_t* processData_p =
        getServerCommonFunctionModuleProcessDataFromNameToken(token);

    // If there is no name token, chances are that we are in an address
    // space level RESMGR running in MASTER.  Most of the storage that
    // we would free is already gone.  Only in rare circumstances
    // would we drive a cleanup routine in this case.
    if (processData_p != NULL) {
        removeServerCommonFunctionModuleProcessDataNameToken(processData_p);
        env_p = processData_p->auth_metalc_env_p;
        auth_csysenv_p = processData_p->auth_metalc_env_parms_p;

        if (env_p != NULL) {
            setenvintoR12(env_p);
            server_task_data* std_p = getServerTaskData();
            std_p->scfmProcessData_p = processData_p;

            // Callouts to cleanup routines go here.
            cleanupClientWolaSharedMemoryAttachments();

            // Cleanup related local comm resources
            cleanupClient_ClientTerm(token);

            // Cleanup the process data and metal C environment.
            if (processData_p->clientDynAreaPool != 0L) {
              destroyClientDynamicAreaCellPool(&(processData_p->clientDynAreaPoolInfo));
              processData_p->clientDynAreaPool = 0L;
            }

            free(processData_p);
            termenv();
            setenvintoR12(NULL);
        }

        if (auth_csysenv_p != NULL) {
            storageRelease(auth_csysenv_p,
                           sizeof(*auth_csysenv_p),
                           SERVER_COMMON_AUTHORIZED_CSYSENV_SUBPOOL,
                           SERVER_COMMON_AUTHORIZED_CSYSENV_KEY);
        }
    } else {
        // Cleanup routines for non-process-specific cleanup should be
        // invoked here, after careful thought is given to why this
        // would be necessary.
    }
}


const struct bbgzscfm BBGZSCFM = {
  .header = {
    .module_name    = "BBGZSCFM",
    .eyecatcher     = BBGZASVT_EYE,
    .version        = BBGZASVT_VERSION_2,
    .flags.generateClientToken = 1,
    .flags._available = 0,
    .size           = sizeof(BBGZSCFM) - sizeof(BBGZSCFM.header.module_name),
    .num_entries    = (sizeof(BBGZSCFM) - sizeof(BBGZSCFM.header) - sizeof(BBGZSCFM.end_eyecatcher)) / sizeof(bbgzasve),
    .version_string = BUILD_LEVEL,
    .process_initialization_routine_ptr = commonServerAuthorizedProcessInitialization,
    .setupEnvironmentAndCallInvokableService = commonInvokeService,
    .process_cleanup_routine_ptr = commonServerAuthorizedProcessCleanup,
    .task_cleanup_routine_ptr = NULL
  },
#define COMMON_DEF(svc_name, auth_name, impl_name, arg_type) \
  .##impl_name = {                                                      \
    .bbgzasve_name      = svc_name,                       /* +0x00 */   \
    .bbgzasve_auth_name = auth_name,                      /* +0x08 */   \
    .bbgzasve_fcn_ptr   = (void(* const)())impl_name,     /* +0x10 */   \
    .bbgzasve_err_ptr   = 0,                              /* +0x18 */   \
    .bbgzasve_loadmod_bits._available = 0,                /* +0x20 */   \
    .bbgzasve_runtime_bits_zeros = 0,                     /* +0x22 */   \
    .bbgzasve_rsvd1     = ""                              /* +0x24 */   \
  },                                                      /* +0x30 */
#include "include/server_common_functions.def"
#undef COMMON_DEF
  .end_eyecatcher = BBGZASVT_EYE_END
};

#pragma insert_asm(" IEANTASM")
