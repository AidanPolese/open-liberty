/*
 * @start_prolog@                                                    
 * Version: @(#) 1.6 SIB/ws/code/sib.comms.client.impl/src/com/ibm/ws/sib/comms/common/CommsDiagnosticModule.java, SIB.comms, WASX.SIB, uu1215.01 06/10/05 05:52:20 [4/12/12 22:14:21]
 * ============================================================================
 * IBM Confidential OCO Source Materials
 * 
 * 5724-I63, 5724-H88, 5655-N02, 5733-W70 (C) Copyright IBM Corp. 2005, 2006 
 * 
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 * ============================================================================
 * @end_prolog@
 * 
 * Change activity:
 *
 * Reason            Date   Origin   Description
 * ---------------   ------ -------- ------------------------------------------
 * Creation          051107 mattheg  Extra information in FFDC's
 * D336596           060109 mattheg  Clone lists before iterating over them
 * D363463           060421 prestona ME-ME heartbeat timeout
 * SIB0048b.com.1    060901 mattheg  Move into server package
 * D395634           061005 mattheg  Re-introduce FFDC module back into client
 * ============================================================================
 */
package com.ibm.ws.sib.comms.common;



import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.ffdc.IncidentStream;
import com.ibm.ws.sib.comms.CommsConstants;
import com.ibm.ws.sib.utils.ffdc.SibDiagnosticModule;
import com.ibm.ws.sib.utils.ras.SibTr;

/**
 * This is the Comms FFDC diagnostic module. This module will be invoked when an FFDC record is 
 * written that has comms code nearest the top of the call stack. At this point, the state of any
 * client conversations and server side client conversations will be dumped into the FFDC.
 * 
 * @author Gareth Matthews
 */
public abstract class CommsDiagnosticModule extends SibDiagnosticModule
{
   /** Register Class with Trace Component */
   private static final TraceComponent tc = SibTr.register(CommsDiagnosticModule.class,
                                                           CommsConstants.MSG_GROUP,
                                                           CommsConstants.MSG_BUNDLE);
   
   /** Class name for FFDC's */
   private static final String CLASS_NAME = CommsDiagnosticModule.class.getName();

   /** Singleton instance of the active diagnostic module */
   private static CommsDiagnosticModule _instance = null;
   
   /** The list of packages we are interested in matching */
   private static String[] packageList = new String[] 
                                         { 
                                            "com.ibm.ws.sib.comms.client",
                                            "com.ibm.ws.sib.comms.common",
                                            "com.ibm.ws.sib.comms.server",
                                            "com.ibm.ws.sib.comms.tests"
                                         };
   
   /**
    * Initialises the diagnostic module.
    */
   public static synchronized void initialise()
   {
      // Only initialise if it has not already been created
      if (_instance == null)
      {
         // Check what environment we are running in. If we are in server mode we must use the 
         // server diagnostic module.
    	 //Liberty COMMS TODO:
    	 //For now not enabling server side diagnostics as can not load SERVER_DIAG_MODULE_CLASS in a clean manner
    	 // without having dependency of COMMs server ( at least more than 4/5 classes are needed) which defeat
    	 // the purpose of COMMS client independence w.r.to COMMS server
    	 //Has to rework on load SERVER_DIAG_MODULE_CLASS without too many dependencies of COMMS server.
    	
    	 /*
         if (RuntimeInfo.isServer())
         {
            if (tc.isDebugEnabled()) SibTr.debug(tc, "We are in Server mode");
            
            try
            {
               Class clazz = Class.forName(CommsConstants.SERVER_DIAG_MODULE_CLASS);
               Method getInstanceMethod = clazz.getMethod("getInstance", new Class[0]);
               _instance = (CommsDiagnosticModule) getInstanceMethod.invoke(null, (Object[]) null);
            }
            catch (Exception e)
            {
               FFDCFilter.processException(e, CLASS_NAME + ".initialise", 
                                           CommsConstants.COMMSDIAGMODULE_INITIALIZE_01,
                                           CommsConstants.SERVER_DIAG_MODULE_CLASS);
               
               if (tc.isDebugEnabled()) SibTr.debug(tc, "Unable to load the Comms Server Diagnostic Module", e);
               
               // In this case, fall out of here with a null _instance and default to the client
               // one. At least in that case we get _some_ diagnostics...
               // This is the point where I mention that that shouldn't ever happen :-).
            }
         }
         */

         // In all other cases use the client diagnostic module. Note we can instantiate this
         // directly as we are located in the same build component
         if (_instance == null)
         {
            if (tc.isDebugEnabled()) SibTr.debug(tc, "We are NOT in Server mode");
            
            _instance = ClientCommsDiagnosticModule.getInstance();
         }
         
         // Now register the packages
         _instance.register(packageList);
      }
   }
   
   /**
    * Called when an FFDC is generated and the stack matches one of our packages.
    * 
    * @see com.ibm.ws.sib.utils.ffdc.SibDiagnosticModule#ffdcDumpDefault(java.lang.Throwable, com.ibm.ws.ffdc.IncidentStream, java.lang.Object, java.lang.Object[], java.lang.String)
    */
   public void ffdcDumpDefault(Throwable t, IncidentStream is, Object callerThis, Object[] objs, String sourceId) 
   {
      if (tc.isEntryEnabled()) SibTr.entry(this, tc, "ffdcDumpDefault", 
                                           new Object[]{t, is, callerThis, objs, sourceId});
      
      // Dump the SIB information
      super.ffdcDumpDefault(t, is, callerThis, objs, sourceId);
      
      is.writeLine("\n= ============= SIB Communications Diagnostic Information =============", "");
      is.writeLine("Current Diagnostic Module: ", _instance);
      
      dumpJFapClientStatus(is);
      dumpJFapServerStatus(is);
      
      if (tc.isEntryEnabled()) SibTr.exit(this, tc, "ffdcDumpDefault");
   }
   
   /**
    * Implemented to dump the status of outbound (client) connections that are currently active.
    * 
    * @param is
    */
   protected abstract void dumpJFapClientStatus(IncidentStream is);
   
   /**
    * Implemented to dump the status of inbound (server) connections that are currently active.
    * 
    * @param is
    */
   protected abstract void dumpJFapServerStatus(IncidentStream is);
}
