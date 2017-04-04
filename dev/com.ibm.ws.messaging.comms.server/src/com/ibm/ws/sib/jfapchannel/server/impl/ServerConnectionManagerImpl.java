/*
 * @start_prolog@
 * Version: @(#) 1.53 SIB/ws/code/sib.jfapchannel.server.impl/src/com/ibm/ws/sib/jfapchannel/impl/ServerConnectionManagerImpl.java, SIB.comms, WASX.SIB, aa1225.01 09/04/22 11:25:04 [7/2/12 05:59:07]
 * ============================================================================
 * IBM Confidential OCO Source Materials
 *
 * 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70 Copyright IBM Corp. 2003, 2009
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
 * D167899         030623 prestona Work around listen binding to 127.0.0.1
 * D168314         030623 prestona Work around listen binding to hostname...
 * D168105         030626 prestona Enable daemon thread usage in TCP Channel
 * F171173         030707 prestona Add capacity reporting interfaces.
 * F173069         030730 prestona Update CF + TCP Channel to M3 CVS head
 * F174602         030819 prestona Switch to using SICommsException
 * F175658         030902 prestona Add support for heartbeating.
 * F177053         030917 prestona Rebase JFAP Channel on pre-M4 CF + TCP
 * F178022         030929 prestona Discover CF + TCP Channel as services
 * D178719         031008 prestona Launchclient hanging at end of running RTS
 * D181601         031031 prestona Improve quality of JFAP Channel RAS
 * F182479         031127 prestona New ConnectionProperties varient required.
 * F184828         031204 prestona Update CF + TCP prereqs to MS 5.1 level
 * d184626         040109 mattheg  Update location of buffer pool manager
 * F188491         030128 prestona Migrate to M6 CF + TCP Channel
 * F189000         030130 prestona Expose WLM endpoints through CF
 * F189351         040203 prestona CF admin support
 * D189676         040205 prestona CF NullPointer during startup
 * F191798         040227 prestona Use proper chain names
 * D192817         040302 prestona com.ibm.wsspi.channel.WSChannelFactory
 * D194678         040319 mattheg
 * F196678.10      040426 prestona JS Client Administration
 * D197042         040811 prestona FFDC entries
 * D199145         040812 prestona Fix Javadoc
 * D226223         040823 prestona Uses new messages
 * D223265         040906 prestona Incorrect conversation type being set
 * D229522         040906 prestona Remove programatic chain creation
 * D330649         051209 prestona Supply an outbound protocol
 * D377648         060714 mattheg  Move BufferPoolManagerReference into sib.utils / Use JFapByteBuffer
 * D341600         060810 prestona Fix Java 5 compiler warnings
 * SIB0048b.com.5  060913 mattheg  JFap channel for Portly client rework
 * SIB0100.wmq.3   070816 mleming  Allow WMQRA to make use of TCP Proxy Bridge
 * 464663          070805 sibcopyr Automatic update of trace guards
 * 462062          080520 mleming  Improve diagnostics
 * 581917          090415 mleming  Start up chains when bus added dynamically
 * 581917          090422 djvines  Refactor the preceding change
 * ============================================================================
 */
package com.ibm.ws.sib.jfapchannel.server.impl;

import java.net.InetSocketAddress;
import java.util.Hashtable;
import java.util.List;

import com.ibm.ejs.ras.TraceComponent;
import com.ibm.ejs.ras.TraceNLS;
import com.ibm.websphere.channelfw.CFEndPoint;
import com.ibm.websphere.sib.exception.SIErrorException;
import com.ibm.websphere.sib.exception.SIResourceException;
import com.ibm.ws.sib.jfapchannel.Conversation;
import com.ibm.ws.sib.jfapchannel.ConversationReceiveListener;
import com.ibm.ws.sib.jfapchannel.JFapChannelConstants;
import com.ibm.ws.sib.jfapchannel.framework.Framework;
import com.ibm.ws.sib.jfapchannel.impl.octracker.OutboundConnectionTracker;
import com.ibm.ws.sib.jfapchannel.server.AcceptListenerFactory;
import com.ibm.ws.sib.jfapchannel.server.ServerConnectionManager;
import com.ibm.ws.sib.utils.ras.SibTr;

