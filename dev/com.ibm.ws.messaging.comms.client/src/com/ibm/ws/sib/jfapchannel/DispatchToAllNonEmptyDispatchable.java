/*
 * @start_prolog@
 * Version: @(#) 1.4 SIB/ws/code/sib.jfapchannel.client/src/com/ibm/ws/sib/jfapchannel/DispatchToAllNonEmptyDispatchable.java, SIB.comms, WASX.SIB, uu1215.01 05/02/04 09:54:32 [4/12/12 22:14:16]
 * ============================================================================
 * IBM Confidential OCO Source Materials
 * 
 * 5724-I63, 5724-H88, 5655-N01, 5733-W60          (C) Copyright IBM Corp. 2004, 2005
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
 * Creation        040701 mattheg  Original
 * ============================================================================
 */
package com.ibm.ws.sib.jfapchannel;

import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.ffdc.FFDCFilter;
import com.ibm.ws.sib.utils.ras.SibTr;

/**
 * This abstract class represents a dispatch to all dispactchable. Users of the JFap channel,
 * when called on their receive listener for the thread context can return the instance of this
 * class and the request will be dispatched to all dispatch queues but only invoked by the last
 * queue that finds him.
 * 
 * @author Gareth Matthews
 */
public abstract class DispatchToAllNonEmptyDispatchable implements Dispatchable
{
   /** Trace */
   private static final TraceComponent tc = SibTr.register(DispatchToAllNonEmptyDispatchable.class, 
                                                           JFapChannelConstants.MSG_GROUP, 
                                                           JFapChannelConstants.MSG_BUNDLE);
   
   /** The singleton instance */
   private static DispatchToAllNonEmptyDispatchable instance = null;
   
   /** The exception that caused any failure */
   private static Exception createException = null;
   
   /**
    * Static initialiser - creates the actual instance of the class.
    */
   static
   {
      if (tc.isDebugEnabled()) SibTr.debug(tc, "@(#)SIB/ws/code/sib.jfapchannel.client/src/com/ibm/ws/sib/jfapchannel/DispatchToAllNonEmptyDispatchable.java, SIB.comms, WASX.SIB, uu1215.01 1.4");
      if (tc.isEntryEnabled()) SibTr.entry(tc, "static <init>");
      
      try
      {
         Class disClass = Class.forName(JFapChannelConstants.DISPATCH_TO_ALL_NONEMPTY_DISPATCHER_CLASS);
         instance = (DispatchToAllNonEmptyDispatchable) disClass.newInstance();
      }
      catch (Exception e)
      {
         FFDCFilter.processException(e, "com.ibm.ws.sib.jfapchannel.DispatchToAllNonEmptyDispatchable",
                                     JFapChannelConstants.DISPATCHTOALLNONEMPTY_STINIT_01);
                                     
         createException = e;
         
         SibTr.error(tc, "NO_DISPATCHTOALL_IMPL_SICJ0034", 
                     new Object[]
                     {
                        JFapChannelConstants.DISPATCHTOALLNONEMPTY_STINIT_01,
                        e
                     });
                     
         if (tc.isEventEnabled()) SibTr.exception(tc, e);
      }
      
      if (tc.isEntryEnabled()) SibTr.exit(tc, "static <init>");
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
