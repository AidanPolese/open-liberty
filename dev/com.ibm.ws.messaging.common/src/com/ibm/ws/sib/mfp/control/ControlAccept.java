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
 * 178364          031107 gatfora  New/changed fields for remoteGet & remoteBrowse
 * 178364.2        031231 susana   Tidy up javadoc
 * ============================================================================
 */
package com.ibm.ws.sib.mfp.control;

/**
 * ControlAccept extends the general ControlMessage interface and provides
 * get/set methods for the fields specific to a Control Accept Message.
 *
 */
public interface ControlAccept extends ControlMessage {

  /* **************************************************************************/
  /* Get Methods                                                              */
  /* **************************************************************************/

  /**
   * Get the Tick values for this request.  Each tick represents an accepted
   * state.
   *
   * @return A long[] containing the Tick values
   */
  public long[] getTick();

  /* **************************************************************************/
  /* Set Methods                                                              */
  /* **************************************************************************/

  /**
   * Set the Tick values for this request.  Each tick represents an accepted
   * state.
   *
   * @param values A long[] containing the Tick values
   */
  public void setTick(long[] values);


}
