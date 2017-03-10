/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2012
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
#include "include/angel_client_process_data.h"

#include <metal.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#include "include/angel_sgoo_services.h"
#include "include/bpx_ipt.h"
#include "include/common_defines.h"
#include "include/ieantc.h"
#include "include/mvs_cell_pool_services.h"
#include "include/mvs_iarv64.h"

#include "include/gen/bpxzotcb.h"
#include "include/gen/ihaascb.h"
#include "include/gen/ihaassb.h"
#include "include/gen/ihastcb.h"
#include "include/gen/ikjtcb.h"

// Get the angel client process data
AngelClientProcessData_t* getAngelClientProcessData(AngelAnchor_t* angelAnchor_p) {
    char acpd_name[16];
    memset(acpd_name, 0, sizeof(acpd_name));

    if (angelAnchor_p != NULL) {
        int processDataInstanceNumber = (int) getAngelAnchorInstanceNumber(angelAnchor_p);
        snprintf(acpd_name, sizeof(acpd_name), ANGEL_CLIENT_PROCESS_DATA_TOKEN_NAME_NAMED, processDataInstanceNumber);
    } else {
        memcpy(acpd_name, ANGEL_CLIENT_PROCESS_DATA_TOKEN_NAME, strlen(ANGEL_CLIENT_PROCESS_DATA_TOKEN_NAME));
    }

    AngelClientProcessData_t* acpd_p = NULL;

    // ---------------------------------------------------------------
    // Find the angel client process data using the name token.
    // ---------------------------------------------------------------
    char acpd_token[16];
    int acpd_name_token_rc;
    int level = IEANT_HOMEAUTH_LEVEL;

    iean4rt(level,
            acpd_name,
            acpd_token,
            &acpd_name_token_rc);

    // -----------------------------------------------------------------------
    // If the name token exists, we can get the angel client process data from
    // it.  Otherwise, no luck.
    // -----------------------------------------------------------------------
    if (acpd_name_token_rc == 0) {
        memcpy(&acpd_p, acpd_token, sizeof(acpd_p));
    }

    return acpd_p;
}

/** Struct defining the system level client process data name token name for named angels. */
struct angelClientProcessDataNameTokenNamePrefix{
    unsigned char prefix[6];
    unsigned short angelAnchorInstanceNumber;
    SToken clientStoken;
};

AngelClientProcessData_t* getAngelClientProcessDataByStoken(void* stoken_p, AngelAnchor_t* angelAnchor_p) {
    AngelClientProcessData_t* acpd_p = NULL;

    // ---------------------------------------------------------------
    // Fill in the name token for lookup by stoken.
    // ---------------------------------------------------------------
    char acpd_name[16];
    char acpd_token[16];

    memset(acpd_name, 0, sizeof(acpd_name));
    if (angelAnchor_p == NULL) {
        memcpy(acpd_name, ANGEL_CLIENT_PROCESS_DATA_TOKEN_NAME, strlen(ANGEL_CLIENT_PROCESS_DATA_TOKEN_NAME));
        memcpy(&(acpd_name[8]), stoken_p, 8);
    } else {
        struct angelClientProcessDataNameTokenNamePrefix* struct_p = (struct angelClientProcessDataNameTokenNamePrefix*)acpd_name;
        memcpy(struct_p->prefix, ANGEL_CLIENT_PROCESS_DATA_TOKEN_NAME_NAMED_PREFIX, strlen(ANGEL_CLIENT_PROCESS_DATA_TOKEN_NAME_NAMED_PREFIX));
        struct_p->angelAnchorInstanceNumber = getAngelAnchorInstanceNumber(angelAnchor_p);
        memcpy(&(struct_p->clientStoken), stoken_p, sizeof(struct_p->clientStoken));
    }

    int acpd_name_token_rc;
    iean4rt(IEANT_SYSTEM_LEVEL,
            acpd_name,
            acpd_token,
            &acpd_name_token_rc);

    if (acpd_name_token_rc == 0) {
        memcpy(&acpd_p, acpd_token, sizeof(acpd_p));
    }

    return acpd_p;
}


