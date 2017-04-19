/*
 * COMPONENT_NAME: sib.unittest.ffdc
 *
 *  ORIGINS: 27
 *
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
 * 
 *
 * Change activity:
 *
 * Reason          Date        Origin   Description
 * --------------- ----------- -------- ----------------------------------------
 * d249011         03-Jun-2005 djvines Initial Test Drop.
 */
package com.ibm.ws.ffdc;

import com.ibm.ws.sib.ffdc.FFDCEngine;

/* ************************************************************************** */
/**
 * This is a version of the real FFDC code that allows a test case to examine the
 * FFDCs etc.
 *
 */
/* ************************************************************************** */
public class FFDCFilter
{
  /* -------------------------------------------------------------------------- */
  /* processException method
  /* -------------------------------------------------------------------------- */
  /**
   * @param th        The Throwable being passed to FFDC
   * @param sourceId  An identifier of the source generating the FFDC
   * @param probeId   An identifier within the source that locates the FFDC 
   */
  public static void processException(Throwable th, String sourceId, String probeId)
  {
    FFDCEngine.processException(th,sourceId,probeId,null,null);
  }
  
  /* -------------------------------------------------------------------------- */
  /* processException method
  /* -------------------------------------------------------------------------- */
  /**
   * @param th         The Throwable being passed to FFDC
   * @param sourceId   An identifier of the source generating the FFDC
   * @param probeId    An identifier within the source that locates the FFDC
   * @param callerThis The object generating the FFDC
   */
  public static void processException(Throwable th, String sourceId, String probeId, Object callerThis)
  {
    FFDCEngine.processException(th,sourceId,probeId,callerThis,null);
  }
  
  /* -------------------------------------------------------------------------- */
  /* processException method
  /* -------------------------------------------------------------------------- */
  /**
   * @param th          The Throwable being passed to FFDC
   * @param sourceId    An identifier of the source generating the FFDC
   * @param probeId     An identifier within the source that locates the FFDC
   * @param objectArray An array of objects to be passed to the FFDC Engine 
   */
  public static void processException(Throwable th, String sourceId, String probeId, Object [] objectArray)
  {
    FFDCEngine.processException(th,sourceId,probeId,null,objectArray);
  }
  
  /* -------------------------------------------------------------------------- */
  /* processException method
  /* -------------------------------------------------------------------------- */
  /**
   * @param th          The Throwable being passed to FFDC
   * @param sourceId    An identifier of the source generating the FFDC
   * @param probeId     An identifier within the source that locates the FFDC 
   * @param callerThis  The object generating the FFDC
   * @param objectArray An array of objects to be passed to the FFDC Engine 
   */
  public static void processException(Throwable th, String sourceId, String probeId, Object callerThis, Object [] objectArray)
  {
    FFDCEngine.processException(th,sourceId,probeId,callerThis,objectArray);
  }
}
