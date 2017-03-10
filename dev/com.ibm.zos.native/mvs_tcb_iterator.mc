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

#include <metal.h>
#include <builtins.h>
#include <stdlib.h>
#include <string.h>

#include "include/mvs_tcb_iterator.h"

#include "include/common_defines.h"
#include "include/mvs_estae.h"
#include "include/mvs_setlock.h"

#include "include/gen/ihapsa.h"
#include "include/gen/ihaascb.h"
#include "include/gen/ihaasxb.h"
#include "include/gen/ikjtcb.h"

// TCB iterator
int callFunctionForEachTCB(tcbScanFunction_t* fcn_p, void* parm_p, unsigned char suppressRegisterDump) {
    int rc = -1;

    if (fcn_p == NULL) {
        return -1;
    }

    // -----------------------------------------------------------------------
    // Get some storage that we pass to get local lock.
    // -----------------------------------------------------------------------
    void* localLockSaveArea_p = __malloc31(LOCAL_LOCK_SAVE_AREA_SIZE);
    if (localLockSaveArea_p == NULL) {
        return -1;
    }

    // -------------------------------------------------------------------
    // Set up an ESTAE in case things go badly.  Set up the retry point
    // before setting up the ESTAE since the ESTAE could get control at
    // any point after it is set.
    // -------------------------------------------------------------------
    volatile struct {
        int establishedEstae : 1,
            gotLocalLock : 1,
            droppedLocalLock : 1,
            startedTcbScan : 1,
            notifiedAbort : 1,
            _available : 27;
    } retryBits;

    int estaeRC = -1, estaeRSN = -1;
    retry_parms retryArea;
    memset(&retryArea, 0, sizeof(retryArea));
    retryArea.setrp_opts.nodump = (suppressRegisterDump == TRUE);
    memset((void*)(&retryBits), 0, sizeof(retryBits));

    SET_RETRY_POINT(retryArea);
    if (retryBits.establishedEstae == 0) {
        retryBits.establishedEstae = 1;
        establish_estaex_with_retry(&retryArea, &estaeRC, &estaeRSN);
    } else {
        // -------------------------------------------------------------------
        // The ESTAE got control already, something is very wrong so just
        // bail out.
        // -------------------------------------------------------------------
        remove_estaex(&estaeRC, &estaeRSN);
        free(localLockSaveArea_p);
        return -1;
    }

    // -----------------------------------------------------------------------
    // Bail out if we were unable to set up the ESTAE.
    // -----------------------------------------------------------------------
    if (estaeRC != 0) {
        free(localLockSaveArea_p);
        return -1;
    }

    // -----------------------------------------------------------------------
    // Get the local lock.
    // -----------------------------------------------------------------------
    volatile int localLockRC = -1;
    SET_RETRY_POINT(retryArea);
    if (retryBits.gotLocalLock == 0) {
        retryBits.gotLocalLock = 1;
        localLockRC = getLocalLock(localLockSaveArea_p);
    }

    // -----------------------------------------------------------------------
    // Continue if we got the lock (RC = 0), or we already had it (RC = 4).
    // -----------------------------------------------------------------------
    if ((localLockRC == 0) || (localLockRC == 4)) {
        // -------------------------------------------------------------------
        // Start the TCB scan.  If the ESTAE gets control, abort the scan.
        // -------------------------------------------------------------------
        SET_RETRY_POINT(retryArea);
        unsigned long long token;
        __stck(&(token));
        ScanTCBFlags_t flags;
        if (retryBits.startedTcbScan == 0) {
            retryBits.startedTcbScan = 1;
            asxb* asxb_p = ((ascb*)(((psa*)0)->psaaold))->ascbasxb;
            tcb* curTcb_p = asxb_p->asxbftcb;
            int fcnRC = 0;
            memset(&flags, 0, sizeof(flags));
            flags.startScan = 1;
            while ((curTcb_p != NULL) && (fcnRC == 0)) {
                fcnRC = fcn_p(&token, &flags, curTcb_p, parm_p);
                flags.startScan = 0;
                curTcb_p = curTcb_p->tcbtcb;
            }

            if (fcnRC == 0) {
                memset(&flags, 0, sizeof(flags));
                flags.endScan = 1;
                fcnRC = fcn_p(&token, &flags, NULL, parm_p);
            }

            rc = fcnRC;
        } else {
            SET_RETRY_POINT(retryArea);
            if (retryBits.notifiedAbort == 0) {
                retryBits.notifiedAbort = 1;
                memset(&flags, 0, sizeof(flags));
                flags.abort = 1;
                fcn_p(&token, &flags, NULL, parm_p);
            }
        }

        // -------------------------------------------------------------------
        // Drop the local lock.
        // -------------------------------------------------------------------
        SET_RETRY_POINT(retryArea);
        if ((localLockRC ==0) && (retryBits.droppedLocalLock == 0)) {
            retryBits.droppedLocalLock = 1;
            releaseLocalLock(localLockSaveArea_p);
        }
    }

    // -----------------------------------------------------------------------
    // Remove the ESTAE.
    // -----------------------------------------------------------------------
    remove_estaex(&estaeRC, &estaeRSN);

    // -----------------------------------------------------------------------
    // Release storage.
    // -----------------------------------------------------------------------
    free(localLockSaveArea_p);

    return rc;
}
