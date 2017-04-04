/*
 * 
 * 
 * ============================================================================
 * IBM Confidential OCO Source Materials
 * 
 * Copyright IBM Corp. 2012
 * 
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 * ============================================================================
 * 
 *
 * Change activity:
 *
 * Reason          Date   Origin   Description
 * --------------- ------ -------- --------------------------------------------
 * LIDB2117        031111 vaughton Original
 * ============================================================================
 */

package com.ibm.ws.sib.trm.wlm.server;

import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.sib.trm.impl.TrmConstantsImpl;
import com.ibm.ws.sib.utils.ras.SibTr;

// This class defines all constants used to build WLM cluster maps

public final class Constants  {

  private static final TraceComponent tc = SibTr.register(Constants.class,TrmConstantsImpl.MSG_GROUP,TrmConstantsImpl.MSG_BUNDLE);

  // Key constants

  public static final String WLM_KEY_CAPABILITY    = "capability";
  public static final String WLM_KEY_IB_ME_UUID    = "inboundMeUuid";
  public static final String WLM_KEY_OB_ME_UUID    = "outboundMeUuid";

  // Key value constants

  private static final String prefix = "WSAF_SIB_";

  public static final String WLM_BRIDGE            = prefix + "BRIDGE";
  public static final String WLM_DESTINATIONS      = prefix + "DESTINATIONS";
  public static final String WLM_DESTINATION_UUID  = prefix + "DESTINATION_UUID";
  public static final String WLM_LINKS             = prefix + "LINKS";
  public static final String WLM_LINK_INFO         = prefix + "LINK_INFO";
  public static final String WLM_LINK_UUID         = prefix + "LINK_UUID";

  

}
