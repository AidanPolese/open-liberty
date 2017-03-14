// IBM Confidential OCO Source Material
// 5724-I63, 5724-H88, 5655-N01, 5733-W61 (C) COPYRIGHT International Business Machines Corp. 2005, 2006
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
// Change History:
// Date     UserId      Defect          Description
// --------------------------------------------------------------------------------
// 08/31/05 gilgen      LIDB3618-2      M2/M3 drops 
// 09/01/05 gilgen      302453          M3 code updates
// 09/22/05 gilgen      307313          Code cleanup/improvements
// 10/02/05 gilgen      308856.1        Code review comments
// 10/12/05 gilgen      310885          Reduce synchronization
// 10/18/05 wigger      314555          new timeout changes
// 10/26/05 gilgen      317392          improve tracing
// 11/01/05 wigger      317392          performance changes, fix race conditions
// 11/03/05 gilgen      320175          make accept and connect common with base TCP/cleanup
// 12/12/05 wigger      331203          don't send error caused by timeout-cancel to the user
// 01/10/06 wigger      306998.3        use isAnyTracingEnabled
// 01/19/06 gilgen      336062          new threading model 
// 01/25/06 gilgen      341342          add additional tracing
// 10/30/06 gilgen      402392          invoke callback on pooled thread when timeout occurs

package com.ibm.io.async;

import java.io.IOException;

import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.tcpchannel.internal.TCPChannelMessageConstants;

abstract class AsyncChannelFuture extends AbstractAsyncFuture {

    private static final TraceComponent tc = Tr.register(AsyncChannelFuture.class,
                                                         TCPChannelMessageConstants.TCP_TRACE_NAME,
                                                         TCPChannelMessageConstants.TCP_BUNDLE);

    // new timeout code - timeout work item for this future object request
    protected TimerWorkItem timeoutTracker = null;

    /**
     * Construct a future representing an operation on the given channel.
     * 
     * @param channel
     */
    protected AsyncChannelFuture(AbstractAsyncChannel channel) {
        super(channel);
    }

    /*
     * @see com.ibm.io.async.AbstractAsyncFuture#addCompletionListener(com.ibm.io.async.ICompletionListener, java.lang.Object)
     */
    public void addCompletionListener(ICompletionListener listener, Object state) {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) {
            Tr.entry(tc, "addCompletionListener, listener " + listener);
        }

        boolean alreadyComplete = true;
        // DBG - the fullyCompleted flag doesn't get set to true until completion listener called, so why check?
        // also, why fully completed? if partial completed, it will still
        // be completed = true, and will bail on next check
        if (!this.fullyCompleted) {
            synchronized (this.completedSemaphore) {
                // check if the receiver is already completed.
                if (!this.completed) {
                    alreadyComplete = false;

                    // need to set listener in sync so future cannot be completed
                    // before listener is added
                    this.firstListener = listener;
                    this.firstListenerState = state;
                }
            }
        }

        // if already complete, listener would not have been notified
        if (alreadyComplete) {
            if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
                Tr.debug(tc, "request already complete - notifying listener");
            }
            invokeCallback(listener, this, state);
        }
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) {
            Tr.exit(tc, "addCompletionListener, listener " + listener);
        }
    }

    /**
     * Attempts to cancel the operation represented by the AsyncFuture.
     * Cancellation will not succeed if the operation is already complete.
     * 
     * @param reason the object that will be thrown when the future results are retrieved.
     */
    public void cancel(Exception reason) {
        // IMPROVEMENT: need to rework how syncs on the future.complete() work. We really should be
        // syncing here, so we don't do the channel.cancel if the request is processing
        // future.complete() on another thread at the same time. Should just do a quick
        // sync, check the future.complete flag, then process only if !complete. That will
        // also mean we can remove a bunch of redundant checks for complete, but we need to check all
        // the paths carefully.
        if (this.channel == null) {
            return;
        }
        synchronized (this.completedSemaphore) {
            if (!this.completed) {
                try {
                    // this ends up calling future.completed()
                    this.channel.cancel(this, reason);
                } catch (Exception e) {
                    // Simply swallow the exception
                } // end try

            } else {
                if (this.channel.readFuture != null) {
                    this.channel.readFuture.setCancelInProgress(0);
                }
                if (this.channel.writeFuture != null) {
                    this.channel.writeFuture.setCancelInProgress(0);
                }
            }
        }
    }

    /**
     * When an async future has completed the following actions are performed,
     * in this order: <ol><li> The Future is marked completed <li> If
     * callbacks are registered, they are invoked in sequence, running
     * arbitrary code. </li><li> The semaphore blocking any waiting threads is
     * signalled to wake them up. </li></ol>
     * 
     */
    // Impl Assumes we are holding the completed sem lock
    protected void fireCompletionActions() {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) {
            Tr.entry(tc, "fireCompletionActions");
        }
        if (this.firstListener != null) {
            ICompletionListener listenerToInvoke = this.firstListener;
            // reset listener so it can't be inadvertently be called on the next request
            this.firstListener = null;
            if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
                Tr.debug(tc, "invoking callback for channel id: " + this.channel.channelIdentifier);
            }
            invokeCallback(listenerToInvoke, this, this.firstListenerState);
        } else {
            if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
                Tr.debug(tc, "no listener found for event, future: " + this);
            }
        }
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) {
            Tr.exit(tc, "fireCompletionActions");
        }
    }

    /**
     * Throws the receiver's exception in its correct class.
     * 
     * This assumes that the exception is not null.
     * 
     * @throws InterruptedException
     * @throws IOException
     */
    protected void throwException() throws InterruptedException, IOException {

        if (this.exception instanceof IOException) {
            throw (IOException) this.exception;
        }
        if (this.exception instanceof InterruptedException) {
            throw (InterruptedException) this.exception;
        }
        if (this.exception instanceof RuntimeException) {
            throw (RuntimeException) this.exception;
        }
        throw new RuntimeException(this.exception);
    }

    /**
     * Store the timeout related item.
     * 
     * @param twi
     */
    public void setTimeoutWorkItem(TimerWorkItem twi) {
        this.timeoutTracker = twi;
    }

    /**
     * Access the stored timeout work item.
     * 
     * @return TimerWorkItem
     */
    public TimerWorkItem getTimeoutWorkItem() {
        return this.timeoutTracker;
    }

}