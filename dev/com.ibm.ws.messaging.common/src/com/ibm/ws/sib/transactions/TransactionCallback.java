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
 * SIB0002.tran.2   290705 schofiel Integration with MS
 * ===========================================================================
 */
package com.ibm.ws.sib.transactions;

/**
 * Callback used to notify the completion events for a transaction.
 */
public interface TransactionCallback
{
   /**
    * Notification that the transaction is about to be completed.  Completion
    * will take place immediately after all registered callbacks have been
    * notified.
    * @param transaction The transaction to which this notification applies
    */
   public void beforeCompletion(TransactionCommon transaction);
   
   /**
    * Notification that completion has just taken place.
    * @param transaction The transaction to which this notification applies
    * @param committed True if (and only if) the completion event was the
    * transaction being committed.
    */
   public void afterCompletion(TransactionCommon transaction, boolean committed);
}
