/*
 * @start_prolog@
 * Version: @(#) 1.27 SIB/ws/code/sib.trm.client.impl/src/com/ibm/ws/sib/trm/client/Utils.java, SIB.trm, WASX.SIB, aa1225.01 05/11/02 03:25:12 [7/2/12 05:58:42]
 * ============================================================================
 * IBM Confidential OCO Source Materials
 * 
 * 5724-I63, 5724-H88, 5655-N02, 5733-W70  (C) Copyright IBM Corp. 2004, 2005
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
 * LIDB2117        030609 vaughton Original
 * 290290          051028 gelderd  Additional trace required in TRM
 * ============================================================================
 */

/*
 * Client attach utility methods
 */

package com.ibm.ws.sib.trm.client;

import java.util.List;

import com.ibm.ejs.ras.TraceNLS;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.sib.admin.JsMessagingEngine;
import com.ibm.ws.sib.trm.TrmConstants;
import com.ibm.ws.sib.trm.impl.TrmConstantsImpl;
import com.ibm.ws.sib.utils.ras.SibTr;

public class Utils {

  //@start_class_string_prolog@
  public static final String $sccsid = "@(#) 1.27 SIB/ws/code/sib.trm.client.impl/src/com/ibm/ws/sib/trm/client/Utils.java, SIB.trm, WASX.SIB, aa1225.01 05/11/02 03:25:12 [7/2/12 05:58:42]";
  //@end_class_string_prolog@

  private static final TraceComponent tc = SibTr.register(Utils.class, TrmConstants.MSG_GROUP, TrmConstants.MSG_BUNDLE);
  private static final TraceNLS nls = TraceNLS.getTraceNLS(TrmConstantsImpl.MSG_BUNDLE);

  /*
   * Method to return the failure reason message received from the other
   * end. Failure reason messages are retrieved in the originating end
   * so that they appear in the correct locale.
   */

  public static String getFailureMessage (List failure) {
    if (tc.isEntryEnabled()) SibTr.entry(tc, "getFailureMessage", new Object[] { failure });

    int len = failure.size();
    String[] insert = new String[len-1];

    for (int i=0; i < (len-1); i++) {
      insert[i] = (String)failure.get(i+1);
    }

    String rc = nls.getFormattedMessage((String)failure.get(0), insert, null);
    
    if (tc.isEntryEnabled()) SibTr.exit(tc, "getFailureMessage", rc);
    return rc;
  }

  /*
   * Given a JsMessagingEngine return just the subnet name which is obtained
   * from the part of TRM which is specific to the messaging engine.
   */

  public static String getSubnet (JsMessagingEngine jsme) {
    if (tc.isEntryEnabled()) SibTr.entry(tc, "getSubnet", new Object[] { jsme });

    //Venu Liberty COMMS .. Just returning DefaultSubnet as dummy like DefaultBus
    if (tc.isEntryEnabled()) SibTr.exit(tc, "getSubnet", "DefaultSubnet");
    return "c";
  }

  /*
   * Given a JsMessagingEngine return just the engine name
   */

  public static String getName (JsMessagingEngine jsme) {
    if (tc.isEntryEnabled()) SibTr.entry(tc, "getName", new Object[] { jsme });
    
    String rc =  jsme.getName();
    
    if (tc.isEntryEnabled()) SibTr.exit(tc, "getName", rc);
    return rc;
  }

  /*
   * Trace formatting methods
   */

  private final static String ls = System.getProperty("line.separator");

  private static String bound (String s, String ch) {

    StringBuffer sb = new StringBuffer();

    String text = " " + s + " ";
    int pad  = (80 - text.length()) / 2;

    if (ch == null) {
      ch = "=";
    }

    sb.append(ls + ls);
    for (int i=0; i < pad; i++ ) {
      sb.append(ch);
    }
    sb.append(text);
    for (int i=0; i < pad; i++ ) {
      sb.append(ch);
    }
    sb.append(ls);

    return sb.toString();
  }

  public static String inBound (String s) {
    return bound("Inbound " + s, null);
  }

  public static String outBound (String s) {
    return bound("Outbound " + s, null);
  }

  public static String commsFailure (String s) {
    return bound("Communications failure " + s, "*");
  }

  public static String messagingEngineDied (String s) {
    return bound("Messaging Engine died " + s, "*");
  }

}
