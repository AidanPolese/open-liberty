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
 * LIDB2117        040729 vaughton Original
 * 220097.0        041203 vaughton Replace use of temp message number
 * 341625          060126 djvines  Resolve unused imports
 * 410953          061211 djvines  Only issue the message once when the property changes
 * ============================================================================
 */

package com.ibm.ws.sib.utils;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.sib.utils.ras.SibTr;

/**
 * This class contains common runtime methods
 */

public final class Runtime {

  private static final TraceComponent tc = SibTr.register(Runtime.class, UtConstants.MSG_GROUP, UtConstants.MSG_BUNDLE);

  private static final ConcurrentMap<String,String> seenProperties = new ConcurrentHashMap<String, String>();
  
  /**
   * This method should be called each time a SIB property value is assigned a
   * none default value. An informational message is output for serviceability
   * reasons so that it is obvious that a property value has been changed.
   *
   * @param name the name of the property that has been changed
   * @param value the new value assigned to the changed property
   */

  public static void changedPropertyValue (String name, String value) {
    if (tc.isEntryEnabled()) SibTr.entry(tc, "changedPropertyValue");

    if (value == null) value = "null"; // Ensure that the new value is non-null (here we're only using for a message insert)

    if (!value.equals(seenProperties.put(name,value)))
    {
      // We haven't seen the property before or it's changed, so issue the message
      SibTr.info(tc, "RUNTIME_CWSIU0001", new Object[] {name,value});   //220097.0
    }

    if (tc.isEntryEnabled()) SibTr.exit(tc, "changedPropertyValue");
  }

}
