/*
 * @start_prolog@
 * Version: @(#) 1.89.1.1 SIB/ws/code/sib.comms.server.impl/src/com/ibm/ws/sib/comms/server/clientsupport/StaticCATConsumer.java, SIB.comms, WASX.SIB, aa1225.01 12/05/08 01:35:39 [7/2/12 05:59:00]
 * ============================================================================
 * IBM Confidential OCO Source Materials
 *
 * 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70  Copyright IBM Corp. 2004, 2014
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
 * mattheg         030630 mattheg  NLS all the messages (and convert to SibTr)
 * mattheg         030704 mattheg  Ensure the discriminator is retrieved correctly
 * f171177         030707 schmittm Add asynchronous readAhead support
 * f169897.2       030708 mattheg  Convert to Core API 0.6
 * f171400         030710 mattheg  Implement Core API 0.6
 * f172297         030725 mattheg  Complete Core API 0.6 implementation
 * f173559         030808 mattheg  Add checks for invalid flags
 * F174602         030820 prestona Switch to using SICommsException
 * f174317         030827 mattheg  Add support for local transactions
 * D175672         030902 Niall    Fix Memory Leaks
 * D177227         030919 prestona Client doesn't tollerate nolocal flag
 * f173765.2       030926 mattheg  Core API M4 update
 * f177889         030929 mattheg  Core API M4 completion
 * d179459         031010 mattheg  Ensure flags are passed and checked correctly and fix spellin'
 * F183828         031204 prestona Update CF + TCP prereqs to MS 5.1 level
 * f179519.1       031209 mattheg  Add SIDestinationWrongTypeException handling
 * f181007         031211 mattheg  Add boolean 'exchange' flag
 * f179339.4       031222 mattheg  Forward and reverse routing support
 * d186970         040116 mattheg  Overhaul the way we send exceptions to client
 * f187521.2.1     040126 mattheg  Unrecoverable reliability -- part 2
 * F188491         030128 prestona Migrate to M6 CF + TCP Channel
 * f176658.3.6.2   040211 mattheg  Add createConsumerSession() method that takes SIDestinationAddress
 * f191114         040218 mattheg  Multicast support
 * d187252         040302 mattheg  Ensure session destination information is only returned if it changes
 * f196076         040407 mattheg  Multicast support -- phase 2
 * f195748.2       040415 mattheg  Core SPI 7.5 updates
 * f199593         040422 mattheg  Complete M7.5 Core SPI updates
 * d200152         040426 mattheg  Ensure correct exceptions for bifurcated sessions
 * f193585.3.2     040503 mattheg  Remove destination filter
 * f176658.4.2.2   040504 mattheg  deliverImmediately flag change
 * F195720.3       040616 prestona WAS Request Metrics in Jetstream
 * F207007.2       040617 mattheg  Core SPI Update of message selector parameters
 * F195720.3.1     040629 prestona WAS Request Metrics in Jetstream
 * D217372         040719 mattheg  Move JFap constants -> JFapChannelConstants (not change-flagged)
 * F201972.2       040727 mattheg  Core SPI Exceptions rework (not change flagged)
 * D199177         040816 mattheg  JavaDoc
 * D210259.1       040819 mattheg  Move deserialization methods to CommsUtils
 * F219476.2       040906 prestona Z3 Core SPI changes
 * D235891         040930 mattheg  Runtime property standards
 * D237047         041005 mattheg  Handle incorrect flags correctly
 * D225856         041006 mattheg  Update FFDC class name (not change flagged)
 * F247845         050201 mattheg  Multicast support
 * D254870         050214 mattheg  Optimize connection close
 * D237663         050422 mattheg  Inline async callbacks on start()
 * D307265         050922 prestona Support for optimized transactions
 * D342106         060130 mattheg  Don't FFDC on SINotAuthorisedException
 * D347591         060217 mattheg  Add support for exchanged starts
 * D350111.1       060302 mattheg  Use send listener for start()
 * D351339.comms   060302 mattheg  Implement ignoreInitialIndoubts createConsumerSession() method
 * D357343         060323 prestona Build break caused by clash between 351339 and 354565
 * D377648         060719 mattheg  Use CommsByteBuffer
 * D384259         060815 prestona Remove multicast support
 * SIB0048b.com.1  060901 mattheg  Use different byte buffer impl on server
 * D441183         072307 mleming  Don't FFDC when calling terminated ME
 * SIB0113.comms.1 070920 vaughton Core SPI changes
 * 471642          071016 vaughton Unable to deregister stoppable async consumer
 * SIB0163.comms.1 071022 vaughton XD extensions
 * SIB0163.comms.2 071029 vaughton XD extensions (Part 2)
 * 494335          080128 mleming  Flow localOnly information on the wire
 * SIB0115.comms.2 080131 vaughton Update registerStoppableAsynchConsumerCallback
 * 498362          080304 vaughton Check FAP level before hauling out a properties map
 * PK73713         161008 ajw      Allow messageset to be unlocked and not increased lock count
 * 568951          081215 mleming  Code review adjustments to PK73713
 * PK83641         090331 ajw      reset LinkLevelState when returning from pool;
 * 592503          090722 mleming  ObjectStoreFullException -> ConversationStateFullException
 * F013661         200412 chetbhat unlockAll(incrementUnlockCount) support
 * ============================================================================
 */
package com.ibm.ws.sib.comms.server.clientsupport;

import java.util.Map;

import com.ibm.ejs.ras.TraceNLS;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.websphere.sib.Reliability;
import com.ibm.websphere.sib.SIDestinationAddress;
import com.ibm.websphere.sib.exception.SIErrorException;
import com.ibm.websphere.sib.exception.SIException;
import com.ibm.websphere.sib.exception.SIResourceException;
import com.ibm.ws.ffdc.FFDCFilter;
import com.ibm.ws.sib.comms.CommsConstants;
import com.ibm.ws.sib.comms.common.CommsByteBuffer;
import com.ibm.ws.sib.comms.common.CommsUtils;
import com.ibm.ws.sib.comms.server.CommsServerByteBuffer;
import com.ibm.ws.sib.comms.server.ConversationState;
import com.ibm.ws.sib.comms.server.ConversationStateFullException;
import com.ibm.ws.sib.comms.server.ServerLinkLevelState;
import com.ibm.ws.sib.jfapchannel.Conversation;
import com.ibm.ws.sib.jfapchannel.HandshakeProperties;
import com.ibm.ws.sib.jfapchannel.JFapChannelConstants;
import com.ibm.ws.sib.utils.ras.SibTr;
import com.ibm.wsspi.sib.core.BifurcatedConsumerSession;
import com.ibm.wsspi.sib.core.ConsumerSession;
import com.ibm.wsspi.sib.core.DestinationType;
import com.ibm.wsspi.sib.core.OrderingContext;
import com.ibm.wsspi.sib.core.SICoreConnection;
import com.ibm.wsspi.sib.core.SIMessageHandle;
import com.ibm.wsspi.sib.core.SelectionCriteria;
import com.ibm.wsspi.sib.core.exception.SINotAuthorizedException;

