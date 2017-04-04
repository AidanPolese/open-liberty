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
 * 178364          031106 gatfora  Original
 * 186848.19.1     040630 baldwint Add get/setIndoubtDiscard
 * ============================================================================
 */
package com.ibm.ws.sib.mfp.control;

/**
 * ControlRequestFlush extends the general ControlMessage interface and provides
 * get/set methods for the fields specific to a Control Request Flushed Message.
 */
public interface ControlRequestFlush extends ControlMessage {
  
  /* **************************************************************************/
  /* Get Methods                                                              */
  /* **************************************************************************/

  /**
   * Get the unique id for this request
   * 
   * @return A long containing the request ID 
   */
  public long getRequestID();
  
  /**
   * Get the flag to indicate how to handle in doubt messages
   *
   * @return A boolean containing the in doubt handling flag
   */
  public boolean getIndoubtDiscard();

  /* **************************************************************************/
  /* Set Methods                                                              */
  /* **************************************************************************/

  /**
   * Set the unique id for this request
   * 
   * @param value A long containing the request ID 
   */ 
  
  public void setRequestID(long value);
  
  /**
   * Set the flag to indicate how to handle in doubt messages
   *
   * @param value A boolean containing the in doubt handling flag
   */
  public void setIndoubtDiscard(boolean value);
}
