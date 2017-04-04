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
 * Reason          Date   Origin   Description
 * --------------- ------ -------- --------------------------------------------
 * 169263          030612 susana   Original
 * 177061          030918 baldwint Message Processor required changes
 * 195928          040413 baldwint Add Request/Reply type & delete redundant fields
 * 576229          090220 susana   Add typing to List parameters and return values
 * ============================================================================
 */

package com.ibm.ws.sib.mfp.control;

import java.util.List;

import com.ibm.ws.sib.mfp.JsMessage;

/**
 * SubscriptionMessage is the interface for accessing and processing any
 * Subscription Propagation Messages.
 * <p>
 * SubscriptionMessage which is a specialization of JsMessage and can be made
 * from a JsMessage of the appropriate type.
 * The SubscriptionMessage interface provides get/set methods for all the
 * message fields.
 *
 */
public interface SubscriptionMessage extends JsMessage {

  /* **************************************************************************/
  /* Get Methods                                                              */
  /* **************************************************************************/

  /**
   *  Get the value of the SubscriptionMessageType from the  message.
   *
   *  @return The SubscriptionMessageType singleton which distinguishes
   *          the type of this message.
   */
  public SubscriptionMessageType getSubscriptionMessageType();

  /**
   *  Get the List of Topics from the message.
   *
   *  @return A List of String Topics.
   *          Null is returned if there are no Topics.
   */
  public List<String> getTopics();

  /**
   *  Get the List of TopicSpaces from the message.
   * 
   *  @return A List of String TopicSpaces.
   *          Null is returned if there are no TopicSpaces.
   */
  public List<String> getTopicSpaces();

  /**
   * Get a list of TopicSpace mappings from the message.
   *
   * @return A List of String TopicSpace mappings.
   *          Null is returned if there are no TopicSpace mappings.
   */
  public List<String> getTopicSpaceMappings();

  /**
   *  Get the ME Name from the message.
   *
   *  @return A String containing the ME Name.
   *          Null is returned if there is no ME Name.
   */
  public String getMEName();

  /**
   *  Get the byte array containing the ME UUID from the message.
   *
   *  @return A byte[] containing the ME UUID.
   *          Null is returned if there is no ME UUID.
   */
  public byte[] getMEUUID();

  /**
   * Get the Bus name for this Neighbour
   *
   * @return String containing the Bus name
   */
  public String getBusName();


  /* **************************************************************************/
  /* Set Methods                                                              */
  /* **************************************************************************/

  /**
   *  Set the value of the SubscriptionMessageType field in the message.
   *
   *  @param value The SubscriptionMessageType instance representing the type
   *         of this message.
   */
  public void setSubscriptionMessageType(SubscriptionMessageType value);

  /**
   *  Set the List of Topics in the message.
   *
   *  @param value A List of String Topics.
   */
  public void setTopics(List<String> value);

  /**
   *  Set the List of TopicSpaces in the message.
   *
   *  @param value A List of String TopicSpaces.
   */
  public void setTopicSpaces(List<String> value);

  /**
   *  Set the List of TopicSpace mappings in the message.
   *
   *  @param value A List of String TopicSpace mappings.
   */
  public void setTopicSpaceMappings(List<String> value);

  /**
   *  Set the ME Name in the message.
   *
   *  @param value The String ME Name.
   */
  public void setMEName(String value);

  /**
   *  Set the byte array ME UUID in the message.
   *
   *  @param value A byte array containing the ME UUID.
   */
  public void setMEUUID(byte[] value);

  /**
   * Set the Bus namefor this message
   *
   * @param value String containing the Bus name
   */
  public void setBusName(String value);
}