/**
 * This class takes care of all operations pertaining to consumers
 * that are received by the server.
 */
public class StaticCATConsumer
{
    /** Class name for FFDC's */
    private static String CLASS_NAME = StaticCATConsumer.class.getName();

    /** Trace */
    private static final TraceComponent tc = SibTr.register(StaticCATConsumer.class,
                                                            CommsConstants.MSG_GROUP,
                                                            CommsConstants.MSG_BUNDLE);
    /** The NLS reference */
    private static final TraceNLS nls = TraceNLS.getTraceNLS(CommsConstants.MSG_BUNDLE);

    static
    {
        if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
            SibTr.debug(tc,
                        "Source info: @(#)SIB/ws/code/sib.comms.server.impl/src/com/ibm/ws/sib/comms/server/clientsupport/StaticCATConsumer.java, SIB.comms, WASX.SIB, aa1225.01 1.89.1.1");
    }

    /**
     * Create a Synchronous Consumer Session, assign the object to the conversation
     * state, and return the ID to client.
     * 
     * Fields:
     * 
     * BIT16 SIConnectionObjectId
     * BIT16 ClientSessionID
     * BIT16 ConsumerFlags
     * BIT16 Reliability
     * BIT32 RequestedBytes
     * BIT16 DestinationType
     * BIT16 UnrecoverableReliability
     * 
     * BIT16 Uuid Length
     * BYTE[] Uuid
     * BIT16 DestinationNameLength
     * BYTE[] DestinationName
     * BIT16 SelectorDomain
     * BIT16 DiscriminatorLength
     * BYTE[] Discriminator
     * BIT16 SelectorLength
     * BYTE[] Selector
     * 
     * @param request
     * @param conversation
     * @param requestNumber
     * @param allocatedFromBufferPool
     * @param partOfExchange
     */
    static void rcvCreateConsumerSess(CommsByteBuffer request, Conversation conversation,
                                      int requestNumber, boolean allocatedFromBufferPool,
                                      boolean partOfExchange)
    {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
            SibTr.entry(tc, "rcvCreateConsumerSess",
                        new Object[]
                        {
                         request,
                         conversation,
                         "" + requestNumber,
                         "" + allocatedFromBufferPool
                        });
        String subscriptionName = null;
        ConversationState convState = (ConversationState) conversation.getAttachment();

        short connectionObjectID = request.getShort(); // BIT16 ConnectionObjectId
        SICoreConnection connection =
                        ((CATConnection) convState.getObject(connectionObjectID)).getSICoreConnection();

        if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
            SibTr.debug(tc, "SICoreConnection Id:", connectionObjectID);

        short clientSessionId = request.getShort(); // BIT16 ClientSessionId
        short consumerFlags = request.getShort(); // BIT16 Consumer Flags
        short reliabilityShort = request.getShort(); // BIT16 Reliability
        int requestedBytes = request.getInt(); // BIT32 Requested Bytes
        short destinationTypeShort = request.getShort();// BIT16 Destination type
        short unrecovShort = request.getShort(); // BIT16 Reliability

        if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
        {
            SibTr.debug(tc, "clientSessionId", clientSessionId);
            SibTr.debug(tc, "consumerFlags", consumerFlags);
            SibTr.debug(tc, "reliability", reliabilityShort);
            SibTr.debug(tc, "requestedBytes", requestedBytes);
            SibTr.debug(tc, "destinationType", destinationTypeShort);
            SibTr.debug(tc, "unrecovReliability", unrecovShort);
        }

        try
        {
            /**************************************************************/
            /* Consumer flags */
            /**************************************************************/
            // Check if the flags are valid
            if (consumerFlags > CommsConstants.CF_MAX_VALID)
            {
                // The flags appear to be invalid
                if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
                    SibTr.debug(tc, "Consumer flags (" + consumerFlags + ") > " + CommsConstants.CF_MAX_VALID); //SIB0113.comms.1

                SIErrorException e = new SIErrorException(
                                nls.getFormattedMessage("INVALID_PROP_SICO8013", new Object[] { "" + consumerFlags }, null)
                                );

                FFDCFilter.processException(e, CLASS_NAME + ".rcvCreateConsumerSess",
                                            CommsConstants.STATICCATCONSUMER_CREATE_04);

                throw e;
            }

            boolean readAheadPermitted = (consumerFlags & CommsConstants.CF_READAHEAD) != 0;
            boolean noLocal = (consumerFlags & CommsConstants.CF_NO_LOCAL) != 0;
            boolean unicastRequested = (consumerFlags & CommsConstants.CF_UNICAST) != 0;
            boolean bifurcatable = (consumerFlags & CommsConstants.CF_BIFURCATABLE) != 0;
            boolean ignoreIndoubts = (consumerFlags & CommsConstants.CF_IGNORE_INITIAL_INDOUBTS) != 0;
            boolean allowMessageGathering = (consumerFlags & CommsConstants.CF_ALLOW_GATHERING) != 0; //SIB0113.comms.1
            // To enable JMS 2.0
            boolean supportsMultipleConsumers = (consumerFlags & CommsConstants.CF_MULTI_CONSUMER) != 0;

            // If a client sends this flow and is speaking < FAP5, they will not have specified the
            // IgnoreInitialIndoubts flag. The default for this flag however is true. As such, if we
            // are FAP5 or above, take what the client specified, otherwise, set the flag to true.
            final HandshakeProperties handshakeProps = conversation.getHandshakeProperties();
            final short clientFapLevel = handshakeProps.getFapLevel();

            if (clientFapLevel < JFapChannelConstants.FAP_VERSION_5)
            {
                if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
                    SibTr.debug(tc, "FAP Version is <5 - setting ignoreInitialIndoubts to true");
                ignoreIndoubts = true;
            }

            // The default value for allowMessageGathering is false so no special handling SIB0113.comms.1
            // is required to set the default value for <FAP9 clients                      SIB0113.comms.1

            if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
                SibTr.debug(tc, "Flags:",
                            new Object[]
                            {
                             "No Local: " + noLocal,
                             "Bifurcatable: " + bifurcatable,
                             "Ignore Initial Indoubts: " + ignoreIndoubts,
                             "Allow Message Gathering: " + allowMessageGathering //SIB0113.comms.1
                            });

            if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
                SibTr.debug(tc, "Selection criteria:",
                            new Object[]
                            {
                             "Read ahead requested    : " + readAheadPermitted,
                             "Unicast requested       : " + unicastRequested,
                             "NoLocal                 : " + noLocal
                            });

            /**************************************************************/
            /* Reliability */
            /**************************************************************/
            Reliability reliability = null;
            if (reliabilityShort != -1)
            {
                reliability = Reliability.getReliability(reliabilityShort);
            }

            /**************************************************************/
            /* Destination type */
            /**************************************************************/
            DestinationType destType = null;
            if (destinationTypeShort != CommsConstants.NO_DEST_TYPE)
            {
                destType = DestinationType.getDestinationType(destinationTypeShort);
            }

            /**************************************************************/
            /* Unrecoverable reliability */
            /**************************************************************/
            // Note this is never passed as null - the client will always convert
            Reliability unrecoverableReliability = Reliability.getReliability(unrecovShort);

            /**************************************************************/
            /* Destination information */
            /**************************************************************/
            SIDestinationAddress destAddr = request.getSIDestinationAddress(clientFapLevel);

            /**************************************************************/
            /* SupscriptionName */
            // To enable shared non durable subscribers
            /**************************************************************/
            if (supportsMultipleConsumers && (clientFapLevel >= JFapChannelConstants.FAP_VERSION_20))
                subscriptionName = request.getString();
            /* Selection criteria */
            /**************************************************************/
            SelectionCriteria criteria = request.getSelectionCriteria();

            /**************************************************************/
            /* Selection criteria */
            /**************************************************************/
            String alternateUser = request.getString();

            /**************************************************************/
            /* Message Control Properties */
            /**************************************************************/
            Map<String, String> messageControlProperties = null;
            if (clientFapLevel >= JFapChannelConstants.FAP_VERSION_9) {
                messageControlProperties = request.getMap(); //SIB0163.comms.1
            }

            CATMainConsumer mainConsumer = null;
            short consSessionObjectID = 0;

            ConsumerSession consumerSession = null;
            // To enable shared non durable subscribers 
            if (subscriptionName != null)
            {
                consumerSession = connection.createSharedConsumerSession(subscriptionName, destAddr, destType, criteria, reliability, readAheadPermitted,
                                                                         supportsMultipleConsumers,
                                                                         readAheadPermitted,
                                                                         unrecoverableReliability, bifurcatable, alternateUser, ignoreIndoubts, allowMessageGathering,
                                                                         messageControlProperties);
            }
            else
            {
                consumerSession = connection.createConsumerSession(destAddr,
                                                                   destType,
                                                                   criteria,
                                                                   reliability,
                                                                   readAheadPermitted,
                                                                   noLocal,
                                                                   unrecoverableReliability,
                                                                   bifurcatable,
                                                                   alternateUser,
                                                                   ignoreIndoubts,
                                                                   allowMessageGathering,
                                                                   messageControlProperties);
            }

            mainConsumer = new CATMainConsumer(conversation,
                            clientSessionId,
                            consumerSession,
                            readAheadPermitted,
                            noLocal,
                            unrecoverableReliability);

            consSessionObjectID = (short) convState.addObject(mainConsumer);
            mainConsumer.setConsumerSessionId(consSessionObjectID);

            if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
                SibTr.debug(tc, "Consumer Session Id:", "" + consSessionObjectID);

            StaticCATHelper.sendSessionCreateResponse(JFapChannelConstants.SEG_CREATE_CONSUMER_SESS_R,
                                                      requestNumber,
                                                      conversation,
                                                      consSessionObjectID,
                                                      consumerSession,
                                                      destAddr);

            // If we are read ahead then start things off by registering the async consumer
            // and start sending messages to the client.
            if (readAheadPermitted)
            {
                try
                {
                    mainConsumer.setRequestedBytes(requestedBytes);
                    mainConsumer.setAsynchConsumerCallback(requestNumber,
                                                           0,
                                                           0,
                                                           1,
                                                           null);
                    mainConsumer.start(requestNumber, false, false, null);
                } catch (RuntimeException e)
                {
                    // If we get a runtime exception here then our setAsync or start
                    // calls have failed.
                    FFDCFilter.processException(e,
                                                CLASS_NAME + ".rcvCreateConsumerSess",
                                                CommsConstants.STATICCATCONSUMER_CREATE_01);

                    if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
                    {
                        SibTr.debug(tc, "Unable to create readahead async consumer");
                        SibTr.exception(tc, (Exception) e.getCause());
                    }

                    // Now throw a SICoreException to inform the client
                    throw new SIResourceException(e.getMessage(), e);
                }
            }
        } catch (ConversationStateFullException e)
        {
            FFDCFilter.processException(e,
                                        CLASS_NAME + ".rcvCreateConsumerSess",
                                        CommsConstants.STATICCATCONSUMER_CREATE_02); //d170639

            if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
                SibTr.debug(tc, e.getMessage(), e);

            StaticCATHelper.sendExceptionToClient(e,
                                                  CommsConstants.STATICCATCONSUMER_CREATE_02, // d186970
                                                  conversation, requestNumber); // f172297
        } catch (SINotAuthorizedException e)
        {
            // No FFDC Code Needed
            if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
                SibTr.debug(tc, e.getMessage(), e);

            StaticCATHelper.sendExceptionToClient(e,
                                                  null,
                                                  conversation, requestNumber);
        } catch (SIException e)
        {
            //No FFDC code needed
            //Only FFDC if we haven't received a meTerminated event.
            if (!convState.hasMETerminated())
            {
                FFDCFilter.processException(e,
                                            CLASS_NAME + ".rcvCreateConsumerSess",
                                            CommsConstants.STATICCATCONSUMER_CREATE_03);
            }

            if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
                SibTr.debug(tc, e.getMessage(), e);

            StaticCATHelper.sendExceptionToClient(e,
                                                  CommsConstants.STATICCATCONSUMER_CREATE_03,
                                                  conversation, requestNumber);
        }

        request.release(allocatedFromBufferPool);

        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
            SibTr.exit(tc, "rcvCreateConsumerSess");
    }

