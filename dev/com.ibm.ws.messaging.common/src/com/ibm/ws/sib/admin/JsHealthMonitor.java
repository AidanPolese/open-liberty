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
 * @author wallisgd
 *
 * JsMainImpl and JsMessagingEngineImpl each implement this interface to allow
 * JsProcessComponents or JsEngineComponents to report error conditions that need
 * to be reported to HAManager when hamanager calls the isAlive method on the
 * JsMessagingEngineImpl's HAGroupCallback.
 * 
 */
public interface JsHealthMonitor {

  /**
   * Report a local error - one which will result in a restart/failover of the
   * Messaging Engine. 
   */
  public void reportLocalError();

  /**
   * Report a global error - one which will not result in a restart/failover of the
   * Messaging Engine because it represents a common mode fault that would affect 
   * other servers also. 
   */
  public void reportGlobalError();

}
