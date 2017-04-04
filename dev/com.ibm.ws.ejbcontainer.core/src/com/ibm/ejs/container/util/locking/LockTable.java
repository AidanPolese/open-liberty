/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2001, 2012
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ejs.container.util.locking;

/**
 * <code>LockTable</code> implements a hash-based collection of locks.
 * Locks are indexed by a key, which may be any <code>Object</code> which
 * implements <code>hashCode()</code>. Two keys which have the same hash
 * value will access the same lock in the table.
 */
public final class LockTable
{
    /**
     * Array of lock objects
     */
    private final Object[] locks;

    /**
     * Create a new instance of <code>LockTable</code>, with the specified
     * number of locks.
     * <p>
     * 
     * @param size The number of locks desired in the table
     */
    public LockTable(int size)
    {
        locks = new Object[size];
    }

    /**
     * Return the lock which is associated with the specified key. Uses
     * <code>hashCode()</code> to index into the table of locks. Keys
     * which hash identically will share a single lock.
     * <p>
     * 
     * @param key The key for which the lock is desired
     */
    public Object getLock(Object key)
    {
        int index = (key.hashCode() & 0x7FFFFFFF) % locks.length;

        // Double-checked locking.  Safe since Object has no state.
        Object lock = locks[index];
        if (lock == null)
        {
            synchronized (this)
            {
                lock = locks[index];
                if (lock == null)
                {
                    lock = new Object();
                    locks[index] = lock;
                }
            }
        }

        return lock;
    }
}
