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
 * ---------------  ------ -------- -------------------------------------------
 * 179629.2.1       241103 jroots   Original
 * 195758.0         050404 jroots   Milestone 7.5 Core SPI changes
 * 199556           260404 jroots   Remove getRetryCount
 * 210259           190804 gatfora  Added getDefaultForwardRoutingPath and getReplyDestination
 * 276259           130505 dware    Improve security related javadoc
 * 310870           131005 cwilkin  Add strict ordering attribute
 * ============================================================================
 */
package com.ibm.wsspi.sib.core;

import java.util.Map;

import com.ibm.websphere.sib.Reliability;
import com.ibm.websphere.sib.SIDestinationAddress;

/**
 * DestinationConfiguration allows the user of the Jetstream Core SPI some
 * limited information regarding any destination that is visible via the 
 * connection on which it is called. It is used, for example, to support the 
 * Mediations programming model. 
 * <p>
 * This class has no security implications.
 */
public interface DestinationConfiguration {

  /**
   * Returns the default priority used for messages sent to the destination.
   * @return the destination's default priority
   */
  public int getDefaultPriority();
  
  /**
   * Returns the name of the exception destination configured for the 
   * destination.
   * @return the destination's exception destination
   */
  public String getExceptionDestination();
  
  /**
   * Returns the name of the destination  
   * @return the name of the destination
   */
  public String getName();

  /**
   * Returns the unique identifier of the destination, as a String  
   * @return the desination's UUID
   */
  public String getUUID();
  
  /**
   * Returns the destination's description
   * @return the destination's description
   */
  public String getDescription();
  
  /**
   * Returns a Map of arbitrary configured properties, known as the "destination
   * context".  
   * @return the destination context
   */
  public Map getDestinationContext();
  
  /**
   * Returns the destination's type
   * @return the destination's type
   */
  public DestinationType getDestinationType();
  
  /**
   * Returns the destination's default reliability, that will be used when no
   * specific reliability is specified when a message is sent.
   * @return the destination's default reliability
   */
  public Reliability getDefaultReliability();
  
  /**
   * Returns the destination's default forward routing path.  
   * @return an array containing the destination's default forward routing path
   */
  public SIDestinationAddress[] getDefaultForwardRoutingPath();

  /**
   * Returns the maximum number of times delivery of any message will be 
   * attempted, before the message is disposed of in accordance with the 
   * configured exception-handling policy.
   * @return the maximum failed delivery attempts for any message
   */
  public int getMaxFailedDeliveries();
  
  /**
   * Returns the destination's maximum reliability - the highest reliability of
   * message that will be accepted on to the destination. 
   * @return the destination's maximum reliability
   */
  public Reliability getMaxReliability();
  
  /**
   * Returns the destination's reply destination.   
   * @return the destination's reply destination
   */
  public SIDestinationAddress getReplyDestination();

  /**
   * Returns true if producer's are permitted to override the default 
   * reliability of the destination. 
   * @return true if producer's can override the default reliability
   */
  public boolean isProducerQOSOverrideEnabled();
  
  /**
   * Returns true if the destination is configured to allow consumers to receive 
   * messages from it.  
   * @return true if receive is allowed
   */
  public boolean isReceiveAllowed();
  
  /**
   * Returns true if the destination is configured such that at most one 
   * consumer may be attached at any one time. 
   * @return true if exclusive receive is enabled
   */
  public boolean isReceiveExclusive();
  
  /**
   * Returns true if the destination is configured to allow producers to send
   * messages to it.  
   * @return true if send is allowed
   */
  public boolean isSendAllowed();
  
  /**
   * Returns true if the destination is configured to provide strict ordering
   * @return true if destination has strict ordering
   */
  public boolean isStrictOrderingRequired();
  
}
