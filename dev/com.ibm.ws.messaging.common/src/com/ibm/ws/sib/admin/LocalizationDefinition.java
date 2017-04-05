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
 * 186445.1        140103 philip   Original implementation; logical replacement
 *                                 of previous MP implementation.
 * 168499.21       030204 philip   Add clone() method
 * 168499.27       280304 philip   Add getIdentifier() method
 * 193906.2        270504 leonarda Change get/putInhibted to receive/sendAllowed
 * 196675.1.7      300504 philip   Add getUuid/Name/ConfigId methods
 * 195809.3        110604 philip   New Queue Depth Limits support
 * 212680.1        270704 philip   Removal of receiveAllowed
 * 223384          131004 philip   Remove get/setMaxMsgs
 * 234931          141004 philip   Remove deprecated methods
 * SIB0002.adm.4   220605 tpm      PEV destination support
 * ============================================================================
 */

package com.ibm.ws.sib.admin;

/**
 * Defines a message point that is hosted on platform messaging
 */
public interface LocalizationDefinition extends BaseLocalizationDefinition {


  /**
   * @return
   */
  public long getDestinationHighMsgs();

  /**
   * @param value
   */
  public void setDestinationHighMsgs(long value);

  /**
   * @return
   */
  public long getDestinationLowMsgs();

  /**
   * @param value
   */
  public void setDestinationLowMsgs(long value);

  /**
   * @return
   */
  public boolean isSendAllowed();

  /**
   * @param arg
   */
  public void setSendAllowed(boolean arg);



}
