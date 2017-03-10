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
#include "include/server_ipt_stubs.h"

#include "include/ras_tracing.h"
#include "include/server_native_service_tracker.h"

#include <errno.h>
#include <string.h>

#define RAS_MODULE_CONST RAS_MODULE_SERVER_IPT_STUBS

#pragma linkage(BPX4IPT, OS64_NOSTACK)
void BPX4IPT(void* func, void* parms, int* rv_p, int* rc_p, int* rsn_p);

int driveAuthorizedServiceOnIPT(DriveAuthorizedServiceOnIPTParms_t* parms_p, int* bpxReturnValue_p, int* bpxReturnCode_p, int* bpxReasonCode_p) {
    // Get the unauthorized server stub to be able to call the BPX4IPT service.
    const server_unauthorized_function_stubs* unauth_stubs_p = getServerUnauthorizedFunctionStubs();
    if (unauth_stubs_p == NULL) {
        return -1;
    }

    void* parm_p2 = &parms_p;
    void* routine_p = (void*)&(unauth_stubs_p->driveAuthorizedServiceOnIPTExitRoutine);

    // -----------------------------------------------------------------------
    // BPX4IPT only lets one task invoke it at a time.  Therefore it seems we
    // need a mutex or something here to prevent us from invoking it from two
    // tasks at the same time.  Unfortunately, we don't own all the tasks in
    // the address space and so another task (ie a JVM task, or some user task)
    // might invoke the service, and our own serialization won't do us any
    // good.  Since we have to support that scenario anyway, and since BPX4IPT
    // will tell us when that happens, we're not going to bother with our own
    // lock and we'll just try again if BPX4IPT says that it's in-use.
    // -----------------------------------------------------------------------
    for (*bpxReturnValue_p = -1, *bpxReturnCode_p = EAGAIN; (*bpxReturnValue_p != 0) && (*bpxReturnCode_p == EAGAIN);) {
        BPX4IPT(routine_p, &parm_p2, bpxReturnValue_p, bpxReturnCode_p, bpxReasonCode_p);
        if ((*bpxReturnValue_p != 0) && (*bpxReturnCode_p == EAGAIN)) {
            sleep(1);
        }
    }

    return 0;
}
