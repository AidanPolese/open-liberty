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
 * SIB0113a.mp.1    240707 cwilkin  Gathered Consumer foundation
 * SIB0113a.mp.5    080807 vaughton Gathered Consumers
 * SIB0163.mp.1     190907 nyoung   XD Integration.
 * SIB0163.mp.3     021107 nyoung   MaxConcurrency of 1 on ConsumerSet causes dithering.
 * 467999           141107 dware    Rework maxActiveMessages for performance, ConsumerSets and correctness
 * 520472           220508 cwilkin  Gathering reattaching
 * 512943.1         300608 cwilkin  Gathering Synchronisation and Threshold
 * ============================================================================
 */
package com.ibm.ws.sib.processor.impl.interfaces;

import com.ibm.websphere.sib.exception.SINotPossibleInCurrentConfigurationException;
import com.ibm.websphere.sib.exception.SIResourceException;
import com.ibm.ws.sib.msgstore.MessageStoreException;
import com.ibm.ws.sib.processor.impl.JSConsumerSet;
import com.ibm.ws.sib.utils.SIBUuid8;

public interface ConsumableKey extends JSConsumerKey {

  ConsumerPoint getConsumerPoint();

  void detach() throws SIResourceException, SINotPossibleInCurrentConfigurationException;

  void start();

  long waiting(long timeout, boolean b);

  void leaveKeyGroup();

  void stop();

  boolean isClosedDueToReceiveExclusive();

  boolean isClosedDueToDelete();

  boolean isClosedDueToLocalizationUnreachable();

  SIMPMessage getMessageLocked() throws MessageStoreException;

  /**
   * Indicates that the caller is about to attempt to lock a message. This
   * method allows the implementor to reject the attempt or track its progress.
   * 
   * Any successful call to this method wil be followed by either commitAddActiveMessage()
   * or rollbackAddActiveMessage()
   */
  boolean prepareAddActiveMessage();

  /**
   * Commits the prepare of an add, see prepareAddActiveMessage()
   */
  void commitAddActiveMessage();

  /**
   * Rolls back the prepare of an add, see prepareAddActiveMessage()
   */
  void rollbackAddActiveMessage();
  
  /**
   * Notifies the key about the removal of an active message. This method supports message 
   * concurrency where SIB is registered with XD
   * 
   * @param messageCount number of messages to remove from the count
   */
  void removeActiveMessages(int messageCount);  
  
  /**
   * Has the Consumer Set associated with this key been suspended because its 
   * concurrent active message limit has been breached. This method supports message 
   * concurrency where SIB is registered with XD
   * 
   * @return
   */
  boolean isConsumerSetSuspended();

  /**
   * Return the Consumer Set associated with this key. This method supports message 
   * concurrency where SIB is registered with XD
   * 
   * @return
   */
  JSConsumerSet getConsumerSet();
  
  /**
   * Called when the consumerpoint has been told to implicitly close
   * @param deleted
   * @param receiveExclusive
   * @param exception
   */
  public boolean close(int closeReason, SIBUuid8 qpoint);

  /**
   * Allow this consumerKey to joing the given keygroup
   * @param keyGroup
   */
  void joinKeyGroup(JSKeyGroup keyGroup) throws SIResourceException;

}
