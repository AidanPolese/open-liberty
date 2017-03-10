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

#include "include/mvs_psw.h"
#include "include/server_wola_client.h"
#include "include/server_wola_connection_pool.h"
#include "include/mvs_utils.h"

/**
 * Release connection.
 *
 * @param parms_p A pointer to the parameter structure.
 */
void wolaConnectionRelease(WOLAConnectionReleaseParms_t* parms_p) {
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

    WolaClientConnectionHandle_t* wolaClientConnectionHandle_p = (WolaClientConnectionHandle_t*) &(parms_p->connectionHandle[0]);
    if (connectionHandleValid(wolaClientConnectionHandle_p) == 0) {
        localRC = WOLA_RC_ERROR8;
        localRSN = WOLA_RSN_INVALID_CONN_HDL;
        memcpy_dk(parms_p->rc_p, &localRC, sizeof(localRC), pswFromLinkageStack.key);
        memcpy_dk(parms_p->rsn_p, &localRSN, sizeof(localRSN), pswFromLinkageStack.key);
        return;
    }

    // TODO why do I have to pull out the rge ptr?  freePooledConnection should just take client connection handle
    // pull out registration_p and then do plo compare and load to pull out the state.
    int freeRc = freePooledConnection(wolaClientConnectionHandle_p->handle_p->registration_p, wolaClientConnectionHandle_p);
    // Note: in tWAS, it was checking for invalid connection state but bbgaconp code is never returning the invalid connection state
    // Do we care if the RGE is destroyed or if the conn handle state is not ready 'coz anyway we are destroying the connection?
    if (freeRc == FreeConn_RC_InvalidClientHandle) {
        localRC = WOLA_RC_ERROR8;
        localRSN = WOLA_RSN_INVALID_CONN_HDL;
    }

    // ----------------------------------------------------------------
    // Push output parameters back to caller
    // ----------------------------------------------------------------
    memcpy_dk(parms_p->rc_p, &localRC, sizeof(localRC), pswFromLinkageStack.key);
    memcpy_dk(parms_p->rsn_p, &localRSN, sizeof(localRSN), pswFromLinkageStack.key);
}
