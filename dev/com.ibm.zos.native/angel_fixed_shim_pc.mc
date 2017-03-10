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
#include <stdio.h>
#include <string.h>

#include "include/angel_armv_services.h"
#include "include/angel_client_pc_recovery.h"
#include "include/angel_client_pc_stub.h"
#include "include/angel_client_process_data.h"
#include "include/angel_dynamic_replaceable_module.h"
#include "include/angel_process_data.h"
#include "include/angel_server_pc_recovery.h"
#include "include/angel_server_pc_stub.h"
#include "include/angel_sgoo_services.h"
#include "include/angel_task_data.h"
#include "include/bbgzarmv.h"
#include "include/bbgzsgoo.h"
#include "include/bpx_load.h"
#include "include/common_defines.h"
#include "include/ieantc.h"
#include "include/mvs_abend.h"
#include "include/mvs_cell_pool_services.h"
#include "include/mvs_enq.h"
#include "include/mvs_estae.h"
#include "include/mvs_iarv64.h"
#include "include/mvs_storage.h"
#include "include/mvs_user_token_manager.h"
#include "include/mvs_utils.h"

#include "include/gen/bbgzacpp.h"
#include "include/gen/ihapsa.h"
#include "include/gen/ihaascb.h"
#include "include/gen/ihaassb.h"

// NOTE: No tracing in the fixed shim module

/** Subpool to get storage for metal C paramters from. */
#define BBGZ_METALC_PARMS_SUBPOOL 249
/** Key to allocate storage for metal C parameters in. */
#define BBGZ_METALC_PARMS_KEY 2

// Version string function
char* getFixedShimVersionString() {
    int maxVersionStringLen = 16;
    char* verString = malloc(maxVersionStringLen);
    if (verString != NULL) {
        snprintf(verString, maxVersionStringLen, "%i",
#ifdef ANGEL_GENERATE_VER
                 ANGEL_GENERATE_VER
#else
                 BBGZ_FIXED_SHIM_MODULE_CODE_MAJOR_VERSION
#endif
                 );
    }
    return verString;
}

// Version integer function
int getFixedShimVersionInt() {
#ifdef ANGEL_GENERATE_VER  
    return ANGEL_GENERATE_VER;
#else
    return BBGZ_FIXED_SHIM_MODULE_CODE_MAJOR_VERSION;
#endif
}

/**
 * Initialize the ARR parameters, and sets them into the ARR so that they can
 * be referenced later in the ARR routine.
 *
 * @param recovery_p A pointer to the recovery area which will be saved using
 *                   the MSTA instruction.
 * @param recoveryLen The length of the recovery area.
 * @param recoveryEyeCatcher An 8 character string which should be copied into
 *                           the begining of the recovery area.
 */
static void setupArrParms(void* recovery_p, unsigned int recoveryLen, char* recoveryEyeCatcher) {
    // -------------------------------------------------------------
    // Set storage into the ARR.  The contents of the low half of
    // registers 4 and 5 end up in the linkage stack entry for the
    // PC, and can be retrieved in the ARR via the SDWA.
    // -------------------------------------------------------------
    memset(recovery_p, 0, recoveryLen);
    memcpy(recovery_p, recoveryEyeCatcher, 8);

    __asm(" L  4,0(%0)\n"
          " L  5,4(%0)\n"
          " MSTA 4" : : "r"(&recovery_p) : "r4","r5");
}

/**
 * Clears the ARR parms from the ARR.
 */
static void cleanup_arr_parms(void) {
    __asm(" LHI  4,0\n"
          " LHI  5,0\n"
          " MSTA 4" : : : "r4","r5");
}

