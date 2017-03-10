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

/**
 * @file
 *
 * PC routine for sync to thread
 *
 */

#include <metal.h>
#include <stdlib.h>
#include <string.h>

#include "include/mvs_utils.h"
#include "include/ras_tracing.h"
#include "include/security_saf_acee.h"
#include "include/security_saf_authorization.h"
#include "include/security_saf_common.h"
#include "include/security_saf_sync_to_thread.h"
#include "include/server_process_data.h"
#include "include/util_registry.h"

#include "include/gen/bpxzotcb.h"
#include "include/gen/ihaascb.h"
#include "include/gen/ihaasxb.h"
#include "include/gen/ihapsa.h"
#include "include/gen/ihastcb.h"
#include "include/gen/ikjtcb.h"

#define RAS_MODULE_CONST RAS_MODULE_SECURITY_SAF_SYNC_TO_THREAD

//---------------------------------------------------------------------
// Error codes.
//---------------------------------------------------------------------
#define SYNC_TO_THREAD_RC_BPX4TLS_DELETE     (RAS_MODULE_SECURITY_SAF_SYNC_TO_THREAD + 1)
#define SYNC_TO_THREAD_RC_ACEE_CREATE        (RAS_MODULE_SECURITY_SAF_SYNC_TO_THREAD + 2)
#define SYNC_TO_THREAD_RC_BPX4TLS_ACEE       (RAS_MODULE_SECURITY_SAF_SYNC_TO_THREAD + 3)
#define SYNC_TO_THREAD_RC_SYNC_NOT_PERMITTED (RAS_MODULE_SECURITY_SAF_SYNC_TO_THREAD + 4)
#define SYNC_TO_THREAD_RC_DISABLED           (RAS_MODULE_SECURITY_SAF_SYNC_TO_THREAD + 5)

#define TLS_DELETE_THREAD_SEC 2
#define TLS_TASK_ACEE 3

#pragma linkage(BPX4TLS,OS64_NOSTACK)
extern void BPX4TLS(int function_code,
                    int identity_type,
                    int identity_length,
                    char* identity,
                    int pass_length,
                    char* pass,
                    int option_flags,
                    int* rv,
                    int* rc,
                    int* rsn);

#define PROFILE_PREFIX_LENGTH 246   // RACROUTE says 255 bytes 255-9 for BBG.SYNC. leaves 246
/**
 * The SyncToThreadParms passed into syncToThread are copied from
 * key 8 into this struct, which is in key 2.
 */
typedef struct {
    RegistryToken       safCredentialToken;                       //!< Input - A token associated with the native security credential (RACO) to be authorized.
    char                profilePrefix[PROFILE_PREFIX_LENGTH + 1]; //!< Input - The profile prefix.
    SAFServiceResult    safServiceResult;                         //!< Output - Contains the SAF return code and RACF return and reason codes.
} SyncToThreadParmsKey2;

/**
 * Validate SyncToThreadParms, then copy them into key 2 storage for use by the
 * metal C routine.  memcpy_sk(8) does the copying.
 *
 * @param key2Parms_p The parm structure to copy into (in key 2)
 * @param parms_p     The parm structure to validate and copy from (in key 8).
 *
 * @return 4 if no saf credential token. 0 if there is a saf credential token.
 */
int validateSyncToThreadParms(SyncToThreadParmsKey2* key2Parms_p, SyncToThreadParms* parms_p) {

    int rc = 0;
    memset(key2Parms_p, 0, sizeof(SyncToThreadParmsKey2));

    int profilePrefixLen = parms_p->profilePrefixLen;
    if (profilePrefixLen > PROFILE_PREFIX_LENGTH) {
        profilePrefixLen = PROFILE_PREFIX_LENGTH;
    }
    memcpy_sk(key2Parms_p->profilePrefix, parms_p->profilePrefix, profilePrefixLen, 8);

    if (parms_p->safCredentialToken != NULL) {
        memcpy_sk(&key2Parms_p->safCredentialToken, parms_p->safCredentialToken, sizeof(RegistryToken), 8);
    } else {
        rc = 4;
    }

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(1),
                    "copied parms to key 2",
                    TRACE_DATA_FUNCTION,
                    TRACE_DATA_INT(rc, "rc"),
                    TRACE_DATA_RAWDATA(sizeof(SyncToThreadParmsKey2), key2Parms_p, "SyncToThreadParmsKey2"),
                    TRACE_DATA_END_PARMS);
    }
    return rc;
}

