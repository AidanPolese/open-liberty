/*
 * 
 * 
 * ============================================================================
 * IBM Confidential OCO Source Material
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
 * 174369.2          110903 matrober Interfaces for JMS message and subscription name creation
 * 181796.5          051103 matrober Core SPI move to com.ibm.wsspi.sib.core
 * 197921.1          040504 jhumber  New Javadoc tags
 * 215550            120704 kingdon  Remove unnecessary public interfaces
 * 225815            200804 matrober ibm-spi tags in internal files
 * 255355.1          210205 kingdon  MQMD.Feedback support. Provide methods to convert between
 *                                     Jetstream and MQ values of feedback.
 * SIB0121.jms.2     170107 jamessid Supporting performance enhancements for large objects
 * ============================================================================
 */
package com.ibm.ws.sib.api.jms;

import java.util.Map;

import javax.jms.JMSException;

import com.ibm.ws.sib.api.jms.JmsSession;
import com.ibm.wsspi.sib.core.SIBusMessage;

/**
 * This interface provides access for the JCA team to some functionality that must
 * be used by both the JMS and JCA implementations.
 * 
 * This class is specifically NOT tagged as ibm-spi because by definition it is not
 * intended for use by either customers or ISV's.
 */
public interface JmsSharedUtils
{
  
  /**
   * Returns the concatenated form of the JMS clientID and subName to
   * be used as the subscriptionName parameter for the core API
   * createDurableSubscription method.<p>
   * 
   * Note that neither of these parameters should be null, however there
   * is no checking in this method to prevent this.
   * 
   * <!-- Javadoc'd: matrober 110903 -->
   * 
   * @param jmsClientID The clientID in effect for this Connection
   * @param jmsSubName The subscription name for this durable subscription
   */
  public String getCoreDurableSubName(String jmsClientID,
                                      String jmsSubName);
                                      
      
  /**
   * This method converts from an SIBusMessage received from the core API
   * to a JMS message that can be returned to user applications.<p>
   * 
   * If the sibMsg parameter passed in is null then a null will be returned
   * from this method.
   * 
   * The JmsSession parameter may be null if the application is not allowed
   * to call msg.acknowledge() on the object that is returned. It must be
   * supplied if the application is allowed to call acknowledge. Note that
   * this parameter is actually required to be an instance of JmsSessionImpl
   * however this cannot be exposed as it forms part of the implementation
   * component. 
   * 
   * <!-- Javadoc'd: matrober 110903 -->
   * 
   * @param sibMsg The message received from the core API.
   * @param theSession The JMS Session that this message is received under.
   * @param passThruProps Session-like properties to associate with the JMS message 
   * @throws JMSException If the user name specified is violates any conditions
   * (reserved for later use).
   */                                    
  public javax.jms.Message inboundMessagePath(SIBusMessage sibMsg,
                                              JmsSession theSession,
                                              Map passThruProps)
                                                throws JMSException;

  /**
   * Convert from Jetstream feedback values to MQ values.
   * 
   * @param fb A Jetstream feedback value
   * @return The equivalent MQ value
   */
  public Integer convertJSFeedbackToMQ(int fb);
  /**
   * Convert from Jetstream feedback values to MQ values.
   * 
   * @param fb A Jetstream feedback value
   * @return The equivalent MQ value
   */
  public Integer convertJSFeedbackToMQ(Integer fb);
  /**
   * Convert from MQ feedback values to Jetstream values.
   * 
   * @param fb An MQ feedback value
   * @return The equivalent Jetstream value
   */
  public Integer convertMQFeedbackToJS(int fb);
  /**
   * Convert from MQ feedback values to Jetstream values.
   * 
   * @param fb An MQ feedback value
   * @return The equivalent Jetstream value
   */
  public Integer convertMQFeedbackToJS(Integer fb);
                                      
                                     

}
