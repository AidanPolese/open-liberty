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
 * ---------------  ------ -------- -------------------------------------------------
 * 186484.10        170504 tevans   MBean Registration
 * ===========================================================================
 */
package com.ibm.ws.sib.processor.runtime.impl;

import com.ibm.ws.sib.admin.RuntimeEvent;
import com.ibm.ws.sib.processor.exceptions.SIMPControllableNotFoundException;
import com.ibm.ws.sib.processor.runtime.SIMPControllable;

public interface ControlAdapter extends SIMPControllable
{
  public void assertValidControllable() throws SIMPControllableNotFoundException;
  
  public void dereferenceControllable();

  /**
   * Register the control adapter as an MBean
   */
  public void registerControlAdapterAsMBean();

  /**
   * Deregister the control adapter
   */
  public void deregisterControlAdapterMBean();
  
  public void runtimeEventOccurred( RuntimeEvent event );
}
