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
 *  82808           12/09/12 Kavitha  Version 1.1 from WASX.SIB copied 
 *  85149           15/09/12 Kavitha  TraceComponent from com.ibm.websphere.ras package used
 * ===========================================================================
 */
package com.ibm.ws.sib.matchspace.utils;

public class TraceComponent
{
  public static boolean isAnyTracingEnabled()
  {
    //Kavitha Liberty change
    return com.ibm.websphere.ras.TraceComponent.isAnyTracingEnabled();
  }
}