    /**
     * Start the Synchronous Consumer Session provided by the client.
     * 
     * Fields:
     * 
     * BIT16 ConnectionObjectId
     * BIT16 SyncConsumerSessionId
     * 
     * @param request
     * @param conversation
     * @param requestNumber
     * @param allocatedFromBufferPool
     * @param partOfExchange
     * @param restart indicates (re)start rather than start
     */
    static void rcvStartSess(CommsByteBuffer request, Conversation conversation, int requestNumber,
                             boolean allocatedFromBufferPool, boolean partOfExchange, boolean restart) //471642
    {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
            SibTr.entry(tc, "rcvStartSess",
                        new Object[]
                        {
                         request,
                         conversation,
                         requestNumber,
                         allocatedFromBufferPool,
                         restart //471642
                        });

        short connectionObjectID = request.getShort(); // BIT16 ConnectionObjectId
        short consumerObjectID = request.getShort(); // BIT16 SyncConsumerSessionId

        if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
        {
            SibTr.debug(tc, "connectionObjectID", connectionObjectID);
            SibTr.debug(tc, "consumerObjectID", consumerObjectID);
        }

        CATMainConsumer mainConsumer =
                        ((CATMainConsumer) ((ConversationState) conversation.getAttachment()).getObject(consumerObjectID));

        mainConsumer.start(requestNumber, true, partOfExchange, null, restart); //471642

        request.release(allocatedFromBufferPool);

        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
            SibTr.exit(tc, "rcvStartSess");
    }

