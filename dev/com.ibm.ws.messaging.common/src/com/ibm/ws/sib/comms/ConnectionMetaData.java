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
 * D196678.10.1    040521 prestona Add connection metadata method.
 * D199148         040812 mattheg  JavaDoc
 * F224759.1       040818 prestona z/OS: isConnectionTrusted() method
 * F247975         050202 prestona Add requiresNonJavaBootstap method
 * F206161.5       050217 prestona Events for system management products
 * SIB0153a.com    061030 mattheg  Add getSSLSession() method
 * SIB201a.com.1    070627 prestona Add getRemotePortNumber() method.
 * 469880          071109 prestona Need to be able to get FAP level from comms
 * SIB0163.comms.3 071227 mleming  Provide information on the location that client is running in
 * ============================================================================
 */
package com.ibm.ws.sib.comms;

import java.net.InetAddress;

import javax.net.ssl.SSLSession;

/**
 * Meta information about a connection.
 */
public interface ConnectionMetaData
{
   /**
    * The chain name that connection has been made over.
    * @return Chain name.
    */
   String getChainName();
   
   /**
    * @return Returns true if the connection passed through a SSL channel.  False otherwise.
    */
   boolean containsSSLChannel();
   
   /**
    * @return Returns true if the connection passed through a HTTP tunnel channel.  False otherwise.
    */
   boolean containsHTTPTunnelChannel();
   
   /**
    * @return Returns true if the connection is inbound (ie. was established by our peer).  False if
    * the connection is outbound (ie. we established the connection with our peer).
    */
   boolean isInbound();

   /**
    * @return Returns true if user information passed over this connection can be trusted.
    * The implication of this trust is that we assume our peer has verified security
    * credentials passed over the connection.  Clearly this method should never return true
    * unless the underlying transport is secured.
    */
   boolean isTrusted();                                           // D224759.1
   
   /**
    * @return Returns true if the peer cannot tolerate the use of Java specific features
    * (for example Java serialisation of objects) as part of the bootstrap process.
    */
   boolean requiresNonJavaBootstrap();                            // F247975
   
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
   InetAddress getRemoteAddress();                                   // F246161.5
   
   /**
    * @return the remote port number that our peer may have their end of the socket
    *         bound to.
    */
   int getRemotePortNumber();
   
   /**
    * @return Returns the SSL Session associated with the connection (or null if this is
    *         not an SSL connection).
    */
   SSLSession getSSLSession();
   
   /**
    * @return the version number, corresponding to the protocol version being used to
    *         communicate with the peer.
    */
   ProtocolVersion getProtocolVersion();
   
   /**
    * Provides the name of the WAS cell that the remote process is associated with. 
    * 
    * <br>
    * This method can return null for several reasons:
    * <ul>
    *    <li>The cell name of the remote process can't be established</li>
    *    <li>The remote process didn't establish this connection</li>
    *    <li>The remote process isn't running inside an AppServer</li>
    * </ul>
    * <br>
    * 
    * NB: A non-null value will only be returned if the remote process established the connection. I.e. <code>isInbound()</code> returns true.
    * 
    * @return the name of the WAS cell that the remote process is running, in or null if the information is not known.
    */
   String getRemoteCellName();
   
   /**
    * Provides the name of the WAS node that the remote process is associated with. 
    * <br>
    * This method can return null for several reasons:
    * <ul>
    *    <li>The node name of the remote process can't be established</li>
    *    <li>The remote process didn't establish this connection</li>
    *    <li>The remote process isn't running inside an AppServer</li>
    * </ul>
    * <br>
    * 
    * NB: A non-null value will only be returned if the remote process established the connection. I.e. <code>isInbound()</code> returns true.
    * 
    * @return the name of the WAS node that the remote process is running in, or null if the information is not known.
    */
   String getRemoteNodeName();
   
   /**
    * Provides the name of the WAS server that the remote process is associated with. 
    * 
    * <br>
    * This method can return null for several reasons:
    * <ul>
    *    <li>The server name of the remote process can't be established</li>
    *    <li>The remote process didn't establish this connection</li>
    *    <li>The remote process isn't running inside an AppServer</li>
    * </ul>
    * <br>
    * 
    * NB: A non-null value will only be returned if the remote process established the connection. I.e. <code>isInbound()</code> returns true.
    * 
    * @return the name of the WAS server that the remote process is running in, or null if the information is not known.
    */
   String getRemoteServerName();
   
   /**
    * Provides the name of the WAS cluster that the remote process is associated with. 
    * 
    * <br>
    * This method can return null for several reasons:
    * <ul>
    *    <li>The remote process isn't running in a WAS cluster</li>
    *    <li>The cluster name of the remote process can't be established</li>
    *    <li>The remote process didn't establish this connection</li>
    *    <li>The remote process isn't running inside an AppServer</li>
    * </ul>
    * <br>
    * 
    * NB: A non-null value will only be returned if the remote process established the connection. I.e. <code>isInbound()</code> returns true.
    * 
    * @return the name of the WAS cluster that the remote process is running in, or null if the information is not known.
    */
   String getRemoteClusterName();   
}
