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
import com.ibm.tx.jta.impl.TxRecoveryAgentImpl;
import com.ibm.tx.jta.util.TxTMHelper;
import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.recoverylog.spi.RecoveryDirector;

public class EmbeddableTMHelper extends TxTMHelper {

    private static final TraceComponent tc = Tr.register(EmbeddableTMHelper.class, TranConstants.TRACE_GROUP, TranConstants.NLS_FILE);

    @Override
    protected TxRecoveryAgentImpl createRecoveryAgent(RecoveryDirector recoveryDirector) throws Exception {
        if (tc.isEntryEnabled())
            Tr.entry(tc, "createRecoveryAgent", recoveryDirector);

        TxRecoveryAgentImpl txAgent = new EmbeddableRecoveryAgentImpl(recoveryDirector);

        if (tc.isEntryEnabled())
            Tr.exit(tc, "createRecoveryAgent", txAgent);

        return txAgent;
    }

}
