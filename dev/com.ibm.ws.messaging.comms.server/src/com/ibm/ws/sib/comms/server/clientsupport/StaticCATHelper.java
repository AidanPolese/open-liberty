/*
 * @start_prolog@
 * Version: @(#) 1.62 SIB/ws/code/sib.comms.server.impl/src/com/ibm/ws/sib/comms/server/clientsupport/StaticCATHelper.java, SIB.comms, WASX.SIB, aa1225.01 08/01/28 09:28:42 [7/2/12 05:59:00]
 * ============================================================================
 * IBM Confidential OCO Source Materials
 *
 * 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70 Copyright IBM Corp. 2004, 2008
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
 * Creation        030604 clarkep  Original
 * f169884         030620 mattheg  Tidy, add SibTr and fix EXCEPTION_STACK function
 * d170639         030627 mattheg  NLS all the messages
 * f171046         030703 mattheg  Improve the server side logging and error handling
 * f172297         030724 mattheg  Centralise the methods for sending responses back to the client
 * F174602         030820 prestona Switch to using SICommsException
 * d172528         030905 mattheg  Enable async exception sending
 * d175811         030919 mattheg  Error handling on JFAP exception
 * f181195         031029 mattheg  Client XA support
 * f173765.7.2     021031 mattheg  Remove setQosReliability method as it is no longer needed
 * F183828         031204 prestona Update CF + TCP prereqs to MS 5.1 level
 * f179339.4       031222 mattheg  Forward and reverse routing support
 * d186323         040105 mattheg  Fix NPE when sending session create response
 * d186970         040116 mattheg  Overhaul the way we send exceptions to client
 * F188491         030128 prestona Migrate to M6 CF + TCP Channel
 * D190797         040216 prestona Unknown exception thrown by core API
 * d175222         040219 mattheg  Ensure SICommsException is reported correctly and not sent to client
 * d187252         040302 mattheg  Ensure session destination information is only returned if it changes
 * d193377         040305 mattheg  Fix reason code buffer size
 * d192293         040308 mattheg  NLS file changes
 * f192759.2       040311 mattheg  Add method getSIDestinationAddress
 * d194950         040318 mattheg  Ensure exception message sends down exception type too
 * f196076         040326 mattheg  Multicast support -- phase 2
 * f199593         040422 mattheg  Complete M7.5 Core SPI updates
 * d200152         040426 mattheg  Ensure consumer response sends data in the correct order
 * f200337         040427 mattheg  Extra trace to getSIDestinationAddress
 * f184312.4.2     040503 mattheg  Ensure we can distinguish a null destination address
 * D202625         040511 mattheg  Fix handling of null destination addresses
 * D208700         040611 mattheg  Capture the bus name with the destination address
 * F195720.3       040616 prestona WAS Request Metrics in Jetstream
 * F207007.2       040617 mattheg  getSelectionCriteria() method
 * F195720.3.1     040629 prestona WAS Request Metrics in Jetstream
 * D215166         040709 mattheg  Bus name is not returned on session create
 * D217372         040719 mattheg  Move JFap constants -> JFapChannelConstants (not change-flagged)
 * F201972.2       040727 mattheg  Core SPI Exceptions rework (not change flagged)
 * D221503         040804 mattheg  Remove possibility for data corruption on getSIDestinationAddress
 * D221834         040805 mattheg  Flow linked exceptions to the client
 * D199177         040816 mattheg  JavaDoc
 * D210259.1       040819 mattheg  Move deserialization methods to CommsUtils
 * D236330         041004 mattheg  Multicast server fixes
 * D225856         041006 mattheg  Update FFDC class name (not change flagged)
 * D267291         050413 mattheg  Ensure CF_RELIABLE_MULTICAST bit is set if appropriate
 * D301654         051102 mattheg  Add handling for command invocation failed exception
 * D365952         060523 mattheg  Add support for SIMessageNotLockedException
 * D377648         060719 mattheg  Use CommsByteBuffer
 * D378229         060808 prestona Avoid synchronizing on ME-ME send()
 * D384259         060815 prestona Remove multicast support
 * SIB0121a.com.1  070706 prestona Propagate exception reason and inserts.
 * D441183         072307 mleming  Don't FFDC when calling terminated ME
 * 471664          071003 vaughton Findbugs tidy up
 * 494335          080128 mleming  Flow localOnly information on the wire
 * 99984           070813 romehla1 Avoid NPE exception when jmsServer feature is removed
 * ============================================================================
 */
package com.ibm.ws.sib.comms.server.clientsupport;

