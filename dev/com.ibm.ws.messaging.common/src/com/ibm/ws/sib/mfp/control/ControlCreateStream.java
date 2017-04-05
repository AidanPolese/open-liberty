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
 * Reason           Date  Origin   Description
 * ---------------  ------ -------- -------------------------------------------
 * 178364           061103 gatfora  Original
 * 187000.4         040310 baldwint New remote durable message types
 * 196309.1         040330 baldwint More remote durable changes
 * 207007.3         040610 baldwint Add selectorDomain attribute
 * 215177           040825 susana   Add get/setSecurityUserid
 * 231523           040915 susana   Add securityUseridSentBySystem
 * 259036.1         050315 kgoodson Add isLocal and isCloned
 * ============================================================================
 */
package com.ibm.ws.sib.mfp.control;

/**
 * ControlCreateStream extends the general ControlMessage interface and provides
 * get/set methods for the fields specific to a Control Create Stream.
 */
public interface ControlCreateStream extends ControlMessage {

  /* **************************************************************************/
  /* Get Methods                                                              */
  /* **************************************************************************/

  /**
   * Get the request ID for this request
   *
   * @return A long containing the request ID
   */
  public long getRequestID();

  /**
   * If the stream will be created for a durable subscription, then
   * get the subscription name which will be used as the name of the
   * destination used for remote access to the durable subscription.
   *
   * @return A subscription name of the form client##name
   */
  public String getDurableSubName();

  /**
   * Get the topic discriminator for the subscription to create.
   *
   * @return A topic discriminator.
   */
  public String getDurableDiscriminator();

  /**
   * Get the selector for the subscription to create.
   *
   * @return A subscription selector.
   */
  public String getDurableSelector();

  /**
   * Get the selector domain for the subscription to create.
   *
   * @return A selector domain value
   */
  public int getDurableSelectorDomain();

  /**
   *  Indicate whether the message was sent by a system user.
   *
   *  @return A boolean indicating whether the message was sent by a system user
   */
  public boolean isSecurityUseridSentBySystem();

  /**
    * Get the noLocal flag value.
    * This indicates that consumers on this durable subscription
    * should not receive messages that have been produced on the same connection
    * as the consumer.
    */
   public boolean isNoLocal();

   /**
    * Get the cloned flag.
    * This indicates that the durable susbcription has been cloned and so
    * can be shared by more than one ME on the bus.
    */
   public boolean isCloned();

  /**
   *  Get the contents of the SecurityUserid field for the subscription.
   *
   *  @return A String containing the SecurityUserid name.
   */
  public String getSecurityUserid();

  /* **************************************************************************/
  /* Set Methods                                                              */
  /* **************************************************************************/

  /**
   * Set the request ID for this request
   *
   * @param value A long containing the request ID
   */
  public void setRequestID(long value);

  /**
   * If the stream will be created for a durable subscription, then set
   * the subscription name to be used for the remote access destination.
   *
   * @param name A subscription name of the form client##name
   */
  public void setDurableSubName(String name);

  /**
   * Set the topic discriminator for the subscription to create.
   *
   * @return The topic discriminator for the new subscription.
   */
  public void setDurableDiscriminator(String discriminator);

  /**
   * Set the selector for the subscription to create.
   *
   * @param selector The selector for the new subscription.
   */
  public void setDurableSelector(String selector);

  /**
   * Set the selector domain for the subscription to create.
   *
   * @param domain The selector domain value
   */
  public void setDurableSelectorDomain(int domain);

  /**
   *  Set whether the message was sent by a system user.
   *
   *  @param value A boolean indicating whether the message was sent by a system user
   */
  public void setSecurityUseridSentBySystem(boolean value);

  /**
   *  Set the contents of the SecurityUserid field for the subscription.
   *
   *  @param value A String containing the SecurityUserid name.
   */
  public void setSecurityUserid(String value);
  
  /**
   * Set the noLocal flag.
   * This indicates that consumers on this durable subscription
   * should not receive messages that have been produced on the same connection
   * as the consumer.
   */ 
  public void setNoLocal(boolean value);

  /**
   * Set the cloned flag
   * This indicates that the durable susbcription has been cloned and so
   * can be shared by more than one ME on the bus.
   */
  public void setCloned(boolean value);

}
