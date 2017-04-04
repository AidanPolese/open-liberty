/*
 * @start_prolog@
 * Version: @(#) 1.6 SIB/ws/code/sib.comms.server/src/com/ibm/ws/sib/comms/MEComponentHandshake.java, SIB.comms, WASX.SIB, aa1225.01 05/02/04 08:55:01 [7/2/12 05:59:04]
 * ============================================================================
 * IBM Confidential OCO Source Materials
 * 
 * 5724-I63, 5724-H88, 5655-N01, 5733-W60          (C) Copyright IBM Corp. 2003, 2005
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
 * Creation        030721 prestona Original
 * ============================================================================
 */
package com.ibm.ws.sib.comms;

/**
 * Callback interface invoked as part of TRM handshake for ME to ME connections.
 * The (TRM) implementor of this interface can use the methods it provides to
 * determine if a ME to ME connection attempt should succeed or not.  It is
 * anticipated this will be done as part of the handshake which takes place
 * when an ME to ME connection is esablished.
 * @see com.ibm.ws.sib.comms.MEConnection
 */
public interface MEComponentHandshake
{
   /**
    * This method is invoked in the originating ME when a new MEConnection
    * is being established.  The implementation of this method will, more than
    * likely, invoke the exchange method on the MEConnection passed as an
    * argument to flow TRM data.  The value returned by this method is used
    * to determine if the connection attempt should be allowed to proceed.
    * @param meConnection The MEConnection for which a connection attempt is
    * being made.
    * @return boolean True iff the connection attempt should be allowed to
    * proceed.  If False is returned then the connection is closed.
    */
   boolean connect(MEConnection meConnection);
   
   /**
    * Invoked to indicate that a communications problem occurred during the
    * connection process.
    * @param meConnection The MEConnection object which was attempting to
    * establish a connection.
    * @param throwable The exceptional condition which prevented the connection
    * from being established or sustained.
    */
   void fail(MEConnection meConnection, Throwable throwable);
}
