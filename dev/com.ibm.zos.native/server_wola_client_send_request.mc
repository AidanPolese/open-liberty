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
#include "include/server_wola_client_send_request.h"
#include "include/server_wola_client_receive_response_length.h"

WolaRegistration_t* getServerRegistration(WolaClientConnectionHandle_t* clientConnectionHandle_p) {
    //TODO replicate this to serialize. where did RGE(BBOARGE) come from?
    /*----------------------------------------------------*/
    /* If the connection handle state was valid, get a    */
    /* reference to the WAS server RGE.  The previous     */
    /* validation of the handle also validates the client */
    /* RGE address we loaded previously in RegEntry@.     */
    /*----------------------------------------------------*/
    //If (ProgramMaskCC = 0) Then              /* @LI4798I7A*/
    // Do;                                     /* @LI4798I7A*/
    //  ?BBOACPH FUNC(LoadFromRGE) RGE(BBOARGE)
    //           RGESTCK(RegEntrySTCK) PgmMask(ProgramMask)
    //           RGEField(argeservrge@) OutValue(ServerRGE@);
    // End;                                    /* @LI4798I7A*/

    return clientConnectionHandle_p->handle_p->registration_p->serverRegistration_p;
}

/**
 * Send request using input connection.
 * Called by wolaSendRequest and wolaInvoke.
 *
 * @param wolaClientConnectionHandle_p client connection handle
 * @param requestType                  request type
 * @param requestDataLength            length of request data
 * @param requestData_p                pointer to request data
 * @param requestDataKey               key of request data
 * @param wolaServiceNameContextArea_p pointer to service name context area
 * @param cicsParms_p                  pointer to cics-specific parms.
 * @param reasonCode_p                 output pointer to reason code
 *
 * @return 0 on success. non 0 on failure.
 */
unsigned int wolaSendRequestCommon(WolaClientConnectionHandle_t* wolaClientConnectionHandle_p,
                                   unsigned int requestType,
                                   unsigned long long requestDataLength,
                                   char* requestData_p,
                                   unsigned char requestDataKey,
                                   WolaServiceNameContextArea_t* wolaServiceNameContextArea_p,
                                   struct bboapc1p* cicsParms_p,
                                   unsigned int* reasonCode_p) {

    unsigned int localRC = 0;
    unsigned char lCommConnectionHandle[sizeof(wolaClientConnectionHandle_p->handle_p->localCommConnectionHandle)];

    if (connectionHandleValid(wolaClientConnectionHandle_p) == 0) {
        *reasonCode_p = WOLA_RSN_INVALID_CONN_HDL;
        return WOLA_RC_ERROR8;
    }

    memcpy(lCommConnectionHandle, wolaClientConnectionHandle_p->handle_p->localCommConnectionHandle, sizeof(lCommConnectionHandle));

    WolaRegistration_t* clientRegistration_p = NULL;
    WolaRegistration_t* serverRegistration_p = NULL;
    unsigned long long connHdlState;
    int getRc = getHandleState(wolaClientConnectionHandle_p, &connHdlState);
    if (getRc == 0) {
        // TODO: Serialize obtaining both registrations
        clientRegistration_p = wolaClientConnectionHandle_p->handle_p->registration_p;
        serverRegistration_p = getServerRegistration(wolaClientConnectionHandle_p);
    }
    if (serverRegistration_p == 0) {
        *reasonCode_p = WOLA_RSN_INVALID_CONN_HDL;
        return WOLA_RC_ERROR8;
    } else if (connHdlState != BBOAHDL_READY) {
        if (connHdlState == BBOAHDL_ERROR) {
            *reasonCode_p = WOLA_RSN_CONN_LCOM_ERROR;
        } else {
            *reasonCode_p = WOLA_RSN_INVALID_CONN_STATE;
        }
        return WOLA_RC_ERROR8;
    }

    // Build the wola message.
    WolaMessageAndContextArea_t messageAndContextArea;
    buildWolaMessageAndContextArea(&messageAndContextArea, requestType, wolaServiceNameContextArea_p);
    setWolaMessageServerNameFromRegistration( &messageAndContextArea.messageHeader, serverRegistration_p );
    setDataAreaLengthAndOffset(&messageAndContextArea.messageHeader, requestDataLength);
    setCallersMvsUserId(&messageAndContextArea.messageHeader, clientRegistration_p, cicsParms_p);

    memcpy(&messageAndContextArea.serviceNameContextArea,
           wolaServiceNameContextArea_p,
           sizeof(messageAndContextArea.serviceNameContextArea));

    // send message header
    int lCommRC = localCommSend(lCommConnectionHandle,
                                sizeof(messageAndContextArea.messageHeader),
                                &messageAndContextArea.messageHeader,
                                2);
    if (lCommRC == 0) {
        // send context header and context
        lCommRC = localCommSend(lCommConnectionHandle,
                                messageAndContextArea.messageHeader.contextAreaLength,
                                &messageAndContextArea.contextHeader,
                                2);
    }
    if (lCommRC == 0) {
        // send data
        lCommRC = localCommSend(lCommConnectionHandle,
                                requestDataLength,
                                requestData_p,
                                requestDataKey);
    }
    if (lCommRC == 0) {
        //Change connection state to ready
        int changeRc = changeHandleState(wolaClientConnectionHandle_p, connHdlState, BBOAHDL_REQUEST_SENT);
        if (changeRc == 0) {
            localRC = messageAndContextArea.messageHeader.returnCode;  // TODO is this correct
            *reasonCode_p = messageAndContextArea.messageHeader.reasonCode; // TODO is this correct
        } else {
            localRC = WOLA_RC_ERROR8;
            *reasonCode_p = WOLA_RSN_INVALID_CONN_HDL;
        }
    } else {
        changeHandleState(wolaClientConnectionHandle_p, connHdlState, BBOAHDL_ERROR);
        localRC = WOLA_RC_ERROR8;
        *reasonCode_p = WOLA_RSN_LC_SEND;
    }

    return localRC;
}