    /**
     * Stop the Synchronous Consumer Session provided by the client.
     * 
     * Fields:
     * 
     * BIT16 ConnectionObjectId
     * BIT16 SyncConsumerSessionId
     * 
     * Note: The client reply is done by the CAT consumer instance
     * 
     * @param request
     * @param conversation
     * @param requestNumber
     * @param allocatedFromBufferPool
     * @param partOfExchange
     */
    static void rcvStopSess(CommsByteBuffer request, Conversation conversation, int requestNumber,
                            boolean allocatedFromBufferPool, boolean partOfExchange)
    {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
            SibTr.entry(tc, "rcvStopSess",
                        new Object[]
                        {
                         request,
                         conversation,
                         "" + requestNumber,
                         "" + allocatedFromBufferPool
                        });

        short connectionObjectID = request.getShort(); // BIT16 ConnectionObjectId
        short consumerObjectID = request.getShort(); // BIT16 SyncConsumerSessionId

        if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
        {
            SibTr.debug(tc, "connectionObjectID", connectionObjectID);
            SibTr.debug(tc, "consumerObjectID", consumerObjectID);
        }

        CATMainConsumer mainConsumer =
                        ((CATMainConsumer) ((ConversationState) conversation.getAttachment()).getObject(consumerObjectID));

        mainConsumer.stop(requestNumber);

        request.release(allocatedFromBufferPool);

        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
            SibTr.exit(tc, "rcvStopSess");
    }

    /**
     * Register an asynchronous consumer for this consumer session.
     * 
     * Fields:
     * 
     * BIT16 ConnectionObjectId
     * BIT16 SyncConsumerSessionId
     * BIT16 MessageOrderContextId
     * BIT16 ClientSessionId
     * BIT32 Max batch size
     * BIT32 Max Sequential Failures
     * 
     * @param request
     * @param conversation
     * @param requestNumber
     * @param allocatedFromBufferPool
     * @param partOfExchange
     * @param stoppable
     */
    static void rcvRegisterAsyncConsumer(CommsByteBuffer request, Conversation conversation,
                                         int requestNumber, boolean allocatedFromBufferPool,
                                         boolean partOfExchange, boolean stoppable) //SIB0115d.comms
    {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
            SibTr.entry(tc, "rcvRegisterAsyncConsumer",
                        new Object[]
                        {
                         request,
                         conversation,
                         requestNumber,
                         allocatedFromBufferPool,
                         stoppable //SIB0115d.comms
                        });

        short connectionObjectId = request.getShort(); // BIT16 ConnectionObjectId
        short consumerObjectId = request.getShort(); // BIT16 SyncConsumerSessionId
        short orderContextId = request.getShort(); // BIT16 OrderContextId
        short clientSessionId = request.getShort(); // BIT16 ClientSessionId
        int maxActiveMessages = request.getInt(); // BIT32 Max active messages
        long messageLockExpiry = request.getLong(); // BIT64 Message lock expiry
        int maxBatchSize = request.getInt(); // BIT32 Maximum batch size
        int maxSequentialFailures = 0; // BIT32 Max Sequental Failures                     SIB0115d.comms
        long hiddenMessageDelay = 0; // BIT64 hidden message delay

        // If stoppable get the maxSequentialFailures value
        if (stoppable) { //SIB0115d.comms
            maxSequentialFailures = request.getInt(); //SIB0115d.comms
            hiddenMessageDelay = request.getLong();
        } //SIB0115d.comms

        if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
        {
            SibTr.debug(tc, "connectionObjectID=" + connectionObjectId);
            SibTr.debug(tc, "consumerObjectID=" + consumerObjectId);
            SibTr.debug(tc, "orderContextID=" + orderContextId);
            SibTr.debug(tc, "clientSessionID=" + clientSessionId);
            SibTr.debug(tc, "maxActiveMessages=" + maxActiveMessages);
            SibTr.debug(tc, "messageLockExpiry=" + messageLockExpiry);
            SibTr.debug(tc, "maxBatchSize=" + maxBatchSize);
            SibTr.debug(tc, "maxSequentialFailures=" + maxSequentialFailures); //SIB0115d.comms
            SibTr.debug(tc, "hiddenMesageDelay=" + hiddenMessageDelay);
        }

        ConversationState convState = (ConversationState) conversation.getAttachment();
        CATMainConsumer mainConsumer = ((CATMainConsumer) convState.getObject(consumerObjectId));
        OrderingContext orderContext = null;

        // Get the message order context if there was one passed up
        if (orderContextId != CommsConstants.NO_ORDER_CONTEXT)
        {
            orderContext = ((CATOrderingContext) convState.getObject(orderContextId)).getOrderingContext(); // F201521
        }

        mainConsumer.setClientSessionId(clientSessionId);

        mainConsumer.setAsynchConsumerCallback(requestNumber,
                                               maxActiveMessages,
                                               messageLockExpiry,
                                               maxBatchSize,
                                               orderContext,
                                               stoppable, //SIB0115d.comms
                                               maxSequentialFailures,
                                               hiddenMessageDelay); //SIB0115d.comms

        request.release(allocatedFromBufferPool);

        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
            SibTr.exit(tc, "rcvRegisterAsyncConsumer");
    }