// PC #1
#pragma prolog(fixedShimPC_Register,"APCPROL")
#pragma epilog(fixedShimPC_Register,"APCEPIL")
int fixedShimPC_Register(AngelPCParmArea_t* latentParms_p, char* server_authorized_function_module_name) {
    // -------------------------------------------------------------
    // Put storage for the ARR parms on the stack.  This storage
    // will be available to the ARR when it runs, and will be
    // freed by the ARR if there is no TGOO (the TGOO would have
    // cached the dynamic area for this routine).
    // -------------------------------------------------------------
    angel_server_pc_recovery arr_parms;
    setupArrParms(&arr_parms, sizeof(arr_parms), ANGEL_SERVER_PC_RECOVERY_EYE);
    arr_parms.free_dynamic_area_if_no_tgoo = TRUE;

    int register_rc = ANGEL_REGISTER_OK;

    long long user_token = getAddressSpaceSupervisorStateUserToken();

    // -----------------------------------------------------------------------
    // Create a metal C environment for key 2 callers.  If this is a
    // re-registration, we can use the one on the angel process data.
    // Otherwise, we have to create a new one, and if we are successful in
    // setting up the rest of our environment, we'll hang it off the angel
    // process data for other callers to use.
    // -----------------------------------------------------------------------
    angel_process_data* apd_p = getAngelProcessData();
    unsigned char reregistration = (apd_p != NULL);
    unsigned char could_not_get_env = 0;

    if (reregistration == FALSE) {
        int so_rc = -1;
        arr_parms.fsm_csysenv_p = storageObtain(sizeof(struct __csysenv_s),
                                                BBGZ_METALC_PARMS_SUBPOOL, // Subpool
                                                BBGZ_METALC_PARMS_KEY,   // Key
                                                &so_rc);

        if (arr_parms.fsm_csysenv_p != NULL) {
            // ----------------------------------------------------------------
            // Create a heap for the metal C environment that we're about to
            // create.
            // ----------------------------------------------------------------
            unsigned long long belowSizes[4] = BBGZ_HEAP_DEFAULT_POOL_SIZES;
            unsigned long long aboveSizes[4] = BBGZ_HEAP_DEFAULT_POOL_SIZES;
            getTaskControlBlockForTCB_t* getTaskDataFcn_p = (getTaskControlBlockForTCB_t*)getAngelTaskDataFromAlternateTCB;
            void* heapAnchor_p = buildHeap(BBGZ_HEAP_ANGEL_AUTH, belowSizes, aboveSizes, getTaskDataFcn_p, TRUE, TRUE);
            if (heapAnchor_p != NULL) {
                arr_parms.fsm_cenv_p = initenv(arr_parms.fsm_csysenv_p, user_token, heapAnchor_p);
                arr_parms.fsm_free_cenv = 1;
            } else {
                void* tempStg_p = arr_parms.fsm_csysenv_p;
                arr_parms.fsm_csysenv_p = NULL;
                storageRelease(tempStg_p, sizeof(struct __csysenv_s), BBGZ_METALC_PARMS_SUBPOOL, BBGZ_METALC_PARMS_KEY);

                could_not_get_env = 1;
            }
        } else {
            could_not_get_env = 1;
        }
    } else {
        setenvintoR12(apd_p->key2_env_p);
        arr_parms.fsm_cenv_p = apd_p->key2_env_p;
    }


    // -----------------------------------------------------------------------
    // Get an ENQ to prevent multiple registrations/deregistrations at the
    // same time.
    // -----------------------------------------------------------------------
    enqtoken registration_enq_token;
    get_enq_exclusive_step(BBGZ_ENQ_QNAME, REGISTRATION_ENQ_RNAME, &registration_enq_token);
    // TODO: Release ENQ in ARR in case caller doesn't let thread die

    // -----------------------------------------------------------------------
    // See if we have angel process data.  This will (in part) tell us if
    // we're re-registering and don't need to re-connect to the shared
    // memory, or  registering for the first time.
    // -----------------------------------------------------------------------
    bbgzsgoo* sgoo_p = NULL;
    if (apd_p == NULL) {
        // -------------------------------------------------------------------
        // Look up the address of the SGOO and connect to it.
        // -------------------------------------------------------------------
        if (latentParms_p->sgoo_p != NULL) {
            accessSharedAbove(latentParms_p->sgoo_p, user_token);
            arr_parms.fsm_sgoo_usertoken = user_token;
            arr_parms.detach_from_shared = 1;
            arr_parms.fsm_attached_sgoo_p = latentParms_p->sgoo_p;
            sgoo_p = latentParms_p->sgoo_p;
        }
    } else {
        // -------------------------------------------------------------------
        // There is angel process data so that means at some point in the
        // past this address space was registered with the Angel.  Make sure
        // that it's not currently registered and that there are no threads
        // still using the old registration.
        // -------------------------------------------------------------------
        if (apd_p->invokecount.deregistered == 0) {
            register_rc = ANGEL_REGISTER_FSM_ALREADY_REG;
        } else if ((apd_p->invokecount.allTasksCleanedUp == 0) || (apd_p->invokecount.curResmgrCount > 0) || (apd_p->invokecount.allClientsUnbound == 0)) {
            register_rc = ANGEL_REGISTER_FSM_ACTIVE_TGOO;
        } else {
            sgoo_p = apd_p->bbgzsgoo_p;
        }
    }

    if (sgoo_p != NULL) {
        // -------------------------------------------------------------------
        // Try to make a connection to the ARMV.  Note that the angel
        // process data may be null if this is the first registration.  In
        // this case we need to pass the ARR parms into the routine so that
        // we can save recovery information (there is no RESMGR yet).
        // -------------------------------------------------------------------
        bbgzarmv* armv_p = attachToARMV(sgoo_p, apd_p,
                                        (apd_p == NULL) ? NULL : &arr_parms);

        if (armv_p != NULL) {
            struct bbgzadrm* drm_p = armv_p->bbgzarmv_drm;

            register_rc = drm_p->register_pc(user_token,
                                             sgoo_p,
                                             apd_p,
                                             armv_p,
                                             server_authorized_function_module_name,
                                             &arr_parms);

            if (register_rc == ANGEL_REGISTER_OK) {
                arr_parms.decrement_armv_use_count = 0;
                arr_parms.detach_from_shared = 0;
                arr_parms.fsm_free_cenv = 0;
            } else {
                if (arr_parms.decrement_armv_use_count == 1) {
                    arr_parms.decrement_armv_use_count = 0;
                    detachFromARMV(armv_p, &arr_parms);
                }

                if (arr_parms.detach_from_shared == 1) {
                    arr_parms.detach_from_shared = 0;
                    detachSharedAbove(sgoo_p, user_token, FALSE);
                }
            }

        } else {
            register_rc = ANGEL_REGISTER_FSM_INACT_ARMV;

            if (arr_parms.detach_from_shared == 1) {
                arr_parms.detach_from_shared = 0;
                detachSharedAbove(sgoo_p, user_token, FALSE);
            }
        }
    } else {
        // -------------------------------------------------------------------
        // Mark the return code as "can't find the SGOO", unless the reason
        // we couldn't find the SGOO was caused by an earlier error.
        // -------------------------------------------------------------------
        if (register_rc == ANGEL_REGISTER_OK) {
            register_rc = ANGEL_REGISTER_FSM_NO_SGOO;
        }
    }

    // -----------------------------------------------------------------------
    // Free the registration ENQ.
    // -----------------------------------------------------------------------
    release_enq(&registration_enq_token);

    // -----------------------------------------------------------------------
    // Destroy the metal C environment if we created one and registration
    // did not complete successfully.
    // -----------------------------------------------------------------------
    if (arr_parms.fsm_free_cenv == 1) {
        arr_parms.fsm_cenv_p = NULL;
        termenv();
        void* tempStg_p = arr_parms.fsm_csysenv_p;
        arr_parms.fsm_csysenv_p = NULL;
        storageRelease(tempStg_p, sizeof(struct __csysenv_s), BBGZ_METALC_PARMS_SUBPOOL, BBGZ_METALC_PARMS_KEY);
    }

    cleanup_arr_parms();

    return register_rc;
}

// PC #2
#pragma prolog(fixedShimPC_Invoke,"APCPROL")
#pragma epilog(fixedShimPC_Invoke,"APCEPIL")
int fixedShimPC_Invoke(AngelPCParmArea_t* latentParms_p,
                       int function_index,
                       int arg_struct_size,
                       void* arg_struct_p) {
    int invoke_rc = ANGEL_INVOKE_OK;

    // -----------------------------------------------------------------------
    // Put storage for the ARR parms on the stack.  This storage will be
    // available to the ARR when it runs, and will be freed by the ARR if
    // there is no TGOO (the TGOO would have cached the dynamic area for
    // this routine).
    // -----------------------------------------------------------------------
    angel_server_pc_recovery arr_parms;
    setupArrParms(&arr_parms, sizeof(arr_parms), ANGEL_SERVER_PC_RECOVERY_EYE);
    arr_parms.free_dynamic_area_if_no_tgoo = TRUE;

    // -----------------------------------------------------------------------
    // Look up the address of the SGOO.
    // -----------------------------------------------------------------------
    if (latentParms_p->sgoo_p != NULL) {
        bbgzsgoo* sgoo_p = latentParms_p->sgoo_p;

        // -------------------------------------------------------------------
        // Get the angel process data.
        // -------------------------------------------------------------------
        angel_process_data* apd_p = getAngelProcessData();

        if (apd_p != NULL) {

            // ---------------------------------------------------------------
            // Attach to our metal C environment.
            // ---------------------------------------------------------------
            setenvintoR12(apd_p->key2_env_p);
            arr_parms.fsm_cenv_p = apd_p->key2_env_p;

            // ---------------------------------------------------------------
            // If the sequence number in the ARMV we retrieved is greater
            // than the sequence number saved in the angel process data, we
            // may need to attach ourselves to this ARMV.  We do not need to
            // pass our recovery information because we have a RESMGR
            // watching over us.
            // ---------------------------------------------------------------
            bbgzarmv* armv_p = attachToARMV(sgoo_p, apd_p, NULL);

            // ---------------------------------------------------------------
            // Ok at this point we have angel process data and an ARMV,
            // branch to the DRM to do the invoke.
            // ---------------------------------------------------------------
            bbgzadrm* drm_p = armv_p->bbgzarmv_drm;

            invoke_rc = drm_p->invoke_pc(function_index, arg_struct_size, arg_struct_p, apd_p, &arr_parms);

        } else {
            invoke_rc = ANGEL_INVOKE_FSM_NO_PGOO;
        }
    } else {
        invoke_rc = ANGEL_INVOKE_FSM_NO_SGOO;
    }  

    // Cleanup the ARR recovery parms
    cleanup_arr_parms();

    return invoke_rc;
}

