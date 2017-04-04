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
 * LIDB2117        031103 vaughton Original
 * 290290.1        051101 gelderd  Improved entry/exit trace for sib.trm
 * ============================================================================
 */

package com.ibm.ws.sib.trm.links;

import com.ibm.ws.sib.utils.SIBUuid12;

/**
 * The link manager is responsible for advertising and finding information on
 * active Jetstream links. Jetstream links can be (i) Jetstream - MQ links or
 * (ii) Jetstream - Jetstream inter-bus links. A singleton implementation of
 * this interface is provided by topology routing and management via the
 * child packages ibl and mql.
 */

public interface LinkManager {

  /**
   * Register an inter-bus link so that all bus messaging engines can see that
   * the link is active.
   *
   * @param linkUuid The uuid of the link to be registered
   *
   * @throws LinkException if the linkUuid is not known or already registered
   */

  void register (SIBUuid12 linkUuid) throws LinkException;

  /**
   * Deregister a link so that bus messaging engines can no longer see that the
   * link is active.
   *
   * @param linkUuid The uuid of the link to be deregistered
   *
   * @throws LinkException if the linkUuid is not known
   */

  void deregister (SIBUuid12 linkUuid) throws LinkException;

  /**
   * Method called to find a information on a link. If the specified link
   * is not currently active then null is returned.
   *
   * @param linkUuid The uuid of the required link
   *
   * @return Information about the required link or null if the required
   * link is not active.
   *
   * @throws LinkException if the linkUuid is not known
   */

  LinkSelection select (SIBUuid12 linkUuid) throws LinkException;

  /**
   * Method called to set a link change listener to be called each time a
   * change occurs in a link.
   *
   * @param lcl Instance of a LinkChangeListener
   */

  void setChangeListener (LinkChangeListener lcl);

}
