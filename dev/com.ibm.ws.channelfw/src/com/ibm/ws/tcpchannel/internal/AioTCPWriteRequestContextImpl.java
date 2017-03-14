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
// 06/29/05 gilgen      287777          Enable AIO in WAS 
// 09/22/05 gilgen      307313          Code cleanup/improvements
// 10/02/05 gilgen      308856          Code review comments
// 10/27/05 wigger      317856          fix WRITE_ALL_DATA to write all data
// 12/01/05 gilgen      328131          remove references to deleted class
// 12/06/05 gilgen      324954          improve trace readability
// 12/17/05 gilgen      333647          get socketIOChannel from connlink instead of req
// 01/10/06 wigger      306998.3        use isAnyTracingEnabled
// 02/01/06 gilgen      343257          update timeout values on partial writes, add more perf stats 
// 06/27/07 wigger      448755          protect event tracing from NPEs

package com.ibm.ws.tcpchannel.internal;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;

import com.ibm.io.async.AsyncTimeoutException;
import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.wsspi.channelfw.VirtualConnection;

/**
 * TCP write context specific to the AIO environment.
 */
public class AioTCPWriteRequestContextImpl extends TCPWriteRequestContextImpl {

    private static final TraceComponent tc = Tr.register(AioTCPWriteRequestContextImpl.class, TCPChannelMessageConstants.TCP_TRACE_NAME, TCPChannelMessageConstants.TCP_BUNDLE);
    private boolean immedTimeoutRequested = false;

    /**
     * Constructor.
     * 
     * @param link
     */
    protected AioTCPWriteRequestContextImpl(TCPConnLink link) {
        super(link);
    }

    public VirtualConnection processAsyncWriteRequest() {
        immedTimeoutRequested = false;
        IOException exThisTime = null;

        try {
            boolean complete = ((AioSocketIOChannel) getTCPConnLink().getSocketIOChannel()).writeAIO(this, isForceQueue(), getTimeoutInterval());
            if (complete) {
                // be quick, return here
                return oTCPConnLink.getVirtualConnection();
            }

        } catch (IOException ioe) {
            if (ioe instanceof AsyncTimeoutException) {
                exThisTime = new SocketTimeoutException(ioe.getMessage());
                exThisTime.initCause(ioe);
            } else {
                exThisTime = ioe;
            }
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

                Tr.event(tc, "IOException while processing writeAsynch request local: " + las + " remote: " + ras);

                Tr.event(tc, "Exception is: " + exThisTime);
            }

            getWriteCompletedCallback().error(getTCPConnLink().getVirtualConnection(), this, exThisTime);
        }
        return null;
    }

    public long processSyncWriteRequest(long numBytes, int timeout) throws IOException {

        immedTimeoutRequested = false;

        // set the context write parameters
        setIOAmount(numBytes);
        setLastIOAmt(0);
        setIODoneAmount(0);
        setTimeoutTime(timeout);
        SocketIOChannel channel = oTCPConnLink.getSocketIOChannel();

        try {
            // be quick, return here
            return ((AioSocketIOChannel) channel).writeAIOSync(this);

        } catch (IOException ioe) {
            if (TraceComponent.isAnyTracingEnabled() && tc.isEventEnabled()) {
                Tr.event(tc, "IOException while processing sync write. request local: " + channel.getSocket().getLocalSocketAddress() + " remote: "
                             + channel.getSocket().getRemoteSocketAddress());
                Tr.event(tc, "Exception is: " + ioe);
            }
            throw ioe;
        }
    }

    protected boolean updateForAllData(long byteCount) {
        ByteBuffer[] buffers = getByteBufferArray();
        int i = 0;
        boolean allComplete = true;

        while ((i < buffers.length) && (allComplete == true)) {
            if (buffers[i].hasRemaining()) {
                allComplete = false;
            }
            i++;
        }

        setLastIOAmt(byteCount);
        setIODoneAmount(getIODoneAmount() + byteCount);

        if (TraceComponent.isAnyTracingEnabled() && tc.isEventEnabled()) {
            SocketIOChannel channel = getTCPConnLink().getSocketIOChannel();
            Tr.event(tc, "Wrote " + byteCount + "(" + +getIODoneAmount() + ")" + " bytes, " + getIOAmount() + " requested on local: " + channel.getSocket().getLocalSocketAddress()
                         + " remote: " + channel.getSocket().getRemoteSocketAddress());
        }

        if (allComplete) {
            // read is complete on current thread
            if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
                Tr.debug(tc, "write complete, at least minimum amount of data written");
            }

        } else {
            Tr.debug(tc, "write not complete, more data needs to be written");
        }

        return allComplete;

    }

    protected void immediateTimeout() {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) {
            Tr.entry(tc, "immediateTimeout");
        }

        immedTimeoutRequested = true;
        ((AioSocketIOChannel) getTCPConnLink().getSocketIOChannel()).timeoutWriteFuture();

        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) {
            Tr.exit(tc, "immediateTimeout");
        }
    }

    protected boolean isImmedTimeoutRequested() {
        return immedTimeoutRequested;
    }

}