// PC #3
#pragma prolog(fixedShimPC_Deregister,"APCPROL")
#pragma epilog(fixedShimPC_Deregister,"APCEPIL")
int fixedShimPC_Deregister(AngelPCParmArea_t* latentParms_p) {
    int deregister_rc = ANGEL_DEREGISTER_OK;

    // -------------------------------------------------------------
    // Put storage for the ARR parms on the stack.  This storage
    // will be available to the ARR when it runs, and will be
    // freed by the ARR if there is no TGOO (the TGOO would have
    // cached the dynamic area for this routine).
    // -------------------------------------------------------------
    angel_server_pc_recovery arr_parms;
    setupArrParms(&arr_parms, sizeof(arr_parms), ANGEL_SERVER_PC_RECOVERY_EYE);
    arr_parms.free_dynamic_area_if_no_tgoo = TRUE;

    // -----------------------------------------------------------------
    // Look up the address of the SGOO.
    // -----------------------------------------------------------------
    if (latentParms_p->sgoo_p != NULL) {
        bbgzsgoo* sgoo_p = latentParms_p->sgoo_p;

        // -------------------------------------------------------------------
        // Get the angel process data and see what version of the ARMV it is
        // using.
        // -------------------------------------------------------------------
        angel_process_data* apd_p = getAngelProcessData();

        if (apd_p != NULL) {
            // -------------------------------------------------------------
            // Attach to our metal C environment.
            // -------------------------------------------------------------
            setenvintoR12(apd_p->key2_env_p);
            arr_parms.fsm_cenv_p = apd_p->key2_env_p;

            // ---------------------------------------------------------------
            // If the sequence number in the ARMV we retrieved is greater
            // than the sequence number saved in the angel process data, we
            // may need to attach ourselves to this ARMV.  We do not need to
            // pass our recovery information because we have a RESMGR
            // watching over us.
            // ---------------------------------------------------------------
            bbgzarmv* armv_p = attachToARMV(sgoo_p, apd_p, NULL);

            // ---------------------------------------------------------------
            // Ok at this point we have angel process data and an ARMV,
            // branch to the DRM to do the deregister.
            // ---------------------------------------------------------------
            bbgzadrm* drm_p = armv_p->bbgzarmv_drm;

            deregister_rc = drm_p->deregister_pc(apd_p, &arr_parms);

            cleanup_arr_parms();
        } else {
            deregister_rc = ANGEL_DEREGISTER_FSM_NO_PGOO;
        }
    } else {
        deregister_rc = ANGEL_DEREGISTER_FSM_NO_SGOO;
    }

    return deregister_rc;
}

/** Macro which does a "safe" storage release (copy ptr then delete). */
#define SAFE_STORAGE_RELEASE(ptr, len, sp, key) { \
    void* tempStg_p = ptr;                        \
    ptr = NULL;                                   \
    storageRelease(tempStg_p, len, sp, key);      \
}

