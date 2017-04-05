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
 * --------------- -------- -------- ------------------------------------------
 * SIB0155.msp.4    151106 nyoung   Enable OSGImin support.
 * 422894           280207 nyoung   FFDC: Uninstrumented catch block.  
 * ============================================================================
 */

package com.ibm.ws.sib.matchspace.utils;

import java.lang.reflect.Method;

public class TraceUtils 
{
  /**
   * Wrapper method to get the trace via the Factory. 
   * @param Class the sourceClass
   * @return Trace implementatiion for the source class.
   */
  public static Trace getTrace(Class sourceClass, String traceGroup) 
  {
    Class traceFactoryClass;
    Object ret = null;
    try 
    {
      traceFactoryClass = Class.forName("com.ibm.ws.sib.matchspace.utils.TraceFactory");

      Class[] params = new Class[]{Class.class, String.class};
      Method getTraceMethod = traceFactoryClass.getMethod("getTrace", params);
      Object[] objParams = new Object[]{sourceClass, traceGroup};
      ret = getTraceMethod.invoke(null, objParams);
    } 
    catch (Exception e) 
    {
      // No FFDC Code Needed.
      // Trace and FFDC not available so print to stdout.      
      e.printStackTrace();
    }    
       
    return (Trace)ret;
  } // getTrace().

}
