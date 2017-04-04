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
 * 175492          030912 baldwint New fields for guaranteed delivery stream resolution
 * ============================================================================
 */
 
package com.ibm.ws.sib.mfp.control;

/**
 * ControlDecision extends the general ControlMessage interface and provides
 * get/set methods for the fields specific to a Control Decision Message.
 *
 */
public interface ControlDecision extends ControlMessage {

  /* **************************************************************************/
  /* Get Methods                                                              */
  /* **************************************************************************/

  /**
   *  Get the Start Tick value from the message.
   *
   *  @return A long containing the Start Tick value.
   */
  public long getStartTick();

  /**
   *  Get the End Tick value from the message.
   *
   *  @return A long containing the End Tick value.
   */
  public long getEndTick();

  /**
   *  Get the CompletedPrefix from the message.
   *
   *  @return A long containing the CompletedPrefix.
   */
  public long getCompletedPrefix();


  /* **************************************************************************/
  /* Set Methods                                                              */
  /* **************************************************************************/

  /**
   *  Set the Start Tick value in the message.
   *
   *  @param value A long containing the Start Tick value.
   */
  public void setStartTick(long value);

  /**
   *  Set the End Tick value in the message.
   *
   *  @param value A long containing the End Tick value.
   */
  public void setEndTick(long value);

  /**
   *  Set the CompletedPrefix in the message.
   *
   *  @param value A long containing the CompletedPrefix.
   */
  public void setCompletedPrefix(long value);
}
