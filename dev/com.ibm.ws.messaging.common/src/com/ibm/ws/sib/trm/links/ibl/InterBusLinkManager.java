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
 * 266910          190405 matrober sSVT: JS Recovery fails on multibus with PubSub and MDB's
 * SIB0211.ibl.2   200407 jamessid Adding accessor methods for admin 
 * ============================================================================
 */

package com.ibm.ws.sib.trm.links.ibl;

import com.ibm.ws.sib.trm.links.LinkException;
import com.ibm.ws.sib.utils.SIBUuid12;

/**
 * The inter-bus link manager is used specifically for managing Jetstream
 * inter-bus links. Once defined inter links can be operated on using the
 * generic com.ibm.ws.sib.trm.links.LinkManager class.
 */

public interface InterBusLinkManager {

  /**
   * Define a new inter-bus link
   *
   * @param interBusLinkConfig the configuration information for the inter-bus link
   *
   * @throws LinkException if the link is already defined
   */
  void define (InterBusLinkConfig interBusLinkConfig) throws LinkException;
  
  /**
   * Un-define an inter-bus link.
   * 
   * This is used when the ME is stopped through the MBean.
   *
   * @throws LinkException if something goes wrong.
   */
  void undefine (SIBUuid12 linkUuid) throws LinkException;

  /**
   * Start a specific inter-bus link
   *
   * @param linkUuid The uuid of the link to be started
   *
   * @throws LinkException if the linkUuid is not known
   */

  void start (SIBUuid12 linkUuid) throws LinkException;

  /**
   * Stop a specific inter-bus link
   *
   * @param linkUuid The uuid of the link to be stopped
   *
   * @throws LinkException if the linkUuid is not known
   */

  void stop (SIBUuid12 linkUuid) throws LinkException;

  /**
   * Is the inter-bus link defined
   *
   * @param linkUuid The uuid of the link
   *
   * @return boolean true if the link is already defined
   */

  boolean isDefined (SIBUuid12 linkUuid);

  /**
   * Is the link started
   *
   * @return boolean true if the lisk is started
   */

  boolean isStarted (SIBUuid12 linkUuid);

  /**
   * Is the link active
   *
   * @return boolean true if the lisk is active
   */

  boolean isActive (SIBUuid12 linkUuid);
  
  /**
   * Gets the currently active target transport chain for the given sib link
   * @param linkUuid UUID of the SIB link being queried
   * @return Currently active transport chain for given SIB link
   */
  String getActiveTargetInboundTransportChain(SIBUuid12 linkUuid);

  /**
   * Gets the currently active bootstrap endpoints for the given sib link
   * @param linkUuid UUID of the SIB link being queried
   * @return Currently active bootstrap endpoints for given SIB link
   */
  String getActiveBootstrapEndpoints(SIBUuid12 linkUuid);
  
  /**
   * Gets the currently active authentication alias for the given sib link
   * @param linkUuid UUID of the SIB link being queried
   * @return Currently active authentication alias for given SIB link
   */
  String getActiveAuthenticationAlias(SIBUuid12 linkUuid);
}
