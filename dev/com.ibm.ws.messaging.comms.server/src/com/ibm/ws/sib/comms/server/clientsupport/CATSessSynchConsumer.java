/*
 * @start_prolog@
 * Version: @(#) 1.55 SIB/ws/code/sib.comms.server.impl/src/com/ibm/ws/sib/comms/server/clientsupport/CATSessSynchConsumer.java, SIB.comms, WASX.SIB, aa1225.01 08/01/18 03:28:45 [7/2/12 05:59:00]
 * ============================================================================
 * IBM Confidential OCO Source Materials
 *
 * 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70  Copyright IBM Corp. 2004, 2008
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
 * Creation        030604 clarkep  Original
 * f169884         030623 mattheg  Add non-blocking sync support
 * d170516         030625 mattheg  Change asnc method call in line with modified core API
 * d170639         030630 mattheg  NLS all the messages
 * f169897.2       030708 mattheg  Convert to Core API 0.6
 * f171400         030714 mattheg  Implement the Core API 0.6
 * f172297         030724 mattheg  Complete Core API 0.6 implementation
 * d174443         030815 mattheg  Ensure receive's honour session state
 * f174317         030827 mattheg  Add local transaction support
 * d176012         030908 mattheg  Allow a synchronous consumer to become asynchronous
 * f173765.2       030926 mattheg  Core API M4 update
 * f177889         030930 mattheg  Core API M4 completion
 * d186970         040116 mattheg  Overhaul the way we send exceptions to client
 * f187521.2.1     040126 mattheg  Unrecoverable reliability -- part 2
 * f192654         040301 mattheg  Allow inlined callback through a config param
 * f192759.10      040323 clarkep  Fix deprecated AlarmManager call to nonDeferred.
 * f191118.1       040423 mattheg  Internal MP interface change
 * f200337         040428 mattheg  Message order context implementation
 * f176658.4.2.2   040504 mattheg  deliverImmediately flag change
 * D202636         040512 mattheg  Missing deliverImmediately flag on start()
 * D209401         040615 mattheg  toString() enhancements
 * D217372         040719 mattheg  Move JFap constants -> JFapChannelConstants (not change-flagged)
 * F201972.2       040727 mattheg  Core SPI Exceptions rework (not change flagged)
 * D199177         040816 mattheg  JavaDoc
 * F219476.2       040906 prestona Z3 Core SPI changes
 * D235891         040930 mattheg  Runtime property standards
 * D225856         041006 mattheg  Update FFDC class name (not change flagged)
 * D307265         050922 prestona Support for optimized transactions
 * D281779         051011 prestona ReceiveWithWait hang on client
 * D329823         051207 mattheg  Trace improvements
 * D347591         060217 mattheg  Add support for exchanged starts
 * D350111.1       060302 mattheg  Use send listener for start()
 * D377648         060719 mattheg  Use CommsByteBuffer
 * D378229         060808 prestona Avoid synchronizing on ME-ME send()
 * PK33011         061016 mattheg  Fix for MDB Listener not being told about ME failure
 * D441183         072307 mleming  Don't FFDC when calling terminated ME
 * 471664          071003 vaughton Findbugs tidy up
 * 492551          080117 mleming  Only register SICoreConnectionListener when required
 * ============================================================================
 */
package com.ibm.ws.sib.comms.server.clientsupport;

import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ejs.util.am.Alarm;
import com.ibm.ejs.util.am.AlarmManager;
import com.ibm.websphere.sib.Reliability;
import com.ibm.websphere.sib.exception.SIException;
import com.ibm.ws.ffdc.FFDCFilter;
import com.ibm.ws.sib.comms.CommsConstants;
import com.ibm.ws.sib.comms.common.CommsUtils;
import com.ibm.ws.sib.comms.server.ConversationState;
import com.ibm.ws.sib.jfapchannel.Conversation;
import com.ibm.ws.sib.jfapchannel.JFapChannelConstants;
import com.ibm.ws.sib.jfapchannel.SendListener;
import com.ibm.ws.sib.jfapchannel.Conversation.ThrottlingPolicy;
import com.ibm.ws.sib.processor.MPConsumerSession;
import com.ibm.ws.sib.utils.ras.SibTr;
import com.ibm.wsspi.sib.core.ConsumerSession;
import com.ibm.wsspi.sib.core.OrderingContext;

