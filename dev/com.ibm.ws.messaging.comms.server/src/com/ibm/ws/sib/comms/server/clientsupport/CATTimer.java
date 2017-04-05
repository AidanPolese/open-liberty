/*
* @start_prolog@
* Version: @(#) 1.27 SIB/ws/code/sib.comms.server.impl/src/com/ibm/ws/sib/comms/server/clientsupport/CATTimer.java, SIB.comms, WASX.SIB, aa1225.01 08/01/18 03:28:52 [7/2/12 05:59:00]
* ============================================================================
* IBM Confidential OCO Source Materials
* 
* 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70  Copyright IBM Corp. 2004, 2008
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
* Creation        030618 mattheg  Original
* d170639         030627 mattheg  Add NLS to all messages
* f169897.2       030708 mattheg  Convert to Core API 0.6
* f172297         030724 mattheg  Complete Core API 0.6 implementation
* F183828         031204 prestona Update CF + TCP prereqs to MS 5.1 level
* d186970         040116 mattheg  Overhaul the way we send exceptions to client
* F188491         030128 prestona Migrate to M6 CF + TCP Channel
* D217372         040719 mattheg  Move JFap constants -> JFapChannelConstants (not change-flagged)
* F201972.2       040727 mattheg  Core SPI Exceptions rework (not change flagged)
* D199177         040816 mattheg  JavaDoc
* D225856         041006 mattheg  Update FFDC class name (not change flagged)
* D329823         051207 mattheg  Trace improvements
* D361676         060412 mattheg  Supress FFDC's on SessionUnavailable when the timer pops
* D377648         060719 mattheg  Use CommsByteBuffer
* D441183         072307 mleming  Don't FFDC when calling terminated ME
* 492551          080117 mleming  Only register SICoreConnectionListener when required
* ============================================================================
*/
package com.ibm.ws.sib.comms.server.clientsupport;

import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ejs.util.am.AlarmListener;
import com.ibm.websphere.sib.exception.SIException;
import com.ibm.ws.ffdc.FFDCFilter;
import com.ibm.ws.sib.comms.CommsConstants;
import com.ibm.ws.sib.processor.MPConsumerSession;
import com.ibm.ws.sib.utils.ras.SibTr;
import com.ibm.wsspi.sib.core.exception.SISessionDroppedException;
import com.ibm.wsspi.sib.core.exception.SISessionUnavailableException;

/**
 * The listener class that will be notified when our timer expires.
 * This is used when doing a synchronous but non-blocking on the server.
 * 
 * @author Gareth Matthews
 */
public class CATTimer implements AlarmListener
{
   /** Class name for FFDC's */
   private static String CLASS_NAME = CATTimer.class.getName();

   /**
    * The asynch reader associated with the timer
    */
   private CATSyncAsynchReader asynchReader = null;
   
   /**
    * Register our trace component
    */
   private static final TraceComponent tc = SibTr.register(CATTimer.class,
                                                           CommsConstants.MSG_GROUP,
                                                           CommsConstants.MSG_BUNDLE);

   static {
      if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) SibTr.debug(tc, "Source info: @(#)SIB/ws/code/sib.comms.server.impl/src/com/ibm/ws/sib/comms/server/clientsupport/CATTimer.java, SIB.comms, WASX.SIB, aa1225.01 1.27");
   }
   
   /**
    * The constructer creates the CATTimer and associates the asynch reader
    * with it. Note that constructing this class does not start the timer.
    * This class is merely the class that will be notified when the timer
    * expires.
    * 
    * @param asynchReader The asynch reader that is also waiting
    *                     for a message.
    */
   public CATTimer(CATSyncAsynchReader asynchReader)
   {
      if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.entry(this, tc, "<init>", asynchReader);
      
      this.asynchReader = asynchReader;
      
      if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.exit(this, tc, "<init>");
   }
   
   /** 
    * This method is called by the Alarm manager when the timeout
    * is exceeded.
    * 
    * @param alarmObj The alarm object - not used.
    */
   public void alarm(Object alarmObj)
   {
      if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.entry(this, tc, "alarm", alarmObj);
      
      // Has the async reader already sent a message back to the client?
      boolean sessionAvailable = true;
      if (!asynchReader.isComplete())
      {
         if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) SibTr.debug(this, tc, "Async reader has not yet got a message");
         // If not stop the session...
         try 
         {
            try
            {
               asynchReader.stopSession();
            }
            catch (SISessionDroppedException e)
            {
               // No FFDC Code Needed
               if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) SibTr.debug(this, tc, "Caught a SISessionDroppedException", e);
               sessionAvailable = false;
               
               // If the session was unavailable this may be because the connection has been closed 
               // while we were in a receiveWithWait(). In this case, we should try and send an error
               // back to the client, but not worry too much if we cannot
               if (asynchReader.isConversationClosed())
               {
                  if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) 
                     SibTr.debug(this, tc, "The Conversation was closed - no need to panic");
               }
               else
               {
                  // The Conversation was not closed, throw the exception on so we can inform
                  // the client
                  throw e;
               }
            }
            catch (SISessionUnavailableException e)
            {
               // No FFDC Code Needed
               if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) SibTr.debug(this, tc, "Caught a SISessionUnavailableException", e);
               sessionAvailable = false;
               
               // See the comments above...
               if (asynchReader.isConversationClosed())
               {
                  if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) 
                     SibTr.debug(this, tc, "The Conversation was closed - no need to panic");
               }
               else
               {
                  throw e;
               }
            }
         }
         catch (SIException sis)
         {
            //No FFDC code needed
            //Only FFDC if we haven't received a meTerminated event.
            if(!asynchReader.hasMETerminated())
            {
               FFDCFilter.processException(sis, CLASS_NAME + ".alarm", 
                                           CommsConstants.CATTIMER_ALARM_01, this);
            }
            
            sessionAvailable = false;
            if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) SibTr.debug(tc, sis.getMessage(), sis);
            
            asynchReader.sendErrorToClient(sis,
                                           CommsConstants.CATTIMER_ALARM_01);
         }
         finally
         {
            //At this point we should deregister asyncReader as a SICoreConnectionListener as the current receive with wait is pretty much finished.
            //Note that there is a bit of a timing window where asyncReader.consumeMessages could be called and we end up deregistering 
            //the listener twice but that is allowed by the API. We minimize this window by only doing a dereg if the asyncReader isn't complete.
            if(!asynchReader.isComplete())
            {
               try
               {
                  final MPConsumerSession mpSession = (MPConsumerSession) asynchReader.getCATMainConsumer().getConsumerSession();
                  mpSession.getConnection().removeConnectionListener(asynchReader);
               }
               catch(SIException e)
               {
                  //No FFDC code needed   
                  if(TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) SibTr.debug(this, tc, e.getMessage(), e);
                  
                  //No need to send an exception back to the client in this case as we are only really performing tidy up processing.
                  //Also, if it is a really bad problem we are likely to have already send the exception back in the previous catch block.
               }
            }
         }
      }
      
      if (sessionAvailable)
      {
         // ...and check again.
         // If the async reader has still not sent a message at this point we
         // assume a timeout and inform the client
         if (asynchReader.isComplete())
         {
            if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) SibTr.debug(this, tc, "Async reader got a message");
         }
         else
         {
            if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) SibTr.debug(this, tc, "No message received");
            
            asynchReader.sendNoMessageToClient();
         }
      }
            
      if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.exit(this, tc, "alarm");
   }
}
