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
 * 280486.1        050613 kgoodson Original
 * 352642          060419 susana   Changed MSG_BUNDLE to file which exists
 * ============================================================================
 */
/**
 *   This class just contains component-wide constants.
 *   It does not include any constants used by other components.
 *
 */
package com.ibm.ws.sib.mfp.jmf;

import com.ibm.ws.sib.mfp.MfpConstants;

public class JmfConstants {

  public final static String MSG_GROUP  = com.ibm.ws.sib.utils.TraceGroups.TRGRP_JMF;
  public final static String MSG_BUNDLE = MfpConstants.MSG_BUNDLE;

}
