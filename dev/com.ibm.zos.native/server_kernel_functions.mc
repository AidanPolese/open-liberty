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

#include <ctype.h>
#include <ieac.h>
#include <limits.h>
#include <metal.h>
#include <string.h>
#include <stdio.h>
#include <stdlib.h>

#include "include/common_defines.h"
#include "include/mvs_estae.h"
#include "include/mvs_utils.h"
#include "include/mvs_wto.h"
#include "include/petvet.h"
#include "include/ras_tracing.h"
#include "include/server_kernel_common.h"
#include "include/server_process_data.h"


#define RAS_MODULE_CONST  RAS_MODULE_SERVER_KERNEL_FUNCTIONS

// ----------------------------------------------------------------------------
// Kernel service calls that require key 0-7 or supervisor state.
// ----------------------------------------------------------------------------


#define _TP_SERVER_KERNEL_FUNCTIONS_CLEANUPACTIVATE_NO_STD            1
#define _TP_SERVER_KERNEL_FUNCTIONS_CLEANUPDEACTIVATE_NO_STD          2
#define _TP_SERVER_KERNEL_FUNCTIONS_CLEANUPACTIVATE_MARKED            3
#define _TP_SERVER_KERNEL_FUNCTIONS_CLEANUPDEACTIVATE_UNMARKED        4

// We use this if/when our task-level RESMGR, serverAuthorizedTaskCleanup, drives our
// kernel_cleanupForHardFailure routine.  This is done during server startup code
// when a Java thread is started and marked it (std_p->taskFlags.cleanupForHardFailure).
// This call disables trace on the calling thread to avoid hangs in tracing code
// during a hard failure.
extern int disableTraceLevelInServerTaskData(void);

// This is a unique recovery routine for releasing threads that are PAUSED in the trace
// code.  It doesn't register and deregister like the other cleanup routines.  It is
// always driven for hard failure server cleanup.
extern void trace_hardFailureRegisteredCleanupRtn(void);

// Local routine to serialize on the update of a word.
static int csSetFlagInInt(int* intTarget_Ptr, int updateMask)
{
  int oldInt;
  int newInt;

  oldInt = *intTarget_Ptr;
  do {
      newInt = oldInt | updateMask;
  } while(cs((cs_t *) &oldInt,
             (cs_t *) intTarget_Ptr,
             (cs_t)   newInt
             ));

  return newInt;

} // end, csSetFlagInInt


void hardFailureCleanupActivate(KERNEL_HardFailureCleanupActivateParms* activateParms) {

    int localRC = 0;
    server_task_data* std_p = getServerTaskData();

    if (std_p != NULL) {
        // Activate resmgr driven cleanup for hard failures triggered by the termination
        // of this thread.
        std_p->taskFlags |= taskFlags_cleanupForHardFailure;

        if (TraceActive(trc_level_detailed)) {
            TraceRecord(trc_level_detailed,
                        TP(_TP_SERVER_KERNEL_FUNCTIONS_CLEANUPACTIVATE_MARKED),
                        "hardFailureCleanupActivate: thread Marked as cleanup thread",
                        TRACE_DATA_RAWDATA(sizeof(*std_p), std_p, "server_task_data"),
                        TRACE_DATA_END_PARMS);
        }
    } else {
        // error
        if (TraceActive(trc_level_exception)) {
            TraceRecord(trc_level_exception,
                        TP(_TP_SERVER_KERNEL_FUNCTIONS_CLEANUPACTIVATE_NO_STD),
                        "hardFailureCleanupActivate: no server_task_data on thread",
                        TRACE_DATA_END_PARMS);
        }

        localRC = 8;
    }

    // Pass back the return code
    memcpy_dk(activateParms->returnCode, &localRC, sizeof(int), 8);
}

void hardFailureCleanupDeactivate(KERNEL_HardFailureCleanupDeactivateParms* deactivateParms) {

    int localRC = 0;
    server_task_data* std_p = getServerTaskData();

    if (std_p != NULL) {
        // Deactivate resmgr driven cleanup for hard failures triggered by the termination
        // of this thread.
        std_p->taskFlags &= ~taskFlags_cleanupForHardFailure;

        if (TraceActive(trc_level_detailed)) {
            TraceRecord(trc_level_detailed,
                        TP(_TP_SERVER_KERNEL_FUNCTIONS_CLEANUPDEACTIVATE_UNMARKED),
                        "hardFailureCleanupDeactivate: thread unMarked as a cleanup thread",
                        TRACE_DATA_RAWDATA(sizeof(*std_p), std_p, "server_task_data"),
                        TRACE_DATA_END_PARMS);
        }
    } else {
        // error
        if (TraceActive(trc_level_detailed)) {
            TraceRecord(trc_level_detailed,
                        TP(_TP_SERVER_KERNEL_FUNCTIONS_CLEANUPDEACTIVATE_NO_STD),
                        "hardFailureCleanupDeactivate: no server_task_data on thread",
                        TRACE_DATA_END_PARMS);
        }

        localRC = 8;
    }

    // Pass back the return code
    memcpy_dk(deactivateParms->returnCode, &localRC, sizeof(int), 8);
}

/**
 * Cleanup routine driven from resmgr for a Task termination of a specially
 * marked thread.  This thread was started from Java during server startup to
 * cover a hard failure of the server (ex. a "kill -9") which bypasses normal
 * server shutdown.
 *
 * This routine will check for and drive any "registered" cleanup routine in
 * the hardfailureCleanupRegistry anchored in the server_process_data.
 *
 * At the time of this initial support there were two potential "registered"
 * routines.  One for AIO and the other for Local Comm.  Their initial purpose
 * was to allow the server to terminated if hit with a hard failure.  Without
 * this support they could have remained in an MVS PAUSE while LE terminated
 * the rest of the threads and crippled the JVM.  For more information on
 * these routines looked for routines plugged into the server_process_data
 * mentioned above.
 *
 */
