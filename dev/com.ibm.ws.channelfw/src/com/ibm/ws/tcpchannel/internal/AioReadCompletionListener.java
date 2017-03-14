// IBM Confidential OCO Source Material
// 5724-I63, 5724-H88, 5655-N01, 5733-W61 (C) COPYRIGHT International Business Machines Corp. 2005, 2006
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
// Change History:
// Date     UserId      Defect          Description
// --------------------------------------------------------------------------------
// 04/05/05 gilgen      LIDB3618-2      Created file
// 05/09/05 wigger      271613          AIO Non-direct buffer support
// 06/30/05 gilgen      287777          Enable AIO from WebSphere
// 09/07/05 gilgen      303495          Add JIT support for ByteBuffers
// 10/02/05 gilgen      308856          Code review comments
// 10/18/05 wigger      314555          new timeout algorithm
// 10/24/05 wigger      314917          implement permisssion logic
// 11/10/05 wigger      314917.1        fix completed race condition
// 12/01/05 gilgen      328131          remove references to deleted class
// 12/01/05 gilgen      328382          don't set complete before all data has arrived
// 12/06/05 gilgen      324954          improve tracing 
// 12/17/05 gilgen      333647          add trace event when exception returned
// 01/10/06 wigger      306998.3        use isAnyTracingEnabled
// 01/30/06 wigger      342415          better connClosedException initialization
// 02/01/06 gilgen      343257          update timeout values on partial reads, add more perf stats
// 02/15/06 gilgen      347777          use public JDK methods to get to dumpStatsInterval
// 03/22/06 gilgen      355205          don't reset timeout if NO_TIMEOUT specified 
// 04/05/06 wigger      359362          null JIT read context buffer on failed IO
// 10/02/06 wigger      402392          return right away if vc is null

package com.ibm.ws.tcpchannel.internal;

import java.io.IOException;
import java.net.SocketTimeoutException;

import com.ibm.io.async.AsyncTimeoutException;
import com.ibm.io.async.IAbstractAsyncFuture;
import com.ibm.io.async.IAsyncFuture;
import com.ibm.io.async.ICompletionListener;
import com.ibm.io.async.TimerWorkItem;
import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.ffdc.FFDCFilter;
import com.ibm.wsspi.channelfw.VirtualConnection;

class AioReadCompletionListener implements ICompletionListener {
    private static final TraceComponent tc = Tr.register(AioReadCompletionListener.class, TCPChannelMessageConstants.TCP_TRACE_NAME, TCPChannelMessageConstants.TCP_BUNDLE);

    private static volatile IOException connClosedException = null; // volatile to ensure other threads see the constructed exception

    AioReadCompletionListener() {
        // nothing needs to be done in constructor
    }

    @Override
    public void futureCompleted(IAbstractAsyncFuture future, Object o) {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) {
            Tr.entry(tc, "AioReadCompletionListener.futureCompleted");
        }
        AioTCPReadRequestContextImpl req = (AioTCPReadRequestContextImpl) o;
        boolean errorOccurred = false;
        IOException ioe = null;
        long byteCount = 0;
        boolean complete = false;
        VirtualConnection vci = null;

        // New timeout code
        // Cancel timeout request
        TimerWorkItem twi = future.getTimeoutWorkItem();
        if (twi != null) {
            twi.state = TimerWorkItem.ENTRY_CANCELLED;
        }

        // check if close has already occurred
        vci = req.getTCPConnLink().getVirtualConnection();

