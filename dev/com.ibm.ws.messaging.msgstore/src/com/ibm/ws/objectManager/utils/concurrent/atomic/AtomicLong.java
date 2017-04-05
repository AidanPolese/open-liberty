package com.ibm.ws.objectManager.utils.concurrent.atomic;

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
 * @see java.util.concurrent.atomic.AtomicLong
 */
public interface AtomicLong {

    /*
     * (non-Javadoc)
     * 
     * @see java.util.concurrent.atomic.AtomicLong#addAndGet(long)
     */
    public abstract long addAndGet(long delta);

    /*
     * (non-Javadoc)
     * 
     * @see java.util.concurrent.atomic.AtomicLong#decrementAndGet()
     */
    public abstract long decrementAndGet();

    /*
     * (non-Javadoc)
     * 
     * @see java.util.concurrent.atomic.AtomicLong#get()
     */
    public abstract long get();

    /*
     * (non-Javadoc)
     * 
     * @see java.util.concurrent.atomic.AtomicLong#incrementAndGet()
     */
    public abstract long incrementAndGet();

    /*
     * (non-Javadoc)
     * 
     * @see java.util.concurrent.atomic.AtomicLong#set(long)
     */
    public abstract void set(long newLongValue);

    /*
     * (non-Javadoc)
     * 
     * @see java.util.concurrent.atomic.AtomicLong#toString()
     */
    public abstract String toString();

} // interface AtomicLong.