/*
 * @start_prolog@
 * Version: @(#) 1.59 SIB/ws/code/sib.comms.server.impl/src/com/ibm/ws/sib/comms/server/clientsupport/StaticCATSubscription.java, SIB.comms, WASX.SIB, aa1225.01 09/07/23 03:14:19 [7/2/12 05:59:01]
 * ============================================================================
 * IBM Confidential OCO Source Materials
 *
 * 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70  Copyright IBM Corp. 2004, 2009
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
 * Creation        030722 mattheg  Original
 * f172297         030722 schmittm Final Core API 0.6 changes
 * f173559         030808 mattheg  Add check for invalid flags
 * F174602         030820 prestona Switch to using new SICommsException
 * f173765.2       030926 mattheg  Core API M4 update
 * f177889         030929 mattheg  Core API M4 completion
 * d179459         031010 mattheg  Ensure flag checking is correct
 * F183828         031204 prestona Update CF + TCP prereqs to MS 5.1 level
 * f179519.1       031210 mattheg  Add SIDestinationWrongTypeException handling
 * f181007         031211 mattheg  Add boolean 'exchange' flag
 * d187056         040115 mattheg  Add read ahead fix as per 181146
 * d186970         040116 mattheg  Overhaul the way we send exceptions to client
 * f187521.2.1     040126 mattheg  Unrecoverable reliability -- part 2
 * F188491         030128 prestona Migrate to M6 CF + TCP Channel
 * f191114         040218 mattheg  Multicast support
 * d175222         040219 mattheg  Ensure SICommsException is reported correctly and not sent to client
 * d187252         040302 mattheg  Ensure session destination information is only returned if it changes
 * d192293         040308 mattheg  NLS file changes
 * f192759.2       040311 mattheg  M7 Core SPI changes
 * d194950         040318 mattheg  Ensure Durable subscriptions work
 * f195758.2       040415 mattheg  M7.5 Core SPI updates
 * f200337         040428 mattheg  Message order context implemenation
 * f176658.4.2.2   040504 mattheg  deliverImmediately flag change
 * F195720.3       040616 prestona WAS Request Metrics in Jetstream
 * F207007.2       040617 mattheg  Core SPI Update of message selector parameters
 * F195720.3.1     040629 prestona WAS Request Metrics in Jetstream
 * D217372         040719 mattheg  Move JFap constants -> JFapChannelConstants (not change-flagged)
 * F201972.2       040727 mattheg  Core SPI Exceptions rework (not change flagged)
 * D210259.1       040819 mattheg  Move deserialization methods to CommsUtils
 * F219476.2       040906 prestona Z3 Core SPI changes
 * D225856         041006 mattheg  Update FFDC class name (not change flagged)
 * D254870         050214 mattheg  Optimize connection close
 * D327083         051130 mattheg  Ensure we don't FFDC when durable subscription is not found
 * D342106         060130 mattheg  Don't FFDC on SINotAuthorisedException
 * D347591         060217 mattheg  Add support for exchanged starts
 * D350111.1       060302 mattheg  Use send listener for start()
 * D377648         060719 mattheg  Use CommsByteBuffer
 * D378229         060808 prestona Avoid synchronizing on ME-ME send()
 * D384259         060815 prestona Remove multicast support
 * D441183         072307 mleming  Don't FFDC when calling terminated ME
 * 471664          071003 vaughton Findbugs tidy up
 * 494335          080128 mleming  Flow localOnly information on the wire
 * 522463          080521 vaughton Incorrect consumer flags ffdc
 * 592503          090722 mleming  ObjectStoreFullException -> ConversationStateFullException
 * ============================================================================
 */
package com.ibm.ws.sib.comms.server.clientsupport;

