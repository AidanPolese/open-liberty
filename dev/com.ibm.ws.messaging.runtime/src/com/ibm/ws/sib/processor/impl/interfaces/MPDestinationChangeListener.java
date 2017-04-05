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
 * 522218.1         030608 nyoung   Add destinationUnavailable() method to TRM interface    
 * ===========================================================================
 */
package com.ibm.ws.sib.processor.impl.interfaces;

import java.util.Set;

import com.ibm.ws.sib.trm.dlm.Capability;
import com.ibm.ws.sib.utils.SIBUuid12;

/**
 * The MPDestinationChangeListener interface is used by MP classes that need to be
 * informed when the Message Processor DestinationChangeListener class has been alerted to a 
 * reachability change by TRM. Classes that implement this interface register with the
 * DestinationChangeListener class by calling addMPDestinationChangeListener and deregister
 * by calling removeMPDestinationChangeListener. 
 */
public interface MPDestinationChangeListener
{
  /**
   * The listener method called on each change.
   *
   * @param destUuid UUID of the destination
   * @param available Set of newly available destination uuid's (additions)
   * @param unavailable Set of newly unavailable destination uuid's (deletions)
   * @param capability The capability of the destination
   */

  void destinationLocationChange (SIBUuid12 destUuid, Set available, Set unavailable, Capability capability);
}
