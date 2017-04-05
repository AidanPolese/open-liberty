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
 * TBD              260303 astley   First version
 * 166318.3         090603 nyoung   Replace Gryphon utilities with JDK Equivalents 
 * 166318.4         160603 nyoung   Move to com.ibm.ws.sib.processor.matchspace.impl
 * 166318.9         160903 nyoung   Restructure mspace interfaces
 * 166318.10        230903 nyoung   Move to matchspace.impl component
 * LIDB3706-5.212   220205 gatfora  Add missing Serial UID's
 * ===========================================================================
 */
package com.ibm.ws.sib.matchspace.impl;

import com.ibm.ws.sib.matchspace.Selector;

import java.util.HashMap;
import java.util.Iterator;

/** This class serves as the "intern table" for all Selector subexpressions within
 * MatchSpace.  It implements the Selector.InternTable interface required by the
 * Selector.intern and unintern methods.
 **/

public final class InternTable extends HashMap implements Selector.InternTable
{

  // Constant governing how high the unique id counter is allowed to grow before
  // attempting compression of unique ids.

  private static final int COUNTER_LIMIT = 10000;

  // Constant governing how much the unique id counter must be reduced by in order to make
  // compression of unique ids worthwhile

  private static final int MIN_REDUCE = 2000;

  // Counter holding the next uniqueId to be assigned

  private int counter = 1;

  private static final long serialVersionUID = 5260158026529697853L;
  /** Implement the getNextUniqueId function */

  public int getNextUniqueId()
  {
    if (counter > COUNTER_LIMIT && counter - size() >= MIN_REDUCE)
      compress();
    return counter++;
  }

  /** Return the required evaluation cache size for an indexed evaluation cache (as
   * implemented by the EvalCache abstract class).
   **/

  public int evalCacheSize()
  {
    return counter;
  }

  // Compress the uniqueId assignments for Selectors currently in the intern table

  private void compress()
  {
    counter = 1;
    for (Iterator e = values().iterator(); e.hasNext(); ) 
    {
      Selector s = (Selector) e.next();
      s.setUniqueId(counter++);
    }
  }

  // Specialize clear() method to reset counter.
  public void clear()
  {
    super.clear();
    counter = 1;
  }

}
