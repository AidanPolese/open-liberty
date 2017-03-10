/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2016
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */

/**
 * @file
 *
 * Assorted unauthorized routines used by the WOLA code.
 *
 */

#include <metal.h>
#include <stdlib.h>
#include <string.h>
#include <stdio.h>

#include "include/bboaims.h"
#include "include/common_defines.h"
#include "include/ras_tracing.h"
#include "include/server_wola_unauth_services.h"
#include "include/mvs_wait.h"
#include "include/server_wola_services.h"

#define RAS_MODULE_CONST  RAS_MODULE_SERVER_WOLA_UNAUTH_SERVICES

__asm("         DC A(X'80000000'+DFSYOPN1)" : "DS:4"(Addr_DFSYOPN1));
__asm("         DC A(X'80000000'+DFSYALOC)" : "DS:4"(Addr_DFSYALOC));
__asm("         DC A(X'80000000'+DFSYSEND)" : "DS:4"(Addr_DFSYSEND));
__asm("         DC A(X'80000000'+DFSYFREE)" : "DS:4"(Addr_DFSYFREE));
__asm("         DC A(X'80000000'+DFSYCLSE)" : "DS:4"(Addr_DFSYCLSE));

/**
 * {@inheritDoc}
 *
 * Call OTMA Open and wait until the open is successful
 *
 * @param OpenOTMAParms
*/
int openOTMAConnection(OpenOTMAParms* parms_p) {

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(1),
                    "pc_OpenOTMAConnection entry",
                    TRACE_DATA_RAWDATA(sizeof(OpenOTMAParms), parms_p, "Parms"),
                    TRACE_DATA_END_PARMS);
    }

    void* __ptr32 otma_open_p;

    memcpy(&otma_open_p, &Addr_DFSYOPN1, sizeof(otma_open_p));

    call31Bit(otma_open_p,
              &(parms_p->otma_anchor_p),
              &(parms_p->dynamic_area));

    wait(parms_p->ecb_p);

    return parms_p->otma_retrsn_p->ret;
}

int otmaAllocate(otma_alloc_parms * parms_p) {

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(3),
                    "otmaAllocate entry",
                    TRACE_DATA_END_PARMS);
    }

    void* __ptr32 otma_alloc_p;

    memcpy(&otma_alloc_p, &Addr_DFSYALOC, sizeof(otma_alloc_p));

    call31Bit(otma_alloc_p,
              &(parms_p->anchor_p),
              &(parms_p->dynamic_area));

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(4),
                    "otmaAllocate exit",
                    TRACE_DATA_END_PARMS);
    }

    return parms_p->retrsn.ret;

}

int otmaSendReceive(otma_sendrcv_parms * parms_p) {

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(5),
                    "otmaSendReceive entry",
                    TRACE_DATA_END_PARMS);
    }

    void* __ptr32 otma_sendrcv_p;

    memcpy(&otma_sendrcv_p, &Addr_DFSYSEND, sizeof(otma_sendrcv_p));

    call31Bit(otma_sendrcv_p,
              &(parms_p->anchor_p),
              &(parms_p->dynamic_area));

    parms_p->postCode = wait(parms_p->ecb_p);

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(6),
                    "otmaSendReceive exit",
                    TRACE_DATA_END_PARMS);
    }

    return parms_p->retrsn.ret;

}

int otmaFree(otma_free_parms * parms_p, void* ecb_p, int* postCode_p) {

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(7),
                    "otmaFree entry",
                    TRACE_DATA_RAWDATA(sizeof(otma_free_parms), parms_p, "OTMA Free parms"),
                    TRACE_DATA_PTR(ecb_p, "ECB Ptr"),
                    TRACE_DATA_INT(((postCode_p != NULL) ? *postCode_p : 0), "Post code"),
                    TRACE_DATA_END_PARMS);
    }

    int post_rc = 0;
    void* __ptr32 otma_free_p;

    memcpy(&otma_free_p, &Addr_DFSYFREE, sizeof(otma_free_p));

    call31Bit(otma_free_p,
              &(parms_p->anchor_p),
              &(parms_p->dynamic_area));

    // If free is being driven for a cancel, we may need to post the ECB for
    // otma_send_receivex.
    if ((parms_p->retrsn.ret == 0) && (ecb_p != NULL)) {
        post_rc = post(ecb_p, *postCode_p);
    }

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(8),
                    "otmaFree exit",
                    TRACE_DATA_INT(parms_p->retrsn.ret, "OTMA Return code"),
                    TRACE_DATA_INT(post_rc, "Post RC"),
                    TRACE_DATA_END_PARMS);
    }

    return parms_p->retrsn.ret;

}

