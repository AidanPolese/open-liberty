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
 * 174699          030820 vaughton Original
 * 178702          031006 vaughton SIBUuid -> cellules
 * 181718.6        031219 susana   SIBUuid changes
 * ============================================================================
 */

package com.ibm.ws.sib.mfp.trm;

import com.ibm.ws.sib.utils.SIBUuid8;
import java.util.List;

/**
 * TrmRouteData extends the general TrmMessage interface and provides
 * get/set methods for all fields specific to a TRM Route Data message.
 *
 */
public interface TrmRouteData extends TrmMessage {

  /* **************************************************************************/
  /* Get Methods                                                              */
  /* **************************************************************************/

  /**
   *  Get the originator messaging engine SIBUuid.
   *
   *  @return A SIBUuid8
   */
  public SIBUuid8 getOriginator();

  /**
   *  Get the list of Cellules in the routing table
   *
   *  @return List of Cellules
   */
  public List getCellules();

  /**
   *  Get the list of route costs in the routing table
   *
   *  @return List of Integer
   */
  public List getCosts();

  /* **************************************************************************/
  /* Set Methods                                                              */
  /* **************************************************************************/

  /**
   *  Set the originator messaging engine SIBUuid.
   *
   *  @param value A SIBUuid8
   */
  public void setOriginator(SIBUuid8 value);

  /**
   *  Set the list of Cellules in the routing table
   *
   *  @param value List of Cellules
   */
  public void setCellules(List value);

  /**
   *  Set the list of route costs in the routing table
   *
   *  @param value List of Integer
   */
  public void setCosts(List value);

}
