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
 * ---------------  ------ -------- -------------------------------------------
 * SIB0002.tran.2   050805 tevans   Reset Change history - previous WAS602.SIB
 * SIB0002.tran.2   050805 tevans   New Transactions interfaces
 * 419906           080307 cwilkin  Remove Cellules
 * SIB0113b.mp.1    040907 dware    Initial support for SIB0113b function
 * ============================================================================
 */
package com.ibm.ws.sib.processor.impl.interfaces;

// Import required classes.
import com.ibm.websphere.sib.exception.SIIncorrectCallException;
import com.ibm.websphere.sib.exception.SINotPossibleInCurrentConfigurationException;
import com.ibm.websphere.sib.exception.SIResourceException;
import com.ibm.ws.sib.processor.impl.store.items.MessageItem;
import com.ibm.ws.sib.transactions.TransactionCommon;
import com.ibm.ws.sib.utils.SIBUuid8;
import com.ibm.wsspi.sib.core.exception.SIConnectionLostException;
import com.ibm.wsspi.sib.core.exception.SIRollbackException;

/**
 * @author tevans
 */
/**
 * An interface class for the different types of inpt class.
 */

public interface InputHandler
{
 
  /**
   * Called by the system when processing a message
   * 
   * @param msg The message
   * @param transaction The transaction to use
   * @param sourceCellule The originator of the message
   * @throws SIConnectionLostException
   * @throws SIRollbackException
   * @throws SINotPossibleInCurrentConfigurationException
   * @throws SIIncorrectCallException
   * @throws SIResourceException
   */
  public void handleMessage(
    MessageItem msg,
    TransactionCommon transaction,
    SIBUuid8 sourceCellule)
  throws SIConnectionLostException, 
         SIRollbackException, 
         SINotPossibleInCurrentConfigurationException, 
         SIIncorrectCallException, 
         SIResourceException;
  
}
