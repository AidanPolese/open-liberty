/*
 * @start_prolog@
 * Version: @(#) 1.25 SIB/ws/code/sib.comms.server.impl/src/com/ibm/ws/sib/comms/server/clientsupport/ServerTransportAcceptListener.java, SIB.comms, WASX.SIB, aa1225.01 10/03/25 10:06:23 [7/2/12 05:59:00]
 * ============================================================================
 * IBM Confidential OCO Source Materials
 *
 * 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70 Copyright IBM Corp. 2004, 2010
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
 * Creation        030325 schmittm Original
 * f166313         030506 schmittm add TRM support + cleanup
 * f166959         030520 schmittm WsByteBuffer and TCPChannel rework
 * d170527         030625 mattheg  Tidy and change to SibTr
 * d173544         030805 mattheg  Take out the call to setConversation()
 * F193735.3       040607 prestona PMI
 * D219803         040810 mattheg  Access on getInstance()
 * D199177         040816 mattheg  JavaDoc
 * D321398         051107 mattheg  Add support for conversation tracking
 * D361638         060410 mattheg  Improve connection cleanup
 * D364053         060421 mattheg  Clean up connection from Map when no Conversations remain
 * SIB0100.wmq.3   070815 mleming  Allow ConversationCloseListener chaining
 * 464663          070905 sibcopyr Automatic update of trace guards
 * 471664          071003 vaughton Findbugs tidy up
 * PK53356         071008 prestona Deadlock between MP close connection and removeAllConversations
 * PK83641         310309 ajw      reset LinkLevelState when returning from pool
 * F002074         091022 mleming  MEP support FIS
 * ============================================================================
 */
package com.ibm.ws.sib.comms.server.clientsupport;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.ffdc.FFDCFilter;
import com.ibm.ws.sib.comms.CommsConstants;
import com.ibm.ws.sib.comms.server.ConversationState;
import com.ibm.ws.sib.comms.server.ServerLinkLevelState;
import com.ibm.ws.sib.jfapchannel.AcceptListener;
import com.ibm.ws.sib.jfapchannel.ConnectionClosedListener;
import com.ibm.ws.sib.jfapchannel.Conversation;
import com.ibm.ws.sib.jfapchannel.ConversationReceiveListener;
import com.ibm.ws.sib.jfapchannel.ConversationUsageType;
import com.ibm.ws.sib.utils.ras.SibTr;

/**
 * This class is invoked when a connection is ready for acceptance by the JFap channel and that
 * connection has been determined to be a Comms Client->Server connection.
 * <p>
 * This class also maintains a list of active conversations so that diagnostic tools such as the
 * FFDC diagnositc module can obtain information about the active conversations.
 * <p>
 * This class implements ConnectionClosedListener to catch the socket going away before a
 * connection to the ME has been established. Once this has occurred, the ConnectionClosedListener
 * is transferred to be the ServerSideConnection instance.
 * 
 * @author schmittm
 */
public class ServerTransportAcceptListener implements AcceptListener, ConnectionClosedListener {
    /** Class name for FFDC's */
    private static String CLASS_NAME = ServerTransportAcceptListener.class.getName();

    /** Trace */
    private static TraceComponent tc = SibTr.register(ServerTransportAcceptListener.class,
                                                      CommsConstants.MSG_GROUP,
                                                      CommsConstants.MSG_BUNDLE);

    /** Dump the class info */
    static {
        if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
            SibTr.debug(tc,
                        "Source info: @(#)SIB/ws/code/sib.comms.server.impl/src/com/ibm/ws/sib/comms/server/clientsupport/ServerTransportAcceptListener.java, SIB.comms, WASX.SIB, aa1225.01 1.25");
        instance = new ServerTransportAcceptListener();
    }

    /** The singleton instance of this class */
    private static ServerTransportAcceptListener instance;

    /** Reference of the comms receive listener */
    private static ServerTransportReceiveListener serverTransportReceiveListener =
                    ServerTransportReceiveListener.getInstance();

    /**
     * @return Returns the instance of this class.
     */
    public static ServerTransportAcceptListener getInstance() {
        return instance;
    }

    /** Map of connections to active conversations accepted by this accept listener */
    private volatile HashMap activeConversations = new HashMap();

    /**
     * @see com.ibm.js.comms.channelfw.AcceptListener#AcceptListener()
     */
    private ServerTransportAcceptListener() {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
            SibTr.entry(this, tc, "<init>");
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
            SibTr.exit(this, tc, "<init>");
    }

