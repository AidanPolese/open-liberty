/*
 * @start_prolog@
 * Version: @(#) 1.1 SIB/ws/code/sib.comms.server.impl/src/com/ibm/ws/sib/comms/server/clientsupport/ServerConsumerMonitorListener.java, SIB.comms, WASX.SIB, aa1225.01 11/09/15 10:43:52 [7/2/12 06:13:59]
 * ============================================================================
 * IBM Confidential OCO Source Materials
 *
 * 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70  Copyright IBM Corp. 2007, 2011
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
 * F011127         280611 chetbhat registerConsumerSetMonitor support
 * ============================================================================
 */
package com.ibm.ws.sib.comms.server.clientsupport;

import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.ffdc.FFDCFilter;
import com.ibm.ws.sib.comms.CommsConstants;
import com.ibm.ws.sib.comms.common.CommsByteBuffer;
import com.ibm.ws.sib.comms.server.ServerJFapCommunicator;
import com.ibm.ws.sib.jfapchannel.Conversation;
import com.ibm.ws.sib.jfapchannel.JFapChannelConstants;
import com.ibm.ws.sib.jfapchannel.Conversation.ThrottlingPolicy;
import com.ibm.ws.sib.utils.ras.SibTr;
import com.ibm.wsspi.sib.core.ConsumerSetChangeCallback;
import com.ibm.wsspi.sib.core.exception.SIConnectionDroppedException;
import com.ibm.wsspi.sib.core.exception.SIConnectionLostException;

public class ServerConsumerMonitorListener extends ServerJFapCommunicator implements ConsumerSetChangeCallback {

    private static String CLASS_NAME = ServerConsumerMonitorListener.class.getName();
    private static final TraceComponent tc = SibTr.register(ServerConsumerMonitorListener.class, CommsConstants.MSG_GROUP, CommsConstants.MSG_BUNDLE);

    static {
        if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
            SibTr.debug(tc, "@(#) SIB/ws/code/sib.comms.server.impl/src/com/ibm/ws/sib/comms/server/clientsupport/ServerConsumerMonitorListener.java, SIB.comms, WASX.SIB");
    }

    private final short consumerMonitorListenerID; // consumerMonitorListener cache Id value
    private final short connectionObjectId; // Conversation Id

    public ServerConsumerMonitorListener(final short id, final short connectionObjectId, final Conversation conversation) {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
            SibTr.entry(this, tc, "<init>", "id=" + id + ",convId=" + connectionObjectId + ",conversation=" + conversation);

        this.consumerMonitorListenerID = id;
        this.connectionObjectId = connectionObjectId;
        setConversation(conversation);

        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
            SibTr.exit(this, tc, "<init>");
    }

    @Override
    public void consumerSetChange(boolean isEmpty) {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
            SibTr.entry(this, tc, "consumerSetChange", new Object[] { isEmpty });
        if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
            SibTr.debug(this, tc, "Sending ConsumerSetChangeCallback callback for: convId=" + connectionObjectId + ",id=" + consumerMonitorListenerID);

        CommsByteBuffer request = getCommsByteBuffer();

        // Put connectionObjectId
        request.putShort(connectionObjectId);

        // Put consumerMonitorListenerid
        request.putShort(consumerMonitorListenerID);

        // Put isEmpty
        request.putBoolean(isEmpty);

        try {
            jfapSend(request, JFapChannelConstants.SEG_REGISTER_CONSUMER_SET_MONITOR_CALLBACK_NOREPLY, JFapChannelConstants.PRIORITY_MEDIUM, true, ThrottlingPolicy.BLOCK_THREAD);
        } catch (SIConnectionLostException e) {
            if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
                SibTr.debug(tc, e.getMessage(), e);
            FFDCFilter.processException(e, CLASS_NAME + ".consumerSetChange", CommsConstants.SERVERCONSUMERMONITORLISTENER_CONSUMERSETCHANGE_01, this);
        } catch (SIConnectionDroppedException e) {
            if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
                SibTr.debug(tc, e.getMessage(), e);
            FFDCFilter.processException(e, CLASS_NAME + ".consumerSetChange", CommsConstants.SERVERCONSUMERMONITORLISTENER_CONSUMERSETCHANGE_02, this);
        }

        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
            SibTr.exit(this, tc, "consumerSetChange");

    }
}
