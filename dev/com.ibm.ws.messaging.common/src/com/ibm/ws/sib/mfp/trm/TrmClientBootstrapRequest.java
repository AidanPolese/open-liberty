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
 * 183236          031118 vaughton Change fields
 * 183786          031124 vaughton Rename remotePreference field
 * 191625          040220 vaughton Add bootstrap protocol field
 * 195309          040322 vaughton Add userid/password fields
 * 199144          040419 susana   Fix javadoc
 * 206397          040601 vaughton Add target significance
 * 224794.1.2      040817 vaughton Protocol -> Transport Chain
 * 224759          040818 vaughton Add credential type
 * 224794.1.4      040825 vaughton Rename remoteProtocol
 * 185656          040902 susana   Tidy up imports etc
 * 250606.1.1      050125 vaughton Recovery mode
 * ============================================================================
 */
package com.ibm.ws.sib.mfp.trm;

/**
 * TrmClientBootstrapRequest extends the general TrmFirstContactMessage
 * interface and provides get/set methods for all fields specific to a
 * TRM Client Bootstrap request.
 *
 */
public interface TrmClientBootstrapRequest extends TrmFirstContactMessage {

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
  public String getUserid();

  /**
   *  Get password
   *
   *  @return A String containing the password
   */
  public String getPassword();

  /**
   *  Get the target group name.
   *
   *  @return A string containing the target group name.
   */
  public String getTargetGroupName();

  /**
   *  Get the target group type.
   *
   *  @return A string containing the target group type.
   */
  public String getTargetGroupType();

  /**
   *  Get the target significance.
   *
   *  @return A string containing the target significance.
   */
  public String getTargetSignificance();

  /**
   *  Get the connection proximity.
   *
   *  @return A string containing the connection proximity.
   */
  public String getConnectionProximity();

  /**
   *  Get the target transport chain.
   *
   *  @return A string containing the target transport chain.
   */
  public String getTargetTransportChain();

  /**
   * Get the bootstrap transport chain.
   *
   * @return A string containing the bootstrap transport chain.
   */
  public String getBootstrapTransportChain();

  /**
   * Get the connection mode.
   *
   * @return A string containing the connection mode.
   */
  public String getConnectionMode();                                                                        //250606.1.1

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
   *  Set the target group name.
   *
   *  @param value A string containing the target group name.
   */
  public void setTargetGroupName(String value);

  /**
   *  Set the target group type.
   *
   *  @param value A string containing the target group type.
   */
  public void setTargetGroupType(String value);

  /**
   *  Set the target significance.
   *
   *  @param value A string containing the target significance.
   */
  public void setTargetSignificance(String value);

  /**
   *  Set the connection proximity.
   *
   *  @param value A string containing the connection proximity.
   */
  public void setConnectionProximity(String value);

  /**
   *  Set the target transport chain.
   *
   *  @param value A string containing the target transport chain.
   */
  public void setTargetTransportChain(String value);

  /**
   *  Set the bootstrap transport chain.
   *
   *  @param value A string containing the bootstrap transport chain.
   */
  public void setBootstrapTransportChain(String value);

  /**
   *  Set the connection mode.
   *
   *  @param value A string containing the connection mode.
   */
  public void setConnectionMode(String value);                                                              //250606.1.1

}
