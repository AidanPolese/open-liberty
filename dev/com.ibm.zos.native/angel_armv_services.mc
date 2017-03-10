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
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#include "include/angel_armv_services.h"
#include "include/angel_bgvt_services.h"
#include "include/bpx_load.h"
#include "include/ieantc.h"

#include "include/gen/ihapsa.h"
#include "include/gen/ihaascb.h"

/**
 * Attach to the ARMV.
 *
 * @param sgoo_p A pointer to the SGOO for this system.
 * @param processDataArmvSeq_p A pointer to the byte in the process data where
 *                             the current ARMV sequence number is stored.  If
 *                             this address space does not have process data
 *                             yet, this parameter can be NULL.
 * @param recoveryDecrementArmvUseCount_p A pointer to a byte which is set to 1
 *                                        to indicate that the ARMV use count
 *                                        was incremented.  If there is a RESMGR
 *                                        set up for this address space and it
 *                                        cleans up all attachments to ARMVs,
 *                                        this parameter can be NULL.
 * @param recoveryArmvToDecrement_p A pointer to a double word where a pointer
 *                                  to the ARMV whose use count was incremented
 *                                  is stored.  If there is a RESMGR set up for
 *                                  this address space and it cleans up all
 *                                  attachments to ARMVs, this parameter can be
 *                                  NULL.
 * @param nameTokenPrefix_p A seven character prefix to use when creating the
 *                          name token for this ARMV attachment.
 *
 * @return A pointer to the ARMV that we connected to.
 */
static bbgzarmv* attachToARMVwithLockCommon(bbgzsgoo* sgoo_p, volatile unsigned char* processDataArmvSeq_p, unsigned char* recoveryDecrementArmvUseCount_p, void** recoveryArmvToDecrement_p, char* nameTokenPrefix_p) {
    // -----------------------------------------------------------
    // Get an ENQ, and check to see if we really need to attach.
    // -----------------------------------------------------------
    bbgzarmv* armv_p;
    enqtoken enq_token;
    get_enq_exclusive_step(BBGZ_ENQ_QNAME, ANGEL_ARMV_CONNECT_ENQ, &enq_token);

    for (unsigned char resolved_armv = 0; resolved_armv == 0;) {
        armv_p = (bbgzarmv*)sgoo_p->bbgzsgoo_armv;
        if ((processDataArmvSeq_p == NULL) || (armv_p->bbgzarmv_instancecount > (*processDataArmvSeq_p))) {
            bbgzarmv_usecount_s old_armv_usecount;
            bbgzarmv_usecount_s new_armv_usecount;

            int cs_rc = -1;

            memcpy(&old_armv_usecount,
                   &(armv_p->bbgzarmv_usecount),
                   sizeof(old_armv_usecount));

            // -------------------------------------------------------
            // If the ARMV we picked is inactive, we need to loop
            // again and take the (hopefully new) ARMV off of the
            // SGOO and try again.
            // -------------------------------------------------------
            if (old_armv_usecount.inactive == 0) {
                memcpy(&new_armv_usecount, &old_armv_usecount, sizeof(new_armv_usecount));
                new_armv_usecount.count = new_armv_usecount.count + 1;
                if (__cs1(&old_armv_usecount, &(armv_p->bbgzarmv_usecount), &new_armv_usecount) == 0) {
                    // ---------------------------------------------------
                    // If we successfully incremented the use count in
                    // this ARMV, update the angel process data armv
                    // sequence, and use this ARMV for this invoke.
                    // ---------------------------------------------------
                    if (processDataArmvSeq_p != NULL) {
                        *processDataArmvSeq_p = armv_p->bbgzarmv_instancecount;
                    }

                    // --------------------------------------------------
                    // Update recovery information if requested.
                    // --------------------------------------------------
                    if ((recoveryDecrementArmvUseCount_p != NULL) && (recoveryArmvToDecrement_p != NULL)) {
                        *recoveryDecrementArmvUseCount_p = 1;
                        *recoveryArmvToDecrement_p = armv_p;
                    }

                    // ---------------------------------------------------
                    // Make a name token representing this ARMV
                    // attachment.
                    // ---------------------------------------------------
                    char armv_name[16];
                    char armv_token[16];

                    psa* psa_p = NULL;
                    ascb* ascb_p = psa_p->psaaold;
                    assb* assb_p = ascb_p->ascbassb;

                    memset(armv_name, 0, sizeof(armv_name));
                    memcpy(armv_name, nameTokenPrefix_p,
                           strlen(nameTokenPrefix_p));
                    armv_name[7] = armv_p->bbgzarmv_instancecount;
                    memcpy(&(armv_name[8]), &(assb_p->assbstkn), sizeof(assb_p->assbstkn));

                    memset(armv_token, 0, sizeof(armv_token));
                    memcpy(armv_token, &armv_p, sizeof(armv_p));

                    int armv_nametoken_rc;
                    iean4cr(IEANT_SYSTEM_LEVEL,
                            armv_name,
                            armv_token,
                            IEANT_PERSIST,
                            &armv_nametoken_rc);

                    resolved_armv = 1;
                }
            }
        } else {
            // -------------------------------------------------------
            // No need to re-attach -- current ARMV is OK.
            // -------------------------------------------------------
            resolved_armv = 1;
        }
    }

    release_enq(&enq_token);

    return armv_p;
}

