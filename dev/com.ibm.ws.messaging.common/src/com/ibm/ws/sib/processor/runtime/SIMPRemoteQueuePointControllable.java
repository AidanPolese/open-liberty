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
 * 186484.18        290604 tevans   Remote queue point control improvements
 * 248030.1         170105 tpm      MBean extensions
 * SIB0113a.mp.10   231107 cwilkin  Message Gathering controllables
 * 421276           260208 cwilkin  Move requests getter to parent
 * ===========================================================================
 */
package com.ibm.ws.sib.processor.runtime;

/**
 * The interface presented by a queueing point localization to perform dynamic
 * control operations.
 * <p>
 * The operations in this interface are specific to a queueing point.
 */
public interface SIMPRemoteQueuePointControllable extends SIMPXmitPoint
{ 
  /**
   * Get the remote consumer receiver. This exists if we are performing or have performed
   * remote get against the remote queue. 
   * 
   * SIB0113a - This only returns the non-gathering stream. 
   *
   * @return A RemoteConsumerReceiver or null if there is none. 
   */
  public SIMPRemoteConsumerReceiverControllable getRemoteConsumerReceiver();
   
  /**
   * As of the introduction of gathering consumers (SIB0113) it is now possible
   * to have multiple remote get streams. Admin now needs to iterate over them
   * and display a collections panel for the list. 
   * 
   * The old method above is kept for legacy code.
   */
  public SIMPIterator getRemoteConsumerReceiverIterator();
  
  /**
   * Get the number of message requests that have completed via this remote queue point.
   * This is required at this level to be able to retrieve the value even when
   * the individual remote consumer receivers have disappeared.
   * 
   */
  public long getNumberOfCompletedRequests();
}
