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
#include "include/angel_server_pc_stub.h"
#include "include/bpx_ipt.h"
#include "include/ieantc.h"
#include "include/mvs_enq.h"
#include "include/mvs_storage.h"
#include "include/mvs_utils.h"
#include "include/mvs_user_token_manager.h"
#include "include/server_task_data.h"

#include "include/server_function_module_stub.h"

#include "include/gen/bpxzotcb.h"
#include "include/gen/ihapsa.h"
#include "include/gen/ihastcb.h"
#include "include/gen/ikjtcb.h"

//---------------------------------------------------------------------
// Get the angel anchor so we can get the PC number to use on invoke.
// The angel anchor is cached in the unauthorized server task data for
// performance reasons.  For the first request on each task, we look it
// up via the angel process data.
//---------------------------------------------------------------------
static int getAngelAnchorFromTaskData(AngelAnchor_t** angelAnchor_p) {
    server_task_data* std_p = getServerTaskData();
    if (std_p->checkedAngelAnchor == 0) {
        // I don't like the dependency on angel_process_data here.
        // However we're going to need it as soon as we PC to invoke so
        // if we don't have it by now, we're going to fail soon anyway.
        angel_process_data* apd_p = getAngelProcessDataFromNameToken();
        if (apd_p == NULL) {
            return -1;
        }

        bbgzsgoo* sgoo_p = apd_p->bbgzsgoo_p;
        std_p->angelAnchor_p = sgoo_p->bbgzsgoo_angelAnchor_p;
        std_p->checkedAngelAnchor = 1;
    }

    *angelAnchor_p = std_p->angelAnchor_p;
    return 0;
}

//---------------------------------------------------------------------
// Declare function prototypes for authorized stubs.  Static declarations
// prevent the entry points from being exported and provides for
// isolation.
//---------------------------------------------------------------------
#define AUTH_DEF(svc_name, auth_name, impl_name, arg_type) \
LINKAGE_STUB( impl_name ); \
static int impl_name##_stub (arg_type *p1);
#include "include/server_authorized_functions.def"
#undef AUTH_DEF


//---------------------------------------------------------------------
// Define a caller side structure that provides access to stub
// implementations with an invocation contract that is compatible with
// the target.
//---------------------------------------------------------------------
#define AUTH_DEF(svc_name, auth_name, impl_name, arg_type) .##impl_name = impl_name##_stub,
const server_authorized_function_stubs auth_stubs = {
#include "include/server_authorized_functions.def"
};
#undef AUTH_DEF


#define UNAUTH_DEF(ret_type, func_name, parms_with_type, parms) .##func_name = func_name##_stub,
const server_unauthorized_function_stubs unauth_stubs = {
    .header = NULL,
#include "include/server_unauthorized_functions.def"
    // Hard coded here by defect 61857. If we ever get a fix for BPX4IPT giving us a 144
    // byte save area, uncomment the line in server_unauthorized_functions.def and
    // delete the following line.
    .driveAuthorizedServiceOnIPTExitRoutine = driveAuthorizedServiceOnIPTExitRoutine_stub,

    .trailer = NULL
};
#undef UNAUTH_DEF

/**
 * The unauthorized function stubs.
 */
const server_function_stubs BBGZSUFM = {
    .authorized_p = &auth_stubs,
    .unauthorized_p = &unauth_stubs
};

