/*
 * @start_prolog@
 * Version: @(#) 1.17 SIB/ws/code/sib.jfapchannel.client.rich.impl/src/com/ibm/ws/sib/jfapchannel/impl/ConversationMetaDataImpl.java, SIB.comms, WASX.SIB, uu1215.01 09/07/21 03:43:06 [4/12/12 22:14:21]
 * ============================================================================
 * IBM Confidential OCO Source Materials
 * 
 * 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70 Copyright IBM Corp. 2004, 2009
 * 
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 * ============================================================================
 * @end_prolog@
 *
 * Change activity:
 *
 * Reason          Date   Origin   Description
 * --------------- ------ -------- --------------------------------------------
 * D196678.10.1    040525 prestona Add conversation metadata method.
 * D197042         040811 prestona FFDC entries
 * F224759.1       040818 prestona z/OS: isConnectionTrusted() method
 * D229536         040906 prestona Make isTrusted a channel custom property
 * F244595         041129 prestona z/OS: TCP Proxy Bridge Support
 * F206161.5       050217 prestona Events for system management products
 * D320083         051103 mattheg  Expose remote port
 * D345065         060209 prestona Avoid spooking users by tracing class not found for TCP proxy
 * D341600         060810 prestona Fix Java 5 compiler warnings
 * SIB0153a.com    061030 mattheg  Add getSSLSession() method
 * D590533         090720 mleming  Don't try and load TCPProxy code on z/OS
 * ============================================================================
 */
package com.ibm.ws.sib.jfapchannel.richclient.impl;

import java.net.InetAddress;

import javax.net.ssl.SSLSession;

import com.ibm.websphere.channelfw.ChainData;
import com.ibm.websphere.channelfw.ChannelData;
import com.ibm.websphere.channelfw.FlowType;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.sib.jfapchannel.ConversationMetaData;
import com.ibm.ws.sib.jfapchannel.JFapChannelConstants;
import com.ibm.ws.sib.utils.ras.SibTr;
import com.ibm.wsspi.channelfw.ConnectionLink;
import com.ibm.wsspi.tcpchannel.SSLConnectionContext;
import com.ibm.wsspi.tcpchannel.TCPConnectionContext;

/**
 * Implementation of conversation meta data interface.
 * @see com.ibm.ws.sib.jfapchannel.ConversationMetaData
 */
public class ConversationMetaDataImpl implements ConversationMetaData
{
   private static final TraceComponent tc = SibTr.register(ConversationMetaData.class, JFapChannelConstants.MSG_GROUP, JFapChannelConstants.MSG_BUNDLE);
   
   private String chainName;
   private boolean containsSSLChannel, containsHTTPTunnelChannel, isInbound;
   private static Class sslChannelFactoryClass;
   private static Class httptChannelFactoryClass;
   private static Class tcpProxyChannelFactoryClass;                    // F244595
   private boolean isTrusted = false;                                               // D224759.1, D229536
   //Romil liberty changes changed BaseChannelLink to ChannelLink
   private ConnectionLink baseLink;                                             // F206161.5
   static 
   {
      if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) SibTr.debug(tc, "@(#) SIB/ws/code/sib.jfapchannel.client.rich.impl/src/com/ibm/ws/sib/jfapchannel/impl/ConversationMetaDataImpl.java, SIB.comms, WASX.SIB, uu1215.01 1.17");
      try
      {
         sslChannelFactoryClass = Class.forName(JFapChannelConstants.CLASS_SSL_CHANNEL_FACTORY);
      }
      catch (ClassNotFoundException e)
      {
         // No FFDC code needed
         if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) SibTr.debug(tc, "Could not find SSL Channel class");
         if (TraceComponent.isAnyTracingEnabled() && tc.isEventEnabled()) SibTr.exception(tc, e);
      }
      
      try
      {
         httptChannelFactoryClass = Class.forName(JFapChannelConstants.CLASS_HTTPT_CHANNEL_FACTORY);
      }
      catch (ClassNotFoundException e)
      {
         // No FFDC code needed
         if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) SibTr.debug(tc, "Could not find HTTP Tunnel Channel class");
         if (TraceComponent.isAnyTracingEnabled() && tc.isEventEnabled()) SibTr.exception(tc, e);         
      }
      
      //Don't bother doing this unless on z/OS.
      // Romil liberty changes
