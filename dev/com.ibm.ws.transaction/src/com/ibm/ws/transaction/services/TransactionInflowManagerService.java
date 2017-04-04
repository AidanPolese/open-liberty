/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2011
 *
 * The source code for this program is not published or other-
 * wise divested of its trade secrets, irrespective of what has
 * been deposited with the U.S. Copyright Office.
 */
package com.ibm.ws.transaction.services;

import javax.resource.spi.XATerminator;
import javax.resource.spi.work.ExecutionContext;
import javax.resource.spi.work.WorkCompletedException;
import javax.transaction.NotSupportedException;

import org.osgi.service.component.annotations.Component;

import com.ibm.tx.jta.TransactionInflowManager;
import com.ibm.tx.jta.impl.TxExecutionContextHandler;
import com.ibm.tx.jta.impl.TxXATerminator;
import com.ibm.tx.util.TMHelper;
import com.ibm.ws.tx.jta.embeddable.EmbeddableTxExecutionContextHandler;

/**
 * Liberty version of TransactionInflowManagerImpl giving access to ExecutionContextHandler and XATerminator.
 */
@Component
public class TransactionInflowManagerService implements TransactionInflowManager {

    @Override
    public void associate(ExecutionContext ec, String inflowCoordinatorName) throws WorkCompletedException {

        // TM needs to be up and running
        try
        {
            TMHelper.checkTMState();
        } catch (NotSupportedException e)
        {
            throw new WorkCompletedException(e);
        }

        EmbeddableTxExecutionContextHandler.instance().associate(ec, inflowCoordinatorName);
    }

    @Override
    public void dissociate() {
        TxExecutionContextHandler.doDissociate();
    }

    @Override
    public XATerminator getXATerminator(String inflowCoordinatorName) {
        return TxXATerminator.instance(inflowCoordinatorName);
    }
}