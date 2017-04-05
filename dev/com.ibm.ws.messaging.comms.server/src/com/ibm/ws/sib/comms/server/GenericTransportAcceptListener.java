/*
 * @start_prolog@
 * Version: @(#) 1.12 SIB/ws/code/sib.comms.server.impl/src/com/ibm/ws/sib/comms/server/GenericTransportAcceptListener.java, SIB.comms, WASX.SIB, aa1225.01 08/06/11 06:37:02 [7/2/12 06:01:49]
 * ============================================================================
 * IBM Confidential OCO Source Materials
 * 
 * 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70  (C) Copyright IBM Corp. 2003, 2008
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
 * Creation        030724 niall    Original
 * F193735.3       040607 prestona PMI
 * D209401         040615 matthg   Comms service utility
 * D199177         040816 mattheg  JavaDoc
 * SIB0048b.com.1  060901 mattheg  Remove CommsServiceUtility hooks
 * ============================================================================
 */
package com.ibm.ws.sib.comms.server;

import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.sib.comms.CommsConstants;
import com.ibm.ws.sib.jfapchannel.AcceptListener;
import com.ibm.ws.sib.jfapchannel.Conversation;
import com.ibm.ws.sib.jfapchannel.ConversationReceiveListener;
import com.ibm.ws.sib.utils.ras.SibTr;

/**
 * A listener which is notified when new incoming conversations are
 * accepted by a listening socket. It provides a generic
 * ReceiveListener when a connection is accepted which
 * will then filter data to an appropriate subordinate ReceiveListener
 * depending on whether the connection is client or ME initiated.
 * <p>
 * By the time this listener is notified, the new conversation will have been
 * established but no initial data flows will have been sent.
 * 
 * @author prestona
 */
public class GenericTransportAcceptListener implements AcceptListener {
    /** Trace */
    private static TraceComponent tc = SibTr.register(GenericTransportAcceptListener.class,
                                                      CommsConstants.MSG_GROUP,
                                                      CommsConstants.MSG_BUNDLE);

    /** Singleton instance */
    private static GenericTransportReceiveListener genericTransportRecieveListnerInstance = GenericTransportReceiveListener.getInstance();

    /** Log class info on load */
    static {
        if (tc.isDebugEnabled())
            SibTr.debug(tc,
                        "Source info: @(#)SIB/ws/code/sib.comms.server.impl/src/com/ibm/ws/sib/comms/server/GenericTransportAcceptListener.java, SIB.comms, WASX.SIB, aa1225.01 1.12");
    }

    /**
     * Constructor
     */
    public GenericTransportAcceptListener() {
        if (tc.isEntryEnabled())
            SibTr.entry(tc, "<init>");

        if (tc.isEntryEnabled())
            SibTr.exit(tc, "<init>");
    }

    /**
     * Notified when a new conversation is accepted by a listening socket.
     * 
     * @param cfConversation The new conversation.
     * 
     * @return The conversation receive listener to use for asynchronous receive
     *         notification for this whole conversation.
     */
    public ConversationReceiveListener acceptConnection(Conversation cfConversation) {
        if (tc.isEntryEnabled())
            SibTr.entry(tc, "acceptConnection");

        // Return new instance of a GenericTransportReceiveListener. This listener
        // determines whether data has been received from a client or ME and routes it
        // accordingly to the ServerTransportReceiveListener or MEConnectionListener

        if (tc.isEntryEnabled())
            SibTr.exit(tc, "acceptConnection");

        return genericTransportRecieveListnerInstance; // F193735.3
    }
}
