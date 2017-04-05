/*
 * @start_prolog@
 * Version: @(#) 1.3 SIB/ws/code/sib.jfapchannel.client.common.impl/src/com/ibm/ws/sib/jfapchannel/impl/eventrecorder/ConversationEventRecorderImpl.java, SIB.comms, WASX.SIB, uu1215.01 06/10/02 04:19:36 [4/12/12 22:14:14]
 * ============================================================================
 * IBM Confidential OCO Source Materials
 *
 * 5724-J08, 5724-I63, 5724-H88, 5655-N01, 5733-W61  (C) Copyright IBM Corp. 2006
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
 * 393407          060927 mnuttall Switch to com.ibm.ws.sib.jfapchannel.approxtime.QuickApproxTime;
 * ============================================================================ 
 */

package com.ibm.ws.sib.jfapchannel.impl.eventrecorder;

import java.util.Calendar;

import com.ibm.ws.sib.jfapchannel.approxtime.QuickApproxTime;         // 393407

/**
 * Implementation of the conversation level event recorder. 
 */
public class ConversationEventRecorderImpl extends EventRecorderImpl implements ConversationEventRecorder
{
   // Seperate array for each field of a event record.  This isn't good OO,
   // we should have an object that encapsulates each of these fields.  However,
   // in this case, good OO has a pretty tragic impact on heap usage...
   private final byte[] eventTypeArray;
   private final long[] eventTimestampArray;
   private final int[] eventSequenceArray;
   private final int[] eventThreadHashcode;
   private final String[] eventDescriptionArray;

   private int totalEvents;
   private int currentEvent;
   private final int maxEvents;
   private final ConnectionEventRecorder connectionEventRecorder;
   private final SequenceNumberGenerator sequenceNumberGenerator;
   private final QuickApproxTime approxTime;   
   
   protected ConversationEventRecorderImpl(ConnectionEventRecorder connectionEventRecorder,
                                           SequenceNumberGenerator sequenceNumberGenerator,
                                           QuickApproxTime approxTime,
                                           int maxEvents)
   {
      this.connectionEventRecorder = connectionEventRecorder;
      this.sequenceNumberGenerator = sequenceNumberGenerator;
      this.maxEvents = maxEvents;
      this.approxTime = approxTime;
      eventTypeArray = new byte[maxEvents];
      eventTimestampArray = new long[maxEvents];
      eventSequenceArray = new int[maxEvents];
      eventThreadHashcode = new int[maxEvents];
      eventDescriptionArray = new String[maxEvents];  
   }
   
   protected synchronized void fillInNextEvent(byte type, String description) 
   {
      eventTypeArray[currentEvent] = type;
      eventTimestampArray[currentEvent] = approxTime.getApproxTime();
      eventSequenceArray[currentEvent] = sequenceNumberGenerator.getNextSequenceNumber();
      eventDescriptionArray[currentEvent] = description;
      eventThreadHashcode[currentEvent] = Thread.currentThread().hashCode();
      currentEvent = (currentEvent + 1) % maxEvents;
      ++totalEvents;
   }

   public ConnectionEventRecorder getConnectionEventRecorder() 
   {
      return connectionEventRecorder;
   }
   
   public synchronized String toString()
   {
      final StringBuffer sb = new StringBuffer(""+totalEvents);
      sb.append(" conversation events recorded in total\n");
      sb.append("timestamp/sequence/thread/type/description\n");
      
      int eventIndex = (totalEvents >= maxEvents) ? currentEvent : 0;
      int eventCount = 0;
      
      while((eventTypeArray[eventIndex] != 0x00) && (eventCount < maxEvents))
      {
         Calendar calendar = Calendar.getInstance();
         calendar.setTimeInMillis(eventTimestampArray[eventIndex]);
         sb.append(calendar.get(Calendar.HOUR_OF_DAY));
         sb.append(":");
         sb.append(calendar.get(Calendar.MINUTE));
         sb.append(":");         
         sb.append(calendar.get(Calendar.SECOND));
         sb.append(":");
         sb.append(calendar.get(Calendar.MILLISECOND));
         sb.append(" ");
         sb.append(eventSequenceArray[eventIndex]);
         sb.append(" ");
         sb.append(Integer.toHexString(eventThreadHashcode[eventIndex]));
         sb.append("\t");
         sb.append((char)eventTypeArray[eventIndex]);
         sb.append("\t");
         sb.append(eventDescriptionArray[eventIndex]);
         sb.append("\n");
         
         eventIndex = (eventIndex + 1) % maxEvents;
         ++eventCount;
      }
      
      return sb.toString();
   }
}
