/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2013
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
#include <metal.h>
#include <stdlib.h>

#include "include/mvs_utils.h"
#include "include/server_local_comm_api.h"
#include "include/server_wola_client.h"
#include "include/server_wola_client_receive_response_length.h"

/**
 * Receive response length for the requested connection.
 *
 * @param wolaClientConnectionHandle_p  Client connection handle.
 * @param waitForData                   Set to one to wait for data.
 * @param responseDataLength_p          Pointer to output response data length.
 * @param reasonCode_p                  Pointer to output reason code.
 * @return 0 on success. non 0 on failure.
 */
unsigned int wolaReceiveResponseLengthCommon(WolaClientConnectionHandle_t* wolaClientConnectionHandle_p, unsigned int waitForData,
                                             unsigned long long* responseDataLength_p, unsigned int* reasonCode_p) {
    unsigned int returnCode = 0;

    if (connectionHandleValid(wolaClientConnectionHandle_p) == 0) {
        *reasonCode_p = WOLA_RSN_INVALID_CONN_HDL;
        return WOLA_RC_ERROR8;
    }

    // get connection attributes here before getHandleState which validates the connection is still
    // valid for the client connection handle.
    WolaRegistration_t* wolaRegistration_p = wolaClientConnectionHandle_p->handle_p->registration_p;
    OpaqueClientConnectionHandle_t lCommConnectionHandle;
    memcpy(&lCommConnectionHandle, wolaClientConnectionHandle_p->handle_p->localCommConnectionHandle, sizeof(lCommConnectionHandle));

    unsigned long long connHdlState;
    int getRc = getHandleState(wolaClientConnectionHandle_p, &connHdlState);
    if (getRc != 0) {
        *reasonCode_p = WOLA_RSN_INVALID_CONN_STATE;
        return WOLA_RC_ERROR8;
    }

    // Check connection state.
    if (connHdlState != BBOAHDL_REQUEST_SENT) {
        if (connHdlState == BBOAHDL_ERROR) {
            *reasonCode_p = WOLA_RSN_CONN_LCOM_ERROR;
        } else {
            *reasonCode_p = WOLA_RSN_INVALID_CONN_STATE;
        }
        return WOLA_RC_ERROR8;
    }

    if (wolaRegistration_p == 0) {
        *reasonCode_p = WOLA_RSN_CONNHDL_NO_REGISTRATION;
        return WOLA_RC_ERROR8;
    }

    // TODO tran sounding stuff   client inactivity timeout
    returnCode = previewAndReadMessageAndContexts(wolaClientConnectionHandle_p,
                                                  &lCommConnectionHandle,
                                                  waitForData,
                                                  responseDataLength_p,
                                                  connHdlState,
                                                  BBOAHDL_RESPONSE_RCVD,
                                                  reasonCode_p);
                                
    return returnCode;
}

/**
 * Receive response length.
 *
 * @param parms_p A pointer to the parameter structure.
 */
void wolaReceiveResponseLength(WOLAReceiveResponseLengthParms_t* parms_p) {
    unsigned int localRC = 0;
    unsigned int localRSN = 0;
    unsigned long long responseDataLength = 0;
    unsigned int waitForData = 1;

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

    WolaClientConnectionHandle_t* wolaClientConnectionHandle_p = (WolaClientConnectionHandle_t*) &(parms_p->connectionHandle[0]);
    if (parms_p->async == 1) {
        waitForData = 0;
    }
    localRC = wolaReceiveResponseLengthCommon(wolaClientConnectionHandle_p, waitForData, &responseDataLength, &localRSN);

    // ----------------------------------------------------------------
    // Push output parameters back to caller
    // ----------------------------------------------------------------
    memcpy_dk(parms_p->responseDataLength_p, &responseDataLength, sizeof(responseDataLength), pswFromLinkageStack.key);
    memcpy_dk(parms_p->rc_p, &localRC, sizeof(localRC), pswFromLinkageStack.key);
    memcpy_dk(parms_p->rsn_p, &localRSN, sizeof(localRSN), pswFromLinkageStack.key);
}
