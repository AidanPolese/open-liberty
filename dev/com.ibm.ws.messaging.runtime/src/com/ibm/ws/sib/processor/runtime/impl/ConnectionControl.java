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
 * 186484.4         050404 tevans   Continued controllable interfaces
 * 186484.5         210404 ajw      Further Continued controllable interfaces
 * 186484.7         270404 tevans   More runtime control interfaces and implementation
 * 186484.9         060504 tevans   Extended runtime control implementation
 * 202387           100504 gatfora  getID should be getId.
 * 186484.10        170504 tevans   MBean Registration
 * 196675.1.7.1     030604 tevans   MBean Registration enhancements
 * 201972.1.1       110804 gatfora  Exception text updates for Core SPI Exceptions
 * 452517           210807 cwilkin  Remove formatState
 * ===========================================================================
 */
package com.ibm.ws.sib.processor.runtime.impl;

import com.ibm.ejs.ras.TraceNLS;
import com.ibm.ws.sib.admin.RuntimeEvent;
import com.ibm.ws.sib.processor.SIMPConstants;
import com.ibm.ws.sib.processor.exceptions.SIMPControllableNotFoundException;
import com.ibm.ws.sib.processor.impl.ConnectionImpl;
import com.ibm.ws.sib.processor.runtime.SIMPConnectionControllable;
import com.ibm.ws.sib.processor.runtime.SIMPIterator;
import com.ibm.ws.sib.processor.runtime.SIMPMessageProcessorControllable;

/**
 * The adapter presented by a Connection to perform dynamic
 * control operations.
 */
public class ConnectionControl extends AbstractControlAdapter implements SIMPConnectionControllable
{
  private static final TraceNLS nls = TraceNLS.getTraceNLS(SIMPConstants.RESOURCE_BUNDLE);  

  // The connection adaptee
  private ConnectionImpl connection;

  public ConnectionControl(ConnectionImpl connection)
  {
    this.connection = connection;
  }

  /* (non-Javadoc)
   * @see com.ibm.ws.sib.processor.runtime.SIMPConnectionControllable#getMessageProcessor()
   */
  public SIMPMessageProcessorControllable getMessageProcessor()
  {
    return null;
  }

  /* (non-Javadoc)
   * @see com.ibm.ws.sib.processor.runtime.SIMPConnectionControllable#getConsumerIterator()
   */
  public SIMPIterator getConsumerIterator()
  {
    return null;
  }

  /* (non-Javadoc)
   * @see com.ibm.ws.sib.processor.runtime.SIMPConnectionControllable#getProducerIterator()
   */
  public SIMPIterator getProducerIterator()
  {
    return null;
  }

  /* (non-Javadoc)
   * @see com.ibm.ws.sib.processor.runtime.SIMPConnectionControllable#getBrowserIterator()
   */
  public SIMPIterator getBrowserIterator()
  {
    return null;
  }

  /* (non-Javadoc)
   * @see com.ibm.ws.sib.processor.runtime.AbstractControllable#checkValidControllable()
   */
  public void assertValidControllable() throws SIMPControllableNotFoundException
  {
    if(connection == null)
    {
      throw new SIMPControllableNotFoundException(
        nls.getFormattedMessage(
            "CONNECTION_CONTROLLABLE_ERROR_CWSIP0571",
              null,
              null));
    }
  }

  /* (non-Javadoc)
   * @see com.ibm.ws.sib.processor.runtime.AbstractControllable#dereferenceControllable()
   */
  public void dereferenceControllable()
  {
    connection = null;
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
