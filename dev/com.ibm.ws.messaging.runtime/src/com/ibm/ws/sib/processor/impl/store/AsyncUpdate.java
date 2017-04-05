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
 * Reason          Date   Origin   Description
 * --------------- ------ -------- --------------------------------------------
 * SIB0002.tran.2   050805 tevans   Reset Change history - previous WAS602.SIB
 * SIB0002.tran.2   050805 tevans   New Transactions interfaces
 * ============================================================================
 */

package com.ibm.ws.sib.processor.impl.store;

import com.ibm.websphere.sib.exception.SINotPossibleInCurrentConfigurationException;
import com.ibm.websphere.sib.exception.SIResourceException;
import com.ibm.ws.sib.transactions.TransactionCommon;

public abstract class AsyncUpdate
{
  /**
   * The method that executes the update.
   * Most error handling should be done by this method itself
   * and not exposed to the caller (by throwing Throwable).
   * The AsyncUpdateThread guarantees that if any
   * Throwable is thrown by this method, it will call rolledback() on
   * this AsyncUpdate.
   */
  public abstract void execute(TransactionCommon t) throws Throwable;

  /**
   * Commit notification method
   */
  public abstract void committed() throws SIResourceException, SINotPossibleInCurrentConfigurationException;

  /**
   * Rolledback notification method
   * @param e The exception that caused the rollback
   */
  public abstract void rolledback(Throwable e);
}
