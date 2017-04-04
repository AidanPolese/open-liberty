/*
 * @start_prolog@
 * Version: @(#) 1.50 SIB/ws/code/sib.comms.server.impl/src/com/ibm/ws/sib/comms/server/clientsupport/StaticCATBrowser.java, SIB.comms, WASX.SIB, aa1225.01 09/07/23 03:14:03 [7/2/12 05:59:00]
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
 * Creation        030711 mattheg  Original
 * 171893          030728 prestona Add support for BrowserSession.
 * F174602         030820 prestona Switch to using SICommsException
 * D175672         030902 Niall    Fix Memory Leaks
 * f173765.2       030926 mattheg  Core API M4 update
 * d180309         031020 mattheg  Need to catch more exceptions on browser create
 * F183828         031204 prestona Update CF + TCP prereqs to MS 5.1 level
 * f179519.1       031209 mattheg  Add SIDestinationWrongTypeException handling
 * f181007         031211 mattheg  Add boolean 'exchange' flag
 * f179339.4       031222 mattheg  Forward and reverse routing support
 * d186970         040116 mattheg  Overhaul the way we send exceptions to client
 * F188491         030128 prestona Migrate to M6 CF + TCP Channel
 * d175222         040219 mattheg  Ensure SICommsException is reported correctly and not sent to client
 * d187252         040302 mattheg  Ensure session destination information is only returned if it changes
 * d192293         040308 mattheg  NLS file changes
 * f192759.2       040311 mattheg  M7 Core SPI changes
 * f195758.2       040415 mattheg  M7.5 Core SPI changes
 * f193585.3.2     040503 mattheg  Remove destination filter
 * F195720.3       040616 prestona WAS Request Metrics in Jetstream
 * F207007.2       040617 mattheg  Core SPI Update of message selector parameters
 * F195720.3.1     040629 prestona WAS Request Metrics in Jetstream
 * D217372         040719 mattheg  Move JFap constants -> JFapChannelConstants (not change-flagged)
 * F201972.2       040727 mattheg  Core SPI Exceptions rework (not change flagged)
 * D210259.1       040819 mattheg  Move deserialization methods to CommsUtils
 * F219476.2       040906 prestona Z3 Core SPI changes
 * D235891         040930 mattheg  Runtime property standards
 * D225856         041006 mattheg  Update FFDC class name (not change flagged)
 * D254870         050214 mattheg  Optimize connection close
 * D258697         050302 mattheg  Ensure queue is pre-loaded to correct value
 * D342106         060130 mattheg  Don't FFDC on SINotAuthorisedException
 * D377648         060719 mattheg  Use CommsByteBuffer
 * D378229         060808 prestona Avoid synchronizing on ME-ME send()
 * D441183         072307 mleming  Don't FFDC when calling terminated ME
 * SIB0113.comms.1 070920 vaughton Core SPI changes
 * 494335          080128 mleming  Flow localOnly information on the wire
 * 592503          090722 mleming  ObjectStoreFullException -> ConversationStateFullException
 * ============================================================================
 */
package com.ibm.ws.sib.comms.server.clientsupport;


import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ejs.ras.TraceNLS;
import com.ibm.websphere.sib.SIDestinationAddress;
import com.ibm.websphere.sib.exception.SIErrorException;
import com.ibm.websphere.sib.exception.SIException;
import com.ibm.ws.ffdc.FFDCFilter;
import com.ibm.ws.sib.comms.CommsConstants;
import com.ibm.ws.sib.comms.common.CommsByteBuffer;
import com.ibm.ws.sib.comms.common.CommsByteBufferPool;
import com.ibm.ws.sib.comms.server.ConversationState;
import com.ibm.ws.sib.comms.server.ConversationStateFullException;
import com.ibm.ws.sib.jfapchannel.Conversation.ThrottlingPolicy;
import com.ibm.ws.sib.jfapchannel.Conversation;
import com.ibm.ws.sib.jfapchannel.HandshakeProperties;         //SIB0113.comms.1
import com.ibm.ws.sib.jfapchannel.JFapChannelConstants;
import com.ibm.ws.sib.utils.ras.SibTr;
import com.ibm.wsspi.sib.core.BrowserSession;
import com.ibm.wsspi.sib.core.DestinationType;
import com.ibm.wsspi.sib.core.SICoreConnection;
import com.ibm.wsspi.sib.core.SelectionCriteria;
import com.ibm.wsspi.sib.core.exception.SINotAuthorizedException;

