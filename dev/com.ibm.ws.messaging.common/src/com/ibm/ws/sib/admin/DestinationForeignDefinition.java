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
 *                                 Version 1.10 copied from CMVC
 * ============================================================================
 */

package com.ibm.ws.sib.admin;

import com.ibm.websphere.sib.Reliability;

public interface DestinationForeignDefinition extends BaseDestinationDefinition {

  /**
   * @return
   */
  public String getBus();

  /**
   * @param busName
   */
  public void setBus(String busName);

  /**
   * @return
   */
  public int getDefaultPriority();

  /**
   * @param arg
   */
  public void setDefaultPriority(int arg);

  /**
   * @return
   */
  public Reliability getMaxReliability();

  /**
   * @param arg
   */
  public void setMaxReliability(Reliability arg);

  /**
   * @return
   */
  public boolean isOverrideOfQOSByProducerAllowed();
  
  /**
   * @param arg
   */
  public void setOverrideOfQOSByProducerAllowed(boolean arg);

  /**
   * @return
   */
  public Reliability getDefaultReliability();

  /**
   * @param arg
   */
  public void setDefaultReliability(Reliability arg);

  /**
   * @return
   */
  public boolean isSendAllowed();

  /**
   * @param arg
   */
  public void setSendAllowed(boolean arg);
}
