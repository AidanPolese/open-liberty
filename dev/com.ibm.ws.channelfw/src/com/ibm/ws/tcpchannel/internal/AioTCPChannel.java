// IBM Confidential OCO Source Material
// 5724-I63, 5724-H88, 5655-N01, 5733-W61 (C) COPYRIGHT International Business Machines Corp. 2005, 2006
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
// Change History:
// Date     UserId      Defect          Description
// --------------------------------------------------------------------------------

// 04/05/05 gilgen      LIDB3618-2      Created new file
// 04/21/05 wigger      LIDB3618-2      Aio Support Changes
// 06/29/05 gilgen      287777          Support for WAS extensions of base 
// 07/21/05 gilgen      219300          Only create 1 AsyncChannelGroup   
// 08/01/05 wigger      294806          pass back null termination object
// 08/21/05 gilgen      298587          eliminate AioThreadPools
// 09/01/05 gilgen      302453          M3 code cleanup
// 09/27/05 gilgen      307313          Code cleanup/improvements
// 10/02/05 gilgen      308856          Code review comments
// 10/02/05 gilgen      308856.1        Code review comments
// 10/24/05 wigger      316352          Don't use native JIT with non-Direct JIT
// 11/04/05 wigger      320175          make connect common with base TCP
// 11/29/05 wigger      327358          create a connection manager per channel
// 01/10/06 wigger      306998.3        use isAnyTracingEnabled
// 01/17/06 gilgen      306062          dump more statistics
// 02/02/06 gilgen      343082          eliminate FFDC when AIO not usable
// 02/13/06 gilgen      345836          add more tracing
// 02/24/06 wigger      327980.1        better connection info debug
// 03/31/06 wigger      358208          prevent security excpetions
// 04/12/06 gilgen      363238          shutdown AIO library if all channels are destroyed
// 03/06/07 wigger      422106          don't shutdown AIO till Channel Factory is destroyed

package com.ibm.ws.tcpchannel.internal;

import java.io.IOException;
import java.net.Socket;
import java.nio.channels.SocketChannel;
import java.util.Hashtable;

import com.ibm.io.async.AsyncChannelGroup;
import com.ibm.io.async.AsyncException;
import com.ibm.io.async.AsyncLibrary;
import com.ibm.io.async.AsyncSocketChannel;
import com.ibm.io.async.IAsyncProvider;
import com.ibm.websphere.channelfw.ChannelData;
import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.wsspi.channelfw.exception.ChannelException;

/**
 * TCP channel class that handles AIO logic.
 */
public class AioTCPChannel extends TCPChannel implements ChannelTermination {

    private AsyncChannelGroup asyncChannelGroup;
    private static Hashtable<String, AsyncChannelGroup> groups = new Hashtable<String, AsyncChannelGroup>();
    private static AioReadCompletionListener aioReadCompletionListener = null;
    private static AioWriteCompletionListener aioWriteCompletionListener = null;
    private static boolean jitSupportedByNative;

    private static final TraceComponent tc = Tr.register(AioTCPChannel.class, TCPChannelMessageConstants.TCP_TRACE_NAME, TCPChannelMessageConstants.TCP_BUNDLE);

    private static AioWorkQueueManager wqm = null;

    /**
     * Constructor.
     */
    public AioTCPChannel() {
        super();
    }

    /*
     * @see
     * com.ibm.ws.tcpchannel.internal.TCPChannel#setup(com.ibm.websphere.channelfw
     * .ChannelData, com.ibm.ws.tcpchannel.internal.TCPChannelConfiguration,
     * com.ibm.ws.tcpchannel.internal.TCPChannelFactory)
     */
    public ChannelTermination setup(ChannelData chanData, TCPChannelConfiguration oTCPChannelConfig, TCPChannelFactory _f) throws ChannelException {

        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) {
            Tr.entry(tc, "setup");
        }
        super.setup(chanData, oTCPChannelConfig, _f);