/**
 * Implementation of the ServerConnectionManager.
 * 
 * @author prestona
 */
public class ServerConnectionManagerImpl extends ServerConnectionManager {
    private static final com.ibm.websphere.ras.TraceComponent tc = SibTr.register(ServerConnectionManagerImpl.class,
                                                                                  JFapChannelConstants.MSG_GROUP,
                                                                                  JFapChannelConstants.MSG_BUNDLE);

    /* ************************************************************************** */
    /**
     * A ServerConfigChangeListener is registered with admin so that we can give
     * the channel framework a prod to reconcile inbound chains if a bus is added
     * or deleted
     */
    /* ************************************************************************** */
    private static final class ServerConfigChangeListener {
        /* -------------------------------------------------------------------------- */
        /*
         * configChanged method
         * /* --------------------------------------------------------------------------
         */
        /**
         * Notified that the config has changed. Since we carefully register for just
         * those changes we care about, we can assume that if we are told about the
         * change we should reconcile the inbound chains
         * 
         * @see com.ibm.ws.management.service.ConfigChangeListener#configChanged(com.ibm.websphere.management.repository.ConfigRepositoryEvent)
         * @param event
         */
        public void configChanged() {
        //Venu Liberty COMMS TODO
        }
    }

    /** NLS */
    private static final TraceNLS nls = TraceNLS.getTraceNLS(JFapChannelConstants.MSG_BUNDLE);

    /** Log class info on load */
    static {
        if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
            SibTr.debug(tc,
                        "@(#) SIB/ws/code/sib.jfapchannel.server.impl/src/com/ibm/ws/sib/jfapchannel/impl/ServerConnectionManagerImpl.java, SIB.comms, WASX.SIB, aa1225.01 1.53");
    }

    /**
     * Register with admin as a regular expression listener for dynamic config updates
     * - when the update arrives we reconcile the inbound chains
     */
    private static final String busUriPattern = ".*/buses/.*/sib-bus.xml";
    private static final ServerConfigChangeListener busListener = new ServerConfigChangeListener();

    /** Listener port map */
    private final Hashtable<Integer, ListenerPortImpl> portToListenerMap;

    /** The outbound connection tracker */
    private static OutboundConnectionTracker connectionTracker = null;

    /** The possible states we could be in */
    private static enum State {
        UNINITIALISED, INITIALISED, INITIALISATION_FAILED
    };

    /** Our current state */
    private static State state = State.UNINITIALISED;

    /** Factory for creating accept listener instances */
    private static AcceptListenerFactory acceptListenerFactory;

    /**
     * Create a new server connection manager
     */
    public ServerConnectionManagerImpl() {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
            SibTr.entry(this, tc, "<init>");
        portToListenerMap = new Hashtable<Integer, ListenerPortImpl>();
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
            SibTr.exit(this, tc, "<init>");
    }

    /**
     * @param quiesce
     */
    @Override
    public void closeAll(boolean quiesce) {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
            SibTr.entry(this, tc, "closeAll", "" + quiesce);
        // MS:4 do we still need this? (tidyup)
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
            SibTr.exit(this, tc, "closeAll");
    }

    /**
     * Initialises the server connection manager by getting hold of the framework.
     * 
     * @param _acceptListenerFactory
     * 
     * @see com.ibm.ws.sib.jfapchannel.ServerConnectionManager#initialise(com.ibm.ws.sib.jfapchannel.AcceptListenerFactory)
     */
    public static void initialise(AcceptListenerFactory _acceptListenerFactory) {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
            SibTr.entry(tc, "initalise");

        acceptListenerFactory = _acceptListenerFactory;

        // Create the maintainer of the configuration.
        Framework framework = Framework.getInstance();
        if (framework == null) {
            state = State.INITIALISATION_FAILED;
        } else {
            state = State.INITIALISED;

            // Extract the chain reference.
            connectionTracker = new OutboundConnectionTracker(framework);
        }

        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
            SibTr.exit(tc, "initalise");
    }

