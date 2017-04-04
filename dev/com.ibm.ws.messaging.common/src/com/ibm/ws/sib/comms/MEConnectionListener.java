/*
 * @start_prolog@
 * Version: @(#) 1.16 SIB/ws/code/sib.comms.server/src/com/ibm/ws/sib/comms/MEConnectionListener.java, SIB.comms, WASX.SIB, aa1225.01 06/12/05 02:51:17 [7/2/12 05:59:04]
 * ============================================================================
 * IBM Confidential OCO Source Materials
 * 
 * 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5724-J08  Copyright IBM Corp. 2003, 2006
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
 * Creation        030426 prestona Original
 * F172152         030721 prestona Update ME-to-ME interfaces.
 * D199148         040812 mattheg  JavaDoc
 * D215177.2       040824 prestona Add new control message interface to MEConnection
 * D235639         030930 prestona MPIO deadlock
 * D378229         060808 prestona Avoid synchronizing on ME-ME send()
 * D408810         061130 tevans   Clean up MP-Comms interfaces
 * ============================================================================
 */
package com.ibm.ws.sib.comms;

import com.ibm.ws.sib.mfp.AbstractMessage;

/**
 * A listener which may be registered for asynchronous notification of 
 * "interesting" MEConnection related events.
 * <p>
 * <em>A note on priority levels:</em>
 * Priority levels run 0 through 15 (inclusive).  With 15 being the highest
 * priority level.  Level 15 is reserved for internal use (heartbeats and the
 * likes) thus any attempt to use this value when transmitting data will result
 * in an exception being thrown.  The priority levels described do not (directly)
 * map onto the prioriy levels used by the core API.  Instead they run in the
 * priority range 2 to 11 with the highest priority message mapping to priority
 * level 11.  A special priority level (defined by the
 * com.ibm.ws.sib.jfapchannel.Conversation.PRIORITY_LOWEST constant) exists.
 * This attempts to queue data for transmission with the lowest priority level
 * of any data currently pending transmission. 
 * @see com.ibm.ws.sib.comms.MEConnection
 */
public interface MEConnectionListener
{
   /**
    * A message was received from the remote ME.  This message will be
    * identical to that sent via the MEConnection send method.
    * @see MEConnection#send(AbstractMessage, int)
    * @param message The message received.
    */
   void receiveMessage(MEConnection conn, AbstractMessage message);
      
   /**
    * Notification that an error has occurred.
    * @param meConnection The MEConnection object for which the error has occurred.
    * @param throwable The exceptional condition that has occurred.
    */
   void error(MEConnection meConnection, Throwable throwable);
}
