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

#ifndef _BBOZ_ANGEL_ARMV_SERVICES_H
#define _BBOZ_ANGEL_ARMV_SERVICES_H

#include "angel_client_process_data.h"
#include "angel_process_data.h"
#include "angel_server_pc_recovery.h"

#include "bbgzarmv.h"
#include "bbgzsgoo.h"

#include "gen/ihaassb.h"

/**
 * Compare the sequence number in the angel process data with the current ARMV, and attach if necessary.
 * If an attach is done, recovery information can be stored in the ARR recovery area (if
 * provided) to allow recovery to be done in the ARR.  Otherwise the code assumes there is
 * a RESMGR established which will do the cleanup when the address space terminates.
 *
 * This version of attachToARMV unconditionally obtains the attachment ENQ, and
 * is not intented to be used by external callers.
 *
 * @param sgoo_p A pointer to the SGOO for this system.
 * @param apd_p A pointer to the angel process data for this address space, or NULL if not created yet.
 * @param recovery_p A pointer to the ARR recovery area, if there is no RESMGR established.
 *
 * @return A pointer to the ARMV that we connected to.
 */
bbgzarmv* attachToARMVwithLock(bbgzsgoo* sgoo_p, angel_process_data* apd_p, angel_server_pc_recovery* recovery_p);

/**
 * Compare the sequence number in the angel client process data with the current
 * ARMV, and attach if necessary.  If an attach is done, recovery information
 * can be stored in the ARR recovery area (if provided) to allow recovery to be
 * done in the ARR.  Otherwise the code assumes there is a RESMGR established
 * which will do the cleanup when the address space terminates.
 *
 * This version of attachClientToARMV unconditionally obtains the attachment
 * ENQ, and is not intented to be used by external callers.
 *
 * @param sgoo_p A pointer to the SGOO for this system.
 * @param acpd_p A pointer to the angel client process data for this address
 *               space, or NULL if not created yet.
 * @param recovery_p A pointer to the client ARR recovery area, if there is no
 *                   RESMGR established.
 *
 * @return A pointer to the ARMV that we connected to.
 */
bbgzarmv* attachClientToARMVwithLock(bbgzsgoo* sgoo_p, AngelClientProcessData_t* acpd_p, angel_client_pc_recovery* recovery_p);

/**
 * Compare the sequence number in the angel process data with the current ARMV, and attach if necessary.
 * If an attach is done, recovery information can be stored in the ARR recovery area (if
 * provided) to allow recovery to be done in the ARR.  Otherwise the code assumes there is
 * a RESMGR established which will do the cleanup when the address space terminates.
 *
 * This version of attachToARMV is optimized to be inlined in the case where a
 * new attachment to the ARMV is not required.
 *
 * @param sgoo_p A pointer to the SGOO for this system.
 * @param apd_p A pointer to the angel process data for this address space, or NULL if not created yet.
 * @param recovery_p A pointer to the ARR recovery area, if there is no RESMGR established.
 *
 * @return A pointer to the ARMV that we connected to.
 */
#pragma inline(attachToARMV)
static bbgzarmv* attachToARMV(bbgzsgoo* sgoo_p, angel_process_data* apd_p, angel_server_pc_recovery* recovery_p) {
    bbgzarmv* armv_p = (bbgzarmv*)sgoo_p->bbgzsgoo_armv;

    // -----------------------------------------------------------------------
    // If the sequence number in the ARMV we retrieved is greater than the
    // sequence number saved in the angel process data, we may need to
    // attach ourselves to this ARMV.
    // -----------------------------------------------------------------------
    if ((apd_p == NULL) || (armv_p->bbgzarmv_instancecount > apd_p->cur_armv_seq)) {
        armv_p = attachToARMVwithLock(sgoo_p, apd_p, recovery_p);
    }

    return armv_p;
}

/**
 * Compare the sequence number in the angel client process data with the current
 * ARMV, and attach if necessary.  If an attach is done, recovery information
 * can be stored in the ARR recovery area (if provided) to allow recovery to be
 * done in the ARR.  Otherwise the code assumes there is a RESMGR established
 * which will do the cleanup when the address space terminates.
 *
 * This version of attachToARMV is optimized to be inlined in the case where a
 * new attachment to the ARMV is not required.
 *
 * @param sgoo_p A pointer to the SGOO for this system.
 * @param acpd_p A pointer to the angel client process data for this address
 *               space, or NULL if not created yet.
 * @param recovery_p A pointer to the client's ARR recovery area, if there is
 *                   no RESMGR established.
 *
 * @return A pointer to the ARMV that we connected to.
 */
#pragma inline(attachClientToARMV)
static bbgzarmv* attachClientToARMV(bbgzsgoo* sgoo_p, AngelClientProcessData_t* acpd_p, angel_client_pc_recovery* recovery_p) {
    bbgzarmv* armv_p = (bbgzarmv*)sgoo_p->bbgzsgoo_armv;

    // -----------------------------------------------------------------------
    // If the sequence number in the ARMV we retrieved is greater than the
    // sequence number saved in the angel process data, we may need to
    // attach ourselves to this ARMV.
    // -----------------------------------------------------------------------
    if ((acpd_p == NULL) || (armv_p->bbgzarmv_instancecount > acpd_p->curArmvSeq)) {
        armv_p = attachClientToARMVwithLock(sgoo_p, acpd_p, recovery_p);
    }

    return armv_p;
}

/**
 * Detaches from an ARMV.  Performs cleanup on the ARMV if we're the last one out.
 *
 * @param armv_p A pointer to the ARMV that we are detaching from.
 * @param recovery_p A pointer to the ARR recovery area, if there is no RESMGR established.
 */
void detachFromARMV(bbgzarmv* armv_p, angel_server_pc_recovery* recovery_p);

/**
 * Detaches from an ARMV in a client process.  Performs cleanup on the ARMV if
 * we're the last one out.
 *
 * @param armv_p A pointer to the ARMV that we are detaching from.
 * @param recovery_p A pointer to the client's ARR recovery area, if there is
 *                   no RESMGR established.
 */
void detachClientFromARMV(bbgzarmv* armv_p, angel_client_pc_recovery* recovery_p);

/**
 * Disconnect from all ARMVs starting at the watermark and working down.
 *
 * @param watermark The watermark where to start.  All ARMVs with sequence less than or
 *                  equal to the watermark will be detached.
 * @param assb_p The ASSB for the address space where the attachments are.
 * @param client Boolean parameter indicating this is a client process (true).
 */
void detachAllARMVs(unsigned char watermark, assb* assb_p, unsigned char client);

#endif

