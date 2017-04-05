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

package com.ibm.ws.sib.processor.utils.linkedlist;

import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.sib.processor.SIMPConstants;
import com.ibm.ws.sib.utils.ras.SibTr;

/**
 * A very simple linked list. There is no locking and little function but
 * it does exactly what we need
 * 
 * @author dware
 */
public class SimpleLinkedList
{
  //The first entry in the list
  protected SimpleEntry first = null;
  //the last entry in the list
  protected SimpleEntry last = null;
  
  private static TraceComponent tc =
    SibTr.register(
      SimpleLinkedList.class,
      SIMPConstants.MP_TRACE_GROUP,
      SIMPConstants.RESOURCE_BUNDLE);


  /**
   * Create a new LinkedList
   */
  public SimpleLinkedList()
  {
  }

  /**
   * Add an entry to the list
   * @param simpleEntry
   */
  public void put(SimpleEntry simpleEntry)
  {
    if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
      SibTr.entry(this, tc, "put", simpleEntry);
    
    simpleEntry.previous = last;
    simpleEntry.list = this;
    
    if(last != null)
      last.next = simpleEntry;
    else
      first = simpleEntry;
      
    last = simpleEntry;

    if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
      SibTr.exit(this, tc, "put", printList());
  }
  
  /**
   * Return the first entry in the list (may be null)
   * @return
   */
  public SimpleEntry getFirst()
  {
    if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
      SibTr.entry(this, tc, "getFirst");

    if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
      SibTr.exit(this, tc, "getFirst", new Object[] {first, printList()});
    
    return first;          
  }
  
  // DO NOT TRACE
  // Return the first and last entries in the list 
  protected String printList()
  {
    String output = "[";
    
    SimpleEntry pointer = first;
    int counter = 0;
    while((pointer != null) && (counter < 3))
    {
      output += "@"+Integer.toHexString(pointer.hashCode());
      pointer = pointer.next;
      if(pointer != null)
        output +=  ", ";
      counter++;
    }
    if(pointer != null)
    {
      output += "..., @"+Integer.toHexString(last.hashCode()) + "]";
    }
    else
      output += "]";
    
    return output;
  }
}
