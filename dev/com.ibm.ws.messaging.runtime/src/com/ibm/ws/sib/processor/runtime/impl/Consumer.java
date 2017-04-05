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
 * 186484.7         270404 tevans   More runtime control interfaces and implementation
 * 186484.9         060504 tevans   Extended runtime control implementation
 * 202387           100504 gatfora  getID should be getId.
 * 186484.10        170504 tevans   MBean Registration
 * 196675.1.7.1     030604 tevans   MBean Registration enhancements
 * 250746           190105 gatfora  Remove unthrown exception declarations
 * 452517           210807 cwilkin  Remove formatState
 * ===========================================================================
 */
package com.ibm.ws.sib.processor.runtime.impl;

import com.ibm.ws.sib.admin.RuntimeEvent;
import com.ibm.ws.sib.processor.runtime.SIMPConnectionControllable;
import com.ibm.ws.sib.processor.runtime.SIMPConsumerControllable;
import com.ibm.wsspi.sib.core.OrderingContext;

/**
 * 
 */
public class Consumer extends AbstractControlAdapter implements SIMPConsumerControllable
{

  /* (non-Javadoc)
   * @see com.ibm.ws.sib.processor.runtime.SIMPConsumerControllable#getConnection()
   */
  public SIMPConnectionControllable getConnection()
  {
    return null;
  }

  /* (non-Javadoc)
   * @see com.ibm.ws.sib.processor.runtime.SIMPConsumerControllable#getDestinationObject()
   */
  public Object getDestinationObject()
  {
    return null;
  }

  /* (non-Javadoc)
   * @see com.ibm.ws.sib.processor.runtime.SIMPConsumerControllable#getOrderingContext()
   */
  public OrderingContext getOrderingContext()
  {
    return null;
  }

  /* (non-Javadoc)
   * @see com.ibm.ws.sib.processor.runtime.AbstractControllable#checkValidControllable()
   */
  public void assertValidControllable()
  {
  }

  /* (non-Javadoc)
   * @see com.ibm.ws.sib.processor.runtime.AbstractControllable#dereferenceControllable()
   */
  public void dereferenceControllable()
  {
  }

  /* (non-Javadoc)
   * @see com.ibm.ws.sib.processor.runtime.ControlAdapter#registerControlAdapterAsMBean()
   */
  public void registerControlAdapterAsMBean()
  {
  }

  /* (non-Javadoc)
   * @see com.ibm.ws.sib.processor.runtime.ControlAdapter#deregisterControlAdapterMBean()
   */
  public void deregisterControlAdapterMBean()
  {
  }

  /* (non-Javadoc)
   * @see com.ibm.ws.sib.processor.runtime.ControlAdapter#runtimeEventOccurred(com.ibm.ws.sib.admin.RuntimeEvent)
   */
  public void runtimeEventOccurred(RuntimeEvent event)
  {
  }

  /* (non-Javadoc)
   * @see com.ibm.ws.sib.admin.Controllable#getName()
   */
  public String getName()
  {
    return null;
  }

  /* (non-Javadoc)
   * @see com.ibm.ws.sib.admin.Controllable#getId()
   */
  public String getId()
  {
    return null;
  }
}
