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
 * Reason            Date   Origin   Description
 * ---------------   ------ -------- ------------------------------------------
 *                          matrober Original (Created on 17-Feb-03)
 * 163425            220503 matrober Null & empty str for name and JsDestName
 * 170067            200603 matrober Refactor JMS interfaces (com.ibm.websphere)
 * 170747            290603 amardeep get/setJSDestDiscrim add, get/setName del
 * 172574            250703 amardeep get/set prod, cons, browse mediations
 * 170807.5          310703 amardeep get/set properties revised
 * 174328            150803 matrober Caching for destination.encode (Perf)
 * 174896            220803 matrober JavaDoc public interfaces
 * 175799            040903 matrober Rename jsDestDiscrim/Name to DestDiscim/Name
 * 176611            160903 kingdon  Add support for ttl,pri,delivMode overrides.
 * 166829.6.2        180903 matrober JMS Durable Subs (readAhead and subscriptionHome)
 * 177912            290903 kingdon  Initial work to reduce the visibility of get/set
 *                                   destName & destDescrim.
 * 177912.1          031003 kingdon  Drop the vibility of destName & destDescrim to package.
 * 179498            151003 kingdon  Impose max value for timeToLive.
 * 179339.7          120104 matrober Forward and reverse routing paths
 * 179339.7.1        210104 matrober Forward/Reverse routing paths (part 2)
 * 186967.2          220104 jhumber  JMS support for MQLink in Milestone 6
 * 184390.7          170204 kingdon  deprecate the npm methods
 * 184904.1          150304 matrober Remove references to Producer/Consumer Mediations for JS1
 * 186967.5          080404 jhumber  API support for Inter-Bus support
 * 190809            190404 kingdon  Remove deprecated non-persistent mapping methods
 * 200218            270404 jhumber  Remove M6RoutingData property
 * 197921.1          040504 jhumber  New Javadoc tags
 * 228338            020904 matrober Javadoc in this file is causing error on Windows IBASE build
 * 338734            180105 holdeni  Fix JavaDoc warnings
 * 497304            150208 susana   Remove commented out methods
 * ============================================================================
 */

package com.ibm.websphere.sib.api.jms;

import java.io.Serializable;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.naming.Referenceable;

/**
 * Contains provider specific methods relating to the javax.jms.Destination interface.
 *
 * @ibm-api
 * @ibm-was-base
 */
public interface JmsDestination extends Destination, Serializable, Referenceable
{

  /**
   * Retrieves the name of the underlying destination to which this
   * javax.jms.Destination refers.<p>
   *
   * <!-- Javadoc'd: matrober 040903 -->
   *
   * @return The name of the underlying destination.
   */
  public String getDestName();

  /**
   * Retrieves the discriminator associated with this Destination.<p>
   *
   * Note that for Queue objects the returned value will always be null, while for
   * Topics it represents the name of the topic within the topic space.
   *
   * <!-- Javadoc'd: matrober 220803 -->
   *
   * @return The discriminator for this Destination.
   */
  public String getDestDiscrim();

  /**
   * Set the deliveryMode.<p>
   * The deliveryMode for sending messages may be overridden with this property.
   * Valid values are:
   * <table border="1">
   * <TR><TD>Value        <TD>Constant name in ApiJmsConstants<TD>Meaning
   * <TR><TD>Application  <TD>DELIVERY_MODE_APP               <TD>The deliveryMode is determined by the application (default)
   * <TR><TD>Persistent   <TD>DELIVERY_MODE_PERSISTENT        <TD>All messages will be sent persistent, irrespective of any
   *                                                              settings in the application.
   * <TR><TD>NonPersistent<TD>DELIVERY_MODE_NONPERSISTENT     <TD>All messages will be sent non-persistent, irrespective of
   *                                                              any settings in the application.
   * </table>
   * @param deliveryMode the deliveryMode to be used by MessageProducers of
   * this Destination.
   * @throws JMSException if the String is not one of the predefined values.
   * @see javax.jms.MessageProducer
   */
  public void setDeliveryMode(String deliveryMode) throws JMSException;

  /**
   * Get the deliveryMode.<p>
   * @see JmsDestination#setDeliveryMode
   * @return a String representing the deliveryMode.
   */
  public String getDeliveryMode();