/**
 * Cleanup any storage malloc'ed when copying the parms from key 8 to key 2,
 * and copy back to key 8 any output parms.
 *
 * @param key2Parms_p The parm structure to clean up and copy back from.
 * @param parms_p     The parm structure to copy back into.
 *
 * @return 0 if all is well. Non-zero otherwise.
 */
int cleanupSyncToThreadParms(SyncToThreadParmsKey2* key2Parms_p, SyncToThreadParms* parms_p) {

    // Copy back the SAF results.
    memcpy_dk(parms_p->safServiceResult, &key2Parms_p->safServiceResult, sizeof(SAFServiceResult), 8);

    return 0;
}

/**
 * Check SURROGAT class to see if user is permitted to
 * sync to thread.
 *
 * @param key2Parms_p The key2 parm structure.
 * @param acee_p      The acee to get the user out of.
 *
 * @return 1 if sync to thread is permitted. Zero otherwise.
 */
int syncPermitted(SyncToThreadParmsKey2* key2Parms_p, acee* acee_p) {
    char entityName[20];
    int permitted = 0;

    memcpy(entityName, "BBG.SYNC.", strlen("BBG.SYNC."));
    memcpy(&(entityName[9]), acee_p->aceeuser._aceeusri, acee_p->aceeuser._aceeusrl);
    entityName[9+acee_p->aceeuser._aceeusrl] = 0;

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(2),
                    TRACE_DESC_FUNCTION_ENTRY,
                    TRACE_DATA_INT(acee_p->aceeuser._aceeusrl, "acee user length"),
                    TRACE_DATA_RAWDATA(sizeof(entityName), entityName, "entity name"),
                    TRACE_DATA_END_PARMS);
    }

    asxb* asxb_p = ((ascb*)(((psa*)0)->psaaold))->ascbasxb;
    int rc = checkAuthorizationFast(&key2Parms_p->safServiceResult.safResults,
                                    0,
                                    ASIS,
                                    NULL,
                                    NULL, // RACO_CB
                                    asxb_p->asxbsenv,
                                    READ,
                                    NULL,
                                    "SURROGAT",
                                    entityName);
    if (rc == 0) {
        if (key2Parms_p->safServiceResult.safReturnCode == 0 ) {
            permitted = 1;
        } else {
            if (key2Parms_p->safServiceResult.safReturnCode == 4
                && key2Parms_p->safServiceResult.racfReturnCode == 4) {
                // 4/4/x possibly means the class is not RACLISTed.  RACLIST it, then re-run
                // the authorization check.
                rc = raclist(NULL, NULL, CREATE, "SURROGAT");
                if (rc == 0) {
                    // RE-Perform the FASTAUTH check.
                    rc = checkAuthorizationFast(&key2Parms_p->safServiceResult.safResults,
                                                0,
                                                ASIS,
                                                NULL,
                                                NULL, // RACO_CB
                                                asxb_p->asxbsenv,
                                                READ,
                                                NULL,
                                                "SURROGAT",
                                                entityName);
                    if (rc == 0) {
                        if (key2Parms_p->safServiceResult.safReturnCode == 0 ) {
                            permitted = 1;
                        }
                    }
                }
            }
            if (rc == 0) {
                key2Parms_p->safServiceResult.safServiceCode = RACROUTE_FASTAUTH; // rc == 0 means RACROUTE was invoked.
            }
        }
    }

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(3),
                    TRACE_DESC_FUNCTION_EXIT,
                    TRACE_DATA_INT(permitted, "permitted"),
                    TRACE_DATA_END_PARMS);
    }

    return permitted;
}

/**
 * Check FACILITY class to see if the server has requested access to
 * the input entity name.
 *
 * @param entityName The entity name to use.
 * @param access_level The access level to be checked.
 *
 * @return 1 if server has requested access. Zero otherwise.
 */
int serverHasAccess(const char* entityName, saf_access_level access_level) {
    int access = 0;
    SAFServiceResult    safServiceResult;
    asxb* asxb_p = ((ascb*)(((psa*)0)->psaaold))->ascbasxb;
    int rc = checkAuthorizationFast(&safServiceResult.safResults,
                                    1,    // When check for CONTROL and server only has
                                          // READ access a message comes out.
                                          // Set flag to suppress it.
                                    ASIS,
                                    NULL,
                                    NULL, // RACO_CB
                                    asxb_p->asxbsenv,
                                    access_level,
                                    NULL,
                                    "FACILITY",
                                    entityName);
    if (rc == 0) {
        if (safServiceResult.safReturnCode == 0 ) {
            access = 1;
        }
    }
    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(4),
                    TRACE_DESC_FUNCTION_EXIT,
                    TRACE_DATA_RAWDATA(strlen(entityName), entityName, "entity name"),
                    TRACE_DATA_INT(access_level, "access level"),
                    TRACE_DATA_INT(access, "access"),
                    TRACE_DATA_END_PARMS);
    }

    return access;
}

