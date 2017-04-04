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
 * 252277.2        060110 susana   Percolate any UnsupportedEncodingException back to the caller
 * 395685.1        070517 susana   setText no longer throws UnsupportedEncodingException
 * ============================================================================
 */
package com.ibm.ws.sib.mfp;

import java.io.UnsupportedEncodingException;

/**
 *  JsJmsTextMessage extends JsJmsMessage and adds the get/set methods specific
 *  to a JMS TextMessage.
 */
public interface JsJmsTextMessage extends JsJmsMessage {

  /**
   *  Get the body (payload) of the message.
   *
   *  @return The String representing the body of the message.
   *
   *  @exception UnsupportedEncodingException is thrown if the payload is encoded
   *             in a codepage which is not supported on this system.
   */
  public String getText() throws UnsupportedEncodingException;

  /**
   *  Set the body (payload) of the message.
   *
   *  @param payload  The String containing the payload to be included in the message.
   */
  public void setText(String payload);

}