// Attach a server process to the current ARMV.
#pragma noinline(attachToARMVwithLock) // Compiler workaround -- PMR xxxxx
bbgzarmv* attachToARMVwithLock(bbgzsgoo* sgoo_p, angel_process_data* apd_p, angel_server_pc_recovery* recovery_p) {
    return attachToARMVwithLockCommon(sgoo_p,
                                      (apd_p != NULL) ? &(apd_p->cur_armv_seq) : NULL,
                                      (recovery_p != NULL) ? &(recovery_p->decrement_armv_use_count) : NULL,
                                      (recovery_p != NULL) ? &(recovery_p->armv_to_decrement) : NULL,
                                      ARMV_ATTACHMENT_NAME_TOKEN_PREFIX);
}

// Attach a client process to the current ARMV.
#pragma noinline(attachClientToARMVwithLock) // Compiler workaround -- PMR xxxxx
bbgzarmv* attachClientToARMVwithLock(bbgzsgoo* sgoo_p, AngelClientProcessData_t* acpd_p, angel_client_pc_recovery* recovery_p) {
    return attachToARMVwithLockCommon(sgoo_p,
                                      (acpd_p != NULL) ? &(acpd_p->curArmvSeq) : NULL,
                                      (recovery_p != NULL) ? &(recovery_p->fsm_decrementArmvUseCount) : NULL,
                                      (recovery_p != NULL) ? &(recovery_p->fsm_armvToDecrement) : NULL,
                                      ARMV_ATTACHMENT_CLIENT_NAME_TOKEN_PREFIX);
}

/**
 * Detaches from an ARMV.  Performs cleanup on the ARMV if we're the last one out.
 *
 * @param armv_p A pointer to the ARMV that we are detaching from.
 * @param recoveryDecrementArmvUseCount_p A pointer to a byte which is set to 0
 *                                        to indicate that the ARMV use count
 *                                        was decremented.  If there is a RESMGR
 *                                        set up for this address space and it
 *                                        cleans up all attachments to ARMVs,
 *                                        this parameter can be NULL.
 * @param recoveryArmvToDecrement_p A pointer to a double word where a pointer
 *                                  to the ARMV whose use count is to be decremented
 *                                  is stored.  If there is a RESMGR set up for
 *                                  this address space and it cleans up all
 *                                  attachments to ARMVs, this parameter can be
 *                                  NULL.
 * @param nameTokenPrefix_p The prefix to use when the name token is removed.
 */
