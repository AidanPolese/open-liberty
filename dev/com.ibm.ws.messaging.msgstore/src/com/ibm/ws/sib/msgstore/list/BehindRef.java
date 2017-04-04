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
 *  Reason         Date     Origin   Description
 * --------------- -------- -------- ------------------------------------------
 *                 11/11/05 schofiel Original
 * 278082          20/12/05 schofiel Rework link position in lists and cursor availability
 * 306998.20       09/01/06 gareth   Add new guard condition to trace statements
 * 426133          26/03/07 gareth   Replace WeakReference with SoftReference
 * ============================================================================
 */

import java.lang.ref.SoftReference;

import com.ibm.ws.sib.msgstore.cache.links.AbstractItemLink;

/**
 * An entry in the list of AILs behind the current position of a cursor.
 */
public class BehindRef extends SoftReference
{
   

    public BehindRef _next;
    public BehindRef _prev;

    public BehindRef(AbstractItemLink ail)
    {
        super(ail);
    }

    public AbstractItemLink getAIL()
    {
        Object ref = get();
        if (ref == null)
            return null;
        else
            return(AbstractItemLink)ref;
    }

    public String toString()
    {
        Object ref = get();
        if (ref == null)
            return "[#]";
        else
        {
            StringBuffer sb = new StringBuffer();
            sb.append('[');
            sb.append(((AbstractItemLink)ref).getPosition());
            sb.append(']');
            return sb.toString();
        }
    }
}
