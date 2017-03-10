/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2015
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
#include <metal.h>

#include "include/mvs_abend.h"
#include "include/mvs_plo.h"
#include "include/mvs_psw.h"
#include "include/mvs_svcdump_services.h"
#include "include/mvs_utils.h"
#include "include/server_wola_client.h"
#include "include/server_wola_client_connection_get.h"
#include "include/server_wola_connection_pool.h"

#define WAIT_SECONDS 2

/**
 * Rebind.  The client thinks it needs to rebind to the server (presumably because one of the
 * other WOLA services told it to).  The server has moved or the WOLA OSGi service has been
 * restarted.
 *
 * @param parms_p A pointer to the parameter structure.
 */
void wolaClientRebind(WOLARebindParms_t* parms_p) {
    unsigned int localRC = 0;

    if (parms_p->rc_p == 0) {
        return;
    }

    bbgz_psw pswFromLinkageStack;
    extractPSWFromLinkageStack(&pswFromLinkageStack);

    /* Make sure the register name exists. */
    GetWolaRegistrationData_t wolaRegistrationData;
    volatile WolaRegistration_t* regEntry_p = getWolaRegistration(parms_p->registerName, &wolaRegistrationData);
    if (regEntry_p == NULL) {
        localRC  = WOLA_RC_INTERNAL_REBIND_ERROR_NAME;
        memcpy_dk(parms_p->rc_p, &localRC, sizeof(localRC), pswFromLinkageStack.key);
        return;
    }

    /* At this point we should still be attached to the WOLA shared memory that we want */
    /* because the client has not broken its bind to the old server instance yet.       */
    /* However, lets connect to it again from the bind that we're running under (to the */
    /* new server instance) right now.                                                  */
    WolaSharedMemoryAnchor_t* wolaAnchor_p = clientConnectToWolaSharedMemoryAnchor(parms_p->daemonGroupName);
    if (wolaAnchor_p == NULL) {
        /* It should be impossible to fall in here.  How could the WOLA anchor be gone? */
        localRC  = WOLA_RC_INTERNAL_REBIND_ERROR_NAME;
        memcpy_dk(parms_p->rc_p, &localRC, sizeof(localRC), pswFromLinkageStack.key);
        return;
    }

    /* Make sure the 3 part name is correct. */
    char localWolaGroupName[8];
    char localWolaNamePart2[8];
    char localWolaNamePart3[8];

    PloCompareAndSwapAreaDoubleWord_t swapArea;
    PloLoadAreaDoubleWord_t loadArea;

    swapArea.expectedValue = wolaRegistrationData.stckLastStateChange;
    swapArea.compare_p = (unsigned long long*)(&(regEntry_p->stckLastStateChange));
    loadArea.loadLocation_p = (void*)(&(regEntry_p->serverRegistration_p));

    /* Try to load the server RGE - if we can't, the client RGE has changed. */
    if (ploCompareAndLoadDoubleWord((void*)(&(regEntry_p->stckLastStateChange)), &swapArea, &loadArea) != 0) {
        localRC  = WOLA_RC_INTERNAL_REBIND_ERROR_NAME;
        memcpy_dk(parms_p->rc_p, &localRC, sizeof(localRC), pswFromLinkageStack.key);
        clientDisconnectFromWolaSharedMemoryAnchor(wolaAnchor_p);
        return;
    }

    /* We need to convert the 'null' characters in the wola group name to blanks, because */
    /* the wola group stored in the shared memory anchor is blank padded. */
    char wolaGroupNameBlankPadded[8];
    for (int x = 0; x < 8; x++) {
        if (parms_p->daemonGroupName[x] == 0) {
            wolaGroupNameBlankPadded[x] = ' ';
        } else {
            wolaGroupNameBlankPadded[x] = parms_p->daemonGroupName[x];
        }
    }

    /* The server RGE does not go away.  We can freely check the rest of the fields. */
    volatile WolaRegistration_t* serverRegistration_p = (WolaRegistration_t*)(loadArea.loadValue);
    if ((wolaAnchor_p != serverRegistration_p->wolaAnchor_p) ||
        (memcmp(wolaAnchor_p->wolaGroup, wolaGroupNameBlankPadded, 8) != 0) ||
        (memcmp((void*)(serverRegistration_p->serverNameSecondPart), parms_p->nodeName, 8) != 0) ||
        (memcmp((void*)(serverRegistration_p->serverNameThirdPart), parms_p->serverName, 8) != 0)) {
        localRC  = WOLA_RC_INTERNAL_REBIND_ERROR_NAME;
        memcpy_dk(parms_p->rc_p, &localRC, sizeof(localRC), pswFromLinkageStack.key);
        clientDisconnectFromWolaSharedMemoryAnchor(wolaAnchor_p);
        return;
    }

    /* Make sure the server's SToken is what the client thinks it should be. */
    if (memcmp((void*)(serverRegistration_p->stoken), &(parms_p->serverStoken), sizeof(SToken)) != 0) {
        localRC  = WOLA_RC_INTERNAL_REBIND_ERROR_STOKEN;
        memcpy_dk(parms_p->rc_p, &localRC, sizeof(localRC), pswFromLinkageStack.key);
        clientDisconnectFromWolaSharedMemoryAnchor(wolaAnchor_p);
        return;
    }

    /* Update the client RGE to reflect the server's current STCK.  We don't store the */
    /* server's SToken in the RGE, since the STCK is updated every time the SToken     */
    /* changes.                                                                        */
    if (memcmp((void*)(&(regEntry_p->serverStartSTCK)),
               (void*)(&(serverRegistration_p->stckLastStateChange)),
               sizeof(regEntry_p->serverStartSTCK)) != 0) {
        PloStoreAreaDoubleWord_t storeArea;
        storeArea.storeLocation_p = (void*)(&(regEntry_p->serverStartSTCK));
        storeArea.storeValue = serverRegistration_p->stckLastStateChange;
        swapArea.expectedValue = wolaRegistrationData.stckLastStateChange;
        swapArea.compare_p = (void*)(&(regEntry_p->stckLastStateChange));
        swapArea.replaceValue = swapArea.expectedValue;
        if (ploCompareAndSwapAndStoreDoubleWord((void*)(&(regEntry_p->stckLastStateChange)), &swapArea, &storeArea) == 0) {
            /* Don't disconnect from the shared memory anchor in this path. */
            /* The client is going to unbind from the failed server, which  */
            /* will detach the other attachment to the shared memory.       */
            localRC = WOLA_RC_INTERNAL_REBIND_OK;

            /* Go try to make a local comm connection to the new server     */
            /* instance.  This will establish a new LSCL control block and  */
            /* force our attachment to the LOCL to stick around after the   */
            /* client unbinds from the old server instance.                 */
            /* TODO: This needs to be better.  There is a lot that can go   */
            /*       wrong.  If we've reached the max connection limit, we  */
            /*       won't be able to get another connection.  Also, the    */
            /*       local comm cleanup code needs to change to recognize   */
            /*       that it can't detach from the LOCL when the client     */
            /*       unbinds from the old server instance if there are      */
            /*       still local comm connections in the pool.  The LSCL is */
            /*       no longer in the chain hung off the LOCL because the   */
            /*       server is gone, but the connections still point to the */
            /*       LSCL until the client destroys them.  The local comm   */
            /*       cleanup code just looks to see if there are LSCLs in   */
            /*       the chain, and if not, detach from the LOCL.           */
            WolaClientConnectionHandle_t connHandle;
            unsigned int getConnRsn = 0;
            int getConnRc = wolaConnectionGetCommon((WolaRegistration_t*)regEntry_p,
                                                    WAIT_SECONDS,
                                                    &connHandle, NULL,
                                                    &getConnRsn);
            if (getConnRc == 0) {
                freePooledConnection((WolaRegistration_t*)regEntry_p, &connHandle);
            } else {
                /* Try to be nice and just take a dump, but if that doesn't work, */
                /* abend.  If the client has other binds to other servers, it     */
                /* will be able to continue.                                      */
                if (takeSvcdump("server_wola_client_rebind.wolaClientRebind()") != 0) {
                    abend(ABEND_TYPE_SERVER, KRSN_SERVER_WOLA_CLIENT_REBIND_GETCONN_FAIL);
                }
            }

        } else {
            localRC = WOLA_RC_INTERNAL_REBIND_ERROR_STATE;
            clientDisconnectFromWolaSharedMemoryAnchor(wolaAnchor_p);
        }
    } else {
        localRC = WOLA_RC_INTERNAL_REBIND_NOT_NECESSARY;
        clientDisconnectFromWolaSharedMemoryAnchor(wolaAnchor_p);
    }

    memcpy_dk(parms_p->rc_p, &localRC, sizeof(localRC), pswFromLinkageStack.key);
}
