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
package com.ibm.ws.sib.processor.impl.interfaces;

import com.ibm.ws.sib.processor.runtime.impl.ControlAdapter;

public interface ControllableResource
{
  /**
   * Get the control adapter
   * @return a control adapter
   */
  public ControlAdapter getControlAdapter();

  /**
   * Create the control adapter for this message item stream
   */
  public void createControlAdapter();
  
  public void dereferenceControlAdapter();

  /**
   * Register the control adapter as an MBean
   */
  public void registerControlAdapterAsMBean();

  /**
   * Deregister the control adapter
   */
  public void deregisterControlAdapterMBean();
}
