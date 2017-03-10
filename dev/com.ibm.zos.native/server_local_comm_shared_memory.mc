/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2013
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */

#include <metal.h>
#include <stdio.h>
#include <stdlib.h>

#include "include/server_local_comm_shared_memory.h"

#include "include/mvs_enq.h"
#include "include/mvs_iarv64.h"

/** Macro to round something to the nearest 16 bytes (quad word). */
#define BBGZ_ROUND_TO_QUAD_WORD(size) ((((size) + 15) / 16) * 16)

void* getLocalCommSharedStorage(LocalCommSharedMemoryInfo_t* info_p, unsigned long long bytes) {
    void* storage_p = NULL;
    unsigned long long roundedBytes = BBGZ_ROUND_TO_QUAD_WORD(bytes);

    while ((info_p != NULL) && (bytes > 0)) {
        unsigned char tooBig = FALSE;
        unsigned int currentNumGuardSegments = info_p->numSegmentsGuard;
        unsigned long long firstInvalidByte = (((info_p->numSegmentsAllocated) - (currentNumGuardSegments)) * 1024 * 1024) + ((unsigned long long)(info_p->baseAddress_p));
        unsigned long long oldNextAvailableByte = (unsigned long long)info_p->nextFreeByte_p;

        for (int csRC = -1; csRC != 0;) {
            unsigned long long newNextAvailableByte = oldNextAvailableByte + roundedBytes;
            if (newNextAvailableByte <= firstInvalidByte) {
                csRC = __cds1(&oldNextAvailableByte, &(info_p->nextFreeByte_p), &newNextAvailableByte);
            } else {
                csRC = 0;
                tooBig = TRUE;
            }
        }

        if (tooBig == FALSE) {
            storage_p = (void*)oldNextAvailableByte;
            break;
        } else {
            // ---------------------------------------------------------
            // Try to expand.  Get an ENQ.  If the current number of
            // guard pages is the same as we read at the beginning of
            // the loop, try to convert one to active.
            // ---------------------------------------------------------
            unsigned char expandSuccess = FALSE;
            enqtoken enqToken;
            get_enq_exclusive_system(BBGZ_ENQ_QNAME,
                                     CLIENT_LOCAL_COMM_LOCL_EXPANSION_ENQ_RNAME,
                                     NULL, /* CURRENT TASK */
                                     &enqToken);

            if (((info_p->numSegmentsGuard) == currentNumGuardSegments) &&
                (currentNumGuardSegments > 0)) {
                int iarv64_rc = 0, iarv64_rsn = 0;
                iarv64_rc = changeSharedAccessLevel((void*)firstInvalidByte, 1L,
                                                    IARV64_SHARED_READWRITE, &iarv64_rsn);
                if (iarv64_rc == 0) {
                    info_p->numSegmentsGuard = currentNumGuardSegments - 1;
                    expandSuccess = TRUE;
                }
            }

            release_enq(&enqToken);

            if (expandSuccess == FALSE) {
                storage_p = NULL;
                break;
            }
        }
    }

    return storage_p;
}

