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
#include <string.h>

#include "include/common_defines.h"
#include "include/mvs_utils.h"
#include "include/ras_tracing.h"
#include "include/server_local_comm_api.h"
#include "include/server_local_comm_ffdc.h"
#include "include/server_wola_client.h"
#include "include/server_wola_message.h"
#include "include/server_wola_registration.h"
#include "include/gen/ihaacee.h"
#include "include/gen/ihapsa.h"
#include "include/gen/ikjtcb.h"
#include "include/gen/ihaasxb.h"

#define RAS_MODULE_CONST  RAS_MODULE_SERVER_WOLA_MESSAGE

/**
 * Initialize the messageHeader_p as a WOLA message with the given workType
 *
 * The totalMessageSize is initialized to the size of the header.  This must be updated
 * if you add contexts and/or app data to the request.
 *
 * Called by wolaSendRequestCommon.
 *
 * @param messageHeader_p - a ptr to storage for the WOLA message header
 * @param workType - the wola message work type
 *
 * @return messageHeader_p
 */
WolaMessage_t * initializeMessageHeader(WolaMessage_t* messageHeader_p, unsigned int workType) {

    memset(messageHeader_p, 0, sizeof(WolaMessage_t));
    memcpy(messageHeader_p->eye, BBOAMSG_EYE, sizeof(messageHeader_p->eye));
    messageHeader_p->amsgver = BBOAMSG_VERSION_2;
    messageHeader_p->totalMessageSize = sizeof(WolaMessage_t);
    messageHeader_p->workType = workType;

    return messageHeader_p;
}

/**
 * Initialize the given contextHeader_p storage as a WolaMessageContextAreaHeader.
 * 
 * Called by wolaSendRequestCommon
 *
 * @param contextHeader_p - a ptr to storage for the WolaMessageContextAreaHeader.
 * @param numberOfContexts - the header's numContexts field value
 *
 * @return contextHeader_p
 */
WolaMessageContextAreaHeader_t * initializeContextHeader(WolaMessageContextAreaHeader_t* contextHeader_p, int numberOfContexts) {

    memset(contextHeader_p, 0, sizeof(WolaMessageContextAreaHeader_t));
    memcpy(contextHeader_p->eye, BBOACTX_EYE, sizeof(contextHeader_p->eye));
    contextHeader_p->version = BBOACTX_VERSION_1;
    contextHeader_p->numContexts = numberOfContexts;

    return contextHeader_p;
}

/**
 * Initialize the given serviceNameContext_p storage area as a service name context
 * with the given requestServiceName.
 *
 * Called by wolaSendRequest and wolaInvoke
 *
 * @param serviceNameContext_p - output - a pointer to the serviceNameContext storage area
 * @param name_p - output - a ptr to the service name field within the service name context.
 *                 requestServiceName_p is copied into here.
 * @param requestServiceNameLength
 * @param requestServiceName_p - the service name to use (storage in callerStorageKey)
 * @param callerStorageKey - the calling address space's storage key, needed for strncpy_sk.
 *
 * @return 0 if all good; 8 if requestServiceName_p is empty
 */
unsigned int buildServiceNameContext(WolaServiceNameContext_t* serviceNameContext_p,
                                     unsigned char* name_p,
                                     unsigned int requestServiceNameLength,
                                     char* requestServiceName_p,
                                     unsigned char callerStorageKey
                                     ) {
    char localName[BBOA_REQUEST_SERVICE_NAME_MAX+1];
    memset(serviceNameContext_p, 0, sizeof(WolaServiceNameContext_t));
    memcpy(serviceNameContext_p->header.eye, BBOASNC_EYE, sizeof(serviceNameContext_p->header.eye));
    serviceNameContext_p->header.contextId = BBOASNC_Identifier;
    serviceNameContext_p->version = BBOASNC_VERSION_1;

    if (requestServiceNameLength == 0) {
        // If the length wasn't specified, copy the maximum number of bytes for the field
        memset(localName + BBOA_REQUEST_SERVICE_NAME_MAX, 0, 1);
        memset(localName, 0, 1);
        strncpy_sk(localName, requestServiceName_p, BBOA_REQUEST_SERVICE_NAME_MAX, callerStorageKey);
        if (strlen(localName) == 0) {
            // ERROR!  empty service name!
            return 8;
        }
        serviceNameContext_p->nameLength = strlen(localName);
        memcpy(name_p, localName, serviceNameContext_p->nameLength);
    } else {
        serviceNameContext_p->nameLength = requestServiceNameLength;
        memcpy_sk(name_p, requestServiceName_p, serviceNameContext_p->nameLength, callerStorageKey);
    }

    // The contextLen is the length of the data following the context header.
    serviceNameContext_p->header.contextLen = sizeof(WolaServiceNameContext_t) - sizeof(WolaMessageContextHeader_t) + serviceNameContext_p->nameLength;
    return 0;
}

