/*
 * @start_prolog@
 * Version: @(#) 1.14 SIB/ws/code/sib.jfapchannel.client/src/com/ibm/ws/sib/jfapchannel/NoCapacityException.java, SIB.comms, WASX.SIB, uu1215.01 06/08/15 03:38:39 [4/12/12 22:14:11]
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
 * Creation        030707 prestona Original
 * D226223         040823 prestona Uses new messages
 * LIDB3706-5.211  050211 prestona serialization compatibility for sib.jfapchannel
 * D274182         050518 mattheg  Modify serialVersionUID to correct value
 * D378229         060808 prestona Avoid synchronizing on ME-ME send()
 * ============================================================================ 
 */
package com.ibm.ws.sib.jfapchannel;

import com.ibm.websphere.sib.exception.SIResourceException;

/**
 * Thrown when a conversation does not currently have the capacity
 * to accept a message for transmission.  This represents a
 * transitory failure condition.
 * @see com.ibm.ws.sib.jfapchannel.Conversation
 * @deprecated
 */
public class NoCapacityException extends SIResourceException
{
   private static final long serialVersionUID = 6326500461142936760L;   // LIDB3706-5.211, D274182
   
   // begin D226223
   public NoCapacityException()
   {
      super();
   }
   
   public NoCapacityException(String msg)
   {
      super(msg);
   }
   // end D226223
}