    /**
     * Called when we are about to accept a connection from a peer.
     * 
     * @param cfConversation
     */
    public ConversationReceiveListener acceptConnection(Conversation cfConversation) {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
            SibTr.entry(tc, "acceptConnection", cfConversation);

        // Add this conversation to our active conversations list
        synchronized (activeConversations) {
            Object connectionReference = cfConversation.getConnectionReference();
            ArrayList list = (ArrayList) activeConversations.get(connectionReference);
            if (list == null) {
                list = new ArrayList();
                activeConversations.put(connectionReference, list);
            } else {
                // This is mank - but if TRM does a redirect it is possible that we may get a
                // connection but then they close the Conversation directly without sending us any
                // kind of close flow. As such, every time we connect we should have a check on all
                // the conversations to ensure they are not closed, and if they are remove them from
                // the list.
                ArrayList removeList = new ArrayList();
                for (int x = 0; x < list.size(); x++) {
                    Conversation conv = (Conversation) list.get(x);
                    if (conv.isClosed()) {
                        removeList.add(conv);
                    }
                }

                // Actually do the remove...
                for (int x = 0; x < removeList.size(); x++) {
                    list.remove(removeList.get(x));
                }
            }
            list.add(cfConversation);

            // At this point we have a look to see if the connection closed listener has been set.
            // If it has not been set then this is a new connection (or one with no active
            // Conversations on it. We must set ourselves as the listener in the event that the
            // socket terminates before any SI connections are established and so any cleanup
            // required.
            // Note that once a connection to the ME is established, this listener is overwritten
            // with one in ServerSideConnection. This also performs the same cleanup but also does
            // MFP cleanup as well (which is not appropriate if the connection goes down at this
            // stage).
            if (cfConversation.getConnectionClosedListener(ConversationUsageType.JFAP) == null) {
                cfConversation.addConnectionClosedListener(this, ConversationUsageType.JFAP);
            }
        }

        if (cfConversation.getAttachment() == null) {
            if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
                SibTr.debug(tc, "Creating conversation state");
            cfConversation.setAttachment(new ConversationState());
        }

        if (cfConversation.getLinkLevelAttachment() == null) {
            if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
                SibTr.debug(tc, "Creating link level state");
            cfConversation.setLinkLevelAttachment(new ServerLinkLevelState());
        }

        // Set a hint that this conversation is being used for ME to client communications.
        cfConversation.setConversationType(Conversation.CLIENT);

        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
            SibTr.exit(tc, "acceptConnection", serverTransportReceiveListener);
        return serverTransportReceiveListener;
    }

    /**
     * This method removes a conversation from the list of active conversations. This would be called
     * if the connection is closed or if a failure is deteceted.
     * 
     * @param conv
     */
    public void removeConversation(Conversation conv) {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
            SibTr.entry(this, tc, "removeConversation", conv);

        synchronized (activeConversations) {
            Object connectionReference = conv.getConnectionReference();
            ArrayList list = (ArrayList) activeConversations.get(connectionReference);
            if (list != null) {
                list.remove(conv);

                if (list.size() == 0) {
                    if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
                        SibTr.debug(this, tc, "List is now empty, removing connection object");
                    activeConversations.remove(connectionReference);
                }
            }
        }

        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
            SibTr.exit(this, tc, "removeConversation");
    }

    /**
     * Driven in the event that the underlying connection closes. This will only be driven if the
     * socket dies before a connection to the ME has had chance to be established.
     * 
     * @see com.ibm.ws.sib.jfapchannel.ConnectionClosedListener#connectionClosed(java.lang.Object)
     */
    public void connectionClosed(Object connectionReference) {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
            SibTr.entry(this, tc, "connectionClosed", connectionReference);

        removeAllConversations(connectionReference);

        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
            SibTr.exit(this, tc, "connectionClosed");
    }

    /**
     * @return Returns the list of active conversations.
     */
    public List getActiveConversations() {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
            SibTr.entry(this, tc, "getActiveConversations");
        ArrayList finalList = new ArrayList();
        synchronized (activeConversations) {
            for (Iterator i = activeConversations.values().iterator(); i.hasNext();) {
                finalList.addAll((Collection) i.next());
            }
        }
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
            SibTr.exit(this, tc, "getActiveConversations", finalList);
        return finalList;
    }

    /**
     * This method is used to clean up any resources that
     * 
     * @param connectionReference
     */
    public void removeAllConversations(Object connectionReference) {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
            SibTr.entry(this, tc, "removeAllConversations", connectionReference);

        final ArrayList list;
        synchronized (activeConversations) {
            list = (ArrayList) activeConversations.remove(connectionReference);
        }

        // Remove the connection reference from the list
        if (list != null) {
            try {
                for (int x = 0; x < list.size(); x++) {
                    Conversation conv = (Conversation) list.get(x);
                    if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
                        SibTr.debug(this, tc, "Found a Conversation in the table: ", conv);
                    // Now call the server transport receive listener to clean up the resources
                    serverTransportReceiveListener.cleanupConnection(conv);
                }
            } catch (Throwable t) {
                FFDCFilter.processException(t, CLASS_NAME + ".removeAllConversations",
                                            CommsConstants.SERVERTRANSPORTACCEPTLISTENER_REMOVEALL_01,
                                            this);

                if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
                    SibTr.debug(this, tc, "Caught an exception cleaning up a connection", t);
            }

            // Now clear the list
            list.clear();
        }

        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
            SibTr.exit(this, tc, "removeAllConversations");
    }
}