void kernel_cleanupForHardFailure(void) {

    server_task_data* std_p = getServerTaskData();
    server_process_data* spd_p = (std_p != NULL) ? std_p->spd_p : NULL;

    if (spd_p != NULL) {
        // -----------------------------------------------------------------------
        // Establish some recovery in case of an abend.
        // -----------------------------------------------------------------------
        int estaex_rc  = -1;
        int estaex_rsn = -1;
        volatile struct {
                     int tryToSetESTAE :                   1,
                         setESTAE :                        1,
                         abendedAndRetried:                1,
                         failedSettingUpESTAE:             1,

                         tryToDisableTrace:                1,
                         failedToDisableTrace:             1,
                         tryToMarkServerHardFailure:       1,
                         failedToMarkServerHardFailure:    1,

                         tryToCallCurrentCleanupRtn:       1,
                         successfullyCalledACleanupRtn:    1,
                         abendedCallingCleanupRtn:         1,
                         loopedThroughAllCleanupSlots:     1,

                         tryToReleaseAuthTraceWaiters:     1,
                         failedToReleaseAuthTraceWaiters:  1,

                         _available : 18;
        } retryFootprints;

        memset((void*)&retryFootprints, 0, sizeof(retryFootprints));
        struct retry_parms retryParms;
        memset(&retryParms, 0, sizeof(retryParms));

        SET_RETRY_POINT(retryParms);
        if (retryFootprints.tryToSetESTAE == 0) {
            retryFootprints.tryToSetESTAE = 1;
            establish_estaex_with_retry(&retryParms, &estaex_rc, &estaex_rsn);
            retryFootprints.setESTAE = (estaex_rc == 0);
        }

        if (retryFootprints.setESTAE == 1) {
            // -----------------------------------------------------------------------
            // Disable Trace on this thread
            // -----------------------------------------------------------------------
            SET_RETRY_POINT(retryParms);
            if (retryFootprints.tryToDisableTrace == 0) {
                retryFootprints.tryToDisableTrace = 1;

                // Disable native trace on this thread...its hopefully going to end when it gets back to an LE
                // Environment
                disableTraceLevelInServerTaskData();

            } else {
                retryFootprints.abendedAndRetried    = 1;
                retryFootprints.failedToDisableTrace = 1;
            }

            // -----------------------------------------------------------------------
            // Mark the server for a hard failure detected.
            // -----------------------------------------------------------------------
            SET_RETRY_POINT(retryParms);
            if (retryFootprints.tryToMarkServerHardFailure == 0) {
                retryFootprints.tryToMarkServerHardFailure = 1;

                // Set flag serverHardFailureDetected
                csSetFlagInInt((int*) &(spd_p->serializedFlags), SPD_SERIALIZEDFLAGS_serverHardFailureDetected);

            } else {
                retryFootprints.abendedAndRetried             = 1;
                retryFootprints.failedToMarkServerHardFailure = 1;
            }

            // -----------------------------------------------------------------------
            // For each "Registered" cleanup routine
            // -----------------------------------------------------------------------
            int registerIndex = 0;

            SET_RETRY_POINT(retryParms);
            do {
                // Check and call
                if (retryFootprints.tryToCallCurrentCleanupRtn == 0) {
                    retryFootprints.tryToCallCurrentCleanupRtn = 1;

                    if (spd_p->hardFailureRegisteredCleanupRtn[registerIndex] != NULL) {
                        // Call it.
                        //    void hardFailureRegisteredCleanupRtn(void)
                        (*spd_p->hardFailureRegisteredCleanupRtn[registerIndex])();

                        retryFootprints.successfullyCalledACleanupRtn = 1;
                        retryFootprints.tryToCallCurrentCleanupRtn = 0;
                    }
                } else {
                    retryFootprints.abendedAndRetried          = 1;
                    retryFootprints.abendedCallingCleanupRtn   = 1;
                    retryFootprints.tryToCallCurrentCleanupRtn = 0;
                }

                // Bump to next
                registerIndex++;

            } while (registerIndex < SERVER_PROCESS_DATA_MAX_REGISTERED_CLEANUPROUTINES);

            retryFootprints.loopedThroughAllCleanupSlots = 1;

            // -----------------------------------------------------------------------
            // Release any waiting threads because of tracing from authorized code
            // (they build a trace element and push to a stack and PAUSE waiting for
            // it to get processed by an unauthorized trace thread--which is most
            // likely terminated by LE at this point).
            // -----------------------------------------------------------------------
            SET_RETRY_POINT(retryParms);
            if (retryFootprints.tryToReleaseAuthTraceWaiters == 0) {
                retryFootprints.tryToReleaseAuthTraceWaiters = 1;

                trace_hardFailureRegisteredCleanupRtn();
            } else {
                retryFootprints.abendedAndRetried               = 1;
                retryFootprints.failedToReleaseAuthTraceWaiters = 1;
            }

            // -----------------------------------------------------------------------
            // Remove the ESTAE.
            // -----------------------------------------------------------------------
            remove_estaex(&estaex_rc, &estaex_rsn);
        } else {
            // Couldn't establish an ESTAE
            retryFootprints.failedSettingUpESTAE = 1;
        }
    }

    return;
}