    /**
     * Request messages for this consumer.
     * 
     * Fields:
     * 
     * BIT16 ConnectionObjectId
     * BIT16 ConsumerSessionId
     * BIT32 ReceivedBytes
     * BIT32 RequestedBytes
     * 
     * @param request
     * @param conversation
     * @param requestNumber
     * @param allocatedFromBufferPool
     * @param partOfExchange
     */
    static void rcvRequestMsgs(CommsByteBuffer request, Conversation conversation, int requestNumber,
                               boolean allocatedFromBufferPool, boolean partOfExchange)
    {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
            SibTr.entry(tc, "rcvRequestMsgs",
                        new Object[]
                        {
                         request,
                         conversation,
                         "" + requestNumber,
                         "" + allocatedFromBufferPool
                        });

        short connectionObjectID = request.getShort(); // BIT16 ConnectionObjectId
        short consumerObjectID = request.getShort(); // BIT16 SyncConsumerSessionId
        int receivedBytes = request.getInt(); // BIT32 ReceivedBytes
        int requestedBytes = request.getInt(); // BIT32 RequestedBytes

        if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
        {
            SibTr.debug(tc, "connectionObjectID", connectionObjectID);
            SibTr.debug(tc, "consumerObjectID", consumerObjectID);
            SibTr.debug(tc, "receivedBytes", receivedBytes);
            SibTr.debug(tc, "requestedBytes", requestedBytes);
        }

        CATMainConsumer mainConsumer =
                        ((CATMainConsumer) ((ConversationState) conversation.getAttachment()).getObject(consumerObjectID));

        mainConsumer.requestMsgs(requestNumber, receivedBytes, requestedBytes);

        request.release(allocatedFromBufferPool);

        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
            SibTr.exit(tc, "rcvRequestMsgs");
    }

    /**
     * Close the Synchronous Consumer Session provided by the client.
     * 
     * Fields:
     * 
     * BIT16 ConnectionObjectId
     * BIT16 SyncConsumerSessionId
     * 
     * @param request
     * @param conversation
     * @param requestNumber
     * @param allocatedFromBufferPool
     * @param partOfExchange
     */
    static void rcvCloseConsumerSess(CommsByteBuffer request, Conversation conversation, int requestNumber,
                                     boolean allocatedFromBufferPool,
                                     boolean partOfExchange)
    {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
            SibTr.entry(tc, "rcvCloseConsumerSess",
                        new Object[]
                        {
                         request,
                         conversation,
                         "" + requestNumber,
                         "" + allocatedFromBufferPool
                        });

        short connectionObjectID = request.getShort(); //        BIT16 ConnectionObjectId
        short consumerObjectID = request.getShort(); //        BIT16 SyncConsumerSessionId

        if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
        {
            SibTr.debug(tc, "connectionObjectID", connectionObjectID);
            SibTr.debug(tc, "consumerObjectID", consumerObjectID);
        }

        ConversationState conversationState = (ConversationState) conversation.getAttachment();
        CATMainConsumer mainConsumer = (CATMainConsumer) conversationState.getObject(consumerObjectID);

        mainConsumer.close(requestNumber);

        conversationState.removeObject(consumerObjectID);

        request.release(allocatedFromBufferPool);

        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
            SibTr.exit(tc, "rcvCloseConsumerSess");
    }

    /**
     * Receive a message using the Synchronous Consumer Session provided by the client.
     * 
     * The receive is farmed off to the CATConsumer handling the receives for this session.
     * Typically this will be the synchronous handler.
     * 
     * Fields:
     * 
     * BIT16 ConnectionObjectId
     * BIT16 SyncConsumerSessionId
     * BIT32 TransactionId
     * BIT64 TimeOut
     * 
     * @param request
     * @param conversation
     * @param requestNumber
     * @param allocatedFromBufferPool
     * @param partOfExchange
     */
    static void rcvSessReceive(CommsServerByteBuffer request,
                               Conversation conversation,
                               int requestNumber,
                               boolean allocatedFromBufferPool,
                               boolean partOfExchange)
    {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
            SibTr.entry(tc, "rcvSessReceive",
                        new Object[]
                        {
                         request,
                         conversation,
                         "" + requestNumber,
                         "" + allocatedFromBufferPool
                        });

        ConversationState conversationState = (ConversationState) conversation.getAttachment();
        ServerLinkLevelState linkState = (ServerLinkLevelState) conversation.getLinkLevelAttachment();

        final boolean optimizedTx = CommsUtils.requiresOptimizedTransaction(conversation);
        short connectionObjectID = request.getShort(); // BIT16 ConnectionObjectId
        short consumerObjectID = request.getShort(); // BIT16 ConsumerObjectId
        int transactionObjectId = request.getSITransactionId(connectionObjectID, linkState, optimizedTx);
        long timeout = request.getLong(); // BIT64 TimeOut

        if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
        {
            SibTr.debug(tc, "connectionObjectID", connectionObjectID);
            SibTr.debug(tc, "consumerObjectID", consumerObjectID);
            SibTr.debug(tc, "transaction Id", transactionObjectId);
            SibTr.debug(tc, "timeout", timeout);
        }

        CATMainConsumer mainConsumer = (CATMainConsumer) conversationState.getObject(consumerObjectID);

        mainConsumer.receive(requestNumber, transactionObjectId, timeout);

        request.release(allocatedFromBufferPool);

        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
            SibTr.exit(tc, "rcvSessReceive");
    }

