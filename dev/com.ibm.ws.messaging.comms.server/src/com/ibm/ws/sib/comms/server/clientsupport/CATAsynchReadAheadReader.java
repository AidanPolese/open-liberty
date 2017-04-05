/*
 * @start_prolog@
 * Version: @(#) 1.38 SIB/ws/code/sib.comms.server.impl/src/com/ibm/ws/sib/comms/server/clientsupport/CATAsynchReadAheadReader.java, SIB.comms, WASX.SIB, aa1225.01 11/12/16 12:08:38 [7/2/12 05:59:53]
 * ============================================================================
 * IBM Confidential OCO Source Materials
 * 
 * 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70  Copyright IBM Corp. 2004, 2011
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
 * Creation        030407 schmittm Original - Add asynchronous readAhead support
 * f169897.2       030708 mattheg  Update to Core API 0.6
 * d172528         030905 mattheg  Use message priority properly and tidy
 * f177889         031001 mattheg  Core API M4 completion
 * d186970         040116 mattheg  Overhaul the way we send exceptions to client
 * f187521.2.1     040127 mattheg  Unrecoverable reliability -- part 2
 * D217372         040719 mattheg  Move JFap constants -> JFapChannelConstants (not change-flagged)
 * F201972.2       040727 mattheg  Core SPI Exceptions rework (not change flagged)
 * D221806         040805 mattheg  Synchronization needed around RH counters
 * D199177         040816 mattheg  JavaDoc
 * D225856         041006 mattheg  Update FFDC class name (not change flagged)
 * F247845         050208 mattheg  Multicast enablement
 * LIDB3472-0.7    150705 brauneis J2SE Prep
 * D329823         051207 mattheg  Trace improvements
 * D384259         060815 prestona Remove multicast support
 * D441183         072307 mleming  Don't FFDC when calling terminated ME
 * D542573         080808 djvines  Forward port of PK48072: Protect against closed connections
 * D605093         090824 mleming  Provide single isRecoverable implementation
 * 609285          090826 sibcopyr Automatic update of trace guards
 * PM34003         110307 slaterpa Stop consumer if conversation closed or no msg data sent
 * F1344-55544.1   111211 skavitha Add records if XCT enabled
 * F1344-55985     161211 skavitha XCT ConsumeMessages changed to ConsumeSend
 * ============================================================================
 */
package com.ibm.ws.sib.comms.server.clientsupport;

import com.ibm.websphere.ras.TraceComponent;
import com.ibm.websphere.sib.exception.SIException;
import com.ibm.ws.ffdc.FFDCFilter;
import com.ibm.ws.sib.comms.CommsConstants;
import com.ibm.ws.sib.comms.common.CommsUtils;
import com.ibm.ws.sib.comms.server.ConversationState;
import com.ibm.ws.sib.jfapchannel.JFapChannelConstants;
import com.ibm.ws.sib.utils.ras.SibTr;
import com.ibm.wsspi.sib.core.AsynchConsumerCallback;
import com.ibm.wsspi.sib.core.LockedMessageEnumeration;
import com.ibm.wsspi.sib.core.SIBusMessage;

/**
 * This class is the async callback for a read ahead session.
 * 
 * @author Gareth Matthews
 */
public class CATAsynchReadAheadReader implements AsynchConsumerCallback {
    /** Class name for FFDC's */
    private static String CLASS_NAME = CATAsynchReadAheadReader.class.getName();

    /** Trace */
    private static final TraceComponent tc = SibTr.register(CATAsynchReadAheadReader.class,
                                                            CommsConstants.MSG_GROUP,
                                                            CommsConstants.MSG_BUNDLE);
    /** Log source info on static load */
    static {
        if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
            SibTr.debug(
                        tc,
                        "Source info: @(#)SIB/ws/code/sib.comms.server.impl/src/com/ibm/ws/sib/comms/server/clientsupport/CATAsynchReadAheadReader.java, SIB.comms, WASX.SIB, aa1225.01 1.38");
    }

    /** The owning instance of the CAT consumer session */
    final CATProxyConsumer consumerSession; // d172528

    /** The main consumer on whose behalf we reading ahead */
    final CATMainConsumer mainConsumer;

    /**
     * Constructor.
     * 
     * @param consumerSession
     * @param mainConsumer
     */
    public CATAsynchReadAheadReader(CATProxyConsumer consumerSession, CATMainConsumer mainConsumer) {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
            SibTr.entry(this, tc, "<init>", new Object[] { consumerSession, mainConsumer });
        this.consumerSession = consumerSession;
        this.mainConsumer = mainConsumer;
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
            SibTr.exit(tc, "<init>");
    }

