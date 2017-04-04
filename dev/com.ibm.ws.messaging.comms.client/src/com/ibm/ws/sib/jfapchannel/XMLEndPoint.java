/*
 * @start_prolog@
 * Version: @(#) 1.1 SIB/ws/code/sib.jfapchannel.client/src/com/ibm/ws/sib/jfapchannel/XMLEndPoint.java, SIB.comms, WASX.SIB, uu1215.01 06/09/14 10:02:57 [4/12/12 22:14:17]
 * ============================================================================
 * IBM Confidential OCO Source Materials
 * 
 * 5724-J08, 5724-I63, 5724-H88, 5655-N01, 5733-W61  (C) Copyright IBM Corp. 2003, 2006 
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
 * 336594          060109 prestona JFAP channel for thin client
 * SIB0048b.com.5  060913 mattheg  JFap channel for Portly client rework
 * ============================================================================
 */
package com.ibm.ws.sib.jfapchannel;

import java.net.InetAddress;

/**
 * The interface for an endpoint that was created from XML information given back from a TRM 
 * handshake. This is needed in the portly client where an XML TRM handshake is performed to work
 * out the endpoint of the correct ME.
 * 
 * @author Adrian Preston
 */
public abstract class XMLEndPoint
{
   /** Enum of the possible chain types this endpoint could represent */
   public static enum ChainTypeEnumeration { HTTP, HTTPS, SSL, TCP, UNKNOWN };
   
   /**
    * @return Returns the address this endpoint is connecting to.
    */
   public abstract InetAddress getAddress();
   
   /**
    * @return Returns the port this endpoint is connecting to.
    */
   public abstract int getPort();
   
   /**
    * @return Returns the chain type enumeration.
    */
   public abstract ChainTypeEnumeration getType();
   
   /**
    * @see java.lang.Object#equals(java.lang.Object)
    */
   public final boolean equals(Object o)
   {
      boolean result;
      
      if (o instanceof XMLEndPoint)
      {
         XMLEndPoint ep = (XMLEndPoint)o;
         result = getAddress().equals(ep.getAddress()) &&
                  (getPort() == ep.getPort()) &&
                  (getType() == ep.getType());
      }
      else
      {
         result = false;
      }
      return result;
   }

   /**
    * @see java.lang.Object#hashCode()
    */
   public final int hashCode()
   {
      return getAddress().hashCode() ^ 
             getPort() ^
             getType().hashCode();
   }
   
   // These are methods that are present on a real CFEndPoint but are not needed for us
   
//   public final String getName() {throw new RuntimeException();}
//   public final void setName(String name) {throw new RuntimeException();}
//   public final String getVhost() {throw new RuntimeException();}
//   public final OutboundChannelDefinition[] getOutboundChannelDefs() {throw new RuntimeException();}
//   public final Class getChannelAccessor() {throw new RuntimeException();}
//   public final WSChainData createOutboundChain() throws ChannelException, ChainException {throw new RuntimeException();}
//   public final WSVirtualConnectionFactory getOutboundVCFactory() {throw new RuntimeException();}
//   public final void setOutboundVCFactory(WSVirtualConnectionFactory vcf) {throw new RuntimeException();}
//   public final boolean isSSLEnabled() {throw new RuntimeException();}
//   public final boolean isLocal() {throw new RuntimeException();}
//   public final WSVirtualConnectionFactory getOutboundVCFactory(Map sslProps, boolean overwriteExisting) throws IllegalArgumentException {throw new RuntimeException();}
//   public final String serializeToXML() throws NotSerializableException {throw new RuntimeException();}
}
