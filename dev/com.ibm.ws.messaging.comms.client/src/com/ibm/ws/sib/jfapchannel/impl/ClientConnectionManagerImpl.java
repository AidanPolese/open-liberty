/*
 * @start_prolog@
 * Version: @(#) 1.47 SIB/ws/code/sib.jfapchannel.client.common.impl/src/com/ibm/ws/sib/jfapchannel/impl/ClientConnectionManagerImpl.java, SIB.comms, WASX.SIB, uu1215.01 10/03/25 07:08:04 [4/12/12 22:14:13]
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
 * Creation        030424 prestona Original
 * F166959         030521 prestona Rebase on non-prototype CF + TCP Channel
 * F167363         030523 prestona Rebase on LIBD_1891_2255 CF + TCP Channel
 * D168105         030626 prestona Enable daemon thread usage in TCP Channel
 * F171173         030707 prestona Add capacity reporting interfaces.
 * F173069         030730 prestona Update CF + TCP Channel to M3 CVS head.
 * F174602         030819 prestona Switch to using SICommsException.
 * F175658         030902 prestona Add support for heartbeating.
 * F177053         030917 prestona Rebase JFAP Channel on pre-M4 CF + TCP
 * F178022         030929 prestona Discover CF + TCP Channel as services
 * D178719         031008 prestona Launchclient hanging at end of running RTS
 * D181601         031031 prestona Improve quality of JFAP Channel RAS
 * F182479         031127 prestona New ConnectionProperties varient required.
 * F184828         031204 prestona Update CF + TCP prereqs to MS 5.1 level
 * d184626         040109 mattheg  Update location of buffer pool manager
 * F189000         030130 prestona Expose WLM endpoints through CF
 * F189351         040203 prestona CF admin support
 * D189676         040205 prestona CF NullPointer during startup
 * D192817         040302 prestona com.ibm.wsspi.channel.WSChannelFactory
 * F196678.10      040426 prestona JS Client Administration
 * D223632         040812 prestona Defend against errors during initialisiation
 * D223265         040906 prestona Incorrect conversation type being set
 * D229522         040906 prestona Remove programatic chain creation
 * F244595         041129 prestona z/OS: TCP Proxy Bridge Support
 * D311987         051101 prestona Delay "no SSL properties file" error until SSL chain usage
 * D321398         051107 mattheg  Expose active conversations in JVM
 * D330649         051209 prestona Supply an outbound protocol
 * D377648         060714 mattheg  Move BufferPoolManagerReference into sib.utils
 * D341600         060810 prestona Fix Java 5 compiler warnings
 * SIB0048b.com.5  060913 mattheg  JFap channel for Portly client rework
 * D406076         061116 prestona Add unit tests for sib.jfapchannel.client.common.impl
 * SIB0100.wmq.3   070813 mleming  Allow WMQRA to use TCP Proxy Bridge
 * 464663          070905 sibcopyr Automatic update of trace guards
 * PM07974         100223 timmccor Add getActiveOutboundConversationsForFfdc()
 * F002074         091022 mleming  MEP support FIS
 * 95897           041613 Chetan   Comms Outbound Chain revamp
 * ============================================================================ 
 */
package com.ibm.ws.sib.jfapchannel.impl;

import java.net.InetSocketAddress;
import java.util.List;

import com.ibm.ejs.ras.TraceNLS;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.websphere.sib.exception.SIErrorException;
import com.ibm.websphere.sib.exception.SIResourceException;
import com.ibm.ws.sib.jfapchannel.ClientConnectionManager;
import com.ibm.ws.sib.jfapchannel.Conversation;
import com.ibm.ws.sib.jfapchannel.ConversationReceiveListener;
import com.ibm.ws.sib.jfapchannel.ConversationUsageType;
import com.ibm.ws.sib.jfapchannel.JFapChannelConstants;
import com.ibm.ws.sib.jfapchannel.framework.Framework;
import com.ibm.ws.sib.jfapchannel.impl.octracker.OutboundConnectionTracker;
import com.ibm.ws.sib.utils.ras.SibTr;

/**
 * Implementation of the client connection manager. Provides useful
 * client functions - like being able to establish conversations with
 * other MEs
 * 
 * @author prestona
 */
public class ClientConnectionManagerImpl extends ClientConnectionManager
{
    /** Trace */
    private static final TraceComponent tc = SibTr.register(ClientConnectionManagerImpl.class,
                                                            JFapChannelConstants.MSG_GROUP,
                                                            JFapChannelConstants.MSG_BUNDLE);

    /** NLS */
    private static final TraceNLS nls = TraceNLS.getTraceNLS(JFapChannelConstants.MSG_BUNDLE); // F178022

    /** Log class info on load */
    static
    {
        if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
            SibTr.debug(tc,
                        "@(#) SIB/ws/code/sib.jfapchannel.client.common.impl/src/com/ibm/ws/sib/jfapchannel/impl/ClientConnectionManagerImpl.java, SIB.comms, WASX.SIB, uu1215.01 1.47");
    }

    // Reference to helper object which keeps track of in use outbound connections.
    private static OutboundConnectionTracker tracker = null;

    // Set to true if we try to initalise but are broken.
    private static boolean initialisationFailed = false;