import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ejs.ras.TraceNLS;
import com.ibm.websphere.sib.Reliability;
import com.ibm.websphere.sib.SIDestinationAddress;
import com.ibm.websphere.sib.exception.SIErrorException;
import com.ibm.websphere.sib.exception.SIException;
import com.ibm.websphere.sib.exception.SIResourceException;
import com.ibm.ws.ffdc.FFDCFilter;
import com.ibm.ws.sib.comms.CommsConstants;
import com.ibm.ws.sib.comms.common.CommsByteBuffer;
import com.ibm.ws.sib.comms.common.CommsByteBufferPool;
import com.ibm.ws.sib.comms.server.ConversationState;
import com.ibm.ws.sib.comms.server.ConversationStateFullException;
import com.ibm.ws.sib.jfapchannel.Conversation;
import com.ibm.ws.sib.jfapchannel.JFapChannelConstants;
import com.ibm.ws.sib.jfapchannel.Conversation.ThrottlingPolicy;
import com.ibm.ws.sib.utils.ras.SibTr;
import com.ibm.wsspi.sib.core.ConsumerSession;
import com.ibm.wsspi.sib.core.SICoreConnection;
import com.ibm.wsspi.sib.core.SelectionCriteria;
import com.ibm.wsspi.sib.core.exception.SIDurableSubscriptionNotFoundException;
import com.ibm.wsspi.sib.core.exception.SINotAuthorizedException;

/**
 * This class handles requests for durable subscriptions from a client.
 *
 * @author Gareth Matthews
 */
public class StaticCATSubscription
{
   /** Class name for FFDC's */
   private static String CLASS_NAME = StaticCATSubscription.class.getName();

   /** Reference to the pool manager */
   private static CommsByteBufferPool poolManager = CommsByteBufferPool.getInstance();

   /** Registers our trace component */
   private static final TraceComponent tc = SibTr.register(StaticCATSubscription.class,
                                                           CommsConstants.MSG_GROUP,
                                                           CommsConstants.MSG_BUNDLE);

   /** Our NLS reference object */
   private static final TraceNLS nls = TraceNLS.getTraceNLS(CommsConstants.MSG_BUNDLE);

