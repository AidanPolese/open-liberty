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
 *                                 Version 1.7 copied from CMVC
 * ============================================================================
 */

package com.ibm.ws.sib.admin;

import java.util.Map;

import com.ibm.ws.sib.utils.SIBUuid12;

/**
 * @author philip
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public interface BaseDestinationDefinition extends Cloneable {

  /**
   * Is this destination a local destination, defined on this bus?
   * @return
   */
  public boolean isLocal();

  /**
   * Is this destination an alias to another destination, possibly defined
   * on this bus?
   * @return
   */
  public boolean isAlias();

  /**
   * Is this a foreign destination, indicating that the referenced destination
   * on the foreign bus exists?
   * @return
   */
  public boolean isForeign();

  /**
   * @return
   */
  public String getName();

  /**
   * @return
   */
  public SIBUuid12 getUUID();

  /**
   * @param value
   */
  public void setUUID(SIBUuid12 value);

  /**
   * @return
   */
  public String getDescription();

  /**
   * @param value
   */
  public void setDescription(String value);

  /**
   * @return
   */
  public Map getDestinationContext();

  /**
   * @param arg
   */
  public void setDestinationContext(Map arg);

  /**
   * @return
   */
  public Object clone();

}