// Create a new angel client process data control block.
AngelClientProcessData_t* createAngelClientProcessData(bbgzsgoo* sgoo_p, unsigned char curArmvSequence, angel_client_pc_recovery* recovery_p) {

    // -----------------------------------------------------------------------
    // The angel client process data comes from a shared above the bar cell
    // pool.  If we can get a cell, fill it in.
    // -----------------------------------------------------------------------
    AngelClientProcessData_t* acpd_p = getCellPoolCell(sgoo_p->bbgzsgoo_angelClientDataPool);
    if (acpd_p != NULL) {
        recovery_p->drm_clientProcessData_p = acpd_p;

        memset(acpd_p, 0, sizeof(*acpd_p));
        memcpy(acpd_p->eyecatcher, ANGEL_CLIENT_PROCESS_DATA_EYE_CATCHER, sizeof(acpd_p->eyecatcher));
        acpd_p->length = sizeof(*acpd_p);
        acpd_p->curArmvSeq = curArmvSequence;
        acpd_p->cenv_p = recovery_p->fsm_cenv_p;
        acpd_p->cenvParms_p = recovery_p->fsm_cenvParms_p;
        acpd_p->cenvParmsSubpool = recovery_p->fsm_cenvParmsSubpool;
        acpd_p->cenvParmsKey = recovery_p->fsm_cenvParmsKey;
        acpd_p->sgoo_p = sgoo_p;

        ascb* ascb_p = ((psa*)0)->psaaold;
        assb* assb_p = (assb*)(ascb_p->ascbassb);
        memcpy(&(acpd_p->stoken), &(assb_p->assbstkn), sizeof(acpd_p->stoken));

        // -------------------------------------------------------------------
        // We need to figure out what kind of client we have.  The 'end of
        // process' task for us will either be the IPT or the jobstep task.
        // -------------------------------------------------------------------
        tcb* ipt_p = getIPTandVerifyCallerIsRelated();
        acpd_p->tcbForTaskResmgr = (ipt_p != NULL) ? ipt_p : ascb_p->ascbxtcb;
        memcpy(&(acpd_p->ttokenForTaskResmgr), ((stcb*)(acpd_p->tcbForTaskResmgr->tcbstcb))->stcbttkn, sizeof(TToken));
        acpd_p->clientDynAreaPool = createClientDynamicAreaCellPool(&(acpd_p->clientDynAreaPoolInfo),
                                                                    &(recovery_p->drm_clientDynAreaPool_p),
                                                                    &(acpd_p->ttokenForTaskResmgr));

        // -------------------------------------------------------------------
        // Create two name tokens.
        //  1) A process level name token used by local callers.
        //  2) A system level name token used by the address space level
        //     RESMGR.  This name token contains our STOKEN.
        // -------------------------------------------------------------------
        if (acpd_p->clientDynAreaPool != 0L) {
            AngelAnchor_t* angelAnchor_p = sgoo_p->bbgzsgoo_angelAnchor_p;

            unsigned char acpdName[16];
            unsigned char acpdNameWithStoken[16];
            unsigned char acpdToken[16];

            // If no angel anchor, use default 'old style' name tokens.
            if (angelAnchor_p == NULL) {
                memcpy(acpdName, ANGEL_CLIENT_PROCESS_DATA_TOKEN_NAME, 8);
                memset(&(acpdName[8]), 0, 8);
                memcpy(acpdNameWithStoken, ANGEL_CLIENT_PROCESS_DATA_TOKEN_NAME, 8);
                memcpy(&(acpdNameWithStoken[8]), &(acpd_p->stoken), 8);
            } else {
                // With an angel anchor, need to encode the instance number into the name token names.
                unsigned short angelAnchorInstanceNumber = getAngelAnchorInstanceNumber(angelAnchor_p);
                memset(acpdName, 0, sizeof(acpdName));
                snprintf(acpdName, sizeof(acpdName), ANGEL_CLIENT_PROCESS_DATA_TOKEN_NAME_NAMED, angelAnchorInstanceNumber);
                struct angelClientProcessDataNameTokenNamePrefix* struct_p = (struct angelClientProcessDataNameTokenNamePrefix*) acpdNameWithStoken;
                memcpy(struct_p->prefix, ANGEL_CLIENT_PROCESS_DATA_TOKEN_NAME_NAMED_PREFIX, strlen(ANGEL_CLIENT_PROCESS_DATA_TOKEN_NAME_NAMED_PREFIX));
                struct_p->angelAnchorInstanceNumber = angelAnchorInstanceNumber;
                memcpy(&(struct_p->clientStoken), &(acpd_p->stoken), 8);
            }

            memcpy(acpdToken, &acpd_p, 8);
            memset(&(acpdToken[8]), 0, 8);

            int tokenRC;
            iean4cr(IEANT_HOME_LEVEL, acpdName, acpdToken, IEANT_NOPERSIST, &tokenRC);

            if (tokenRC == 0) {
                iean4cr(IEANT_SYSTEM_LEVEL, acpdNameWithStoken, acpdToken, IEANT_PERSIST, &tokenRC);

                if (tokenRC != 0) {
                    iean4dl(IEANT_HOME_LEVEL, acpdName, &tokenRC);
                    acpd_p->clientDynAreaPool = 0L;
                    destroyClientDynamicAreaCellPool(&(acpd_p->clientDynAreaPoolInfo));
                    recovery_p->drm_clientProcessData_p = NULL;
                    freeCellPoolCell(sgoo_p->bbgzsgoo_angelClientDataPool, acpd_p);
                    acpd_p = NULL;
                }
            } else {
                acpd_p->clientDynAreaPool = 0L;
                destroyClientDynamicAreaCellPool(&(acpd_p->clientDynAreaPoolInfo));
                recovery_p->drm_clientProcessData_p = NULL;
                freeCellPoolCell(sgoo_p->bbgzsgoo_angelClientDataPool, acpd_p);
                acpd_p = NULL;
            }
        } else {
            recovery_p->drm_clientProcessData_p = NULL;
            freeCellPoolCell(sgoo_p->bbgzsgoo_angelClientDataPool, acpd_p);
            acpd_p = NULL;
        }
    }

    return acpd_p;
}

