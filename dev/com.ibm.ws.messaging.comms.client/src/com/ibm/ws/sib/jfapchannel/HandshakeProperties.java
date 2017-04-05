/*
 * @start_prolog@
 * Version: @(#) 1.4 SIB/ws/code/sib.jfapchannel.client/src/com/ibm/ws/sib/jfapchannel/HandshakeProperties.java, SIB.comms, WASX.SIB, uu1215.01 08/01/18 03:47:13 [4/12/12 22:14:12]
 * ============================================================================
 * IBM Confidential OCO Source Materials
 * 
 * 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70  Copyright IBM Corp. 2006, 2008 
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
 * D354565         060320 prestona ClassCastException thrown during failover
 * SIB0163.comms.3 071227 mleming  Provide information on the location that client is running in
 * ============================================================================ 
 */
package com.ibm.ws.sib.jfapchannel;

/**
 * Properties pertaining to the handshaking of a JFAP connection.  These properties may
 * be associated with a link level connection via invoking the conversation.setHandshakeProperties
 * method. 
 */
public interface HandshakeProperties 
{
   /**
    * @return the version of the FAP being used for this link.
    */
   short getFapLevel();
   
   /**
    * @return a bitmap providing information about the capabilities supported by the link.
    */
   short getCapabilites();
   
   /**
    * @return the major version of the Core SPI being used via the link.
    */
   short getMajorVersion();
   
   /**
    * @return the minor version of the Core SPI being used via the link.
    */
   short getMinorVersion();
   
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
    * NB: A non-null value will only be returned if the remote process established the connection.
    * 
    * @return the name of the WAS cell that the remote process is running in, or null if the information is not known.
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
    * NB: A non-null value will only be returned if the remote process established the connection.
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
    * NB: A non-null value will only be returned if the remote process established the connection.
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
    * NB: A non-null value will only be returned if the remote process established the connection.
    * 
    * @return the name of the WAS cluster that the remote process is running in, or null if the information is not known.
    */
   String getRemoteClusterName();   
}