  /**
   * Set the timeToLive (in milliseconds) to be used for all messages sent
   * using this destination.<p>
   *
   * A value of 0 means that the message will never expire. The default for
   * this property is null, which allows the application to determine the
   * timeToLive.<p>
   *
   * For compatibility with MQJMS, the value of -2 is treated in the same
   * way as null.<p>
   *
   * The maximum value that will be accepted for timeToLive is defined in
   * ApiJmsConstants.MAX_TIME_TO_LIVE.
   *
   * @param timeToLive The time in milliseconds that the message should
   *           live before expiry.
   *
   * @see ApiJmsConstants#MAX_TIME_TO_LIVE
   * @throws JMSException if the value provided is not valid.
   */
  public void setTimeToLive(Long timeToLive) throws JMSException;

  /**
   * Get the timeToLive that will be used for all messages sent using
   * this destination.<p>
   *
   * @see JmsDestination#setTimeToLive
   * @return Long The timeToLive for message sent using this destination.
   */
  public Long getTimeToLive();

  /**
   * Set the priority to be used for all messages sent using this Destination.<p>
   *
   * The valid parameters to this method are integers 0 to 9 inclusive, which will
   * be used as the priority for messages sent using this destination.<p>
   *
   * The default value for this property is null, which indicates that the
   * priority of the message will be set by the application.<p>
   *
   * For compatibility with MQJMS, the value of -2 will be treated in the same
   * way as null.
   *
   * @param priority The priority to be used for messages sent using this Destination.
   * @throws JMSException If the value provided is not valid.
   */
  public void setPriority(Integer priority) throws JMSException;

  /**
   * Get the priority.
   * @return the priority
   * @see JmsDestination#setPriority
   */
  public Integer getPriority();

  /**
   * Set the required value for ReadAhead on all consumers created using
   * this JmsDestination.<p>
   *
   * Please see {@link JmsConnectionFactory#setReadAhead(String)} for information
   * on the effect of the ReadAhead property.<br><br>
   *
   * Permitted values for the ReadAhead property of a JmsDestination are
   * as follows;
   *
   * <ul>
   * <li>{@link ApiJmsConstants#READ_AHEAD_AS_CONNECTION} - The default behaviour,
   *     where the value is inherited from the value set on the JmsConnectionFactory
   *     at the time that the Connection was created.
   * <li>{@link ApiJmsConstants#READ_AHEAD_ON} - All consumers created using
   *     this JmsDestination will have ReadAhead turned on.
   * <li>{@link ApiJmsConstants#READ_AHEAD_OFF} - All consumers created using
   *     this JmsDestination will have ReadAhead turned off.
   * </ul>
   * <br><br>
   *
   * Note that the value specified will override the value specified on the
   * JmsConnectionFactory if the AS_CONNECTION value is not specified.
   *
   * @param value The required value for ReadAhead on this JmsDestination
   * @throws JMSException If the value specified is not one of the supported constants.
   *
   * @see ApiJmsConstants#READ_AHEAD_AS_CONNECTION
   * @see ApiJmsConstants#READ_AHEAD_ON
   * @see ApiJmsConstants#READ_AHEAD_OFF
   * @see JmsConnectionFactory#setReadAhead(String)
   */
  public void setReadAhead(String value) throws JMSException;

  /**
   * Retrieve the current setting for the ReadAhead property for this
   * JmsDestination.<p>
   *
   * @return The current setting for ReadAhead.
   */
  public String getReadAhead();

  /**
   *  Get the name of the bus on which this JMS Destination resides.
   *
   *  @return String The name of the Bus.
   */
  public String getBusName();

  /**
   *  Set the name of the bus on which this JMS Destination resides.
   *  Setting this property defines the name of the bus on which the
   *  Destination is hosted. This enables applications to send messages to
   *  Destinations outside the local bus - for example remote MQ networks.
   *
   *  By default this property is set to null, indicating that the
   *  Destination resides on the local bus. The setting of this property is
   *  optional, and the value of empty String is taken to be equivalent
   *  to null.
   *
   *  @throws JMSException
   */
  public void setBusName(String busName)throws JMSException;

}
