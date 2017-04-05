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
 * 504438.5         220408 nyoung   Demote SelectionCriteria Core SPI change to MP  
 * ============================================================================
 */
package com.ibm.ws.sib.processor;

import java.util.Map;

import com.ibm.wsspi.sib.core.SelectionCriteria;

public interface MPSelectionCriteria extends SelectionCriteria
{
  /**
   * Returns a map of properties that are associated with the selector. 
   *   
   * @return the properties map
   */
  public Map<String, Object> getSelectorProperties();
  
  /**
   * Sets a map of properties that are associated with the selector.
   *   
   * @param selectorProperties
   */
  public void setSelectorProperties(Map<String, Object> selectorProperties);  
}
