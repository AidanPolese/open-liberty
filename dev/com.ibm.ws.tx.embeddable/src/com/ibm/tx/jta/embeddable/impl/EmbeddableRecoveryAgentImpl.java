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
import com.ibm.tx.jta.impl.FailureScopeController;
import com.ibm.tx.jta.impl.TxRecoveryAgentImpl;
import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.recoverylog.spi.FailureScope;
import com.ibm.ws.recoverylog.spi.RecoveryDirector;

/**
 *
 */
public class EmbeddableRecoveryAgentImpl extends TxRecoveryAgentImpl {

    private static final TraceComponent tc = Tr.register(EmbeddableRecoveryAgentImpl.class, TranConstants.TRACE_GROUP, TranConstants.NLS_FILE);

    /**
     * @param recoveryDirector
     * @throws Exception
     */
    public EmbeddableRecoveryAgentImpl(RecoveryDirector recoveryDirector) throws Exception {
        super(recoveryDirector);
    }

    @Override
    protected FailureScopeController createFailureScopeController(FailureScope currentFailureScope) throws Exception {
        if (tc.isEntryEnabled())
            Tr.entry(tc, "createFailureScopeController", currentFailureScope);

        FailureScopeController fsc = new EmbeddableFailureScopeController(currentFailureScope);

        if (tc.isEntryEnabled())
            Tr.exit(tc, "createFailureScopeController", fsc);

        return fsc;
    }
}
