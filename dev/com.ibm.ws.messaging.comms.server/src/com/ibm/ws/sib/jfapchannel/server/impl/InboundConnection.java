/*
 * @start_prolog@
 * Version: @(#) 1.42 SIB/ws/code/sib.jfapchannel.server.impl/src/com/ibm/ws/sib/jfapchannel/impl/InboundConnection.java, SIB.comms, WASX.SIB, aa1225.01 10/02/15 03:11:45 [7/2/12 05:59:07]
 * ============================================================================
 * IBM Confidential OCO Source Materials
 *
 * 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70 Copyright IBM Corp. 2003, 2010
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
 * Creation        030521 prestona Original
 * F174772         030826 prestona Make JFAP Channel support close.
 * F175658         030902 prestona Add support for heartbeating.
 * F176003         030908 prestona Misc. JFAP Channel reliability fixes.
 * F177053         030917 prestona Rebase JFAP Channel on pre-M4 CF + TCP
 * D179183         031008 prestona Various deadlocks in comms
 * D181601         031031 prestona Improve quality of JFAP Channel RAS
 * F188491         030128 prestona Migrate to M6 CF + TCP Channel
 * D181493         040206 prestona createQueueConnection is not thread safe.
 * D196678.10.1    040525 prestona Insufficient chain data passed to TRM
 * D221433         040811 prestona Add handshakeFailed
 * D199145         040812 prestona Fix Javadoc
 * D224570         040818 prestona JFap trace needs improving
 * D226223         040823 prestona Uses new messages
 * D230746         040911 mayur    Get connection context
 * D320083         051103 mattheg  Improve error messages
 * D321398         051107 mattheg  Implement toString() method
 * D284617         051114 prestona OutOfMemoryException has to wait for heartbeat to cleanup
 * D363463         060421 prestona ME-ME heartbeat timeout
 * SIB0048b.com.4  060905 mattheg  Re-packaging into client / server
 * SIB0048b.com.5  060913 mattheg  JFap channel for Portly client rework
 * 494863          080213 mleming  Prevent NPE if TCP/IP connection goes while establishing connection
 * 498662          080218 sibcopyr Automatic update of trace guards
 * 516687          080509 vaughton Add SIBJFapSummary trace group
 * 466186          080527 mleming  Provide more information for filtering on JFAPSUMMARY
 * PK91199         090714 pbroad   Correct invalidation logic to ensure physical close occurs
 * 635295          100215 pbroad   Added connectionClosedByPeer method
 * ============================================================================
 */
package com.ibm.ws.sib.jfapchannel.server.impl;

import com.ibm.ejs.ras.TraceNLS;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.websphere.sib.exception.SIErrorException;
import com.ibm.websphere.sib.exception.SIResourceException;
import com.ibm.ws.sib.jfapchannel.AcceptListener;
import com.ibm.ws.sib.jfapchannel.Conversation;
import com.ibm.ws.sib.jfapchannel.ConversationMetaData;
import com.ibm.ws.sib.jfapchannel.ConversationReceiveListener;
import com.ibm.ws.sib.jfapchannel.JFapChannelConstants;
import com.ibm.ws.sib.jfapchannel.JFapConnectionBrokenException;
import com.ibm.ws.sib.jfapchannel.framework.FrameworkException;
import com.ibm.ws.sib.jfapchannel.framework.NetworkConnection;
import com.ibm.ws.sib.jfapchannel.framework.NetworkConnectionContext;
import com.ibm.ws.sib.jfapchannel.impl.Connection;
import com.ibm.ws.sib.jfapchannel.impl.ConversationImpl;
import com.ibm.ws.sib.jfapchannel.impl.JFapUtils;
import com.ibm.ws.sib.utils.ras.SibTr;

/**
 * A object which represents the server side of a connection (socket).
 */
public class InboundConnection extends Connection {
    private static final TraceComponent tc = SibTr.register(InboundConnection.class,
                                                            JFapChannelConstants.MSG_GROUP,
                                                            JFapChannelConstants.MSG_BUNDLE);

    //@start_class_string_prolog@
    public static final String $sccsid = "@(#) 1.42 SIB/ws/code/sib.jfapchannel.server.impl/src/com/ibm/ws/sib/jfapchannel/impl/InboundConnection.java, SIB.comms, WASX.SIB, aa1225.01 10/02/15 03:11:45 [7/2/12 05:59:07]";
    //@end_class_string_prolog@

    static {
        if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
            SibTr.debug(tc, "Source Info: " + $sccsid);
    }

    private AcceptListener acceptListener = null;

    /** Eye catcher for use in debugSummaryMessage. */
    private final String eyeCatcher;

    /**
     * Creates a new server connection.
     * 
     * @param channel
     * @param al
     * 
     * @throws FrameworkException if anything goes wrong while creating a new instance of InboundConnection.
     */
    public InboundConnection(NetworkConnectionContext channel,
                             NetworkConnection vc,
                             AcceptListener al,
                             int heartbeatInterval,
                             int heartbeatTimeout) throws FrameworkException {
        super(channel, vc, heartbeatInterval, heartbeatTimeout);

        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
            SibTr.entry(this, tc, "<init>",
                                           new Object[] { channel, vc, al, "" + heartbeatInterval, "" + heartbeatTimeout });

        acceptListener = al;

        // Make description "IN localhost:port<-remotehost:port"
        // using dotted decimal host addresses.
        description = "IN " + tcpCtx.getLocalAddress().getHostAddress() + ":" + tcpCtx.getLocalPort() +
                      "<-" + tcpCtx.getRemoteAddress().getHostAddress() + ":" + tcpCtx.getRemotePort();

        //Generate eye catcher for use with debugSummaryMessage.
        //This is always of the format: client ip address:client port:server ip address:server port
        eyeCatcher = tcpCtx.getRemoteAddress().getHostAddress() + ":" + tcpCtx.getRemotePort() + ":" + tcpCtx.getLocalAddress().getHostAddress() + ":" + tcpCtx.getLocalPort();

        remoteHostAddress = tcpCtx.getRemoteAddress().getHostAddress();
        chainName = getMetaData().getChainName();

        //@stoptracescan@
        if (TraceComponent.isAnyTracingEnabled())
            JFapUtils.debugSummaryMessage(tc, this, null, "New inbound connection established");
        //@starttracescan@

        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
            SibTr.exit(this, tc, "<init>");
    }

