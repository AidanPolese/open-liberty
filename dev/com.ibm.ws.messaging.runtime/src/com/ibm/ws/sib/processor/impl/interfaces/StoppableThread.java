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
 * ---------------  ------ -------- -------------------------------------------
 * 251859           310105 gatfora  Allow threads to be stopped in ME failure case
 * ============================================================================
 */
package com.ibm.ws.sib.processor.impl.interfaces;

import com.ibm.ws.sib.processor.utils.StoppableThreadCache;

/**
 * @author gatfora
 * 
 * This interface is used for registering with MP any system threads that 
 * are started which may need stopping at Messaging Engine shutdown time.
 *
 */
public interface StoppableThread
{
  /**
   * The notification method to stop the system thread. 
   */
  public void stopThread(StoppableThreadCache cache);
}
