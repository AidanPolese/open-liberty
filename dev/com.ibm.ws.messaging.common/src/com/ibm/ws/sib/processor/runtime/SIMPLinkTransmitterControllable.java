/*
 * 
 * 
 * ===========================================================================
 * IBM Confidential OCO Source Material
 *
 * Copyright IBM Corp. 2012
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 * ===========================================================================
 * 
 *
 * Change activity:
 *
 * Reason           Date   Origin   Description
 * ---------------  ------ -------- -------------------------------------------------
 * SIB0105.mp.1     071106 cwilkin  Original
 * 423911           210807 cwilkin  Throw SIMPControllableNotFound on some methods
 * ===========================================================================
 */
package com.ibm.ws.sib.processor.runtime;

import com.ibm.ws.sib.processor.exceptions.SIMPControllableNotFoundException;

public interface SIMPLinkTransmitterControllable extends 
  SIMPPtoPOutboundTransmitControllable {
  
  /**
   * Time since the last message was sent
   * @return 
   */
  public long getTimeSinceLastMessageSent()
    throws SIMPControllableNotFoundException;
  
  /**
   * Is this destination put disabled?
   * @return
   */
  public boolean isPutInhibited();
  
  /**
   * Get a string showing the type of the link, i.e. "SIB" or "MQ"
   * 
   * @return String The link Type
   */
  public String getLinkType();
  
  /**
   * Get the Uuid of the Link
   * @return String The Unique id of the Link
   */
  public String getLinkUuid();
  
  /**
   * Get the name of the link
   * @return The Name of the link
   */
  public String getLinkName();
  
  /**
   * Get the bus name where this link is transmitting to
   * @return The bus name
   */
  public String getTargetBusName();
  
  /**
   * Get the Messaging engine uuid where this link is targetted at
   * @return The uuid of the target messaging engine of the link
   */
  public String getTargetEngineUuid();

}
