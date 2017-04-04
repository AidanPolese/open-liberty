/*
 * @start_prolog@
 * Version: @(#) 1.16 SIB/ws/code/sib.jfapchannel.client.common.impl/src/com/ibm/ws/sib/jfapchannel/impl/rldispatcher/ReceiveListenerDataReceivedInvocation.java, SIB.comms, WASX.SIB, uu1215.01 08/05/27 15:58:19 [4/12/12 22:14:15]
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
 * F188491         030128 prestona Migrate to M6 CF + TCP Channel
 * F201521         040505 mattheg  getThreadContext implementation
 * D224570         040818 prestona JFap trace needs improving
 * F248849         050201 prestona Improve receive listener dispatcher performance
 * D341600         060810 prestona Fix Java 5 compiler warnings
 * SIB0048b.com.5  060913 mattheg  JFap channel for Portly client rework
 * D494112         080218 mleming  Fix synchronization issues
 * 500219          080225 sibcopyr Automatic update of trace guards 
 * D500366         080527 mleming  Fix synchronization issues raised by findbugs
 * ============================================================================
 */
package com.ibm.ws.sib.jfapchannel.impl.rldispatcher;

import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.ffdc.FFDCFilter;
import com.ibm.ws.sib.jfapchannel.Conversation;
import com.ibm.ws.sib.jfapchannel.Dispatchable;
import com.ibm.ws.sib.jfapchannel.JFapChannelConstants;
import com.ibm.ws.sib.jfapchannel.ReceiveListener;
import com.ibm.ws.sib.jfapchannel.buffer.WsByteBuffer;
import com.ibm.ws.sib.jfapchannel.impl.Connection;
import com.ibm.ws.sib.jfapchannel.impl.JFapUtils;
import com.ibm.ws.sib.utils.ras.SibTr;
import com.ibm.ws.util.ObjectPool;

/**
 * Represents a invocation of the data received method of the
 * receive listener class.  This class encapsulates all the information
 * and logic required to invoke the receive method on the receive
 * listener class.
 * @see ReceiveListener#dataReceived(WsByteBuffer, int, int, int, boolean, boolean, Conversation)
 */
final class ReceiveListenerDataReceivedInvocation extends AbstractInvocation
{
   private static final TraceComponent tc = SibTr.register(ReceiveListenerDataReceivedInvocation.class, JFapChannelConstants.MSG_GROUP, JFapChannelConstants.MSG_BUNDLE);

   private ReceiveListener listener;
   private WsByteBuffer data;
   private boolean allocatedFromPool;
   private boolean partOfExchange;
   private ObjectPool owningPool;

   protected ReceiveListenerDataReceivedInvocation(Connection connection,
                                                   ReceiveListener listener,
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
                                              owningPool
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
    * Not needed for receive listener data invocations
    *
    * @return Returns null.
    */
   protected Dispatchable getThreadContext()
   {
      if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.entry(this, tc, "getThreadContext");
      if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.exit(this, tc, "getThreadContext");

      return null;
   }
   // End F201521

   /**
    * Invokes the dataReceived method.  If the callback throws an exception
    * than the connection associated with it is invalidated.
    */
   protected synchronized void invoke()
   {
      if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.entry(this, tc, "invoke");
      try
      {
         // Pass details to implementor's conversation receive listener.
         if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) JFapUtils.debugTraceWsByteBuffer(this, tc, data, 16, "data passed to dataReceived method");
         listener.dataReceived(data,
                               segmentType,
                               requestNumber,
                               priority,
                               allocatedFromPool,
                               partOfExchange,
                               conversation);
      }
      catch(Throwable t)
      {
         FFDCFilter.processException
            (t, "com.ibm.ws.sib.jfapchannel.impl.rldispatcher.ReceiveListenerDataReceivedInvocation", JFapChannelConstants.RLDATARECEIVEDINVOKE_INVOKE_01);
         if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) SibTr.debug(this, tc, "exception thrown by dataReceived");
         if (TraceComponent.isAnyTracingEnabled() && tc.isEventEnabled()) SibTr.exception(this, tc, t);

         // User has thrown an exception from data received method.  Probably
         // the best way to deal with this is to invalidate their connection.
         // That'll learn 'em.
         connection.invalidate(true, t, "exception thrown by dataReceived - "+t.getLocalizedMessage());  // D224570

      }

      if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.exit(this, tc, "invoke");
   }

   /** Resets the state of this invocation object - used by the pooling code. */
   protected synchronized void reset(Connection connection,
                        ReceiveListener listener,
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
      // end F181705.5
      owningPool.add(this);
      setDispatchable(null);

      if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.exit(this, tc, "repool");
   }
}
