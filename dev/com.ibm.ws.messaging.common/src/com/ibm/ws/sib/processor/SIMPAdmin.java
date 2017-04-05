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
 * Reason           Date   Origin   Description
 * ---------------  ------ -------- ------------------------------------------
 * 168985.2         130603 prmf     Add administration interface
 * ===========================================================================
 */

package com.ibm.ws.sib.processor;

/**
 * @author prmf
 */
public interface SIMPAdmin
{
  /**
   * Gets the Administrator for the message processor.
   * <p>The Administrator provides the interface for administration
   * operations on the message processor.</p>
   * @return administrator
   */
  public Administrator getAdministrator();
}
