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

/**
 * Send response exception.
 *
 * @param parms_p A pointer to the parameter structure.
 */
void wolaSendResponseException(WOLASendResponseExceptionParms_t* parms_p) {
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
    int bufferCheckRC = checkBuffer(parms_p->excResponseData_p,
                                    parms_p->excResponseDataLength,
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
                                     parms_p->excResponseDataLength,
                                     parms_p->excResponseData_p,
                                     pswFromLinkageStack.key,
                                     1, // exception response
                                     &localRSN);

    // ----------------------------------------------------------------
    // Push output parameters back to caller
    // ----------------------------------------------------------------
    memcpy_dk(parms_p->rc_p, &localRC, sizeof(localRC), pswFromLinkageStack.key);
    memcpy_dk(parms_p->rsn_p, &localRSN, sizeof(localRSN), pswFromLinkageStack.key);
}