#pragma noinline(detachFromARMVCommon) // Compiler workaround -- PMR xxxxx
static void detachFromARMVCommon(bbgzarmv* armv_p, unsigned char* recoveryDecrementArmvUseCount_p, void** recoveryArmvToDecrement_p, char* nameTokenPrefix_p) {
    // -----------------------------------------------------------------------
    // Remove the name token.
    // -----------------------------------------------------------------------
    char armv_name[16];

    psa* psa_p = NULL;
    ascb* ascb_p = psa_p->psaaold;
    assb* assb_p = ascb_p->ascbassb;

    memset(armv_name, 0, sizeof(armv_name));
    memcpy(armv_name, nameTokenPrefix_p,
           strlen(nameTokenPrefix_p));
    armv_name[7] = armv_p->bbgzarmv_instancecount;
    memcpy(&(armv_name[8]), &(assb_p->assbstkn),
           sizeof(assb_p->assbstkn));

    int armv_nametoken_rc;
    iean4dl(IEANT_SYSTEM_LEVEL,
            armv_name,
            &armv_nametoken_rc);

    // ---------------------------------------------------
    // Update recovery information if requested.
    // ---------------------------------------------------
    if ((recoveryDecrementArmvUseCount_p != NULL) &&  (recoveryArmvToDecrement_p != NULL)) {
        *recoveryDecrementArmvUseCount_p = 0;
        *recoveryArmvToDecrement_p = NULL;
    }

    // -----------------------------------------------------------
    // Decrement the in-use count.
    // -----------------------------------------------------------
    int cs_rc = -1;
    bbgzarmv_usecount_s old_armv_usecount;
    bbgzarmv_usecount_s new_armv_usecount;

    memcpy(&old_armv_usecount,
           &(armv_p->bbgzarmv_usecount),
           sizeof(old_armv_usecount));

    while (cs_rc != 0) {
        memcpy(&new_armv_usecount, &old_armv_usecount, sizeof(new_armv_usecount));
        new_armv_usecount.count = new_armv_usecount.count - 1;
        cs_rc = __cs1(&old_armv_usecount, &(armv_p->bbgzarmv_usecount), &new_armv_usecount);
    }

    // -----------------------------------------------------------------------
    // If the ARMV is marked inactive, and we were the last ones out, free
    // the DRM attached to the ARMV.  We don't want to free the ARMV, the
    // cells are never freed (see angel_main.mc).
    // -----------------------------------------------------------------------
    if ((new_armv_usecount.count == 0) && (new_armv_usecount.inactive == 1)) {
        loadhfs_details old_drm_details;

        old_drm_details.mod_len = armv_p->bbgzarmv_drm_len;
        old_drm_details.mod_p = armv_p->bbgzarmv_drm_mod_p;
        old_drm_details.entry_p = armv_p->bbgzarmv_drm;
        memcpy(old_drm_details.delete_token,
               armv_p->bbgzarmv_drm_del_token,
               sizeof(old_drm_details.delete_token));

        unload_from_hfs(&old_drm_details);
    }
}

// Detach server from ARMV.
#pragma noinline(detachFromARMV) // Compiler workaround -- PMR xxxxx
void detachFromARMV(bbgzarmv* armv_p, angel_server_pc_recovery* recovery_p) {
    detachFromARMVCommon(armv_p, (recovery_p != NULL) ? &(recovery_p->decrement_armv_use_count) : NULL, (recovery_p != NULL) ? &(recovery_p->armv_to_decrement) : NULL, ARMV_ATTACHMENT_NAME_TOKEN_PREFIX);
}

// Detach client from ARMV.
#pragma noinline(detachClientFromARMV) // Compiler workaround -- PMR xxxxx
void detachClientFromARMV(bbgzarmv* armv_p, angel_client_pc_recovery* recovery_p) {
    detachFromARMVCommon(armv_p, (recovery_p != NULL) ? &(recovery_p->fsm_decrementArmvUseCount) : NULL, (recovery_p != NULL) ? &(recovery_p->fsm_armvToDecrement) : NULL, ARMV_ATTACHMENT_CLIENT_NAME_TOKEN_PREFIX);
}

void detachAllARMVs(unsigned char watermark, assb* assb_p, unsigned char client) {
    // -------------------------------------------------------------
    // Look for ARMV connections and disconnect.  There may be
    // more than one.  Start at the instance count in the angel
    // process data and work backwards to zero.
    // -------------------------------------------------------------
    unsigned char cur_armv_seq = watermark;
    char* nameTokenPrefix_p = (client == TRUE) ? ARMV_ATTACHMENT_CLIENT_NAME_TOKEN_PREFIX : ARMV_ATTACHMENT_NAME_TOKEN_PREFIX;

    while (cur_armv_seq > 0) {
        char armv_name[16];
        char armv_token[16];

        memset(armv_name, 0, sizeof(armv_name));
        memcpy(armv_name, nameTokenPrefix_p,
               strlen(nameTokenPrefix_p));
        armv_name[7] = cur_armv_seq;
        memcpy(&(armv_name[8]), &(assb_p->assbstkn),
               sizeof(assb_p->assbstkn));

        memset(armv_token, 0, sizeof(armv_token));

        int armv_nametoken_rc;
        iean4rt(IEANT_SYSTEM_LEVEL,
                armv_name,
                armv_token,
                &armv_nametoken_rc);

        // -----------------------------------------------------------
        // If we found this name token, it means this address space
        // used this version of the ARMV.  We need to disconnect
        // from it.
        // -----------------------------------------------------------
        if (armv_nametoken_rc == 0) {
            bbgzarmv* armv_p;
            memcpy(&armv_p, armv_token, sizeof(armv_p));
            detachFromARMVCommon(armv_p, NULL, NULL, nameTokenPrefix_p);
        }

        // -----------------------------------------------------------
        // Look for the next ARMV.
        // -----------------------------------------------------------
        cur_armv_seq = cur_armv_seq - 1;
    }
}
