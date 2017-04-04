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
 * Reason   Date   Origin   Description
 * -------- ------ -------- --------------------------------------------------
 * 205894   040528 baldwint Add encode/decode support for web client
 * ============================================================================
 */

package com.ibm.ws.sib.mfp;

public interface WebJsMessageEncoder {
  /**
   * Encode a JsJmsMessage into the simple text format supported by the
   * Web client.  Only JMS messages can be encoded this way.
   *
   * @return A String containing the simple message encoding
   * @exception MessageEncodeFailedException if the message could not be encoded
   */
  public String encodeForWebClient() throws MessageEncodeFailedException;
}