    /**
     * Tell the main consumer object to do a flush.
     * 
     * Fields:
     * 
     * BIT16 ConnectionObjectId
     * BIT16 ConsumerSessionId
     * 
     * @param request
     * @param conversation
     * @param requestNumber
     * @param allocatedFromBufferPool
     * @param partOfExchange
     */
    static void rcvFlushSess(CommsByteBuffer request, Conversation conversation, int requestNumber,
                             boolean allocatedFromBufferPool, boolean partOfExchange)
    {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
            SibTr.entry(tc, "rcvFlushSess",
                        new Object[]
                        {
                         request,
                         conversation,
                         "" + requestNumber,
                         "" + allocatedFromBufferPool
                        });

        short connectionObjectId = request.getShort(); // BIT16 ConnectionObjectId
        short consumerObjectId = request.getShort(); // BIT16 ConsumerSessionId

        if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
        {
            SibTr.debug(tc, "connectionObjectId", connectionObjectId);
            SibTr.debug(tc, "consumerObjectId", consumerObjectId);
        }

        CATMainConsumer mainConsumer =
                        (CATMainConsumer) ((ConversationState) conversation.getAttachment()).getObject(consumerObjectId);

        mainConsumer.flush(requestNumber);

        request.release(allocatedFromBufferPool);

        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
            SibTr.exit(tc, "rcvFlushSess");
    }

    /**
     * Deregisters a currently registered asynchronous consumer.
     * 
     * Fields:
     * 
     * BIT16 ConnectionObjectId
     * BIT16 ConsumerSessionId
     * 
     * @param request
     * @param conversation
     * @param requestNumber
     * @param allocatedFromBufferPool
     * @param partOfExchange
     */
    static void rcvDeregisterAsyncConsumer(CommsByteBuffer request, Conversation conversation,
                                           int requestNumber, boolean allocatedFromBufferPool,
                                           boolean partOfExchange, boolean stoppable) //SIB0115d.comms
    {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
            SibTr.entry(tc, "rcvDeregisterAsyncConsumer",
                        new Object[]
                        {
                         request,
                         conversation,
                         requestNumber,
                         allocatedFromBufferPool,
                         stoppable //SIB0115d.comms
                        });

        short connectionObjectId = request.getShort(); // BIT16 ConnectionObjectId
        short consumerObjectId = request.getShort(); // BIT16 ConsumerSessionId

        if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
        {
            SibTr.debug(tc, "connectionObjectId", connectionObjectId);
            SibTr.debug(tc, "consumerObjectId", consumerObjectId);
        }

        CATMainConsumer mainConsumer =
                        (CATMainConsumer) ((ConversationState) conversation.getAttachment()).getObject(consumerObjectId);

        mainConsumer.unsetAsynchConsumerCallback(requestNumber, stoppable); //SIB0115d.comms

        request.release(allocatedFromBufferPool);

        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
            SibTr.exit(tc, "rcvDeregisterAsyncConsumer");
    }

    /**
     * Unlocks all locked messages on the server.
     * 
     * Fields:
     * 
     * BIT16 ConnectionObjectId
     * BIT16 ConsumerSessionId
     * 
     * @param request
     * @param conversation
     * @param requestNumber
     * @param allocatedFromBufferPool
     * @param partOfExchange
     */
    static void rcvUnlockAll(CommsByteBuffer request, Conversation conversation, int requestNumber,
                             boolean allocatedFromBufferPool, boolean partOfExchange)
    {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
            SibTr.entry(tc, "rcvUnlockAll",
                        new Object[]
                        {
                         request,
                         conversation,
                         "" + requestNumber,
                         "" + allocatedFromBufferPool
                        });

        short connectionObjectId = request.getShort(); // BIT16 ConnectionObjectId
        short consumerObjectId = request.getShort(); // BIT16 ConsumerSessionId

        if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
        {
            SibTr.debug(tc, "connectionObjectId", connectionObjectId);
            SibTr.debug(tc, "consumerObjectId", consumerObjectId);
        }

        CATMainConsumer mainConsumer =
                        (CATMainConsumer) ((ConversationState) conversation.getAttachment()).getObject(consumerObjectId);

        mainConsumer.unlockAll(requestNumber);

        request.release(allocatedFromBufferPool);

        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
            SibTr.exit(tc, "rcvUnlockAll");
    }

    /**
     * Unlocks a set of messages that have been locked by the server.
     * <p>
     * Note this method may be called when the client expects a reply and when it does not and the
     * 'partOfExchange' flag in the JFap header should be examined to determine whether to send a
     * reply or not.
     * 
     * Fields:
     * 
     * BIT16 ConnectionObjectId
     * BIT16 ConsumerSessionId
     * BIT32 ArrayCount
     * 
     * BIT32[] MsgIds
     * 
     * If FAP is 7, 8 or greater than 9 the following Field is also applicable:
     * BYTE IncrementLockCount
     * 
     * @param request
     * @param conversation
     * @param requestNumber
     * @param allocatedFromBufferPool
     * @param partOfExchange
     */
    static void rcvUnlockSet(CommsByteBuffer request, Conversation conversation, int requestNumber,
                             boolean allocatedFromBufferPool, boolean partOfExchange)
    {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
            SibTr.entry(tc, "rcvUnlockSet",
                        new Object[]
                        {
                         request,
                         conversation,
                         "" + requestNumber,
                         "" + allocatedFromBufferPool
                        });

        short connectionObjectId = request.getShort(); // BIT16 ConnectionObjectId
        short consumerObjectId = request.getShort(); // BIT16 ConsumerSessionId

        if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
        {
            SibTr.debug(tc, "connectionObjectId", connectionObjectId);
            SibTr.debug(tc, "consumerObjectId", consumerObjectId);
        }

        SIMessageHandle[] msgHandles = request.getSIMessageHandles();

        CATMainConsumer mainConsumer =
                        (CATMainConsumer) ((ConversationState) conversation.getAttachment()).getObject(consumerObjectId);

        //Flowing incrementLockCount is only valid for faps 7, 8 and greater 9
        final int fapLevel = conversation.getHandshakeProperties().getFapLevel();
        if (!(fapLevel < JFapChannelConstants.FAP_VERSION_7 || fapLevel == JFapChannelConstants.FAP_VERSION_9))
        {
            byte incrementlockCountByte = request.get();
            boolean incrementlockCount = (incrementlockCountByte == 1);
            mainConsumer.unlockSet(requestNumber, msgHandles, partOfExchange, incrementlockCount);
        }
        else
        {
            mainConsumer.unlockSet(requestNumber, msgHandles, partOfExchange);
        }

        request.release(allocatedFromBufferPool);

        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
            SibTr.exit(tc, "rcvUnlockSet");
    }