/**
 * Copy the server's WOLA name (part 2 and 3) into the given WolaMessage from the
 * given serverRegistration_p.
 *
 * Called by wolaSendRequestCommon
 *
 * @param messageHeader_p - the target WOLA message
 * @param serverRegistration_p - the server's WolaRegistration_t
 *
 * @return messageHeader_p
 */
WolaMessage_t * setWolaMessageServerNameFromRegistration( WolaMessage_t * messageHeader_p, 
                                                          WolaRegistration_t * serverRegistration_p ) {

    memcpy(messageHeader_p->wolaNamePartTwo,
           serverRegistration_p->serverNameSecondPart,
           sizeof(messageHeader_p->wolaNamePartTwo));

    memcpy(messageHeader_p->wolaNamePartThree,
           serverRegistration_p->serverNameThirdPart,
           sizeof(messageHeader_p->wolaNamePartThree));

    return messageHeader_p;
}

/**
 * @return the total length of the given WolaMessageContext.
 */
int getWolaMessageContextTotalLength( WolaMessageContextHeader_t * wolaMessageContextHeader_p ) {
    return sizeof(WolaMessageContextHeader_t) + wolaMessageContextHeader_p->contextLen;
}

/**
 * Build a WOLA message header and context area.
 *
 * Note: The wolaServiceNameContextArea_p is used to determine the contextAreaLength and 
 * and totalMessageSize;  it is NOT actually copied into the messageAndContextArea_p.
 *
 * @param messageAndContextArea_p - The message header and context area to build/initialize
 * @param workType - The message work type
 * @param wolaServiceNameContextArea_p - The service name context (needed to determine the total contextArea length)
 *
 * @return messageAndContextArea_p
 */
WolaMessageAndContextArea_t * buildWolaMessageAndContextArea(WolaMessageAndContextArea_t * messageAndContextArea_p, 
                                                             unsigned int workType,
                                                             WolaServiceNameContextArea_t* wolaServiceNameContextArea_p ) {

    WolaMessage_t * messageHeader_p = &(messageAndContextArea_p->messageHeader);
    WolaMessageContextAreaHeader_t * contextHeader_p = &(messageAndContextArea_p->contextHeader);

    initializeMessageHeader(messageHeader_p, workType);
    initializeContextHeader(contextHeader_p, 1);

    // The contextAreaLength is equal to the size of the context area header,
    // plus the size of the service name context (does not include the actual name),
    // plus the length of the service name.
    int contextAreaLength = sizeof(WolaMessageContextAreaHeader_t) +
                            getWolaMessageContextTotalLength( &wolaServiceNameContextArea_p->serviceNameContext.header );

    setContextAreaLengthAndOffset( messageHeader_p, contextAreaLength );

    return messageAndContextArea_p;
}

/**
 * Set the contextAreaLength/contextAreaOffset fields within the given messageHeader_p.
 *
 * The contextAreaOffset is set to the current totalMessageSize (i.e. it's like we're "appending" the data).
 * The totalMessageSize is updated to include the contextAreaLength
 *
 * NOTE: it's assumed the WolaMessage header has already been set up and the contextArea will immediately 
 * follow the header.
 *
 * @param messageHeader_p
 * @param contextAreaLength 
 *
 * @return messageHeader_p
 */
