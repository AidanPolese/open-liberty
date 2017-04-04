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
 * Reason          Date  Origin   Description
 * --------------- ------ -------- --------------------------------------------
 * 178364          061103 gatfora  Original
 * 178364.2        031231 susana   Tidy up javadoc
 * 207007.3        040610 baldwint Add selectorDomain attribute
 * 180483.15       040630 baldwint Add disciminator and multiple filters/selectorDomains
 * ============================================================================
 */
package com.ibm.ws.sib.mfp.control;

/**
 * ControlRequest extends the general ControlMessage interface and provides
 * get/set methods for the fields specific to a Control Request Message which
 * at present is none.
 */
public interface ControlRequest extends ControlMessage {

  /* **************************************************************************/
  /* Get Methods                                                              */
  /* **************************************************************************/

  /**
   * Get the content filters for this request
   *
   * @return A String[] containing the content filters
   */
  public String[] getFilter();

  /**
   * Get the Reject start tick for this request
   *
   * @return A long[] containing the reject start ticks
   */
  public long[] getRejectStartTick();

  /**
   * Get the Get Tick value for this request.
   *
   * @return A long[] containing the GetTick values
   */
  public long[] getGetTick();

  /**
   * Get the Timeout values for this request
   *
   * @return A long[] containing the timeout values
   */
  public long[] getTimeout();
  
  /**
   * Get the Selector Domains for this request
   * 
   * @return An int[] containing the selector domain values
   */
  public int[] getSelectorDomain();
  
  /**
   * Get the Discriminators this request
   * 
   * @return A String[] containing the discriminator values
   */
  public String[] getControlDisciminator();
 
  /* **************************************************************************/
  /* Set Methods                                                              */
  /* **************************************************************************/

  /**
   * Set the content filters for this request
   *
   * @param value A String[] containing the content filters
   */
  public void setFilter(String[] values);

  /**
   * Set the Reject start tick for this request
   *
   * @param values A long[] containing the reject start ticks
   */
  public void setRejectStartTick(long[] values);

  /**
   * Set the Get Tick value for this request.
   *
   * @param values A long[] containing the GetTick values
   */
  public void setGetTick(long[] values);

  /**
   * Set the Timeout values for this request
   *
   * @param values A long[] containing the timeout values
   */
  public void setTimeout(long[] values);
  
  /**
   * Set the Selector Domains for this request
   * 
   * @param value An int[] containing the selector domain values
   */
  public void setSelectorDomain(int[] values);
  
  /**
   * Set the Disciminators for this request
   * 
   * @param value A String[] containing the discriminator values
   */
  public void setControlDiscriminator(String[] values);
}
