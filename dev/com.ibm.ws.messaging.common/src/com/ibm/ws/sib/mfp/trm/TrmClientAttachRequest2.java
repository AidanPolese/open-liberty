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
 * 183236          031118 vaughton Original
 * 195309          040322 vaughton Add userid/password fields
 * 199144          040419 susana   Fix javadoc
 * 193911.1        040505 vaughton Tidy up
 * 224759          040818 vaughton Add credential type
 * ============================================================================
 */
package com.ibm.ws.sib.mfp.trm;

import com.ibm.ws.sib.utils.SIBUuid8;

/**
 * TrmClientAttachRequest2 extends the general TrmFirstContactMessage
 * interface and provides get/set methods for all fields specific to a
 * TRM Client Attach request 2.
 *
 */

public interface TrmClientAttachRequest2 extends TrmFirstContactMessage {

  /* **************************************************************************/
  /* Get Methods                                                              */
  /* **************************************************************************/

  /**
   *  Get the required Bus name from the message.
   *
   *  @return A String containing the Bus name.
   */
  public String getRequiredBusName();

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
   *  Get the messaging engine uuid.
   *
   *  @return The requesting ME UUID.
   */
  public SIBUuid8 getMeUuid();

  /* **************************************************************************/
  /* Set Methods                                                              */
  /* **************************************************************************/

  /**
   *  Set the required Bus name in the message.
   *
   *  @param value A String containing the Bus name.
   */
  public void setRequiredBusName(String value);

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
   *  Set the messaging engine uuid.
   *
   *  @param value The replying ME UUID.
   */
  public void setMeUuid(SIBUuid8 value);

}
