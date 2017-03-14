//-------------------------------------------------------------------------------
//IBM Confidential OCO Source Material
//5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N01, 5733-W61 (C) COPYRIGHT International Business Machines Corp. 2003, 2005, 2007
//The source code for this program is not published or otherwise divested
//of its trade secrets, irrespective of what has been deposited with the
//U.S. Copyright Office.
//
//Change History:
//
//Change ID     Author    Abstract
//---------     --------  -------------------------------------------------------
//d306341		mjohn256  Add RAS logging support to UDP Channel.
//-------------------------------------------------------------------------------
package com.ibm.ws.udpchannel.internal;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.wsspi.bytebuffer.WsByteBuffer;
import com.ibm.wsspi.channelfw.VirtualConnection;
import com.ibm.wsspi.udpchannel.UDPWriteCompletedCallback;
import com.ibm.wsspi.udpchannel.UDPWriteRequestContext;

/**
 * UDP write specific context for interaction with users of the UDP channel.
 * 
 * @author mjohnson
 */
public class UDPWriteRequestContextImpl extends UDPRequestContextImpl implements UDPWriteRequestContext {

    private static final TraceComponent tc = Tr.register(UDPWriteRequestContextImpl.class, UDPMessages.TR_GROUP, UDPMessages.TR_MSGS);

    private UDPWriteCompletedCallback writeCallback = null;
    private WsByteBuffer writeBuffer = null;
    private SocketAddress targetAddress = null;
    private boolean bIsForceQueue = false;

    /**
     * Constructor.
     * 
     * @param udpContext
     * @param wqm
     */
    public UDPWriteRequestContextImpl(UDPConnLink udpContext, WorkQueueManager wqm) {
        super(udpContext, wqm);
    }

    /*
     * @see
     * com.ibm.websphere.udp.channel.UDPWriteRequestContext#setBuffer(com.ibm.
     * wsspi.bytebuffer.WsByteBuffer)
     */
    public void setBuffer(WsByteBuffer buf) {
        this.writeBuffer = buf;
    }

    protected WsByteBuffer getBuffer() {
        return this.writeBuffer;
    }

    /*
     * @see com.ibm.websphere.udp.channel.UDPWriteRequestContext#write(java.net.
     * SocketAddress, com.ibm.websphere.udp.channel.UDPWriteCompletedCallback,
     * boolean)
     */
    public VirtualConnection write(SocketAddress address, UDPWriteCompletedCallback callback, boolean forceQueue) {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) {
            Tr.entry(tc, "write()");
        }

        if (((InetSocketAddress) address).getAddress() == null) {
            if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
                Tr.debug(tc, "Adddress is unresolvable [" + address + "]");
            }

            CumulativeLogger.logLookupFailure(((InetSocketAddress) address).getHostName());

            if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) {
                Tr.exit(tc, "write when address is unresolvable.");
            }
            return getConnLink().getVirtualConnection();
        }

        setWriteCallback(callback);
        this.targetAddress = address;
        this.bIsForceQueue = forceQueue;

        if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
            Tr.debug(tc, "write called for buffer " + getBuffer() + " to address " + address);
        }

        VirtualConnection conn = getWorkQueueManager().processWork(this);

        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) {
            Tr.exit(tc, "write: " + conn);
        }
        return conn;
    }

    protected SocketAddress getAddress() {
        return this.targetAddress;
    }

    protected void setWriteCallback(UDPWriteCompletedCallback writeCallback) {
        this.writeCallback = writeCallback;
    }

    protected UDPWriteCompletedCallback getWriteCallback() {
        return this.writeCallback;
    }

    /**
     * @return boolean
     */
    public boolean isForceQueue() {
        return this.bIsForceQueue;
    }

    /*
     * @see com.ibm.ws.udp.channel.internal.UDPRequestContextImpl#isRead()
     */
    @Override
    public boolean isRead() {
        return false;
    }
}