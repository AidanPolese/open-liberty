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
 * SIB0105.mp.2     071106 cwilkin  Original
 * SIB0105.mp.7     250607 cwilkin  Link Publication Point Controls
 * ===========================================================================
 */
package com.ibm.ws.sib.processor.runtime;

public interface SIMPLinkRemoteMessagePointControllable {

  /**
   * Get a single delivery stream set. This exists if we are sending
   * or have sent messages to a remote queue. 
   * 
   * @return The delivery stream or null if it is non existent. 
   */
  public SIMPLinkTransmitterControllable getOutboundTransmit();
  
  /**
   * Is this link transmitter for a topicpsace
   */
  public boolean isPublicationTransmitter();
  
  /**
   * Get the topicspace this publication transmitter is targetting
   * @return the name of the target is isPublicationTransmitter returns true
   * if false - return null
   */
  public String getTargetDestination();
  
}
