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
#include "include/server_local_comm_api.h"
#include "include/server_wola_client.h"

/**
 * Send response using input connection.
 *
 * @param wolaClientConnectionHandle_p client connection handle
 * @param responseDataLength           length of response data
 * @param responseData_p               pointer to response data
 * @param responseDataKey              key of response data
 * @param responseException            set to 1 when response is an exception.
 * @param reasonCode_p                 output pointer to reason code
 *
 * @return 0 on success. non 0 on failure.
 *
 */
unsigned int wolaSendResponseCommon(WolaClientConnectionHandle_t* wolaClientConnectionHandle_p,
                                    unsigned long long responseDataLength,
                                    char* responseData_p,           // need to use move with source key
                                    unsigned char responseDataKey,
                                    unsigned int responseException,
                                    unsigned int* reasonCode_p) {

    unsigned int localRC = 0;
    unsigned char lCommConnectionHandle[sizeof(wolaClientConnectionHandle_p->handle_p->localCommConnectionHandle)];

    if (connectionHandleValid(wolaClientConnectionHandle_p) == 0) {
        *reasonCode_p = WOLA_RSN_INVALID_CONN_HDL;
        return WOLA_RC_ERROR8;
    }

    // Why do we copy the lcommConnectionHandle onto the stack?  
    memcpy(lCommConnectionHandle, wolaClientConnectionHandle_p->handle_p->localCommConnectionHandle, sizeof(lCommConnectionHandle));

    // TODO: report errors from both getHandleState and getServerRegistration.
    WolaRegistration_t* serverRegistration_p = 0;
    unsigned long long connHdlState;
    int getRc = getHandleState(wolaClientConnectionHandle_p, &connHdlState);
    if (getRc == 0) {
        serverRegistration_p = getServerRegistration(wolaClientConnectionHandle_p);
    }
    if (serverRegistration_p == 0) {
        *reasonCode_p = WOLA_RSN_INVALID_CONN_HDL;
        return WOLA_RC_ERROR8;
    } else if (connHdlState != BBOAHDL_DATA_RCVD) {
        if (connHdlState == BBOAHDL_ERROR) {
            *reasonCode_p = WOLA_RSN_CONN_LCOM_ERROR;
        } else {
            *reasonCode_p = WOLA_RSN_INVALID_CONN_STATE;
        }
        return WOLA_RC_ERROR8;
    }

    // Build Wola Message.
    WolaMessage_t messageHeader;
    buildWolaMessageResponseHeader(&messageHeader,
                                   0, /* no worktype */
                                   wolaClientConnectionHandle_p->handle_p->requestId,
                                   responseDataLength,
                                   responseException);

    // send message header
    int lCommRC = localCommSend(lCommConnectionHandle,
                                sizeof(messageHeader),
                                &messageHeader,
                                2);

    if (lCommRC == 0) {
        // send response
        lCommRC = localCommSend(lCommConnectionHandle,
                                responseDataLength,
                                responseData_p,
                                responseDataKey);
    }

    if (lCommRC == 0) {
        //Change connection state to ready
        int changeRc = changeHandleState(wolaClientConnectionHandle_p, connHdlState, BBOAHDL_READY);
        if (changeRc == 0) {
            // twas set amsgtoken  TODO investigate
        } else {
            // TODO: report error from changeHandleState
            localRC = WOLA_RC_ERROR8;
            *reasonCode_p = WOLA_RSN_INVALID_CONN_HDL;
        }
    } else {
        // TODO: report lCommRC.
        changeHandleState(wolaClientConnectionHandle_p, connHdlState, BBOAHDL_ERROR);
        localRC = WOLA_RC_ERROR8;
        *reasonCode_p = WOLA_RSN_LC_SEND;
    }

    return localRC;
}


/**
 * Send response.
 *
 * @param parms_p A pointer to the parameter structure.
 */
void wolaSendResponse(WOLASendResponseParms_t* parms_p) {
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
    // validate first and last byte like twas
    int bufferCheckRC = checkBuffer(parms_p->responseData_p,
                                    parms_p->responseDataLength,
                                    pswFromLinkageStack.key,
                                    1 /* Read */,
                                    0 /* Response buffer*/);
    if (bufferCheckRC != 0) {
        localRC = WOLA_RC_ERROR8;
        if (bufferCheckRC == 3) {
            localRSN = WOLA_RSN_SYSTEM_LIMIT;
        } else if (bufferCheckRC == 1) {
            localRSN = WOLA_RSN_BAD_BUFFER_RSP1;
        } else {
            localRSN = WOLA_RSN_BAD_BUFFER_RSP2;
        }
        memcpy_dk(parms_p->rc_p, &localRC, sizeof(localRC), pswFromLinkageStack.key);
        memcpy_dk(parms_p->rsn_p, &localRSN, sizeof(localRSN), pswFromLinkageStack.key);
        return;
    }

    WolaClientConnectionHandle_t* wolaClientConnectionHandle_p = (WolaClientConnectionHandle_t*) &(parms_p->connectionHandle[0]);

    localRC = wolaSendResponseCommon(wolaClientConnectionHandle_p,
                                     parms_p->responseDataLength,
                                     parms_p->responseData_p,
                                     pswFromLinkageStack.key,
                                     0, // not a exception response
                                     &localRSN);

    // ----------------------------------------------------------------
    // Push output parameters back to caller
    // ----------------------------------------------------------------
    memcpy_dk(parms_p->rc_p, &localRC, sizeof(localRC), pswFromLinkageStack.key);
    memcpy_dk(parms_p->rsn_p, &localRSN, sizeof(localRSN), pswFromLinkageStack.key);
}

