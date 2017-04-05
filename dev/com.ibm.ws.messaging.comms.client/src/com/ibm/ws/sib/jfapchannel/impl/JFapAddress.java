/*
 * @start_prolog@
 * Version: @(#) 1.21 SIB/ws/code/sib.jfapchannel.client.common.impl/src/com/ibm/ws/sib/jfapchannel/impl/JFapAddress.java, SIB.comms, WASX.SIB, uu1215.01 08/01/18 04:25:33 [4/12/12 22:14:13]
 * ============================================================================
 * IBM Confidential OCO Source Materials
 *
 * 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70  Copyright IBM Corp. 2003, 2008
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
 * Creation        030424 prestona Original
 * F166959         030521 prestona Rebase on non-prototype CF + TCP Channel
 * F167363         030523 prestona Rebase on LIBD_1891_2255 CF + TCP Channel
 * D181601         031031 prestona Improve quality of JFAP Channel RAS
 * F188491         030128 prestona Migrate to M6 CF + TCP Channel
 * D199145         040812 prestona Fix Javadoc
 * D330649         051209 prestona Supply an outbound protocol
 * SIB0048b.com.5  060913 mattheg  JFap channel for Portly client rework
 * PK58698         080102 vaughton Connect timeout property
 * ============================================================================
 */
package com.ibm.ws.sib.jfapchannel.impl;

import java.net.InetSocketAddress;

import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.ffdc.FFDCFilter;                                                                             //PK58698
import com.ibm.ws.sib.jfapchannel.Conversation;
import com.ibm.ws.sib.jfapchannel.JFapChannelConstants;
import com.ibm.ws.sib.jfapchannel.framework.NetworkConnectionTarget;
import com.ibm.ws.sib.utils.RuntimeInfo;                                                                       //PK58698
import com.ibm.ws.sib.utils.ras.SibTr;
import com.ibm.wsspi.tcpchannel.TCPConnectRequestContext;              // f167363, F188491

/**
 * Address object passed to the JFAP channel chain when establishing
 * an outbound connection.
 * @author prestona
 */
public class JFapAddress implements NetworkConnectionTarget
{
   private static final TraceComponent tc = SibTr.register(JFapAddress.class, JFapChannelConstants.MSG_GROUP, JFapChannelConstants.MSG_BUNDLE);

   static {
     if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) SibTr.debug(tc, "@(#) SIB/ws/code/sib.jfapchannel.client.common.impl/src/com/ibm/ws/sib/jfapchannel/impl/JFapAddress.java, SIB.comms, WASX.SIB, uu1215.01 1.21");
   }

   private final InetSocketAddress remoteAddress;
   private final Conversation.ConversationType attachType;

   /**
    * Creates a new JFAP address.
    * @param remoteAddress the remote host to connect to.
    * @param attachType the type of outbound connection being established.
    */
   public JFapAddress(InetSocketAddress remoteAddress, Conversation.ConversationType attachType)
   {
      if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.entry(this, tc, "<init>", new Object[]{remoteAddress, attachType});
      this.remoteAddress = remoteAddress;
      this.attachType = attachType;
      if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.exit(this, tc, "<init>");
   }

   /**
    * Retrieve the address of the local NIC to bind to.
    * @see TCPConnectRequestContext#getLocalAddress()
    */
   public InetSocketAddress getLocalAddress()
   {
      if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.entry(this, tc, "getLocalAddress");
      if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.exit(this, tc, "getLocalAddress","rc="+null);
      return null;
   }

   /**
    * Retrieves the address of the remote address to connect to.
    * @see TCPConnectRequestContext#getRemoteAddress()
    */
   public InetSocketAddress getRemoteAddress()
   {
      if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.entry(this, tc, "getRemoteAddress");
      if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.exit(this, tc, "getRemoteAddress","rc="+remoteAddress);
      return remoteAddress;
   }

   // begin F188491
   /** @see com.ibm.wsspi.tcp.channel.TCPConnectRequestContext#getConnectTimeout() */
   public int getConnectTimeout()                                                                              //PK58698
   {
     if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.entry(this, tc, "getConnectTimeout");

     int seconds = JFapChannelConstants.CONNECT_TIMEOUT_DEFAULT;
     try {
       seconds = Integer.parseInt(RuntimeInfo.getPropertyWithMsg(JFapChannelConstants.CONNECT_TIMEOUT_JFAP_KEY, Integer.toString(seconds)));
     } catch (NumberFormatException e) {
       FFDCFilter.processException(e, "com.ibm.ws.sib.jfapchannel.impl.JFapAddress.getConnectTimeout", JFapChannelConstants.JFAPADDRESS_GETCONNECTTIMEOUT_01);
     }
     final int timeout = seconds * 1000; // seconds -> milliseconds

     if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.exit(this, tc, "getConnectTimeout","rc="+timeout);
     return timeout;
   }                                                                                                           //PK58698
   // end F188491

   /** Returns the type of connection that this address will be used to establish */
   public Conversation.ConversationType getAttachType()
   {
      return attachType;
   }

   public String toString()
   {
      return super.toString()+" [remoteAddress: "+remoteAddress+" attachType:"+attachType+"]";
   }
}
