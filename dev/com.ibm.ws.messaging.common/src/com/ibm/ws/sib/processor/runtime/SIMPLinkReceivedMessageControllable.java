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
 * ===========================================================================
 */
package com.ibm.ws.sib.processor.runtime;

public interface SIMPLinkReceivedMessageControllable extends
    SIMPReceivedMessageControllable {

  /**
   * The name of the destination to which this message is being sent
   * @return Destination name
   */
  public String getTargetDestination();
  
  /**
   * The name of the bus to which this message is being sent
   * @return Bus Name
   */  
  public String getTargetBus();
}