    public ClientConnectionManagerImpl()
    {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
            SibTr.entry(this, tc, "<init>");
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
            SibTr.exit(this, tc, "<init>");
    }

    /**
     * Implementation of the connect method provided by our abstract parent. Attempts to establish a
     * conversation to the specified remote host using the appropriate chain. This may involve
     * creating a new connection or reusing an existing one. The harder part is doing this in such a
     * way as to avoid blocking all calls while processing a single new outbound connection attempt.
     * 
     * @param remoteHost
     * @param arl
     * @return Connection
     */
    @Override
    public Conversation connect(InetSocketAddress remoteHost,
                                ConversationReceiveListener arl,
                                String chainName)
                    throws SIResourceException
    {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
            SibTr.entry(this, tc, "connect", new Object[] { remoteHost, arl, chainName });

        if (initialisationFailed)
        {
            String nlsMsg = nls.getFormattedMessage("EXCP_CONN_FAIL_NO_CF_SICJ0007", null, null);
            if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
                SibTr.debug(this, tc, "connection failed because comms failed to initialise");
            throw new SIResourceException(nlsMsg);
        }

        Conversation conversation = tracker.connect(remoteHost, arl, chainName, Conversation.CLIENT);

        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
            SibTr.exit(this, tc, "connect", conversation);
        return conversation;
    }

    /**
     * Starts a Conversation with the host using the information specified in the endpoint.
     * 
     * @param endpoint The endpoint to connect to.
     * @param conversationReceiveListener The receive listener to use.
     * 
     * @return Returns a Conversation to the host.
     */
    @Override
    public Conversation connect(Object endpoint,
                                ConversationReceiveListener conversationReceiveListener)
                    throws SIResourceException
    {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
            SibTr.entry(this, tc, "connect", new Object[] { endpoint, conversationReceiveListener });

        if (initialisationFailed)
        {
            String nlsMsg = nls.getFormattedMessage("EXCP_CONN_FAIL_NO_CF_SICJ0007", null, null);
            if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
                SibTr.debug(this, tc, "connection failed because comms failed to initialise");
            throw new SIResourceException(nlsMsg);
        }

        Conversation conversation = tracker.connect(endpoint, conversationReceiveListener, Conversation.CLIENT);

        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
            SibTr.exit(this, tc, "connect", conversation);
        return conversation;
    }

    /**
     * Starts a Conversation on z/OS across the Cross-Memory channel.
     * 
     * @param receiveListener
     * @param type the way the conversation will be used.
     * 
     * @return Returns a Conversation.
     */
    @Override
    public Conversation connect(final ConversationReceiveListener receiveListener, final ConversationUsageType type) throws SIResourceException
    {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
            SibTr.entry(this, tc, "connect", new Object[] { receiveListener, type });

        if (initialisationFailed)
        {
            String nlsMsg = nls.getFormattedMessage("EXCP_CONN_FAIL_NO_CF_SICJ0007", null, null);
            if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
                SibTr.debug(this, tc, "connection failed because comms failed to initialise");
            throw new SIResourceException(nlsMsg);
        }

        Conversation conversation = tracker.connect(receiveListener, Conversation.CLIENT, type);
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
            SibTr.exit(this, tc, "connect", conversation);
        return conversation;
    }

    /**
     * Initialises the Client Connection Manager.
     * 
     * @see com.ibm.ws.sib.jfapchannel.ClientConnectionManager#initialise()
     */
    public static void initialise() throws SIErrorException
    {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
            SibTr.entry(tc, "initialise");

        initialisationFailed = true;

        Framework framework = Framework.getInstance();

        if (framework != null)
        {
            tracker = new OutboundConnectionTracker(framework);
            initialisationFailed = false;
        }
        else
        {
            if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
                SibTr.debug(tc, "initialisation failed");
        }

        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
            SibTr.exit(tc, "initialise");
    }

    /**
     * @return Returns a List of the active outbound conversations.
     */
    @Override
    public List getActiveOutboundConversations()
    {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
            SibTr.entry(this, tc, "getActiveOutboundConversations");

        List convs = null;
        if (tracker != null)
        {
            convs = tracker.getAllOutboundConversations();
        }

        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
            SibTr.exit(this, tc, "getActiveOutboundConversations", convs);
        return convs;
    }

    /**
     * @return Returns a dirty List of the active outbound conversations without obtaining locks, or null if we weren't able to
     *         generate the list
     */
    @Override
    public List getActiveOutboundConversationsForFfdc()
    {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
            SibTr.entry(this, tc, "getActiveOutboundConversationsForFfdc");

        List convs = null;
        if (tracker != null)
        {
            convs = tracker.getAllOutboundConversationsForFfdc();
        }

        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
            SibTr.exit(this, tc, "getActiveOutboundConversationsForFfdc", convs);
        return convs;
    }

    /**
     * For unit test only!
     * 
     * @param tracker
     */
    protected static void setOutboundConnectionTracker(OutboundConnectionTracker tracker)
    {
        ClientConnectionManagerImpl.tracker = tracker;
    }

    @Override
    public OutboundConnectionTracker getOutboundConnectionTracker() {
        return tracker;
    }
}