/**
 * Static methods relating to browser sessions.  This methods accept
 * an inbound request and contain the logic to interpret its contents.
 * Once this has been done, actioning the request may then be delegated.
 */
public class StaticCATBrowser
{
   /** Class name for FFDC's */
   private static String CLASS_NAME = StaticCATBrowser.class.getName();

   /** Trace */
   private static final TraceComponent tc = SibTr.register(StaticCATBrowser.class,
                                                           CommsConstants.MSG_GROUP,
                                                           CommsConstants.MSG_BUNDLE);

   /** NLS handle */
   private static final TraceNLS nls = TraceNLS.getTraceNLS(CommsConstants.MSG_BUNDLE);

   /** Used to cache a reference to the buffer pool manager */
   private static CommsByteBufferPool poolManager = CommsByteBufferPool.getInstance();

   /** Log class info on load */
   static
   {
      if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) SibTr.debug(tc, "Source info: @(#)SIB/ws/code/sib.comms.server.impl/src/com/ibm/ws/sib/comms/server/clientsupport/StaticCATBrowser.java, SIB.comms, WASX.SIB, aa1225.01 1.50");
   }

   /**
    * Invoked when a create browser session flow is received.  This code extracts enough
    * data from the request to create a browser session.  This browser session is then
    * wrapped in a new instance of CATMainConsumer and stashed in the conversation state
    * for the conversation the request came in on.  Finally the id returned by storing the
    * session in the state object is returned to the client.
    *
    * @param request The request data received from our peer.  By this point it has been
    * identified as being a request to create a browser session.  This data is parsed to
    * determine the details for the browser session to create.
    * @param conversation The conversation the request came in on.  The browser session
    * created by this method is associated with this conversation so that we can easily
    * locate it again in the future.
    * @param requestNumber The request number the create browser session request came in on.
    * This is required when replying to the request.
    * @param allocatedFromBufferPool Was the WsByteBuffer containing request data allocated
    * from a pool?  If so, we need to re pool it when we are finished with it.
    * @param partOfExchange Whether the client is expecting a reply to this call.
    */
   public static void rcvCreateBrowserSess(CommsByteBuffer request, Conversation conversation,
                                           int requestNumber, boolean allocatedFromBufferPool,
                                           boolean partOfExchange)
   {
      if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.entry(tc, "rcvCreateBrowserSess",
                                           new Object[]
                                           {
                                              request,
                                              conversation,
                                              ""+requestNumber,
                                              ""+allocatedFromBufferPool
                                            });

      // Obtain the state object for this conversation.
      ConversationState convState = (ConversationState)conversation.getAttachment();

      // Extract all the interesting bits out of the buffer we were passed.
      short connectionObjectID =        request.getShort(); // BIT16 ConnectionObjectId
      short clientSessionId =           request.getShort();
      int requestedBytes =              request.getInt();
      short destinationTypeShort =      request.getShort();

      boolean allowMessageGathering = false;                   //SIB0113.comms.1 start
      final HandshakeProperties handshakeProps = conversation.getHandshakeProperties();
      if (handshakeProps.getFapLevel() >= JFapChannelConstants.FAP_VERSION_9) {
        final short browserFlags = request.getShort();

        if (browserFlags > CommsConstants.BF_MAX_VALID)
        {
           // The flags appear to be invalid
           if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) SibTr.debug(tc, "Browser flags ("+browserFlags+") > " + CommsConstants.BF_MAX_VALID);

           SIErrorException e = new SIErrorException(
              nls.getFormattedMessage("INVALID_PROP_SICO8017", new Object[] {""+browserFlags }, null)
           );

           FFDCFilter.processException(e, CLASS_NAME + ".rcvCreateBrowserSess",
                                       CommsConstants.STATICCATBROWSER_RCVCREATEBROWSERSESS_04);

           throw e;
        }

        allowMessageGathering = (browserFlags & CommsConstants.BF_ALLOW_GATHERING) != 0;
      }                                                        //SIB0113.comms.1 end

      SIDestinationAddress destAddr =   request.getSIDestinationAddress(conversation.getHandshakeProperties().getFapLevel());
      SelectionCriteria criteria =      request.getSelectionCriteria();
      String alternateUser =            request.getString();

      if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
         SibTr.debug(tc, "\nrcvCreateBrowserSess> connectionObjectID    = "+connectionObjectID+
                         "\nrcvCreateBrowserSess> clientSessionId       = "+clientSessionId+
                         "\nrcvCreateBrowserSess> requestedBytes        = "+requestedBytes+
                         "\nrcvCreateBrowserSess> destinationTypeShort  = "+destinationTypeShort+
                         "\nrcvCreateBrowserSess> allowMessageGathering = "+allowMessageGathering+
                         "\nrcvCreateBrowserSess> criteria              = "+criteria+
                         "\nrcvCreateBrowserSess> alternateUser         = "+alternateUser);

      // We are done with the buffer, so release it (if we can)
      request.release(allocatedFromBufferPool);

      // Convert from the destination type as a short to the correct enumerated value.
      DestinationType destinationType = null;
      if (destinationTypeShort != CommsConstants.NO_DEST_TYPE)
      {
         destinationType = DestinationType.getDestinationType(destinationTypeShort);
      }

      // Extract the Core API Connection object from the conversation state block
      SICoreConnection connection =
         ((CATConnection) convState.getObject(connectionObjectID)).getSICoreConnection();

      // Create the browser session.
      BrowserSession browserSession = null;

      try
      {
         browserSession =
                           connection.createBrowserSession(destAddr,
                                                           destinationType,
                                                           criteria,
                                                           alternateUser,
                                                           allowMessageGathering); //SIB0113.comms.1
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
            FFDCFilter.processException(e, CLASS_NAME + ".rcvCreateBrowserSess",
                                        CommsConstants.STATICCATBROWSER_RCVCREATEBROWSERSESS_01);
         }

         if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) SibTr.debug(tc, e.getMessage(), e);

         StaticCATHelper.sendExceptionToClient(e,
                                               CommsConstants.STATICCATBROWSER_RCVCREATEBROWSERSESS_01,  // d186970
                                               conversation, requestNumber);
      }

      // Only continue of we successfully managed to create a browser session...
      if (browserSession != null)
      {
         // Create a main consumer object which represents the browser session and is
         // suitable for storing in the conversation state object
         CATMainConsumer mainConsumer = new CATMainConsumer(conversation, clientSessionId, browserSession);

         // Stash the browser session in  the conversation state object.
         try
         {
            short sessionId = (short) convState.addObject(mainConsumer);
            if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) SibTr.debug(tc, "browser session id = "+sessionId);

            // Use the CATBrowseConsumer's requestMsgs method to send a bunch of messages
            // down to the client.  Normally requestMsgs would be called when the client
            // is running low of messages on its proxy queue - however, in this case we
            // use it to "pre-load" the queue.
            mainConsumer.requestMsgs(requestNumber, 0, requestedBytes);

            StaticCATHelper.sendSessionCreateResponse(JFapChannelConstants.SEG_CREATE_BROWSER_SESS_R,
                                                      requestNumber,
                                                      conversation,
                                                      sessionId,
                                                      browserSession,
                                                      destAddr);
         }
         catch (ConversationStateFullException e)
         {
            FFDCFilter.processException(e, CLASS_NAME + ".rcvCreateBrowserSess",
                                        CommsConstants.STATICCATBROWSER_RCVCREATEBROWSERSESS_02);
            if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) SibTr.debug(tc, e.getMessage(), e);

            StaticCATHelper.sendExceptionToClient(e,
                                                  CommsConstants.STATICCATBROWSER_RCVCREATEBROWSERSESS_03,  // d186970
                                                  conversation, requestNumber);

            // Attempt to clean up by closing the browse session we have already created.
            try
            {
               browserSession.close();
            }
            catch (SIException coreException)
            {
               //No FFDC code needed
               //Only FFDC if we haven't received a meTerminated event.
               if(!convState.hasMETerminated())
               {
                  FFDCFilter.processException(coreException, CLASS_NAME + ".rcvCreateBrowserSess",
                                              CommsConstants.STATICCATBROWSER_RCVCREATEBROWSERSESS_03);
               }

               if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) SibTr.debug(tc, coreException.getMessage(), coreException);

               // Can't do much here as we have already failed, so informing the client for
               // a second time wouldn't buy us anything (and it would upset the client).
            }
         }
      }

      if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.exit(tc, "rcvCreateBrowserSess");
   }

   /**
    * Processes a request to reset a browser session.  This is achieved by decoding the
    * request data and using it to locate the browser session to reset.
    *
    * @param request The data sent to us by the client which represents a request to reset the
    * browse session.  We must decode this data to determine which browse session it refers to.
    * @param conversation The conversation used to send the reset request.  This is required
    * because the browser sessions created by comms code are scoped by conversation.  We need
    * it's attachment object in order to locate the appropriate browser session.  In addition,
    * it is this conversation we will use when sending a reply to the client.
    * @param requestNumber The request number the request to reset the browser session came in
    * on.  This is required when replying.
    * @param allocatedFromBufferPool Set to true if the buffer containing data to decode was
    * allocated from a pool (and hence needs returning to the pool when we are finsihed).
    * @param partOfExchange Whether the client is expecting a reply to this call.
    */
   public static void rcvResetBrowse(CommsByteBuffer request, Conversation conversation,
                                     int requestNumber, boolean allocatedFromBufferPool,
                                     boolean partOfExchange)
   {
      if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.entry(tc, "rcvResetBrowse",
                                           new Object[]
                                           {
                                              request,
                                              conversation,
                                              ""+requestNumber,
                                              ""+allocatedFromBufferPool
                                            });

      // Strip information from reset request.
      short connectionObjectId = request.getShort();                 // Connection Id
      short browserSessionId = request.getShort();                   // Id of browser session.

      if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) SibTr.debug(tc, "rcvResetBrowse> connectionObjectId = "+connectionObjectId+
                                               "\nrcvResetBrowse> browserSessionId   = "+browserSessionId);

      BrowserSession browserSession = null;
      ConversationState convState = null;

      try
      {
         // Locate browser session from conversation state.
         convState = (ConversationState)conversation.getAttachment();
         CATMainConsumer mainConsumer = (CATMainConsumer)convState.getObject(browserSessionId);
         browserSession = mainConsumer.getBrowserSession();

         if (browserSession == null)
         {
            // The browser session from the main consumer should not be null here
            SIErrorException e = new SIErrorException(
               nls.getFormattedMessage("BROWSER_SESSION_NULL_SICO20", null, null)
            );

            FFDCFilter.processException(e, CLASS_NAME + ".rcvResetBrowse",
                                        CommsConstants.STATICCATBROWSER_RCVRESETBROWSERSESS_04);

            throw e;
         }
      }
      catch(NullPointerException npe)
      {
         FFDCFilter.processException(npe, CLASS_NAME + ".rcvResetBrowse",
                                     CommsConstants.STATICCATBROWSER_RCVRESETBROWSERSESS_01);

         if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) SibTr.debug(tc, "Browser session was null!", npe);

         // This is an internal error, so inform the client
         StaticCATHelper.sendExceptionToClient(npe,
                                               CommsConstants.STATICCATBROWSER_RCVRESETBROWSERSESS_01,   // d186970
                                               conversation, requestNumber);
      }

      if (browserSession != null)
      {
         try
         {
            browserSession.reset();
         }
         catch (SIException e)
         {
            //No FFDC code needed
            //Only FFDC if we haven't received a meTerminated event.
            if(!convState.hasMETerminated())
            {
               FFDCFilter.processException(e, CLASS_NAME + ".rcvResetBrowse",
                                           CommsConstants.STATICCATBROWSER_RCVRESETBROWSERSESS_02);
            }

            if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) SibTr.debug(tc, e.getMessage(), e);

            // Send error to client.
            StaticCATHelper.sendExceptionToClient(e,
                                                  CommsConstants.STATICCATBROWSER_RCVRESETBROWSERSESS_02,   // d186970
                                                  conversation, requestNumber);
         }
      }

      // Send response.
      CommsByteBuffer reply = poolManager.allocate();
      try
      {
         conversation.send(reply,
                           JFapChannelConstants.SEG_RESET_BROWSE_R,
                           requestNumber,
                           JFapChannelConstants.PRIORITY_MEDIUM,
                           true,
                           ThrottlingPolicy.BLOCK_THREAD,
                           null);
      }
      catch (SIException e)
      {
         FFDCFilter.processException(e, CLASS_NAME + ".rcvResetBrowse",
                                     CommsConstants.STATICCATBROWSER_RCVRESETBROWSERSESS_03);

         SibTr.error(tc, "COMMUNICATION_ERROR_SICO2020", e);

         // Can't really do much as a communications exception implies that
         // we have lost contact with the client anyway.
      }

      request.release(allocatedFromBufferPool);

      if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.exit(tc, "rcvResetBrowse");
   }
}
