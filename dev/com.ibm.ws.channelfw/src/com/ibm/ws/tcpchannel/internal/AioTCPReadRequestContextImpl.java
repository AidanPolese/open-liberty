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
// 04/28/05 wigger      271613          Nio class break out changes
// 06/29/05 gilgen      287777          Enable AIO for WAS  
// 09/05/05 gilgen      302453          M3 code cleanup/perf improvements
// 09/07/05 gilgen      303495          Add JIT support for ByteBuffers
// 09/22/05 gilgen      307313          Code cleanup/improvements
// 10/02/05 gilgen      308856          Code review comments
// 12/01/05 gilgen      328131          remove references to deleted class
// 12/07/05 wigger      329877          remove redundant error message output
// 12/17/05 gilgen      333647          get socketIOChannel from connlink instead of req
// 01/10/06 wigger      306998.3        use isAnyTracingEnabled
// 02/01/06 gilgen      343257          update timeout values on partial reads, add more perf stats 
// 04/05/06 wigger      359362          null JIT read context buffer on failed IO
// 06/27/07 wigger      448755          can't JIT Allocate of nonDirect buffers at native layer

package com.ibm.ws.tcpchannel.internal;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;

import com.ibm.io.async.AsyncTimeoutException;
import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.wsspi.channelfw.ChannelFrameworkFactory;
import com.ibm.wsspi.channelfw.VirtualConnection;

/**
 * TCP channel's AIO specific implemention for the read service context.
 */
public class AioTCPReadRequestContextImpl extends TCPReadRequestContextImpl {

    private static final TraceComponent tc = Tr.register(AioTCPReadRequestContextImpl.class, TCPChannelMessageConstants.TCP_TRACE_NAME, TCPChannelMessageConstants.TCP_BUNDLE);

    private boolean immedTimeoutRequested = false;
    private boolean jITAllocatedDirect = false;

    /**
     * Constructor.
     * 
     * @param value
     */
    public AioTCPReadRequestContextImpl(TCPConnLink value) {
        super(value);
    }

    public VirtualConnection processAsyncReadRequest() {
        IOException exThisTime = null;
        immedTimeoutRequested = false;

        // IMPROVEMENT: logic associated with allocating buffers should be
        // done before ever calling this method - its not specific to AIO
        setJITAllocateAction(false);
        this.jITAllocatedDirect = false;
        if (getJITAllocateSize() > 0 && getBuffers() == null) {
            if (oTCPConnLink.getConfig().getAllocateBuffersDirect()) {
                setBuffer(ChannelFrameworkFactory.getBufferManager().allocateDirect(getJITAllocateSize()));
                this.jITAllocatedDirect = true;
            } else {
                setBuffer(ChannelFrameworkFactory.getBufferManager().allocate(getJITAllocateSize()));
                // Only Direct buffers will be truly JIT allocated in ResultHandler.
                // This is because one ResultHandler is used by multiple channels,
                // and cannot pre-determine what type of buffer to allocate.
            }
            setJITAllocateAction(true);
        }

        try {
            boolean complete = ((AioSocketIOChannel) getTCPConnLink().getSocketIOChannel()).readAIO(this, isForceQueue(), getTimeoutInterval());

            // return right away, without using a future, if data was available right
            // away
            if (complete) {
                // be quick, return here
                return oTCPConnLink.getVirtualConnection();
            }

        } catch (IOException ex) {
            if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
                Tr.debug(tc, "IOexception caught: " + ex);
            }
            exThisTime = ex;
        }

        if (exThisTime != null) {
            if (TraceComponent.isAnyTracingEnabled() && tc.isEventEnabled()) {

                // make a reasonable attempt not to NPE in the event tracing
                SocketIOChannel x = getTCPConnLink().getSocketIOChannel();
                Socket socket = null;
                SocketAddress la = null;
                String las = null;
                SocketAddress ra = null;
                String ras = null;

                if (x != null) {
                    socket = x.getSocket();
                    if (socket != null) {
                        la = socket.getLocalSocketAddress();
                        if (la != null) {
                            las = la.toString();
                        }
                        ra = socket.getRemoteSocketAddress();
                        if (ra != null) {
                            ras = ra.toString();
                        }
                    }
                }

                Tr.event(tc, "IOException while processing readAsynch request local: " + las + " remote: " + ras);

                Tr.event(tc, "Exception is: " + exThisTime);
            }

            // release unused JIT buffer if allocated
            if (getJITAllocateAction()) {
                getBuffer().release();
                setBuffer(null);
                setJITAllocateAction(false);
            }

            // if immediate IOException, we always call the error method before
            // returning
            getReadCompletedCallback().error(getTCPConnLink().getVirtualConnection(), this, exThisTime);
        }
        return null;
    }

    // IMPROVEMENT: look at corresponding improvement(s) in the process Async path
    // that apply here
    public long processSyncReadRequest(long numBytes, int timeout) throws IOException {

        long bytesRead = 0;
        boolean freeJIT = false;
        IOException exThisTime = null;
        immedTimeoutRequested = false;

        setJITAllocateAction(false);
        this.jITAllocatedDirect = false;
        // allocate buffers if asked to do so, and none exist
        if (getJITAllocateSize() > 0 && getBuffers() == null) {
            if (oTCPConnLink.getConfig().getAllocateBuffersDirect()) {
                setBuffer(ChannelFrameworkFactory.getBufferManager().allocateDirect(getJITAllocateSize()));
                this.jITAllocatedDirect = true;
            } else {
                setBuffer(ChannelFrameworkFactory.getBufferManager().allocate(getJITAllocateSize()));
            }
            setJITAllocateAction(true);
        }

        // set context read parameters
        setIOAmount(numBytes);
        setLastIOAmt(0);
        setIODoneAmount(0);
        setTimeoutTime(timeout);

        try {
            bytesRead = ((AioSocketIOChannel) getTCPConnLink().getSocketIOChannel()).readAIOSync(numBytes, this);

            if (numBytes == 0 && bytesRead == 0) {
                freeJIT = true;
            }

        } catch (AsyncTimeoutException ate) {
            exThisTime = new SocketTimeoutException(ate.getMessage());
            exThisTime.initCause(ate);
            freeJIT = true;

        } catch (IOException ioe) {
            exThisTime = ioe;
            freeJIT = true;

        }

        if (freeJIT && getJITAllocateAction()) {
            getBuffer().release();
            setBuffer(null);
            setJITAllocateAction(false);
        }

        if (exThisTime != null) {
            throw exThisTime;
        }

        return bytesRead;

    }

    protected void immediateTimeout() {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) {
            Tr.entry(tc, "immediateTimeout");
        }

        this.immedTimeoutRequested = true;
        ((AioSocketIOChannel) getTCPConnLink().getSocketIOChannel()).timeoutReadFuture();

        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) {
            Tr.exit(tc, "immediateTimeout");
        }
    }

    protected boolean isImmedTimeoutRequested() {
        return this.immedTimeoutRequested;
    }

    protected boolean getJITAllocatedDirect() {
        return this.jITAllocatedDirect;
    }

}