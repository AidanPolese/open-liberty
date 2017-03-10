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
#ifndef _BBOZ_MVS_TCB_ITERATOR_H
#define _BBOZ_MVS_TCB_ITERATOR_H

/**
 * @file
 * This part contains routines that can be used by authorized code to run the
 * TCB chain (all tasks in the address space) and perform some operation for
 * each.  The MVS local lock is held while the scan is performed so that the
 * caller can be assured no tasks will come or go during the scan.  The caller
 * provides a function which is invoked for each TCB, which performs the
 * desired operation.
 */

/** Flags provided on the TCB operation. */
typedef struct scanTCBFlags {
    int startScan : 1, //!< Starts the scan.  This is set on the first callback.
        endScan : 1, //!< Ends the scan.  This is set on the last callback.
        abort : 1, //!< Ends the scan abnormally.  The scan did not complete.
        _available : 29; //!< Reserved for future flags.
} ScanTCBFlags_t;

/**
 * Prototype of the callback function used during the TCB scan.  This function
 * is called once for each TCB in the scan.  The MVS local lock is held for the
 * duration of this method.
 *
 * @param token_p A pointer to a double word token uniquely describing this
 *                scan.
 * @param flags_p A pointer to the flags struct, indicating the state of the scan.
 * @param tcb_p A pointer to the TCB being scanned on this iteration.  If the
 *              tcb_p pointer is NULL, there is no TCB to process on this
 *              iteration.  In this case, it is likely that flags_p has changed
 *              and should be checked for a new state.
 * @param parm_p A pointer to the parm area, provided when the scan was requested.
 *
 * @return The function should return 0 if the scan should continue with the next
 *         TCB, or non-zero if the scan should abort.  If an abort is requested,
 *         the function will not be called again with the abort flag set.
 */
typedef int tcbScanFunction_t(unsigned long long* token_p, ScanTCBFlags_t* flags_p, void* tcb_p, void* parm_p);

/**
 * Function which requests a TCB scan.  The provided function will be called
 * once for each TCB in the TCB chain.  The MVS local lock is held for the
 * duration of the scan.
 *
 * @param fcn_p A pointer to the function which should be called for each TCB in
 *              the TCB chain.  The MVS local lock is held when this function
 *              is called.  If the function must drop the local lock, it must
 *              re-acquire the local lock before it returns, and must return
 *              a failing return code so that the scan can be aborted.
 * @param parm_p A pointer to an area which is passed to fcn_p.  This could be
 *               used by fcn_p as a work area while processing TCBs.
 * @param suppressRegisterDump Set to 1 (TRUE) if the ESTAE should suppress
 *                             dumping the registers to the glass if an abend
 *                             occurs while processing.
 *
 * @return Returns 0 if the scan completed successfully, non-zero if the scan
 *         did not complete successfully, either because the fcn_p requested
 *         that the scan abort, or due to another error.
 */
int callFunctionForEachTCB(tcbScanFunction_t* fcn_p, void* parm_p, unsigned char suppressRegisterDump);

#endif
