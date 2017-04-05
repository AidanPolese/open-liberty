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
 * ---------------  ------ -------- -------------------------------------------
 * SIB0002.tran.2   050805 tevans   Reset Change history - previous WAS602.SIB
 * SIB0002.tran.2   050805 tevans   New Transactions interfaces
 * ============================================================================
 */
package com.ibm.ws.sib.processor.impl.interfaces;

// Import required classes.
import com.ibm.websphere.sib.exception.SIResourceException;
import com.ibm.ws.sib.msgstore.ItemStream;
import com.ibm.ws.sib.processor.impl.store.items.MessageItem;
import com.ibm.ws.sib.transactions.TransactionCommon;

/**
 * @author tevans
 */
/**
 * An interface class for the different types of inpt class.
 */

public interface InputHandlerStore
{
 
  void storeMessage(MessageItem msg, TransactionCommon tran) 
  throws SIResourceException;
  
  public ItemStream getItemStream();
  
  
}