        // looking at the vci is messy here, but we're not doing the try, catch
        // to save synchronization logic. vci could be null if connection
        // is closing before we can asked to request permission to finish
        // the read.
        if (vci != null) {
            if ((vci.isInputStateTrackingOperational())) {
                if (!vci.requestPermissionToFinishRead()) {
                    // can't get permission, so throw away request
                    if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
                        Tr.debug(tc, "Can't get permission to finish read, throwing read request away.");
                    }
                    if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) {
                        Tr.exit(tc, "AioReadCompletionListener.futureCompleted");
                    }
                    return;
                }
            }
        } else {
            // can't get the virtual connection object, so throw away request
            if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
                Tr.debug(tc, "Can't get virtual connection object, throwing read request away.");
            }
            if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) {
                Tr.exit(tc, "AioReadCompletionListener.futureCompleted");
            }
            return;
        }

        try {
            IAsyncFuture fut = (IAsyncFuture) future;
            byteCount = fut.getByteCount();
            // if a JIT buffer was used instead of the supplied buffers, replace them
            if (fut.getJITBuffer() != null) {
                req.setBuffer(fut.getJITBuffer());
            }

            if (byteCount == 0) {
                if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
                    Tr.debug(tc, "getByteCount returned 0, channel must be closed ");
                }
                errorOccurred = true;

                if (connClosedException == null) {
                    connClosedException = new IOException("Read failed: Connection closed by peer.");
                }
                ioe = connClosedException;
            } else {
                // Need to postProcess the buffers here, for indirect buffer support
                req.postProcessReadBuffers(byteCount);
                complete = req.updateIOCounts(byteCount, 0);
            }

        } catch (AsyncTimeoutException ate) {
            if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
                Tr.debug(tc, "exception caught: " + ate);
            }
            // convert AsyncTimeoutException to SocketTimeoutException
            ioe = new SocketTimeoutException(ate.getMessage());
            ioe.initCause(ate);
            errorOccurred = true;

        } catch (IOException ex) {
            if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
                Tr.debug(tc, "exception caught: " + ex);
            }
            ioe = ex;
            errorOccurred = true;

        } catch (InterruptedException ie) {
            if (TraceComponent.isAnyTracingEnabled() && tc.isEventEnabled()) {
                Tr.event(tc, "AioReadCompletionListener caught exception. " + ie);
            }
            FFDCFilter.processException(ie, getClass().getName() + ".futureCompleted", "134");
        }

        if (!errorOccurred) {
            if (complete) {
                future.setFullyCompleted(true);
                if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
                    Tr.debug(tc, "calling callback.complete method");
                }
                req.getReadCompletedCallback().complete(req.getTCPConnLink().getVirtualConnection(), req);
            } else {
                // we didn't get all the data we needed
                // update stats for partial read
                if (req.getTCPConnLink().getConfig().getDumpStatsInterval() > 0) {
                    req.getTCPConnLink().getTCPChannel().totalPartialAsyncReads.incrementAndGet();
                }
                // reset the timeout value if not infinite
                long remainingTimeout = req.getTimeoutInterval();
                if (remainingTimeout != 0) {
                    // timeout specified, reset it to the time left
                    remainingTimeout = req.getTimeoutTime() - System.currentTimeMillis();
                    // if timeout already expired, set IOException
                    if (remainingTimeout <= 0) {
                        ioe = new SocketTimeoutException("Async read timed out after reading partial data");
                        errorOccurred = true;
                    }
                }
                if (!errorOccurred) {
                    // still no errors, do another async read
                    try {
                        ((AioSocketIOChannel) req.getTCPConnLink().getSocketIOChannel()).readAIO(req, true, remainingTimeout);
                    } catch (IOException ex) {
                        ioe = ex;
                        errorOccurred = true;
                    }
                }
            }
        }
        if (errorOccurred) {
            if (req.getJITAllocateAction() == true && (req.getBuffer() != null)) {
                req.getBuffer().release();
                req.setBuffer(null);
            }
            if (TraceComponent.isAnyTracingEnabled() && tc.isEventEnabled()) {
                Tr.event(tc, "IOException while doing IO requested on local: " + req.getTCPConnLink().getSocketIOChannel().getSocket().getLocalSocketAddress() + " remote: "
                             + req.getTCPConnLink().getSocketIOChannel().getSocket().getRemoteSocketAddress());
                Tr.event(tc, "Calling read error callback with Exception is: " + ioe);
            }
            req.getReadCompletedCallback().error(req.getTCPConnLink().getVirtualConnection(), req, ioe);
        }
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) {
            Tr.exit(tc, "AioReadCompletionListener.futureCompleted");
        }
    }

}