    /**
     * Deletes a set of messages that are currently locked by the
     * server.
     * 
     * Fields:
     * 
     * BIT16 ConnectionObjectId
     * BIT16 ConsumerSessionId
     * BIT32 TranasctionId
     * BIT32 ArrayCount
     * 
     * BIT32[] MsgIds
     * 
     * @param request
     * @param conversation
     * @param requestNumber
     * @param allocatedFromBufferPool
     * @param partOfExchange
     * @param optimizedTx
     */
    static void rcvDeleteSet(CommsServerByteBuffer request,
                             Conversation conversation,
                             int requestNumber,
                             boolean allocatedFromBufferPool,
                             boolean partOfExchange)
    {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
            SibTr.entry(tc, "rcvDeleteSet",
                        new Object[]
                        {
                         request,
                         conversation,
                         "" + requestNumber
                        });

        ServerLinkLevelState linkState = (ServerLinkLevelState) conversation.getLinkLevelAttachment();

        final boolean optimizedTx = CommsUtils.requiresOptimizedTransaction(conversation);
        short connectionObjectId = request.getShort(); // BIT16 ConnectionObjectId
        short consumerObjectId = request.getShort(); // BIT16 ConsumerSessionId
        int transactionId = request.getSITransactionId(connectionObjectId, linkState, optimizedTx);

        if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
        {
            SibTr.debug(tc, "connectionObjectId", connectionObjectId);
            SibTr.debug(tc, "consumerObjectId", consumerObjectId);
            SibTr.debug(tc, "transactionId", transactionId);
        }

        SIMessageHandle[] siMsgHandles = request.getSIMessageHandles();

        CATMainConsumer mainConsumer =
                        (CATMainConsumer) ((ConversationState) conversation.getAttachment()).getObject(consumerObjectId);

        // If the transaction id is not null then retrieve the
        // transaction object from the table in the link level

        mainConsumer.deleteSet(requestNumber, siMsgHandles, transactionId, partOfExchange); // f187521.2.1, F219476.2

        request.release(allocatedFromBufferPool);

        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
            SibTr.exit(tc, "rcvDeleteSet");
    }

    /**
     * Reads a set of messages that are currently locked by the server.
     * 
     * Fields:
     * 
     * BIT16 ConnectionObjectId
     * BIT16 ConsumerSessionId
     * BIT32 ArrayCount
     * 
     * BIT32[] MsgIds
     * 
     * @param request
     * @param conversation
     * @param requestNumber
     * @param allocatedFromBufferPool
     * @param partOfExchange
     */
    static void rcvReadSet(CommsByteBuffer request, Conversation conversation, int requestNumber,
                           boolean allocatedFromBufferPool, boolean partOfExchange)
    {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
            SibTr.entry(tc, "rcvReadSet",
                        new Object[]
                        {
                         request,
                         conversation,
                         "" + requestNumber,
                         "" + allocatedFromBufferPool
                        });

        short connectionObjectId = request.getShort(); // BIT16 ConnectionObjectId
        short consumerObjectId = request.getShort(); // BIT16 ConsumerSessionId

        if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
        {
            SibTr.debug(tc, "connectionObjectId", connectionObjectId);
            SibTr.debug(tc, "consumerObjectId", consumerObjectId);
        }

        SIMessageHandle[] msgHandles = request.getSIMessageHandles();

        CATMainConsumer mainConsumer =
                        (CATMainConsumer) ((ConversationState) conversation.getAttachment()).getObject(consumerObjectId);

        mainConsumer.readSet(requestNumber, msgHandles);

        request.release(allocatedFromBufferPool);

        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
            SibTr.exit(tc, "rcvReadSet");
    }

    /**
     * Reads and then deletes a set of messages that are currently locked by the server.
     * 
     * Fields:
     * 
     * BIT16 ConnectionObjectId
     * BIT16 ConsumerSessionId
     * BIT32 TranasctionId
     * BIT32 ArrayCount
     * 
     * BIT32[] MsgIds
     * 
     * @param request
     * @param conversation
     * @param requestNumber
     * @param allocatedFromBufferPool
     * @param partOfExchange
     */
    static void rcvReadAndDeleteSet(CommsServerByteBuffer request,
                                    Conversation conversation,
                                    int requestNumber,
                                    boolean allocatedFromBufferPool,
                                    boolean partOfExchange)
    {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
            SibTr.entry(tc, "rcvReadAndDeleteSet",
                        new Object[]
                        {
                         request,
                         conversation,
                         "" + requestNumber,
                         "" + allocatedFromBufferPool,
                         "" + partOfExchange
                        });

        ServerLinkLevelState linkState = (ServerLinkLevelState) conversation.getLinkLevelAttachment();

        final boolean optimizedTx = CommsUtils.requiresOptimizedTransaction(conversation);
        short connectionObjectId = request.getShort(); // BIT16 ConnectionObjectId
        short consumerObjectId = request.getShort(); // BIT16 ConsumerSessionId
        int transactionId = request.getSITransactionId(connectionObjectId, linkState, optimizedTx);

        if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
        {
            SibTr.debug(tc, "connectionObjectId", connectionObjectId);
            SibTr.debug(tc, "consumerObjectId", consumerObjectId);
            SibTr.debug(tc, "transactionId", transactionId);
        }

        SIMessageHandle[] msgHandles = request.getSIMessageHandles();

        CATMainConsumer mainConsumer =
                        (CATMainConsumer) ((ConversationState) conversation.getAttachment()).getObject(consumerObjectId);

        mainConsumer.readAndDeleteSet(requestNumber, msgHandles, transactionId);

        request.release(allocatedFromBufferPool);

        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
            SibTr.exit(tc, "rcvReadAndDeleteSet");
    }