// Client bind.
#pragma prolog(fixedShimPC_clientBind,"CPCPROL")
#pragma epilog(fixedShimPC_clientBind,"CPCEPIL")
int fixedShimPC_clientBind(AngelPCParmArea_t* latentParms_p, SToken* targetServerStoken_p, bbgzasvt_header** clientFunctionTablePtr_p, void** bindToken_p) {
    // -----------------------------------------------------------------------
    // Put storage for the ARR parms on the stack.  This storage will be
    // available to the ARR when it runs, and will be freed by the ARR if
    // an error occurs.
    // -----------------------------------------------------------------------
    angel_client_pc_recovery arrParms;
    setupArrParms(&arrParms, sizeof(arrParms), ANGEL_CLIENT_PC_RECOVERY_EYE);
    arrParms.fsm_latentPcParm_p = latentParms_p;

    int bindRc = ANGEL_CLIENT_BIND_OK;

    // -----------------------------------------------------------------------
    // Get an ENQ to cause all binds and unbinds for this client address space
    // to be serialized.
    // -----------------------------------------------------------------------
    arrParms.shr_freeBindEnq = 1;
    get_enq_exclusive_step(BBGZ_ENQ_QNAME, CLIENT_BIND_ENQ_RNAME, &(arrParms.shr_bindEnqToken));

    // -----------------------------------------------------------------------
    // Try to get the angel client process data.  If it exists already, we'll
    // get our metal C environment from it.  Otherwise, we'll make a new
    // one.  Remember, one angel client process data per angel per client
    // address space, so there can be more than one.
    // -----------------------------------------------------------------------
    bbgzsgoo* sgoo_p = latentParms_p->sgoo_p;
    AngelClientProcessData_t* acpd_p = getAngelClientProcessData(latentParms_p->angelAnchor_p);
    if (acpd_p != NULL) {
        setenvintoR12(acpd_p->cenv_p);
        arrParms.fsm_cenv_p = acpd_p->cenv_p;
    } else {
        // -------------------------------------------------------------------
        // Create the metal C environment.
        // -------------------------------------------------------------------
        arrParms.fsm_cenvParms_p = storageObtain(sizeof(struct __csysenv_s),
                                                 BBGZ_METALC_PARMS_SUBPOOL,
                                                 BBGZ_METALC_PARMS_KEY,
                                                 NULL);
        if (arrParms.fsm_cenvParms_p != NULL) {
            arrParms.fsm_cenvParmsSubpool = BBGZ_METALC_PARMS_SUBPOOL;
            arrParms.fsm_cenvParmsKey = BBGZ_METALC_PARMS_KEY;
            long long userToken = getAddressSpaceSupervisorStateUserTokenWithBias((UserTokenBias_t*)&CLIENT_SGOO_BIAS);
            unsigned long long poolSizes[4] = BBGZ_HEAP_DEFAULT_POOL_SIZES;
            void* heapAnchor_p = buildHeap(BBGZ_HEAP_ANGEL_CLIENT_AUTH, poolSizes, poolSizes, NULL, FALSE, FALSE);
            if (heapAnchor_p != NULL) {
                arrParms.fsm_cenv_p = initenv(arrParms.fsm_cenvParms_p, userToken, heapAnchor_p);

                // -----------------------------------------------------------
                // Look up the SGOO using the latent parm.
                // -----------------------------------------------------------
                accessSharedAbove((void*)(latentParms_p->sgoo_p), userToken);
                arrParms.fsm_sgooUserToken = userToken;
                arrParms.fsm_sgoo_p = latentParms_p->sgoo_p;
            } else {
                SAFE_STORAGE_RELEASE(arrParms.fsm_cenvParms_p, sizeof(struct __csysenv_s), BBGZ_METALC_PARMS_SUBPOOL, BBGZ_METALC_PARMS_KEY);
            }
        }
    }

    // -----------------------------------------------------------------------
    // If we got the SGOO, we can attach ourselves to the ARMV and forward to
    // the bind method in the dynamic replaceable module.
    // -----------------------------------------------------------------------
    if (sgoo_p != NULL) {
        bbgzarmv* armv_p = attachClientToARMV(sgoo_p, acpd_p, (acpd_p == NULL) ? NULL : &arrParms);

        if (armv_p != NULL) {
            arrParms.fsm_armvUsedOnMethodCall_p = armv_p;
            struct bbgzadrm* drm_p = armv_p->bbgzarmv_drm;
            bindRc = drm_p->clientBind_pc(targetServerStoken_p, clientFunctionTablePtr_p, bindToken_p, sgoo_p, armv_p, acpd_p, &arrParms);

            // ---------------------------------------------------------------
            // If the bind completed successfully, we should not clean up the
            // metal C environment or the ARMV attachment because other
            // callers will be using them.
            // ---------------------------------------------------------------
            if (bindRc == ANGEL_CLIENT_BIND_OK) {
                arrParms.fsm_decrementArmvUseCount = 0;
                arrParms.fsm_sgooUserToken = 0L;
                arrParms.fsm_cenvParms_p = NULL;
            } else {
                if (arrParms.fsm_decrementArmvUseCount == 1) {
                    arrParms.fsm_decrementArmvUseCount = 0;
                    detachClientFromARMV(armv_p, &arrParms);
                }

                if (arrParms.fsm_sgooUserToken != 0L) {
                    long long userToken = arrParms.fsm_sgooUserToken;
                    arrParms.fsm_sgooUserToken = 0L;
                    detachSharedAbove(sgoo_p, userToken, FALSE);
                }
            }
        } else {
            bindRc = ANGEL_CLIENT_BIND_FSM_INACT_ARMV;

            if (arrParms.fsm_sgooUserToken != 0L) {
                long long userToken = arrParms.fsm_sgooUserToken;
                arrParms.fsm_sgooUserToken = 0L;
                detachSharedAbove(sgoo_p, userToken, FALSE);
            }
        }
    } else {
        bindRc = (bindRc == ANGEL_CLIENT_BIND_OK) ? ANGEL_CLIENT_BIND_FSM_NO_SGOO : bindRc;
    }

    // -----------------------------------------------------------------------
    // Release the bind ENQ.
    // -----------------------------------------------------------------------
    release_enq(&(arrParms.shr_bindEnqToken));
    arrParms.shr_freeBindEnq = 0;

    // -----------------------------------------------------------------------
    // Destroy the metal C environment if we created one and registration did
    // not complete successfully.
    // -----------------------------------------------------------------------
    if (arrParms.fsm_cenvParms_p != NULL) {
        arrParms.fsm_cenv_p = NULL;
        termenv();
        SAFE_STORAGE_RELEASE(arrParms.fsm_cenvParms_p, sizeof(struct __csysenv_s), BBGZ_METALC_PARMS_SUBPOOL, BBGZ_METALC_PARMS_KEY);
    }

    cleanup_arr_parms();

    return bindRc;
}

// Client invoke.  Validate bind token and get dynamic area from pool.
#pragma prolog(fixedShimPC_clientInvoke," CPCPROL BINDTOK=YES,DYNAREA=BINDTOK")
#pragma epilog(fixedShimPC_clientInvoke,"CPCEPIL")
int fixedShimPC_clientInvoke(AngelPCParmArea_t* latentParms_p, void* bindToken_p, int serviceIndex, int parm_len, void* parm_p) {
    // -----------------------------------------------------------------------
    // Put storage for the ARR parms on the stack.  This storage will be
    // available to the ARR when it runs, and will be freed by the ARR if
    // an error occurs.
    // -----------------------------------------------------------------------
    angel_client_pc_recovery arrParms;
    setupArrParms(&arrParms, sizeof(arrParms), ANGEL_CLIENT_PC_RECOVERY_EYE);
    arrParms.fsm_latentPcParm_p = latentParms_p;

    int invokeRc = ANGEL_CLIENT_INVOKE_OK;

    // -----------------------------------------------------------------------
    // Get the metal C environment and place it in R12.  The bind token was
    // verified in the entry linkage so there is no need for further
    // verification here.
    // -----------------------------------------------------------------------
    angel_task_data* atd_p = getAngelTaskData();
    AngelClientBindData_t* bindData_p = atd_p->validatedClientBindData_p;
    AngelClientProcessData_t* acpd_p = bindData_p->clientProcessData_p;
    bbgzsgoo* sgoo_p = acpd_p->sgoo_p;
    setenvintoR12(acpd_p->cenv_p);
    arrParms.fsm_cenv_p = acpd_p->cenv_p;

    // -----------------------------------------------------------------------
    // If the sequence number in the ARMV we retrieved is greater than the
    // sequence number saved in the angel client process data, we may need to
    // attach ourselves to this ARMV.  We do not need to pass our recovery
    // information because we have a RESMGR watching over us.
    // -----------------------------------------------------------------------
    bbgzarmv* armv_p = attachClientToARMV(sgoo_p, acpd_p, NULL);
    arrParms.fsm_armvUsedOnMethodCall_p = armv_p;

    // -----------------------------------------------------------------------
    // Ok at this point we have angel process data and an ARMV,
    // branch to the DRM to do the client invoke.
    // -----------------------------------------------------------------------
    bbgzadrm* drm_p = armv_p->bbgzarmv_drm;
    invokeRc = drm_p->clientInvoke_pc(bindToken_p, serviceIndex, parm_len, parm_p, &arrParms);

    // -----------------------------------------------------------------------
    // Cleanup the ARR recovery parms
    // -----------------------------------------------------------------------
    cleanup_arr_parms();

    return invokeRc;
}

