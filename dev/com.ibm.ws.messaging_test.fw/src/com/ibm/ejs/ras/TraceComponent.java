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
 * Reason           Date   Origin   Description
 * ---------------  ------ -------- ------------------------------------------
 * fLIDB3418-56.1   050421 nottinga Original
 * d405087          061128 djvines  Support trace specification via java property
 * ===========================================================================
 */
package com.ibm.ejs.ras;

import java.util.Enumeration;
import java.util.Properties;

import com.ibm.websphere.ws.sib.unittest.ras.Trace;
import com.ibm.ws.sib.unittest.ras.Logger;

public class TraceComponent extends TraceElement
{
  private Dumpable _dumpable;
  private String _bundle;

  TraceComponent(Logger logger, String bundle)
  {
    super(logger);
    _bundle = bundle;
  }

  public final boolean isDumpEnabled()
  {
    return false;
  }

  public final String getResourceBundleName()
  {
    return _bundle;
  }

  void registerDumpable(Dumpable d)
  {
    _dumpable = d;
  }

  Dumpable getRegisteredDumpable()
  {
    return _dumpable;
  }

  public static boolean isAnyTracingEnabled()
  {
    return TraceElement.isAnyTracingEnabled();
  }

}