    /**
     * Create a Synchronous Consumer Session, assign the object to the conversation
     * state, and return the ID to client.
     * 
     * Fields:
     * 
     * BIT16 SIConnectionObjectId
     * BIT16 ClientSessionID
     * BIT16 ConsumerFlags
     * BIT16 Reliability
     * BIT32 RequestedBytes
     * BIT16 DestinationType
     * BIT16 UnrecoverableReliability
     * 
     * BIT16 Uuid Length
     * BYTE[] Uuid
     * BIT16 DestinationNameLength
     * BYTE[] DestinationName
     * BIT16 DiscriminatorLength
     * BYTE[] Discriminator
     * BIT16 SelectorLength
     * BYTE[] Selector
     * 
     * @param request
     * @param conversation
     * @param requestNumber
     * @param allocatedFromBufferPool
     * @param partOfExchange
     */
    static void rcvCreateBifurcatedSess(CommsByteBuffer request, Conversation conversation,
                                        int requestNumber, boolean allocatedFromBufferPool,
                                        boolean partOfExchange)
    {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
            SibTr.entry(tc, "rcvCreateBifurcatedSess",
                        new Object[]
                        {
                         request,
                         conversation,
                         "" + requestNumber,
                         "" + allocatedFromBufferPool
                        });

        ConversationState convState = (ConversationState) conversation.getAttachment();

        short connectionObjectID = request.getShort(); // BIT16 ConnectionObjectId
        long mpSessionId = request.getLong(); // BIT64 MessageProcessorSessionId

        if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
        {
            SibTr.debug(tc, "SICoreConnection Id:", "" + connectionObjectID);
            SibTr.debug(tc, "MP Session Id:", "" + mpSessionId);
        }

        SICoreConnection connection =
                        ((CATConnection) convState.getObject(connectionObjectID)).getSICoreConnection();

        try
        {
            BifurcatedConsumerSession bifConsumerSession =
                            connection.createBifurcatedConsumerSession(mpSessionId);

            CATMainConsumer mainConsumer = new CATMainConsumer(conversation,
                            (short) 0, // Client sess id
                            null, // ConsumerSession
                            false, // Read ahead
                            false, // No local
                            null); // Unrecov reliability

            short consSessionObjectID = (short) convState.addObject(mainConsumer);
            mainConsumer.setConsumerSessionId(consSessionObjectID);
            mainConsumer.setBifurcatedSession(bifConsumerSession);

            if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
                SibTr.debug(tc, "Consumer Session Id:", consSessionObjectID);

            StaticCATHelper.sendSessionCreateResponse(JFapChannelConstants.SEG_CREATE_BIFURCATED_SESSION_R,
                                                      requestNumber,
                                                      conversation,
                                                      consSessionObjectID,
                                                      bifConsumerSession,
                                                      null);
        } catch (ConversationStateFullException e)
        {
            FFDCFilter.processException(e,
                                        CLASS_NAME + ".rcvCreateBifurcatedSess",
                                        CommsConstants.STATICCATCONSUMER_CREATEBIF_01);

            if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
                SibTr.debug(tc, e.getMessage(), e);

            StaticCATHelper.sendExceptionToClient(e,
                                                  CommsConstants.STATICCATCONSUMER_CREATEBIF_01,
                                                  conversation, requestNumber);
        } catch (SINotAuthorizedException e)
        {
            // No FFDC Code Needed
            if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
                SibTr.debug(tc, e.getMessage(), e);

            StaticCATHelper.sendExceptionToClient(e,
                                                  null,
                                                  conversation, requestNumber);
        } catch (SIException e)
        {
            //No FFDC code needed
            //Only FFDC if we haven't received a meTerminated event.
            if (!convState.hasMETerminated())
            {
                FFDCFilter.processException(e,
                                            CLASS_NAME + ".rcvCreateBifurcatedSess",
                                            CommsConstants.STATICCATCONSUMER_CREATEBIF_02);
            }

            if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
                SibTr.debug(tc, e.getMessage(), e);

            StaticCATHelper.sendExceptionToClient(e,
                                                  CommsConstants.STATICCATCONSUMER_CREATEBIF_02,
                                                  conversation, requestNumber);
        }

        request.release(allocatedFromBufferPool);

        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
            SibTr.exit(tc, "rcvCreateBifurcatedSess");
    }

    /**
     * Unlocks all locked messages on the server.
     * 
     * Fields:
     * 
     * BIT16 ConnectionObjectId
     * BIT16 ConsumerSessionId
     * BYTE incrementUnlockCount
     * 
     * @param request
     * @param conversation
     * @param requestNumber
     * @param allocatedFromBufferPool
     * @param partOfExchange
     */
    static void rcvUnlockAllWithUnlockCountFlag(CommsByteBuffer request, Conversation conversation, int requestNumber,
                                                boolean allocatedFromBufferPool, boolean partOfExchange)
    {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
            SibTr.entry(tc, "rcvUnlockAllWithUnlockCountFlag",
                        new Object[]
                        {
                         request,
                         conversation,
                         "" + requestNumber,
                         "" + allocatedFromBufferPool
                        });

        short connectionObjectId = request.getShort(); // BIT16 ConnectionObjectId
        short consumerObjectId = request.getShort(); // BIT16 ConsumerSessionId
        boolean incrementUnlockCount = request.getBoolean(); //BYTE incrementUnlockCount

        if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
        {
            SibTr.debug(tc, "connectionObjectId", connectionObjectId);
            SibTr.debug(tc, "consumerObjectId", consumerObjectId);
            SibTr.debug(tc, "incrementUnlockCount", incrementUnlockCount);
        }

        CATMainConsumer mainConsumer =
                        (CATMainConsumer) ((ConversationState) conversation.getAttachment()).getObject(consumerObjectId);

        mainConsumer.unlockAll(requestNumber, incrementUnlockCount);

        request.release(allocatedFromBufferPool);

        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
            SibTr.exit(tc, "rcvUnlockAllWithUnlockCountFlag");
    }

}
