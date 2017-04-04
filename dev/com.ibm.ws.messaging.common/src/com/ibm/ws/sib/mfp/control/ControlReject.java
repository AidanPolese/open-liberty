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
 * 171889          030722 susana   Original
 * 174111          030814 susana   Change method/field names to be less daft
 * 178364          031107 susana   New/changed fields for remoteGet & remoteBrowse
 * 178364.2        031231 susana   Tidy up javadoc
 * 488794.1        080320 susana   Add get/setRMEUnlockCount
 * 488794.3        080605 djvines  Change get/setRMEUnlockCount to long[]
 * ============================================================================
 */
package com.ibm.ws.sib.mfp.control;

/**
 * ControlReject extends the general ControlMessage interface and provides
 * get/set methods for the fields specific to a Control Reject Message.
 *
 */
public interface ControlReject extends ControlMessage {

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

  /**
   * Get the recovery value for this request.
   *
   * @return a boolean true if these ticks were turned to rejected
   * state during crash recovery.
   */
  public boolean getRecovery();

  /**
   * Get the RMEUnlockCountValue for this request.
   *
   * @return A long[] which are the RMEUnlockCount's.
   *         NB: The value will always be a 0-length array if the message arrived from a pre-WAS7 ME.
   */
  public long[] getRMEUnlockCount();

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

  /**
   * Set the Recovery value for this request
   *
   * @param value A boolean containing the recovery value
   */
  public void setRecovery(boolean value);

  /**
   * Set the RMEUnlockCountValue for this request.
   *
   * @param value A long[] containing the RMEUnlockCountValue's
   */
  public void setRMEUnlockCount(long[] value);

}
