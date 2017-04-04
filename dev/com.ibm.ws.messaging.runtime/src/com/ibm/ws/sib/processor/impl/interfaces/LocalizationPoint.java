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
 * ---------------  ------ -------- ------------------------------------------
 * SIB0002.mp.1     210605 tevans   PEV Prototype
 * SIB0002.mp.4.1   290605 tpm      PEV Stats
 * 515543           180708 cwilkin  Handle MessageStoreRuntimeExceptions on msgstore interface
 * ===========================================================================
 */
package com.ibm.ws.sib.processor.impl.interfaces;

import com.ibm.ws.sib.msgstore.MessageStoreException;
import com.ibm.ws.sib.processor.impl.BaseDestinationHandler;
import com.ibm.ws.sib.utils.SIBUuid8;


public interface LocalizationPoint extends ControllableResource
{
  public ConsumerManager createConsumerManager();
  public ConsumerManager getConsumerManager();
  public void dereferenceConsumerManager();
  
  /**
   * @return
   */
  public BaseDestinationHandler getDestinationHandler();
  /**
   * @return
   */
  public boolean reallocateMsgs();
  /**
   * @return
   */
  public SIBUuid8 getLocalizingMEUuid();
  /**
   * @return
   */
  public OutputHandler getOutputHandler();
  /**
   * @return
   */
  public boolean isSendAllowed();
  /**
   * @return
   */
  public boolean isQHighLimit();
  /**
   * @return
   */
  public long getID() throws MessageStoreException;

  /**
   * @param outputHandler
   */
  public void setOutputHandler(OutputHandler outputHandler);
  
  public void initializeNonPersistent(BaseDestinationHandler destinationHandler);
  /**
   * @return
   */
  public boolean isQLowLimit();
  
  /**
   * @return a long for the age of the oldest message on the queue 
   */
  public long getOldestMessageAge();
  
  /**
   * @return a long for the number of unlocked messages on the queue
   */
  public long getAvailableMessageCount();
  
  /**
   * @return a long for the number of locked messages on the queue
   */
  public long getUnAvailableMessageCount();
}
