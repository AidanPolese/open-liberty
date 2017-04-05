/*
 * @start_prolog@
 * Version: @(#) 1.20 SIB/ws/code/sib.comms.client.impl/src/com/ibm/ws/sib/comms/client/Proxy.java, SIB.comms, WASX.SIB, uu1215.01 08/04/29 21:36:49 [4/12/12 22:14:06]
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
 * Reason          Date   Origin   Description
 * --------------- ------ -------- --------------------------------------------
 * Creation        030325 niall    Original
 * F166959         030521 niall    Rebase on non-prototype CF + TCP Channel
 * d168211         030605 mattheg  Allow checking of closed sessions / connections
 * d170527         030625 mattheg  Tidy and change to SibTr
 * F168604.2       030601 prestona Exploit asynchronous client API.
 * f173559         030807 mattheg  Move location of JFAPCommunicator
 * F174602         030819 prestona Switch to using SICommsException
 * F201972.2       040727 mattheg  Core SPI Exceptions rework (not change flagged)
 * D225856         041006 mattheg  Update FFDC class name (not change flagged)
 * D276260         050516 mattheg  Add hashcode to trace (not change flagged)
 * SIB0048b.com.1  060901 mattheg  Use seperate JFAP Communicators for client / server
 * 514462          080421 vaughton Further performance optimisation
 * 516882          080430 sibcopyr Automatic update of trace guards 
 * ============================================================================
 */
package com.ibm.ws.sib.comms.client;

import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ejs.ras.TraceNLS;
import com.ibm.websphere.sib.exception.SIErrorException;
import com.ibm.ws.ffdc.FFDCFilter;
import com.ibm.ws.sib.comms.CommsConstants;
import com.ibm.ws.sib.jfapchannel.Conversation;
import com.ibm.ws.sib.utils.ras.SibTr;

/**
 * This class is the root class for all client side Core SPI proxy implementations.
 *
 * @author niall
 */
public abstract class Proxy extends ClientJFapCommunicator
{
   /** Class name for FFDC's */
   private static String CLASS_NAME = Proxy.class.getName();

   /** Register Class with Trace Component */
   private static final TraceComponent tc = SibTr.register(Proxy.class,
                                                           CommsConstants.MSG_GROUP,
                                                           CommsConstants.MSG_BUNDLE);

   /** NLS handle */
   private static final TraceNLS nls = TraceNLS.getTraceNLS(CommsConstants.MSG_BUNDLE);

   /** Log Source code level on static load of class */
   static
   {
      if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) SibTr.debug(tc, "Source info: @(#)SIB/ws/code/sib.comms.client.impl/src/com/ibm/ws/sib/comms/client/Proxy.java, SIB.comms, WASX.SIB, uu1215.01 1.20");
   }

   /**
    * The Proxy ID that maps to the real Object on the server
    */
   private short proxyID = -1;

   /**
    * Flag used to determine if the proxy ID has been set or not.  We cannot
    * use a "special" proxy value to determine this as the short value
    * may wrap around to the "special" value.
    */
   private boolean proxyIDSet = false;

   /**
     * Flag to indicate whether the object has been closed
     */
   private volatile boolean closed = false;

   /**
    * A reference to the Connection Object
    */
   private ConnectionProxy connectionProxy = null;

   /**
    *
    * @param con A reference to the Conversation with the ME
    * @param cp A reference to the Connection Proxy (root of proxy structure)
    */
   protected Proxy(Conversation con, ConnectionProxy cp)
   {
      if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.entry(this, tc, "<init>", new Object[] {con, cp});

      //Store the reference to the conversation with the ME
      setConversation(con);

      //and a reference to the Connection Proxy
      connectionProxy = cp;

      if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.exit(this, tc, "<init>");
   }

   /**
    * Marks this proxy object as being open.
    *
    */
   protected void setOpen () {
     if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.entry(this, tc, "setOpen");
     closed = false;
     if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.exit(this, tc, "setOpen");
   }

   /**
    * This method identifies whether we are able to close this proxy
    * object. If this object represents a session object, then this
    * can only be closed if we have not been closed and if the connection
    * has not been closed. If it represents a connection, then we
    * can only close if we have not already been closed.
    *
    * @return Returns true if we have already been closed or if the
    *          underlying connection has been closed.
    */
   public boolean isClosed()
   {
      if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.entry(this, tc, "isClosed");
      boolean retValue = false;

      if (connectionProxy == null)
      {
         retValue = closed;
      }
      else
      {
         retValue = closed || connectionProxy.isClosed();
      }

      if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.exit(this, tc, "isClosed", ""+retValue);
      return retValue;
   }

   /**
    * Marks this proxy object as being closed.
    *
    */
   protected void setClosed()
   {
      if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.entry(this, tc, "setClosed");
      closed = true;
      if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.exit(this, tc, "setClosed");
   }

   /**
    * Returns the proxy's Id correspondoing to the real object on the server
    * @return ID The ID of the proxy object
    */
   public short getProxyID()
   {
      if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.entry(this, tc, "getProxyID");

      if (!proxyIDSet)
      {
         // Someone is trying to use the ID of this proxy object but it has not been set yet. This
         // is an error as the ID will be invalid anyway.
         SIErrorException e = new SIErrorException(
            nls.getFormattedMessage("PROXY_ID_NOT_SET_SICO1052", null, null)
         );

         FFDCFilter.processException(e, CLASS_NAME + ".getProxyID",
                                     CommsConstants.PROXY_GETPROXYID_01, this);

         throw e;
      }

      if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.exit(this, tc, "getProxyID", ""+proxyID);
      return proxyID;
   }

   /**
    * Returns a reference to the Connection Proxy
    * @return ConnectionProxy The Connection proxy
    */
   protected ConnectionProxy getConnectionProxy()
   {
      if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.entry(this, tc, "getConnectionProxy");

      if (connectionProxy == null)
      {
         // Someone is trying to use the connection proxy associated with this proxy object but it
         // has not been set or has been nulled out.
         SIErrorException e = new SIErrorException(
            nls.getFormattedMessage("CONNECTION_PROXY_NOT_SET_SICO1053", null, null)
         );

         FFDCFilter.processException(e, CLASS_NAME + ".getConnectionProxy",
                                     CommsConstants.PROXY_GETCONNECTIONPROXY_01, this);

         throw e;
      }

      if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.exit(this, tc, "getConnectionProxy", connectionProxy);
      return connectionProxy;
   }

   /**
    * Sets the ID corresponding to the real object on the server
    * @param s
    */
   protected void setProxyID(short s)
   {
      if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.entry(this, tc, "setProxyID", ""+s);
      proxyID = s;
      proxyIDSet = true;
      if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.exit(this, tc, "setProxyID");
   }

}