int closeOtmaConnection(otma_close_parms * parms_p) {

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(9),
                    "closeOtmaConnection entry",
                    TRACE_DATA_END_PARMS);
    }

    void* __ptr32 otma_close_p;

    memcpy(&otma_close_p, &Addr_DFSYCLSE, sizeof(otma_close_p));

    call31Bit(otma_close_p,
              &(parms_p->anchor_p),
              &(parms_p->dynamic_area));

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(10),
                    "closeOtmaConnection exit",
                    TRACE_DATA_END_PARMS);
    }

    return parms_p->retrsn.ret;

}

void call31Bit(void* fcn_p, void* parms_p, void* dynArea_p) {
   __asm(" LG    15,%0 \n"                           /* Load your function pointer into R15 */
         " LG    1,%1  \n"                           /* Load your parm list into R1 */
         " LGR   2,13  \n"                           /* Save the current dynamic area address in R2 */
         " LG    13,%2 \n"                           /* Load your dynamic area address into R13, can't reference % fields now */
         " SAM31       \n"                           /* Switch to 31 bit mode */
         " BASR  14,15 \n"                           /* Branch and save caller address */
         " SAM64       \n"                           /* Switch back to 64 bit mode */
         " LGR   13,2  \n" :                         /* Restore original dynamic area, can reference % fields now */
         :                                           /* Nothing in the modifies list */
         "m"(fcn_p), "m"(parms_p), "m"(dynArea_p) :  /* Fillins 0, 1 and 2 */
         "r0", "r1", "r2", "r14", "r15");            /* Clobbers R0, R1, R2, R14, R15 */
}

/**
 * If the server process data has a list of OTMA anchors, close each one.
 *
 * @param spd_p The server process data to clean up
 */
void cleanupOTMAAnchors(server_process_data * spd_p) {

    if (TraceActive(trc_level_detailed)) {
            TraceRecord(trc_level_detailed,
                        TP(11),
                        "cleanupOTMAAnchors entry",
                        TRACE_DATA_END_PARMS);
        }


    while(spd_p->wola_otma_anchors_p != NULL){

        if (TraceActive(trc_level_detailed)) {
            TraceRecord(trc_level_detailed,
                        TP(12),
                        "cleanupOTMAAnchors",
                        TRACE_DATA_RAWDATA(sizeof(otma_anchor_t), spd_p->wola_otma_anchors_p->otma_anchor, "OTMA anchor"),
                        TRACE_DATA_END_PARMS);
        }

        // Copy parms below the bar
        otma_close_parms* parms_p = __malloc31(sizeof(otma_close_parms));

        if (parms_p != NULL) {

            memcpy(&parms_p->anchor, spd_p->wola_otma_anchors_p->otma_anchor, sizeof(otma_anchor_t));

            parms_p->retrsn.ret = -1;
            parms_p->retrsn.rsn[0] = -1;
            parms_p->retrsn.rsn[1] = -1;
            parms_p->retrsn.rsn[2] = -1;
            parms_p->retrsn.rsn[3] = -1;

            parms_p->anchor_p = &(parms_p->anchor);
            parms_p->retrsn_p = &(parms_p->retrsn);

            // Set high order bit to mark last parameter
            parms_p->retrsn_p = (otma_retrsn_t * __ptr32)
                    ((unsigned int) parms_p->retrsn_p | 0X80000000U);

            // Close the connection
            closeOtmaConnection(parms_p);

            // Get the next anchor in the list
            spd_p->wola_otma_anchors_p = spd_p->wola_otma_anchors_p->nextAnchor_p;
        }
        free(parms_p);
    }

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(13),
                    "cleanupOTMAAnchors exit",
                    TRACE_DATA_END_PARMS);
    }
}

#pragma insert_asm(" EXTRN DFSYOPN1")
#pragma insert_asm(" EXTRN DFSYALOC")
#pragma insert_asm(" EXTRN DFSYSEND")
#pragma insert_asm(" EXTRN DFSYFREE")
#pragma insert_asm(" EXTRN DFSYCLSE")

