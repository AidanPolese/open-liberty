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
 * ============================================================================
 */
package com.ibm.ws.sib.processor.impl;

import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.sib.processor.MulticastProperties;
import com.ibm.ws.sib.processor.SIMPConstants;
import com.ibm.ws.sib.utils.ras.SibTr;

/**
 */
public class MulticastPropertiesImpl implements MulticastProperties
{
  private static final TraceComponent tc =
    SibTr.register(MulticastPropertiesImpl.class, SIMPConstants.MP_TRACE_GROUP, SIMPConstants.RESOURCE_BUNDLE);

  private String _multicastInterfaceAddress;
  private int _multicastPort; 
  private int _multicastPacketSize; 
  private int _multicastTTL;
  private String _multicastGroupAddress;
  private boolean _multicastUseReliableRMM;

  /**
   * @param multicastInterfaceAddress
   * @param multicastPort
   * @param multicastPacketSize
   * @param multicastTTL
   * @param multicastGroupAddress
   */
  public MulticastPropertiesImpl(String multicastInterfaceAddress, 
                                 int multicastPort, 
                                 int multicastPacketSize, 
                                 int multicastTTL, 
                                 String multicastGroupAddress,
                                 boolean multicastUseReliableRMM)
  {
    if (tc.isEntryEnabled())
      SibTr.entry(
        tc,
        "MulticastPropertiesImpl",
        new Object[] {
          multicastInterfaceAddress,
          new Integer(multicastPort),
          new Integer(multicastPacketSize),
          new Integer(multicastTTL),
          multicastGroupAddress,
          new Boolean(multicastUseReliableRMM) });
          
    _multicastInterfaceAddress = multicastInterfaceAddress;
    _multicastPort = multicastPort;
    _multicastPacketSize = multicastPacketSize;
    _multicastTTL = multicastTTL;
    _multicastGroupAddress = multicastGroupAddress; 
    _multicastUseReliableRMM = multicastUseReliableRMM;
    
    if (tc.isEntryEnabled())
      SibTr.exit(tc, "MulticastPropertiesImpl", this);  
  }

  /* (non-Javadoc)
   * @see com.ibm.ws.sib.processor.MulticastProperties#getMulticastGroupAddress()
   */
  public String getMulticastGroupAddress()
  {
    return _multicastGroupAddress;
  }

  /* (non-Javadoc)
   * @see com.ibm.ws.sib.processor.MulticastProperties#getMulticastInterfaceAddress()
   */
  public String getMulticastInterfaceAddress()
  {
    return _multicastInterfaceAddress;
  }

  /* (non-Javadoc)
   * @see com.ibm.ws.sib.processor.MulticastProperties#getMulticastPort()
   */
  public int getMulticastPort()
  {
    return _multicastPort;
  }

  /* (non-Javadoc)
   * @see com.ibm.ws.sib.processor.MulticastProperties#getMulticastPacketSize()
   */
  public int getMulticastPacketSize()
  {
    return _multicastPacketSize;
  }

  /* (non-Javadoc)
   * @see com.ibm.ws.sib.processor.MulticastProperties#getMulticastTTL()
   */
  public int getMulticastTTL()
  {
    return _multicastTTL;
  }

  /* (non-Javadoc)
   * @see com.ibm.ws.sib.processor.MulticastProperties#isReliable()
   */
  public boolean isReliable()
  {
    return _multicastUseReliableRMM;
  }
  
  public String toString()
  {
    return "GroupAddress:" + _multicastGroupAddress + 
          ",InterfaceAddress:" + _multicastInterfaceAddress +
          ",Port:" + _multicastPort + 
          ",PacketSize:" + _multicastPacketSize + 
          ",TTL:" + _multicastTTL + 
          ",useReliableRMM:"+_multicastUseReliableRMM;
  }

}
