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
package com.ibm.ws.sib.processor.runtime.impl;

import java.util.Iterator;

import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.sib.processor.SIMPConstants;
import com.ibm.ws.sib.processor.runtime.SIMPTransmitMessageControllable;
import com.ibm.ws.sib.utils.ras.SibTr;


/**
 * Iterates along the messages on a source stream
 * @author tpm100
 */
public class TransmitMessageControllableIterator extends BasicSIMPIterator
{
  
  private static final TraceComponent tc =
    SibTr.register(
      TransmitMessageControllableIterator.class,
      SIMPConstants.MP_TRACE_GROUP,
      SIMPConstants.RESOURCE_BUNDLE);

  
  public TransmitMessageControllableIterator(Iterator parent)
  {
    super(parent);
  } 
  
  public Object next()
  {
    if (tc.isEntryEnabled())
      SibTr.entry(tc, "next");
    
    SIMPTransmitMessageControllable tran = 
       (SIMPTransmitMessageControllable)super.next();

    if (tc.isEntryEnabled())
      SibTr.exit(tc, "next", tran);      
    return tran;
  }
  

}
