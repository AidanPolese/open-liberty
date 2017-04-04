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
 * 180540          031114 susana   Original.
 * 181718.6        031219 susana   SIBUuid changes.
 * 186967.1.1      040225 susana   Add Bus name for inter-bus support.
 * 255694          050221 nottinga Added isFromMediated flag.
 * ============================================================================
 */
package com.ibm.ws.sib.mfp;

import java.io.Serializable;

import com.ibm.websphere.sib.SIDestinationAddress;
import com.ibm.ws.sib.utils.SIBUuid8;

/**
 * JsDestinationAddress is the internal interface for Jetstream components to
 * access an SIDestinationAddress. It allows access to all the fields.
 */
public interface JsDestinationAddress extends SIDestinationAddress, Serializable {


  /* **************************************************************************/
  /* Get methods                                                              */
  /* **************************************************************************/

  /**
   *  Determine whether the LocalOnly indicator is set in the SIDestinationAddress.
   *
   *  @return boolean true if the LocalOnly indicator is set.
   */
  public boolean isLocalOnly();

  
  /**
   *  Get the Id of the Message Engine where the Destination is localized.
   *
   *  @return SIBUuid  The Id of the Message Engine where the destination is localized
   *                   Null will be returned if no Localization is set.
   */

  public SIBUuid8 getME();


  /* **************************************************************************/
  /* Set methods                                                              */
  /* **************************************************************************/

  /**
   *  Set the Id of the Message Engine where the Destination is localized.
   *  This method should only be called by the Message Processor component.
   *
   *  @param meId    The Id of the Message Engine where the destination is localized
   *  @exception IllegalStateException if isFromMediation returns true.
   */
  public void setME(SIBUuid8 meId);


  /**
   *  Set the name of the Bus where the Destination is localized.
   *  This method should only be called by the Message Processor component.
   *
   *  @param busName  The name of the Bus where the destination is localized
   *  @exception IllegalStateException if isFromMediation returns true.
   */
  public void setBusName(String busName);


}
