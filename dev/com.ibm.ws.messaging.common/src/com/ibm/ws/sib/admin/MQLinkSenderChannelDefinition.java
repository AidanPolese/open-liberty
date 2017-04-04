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
 * F004142-657547  190710 urwashi  Add getter for new property connameList
 * ============================================================================
 */
package com.ibm.ws.sib.admin;

/**
 * @author leonarda
 *
 * This class is a wrapper for the MQLinkSenderChannel
 * configuration.
 *
 */
public interface MQLinkSenderChannelDefinition {

  /**
   * Get the configId WCCM refId
   * @return String configId
   */
  public String getConfigId();

  /**
   * Get the Sender channel name
   * @return string containing the name of the sender channel
   */
  public String getSenderChannelName();

  /**
   * Get the host name
   * @return string host name
   */
  public String getHostName();

   /**
   * Get the connameList
   * @return string connameList
   */
  public String getConnameList();
  
  /**
   * Get the port
   * @return int port
   */
  public int getPort();

  /**
   * Get the protocol name
   * @return string protocol name
   */
  public String getProtocolName();

  /**
   * Get the disc interval
   * @return int disc interval
   */
  public int getDiscInterval();

  /**
   * Get the short retry count
   * @return int short retry count
   */
  public int getShortRetryCount();

  /**
   * Get the short retry interval
   * @return int short retry interval
   */
  public int getShortRetryInterval();

  /**
   * Get the long retry count
   * @return long long retry count
   */
  public long getLongRetryCount();

  /**
   * Get the long retry interval
   * @return long long retry interval
   */
  public long getLongRetryInterval();

  /**
   * Get the initial state
   * @return String initial state
   *         CT_SIBMQLinkInitialState.STOPPED |
   *         CT_SIBMQLinkInitialState.STARTED
   */
  public String getInitialState();

}

