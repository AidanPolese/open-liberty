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

#include "include/mvs_utils.h"
#include "include/server_wola_client.h"
#include "include/server_wola_client_connection_get.h"
#include "include/server_wola_client_get_data.h"
#include "include/server_wola_client_receive_request_specific.h"
#include "include/server_wola_connection_pool.h"

/**
 * Host service.
 *
 * @param parms_p A pointer to the parameter structure.
 */
void wolaHostService(WOLAHostServiceParms_t* parms_p) {
    unsigned int localRC = 0;
    unsigned int localRSN = 0;
    unsigned int localRV = 0;

    if (parms_p->rc_p == 0) {
        return;
    }
    bbgz_psw pswFromLinkageStack;
    extractPSWFromLinkageStack(&pswFromLinkageStack);
    if ((parms_p->rsn_p == 0) || (parms_p->rv_p == 0)) {
        localRC =  WOLA_RC_BAD_PARM;
        memcpy_dk(parms_p->rc_p, &localRC, sizeof(localRC), pswFromLinkageStack.key);
        return;
    }

    unsigned int requestServiceNameLength = 0;
    memcpy_sk(&requestServiceNameLength, parms_p->requestServiceNameLength_p,
              sizeof(requestServiceNameLength), pswFromLinkageStack.key);

    if (requestServiceNameLength > BBOA_REQUEST_SERVICE_NAME_MAX) {
        localRC = WOLA_RC_ERROR8;
        localRSN = WOLA_RSN_SERVICE_NAME_LEN;
        memcpy_dk(parms_p->rc_p, &localRC, sizeof(localRC), pswFromLinkageStack.key);
        memcpy_dk(parms_p->rsn_p, &localRSN, sizeof(localRSN), pswFromLinkageStack.key);
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

    // If we're in CICS, copy out the CICS parameters.
    struct bboapc1p cicsParms;
    if (parms_p->cicsParms_p != NULL) {
        memcpy_sk(&cicsParms, parms_p->cicsParms_p, sizeof(cicsParms), pswFromLinkageStack.key);
    } else {
        memset(&cicsParms, 0, sizeof(cicsParms));
    }

    // get connection
    WolaClientConnectionHandle_t clientConnectionHandle;
    localRC = wolaConnectionGetCommon(regEntry_p, parms_p->waitTime, &clientConnectionHandle, &cicsParms, &localRSN);
    if (localRC != 0) {
        memcpy_dk(parms_p->rc_p, &localRC, sizeof(localRC), pswFromLinkageStack.key);
        memcpy_dk(parms_p->rsn_p, &localRSN, sizeof(localRSN), pswFromLinkageStack.key);
        return;
    }

    memcpy_dk(parms_p->connectionHandle_p, &clientConnectionHandle, sizeof(clientConnectionHandle), pswFromLinkageStack.key);

    unsigned long long requestDataLength = 0;
    localRC = wolaReceiveRequestSpecificCommon(&clientConnectionHandle,
                                               parms_p->requestServiceName_p,
                                               &requestServiceNameLength,
                                               &requestDataLength,
                                               0, // not async
                                               pswFromLinkageStack.key, &localRSN);
    // TODO requestDataLength how used? Seems bad if it comes back 0.
    if (localRC == 0) {
        localRC = wolaGetDataCommon(&clientConnectionHandle, parms_p->requestData_p, parms_p->requestDataLength,
                                    pswFromLinkageStack.key, &localRSN, &localRV);
    }

    // ----------------------------------------------------------------
    // Push output parameters back to caller
    // ----------------------------------------------------------------
    if (localRC == 0) {
        memcpy_dk(parms_p->requestServiceNameLength_p, &requestServiceNameLength, sizeof(requestServiceNameLength), pswFromLinkageStack.key);
    }
    //If return code is not 0, the client is responsible for freeing the connection.

    memcpy_dk(parms_p->rc_p, &localRC, sizeof(localRC), pswFromLinkageStack.key);
    memcpy_dk(parms_p->rsn_p, &localRSN, sizeof(localRSN), pswFromLinkageStack.key);
    memcpy_dk(parms_p->rv_p, &localRV, sizeof(localRV), pswFromLinkageStack.key);
}

