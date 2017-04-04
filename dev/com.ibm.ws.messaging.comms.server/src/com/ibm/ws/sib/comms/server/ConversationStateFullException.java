/*
 * @start_prolog@
 * Version: @(#) 1.1 SIB/ws/code/sib.comms.server.impl/src/com/ibm/ws/sib/comms/server/ConversationStateFullException.java, SIB.comms, WASX.SIB, aa1225.01 09/07/22 10:12:58 [7/2/12 05:59:34]
 * ============================================================================
 * IBM Confidential OCO Source Materials
 * 
 * 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70 Copyright IBM Corp. 2003, 2009
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
 * Creation        030325 rajam    Original
 * LIDB3706-5.195  050211 prestona serialization compatibility for sib.comms.impl
 * D274182         050518 mattheg  Modify serialVersionUID to correct value
 * D592503         090722 mleming  ObjectStoreFullException -> ConversationStateFullException
 * ============================================================================
 */
package com.ibm.ws.sib.comms.server;

/**
 * An exception that is thrown when ConversationState is full.
 */
public class ConversationStateFullException extends Exception {

   private static final long serialVersionUID = 1972147846720272403L;
   
}
