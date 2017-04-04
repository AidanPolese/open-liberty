/*
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
 * Change activity:
 *
 * Reason          Date   Origin   Description
 * --------------- ------ -------- --------------------------------------------
 *                                 Version X copied from CMVC
 * ============================================================================
 */
 
package com.ibm.ws.sib.admin;

/**
 * Those components that require to participate in the Event Management infrastructure
 * need to implement this interface rather than the JsEngineComponent interface.
 * 
 * Such components can support a RuntimeEventListener (set into them by Admin)
 * through which they can emit run-time events.
 */
public interface JsEngineComponentWithEventListener extends JsEngineComponent
{
  /**
   * Set the Engine Component's RuntimeEventListener.
   *  
   * @param listener A RuntimeEventListener
   */
  public void setRuntimeEventListener(RuntimeEventListener listener);
  /**
   * Get the Engine Component's RuntimeEventListener.
   */  
  public RuntimeEventListener getRuntimeEventListener();
}
