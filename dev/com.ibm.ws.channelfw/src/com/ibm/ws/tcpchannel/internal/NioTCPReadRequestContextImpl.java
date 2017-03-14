//* ===========================================================================
//*
//* IBM SDK, Java(tm) 2 Technology Edition, v5.0
//* (C) Copyright IBM Corp. 2005, 2006
//*
//* The source code for this program is not published or otherwise divested of
//* its trade secrets, irrespective of what has been deposited with the U.S.
//* Copyright office.
//*
//* ===========================================================================
//
// Change History:
// Date     UserId      Defect          Description
// --------------------------------------------------------------------------------
// 09/21/04 gilgen      233448          Add copyright statement and change history.
// 09/27/04 trg         234899          Added logic for sync work queue manager.
// 09/27/04 gilgen      235046          Added changeTimeout support
// 09/29/04 wdw         235756          Added immediate timeout support
// 04/15/05 gilgen      LIDB3618-2      Add AIO support, ake base TCPChannel extendable
// 04/28/05 wigger      271613          Nio class break out changes, file created from TCPReadRequestContextImpl
// 06/15/05 gilgen      287777          Enable AIO from WebSphere
// 07/29/05 wigger      294806          clean up comments.
// 12/17/05 gilgen      333647          Fix socketIOChannel reference, don't increment numReads 
// 04/05/06 wigger      359362          null JIT read context buffer on failed IO
// 08/13/07 wigger      451669          where possible use NonSafe methods for optomization

package com.ibm.ws.tcpchannel.internal;

import java.io.IOException;

import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.wsspi.bytebuffer.WsByteBuffer;
import com.ibm.wsspi.channelfw.ChannelFrameworkFactory;
import com.ibm.wsspi.channelfw.VirtualConnection;

/**
 * TCP read request specific for the NIO channel.
 */
public class NioTCPReadRequestContextImpl extends TCPReadRequestContextImpl {

    private static final TraceComponent tc = Tr.register(NioTCPReadRequestContextImpl.class, TCPChannelMessageConstants.TCP_TRACE_NAME, TCPChannelMessageConstants.TCP_BUNDLE);

    /**
     * Constructor.
     * 
     * @param link
     */
    public NioTCPReadRequestContextImpl(TCPConnLink link) {
        super(link);
    }

    /*
     * @see
     * com.ibm.ws.tcpchannel.internal.TCPReadRequestContextImpl#processSyncReadRequest
     * (long, int)
     */
    @Override
    public long processSyncReadRequest(long numBytes, int timeout) throws IOException {
        long bytesRead = 0L;
        if (numBytes != 0L) {
            if (this.blockWait == null) {
                this.blockWait = new SimpleSync();
            }

            this.blockingIOError = null;

            // before we read, signal that we want to do the read ourselves
            // and not the worker threads.
            this.blockedThread = true;

            VirtualConnection vc = readInternal(numBytes, null, false, timeout);

            while (vc == null) {
                // block until we are told to read
                this.blockWait.simpleWait();

                if (this.blockingIOError == null) {
                    vc = ((NioTCPChannel) getTCPConnLink().getTCPChannel()).getWorkQueueManager().processWork(this, 1);
                } else {
                    break;
                }
            }
            this.blockedThread = false;

            if (this.blockingIOError != null) {
                throw this.blockingIOError;
            }
            // return the number of bytes read
            bytesRead = getIOCompleteAmount();
        } else {
            // read immediately and return
            setJITAllocateAction(false);

            if ((getJITAllocateSize() > 0) && (getBuffers() == null)) {
                // User wants us to allocate the buffer
                if (getConfig().getAllocateBuffersDirect()) {
                    setBuffer(ChannelFrameworkFactory.getBufferManager().allocateDirect(getJITAllocateSize()));
                } else {
                    setBuffer(ChannelFrameworkFactory.getBufferManager().allocate(getJITAllocateSize()));
                }
                setJITAllocateAction(true);
            }
            WsByteBuffer wsBuffArray[] = getBuffers();
            NioSocketIOChannel channel = (NioSocketIOChannel) getTCPConnLink().getSocketIOChannel();
            if (wsBuffArray.length == 1) {
                bytesRead = channel.read(wsBuffArray[0].getWrappedByteBufferNonSafe());
            } else {
                bytesRead = channel.read(getByteBufferArray());
            }

            if (bytesRead < 0) {
                if (getJITAllocateAction()) {
                    getBuffer().release();
                    setBuffer(null);
                    setJITAllocateAction(false);
                }
                if (TraceComponent.isAnyTracingEnabled() && tc.isEventEnabled()) {
                    Tr.event(this, tc, "Sync read throwing IOException");
                }

                throw new IOException("Read failed.  End of data reached.");
            }
        }
        return bytesRead;
    }

    /*
     * @see com.ibm.ws.tcpchannel.internal.TCPReadRequestContextImpl#
     * processAsyncReadRequest()
     */
    @Override
    public VirtualConnection processAsyncReadRequest() {
        return ((NioTCPChannel) getTCPConnLink().getTCPChannel()).getWorkQueueManager().processWork(this, 0);
    }

    /*
     * @see
     * com.ibm.ws.tcpchannel.internal.TCPReadRequestContextImpl#immediateTimeout()
     */
    @Override
    protected void immediateTimeout() {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEventEnabled()) {
            Tr.event(this, tc, "immediateTimeout");
        }

        // if we do not have a selector, then there has not been a read yet
        // on this connection. If we have a selector, it doesn't mean there's
        // an active read; however, waking up the selector with a false timeout
        // attempt is not optimal but not a big deal
        ChannelSelector sel = ((NioSocketIOChannel) getTCPConnLink().getSocketIOChannel()).getChannelSelectorRead();
        if (null != sel) {
            // selector uses granularity of 1 second, so subtract 2 seconds
            // to guarantee the timeout will fire immediately
            this.timeoutTime = System.currentTimeMillis() - 2000L;
            sel.resetTimeout(this.timeoutTime);
        } else {
            if (TraceComponent.isAnyTracingEnabled() && tc.isEventEnabled()) {
                Tr.event(tc, "No read selector, ignoring immediate timeout");
            }
        }

    }

}