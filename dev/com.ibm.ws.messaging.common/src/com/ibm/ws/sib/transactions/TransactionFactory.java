/*
 * 
 * 
 * ============================================================================
 * IBM Confidential OCO Source Materials
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
 * Reason           Date   Origin   Description
 * ---------------  ------ -------- -------------------------------------------------
 * ===========================================================================
 */
package com.ibm.ws.sib.transactions;

import com.ibm.ws.sib.msgstore.transactions.ExternalAutoCommitTransaction;
import com.ibm.ws.sib.msgstore.transactions.ExternalLocalTransaction;
import com.ibm.ws.sib.msgstore.transactions.ExternalXAResource;

/**
 * A factory for creating transactions.
 */
public interface TransactionFactory
{
    /**
     * The default maximum count agreed by feature to be 100.
     */
    public static final int DEFAULT_MAXIMUM_TRANSACTION_SIZE = 100;

    /** 
     * @return An auto commit transaction
     */
    public ExternalAutoCommitTransaction createAutoCommitTransaction();
   
    /**
     * @return A local transaction.  This is suitable for use where only
     * a single internal resource (Platform Messaging) is being coordinated.
     */
    public ExternalLocalTransaction createLocalTransaction();   
   
    /** 
     * @return An XAResource that can be used to enlist Plaform Messaging
     * with a global transaction.  This resource is suitable for use where
     * only a single internal resource (Platform Messaging) is being
     * coordinated.
     */
    public ExternalXAResource createXAResource();


    /**
     * @return The maximum number of operations in a transaction.
     */
    public int getMaximumTransactionSize();

    /**
     * Sets the maximum number of operations in a transaction.
     * @param maximumSize
     */
    public void setMaximumTransactionSize(int maximumSize); 
}
