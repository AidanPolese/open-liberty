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

#ifndef SERVER_KERNEL_COMMON_H_
#define SERVER_KERNEL_COMMON_H_


/**
 * Parameter structure used by the @c hardFailureCleanupActivate routine.
 */
typedef struct {
    int* returnCode;             //!< Output - return code
} KERNEL_HardFailureCleanupActivateParms;

/**
 * Set a marker on the caller thread that will indicate to the task-level resmgr that it should
 * drive any "registered" hard failure cleanup routines found in the server_process_data.
 *
 * An implementor of a hard failure cleanup routine is within the AsyncIO code. Its routine
 * performs cleanup/wake up of AIO related stuff. It specifically, makes sure to release any
 * PAUSED ResultHandler threads.  This is needed if the server is being terminated hard
 * (ie. not with a "server stop" or modify stop).  For instance, if the server had a "kill -9"
 * issued against it.  Without this support the ResultHandler threads would continue to wait
 * for Completed IO or may timeout and return to Java only to came back down and wait because
 * the java routine has been JIT'd.
 *
 * @param parms A @c KERNEL_HardFailureCleanupActivateParms structure containing return code.
 *
 */
void hardFailureCleanupActivate(KERNEL_HardFailureCleanupActivateParms* parms);

/**
 * Parameter structure used by the @c hardFailureCleanupDeactivate routine.
 */
typedef struct {
    int* returnCode;             //!< Output - return code
} KERNEL_HardFailureCleanupDeactivateParms;

/**
 * unSet the marker on the calling thread that indicated to the task-level resmgr that it should
 * drive any "registered" hard failure cleanup routines found in the server_process_data.
 *
 * See hardFailureCleanupActivate for more information.
 *
 * @param parms A @c KERNEL_HardFailureCleanupDeactivateParms structure containing return code.
 *
 */
void hardFailureCleanupDeactivate(KERNEL_HardFailureCleanupDeactivateParms* parms);

/**
 * This routine is called from our task-level resmgr for a hard failure of the server.
 *
 * It determines a hard failure by seeing a specially marked Java thread in task termination.
 * This specially marked Java thread is started at server startup and "marked" (marked by
 * driving hardFailureCleanupActivate method)..  It is "unmarked" during server shutdown
 * before the Java thread is terminated.
 *
 * If our task-level resmgr sees this marked thread then it means that the thread has entered
 * terminated without the help of a normal server shutdown...this indicates a hard failure.
 * It will then drive any "Registered" Cleanup routines.
 *
 * A "registered" cleanup routine cleans up or releases resources to allow the server to
 * terminate.
 *
 * An example is the cleanup routine for AsyncIO.  The AIO routine will check for and release
 * any ResultHandler thread that is currently in a MVS PAUSED state which will prevent LE
 * from terminating the server (hung server).
 *
 */
void kernel_cleanupForHardFailure(void);



/**
 * server_process_data.hardfailureCleanupRegistry slot assignments.
 */

// AIO Hardfailure Cleanup Routine registry slot
#define HARDFAILURE_REGISTRY_SLOT_AIO 0

// Local Comm HardFailure Cleanup Routine registry slot
#define HARDFAILURE_REGISTRY_SLOT_LCOM 1


/**
 * "Registered" Cleanup routine driven from kernel_cleanupForHardFailure().
 *
 * A "registered" cleanup routine cleans up or releases resources to allow the server to
 * terminate.
 */
void hardFailureRegisteredCleanupRtn(void);

#endif /* SERVER_KERNEL_COMMON_H_ */
