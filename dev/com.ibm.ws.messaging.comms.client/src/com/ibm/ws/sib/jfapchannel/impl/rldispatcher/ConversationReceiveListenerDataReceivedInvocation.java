/*
 * @start_prolog@
 * Version: @(#) 1.26 SIB/ws/code/sib.jfapchannel.client.common.impl/src/com/ibm/ws/sib/jfapchannel/impl/rldispatcher/ConversationReceiveListenerDataReceivedInvocation.java, SIB.comms, WASX.SIB, uu1215.01 08/06/10 10:37:57 [4/12/12 22:14:15]
 * ============================================================================
 * IBM Confidential OCO Source Materials
 *
 * 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70 Copyright IBM Corp. 2004, 2008
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
 * F185831         040106 prestona Original
 * F181603.2       040119 prestona JFAP Segmentation
 * F188491         040128 prestona Migrate to M6 CF + TCP Channel
 * F201521         040505 mattheg  getThreadContext implementation
 * D202625         040511 mattheg  Fix exception handling in getThreadContext
 * D202636         040511 mattheg  Ensure dispatcher stops on error in getThreadContext
 * D224570         040818 prestona JFap trace needs improving
 * D226223         040823 prestona Uses new messages
 * F248849         050201 prestona Improve receive listener dispatcher performance
 * D341600         060810 prestona Fix Java 5 compiler warnings
 * SIB0048b.com.5  060913 mattheg  JFap channel for Portly client rework
 * D418184         070919 mleming  Do nothing if conversation is already closed on invoke
 * D468802         070921 sibcopyr Automatic update of trace guards
 * D492528         080118 mayur    Fix typo in method name
 * D494112         080218 mleming  Fix synchronization issues
 * D500366         080527 mleming  Fix synchronization issues raised by findbugs
 * D524707         080529 mleming  Don't set ConversationReceiveListener multiple times
 * D528455         080610 djvines  Fix up FFDC calls
 * ============================================================================
 */
package com.ibm.ws.sib.jfapchannel.impl.rldispatcher;

import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ejs.ras.TraceNLS;
import com.ibm.websphere.sib.exception.SIErrorException;
import com.ibm.ws.ffdc.FFDCFilter;
import com.ibm.ws.sib.jfapchannel.Conversation;
import com.ibm.ws.sib.jfapchannel.ConversationReceiveListener;
import com.ibm.ws.sib.jfapchannel.Dispatchable;
import com.ibm.ws.sib.jfapchannel.JFapChannelConstants;
import com.ibm.ws.sib.jfapchannel.buffer.WsByteBuffer;
import com.ibm.ws.sib.jfapchannel.impl.Connection;
import com.ibm.ws.sib.jfapchannel.impl.ConversationImpl;
import com.ibm.ws.sib.jfapchannel.impl.JFapUtils;
import com.ibm.ws.sib.utils.ras.SibTr;
import com.ibm.ws.util.ObjectPool;

/**
 * Represents a invocation of the data received method of the
 * conversation receive listener class.  This class encapsulates
 * all the information and logic required to invoke the receive
 * method on the conversation receive listener class.
 * @see ConversationReceiveListener#dataReceived(WsByteBuffer, int, int, int, boolean, boolean, Conversation)
 */
final class ConversationReceiveListenerDataReceivedInvocation extends AbstractInvocation
{
   private static final TraceComponent tc = SibTr.register(ConversationReceiveListenerDataReceivedInvocation.class, JFapChannelConstants.MSG_GROUP, JFapChannelConstants.MSG_BUNDLE);

   private ConversationReceiveListener listener;
   private WsByteBuffer data;
   private boolean allocatedFromPool;
   private boolean partOfExchange;
   private ObjectPool owningPool;

   protected ConversationReceiveListenerDataReceivedInvocation(Connection connection,
                                                               ConversationReceiveListener listener,
                                                               WsByteBuffer data,
                                                               int size,
                                                               int segmentType,
                                                               int requestNumber,
                                                               int priority,
                                                               boolean allocatedFromPool,
                                                               boolean partOfExchange,
                                                               Conversation conversation,
                                                               ObjectPool owningPool)
   {
      super(connection,
            size,
            segmentType,
            requestNumber,
            priority,
            conversation);

      if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.entry(this, tc, "<init>",
                                           new Object[]
                                           {
                                              connection,
                                              listener,
                                              data,
                                              ""+size,
                                              ""+segmentType,
                                              ""+requestNumber,
                                              ""+priority,
                                              ""+allocatedFromPool,
                                              ""+partOfExchange,
                                              conversation,
                                           });

      this.listener = listener;
      this.data = data;
      this.allocatedFromPool = allocatedFromPool;
      this.partOfExchange = partOfExchange;
      this.owningPool = owningPool;

      if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.exit(this, tc, "<init>");
   }

