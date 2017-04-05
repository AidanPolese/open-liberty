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
 * 186484.22.3      290704 ajw      Renamed to SIMPAttachedRemoteSubscriberControllable
 * 248030.1         170105 tpm      MBean extensions
 * ===========================================================================
 */
package com.ibm.ws.sib.processor.runtime;

/**
 * Interface to manipulate a subscriber that is located on a remote messaging
 * engine
 */
public interface SIMPAttachedRemoteSubscriberControllable extends SIMPControllable
{
   /**
    * Locates the Topic Space relating to the attached remote subscriber. 
    *
    * @return SIMPTopicSpaceControllable  The topicSpace object.
    */
   SIMPTopicSpaceControllable getTopicSpace();

   /**
    * Locates the remote consumer receiver. 
    *
    * @return SIMPRemoteConsumerReceiverController  a remoteConsumerReceiver. 
    */
   SIMPRemoteConsumerReceiverControllable getRemoteConsumerReceiver();

   /**
    * Locates the consumers attached to the remote subscriber. 
    *
    * @return Iterator  An iterator over all of the Consumer objects. 
    */
   SIMPIterator getConsumerIterator();
   
  /**
   * Returns a string for each topic to which this consumer is subscribed.
   * @return SIMPIterator  An iterator of Strings 
   */
  SIMPIterator getTopicNameIterator();   
  
  /**
   * Clears all of the topics that this remote subscription is currently 
   * subscribed for.
   */
  void clearAllTopics();
}
