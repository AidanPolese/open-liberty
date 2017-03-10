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
#include <stdlib.h>

#include "include/mvs_utils.h"
#include "include/server_wola_client.h"
#include "include/server_wola_client_get_data.h"

/**
 * Read in a loop, reading the number of bytes specified into the buffer.
 *
 * @param messageData_p The buffer to read into.
 * @param readLength The number of bytes to read.
 * @param lCommConnectionHandle_p A pointer to the local comm conection handle.
 * @param callerKey the key that messageData_p is in.
 *
 * @return zero (FALSE) on success, non-zero (TRUE) on local comm error before all data was read.
 */
static int readLoop(void* messageData_p, unsigned long long readLength, OpaqueClientConnectionHandle_t* lCommConnectionHandle_p, unsigned char callerKey) {
    unsigned long long dataLeftToRead = readLength;
    unsigned char* curReadLoc_p = messageData_p;
    unsigned char localCommError = FALSE;
    while ((localCommError == FALSE) && (dataLeftToRead > 0)) {
        // Make sure there is some local comm data available for us to read.
        int timeToWait = 0;
        unsigned long long bytesAvailable = 0L;
        int previewMessageContextRC = localCommClientPreview(lCommConnectionHandle_p, 1 /* wait */, timeToWait, &bytesAvailable);
        if (previewMessageContextRC != LCOM_PREVIEW_RC_OK) {
            localCommError = TRUE;
        } else {
            // Read at least part of the data.
            unsigned long long lengthToReceive = (bytesAvailable >= dataLeftToRead) ? dataLeftToRead : bytesAvailable;
            int receiveDataRC = localCommReceive(lCommConnectionHandle_p, lengthToReceive, curReadLoc_p, callerKey);
            if (receiveDataRC > 0) { /* Read some data */
                curReadLoc_p += lengthToReceive;
                dataLeftToRead -= lengthToReceive;
            } else {
                localCommError = TRUE;
            }
        }
    }

    return localCommError;
}

/**
 * Get data associated with the input connection.
 *
 * Note: this method frees the cachedMessage_p and cachedContexts_p (the WolaMessage)
 *       from the WolaConnectionHandle.
 *
 * @param wolaClientConnectionHandle_p client connection handle
 * @param messageData_p     pointer to message data
 * @param messageDataLength length of message data
 * @param callerKey         Key of the caller. Used when using message data pointer.
 * @param reasonCode_p      Output area to get the reason code.
 * @param reasonValue_p     Output area to get the reason value.
 *
 * @return 0 on success. non 0 on failure.
 */