/**
 * Check FACILITY class to see if the server has READ access to
 * the input entity name.
 *
 * @param entityName The entity name to use.
 *
 * @return 1 if server has READ access. Zero otherwise.
 */
int serverHasReadAccess(const char* entityName) {
    // BBG.SYNC.<profilePrefix> CLASS(FACILITY) ID(<server user ID>) ACC(READ)
    // when READ access sync to thread is enabled
    server_process_data* spd = getServerProcessData();
    if (spd->checked_for_read_access == 0) {
        if (serverHasAccess(entityName, READ)){
            spd->sync_to_thread_enabled = 1;
        } else {
            spd->sync_to_thread_enabled = 0;
        }
        spd->checked_for_read_access = 1;
    }
    return spd->sync_to_thread_enabled;
}

/**
 * Invoke BPX4TLS with the specified function code.
 *
 * @param functionCode    The BPX4TLS function to invoke.
 * @param serviceResult_p Pointer to SAF service result area.
 *
 * @return The BPX4TLS return value is returned.
 */
int invokeBPX4TLS (int functionCode, SAFServiceResult* serviceResult_p) {
    int  rc = 0;
    int  rsn = 0;
    int  rv = 0;
    char tlsChar[9];

    BPX4TLS(functionCode,          /* Function code  */
            0,                     /* Identity Type  */
            0,                     /* Identity Len   */
            tlsChar,               /* Identity       */
            0,                     /* Password Len   */
            tlsChar,               /* Password       */
            0,                     /* Option Flags   */
            &rv,                   /* Return Value   */
            &rc,                   /* Return Code    */
            &rsn);                 /* Reason Code    */
    if (rv != 0) {
        if (serviceResult_p) {
            if (functionCode == TLS_DELETE_THREAD_SEC) {
                serviceResult_p->safServiceCode = BPX4TLS_DELETE_THREAD_SEC;
            } else {
                serviceResult_p->safServiceCode = BPX4TLS_TASK_ACEE;
            }
            serviceResult_p->bpx4tlsResults.bpx4tlsReturnValue = rv;
            serviceResult_p->bpx4tlsResults.bpx4tlsReturnCode = rc;
            serviceResult_p->bpx4tlsResults.bpx4tlsReasonCode = rsn;
        }
        if (TraceActive(trc_level_exception)) {
            TraceRecord(trc_level_exception,
                        TP(5),
                        "invoke BPX4TLS",
                        TRACE_DATA_INT(functionCode, "function code"),
                        TRACE_DATA_INT(rv, "return value"),
                        TRACE_DATA_INT(rc, "return code"),
                        TRACE_DATA_HEX_INT(rsn,"reason code"),
                        TRACE_DATA_END_PARMS);
        }
    }
    return rv;
}

/**
 * See security_saf_sync_to_thread.h for method description.
 */
