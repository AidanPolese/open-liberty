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

#include <metal.h>

#include "include/gen/ihapsa.h"
#include "include/gen/cvt.h"
#include "include/gen/ihaecvt.h"

#include "include/ras_tracing.h"
#include "include/server_utils.h"

#define RAS_MODULE_CONST  RAS_MODULE_SERVER_UTILS_UNAUTH
#define TP_SERVER_UTILS_UNAUTH_TIMEUSED_ECT                             1
#define TP_SERVER_UTILS_UNAUTH_TIMEUSED_NO_ECT                          2

int getTimeusedData(TimeusedData * outDataPtr) {

    int rc = 16;
    psa*  psa_p = NULL;
    cvt*  cvt_p = (cvt* __ptr32) psa_p->flccvt;
    // check cvtect1
    if ((cvt_p->cvtoslv8 & cvtect1) == cvtect1 ) {
        struct parm31 {
            char f4sa[144];
            char storageArea[TIMEUSED_DATA_AREA_SIZE];
        };
        struct parm31* parms31_p = __malloc31(sizeof(struct parm31));
        memset(parms31_p, 0, sizeof(struct parm31));
        if (parms31_p != NULL) {
            // NOTE:
            // For TIME_ON_CP=YES, register 13 must contain
            // the address of a 36-word save area in F4SA format that resides below
            // the 2-gigabyte bar.
            //
            // When ECT=YES and one or more of TIME_ON_CP, OFFLOAD_TIME,
            // and OFFLOAD_ON_CP are specified, STORADR=addr specifies the address
            // of an 8-word area in 31-bit storage of the primary address space where
            // the accumulated time value(s) are returned.

            char * f4sa_p = &(parms31_p->f4sa[0]);
            __asm(" LGR 2,13 Save dynamic area address\n"
                  " LA  13,%0\n"
                  " TIMEUSED TIME_ON_CP=YES,OFFLOAD_TIME=YES,OFFLOAD_ON_CP=YES,ECT=YES,STORADR=(%1)\n"
                  " LGR 13,2 Restore dynamic area address" :
                  :
                  "m"(*(f4sa_p)),"r"(parms31_p->storageArea) :
                  "r0","r1","r2","r14","r15");
            if (TraceActive(trc_level_detailed)) {
                TraceRecord(trc_level_detailed,
                            TP(TP_SERVER_UTILS_UNAUTH_TIMEUSED_ECT),
                            "getTimeusedData, TIMEUSED ECT",
                            TRACE_DATA_RAWDATA(sizeof(parms31_p->storageArea),
                                               &(parms31_p->storageArea[0]),
                                               "storageArea"),
                            TRACE_DATA_END_PARMS);
            }
            memcpy(outDataPtr, &(parms31_p->storageArea[0]), sizeof(parms31_p->storageArea));
            free(parms31_p);
            parms31_p = 0;
            rc = 0;
        } else {
            // TODO error
        }

    } else {
        char storageArea[8];
        // doc for storaddr with these parms says address of a doubleword area
        __asm(" TIMEUSED LINKAGE=SYSTEM,CPU=TOD,STORADR=(%0)" :
              :
              "r"(storageArea) :
              "r0","r1","r14","r15");
        if (TraceActive(trc_level_detailed)) {
            TraceRecord(trc_level_detailed,
                        TP(TP_SERVER_UTILS_UNAUTH_TIMEUSED_NO_ECT),
                        "getTimeusedData, TIMEUSED no ECT",
                        TRACE_DATA_RAWDATA(sizeof(storageArea),
                                           storageArea,
                                           "storageArea"),
                        TRACE_DATA_END_PARMS);
        }
        memcpy(outDataPtr, storageArea, sizeof(storageArea));
        rc = 4;
    }
    return rc;
}

#pragma insert_asm(" CVT DSECT=YES")
#pragma insert_asm(" IHAECVT")
