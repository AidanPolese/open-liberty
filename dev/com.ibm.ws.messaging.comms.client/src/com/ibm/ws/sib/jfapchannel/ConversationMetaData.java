/*
 * @start_prolog@
 * Version: @(#) 1.11 SIB/ws/code/sib.jfapchannel.client/src/com/ibm/ws/sib/jfapchannel/ConversationMetaData.java, SIB.comms, WASX.SIB, uu1215.01 06/10/31 02:44:20 [4/12/12 22:14:12]
 * ============================================================================
 * IBM Confidential OCO Source Materials
 * 
 * 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5724-J08  (C) Copyright IBM Corp. 2004, 2006 
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
 * D196678.10.1    040521 prestona Add conversation metadata method.
 * D199145         040812 prestona Fix Javadoc
 * F224759.1       040818 prestona z/OS: isConnectionTrusted() method
 * F206161.5       050217 prestona Events for system management products
 * D320083         051103 mattheg  Expose remote port
 * SIB0153a.com    061030 mattheg  Add getSSLSession() method
 * ============================================================================
 */
package com.ibm.ws.sib.jfapchannel;

import java.net.InetAddress;

import javax.net.ssl.SSLSession;

/**
 * Meta information about a conversation.
 */
public interface ConversationMetaData
{
   /**
    * The chain name that conversation has been established over.
    * @return Chain name.
    */
   String getChainName();
   
   /**
    * True if the conversation passed through a SSL channel.  False otherwise.
    * @return True iff the conversation passed through an SSL channel.
    */
   boolean containsSSLChannel();
   
   /**
    * True if the conversation passed through a HTTP tunnel channel.  False otherwise.
    * @return True iff the conversation passed through a HTTP tunnel channel.
    */
   boolean containsHTTPTunnelChannel();
   
   /**
    * True if the conversation is inbound.  I.e. the conversation was established by our peer.
    * False if the conversation is outbound (ie. we established the conversation with our peer).
    * @return True iff the conversation is inbound
    */
   boolean isInbound();

   /**
    * @return True if the conversation is over a "trusted" connection.  An example of such
    * a connection would be the z/OS cross memory channel.
    */
   boolean isTrusted();                                                 // F224759.1
   
   /**
    * @return Network address of the peer to which this connection is connected.
    * The value returned might be:<ul>
    * <li>The network address.</li>
    * <li>A network address - but not the address of the peer (in the
    * eventuality that network address translation or other obfuscation has
    * taken place).</li>
    * <li>null - in the case where a network address doesn't make any sense.
    * For example, where the underlying communications medium is not a network.</li>
    * </ul>
    */
   InetAddress getRemoteAddress();                                       // F206161.5
   
   /**
    * @return Returns the port number of the remote connection. If it is not known, 0 is returned.
    */
   int getRemotePort();
   
   /**
    * @return Returns the SSL Session associated with the connection (or null if this is
    *         not an SSL connection).
    */
   SSLSession getSSLSession();
}
