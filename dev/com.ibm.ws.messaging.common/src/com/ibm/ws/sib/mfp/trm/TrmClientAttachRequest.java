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
 * 164548          030502 susana   Original
 * 176766          030915 vaughton Add Subnet methods
 * 195309          040322 vaughton Add userid/password fields
 * 224759          040818 vaughton Add credential type
 * SIB0034.trm     050831 tmitchel Add token and tokenType fields.
 * ============================================================================
 */
package com.ibm.ws.sib.mfp.trm;

/**
 * TrmClientAttachRequest extends the general TrmFirstContactMessage
 * interface and provides get/set methods for all fields specific to a
 * TRM Client Attach request.
 *
 */
public interface TrmClientAttachRequest extends TrmFirstContactMessage {

  /* **************************************************************************/
  /* Get Methods                                                              */
  /* **************************************************************************/

  /**
   *  Get the Bus name from the message.
   *
   *  @return A String containing the Bus name.
   */
  public String getBusName();

  /**
   *  Get credential type
   *
   *  @return A String containing the credential type
   */
  public String getCredentialType();

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
   *  Get the ME name from the message.
   *
   *  @return A String containing the ME name.
   */
  public String getMeName();

  /**
   *  Get the subnet name from the message.
   *
   *  @return A String containing the subnet name.
   */
  public String getSubnetName();
  
  /**
   *  Get the Security Token from the message.
   *
   *  @return A byte[] containing the security token.
   */
  public byte[] getToken();
  
  /**
   *  Get the Security Token Type from the message.
   *
   *  @return A String containing the security token type.
   */
  public String getTokenType();


  /* **************************************************************************/
  /* Set Methods                                                              */
  /* **************************************************************************/

  /**
   *  Set the Bus name in the message.
   *
   *  @param value A String containing the Bus name.
   */
  public void setBusName(String value);

  /**
   *  Set the credential type
   *
   *  @param value A String containing the credential type
   */
  public void setCredentialType (String value);

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
   *  Set the ME name in the message.
   *
   *  @param value A String containing the ME name.
   */
  public void setMeName(String value);

  /**
   *  Set the subnet name in the message.
   *
   *  @param value A String containing the subnet name.
   */
  public void setSubnetName(String value);
  
  /**
   *  Set the Security Token in the message.
   *
   *  @param value A byte[] containing the security token.
   */
  public void setToken(byte[] value);
  
  /**
   *  Set the Security Token Type from the message.
   *
   *  @param A String containing the security token type.
   */
  public void setTokenType(String value);

}