// Destroy the angel client process data.
void destroyAngelClientProcessData(AngelClientProcessData_t* acpd_p, angel_client_pc_recovery* recovery_p) {
    // -----------------------------------------------------------------------
    // First clean up the name tokens.
    // -----------------------------------------------------------------------
    int tokenRC;
    unsigned char acpdName[16];
    unsigned char acpdNameWithStoken[16];

    bbgzsgoo* sgoo_p = acpd_p->sgoo_p;
    AngelAnchor_t* angelAnchor_p = sgoo_p->bbgzsgoo_angelAnchor_p;

    if (angelAnchor_p == NULL) {
        memcpy(acpdName, ANGEL_CLIENT_PROCESS_DATA_TOKEN_NAME, 8);
        memset(&(acpdName[8]), 0, 8);
        memcpy(acpdNameWithStoken, ANGEL_CLIENT_PROCESS_DATA_TOKEN_NAME, 8);
        memcpy(&(acpdNameWithStoken[8]), &(acpd_p->stoken), 8);
    } else {
        // With an angel anchor, need to encode the instance number into the name token names.
        unsigned short angelAnchorInstanceNumber = getAngelAnchorInstanceNumber(angelAnchor_p);
        memset(acpdName, 0, sizeof(acpdName));
        snprintf(acpdName, sizeof(acpdName), ANGEL_CLIENT_PROCESS_DATA_TOKEN_NAME_NAMED, angelAnchorInstanceNumber);
        struct angelClientProcessDataNameTokenNamePrefix* struct_p = (struct angelClientProcessDataNameTokenNamePrefix*) acpdNameWithStoken;
        memcpy(struct_p->prefix, ANGEL_CLIENT_PROCESS_DATA_TOKEN_NAME_NAMED_PREFIX, strlen(ANGEL_CLIENT_PROCESS_DATA_TOKEN_NAME_NAMED_PREFIX));
        struct_p->angelAnchorInstanceNumber = angelAnchorInstanceNumber;
        memcpy(&(struct_p->clientStoken), &(acpd_p->stoken), 8);
    }

    iean4dl(IEANT_SYSTEM_LEVEL, acpdNameWithStoken, &tokenRC);

    // -----------------------------------------------------------------------
    // Only delete the address space level token if we're running in the
    // home address space.
    // -----------------------------------------------------------------------
    if (memcmp(&(((assb*)(((ascb*)(((psa*)0)->psaaold))->ascbassb))->assbstkn), &(acpd_p->stoken), sizeof(SToken)) == 0) {
        iean4dl(IEANT_HOME_LEVEL, acpdName, &tokenRC);
    }

    // -----------------------------------------------------------------------
    // Next, free the storage.
    // -----------------------------------------------------------------------
    recovery_p->drm_clientDynAreaPool_p = NULL;
    long long cellPoolID = acpd_p->clientDynAreaPool;
    if (cellPoolID != 0L) {
        acpd_p->clientDynAreaPool = 0L;
        destroyClientDynamicAreaCellPool(&(acpd_p->clientDynAreaPoolInfo));
    }
    recovery_p->drm_clientProcessData_p = NULL;
    freeCellPoolCell(acpd_p->sgoo_p->bbgzsgoo_angelClientDataPool, acpd_p);
}

