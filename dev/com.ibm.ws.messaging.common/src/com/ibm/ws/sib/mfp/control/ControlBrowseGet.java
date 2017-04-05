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
 * 180483.15       040630 baldwint Add disciminator attribute
 * ============================================================================
 */
package com.ibm.ws.sib.mfp.control;

/**
 * ControlBrowseGet extends the general ControlMessage interface and provides
 * get/set methods for the fields specific to a Control Browse Get Message.
 */
public interface ControlBrowseGet extends ControlMessage {

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
   * Get the sequence number for this request
   *
   * @return A long containing the sequence number
   */
  public long getSequenceNumber();

  /**
   * Get the content filter for this request
   *
   * @return A String containing the content filter
   */
  public String getFilter();
  
  /**
   * Get the selector domain for this request
   * 
   * @return An int containing the selector domain value
   */
  public int getSelectorDomain();
  
  /** 
   * Get the discriminator for this request. 
   * 
   * @return A String containing the discriminator. 
   */ 
  public String getControlDiscriminator(); 
 
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
   * Set the sequence number for this request
   *
   * @param value A long containing the sequence number
   */
  public void setSequenceNumber(long value);

  /**
   * Set the content filter for this request
   *
   * @param value A String containing the content filter
   */
  public void setFilter(String value);
  
  /**
   * Set the selector domain for this request
   * 
   * @param value An int containing the selector domain value
   */
  public void setSelectorDomain(int value);
  
  /** 
   * Set the discriminator for this request. 
   * 
   * @param value A String containing the discriminator value
   */ 
  public void setControlDiscriminator(String value); 
}
