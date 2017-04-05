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
 * 227424           290804 sbhola   started
 * ===========================================================================
 */
package com.ibm.ws.sib.processor.impl.interfaces;

import com.ibm.wsspi.sib.core.SelectionCriteria;
import com.ibm.websphere.sib.exception.SIException;

/**
 * RemoteDispatchableKey should be implemented by any class wishing to register
 * with a RemoteConsumerDispatcher for dispatching
 */
public interface RemoteDispatchableKey extends DispatchableKey {
  
  public SelectionCriteria[] getSelectionCriteria();
  
  public void notifyException(SIException e);

  public boolean hasNonSpecificConsumers();
}
