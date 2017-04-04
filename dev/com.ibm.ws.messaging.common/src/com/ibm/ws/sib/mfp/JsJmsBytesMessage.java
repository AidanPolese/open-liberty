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
 * 158444          030207 susana   Original
 * ============================================================================
 */
package com.ibm.ws.sib.mfp;

/**
 *  JsJmsBytesMessage extends JsJmsMessage and adds the get/set methods specific
 *  to a JMS BytesMessage.
 */
public interface JsJmsBytesMessage extends JsJmsMessage {

  /**
   *  Get the body (payload) of the message.
   *
   *  @return The byte array representing the body of the message.
   */
  public byte[] getBytes();

  /**
   *  Set the body (payload) of the message.
   *  The act of setting the payload will cause a copy of the byte array to be
   *  made, in order to ensure that the payload sent matches the byte array
   *  passed in. If no copy was made it would be possible for the content to
   *  changed before the message was transmitted or delivered.
   *
   *  @param payload  The byte arraycontaining the payload to be included in the message.
   */
  public void setBytes(byte[] payload);

}
