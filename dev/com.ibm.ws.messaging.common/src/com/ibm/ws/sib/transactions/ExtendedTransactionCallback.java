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
 * D304025          071005 prestona Creation
 * ===========================================================================
 */
package com.ibm.ws.sib.transactions;

/**
 * Extension to the transaction callback interface.  This interface provides
 * an enhanced after completion method which will be called in preference to
 * the after completion method of TransactionCallback.
 */
public interface ExtendedTransactionCallback extends TransactionCallback
{
   /**
    * Provides notification that the transaction, or transaction branch has
    * completed.
    * @param transaction the transaction that has completed
    * @param tranId the persistent transaction identifier that identifies the transaction
    * or transaction branch being completed.
    * @param committed true if the transaction completion resulted in the unit of work
    * being committed successfully.
    */
   public void afterCompletion(TransactionCommon transaction, PersistentTranId tranId, boolean committed);
}
