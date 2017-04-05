package com.ibm.ws.sib.msgstore.transactions.impl;
/*
 * 
 * 
 * ============================================================================
 * IBM Confidential OCO Source Material
 *
 * Copyright IBM Corp. 2012
 * 
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 * ============================================================================
 * 
 *
 * Change activity:
 *
 * Reason        Date     Origin     Description
 * ------------- -------- ---------- ------------------------------------------
 * 188494        27/01/04  gareth    Tie transactions to individual ME
 * 199334.1      27/05/04  gareth    Add transaction size counter
 * 216527        15/07/04  gareth    Handle SeverePersistenceException
 * SIB0002.ms.1  28/07/05  schofiel  Changes for remote MQ subordinate resources
 * SIB0003.ms.13 26/08/05  schofiel  1PC optimisation only works for data store not file store           
 * 306998.20     09/01/06  gareth    Add new guard condition to trace statements
 * ============================================================================
 */

import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.sib.msgstore.MessageStoreConstants;
import com.ibm.ws.sib.msgstore.impl.MessageStoreImpl;
import com.ibm.ws.sib.msgstore.transactions.ExternalAutoCommitTransaction;
import com.ibm.ws.sib.msgstore.transactions.ExternalLocalTransaction;
import com.ibm.ws.sib.msgstore.transactions.ExternalXAResource;
import com.ibm.ws.sib.transactions.TransactionFactory;
import com.ibm.ws.sib.utils.ras.SibTr;

/**
 * This class is a factory for the interfaces used to achieve transactional
 * behaviour within the MessageStore.
 */
public final class MSTransactionFactory implements TransactionFactory
{
    private static TraceComponent tc = SibTr.register(MSTransactionFactory.class,
                                                      MessageStoreConstants.MSG_GROUP,
                                                      MessageStoreConstants.MSG_BUNDLE);
    // The maximum number of operations permitted in a transaction.
    private int _maximumSize = DEFAULT_MAXIMUM_TRANSACTION_SIZE;
    
   
    private MessageStoreImpl   _ms;
    private PersistenceManager _persistence;
    private boolean            _persistenceSupports1PCOptimisation;


    public MSTransactionFactory(MessageStoreImpl ms, PersistenceManager persistence)
    {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.entry(tc, "MSTransactionFactory", "MessageStore="+ms+", Persistence="+persistence);

        _ms          = ms;
        _persistence = persistence;
        _persistenceSupports1PCOptimisation = _persistence.supports1PCOptimisation();

        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.exit(tc, "MSTransactionFactory");
    }


    /**
     * This method returns an object that represents a zero-phase or AutoCommit 
     * transaction. It can be used to ensure that a piece of work is carried out 
     * at once, essentially outside of a transaction coordination scope.
     * 
     * @return An instance of AutoCommitTransaction
     */
    public ExternalAutoCommitTransaction createAutoCommitTransaction()
    {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.entry(tc, "createAutoCommitTransaction");

        ExternalAutoCommitTransaction instance = new MSAutoCommitTransaction(_ms, _persistence, getMaximumTransactionSize());

        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.exit(tc, "createAutoCommitTransaction", "return="+instance);
        return instance;
    }


    /**
     * This method is used to create a LocalResource that can either be enlisted as 
     * a particpant in a WebSphere LocalTransactionCoordination scope or used directly
     * to demarcate a one-phase Resource Manager Local Transaction.
     * 
     * @return An instance of Object
     */
    public ExternalLocalTransaction createLocalTransaction()
    {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.entry(tc, "createLocalTransaction");

        ExternalLocalTransaction instance;
        
        if (_persistenceSupports1PCOptimisation)
        {
            instance = new MSDelegatingLocalTransactionSynchronization(_ms, _persistence, getMaximumTransactionSize());
        }
        else
        {
            instance = new MSDelegatingLocalTransaction(_ms, _persistence, getMaximumTransactionSize());
        }

        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.exit(tc, "createLocalTransaction", "return="+instance);
        return instance;
    }


    /**
     * This method is used to create an XAResource that can be enlisted as a 
     * particpant in a two-phase XA compliant transaction.
     * 
     * @return An instance of XAResource
     */
    public ExternalXAResource createXAResource()
    {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.entry(tc, "createXAResource");

        ExternalXAResource instance = new MSDelegatingXAResource(_ms, _persistence, getMaximumTransactionSize());

        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.exit(tc, "createXAResource", "return="+instance);
        return instance;
    }
    
    public final int getMaximumTransactionSize()
    {
 	   return _maximumSize;
    }

    public final void setMaximumTransactionSize(int maximumSize) 
    {
        if (tc.isEntryEnabled()) SibTr.entry(tc, "setMaximumTransactionSize", "MaximumTranSize="+maximumSize);
        _maximumSize = maximumSize;
        if (tc.isEntryEnabled()) SibTr.exit(tc, "setMaximumTransactionSize");
    }
}