// Client unbind. Validate bind token but don't get dynamic area from pool.
#pragma prolog(fixedShimPC_clientUnbind," CPCPROL BINDTOK=YES")
#pragma epilog(fixedShimPC_clientUnbind,"CPCEPIL")
int fixedShimPC_clientUnbind(AngelPCParmArea_t* latentParms_p, void* bindToken_p) {
    // -----------------------------------------------------------------------
    // Put storage for the ARR parms on the stack.  This storage will be
    // available to the ARR when it runs, and will be freed by the ARR if
    // an error occurs.
    // -----------------------------------------------------------------------
    angel_client_pc_recovery arrParms;
    setupArrParms(&arrParms, sizeof(arrParms), ANGEL_CLIENT_PC_RECOVERY_EYE);
    arrParms.fsm_latentPcParm_p = latentParms_p;

    int unbindRc = ANGEL_CLIENT_UNBIND_OK;

    // -----------------------------------------------------------------------
    // Get an ENQ to cause all binds and unbinds for this client address space
    // to be serialized.
    // -----------------------------------------------------------------------
    arrParms.shr_freeBindEnq = 1;
    get_enq_exclusive_step(BBGZ_ENQ_QNAME, CLIENT_BIND_ENQ_RNAME, &(arrParms.shr_bindEnqToken));

    // -----------------------------------------------------------------------
    // Get the metal C environment and place it in R12.  The bind token was
    // verified in the entry linkage so there is no need for further
    // verification here.
    // -----------------------------------------------------------------------
    angel_task_data* atd_p = getAngelTaskData();
    AngelClientBindData_t* bindData_p = atd_p->validatedClientBindData_p;
    AngelClientProcessData_t* acpd_p = bindData_p->clientProcessData_p;
    bbgzsgoo* sgoo_p = acpd_p->sgoo_p;
    setenvintoR12(acpd_p->cenv_p);
    arrParms.fsm_cenv_p = acpd_p->cenv_p;

    // -----------------------------------------------------------------------
    // If the sequence number in the ARMV we retrieved is greater than the
    // sequence number saved in the angel client process data, we may need to
    // attach ourselves to this ARMV.  We do not need to pass our recovery
    // information because we have a RESMGR watching over us.
    // -----------------------------------------------------------------------
    bbgzarmv* armv_p = attachClientToARMV(sgoo_p, acpd_p, NULL);
    arrParms.fsm_armvUsedOnMethodCall_p = armv_p;

    // -----------------------------------------------------------------------
    // Ok at this point we have angel process data and an ARMV,
    // branch to the DRM to do the client invoke.
    // -----------------------------------------------------------------------
    bbgzadrm* drm_p = armv_p->bbgzarmv_drm;

    unsigned char doCleanup = FALSE;
    unsigned char armvWatermark = acpd_p->curArmvSeq;
    void* cenvParms_p = acpd_p->cenvParms_p;
    unsigned short cenvParmsSubpool = acpd_p->cenvParmsSubpool;
    unsigned char cenvParmsKey = acpd_p->cenvParmsKey;
    unbindRc = drm_p->clientUnbind_pc(bindToken_p, &arrParms, &doCleanup);

    // -----------------------------------------------------------------------
    // If DRM told us to finish cleanup, we should destroy the metal C
    // environment and detach from the ARMV, because there is no longer a
    // RESMGR watching over us.  Also detach from the shared memory.
    //
    // NOTE that doCleanup cannot currently be set to true, because it is not
    // safe to do so.  An invoker might be using some of these objects at the
    // time we are trying to clean them up (ie we've called invoke but have
    // not incremented the invoke count yet).
    // -----------------------------------------------------------------------
    if (doCleanup == TRUE) {
        arrParms.fsm_cenvParms_p = cenvParms_p;
        arrParms.fsm_cenv_p = getenvfromR12();
        arrParms.fsm_sgooUserToken = getAddressSpaceSupervisorStateUserTokenWithBias((UserTokenBias_t*)&CLIENT_SGOO_BIAS);
        arrParms.fsm_sgoo_p = sgoo_p;
        arrParms.fsm_armvWatermark = armvWatermark;
        arrParms.fsm_detachFromAllARMVs = TRUE;

        assb* assb_p = (assb*)(((ascb*)(((psa*)0)->psaaold))->ascbassb);
        detachAllARMVs(armvWatermark, assb_p, TRUE);

        detachSharedAbove(sgoo_p, arrParms.fsm_sgooUserToken, 0);

        termenv();
        storageRelease(cenvParms_p, sizeof(struct __csysenv_s), cenvParmsSubpool, cenvParmsKey);
    }

    // -----------------------------------------------------------------------
    // Release the bind ENQ.
    // -----------------------------------------------------------------------
    release_enq(&(arrParms.shr_bindEnqToken));
    arrParms.shr_freeBindEnq = 0;

    // -----------------------------------------------------------------------
    // Cleanup the ARR recovery parms
    // -----------------------------------------------------------------------
    cleanup_arr_parms();

    return unbindRc;
}

// PC #7
#pragma prolog(fixedShimPC_rsvd7,"APCPROL")
#pragma epilog(fixedShimPC_rsvd7,"APCEPIL")
int fixedShimPC_rsvd7(AngelPCParmArea_t* latentParms_p) {
    abend_with_data(ABEND_TYPE_SERVER, KRSN_ANGEL_FIXED_SHIM_PC_UNDEFINED_PC, (void*)7, NULL);
    return 0;
}

/* PC #8 */
#pragma prolog(fixedShimPC_rsvd8,"APCPROL")
#pragma epilog(fixedShimPC_rsvd8,"APCEPIL")
int fixedShimPC_rsvd8(AngelPCParmArea_t* latentParms_p) {
    abend_with_data(ABEND_TYPE_SERVER, KRSN_ANGEL_FIXED_SHIM_PC_UNDEFINED_PC, (void*)8, NULL);
    return 0;
}

/**
 * Perform ARR actions for a task in a client address space.
 *
 * @param sdwa_p The SDWA provided by the ARR.
 * @param recovery_p A pointer to the client PC recovery control block from the
 *                   caller.
 */