/**
 * Send request.
 *
 * @param parms_p A pointer to the parameter structure.
 */
void wolaSendRequest(WOLASendRequestParms_t* parms_p) {

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

    // Validate request type
    if (parms_p->requestType != WOLA_REQUEST_TYPE_1_LOCAL_EJB_INV &&
        parms_p->requestType != WOLA_REQUEST_TYPE_2_REMOTE_EJB_INV) {
        localRC = WOLA_RC_ERROR8;
        localRSN = WOLA_RSN_INVAL_REQ_TYPE;
        memcpy_dk(parms_p->rc_p, &localRC, sizeof(localRC), pswFromLinkageStack.key);
        memcpy_dk(parms_p->rsn_p, &localRSN, sizeof(localRSN), pswFromLinkageStack.key);
        return;
    }

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

    if (parms_p->requestServiceNameLength > BBOA_REQUEST_SERVICE_NAME_MAX) {
        localRC = WOLA_RC_ERROR8;
        localRSN = WOLA_RSN_SERVICE_NAME_LEN;
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
    if (buildRc == 0) {
        WolaClientConnectionHandle_t* wolaClientConnectionHandle_p = (WolaClientConnectionHandle_t*) &(parms_p->connectionHandle[0]);

        // If we're in CICS, copy out the CICS parameters.
        struct bboapc1p cicsParms;
        if (parms_p->cicsParms_p != NULL) {
            memcpy_sk(&cicsParms, parms_p->cicsParms_p, sizeof(cicsParms), pswFromLinkageStack.key);
        } else {
            memset(&cicsParms, 0, sizeof(cicsParms));
        }

        localRC = wolaSendRequestCommon(wolaClientConnectionHandle_p, parms_p->requestType, parms_p->requestDataLength, parms_p->requestData_p,
                                        pswFromLinkageStack.key, &serviceNameContextArea, &cicsParms, &localRSN);

        if (localRC == 0) {

            unsigned int waitForData = 1;
            if(parms_p->async == 1){
                //We are async and should not wait for data
                waitForData = 0;
            }

            // Receive response length
            unsigned long long receiveResponseDataLength = 0;
            localRC = wolaReceiveResponseLengthCommon(wolaClientConnectionHandle_p,
                                                      waitForData,
                                                      &receiveResponseDataLength, &localRSN);

            if(localRC == 0) {
                memcpy_dk(parms_p->responseDataLength_p, &receiveResponseDataLength, sizeof(receiveResponseDataLength), pswFromLinkageStack.key);
            }
        }

    } else {
        localRC = WOLA_RC_ERROR8;
        localRSN = WOLA_RSN_SERVICE_NAME_LEN;
    }

    // ----------------------------------------------------------------
    // Push output parameters back to caller
    // ----------------------------------------------------------------
    memcpy_dk(parms_p->rc_p, &localRC, sizeof(localRC), pswFromLinkageStack.key);
    memcpy_dk(parms_p->rsn_p, &localRSN, sizeof(localRSN), pswFromLinkageStack.key);
}

