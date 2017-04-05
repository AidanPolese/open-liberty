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
 * Reason          Date   Origin   Description
 * --------------- ------ -------- --------------------------------------------
 * Creation        030426 prestona Original
 * F182479         031127 prestona New ConnectionProperties varient required.
 * F189000         030130 prestona Expose WLM endpoints through CF
 * D189754         040106 prestona Change deprecation of APIs
 * F191798         040227 prestona Use proper chain names
 * F184185.7.2     040323 mattheg  Remove user names and password
 * F196678.10      040426 prestona JS Client Administration
 * D199148         040812 mattheg  JavaDoc
 * F244595         041116 prestona z/OS: TCP Proxy Bridge Support
 * F247845         050131 mattheg  Multicast support
 * D70632          050422 mattheg  Defend against nulls
 * D270373         050721 mattheg  Remove temporary NLS messages
 * d274452         050823 gelderd  Merge TrmEndPoint and EndPoint classes
 * SIB0048b.com.6  060918 mattheg  Remove references to CFEndPoint
 * ============================================================================
 */
package com.ibm.ws.sib.comms;

import java.util.Map;

import com.ibm.ejs.ras.TraceNLS;
import com.ibm.websphere.sib.exception.SIErrorException;
import com.ibm.ws.sib.utils.comms.ProviderEndPoint;

/**
 * A class which represents a set of connection properties.
 * Feature 182479 makes this class bi-modal in so far as it can represent
 * either an endpoint object, which in effect is a hostname and port - or
 * it can represent a WLM endpoint.  Attempting to invoke the wrong getter
 * methods for the current mode of operation will result in an illegal
 * argument exception being thrown.
 * 
 * @see com.ibm.ws.sib.comms.ClientConnection#connect(ConnectionProperties, ClientComponentHandshake)
 * @see com.ibm.ws.sib.comms.MEConnection#connect(ConnectionProperties, MEComponentHandshake)
 */
public class ConnectionProperties
{  
   /** NLS */
   private static final TraceNLS nls = TraceNLS.getTraceNLS(CommsConstants.MSG_BUNDLE);
   
   private ProviderEndPoint endPoint;
   private Object wlmEndPointData;
   private PropertiesType mode;
   private Map clientConnectionProperties = null;
   public enum PropertiesType { WLM_EP, HOST_PORT, ZOS_PROXY };


   // Venu Liberty COMMS
   // directly assigning values .. to separate JFapChannelConstants
   /** Chain name for JFAP -> TCP channel */
   public final static String CHAIN_NAME_JFAP_TCP          = "BootstrapBasicMessaging";
   /** Chain name for JFAP -> SSL-> TCP channel */
   public final static String CHAIN_NAME_JFAP_SSL_TCP      = "BootstrapSecureMessaging";
   /** Chain name for JFAP -> HTTPT -> TCP channel */
   public final static String CHAIN_NAME_JFAP_HTTP_TCP     = "BootstrapTunneledMessaging";
   /** Chain name for JFAP -> HTTPT -> SSL -> TCP channel */
   public final static String CHAIN_NAME_JFAP_HTTP_SSL_TCP = "BootstrapTunneledSecureMessaging";
   
   /**
    * Creates a connection properties object for the z/OS TCP Proxy Bridge.
    * This is only valid for client connections.
    */      
   public ConnectionProperties()
   {
      mode = PropertiesType.ZOS_PROXY;
   }
   
   /**
    * Creates a connection properties object using the specified values. 
    * The object constructed will use the host/port mode of operation.
    * @param endPoint The end point.
    */   
   public ConnectionProperties(ProviderEndPoint endPoint)
   {
      if (endPoint == null)
      {
         throw new SIErrorException(
            nls.getFormattedMessage("NULL_EP_SICO0004", null, "NULL_EP_SICO0004")
         );
      }
      if (endPoint.getChain() == null)
      {
         throw new SIErrorException(
            nls.getFormattedMessage("NULL_CHAIN_SICO0005", null, "NULL_CHAIN_SICO0005")
         );
      }      
      
      this.endPoint = endPoint;
      mode = PropertiesType.HOST_PORT;
   }
   
