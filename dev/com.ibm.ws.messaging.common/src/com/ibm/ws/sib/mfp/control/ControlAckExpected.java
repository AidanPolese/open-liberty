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
 * 174111          030814 susana   Change method/field names to be less daft
 * ============================================================================
 */
package com.ibm.ws.sib.mfp.control;

/**
 * ControlAckExpected extends the general ControlMessage interface and provides
 * get/set methods for the fields specific to a Control AckExpected Message.
 *
 */
public interface ControlAckExpected extends ControlMessage {

  /* **************************************************************************/
  /* Get Methods                                                              */
  /* **************************************************************************/

  /**
   *  Get the Tick value from the message.
   *
   *  @return A long containing the Tick value.
   */
  public long getTick();


  /* **************************************************************************/
  /* Set Methods                                                              */
  /* **************************************************************************/

  /**
   *  Set the Tick value in the message.
   *
   *  @param value A long containing the Tick value.
   */
  public void setTick(long value);

}
