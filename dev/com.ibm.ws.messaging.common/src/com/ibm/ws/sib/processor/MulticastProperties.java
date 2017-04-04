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
 * ---------------  ------ -------- -------------------------------------------
 * 247845.1         050105 gatfora  Multicast enablement
 * 247845.1.2       040205 gatfora  Added isReliable & changed interface default
 * 329455           061205 ajw      Removed non UTF-8 character
 * ============================================================================
 */
package com.ibm.ws.sib.processor;

/**
 * A Class representing the current Multicast properties for the Messaging engine.
 */
public interface MulticastProperties
{
  /**
   * Return the Multicast Group Address
   * 
   * 
   * Default 234.6.17.92
   */
  public String getMulticastGroupAddress();
  
  /**
   * Return the Multicast Interface Address
   * Determines the network adapter to use for multicast 
   * transmissions on a multi-homed system. A value of blank specified all adapters.
   * 
   * Default "none"
   */
  public String getMulticastInterfaceAddress();
  
  /**
   * Return the Multicast Port
   * 
   * Default 34343
   */
  public int getMulticastPort();
  
  /**
   * Return the Multicast Packet Size
   * 
   * Default 7000 (bytes)
   */
  public int getMulticastPacketSize();
  
  /**
   * Specifies the network range for multicast transmissions. 
   * Routers decrement the TTL and when the value reaches 0, the packet is discarded. 
   * The value of 1 therefore confines the packet to the local LAN subnet.
   * 
   * Default 1
   */
  public int getMulticastTTL();
  
  /**
   * Specifies whether RMM should operate in 'reliable' mode.
   * 
   * Default false
   */
  public boolean isReliable();  
  
}
