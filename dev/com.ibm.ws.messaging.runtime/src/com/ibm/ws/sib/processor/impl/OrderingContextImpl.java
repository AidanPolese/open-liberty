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
package com.ibm.ws.sib.processor.impl;

import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.sib.processor.SIMPConstants;

import com.ibm.ws.sib.utils.ras.SibTr;
import com.ibm.wsspi.sib.core.OrderingContext;

/**
 * This class conatains the implementation of the OrderingContext from the CoreSPI.
 */
public class OrderingContextImpl implements OrderingContext
{
  //trace
  private static final TraceComponent tc =
    SibTr.register(
      OrderingContextImpl.class,
      SIMPConstants.MP_TRACE_GROUP,
      SIMPConstants.RESOURCE_BUNDLE);
    
    
  /**
   * Create a new ordering context group object
   */
  public OrderingContextImpl()
  {
  }
}