WolaMessage_t * setContextAreaLengthAndOffset(WolaMessage_t * messageHeader_p, unsigned int contextAreaLength) {

    messageHeader_p->contextAreaLength = contextAreaLength;
    messageHeader_p->contextAreaOffset = messageHeader_p->totalMessageSize;
    messageHeader_p->totalMessageSize += contextAreaLength;

    return messageHeader_p;
}

/**
 * Set the dataAreaLength/dataAreaOffset fields within the given messageHeader_p.
 *
 * The dataAreaLength is equal to requestDataLength.
 * The dataAreaOffset is set to the current totalMessageSize (i.e. it's like we're "appending" the data).
 * The totalMessageSize is updated to include the requestDataLength.
 *
 * NOTE: it's assumed the WolaMessage header and context area have already been set up and 
 * that the dataArea will immediately follow the header/context area.
 *
 * @param messageHeader_p
 * @param requestDataLength - the dataAreaLength.
 *
 * @return messageHeader_p
 */
WolaMessage_t * setDataAreaLengthAndOffset(WolaMessage_t * messageHeader_p, unsigned int requestDataLength) {

    messageHeader_p->dataAreaLength = requestDataLength;
    messageHeader_p->dataAreaOffset = messageHeader_p->totalMessageSize;
    messageHeader_p->totalMessageSize += requestDataLength;

    return messageHeader_p;
}


/**
 * Build a WolaMessage header to represent a response message.
 *
 * Note the responseData itself is not copied into the header; only the length and
 * offset to the responseData is set (this method assumes the responseData will
 * immediately follow the header).
 *
 * @param messageHeader_p - The WolaMessage header to populate
 * @param workType
 * @param requestId - The id of the request associated with this response
 * @param responseDataLength - The length of the response data payload
 * @param responseException - flag indicates whether the response is an exception
 *
 * @return messageHeader_p
 */
WolaMessage_t * buildWolaMessageResponseHeader(WolaMessage_t * messageHeader_p, 
                                               unsigned int workType,
                                               unsigned int requestId,
                                               unsigned int responseDataLength,
                                               unsigned int responseException) {

    initializeMessageHeader(messageHeader_p, workType);
    messageHeader_p->requestId = requestId;
    messageHeader_p->responseException = responseException;
    setDataAreaLengthAndOffset(messageHeader_p, responseDataLength);
    messageHeader_p->messageType = WOLA_MESSAGE_TYPE_RESPONSE ;

    return messageHeader_p;
}

/**
 * @return A ref to the ACEE in the TCBSENV field for this TCB.
 */
acee * __ptr32 getAceeFromTCB(void) {
    psa* psa_p = (psa *)0L;
    tcb* tcb_p = (tcb*) psa_p->psatold;
    return (acee * __ptr32) tcb_p->tcbsenv;
}

/**
 * @return A ref to the ACEE in the ASXBSENV field.
 */
acee * __ptr32 getAceeFromASXB(void) {
    asxb* asxb_p = ((ascb*)(((psa*)0)->psaaold))->ascbasxb;
    return (acee * __ptr32) asxb_p->asxbsenv;
}

/**
 * Copy the userId from the CICS ACEE provided in the cicsParms.
 *
 * @param messageHeader_p - The wola message
 * @param registration_p - A pointer to the client WOLA registration.
 * @param cicsParms_p - A pointer to CICS-specific parameters.
 * @param srcKey - The CICS ACEE storage key
 *
 * @return messageHeader_p
 */
WolaMessage_t * setMvsUserIdFromCics_sk(WolaMessage_t * messageHeader_p, 
                                        WolaRegistration_t* registration_p, 
                                        struct bboapc1p* cicsParms_p, 
                                        int srcKey) {

    acee* tempAcee_p = (void*) BBGZ_resetHighOrderBit(cicsParms_p->bboapc1p_acee);

    int userIdLength = 0;
    memcpy_sk(&userIdLength, &(tempAcee_p->aceeuser._aceeusrl), sizeof(tempAcee_p->aceeuser._aceeusrl), srcKey);

    memcpy_sk(messageHeader_p->mvsUserID, 
              tempAcee_p->aceeuser._aceeusri, 
              BBGZ_min(sizeof(messageHeader_p->mvsUserID), userIdLength), 
              srcKey);

    return messageHeader_p;
}