// Tries to find a bind between a client and a server.
AngelClientBindData_t* checkForExistingBind(AngelClientProcessData_t* acpd_p, SToken* targetServerStoken_p, int serverInstanceCount) {
    AngelClientBindDataNode_t* curNode_p = acpd_p->bindHead_p;
    AngelClientBindData_t* bindData_p = NULL;

    while ((curNode_p != NULL) && (bindData_p == NULL)) {
        AngelClientBindData_t* curBind_p = curNode_p->data_p;
        if ((memcmp(targetServerStoken_p, &(curBind_p->serverStoken), sizeof(*targetServerStoken_p)) == 0) &&
            (serverInstanceCount == curBind_p->serverInstanceCount)) {
            bindData_p = curBind_p;
        }
        curNode_p = curNode_p->next_p;
    }

    return bindData_p;
}

// Adds a new bind.
int addBindToClientProcessData(AngelClientProcessData_t* acpd_p, AngelClientBindData_t* bindData_p) {
    AngelClientBindDataNode_t* node_p = getCellPoolCell(acpd_p->sgoo_p->bbgzsgoo_clientBindDataNodePool);

    if (node_p != NULL) {
        node_p->data_p = bindData_p;
        node_p->next_p = acpd_p->bindHead_p;
        acpd_p->bindHead_p = node_p;
    }

    return (node_p != NULL) ? 0 : -1;
}

// Removes a bind.
int removeBindFromClientProcessData(AngelClientBindData_t* bindData_p) {
    AngelClientProcessData_t* acpd_p = bindData_p->clientProcessData_p;
    AngelClientBindDataNode_t* curNode_p = acpd_p->bindHead_p;
    AngelClientBindDataNode_t* prevNode_p = NULL;
    unsigned char removed = FALSE;

    while ((curNode_p != NULL) && (removed == FALSE)) {
        if (curNode_p->data_p == bindData_p) {
            if (prevNode_p == NULL) {
                acpd_p->bindHead_p = curNode_p->next_p;
            } else {
                prevNode_p->next_p = curNode_p->next_p;
            }
            freeCellPoolCell(acpd_p->sgoo_p->bbgzsgoo_clientBindDataNodePool, curNode_p);
            removed = TRUE;
        } else {
            prevNode_p = curNode_p;
            curNode_p = curNode_p->next_p;
        }
    }

    return (removed == TRUE) ? 0 : -1;
}