    /**
     * Set the AcceptListenerFactory.
     * 
     * @param _acceptListenerFactory
     */
    public static void initialiseAcceptListenerFactory(AcceptListenerFactory _acceptListenerFactory) {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
            SibTr.entry(tc, "initialiseAcceptListenerFactory", _acceptListenerFactory);

        acceptListenerFactory = _acceptListenerFactory;

        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
            SibTr.exit(tc, "initialiseAcceptListenerFactory");
    }

    /**
     * Connect to a remote ME.
     * 
     * @see com.ibm.ws.sib.jfapchannel.ServerConnectionManager#connect(java.net.InetSocketAddress, com.ibm.ws.sib.jfapchannel.ConversationReceiveListener, java.lang.String)
     */
    // begin F171173
    @Override
    public Conversation connect(InetSocketAddress remoteHost,
                                ConversationReceiveListener convRecvListener,
                                String chainName)
                    throws SIResourceException {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
            SibTr.entry(this, tc, "connect", new Object[] { remoteHost, convRecvListener, chainName });
        // begin F178022, F196678.10
        if (state == State.UNINITIALISED) {
            throw new SIErrorException(nls.getFormattedMessage("SVRCONNMGR_INTERNAL_SICJ0059", null, "SVRCONNMGR_INTERNAL_SICJ0059")); // D226223
        } else if (state == State.INITIALISATION_FAILED) {
            String nlsMsg = nls.getFormattedMessage("EXCP_CONN_FAIL_NO_CF_SICJ0007", null, null);
            if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
                SibTr.debug(this, tc, "connection failed because comms failed to initialise");
            throw new SIResourceException(nlsMsg);
        }
        // end F178022, F196678.10

        Conversation retValue = connectionTracker.connect(remoteHost,
                                                          convRecvListener,
                                                          chainName,
                                                          Conversation.ME);
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
            SibTr.exit(this, tc, "connect", retValue);
        return retValue;
    }

    // end F171173

    /**
     * Implementation of the connect method provided by the abstract parent
     * class. This flavour of connect establishes a connection using WLM
     * endpoint data, which, can be treated as an opaque object passed to the
     * Channel Framework.
     * 
     * @see ServerConnectionManager
     * @param endpoint
     * @param convRecvListener
     */
    @Override
    public Conversation connect(CFEndPoint endpoint,
                                ConversationReceiveListener convRecvListener)
                    throws SIResourceException {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
            SibTr.entry(this, tc, "connect", new Object[] { endpoint, convRecvListener });
        // begin F178022, F196678.10
        if (state == State.UNINITIALISED) {
            throw new SIErrorException(nls.getFormattedMessage("SVRCONNMGR_INTERNAL_SICJ0059", null, "SVRCONNMGR_INTERNAL_SICJ0059")); // D226223
        } else if (state == State.INITIALISATION_FAILED) {
            String nlsMsg = nls.getFormattedMessage("EXCP_CONN_FAIL_NO_CF_SICJ0007", null, null);
            if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
                SibTr.debug(this, tc, "connection failed because comms failed to initialise");
            throw new SIResourceException(nlsMsg); // F173069, // F174602
        }
        // end F178022, F196678.10

        Conversation retValue = connectionTracker.connect(endpoint, convRecvListener, Conversation.ME);
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
            SibTr.exit(this, tc, "connect", retValue);
        return retValue;
    }

    // begin F189351
    protected static AcceptListenerFactory getAcceptListenerFactory() {
        return acceptListenerFactory;
    }

    // end F189351

    /**
     * Obtains a list of active outbound ME to ME conversations in this JVM.
     * 
     * @return a list of Conversations
     */
    @Override
    public List getActiveOutboundMEtoMEConversations() {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
            SibTr.entry(this, tc, "getActiveOutboundMEtoMEConversations");

        List convs = null;
        if (connectionTracker != null) {
            convs = connectionTracker.getAllOutboundConversations();
        }

        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
            SibTr.exit(this, tc, "getActiveOutboundMEtoMEConversations", convs);
        return convs;
    }
}
