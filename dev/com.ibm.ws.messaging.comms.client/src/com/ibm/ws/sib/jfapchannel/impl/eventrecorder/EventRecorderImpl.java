/*
 * @start_prolog@
 * Version: @(#) 1.1 SIB/ws/code/sib.jfapchannel.client.common.impl/src/com/ibm/ws/sib/jfapchannel/impl/eventrecorder/EventRecorderImpl.java, SIB.comms, WASX.SIB, uu1215.01 06/04/21 11:09:10 [4/12/12 22:14:14]
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
 * Base implementation of the event recorder interface.
 */
public abstract class EventRecorderImpl implements EventRecorder 
{
   private static final byte DEBUG_EVENT = (byte)'D';
   private static final byte ERROR_EVENT = (byte)'E';
   private static final byte ENTRY_EVENT = (byte)'>';
   private static final byte EXIT_EVENT  = (byte)'<';
   
   public void logDebug(String description) 
   {
      fillInNextEvent(DEBUG_EVENT, description);
   }

   public void logError(String description) 
   {
      fillInNextEvent(ERROR_EVENT, description);
   }

   public void logEntry(String description) 
   {
      fillInNextEvent(ENTRY_EVENT, description);   
   }

   public void logExit(String description) 
   {
      fillInNextEvent(EXIT_EVENT, description);      
   }
   
   protected abstract void fillInNextEvent(byte type, String description);
}
