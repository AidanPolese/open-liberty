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
 * Reason           Date  Origin   Description
 * ---------------  ------ -------- -------------------------------------------------
 * 195758.1         130404 gatfora  MileStone 7.5 Core SPI updates
 * 199556.1         280404 gatfora  Removal of retry count from destination configuration
 * 210259           190804 gatfora  Added getForwardRoutingPath and getReplyDestination
 * 210259           190804 gatfora  DestinationConfiguration added methods getDefaultFRP and getReplyDest
 * 310870           131005 cwilkin  Add strict ordering attribute
 * ===========================================================================
 */
package com.ibm.ws.sib.processor.impl;

import java.util.Map;

import com.ibm.websphere.sib.Reliability;
import com.ibm.websphere.sib.SIDestinationAddress;
import com.ibm.wsspi.sib.core.DestinationConfiguration;
import com.ibm.wsspi.sib.core.DestinationType;

/**
 */
public class DestinationConfigurationImpl implements DestinationConfiguration
{
  
  private boolean _sendAllowed;

  private boolean _receiveExclusive;

  private boolean _receiveAllowed;

  private boolean _producerQOSOverrideEnabled;

  private String _uuid;

  private String _name;

  private Reliability _maxReliability;

  private int _maxFailedDeliveries;

  private String _exceptionDestination;

  private DestinationType _destinationType;

  private Map _destinationContext;

  private String _description;

  private Reliability _defaultReliability;

  private int _defaultPriority;
  
  private boolean _isStrictOrderingRequired;
  
  private SIDestinationAddress _defaultForwardRoutingPath[];
  
  private SIDestinationAddress _replyDestination;

  DestinationConfigurationImpl(int defaultPriority,
                               Reliability defaultReliability,
                               String description,
                               Map destinationContext,
                               DestinationType destinationType,
                               String exceptionDestination,
                               int maxFailedDeliveries,
                               Reliability maxReliability,
                               String name,
                               String uuid,
                               boolean producerQOSOverrideEnabled,
                               boolean receiveAllowed,
                               boolean receiveExclusive,
                               boolean sendAllowed,
                               SIDestinationAddress defaultRoutingPath[],
                               SIDestinationAddress replyDestination,
                               boolean isStrictOrderingRequired)
  {
    _defaultPriority = defaultPriority;
    _defaultReliability = defaultReliability;
    _description = description;
    _destinationContext = destinationContext;
    _destinationType = destinationType;
    _exceptionDestination = exceptionDestination;
    _maxFailedDeliveries = maxFailedDeliveries;
    _maxReliability = maxReliability;
    _name = name;
    _uuid = uuid;
    _producerQOSOverrideEnabled = producerQOSOverrideEnabled;
    _receiveAllowed = receiveAllowed;
    _receiveExclusive = receiveExclusive;
    _sendAllowed = sendAllowed;    
    _defaultForwardRoutingPath = defaultRoutingPath;
    _replyDestination = replyDestination;   
    _isStrictOrderingRequired = isStrictOrderingRequired;                           
  }
  
  /* (non-Javadoc)
   * @see com.ibm.wsspi.sib.core.DestinationConfiguration#getDefaultPriority()
   */
  public int getDefaultPriority()
  {
    return _defaultPriority;
  }

  /* (non-Javadoc)
   * @see com.ibm.wsspi.sib.core.DestinationConfiguration#getExceptionDestination()
   */
  public String getExceptionDestination()
  {
    return _exceptionDestination;
  }

  /* (non-Javadoc)
   * @see com.ibm.wsspi.sib.core.DestinationConfiguration#getName()
   */
  public String getName()
  {
    return _name;
  }

  /* (non-Javadoc)
   * @see com.ibm.wsspi.sib.core.DestinationConfiguration#getUUID()
   */
  public String getUUID()
  {
    return _uuid;
  }

  /* (non-Javadoc)
   * @see com.ibm.wsspi.sib.core.DestinationConfiguration#getDescription()
   */
  public String getDescription()
  {
    return _description;
  }

  /* (non-Javadoc)
   * @see com.ibm.wsspi.sib.core.DestinationConfiguration#getDestinationContext()
   */
  public Map getDestinationContext()
  {
    return _destinationContext;
  }

  /* (non-Javadoc)
   * @see com.ibm.wsspi.sib.core.DestinationConfiguration#getDestinationType()
   */
  public DestinationType getDestinationType()
  {
    return _destinationType;
  }

  /* (non-Javadoc)
   * @see com.ibm.wsspi.sib.core.DestinationConfiguration#getDefaultReliability()
   */
  public Reliability getDefaultReliability()
  {
    return _defaultReliability;
  }

  /* (non-Javadoc)
   * @see com.ibm.wsspi.sib.core.DestinationConfiguration#getDefaultForwardRoutingPath()
   */
  public SIDestinationAddress[] getDefaultForwardRoutingPath()
  {
    return _defaultForwardRoutingPath;
  }

  /* (non-Javadoc)
   * @see com.ibm.wsspi.sib.core.DestinationConfiguration#getMaxFailedDeliveries()
   */
  public int getMaxFailedDeliveries()
  {
    return _maxFailedDeliveries;
  }

  /* (non-Javadoc)
   * @see com.ibm.wsspi.sib.core.DestinationConfiguration#getMaxReliability()
   */
  public Reliability getMaxReliability()
  {
    return _maxReliability;
  }
  
  /* (non-Javadoc)
   * @see com.ibm.wsspi.sib.core.DestinationConfiguration#getReplyDestination()
   */
  public SIDestinationAddress getReplyDestination()
  {
    return _replyDestination;
  }

  /* (non-Javadoc)
   * @see com.ibm.wsspi.sib.core.DestinationConfiguration#isProducerQOSOverrideEnabled()
   */
  public boolean isProducerQOSOverrideEnabled()
  {
    return _producerQOSOverrideEnabled;
  }

  /* (non-Javadoc)
   * @see com.ibm.wsspi.sib.core.DestinationConfiguration#isReceiveAllowed()
   */
  public boolean isReceiveAllowed()
  {
    return _receiveAllowed;
  }

  /* (non-Javadoc)
   * @see com.ibm.wsspi.sib.core.DestinationConfiguration#isReceiveExclusive()
   */
  public boolean isReceiveExclusive()
  {
    return _receiveExclusive;
  }

  /* (non-Javadoc)
   * @see com.ibm.wsspi.sib.core.DestinationConfiguration#isSendAllowed()
   */
  public boolean isSendAllowed()
  {
    return _sendAllowed;
  }

  /* (non-Javadoc)
   * @see com.ibm.wsspi.sib.core.DestinationConfiguration#isStrictOrderingRequired()
   */
  public boolean isStrictOrderingRequired() 
  {
    return _isStrictOrderingRequired;
  }

}