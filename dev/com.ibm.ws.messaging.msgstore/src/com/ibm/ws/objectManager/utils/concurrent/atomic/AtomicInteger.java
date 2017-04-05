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
 * @see java.util.concurrent.atomic.AtomicInteger
 */
public interface AtomicInteger {

    /*
     * (non-Javadoc)
     * 
     * @see java.util.concurrent.atomic.AtomicInteger#addAndGet(int)
     */
    public abstract int addAndGet(int delta);

    /*
     * (non-Javadoc)
     * 
     * @see java.util.concurrent.atomic.AtomicInteger#decrementAndGet()
     */
    public abstract int decrementAndGet();

    /*
     * (non-Javadoc)
     * 
     * @see java.util.concurrent.atomic.AtomicInteger#get()
     */
    public abstract int get();

    /*
     * (non-Javadoc)
     * 
     * @see java.util.concurrent.atomic.AtomicInteger#incrementAndGet()
     */
    public abstract int incrementAndGet();

    /*
     * (non-Javadoc)
     * 
     * @see java.util.concurrent.atomic.AtomicInteger#set(int)
     */
    public abstract void set(int newIntegerValue);

    /*
     * (non-Javadoc)
     * 
     * @see java.util.concurrent.atomic.AtomicInteger#toString()
     */
    public abstract String toString();
} // interface AtomicInteger.