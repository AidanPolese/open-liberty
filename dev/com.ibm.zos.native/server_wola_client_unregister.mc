/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2014
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
#include <metal.h>

#include "include/ieantc.h"
#include "include/mvs_cell_pool_services.h"
#include "include/mvs_iarv64.h"
#include "include/mvs_plo.h"
#include "include/mvs_psw.h"
#include "include/mvs_user_token_manager.h"
#include "include/mvs_utils.h"
#include "include/server_wola_client.h"
#include "include/server_wola_connection_pool.h"
#include "include/server_wola_shared_memory_anchor.h"
#include "include/server_wola_service_queues.h"


/**
 * Unregister with server.
 *
 * @param parms_p A pointer to the parameter structure.
 */
void wolaUnregister(WOLAUnregisterParms_t* parms_p) {
    unsigned int localRC = 0;
    unsigned int localRSN = 0;

    if (parms_p->rc_p == 0) {
        return;
    }
    bbgz_psw pswFromLinkageStack;
    extractPSWFromLinkageStack(&pswFromLinkageStack);
    if (parms_p->rsn_p == 0) {
        localRC =  WOLA_RC_BAD_PARM;
        memcpy_dk(parms_p->rc_p, &localRC, sizeof(localRC), pswFromLinkageStack.key);
        return;
    }

    GetWolaRegistrationData_t wolaRegistrationData;
    WolaRegistration_t* regEntry_p = getWolaRegistration(parms_p->registerName, &wolaRegistrationData);

    if (regEntry_p == NULL) {
        localRC = WOLA_RC_ERROR8;
        localRSN = WOLA_RSN_MISSING_BBOARGE_NAMETOK;
        memcpy_dk(parms_p->rc_p, &localRC, sizeof(localRC), pswFromLinkageStack.key);
        memcpy_dk(parms_p->rsn_p, &localRSN, sizeof(localRSN), pswFromLinkageStack.key);
        return;
    }

    unsigned int forceSpecified = 0;
    if ((parms_p->unregisterFlags & WOLA_UNREGISTER_FLAGS_FORCE) == WOLA_UNREGISTER_FLAGS_FORCE) {
        forceSpecified = 1;
    }
    if ((forceSpecified == 1) && (wolaRegistrationData.registrationflags.unregisterCalled == 0)) {
        localRC = WOLA_RC_ERROR8;
        localRSN = WOLA_RSN_FORCE_NOT_ALLOWED;
        memcpy_dk(parms_p->rc_p, &localRC, sizeof(localRC), pswFromLinkageStack.key);
        memcpy_dk(parms_p->rsn_p, &localRSN, sizeof(localRSN), pswFromLinkageStack.key);
        return;
    }

    if (forceSpecified == 0) {
        unsigned int programMaskCC = 3;
        while (programMaskCC != 0) {
            /* Only unregister if we haven't done so yet.     */
            if (wolaRegistrationData.registrationflags.unregisterCalled == 0) {

                /* Use a PLO to turn on the unregister bit.     */
                /* This guarantees only one task will do it.    */
                PloCompareAndSwapAreaDoubleWord_t stckArea;
                PloCompareAndSwapAreaDoubleWord_t flagsArea;
                stckArea.compare_p = &(regEntry_p->stckLastStateChange);
                stckArea.expectedValue = wolaRegistrationData.stckLastStateChange;
                stckArea.replaceValue = wolaRegistrationData.stckLastStateChange;

                WolaRegistrationFlags_t newFlags;
                flagsArea.compare_p = &(regEntry_p->flags);
                memcpy(&(flagsArea.expectedValue), &(wolaRegistrationData.registrationflags), sizeof(flagsArea.expectedValue));
                memcpy(&newFlags, &(wolaRegistrationData.registrationflags), sizeof(newFlags));
                newFlags.unregisterCalled = 1;
                memcpy(&(flagsArea.replaceValue), &newFlags, sizeof(flagsArea.replaceValue));

                programMaskCC = ploDoubleCompareAndSwapDoubleWord(regEntry_p, &stckArea, &flagsArea);
                if (programMaskCC == 0) {
                    unregisterServiceQueues(regEntry_p);

                    int cleanupConnPoolRC = cleanupConnectionPool(regEntry_p, wolaRegistrationData.stckLastStateChange);
                    if (cleanupConnPoolRC != 0) {
                        localRC = WOLA_RC_WARN4;
                        localRSN = WOLA_RSN_FORCE_CONN_ACTIVE;
                    }
                } else if (programMaskCC == 2) {
                    /* CC=2 means the CS on the flags failed.       */
                    // for CC=2 we need to use the updated flags when we try again.
                    memcpy(&(wolaRegistrationData.registrationflags), &(flagsArea.expectedValue) ,sizeof(wolaRegistrationData.registrationflags));
                } else {
                    /*----------------------------------------------*/
                    /* Anything other than 2 is bad and should      */
                    /* result in an error condition (most likely    */
                    /* CC=1, the first CS failed, which is the RGE  */
                    /* STCK, meaning the RGE has been reused).      */
                    /*----------------------------------------------*/
                    localRC = WOLA_RC_ERROR8;
                    localRSN = WOLA_RSN_REGISTRATION_REUSED;
                    programMaskCC = 0;  // Get out of loop
                }
            } else {
                /*----------------------------------------------*/
                /* The unregister bit was already set.  Tell    */
                /* the caller.  Note that we could also send    */
                /* back the "RGE not found" return code here,   */
                /* but if the caller issued a register at this  */
                /* point, it would fail since the RGE is still  */
                /* around, and that would be confusing.         */
                /*----------------------------------------------*/
                localRC  = WOLA_RC_ERROR8;
                localRSN = WOLA_RSN_DUP_UNREG;
                programMaskCC = 0; // Get out of loop
            }
        } // end while (programMaskCC != 0)
    } else {  // force
        int cleanupConnPoolRC = cleanupConnectionPoolForce(regEntry_p, wolaRegistrationData.stckLastStateChange);
        if (cleanupConnPoolRC != 0) {
            localRC = WOLA_RC_ERROR8;
            localRSN = WOLA_RSN_CONN_POOL_CLEANUP_FAIL;
        }
    }

    // ----------------------------------------------------------------
    // Push output parameters back to caller
    // ----------------------------------------------------------------
    memcpy_dk(parms_p->rc_p, &localRC, sizeof(localRC), pswFromLinkageStack.key);
    memcpy_dk(parms_p->rsn_p, &localRSN, sizeof(localRSN), pswFromLinkageStack.key);
}
