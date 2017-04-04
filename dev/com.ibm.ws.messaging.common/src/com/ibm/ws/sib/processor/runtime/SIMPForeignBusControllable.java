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
 * ---------------  ------ -------- -------------------------------------------------
 * 186484.5         210404 ajw      Further Continued controllable interfaces
 * 186484.7         270404 tevans   More runtime control interfaces and implementation
 * 186484.10        170504 tevans   MBean Registration
 * 186484.14        160604 cwilkin  Foreign Bus controllables
 * 186484.14.2      020704 cwilkin  New methods on foreign bus interface
 * ===========================================================================
 */
package com.ibm.ws.sib.processor.runtime;

import com.ibm.websphere.sib.Reliability;
import com.ibm.ws.sib.admin.ForeignBusDefinition;

/**
 *
 */
public interface SIMPForeignBusControllable extends SIMPMessageHandlerControllable
{
  /**
   * Locates the inter bus link associated with this bus 
   *
   * @return SIMPVirtualLinkControllable  An control adapter for the link
   */
  SIMPVirtualLinkControllable getVirtualLinkControlAdapter();
  
  /**
   * Retrieves the ForeignBusDefinition object for this foreign bus 
   *
   * @return ForeignBusDefinition The foreignBusDefinition
   */
  ForeignBusDefinition getForeignBusDefinition();
  
  /**
   * Retrieves the default priority for the foreign bus 
   *
   * @return int The default priority
   */
  public int getDefaultPriority();
  
  /**
   * Retrieves the default reliability for the foreign bus 
   *
   * @return int The default reliability
   */
  public Reliability getDefaultReliability();
  
  /**
   * Determines whether messages can be sent to the foreign bus
   * @return
   */
  public boolean isSendAllowed();
}