//      if(PlatformHelperFactory.getPlatformHelper().isZOS())
//      {
//         try
//         {
//            tcpProxyChannelFactoryClass = Class.forName(JFapChannelConstants.CLASS_TCPPROXY_CHANNEL_FACTORY);
//         }
//         catch (ClassNotFoundException e)
//         {
//            // No FFDC code needed
//            if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) SibTr.debug(tc, "Could not find TCP Proxy Bridge Service Channel class");
//         }
//      }      
   }
   // Romil liberty change make this method public so that server pacakge can access it
   public ConversationMetaDataImpl(ChainData chainData, ConnectionLink baseLink)            // F206161.5
   {
      if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.entry(this, tc, "<init>", new Object[]{chainData, baseLink});   // F206161.5
      
      isTrusted = false;                                                      // D224759.1
      chainName = chainData.getName();
      isInbound = chainData.getType() == FlowType.INBOUND;
      this.baseLink = baseLink;
      
      ChannelData[] channelData = chainData.getChannelList();
      containsSSLChannel = false;
      containsHTTPTunnelChannel = false;
      for (int i=0; i < channelData.length; ++i)
      {
         if (sslChannelFactoryClass != null)
         {
            containsSSLChannel |= 
               channelData[i].getFactoryType().equals(sslChannelFactoryClass);
         }
         if (httptChannelFactoryClass != null)
         {
            containsHTTPTunnelChannel |=
               channelData[i].getFactoryType().equals(httptChannelFactoryClass);
         }
         // begin F244595
         if (tcpProxyChannelFactoryClass != null)
         {
            isTrusted |=
               channelData[i].getFactoryType().equals(tcpProxyChannelFactoryClass);
         }
         // end F244595
      }
      if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) SibTr.debug(this, tc, "chainName="+chainName+
                                                     "\nisInbound="+isInbound+
                                                     "\ncontainsSSLChannel="+containsSSLChannel+
                                                     "\ncontainsHTTPTunnelChannel="+containsHTTPTunnelChannel+
                                                     "\nisTrusted="+isTrusted);     // D228536
      
      if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.exit(this, tc, "<init>");
   }
   
   /** @see com.ibm.ws.sib.jfapchannel.ConversationMetaData#getChainName() */
   public String getChainName()               { return chainName; }

   /** @see com.ibm.ws.sib.jfapchannel.ConversationMetaData#containsSSLChannel() */
   public boolean containsSSLChannel()        { return containsSSLChannel; }

   /** @see com.ibm.ws.sib.jfapchannel.ConversationMetaData#containsHTTPTunnelChannel() */
   public boolean containsHTTPTunnelChannel() { return containsHTTPTunnelChannel; }

   /** @see com.ibm.ws.sib.jfapchannel.ConversationMetaData#isInbound() */
   public boolean isInbound()                 { return isInbound; }

   /** @see com.ibm.ws.sib.jfapchannel.ConversationMetaData#isTrusted() */
   public boolean isTrusted()                 { return isTrusted; }              // D224759.1

   /** @see com.ibm.ws.sib.jfapchannel.ConversationMetaData#getRemoteAddress() */
   // begin F206161.5
   public InetAddress getRemoteAddress()
   {
      if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.entry(this, tc, "getRemoteAddress");
      InetAddress result = null;
      TCPConnectionContext tcpContext = null;
      ConnectionLink connLinkRef = baseLink.getDeviceLink();
      if (connLinkRef != null)
      {
         tcpContext = (TCPConnectionContext)connLinkRef.getChannelAccessor();
         if (tcpContext != null) result = tcpContext.getRemoteAddress();
      }
      if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.exit(this, tc, "getRemoteAddress", result);
      return result;
   }
   // end F206161.5
   
   /**
    * @return Returns the remote port number. Returns 0 if it is not known.
    */
   public int getRemotePort()
   {
      if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.entry(this, tc, "getRemotePort");
      
      int portNumber = 0;
      TCPConnectionContext tcpContext = null;
      ConnectionLink connLinkRef = baseLink.getDeviceLink();
      if (connLinkRef != null)
      {
         tcpContext = (TCPConnectionContext)connLinkRef.getChannelAccessor();
         if (tcpContext != null) portNumber = tcpContext.getRemotePort();
      }
      
      if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.exit(this, tc, "getRemotePort", ""+portNumber);
      return portNumber;
   }
   
   /**
    * @return Returns the SSL session from the TCPConnectionContext
    * @see com.ibm.ws.sib.jfapchannel.ConversationMetaData#getSSLSession()
    */
   public SSLSession getSSLSession()
   {
      if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.entry(this, tc, "getSSLSession");
      
      SSLSession sslSession = null;
      TCPConnectionContext tcpContext = null;
      ConnectionLink connLinkRef = baseLink.getDeviceLink();
      
      if (connLinkRef != null)
      {
         tcpContext = (TCPConnectionContext)connLinkRef.getChannelAccessor();
         if (tcpContext != null)
         {
            SSLConnectionContext sslConnCtx = tcpContext.getSSLContext();
            if (sslConnCtx != null) sslSession = sslConnCtx.getSession();
         }
      }
 
      if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.exit(this, tc, "getSSLSession", sslSession);
      return sslSession;
   }
}