//---------------------------------------------------------------------
// Define the stub implementations as static functions to prevent
// them from being externally resolvable entry points.
//---------------------------------------------------------------------
#define AUTH_DEF(svc_name, auth_name, impl_name, arg_type)          \
PROLOG_STUB( impl_name );                                           \
static int impl_name##_stub (arg_type *p1) {                        \
    AngelAnchor_t* angelAnchor_p = NULL;                            \
    int aa_rc = getAngelAnchorFromTaskData(&angelAnchor_p);         \
    if (aa_rc != 0) { return aa_rc; }                               \
    int offset =                                                    \
      (int)(((char*)(&(BBGZSUFM.authorized_p->##impl_name)) -       \
             (char*)(BBGZSUFM.authorized_p)) / sizeof(void*));      \
    int arg_struct_size = sizeof(arg_type);                         \
    return angel_invoke_pc_client_stub(offset, arg_struct_size, p1, angelAnchor_p);\
}
#include "include/server_authorized_functions.def"
#undef AUTH_DEF

/**
 * This is a special unauthorized stub that will be used to call a
 * routine in the authorized function table using BPX4IPT.
 *
 * @param parms_p The parameters defining which authorized service to call.
 *
 * @return The return code from the authorized PC service.  If the return code
 *         is zero, the PC instruction was executed successfully.  No guarantee
 *         is made that the authorized service compelted successfully.  A
 *         nonzero return code indicates that a problem occured before invoking
 *         the PC, or that the PC itself failed.
 */
static int driveAuthorizedServiceOnIPTExitRoutine(DriveAuthorizedServiceOnIPTParms_t* parms_p) {
    AngelAnchor_t* angelAnchor_p = NULL;
    int aa_rc = getAngelAnchorFromTaskData(&angelAnchor_p);
    if (aa_rc != 0) { return aa_rc; }
    int offset = (int)(((char*)(parms_p->authRoutine_p) - (char*)(BBGZSUFM.authorized_p)) / sizeof(void*));
    parms_p->pcReturnCode = angel_invoke_pc_client_stub(offset, parms_p->parmStructSize, parms_p->parmStruct_p, angelAnchor_p);
    return parms_p->pcReturnCode; // Need to return something from unauthorized services
}

/**
 * Structure used to store the parameters used to create the unauthorized
 * metal C environment, and to verify that it belongs to this process.
 */
typedef struct unauthorizedMCRTLParms{
    unsigned char eyecatcher[8]; //!< Eyecatcher.
    unsigned short version; //!< Control block version.
    unsigned short length; //!< Length of the control block.
    unsigned int _available1; //!< Available for use.
    void* cenv_p; //!< The metal C environment pointer.
    TToken iptTToken; //!< The TToken for the IPT of this process.
    unsigned char _available[216]; //!< Available for use.
    struct __csysenv_s mcrtlParms; //!< Unauthorized MCRTL parameters.
} UnauthorizedMCRTLParms_t;

/** Eye catcher for UnauthorizedMCRTLParms_t. */
#define UNAUTH_MCRTL_PARMS_EYE "BBGZUMRP"

//---------------------------------------------------------------------
// Utility used by the unauthorized client to connect to a key 8
// metal C environment
//---------------------------------------------------------------------
#pragma prolog(connectUnauthorizedCEnv,"R12PROL")
#pragma epilog(connectUnauthorizedCEnv,"R12EPIL")
static void* connectUnauthorizedCEnv(void) {
    void* the_env_p = NULL;

    // We should have a stack with a 64k prefix area, so get a reference to
    // that.
    server_task_data* std_p = getServerTaskData();

    // See if the metal C environment has been cached in the prefix area.
    if ((std_p->unauth_cenv_p) != NULL) {
        the_env_p = std_p->unauth_cenv_p;
    } else {
        // Not in the prefix area, so look up the name token.
        char* name = "BBGZ_KEY8_METALC";
        struct {
            unsigned char _available[8];
            UnauthorizedMCRTLParms_t* mcrtlParms_p;
        } token;
        int rc;

        iean4rt(IEANT_HOME_LEVEL, name, (char*)&token, &rc);

        if (rc == 0) {
            // Found the name token, verify that the IPT TToken is the right one.
            // If the address space is re-used, the IPT will change and we can't
            // rely on the values in the name token.
            TToken savedTToken, foundTToken;
            memcpy(&savedTToken, &(token.mcrtlParms_p->iptTToken), sizeof(savedTToken));
            int foundIPT = getIPT_TToken(&foundTToken);
            if (foundIPT == 0) {
                if (memcmp(&savedTToken, &foundTToken, sizeof(savedTToken)) == 0) {
                    the_env_p = token.mcrtlParms_p->cenv_p; // All is well.
                } else {
                    // The IPT has changed.  Need to serialize, check again,
                    // and possibly create a new metal C environment.
                    enqtoken enqToken;
                    get_enq_exclusive_step(BBGZ_UNAUTH_ENQ_QNAME, SERVER_UNAUTH_MCRTL_ENQ, &enqToken);
                    memcpy(&savedTToken, &(token.mcrtlParms_p->iptTToken), sizeof(savedTToken));
                    if (memcmp(&savedTToken, &foundTToken, sizeof(savedTToken)) == 0) {
                        the_env_p = token.mcrtlParms_p->cenv_p; // Someone else already re-connected.
                    } else {
                        // --------------------------------------------------------
                        // The IPT has changed.  Create a new metal C environment.
                        // TODO: There is a bug in the MCRTL where __cterm() calls
                        //  STORAGE RELEASE without specifying a storage key.  We
                        //  use subpool 131 which means we'll try to free storage
                        //  in key 0 and results in a B78 abend.  Instead, for now,
                        //  just skip __cterm() (really termenv) and free the heap
                        //  instead.  Someday we need to call __cterm().
                        // --------------------------------------------------------
                        //setenvintoR12(token.cenv_p);
                        //termenv();
                        //setenvintoR12(NULL);
                        struct __csysenvtoken_s* cenv_p = (struct __csysenvtoken_s*)token.mcrtlParms_p->cenv_p;
                        void* heapAnchor_p = (cenv_p != NULL) ? (void*)(cenv_p->__csetheapuserdata) : NULL;
                        if (heapAnchor_p != NULL) {
                            destroyHeap(heapAnchor_p);
                            heapAnchor_p = NULL;
                        }
                        // END of __cterm() workaround.
                        unsigned long long belowSizes[4] = BBGZ_HEAP_DEFAULT_POOL_SIZES;
                        unsigned long long aboveSizes[4] = BBGZ_HEAP_DEFAULT_POOL_SIZES;
                        heapAnchor_p = buildHeap(BBGZ_HEAP_SERVER_UNAUTH, belowSizes, aboveSizes, NULL, FALSE, FALSE);
                        if (heapAnchor_p != NULL) {
                            token.mcrtlParms_p->cenv_p = initenv(&(token.mcrtlParms_p->mcrtlParms), getAddressSpaceProblemStateUserToken(), heapAnchor_p);
                            memcpy(&(token.mcrtlParms_p->iptTToken), &foundTToken, sizeof(foundTToken));
                            the_env_p = token.mcrtlParms_p->cenv_p;
                        }
                    }
                    release_enq(&enqToken);
                }
            }
        } else {
            // No name token, create a C environment and set it into the token
            unsigned long long belowSizes[4] = BBGZ_HEAP_DEFAULT_POOL_SIZES;
            unsigned long long aboveSizes[4] = BBGZ_HEAP_DEFAULT_POOL_SIZES;
            void* heapAnchor_p = buildHeap(BBGZ_HEAP_SERVER_UNAUTH, belowSizes, aboveSizes, NULL, -1, -1);
            if (heapAnchor_p != NULL) {
                token.mcrtlParms_p = storageObtain(sizeof(*token.mcrtlParms_p),
                                                   131, /* Subpool */
                                                   8,   /* Key     */
                                                   NULL);

                if (token.mcrtlParms_p != NULL) {
                    memset(token.mcrtlParms_p, 0, sizeof(*token.mcrtlParms_p));
                    memcpy(token.mcrtlParms_p->eyecatcher, UNAUTH_MCRTL_PARMS_EYE, sizeof(token.mcrtlParms_p->eyecatcher));
                    token.mcrtlParms_p->version = 0;
                    token.mcrtlParms_p->length = sizeof(*token.mcrtlParms_p);
                    int foundIPT = getIPT_TToken(&(token.mcrtlParms_p->iptTToken));
                    if (foundIPT == 0) {
                        token.mcrtlParms_p->cenv_p = initenv(&(token.mcrtlParms_p->mcrtlParms), getAddressSpaceProblemStateUserToken(), heapAnchor_p);
                        if (token.mcrtlParms_p->cenv_p != NULL) {
                            // Try to create the name token
                            memset(token._available, 0, sizeof(token._available));
                            iean4cr(IEANT_HOME_LEVEL, name, (char*)&token, IEANT_NOPERSIST, &rc);
                            if (rc == 0) {
                                // Created the name token, save the resulting C environment
                                the_env_p = token.mcrtlParms_p->cenv_p;
                            } else {
                                // Someone else created the name token, release storage and
                                // try to look up the one the other guy created.
                                storageRelease(token.mcrtlParms_p, sizeof(*token.mcrtlParms_p), 131, 8);
                                destroyHeap(heapAnchor_p);
                                iean4rt(IEANT_HOME_LEVEL, name, (char*)&token, &rc);
                                if (rc == 0) {
                                    the_env_p = token.mcrtlParms_p->cenv_p;
                                }
                            }
                        } else {
                            storageRelease(token.mcrtlParms_p, sizeof(*token.mcrtlParms_p), 131, 8);
                            destroyHeap(heapAnchor_p);
                        }
                    } else {
                        storageRelease(token.mcrtlParms_p, sizeof(*token.mcrtlParms_p), 131, 8);
                        destroyHeap(heapAnchor_p);
                    }
                } else {
                    destroyHeap(heapAnchor_p);
                }
            }
        }

        if (the_env_p != NULL) {
            std_p->unauth_sysenv_p = &(token.mcrtlParms_p->mcrtlParms);
            std_p->unauth_cenv_p = token.mcrtlParms_p->cenv_p;
        }
    }

    if (the_env_p != NULL) {
        setenvintoR12(the_env_p);
    }

    return the_env_p;
}

//---------------------------------------------------------------------
// Define unauthorized stub implementations.
//---------------------------------------------------------------------
#define UNAUTH_DEF(ret_type, func_name, parms_with_type, parms) \
PROLOG_STUB( func_name ); \
ret_type func_name##_stub parms_with_type { \
	connectUnauthorizedCEnv(); \
	return func_name parms; \
}
#include "include/server_unauthorized_functions.def"
#undef UNAUTH_DEF

// Hard coded here by defect 61857. If we ever get a fix for BPX4IPT giving us a 144
// byte save area, uncomment the line in server_unauthorized_functions.def and
// delete the following line.
_Pragma("prolog(driveAuthorizedServiceOnIPTExitRoutine_stub,\" SUAUTHPR SAVEAREA=72\")");_Pragma("epilog(driveAuthorizedServiceOnIPTExitRoutine_stub,\" SUAUTHEP SAVEAREA=72\")"); int driveAuthorizedServiceOnIPTExitRoutine_stub (DriveAuthorizedServiceOnIPTParms_t* parms_p) { connectUnauthorizedCEnv(); return driveAuthorizedServiceOnIPTExitRoutine (parms_p); }


#pragma insert_asm(" IHAPSA")
#pragma insert_asm(" IKJTCB")
#pragma insert_asm(" IHASTCB")
#pragma insert_asm(" IEANTASM")
