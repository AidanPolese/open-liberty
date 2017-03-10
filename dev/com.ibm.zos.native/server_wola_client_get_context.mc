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
#include "include/server_wola_cics_link_server.h"
#include "include/server_wola_client.h"
#include "include/server_wola_message.h"

int isFieldEmpty(unsigned char* inField, int length) {
    for (int i = 0; i < length; i++) {
        if (!(inField[i] == 0 || inField[i] == ' ')) {
            return 0;
        }
    }
    return 1;
}

void initializeBboauctx(struct BBOAUCTX * bboauctx_p) {
    memset(bboauctx_p, 0, sizeof(struct BBOAUCTX));
    memcpy(bboauctx_p->auctxeye, BBOAUCTX_EYE, sizeof(bboauctx_p->auctxeye));
    bboauctx_p->auctxver = BBOAUCTX_VER_1;
    bboauctx_p->auctxsiz = sizeof(struct BBOAUCTX);
    bboauctx_p->auctxsecoffs = offsetof(struct BBOAUCTX, auctxsecdata);
    bboauctx_p->auctxcicsoffs = offsetof(struct BBOAUCTX, auctxcicsdata);
    bboauctx_p->auctxtxnoffs = offsetof(struct BBOAUCTX, auctxtxndata);
}

void processSecurityContext(WolaMessage_t * wolaMessage_p, struct BBOAUCTX * bboauctx_p) {
    memcpy(bboauctx_p->auctxsecdata.auseceye, BBOAUSEC_EYE,
           sizeof(bboauctx_p->auctxsecdata.auseceye));
    bboauctx_p->auctxsecdata.ausecver = BBOAUSEC_VER_1;
    if (isFieldEmpty(wolaMessage_p->mvsUserID, sizeof(wolaMessage_p->mvsUserID))) {
        bboauctx_p->auctxsecdata.ausecflags.ausecflg_propsec = 0;
    } else {
        bboauctx_p->auctxsecdata.ausecflags.ausecflg_propsec = 1;
        memcpy(bboauctx_p->auctxsecdata.ausecuserid, wolaMessage_p->mvsUserID,
               sizeof(bboauctx_p->auctxsecdata.ausecuserid));
    }
}

void processCicsLinkServerContext (WolaCicsLinkServerContext_t * wolaCicsLinkServerContext_p, struct BBOAUCTX * bboauctx_p) {
    memcpy(bboauctx_p->auctxcicsdata.aucicseye, BBOAUCIC_EYE,
           sizeof(bboauctx_p->auctxcicsdata.aucicseye));
    bboauctx_p->auctxcicsdata.aucicsver = BBOAUCIC_VER_1;

    if (isFieldEmpty(wolaCicsLinkServerContext_p->linkTaskTranID, sizeof(wolaCicsLinkServerContext_p->linkTaskTranID))) {
        memset(bboauctx_p->auctxcicsdata.aucicslnktranid, ' ',
               sizeof(bboauctx_p->auctxcicsdata.aucicslnktranid));
    } else {
        memcpy(bboauctx_p->auctxcicsdata.aucicslnktranid, wolaCicsLinkServerContext_p->linkTaskTranID,
               sizeof(bboauctx_p->auctxcicsdata.aucicslnktranid));
    }

    // Determine which of the flags to use. Container bit on plus channel ID present = channel.
    // Container bit on but no channel ID = container. Container bit off = commarea.
    bboauctx_p->auctxcicsdata.aucicsflags.aucicsflg_commarea = 0;
    bboauctx_p->auctxcicsdata.aucicsflags.aucicsflg_container = 0;
    bboauctx_p->auctxcicsdata.aucicsflags.aucicsflg_channel = 0;
    if (wolaCicsLinkServerContext_p->useCICSContainer == 1 &&
        !isFieldEmpty(wolaCicsLinkServerContext_p->linkTaskChanID, sizeof(wolaCicsLinkServerContext_p->linkTaskChanID))) {
        bboauctx_p->auctxcicsdata.aucicsflags.aucicsflg_channel = 1;
        memcpy(bboauctx_p->auctxcicsdata.aucicslnkreqcontid, wolaCicsLinkServerContext_p->linkTaskChanID,
               sizeof(bboauctx_p->auctxcicsdata.aucicslnkreqcontid));
        bboauctx_p->auctxcicsdata.aucicslnkreqconttype =  wolaCicsLinkServerContext_p->linkTaskChanType;
    }
    else if (wolaCicsLinkServerContext_p->useCICSContainer == 1) {
        bboauctx_p->auctxcicsdata.aucicsflags.aucicsflg_container = 1;
        memcpy(bboauctx_p->auctxcicsdata.aucicslnkreqcontid, wolaCicsLinkServerContext_p->linkTaskReqContID,
               sizeof(bboauctx_p->auctxcicsdata.aucicslnkreqcontid));
        bboauctx_p->auctxcicsdata.aucicslnkreqconttype =  wolaCicsLinkServerContext_p->linkTaskReqContType;
    }
    else {
        bboauctx_p->auctxcicsdata.aucicsflags.aucicsflg_commarea = 1;
    }

    memcpy(bboauctx_p->auctxcicsdata.aucicslnkrspcontid, wolaCicsLinkServerContext_p->linkTaskRspContID,
           sizeof(bboauctx_p->auctxcicsdata.aucicslnkrspcontid));
    bboauctx_p->auctxcicsdata.aucicslnkrspconttype =  wolaCicsLinkServerContext_p->linkTaskRspContType;
}