/**
 * Copy the TCBSENV (or if that's null, the ASXBSENV) user ID into the WOLA Message.
 *
 * @param messageHeader_p - The wola message
 *
 * @return messageHeader_p
 */
WolaMessage_t * propagateMvsUserId(WolaMessage_t * messageHeader_p) {
    
    acee * __ptr32 acee_p = getAceeFromTCB();
    acee_p = (acee_p == NULL) ? getAceeFromASXB() : acee_p;

    if (acee_p != NULL) {
        memcpy( messageHeader_p->mvsUserID, acee_p->aceeuser._aceeusri, BBGZ_min(sizeof(messageHeader_p->mvsUserID), (int) acee_p->aceeuser._aceeusrl));
    }

    return messageHeader_p;
}

/**
 * Set the MvsUserId field in the WolaMessage with the userId from the ACEE on the
 * TCB (TCBSENV).
 *
 * This method should be invoked on the client TCB (after PC-ing to our authorized code),
 * since we're pulling the ID from the TCB.
 *
 * Later on, the WOLA channel in the server will create a SAFCredential and a Subject 
 * for the ID and set it as the J2EE RunAs Subject prior to invoking the EJB.  The EJB
 * wrapper handles calling the security service with the Subject to perform J2EE authorization.
 *
 * Note: there are z/OS integrity/security concerns with this path, since the server will
 * create an asserted credential for whatever ID is in the WOLA message.  The powers that
 * be say we're OK with this, because we trust where we got the ID from (WOLA messages are
 * built only within our authorized code and delivered via localcomm, which is all trusted).
 *
 * @param messageHeader_p - The wola message
 * @param registration_p - A pointer to the client WOLA registration.
 * @param cicsParms_p - A pointer to CICS-specific parameters.
 *
 * @return messageHeader_p
 */
WolaMessage_t * setCallersMvsUserId(WolaMessage_t * messageHeader_p, WolaRegistration_t* registration_p, struct bboapc1p* cicsParms_p) {

    // If the client registration has security enabled, and if we have CICS parameters, use the ACEE
    // in the CICS parameters to generate the user ID.
    if ((registration_p->flags.propAceeFromTrueIntoServer == 1) && (cicsParms_p != NULL) && (cicsParms_p->bboapc1p_acee != 0)) {

        bbgz_psw pswFromLinkageStack;
        return setMvsUserIdFromCics_sk( messageHeader_p, 
                                        registration_p,
                                        cicsParms_p,
                                        extractPSWFromLinkageStack(&pswFromLinkageStack)->key );
    } else {
        return propagateMvsUserId(messageHeader_p);
    }
}

#pragma linkage(BBGAIMSS, OS_NOSTACK)
int BBGAIMSS(void);

/**
 * Preview and read a WOLA request or response.
 *
 * Note: the WolaMessage is cached in the WolaConnectionHandle.
 *
 * TODO: break function into:
 *       WolaMessage_t * previewAndReadWolaMessage(...)
 *       setMessageAndContextAreas(WolaClientConnectionHandle_t *, WolaMessage_t *)
 *
 * @param wolaClientConnectionHandle_p Client connection handle.
 * @param lCommConnectionHandle_p      Local comm connection handle.
 * @param waitForData                  Wait for data flag.
 * @param dataLength_p                 Output area to get data length.
 * @param connHdlState                 Current connection handle state.
 * @param newConnHdlState              New connection handle state. Set after successful read.
 * @param reasonCode_p                 Output area to get the reason code.
 *
 * @return WolaMessage_t.returnCode (0 for success); otherwise WOLA_RC_ERROR8 (see reasonCode)
 */