   /**
    * Creates a connection properties object using the specified
    * WLM endpoint values.  The object constructed will use the
    * WLM endpoint mode of operation.
    */
   public ConnectionProperties(Object wlmEndPointData)
   {
      if (wlmEndPointData == null)
      {
         throw new SIErrorException(
            nls.getFormattedMessage("NULL_EP_SICO0004", null, "NULL_EP_SICO0004")
         );
      }
      
      this.wlmEndPointData = wlmEndPointData;
      mode = PropertiesType.WLM_EP;
   }

   /**
    * Returns the WLM endpoint data associated with this instance
    * of connection properties.  This is only valid if the instance
    * was created to use the WLM endpoint mode of operation.  An
    * IllegalArgumentException will be thrown, otherwise.
    */
   public Object getWLMEndPointData()
   {
      if (mode != PropertiesType.WLM_EP)
      {
         throw new SIErrorException(
            nls.getFormattedMessage("INVALID_METHOD_FOR_OBJECT_TYPE_SICO0006", null, "INVALID_METHOD_FOR_OBJECT_TYPE_SICO0006")  // D270373
         );
      }
      return wlmEndPointData;
   }
      
   /**
    * Returns the mode of operation that this connection properties
    * was created to use.
    */
   public PropertiesType getMode()
   {
      return mode;
   }
      
   /**
    * Returns the end point previously assigned.  This is only valid
    * when in hostname/port mode of operation - otherwise an illegal
    * argument exception will be thrown.
    * @return EndPoint The end point.
    */
   public ProviderEndPoint getEndPoint()
   {
      if (mode != PropertiesType.HOST_PORT)
      {
         throw new SIErrorException(
            nls.getFormattedMessage("INVALID_METHOD_FOR_OBJECT_TYPE_SICO0006", null, "INVALID_METHOD_FOR_OBJECT_TYPE_SICO0006")  // D270373
         );
      }
      
      return endPoint;
   }
   
   /**
    * Returns the Channel Framework chain name previously assigned.
    * This is only valid in hostname/port mode of operation - otherwise
    * an IllegalArgumentException will be thrown.
    * @return String The chain name.
    */
   public String getChainName()
   {
      if (mode != PropertiesType.HOST_PORT)
      {
         throw new SIErrorException(
            nls.getFormattedMessage("INVALID_METHOD_FOR_OBJECT_TYPE_SICO0006", null, "INVALID_METHOD_FOR_OBJECT_TYPE_SICO0006")  // D270373
         );
      }

      return endPoint.getChain();
   }
   
   /**
    * Used to set the list of properties used by the top level application into this class so that
    * it can be examined by the communications layer.
    * 
    * @param clientConnectionProperties The complete map of properties.
    */
   public void setClientConnectionPropertyMap(Map clientConnectionProperties)
   {
      this.clientConnectionProperties = clientConnectionProperties;
   }

   /**
    * Used to return the list of properties used by the top level application to initiate the 
    * connect. Further layers can look at these properties if they need to.
    * 
    * @return Returns a map of all the properties.
    */
   public Map getClientConnectionPropertyMap()
   {
      return clientConnectionProperties;
   }
   
   /**
    * @return Returns useful info about this object.
    */
   public String toString()
   {
      String s = "ConnectionProperties@" + Integer.toHexString(System.identityHashCode(this)) + ":- ";
      s+= "Mode: " + mode;
      
      if (mode == PropertiesType.HOST_PORT)
      {
         s += ", Endpoint: " + endPoint;
      }
      else if (mode == PropertiesType.WLM_EP)
      {
         s += ", CFEndpoint: " + wlmEndPointData;
      }
      
      s += ", ClientConnectionProperties: " + clientConnectionProperties;
      
      return s;
   }
}
