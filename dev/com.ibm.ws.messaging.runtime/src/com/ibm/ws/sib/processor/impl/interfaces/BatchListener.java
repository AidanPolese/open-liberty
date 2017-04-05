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
 * SIB0002.tran.2   050805 tevans   Reset Change history - previous WAS602.SIB
 * SIB0002.tran.2   050805 tevans   New Transactions interfaces
 * ===========================================================================
 */
package com.ibm.ws.sib.processor.impl.interfaces;

import com.ibm.ws.sib.transactions.TransactionCommon;

/**
 * BatchListener should be implemented by any class wishing to receive
 * batching events
 * 
 * @author tevans
 */
public interface BatchListener {

  /**
   * @param currentTran
   */
  public void batchPrecommit(TransactionCommon currentTran);

  /**
   * 
   */
  public void batchCommitted();

  /**
   * 
   */
  public void batchRolledBack();
  
}
