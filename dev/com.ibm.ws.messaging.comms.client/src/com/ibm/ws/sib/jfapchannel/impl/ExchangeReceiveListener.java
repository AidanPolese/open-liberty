/*
 * @start_prolog@
 * Version: @(#) 1.28 SIB/ws/code/sib.jfapchannel.client.common.impl/src/com/ibm/ws/sib/jfapchannel/impl/ExchangeReceiveListener.java, SIB.comms, WASX.SIB, uu1215.01 08/05/28 21:41:42 [4/12/12 22:14:13]
 * ============================================================================
 * IBM Confidential OCO Source Materials
 *
 * 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70  Copyright IBM Corp. 2003, 2008
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
 * Creation        030424 prestona Original
 * F166959         030521 prestona Rebase on non-prototype CF + TCP Channel
 * F174602         030819 prestona Switch to using SICommsException
 * F174678         030820 prestona Switch to using Semaphore from SIB.utils
 * D181601         031031 prestona Improve quality of JFAP Channel RAS
 * f181007         031211 mattheg  Add boolean 'exchange' flag on dataReceived()
 * F188491         030128 prestona Migrate to M6 CF + TCP Channel
 * D226223         040823 prestona Uses new messages
 * D289992         051114 prestona Reduce Semaphore creation
 * D329817         051207 mattheg  FFDC Failures
 * D343603         060202 prestona IllegalMonitorStateException in errorOccured
 * D377648         060714 mattheg  Move BufferPoolManagerReference into sib.utils / Use JFapByteBuffer
 * D341600         060810 prestona Fix Java 5 compiler warnings
 * SIB0048b.com.5  060913 mattheg  JFap channel for Portly client rework
 * 516687          080506 vaughton Add trace & sort out threading
 * 524762          080529 sibcopyr Automatic update of trace guards 
 * ============================================================================
 */
package com.ibm.ws.sib.jfapchannel.impl;

import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ejs.ras.TraceNLS;
import com.ibm.websphere.sib.exception.SIErrorException;
import com.ibm.ws.sib.jfapchannel.Conversation;
import com.ibm.ws.sib.jfapchannel.JFapChannelConstants;
import com.ibm.ws.sib.jfapchannel.ReceiveListener;
import com.ibm.ws.sib.jfapchannel.ReceivedData;
import com.ibm.ws.sib.jfapchannel.buffer.WsByteBuffer;
import com.ibm.ws.sib.utils.ras.SibTr;
import com.ibm.wsspi.sib.core.exception.SIConnectionLostException;

/**
 * Receive listener registered with send operations which can block until a response is received
 */

//@ThreadSafe
public class ExchangeReceiveListener implements ReceiveListener, ReceivedData {
  private static final TraceComponent tc = SibTr.register(ExchangeReceiveListener.class, JFapChannelConstants.MSG_GROUP, JFapChannelConstants.MSG_BUNDLE);

  //@start_class_string_prolog@
  public static final String $sccsid = "@(#) 1.28 SIB/ws/code/sib.jfapchannel.client.common.impl/src/com/ibm/ws/sib/jfapchannel/impl/ExchangeReceiveListener.java, SIB.comms, WASX.SIB, uu1215.01 08/05/28 21:41:42 [4/12/12 22:14:13]";
  //@end_class_string_prolog@

