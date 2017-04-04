/*
 * 
 * 
 * ===========================================================================
 * IBM Confidential OCO Source Material
 *
 * Copyright IBM Corp. 2012
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 * ===========================================================================
 * 
 *
 * Change activity:
 *
 * Reason           Date   Origin   Description
 * ---------------  ------ -------- -------------------------------------------------
 * 186484.5         210404 ajw      Further Continued controllable interfaces
 * 186484.10        170504 tevans   MBean Registration
 * 186484.14        160604 cwilkin  Virtual link controllables
 * 186484.14.3      150704 cwilkin  Inbound/Outbound Link Controls
 * SIB0105.mp.1     081106 cwilkin  Link Transmission Controllables
 * SIB0105.mp.2     091106 cwilkin  Link Transmission Implementation
 * 406873           201106 cwilkin  add getTargetBus
 * ===========================================================================
 */
package com.ibm.ws.sib.processor.runtime;

/**
 *
 */
public interface SIMPVirtualLinkControllable extends SIMPControllable
{  
  
  /**
   * Returns the Link receiver controllablea for the virtual link. These are the
   * target streams of msgs coming from a source bus and arriving over this link.
   *
   * @return SIMPIterator  The link receiver controllables
   *
   */
  SIMPIterator getLinkReceiverControllableIterator();
    
  /**
   * Locates the foreign bus control adapter associated with this link
   *
   * @return SIMPForeignBusControllable The foreign bus control adapter
   *
   */
  SIMPForeignBusControllable getForeignBusControllable();
  
  /**
   * Returns the Link Transmitter controllable associated with this link. This
   * is the transmission itemstream for messages being sent to the target bus
   * over this link.
   * 
   * @return SIMPLinkTransmitterControllable The queue point used to transmit messages
   *
   */
  SIMPIterator getLinkRemoteQueuePointControllableIterator();
  
  /**
   * Returns the bus name that this link is targetted at
   * @return String busname
   */
  String getTargetBus();
  
}
