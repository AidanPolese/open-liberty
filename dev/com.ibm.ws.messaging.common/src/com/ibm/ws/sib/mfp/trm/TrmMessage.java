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
 * 174699          030820 vaughton Original
 * ============================================================================
 */

package com.ibm.ws.sib.mfp.trm;

import com.ibm.ws.sib.mfp.JsMessage;

/**
 * TrmMessage is the basic interface for accessing and processing any
 * Topology Routing and Management control Messages.
 * <p>
 * All of the Trm messages are specializations of TrmMessage which is in turn
 * a specialization of JsMessage. All TrmMessages can be made from a JsMessage
 * of the appropriate type.
 */

public interface TrmMessage extends JsMessage {

  /* **************************************************************************/
  /* Get Methods                                                              */
  /* **************************************************************************/

  /**
   *  Get the value of the TrmMessageType from the  message.
   *
   *  @return The TrmMessageType singleton which distinguishes
   *          the type of this message.
   */
  public TrmMessageType getTrmMessageType();

  /**
   *  Get the Magic Number from the message.
   *
   *  @return A long containing the Magic Number.
   */
  public long getMagicNumber();

  /* **************************************************************************/
  /* Set Methods                                                              */
  /* **************************************************************************/

  /**
   *  Set the Magic Number field in the message.
   *
   *  @param value  An long containing the Magic Number.
   */
  public void setMagicNumber(long value);

}
