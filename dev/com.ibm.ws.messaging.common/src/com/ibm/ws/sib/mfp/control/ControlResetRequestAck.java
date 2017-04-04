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
 * ControlResetRequestAck extends the general ControlMessage 
 * interface and provides get/set methods for the fields specific to a 
 * Control Reset Request Ack Message.
 */
public interface ControlResetRequestAck extends ControlMessage {

  /* **************************************************************************/
  /* Get Methods                                                              */
  /* **************************************************************************/

  /**
   * Get the DME version for this request
   *
   * @return A long containing the DME version
   */
  public long getDMEVersion();
    
  /* **************************************************************************/
  /* Set Methods                                                              */
  /* **************************************************************************/

  /**
   * Set the DME version for this request
   * 
   * @param value A long containing the DME version
   */ 
  public void setDMEVersion(long value);
}
