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
 * d412471          061222 djvines  Allow all tracing to be disabled  
 * ===========================================================================
 */

package com.ibm.ejs.ras;

import com.ibm.ws.sib.unittest.ras.Level;
import com.ibm.ws.sib.unittest.ras.Logger;

public class TraceElement extends SibTraceBaseClass
{ 
  public static boolean isAnyTracingEnabled() {
     return SibTraceBaseClass.isAnyTracingEnabled();
  }

  public Logger _logger;

  TraceElement(Logger logger)
  {
    _logger = logger;
  }

  /**
   * Return name of this trace element.
   */
  public final String getName()
  {
    return _logger.getSource();
  }

  /**
   * Determine if debug tracing is currently enabled for this <code>TraceElement</code>,
   * <p>
   * @return true if debug tracing is enabled, else return false.
   */
  public final boolean isDebugEnabled()
  {
    return SibTraceBaseClass.isAllTracingEnabled() || _logger.doLog(Level.DEBUG);
  }

  /**
   * Determine if entry/exit tracing is currently enabled for this <code>TraceElement</code>,
   * <p>
   * @return true if entry/exit tracing is enabled, else return false.
   */
  public final boolean isEntryEnabled()
  {
    return SibTraceBaseClass.isAllTracingEnabled() ||_logger.doLog(Level.ENTRY);
  }

  /**
   * Determine if event tracing is currently enabled for this <code>TraceElement</code>,
   * <p>
   * @return true if event tracing is enabled, else return false.
   */
  public final boolean isEventEnabled()
  {
    return SibTraceBaseClass.isAllTracingEnabled() || _logger.doLog(Level.EVENT);
  }

  /**
   * Determine if info logging is currently enabled for this <code>TraceElement</code>,
   * <p>
   * @return true if info logging is enabled, else return false.
   */
  public final boolean isInfoEnabled()
  {
    return SibTraceBaseClass.isAllTracingEnabled() || _logger.doLog(Level.INFORMATION);
  }

  /**
   * Determine if audit logging is currently enabled for this <code>TraceElement</code>,
   * <p>
   * @return true if audit logging is enabled, else return false.
   */
  public final boolean isAuditEnabled()
  {
    return SibTraceBaseClass.isAllTracingEnabled() || _logger.doLog(Level.AUDIT);
  }

  /**
   * Determine if warning logging is currently enabled for this <code>TraceElement</code>,
   * <p>
   * @return true if warning logging is enabled, else return false.
   */
  public final boolean isWarningEnabled()
  {
    return SibTraceBaseClass.isAllTracingEnabled() || _logger.doLog(Level.WARNING2);
  }

  /**
   * Determine if error logging is currently enabled for this <code>TraceElement</code>,
   * <p>
   * @return true if error logging is enabled, else return false.
   */
  public final boolean isErrorEnabled()
  {
    return SibTraceBaseClass.isAllTracingEnabled() || _logger.doLog(Level.ERROR);
  }

  /**
   * Determine if fatal logging is currently enabled for this <code>TraceElement</code>,
   * <p>
   * @return true if fatal logging is enabled, else return false.
   */
  public final boolean isFatalEnabled()
  {
    return SibTraceBaseClass.isAllTracingEnabled() || _logger.doLog(Level.FATAL);
  }

  /**
   * Determine if service logging is currently enabled for this <code>TraceElement</code>,
   * <p>
   * @return true if service logging is enabled, else return false.
   */
  public final boolean isServiceEnabled()
  {
    return SibTraceBaseClass.isAllTracingEnabled() || _logger.doLog(Level.SERVICE);
  }

  public final int getLevel()
  {
    return Level.ALL.intValue();
  }
  
  Logger getLogger()
  {
    return _logger;
  }
}