void syncToThread(SyncToThreadParms* parms) {

    SyncToThreadParmsKey2 key2Parms;
    RegistryDataArea regDataArea;
    int rc = 0;
    void* __ptr32 nullPtr = 0;
    psa* psa_p = NULL;
    tcb* tcb_p = (tcb*) psa_p->psatold;
    acee* __ptr32 oldACEE_p = (acee* __ptr32) tcb_p->tcbsenv;

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(6),
                    TRACE_DESC_FUNCTION_ENTRY,
                    TRACE_DATA_PTR(parms, "SyncToThreadParms ptr"),
                    TRACE_DATA_END_PARMS);
    }

    // Copy the parms into key 2.
    rc = validateSyncToThreadParms(&key2Parms, parms);

    char entityName[9+sizeof(key2Parms.profilePrefix)];
    // BBG.SYNC.<profilePrefix> CLASS(FACILITY) ID(<server user ID>) ACC(READ)
    memcpy(entityName, "BBG.SYNC.", strlen("BBG.SYNC."));
    memcpy(&(entityName[9]), key2Parms.profilePrefix, sizeof(key2Parms.profilePrefix));

    // Check to see if sync to thread is enabled or should be enabled.
    // Someone could have bypassed the isSyncToThreadEnabled call.
    if (serverHasReadAccess(entityName)) {
        // if BPX4TLS fails get out
        if (invokeBPX4TLS(TLS_DELETE_THREAD_SEC, &key2Parms.safServiceResult) != 0) {
            // Set the failure rc and exit.
            key2Parms.safServiceResult.wasReturnCode = SYNC_TO_THREAD_RC_BPX4TLS_DELETE;
            cleanupSyncToThreadParms(&key2Parms, parms);
            return;
        }
        //PSATOLD->TCBSENV = 0;
        memcpy_dk(&(tcb_p->tcbsenv), &nullPtr, sizeof(tcb_p->tcbsenv), 0);
        /* Delete the old TCBSENV ACEE if there was one */
        if (oldACEE_p != 0) {
            deleteACEEObject(oldACEE_p, NULL);
        }
        if (TraceActive(trc_level_detailed)) {
            TraceRecord(trc_level_detailed,
                        TP(7),
                        "after delete acee",
                        TRACE_DATA_PTR(oldACEE_p, "old acee ptr"),
                        TRACE_DATA_END_PARMS);
        }
        // if caller passed in a token.
        if (rc == 0) {
            if (TraceActive(trc_level_detailed)) {
                TraceRecord(trc_level_detailed,
                            TP(8),
                            "token passed",
                            TRACE_DATA_END_PARMS);
            }
            // Retrieve SAF NSC data (RACO_CB) from the registry.
            rc = registryGetAndSetUsed(&key2Parms.safCredentialToken, &regDataArea);
            if (rc != 0) {
                // Set the failure rc and exit.
                key2Parms.safServiceResult.wasReturnCode = rc;
                cleanupSyncToThreadParms(&key2Parms, parms);
                return;
            }
            if (TraceActive(trc_level_detailed)) {
                TraceRecord(trc_level_detailed,
                            TP(9),
                            "retrieved RegistryDataArea, which contains ref to RACO_CB",
                            TRACE_DATA_RAWDATA(sizeof(RegistryDataArea), &regDataArea, "RegistryDataArea"),
                            TRACE_DATA_END_PARMS);
            }
            RACO_CB* raco_cb_p = *((RACO_CB**)&regDataArea);
            RACO* raco_p = &raco_cb_p->ENVR_RACO;
            acee* __ptr32 myAcee_p = (acee* __ptr32) createACEEFromRACO(raco_p, &key2Parms.safServiceResult);
            if (myAcee_p == 0){
                // Twas did this BPX4TLS not sure why?
                invokeBPX4TLS(TLS_DELETE_THREAD_SEC, NULL);
                // Return token to registry.
                registrySetUnused(&key2Parms.safCredentialToken, TRUE);
                // If createACEEFromRACO did not set wasReturnCode, set wasReturnCode and exit.
                if (key2Parms.safServiceResult.wasReturnCode == 0) {
                    key2Parms.safServiceResult.wasReturnCode = SYNC_TO_THREAD_RC_ACEE_CREATE;
                }
                cleanupSyncToThreadParms(&key2Parms, parms);
                return;
            }
            server_process_data* spd = getServerProcessData();
            // BBG.SYNC.<profilePrefix> CLASS(FACILITY) ID(<server user ID>) ACC(CONTROL)
            // when CONTROL access SURROGAT checks are not needed
            if (spd->checked_for_control_access == 0){
                // BBG.SYNC.<profilePrefix> CLASS(FACILITY) ID(<server user ID>) ACC(CONTROL)
                if (serverHasAccess(entityName, CONTROL)){
                    spd->skipping_surrogat_checks = 1;
                }
                spd->checked_for_control_access = 1;
            }
            if (spd->skipping_surrogat_checks == 0) {
                // check SURROGAT stuff to see if sync is permitted
                // if sync is not permitted get out
                if (!syncPermitted(&key2Parms, myAcee_p)) {
                    // delete acee
                    deleteACEEObject(myAcee_p, NULL);
                    invokeBPX4TLS(TLS_DELETE_THREAD_SEC, NULL);
                    // Return token to registry.
                    registrySetUnused(&key2Parms.safCredentialToken, TRUE);
                    // Set the failure rc and exit.
                    key2Parms.safServiceResult.wasReturnCode = SYNC_TO_THREAD_RC_SYNC_NOT_PERMITTED;
                    cleanupSyncToThreadParms(&key2Parms, parms);
                    return;
                }
            }
            //PSATOLD->TCBSENV = myAcee_p;
            memcpy_dk(&(tcb_p->tcbsenv), &myAcee_p, sizeof(tcb_p->tcbsenv), 0);
            rc = invokeBPX4TLS(TLS_TASK_ACEE, &key2Parms.safServiceResult);
            // if error get out
            if (rc != 0) {
                //PSATOLD->TCBSENV = 0;
                memcpy_dk(&(tcb_p->tcbsenv), &nullPtr, sizeof(tcb_p->tcbsenv), 0);
                deleteACEEObject(myAcee_p, NULL);
                // Return token to registry.
                registrySetUnused(&key2Parms.safCredentialToken, TRUE);
                // Set the failure rc and exit.
                key2Parms.safServiceResult.wasReturnCode = SYNC_TO_THREAD_RC_BPX4TLS_ACEE;
                cleanupSyncToThreadParms(&key2Parms, parms);
                return;
            }
            // Return token to registry.
            registrySetUnused(&key2Parms.safCredentialToken, TRUE);
        } else {
            rc = 0;
        }
    } else {
        rc = SYNC_TO_THREAD_RC_DISABLED;
    }

    // Copy back the SAF results.
    key2Parms.safServiceResult.wasReturnCode = rc;
    cleanupSyncToThreadParms(&key2Parms, parms);
    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(10),
                    TRACE_DESC_FUNCTION_EXIT,
                    TRACE_DATA_PTR32(tcb_p->tcbsenv, "tcbsenv"),
                    TRACE_DATA_INT(rc, "return code"),
                    TRACE_DATA_END_PARMS);
    }
    return;
}

