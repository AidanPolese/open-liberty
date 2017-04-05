/*
 * @start_prolog@
 * Version: @(#) 1.2 SIB/ws/code/sib.jfapchannel.client.rich.impl/src/com/ibm/ws/sib/jfapchannel/framework/impl/CFWNetworkConnectionContext.java, SIB.comms, WASX.SIB, uu1215.01 08/02/13 08:19:49 [4/12/12 22:14:19]
 * ============================================================================
 * IBM Confidential OCO Source Materials
 * 
 * 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70 Copyright IBM Corp. 2003, 2008 
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
 * SIB0048b.com.5  060913 mattheg  JFap channel for Portly client rework
 * 494863          080213 mleming  Prevent NPE if TCP/IP connection goes while establishing connection
 * ============================================================================
 */
package com.ibm.ws.sib.jfapchannel.richclient.framework.impl;

import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.sib.jfapchannel.ConversationMetaData;
import com.ibm.ws.sib.jfapchannel.JFapChannelConstants;
import com.ibm.ws.sib.jfapchannel.MetaDataProvider;
import com.ibm.ws.sib.jfapchannel.framework.IOConnectionContext;
import com.ibm.ws.sib.jfapchannel.framework.NetworkConnection;
import com.ibm.ws.sib.jfapchannel.framework.NetworkConnectionContext;
import com.ibm.ws.sib.utils.ras.SibTr;
import com.ibm.wsspi.channelfw.ConnectionLink;
import com.ibm.wsspi.kernel.service.utils.FrameworkState;
import com.ibm.wsspi.tcpchannel.TCPConnectionContext;

/**
 * An implementation of com.ibm.ws.sib.jfapchannel.framework.NetworkConnectionContext. It
 * basically wrappers the com.ibm.wsspi.channel.ConnectionLink code in the
 * underlying TCP channel.
 * 
 * @see com.ibm.ws.sib.jfapchannel.framework.NetworkConnectionContext
 * @see com.ibm.wsspi.channel.ConnectionLink
 * 
 * @author Gareth Matthews
 */
public class CFWNetworkConnectionContext implements NetworkConnectionContext
{
    /** Trace */
    private static final TraceComponent tc = SibTr.register(CFWNetworkConnectionContext.class,
                                                            JFapChannelConstants.MSG_GROUP,
                                                            JFapChannelConstants.MSG_BUNDLE);

    /** Log class info on load */
    static
    {
        if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
            SibTr.debug(tc,
                        "@(#) SIB/ws/code/sib.jfapchannel.client.rich.impl/src/com/ibm/ws/sib/jfapchannel/framework/impl/CFWNetworkConnectionContext.java, SIB.comms, WASX.SIB, uu1215.01 1.2");
    }

    /** The underlying connection link */
    private ConnectionLink connLink = null;

    /** The connection reference */
    private NetworkConnection conn = null;

    /**
     * @param connLink
     */
    public CFWNetworkConnectionContext(NetworkConnection conn, ConnectionLink connLink)
    {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
            SibTr.entry(this, tc, "<init>", new Object[] { conn, connLink });
        this.conn = conn;
        this.connLink = connLink;
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
            SibTr.exit(tc, "<init>");
    }

    /**
     * @see com.ibm.ws.sib.jfapchannel.framework.NetworkConnectionContext#close(com.ibm.ws.sib.jfapchannel.framework.NetworkConnection, java.lang.Throwable)
     */
    @Override
    public void close(NetworkConnection networkConnection, Throwable throwable)
    {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
            SibTr.entry(this, tc, "close", new Object[] { networkConnection, throwable });

        // If the server is stopping, all connections will be closed/flushed by channel 
        // framework, so don't do it here. 
        if (FrameworkState.isStopping()) {
            if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
                SibTr.exit(this, tc, "close");
            return;
        }
        Exception exception = null;
        if (throwable instanceof Exception)
        {
            exception = (Exception) throwable;
        }
        else
        {
            exception = new Exception(throwable);
        }
        connLink.close(((CFWNetworkConnection) networkConnection).getVirtualConnection(),
                       exception);

        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
            SibTr.exit(this, tc, "close");
    }

    /**
     * @see com.ibm.ws.sib.jfapchannel.framework.NetworkConnectionContext#getIOContextForDevice()
     */
    @Override
    public IOConnectionContext getIOContextForDevice()
    {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
            SibTr.entry(this, tc, "getIOContextForDevice");

        final IOConnectionContext ioConnCtx;
        final ConnectionLink deviceLink = connLink.getDeviceLink();

        if (deviceLink == null)
        {
            if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
                SibTr.debug(this, tc, "Got a null device link.");
            ioConnCtx = null;
        }
        else
        {
            final TCPConnectionContext tcpCtx = (TCPConnectionContext) deviceLink.getChannelAccessor();
            ioConnCtx = new CFWIOConnectionContext(conn, tcpCtx);
        }

        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
            SibTr.exit(this, tc, "getIOContextForDevice", ioConnCtx);
        return ioConnCtx;
    }

    /**
     * @see com.ibm.ws.sib.jfapchannel.framework.NetworkConnectionContext#getMetaData()
     */
    @Override
    public ConversationMetaData getMetaData()
    {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
            SibTr.entry(this, tc, "getMetaData");
        ConversationMetaData metaData = ((MetaDataProvider) connLink).getMetaData();
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
            SibTr.exit(this, tc, "getMetaData", metaData);
        return metaData;
    }
}
