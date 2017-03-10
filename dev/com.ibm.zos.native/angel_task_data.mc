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

#include "include/angel_task_data.h"

#include "include/angel_process_data.h"
#include "include/common_defines.h"
#include "include/mvs_storage.h"
#include "include/mvs_utils.h"

angel_task_data* getAngelTaskDataFromAlternateTCB(void* tcb_p) {
    stcb* stcb_p = ((tcb*)tcb_p)->tcbstcb;
    common_task_data_anchor* ctda_p = (common_task_data_anchor*)stcb_p->stcbbcba;
    angel_task_data* atd_p = (ctda_p != NULL) ? (angel_task_data*)(ctda_p->angel_task_data_p) : NULL;
    return atd_p;
}

angel_task_data* initializeAngelTaskData(void) {
    angel_task_data* atd_p = NULL;

    psa* psa_p = NULL;
    tcb* tcb_p = (tcb*) psa_p->psatold;
    stcb* stcb_p = tcb_p->tcbstcb;

    // -----------------------------------------------------------------------
    // Only initialize if the STCBBCBA is not set.  If it's already set,
    // leave it alone.
    // -----------------------------------------------------------------------
    if (stcb_p->stcbbcba == NULL) {
        int soReturnCode;
        common_task_data_anchor* ctda_p = storageObtain(sizeof(common_task_data_anchor),
                                                        COMMON_TASK_DATA_ANCHOR_SUBPOOL,
                                                        COMMON_TASK_DATA_ANCHOR_KEY,
                                                        &soReturnCode);
        if (ctda_p != NULL) {
            memset(ctda_p, 0, sizeof(common_task_data_anchor));
            memcpy(ctda_p->eyecatcher, COMMON_TASK_DATA_ANCHOR_EYE, strlen(COMMON_TASK_DATA_ANCHOR_EYE));
            ctda_p->length = sizeof(common_task_data_anchor);
            ctda_p->version = 0;

            // ---------------------------------------------------------------
            // The angel task data is located in the stack prefix area.
            // ---------------------------------------------------------------
            atd_p = getAngelTaskData();
            ctda_p->angel_task_data_p = atd_p;

            // ---------------------------------------------------------------
            // Store into the STCBBCBA.
            // ---------------------------------------------------------------
            unsigned char saved_key = switchToKey0();
            stcb_p->stcbbcba = ctda_p;
            switchToSavedKey(saved_key);
        }
    }

    return atd_p;
}

void destroyAngelTaskData(void) {
    // -----------------------------------------------------------------------
    // See if we have set ourselves into the STCB yet.
    // -----------------------------------------------------------------------
    psa* psa_p = NULL;
    tcb* tcb_p = (tcb*) psa_p->psatold;
    stcb* stcb_p = tcb_p->tcbstcb;
    common_task_data_anchor* ctda_p = (common_task_data_anchor*)stcb_p->stcbbcba;

    if (ctda_p != NULL) {
        // --------------------------------------------------------------------
        // Free up the heap cache for this task.
        // --------------------------------------------------------------------
        angel_process_data* apd_p = getAngelProcessData();
        if (apd_p != NULL) {
            struct __csysenvtoken_s* cenv_p = (struct __csysenvtoken_s*)(apd_p->key2_env_p);
            if (cenv_p != NULL) {
                void* heapAnchor_p = (void*)(cenv_p->__csetheapuserdata);
                if (heapAnchor_p != NULL) {
                    taskLevelHeapCleanup(heapAnchor_p, NULL);
                }
            }
        }

        angel_task_data* atd_p = (angel_task_data*)(ctda_p->angel_task_data_p);
        if (atd_p != NULL) {
            atd_p->noTaskLevelHeapCache = 1; // Prevent future caches.
        }

        // ------------------------------------------------------------------------
        // Rip out the STCBBCBA.  Some exit linkage will free the angel task
        // data when it sees that the STCBBCBA is not set.  If we're not using
        // that exit linkage, the task data will be freed by MVS when the task
        // ends.
        // ------------------------------------------------------------------------
        unsigned char saved_key = switchToKey0();
        stcb_p->stcbbcba = NULL;
        switchToSavedKey(saved_key);

        storageRelease(ctda_p, sizeof(common_task_data_anchor),
                       COMMON_TASK_DATA_ANCHOR_SUBPOOL,
                       COMMON_TASK_DATA_ANCHOR_KEY);
    }
}

