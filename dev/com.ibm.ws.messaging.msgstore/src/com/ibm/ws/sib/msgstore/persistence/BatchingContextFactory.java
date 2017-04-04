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
 * Reason     Date        Origin       Description
 * ---------- ----------- --------     --------------------------------------------
 * 182068.2   21-Nov-03   schofiel     Creation
 * 180763.7   10-Feb-04   pradine      Add support for mutiple item tables
 * 188050.4   06-Apr-04   pradine      SpecJAppServer2003 optimization
 * 229186     03-Sep-04   pradine      Cache the batching context inside a transaction
 * ============================================================================
 */
package com.ibm.ws.sib.msgstore.persistence;

import com.ibm.ws.sib.msgstore.transactions.impl.PersistentTransaction;

/**
 * Instances of this interface provide factory methods for creation of instances of
 * {@link BatchingContext}. This makes it easier to plug in alternative batching contexts. 
 */
public interface BatchingContextFactory
{
    /**
     * Returns a new {@link BatchingContext}
     * 
     * @return a new {@link BatchingContext}
     */
    public BatchingContext createBatchingContext();
    
    /**
     * Returns a new {@link BatchingContext}
     * 
     * @param capacity the maximum number of objects to be stored in the {@link BatchingContext}
     * @return a new {@link BatchingContext}
     */
    public BatchingContext createBatchingContext(int capacity);

    /**
     * Returns a new {@link BatchingContext}
     * 
     * @param capacity the maximum number of objects to be stored in the {@link BatchingContext}
     * @param useEnlistedConnections <code>true</code> to enlist connections, <code>false</code> otherwise.
     * @return a new {@link BatchingContext}
     */
    public BatchingContext createBatchingContext(int capacity, boolean useEnlistedConnections);
    
    /**
     * Returns the {@link BatchingContext} associated with a transaction
     *
     * @param transaction the transaction
     * @param capacity the maximum number of objects to be stored in the {@link BatchingContext}
     * @return a {@link BatchingContext}
     */
    public BatchingContext getBatchingContext(PersistentTransaction transaction, int capacity);
    
    /**
     * Returns the {@link BatchingContext} associated with a transaction
     * 
     * @param transaction the transaction
     * @param capacity the maximum number of objects to be stored in the {@link BatchingContext}
     * @param useEnlistedConnections <code>true</code> to enlist connections, <code>false</code> otherwise.
     * @return a {@link BatchingContext}
     */
    public BatchingContext getBatchingContext(PersistentTransaction transaction, int capacity, boolean useEnlistedConnections);    
}
