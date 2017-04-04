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
 * 186484.5         210404 ajw      Further Continued controllable interfaces
 * 186484.12        090604 ajw      Finish off runtime controllable interfaces
 * ===========================================================================
 */
package com.ibm.ws.sib.processor.runtime;

import com.ibm.wsspi.sib.core.OrderingContext;
/**
 *
 */
public interface SIMPBrowserControllable extends SIMPControllable
{
  /**
   * Locates the Connection relating to the Browser. 
   *
   * @return SIMPConnectionControllable The connection object. 
   *
   */
  SIMPConnectionControllable getConnection();
  
  /**
   * Locates the administration destination that the browser is browsing.  
   *
   * @return Object  A Queue. 
   *
   */
  Object getDestinationObject();
  
  /**
   * Locates the ordering context for the browser.  
   *
   * @return OrderingContext  An OrderingContext or null if there is none. 
   */
  OrderingContext getOrderingContext();
  
  /**
   * Gets the remote messaging engine name that is browsing
   * 
   * @return String of the remote ME
   */
  String getRemoteMEName();
  
}
