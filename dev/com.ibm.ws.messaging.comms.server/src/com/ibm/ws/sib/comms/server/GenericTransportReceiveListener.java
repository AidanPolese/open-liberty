/*
 * @start_prolog@
 * Version: @(#) 1.33 SIB/ws/code/sib.comms.server.impl/src/com/ibm/ws/sib/comms/server/GenericTransportReceiveListener.java, SIB.comms, WASX.SIB, aa1225.01 09/04/01 07:23:34 [7/2/12 06:01:49]
 * ============================================================================
 * IBM Confidential OCO Source Materials
 *
 * 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5724-J08  Copyright IBM Corp. 2003, 2007
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
 * Creation        030725 Niall    Original
 * Dxxxxxx         030807 Niall    Modify behaviour of secondary conversations
 * F174602         030820 prestona Switch to using SICommsException
 * F174776         030828 Niall    Chained Receive Listener Support
 * F176003         030912 prestona Misc JFAP Channel reliability fixes.
 * d175811         030919 mattheg  Make handshake obey spec
 * f181007         031211 mattheg  Add boolean 'exchange' flag on dataReceived()
 * d186970         040116 mattheg  Overhaul the way we send exceptions to client
 * F188491         040128 prestona Migrate to M6 CF + TCP Channel
 * d192002         040226 mattheg  Use NLS text when calling SibTr.error
 * d192293         040308 mattheg  NLS file changes
 * d195714         040419 mattheg  Add NLS messages to closeReceived
 * F201521         040506 mattheg  Add getThreadContext() method
 * F193735.3       040607 prestona PMI
 * D217372         040719 mattheg  Move JFap constants -> JFapChannelConstants (not change-flagged)
 * F201972.2       040727 mattheg  Core SPI Exceptions rework (not change flagged)
 * D199177         040816 mattheg  JavaDoc
 * D225856         041006 mattheg  Update FFDC class name (not change flagged)
 * SIB0048b.com.5  060913 mattheg  JFap channel for Portly client rework
 * D381838         070130 mattheg  Use Converstion.getFullSummary() instead of toString()
 * 471664          071003 vaughton Findbugs tidy up
 * PK83641         310309 ajw      reset LinkLevelState when returning from pool;
 * ============================================================================
 */
package com.ibm.ws.sib.comms.server;

import com.ibm.ejs.ras.TraceNLS;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.ffdc.FFDCFilter;
import com.ibm.ws.sib.comms.CommsConstants;
import com.ibm.ws.sib.comms.server.clientsupport.ServerTransportAcceptListener;
import com.ibm.ws.sib.comms.server.clientsupport.StaticCATHelper;
import com.ibm.ws.sib.jfapchannel.Conversation;
import com.ibm.ws.sib.jfapchannel.ConversationReceiveListener;
import com.ibm.ws.sib.jfapchannel.Dispatchable;
import com.ibm.ws.sib.jfapchannel.JFapChannelConstants;
import com.ibm.ws.sib.jfapchannel.buffer.WsByteBuffer;
import com.ibm.ws.sib.utils.ras.SibTr;
import com.ibm.wsspi.sib.core.exception.SIConnectionLostException;

/**
 * @author Niall
 * 
 *         Generic Receive Listener that acts as a filter to pass received data
 *         to either the MEConnection Listener or the ServerTransportReceive Listener
 *         The first time data is received on a conversation, the dataReceived()
 *         method is driven. The segment type can then be used to determine whether
 *         a client or ME initiated this conversation and the appropriate listener
 *         is then associated with it.
 */
public class GenericTransportReceiveListener implements ConversationReceiveListener {
    /** Class name for FFDC's */
    private static String CLASS_NAME = GenericTransportReceiveListener.class.getName();

    /** Trace */
    private static final TraceComponent tc = SibTr.register(GenericTransportReceiveListener.class,
                                                            CommsConstants.MSG_GROUP,
                                                            CommsConstants.MSG_BUNDLE);

