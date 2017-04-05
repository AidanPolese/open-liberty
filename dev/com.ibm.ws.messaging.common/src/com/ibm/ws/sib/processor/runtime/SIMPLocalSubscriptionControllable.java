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
 * 186484.9         040504 tevans   Yet another runtime control point feature
 * 186484.9         060504 tevans   Extended runtime control implementation
 * 186484.10        170504 tevans   MBean Registration
 * 216945           160704 gatfora  getNumberOfQueuedMessages returns long
 * 216685           160704 ajw      cleanup anycast runtime control impl
 * ===========================================================================
 */
package com.ibm.ws.sib.processor.runtime;

import com.ibm.ws.sib.processor.exceptions.*;
import com.ibm.ws.sib.processor.exceptions.SIMPException;

/**
 * 
 */
public interface SIMPLocalSubscriptionControllable extends SIMPControllable
{
  /**
   * Locates the Topic Space relating to the attached remote subscriber. 
   *
   * @return SIMPTopicSpaceControllable  The TopicSpace object. 
   */
  public SIMPTopicSpaceControllable getTopicSpace();

  /**
    * Returns the selector.
    * @return String
    */
  public String getSelector();

  /**
   * Returns the subscriberID.
   * @return String
   */
  public String getSubscriberID();

  /**
   * Returns the array of topics.
   * 
   * @return String[]  The array of topics
   */
  public String[] getTopics();

  /**
   * Locates the consumers attached to the local subscription. 
   *
   * @return Iterator  An iterator over all of the Consumer objects. 
   */
  public SIMPIterator getConsumerIterator();

  /**
   * Locates the remote consumer xmit point. This exists of there is a remote consumer
   *  of the messages queued against this subscription. 
   *
   * @return SIMPIterator  a iterator over all the SIMPRemoteConsumerTransmitControllable 
   */
  public SIMPIterator getRemoteConsumerTransmit();

  /**
   * Locates the queued messages. 
   *
   * @return Iterator  An iterator over all of the QueuedMessage objects queued for this local subscription. 
   */
  public SIMPIterator getQueuedMessageIterator();
  public SIMPQueuedMessageControllable getQueuedMessageByID(String ID)
    throws SIMPInvalidRuntimeIDException,
           SIMPControllableNotFoundException,
           SIMPException;
  
  /**
   * Returns the number of queued messages for this subscription.
   */
  public long getNumberOfQueuedMessages();
}