static void clientArrRecovery(sdwa* sdwa_p, angel_client_pc_recovery* recovery_p) {
    struct __csysenv_s env_p;

    int estaex_rc = -1;
    int estaex_rsn = -1;

    // -----------------------------------------------------------------------
    // Retry variable must be volatile so that bits are set immediately into
    // memory and not cached in a register.
    // -----------------------------------------------------------------------
    volatile struct
    {
      int need_to_free_metal_c_env : 1;
      int tried_to_free_metal_c_env : 1;
      int freed_metal_c_env : 1;
      int tried_to_free_caller_dynamic_area : 1;
      int freed_caller_dynamic_area : 1;
      int tried_to_call_drm : 1;
      int called_drm : 1;
      int tried_to_detach_from_shared : 1;
      int detached_from_shared : 1;
      int tried_to_decrement_armv_count : 1;
      int decremented_armv_count : 1;
      int tried_to_free_bind_enq : 1;
      int freed_bind_enq : 1;
      int tried_to_obtain_bind_enq : 1;
      int obtained_bind_enq : 1;
      int tried_to_detach_from_all_armvs : 1;
      int detached_from_all_armvs : 1;
      int _available : 15;
    } retry_bits;

    volatile struct __csysenv_s* env_parms_to_free_p = NULL;

    memset((void*)(&retry_bits), 0, sizeof(retry_bits));

    // -----------------------------------------------------------
    // Get the metal C environment that we used for the failed
    // function call and use that for the ARR.  That way we can
    // free any storage that was allocated using that env.
    // If there is no metal C environment, we'll make a new one,
    // because it is required to create an ESTAE, but the only
    // recovery we'll try to do is to free the caller's dynamic
    // area.
    // -----------------------------------------------------------
    if (recovery_p->fsm_cenv_p != NULL) {
        setenvintoR12(recovery_p->fsm_cenv_p);
        retry_bits.need_to_free_metal_c_env = (recovery_p->fsm_cenvParms_p != NULL);
        env_parms_to_free_p = recovery_p->fsm_cenvParms_p;
    } else {
        initenv(&env_p, getTaskSupervisorStateUserToken(), NULL);
        retry_bits.need_to_free_metal_c_env = 1;
    }

    // -----------------------------------------------------------
    // Set up an ESTAE in case we run into trouble.
    // -----------------------------------------------------------
    retry_parms angel_retry_area;
    memset(&angel_retry_area, 0, sizeof(angel_retry_area));
    establish_estaex_with_retry(&angel_retry_area,
                                &estaex_rc,
                                &estaex_rsn);

    // -----------------------------------------------------------
    // Only perform this recovery if we have the Metal C
    // environment from the failed caller.
    // -----------------------------------------------------------
    if (recovery_p->fsm_cenv_p != NULL) {

        // -------------------------------------------------------------------
        // To simplify the recovery logic, we'll always run the ARR with the
        // bind ENQ held.  If we don't already hold the bind ENQ, get it.
        // Further, don't rely on the recovery area to determine whether we
        // hold the ENQ.  Ask MVS about it.
        // -------------------------------------------------------------------
        SET_RETRY_POINT(angel_retry_area);
        if (retry_bits.tried_to_obtain_bind_enq == 0) {
            retry_bits.tried_to_obtain_bind_enq = 1;
            enqtoken bindEnqToken;
            // TODO: Test this function with and without the ENQ being held.
            if (test_enq_step(BBGZ_ENQ_QNAME, CLIENT_BIND_ENQ_RNAME, &bindEnqToken) == TASK_OWNS_EXCLUSIVE) {
                memcpy(&(recovery_p->shr_bindEnqToken), &bindEnqToken, sizeof(enqtoken));
                recovery_p->shr_heldBindEnqOnEntry = 1;
            } else {
                get_enq_exclusive_step(BBGZ_ENQ_QNAME, CLIENT_BIND_ENQ_RNAME, &(recovery_p->shr_bindEnqToken));
            }
            recovery_p->shr_freeBindEnq = 1;
            retry_bits.obtained_bind_enq = 1;
        }

        // -------------------------------------------------------------------
        // Get a reference to the DRM so we can call its ARR cleanup routine.
        // Only call into the DRM if we attached to the ARMV.
        // -------------------------------------------------------------------
        if ((retry_bits.obtained_bind_enq == 1) &&
            (recovery_p->fsm_armvUsedOnMethodCall_p != NULL)) {
            SET_RETRY_POINT(angel_retry_area);
            if (retry_bits.tried_to_call_drm == 0) {
                retry_bits.tried_to_call_drm = 1;
                bbgzarmv* armv_p = recovery_p->fsm_armvUsedOnMethodCall_p;
                bbgzadrm* drm_p = armv_p->bbgzarmv_drm;
                drm_p->clientArr(sdwa_p, recovery_p);
                retry_bits.called_drm = 1;
            }
        }

        // -------------------------------------------------------------------
        // Decrement the ARMV in-use count if we incremented it and there is
        // not a RESMGR watching over us to do it.
        // -------------------------------------------------------------------
        if (recovery_p->fsm_decrementArmvUseCount != 0) {
            SET_RETRY_POINT(angel_retry_area);

            if (retry_bits.tried_to_decrement_armv_count == 0) {
                retry_bits.tried_to_decrement_armv_count = 1;
                detachClientFromARMV((bbgzarmv*)recovery_p->fsm_armvToDecrement, recovery_p);
                retry_bits.decremented_armv_count = 1;
            }
        }

        // -------------------------------------------------------------------
        // Detach from all ARMVs, if the caller asked us to do that (unbind).
        // -------------------------------------------------------------------
        if ((recovery_p->fsm_detachFromAllARMVs != 0) &&
            (recovery_p->shr_heldBindEnqOnEntry == 1)) {
            SET_RETRY_POINT(angel_retry_area);

            if (retry_bits.tried_to_detach_from_all_armvs == 0) {
                retry_bits.tried_to_detach_from_all_armvs = 1;
                assb* assb_p = (assb*)(((ascb*)(((psa*)0)->psaaold))->ascbassb);
                detachAllARMVs(recovery_p->fsm_armvWatermark, assb_p, TRUE);
                retry_bits.detached_from_all_armvs = 1;
            }
        }

        // -----------------------------------------------------------
        // Detach from the shared memory if we need to.
        // -----------------------------------------------------------
        if ((recovery_p->fsm_sgooUserToken != 0L) &&
            (recovery_p->fsm_sgoo_p != NULL) &&
            (recovery_p->shr_heldBindEnqOnEntry == 1)) {
            SET_RETRY_POINT(angel_retry_area);

            if (retry_bits.tried_to_detach_from_shared == 0) {
                retry_bits.tried_to_detach_from_shared = 1;
                bbgzsgoo* sgoo_p = (bbgzsgoo*) recovery_p->fsm_sgoo_p;
                detachSharedAbove(sgoo_p, recovery_p->fsm_sgooUserToken, FALSE);
                retry_bits.detached_from_shared = 1;
            }
        }
    }

    // -----------------------------------------------------------------------
    // Free the bind ENQ.
    // -----------------------------------------------------------------------
    if (recovery_p->shr_freeBindEnq != 0) {
        SET_RETRY_POINT(angel_retry_area);
        if (retry_bits.tried_to_free_bind_enq == 0) {
            retry_bits.tried_to_free_bind_enq = 1;
            release_enq(&(recovery_p->shr_bindEnqToken));
            retry_bits.freed_bind_enq = 1;
        }
    }

    // -----------------------------------------------------------------------
    // The dynamic area from the PC routine needs to be freed.
    // The parms for the ARR are in the dynamic area.
    // Note that if the caller fails very early the latent parm area won't be
    // set in the ARR recovery area, and we won't be able to find the cell
    // pool to return the storage to.
    // TODO: Investigate if there's another way to get at the latent parm from
    //       within the ARR.
    // -----------------------------------------------------------------------
    SET_RETRY_POINT(angel_retry_area);
    if (retry_bits.tried_to_free_caller_dynamic_area == 0) {
        retry_bits.tried_to_free_caller_dynamic_area = 1;
        void* dynamicAreaAddr_p = (void*)(((long long)recovery_p) & ANGEL_TASK_DATA_PREFIX_MASK);
        AngelPCParmArea_t* latentParms_p = recovery_p->fsm_latentPcParm_p;
        AngelClientProcessData_t* acpd_p = (latentParms_p != NULL) ? getAngelClientProcessData(latentParms_p->angelAnchor_p) : NULL;
        if ((acpd_p != NULL) && (verifyCellInPool(acpd_p->clientDynAreaPool, dynamicAreaAddr_p, NULL) == 1)) {
            freeCellPoolCell(acpd_p->clientDynAreaPool, dynamicAreaAddr_p);
        } else {
            int iarv64_rc, iarv64_rsn;
            release_iarv64(dynamicAreaAddr_p, NULL, &iarv64_rc, &iarv64_rsn);
        }
        retry_bits.freed_caller_dynamic_area = 1;
    }

    // Note: recovery_p can't be referenced anymore

    // -----------------------------------------------------------
    // Clean up the metal C environment if necessary (either
    // the one we created, or the caller's if they need it
    // freed).
    // -----------------------------------------------------------
    if (retry_bits.need_to_free_metal_c_env == 1) {
        SET_RETRY_POINT(angel_retry_area);

        if (retry_bits.tried_to_free_metal_c_env == 0) {
            retry_bits.tried_to_free_metal_c_env = 1;
            termenv();
            if (env_parms_to_free_p != NULL) {
                storageRelease((void*)env_parms_to_free_p, sizeof(struct __csysenv_s), BBGZ_METALC_PARMS_SUBPOOL, BBGZ_METALC_PARMS_KEY);
                env_parms_to_free_p = NULL;
            }
            retry_bits.freed_metal_c_env = 1;
        }
    }

    // -----------------------------------------------------------
    // Cancel the ESTAE
    // -----------------------------------------------------------
    remove_estaex(&estaex_rc, &estaex_rsn);
}

