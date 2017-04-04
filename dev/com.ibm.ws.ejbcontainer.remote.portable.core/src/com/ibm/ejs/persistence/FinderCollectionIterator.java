/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 1998, 2002
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ejs.persistence;

import java.util.*;
import javax.ejb.EJBObject;

// FIX ME : Currently no difference between lazy and greedy modes on collections
// Lazy can be optimized by implementing a "smarter" iterator which will work
// off the lazy FinderEnumerator.

public class FinderCollectionIterator implements Iterator
{

    public FinderCollectionIterator(EJBObject[] elements)
    {
        this.elements = elements;
        this.index = 0;
    }

    public boolean hasNext()
    {
        if (elements != null)
            return (index < elements.length);
        else
            return false;
    }

    public Object next()
    {
        if ((elements != null) && (index < elements.length))
            return (elements[index++]);
        else
            throw new NoSuchElementException();
    }

    public void remove()
    {
        throw new UnsupportedOperationException();
    }

    private EJBObject[] elements;
    private int index = 0;

}
