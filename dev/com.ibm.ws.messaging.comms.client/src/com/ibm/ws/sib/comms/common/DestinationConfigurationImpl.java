/*
 * @start_prolog@
 * Version: @(#) 1.12 SIB/ws/code/sib.comms.client.impl/src/com/ibm/ws/sib/comms/common/DestinationConfigurationImpl.java, SIB.comms, WASX.SIB, uu1215.01 07/04/26 04:13:31 [4/12/12 22:14:09]
 * ============================================================================
 * IBM Confidential OCO Source Materials
 * 
 * 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70  Copyright IBM Corp. 2004, 2007
 * 
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 * ============================================================================
 * @end_prolog@
 *
 * Change activity:
 *
 * Reason           Date   Origin   Description
 * ---------------  ------ -------- ------------------------------------------
 * Creation         040415 matthag  MileStone 7.5 Core SPI updates
 * D203921          040518 mattheg  Remove retry count
 * D210259.1        040819 mattheg  Core SPI interface change
 * D310870.1        051017 prestona Support isOrderingRequired on DestinationConfiguration
 * D434395         070424 prestona FINBUGS: fix findbug warnings in sib.comms.client.impl
 * ===========================================================================
 */
package com.ibm.ws.sib.comms.common;

import java.util.Map;

import com.ibm.websphere.sib.Reliability;
import com.ibm.websphere.sib.SIDestinationAddress;
import com.ibm.wsspi.sib.core.DestinationConfiguration;
import com.ibm.wsspi.sib.core.DestinationType;

/**
 * This class is the comms implementation of destination configuration.
 * 
 * @author Gareth Matthews
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
   
   private SIDestinationAddress[] _defaultForwardRoutingPath;                             // D210259.1
   
   private SIDestinationAddress _replyDestination;                                        // D210259.1

   private boolean _strictOrderingRequired; 
   
   public DestinationConfigurationImpl(int defaultPriority,
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
                                       SIDestinationAddress[] defaultForwardRoutingPath,  // D210259.1
                                       SIDestinationAddress replyDestination,             // D210259.1
                                       boolean strictOrderingRequired)
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
      if (defaultForwardRoutingPath == null)
      {
         _defaultForwardRoutingPath = null;
      }
      else
      {
         _defaultForwardRoutingPath = new SIDestinationAddress[defaultForwardRoutingPath.length];
         System.arraycopy(defaultForwardRoutingPath, 0, _defaultForwardRoutingPath, 0, defaultForwardRoutingPath.length);
      }
      _replyDestination = replyDestination;                                               // D210259.1
      _strictOrderingRequired = strictOrderingRequired;
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

   // Start D210259.1
   /**
    * @see com.ibm.wsspi.sib.core.DestinationConfiguration#getDefaultForwardRoutingPath()
    */
   public SIDestinationAddress[] getDefaultForwardRoutingPath()
   {      
      final SIDestinationAddress[] result;
      if (_defaultForwardRoutingPath == null)
      {
         result = null;
      }
      else
      {
         result = new SIDestinationAddress[_defaultForwardRoutingPath.length];
         System.arraycopy(_defaultForwardRoutingPath, 0, result, 0, result.length);
      }
      return result;
   }

   /**
    * @see com.ibm.wsspi.sib.core.DestinationConfiguration#getReplyDestination()
    */
   public SIDestinationAddress getReplyDestination()
   {
      return _replyDestination;
   }
   // End D210259.1

   /** @see com.ibm.wsspi.sib.core.DestinationConfiguration#isStrictOrderingRequired() */
   public boolean isStrictOrderingRequired()
   {
      return _strictOrderingRequired;
   }
}
