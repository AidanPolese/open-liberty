/*
 * @start_prolog@
 * Version: @(#) 1.16 SIB/ws/code/sib.comms.client.impl/src/com/ibm/ws/sib/comms/client/ConnectionMetaDataImpl.java, SIB.comms, WASX.SIB, uu1215.01 08/01/18 04:15:04 [4/12/12 22:14:06]
 * ============================================================================
 * IBM Confidential OCO Source Materials
 * 
 * 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70  Copyright IBM Corp. 2004, 2008 
 * 
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 * ============================================================================
 * @end_prolog@
 *
 * Change activity:
 *
 * CORE API 0.6 Implementation
 *
 * Reason          Date   Origin   Description
 * --------------- ------ -------- --------------------------------------------
 * Creation        030807 mattheg  Original
 * F224759.1       040818 prestona z/OS: isConnectionTrusted() method
 * F247975         050203 prestona Add requiresNonJavaBootstrap() method
 * F206161.5       050217 prestona Events for system management products
 * D354565         060320 prestona ClassCastException thrown during failover
 * SIB0153a.com    061030 mattheg  Add getSSLSession() method
 * SIB201a.com.1   070621 prestona Add getRemotePortNumber method.
 * D469880         071109 prestona Need to be able to get FAP level from comms
 * SIB0163.comms.3 071227 mleming  Provide information on the location that client is running in
 * ============================================================================
 */
package com.ibm.ws.sib.comms.client;

import java.net.InetAddress;
import javax.net.ssl.SSLSession;
import com.ibm.ws.sib.comms.CommsConstants;
import com.ibm.ws.sib.comms.ConnectionMetaData;
import com.ibm.ws.sib.comms.ProtocolVersion;
import com.ibm.ws.sib.jfapchannel.ConversationMetaData;
import com.ibm.ws.sib.jfapchannel.HandshakeProperties;

/**
 * Comms implementation of ConnectionMetaData that wrappers of JFAP ConversationMetData
 */
public class ConnectionMetaDataImpl implements ConnectionMetaData
{
   private final ConversationMetaData conversationMetaData;
   private final HandshakeProperties handshakeProperties;                              // F247975
   
   public ConnectionMetaDataImpl(ConversationMetaData conversationMetaData,
                                 HandshakeProperties handshakeProperties)        // F247975
   {
      this.conversationMetaData = conversationMetaData;
      this.handshakeProperties = handshakeProperties;                               // F247975
   }

   /** @see com.ibm.ws.sib.comms.ConnectionMetaData#getChainName() */
   public String getChainName() { return conversationMetaData.getChainName(); }

   /** @see com.ibm.ws.sib.comms.ConnectionMetaData#containsSSLChannel() */
   public boolean containsSSLChannel() { return conversationMetaData.containsSSLChannel(); }

   /** @see com.ibm.ws.sib.comms.ConnectionMetaData#containsHTTPTunnelChannel() */
   public boolean containsHTTPTunnelChannel() { return conversationMetaData.containsHTTPTunnelChannel(); }

   /** @see com.ibm.ws.sib.comms.ConnectionMetaData#isInbound() */
   public boolean isInbound() { return conversationMetaData.isInbound(); }
   
   /** @see com.ibm.ws.sib.comms.ConnectionMetaData#isTrusted() */
   public boolean isTrusted() { return conversationMetaData.isTrusted(); }         // F224759.1
   
   /** @see com.ibm.ws.sib.comms.ConnectionMetaData#getSSLSession() */
   public SSLSession getSSLSession() { return conversationMetaData.getSSLSession(); }

   /** @see com.ibm.ws.sib.comms.ConnectionMetaData#requiresNonJavaBootstrap() */
   // begin F247975
   public boolean requiresNonJavaBootstrap()
   {
      return (handshakeProperties.getCapabilites() & 
              CommsConstants.CAPABILITIY_REQUIRES_NONJAVA_BOOTSTRAP) == CommsConstants.CAPABILITIY_REQUIRES_NONJAVA_BOOTSTRAP;
   }
   // end F247975

   /** @see com.ibm.ws.sib.comms.ConnectionMetaData#getRemoteAddress() */
   // begin F246161.5
   public InetAddress getRemoteAddress()
   {
      return conversationMetaData.getRemoteAddress();
   }
   // end F246161.5

   /** @see com.ibm.ws.sib.comms.ConnectionMetaData#getRemotePortNumber() */
   public int getRemotePortNumber()
   {
      return conversationMetaData.getRemotePort();
   }

   /** @see com.ibm.ws.sib.comms.ConnectionMetaData#getProtocolVersion() */
   public ProtocolVersion getProtocolVersion()
   {
      final short fapLevel = handshakeProperties.getFapLevel();
      ProtocolVersion result = ProtocolVersion.UNKNOWN;

      if (fapLevel == 1)                       result = ProtocolVersion.VERSION_6_0;
      else if (fapLevel >= 2 && fapLevel <= 4) result = ProtocolVersion.VERSION_6_0_2;
      else if (fapLevel >= 5 && fapLevel <= 8) result = ProtocolVersion.VERSION_6_1;
      else if (fapLevel >= 9)                  result = ProtocolVersion.VERSION_7;
      
      return result;
   }

   /**
    * @see com.ibm.ws.sib.comms.ConnectionMetaData#getRemoteCellName()
    */
   public String getRemoteCellName() 
   {
      return handshakeProperties.getRemoteCellName();
   }

   /**
    * @see com.ibm.ws.sib.comms.ConnectionMetaData#getRemoteNodeName()
    */
   public String getRemoteNodeName() 
   {
      return handshakeProperties.getRemoteNodeName();
   }

   /**
    * @see com.ibm.ws.sib.comms.ConnectionMetaData#getRemoteServerName()
    */
   public String getRemoteServerName() 
   {
      return handshakeProperties.getRemoteServerName();
   }

   /**
    * @see com.ibm.ws.sib.comms.ConnectionMetaData#getRemoteClusterName()
    */
   public String getRemoteClusterName() 
   {
      return handshakeProperties.getRemoteClusterName();
   }
}
