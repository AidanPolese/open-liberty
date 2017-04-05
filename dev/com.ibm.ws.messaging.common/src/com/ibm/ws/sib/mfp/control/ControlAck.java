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
 * 171889          030722 susana   Original
 * ============================================================================
 */
package com.ibm.ws.sib.mfp.control;

/**
 * ControlAck the general ControlMessage interface and provides
 * get/set methods for the fields specific to a Control Ack Message.
 *
 */
public interface ControlAck extends ControlMessage {

  /* **************************************************************************/
  /* Get Methods                                                              */
  /* **************************************************************************/

  /**
   *  Get the Ack Prefix from the message.
   *
   *  @return A long containing the Ack Prefix.
   */
  public long getAckPrefix();


  /* **************************************************************************/
  /* Set Methods                                                              */
  /* **************************************************************************/

  /**
   *  Set the Ack Prefix in the message.
   *
   *  @param value A long containing the Ack Prefix.
   */
  public void setAckPrefix(long value);

}
