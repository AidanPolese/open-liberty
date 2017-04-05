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
 * LIDB2117        030718 vaughton Original
 * 528148.1        080630 timoward Update to allow Processor to create direct ME connections if possible
 * ============================================================================
 */

package com.ibm.ws.sib.trm.topology;

import com.ibm.ws.sib.comms.MEConnection;
import com.ibm.ws.sib.utils.SIBUuid8;

/**
 * This interface defines the main routing interface into topology routing &
 * management.
 */

public interface RoutingManager {

  /**
   * Return a list all available connections that may be used to reach a
   * specified Cellule from the current message engine.
   *
   * @param c The specified Cellule
   *
   * @return A list of connections which may be used to route a message to the
   * specified Cellule
   */

  public MEConnection[] listConnections (Cellule c);

  public MEConnection connectToME(SIBUuid8 meUuid);

}
