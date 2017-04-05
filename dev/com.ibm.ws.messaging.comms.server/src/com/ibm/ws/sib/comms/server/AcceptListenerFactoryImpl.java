/*
 * @start_prolog@
 * Version: @(#) 1.1 SIB/ws/code/sib.comms.server.impl/src/com/ibm/ws/sib/comms/server/AcceptListenerFactoryImpl.java, SIB.comms, WASX.SIB, aa1225.01 07/09/03 09:31:43 [7/2/12 05:59:34]
 * ============================================================================
 * IBM Confidential OCO Source Materials
 * 
 * 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70 Copyright IBM Corp. 2007
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
 * SIB0100.wmq.3   070813 mleming  Allow WMQRA to use TCP Proxy Bridge
 * ============================================================================
 */
package com.ibm.ws.sib.comms.server;

import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.sib.comms.CommsConstants;
import com.ibm.ws.sib.jfapchannel.AcceptListener;
import com.ibm.ws.sib.jfapchannel.server.AcceptListenerFactory;
import com.ibm.ws.sib.utils.ras.SibTr;

/**
 * The Comms implementation of an accept listener that returns the generic listener which does
 * the real work.
 */
public class AcceptListenerFactoryImpl implements AcceptListenerFactory
{
   //@start_class_string_prolog@
   public static final String $sccsid = "@(#) 1.1 SIB/ws/code/sib.comms.server.impl/src/com/ibm/ws/sib/comms/server/AcceptListenerFactoryImpl.java, SIB.comms, WASX.SIB, aa1225.01 07/09/03 09:31:43 [7/2/12 05:59:34]";
   //@end_class_string_prolog@
   
   private static final TraceComponent tc = SibTr.register(AcceptListenerFactoryImpl.class, CommsConstants.MSG_GROUP, CommsConstants.MSG_BUNDLE);
   
   static
   {
      if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) SibTr.debug(tc, $sccsid);
   }
   
   public AcceptListener manufactureAcceptListener()
   {
      if(TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.entry(this, tc, "manufactureAcceptListener");

      final AcceptListener al = new GenericTransportAcceptListener();
      
      if(TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.exit(this, tc, "manufactureAcceptListener", al);
      return al;
   }
}
