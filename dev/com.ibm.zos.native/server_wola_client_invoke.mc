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
#include "include/server_wola_client_send_request.h"
#include "include/server_wola_client_get_data.h"
#include "include/server_wola_client_receive_response_length.h"
#include "include/server_wola_connection_pool.h"

/**
 * Invoke.
 *
 * @param parms_p A pointer to the parameter structure.
 */
void wolaInvoke(WOLAInvokeParms_t* parms_p) {
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

    if (parms_p->requestServiceNameLength > BBOA_REQUEST_SERVICE_NAME_MAX) {
        localRC = WOLA_RC_ERROR8;
        localRSN = WOLA_RSN_SERVICE_NAME_LEN;
        memcpy_dk(parms_p->rc_p, &localRC, sizeof(localRC), pswFromLinkageStack.key);
        memcpy_dk(parms_p->rsn_p, &localRSN, sizeof(localRSN), pswFromLinkageStack.key);
        return;
    }

    // checkBuffer
    int bufferCheckRC = checkBuffer(parms_p->requestData_p,
                                    parms_p->requestDataLength,
                                    pswFromLinkageStack.key,
                                    1 /* Read */,
                                    1 /* Request buffer */);
    if (bufferCheckRC != 0) {
        localRC = WOLA_RC_ERROR8;
        if (bufferCheckRC == 3) {
            localRSN = WOLA_RSN_SYSTEM_LIMIT;
        } else if (bufferCheckRC == 1) {
            localRSN = WOLA_RSN_BAD_BUFFER_REQ1;
        } else {
            localRSN = WOLA_RSN_BAD_BUFFER_REQ2;
        }
        memcpy_dk(parms_p->rc_p, &localRC, sizeof(localRC), pswFromLinkageStack.key);
        memcpy_dk(parms_p->rsn_p, &localRSN, sizeof(localRSN), pswFromLinkageStack.key);
        return;
    }

    // Validate request type
    if (parms_p->requestType != WOLA_REQUEST_TYPE_1_LOCAL_EJB_INV &&
        parms_p->requestType != WOLA_REQUEST_TYPE_2_REMOTE_EJB_INV) {
        localRC = WOLA_RC_ERROR8;
        localRSN = WOLA_RSN_INVAL_REQ_TYPE;
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

    WolaServiceNameContextArea_t serviceNameContextArea;
    unsigned int buildRc = buildServiceNameContext(&serviceNameContextArea.serviceNameContext,
                                                   &serviceNameContextArea.name[0],
                                                   parms_p->requestServiceNameLength,
                                                   parms_p->requestServiceName_p,
                                                   pswFromLinkageStack.key);
    if (buildRc != 0) {
        localRC = WOLA_RC_ERROR8;
        localRSN = WOLA_RSN_SERVICE_NAME_LEN;
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

    // Send request
    localRC = wolaSendRequestCommon(&clientConnectionHandle, parms_p->requestType, parms_p->requestDataLength, parms_p->requestData_p,
                                    pswFromLinkageStack.key, &serviceNameContextArea, &cicsParms, &localRSN);
    if (localRC == 0) {
        // receive response length
        unsigned long long receiveResponseDataLength = 0;
        localRC = wolaReceiveResponseLengthCommon(&clientConnectionHandle,
                                                  1, // wait for data
                                                  &receiveResponseDataLength, &localRSN);
        if ((localRC == 0) && (receiveResponseDataLength >= 0)) {

            localRC = wolaGetDataCommon(&clientConnectionHandle, parms_p->responseData_p, parms_p->responseDataLength,
                                        pswFromLinkageStack.key, &localRSN, &localRV);
        }
    }
    // release connection
    int freeRc = freePooledConnection(clientConnectionHandle.handle_p->registration_p, &clientConnectionHandle);
    // We only want to propagate a release connection failure if no
    // other API has failed. Otherwise it's the first failure
    // that is most interesting.
    if ((localRC == 0) && (freeRc > 0)) {
        if (freeRc == FreeConn_RC_ConnectionStateError) {
            localRC = WOLA_RC_ERROR8;
            localRSN = WOLA_RSN_INVALID_CONN_STATE;
        }
        // TODO did twas care about any other return codes
    }

    // ----------------------------------------------------------------
    // Push output parameters back to caller
    // ----------------------------------------------------------------
    memcpy_dk(parms_p->rc_p, &localRC, sizeof(localRC), pswFromLinkageStack.key);
    memcpy_dk(parms_p->rsn_p, &localRSN, sizeof(localRSN), pswFromLinkageStack.key);
    memcpy_dk(parms_p->rv_p, &localRV, sizeof(localRV), pswFromLinkageStack.key);
}