import com.ibm.websphere.ras.TraceComponent;
import com.ibm.websphere.sib.SIDestinationAddress;
import com.ibm.websphere.sib.exception.SIException;
import com.ibm.ws.ffdc.FFDCFilter;
import com.ibm.ws.jfap.inbound.channel.CommsServerServiceFacade;
import com.ibm.ws.sib.comms.CommsConstants;
import com.ibm.ws.sib.comms.common.CommsByteBuffer;
import com.ibm.ws.sib.comms.common.CommsByteBufferPool;
import com.ibm.ws.sib.comms.server.ConversationState;
import com.ibm.ws.sib.jfapchannel.Conversation;
import com.ibm.ws.sib.jfapchannel.Conversation.ThrottlingPolicy;
import com.ibm.ws.sib.jfapchannel.JFapChannelConstants;
import com.ibm.ws.sib.mfp.JsDestinationAddress;
import com.ibm.ws.sib.utils.ras.SibTr;
import com.ibm.wsspi.sib.core.ConsumerSession;
import com.ibm.wsspi.sib.core.DestinationSession;

/**
 * This class has some static helper methods for the server code.
 * 
 * @author Gareth Matthews
 */
public class StaticCATHelper {
    /** Class name for FFDC's */
    private static String CLASS_NAME = StaticCATHelper.class.getName();

    /** Our buffer pool manager */
    private static CommsByteBufferPool poolManager = CommsByteBufferPool.getInstance();

    /** Registers our trace component */
    private static final TraceComponent tc = SibTr.register(StaticCATHelper.class,
                                                            CommsConstants.MSG_GROUP,
                                                            CommsConstants.MSG_BUNDLE);

    /** Log class info on static load */
    static {
        if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
            SibTr.debug(tc,
                        "Source info: @(#)SIB/ws/code/sib.comms.server.impl/src/com/ibm/ws/sib/comms/server/clientsupport/StaticCATHelper.java, SIB.comms, WASX.SIB, aa1225.01 1.62");
    }

    // *******************************************************************************************
    // *                         Exeption Handling Methods                                       *
    // *******************************************************************************************

    // Exception handling is done by using 1 public methods and 3 private helper methods and works
    // as follows:
    //
    // 1 - Comms code will call sendExceptionToClient()
    // 2 - This code create the buffer and send the data. The buffer is created by the method
    //     createExceptionBuffer().
    // 3 - The exception Id is determined by the getExceptionId() method and then the addException()
    //     will add the parts of the exception (the message etc) to the buffer. The
    //     createExceptionBuffer() method will also traverse down all the linked exceptions.

    /**
     * Sends an exception response back to the client.
     * 
     * @param throwable The exception to send back
     * @param probeId The probe ID of any corresponding FFDC record.
     * @param conversation The conversaton to use.
     * @param requestNumber The request number to reply with.
     */
    public static void sendExceptionToClient(Throwable throwable, String probeId,
                                             Conversation conversation, int requestNumber) {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
            SibTr.entry(tc, "sendExceptionToClient",
                        new Object[]
                        {
                         throwable,
                         probeId,
                         conversation,
                         requestNumber
                        });

        CommsByteBuffer buffer = poolManager.allocate();
        buffer.putException(throwable, probeId, conversation);
        // defect 99984 checking whether jmsServer feature is intact or its removed 
        if (CommsServerServiceFacade.getJsAdminService() != null)
        {
            try {
                conversation.send(buffer,
                                  JFapChannelConstants.SEG_EXCEPTION,
                                  requestNumber,
                                  JFapChannelConstants.PRIORITY_MEDIUM,
                                  true,
                                  ThrottlingPolicy.BLOCK_THREAD,
                                  null);
            } catch (SIException c) {
                FFDCFilter.processException(c, CLASS_NAME + ".sendExceptionToClient",
                                            CommsConstants.STATICCATHELPER_SEND_EXCEP_01);

                SibTr.error(tc, "COMMUNICATION_ERROR_SICO2023", c);
            }
        }
        else
        {
            if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
                SibTr.entry(tc, "conversation send is not being called as jmsadminService is null");
        }

        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
            SibTr.exit(tc, "sendExceptionToClient");
    }