        // try to load the AsyncLibrary. It will throw an exception if it can't load
        try {
            IAsyncProvider provider = AsyncLibrary.createInstance();

            if (getConfig().getAllocateBuffersDirect()) {
                jitSupportedByNative = provider.hasCapability(IAsyncProvider.CAP_JIT_BUFFERS);
            } else {
                jitSupportedByNative = false;
            }
        } catch (AsyncException ae) {
            if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
                Tr.debug(tc, "AioTCPChannel couldn't load native AIO library: " + ae.getMessage());
            }
            throw new ChannelException(ae);
        }

        if (!getConfig().isInbound()) {
            boolean startSelectors = false;
            if (wqm == null) {
                wqm = new AioWorkQueueManager();
                startSelectors = true;
            }
            super.connectionManager = new ConnectionManager(this, wqm);
            if (startSelectors) {
                wqm.startSelectors(false);
            }
        }

        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) {
            Tr.exit(tc, "setup");
        }
        return this;
    }

    /*
     * @see com.ibm.wsspi.channelfw.Channel#init()
     */
    public void init() throws ChannelException {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) {
            Tr.entry(tc, "init");
        }
        super.init();

        if (!getConfig().isInbound()) {
            try {
                this.asyncChannelGroup = findOrCreateACG();
                if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
                    Tr.debug(tc, "Created completion port for outbound connections, completionPort = " + this.asyncChannelGroup.getCompletionPort());
                }
            } catch (AsyncException ae) {
                ChannelException ce = new ChannelException("Error creating async channel group ");
                ce.initCause(ae);
                throw ce;
            }
        }
        // create AIO CompletionListeners
        aioReadCompletionListener = new AioReadCompletionListener();
        aioWriteCompletionListener = new AioWriteCompletionListener();

        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) {
            Tr.exit(tc, "init");
        }
    }

    /*
     * @see com.ibm.wsspi.channelfw.Channel#start()
     */
    public void start() throws ChannelException {
        super.start();
        getAsyncChannelGroup().activate();
    }

    /*
     * @see com.ibm.ws.tcpchannel.internal.ChannelTermination#terminate()
     */
    public void terminate() {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) {
            Tr.entry(tc, "terminate");
        }

        AsyncLibrary.shutdown();
        groups.clear();

        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) {
            Tr.exit(tc, "terminate");
        }
    }

    /*
     * @see com.ibm.ws.tcpchannel.internal.TCPChannel#createEndPoint()
     */
    public TCPPort createEndPoint() throws ChannelException {
        TCPPort tcpPort = super.createEndPoint();
        try {
            this.asyncChannelGroup = findOrCreateACG();
            if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
                Tr.debug(tc, "AioTCPChannel created completion port for inbound connections on host " + getConfig().getHostname() + ", port " + getConfig().getPort()
                             + ", completionPort = " + this.asyncChannelGroup.getCompletionPort());
            }

        } catch (AsyncException ae) {
            ChannelException ce = new ChannelException("Error creating async channel group ");
            ce.initCause(ae);
            throw ce;
        }
        return tcpPort;
    }

    private AsyncChannelGroup findOrCreateACG() throws AsyncException {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) {
            Tr.entry(tc, "findOrCreateACG");
        }

        String groupName = getConfig().getWorkGroupName();
        AsyncChannelGroup group = groups.get(groupName);
        if (group == null) {
            if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
                Tr.debug(tc, "create new AsyncChannelGroup: " + groupName);
            }
            group = new AsyncChannelGroup(groupName);
            groups.put(groupName, group);
        }

        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) {
            Tr.exit(tc, "findOrCreateACG");
        }
        return group;
    }

    /*
     * @see
     * com.ibm.ws.tcpchannel.internal.TCPChannel#createReadInterface(com.ibm.ws
     * .tcpchannel.internal.TCPConnLink)
     */
    public TCPReadRequestContextImpl createReadInterface(TCPConnLink connLink) {
        return new AioTCPReadRequestContextImpl(connLink);
    }

    /*
     * @see
     * com.ibm.ws.tcpchannel.internal.TCPChannel#createWriteInterface(com.ibm.
     * ws.tcpchannel.internal.TCPConnLink)
     */
    public TCPWriteRequestContextImpl createWriteInterface(TCPConnLink connLink) {
        return new AioTCPWriteRequestContextImpl(connLink);
    }

    /**
     * @return AioReadCompletionListener
     */
    public static AioReadCompletionListener getAioReadCompletionListener() {
        return aioReadCompletionListener;
    }

    /**
     * @return AioWriteCompletionListener
     */
    public static AioWriteCompletionListener getAioWriteCompletionListener() {
        return aioWriteCompletionListener;
    }

    /**
     * Check whether the native AIO library reported that it supports the
     * use of JIT buffers.
     * 
     * @return boolean
     */
    public static boolean getJitSupportedByNative() {
        return jitSupportedByNative;
    }

    /*
     * @see
     * com.ibm.ws.tcpchannel.internal.TCPChannel#createOutboundSocketIOChannel()
     */
    public SocketIOChannel createOutboundSocketIOChannel() throws IOException {
        AsyncSocketChannel achannel = AsyncSocketChannel.open(getAsyncChannelGroup());
        Socket socket = achannel.socket();
        return AioSocketIOChannel.createIOChannel(socket, achannel, this);
    }

    /*
     * @see
     * com.ibm.ws.tcpchannel.internal.TCPChannel#createInboundSocketIOChannel(
     * java.nio.channels.SocketChannel)
     */
    public SocketIOChannel createInboundSocketIOChannel(SocketChannel sc) throws IOException {
        AsyncSocketChannel asc = new AsyncSocketChannel(sc, getAsyncChannelGroup());
        return AioSocketIOChannel.createIOChannel(sc.socket(), asc, this);
    }

    /**
     * Access the AIO group that this channel belongs to.
     * 
     * @return AsyncChannelGroup
     */
    protected AsyncChannelGroup getAsyncChannelGroup() {
        return this.asyncChannelGroup;
    }

    /*
     * @see com.ibm.ws.tcpchannel.internal.TCPChannel#dumpStatistics()
     */
    protected void dumpStatistics() {
        super.dumpStatistics();
        this.asyncChannelGroup.dumpStatistics();
    }
}