    /** Our NLS reference object */
    private static final TraceNLS nls = TraceNLS.getTraceNLS(CommsConstants.MSG_BUNDLE);

    /** Singleton */
    private static GenericTransportReceiveListener instance;

    static {
        if (tc.isDebugEnabled())
            SibTr.debug(
                        tc,
                        "Source info: @(#)SIB/ws/code/sib.comms.server.impl/src/com/ibm/ws/sib/comms/server/GenericTransportReceiveListener.java, SIB.comms, WASX.SIB, aa1225.01 1.33");

        instance = new GenericTransportReceiveListener();
    }

    /** Server Transport accept listener */
    private static ServerTransportAcceptListener serverTransportAcceptListener =
                                                      ServerTransportAcceptListener.getInstance();

    /**
     * @return Returns the singleton instance.
     */
    protected static GenericTransportReceiveListener getInstance() {
        return instance;
    }

    /**
     * Interpret the segment type sent from the client and call the call the appropriate "segment" method.
     * The called method will extract the data from the WsByteBuffer, perform desired behavior, and send a
     * reply back to the client on the conversation referencing the request number initially sent. If the
     * segment type is not understood the default case will be called and a comms exception will be thrown.
     * 
     * @param data
     * @param segmentType
     * @param requestNumber
     * @param priority
     * @param allocatedFromBufferPool
     * @param partOfExchange
     * @param conversation
     * 
     * @return ConversationReceiveListener A more appropriate receiveListener to receive
     *         future data on this conversation
     */
    public ConversationReceiveListener dataReceived(WsByteBuffer data, int segmentType,
                                                    int requestNumber, int priority,
                                                    boolean allocatedFromBufferPool,
                                                    boolean partOfExchange, // f181007
                                                    Conversation conversation) {
        if (tc.isEntryEnabled())
            SibTr.entry(tc, "dataReceived");

        ConversationReceiveListener listener = null; // F193735.3

        int connectionType = -1;
        ServerLinkLevelState lls = null;

        if (tc.isDebugEnabled()) {
            String LF = System.getProperty("line.separator");
            String debugInfo = LF + LF + "-------------------------------------------------------" + LF;

            debugInfo += " Segment type  : " + JFapChannelConstants.getSegmentName(segmentType) +
                                        " - " + segmentType +
                                        " (0x" + Integer.toHexString(segmentType) + ")" + LF;
            debugInfo += " Request number: " + requestNumber + LF;
            debugInfo += " Priority      : " + priority + LF;
            debugInfo += " Exchange?     : " + partOfExchange + LF;
            debugInfo += " From pool?    : " + allocatedFromBufferPool + LF;
            debugInfo += " Conversation  : " + conversation + LF;

            debugInfo += "-------------------------------------------------------" + LF;

            SibTr.debug(this, tc, debugInfo);
            SibTr.debug(this, tc, conversation.getFullSummary());
        }

        // If no listener has yet been associated with this conversation, determine
        // the appropriate one to use based on the first byte of the handshake data.
        // SEG_HANDSHAKE is expected at this point.
        if (segmentType == JFapChannelConstants.SEG_HANDSHAKE) {
            data.flip();

            //Now get connection mode
            connectionType = data.get(); // d175811

            //Store away the connection type for fututre conversations
            switch (connectionType) {
                // Client handshake
                case CommsConstants.HANDSHAKE_CLIENT:
                    listener = serverTransportAcceptListener.acceptConnection(conversation);
                    lls = (ServerLinkLevelState) conversation.getLinkLevelAttachment();
                    lls.setConnectionType(CommsConstants.HANDSHAKE_CLIENT);
                    break;

                // Unknown handshake type
                default:
                    // begin F174602
                    String nlsText = nls.getFormattedMessage("INVALID_PROP_SICO8008",
                                                             new Object[] { "" + connectionType }, // d192293
                                                             null);

                    SIConnectionLostException commsException = new SIConnectionLostException(nlsText);

                    StaticCATHelper.sendExceptionToClient(commsException,
                                                          null, // d186970
                                                          conversation,
                                                          requestNumber);
                    break;
                // end F174602
            }
        } else {
            //We've previously noted the initiating connection type from an earlier
            //conversation on this connections so we can retrieve it from the
            //Link level state

            lls = (ServerLinkLevelState) conversation.getLinkLevelAttachment();
            connectionType = lls.getConnectionType();

            switch (connectionType) {
                // Client handshake
                case CommsConstants.HANDSHAKE_CLIENT:
                    if (tc.isDebugEnabled())
                        SibTr.debug(tc, "Conversation was initiated by a Client. Filtering accordingly");
                    listener = serverTransportAcceptListener.acceptConnection(conversation);
                    break;

                // Unknown handshake type
                default: //F174776
                    if (tc.isDebugEnabled())
                        SibTr.debug(tc, "Conversation was initiated by an unknown entity ", "" + connectionType);
                    break;
                // end F174602
            }
        }

        if (tc.isEntryEnabled())
            SibTr.exit(tc, "dataReceived", listener); // F193735.3

        // F174776 Return the chosen subordinate listener to handle all data from
        // now on, on this conversation
        return listener;
    }

