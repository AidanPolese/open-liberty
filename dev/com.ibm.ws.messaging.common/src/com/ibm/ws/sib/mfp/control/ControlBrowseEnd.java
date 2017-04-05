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
 * ControlBrowseEnd extends the general ControlMessage interface and provides
 * get/set methods for the fields specific to a Control Browse End Message.
 */
public interface ControlBrowseEnd extends ControlMessage {

  /* **************************************************************************/
  /* Get Methods                                                              */
  /* **************************************************************************/

  /**
   * Get the unique id for this request
   *
   * @return A long containing the browse ID
   */
  public long getBrowseID();

  /**
   * Get the exception code for the browse end
   *
   * @return An int containing the exception code.
   */
  public int getExceptionCode();


  /* **************************************************************************/
  /* Set Methods                                                              */
  /* **************************************************************************/

  /**
   * Set the unique id for this request
   *
   * @param value A long containing the browse ID
   */
  public void setBrowseID(long value);

  /**
   * Set the exception code for the browse end
   *
   * @param value An int containing the exception code.
   */
  public void setExceptionCode(int value);

}
