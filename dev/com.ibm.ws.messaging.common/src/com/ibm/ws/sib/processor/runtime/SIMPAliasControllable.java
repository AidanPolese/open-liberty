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
 * 186484.4         050404 tevans   Continued controllable interfaces
 * 199152           200404 gatfora  Correct javadoc.
 * 186484.7         270404 tevans   More runtime control interfaces and implementation
 * 186484.10        170504 tevans   MBean Registration 
 * ===========================================================================
 */
package com.ibm.ws.sib.processor.runtime;

import com.ibm.websphere.sib.Reliability;

/**
 * The interface presented by a queue to perform dynamic
 * control operations.
 * <p>
 * The operations in this interface are specific to a queueing point.
 */
public interface SIMPAliasControllable extends SIMPMessageHandlerControllable
{
  /**
   * Get the target message handler as known to the MP. 
   *
   * @return The target SIMPMessageHandlerControllable object. 
   */
  public SIMPMessageHandlerControllable getTargetMessageHandler();

  public String getBus();
  public int getDefaultPriority();
  public Reliability getMaxReliability();
  public Reliability getDefaultReliability();
  public boolean isProducerQOSOverrideEnabled();
  public boolean isReceiveAllowed();
  public boolean isSendAllowed();
}
