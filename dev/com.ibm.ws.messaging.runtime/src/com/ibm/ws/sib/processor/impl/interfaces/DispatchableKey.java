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
 *                  210605 tevans   Reset Change history - previous WAS602.SIB
 * SIB0002.mp.1     210605 tevans   PEV Prototype
 * SIB0113a.mp.1    240707 cwilkin  Gathered Consumer foundation
 * 520472           220508 cwilkin  Gathering reattaching
 * ===========================================================================
 */
package com.ibm.ws.sib.processor.impl.interfaces;

import com.ibm.websphere.sib.exception.SIException;
import com.ibm.websphere.sib.exception.SIResourceException;
import com.ibm.ws.sib.matchspace.Selector;

/**
 * DispatchableKey should be implemented by any class wishing to register
 * with a ConsumerDispatcher for dispatching
 */
public interface DispatchableKey extends JSConsumerKey
{
  
  public long getVersion();
  public DispatchableConsumerPoint getConsumerPoint();
  public boolean requiresRecovery(SIMPMessage message) throws SIResourceException;
  public JSConsumerKey getParent();
  public DispatchableKey resolvedKey();
  public Selector getSelector();
  public void notifyReceiveAllowed(boolean newReceiveAllowed, DestinationHandler handler);
  public void notifyConsumerPointAboutException(SIException e);

}