  static {
   if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) SibTr.debug(tc, "Source Info: " + $sccsid);
  }

  private final ExchangeReceiveListenerPool pool; // The pool to which this listener belongs

  //@GuardedBy("this")
  private int expectedRequestNumber; // Request number of expected data

  //@GuardedBy("this")
  private WsByteBuffer data; // Received data

  //@GuardedBy("this")
  private int segmentNumber; // Received segment number

  //@GuardedBy("this")
  private int requestNumber; // Received request number

  //@GuardedBy("this")
  private int priority; // Received priority

  //@GuardedBy("this")
  private boolean canPool; // Received can pool value

  //@GuardedBy("this")
  private SIConnectionLostException exception = null; // Received exception (if any)

  //@GuardedBy("this")
  private boolean requestComplete = false; // Indicated when receive request is complete

  // Constructor
  public ExchangeReceiveListener (final ExchangeReceiveListenerPool pool) {
    if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.entry(this, tc, "<init>", pool);
    this.pool = pool;
    if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.exit(this, tc, "<init>");
  }

  protected synchronized void setExpectedRequestNumber (final int reqNum) {
    if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.entry(this, tc, "setExpectedRequestNumber", "reqNum="+reqNum);
    expectedRequestNumber = reqNum;
    if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.exit(this, tc, "setExpectedRequestNumber");
  }

  protected synchronized void reset () {
    if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.entry(this, tc, "reset");
    data = null;
    exception = null;
    requestComplete = false;
    if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.exit(this, tc, "reset");
  }

  public synchronized void dataReceived(final WsByteBuffer d,
                                        final int segmentNumber,
                                        final int requestNumber,
                                        final int priority,
                                        final boolean canPool,
                                        final boolean partOfExchange,
                                        final Conversation conversation)
  {
    if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.entry(this, tc, "dataReceived", new Object[] {d, ""+segmentNumber, ""+requestNumber, ""+priority, ""+canPool, ""+partOfExchange, conversation});
    if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) JFapUtils.debugTraceWsByteBuffer(this, tc, d, 32, "Exchange listener data received");

    requestComplete = true;
    notify();

    if (requestNumber != expectedRequestNumber) {
      if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) SibTr.debug(this, tc, "detaReceived", "reqNum != expected");
      throw new SIErrorException(TraceNLS.getFormattedMessage(JFapChannelConstants.MSG_BUNDLE, "EXCHANGERL_INTERNAL_SICJ0049", null, "EXCHANGERL_INTERNAL_SICJ0049"));
    }

    if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
      StringBuffer sb = new StringBuffer("dataReceived:\ndata=");
      sb.append(data);
      sb.append("\nsegmentNumber="+segmentNumber);
      sb.append("\nrequestNumber="+requestNumber);
      sb.append("\npriority="+priority);
      sb.append("\ncanPool="+canPool);
      sb.append("\npartOfExchange="+partOfExchange);
      SibTr.debug(this, tc, sb.toString());
    }

    this.data = d;
    this.segmentNumber = segmentNumber;
    this.requestNumber = requestNumber;
    this.priority = priority;
    this.canPool = canPool;
    exception = null;

    if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.exit(this, tc, "dataReceived");
  }

  public synchronized void errorOccurred(final SIConnectionLostException e,
                                         final int segmentNumber,
                                         final int requestNumber,
                                         final int priority,
                                         final Conversation conversation) {
    if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.entry(this, tc, "errorOccurred", new Object[] {e, ""+segmentNumber, ""+requestNumber, ""+priority, conversation});

    requestComplete = true;
    notify();

    if (requestNumber != expectedRequestNumber) {
      if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) SibTr.debug(this, tc, "requestNumber ("+requestNumber+") != expectedRequestNumber ("+expectedRequestNumber+")");
      throw new SIErrorException(TraceNLS.getFormattedMessage(JFapChannelConstants.MSG_BUNDLE, "EXCHANGERL_INTERNAL_SICJ0049", null, "EXCHANGERL_INTERNAL_SICJ0049"));
    }

    exception = e;

    if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.exit(this, tc, "errorOccurred");
  }

  public synchronized boolean successful () {
    if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.entry(this, tc, "successful");
    final boolean rc = (exception == null);
    if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.exit(this, tc, "successful","rc="+rc);
    return rc;
  }

  public synchronized SIConnectionLostException getException () {
    if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.entry(this, tc, "getException");
    if (TraceComponent.isAnyTracingEnabled() && tc.isEventEnabled() && (exception != null)) SibTr.exception(this, tc, exception);
    if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.exit(this, tc, "getException",exception);
    return exception;
  }

  /** @see com.ibm.js.comms.channelfw.ReceiveListener#closeRecieved(boolean) */
  public void closeReceived (final boolean quiesce) { // Not synchronized as is a nop
    if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.entry(this, tc, "closeReceived", "quiesce="+quiesce);
    if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.exit(this, tc, "closeReceived");
  }

  /** @see com.ibm.ws.sib.jfapchannel.ReceivedData#getBuffer() */
  public synchronized WsByteBuffer getBuffer () {
    return data;
  }

  /** @see com.ibm.ws.sib.jfapchannel.ReceivedData#getSegmentType() */
  public synchronized int getSegmentType () {
    return segmentNumber;
  }

  /** @see com.ibm.ws.sib.jfapchannel.ReceivedData#getRequestId() */
  public synchronized int getRequestId () {
    return requestNumber;
  }

  /** @see com.ibm.ws.sib.jfapchannel.ReceivedData#getPriority() */
  public synchronized int getPriority () {
    return priority;
  }

  /** @see com.ibm.ws.sib.jfapchannel.ReceivedData#getAllocatedFromBufferPool() */
  public synchronized boolean getAllocatedFromBufferPool () {
    return canPool;
  }

  /**
   * Wait for the request to complete.  This blocks until the dataReceived or errorOccured
   * methods are invoked.
   */
  protected synchronized void waitToComplete () {
    if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.entry(this, tc, "waitToComplete");

    while (!requestComplete) {
      try {
        wait();
      } catch (InterruptedException e) {
        // No FFDC Code Needed
      }
    }

    if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.exit(this, tc, "waitToComplete");
  }

  /** @see com.ibm.ws.sib.jfapchannel.ReceivedData#release() */
  public void release() { // Not synchronized because only reads a final variable
    if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.entry(this, tc, "release");
    pool.release(this);
    if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.exit(this, tc, "release");
  }
}
