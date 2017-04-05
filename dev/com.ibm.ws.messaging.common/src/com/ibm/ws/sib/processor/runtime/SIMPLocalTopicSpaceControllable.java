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
 * 193906.3         190504 prmf     Send/Receive Allowed Runtime
 * 193906.5         070604 prmf     Put/Get Inhibit now Send/Receive Allowed
 * 195809.4         170604 prmf     Queue Depth Limits - new attributes
 * 224010           130804 gatfora  Remove getMaxMsgs and setMaxMsgs.
 * 233063           200904 prmf     Remove receiveAllowed from localization
 * 248030.1         240105 tpm      MBean extensions
 * PK62569          160308 pbroad   Add depth attribute to SIBPublicationPoint
 * ===========================================================================
 */
package com.ibm.ws.sib.processor.runtime;

/**
 * 
 */
public interface SIMPLocalTopicSpaceControllable extends SIMPControllable
{
  /**
   * Locates the Topic Space relating to the attached remote subscriber. 
   *
   * @return SIMPTopicSpaceControllable  The TopicSpace object. 
   *
   */
  SIMPTopicSpaceControllable getTopicSpace();

  /**
   * Returns the number of messages currently on the publication point that
   * have not yet been consumed by all subscribers.
   *  
   * @return The number of unique messages on this localization.
   */
  public long getNumberOfQueuedMessages();
  
  /**
   * Returns the high messages limit property.
   *  
   * @return The destination high messages threshold for this localization.
   */
  public long getDestinationHighMsgs();
  
  /**
   * Allows the caller to find out whether this localization accepts messages
   * 
   * @return false if the localization prevents new messages being put, true
   * if further messages may be put.
   */
  public boolean isSendAllowed();

  /**
   * Allows the mbean user to set the current destination high messages threshold.
   * This value is not persisted.
   * 
   * @param arg The high messages threshold for this localization.
   */
  public void setDestinationHighMsgs(long arg);
  
  /**
   * Allows the caller to stop this localization accepting further messages
   * or not, depending on the value.
   * <p>
   * 
   * @param arg false if messages are to be prevented being put onto this 
   * localization, true if messages are to be allowed onto this localization.
   */
  public void setSendAllowed(boolean arg);

  /**
   * Get an iterator over all of the inbound receivers for this local topicspace.
   * @return an iterator containing SIMPInboundReceiverControllable objects.
   */
  public SIMPIterator getPubSubInboundReceiverIterator();
  
  /**
   * Get an iterator over all of the local subscriptions on this
   * topic space.
   * This iterator contains SIMPLocalSubscriptionControllable
   * @return
   * @author tpm
   */
  public SIMPIterator getLocalSubscriptions();
  
  

}