unsigned int previewAndReadMessageAndContexts(WolaClientConnectionHandle_t* wolaClientConnectionHandle_p,
                                              OpaqueClientConnectionHandle_t * lCommConnectionHandle_p,
                                              unsigned int waitForData,
                                              unsigned long long* dataLength_p,
                                              unsigned long long connHdlState,
                                              unsigned long long newConnHdlState,
                                              unsigned int* reasonCode_p ) {

    unsigned long long dataLength = 0;
    int timeToWait = 60;
    int isIMSStopping = 0;

    int rc = localCommClientPreview(lCommConnectionHandle_p, (unsigned char) waitForData, timeToWait, &dataLength);

    // Check after 60 seconds to see if we might be in an IMS region that's shutting down.
    while (rc == LCOM_PREVIEW_RC_TIMEDOUT) {
        isIMSStopping = BBGAIMSS();

        if (isIMSStopping == 4) {
            // We're not in an IMS region, so just wait with no timeout
            timeToWait = 0;
        } else if (isIMSStopping == 1) {
            // IMS is stopping, stop listening and return
            *reasonCode_p = WOLA_RSN_IMS_STOPPING;
            return WOLA_RC_ERROR8;
        }

        // Retry
        rc = localCommClientPreview(lCommConnectionHandle_p, (unsigned char) waitForData, timeToWait, &dataLength);
    }

    if (rc != 0) {
        localCommFFDC(lCommConnectionHandle_p, TP(1), "localCommClientPreview:rc=%d,waitForData=%d,timeToWait=%d", rc, waitForData, timeToWait);
        changeHandleState(wolaClientConnectionHandle_p, connHdlState, BBOAHDL_ERROR);
        *reasonCode_p = WOLA_RSN_LC_PREVIEW;
        return WOLA_RC_ERROR8;
    }
    if ((waitForData == 0) && (dataLength == 0)) {
        *dataLength_p = 0xFFFFFFFFFFFFFFFF; // set to all FFs (-1)
    } else {
        // if data less than size of a message header get out
        if (dataLength < sizeof(WolaMessage_t)) {
            localCommFFDC(lCommConnectionHandle_p, TP(2), "previewAndRead:dataLength too small(%d<%d)", dataLength, sizeof(WolaMessage_t));
            changeHandleState(wolaClientConnectionHandle_p, connHdlState, BBOAHDL_ERROR);
            *reasonCode_p = WOLA_RSN_LC_PREVIEW3;
            return WOLA_RC_ERROR8;
        }

        // We're going to read the message header, and contexts (if any).
        void* msgCell_p = malloc(sizeof(WolaMessage_t));
        if (msgCell_p == 0) {
            localCommFFDC(lCommConnectionHandle_p, TP(3), "previewAndRead:malloc(%d) failed", sizeof(WolaMessage_t));
            changeHandleState(wolaClientConnectionHandle_p, connHdlState, BBOAHDL_ERROR);
            *reasonCode_p = WOLA_RSN_NO_MEMORY;
            return WOLA_RC_ERROR8;
        }

        // read message header so can get length
        rc = localCommReceive(lCommConnectionHandle_p, sizeof(WolaMessage_t), msgCell_p, 2);
        // for the client read, rc should get the returnDataSize
        if (rc > 0) {
            WolaMessage_t* wolaMessage_p = msgCell_p;
            dataLength -= sizeof(WolaMessage_t);
            void* msgCell2_p = NULL;

            // Read the contexts, if any.
            int previewMessageContextRC = 0, receiveContextsRC = 0;
            unsigned char localCommError = FALSE;
            if (wolaMessage_p->contextAreaLength > 0) {
                unsigned long long contextDataLeftToRead = wolaMessage_p->contextAreaLength;
                msgCell2_p = malloc(contextDataLeftToRead); // TODO: Return code check.
                unsigned char* curReadLoc_p = msgCell2_p;
                while ((localCommError == FALSE) && (contextDataLeftToRead > 0)) {
                    // Make sure there is some local comm data available for us to read.
                    previewMessageContextRC = 0;
                    receiveContextsRC = 0;
                    if (dataLength == 0) {
                        int previewMessageContextRC = localCommClientPreview(lCommConnectionHandle_p, 1 /* wait */, timeToWait, &dataLength);
                        if (previewMessageContextRC != LCOM_PREVIEW_RC_OK) {
                            localCommError = TRUE;
                        }
                    }

                    // Read at least part of the contexts.
                    if (localCommError == FALSE) {
                        unsigned long long lengthToReceive = (dataLength >= contextDataLeftToRead) ? contextDataLeftToRead : dataLength;
                        int receiveContextsRC = localCommReceive(lCommConnectionHandle_p, lengthToReceive, curReadLoc_p, 2);
                        if (receiveContextsRC > 0) { /* Read some data */
                            contextDataLeftToRead -= receiveContextsRC;
                            curReadLoc_p += receiveContextsRC;
                        } else {
                            localCommError = TRUE;
                        }
                    }
                }
            }

            // Save message and contexts in the connection handle, for later get context/data calls.
            if (localCommError == FALSE)  {
                // TODO look at twas it had some more error checking and reason codes
                rc = changeHandleState(wolaClientConnectionHandle_p, connHdlState, newConnHdlState);
                if (rc == 0) {
                    rc = setMessageAndContextAreas(wolaClientConnectionHandle_p, msgCell_p, msgCell2_p);
                }
                if (rc != 0) {
                    localCommFFDC(lCommConnectionHandle_p, TP(4), "changeHandleState:rc=%d,old:x%llx,new:x%llx", rc, connHdlState, newConnHdlState);
                    free(msgCell_p);
                    if (msgCell2_p != NULL) free(msgCell2_p);
                    changeHandleState(wolaClientConnectionHandle_p, connHdlState, BBOAHDL_ERROR);
                    *reasonCode_p = WOLA_RSN_INVALID_CONN_HDL;
                    return WOLA_RC_ERROR8;
                }
                // SUCCESS PATH!
                // return length of the response, and return/reason code from the
                // message.  If we cached any data, it will get cleaned up when
                // the connection is returned to the pool.
                
                // Copy the requestId into the conn handle for use later on the response.
                wolaClientConnectionHandle_p->handle_p->requestId = wolaMessage_p->requestId;

                *dataLength_p = wolaMessage_p->dataAreaLength;
                *reasonCode_p = wolaMessage_p->reasonCode;
                return wolaMessage_p->returnCode;
            } else { // else did not get message header and contexts
                localCommFFDC(lCommConnectionHandle_p, TP(5), "previewAndRead:previewMessageContextRC=%d,receiveContextsRC=%d", previewMessageContextRC, receiveContextsRC);
                free(msgCell_p);
                if (msgCell2_p != NULL) free(msgCell2_p);
                changeHandleState(wolaClientConnectionHandle_p, connHdlState, BBOAHDL_ERROR);
                *reasonCode_p = WOLA_RSN_LC_RECEIVE;
                return WOLA_RC_ERROR8;
            }
        } else {
            localCommFFDC(lCommConnectionHandle_p, TP(6), "localCommReceive:rc=%d,dataLength=%d", rc, sizeof(WolaMessage_t));
            free(msgCell_p);
            changeHandleState(wolaClientConnectionHandle_p, connHdlState, BBOAHDL_ERROR);
            *reasonCode_p = WOLA_RSN_LC_RECEIVE;
            return WOLA_RC_ERROR8;
        }
    }
    return 0;
}

