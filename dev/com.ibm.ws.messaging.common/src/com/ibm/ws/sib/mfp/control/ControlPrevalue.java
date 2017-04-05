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
 * 171889.1        030724 susana   More fields, and default booleans to false
 * 174111          030814 susana   Various method/field changes
 * 175492          030912 baldwint New fields for guaranteed delivery stream resolution
 * ============================================================================
 */
 
package com.ibm.ws.sib.mfp.control;

/**
 * ControlPrevalue extends the general ControlMessage interface and provides
 * get/set methods for the fields specific to a Control Prevalue Message.
 *
 */
public interface ControlPrevalue extends ControlMessage {

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
   *  Get the Value Tick value from the message.
   *
   *  @return A long containing the Value Tick value.
   */
  public long getValueTick();

  /**
   *  Get the CompletedPrefix from the message.
   *
   *  @return A long containing the CompletedPrefix.
   */
  public long getCompletedPrefix();

  /**
   *  Get the Force value from the message.
   *
   *  @return A boolean containing the value of Force.
   */
  public boolean getForce();

  /**
   *  Get the RequestedOnly value from the message.
   *
   *  @return A boolean containing the value of RequestedOnly.
   */
  public boolean getRequestedOnly();


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
   *  Set the Value Tick value in the message.
   *
   *  @param value A long containing the Value Tick value.
   */
  public void setValueTick(long value);

  /**
   *  Set the CompletedPrefix in the message.
   *
   *  @param value A long containing the CompletedPrefix.
   */
  public void setCompletedPrefix(long value);

  /**
   *  Set the Force field in the message.
   *
   *  @param value A boolean containing the value of Force.
   */
  public void setForce(boolean value);

  /**
   *  Set the RequestedOnly value in the message.
   *
   *  @param value A boolean containing the value of RequestedOnly.
   */
  public void setRequestedOnly(boolean value);
}
