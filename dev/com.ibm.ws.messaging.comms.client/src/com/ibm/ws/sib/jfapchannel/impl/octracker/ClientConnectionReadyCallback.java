/*
 * @start_prolog@
 * Version: @(#) 1.7 SIB/ws/code/sib.jfapchannel.client.common.impl/src/com/ibm/ws/sib/jfapchannel/impl/octracker/ClientConnectionReadyCallback.java, SIB.comms, WASX.SIB, uu1215.01 06/10/02 04:23:32 [4/12/12 22:14:15]
 * ============================================================================
 * IBM Confidential OCO Source Materials
 * 
 * 5724-J08, 5724-I63, 5724-H88, 5655-N01, 5733-W61  (C) Copyright IBM Corp. 2004, 2006 
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
 * F191566         040301 prestona Created
 * SIB0048b.com.5  060913 mattheg  JFap channel for Portly client rework
 * ============================================================================ 
 */ 
 
package com.ibm.ws.sib.jfapchannel.impl.octracker;

import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.sib.jfapchannel.JFapChannelConstants;
import com.ibm.ws.sib.jfapchannel.framework.ConnectRequestListener;
import com.ibm.ws.sib.jfapchannel.framework.NetworkConnection;
import com.ibm.ws.sib.utils.Semaphore;

import com.ibm.ws.sib.utils.ras.SibTr;

/**
 * Callback used by clients to detect that a connection has been made.
 * Essentially posts a semaphore.
 */
class ClientConnectionReadyCallback implements ConnectRequestListener
{
   private static final TraceComponent tc = SibTr.register(ClientConnectionReadyCallback.class,
                                                           JFapChannelConstants.MSG_GROUP,
                                                           JFapChannelConstants.MSG_BUNDLE);

   static
   {
      if (tc.isDebugEnabled()) SibTr.debug(tc, "@(#) SIB/ws/code/sib.jfapchannel.client.common.impl/src/com/ibm/ws/sib/jfapchannel/impl/octracker/ClientConnectionReadyCallback.java, SIB.comms, WASX.SIB, uu1215.01 1.7");
   }

   private Semaphore semaphore;

   private Exception exception;

   protected ClientConnectionReadyCallback(Semaphore s)
   {
      if (tc.isEntryEnabled()) SibTr.entry(this, tc, "<init>", s);
      semaphore = s;
      if (tc.isEntryEnabled()) SibTr.exit(this, tc, "<init>");
   }

   protected Exception getException()
   {
      if (tc.isEntryEnabled()) SibTr.entry(this, tc, "getException");
      if (tc.isEntryEnabled()) SibTr.exit(this, tc, "getException", exception);
      return exception;
   }

   protected boolean connectionSucceeded()
   {
      if (tc.isEntryEnabled()) SibTr.entry(this, tc, "connectionSucceeded");
      boolean returnValue = exception == null;
      if (tc.isEntryEnabled()) SibTr.exit(this, tc, "connectionSucceeded", "" + returnValue);
      return returnValue;
   }

   public void connectRequestSucceededNotification(NetworkConnection vc)
   {
      if (tc.isEntryEnabled()) SibTr.entry(this, tc, "connectRequestSucceededNotification", vc);
      semaphore.post();
      if (tc.isEntryEnabled()) SibTr.exit(this, tc, "connectRequestSucceededNotification");
   }

   public void connectRequestFailedNotification(Exception e)
   {
      if (tc.isEntryEnabled()) SibTr.entry(this, tc, "connectRequestFailedNotification", e);
      if (tc.isEventEnabled() && (e != null)) SibTr.exception(this, tc, e);
      exception = e;
      semaphore.post();
      if (tc.isEntryEnabled()) SibTr.exit(this, tc, "connectRequestFailedNotification");
   }
}

