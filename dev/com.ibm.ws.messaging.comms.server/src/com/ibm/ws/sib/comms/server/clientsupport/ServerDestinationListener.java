/*
 * @start_prolog@
 * Version: @(#) 1.7 SIB/ws/code/sib.comms.server.impl/src/com/ibm/ws/sib/comms/server/clientsupport/ServerDestinationListener.java, SIB.comms, WASX.SIB, aa1225.01 08/01/28 09:28:20 [7/2/12 05:58:40]
 * ============================================================================
 * IBM Confidential OCO Source Materials
 *
 * 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70  Copyright IBM Corp. 2007, 2008
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
 * SIB0137.comms.2 070925 vaughton addDestinationListener support
 * SIB0137.comms.3 070925 vaughton addDestinationListener support Part 2
 * 494335          080128 mleming  Flow localOnly information on the wire
 * ============================================================================
 */
package com.ibm.ws.sib.comms.server.clientsupport;

import com.ibm.websphere.ras.TraceComponent;
import com.ibm.websphere.sib.SIDestinationAddress;
import com.ibm.ws.ffdc.FFDCFilter;
import com.ibm.ws.sib.comms.CommsConstants;
import com.ibm.ws.sib.comms.common.CommsByteBuffer;
import com.ibm.ws.sib.comms.server.ServerJFapCommunicator;
import com.ibm.ws.sib.jfapchannel.Conversation;
import com.ibm.ws.sib.jfapchannel.JFapChannelConstants;
import com.ibm.ws.sib.jfapchannel.Conversation.ThrottlingPolicy;
import com.ibm.ws.sib.utils.ras.SibTr;
import com.ibm.wsspi.sib.core.DestinationAvailability;
import com.ibm.wsspi.sib.core.DestinationListener;
import com.ibm.wsspi.sib.core.SICoreConnection;
import com.ibm.wsspi.sib.core.exception.SIConnectionDroppedException;
import com.ibm.wsspi.sib.core.exception.SIConnectionLostException;

public class ServerDestinationListener extends ServerJFapCommunicator implements DestinationListener {

    private static String CLASS_NAME = ServerDestinationListener.class.getName();
    private static final TraceComponent tc = SibTr.register(ServerDestinationListener.class, CommsConstants.MSG_GROUP, CommsConstants.MSG_BUNDLE);

    static {
        if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
            SibTr.debug(tc,
                        "@(#) SIB/ws/code/sib.comms.server.impl/src/com/ibm/ws/sib/comms/server/clientsupport/ServerDestinationListener.java, SIB.comms, WASX.SIB, aa1225.01 1.7");
    }

    private final short id; // DestinationListener cache Id value
    private final short convId; // Conversation Id

    public ServerDestinationListener(final short id, final short convId, final Conversation conversation) {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
            SibTr.entry(this, tc, "<init>", "id=" + id + ",convId=" + convId + ",conversation=" + conversation);

        this.id = id;
        this.convId = convId;
        setConversation(conversation);

        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
            SibTr.exit(this, tc, "<init>");
    }

    public void destinationAvailable(SICoreConnection connection, SIDestinationAddress destinationAddress, DestinationAvailability destinationAvailability) {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
            SibTr.entry(this, tc, "destinationAvailable", new Object[] { connection, destinationAddress, destinationAvailability });
        if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
            SibTr.debug(this, tc, "Sending DestinationListener callback for: convId=" + convId + ",id=" + id);

        // We don't flow the SICoreConnection as we can recreate this at the client end from the conversation

        CommsByteBuffer request = getCommsByteBuffer();

        // Put conversation id
        request.putShort(convId);

        // Put DestinationListener id
        request.putShort(id);

        // Put SIDestinationAddress
        request.putSIDestinationAddress(destinationAddress, getConversation().getHandshakeProperties().getFapLevel());

        // Put DestinationAvailability
        if (destinationAvailability == null) {
            request.putShort(CommsConstants.NO_DEST_AVAIL);
        } else {
            request.putShort((short) destinationAvailability.toInt());
        }

        try {
            jfapSend(request, JFapChannelConstants.SEG_DESTINATION_LISTENER_CALLBACK_NOREPLY, JFapChannelConstants.PRIORITY_MEDIUM, true, ThrottlingPolicy.BLOCK_THREAD);
        } catch (SIConnectionLostException e) {
            FFDCFilter.processException(e, CLASS_NAME + ".destinationAvailable", CommsConstants.SERVERDESTINATIONLISTENER_DESTAVAILABLE_01, this);
        } catch (SIConnectionDroppedException e) {
            FFDCFilter.processException(e, CLASS_NAME + ".destinationAvailable", CommsConstants.SERVERDESTINATIONLISTENER_DESTAVAILABLE_02, this);
        }

        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
            SibTr.exit(this, tc, "destinationAvailable");
    }
}