    /**
     * This method is called by the core API when a message is available.
     * Here, we must send the message back to the client and keep track of
     * how many bytes we have sent.
     * <p>
     * Pacing works by the read ahead consumer on the server (us) sending
     * messages to the server. We will send up to x bytes of messages, as
     * requested by the client. When we have sent enough messages, we will
     * stop the consumer, to prevent any more messages being sent.
     * <p>
     * The client application will then consume the messages that it has
     * been delivered. When the amount of bytes left to consume falls below
     * a threshold value, the client will request more messages and will
     * inform us how much has been consumed, and the total bytes they are
     * prepared to cope with. We then will resend enough messages to keep
     * the client topped up.
     * 
     * @param vEnum
     */
    public void consumeMessages(LockedMessageEnumeration vEnum) {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
            SibTr.entry(this, tc, "consumeMessages", vEnum);

        if (mainConsumer.getConversation().getConnectionReference().isClosed()) {
            // stop consumer to avoid infinite loop     
            if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
                SibTr.debug(this, tc, "The connection is closed so we shouldn't consume anymore messages. Consumer Session should be closed soon");
            stopConsumer();
        } else {
            String xctErrStr = null;

            try {
                // Get the next message in the vEnum
                if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
                    SibTr.debug(this, tc, "Getting next locked message");

                SIBusMessage sibMessage = vEnum.nextLocked(); // d172528

                if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
                    SibTr.debug(this, tc, "Received message", sibMessage);

                // Send the message
                int msgLen = consumerSession.sendMessage(sibMessage); // d172528    // D221806

                // If the messages are unrecoverable then we can optimise this by deleting
                // the message now
                if (!CommsUtils.isRecoverable(sibMessage, consumerSession.getUnrecoverableReliability())) {
                    if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
                        SibTr.debug(this, tc, "Deleting the message");
                    vEnum.deleteCurrent(null);
                }

                consumerSession.setLowestPriority(JFapChannelConstants.getJFAPPriority(sibMessage.getPriority())); // d172528

                // Start D221806
                // Ensure we take a lock on the consumer session so that the request for more messages
                // doesn't corrupt the counters.
                synchronized (consumerSession) {
                    int oldSentBytes = consumerSession.getSentBytes();
                    int newSentBytes = oldSentBytes + msgLen;
                    consumerSession.setSentBytes(newSentBytes);

                    if (msgLen == 0 || newSentBytes >= consumerSession.getRequestedBytes()) {
                        // in addition to the pacing control, we must avoid an infinite loop
                        // attempting to send messages that don't get through.  If msgLen
                        // is 0 then no message was sent, and we must stop the consumer
                        // and crucially, give up the asynchconsumerbusylock so the consumer
                        // can be closed if need be.
                        if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
                            SibTr.debug(this, tc, "Stopping consumer session (sent bytes >= requested bytes || msgLen = 0)");
                        stopConsumer();
                    }
                }
                // End D221806
            }
            // start d172528
            catch (Throwable e) {
                //No FFDC code needed
                //Only FFDC if we haven't received a meTerminated event OR if e isn't a SIException
                final ConversationState convState = (ConversationState) consumerSession.getConversation().getAttachment();

                if (!(e instanceof SIException) || !convState.hasMETerminated()) {
                    FFDCFilter.processException(e,
                                                CLASS_NAME + ".consumeMessages",
                                                CommsConstants.CATASYNCHRHREADER_CONSUME_MSGS_01,
                                                this);
                }

                if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
                    SibTr.debug(tc, e.getMessage(), e);

                StaticCATHelper.sendAsyncExceptionToClient(e,
                                                           CommsConstants.CATASYNCHRHREADER_CONSUME_MSGS_01, // d186970
                                                           consumerSession.getClientSessionId(),
                                                           consumerSession.getConversation(), 0);
            } // end d172528

        }

        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
            SibTr.exit(this, tc, "consumeMessages");
    }

    /**
     * Safely stop the consumer
     * 
     * @param Message to be traced for stop reason.
     */
    public void stopConsumer() {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
            SibTr.entry(this, tc, "stopConsumer");
        try {
            // lock the consumerSession to ensure visibility of update to started.
            synchronized (consumerSession) {
                consumerSession.getConsumerSession().stop();
                consumerSession.started = false;
            }
        } catch (Throwable t) {
            FFDCFilter.processException(t,
                                        CLASS_NAME + ".consumeMessages",
                                        CommsConstants.CATASYNCHRHREADER_CONSUME_MSGS_02,
                                        this);
            if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
                SibTr.debug(this, tc, "Unable to stop consumer session due to Throwable: " + t);
        }

        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
            SibTr.exit(this, tc, "stopConsumer");
    }
}
