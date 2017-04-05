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
 * SIB0002.tran.2   030805 tevans   Integration with MP
 * ============================================================================
 */
package com.ibm.ws.sib.transactions;

import com.ibm.websphere.sib.exception.SIResourceException;

/**
 * Common transaction functionality.  All transaction instances passed to the Core SPI should
 * implement this interface.
 */
public interface TransactionCommon
{
    /**
     * @return The unique transaction ID
     */
    public PersistentTranId getPersistentTranId();
       
    /**
     * @return True if this transaction object currently represents a
     * transaction being tracked by the transaction manager. 
     */
    public boolean isAlive();
       
    /**
     * @return True if (and only if) the transaction is auto commit.
     */
    public boolean isAutoCommit();
       
    /**
     * @return True if (and only if) the transaction has (or can have)
     * subordinates.
     */
    public boolean hasSubordinates();
      
    /**
     * @param callback Registers a transaction lifecycle callback with
     * the transaction.  This will be notified prior to the transaction
     * been completed and after completion of the transaction.
     */
    public void registerCallback(TransactionCallback callback);
       
    /**
     * Increments the current size of the transaction.
     * @throws SIResourceException Thrown if too many operations are
     * performed by the transaction.
     */
    public void incrementCurrentSize() throws SIResourceException;
}
