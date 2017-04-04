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
 * Reason    Date   Origin   Description
 * -------   ------ -------- --------------------------------------------------
 * 187000.4  040310 baldwint New remote durable message types
 * ============================================================================
 */
package com.ibm.ws.sib.mfp.control;

/**
 * ControlDurableConfirm extends the general ControlMessage interface and provides
 * get/set methods for the fields specific to a Control Durable Confirm.
 */
public interface ControlDurableConfirm extends ControlMessage {

  /* **************************************************************************/
  /* Get Methods                                                              */
  /* **************************************************************************/

  /**
   * Get the request ID for this request
   *
   * @return A long containing the request ID
   */
  public long getRequestID();

  /**
   * Get the status for this request.
   *
   * @return An int status code.
   */
  public int getStatus();

  /* **************************************************************************/
  /* Set Methods                                                              */
  /* **************************************************************************/

  /**
   * Set the request ID for this request
   *
   * @param value A long containing the request ID
   */
  public void setRequestID(long value);

  /**
   * Set the status for this request.
   *
   * @param status An int status code.
   */
  public void setStatus(int status);
}