/**
 * @return a ref to the first WolaMessageContextHeader in the given context area.
 */
WolaMessageContextHeader_t * getFirstContext(WolaMessageContextAreaHeader_t * wolaMessageContextAreaHeader_p) {
    if (wolaMessageContextAreaHeader_p->numContexts == 0) {
        return NULL;
    } else {
        return (WolaMessageContextHeader_t*) (((char *)wolaMessageContextAreaHeader_p) + sizeof(WolaMessageContextAreaHeader_t));
    }
}

/**
 * Note: Make sure to use this method withing a for-loop checking the numContexts as listed in the 
 * context area header, otherwise this function will blindly walk off the end of the context list.
 *
 * @return a ref to the next WolaMessageContextHeader (which immediately follows the given context).
 */
WolaMessageContextHeader_t * getNextContext(WolaMessageContextHeader_t * wolaMessageContextHeader_p) {
    return (WolaMessageContextHeader_t*) (((char*) wolaMessageContextHeader_p) + getWolaMessageContextTotalLength(wolaMessageContextHeader_p));
}

/**
 * @return A ref to the WolaMessageContextHeader_t with the given contextId in the given context area;
 *         or NULL if no such context exists.
 */
WolaMessageContextHeader_t * getWolaMessageContext( WolaMessageContextAreaHeader_t * wolaMessageContextAreaHeader_p, int contextId) {

    WolaMessageContextHeader_t* wolaMessageContextHeader_p = getFirstContext( wolaMessageContextAreaHeader_p );

    // Loop thru the contexts looking for the one we want
    for (int i = 0; i < wolaMessageContextAreaHeader_p->numContexts; ++i) {

        if (wolaMessageContextHeader_p->contextId == contextId) {
            return wolaMessageContextHeader_p;
        } else {
            wolaMessageContextHeader_p = getNextContext( wolaMessageContextHeader_p ); 
        }
    } 

    return NULL;
}

