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
 * Reason          Date   Origin   Description
 * --------------- ------ -------- --------------------------------------------
 * 181851.6        080104 wallisgd Created
 * 181851.11       200404 wallisgd Changed to JsHealthState getHealthState()
 * ============================================================================
 */

package com.ibm.ws.sib.admin;

import com.ibm.ws.sib.admin.JsHealthState;

/**
 * @author wallisgd
 *
 * Any JsProcessComponent or JsEngineComponent that additionally implements
 * JsMonitoredComponent will be polled when hamanager calls the isAlive 
 * method on the JsMessagingEngineImpl's HAGroupCallback.
 * 
 * If a component requires fault monitoring it should implement this interface.
 *   
 */
public interface JsMonitoredComponent {

  /**
   * Return an indication of whether the component is healthy or not.
   * "Healthy" in this context means that the component can continue to operate 
   * without requiring a restart of the Messaging Engine.
   * 
   * Within isAlive, the component can perform internal polling of the constituent
   * parts of its state - but it would be inadvisable to solicit input from large
   * numbers of objects, because of performance.
   *  
   * @return JsHealthState
   */
  public JsHealthState getHealthState();

}
