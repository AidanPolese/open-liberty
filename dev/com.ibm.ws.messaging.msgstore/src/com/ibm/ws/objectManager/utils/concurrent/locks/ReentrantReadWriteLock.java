package com.ibm.ws.objectManager.utils.concurrent.locks;

/*
 * ============================================================================
 * IBM Confidential OCO Source Materials
 * 
 * 5724-H88, 5724-J08, 5724-I63, 5655-W65, 5724-H89, 5722-WE2   Copyright IBM Corp., 2013
 * 
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 * ============================================================================
 */

/**
 * @see java.util.concurrent.locks.ReentrantReadWriteLock
 */
public interface ReentrantReadWriteLock
                extends ReadWriteLock {

    /*
     * (non-Javadoc)
     * 
     * @see java.util.concurrent.locks.ReentrantReadWriteLock#readLock()
     */
    public abstract Lock readLock();

    /*
     * (non-Javadoc)
     * 
     * @see java.util.concurrent.locks.ReentrantReadWriteLock#writeLock()
     */
    public abstract Lock writeLock();

} // interface ReentrantReadWriteLock.