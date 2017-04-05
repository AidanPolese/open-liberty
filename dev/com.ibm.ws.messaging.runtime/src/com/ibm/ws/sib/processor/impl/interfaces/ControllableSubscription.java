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
 * 186484.8         300404 tevans   Runtime Subscription Administration
 * 186484.10        170504 tevans   MBean Registration
 * ===========================================================================
 */
package com.ibm.ws.sib.processor.impl.interfaces;

import com.ibm.ws.sib.processor.impl.ConsumerDispatcherState;
import com.ibm.ws.sib.utils.SIBUuid12;

public interface ControllableSubscription extends ControllableResource
{
  public SIBUuid12 getSubscriptionUuid();
  public ConsumerDispatcherState getConsumerDispatcherState();
  public OutputHandler getOutputHandler();
  //local or proxy
  public boolean isLocal();
  public boolean isDurable();  
}
