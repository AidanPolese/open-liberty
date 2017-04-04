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
 * 186484.6         220404 tevans   Re-write of destination lookups to enable runtime admin
 * 186484.10        170504 tevans   MBean Registration
 * 186484.18        290604 tevans   Remote queue point control improvements
 * 317023           071105 tevans   Do not return nulls
 * 329818           071205 gatfora  Add missing FFDC statements
 * ===========================================================================
 */
package com.ibm.ws.sib.processor.runtime.impl;

import java.util.Iterator;
import java.util.NoSuchElementException;

import com.ibm.ejs.ras.TraceNLS;
import com.ibm.ws.sib.processor.SIMPConstants;
import com.ibm.ws.sib.processor.impl.exceptions.InvalidOperationException;
import com.ibm.ws.sib.processor.impl.interfaces.ControllableResource;
import com.ibm.ws.sib.processor.runtime.SIMPControllable;
import com.ibm.ws.sib.processor.runtime.SIMPIterator;

public class ControllableIterator implements SIMPIterator
{
  // NLS for component
  private static final TraceNLS nls =
    TraceNLS.getTraceNLS(SIMPConstants.RESOURCE_BUNDLE);

  private Iterator parent;
  SIMPControllable next = null;

  public ControllableIterator(Iterator parent)
  {
    this.parent = parent;
  }

  /* (non-Javadoc)
   * @see java.util.Iterator#hasNext()
   */
  public boolean hasNext()
  {
    while(next == null)
    {
      try
      {
        next = (SIMPControllable) next();
      }
      catch(NoSuchElementException e)
      {
        //No FFDC code needed
        break;
      }
    }
    boolean hasNext = (next != null);
    return hasNext;
  }

  /* (non-Javadoc)
   * @see java.util.Iterator#next()
   */
  public Object next()
  {
    SIMPControllable control = null;
    if(next == null)
    {
      ControllableResource controllable = (ControllableResource)parent.next();
      if(controllable != null)
      {
        control = controllable.getControlAdapter();
      }
      while(control == null)
      {
        controllable = (ControllableResource)parent.next();
        if(controllable != null)
        {
          control = controllable.getControlAdapter();
        }
      }
    }
    else
    {
      control = next;
      next = null;
    }
    return control;
  }

  /* (non-Javadoc)
   * @see java.util.Iterator#remove()
   */
  public void remove()
  {
    throw new InvalidOperationException(nls.getFormattedMessage(
      "INTERNAL_MESSAGING_ERROR_CWSIP0001",
      new Object[] {
        "com.ibm.ws.sib.processor.runtime.ControllableIterator",
        "1:110:1.12" },
      null)
      );
  }

  /* (non-Javadoc)
   * @see com.ibm.ws.sib.processor.runtime.SIMPIterator#finished()
   */
  public void finished()
  {
    if(parent instanceof SIMPIterator)
    {
      ((SIMPIterator)parent).finished();
    } 
  }

}
