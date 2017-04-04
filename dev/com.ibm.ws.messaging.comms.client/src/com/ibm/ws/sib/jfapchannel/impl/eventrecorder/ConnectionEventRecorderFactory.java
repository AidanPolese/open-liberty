/*
 * @start_prolog@
 * Version: @(#) 1.1 SIB/ws/code/sib.jfapchannel.client.common.impl/src/com/ibm/ws/sib/jfapchannel/impl/eventrecorder/ConnectionEventRecorderFactory.java, SIB.comms, WASX.SIB, uu1215.01 06/04/21 11:09:03 [4/12/12 22:14:14]
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
 * Factory for creating connection event recorders. 
 */
public class ConnectionEventRecorderFactory 
{
   /**
    * @return a new connection event recorder that uses the default number of
    * connection and conversation events in its circular log.
    */
   public static final ConnectionEventRecorder getConnectionEventRecorder()
   {
      return new ConnectionEventRecorderImpl();
   }

   /**
    * @param maxConnectionEvents the maximum number of connection events to
    * log in the recorders circular buffer
    * @return a new connection event recorder that uses the default number of
    * conversation events in its circular log.
    */
   public static final ConnectionEventRecorder getConnectionEventRecorder(int maxConnectionEvents)
   {
      return new ConnectionEventRecorderImpl(maxConnectionEvents);
   }

   /**
    * @param maxConnectionEvents the maximum number of connection events to
    * log in the recorders circular buffer
    * @param maxConversationEvents the maximum number of conversation events to
    * log in the recorders circular buffer
    * @return a connection event recorder that logs a number of events determined
    * by the parameters passed to this method when it is invoked.
    */
   public static final ConnectionEventRecorder getConnectionEventRecorder(int maxConnectionEvents, int maxConversationEvents)
   {
      return new ConnectionEventRecorderImpl(maxConnectionEvents, maxConversationEvents);
   }
   
}