    /**
     * This method is used to flow a message down to the client that will get picked up
     * and delivered to the asynchronousException method of any listeners that the client
     * has registered.
     * 
     * @param throwable
     * @param probeId
     * @param clientSessionId
     * @param conversation
     * @param requestNumber
     */
    public static void sendAsyncExceptionToClient(Throwable throwable,
                                                  String probeId, short clientSessionId,
                                                  Conversation conversation, int requestNumber) {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
            SibTr.entry(tc, "sendAsyncExceptionToClient",
                        new Object[]
                        {
                         throwable,
                         probeId,
                         "" + clientSessionId,
                         conversation,
                         "" + requestNumber
                        });

        // BIT16 ConnectionObjectId
        // BIT16 Event Id
        // BIT16 ConsumerSessionId
        // Exception...
        CommsByteBuffer buffer = poolManager.allocate();
        buffer.putShort(0); // We do not need the connection object ID on the client
        buffer.putShort(CommsConstants.EVENTID_ASYNC_EXCEPTION); // Async exception
        buffer.putShort(clientSessionId);
        buffer.putException(throwable, probeId, conversation);

        try {
            conversation.send(buffer,
                              JFapChannelConstants.SEG_EVENT_OCCURRED,
                              requestNumber,
                              JFapChannelConstants.PRIORITY_MEDIUM,
                              true,
                              ThrottlingPolicy.BLOCK_THREAD,
                              null);
        } catch (SIException c) {
            FFDCFilter.processException(c, CLASS_NAME + ".sendAsyncExceptionToClient",
                                        CommsConstants.STATICCATHELPER_SEND_ASEXCEP_01);

            SibTr.error(tc, "COMMUNICATION_ERROR_SICO2023", c);
        }

        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
            SibTr.exit(tc, "sendAsyncExceptionToClient");
    }

    // *******************************************************************************************
    // *                         Session Response Methods                                        *
    // *******************************************************************************************

    /**
     * Because of the larger amount of data needed to be sent back on the response to a session
     * create, I have split this into a seperate method so that we are not repeating code
     * all over the place.
     * 
     * @param segmentType The segment type to send the response with.
     * @param requestNumber The request number we are replying to.
     * @param conversation The conversation to send the reply on.
     * @param sessionId The session id of the session we just created.
     * @param session The session.
     * @param originalDestinationAddr The original address that was passed in. We will only send back
     *            a destination address if the actual one is different.
     */
    public static void sendSessionCreateResponse(int segmentType, int requestNumber,
                                                 Conversation conversation, short sessionId,
                                                 DestinationSession session,
                                                 SIDestinationAddress originalDestinationAddr) {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
            SibTr.entry(tc, "sendSessionCreateResponse");

        CommsByteBuffer buffer = poolManager.allocate();

        // Add the Message processor session id if we are sending back a consumer response
        if (segmentType == JFapChannelConstants.SEG_CREATE_CONS_FOR_DURABLE_SUB_R ||
            segmentType == JFapChannelConstants.SEG_CREATE_CONSUMER_SESS_R) {
            long id = 0;
            try {
                id = ((ConsumerSession) session).getId();
            } catch (SIException e) {
                //No FFDC code needed
                //Only FFDC if we haven't received a meTerminated event.
                if (!((ConversationState) conversation.getAttachment()).hasMETerminated()) {
                    FFDCFilter.processException(e, CLASS_NAME + ".sendSessionCreateResponse",
                                                CommsConstants.STATICCATHELPER_SENDSESSRESPONSE_02);
                }

                // Not a lot we can do here - so just debug the error
                if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
                    SibTr.debug(tc, "Unable to get session id", e);
            }
            buffer.putLong(id);
        }

        if (segmentType == JFapChannelConstants.SEG_CREATE_CONSUMER_SESS_R) {
            buffer.putShort(CommsConstants.CF_UNICAST);
        }

        buffer.putShort(sessionId);

        // Now get the destination address from the session so we can get sizes
        JsDestinationAddress destAddress = (JsDestinationAddress) session.getDestinationAddress();

        // We should only send back the destination address if it is different from the original.
        // To do this, we can do a simple compare on their toString() methods.
        if (originalDestinationAddr == null ||
            (!originalDestinationAddr.toString().equals(destAddress.toString()))) {
            if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
                SibTr.debug(tc, "Destination address is different: Orig, New",
                            new Object[]
                            {
                             originalDestinationAddr,
                             destAddress
                            });

            buffer.putSIDestinationAddress(destAddress, conversation.getHandshakeProperties().getFapLevel());
        }

        try {
            // Send the response to the client.
            conversation.send(buffer,
                              segmentType,
                              requestNumber,
                              JFapChannelConstants.PRIORITY_MEDIUM,
                              true,
                              ThrottlingPolicy.BLOCK_THREAD,
                              null);
        } catch (SIException e) {
            FFDCFilter.processException(e,
                                        CLASS_NAME + ".sendSessionCreateResponse",
                                        CommsConstants.STATICCATHELPER_SENDSESSRESPONSE_01);

            if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
                SibTr.debug(tc, e.getMessage(), e);

            SibTr.error(tc, "COMMUNICATION_ERROR_SICO2023", e);
        }

        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
            SibTr.exit(tc, "sendSessionCreateResponse");
    }

}
