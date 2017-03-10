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
#include "include/server_wola_client_receive_request_specific.h"
#include "include/server_wola_service_queues.h"

/**
 * Receive request specific.
 *
 * This function is called by both wolaRequestRequestAny and wolaReceiveRequestSpecific.
 *
 * @param wolaClientConnectionHandle_p Client connection handle
 * @param requestServiceName_p         Pointer to request service name.
 * @param requestServiceNameLength_p   Pointer to request service name length.
 * @param requestDataLength_p          Pointer to output area to get data length.
 * @param async                        When zero, caller is waited until a request is received. When 1, the caller wants control returned immediately.
 * @param callerKey                    Key of the caller. Used when using service name.
 * @param reasonCode_p                 Output area to get the reason code.
 *
 * @return 0 on success. non 0 on failure.
 */
unsigned int wolaReceiveRequestSpecificCommon(WolaClientConnectionHandle_t* wolaClientConnectionHandle_p,
                                              char* requestServiceName_p,               // MUST use source key and destination key
                                              unsigned int* requestServiceNameLength_p,
                                              unsigned long long* requestDataLength_p,
                                              unsigned int async,
                                              unsigned char callerKey, unsigned int* reasonCode_p) {
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
    if (connHdlState != BBOAHDL_READY) {
        if (connHdlState == BBOAHDL_ERROR) {
            *reasonCode_p = WOLA_RSN_CONN_LCOM_ERROR;
        } else {
            *reasonCode_p = WOLA_RSN_INVALID_CONN_STATE;
        }
        return WOLA_RC_ERROR8;
    }


    // Make sure service name is null terminated putClientService expects it that way.
    char outputRequestServiceName[257]; // extra byte for null
    getRc = getServiceName(*requestServiceNameLength_p,
                          requestServiceName_p,
                          callerKey,
                          outputRequestServiceName);
    if (getRc != 0) {
        *reasonCode_p = WOLA_RSN_SERVICE_NAME_LEN;
        return WOLA_RC_ERROR8;

    }

    if (wolaRegistration_p == 0) {
        *reasonCode_p = WOLA_RSN_CONNHDL_NO_REGISTRATION;
        return WOLA_RC_ERROR8;
    }

    // if the async flag is passed in, then putClientService should check to see if there is a waiter
    // that matches the input service name, and if not, should return immediately with no match and
    // go back to client without doing the preview.
    // (if there was a waiter then it behaves normally as it would for async = 0).
    // putClientService returns 1 if async and no waiter found
    returnCode = putClientService((struct localCommClientConnectionHandle *) &lCommConnectionHandle, wolaRegistration_p, outputRequestServiceName, async);

    if(returnCode == 0){
        // Preview and read to get the request length
        returnCode = previewAndReadMessageAndContexts(wolaClientConnectionHandle_p,
                                                  &lCommConnectionHandle,
                                                  1, // wait for data
                                                  requestDataLength_p,
                                                  connHdlState,
                                                  BBOAHDL_REQUEST_RCVD,
                                                  reasonCode_p);

        /*-------------------------------------------------*/
        /* If the service name contained an * wildcard,    */
        /* we need to also return the name and length      */
        /* of the service that we're processing.           */
        /*-------------------------------------------------*/
        if (returnCode == 0) {
            if (strstr(outputRequestServiceName, "*") != 0) {
                char requestServiceNameFromContext[257]; // extra byte for null
                getServiceNameFromContext(wolaClientConnectionHandle_p, requestServiceNameFromContext);
                memcpy_dk(requestServiceName_p, requestServiceNameFromContext , strlen(requestServiceNameFromContext), callerKey);
                *requestServiceNameLength_p = strlen(requestServiceNameFromContext);
            }
        } else {
            // The local comm read failed.  Maybe the server went down?
            // Need to remove the available service if it's stil out there.
            removeAvailableServiceByHandle(&lCommConnectionHandle, wolaRegistration_p);
        }
    }
    return returnCode;
}

/**
 * Receive request specific.
 *
 * Called via BBOA1RCS.
 *
 * @param parms_p A pointer to the parameter structure.
 */
void wolaReceiveRequestSpecific(WOLAReceiveRequestSpecificParms_t* parms_p) {
    unsigned int localRC = 0;
    unsigned int localRSN = 0;
    unsigned long long requestDataLength = 0;
    unsigned int requestServiceNameLength = 0;

    // Bail if the client doesn't provide a RC area.
    if (parms_p->rc_p == 0) {
        return;
    }

    // Extract the PSW.  Bail if we can't.
    bbgz_psw pswFromLinkageStack;
    extractPSWFromLinkageStack(&pswFromLinkageStack);
    if (parms_p->rsn_p == 0) {
        localRC =  WOLA_RC_BAD_PARM;
        memcpy_dk(parms_p->rc_p, &localRC, sizeof(localRC), pswFromLinkageStack.key);
        return;
    }

    // Verify the service name length.  Bail if too long.
    memcpy_sk(&requestServiceNameLength, 
              parms_p->requestServiceNameLength_p,
              sizeof(requestServiceNameLength), 
              pswFromLinkageStack.key);

    if (requestServiceNameLength > BBOA_REQUEST_SERVICE_NAME_MAX) {
        localRC = WOLA_RC_ERROR8;
        localRSN = WOLA_RSN_SERVICE_NAME_LEN;
        memcpy_dk(parms_p->rc_p, &localRC, sizeof(localRC), pswFromLinkageStack.key);
        memcpy_dk(parms_p->rsn_p, &localRSN, sizeof(localRSN), pswFromLinkageStack.key);
        return;
    }

    WolaClientConnectionHandle_t* wolaClientConnectionHandle_p = (WolaClientConnectionHandle_t*) &(parms_p->connectionHandle[0]);
    localRC = wolaReceiveRequestSpecificCommon(wolaClientConnectionHandle_p,
                                               parms_p->requestServiceName_p,
                                               &requestServiceNameLength,
                                               &requestDataLength,
                                               parms_p->async,
                                               pswFromLinkageStack.key, &localRSN);

    if((localRC == 1) && (parms_p->async == 1)){
        requestDataLength = -1;
        localRC = 0;
    }
    // ----------------------------------------------------------------
    // Push output parameters back to caller
    // ----------------------------------------------------------------
    memcpy_dk(parms_p->requestServiceNameLength_p, &requestServiceNameLength, sizeof(requestServiceNameLength), pswFromLinkageStack.key);
    memcpy_dk(parms_p->requestDataLength_p, &requestDataLength, sizeof(requestDataLength), pswFromLinkageStack.key);
    memcpy_dk(parms_p->rc_p, &localRC, sizeof(localRC), pswFromLinkageStack.key);
    memcpy_dk(parms_p->rsn_p, &localRSN, sizeof(localRSN), pswFromLinkageStack.key);
}