/**
 * <p>This is the wrapped version of a synchronous consumer. While
 * extending the main consumer class this overrides the receive
 * method to perform synchronous receiving.
 *
 * <p>However, to ensure that we do not block any thread if a timeout
 * is specified this is implemented asynchrously under the covers by
 * this class.
 *
 * @author Gareth Matthews
 */
public class CATSessSynchConsumer extends CATConsumer
{
   /** Class name for FFDC's */
   private static String CLASS_NAME = CATSessSynchConsumer.class.getName();

   /** Reference to the CATMainConsumer */
   private CATMainConsumer mainConsumer = null;

   /** The async reader we are using for synchronous receives */
   private CATSyncAsynchReader asynchReader = null;

   /**
    * A flag to indicate whether the client thinks we are
    * started or stopped, as opposed to what we have done to
    * the session on the client's behalf
    */
   private boolean logicallyStarted = false;

   /** Trace */
   private static final TraceComponent tc = SibTr.register(CATSessSynchConsumer.class,
                                                           CommsConstants.MSG_GROUP,
                                                           CommsConstants.MSG_BUNDLE);

   /** Log class info on load */
   static
   {
      if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) SibTr.debug(tc, "Source info: @(#)SIB/ws/code/sib.comms.server.impl/src/com/ibm/ws/sib/comms/server/clientsupport/CATSessSynchConsumer.java, SIB.comms, WASX.SIB, aa1225.01 1.55");
   }

   /**
    * Constructs a new synchronous consumer.
    *
    * @param mainConsumer The main consumer
    */
   public CATSessSynchConsumer(CATMainConsumer mainConsumer)
   {
      super();

      if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.entry(this, tc, "<init>", mainConsumer);

      this.mainConsumer = mainConsumer;
      if (mainConsumer.isStarted()) logicallyStarted = true;

      if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.exit(this, tc, "<init>");
   }

   /**
    * @return Returns the actual SI ConsumerSession
    */
   protected ConsumerSession getConsumerSession()
   {
      if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.entry(this, tc, "getConsumerSession");
      ConsumerSession sess = mainConsumer.getConsumerSession();
      if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.exit(this, tc, "getConsumerSession", sess);
      return sess;
   }

   /**
    * @return Returns the conversation.
    */
   protected Conversation getConversation()
   {
      if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.entry(this, tc, "getConversation");
      Conversation conv = mainConsumer.getConversation();
      if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.exit(this, tc, "getConversation", conv);
      return conv;
   }

   /**
    * @return Returns the session lowest priority.
    */
   protected int getLowestPriority()
   {
      if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.entry(this, tc, "getLowestPriority");
      int lowestPri = mainConsumer.getLowestPriority();
      if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.exit(this, tc, "getLowestPriority", lowestPri);
      return lowestPri;
   }

   /**
    * @return Returns the client session Id.
    */
   protected short getClientSessionId()
   {
      if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.entry(this, tc, "getClientSessionId");
      short sessId = mainConsumer.getClientSessionId();
      if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.exit(this, tc, "getClientSessionId");
      return sessId;
   }

   /**
    * @return Returns the sessions unrecoverable reliability.
    */
   protected Reliability getUnrecoverableReliability()
   {
      if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.entry(this, tc, "getUnrecoverableReliability");
      Reliability rel = mainConsumer.getUnrecoverableReliability();
      if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.exit(this, tc, "getUnrecoverableReliability");
      return rel;
   }

   /**
    * <p>This method is called when receive is called on this consumer
    * session. This must do an asychronous receive so that this method
    * returns control to the TCP thread.
    *
    * <p>Note that this method does not reply to the client. This is
    * done by the async reader class.
    *
    * <p>Timeouts that are flown as part of the FAP are different to those
    * which would normally be passed into the <code>receive()</code> method.
    * These are:
    *
    * <ul>
    *   <li>Timeout = -1: No wait should be performed</li>
    *   <li>Timeout =  0: Wait forever<li>
    *   <li>Otherwise   : Wait for the specified number of milliseconds</li>
    * </ul>
    *
    * @param requestNumber The request number the async callback should reply with.
    * @param transaction The current transaction.
    * @param timeout The timeout of this receive.
    */
   public void receive(int requestNumber,
                       int transaction,
                       long timeout)
   {
      if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.entry(this, tc, "receive",
                                           new Object[]{requestNumber, transaction, timeout});

      if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
      {
         SibTr.debug(this, tc, "RQ: " + requestNumber + ", Timeout: " + timeout);

         if (timeout == -1)
         {
            SibTr.debug(this, tc, "Emulating a receiveNoWait()");
         }
         else if (timeout == 0)
         {
            SibTr.debug(this, tc, "Emulating an indefinate receive()");
         }
         else
         {
            SibTr.debug(this, tc, "Emulating a receive() for " + timeout + "ms");
         }
      }

      requestsReceived++;

      // First ensure the session is stopped
      try
      {
         if (mainConsumer.isStarted()) getConsumerSession().stop();
      }
      catch (SIException sis)
      {
         //No FFDC code needed
         //Only FFDC if we haven't received a meTerminated event.
         if(!((ConversationState)getConversation().getAttachment()).hasMETerminated())
         {
            FFDCFilter.processException(sis, CLASS_NAME + ".receive",
                                        CommsConstants.CATSESSSYNCHCONSUMER_RECEIVE_01, this);
         }

         if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) SibTr.debug(this, tc, sis.getMessage(), sis);

         // At this point we can not do much - so we will carry on
         // It is likely that if an exception was thrown here something
         // fairly bad went wrong and this should be flagged by subsequent
         // actions in this method
      }

      // Have we an async reader for this session?
      if (asynchReader == null)
      {
         if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) SibTr.debug(this, tc, "Creating async reader for first time");

         asynchReader = new CATSyncAsynchReader(transaction,        // The transaction ID
                                                getConversation(),  // The conversation
                                                mainConsumer,       // The main consumer
                                                requestNumber);     // The initial request #
         try
         {
            // Here we need to examine the config parameter that will denote whether we are telling
            // MP to inline our async callbacks or not. We will default to false, but this can
            // be overrideen.
            boolean inlineCallbacks =
               CommsUtils.getRuntimeBooleanProperty(CommsConstants.INLINE_ASYNC_CBACKS_KEY,
                                                    CommsConstants.INLINE_ASYNC_CBACKS);

            if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) SibTr.debug(this, tc, "Inline async callbacks: " + inlineCallbacks);

            // Here we need to examine the unrecoverable reliability setting for the session.
            // The deal is that if we are not transacted then we want to ensure that as little
            // overhead takes place as possible. As such, if we are not transacted we will
            // override the sessions unrecoverable reliability with the highest reliability
            // - making everything unrecoverable.
            // We can only override this by casting to the special MP form of consumer session

            Reliability unrecov = getUnrecoverableReliability();
            if (transaction == CommsConstants.NO_TRANSACTION)
            {
               if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) SibTr.debug(this, tc, "Setting unrecoverable reliability to max");
               unrecov = Reliability.ASSURED_PERSISTENT;
            }

            MPConsumerSession mpSession = (MPConsumerSession) getConsumerSession();
            mpSession.registerAsynchConsumerCallback(asynchReader,    // The reader
                                                     0,               // Max active messages
                                                     0,               // Message lock expiry
                                                     1,               // Batch size
                                                     unrecov,         // Unrecov reliability
                                                     inlineCallbacks, // Inline
                                                     null);           // Ordering context
         }
         catch (SIException s)
         {
            //No FFDC code needed
            //Only FFDC if we haven't received a meTerminated event.
            if(!((ConversationState)getConversation().getAttachment()).hasMETerminated())
            {
               FFDCFilter.processException(s, CLASS_NAME + ".receive",
                                           CommsConstants.CATSESSSYNCHCONSUMER_RECEIVE_02, this);
            }

            if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) SibTr.debug(this, tc, s.getMessage(), s);

            // Inform the client - note that this will mark the reader
            // complete so that only one response is sent to the client
            asynchReader.sendErrorToClient(s,
                                           CommsConstants.CATSESSSYNCHCONSUMER_RECEIVE_02);

            // Kill me
            if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.exit(this, tc, "receive");
            return;
         }
      }
      else
      {
         // Inform the consumer that we have not receieved a message
         asynchReader.setComplete(false);
         // Save the request number in the async consumer
         asynchReader.setRequestNumber(requestNumber);
         // Ensure we tell the reader which transaction to operate under
         asynchReader.setTransaction(transaction);
      }

      // Flush the consumer as a one shot to see if we can get
      // a message
      try
      {
         if (logicallyStarted) mainConsumer.getConsumerSession().activateAsynchConsumer(true);
      }
      catch (SIException s)
      {
         //No FFDC code needed
         //Only FFDC if we haven't received a meTerminated event.
         if(!((ConversationState)getConversation().getAttachment()).hasMETerminated())
         {
            FFDCFilter.processException(s, CLASS_NAME + ".receive",
                                        CommsConstants.CATSESSSYNCHCONSUMER_RECEIVE_03, this);
         }

         if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) SibTr.debug(tc, s.getMessage(), s);

         // Inform the client - note that this will mark the reader
         // complete so that only one response is sent to the client
         asynchReader.sendErrorToClient(s, CommsConstants.CATSESSSYNCHCONSUMER_RECEIVE_03);

         // Kill me
         if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.exit(this, tc, "receive");
         return;
      }

      // If the consumer received a message then we are done
      if (!asynchReader.isComplete())
      {
         // If we specified a timeout, then start the consumer
         // and start the timer
         if (timeout == -1)
         {
            if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) SibTr.debug(this, tc, "No message received");

            asynchReader.sendNoMessageToClient();
         }
         else
         {
            asynchReader.setCurrentlyDoingReceiveWithWait(true);
            
            //At this point we are poised to go asynch so we need to register asynchReader as a SICoreConnectionListener.
            try
            {
               final MPConsumerSession mpSession = (MPConsumerSession)getConsumerSession();
               mpSession.getConnection().addConnectionListener(asynchReader);
            }        
            catch(SIException s)
            {
               //No FFDC code needed
               //Only FFDC if we haven't received a meTerminated event.
               if(!((ConversationState)getConversation().getAttachment()).hasMETerminated())
               {
                  FFDCFilter.processException(s, CLASS_NAME + ".receive", CommsConstants.CATSESSSYNCHCONSUMER_RECEIVE_04, this);
               }
               
               if(TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) SibTr.debug(this, tc, s.getMessage(), s);
               
               //Inform the client - note that this will mark the reader
               //complete so that only one response is sent to the client
               asynchReader.sendErrorToClient(s, CommsConstants.CATSESSSYNCHCONSUMER_RECEIVE_04);
               
               if(TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.exit(this, tc, "receive");
               return;
            }

            // If we are waiting indefinately, then we do not need a wake up call
            if (timeout != 0)
            {
               // Start the timer and associate it with the reader
               CATTimer catTimer = new CATTimer(asynchReader);
               Alarm alarm = AlarmManager.createNonDeferrable(timeout, catTimer, "RQ: "+requestNumber);
               if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
                  SibTr.debug(this, tc, "Setting async readers alarm to: " + alarm.toString());
               asynchReader.setCATTimer(alarm);
            }

            // Now start the message delivery
            if (logicallyStarted)
            {
               if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
                  SibTr.debug(this, tc, "Starting async consumer. Timeout = " + timeout + "ms");
               mainConsumer.start(requestNumber, true, false, null);
            }
         }
      }

      if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.exit(this, tc, "receive");
   }

   /**
    * Marks this session as started. If we are currently doing a receiveWithWait()
    * then actually starts the session for us so that message delivery can
    * continue.
    *
    * @param requestNumber
    * @param deliverImmediately The deliver immediately flag is ignored for this type of receive
    * @param sendReply
    * @param sendListener
    */
   public void start(int requestNumber, boolean deliverImmediately, boolean sendReply, SendListener sendListener)
   {
      if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.entry(this, tc, "start",
                                           new Object[]{requestNumber, deliverImmediately});

      logicallyStarted = true;

      // If the async reader is currently in the middle of a receiveWithWait() then all we need to
      // do is call start() on the CATConsumer class which will start the session and then send
      // a reply if one is required
      if (asynchReader.isCurrentlyDoingReceiveWithWait())
      {
         super.start(requestNumber, true, sendReply, sendListener);
      }
      // If we do not need to actually start the session then we still need to reply (potentially)
      // so ensure we do this here.
      else
      {
         if (sendReply)
         {
            try
            {
               getConversation().send(poolManager.allocate(),
                                      JFapChannelConstants.SEG_START_SESS_R,
                                      requestNumber,
                                      JFapChannelConstants.PRIORITY_HIGHEST,
                                      true,
                                      ThrottlingPolicy.BLOCK_THREAD,
                                      sendListener);
            }
            catch (SIException e)
            {
               FFDCFilter.processException(e,
                                           CLASS_NAME + ".start",
                                           CommsConstants.CATSESSSYNCHCONSUMER_START_01,
                                           this);

               if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) SibTr.debug(this, tc, e.getMessage(), e);

               SibTr.error(tc, "COMMUNICATION_ERROR_SICO2013", e);

               sendListener.errorOccurred(null, getConversation());
            }
         }
         else
         {
            sendListener.dataSent(getConversation());
         }
      }

      if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.exit(this, tc, "start");
   }

   /**
    * Stops the session and marks it as stopped.
    *
    * @param requestNumber
    * @param sendListener
    */
   public void stop(int requestNumber, SendListener sendListener)
   {
      if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.entry(this, tc, "stop", requestNumber);

      logicallyStarted = false;
      super.stop(requestNumber, sendListener);

      if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.exit(this, tc, "stop");
   }

   /**
    * Closes the session. If we are currently doing a receiveWithWait then
    * that will be interrupted and a response will be sent to the client.
    *
    * @param requestNumber
    */
   public void close(int requestNumber)
   {
      if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.entry(this, tc, "close", requestNumber);

      // Deregister any error callback created for this connection.
      if (asynchReader != null)
      {
              try
              {
                 mainConsumer.getConsumerSession().getConnection().removeConnectionListener(asynchReader);
              }
              catch (SIException e)
              {
            //No FFDC code needed
            //Only FFDC if we haven't received a meTerminated event.
            if(!((ConversationState)getConversation().getAttachment()).hasMETerminated())
            {
               FFDCFilter.processException(e, CLASS_NAME + ".close",
                     CommsConstants.CATSESSSYNCHCONSUMER_CLOSE_01, this);
            }

                 if (TraceComponent.isAnyTracingEnabled() && tc.isEventEnabled()) SibTr.exception(this, tc, e);
              }

        if (asynchReader.isCurrentlyDoingReceiveWithWait()) asynchReader.sendNoMessageToClient();
      }
      super.close(requestNumber);

      if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.exit(this, tc, "close");
   }

   /**
    * This will cause the synchronous session to become asynchrnous.
    * As such, this class will not handle the session anymore, so we
    * inform the main consumer and that will then switch over the
    * delegated class to handle the consumer.
    *
    * @param requestNumber
    * @param maxActiveMessages
    * @param messageLockExpiry
    * @param batchsize
    * @param orderContext
    */
   public void setAsynchConsumerCallback(int requestNumber,
                                         int maxActiveMessages,
                                         long messageLockExpiry,
                                         int batchsize,
                                         OrderingContext orderContext)
   {
      if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.entry(this, tc, "setAsynchConsumerCallback",
                                           new Object[]
                                           {
                                              requestNumber,
                                              maxActiveMessages,
                                              messageLockExpiry,
                                              batchsize,
                                              orderContext
                                           });

      mainConsumer.setAsynchConsumerCallback(requestNumber,
                                             maxActiveMessages,
                                             messageLockExpiry,
                                             batchsize,
                                             orderContext);

      if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.exit(this, tc, "setAsynchConsumerCallback");
   }

   /**
    * @return Returns a String representing the status of this consumer.
    */
   public String toString()
   {
      String s = "CATSessSyncConsumer@" + Integer.toHexString(hashCode()) +
                 ": logicallyStarted: " + logicallyStarted +
                 ", requestsReceived: " + requestsReceived +
                 ", messagesSent: " + messagesSent;

      if (asynchReader != null)
      {
         s += ", " + asynchReader.toString();
      }

      return s;
   }
}
