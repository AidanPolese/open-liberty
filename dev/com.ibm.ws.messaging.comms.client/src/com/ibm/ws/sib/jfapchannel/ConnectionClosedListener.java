/*
 * @start_prolog@
 * Version: @(#) 1.7 SIB/ws/code/sib.jfapchannel.client/src/com/ibm/ws/sib/jfapchannel/ConnectionClosedListener.java, SIB.comms, WASX.SIB, uu1215.01 06/04/13 03:23:51 [4/12/12 22:14:12]
 * ============================================================================
 * IBM Confidential OCO Source Materials
 * 
 * 5724-I63, 5724-H88, 5655-N01, 5733-W61  (C) Copyright IBM Corp. 2004, 2006 
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
 * Creation        040621 mattheg  Original
 * D199145         040812 prestona Fix Javadoc
 * D361638         060411 mattheg  Pass connection on connection closed listener
 * ============================================================================
 */
package com.ibm.ws.sib.jfapchannel;

/**
 * Implementers of this class can use it to be notified when a physical connection (that backs a
 * conversation) closes. The callback will only be called once and may be called due to a timeout, 
 * user intervention or error.
 * 
 * @author Gareth Matthews
 */
public interface ConnectionClosedListener
{
   /**
    * Driven when the connection is closed.
    * 
    * @param connectionReference A reference to the connection to which was closed.
    */
   void connectionClosed(Object connectionReference);
}
