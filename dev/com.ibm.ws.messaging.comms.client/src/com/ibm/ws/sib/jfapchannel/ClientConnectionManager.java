/*
 * @start_prolog@
 * Version: @(#) 1.36 SIB/ws/code/sib.jfapchannel.client/src/com/ibm/ws/sib/jfapchannel/ClientConnectionManager.java, SIB.comms, WASX.SIB, uu1215.01 10/03/25 05:19:36 [4/12/12 22:14:11]
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
 * F174602         030819 prestona Switch to using SICommsException
 * F175658         030902 prestona Add support for heartbeating.
 * D181601         031031 prestona Improve quality of JFAP Channel RAS
 * F182479         031127 prestona New ConnectionProperties varient required.
 * F184828         031204 prestona Update CF + TCP prereqs to MS 5.1 level
 * F188491         030128 prestona Migrate to M6 CF + TCP Channel
 * F189000         030130 prestona Expose WLM endpoints through CF
 * F191798         030227 prestona Use proper chain names
 * F196678.10      040426 prestona JS Client Administration
 * F201972.2       040727 mattheg  Core SPI Exceptions rework (not change flagged)
 * D199145         040812 prestona Fix Javadoc
 * D223632         040812 prestona Defend against errors during initialisiation
 * F244595         041129 prestona z/OS: TCP Proxy Bridge Support
 * D321398         051107 mattheg  Expose a list of active outbound connections
 * D377648         060714 mattheg  Remove getBufferPoolManager() method
 * SIB0048b.com.5  060913 mattheg  JFap channel for Portly client rework
 * SIB0100.wmq.3   070813 mleming  Allow WMQRA to use TCP Proxy Bridge
 * D453961.1       070904 mleming  Rethrow any exception caught on initialise
 * D464663         070905 sibcopyright Automatic update of trace guards
 * PM07974         100223 timmccor Add getActiveOutboundConversationsForFfdc()
 * F002074         091022 mleming  MEP support FIS
 * 95897           041613 Chetan   Comms Outbound Chain revamp
 * ============================================================================  
 */
package com.ibm.ws.sib.jfapchannel;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.util.List;

import com.ibm.websphere.ras.TraceComponent;
import com.ibm.websphere.sib.exception.SIResourceException;
import com.ibm.ws.ffdc.FFDCFilter;
import com.ibm.ws.sib.jfapchannel.impl.octracker.OutboundConnectionTracker;
import com.ibm.ws.sib.utils.ras.SibTr;

/**
 * An abstraction of the essential Channel Framework / TCP Channel network
 * services wrapped into a JetStream friendly class. In essence, this class
 * attempts to hide the complexities required to play in the channel
 * framework from the rest of the communications code.
 */
public abstract class ClientConnectionManager
{
    private static final TraceComponent tc = SibTr.register(ClientConnectionManager.class, JFapChannelConstants.MSG_GROUP, JFapChannelConstants.MSG_BUNDLE);

    static
    {
        if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
            SibTr.debug(tc, "@(#) SIB/ws/code/sib.jfapchannel.client/src/com/ibm/ws/sib/jfapchannel/ClientConnectionManager.java, SIB.comms, WASX.SIB, uu1215.01 1.36");
    }

    // Tracks if this class has been initialised or not.
    private static boolean initialised = false;

    // Reference to sole instance of this class in existence.
    private static ClientConnectionManager instance = null;

    /**
     * Chain name for a "raw" TCP socket (ie. not tunneled).
     * <strong>NOTE:</strong> This shouldn't be used any more - use chain names defined in the
     * comms component ConnectionProperties class.
     */
    public static final String TCP_CHAIN = JFapChannelConstants.CHAIN_NAME_DEFAULT_OUTBOUND_JFAP_TCP; // F196678.10

    static
    {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
            SibTr.entry(tc, "static <init>");
        try
        {
            Class clientImpl =
                            Class.forName(JFapChannelConstants.CLIENT_MANAGER_CLASS);

            Constructor clientImplConstructor =
                            clientImpl.getConstructor(new Class[] {});
            instance = (ClientConnectionManager)
                            clientImplConstructor.newInstance(new Object[] {});
        } catch (Exception e)
        {
            FFDCFilter.processException(e,
                                        "com.ibm.ws.sib.jfapchannel.ClientConnectionManager.{}",
                                        JFapChannelConstants.CLNTCONNMGR_STATICCONS_01);
            SibTr.error(tc, "EXCP_DURING_INIT_SICJ0001", new Object[] {
                                                                       JFapChannelConstants.CLIENT_MANAGER_CLASS, e }); // D192293.1
            if (TraceComponent.isAnyTracingEnabled() && tc.isEventEnabled())
                SibTr.exception(tc, e);
        }
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
            SibTr.exit(tc, "static <init>");
    }

