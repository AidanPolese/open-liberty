/*
 * @start_prolog@
 * Version: @(#) 1.12.1.4 SIB/ws/code/sib.comms.client.impl/src/com/ibm/ws/sib/comms/client/ClientLinkLevelState.java, SIB.comms, WASX.SIB, uu1215.01 09/04/06 08:16:43 [4/12/12 22:14:06]
 * ============================================================================
 * IBM Confidential OCO Source Materials
 * 
 * 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5724-J08  Copyright IBM Corp. 2004, 2007 
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
 * Creation        030808 mattheg  Original
 * f174317         030827 mattheg  Add local transaction support
 * f176954         030918 mattheg  Move location of handshake params
 * f181927         031111 mattheg  Allow transaction IDs to be used by global and local transactions
 * D276260         050516 mattheg  Add hashcode to trace (not change flagged)
 * D354565         060320 prestona ClassCastException thrown during failover
 * D373006.2       070111 mattheg  1pc Optimisation
 * PK83641         310309 ajw      reset LinkLevelState when returning from pool;
 * ============================================================================
 */
package com.ibm.ws.sib.comms.client;

import java.util.HashMap;

import javax.transaction.xa.Xid;

import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.sib.comms.CommsConstants;
import com.ibm.ws.sib.jfapchannel.LinkLevelState;
import com.ibm.ws.sib.utils.ras.SibTr;

/**
 * This class holds state at the physical socket level. Each Conversation
 * that uses the same physical socket will have reference to this state.
 * 
 * @author Gareth Matthews
 */
public class ClientLinkLevelState implements LinkLevelState
{
   /**
    * Register Class with Trace Component
    */
   private static final TraceComponent tc = SibTr.register(ClientLinkLevelState.class,
                                                           CommsConstants.MSG_GROUP,
                                                           CommsConstants.MSG_BUNDLE);
   
   /** The last transaction id that was allocated */
   private int lastTransactionId = 0;
   
   /** A map of XAResource's keyed by their corresponding XId's for inflight transactions */
   private HashMap<Xid, SIXAResourceProxy> xidToXAResourceMap = new HashMap<Xid, SIXAResourceProxy>();
   
   /**
    * Constructor.
    */
   public ClientLinkLevelState()
   {
      if (tc.isEntryEnabled()) SibTr.entry(this, tc, "<init>");
      if (tc.isEntryEnabled()) SibTr.exit(this, tc, "<init>");
   }
   
   // start f181927
   /**
    * @return Returns the next available transaction ID.
    */
   public synchronized int getNextTransactionId()
   {
      if (tc.isEntryEnabled()) SibTr.entry(this, tc, "getNextTransactionId");
      int nextTransactionId = ++lastTransactionId;
      if (tc.isEntryEnabled()) SibTr.exit(this, tc, "getNextTransactionId", ""+nextTransactionId);
      return nextTransactionId;
   }
   // end f181927
   
   /**
    * @return Returns a Map of XAResources keyed by XId for current in-flight, unoptimized 
    *         transactions.
    */
   public HashMap<Xid, SIXAResourceProxy> getXidToXAResourceMap()
   {
      if (tc.isEntryEnabled()) SibTr.entry(this, tc, "getXidToXAResourceMap");
      if (tc.isEntryEnabled()) SibTr.exit(this, tc, "getXidToXAResourceMap", xidToXAResourceMap);
      return xidToXAResourceMap;
   }
   
   public void reset()
   {
       xidToXAResourceMap = new HashMap<Xid, SIXAResourceProxy>();
   }
}
