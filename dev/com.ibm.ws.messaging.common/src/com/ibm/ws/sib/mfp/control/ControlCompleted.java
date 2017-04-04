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
 * 178364.2        031231 susana   Tidy up javadoc
 * ===========================================================================
 */
package com.ibm.ws.sib.mfp.control;

/**
 * ControlCompleted extends the general ControlMessage interface and provides
 * get/set methods for the fields specific to a Control Completed Message.
 */
public interface ControlCompleted extends ControlMessage {

  /* **************************************************************************/
  /* Get Methods                                                              */
  /* **************************************************************************/

  /**
   * Get the Start Tick value for this request.
   *
   * @return A long[] containing the Tick values
   */
  public long[] getStartTick();

  /**
   * Get the End Tick value for this request.
   *
   * @return A long[] containing the End Tick values
   */
  public long[] getEndTick();

  /* **************************************************************************/
  /* Set Methods                                                              */
  /* **************************************************************************/

  /**
   * Set the Start Tick values for this request.
   *
   * @param values A long[] containing the Start Tick values
   */
  public void setStartTick(long[] values);

  /**
   * Set the End Tick values for this request.
   *
   * @param values A long[] containing the End Tick values
   */
  public void setEndTick(long[] values);

}
