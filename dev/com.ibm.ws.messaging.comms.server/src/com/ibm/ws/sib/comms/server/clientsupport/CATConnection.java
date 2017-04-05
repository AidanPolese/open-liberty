/* 
 * @start_prolog@ 
 * Version: @(#) 1.5 SIB/ws/code/sib.comms.server.impl/src/com/ibm/ws/sib/comms/server/clientsupport/CATConnection.java, SIB.comms, WASX.SIB, aa1225.01 08/05/21 09:40:19 [7/2/12 05:58:59]
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
 * Creation        050214 mattheg  Original
 * D267722         050412 mattheg  Ensure synchronized access to tranIds
 * D321471         051109 prestona Optimized transaction related problems
 * D462062         080520 mleming  Improve diagnostics
 * ============================================================================ 
 */
package com.ibm.ws.sib.comms.server.clientsupport;

import java.util.LinkedList;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.sib.comms.CommsConstants;
import com.ibm.ws.sib.utils.ras.SibTr;
import com.ibm.wsspi.sib.core.SICoreConnection;

/**
 * This class wraps an SICoreConnection that is saved in our object store. It also keeps track of
 * any transactions that the connection owns so that when the connection is closed the transactions
 * can be removed from the table (that spans the socket) of transactions.
 * 
 * @author Gareth Matthews
 */
public class CATConnection
{
   /** Trace */
   private static final TraceComponent tc = SibTr.register(CATConnection.class, 
                                                           CommsConstants.MSG_GROUP,
                                                           CommsConstants.MSG_BUNDLE);

   /** Log class info on load */
   static
   {
      if (tc.isDebugEnabled()) SibTr.debug(tc, "Source info: @(#)SIB/ws/code/sib.comms.server.impl/src/com/ibm/ws/sib/comms/server/clientsupport/CATConnection.java, SIB.comms, WASX.SIB, aa1225.01 1.5");
   }

   /** The underlying SICoreConnection */
   private SICoreConnection conn = null;
   
   /** The list of transaction id's that are owned by this connection */
   private LinkedList tranIds = new LinkedList();
   
   /** 
    * Constructor
    * 
    * @param conn
    */
   public CATConnection(SICoreConnection conn)
   {
      this.conn = conn;
   }
   
   /**
    * @return Returns the underlying SICoreConnection.
    */
   public SICoreConnection getSICoreConnection()
   {
      return conn;
   }
   
   /**
    * @return Returns info about this object.
    */
   public String toString()
   {
      return "CATConnection@" + Integer.toHexString(System.identityHashCode(this)) + ": " +
             ", SICoreConnection: " + conn + 
             ", ME Name: " + conn.getMeName() + " [" + conn.getMeUuid() + "] " +
             ", Version: " + conn.getApiLevelDescription() + 
             ", Associated transactions: " + tranIds;
   }
}
