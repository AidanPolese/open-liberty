/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2016
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
package com.ibm.ejs.j2c;

import javax.transaction.xa.Xid;

import com.ibm.tx.jta.AbortableXAResource;
import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;

/**
 * AbortableXATransactionWrapper is XATransactionWrapper that only implements AbortableXAResource
 */
public class AbortableXATransactionWrapper extends XATransactionWrapper implements AbortableXAResource {

    private final MCWrapper mcWrapper;
    private static final TraceComponent tc =
                    Tr.register(AbortableXATransactionWrapper.class,
                                J2CConstants.traceSpec,
                                J2CConstants.messageFile);

    /**
     * @param mcWrapper
     */
    protected AbortableXATransactionWrapper(MCWrapper mcWrapper) {
        super(mcWrapper);
        final boolean isTracingEnabled = TraceComponent.isAnyTracingEnabled();
        if (isTracingEnabled && tc.isEntryEnabled()) {
            Tr.entry(this, tc, "<init>");
        }
        this.mcWrapper = mcWrapper;
        if (isTracingEnabled && tc.isEntryEnabled()) {
            Tr.exit(this, tc, "<init>");
        }
    }

    @Override
    public void abort(Xid xid) {
        if (tc.isDebugEnabled())
            Tr.debug(this, tc, "abort", xid);
        mcWrapper.abortMC();
    }
}
