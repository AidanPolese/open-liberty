package com.ibm.ws.sib.msgstore.list;
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
 * Reason           Date     Origin    Description
 * --------------- -------- --------  -----------------------------------------
 *                 11/11/05 schofiel  Original
 * 278082          20/12/05 schofiel  Rework link position in lists and cursor availability
 * 306998.20       09/01/06 gareth    Add new guard condition to trace statements
 * 673411          24/02/11 urwashi   Added new method next(int fromIndex)
 * ============================================================================
 */

import com.ibm.ws.sib.msgstore.AbstractItem;
import com.ibm.ws.sib.msgstore.Filter;
import com.ibm.ws.sib.msgstore.MessageStoreException;
import com.ibm.ws.sib.msgstore.NonLockingCursor;

/**
 * This class implements a nonlocking cursor on an unprioritized list. It simply delegates to
 * the subcursor that it contains. 
 */
public class UnprioritizedNonlockingCursor implements NonLockingCursor
{
  
    private boolean _allowUnavailable = false;
    private final Subcursor _cursor;

    public UnprioritizedNonlockingCursor(LinkedList parent, final Filter filter)
    {
        // No jumpback on this type of cursor
        _cursor = new Subcursor(parent, filter, false);
    }

    public void allowUnavailableItems()
    {
        _allowUnavailable = true;
    }

    public void finished()
    {
        _cursor.finished();
    }

    public Filter getFilter()
    {
        return _cursor.getFilter();
    }

    public AbstractItem next() throws MessageStoreException
    {
        return _cursor.next(_allowUnavailable);
    }
//673411-start
@Override 
   public AbstractItem next(int fromIndex)	throws MessageStoreException
   {
	return _cursor.next(_allowUnavailable);     
   }
// 673411-ends
}
