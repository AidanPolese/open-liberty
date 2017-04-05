/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2012
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 *
 * Change activity:
 *
 * Reason           Date     Origin   Description
 * ---------------  -------- -------- ------------------------------------------
 *  82808           12/09/12 Kavitha  Version 1.2 from WASX.SIB copied
 *  85149           15/10/12 Kavitha  Except getTrace all other methods removed
 * 
 * ===========================================================================
 */

package com.ibm.ws.sib.matchspace.utils;


/**
 * @author Neil Young
 *
 * Make concrete instances of Trace.
 */
public class TraceFactory
{
  // The size of the ring buffer in bytes for ringBuffer tracing, 0 means no ring buffer.

  
  /**
   * Factory method to get the trace. 
   * @param Class the sourceClass
   * @return Trace implementatiion for the source class.
   */
  public static Trace getTrace(Class sourceClass, String traceGroup) 
  {
    return new TraceImpl(sourceClass, traceGroup);
  } // getTrace().
}
  