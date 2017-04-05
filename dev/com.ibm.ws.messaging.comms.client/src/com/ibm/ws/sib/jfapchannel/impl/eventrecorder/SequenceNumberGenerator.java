/*
 * @start_prolog@
 * Version: @(#) 1.1 SIB/ws/code/sib.jfapchannel.client.common.impl/src/com/ibm/ws/sib/jfapchannel/impl/eventrecorder/SequenceNumberGenerator.java, SIB.comms, WASX.SIB, uu1215.01 06/04/21 11:09:12 [4/12/12 22:14:14]
 * ============================================================================
 * IBM Confidential OCO Source Materials
 *
 * 5724-I63, 5724-H88, 5655-N01, 5733-W61  (C) Copyright IBM Corp. 2006  
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
 * D363463         060421 prestona ME-ME heartbeat timeout
 * ============================================================================ 
 */

package com.ibm.ws.sib.jfapchannel.impl.eventrecorder;

/**
 * Generates and returns a monoatomically incrementing sequence number.  
 */
final class SequenceNumberGenerator 
{
   private int sequenceNumber;
   
   protected synchronized int getNextSequenceNumber()
   {
      return sequenceNumber++;
   }
}
