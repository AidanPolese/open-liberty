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

import javax.transaction.SystemException;

import com.ibm.tx.TranConstants;
import com.ibm.tx.jta.impl.FailureScopeController;
import com.ibm.tx.jta.impl.RecoveryManager;
import com.ibm.tx.jta.impl.TxExecutionContextHandler;
import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.recoverylog.spi.InternalLogException;
import com.ibm.ws.recoverylog.spi.LogCursor;
import com.ibm.ws.recoverylog.spi.NotSupportedException;
import com.ibm.ws.recoverylog.spi.RecoverableUnit;
import com.ibm.ws.recoverylog.spi.RecoveryAgent;
import com.ibm.ws.recoverylog.spi.RecoveryLog;

/**
 *
 */
public class EmbeddableRecoveryManager extends RecoveryManager {

    private static final TraceComponent tc = Tr.register(EmbeddableRecoveryManager.class, TranConstants.TRACE_GROUP, TranConstants.NLS_FILE);

    /**
     * @param fsc
     * @param agent
     * @param tranLog
     * @param xaLog
     * @param recoverXaLog
     * @param defaultApplId
     * @param defaultEpoch
     */
    public EmbeddableRecoveryManager(FailureScopeController fsc, RecoveryAgent agent, RecoveryLog tranLog, RecoveryLog xaLog, RecoveryLog recoverXaLog, byte[] defaultApplId,
                                     int defaultEpoch) {
        super(fsc, agent, tranLog, xaLog, recoverXaLog, defaultApplId, defaultEpoch);
    }

    @Override
    protected boolean handleTranRecord(RecoverableUnit ru, boolean recoveredTransactions, LogCursor recoverableUnits) throws SystemException, NotSupportedException, InternalLogException {
        if (tc.isEntryEnabled())
            Tr.entry(tc, "handleTranRecord", new Object[] { ru, recoveredTransactions, recoverableUnits });

        final EmbeddableTransactionImpl tx = new EmbeddableTransactionImpl((EmbeddableFailureScopeController) _failureScopeController);
        if (tx.reconstruct(ru, _tranLog)) {
            // If this txn was imported from an RA we need to re insert it in
            // TxExecutionHandler.txnTable
            if (tx.isRAImport()) {
                TxExecutionContextHandler.addTxn(tx);
            }

            recoveredTransactions = true;
        } else {
            // Discard any recoverable unit that is not reconstructed
            recoverableUnits.remove();
        }

        if (tc.isEntryEnabled())
            Tr.exit(tc, "handleTranRecord", recoveredTransactions);
        return recoveredTransactions;
    }

}