   /** Log class info on load */
   static
   {
      if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) SibTr.debug(tc, "Source info: @(#)SIB/ws/code/sib.comms.server.impl/src/com/ibm/ws/sib/comms/server/clientsupport/StaticCATSubscription.java, SIB.comms, WASX.SIB, aa1225.01 1.59");
   }

   /**
    * This method will actually create the consumer session that attaches to the durable
    * subscription.
    *
    * BIT16    ConnectionObjectID
    * BIT16    ConsumerFlags
    *
    * BIT16    Uuid Length
    * BYTE[]   Uuid
    * BIT16    DestinationNameLength
    * BYTE[]   DestinationName
    *
    * BIT16    SubscriptionNameLength
    * BYTE[]   SubscriptionName
    * BIT16    SubscriptionHomeLength
    * BYTE[]   SubscriptionHome
    * BIT16    SelectorDomain
    * BIT16    DiscriminatorLength
    * BYTE[]   Discriminator
    * BIT16    SelectorLength
    * BYTE[]   Selector
    *
    * @param request
    * @param conversation
    * @param requestNumber
    * @param allocatedFromBufferPool
    * @param partOfExchange
    */
   public static void rcvCreateDurableSub(CommsByteBuffer request, Conversation conversation,
                                          int requestNumber,
                                          boolean allocatedFromBufferPool,
                                          boolean partOfExchange)
   {
      if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.entry(tc, "rcvCreateDurableSub",
                                           new Object[]
                                           {
                                              request,
                                              conversation,
                                              ""+requestNumber,
                                              ""+allocatedFromBufferPool
                                            });

      ConversationState convState = (ConversationState) conversation.getAttachment();

      short connectionObjectID = request.getShort();  // BIT16 ConnectionObjectId
      short consumerFlags = request.getShort();       // BIT16 Consumer Flags

      if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
      {
         SibTr.debug(tc, "ConnectionObjectId:", connectionObjectID);
         SibTr.debug(tc, "ConsumerFlags:", consumerFlags);
      }

      /**************************************************************/
      /* Consumer flags                                             */
      /**************************************************************/
      // Check if the flags are valid
      if (consumerFlags > 0x07)
      {
         // The flags appear to be invalid
         if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) SibTr.debug(tc, "Consumer flags ("+consumerFlags+") > 0x07");

         SIErrorException e = new SIErrorException(
            nls.getFormattedMessage("INVALID_PROP_SICO8014", new Object[] {""+consumerFlags }, null)
         );

         FFDCFilter.processException(e, CLASS_NAME + ".rcvCreateDurableSub",
                                     CommsConstants.STATICCATSUBSCRIPTION_CREATE_03);
      }

      boolean noLocal                  = (consumerFlags & 0x02) != 0;
      boolean supportMultipleConsumers = (consumerFlags & 0x04) != 0;

      /**************************************************************/
      /* Destination Info                                           */
      /**************************************************************/
      SIDestinationAddress destAddress = request.getSIDestinationAddress(conversation.getHandshakeProperties().getFapLevel());

      /**************************************************************/
      /* Subscription Name                                          */
      /**************************************************************/
      String subscriptionName = request.getString();
      if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) SibTr.debug(tc, "Subscription Name:", subscriptionName);

      /**************************************************************/
      /* Subscription Home                                          */
      /**************************************************************/
      String subscriptionHome = request.getString();
      if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) SibTr.debug(tc, "Subscription Home:", subscriptionHome);

      /**************************************************************/
      /* SelectionCriteria                                          */
      /**************************************************************/
      SelectionCriteria criteria = request.getSelectionCriteria();

      SICoreConnection connection =
         ((CATConnection) convState.getObject(connectionObjectID)).getSICoreConnection();

      /**************************************************************/
      /* Alternate User Id                                          */
      /**************************************************************/
      String alternateUser = request.getString();

      try
      {
         connection.createDurableSubscription(subscriptionName,
                                              subscriptionHome,
                                              destAddress,
                                              criteria,
                                              supportMultipleConsumers,
                                              noLocal,
                                              alternateUser);

         try
         {
            conversation.send(poolManager.allocate(),
                              JFapChannelConstants.SEG_CREATE_DURABLE_SUB_R,
                              requestNumber,
                              JFapChannelConstants.PRIORITY_MEDIUM,
                              true,
                              ThrottlingPolicy.BLOCK_THREAD,
                              null);
         }
         catch (SIException e)
         {
            FFDCFilter.processException(e,
                                        CLASS_NAME + ".rcvCreateDurableSub",
                                        CommsConstants.STATICCATSUBSCRIPTION_CREATE_01);

            if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) SibTr.debug(tc, e.getMessage(), e);

            SibTr.error(tc, "COMMUNICATION_ERROR_SICO2025", e);
         }
      }
      catch (SINotAuthorizedException e)
      {
         // No FFDC Code Needed
         if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) SibTr.debug(tc, e.getMessage(), e);

         StaticCATHelper.sendExceptionToClient(e,
                                               null,
                                               conversation, requestNumber);
      }
      catch (SIException e)
      {
         //No FFDC code needed
         //Only FFDC if we haven't received a meTerminated event.
         if(!convState.hasMETerminated())
         {
            FFDCFilter.processException(e,
                                        CLASS_NAME + ".rcvCreateDurableSub",
                                        CommsConstants.STATICCATSUBSCRIPTION_CREATE_02);
         }

         if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) SibTr.debug(tc, e.getMessage(), e);

         StaticCATHelper.sendExceptionToClient(e,
                                               CommsConstants.STATICCATSUBSCRIPTION_CREATE_02,        // d186970
                                               conversation, requestNumber);                          // f172297

      }

      request.release(allocatedFromBufferPool);

      if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.exit(tc, "rcvCreateDurableSub");
   }

   /**
    * This method will actually create the consumer session that attaches to the durable
    * subscription.
    *
    * BIT16    ConnectionObjectID
    * BIT16    ClientSessionID
    * BIT16    ConsumerFlags
    * BIT16    Reliability
    * BIT32    RequestedBytes
    * BIT16    UnrecoverableReliability
    *
    * BIT16    Uuid Length
    * BYTE[]   Uuid
    * BIT16    DestinationNameLength
    * BYTE[]   DestinationName
    *
    * BIT16    SubscriptionNameLength
    * BYTE[]   SubscriptionName
    * BIT16    SubscriptionHomeLength
    * BYTE[]   SubscriptionHome
    * BIT16    SelectorDomain
    * BIT16    DiscriminatorLength
    * BYTE[]   Discriminator
    * BIT16    SelectorLength
    * BYTE[]   Selector
    *
    * @param request
    * @param conversation
    * @param requestNumber
    * @param allocatedFromBufferPool
    * @param partOfExchange
    */
   public static void rcvCreateConsumerForDurableSub(CommsByteBuffer request, Conversation conversation,
                                                     int requestNumber,
                                                     boolean allocatedFromBufferPool,
                                                     boolean partOfExchange)
   {
      if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.entry(tc, "rcvCreateConsumerForDurableSub",
                                           new Object[]
                                           {
                                              request,
                                              conversation,
                                              ""+requestNumber,
                                              ""+allocatedFromBufferPool
                                            });

      ConversationState convState = (ConversationState) conversation.getAttachment();

      short connectionObjectID = request.getShort();  // BIT16 ConnectionObjectId
      short clientSessionId = request.getShort();     // BIT16 ClientSessionId
      short consumerFlags = request.getShort();       // BIT16 Consumer Flags
      short reliabilityShort = request.getShort();    // BIT16 Relibility
      int requestedBytes = request.getInt();          // BIT32 Requested bytes
      short unrecovShort = request.getShort();        // BIT16 UnrecoverableReliability

      if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
      {
         SibTr.debug(tc, "ConnectionObjectId:", connectionObjectID);
         SibTr.debug(tc, "ClientSessionId:", clientSessionId);
         SibTr.debug(tc, "ConsumerFlags:", consumerFlags);
         SibTr.debug(tc, "Reliability:", reliabilityShort);
         SibTr.debug(tc, "RequestedBytes:", requestedBytes);
         SibTr.debug(tc, "UnrecovReliability", unrecovShort);
      }

      try
      {
         /**************************************************************/
         /* Reliability                                                */
         /**************************************************************/
         Reliability reliability = null;
         if (reliabilityShort != -1)
         {
            reliability = Reliability.getReliability(reliabilityShort);
         }

         /**************************************************************/
         /* Consumer flags                                             */
         /**************************************************************/

         final short validFlags = 0x0007|CommsConstants.CF_BIFURCATABLE;
         if ((consumerFlags|validFlags) != validFlags) {
           if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) SibTr.debug(tc, "Consumer flags ("+consumerFlags+") not valid, valid flags ="+validFlags);
           SIErrorException e = new SIErrorException(nls.getFormattedMessage("INVALID_PROP_SICO8014", new Object[] {""+consumerFlags }, null));
           FFDCFilter.processException(e, CLASS_NAME + ".rcvCreateConsumerForDurableSub",CommsConstants.STATICCATSUBSCRIPTION_CREATECONS_05);
         }

         boolean readAheadPermitted       = (consumerFlags & 0x01) != 0;
         boolean noLocal                  = (consumerFlags & 0x02) != 0;
         boolean supportMultipleConsumers = (consumerFlags & 0x04) != 0;
         boolean bifurcatable             = (consumerFlags & CommsConstants.CF_BIFURCATABLE) != 0;

         /**************************************************************/
         /* Unrecoverable Reliability                                  */
         /**************************************************************/
         Reliability unrecoverableReliability = null;
         if (unrecovShort != -1)
         {
            unrecoverableReliability = Reliability.getReliability(unrecovShort);
         }

         /**************************************************************/
         /* Destination Info                                           */
         /**************************************************************/
         SIDestinationAddress destAddress = request.getSIDestinationAddress(conversation.getHandshakeProperties().getFapLevel());
         if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) SibTr.debug(tc, "Destination Address", destAddress);

         /**************************************************************/
         /* Subscription Name                                          */
         /**************************************************************/
         String subscriptionName = request.getString();
         if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) SibTr.debug(tc, "Subscription Name:", subscriptionName);

         /**************************************************************/
         /* Subscription Home                                          */
         /**************************************************************/
         String subscriptionHome = request.getString();
         if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) SibTr.debug(tc, "Subscription Home:", subscriptionHome);

         /**************************************************************/
         /* SelectionCriteria                                          */
         /**************************************************************/
         SelectionCriteria criteria = request.getSelectionCriteria();
         if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) SibTr.debug(tc, "Selection Criteria", criteria);

         /**************************************************************/
         /* Alternative user                                           */
         /**************************************************************/
         String alternateUser = request.getString();
         if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) SibTr.debug(tc, "Alternate User Id: ", alternateUser);

         SICoreConnection connection =
            ((CATConnection) convState.getObject(connectionObjectID)).getSICoreConnection();


         try
         {
            ConsumerSession session =
               connection.createConsumerSessionForDurableSubscription(subscriptionName,
                                                                      subscriptionHome,
                                                                      destAddress,
                                                                      criteria,
                                                                      supportMultipleConsumers,
                                                                      noLocal,
                                                                      reliability,
                                                                      readAheadPermitted,
                                                                      unrecoverableReliability,
                                                                      bifurcatable,
                                                                      alternateUser);

            CATMainConsumer mainConsumer = new CATMainConsumer(conversation,
                                                               clientSessionId,
                                                               session,
                                                               readAheadPermitted,
                                                               noLocal,
                                                               unrecoverableReliability);

            short durableSubscriberObjectID = (short) convState.addObject(mainConsumer);

            mainConsumer.setConsumerSessionId(durableSubscriberObjectID);

            if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
               SibTr.debug(tc, "Durable Subscription Id:", durableSubscriberObjectID);

            // Send the response to the client
            StaticCATHelper.sendSessionCreateResponse(JFapChannelConstants.SEG_CREATE_CONS_FOR_DURABLE_SUB_R,
                                                      requestNumber,
                                                      conversation,
                                                      durableSubscriberObjectID,
                                                      session,
                                                      destAddress);

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
               }
               catch (RuntimeException e)
               {
                  // If we get a runtime exception here then our setAsync or start
                  // calls have failed.
                  FFDCFilter.processException(e,
                                              CLASS_NAME + ".rcvCreateConsumerForDurableSub",
                                              CommsConstants.STATICCATSUBSCRIPTION_CREATECONS_01);

                  if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
                  {
                     SibTr.debug(tc, "Unable to create readahead async consumer");
                     SibTr.exception(tc, (Exception) e.getCause());
                  }

                  // Now throw a SICoreException to inform the client
                  throw new SIResourceException(e.getMessage());
               }
            }
         }
         catch (SIDurableSubscriptionNotFoundException e)
         {
            // No FFDC Code Needed
            // We don't want to FFDC here as JMS will cause this exception to happen in the routine
            // creation of a durable subscription. This is not a serious or internal error anyway.
            if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) SibTr.debug(tc, e.getMessage(), e);

            StaticCATHelper.sendExceptionToClient(e, null, conversation, requestNumber);
         }
         catch (SINotAuthorizedException e)
         {
            // No FFDC Code Needed
            if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) SibTr.debug(tc, e.getMessage(), e);

            StaticCATHelper.sendExceptionToClient(e,
                                                  null,
                                                  conversation, requestNumber);
         }
         catch (SIException e)
         {
            //No FFDC code needed
            //Only FFDC if we haven't received a meTerminated event.
            if(!convState.hasMETerminated())
            {
               FFDCFilter.processException(e,
                                           CLASS_NAME + ".rcvCreateConsumerForDurableSub",
                                           CommsConstants.STATICCATSUBSCRIPTION_CREATECONS_02);
            }

            if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
               SibTr.debug(tc, e.getMessage(), e);

            StaticCATHelper.sendExceptionToClient(e,
                                                  CommsConstants.STATICCATSUBSCRIPTION_CREATECONS_02,
                                                  conversation, requestNumber);
         }
         catch (ConversationStateFullException e)
         {
            FFDCFilter.processException(e,
                                        CLASS_NAME + ".rcvCreateConsumerForDurableSub",
                                        CommsConstants.STATICCATSUBSCRIPTION_CREATECONS_03);

            if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) SibTr.debug(tc, e.getMessage(), e);

            StaticCATHelper.sendExceptionToClient(e,
                                                  CommsConstants.STATICCATSUBSCRIPTION_CREATECONS_03,
                                                  conversation, requestNumber);

         }
      }
      catch (Exception e)
      {
         FFDCFilter.processException(e,
                                     CLASS_NAME + ".rcvCreateConsumerForDurableSub",
                                     CommsConstants.STATICCATSUBSCRIPTION_CREATECONS_04);

         if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) SibTr.debug(tc, e.getMessage(), e);

         StaticCATHelper.sendExceptionToClient(e,
                                               CommsConstants.STATICCATSUBSCRIPTION_CREATECONS_04,
                                               conversation, requestNumber);

      }

      request.release(allocatedFromBufferPool);

      if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.exit(tc, "rcvCreateConsumerForDurableSub");
   }

   /**
    * Deletes a durable subscription
    *
    * BIT16    ConnectionObjectID
    * BIT16    SubscriptionNameLength
    * BYTE[]   SubscriptionName
    * BIT16    SubscriptionHomeLength
    * BYTE[]   SubscriptionHome
    *
    * @param request
    * @param conversation
    * @param requestNumber
    * @param allocatedFromBufferPool
    * @param partOfExchange
    */
   public static void rcvDeleteDurableSub(CommsByteBuffer request, Conversation conversation,
                                          int requestNumber, boolean allocatedFromBufferPool,
                                          boolean partOfExchange)
   {
      if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.entry(tc, "rcvDeleteDurableSub",
                                           new Object[]
                                           {
                                              request,
                                              conversation,
                                              ""+requestNumber,
                                              ""+allocatedFromBufferPool
                                            });

      ConversationState convState = (ConversationState) conversation.getAttachment();

      short connectionObjectID = request.getShort();  // BIT16 ConnectionObjectId

      if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
         SibTr.debug(tc, "ConnectionObjectId:", connectionObjectID);

      /**************************************************************/
      /* Subscription Name                                          */
      /**************************************************************/
      String subscriptionName = request.getString();
      if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) SibTr.debug(tc, "Subscription Name:", subscriptionName);

      /**************************************************************/
      /* Subscription Home                                          */
      /**************************************************************/
      String subscriptionHome = request.getString();
      if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) SibTr.debug(tc, "Subscription Home:", subscriptionHome);

      SICoreConnection connection =
         ((CATConnection) convState.getObject(connectionObjectID)).getSICoreConnection();

      try
      {
         connection.deleteDurableSubscription(subscriptionName, subscriptionHome);

         try
         {
            conversation.send(poolManager.allocate(),
                              JFapChannelConstants.SEG_DELETE_DURABLE_SUB_R,
                              requestNumber,
                              JFapChannelConstants.PRIORITY_MEDIUM,
                              true,
                              ThrottlingPolicy.BLOCK_THREAD,
                              null);
         }
         catch (SIException e)
         {
            FFDCFilter.processException(e,
                                        CLASS_NAME + ".rcvDeleteDurableSub",
                                        CommsConstants.STATICCATSUBSCRIPTION_DELETE_01);

            if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) SibTr.debug(tc, e.getMessage(), e);

            SibTr.error(tc, "COMMUNICATION_ERROR_SICO2025", e);
         }
      }
      catch (SINotAuthorizedException e)
      {
         // No FFDC Code Needed
         if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) SibTr.debug(tc, e.getMessage(), e);

         StaticCATHelper.sendExceptionToClient(e,
                                               null,
                                               conversation, requestNumber);
      }
      catch (SIException e)
      {
         //No FFDC code needed
         //Only FFDC if we haven't received a meTerminated event.
         if(!convState.hasMETerminated())
         {
            com.ibm.ws.ffdc.FFDCFilter.processException(e,
                                                        CLASS_NAME + ".rcvDeleteDurableSub",
                                                        CommsConstants.STATICCATSUBSCRIPTION_DELETE_02);
         }

         if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) SibTr.debug(tc, e.getMessage(), e);

         StaticCATHelper.sendExceptionToClient(e,
                                               CommsConstants.STATICCATSUBSCRIPTION_DELETE_02,
                                               conversation, requestNumber);

      }

      request.release(allocatedFromBufferPool);

      if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.exit(tc, "rcvDeleteDurableSub");
   }
}
