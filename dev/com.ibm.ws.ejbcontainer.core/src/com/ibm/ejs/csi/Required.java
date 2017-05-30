/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2000, 2014
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ejs.csi;

import com.ibm.ejs.container.EJBMethodInfoImpl;
import com.ibm.websphere.csi.CSIException;
import com.ibm.websphere.csi.EJBKey;
import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.LocalTransaction.LocalTransactionCoordinator;
import com.ibm.ws.ffdc.FFDCFilter;

/**
 * <code> Required </code> implements TX_REQUIRED semantics.
 * A global transaction will be started if one does not already
 * exist on the current thread.
 **/

final class Required extends TranStrategy {
    //d121558
    private static final TraceComponent tc = Tr.register(Required.class, "EJBContainer", "com.ibm.ejs.container.container");

    Required(TransactionControlImpl txCtrl) {
        super(txCtrl);
    }

    @Override
    TxCookieImpl preInvoke(EJBKey key, EJBMethodInfoImpl methodInfo)
                    throws CSIException
    {
        final boolean entryEnabled =
                        TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled();
        if (entryEnabled) {
            Tr.entry(tc, "preInvoke");
        }

        // Suspend a local tran, if it exists (null is returned if not).
        // This should only be required if a global tran is not present,
        // but this code tolerates scenarios where applications use
        // internals to begin/resume global trans on threads that already
        // have a local transaction.                                   PI10351
        LocalTransactionCoordinator savedLocalTx = suspendLocalTx();

        boolean begun = false;
        try {
            if (!globalTxExists(true)) {
                beginGlobalTx(key, methodInfo);
                begun = true;
            }
        } catch (CSIException ex) {
            if (TraceComponent.isAnyTracingEnabled() && tc.isEventEnabled()) {
                Tr.event(tc, "Begin of global tx failed", ex);
            }

            // LIDB2446: have to restore any suspended local tx here
            if (savedLocalTx != null)
            {
                try
                {
                    resumeLocalTx(savedLocalTx);
                } catch (Throwable ex2) {
                    FFDCFilter.processException(ex2, "com.ibm.ejs.csi.Required.preInvoke", "95", this);
                    if (TraceComponent.isAnyTracingEnabled() && tc.isEventEnabled()) {
                        Tr.event(tc, "Saved local tx resume failed", ex2);
                    }
                }
            }

            throw ex;
        }

        if (entryEnabled) {
            Tr.exit(tc, "preInvoke");
        }

        TxCookieImpl cookie = new TxCookieImpl(begun, false, this, null);
        // LIDB2446 add suspended ltc to cookie
        cookie.suspendedLocalTx = savedLocalTx;
        return cookie;
    }

} // Required

