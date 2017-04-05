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
 * 164142          230403 vaughton Original
 * 165962          120503 vaughton common -> utils
 * 197712          080404 drphill  made class public so it can be used by trace in
 *                        collections
 * 194466.1.1      210404 jhumber  Add TopicWildcardTranslation factory class constant
 * ============================================================================
 */

/*
 * This class contains common services constants used by the common package
 * interface and as such deliberately has package scope.
 */

package com.ibm.ws.sib.utils;

public class UtConstants {

  public final static String MSG_GROUP  = TraceGroups.TRGRP_UTILS;
  public final static String MSG_BUNDLE = "com.ibm.ws.sib.utils.CWSIUMessages";
  public final static String TWT_FACTORY_CLASS = "com.ibm.ws.sib.utils.TopicWildcardTranslationImpl";

  public final static String PROBE_1 = "1";
  public final static String PROBE_2 = "2";
  public final static String PROBE_3 = "3";
  public final static String PROBE_4 = "4";
  public final static String PROBE_5 = "5";
  public final static String PROBE_6 = "6";
  public final static String PROBE_7 = "7";
  public final static String PROBE_8 = "8";
  public final static String PROBE_9 = "9";
}
