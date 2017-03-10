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
#include <string.h>

#include "include/bpx_ipt.h"

#include "include/gen/bpxzotcb.h"
#include "include/gen/ihapsa.h"
#include "include/gen/ihastcb.h"

/**
 * Gets the TTOKEN for the IPT.  We use this to identify the process.  We would
 * use BPX4GPI (getPid()) but we can't call this when we are under the BPX4IPT
 * exit or we'll take a EC6 abend.
 *
 * @param ttoken_p A pointer to an area where the IPT's ttoken should be copied.
 *
 * @return 0 if the IPT ttoken was obtained and copied into the caller's area.
 *         non-zero if the IPT could not be located in the parent task chain.
 */
int getIPT_TToken(TToken* ttoken_p) {

    tcb* ipt_tcb_p = getIPTandVerifyCallerIsRelated();
    if (ipt_tcb_p != NULL) {
        stcb* stcb_p = ipt_tcb_p->tcbstcb;
        memcpy(ttoken_p, stcb_p->stcbttkn, sizeof(TToken));
    }

    return (ipt_tcb_p == NULL);
}


/**
 * Looks in the current task's TCB chain to find the initial pthread creating
 * task (IPT), and if found, returns a reference to it.
 *
 * @return The address of the TCB which is the IPT, if the IPT is found in the
 *         parent chain of the calling TCB.
 */
tcb* getIPTandVerifyCallerIsRelated(void) {
    psa* psa_p = NULL;
    tcb* caller_tcb_p = psa_p->psatold;
    tcb* current_tcb_p = caller_tcb_p;
    tcb* ipt_p = NULL;

    while ((ipt_p == NULL) && (current_tcb_p != NULL)) {
        stcb* stcb_p = current_tcb_p->tcbstcb;
        otcb* otcb_p = stcb_p->stcbotcb;

        if ((otcb_p != NULL) && (((otcb_p->otcbflagsb2) & otcbipt) == otcbipt)) {
            ipt_p = current_tcb_p;
        }

        current_tcb_p = current_tcb_p->tcbotc;
    }

    // -----------------------------------------------------------------------
    // If there's no IPT, see if our task is the top of the tree.  It could be
    // that it hasn't made any subtasks yet, and therefore there is no IPT.
    // -----------------------------------------------------------------------
    if (ipt_p == NULL) {
        stcb* callerStcb_p = caller_tcb_p->tcbstcb;
        otcb* callerOtcb_p = callerStcb_p->stcbotcb;

        tcb* nextTcb_p = caller_tcb_p->tcbotc;
        stcb* nextStcb_p = (nextTcb_p != NULL) ? nextTcb_p->tcbstcb : NULL;
        otcb* nextOtcb_p = (nextStcb_p != NULL) ? nextStcb_p->stcbotcb : NULL;

        if ((callerOtcb_p != NULL) &&
            (((callerOtcb_p->otcbflagsb1) & otcbinitialthread) == otcbinitialthread) &&
            (nextTcb_p != NULL) &&
            (nextOtcb_p == NULL)) {
            ipt_p = caller_tcb_p;
        }
    }

    return ipt_p;
}