    /**
     * Starts a new conversation.
     * 
     * @param c
     * @return ConversationImpl
     */
    @Override
    protected ConversationImpl startNewConversation(ConversationImpl c) throws SIResourceException {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
            SibTr.entry(this, tc, "startNewConversation", c);
        ConversationImpl retValue = startNewConversationGeneric(c, false, acceptListener);
        first = false; // D221433
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
            SibTr.exit(this, tc, "startNewConversation", retValue);
        return retValue;
    }

    /**
     * Notification (called by a conversation) when it is asked to close.
     */
    @Override
    public void closeNotification(Conversation c) {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
            SibTr.entry(this, tc, "closeNotification", c);

        // If we still have an entry in the conversation table,
        // remove it. We don't need it any more.
        if (conversationTable.contains(c.getId()))
            conversationTable.remove(c.getId());

        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
            SibTr.exit(this, tc, "closeNotification");
    }

    /** @see Connection#invalidate(boolean, Throwable) */
    @Override
    public void invalidateImpl(boolean notifyPeer, Throwable throwable) {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
            SibTr.entry(this, tc, "invalidateImpl", new Object[] { "" + notifyPeer, throwable });
        if ((throwable != null) && TraceComponent.isAnyTracingEnabled() && tc.isEventEnabled())
            SibTr.exception(tc, throwable);

        JFapConnectionBrokenException exception = new JFapConnectionBrokenException(
                        TraceNLS.getFormattedMessage(JFapChannelConstants.MSG_BUNDLE,
                                                     "INBOUNDCONV_INVALIDATE_SICJ0051",
                                                     new Object[] { remoteHostAddress, chainName },
                                                     null)
                        );
        if (throwable != null)
            exception.initCause(throwable);
        wakeupAllConversationsWithException(exception, true);

        // Call the appropriate physical close method.
        physicalCloseFromInvalidateImpl(notifyPeer);

        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
            SibTr.exit(this, tc, "invalidateImpl");
    }

    /**
     * No additional work is required on an inbound connection when it is closed by the peer.
     */
    @Override
    protected void connectionClosedByPeer() {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) {
            SibTr.entry(this, tc, "connectionClosedByPeer");
            SibTr.exit(this, tc, "connectionClosedByPeer");
        }
    }

    /**
     * Trivial implementation of handshake complete. We do not care about this event for
     * inbound connections.
     */
    @Override
    protected void handshakeComplete() {
        if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
            SibTr.debug(this, tc, "handshakeComplete invoked on inbound connection");
    }

    /** @see com.ibm.ws.sib.jfapchannel.impl.Connection#handshakeFailed() */
    @Override
    protected void handshakeFailed() {
        if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
            SibTr.debug(this, tc, "handshakeFailed invoked on inbound connection");
    }

    @Override
    protected Conversation cloneConversation(ConversationReceiveListener receiveListener)
                    throws SIResourceException {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
            SibTr.entry(this, tc, "cloneConnection", receiveListener);
        if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
            SibTr.debug(this, tc, "attempt to clone inbound conversation!");
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
            SibTr.exit(this, tc, "cloneConnection");
        throw new SIErrorException(TraceNLS.getFormattedMessage(JFapChannelConstants.MSG_BUNDLE, "INBOUNDCONV_INTERNAL_SICJ0052", null, "INBOUNDCONV_INTERNAL_SICJ0052"));
    }

    /** @see com.ibm.ws.sib.jfapchannel.impl.Connection#getMetaData() */
    @Override
    protected ConversationMetaData getMetaData() {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
            SibTr.entry(this, tc, "getMetaData");
        ConversationMetaData retValue = connChannel.getMetaData();
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
            SibTr.exit(this, tc, "getMetaData", retValue);
        return retValue;
    }

    /**
     * @return Returns true to indicate this is an inbound connection.
     */
    @Override
    protected boolean isInbound() {
        return true;
    }

    /**
     * @return Returns useful information about this connection.
     */
    @Override
    public String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append("InboundConnection@").append(Integer.toHexString(System.identityHashCode(this)));
        buf.append(": {");
        buf.append("Remote Address: ");
        buf.append(remoteHostAddress);
        buf.append(", Chain: ");
        buf.append(chainName);
        buf.append(", Heartbeat Timeout: ");
        buf.append(getHeartbeatTimeoutForToString());
        buf.append(", Heartbeat Interval: ");
        buf.append(getHeartbeatIntervalForToString());
        buf.append("}\nEvents follow:\n");
        buf.append(getDiagnostics(false));

        return buf.toString();
    }

    /**
     * @see Connection#getEyeCatcher()
     */
    @Override
    public String getEyeCatcher() {
        return eyeCatcher;
    }
}
