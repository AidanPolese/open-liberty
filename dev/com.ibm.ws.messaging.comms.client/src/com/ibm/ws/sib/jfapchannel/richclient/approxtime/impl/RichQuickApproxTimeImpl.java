/*
 * @start_prolog@
 * Version: @(#) 1.4 SIB/ws/code/sib.jfapchannel.client.rich.impl/src/com/ibm/ws/sib/jfapchannel/approxtime/impl/RichQuickApproxTimeImpl.java, SIB.comms, WASX.SIB, uu1215.01 08/05/21 05:29:28 [4/12/12 22:14:18]
 * ============================================================================
 * IBM Confidential OCO Source Materials
 *
 * 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70  Copyright IBM Corp. 2003, 2008
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
 * SIB0048b.com.5  060913 mattheg  JFap channel for Portly client rework
 * 511981          080411 vaughton Performance - remove trace statements + other tweaks
 * 512855          080415 sibcopyr Automatic update of trace guards
 * 522407          080521 djvines  Use Long.valueOf
 * ============================================================================
 */
package com.ibm.ws.sib.jfapchannel.richclient.approxtime.impl;

import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.sib.jfapchannel.JFapChannelConstants;
import com.ibm.ws.sib.jfapchannel.approxtime.QuickApproxTime;
import com.ibm.ws.sib.utils.ras.SibTr;

/**
 * Rich client implementation of QuickApproxTime which uses the real TCP channel QuickApproxTime
 * under the covers to get an approximate time.
 */
public class RichQuickApproxTimeImpl implements QuickApproxTime {
  private static final TraceComponent tc = SibTr.register(RichQuickApproxTimeImpl.class, JFapChannelConstants.MSG_GROUP,  JFapChannelConstants.MSG_BUNDLE);

  //@start_class_string_prolog@
  public static final String $sccsid = "@(#) 1.4 SIB/ws/code/sib.jfapchannel.client.rich.impl/src/com/ibm/ws/sib/jfapchannel/approxtime/impl/RichQuickApproxTimeImpl.java, SIB.comms, WASX.SIB, uu1215.01 08/05/21 05:29:28 [4/12/12 22:14:18]";
  //@end_class_string_prolog@

  static {
    if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) SibTr.debug(tc, "Source Info: " + $sccsid);
  }

   /**
   * @see com.ibm.ws.sib.jfapchannel.approxtime.QuickApproxTime#getApproxTime()
   */
  public long getApproxTime() {
    // For performance reasons there is no trace entry statement here
    // For performance reasons there is no trace exit statement here
      return com.ibm.wsspi.timer.QuickApproxTime.getApproxTime();
  }

  /**
   * @see com.ibm.ws.sib.jfapchannel.approxtime.QuickApproxTime#setInterval(long)
   */
  public void setInterval(long interval) {
    if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.entry(this, tc, "setInterval", Long.valueOf(interval));
    //Venu Liberty COMMS .. TODO
    //wasQuickApproxTime.setInterval(interval);
    if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.exit(this, tc, "setInterval");
  }
}
