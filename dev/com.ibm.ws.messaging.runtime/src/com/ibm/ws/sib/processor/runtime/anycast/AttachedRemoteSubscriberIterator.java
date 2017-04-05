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
package com.ibm.ws.sib.processor.runtime.anycast;

import java.util.Collection;
import java.util.Iterator;

import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.sib.processor.SIMPConstants;
import com.ibm.ws.sib.processor.impl.AnycastInputHandler;
import com.ibm.ws.sib.processor.runtime.SIMPIterator;
import com.ibm.ws.sib.utils.ras.SibTr;

/**
 * @author whitfiea
 *
 */
public class AttachedRemoteSubscriberIterator implements SIMPIterator
{
  private static TraceComponent tc =
    SibTr.register(
      AttachedRemoteSubscriberIterator.class,
      SIMPConstants.MP_TRACE_GROUP,
      SIMPConstants.RESOURCE_BUNDLE);

  //An iterator of AIHs
  private Iterator anycastIHIterator;
  
  public AttachedRemoteSubscriberIterator(Collection anycastInputHandlerList)
  {
    if (tc.isEntryEnabled()) 
      SibTr.entry(tc, "AttachedRemoteSubscriberIterator", new Object[]{anycastInputHandlerList});

    //we build a list of AIHs
    if (anycastInputHandlerList != null)
    {
      anycastIHIterator = anycastInputHandlerList.iterator();
    }
    
    if (tc.isEntryEnabled()) 
      SibTr.exit(tc, "AttachedRemoteSubscriberIterator", this);
  }
  
  /* (non-Javadoc)
   * @see com.ibm.ws.sib.processor.runtime.SIMPIterator#finished()
   */
  public void finished()
  {
    if (tc.isEntryEnabled()) 
      SibTr.entry(tc, "finished");
      
    anycastIHIterator = null;
      
    if (tc.isEntryEnabled()) 
      SibTr.exit(tc, "finished");
  }

  /* (non-Javadoc)
   * @see java.util.Iterator#hasNext()
   */
  public boolean hasNext()
  {
    if (tc.isEntryEnabled()) 
      SibTr.entry(tc, "hasNext");
    
    boolean hasNext = false;
    if (anycastIHIterator != null)
    {  
      hasNext = anycastIHIterator.hasNext();
    }
    
    if (tc.isEntryEnabled()) 
      SibTr.exit(tc, "hasNext", new Boolean(hasNext));
    return hasNext;
  }

  /**
   * This method returns the attached remote subscriber control or null if there isn't one.
   * 
   * @return SIMPAttachedRemoteSubscriberControl
   * 
   * @see java.util.Iterator#next()
   */
  public Object next()
  {
    if (tc.isEntryEnabled())
    {
      SibTr.entry(tc, "next");
    } 
    
    Object result;
    if (hasNext())
    {
      AnycastInputHandler aih = (AnycastInputHandler) anycastIHIterator.next();
      
      result = aih.getControlAdapter();
    }
    else
    {
      result = null;
    }
    
    if (tc.isEntryEnabled())
    {
      SibTr.exit(tc, "next", result);
    } 
    return result;
  }

  /* (non-Javadoc)
   * @see java.util.Iterator#remove()
   */
  public void remove()
  {
    if (tc.isEntryEnabled()) 
      SibTr.entry(tc, "remove");
    
    if (anycastIHIterator != null)
    {  
      anycastIHIterator.remove();
    }
    
    if (tc.isEntryEnabled()) 
      SibTr.exit(tc, "remove");
  }

}
