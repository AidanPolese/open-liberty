/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2002, 2012
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */

/**
 *  <code>UOWHandleImpl</code> instances are used by <code>UOWControl</code> implementations
 *  suspend and resume methods to convey context management information accross a suspend
 *  and resume call.
 */

package com.ibm.ejs.csi;

import javax.transaction.Transaction;

import com.ibm.ws.LocalTransaction.LocalTransactionCoordinator;

public class UOWHandleImpl implements UOWHandle {

    /**
     * Suspended global transaction, if any.
     */
    protected final Transaction suspendedGlobalTx;

    /**
     * Suspended local transaction if any.
     */
    protected final LocalTransactionCoordinator suspendedLocalTx;

    /**
     * Suspended ActivitySession, if any.
     */
//    protected final ActivitySession suspendedActivitySession;

    /**
     * Create new UOWHandleImpl instance to hold a suspended global tx.
     */
    UOWHandleImpl(Transaction suspendedGlobalTx) {
//        this.suspendedActivitySession = null;
        this.suspendedLocalTx = null;
        this.suspendedGlobalTx = suspendedGlobalTx;
    } //ctor

    /**
     * Create new UOWHandleImpl instance to hold a suspended local tx.
     */
    UOWHandleImpl(LocalTransactionCoordinator ltc) {
//        this.suspendedActivitySession = null;
        this.suspendedLocalTx = ltc;
        this.suspendedGlobalTx = null;
    } //ctor

    /**
     * Create new UOWHandleImpl instance to hold a suspended ActivitySession.
     */
//    UOWHandleImpl(ActivitySession as) {
//        this.suspendedActivitySession = as;
//        this.suspendedLocalTx = null;
//        this.suspendedGlobalTx = null;
//    } //ctor
}// UOWHandleImpl
