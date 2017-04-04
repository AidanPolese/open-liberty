/*
 * @start_prolog@
 * Version: @(#) 1.1 SIB/ws/code/sib.jfapchannel.client.rich.impl/src/com/ibm/ws/sib/jfapchannel/framework/impl/CFWIOBaseContext.java, SIB.comms, WASX.SIB, uu1215.01 06/09/14 10:09:58 [4/12/12 22:14:19]
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
 * SIB0048b.com.5  060913 mattheg  JFap channel for Portly client rework
 * ============================================================================
 */
package com.ibm.ws.sib.jfapchannel.richclient.framework.impl;

import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.sib.jfapchannel.JFapChannelConstants;
import com.ibm.ws.sib.jfapchannel.framework.NetworkConnection;
import com.ibm.ws.sib.utils.ras.SibTr;
import com.ibm.wsspi.channelfw.VirtualConnection;

/**
 * This class is extended by both the CFWIOReadRequestContext and CFWIOWriteRequestContext classes
 * to provide any common function.
 * 
 * @author Gareth Matthews
 */
public abstract class CFWIOBaseContext
{
   /** Trace */
   private static final TraceComponent tc = SibTr.register(CFWIOBaseContext.class, 
                                                           JFapChannelConstants.MSG_GROUP, 
                                                           JFapChannelConstants.MSG_BUNDLE);
   
   /** Log class info on load */
   static
   {
      if (tc.isDebugEnabled()) SibTr.debug(tc, "@(#) SIB/ws/code/sib.jfapchannel.client.rich.impl/src/com/ibm/ws/sib/jfapchannel/framework/impl/CFWIOBaseContext.java, SIB.comms, WASX.SIB, uu1215.01 1.1");
   }

   /** The connection reference */
   private NetworkConnection conn = null;
   
   /**
    * Constructor.
    * 
    * @param conn
    */
   public CFWIOBaseContext(NetworkConnection conn)
   {
      if (tc.isEntryEnabled()) SibTr.entry(this, tc, "<init>", conn);
      this.conn = conn;
      if (tc.isEntryEnabled()) SibTr.exit(tc, "<init>");
   }
   
   /**
    * This method tries to avoid creating a new instance of a CFWNetworkConnection object by seeing
    * if the specified virtual connection is the one that we are wrapping in the 
    * CFWNetworkConnection instance that created this context. If it is, we simply return that.
    * Otherwise we must create a new instance.
    * 
    * @param vc The virtual connection.
    * 
    * @return Returns a NetworkConnection instance that wraps the virtual connection.
    */
   protected NetworkConnection getNetworkConnectionInstance(VirtualConnection vc)
   {
      if (tc.isEntryEnabled()) SibTr.entry(this, tc, "getNetworkConnectionInstance", vc);
      
      NetworkConnection retConn = null;
      if (vc != null)
      {
         // Default to the connection that we were created from
         retConn = conn;
         
         if (vc != ((CFWNetworkConnection) conn).getVirtualConnection())
         {
            // The connection is different - nothing else to do but create a new instance
            retConn = new CFWNetworkConnection(vc);
         }
      }
      
      if (tc.isEntryEnabled()) SibTr.exit(this, tc, "getNetworkConnectionInstance", retConn);
      return retConn;
   }
}
