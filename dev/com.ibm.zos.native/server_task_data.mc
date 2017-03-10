/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2011
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
#include <metal.h>
#include <string.h>

#include "include/ieantc.h"
#include "include/server_task_data.h"

#include "include/common_defines.h"
#include "include/mvs_utils.h"

// --------------------------------------------------------------------------
// This token name is also defined in macros/SUAUTHPR.
// --------------------------------------------------------------------------
#define UNAUTH_TASK_DATA_TOKEN_NAME "BBGZ_SUAUTHPR_DA"

void setServerTaskDataIntoTrustedChain(void) {
    psa* psa_p = NULL;
    tcb* tcb_p = (tcb*) psa_p->psatold;
    stcb* stcb_p = tcb_p->tcbstcb;
    common_task_data_anchor* ctda_p = (common_task_data_anchor*)stcb_p->stcbbcba;
    if (ctda_p != NULL) {
        if (ctda_p->server_task_data_p == NULL) {
            void* std_p = getServerTaskData();
            ctda_p->server_task_data_p = std_p;
        }

        if (ctda_p->server_task_data_unauth_p == NULL) {
            char unauthTaskDataTokenName[16];
            char unauthTaskDataToken[16];
            int rc = -1;

            memcpy(unauthTaskDataTokenName, UNAUTH_TASK_DATA_TOKEN_NAME, sizeof(unauthTaskDataTokenName));

            iean4rt(IEANT_TASK_LEVEL,
                    unauthTaskDataTokenName,
                    unauthTaskDataToken,
                    &rc);

            if (rc == 0) {
                memcpy(&(ctda_p->server_task_data_unauth_p), unauthTaskDataToken, sizeof(ctda_p->server_task_data_unauth_p));
            }
        }
    }
}

server_task_data* getServerTaskDataFromAlternateTCB(void* tcb_p) {
    stcb* stcb_p = ((tcb*)tcb_p)->tcbstcb;
    common_task_data_anchor* ctda_p = (common_task_data_anchor*)stcb_p->stcbbcba;
    server_task_data* std_p = (ctda_p != NULL) ? (server_task_data*)(ctda_p->server_task_data_p) : NULL;
    return std_p;
}