unsigned int wolaGetDataCommon(WolaClientConnectionHandle_t* wolaClientConnectionHandle_p, char* messageData_p,
                               unsigned long long messageDataLength, unsigned char callerKey,
                               unsigned int* reasonCode_p, unsigned int* reasonValue_p) {

    unsigned int returnCode = 0;

    if (connectionHandleValid(wolaClientConnectionHandle_p) == 0) {
        *reasonCode_p = WOLA_RSN_INVALID_CONN_HDL;
        return WOLA_RC_ERROR8;
    }

    // get connection attributes here before getHandleState which validates the connection is still
    // valid for the client connection handle.
    WolaRegistration_t* wolaRegistration_p = wolaClientConnectionHandle_p->handle_p->registration_p;
    /*---------------------------------------------------*/
    /* Check connection state                            */
    /*---------------------------------------------------*/
    unsigned long long connHdlState;
    int getRc = getHandleState(wolaClientConnectionHandle_p, &connHdlState);
    if (getRc != 0) {
        *reasonCode_p = WOLA_RSN_INVALID_CONN_HDL;
        return WOLA_RC_ERROR8;
    } else if ((connHdlState != BBOAHDL_RESPONSE_RCVD) &&
        (connHdlState != BBOAHDL_REQUEST_RCVD)) {
        if (connHdlState == BBOAHDL_ERROR) {
            *reasonCode_p = WOLA_RSN_CONN_LCOM_ERROR;
        } else {
            *reasonCode_p = WOLA_RSN_INVALID_CONN_STATE;
        }
        return WOLA_RC_ERROR8;
    }

    // Get the local comm connection handle.
    OpaqueClientConnectionHandle_t lCommConnectionHandle;
    if (getLocalCommConnectionHandleFromWolaConnectionHandle(wolaClientConnectionHandle_p, &lCommConnectionHandle) != 0) {
        *reasonCode_p = WOLA_RSN_INVALID_CONN_HDL;
        return WOLA_RC_ERROR8;
    }

    unsigned int bufferCheckReq;
    if (connHdlState == BBOAHDL_REQUEST_RCVD) {
        bufferCheckReq = 1;
    } else {
        bufferCheckReq = 0;
    }

    // Make sure we can write to the buffer the caller provided.
    int bufferCheckRC = checkBuffer(messageData_p,
                                    messageDataLength,
                                    callerKey,
                                    0 /* Write */,
                                    bufferCheckReq);
    if (bufferCheckRC != 0) {
        if (bufferCheckRC == 3) {
            *reasonCode_p = WOLA_RSN_SYSTEM_LIMIT;
        } else if (bufferCheckRC == 1) {
            if (bufferCheckReq == 1) {
                *reasonCode_p = WOLA_RSN_BAD_BUFFER_REQ1;
            } else {
                *reasonCode_p = WOLA_RSN_BAD_BUFFER_RSP1;
            }

        } else {
            if (bufferCheckReq == 1) {
                *reasonCode_p = WOLA_RSN_BAD_BUFFER_REQ2;
            } else {
                *reasonCode_p = WOLA_RSN_BAD_BUFFER_RSP2;
            }
        }
        return WOLA_RC_ERROR8;
    }

    if (wolaRegistration_p == 0) {
        *reasonCode_p = WOLA_RSN_CONNHDL_NO_REGISTRATION;
        return WOLA_RC_ERROR8;
    }

    void* message_p = 0;
    void* contexts_p = 0;
    if (getMessageAndContextAreas(wolaClientConnectionHandle_p, &message_p, &contexts_p) != 0) {
        *reasonCode_p = WOLA_RSN_INVALID_CONN_HDL;
        return WOLA_RC_ERROR8;
    }

    // If there are still contexts, free them now.
    if (contexts_p != 0) {
        free(contexts_p);
    }

    // If we don't have a message pointer, we can't continue.
    if (message_p == 0) {
        changeHandleState(wolaClientConnectionHandle_p, connHdlState, BBOAHDL_ERROR);
        *reasonCode_p = WOLA_RSN_INVALID_CONN_STATE;
        return WOLA_RC_ERROR8;
    }

    WolaMessage_t* wolaMessage_p = (WolaMessage_t*) message_p;
    // Need to go get the message.
    if (wolaMessage_p->dataAreaLength == 0) {
        *reasonValue_p = 0;
    } else {
        // Check to see if the caller provided enough storage to read the message.
        // If not, alter the amount to read and set the reason code.
        unsigned long long dataLeftToRead = (wolaMessage_p->dataAreaLength > messageDataLength) ? messageDataLength : wolaMessage_p->dataAreaLength;
        int localCommError = readLoop(messageData_p, dataLeftToRead, &lCommConnectionHandle, callerKey);

        if (localCommError == FALSE) {
            // Compare messageDataLength to wolaMessage_p->dataAreaLength and if the message received
            // is larger than the area provided to copy in to, we raise
            // a RC8. The real message length received is always in the
            // RV (return value).
            *reasonValue_p = wolaMessage_p->dataAreaLength;
            if (wolaMessage_p->dataAreaLength > messageDataLength) {
                returnCode = WOLA_RC_ERROR8;
                *reasonCode_p = WOLA_RSN_MSGLEN_SMALLER;

                // Read the rest of the message.  Need to get it out of
                // the local comm buffers so we can read the next
                // request on this connection.
                unsigned char extraReadArea[8192];
                unsigned long long extraDataToReadLen = wolaMessage_p->dataAreaLength - messageDataLength;
                while (extraDataToReadLen > 0) {
                    unsigned long long curReadLen = (extraDataToReadLen > sizeof(extraReadArea)) ? sizeof(extraReadArea) : extraDataToReadLen;
                    if (readLoop(extraReadArea, curReadLen, &lCommConnectionHandle, 2) == FALSE) {
                        extraDataToReadLen -= curReadLen;
                    } else {
                        free(message_p);
                        // We'll return the buffer that we read, but we're changing the
                        // handle state to error so it can't be used anymore.
                        changeHandleState(wolaClientConnectionHandle_p, connHdlState, BBOAHDL_ERROR);
                        return returnCode;
                    }
                }
            }
        } else {
            free(message_p);
            changeHandleState(wolaClientConnectionHandle_p, connHdlState, BBOAHDL_ERROR);
            *reasonCode_p = WOLA_RSN_CONN_LCOM_ERROR;
            return WOLA_RC_ERROR8;
        }
    }

    free(message_p);

    /*---------------------------------------------------*/
    /* Change connection state                           */
    /*---------------------------------------------------*/
    unsigned long long newConnHdlState;
    if (connHdlState == BBOAHDL_RESPONSE_RCVD) {
        newConnHdlState = BBOAHDL_READY;
    } else {
        newConnHdlState = BBOAHDL_DATA_RCVD;
    }
    int changeRc = changeHandleState(wolaClientConnectionHandle_p,
                                     connHdlState,
                                     newConnHdlState);
    if (changeRc != 0) {
        returnCode = WOLA_RC_ERROR8;
        *reasonCode_p = WOLA_RSN_INVALID_CONN_HDL;
    }

    return returnCode;
}
/**
 * Get data.
 *
 * @param parms_p A pointer to the parameter structure.
 */
void wolaGetData(WOLAGetDataParms_t* parms_p) {
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

    WolaClientConnectionHandle_t* wolaClientConnectionHandle_p = (WolaClientConnectionHandle_t*) &(parms_p->connectionHandle[0]);
    localRC = wolaGetDataCommon(wolaClientConnectionHandle_p, parms_p->messageData_p, parms_p->messageDataLength,
                                pswFromLinkageStack.key, &localRSN, &localRV);

    // ----------------------------------------------------------------
    // Push output parameters back to caller
    // ----------------------------------------------------------------
    memcpy_dk(parms_p->rc_p, &localRC, sizeof(localRC), pswFromLinkageStack.key);
    memcpy_dk(parms_p->rsn_p, &localRSN, sizeof(localRSN), pswFromLinkageStack.key);
    memcpy_dk(parms_p->rv_p, &localRV, sizeof(localRV), pswFromLinkageStack.key);
}

