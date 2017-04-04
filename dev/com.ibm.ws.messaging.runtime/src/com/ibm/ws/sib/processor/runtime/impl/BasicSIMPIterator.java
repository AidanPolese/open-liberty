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
 * 186484.7         270404 tevans   More runtime control interfaces and implementation
 * 202387           100504 gatfora  getID should be getId.
 * ===========================================================================
 */
package com.ibm.ws.sib.processor.runtime.impl;

import java.util.Iterator;

import com.ibm.ws.sib.processor.runtime.SIMPIterator;

public class BasicSIMPIterator implements SIMPIterator
{
  private Iterator parent;

  public BasicSIMPIterator(Iterator parent)
  {
    this.parent = parent;
  }

  /* (non-Javadoc)
   * @see java.util.Iterator#hasNext()
   */
  public boolean hasNext()
  {
    return parent.hasNext();
  }

  /* (non-Javadoc)
   * @see java.util.Iterator#next()
   */
  public Object next()
  {
    return parent.next();
  }

  /* (non-Javadoc)
   * @see java.util.Iterator#remove()
   */
  public void remove()
  {
    parent.remove();
  }

  /* (non-Javadoc)
   * @see com.ibm.ws.sib.processor.runtime.SIMPIterator#finished()
   */
  public void finished()
  {
    //do nothing
  }
}
