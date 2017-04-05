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
 * 188046.4          110304 matrober Efficient dest encoding for msg
 * 197921.1          040504 jhumber  New Javadoc tags
 * 216223            160804 matrober BlockedDestination marking for Request/Reply
 * 225815            200804 matrober ibm-spi tags in internal files
 * 490670            300108 susana   Remove unused & non-public methods
 * 479100            020708 djvines  Provide different encoding levels
 * ============================================================================
 */
package com.ibm.ws.sib.api.jms;

import javax.jms.JMSException;

import com.ibm.websphere.sib.api.jms.JmsDestination;

/**
 * This interface provides utility methods that are used by the JMS implementation
 * to encode and decode a JmsDestination object to an efficient byte[] representation
 * for the purposes of transmission within the message.
 *
 * This class is specifically NOT tagged as ibm-spi because by definition it is not
 * intended for use by either customers or ISV's.
 *
 * @author matrober
 */
public interface MessageDestEncodingUtils
{

  /**
   * Returns the efficient byte[] representation of the parameter destination
   * that can be stored in the message for transmission. The boolean parameter
   * indicates whether a full (normal dest) or partial (reply dest) encoding
   * should be carried out.
   *
   * Throws a JMSException if there are problems during the serialization process, for
   *    example if the parameter is null.
   */
  public byte[] getMessageRepresentationFromDest(JmsDestination dest, EncodingLevel encodingLevel) throws JMSException;

  /**
   * Inflates the efficient byte[] representation from the message into a
   * JmsDestination object.
   *
   * Throws a JMSException if there are problems during the deserialization process, for
   *    example if the parameter is null.
   */
  public JmsDestination getDestinationFromMsgRepresentation(byte[] msgForm) throws JMSException;
}
