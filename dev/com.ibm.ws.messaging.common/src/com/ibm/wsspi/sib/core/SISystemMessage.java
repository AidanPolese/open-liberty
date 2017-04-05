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
 * 169602          030811 susana   Original
 * 172633.5        031029 susana   Change package name to ....wsspi...
 * 172633.5.0      031030 susana   Change ...extensions... to ...spi...
 * 179760          031104 susana   Change package name for Core SPI MFP classes
 * 245265          041217 susana   Reorganise com.ibm.wsspi.mfp.spi out of existence
 * ============================================================================
 */
package com.ibm.wsspi.sib.core;

import com.ibm.websphere.sib.SIMessage;

/**
 * The SISystemMessage interface is the SPI interface to an SIBus message for
 * use by the Mediation Framework as well as other SIBus components.
 * Thiis interfaces allows an SIBus Core SPI user to obtain an SIBusMessage to
 * send to the Bus.
 */
public interface SISystemMessage extends SIMessage {

  /* **************************************************************************/
  /* Method for obtaining the SIBusMessage which represents the same message. */
  /* **************************************************************************/

  /**
   * Obtain the SIBusMessage which represents this message.
   *
   * @return SIBusMessage The corresponding SIBusMessage.
   */
  public SIBusMessage toSIBusMessage();


}
