/*
 * 
 * 
 * ============================================================================
 * IBM Confidential OCO Source Materials
 * 
 * 5639-D57, 5630-A36, 5630-A37, Copyright IBM Corp. 2012
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
 * SIB0113b.mp.1    040907 dware    Initial support for SIB0113b function
 * ===========================================================================
 */
package com.ibm.ws.sib.processor.impl.interfaces;

import com.ibm.websphere.sib.exception.SIResourceException;
import com.ibm.ws.sib.processor.impl.store.items.MessageItem;
import com.ibm.ws.sib.transactions.TransactionCommon;
import com.ibm.ws.sib.utils.SIBUuid8;
import com.ibm.wsspi.sib.core.exception.SIDiscriminatorSyntaxException;

/**
 * An interface class for the different types of output class.
 */
public interface OutputHandler
{
  /**
   * Put a message on this OutputHandler for delivery to consumers/remote ME's 
   * 
   * @param msg The message to be delivered
   * @param tran The transaction to be used (must at least have an autocommit transaction)
   * @param inputHandlerStore The input handler putting this message 
   * @param storedByIH true if the message has already been stored in the Input Handler
   * @return true if the message was stored in the Input Handler (either before or during this call)
   * 
   * @throws SIResourceException  thrown if there is some problem with GD 
   */
  public boolean put(
    SIMPMessage msg,
    TransactionCommon transaction,
    InputHandlerStore inputHandlerStore,
    boolean storedByIH)
    throws SIResourceException, SIDiscriminatorSyntaxException;

  /** 
   * @param msg
   * @return boolean true if message can be sent to another ME
   */
  public boolean commitInsert(MessageItem msg)
    throws SIResourceException;
  
  /** 
   * @param  msg
   * @return boolean true if message can be sent to another ME
   */
  public boolean rollbackInsert(MessageItem msg)
    throws SIResourceException;

  /**  
   * @return boolean true if this outputhandler from wlm was guessed
   */
  public boolean isWLMGuess();
  
  /** 
   * @param  guess
   */
  public void setWLMGuess(boolean guess);
    
  /**  
   * @return boolean true if this outputhandler's itemstream has reached QHighMessages
   */
  public boolean isQHighLimit();
  
  /**
   * Get the Uuid of the ME that this OutputHandler represents
   * @return SIBUuid8
   */
  public SIBUuid8 getTargetMEUuid();
}
