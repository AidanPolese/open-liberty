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
 * LIDB2117        041103 vaughton Original
 * d522218         080603 timoward Update to allow TRM to inform processor that
 *                                 a destination is no longer available
 * ============================================================================
 */

package com.ibm.ws.sib.trm.dlm;

import com.ibm.ws.sib.utils.SIBUuid12;
import java.util.Set;

/**
 * The Destination Location Manager change listener is an interface which is
 * called by the Destination Location Manager whenever changes occur in the
 * set of messaging engines localising a destination. A
 * DestinationLocationChangeListener is registered via the DestinationLocationManager
 * setChangeListener method.
 */

public interface DestinationLocationChangeListener {

  /**
   * The listener method called on each change.
   *
   * @param destUuid UUID of the destination
   *
   * @param available Set of newly available destination uuid's (additions)
   *
   * @param unavailable Set of newly unavailable destination uuid's (deletions)
   *
   * @param capability The capability of the destination
   *
   */

  void destinationLocationChange (SIBUuid12 destUuid, Set available, Set unavailable, Capability capability);

  
  /**
   * Because TRM doesn't get told the name of the last ME to stop hosting a destination we
   * have this method to indicate that the destination is no longer available anywhere.
   *
   * @param destUuid UUID of the destination
   *
   * @param capability The capability of the destination
   *
   */
  void destinationUnavailable(SIBUuid12 destUuid, Capability capability);
  
}