    /**
     * Initialises the channel framework. This must be called before any
     * other method may be invoked in this class. Don't worry - you can call
     * this multiple times without anything bad happening.
     * 
     * @throws SIResourceException
     */
    public static synchronized void initialise() throws SIResourceException
    {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
            SibTr.entry(tc, "initialise");
        if (!initialised)
        {
            if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
                SibTr.debug(tc, "not previously initialised");

            //   new com.ibm.ws.sib.jfapchannel.impl.ClientConnectionManagerImpl();
            Class clientImpl = instance.getClass();
            Method initialiseMethod;
            try
            {
                initialiseMethod = clientImpl.getMethod("initialise", new Class[] {});
                initialiseMethod.invoke(clientImpl, new Object[] {});
                initialised = true; // D223632
            } catch (Exception e)
            {
                FFDCFilter.processException
                                (e, "com.ibm.ws.sib.jfapchannel.ClientConnectionManager.initialise",
                                 JFapChannelConstants.CLNTCONNMGR_INITIALISE_01);

                SibTr.error(tc, "EXCP_DURING_INIT_SICJ0002", new Object[] { "initialise", JFapChannelConstants.CLIENT_MANAGER_CLASS, e });
                if (TraceComponent.isAnyTracingEnabled() && tc.isEventEnabled())
                    SibTr.exception(tc, e);
                throw new SIResourceException(e);
            }

        }
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
            SibTr.exit(tc, "initialise");
    }

    /**
     * Returns a reference to the single instance of this class in existence.
     * The class must have been previously initilised by a call to the
     * "initialise" method - otherwise invoking this method will generate a
     * runtime exception.
     * This class implements the singleton design pattern.
     * 
     * @return ChannelFramework A reference to the only instance of this class
     *         which exists.
     */
    public static synchronized ClientConnectionManager getRef()
    {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
            SibTr.entry(tc, "getRef");
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
            SibTr.exit(tc, "getRef", instance);
        return instance;
    }

    /**
     * Attempts a TCP connection to the specified ME. A new connection object
     * is returned. If successful, this method will return once a connection
     * has been established. The calling code needs to arrange for the initial
     * flows to take place.
     * 
     * @param remoteHost The remote host to connect to.
     * @param conversationReceiveListener The asynchronous receive listener to
     *            be notified when a asynchronous request (ie. one we didn't
     *            immediately solicit) is received from our peer.
     * @param chainName The name of the CF chain to use to establish the connection.
     *            This should be taken from the constants provided by
     *            this class.
     * @return Conversation An object representing the conversation established.
     * 
     * @throws SIResourceException
     */
    public abstract Conversation connect(InetSocketAddress remoteHost,
                                         ConversationReceiveListener conversationReceiveListener,
                                         String chainName)
                    throws SIResourceException;

    // begin F189000
    /**
     * Attempts to establish an outbound network connection with the target
     * WLM end point. The end point data supplied is an opaque "cookie" which
     * is passed through the the Channel Framework.
     * 
     * @param endPoint The end point to connect using.
     * @param conversationReceiveListener A listener that is notified when data is received
     *            asynchronously.
     * @throws SIResourceException
     */
    public abstract Conversation connect(Object endPoint,
                                         ConversationReceiveListener conversationReceiveListener)
                    throws SIResourceException;

    // end F189000

    // begin F244595
    /**
     * Attempts to establish an outbound network connection from a z/OS
     * SR to the z/OS CRA running in the same process.
     * 
     * @throws SIResourceException Thrown if the connection cannot be
     *             established (this may, for example, be because the code is not
     *             executing on Z).
     * @param conversationReceiveListener A listener that is notified when data is received
     *            asynchronously.
     * 
     * @param type indicates the way the conversation will be used.
     */
    public abstract Conversation connect(ConversationReceiveListener conversationReceiveListener, ConversationUsageType type)
                    throws SIResourceException;

    // end F244595

    /**
     * Obtains a list of the active outbound conversations in this JVM.
     * 
     * @return Returns a list of Conversations
     */
    public abstract List getActiveOutboundConversations();

    /**
     * Obtains a dirty list of the active outbound conversations in this JVM, without obtaining locks.
     * 
     * @return Returns a list of Conversations, or null if we weren't able to generate the list
     */
    public abstract List getActiveOutboundConversationsForFfdc();

    /**
     * Returns the OutboundConnectiontracker
     * 
     * @return
     */
    public abstract OutboundConnectionTracker getOutboundConnectionTracker();
}
