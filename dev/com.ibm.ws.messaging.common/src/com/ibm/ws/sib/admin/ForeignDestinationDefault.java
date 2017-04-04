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
 *                                 Version 1.6 copied from CMVC
 * ============================================================================
 */

package com.ibm.ws.sib.admin;

import java.util.Map;

import com.ibm.websphere.sib.Reliability;

/**
 * @author philip
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public interface ForeignDestinationDefault {

  /**
   * Get the value of the defaultPriority attribute
   * @return the value
   */
  public int getDefaultPriority();

  /**
   * Get the value of the reliability attribute
   * @return the value
   */
  public Reliability getDefaultReliability();

  /**
   * @return
   */
  public Map getDestinationContext();

  /**
   * Get the value of the maxReliability attribute
   * @return the value
   */
  public Reliability getMaxReliability();

  /**
   * Is overrideOfQOSByProducerAllowed set?
   * @return
   */
  public boolean isOverrideOfQOSByProducerAllowed();

  /**
   * Set the value of the overrideOfQOSByProducerAllowed attribute
   * @param arg
   */
  public void setOverrideOfQOSByProducerAllowed(boolean arg);

  /**
   * @return
   */
  public boolean isReceiveAllowed();

  /**
   * @return
   */
  public boolean isSendAllowed();
}
