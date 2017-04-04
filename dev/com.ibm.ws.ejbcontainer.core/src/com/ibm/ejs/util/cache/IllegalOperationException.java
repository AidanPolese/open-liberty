/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 1999, 2005
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ejs.util.cache;

/**
 * <code>IllegalOperationException</code> is thrown by <code>Cache</code> when
 * an operation is invoked with a key for an object which is currenly not in
 * the correct state. Typically this occurs if the object is pinned when it
 * should not be, or vice-versa.
 * <p>
 * 
 * @see Cache
 * @see Cache#remove
 * @see Cache#unpin
 * 
 */

public class IllegalOperationException
                extends com.ibm.websphere.csi.IllegalOperationException
{
    private static final long serialVersionUID = 838891005517005185L;

    /**
     * Constructs an <code>IllegalOperationException</code> identifying
     * the specified key as the target object in an operation which cannot
     * be performed on that object while it is in its current state.
     * <p>
     * 
     * @param key The key of the target object
     * 
     */

    IllegalOperationException(Object key, int count)
    {
        super(key.toString() + " is currently pinned: " + count);
    }
}