/**
 * Copy out the service name from the given service name context.
 *
 * @param buffer_p - The buffer to copy into (must be at least 256+1 bytes)
 * @param wolaServiceNameContext_p
 *
 * @return 0 if all is well; 8 if name was longer than 256.
 */
int extractServiceName( char* buffer_p, WolaServiceNameContext_t * wolaServiceNameContext_p ) {

    if (wolaServiceNameContext_p->nameLength > 256) {
        return 8;
    }
    memcpy(buffer_p, ((char*) wolaServiceNameContext_p) + sizeof(WolaServiceNameContext_t), wolaServiceNameContext_p->nameLength);
    memset(buffer_p + wolaServiceNameContext_p->nameLength, 0, 1); // null terminate it
    return 0;
}

/**
 * Parse the message for the service name context and return a null terminated service name.
 *
 * @param wolaClientConnectionHandle_p     Client connection handle. Contains the WolaMessage.
 * @param requestServiceNameFromContext_p  Pointer to 257 byte output area to get the null terminated service name.
 *
 * @return 0 if all is well;
 *         non-zero if message could not be pulled out of the connection handle;
 *         4 if the message contains no context area;
 *         8 if the service name length > 256 bytes long;
 *         16 if the service name context is not found;
 */
unsigned int getServiceNameFromContext(WolaClientConnectionHandle_t* wolaClientConnectionHandle_p, char* requestServiceNameFromContext_p) {

    // null terminate for when we go back without finding the service context name
    memset(requestServiceNameFromContext_p, 0, 1); 

    // pull out the contexts from the handle with plo.
    WolaMessage_t* wolaMessage_p;
    WolaMessageContextAreaHeader_t* contexts_p;
    int rc = getMessageAndContextAreas(wolaClientConnectionHandle_p, (void **) &wolaMessage_p, (void**) &contexts_p);
    if (rc != 0) {
        return rc;  // Error. Bail.
    }

    if (contexts_p == NULL) {
        return 4;   // Error. Bail.
    }

    WolaServiceNameContext_t* wolaServiceNameContext_p = (WolaServiceNameContext_t *) getWolaMessageContext( contexts_p, BBOASNC_Identifier);
    if (wolaServiceNameContext_p == NULL) {
        return 16;  // Error. Bail.
    }

    // copy the service name out of the context
    rc = extractServiceName( requestServiceNameFromContext_p, wolaServiceNameContext_p );
    if (rc != 0) {
        return rc; // Error. Bail.
    }

    // Put the contexts and message back in the message so next caller can use them.
    rc = setMessageAndContextAreas(wolaClientConnectionHandle_p, wolaMessage_p, contexts_p);
    if (rc != 0) {
        return rc; // Error. Bail.
    }

    return 0;
}


