/*
 * @start_prolog@
 * Version: @(#) 1.3 SIB/ws/code/sib.jfapchannel.client.common.impl/src/com/ibm/ws/sib/jfapchannel/impl/eventrecorder/ConnectionEventRecorderImpl.java, SIB.comms, WASX.SIB, uu1215.01 06/10/02 04:19:00 [4/12/12 22:14:14]
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
 * 393407          060927 mnuttall Change to com.ibm.ws.sib.jfapchannel.approxtime.QuickApproxTime
 * ============================================================================ 
 */

package com.ibm.ws.sib.jfapchannel.impl.eventrecorder;

import java.util.Calendar;

import com.ibm.ws.sib.jfapchannel.approxtime.QuickApproxTime;
import com.ibm.ws.sib.jfapchannel.framework.Framework;

/**
 * Implementation of the connection event recorder.  This is a circular buffer of event records.
 */
public class ConnectionEventRecorderImpl extends EventRecorderImpl implements ConnectionEventRecorder 
{
   private static final QuickApproxTime approxTime;
   
   // Default maximum number of events to keep.  Specifying a value on the
   // constructor overrides this value.
   private static final int DEFAULT_MAX_CONNECTION_EVENTS = 20;
   
   private static final int DEFAULT_MAX_CONVERSATION_EVENTS = 40;
   
   // Frequence (in ms) for the quick approximate time keeping thread.  This
   // determines how accurate the timestamps are.
   private static final int APPROXTIME_FREQUENCY = 100;
   
   // Seperate array for each field of a event record.  This isn't good OO,
   // we should have an object that encapsulates each of these fields.  However,
   // in this case, good OO has a pretty tragic impact on heap usage...
   private final byte[] eventTypeArray;
   private final long[] eventTimestampArray;
   private final int[] eventSequenceArray;
   private final int[] eventThreadHashcode;
   private final String[] eventDescriptionArray;
   
   private final int maxConnectionEvents;
   private final int maxConversationEvents;
   private int currentEvent;
   private int totalEvents;
   
   private final SequenceNumberGenerator sequenceNumberGenerator = new SequenceNumberGenerator();
   
   static
   {
      approxTime = Framework.getInstance().getApproximateTimeKeeper();  // 393407
      approxTime.setInterval(APPROXTIME_FREQUENCY);
   }
   
   public ConnectionEventRecorderImpl()
   {
      this(DEFAULT_MAX_CONNECTION_EVENTS, DEFAULT_MAX_CONVERSATION_EVENTS);
   }
   
   public ConnectionEventRecorderImpl(int maxConnectionEvents)
   {
      this(maxConnectionEvents, DEFAULT_MAX_CONVERSATION_EVENTS);
   }
   
   public ConnectionEventRecorderImpl(int maxConnectionEvents, int maxConversationEvents)
   {
      this.maxConnectionEvents = maxConnectionEvents;
      this.maxConversationEvents = maxConversationEvents;
      eventTypeArray = new byte[maxConnectionEvents];
      eventTimestampArray = new long[maxConnectionEvents];
      eventSequenceArray = new int[maxConnectionEvents];
      eventThreadHashcode = new int[maxConnectionEvents];      
      eventDescriptionArray = new String[maxConnectionEvents];
   }
    
   public ConversationEventRecorder getConversationEventRecorder() 
   {
      return new ConversationEventRecorderImpl(this, 
                                               sequenceNumberGenerator, 
                                               approxTime, 
                                               maxConversationEvents);
   }
   
   protected final synchronized void fillInNextEvent(byte type, String description)
   {
      eventTypeArray[currentEvent] = type;
      eventTimestampArray[currentEvent] = approxTime.getApproxTime();
      eventSequenceArray[currentEvent] = sequenceNumberGenerator.getNextSequenceNumber();
      eventThreadHashcode[currentEvent] = Thread.currentThread().hashCode();
      eventDescriptionArray[currentEvent] = description;
      
      currentEvent = (currentEvent + 1) % maxConnectionEvents;
      ++totalEvents;
   }
 
   public synchronized String toString()
   {
      final StringBuffer sb = new StringBuffer(""+totalEvents);
      sb.append(" connection events recorded in total\n");
      sb.append("timestamp/sequence/thread/type/description\n");
      
      int eventIndex = (totalEvents >= maxConnectionEvents) ? currentEvent : 0;
      int eventCount = 0;
      
      while((eventTypeArray[eventIndex] != 0x00) && (eventCount < maxConnectionEvents))
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
         
         eventIndex = (eventIndex + 1) % maxConnectionEvents;
         ++eventCount;
      }
      
      return sb.toString();
   }
}