/**
 * Perform ARR actions for a task in the liberty server.
 *
 * @param sdwa_p The SDWA provided by the ARR.
 * @param recovery_p A pointer to the server PC recovery control block from the
 *                   caller.
 */
static void serverArrRecovery(sdwa* sdwa_p, angel_server_pc_recovery* recovery_p) {

    struct __csysenv_s env_p;

    int estaex_rc = -1;
    int estaex_rsn = -1;

    // -----------------------------------------------------------------------
    // Retry variable must be volatile so that bits are set immediately into
    // memory and not cached in a register.
    // -----------------------------------------------------------------------
    volatile struct
    {
      int need_to_free_metal_c_env : 1;
      int tried_to_free_metal_c_env : 1;
      int freed_metal_c_env : 1;
      int tried_to_reset_tgoo_invoke_bit : 1;
      int reset_tgoo_invoke_bit : 1;
      int tried_to_free_caller_dynamic_area : 1;
      int freed_caller_dynamic_area : 1;
      int tried_to_call_drm : 1;
      int called_drm : 1;
      int tried_to_detach_from_shared : 1;
      int detached_from_shared : 1;
      int tried_to_decrement_armv_count : 1;
      int decremented_armv_count : 1;
      int _available : 19;
    } retry_bits;

    volatile struct __csysenv_s* env_parms_to_free_p = NULL;

    memset((void*)(&retry_bits), 0, sizeof(retry_bits));

    // -----------------------------------------------------------
    // Get the metal C environment that we used for the failed
    // function call and use that for the ARR.  That way we can
    // free any storage that was allocated using that env.
    // If there is no metal C environment, we'll make a new one,
    // because it is required to create an ESTAE, but the only
    // recovery we'll try to do is to free the caller's dynamic
    // area.
    // -----------------------------------------------------------
    if (recovery_p->fsm_cenv_p != NULL) {
        setenvintoR12(recovery_p->fsm_cenv_p);
        retry_bits.need_to_free_metal_c_env = recovery_p->fsm_free_cenv;
        env_parms_to_free_p = recovery_p->fsm_csysenv_p;
    } else {
        initenv(&env_p, getTaskSupervisorStateUserToken(), NULL);
        retry_bits.need_to_free_metal_c_env = 1;
    }

    // -----------------------------------------------------------
    // Set up an ESTAE in case we run into trouble.
    // -----------------------------------------------------------
    retry_parms angel_retry_area;
    memset(&angel_retry_area, 0, sizeof(angel_retry_area));
    establish_estaex_with_retry(&angel_retry_area,
                                &estaex_rc,
                                &estaex_rsn);

    // -----------------------------------------------------------
    // Only perform this recovery if we have the Metal C
    // environment from the failed caller.
    // -----------------------------------------------------------
    if (recovery_p->fsm_cenv_p != NULL) {
        // -------------------------------------------------------
        // See if we have angel process data for the next set of
        // recovery code.
        // -------------------------------------------------------
        angel_process_data* apd_p = getAngelProcessData();
        if (apd_p != NULL) {
            // ---------------------------------------------------
            // Get a reference to the DRM so we can call its ARR
            // cleanup routine.  We only do this if there is
            // angel process data.  The logic here is that if we
            // don't have process data yet, we probably didn't
            // get far enough to call the DRM in the first
            // place, and therefore don't need to call it for
            // cleanup.  In addition, if we have angel process
            // data, we also have a RESMGR and therefore don't
            // need to worry about cleaning up the attachment to
            // the ARMV which we might be creating by driving
            // the DRM ARR routine.
            // ---------------------------------------------------
            SET_RETRY_POINT(angel_retry_area);

            if (retry_bits.tried_to_call_drm == 0) {
                retry_bits.tried_to_call_drm = 1;
                bbgzsgoo* sgoo_p = apd_p->bbgzsgoo_p;
                bbgzarmv* armv_p = attachToARMV(sgoo_p, apd_p, NULL);
                bbgzadrm* drm_p = armv_p->bbgzarmv_drm;
                drm_p->arr(sdwa_p, recovery_p);
                retry_bits.called_drm = 1;
            }
        }

        // -----------------------------------------------------------
        // Decrement the ARMV in-use count if we incremented it and
        // there is not a RESMGR watching over us to do it.
        // -----------------------------------------------------------
        if (recovery_p->decrement_armv_use_count != 0) {
            SET_RETRY_POINT(angel_retry_area);

            if (retry_bits.tried_to_decrement_armv_count == 0) {
                retry_bits.tried_to_decrement_armv_count = 1;
                detachFromARMV((bbgzarmv*)recovery_p->armv_to_decrement, recovery_p);
                retry_bits.decremented_armv_count = 1;
            }
        }

        // -----------------------------------------------------------
        // Detach from the shared memory if we need to.
        // -----------------------------------------------------------
        if ((recovery_p->detach_from_shared != 0) && (recovery_p->fsm_attached_sgoo_p != NULL)) {
            SET_RETRY_POINT(angel_retry_area);

            if (retry_bits.tried_to_detach_from_shared == 0) {
                retry_bits.tried_to_detach_from_shared = 1;
                bbgzsgoo* sgoo_p = (bbgzsgoo*) recovery_p->fsm_attached_sgoo_p;
                detachSharedAbove(sgoo_p, recovery_p->fsm_sgoo_usertoken, FALSE);
                retry_bits.detached_from_shared = 1;
            }
        }
    }

    // -----------------------------------------------------------
    // The dynamic area from the PC routine needs to be freed.
    // The parms for the ARR are in the dynamic area.  We will
    // do the same thing the exit linkage for the PC would have
    // done -- if there is no task data caching the dynamic area
    // for this thread, we'll free it.  If there is task data,
    // we need to reset the "in-use" bit so that if the task is
    // used again, the dynamic area is available.
    // -----------------------------------------------------------
    angel_task_data* atd_p = getAngelTaskDataFromSTCB();
    SET_RETRY_POINT(angel_retry_area);
    if (retry_bits.tried_to_free_caller_dynamic_area == 0) {
        retry_bits.tried_to_free_caller_dynamic_area = 1;
        if (atd_p == NULL) {
            void* dynamic_area_addr_p = (void*)(((long long)recovery_p) & ANGEL_TASK_DATA_PREFIX_MASK);
            int iarv64_rc, iarv64_rsn;
            release_iarv64(dynamic_area_addr_p, NULL, &iarv64_rc, &iarv64_rsn);
        } else {
            int old_inuse = -1, new_inuse = 0;
            __cs1(&old_inuse, &(atd_p->dynamic_area_in_use), &new_inuse);
        }
        retry_bits.freed_caller_dynamic_area = 1;
    }

    // Note: recovery_p can't be referenced anymore

    // -----------------------------------------------------------
    // Clean up the metal C environment if necessary (either
    // the one we created, or the caller's if they need it
    // freed).
    // -----------------------------------------------------------
    if (retry_bits.need_to_free_metal_c_env == 1) {
        SET_RETRY_POINT(angel_retry_area);

        if (retry_bits.tried_to_free_metal_c_env == 0) {
            retry_bits.tried_to_free_metal_c_env = 1;
            termenv();
            if (env_parms_to_free_p != NULL) {
                storageRelease((void*)env_parms_to_free_p, sizeof(struct __csysenv_s), BBGZ_METALC_PARMS_SUBPOOL, BBGZ_METALC_PARMS_KEY);
                env_parms_to_free_p = NULL;
            }
            retry_bits.freed_metal_c_env = 1;
        }
    }

    // -----------------------------------------------------------
    // Cancel the ESTAE
    // -----------------------------------------------------------
    remove_estaex(&estaex_rc, &estaex_rsn);
}

