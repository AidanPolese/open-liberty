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
 * SIB0113a.mp.1    240707 cwilkin  Gathered Consumer foundation
 * SIB0113a.mp.3    010807 vaughton Class rename
 * SIB0113a.mp.9    261007 cwilkin  Remote Gathering
 * SIB0113a.mp.11   131207 cwilkin  Message Gathering IME Recovery
 * 520472           220508 cwilkin  Gathering reattaching
 * 515543           180708 cwilkin  Handle MessageStoreRuntimeExceptions on msgstore interface
 */

package com.ibm.ws.sib.processor.impl.interfaces;

import com.ibm.websphere.sib.exception.SIResourceException;
import com.ibm.ws.sib.processor.impl.ConsumerDispatcherState;
import com.ibm.ws.sib.processor.impl.LocalQPConsumerKeyGroup;
import com.ibm.ws.sib.processor.impl.JSLockedMessageEnumeration;
import com.ibm.ws.sib.processor.impl.store.items.AOValue;
import com.ibm.ws.sib.transactions.TransactionCommon;

public interface JSConsumerManager extends ConsumerManager {

  public void checkInitialIndoubts(DispatchableConsumerPoint point) throws SIResourceException;

  public void setCurrentTransaction(TransactionCommon transaction, JSLockedMessageEnumeration lme);

  public ConsumerDispatcherState getConsumerDispatcherState();

  public long newReadyConsumer(JSConsumerKey key, boolean specific);

  public void removeReadyConsumer(JSConsumerKey key, boolean specific);

  public void removeKeyGroup(LocalQPConsumerKeyGroup group);

  public void setCurrentTransaction(SIMPMessage msg, boolean isInDoubtOnRemoteConsumer);

  public boolean isPubSub();
  
  /** Methods for accessing msgs from AOStreams **/
  
  /**
   * On Restart of a DME/IME, the messages referenced by an AOValue are retrieved via the 
   * RemoteConsumerDispatcher using the getMessageByValue method.
   */
  public SIMPMessage getMessageByValue(AOValue value)
    throws SIResourceException;

}
