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
 * 215177    040824 susana   Add get/setSecurityUserid
 * ============================================================================
 */
package com.ibm.ws.sib.mfp.control;

/**
 * ControlDeleteDurable extends the general ControlMessage interface and provides
 * get/set methods for the fields specific to a Control Delete Durable.
 */
public interface ControlDeleteDurable extends ControlMessage {

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
   * Get the name of the subscription to delete
   *
   * @return A subscription name of the form client##name
   */
  public String getDurableSubName();

  /**
   *  Get the contents of the SecurityUserid field for the subscription.
   *
   *  @return A String containing the SecurityUserid name.
   */
  public String getSecurityUserid();

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
   * Set the name of the subscription to delete.
   *
   * @param name A subscription name of the form client##name
   */
  public void setDurableSubName(String name);

  /**
   *  Set the contents of the SecurityUserid field for the subscription.
   *
   *  @param value A String containing the SecurityUserid name.
   */
  public void setSecurityUserid(String value);

}
