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
 * 186484.5         210404 ajw      Further Continued controllable interfaces
 * 186484.22.1      140704 cwilkin  KnownDurableSubscriptions
 * ===========================================================================
 */
package com.ibm.ws.sib.processor.runtime;

/**
 * 
 */
public interface SIMPKnownDurableSubscriptionControllable extends SIMPControllable
{
  /**
   * Locate the name of the ME which hosts the subscription
   * 
   * @return String Name of the ME.
   */
  public String getDurableHome();

  /**
   * Locates the LocalSubscription controllable of this durable subscription.
   *
   * @return SIMPLocalSubscriptionControllable  The LocalSubscriptionControl object. 
   */
  SIMPLocalSubscriptionControllable getLocalSubscriptionControl();
  
}
