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
 * 174700          030909 susana   Remove setControlMessageType from interface
 * 175492          030912 baldwint New fields for guaranteed delivery stream resolution
 * 178364          031107 susana   New/changed fields for remoteGet & remoteBrowse
 * 182771          031124 susana   Remove unnecessary methods
 * 181718.6        031219 susana   Remove SIBUuid import
 * 178364.2        031231 susana   Tidy up imports
 * 215177          040420 susana   Change Control Messages to single part messages
 * 185656          040902 susana   Tidy up imports etc
 * 348294          060816 susana   Fix encodeFast properly
 * 408810.1        061130 susana   Rename CommonMessageHeaders to AbstractMessage & common up more methods
 * 451831          070921 susana   Remove unused import
 * ============================================================================
 */
package com.ibm.ws.sib.mfp.control;

import com.ibm.websphere.sib.Reliability;
import com.ibm.ws.sib.mfp.AbstractMessage;

/**
 * ControlMessage is the basic interface for accessing and processing any
 * Message Processor Control Messages.
 * <p>
 * All of the Control  messages are specializations of ControlMessage which is
 * a separate top level message and not an extension of SIBusMessage.
 * The ControlMessage interface provides get/set methods for the common
 * Control message fields and extends AbstractMessage which provides
 * get/set methods for the fields common to Control messages and JsMessages.
 */
public interface ControlMessage extends AbstractMessage {

  /* **************************************************************************/
  /* Get Methods                                                              */
  /* **************************************************************************/

  /**
   * Get the value of the ControlMessageType from the  message.
   *
   * @return The ControlMessageType singleton which distinguishes
   *          the type of this message.
   */
  public ControlMessageType getControlMessageType();

  /**
   *  Get the value of the Reliability field from the message header.
   *
   *  @return The Reliability instance representing the Reliability of the
   *          message (i.e. Express, Reliable or Assured).
   *          Reliability.UNKNOWN is returned if the field is not set.
   */
  public Reliability getReliability();


  /* **************************************************************************/
  /* Set Methods                                                              */
  /* **************************************************************************/

  /**
   *  Set the value of the Priority field in the message header.
   *
   *  @param value An int containing the Priority of the message.
   *
   *  @exception IllegalArgumentException The value given is outside the
   *             permitted range.
   */
  public void setPriority(int value);

  /**
   *  Set the value of the Reliability field in the message header.
   *
   *  @return The Reliability instance representing the reliability of the
   *          message (i.e. Express, Reliable or Assured).
   *
   *  @exception NullPointerException Null is not a valid Reliability.
   */
  public void setReliability(Reliability value);


}
