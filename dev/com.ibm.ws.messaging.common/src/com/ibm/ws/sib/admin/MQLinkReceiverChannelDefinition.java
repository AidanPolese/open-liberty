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
 * SIB0211.adm.1   260107 leonarda New
 * SIB0113.adm.4   051207 andrewc  Changesto support SIB0113
 * ============================================================================
 */
package com.ibm.ws.sib.admin;

/**
 * @author leonarda
 *
 * This class is a wrapper for the MQLink receiver channel
 * configuration.
 *
 */
public interface MQLinkReceiverChannelDefinition {

  /**
   * Get the configId WCCM refId
   * @return String configId
   */
  public String getConfigId();

  /**
   * Get the receiver channel name
   * @return string receiver channel name
   */
  public String getReceiverChannelName();

  /**
   * Get the inbound npm reliability
   * @return string inbound npm reliability
   *         CT_SIBMQNonPersistentReliability.BEST_EFFORT |
   *         CT_SIBMQNonPersistentReliability.EXPRESS |
   *         CT_SIBMQNonPersistentReliability.RELIABLE
   */
  public String getInboundNpmReliability();

  /**
   * Get the inbound pm reliability
   * @return string inbound pm reliability
   *         CT_SIBMQPersistentReliability.RELIABLE |
   *         CT_SIBMQPersistentReliability.ASSURED
   */
  public String getInboundPmReliability();

  /**
   * Get the initial state
   * @return String initial state
   *         CT_SIBMQLinkInitialState.STOPPED |
   *         CT_SIBMQLinkInitialState.STARTED
   */
  public String getInitialState();
 /**
   * Return whether local queue points are
   * preferred (now optional in WAS7.x)
   * 
   * @return boolean
   */
  public boolean getPreferLocal();

}

