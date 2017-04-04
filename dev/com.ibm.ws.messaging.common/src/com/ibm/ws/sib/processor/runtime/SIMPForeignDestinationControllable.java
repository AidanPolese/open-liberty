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
 * 186484.14.1      230604 cwilkin  Foreign Destination controllables
 * ===========================================================================
 */
package com.ibm.ws.sib.processor.runtime;

import com.ibm.ws.sib.admin.DestinationForeignDefinition;

/**
 * 
 */
public interface SIMPForeignDestinationControllable extends SIMPMessageHandlerControllable
{
  /**
   * Locates the foreign destination definition relating to the queue. 
   *
   * @return DestinationForeignDefinition  A clone of the foreign destination definition as known at this time to the Message Processor. 
   */
  DestinationForeignDefinition getForeignDestinationDefinition();

  /**
   * Locates the local queues known to the MP and localized in this ME. 
   *
   * @return ForeignBus  An iterator over all of the LocalQueue objects. 
   */
  SIMPForeignBusControllable getTargetForeignBus();
}
