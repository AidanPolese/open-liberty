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
 * SIB0113a.mp.1    240707 cwilkin  Gathered Consumer foundation
 * SIB0163.mp.1     190907 nyoung   XD Integration.
 * 450667           071007 dware    Use ReentrantLocks instead of sychronized
 * ===========================================================================
 */

package com.ibm.ws.sib.processor.impl.interfaces;

import com.ibm.websphere.sib.exception.SIResourceException;
import com.ibm.ws.sib.msgstore.LockingCursor;

public interface JSKeyGroup extends JSConsumerKey, ConsumerKeyGroup
{

  void addMember(JSConsumerKey key) throws SIResourceException;

  void removeMember(JSConsumerKey key);

  void stopMember();

  void startMember();

  boolean isStarted();

  void groupNotReady();

  void groupReady();

  void setConsumerActive(boolean b);

  boolean isGroupReady();

  void attachMessage(ConsumableKey key);

  ConsumableKey getAttachedMember();

  ConsumableKey getMatchingMember(ConsumableKey key);

  Object getAsynchGroupLock();

  LockingCursor getDefaultGetCursor();

  LockingCursor getGetCursor(int classification);
  
  /*
   * Methods that implement the required ReentrantLock methods
   */
  void lock();
  
  void unlock();

}
