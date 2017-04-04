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
 * A class to encapsulate an entry in the linked list
 * 
 * @author tevans
 */
public class SimpleLinkedListEntry extends Entry
{
  //The original object
  public Object data = null;
  
  private static TraceComponent tc =
    SibTr.register(
      SimpleLinkedListEntry.class,
      SIMPConstants.MP_TRACE_GROUP,
      SIMPConstants.RESOURCE_BUNDLE);

  
  public SimpleLinkedListEntry()
  {
    if (tc.isEntryEnabled())
      SibTr.entry(tc, "SimpleLinkedListEntry");

    if (tc.isEntryEnabled())
      SibTr.exit(tc, "SimpleLinkedListEntry", this);      
  }
  
  public SimpleLinkedListEntry(Object data)
  {
    if (tc.isEntryEnabled())
      SibTr.entry(tc, "SimpleLinkedListEntry", new Object[] { data });

    this.data = data;
    
    if (tc.isEntryEnabled())
      SibTr.exit(tc, "SimpleLinkedListEntry", this);      
  }
  
  public synchronized String toString(String indent)
  {    
    StringBuffer buffer = new StringBuffer();
    
    if(parentList == null)
    {
      buffer.append("SimpleLinkedListEntry not in list");
    }
    else
    {
      buffer.append(indent);
      buffer.append("SimpleLinkedListEntry("+data+")");    
      Cursor cursor = firstCursor;
      while(cursor != null)
      {        
        buffer.append("\n");
        buffer.append(indent);
        buffer.append("\\-->");
        buffer.append(cursor);
        cursor = (Cursor) cursor.next;
      }
    }      
      
    return buffer.toString();
  }
}
