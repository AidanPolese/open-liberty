/*
 * 
 * 
 * ===========================================================================
 * IBM Confidential OCO Source Material
 *
 * Copyright IBM Corp. 2012
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 * ===========================================================================
 * 
 *
 * Change activity:
 *
 * Reason           Date   Origin   Description
 * ---------------  ------ -------- -------------------------------------------------
 * 186484.18        290604 tevans   Remote queue point control improvements
 * 219133           050804 cwilkin  Remove deprecated findByID calls in Runtime pkg
 * SIB0102.mp.2     151106 cwilkin  Link Transmission Controllables
 * SIB0105.mp.7     250607 cwilkin  Link Publication Point Controls
 * ===========================================================================
 */
package com.ibm.ws.sib.processor.runtime.impl;

public class RuntimeControlConstants
{
  public static final String QUEUE_ID_INSERT = "_QUEUE_";
  public static final String MEDIATION_ID_INSERT = "_MEDIATION_";
  public static final String REMOTE_QUEUE_ID_INSERT = "_REMOTE_QUEUE_";
  public static final String REMOTE_MEDIATION_ID_INSERT = "_REMOTE_MEDIATION_";
  public static final String LINK_TRANSMITTER_ID_INSERT = "_LINK_TRANSMITTER";
}
