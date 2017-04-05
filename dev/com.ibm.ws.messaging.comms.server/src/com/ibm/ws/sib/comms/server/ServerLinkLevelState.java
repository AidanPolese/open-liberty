/*
 * @start_prolog@
 * Version: @(#) 1.15.1.2 SIB/ws/code/sib.comms.server.impl/src/com/ibm/ws/sib/comms/server/ServerLinkLevelState.java, SIB.comms, WASX.SIB, aa1225.01 09/04/06 08:17:59 [7/2/12 05:59:34]
 * ============================================================================
 * IBM Confidential OCO Source Materials
 * 
 * 5724-I63, 5724-H88, 5655-N02, 5733-W70  (C) Copyright IBM Corp. 2003, 2006 
 * 
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 * ============================================================================
 * @end_prolog@
 *
 * Change activity:
 *
 * CORE API 0.6 Implementation
 *
 * Reason          Date   Origin   Description
 * --------------- ------ -------- --------------------------------------------
 * Creation        030725 mattheg  Original
 * D173761         030807 Niall    Add store of type of connection initiator
 * F173772         030807 prestona Implement Clone Connection in JFAP Channel
 * D174684         030820 Niall    Change location of CatHandshakeGroup
 * f174317         030827 mattheg  Add support for local transactions
 * f176954         030918 mattheg  Change location of the handshake properties
 * D297060         050821 prestona IdToTransactionTable memory leak
 * D354565         060320 prestona ClassCastException thrown during failover
 * PK83641         310309 ajw      Rename to ServerLinkLevelState and implement reset() 
 * ============================================================================
 */
package com.ibm.ws.sib.comms.server;

import com.ibm.ws.sib.comms.server.clientsupport.*;
import com.ibm.ws.sib.jfapchannel.LinkLevelState;


/**
 * This class holds state at the physical link layer. It is possible
 * that the JFAP channel may multi-plex our conversations so that
 * individual conversations use the same physical socket. In that case
 * they will have their own individual conversation attachement. This
 * attachement is done at the physical socket layer so that all 
 * conversations on the same socket share this object.
 * 
 * @author Gareth Matthews
 */
public class ServerLinkLevelState implements LinkLevelState
{
   /** The connection listener instance */
   private ServerSICoreConnectionListener listener = new ServerSICoreConnectionListener();

   /** The type of connection initiator e.g Client or ME */
   private int connectionType = -1;

   /** A table which maps core connection ID's to their respective objects */
   private IdToSICoreConnectionTable idToSICoreConnectionTable = new IdToSICoreConnectionTable(); // F173772
   
   /** A table which maps client transaction Id's to their respective objects */
   private IdToTransactionTable idToTransactionTable = new IdToTransactionTable();     // f174317

   /** 
    * Maps a local transaction, or work done by a resource as part of a global transaction to
    * a dispatchable object.
    */
   private TransactionToDispatchableMap txToDispatchableMap = new TransactionToDispatchableMap();  // D297060
   
   /**
    * Accessor method for the core connection listener.
    * 
    * @return ServerSICoreConnectionListener
    */
   public ServerSICoreConnectionListener getSICoreConnectionListener()
   {
      return listener;
   }
   
   /**
    * Accessor method for connectionType
    * @return Type of Connection initiator e.g. Client or ME
    */
   // begin D173761
   public int getConnectionType()
   {
      return connectionType;
   }
   // end D173761
   
   /**
    * Accessor method for connectionType
    * @param ct The type of Connection initiator e.g. Client or ME
    */
   // begin D173761
   public void setConnectionType(int ct)
   {
      connectionType = ct;
   }
   // end D173761
   
   /**
    * Returns the table which maps core connection ID's to their respective core
    * connection objects.
    * @return IdToSICoreConnectionTable
    */
   // begin F173772
   public IdToSICoreConnectionTable getSICoreConnectionTable()
   {
      return idToSICoreConnectionTable;
   }
   // end F173772

   // Start f174317
   /**
    * Returns the table mapping client transaction Id's to their respective
    * objects.
    * 
    * @return IdToTransactionTable
    */
   public IdToTransactionTable getTransactionTable()
   {
      return idToTransactionTable;
   }
   // end f174317
   
   // begin D297060
   /**
    * @return TransactionToDispatchableMap maps transaction to the dispatchable it
    * should use when interacting with the receive listener dispatcher.
    */
   public TransactionToDispatchableMap getDispatchableMap()
   {
      return txToDispatchableMap;
   }
   // end D297060
   
   public void reset()
   {
       idToSICoreConnectionTable = new IdToSICoreConnectionTable();
       idToTransactionTable = new IdToTransactionTable();
       txToDispatchableMap = new TransactionToDispatchableMap();
       listener = new ServerSICoreConnectionListener();
   }
}
