/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2015
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.tx.jta.embeddable.impl;

import com.ibm.tx.TranConstants;
import com.ibm.tx.jta.impl.TransactionImpl;
import com.ibm.tx.jta.impl.TransactionState;
import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.recoverylog.spi.RecoverableUnitSection;

/**
 *
 */
public class EmbeddableTransactionState extends TransactionState {
    private static final TraceComponent tc = Tr.register(EmbeddableTransactionState.class, TranConstants.TRACE_GROUP, TranConstants.NLS_FILE);

    /**
     * @param tran
     */
    public EmbeddableTransactionState(EmbeddableTransactionImpl tran) {
        super(tran);
    }

    @Override
    protected void logSupOrRecCoord() throws Exception
    {
        final boolean traceOn = TraceComponent.isAnyTracingEnabled();

        if (traceOn && tc.isEntryEnabled())
            Tr.entry(tc, "logSupOrRecCoord", this);

        final WSATRecoveryCoordinator wsatRC = ((EmbeddableTransactionImpl) _tran).getWSATRecoveryCoordinator();

        if (wsatRC != null)
        {
            final RecoverableUnitSection recoveryCoordSection = _tranLog.createSection(TransactionImpl.RECCOORD_WSAT_SECTION, true);
            recoveryCoordSection.addData(wsatRC.toLogData());

            if (traceOn && tc.isEventEnabled())
                Tr.event(tc, "WSATRecoveryCoordinator logged ", wsatRC);
        }
        else
        {
            // Throw exception to convert a prepare vote to rollback
            final NullPointerException npe = new NullPointerException("Null recovery coordinator");
            if (traceOn && tc.isEntryEnabled())
                Tr.exit(tc, "logSupOrRecCoord", npe);
            throw npe;
        }

        if (traceOn && tc.isEntryEnabled())
            Tr.exit(tc, "logSupOrRecCoord");
    }
}