/**
 * The IsSyncToThreadEnabledParmsKey2 passed into isSyncToThreadEnabled are copied from
 * key 8 into this struct, which is in key 2.
 */
typedef struct {
    char                profilePrefix[PROFILE_PREFIX_LENGTH + 1]; //!< Input - The profile prefix.
    int                 syncToThreadEnabled;                      //!< Output -
} IsSyncToThreadEnabledParmsKey2;

/**
 * See security_saf_sync_to_thread.h for method description.
 */
void isSyncToThreadEnabled(IsSyncToThreadEnabledParms* parms_p) {

    IsSyncToThreadEnabledParmsKey2 key2Parms;
    memset(&key2Parms, 0, sizeof(IsSyncToThreadEnabledParmsKey2));
    int profilePrefixLen = parms_p->profilePrefixLen;
    if (profilePrefixLen > PROFILE_PREFIX_LENGTH) {
        profilePrefixLen = PROFILE_PREFIX_LENGTH;
    }
    memcpy_sk(key2Parms.profilePrefix, parms_p->profilePrefix, profilePrefixLen, 8);
    char entityName[9+sizeof(key2Parms.profilePrefix)];
    // BBG.SYNC.<profilePrefix> CLASS(FACILITY) ID(<server user ID>) ACC(READ)
    memcpy(entityName, "BBG.SYNC.", strlen("BBG.SYNC."));
    memcpy(&(entityName[9]), key2Parms.profilePrefix, sizeof(key2Parms.profilePrefix));

    if (serverHasReadAccess(entityName)) {
        key2Parms.syncToThreadEnabled = 1;
    }
    memcpy_dk(parms_p->syncToThreadEnabled, &key2Parms.syncToThreadEnabled, sizeof(key2Parms.syncToThreadEnabled), 8);
    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(11),
                    TRACE_DESC_FUNCTION_EXIT,
                    TRACE_DATA_INT(key2Parms.syncToThreadEnabled, "sync to thread enabled"),
                    TRACE_DATA_END_PARMS);
    }
    return;
}

/**
 * See security_saf_sync_to_thread.h for method description.
 */
void resetSyncToThreadEnabled(ResetSyncToThreadEnabledParms* parms_p) {
    int rc = 0;
    server_process_data* spd = getServerProcessData();
    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(12),
                    TRACE_DESC_FUNCTION_ENTRY,
                    TRACE_DATA_STRING(spd->checked_for_read_access ? "ON" : "OFF", "checked_for_read_access"),
                    TRACE_DATA_END_PARMS);
    }
    spd->sync_to_thread_enabled = 0;
    spd->checked_for_read_access = 0;
    spd->skipping_surrogat_checks = 0;
    spd->checked_for_control_access =0;
    memcpy_dk(parms_p->returnCode, &rc, sizeof(rc), 8);
    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(13),
                    TRACE_DESC_FUNCTION_EXIT,
                    TRACE_DATA_STRING(spd->checked_for_read_access ? "ON" : "OFF", "checked_for_read_access"),
                    TRACE_DATA_END_PARMS);
    }
    return;
}
