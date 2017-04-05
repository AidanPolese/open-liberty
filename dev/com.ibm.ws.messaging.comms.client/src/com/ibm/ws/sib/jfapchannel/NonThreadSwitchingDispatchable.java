/*
 * @start_prolog@
 * Version: @(#) 1.6 SIB/ws/code/sib.jfapchannel.client/src/com/ibm/ws/sib/jfapchannel/NonThreadSwitchingDispatchable.java, SIB.comms, WASX.SIB, uu1215.01 08/06/10 10:37:53 [4/12/12 22:14:16]
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
 * Creation        040506 mattheg  Original
 * D528455         080610 djvines  Get the FFDCs correct
 * ============================================================================
 */
package com.ibm.ws.sib.jfapchannel;

import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.ffdc.FFDCFilter;
import com.ibm.ws.sib.utils.ras.SibTr;

/**
 * This abstract class represents a non thread switching dispactchable. Users of the JFap channel,
 * when called on their receive listener for the thread context can return the instance of this
 * class and no thread switch will occur when the dispatcher comes to invoke the request.
 * 
 * @author Gareth Matthews
 */
public abstract class NonThreadSwitchingDispatchable implements Dispatchable
{
   /** Trace */
   private static final TraceComponent tc = SibTr.register(NonThreadSwitchingDispatchable.class, 
                                                           JFapChannelConstants.MSG_GROUP, 
                                                           JFapChannelConstants.MSG_BUNDLE);
   
   //@start_class_string_prolog@
   /* The version for trace */
   private static final String $sccsid = "@(#) 1.6 SIB/ws/code/sib.jfapchannel.client/src/com/ibm/ws/sib/jfapchannel/NonThreadSwitchingDispatchable.java, SIB.comms, WASX.SIB, uu1215.01 08/06/10 10:37:53 [4/12/12 22:14:16]";
   //@end_class_string_prolog@

   /** The singleton instance */
   private static NonThreadSwitchingDispatchable instance = null;
   
   /** The exception that caused any failure */
   private static Exception createException = null;
   
   /**
    * Static initialiser - creates the actual instance of the class.
    */
   static
   {
      if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) SibTr.debug(tc, $sccsid);
      if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.entry(tc, "<clinit>");
      
      try
      {
         Class disClass = Class.forName(JFapChannelConstants.NON_THREAD_SWITCHING_DISPATCHER_CLASS);
         instance = (NonThreadSwitchingDispatchable) disClass.newInstance();
      }
      catch (Exception e)
      {
         FFDCFilter.processException(e, "com.ibm.ws.sib.jfapchannel.NonThreadSwitchingDispatchable.<clinit>",
                                     JFapChannelConstants.NONTSDISPATCHABLE_STINIT_01);
                                     
         createException = e;
         
         SibTr.error(tc, "NO_NON_TSWITCH_IMPL_SICJ0033", 
                     new Object[]
                     {
                        JFapChannelConstants.NON_THREAD_SWITCHING_DISPATCHER_CLASS,
                        e
                     });
                     
         if (TraceComponent.isAnyTracingEnabled() && tc.isEventEnabled()) SibTr.exception(tc, e);
      }
      
      if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.exit(tc, "<clinit>");
   }
   
   /**
    * @return Returns the instance of the class that was created above.
    * 
    * @throws Exception if the create failed.
    */
   public static Dispatchable getInstance() throws Exception
   {
      if (instance == null) throw createException;
      return instance;
   }
}