   // Start F201521
   /**
    * This method will ask the receive listener for the thread context.
    *
    * @return Returns any thread context that the receive listener has for this data.
    */
   protected synchronized Dispatchable getThreadContext()
   {
      if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.entry(this, tc, "getThreadContext");

      // Start D202625
      // Save the position of the data
      int currentPos = data.position();
      int currentLimit = data.limit();
      Dispatchable dis = null;

      try
      {
         dis = listener.getThreadContext(conversation, data, segmentType);
      }
      catch (Throwable t)
      {
         FFDCFilter.processException(t, "com.ibm.ws.sib.jfapchannel.impl.rldispatcher.ConversationReceiveListenerDataReceivedInvocation.getThreadContext",
                                     JFapChannelConstants.CRLDATARECEIVEDINVOKE_GETTHREADCONTEXT_01,
                                     this);

         if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) SibTr.debug(this, tc, "exception thrown by getThreadContext");
         if (TraceComponent.isAnyTracingEnabled() && tc.isEventEnabled()) SibTr.exception(this, tc, t);

         // User has thrown an exception from getThreadContext method.  Probably
         // the best way to deal with this is to invalidate their connection.
         // That'll learn 'em.
         connection.invalidate(true, t, "exception thrown from getThreadContext - "+t.getMessage());  // D224570

         // Throw this bad boy to indicate to the dispatcher that something has gone wrong and
         // that it should abort processing this segment.
         throw new SIErrorException(TraceNLS.getFormattedMessage(JFapChannelConstants.MSG_BUNDLE, "CRLDRI_INTERNAL_SICJ0065", null, "CRLDRI_INTERNAL_SICJ0065")); // D226223
      }

      // Make sure we re-position the data
      data.position(currentPos);
      data.limit(currentLimit);
      // End D202625

      if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.exit(this, tc, "getThreadContext",dis);
      return dis;
   }
   // End F201521

   /**
    * Invokes the dataReceived method.  There are two things to watch out for here.
    * <ul>
    * <li>The callback can return a new data received listener to replace itself
    *     with.  If this happens, we need to invoke the new listener with the
    *     same set of data.</li>
    * <li>If the callback throws an exception, we need to handle this in a
    *     reasonable fashion - i.e. invalidate the connection.</li>
    * </ul>
    */
   protected synchronized void invoke()
   {
      if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.entry(this, tc, "invoke");

      //Have a quick look at the conversation, if it is not closed invoke the receive listener.
      if(!conversation.isClosed())
      {
         try
         {
            ConversationReceiveListener newReceiveListener = null;
            
            do
            {
               // Remember details about the byte buffer we pass in, in case
               // it is changed.
               int position = data.position();
               int limit = data.limit();

               // Pass details to implementor's conversation receive listener.
               if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) JFapUtils.debugTraceWsByteBuffer(this, tc, data, 16, "data passed to dataReceived method");
               newReceiveListener =
                 listener.dataReceived(data,
                                        segmentType,
                                        requestNumber,
                                        priority,
                                        allocatedFromPool,
                                        partOfExchange,
                                        conversation);

               if (newReceiveListener != null)
               {
                  // If implementor supplies a different listener, make this
                  // take effect.
                  if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) SibTr.debug(this, tc, "new receive listener supplied: "+newReceiveListener);
                  ((ConversationImpl)conversation).setDefaultReceiveListener(newReceiveListener);
                  listener = newReceiveListener;
                  data.limit(limit);
                  data.position(position);
               }
            }
            while(newReceiveListener != null);
         }
         catch(Throwable t)
         {
            FFDCFilter.processException
               (t, "com.ibm.ws.sib.jfapchannel.impl.rldispatcher.ConversationReceiveListenerDataReceivedInvocation.invoke", JFapChannelConstants.CRLDATARECEIVEDINVOKE_INVOKE_01);
            if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) SibTr.debug(this, tc, "exception thrown by dataReceived");
            if (TraceComponent.isAnyTracingEnabled() && tc.isEventEnabled()) SibTr.exception(this, tc, t);

            // User has thrown an exception from data received method.  Probably
            // the best way to deal with this is to invalidate their connection.
            // That'll learn 'em.
            connection.invalidate(true, t, "execption thrown by dataReceived method - "+t.getMessage());    // D224570

         }
      }
      else
      {
         if(TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) SibTr.debug(this, tc, "Conversation was already closed bypassing invoke.");
      }

      if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.exit(this, tc, "invoke");
   }

   /** Resets the state of this invocation object - used by the pooling code. */
   protected synchronized void reset(Connection connection,
                        ConversationReceiveListener listener,
                        WsByteBuffer data,
                        int size,
                        int segmentType,
                        int requestNumber,
                        int priority,
                        boolean allocatedFromPool,
                        boolean partOfExchange,
                        Conversation conversation)
   {
      if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.entry(this, tc, "reset",
                                           new Object[]
                                           {
                                              connection,
                                              listener,
                                              data,
                                              ""+size,
                                              ""+segmentType,
                                              ""+requestNumber,
                                              ""+priority,
                                              ""+allocatedFromPool,
                                              ""+partOfExchange,
                                              conversation
                                           });

      this.connection = connection;
      this.listener = listener;
      this.data = data;
      this.size = size;
      this.segmentType = segmentType;
      this.requestNumber = requestNumber;
      this.priority = priority;
      this.allocatedFromPool = allocatedFromPool;
      this.partOfExchange = partOfExchange;
      this.conversation = conversation;
      setDispatchable(null);

      if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.exit(this, tc, "reset");
   }

   /** Returns this object to its associated object pool. */
   protected synchronized void repool()
   {
      if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.entry(this, tc, "repool");

      // begin F181705.5
      connection = null;
      listener = null;
      data = null;
      conversation = null;
      setDispatchable(null);
      owningPool.add(this);
      // end F181705.5

      if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.exit(this, tc, "repool");
   }
}
