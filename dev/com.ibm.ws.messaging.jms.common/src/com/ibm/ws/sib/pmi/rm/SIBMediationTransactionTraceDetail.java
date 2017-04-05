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
* SIB0025.pmir.3   260805 ajw      Refactor and Registration support
* SIB0025.pmir.4   140905 ajw      Add guards to context and transaction info
*/
package com.ibm.ws.sib.pmi.rm;

import java.util.Properties;

import com.ibm.websphere.sib.SIDestinationAddress;
import com.ibm.wsspi.sib.core.DestinationType;
import com.ibm.wsspi.sib.core.SIBusMessage;

/**
 * Class to be used by instrumentation points in the SIB layer to create
 * the context values needed on methods on SIBPmiRm class. If the component is
 * not SIB and not a mediation then this class should not be used.
 */
public class SIBMediationTransactionTraceDetail
{
  private Properties properties = new Properties();
  
  private void validateAndPutProperty(String key, Object value)
  {
    if (value == null)
    {
      value = "";
    }
    properties.put(key, value);      
  }
  
  /**
   * Method to create the detail information needed when the transaction trace
   * level is set to <code>TRAN_DETAIL_LEVEL_BASIC</code>
   * 
   * @return
   */
  public void setBasicTraceDetail(SIDestinationAddress destinationAddress, 
                                        DestinationType destinationType,
                                        SIBusMessage message, String mediationName)
  {
    if (destinationAddress == null)
    {
      validateAndPutProperty("BusName", "");
      validateAndPutProperty("DestinationName", "");
    }
    else
    {
      validateAndPutProperty("BusName", destinationAddress.getBusName());
      validateAndPutProperty("DestinationName", destinationAddress.getDestinationName());
    }
    
    validateAndPutProperty("DestinationType", destinationType);
    
    if (message == null)
    {
      validateAndPutProperty("SystemMessageID", "");
      validateAndPutProperty("Priority", "");
      validateAndPutProperty("Reliability", "");
      validateAndPutProperty("Discriminator","");
    }
    else
    {
      validateAndPutProperty("SystemMessageID", message.getSystemMessageId());
      validateAndPutProperty("Priority", message.getPriority());
      validateAndPutProperty("Reliability", message.getReliability());
      validateAndPutProperty("Discriminator",message.getDiscriminator());
    }
    
    validateAndPutProperty("MediationName", mediationName);
  }

  /**
   * Method to add the extra detail information needed when the transaction trace
   * level is set to <code>TRAN_DETAIL_LEVEL_EXTENDED</code>
   * 
   * The <code>setBasicTraceDetial<code> method should be called before this
   * method
   * 
   */
  public void addExtendedTraceDetail(SIBusMessage message)
  {
    if (message == null)
    {
      validateAndPutProperty("MessageTTL", "");
      validateAndPutProperty("RemainingTTL", "");
      validateAndPutProperty("FRP", "");
      validateAndPutProperty("RRP", "");
      validateAndPutProperty("ReplyDiscriminator", "");
      validateAndPutProperty("ReplyPriority", "");
      validateAndPutProperty("ReplyReliability", "");
      validateAndPutProperty("ReplyTTL", "");
    }
    else
    {
      validateAndPutProperty("MessageTTL", message.getTimeToLive());
      validateAndPutProperty("RemainingTTL", new Long(message.getRemainingTimeToLive()));
      validateAndPutProperty("FRP", message.getForwardRoutingPath());
      validateAndPutProperty("RRP", message.getReverseRoutingPath());
      validateAndPutProperty("ReplyDiscriminator", message.getReplyDiscriminator());
      validateAndPutProperty("ReplyPriority", message.getReplyPriority());
      validateAndPutProperty("ReplyReliability", message.getReplyReliability());
      validateAndPutProperty("ReplyTTL", message.getReplyTimeToLive());
    }
  }
  
  /**
   * @return Properties
   */
  public Properties getTraceDetail()
  {
    return properties;
  }
}