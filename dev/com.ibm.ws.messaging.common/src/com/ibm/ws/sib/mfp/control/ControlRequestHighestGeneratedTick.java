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
 * Reason           Date  Origin   Description
 * ---------------  ------ -------- -------------------------------------------------
 * 178364          061103 gatfora  Original
 * ===========================================================================
 */
package com.ibm.ws.sib.mfp.control;

/**
 * ControlRequestHighestGeneratedTick extends the general ControlMessage 
 * interface and provides get/set methods for the fields specific to a 
 * Control Request Highest Generated Tick Message.
 * 
 */
public interface ControlRequestHighestGeneratedTick extends ControlMessage {

  /* **************************************************************************/
  /* Get Methods                                                              */
  /* **************************************************************************/

  /**
   * Get the unique id for this request
   * 
   * @return A long containing the request ID 
   */
  public long getRequestID();
  
  /* **************************************************************************/
  /* Set Methods                                                              */
  /* **************************************************************************/

  /**
   * Set the unique id for this request
   * 
   * @param value A long containing the request ID 
   */ 
  public void setRequestID(long value);

}
