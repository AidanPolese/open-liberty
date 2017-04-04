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
 * 172028          030718 vaughton Original
 * 172429          030723 vaughton Add ReqMeName/ReqSubnetName methods
 * 173993          030811 vaughton Add UUID field & remove type field
 * 175336          030828 susana   Add set/getMagicNumber methods
 * 195309          040322 vaughton Add userid/password fields
 * 193911          040422 vaughton Tidy up
 * ============================================================================
 */
package com.ibm.ws.sib.mfp.trm;

import com.ibm.ws.sib.utils.SIBUuid8;

/**
 * TrmMeLinkRequest extends the general TrmFirstContactMessage
 * interface and provides get/set methods for all fields specific to a
 * TRM ME Link request.
 *
 */
public interface TrmMeLinkRequest extends TrmFirstContactMessage {

  /* **************************************************************************/
  /* Get Methods                                                              */
  /* **************************************************************************/

  /**
   *  Get the Magic Number from the message.
   *
   *  @return A long containing the Magic Number.
   */
  public long getMagicNumber();

  /**
   *  Get the required Bus name from the message.
   *
   *  @return A String containing the Bus name.
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
   *  Get the required ME name from the message.
   *
   *  @return A String containing the ME name.
   */
  public String getRequiredMeName();

  /**
   *  Get the required Subnet name from the message.
   *
   *  @return A String containing the Subnet name.
   */
  public String getRequiredSubnetName();

  /**
   *  Get the requesting ME name from the message.
   *
   *  @return A String containing the requesting ME name.
   */
  public String getRequestingMeName();

  /**
   *  Get the requesting ME UUID from the message.
   *
   *  @return The requesting ME UUID.
   */
  public SIBUuid8 getRequestingMeUuid();

  /**
   *  Get the requesting Subnet name from the message.
   *
   *  @return A String containing the requesting Subnet name.
   */
  public String getRequestingSubnetName();

  /* **************************************************************************/
  /* Set Methods                                                              */
  /* **************************************************************************/

  /**
   *  Set the Magic Number field in the message.
   *
   *  @param value  An long containing the Magic Number.
   */
  public void setMagicNumber(long value);

  /**
   *  Set the required Bus name in the message.
   *
   *  @param value A String containing the Bus name.
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
   *  Set the required ME name in the message.
   *
   *  @param value A String containing the ME name.
   */
  public void setRequiredMeName(String value);

  /**
   *  Set the required Subnet name in the message.
   *
   *  @param value A String containing the Subnet name.
   */
  public void setRequiredSubnetName(String value);

  /**
   *  Set the requesting ME name in the message.
   *
   *  @param value A String containing the requesting ME name.
   */
  public void setRequestingMeName(String value);

  /**
   *  Set the requesting ME UUID in the message.
   *
   *  @param value The requesting ME UUID.
   */
  public void setRequestingMeUuid(SIBUuid8 value);

  /**
   *  Set the requesting Subnet name in the message.
   *
   *  @param value A String containing the requesting Subnet name.
   */
  public void setRequestingSubnetName(String value);

}
