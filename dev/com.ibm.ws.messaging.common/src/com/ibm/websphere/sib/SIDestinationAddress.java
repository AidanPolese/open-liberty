/*
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
 * Change activity:
 *
 * Reason          Date   Origin   Description
 * --------------- ------ -------- --------------------------------------------
 *                                 Version 1.8 copied from CMVC
 * ============================================================================
 */
package com.ibm.websphere.sib;


/**
 * The SIDestinationAdress is the public interface which represents an SIBus
 * Destination.
 *
 * @ibm-was-base
 * @ibm-api
 */
public interface SIDestinationAddress {


  /* **************************************************************************/
  /* Get methods                                                              */
  /* **************************************************************************/

  /**
   *  Determine whether the SIDestinationAddress represents a Temporary or
   *  Permanent Destination.
   *
   *  @return boolean true if the Destination is Temporary, false if it is permanent.
   */
  public boolean isTemporary();


  /**
   *  Get the name of the Destination represented by this SIDestinationAddress.
   *
   *  @return String The name of the Destination.
   */
  public String getDestinationName();


  /**
   *  Get the Bus name for the Destination represented by this SIDestinationAddress.
   *
   *  @return String The Bus name of the SIDestinationAddress.
   */
  public String getBusName();

}
