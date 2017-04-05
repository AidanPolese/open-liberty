/*
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
 * Change activity:
 *
 * Reason          Date   Origin   Description
 * --------------- ------ -------- --------------------------------------------
 *                                 Version X copied from CMVC
 * ============================================================================
 */
package com.ibm.wsspi.sib.pacing;

import com.ibm.ejs.ras.RasHelper;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.ffdc.FFDCFilter;
import com.ibm.ws.sib.utils.ras.SibTr;
import com.ibm.ws.sib.admin.JsConstants;

/**
 * @author wallisgd
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */

public abstract class MessagePacingControlFactory {

  private static String CLASS_NAME = MessagePacingControlFactory.class.getName();

  private static final TraceComponent tc =
    SibTr.register(MessagePacingControlFactory.class, JsConstants.TRGRP_AS, JsConstants.MSG_BUNDLE);


  // Reference to singleton implementation of MPC.
  private static MessagePacingControl singleton = null;

  // getInstance()
  synchronized public static final MessagePacingControl getInstance() {
    // If not a server process, ie.the client, then return null as Pacing is not available on the client
    if (!RasHelper.isServer()) {
      if (tc.isDebugEnabled()) {
        SibTr.debug(tc, "MessagePacingControl not available in a client process");
      }
      return null;
    }
  
    if (singleton == null) {
      try {
        Class cls = Class.forName("com.ibm.wsspi.sib.pacing.impl.MessagePacingControlImpl");
        singleton = (MessagePacingControl) cls.newInstance();

      } catch (ClassNotFoundException cnfe) {
	  // No FFDC Code Needed
          // This happens in eWAS, as it's rather cut down from full WAS (but returns true to isServer())
          if(tc.isDebugEnabled()) SibTr.debug(tc, "MessagePacingControl not available within application server");
          return null;
      }
        catch (Exception e) {
        FFDCFilter.processException(e, CLASS_NAME + ".getInstance", "1");
        if (tc.isEventEnabled()) {
          SibTr.exception(tc, e);
        }
        throw new RuntimeException(e);
      }
    }
    return singleton;
  }

}
