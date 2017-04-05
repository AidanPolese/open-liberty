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
 * LIDB2117        040226 vaughton Original
 * 224794.1.2      040817 vaughton Protocol -> Transport Chain
 * 185656          040902 susana   Tidy up imports etc
 * ============================================================================
 */
package com.ibm.ws.sib.mfp.trm;

/**
 * TrmMeBridgeBootstrapRequest extends the general TrmFirstContactMessage
 * interface and provides get/set methods for all fields specific to a
 * TRM Me Bridge Bootstrap request.
 *
 */
public interface TrmMeBridgeBootstrapRequest extends TrmFirstContactMessage {

  /* **************************************************************************/
  /* Get Methods                                                              */
  /* **************************************************************************/

  /**
   *  Get the required Bus from the message.
   *
   *  @return A String containing the required Bus name.
   */
  public String getRequiredBusName();

  /**
   *  Get userid
   *
   *  @return A String containing the userid
   */
  public String getUserid ();

  /**
   *  Get password
   *
   *  @return A String containing the password
   */
  public String getPassword ();

  /**
   *  Get the requesting Bus from the message.
   *
   *  @return A String containing the requesting Bus name.
   */
  public String getRequestingBusName();

  /**
   *  Get the Link name from the message.
   *
   *  @return A string containing the Link name.
   */
  public String getLinkName();

  /**
   *  Get the required transport chain from the message.
   *
   *  @return A string containing the required transport chain.
   */
  public String getRequiredTransportChain();

  /* **************************************************************************/
  /* Set Methods                                                              */
  /* **************************************************************************/

  /**
   *  Set the required Bus name in the message.
   *
   *  @param value A String containing the required Bus name.
   */
  public void setRequiredBusName(String value);

  /**
   *  Set the userid
   *
   *  @param value A String containing the userid
   */
  public void setUserid (String value);

  /**
   *  Set the password
   *
   *  @param value A String containing the password
   */
  public void setPassword (String value);

  /**
   *  Set the requesting Bus name in the message.
   *
   *  @param value A String containing the requesting Bus name.
   */
  public void setRequestingBusName(String value);

  /**
   *  Set the Link name in the message.
   *
   *  @param value A String containing the Link name.
   */
  public void setLinkName(String value);

  /**
   *  Set the required transport chain in the message.
   *
   *  @param value A String containing the required transport chain.
   */
  public void setRequiredTransportChain(String value);

}