/**
 * Get contexts.
 *
 * @param parms_p A pointer to the parameter structure.
 */
void wolaGetContext(WOLAGetContextParms_t* parms_p) {
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

    if (connectionHandleValid(wolaClientConnectionHandle_p) == 0) {
        localRSN = WOLA_RSN_INVALID_CONN_HDL;
        localRC = WOLA_RC_ERROR8;
    } else {
        /*---------------------------------------------------*/
        /* Check connection state                            */
        /*---------------------------------------------------*/
        unsigned long long connHdlState;
        int getRc = getHandleState(wolaClientConnectionHandle_p, &connHdlState);
        if (getRc != 0) {
            localRSN = WOLA_RSN_INVALID_CONN_HDL;
            localRC = WOLA_RC_ERROR8;
        } else if ((connHdlState != BBOAHDL_RESPONSE_RCVD) &&
                   (connHdlState != BBOAHDL_REQUEST_RCVD)) {
            localRSN = (connHdlState == BBOAHDL_ERROR) ? WOLA_RSN_CONN_LCOM_ERROR : WOLA_RSN_INVALID_CONN_STATE;
            localRC = WOLA_RC_ERROR8;
        } else {
            void* message_p = 0;
            void* contexts_p = 0;
            // Retrieve the message header and contexts.
            if (getMessageAndContextAreas(wolaClientConnectionHandle_p, &message_p, &contexts_p) != 0) {
                localRSN = WOLA_RSN_INVALID_CONN_HDL;
                localRC = WOLA_RC_ERROR8;
            } else {
                // Declare some local storage which we'll use to build the contexts
                // that we'll return to the caller.
                struct BBOAUCTX localReturnContexts;
                initializeBboauctx(&localReturnContexts);

                // Security context
                processSecurityContext((WolaMessage_t *)message_p, &localReturnContexts);

                // CICS context
                WolaCicsLinkServerContext_t* wolaCicsLinkServerContext_p =
                    (WolaCicsLinkServerContext_t *) getWolaMessageContext( contexts_p, CicsLinkServerContextId);
                processCicsLinkServerContext(wolaCicsLinkServerContext_p, &localReturnContexts);

                // Transaction context.  Not supported right now.
                memcpy(localReturnContexts.auctxtxndata.autxneye, BBOAUTXN_EYE,
                       sizeof(localReturnContexts.auctxtxndata.autxneye));
                localReturnContexts.auctxtxndata.autxnver = BBOAUTXN_VER_1;

                // Figure out how much data we can copy back to the caller.
                int copyLen = sizeof(localReturnContexts);
                if (parms_p->messageContextLength < sizeof(localReturnContexts)) {
                    copyLen = parms_p->messageContextLength;
                    localRSN = WOLA_RSN_MSGLEN_SMALLER;
                    localRC = WOLA_RC_ERROR8;
                }

                memcpy_dk(parms_p->messageContext_p, &localReturnContexts, copyLen, pswFromLinkageStack.key);
                localRV = sizeof(localReturnContexts);

                // Put the message header back in the message so next caller can use it.
                free(contexts_p);
                contexts_p = NULL;
                if (setMessageAndContextAreas(wolaClientConnectionHandle_p, message_p, contexts_p) != 0) {
                    // Over-ride any return code we may have already set.
                    localRSN = WOLA_RSN_INVALID_CONN_HDL;
                    localRC = WOLA_RC_ERROR8;
                }
            }
        }
    }

    // ----------------------------------------------------------------
    // Push output parameters back to caller
    // ----------------------------------------------------------------
    memcpy_dk(parms_p->rc_p, &localRC, sizeof(localRC), pswFromLinkageStack.key);
    memcpy_dk(parms_p->rsn_p, &localRSN, sizeof(localRSN), pswFromLinkageStack.key);
    memcpy_dk(parms_p->rv_p, &localRV, sizeof(localRV), pswFromLinkageStack.key);
}

