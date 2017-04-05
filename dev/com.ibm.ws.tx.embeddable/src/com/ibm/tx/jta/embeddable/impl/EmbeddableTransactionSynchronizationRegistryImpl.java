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
 * 
 * %Z% %I% %W% %G% %U% [%H% %T%]
 * 
 */
package com.ibm.tx.jta.embeddable.impl;

import com.ibm.tx.jta.embeddable.EmbeddableTransactionManagerFactory;
import com.ibm.tx.jta.impl.TranManagerSet;
import com.ibm.tx.jta.impl.TransactionImpl;
import com.ibm.tx.jta.impl.TransactionSynchronizationRegistryImpl;

public class EmbeddableTransactionSynchronizationRegistryImpl extends TransactionSynchronizationRegistryImpl {

    public EmbeddableTransactionSynchronizationRegistryImpl() {
        super();
    }

    /**
     * Ensure we use the EmbeddableTransactionManagerFactory, since the super class uses the non-embeddable version
     * in its implementation of "getTransaction"
     */
    @Override
    protected TransactionImpl getTransaction() {
        return getTransactionManager().getTransactionImpl();
    }

    /**
     * Convenience method to get the tran manager
     */
    protected TranManagerSet getTransactionManager() {
        return (TranManagerSet) EmbeddableTransactionManagerFactory.getTransactionManager();
    }

    /**
     * This method was using the non-embeddable tran manager in the super class
     */
    @Override
    public int getTransactionStatus() {
        return getTransactionManager().getStatus();
    }

    /**
     * This method was using the non-embeddable tran manager in the super class
     */
    @Override
    public void setRollbackOnly() {
        getTransactionManager().setRollbackOnly();
    }

}
