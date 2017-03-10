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

#include "include/mvs_psw.h"
#include "include/mvs_utils.h"
#include "include/server_wola_client.h"
#include "include/server_wola_client_connection_get.h"
#include "include/server_wola_connection_pool.h"

/**
 * Get a connection.
 *
 * @param regEntry_p   Pointer to registration.
 * @param waitTime     An integer containing the number of seconds to wait for the
 *                     connection to complete before returning
 *                     a connection unavailable reason code. A value of 0 implies
 *                     there is no wait time and the API waits indefinitely.
 * @param handle_p     Output area to get client connection handle.
 * @param cicsParms_p  A pointer to cics-specific parms, in the client's key.
 * @param reasonCode_p Output area to get the reason code.
 *
 * @return 0 on success. non 0 on failure.
 */
unsigned int wolaConnectionGetCommon(WolaRegistration_t* regEntry_p, unsigned int waitTime,
                                     WolaClientConnectionHandle_t* handle_p,
                                     struct bboapc1p* cicsParms_p,
                                     unsigned int*  reasonCode_p) {

    unsigned int rc = 0;
    if (regEntry_p->flags.active == 0) {
        *reasonCode_p = WOLA_RSN_REG_INACT;
        return WOLA_RC_ERROR8;
    }

    // Check to see if the server is down, or has been re-started.  If either of these are true,
    // we need to re-bind to the new server.  This is an angel concept, not a WOLA concept, but
    // there are WOLA implications.  The rebind makes sure we are calling into the SCFM of the
    // new server instance.  The client RGE needs to be updated with the new server STCK.
    if (regEntry_p->serverRegistration_p->flags.active == 0) {
        *reasonCode_p = WOLA_RSN_BADCONN; // Pretend LC connect failed.
        return WOLA_RC_SEVERE12;
    }

    if (regEntry_p->serverRegistration_p->stckLastStateChange != regEntry_p->serverStartSTCK) {
        *reasonCode_p = WOLA_RSN_INTERNAL_REBIND_REQUIRED;
        return WOLA_RC_SEVERE12;
    }

    unsigned int connGetRC = 256;
    connGetRC = getPooledConnection(regEntry_p, regEntry_p->stckLastStateChange, waitTime, handle_p, cicsParms_p);
    if (connGetRC != 0) {
        if (connGetRC == GetConn_RC_ExceededMaxConnections) {
            rc = WOLA_RC_ERROR8;
            *reasonCode_p = WOLA_RSN_MAXCONN;
        } else if (connGetRC == GetConn_RC_LocalCommSAFError) {
            rc = WOLA_RC_SEVERE12;
            *reasonCode_p = WOLA_RSN_SAF_UNAUTH;
        } else {
            rc = WOLA_RC_SEVERE12;
            *reasonCode_p = WOLA_RSN_BADCONN;
        }
    }
    return rc;
}

/**
 * Get connection.
 *
 * @param parms_p A pointer to the parameter structure.
 */
void wolaConnectionGet(WOLAConnectionGetParms_t* parms_p) {
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
        localRC  = WOLA_RC_ERROR8;
        localRSN = WOLA_RSN_MISSING_BBOARGE_NAMETOK;
        memcpy_dk(parms_p->rc_p, &localRC, sizeof(localRC), pswFromLinkageStack.key);
        memcpy_dk(parms_p->rsn_p, &localRSN, sizeof(localRSN), pswFromLinkageStack.key);
        return;
    }

    // If we're in CICS, copy out the CICS parameters.
    struct bboapc1p cicsParms;
    if (parms_p->cicsParms_p != NULL) {
        memcpy_sk(&cicsParms, parms_p->cicsParms_p, sizeof(cicsParms), pswFromLinkageStack.key);
    } else {
        memset(&cicsParms, 0, sizeof(cicsParms));
    }

    WolaClientConnectionHandle_t  clientConnectionHandle;
    localRC = wolaConnectionGetCommon(regEntry_p, parms_p->waitTime, &clientConnectionHandle, &cicsParms, &localRSN);

    // ----------------------------------------------------------------
    // Push output parameters back to caller
    // ----------------------------------------------------------------
    if (localRC == 0) {
        memcpy_dk(parms_p->connectionHandle_p, &clientConnectionHandle, sizeof(WolaClientConnectionHandle_t), pswFromLinkageStack.key);
    }
    memcpy_dk(parms_p->rc_p, &localRC, sizeof(localRC), pswFromLinkageStack.key);
    memcpy_dk(parms_p->rsn_p, &localRSN, sizeof(localRSN), pswFromLinkageStack.key);
}