/**
 * Code issued when we want to return to the caller from the ARR with an error RC.
 */
static const unsigned long long ARR_ERROR_RETURN_CODE_CLIENT = 0xA7F9020201010000; // Set RC and return (PR).

// ARR
#pragma prolog(fixedShim_ARR," RETRPROL ENVIRON=ANGEL")
#pragma epilog(fixedShim_ARR,"RETREPIL")
void fixedShim_ARR(sdwa* sdwa_p) {
    // -----------------------------------------------------------------------
    // SWDAPARM field points to 8 byte modifiable area of linkage stack
    // entry, which would contain a pointer to our recovery parms.
    // -----------------------------------------------------------------------
    void* retryAddr_p = NULL;
    if ((sdwa_p != NULL) && (sdwa_p->sdwaparm != NULL)) {
        void* recovery_p;
        memcpy(&recovery_p, sdwa_p->sdwaparm, sizeof(recovery_p));

        // -------------------------------------------------------------------
        // The first 8 bytes of this area are an eye catcher which we'll
        // check for the recovery type.
        // -------------------------------------------------------------------
        if (recovery_p != NULL) {
            if (((unsigned long long)recovery_p) == 8) {
                // -----------------------------------------------------------
                // We failed very very early in the entry linkage to client
                // invoke or unbind.  We probably were passed a bum bind
                // token.  Return to the caller with a bad return code.
                // -----------------------------------------------------------
                retryAddr_p = (void*)&ARR_ERROR_RETURN_CODE_CLIENT;
            } else if (memcmp(recovery_p, ANGEL_SERVER_PC_RECOVERY_EYE, strlen(ANGEL_SERVER_PC_RECOVERY_EYE)) == 0) {
                serverArrRecovery(sdwa_p, (angel_server_pc_recovery*) recovery_p);
            } else if (memcmp(recovery_p, ANGEL_CLIENT_PC_RECOVERY_EYE, strlen(ANGEL_CLIENT_PC_RECOVERY_EYE)) == 0) {
                clientArrRecovery(sdwa_p, (angel_client_pc_recovery*) recovery_p);
            } else if (memcmp(recovery_p, ANGEL_CLIENT_PC_RECOVERY_PROLOG_EYE, strlen(ANGEL_CLIENT_PC_RECOVERY_PROLOG_EYE)) == 0) {
                // -----------------------------------------------------------
                // We failed in the entry linkage to client invoke or unbind.
                // Put the dynamic area back where we found it, and return to
                // the caller with a bad return code.
                // THE RETURN CODE IS 0x202 or decimal 514!!!
                // -----------------------------------------------------------
                cpcprol_tempdyn* tempDynArea_p = (cpcprol_tempdyn*)recovery_p;
                if (tempDynArea_p->cpcprol_tempdyn_pool != 0) {
                    bbgzsgoo* sgoo_p = tempDynArea_p->cpcprol_tempdyn_sgoo;
                    if (sgoo_p != NULL) {
                        freeCellPoolCell(sgoo_p->bbgzsgoo_clientPreDynamicAreaPool, tempDynArea_p);
                    }
                } else {
                    storageRelease(tempDynArea_p, tempDynArea_p->cpcprol_tempdyn_len, tempDynArea_p->cpcprol_tempdyn_sp, tempDynArea_p->cpcprol_tempdyn_key);
                }
                retryAddr_p = (void*)&ARR_ERROR_RETURN_CODE_CLIENT;
            }
        }
    }

    if (retryAddr_p == NULL) {
        __asm(" SETRP WKAREA=(%0),DUMP=IGNORE,RC=0,RECORD=YES" : :
              "r"(sdwa_p) :
              "r0","r1","r14","r15");
    } else {
        __asm(" SETRP WKAREA=(%0),DUMP=NO,RETADDR=(%1),RC=4,RETREGS=NO,FRESDWA=YES,RETRYAMODE=64,RECORD=YES" : :
              "r"(sdwa_p),"r"(retryAddr_p) :
              "r0","r1","r14","r15");
    }
}

#pragma insert_asm(" CVT DSECT=YES")
#pragma insert_asm(" IHAECVT")
#pragma insert_asm(" IHASDWA")
#pragma insert_asm(" IHAPSA")
#pragma insert_asm(" IKJTCB")
#pragma insert_asm(" IHASTCB")
#pragma insert_asm(" IEANTASM")