    /**
     * Notification that an error occurred when we were expecting to receive
     * a response. This method is used to "wake up" any conversations using
     * a connection for which an error occurres. At the point this method is
     * invoked, the connection will already have been marked "invalid".
     * <p>
     * It is used to notify
     * the per conversation receive listener of (almost) all error conditions
     * encountered on the associated connection.
     * 
     * @see ConversationReceiveListener
     * @param exception The exception which occurred.
     * @param segmentType The segment type of the data (-1 if not known)
     * @param requestNumber The request number associated with the failing
     *            request (-1 if not known)
     * @param priority The priority associated with the failing request
     *            (-1 if not known).
     * @param conversation The conversation (null if not known)
     */
    public void errorOccurred(SIConnectionLostException exception, // F174602
                              int segmentType,
                              int requestNumber,
                              int priority,
                              Conversation conversation) {
        if (tc.isEntryEnabled())
            SibTr.entry(this, tc, "errorOccurred",
                                           new Object[] { exception, segmentType, requestNumber, priority, conversation });

        FFDCFilter.processException(exception,
                                    CLASS_NAME + ".errorOccurred",
                                    CommsConstants.GENERICTRANSPORTRECEIVELISTENER_ERROR_01,
                                    this);

        if (tc.isDebugEnabled()) {
            Object[] debug =
                          {
                             "Segment type  : " + segmentType + " (0x" + Integer.toHexString(segmentType) + ")",
                             "Request number: " + requestNumber,
                             "Priority      : " + priority
                          };
            SibTr.debug(tc, "Received an error in the GenericTransportReceiveListener", debug);

            SibTr.debug(tc, "Primary exception:");
            SibTr.exception(tc, exception);
        }

        if (tc.isEntryEnabled())
            SibTr.exit(tc, "errorOccurred");
    }

    // Start F201521
    /**
     * This method is called by the JFap channel to give us the oppurtunity to specify which thread
     * the receive listener should dispatch the data on.
     * <p>
     * In this instance, we do not want to do anything special here, so returning null indicates to
     * the JFap channel that dispatching should be done on a per conversation basis.
     * 
     * @param conversation The conversation the data is about to be dispatched on.
     * @param data The data about to be dispatched.
     * @param segmentType The segment type of the data.
     * 
     * @return Returns null.
     */
    public Dispatchable getThreadContext(Conversation conversation, WsByteBuffer data, int segmentType) {
        if (tc.isEntryEnabled())
            SibTr.entry(this, tc, "getThreadContext",
                                           new Object[] { conversation, data, segmentType });
        if (tc.isEntryEnabled())
            SibTr.exit(tc, "getThreadContext", null);

        return null;
    }
    // End F201521
}
