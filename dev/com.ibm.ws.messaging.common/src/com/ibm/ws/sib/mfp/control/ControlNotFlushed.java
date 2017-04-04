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
 * 178364          061103 gatfora  Original
 * 187000.4        040310 baldwint New remote durable message types
 * 195935.1        040329 baldwint Remote durable changes
 * ============================================================================
 */
package com.ibm.ws.sib.mfp.control;

/**
 * ControlNotFlushed extends the general ControlMessage interface and provides
 * get/set methods for the fields specific to a Control Not Flushed Message.
 */
public interface ControlNotFlushed extends ControlMessage {

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
   * Get the Completed Prefix priority
   *
   * @return An int[] containing priorities
   */
  public int[] getCompletedPrefixPriority();

  /**
   * Get the Completed Prefix QOS
   *
   * @return An int[] containing QOS
   */
  public int[] getCompletedPrefixQOS();

  /**
   * Get the Completed Prefix Ticks
   *
   * @return A long[] containing Ticks
   */
  public long[] getCompletedPrefixTicks();

  /**
   * Get the Duplicate Prefix priority
   *
   * @return An int[] containing priorities
   */
  public int[] getDuplicatePrefixPriority();

  /**
   * Get the Duplicate Prefix QOS
   *
   * @return An int[] containing QOS
   */
  public int[] getDuplicatePrefixQOS();

  /**
   * Get the Duplicate Prefix Ticks
   *
   * @return A long[] containing Ticks
   */
  public long[] getDuplicatePrefixTicks();

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
   * Set the Completed Prefix priority
   *
   * @param values An int[] containing priorities
   */
  public void setCompletedPrefixPriority(int[] values);

  /**
   * Set the Completed Prefix QOS
   *
   * @param values An int[] containing QOS
   */
  public void setCompletedPrefixQOS(int[] values);

  /**
   * Set the Completed Prefix Ticks
   *
   * @param values A long[] containing Ticks
   */
  public void setCompletedPrefixTicks(long[] values);

  /**
   * Set the Duplicate Prefix priority
   *
   * @param values An int[] containing priorities
   */
  public void setDuplicatePrefixPriority(int[] values);

  /**
   * Set the Duplicate Prefix QOS
   *
   * @param values An int[] containing QOS
   */
  public void setDuplicatePrefixQOS(int[] values);

  /**
   * Set the Duplicate Prefix Ticks
   *
   * @param values A long[] containing Ticks
   */
  public void setDuplicatePrefixTicks(long[] values);

}
