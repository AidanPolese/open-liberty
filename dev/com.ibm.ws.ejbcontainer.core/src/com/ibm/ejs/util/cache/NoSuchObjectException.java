/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 1998, 2005
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ejs.util.cache;

/**
 * <code>Cache</code> throws a <code>NoSuchObjectException</code> when an
 * operation which requires that the target object exist in the cache is
 * attempted but the target cannot be found in the cache.
 * <p>
 * 
 * @see Cache
 * @see Cache#pin
 * @see Cache#unpin
 * 
 */

public class NoSuchObjectException extends RuntimeException
{
    private static final long serialVersionUID = 8692192410150034609L;

    /**
     * Constructs a <code>NoSuchObjectException</code> object,
     * identifying the object which is not in the cache
     * <p>
     * 
     * @param key The key of the object
     * 
     */

    NoSuchObjectException(Object key)
    {
        super(key.toString() + " could not be found in the cache");
    }
}
