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
 * @see java.util.concurrent.atomic.AtomicIntegerArray
 */
public interface AtomicIntegerArray {

    /*
     * (non-Javadoc)
     * 
     * @see java.util.concurrent.atomic.AtomicIntegerArray#addAndGet(int,int)
     */
    public abstract int addAndGet(int i, int delta);

    /*
     * (non-Javadoc)
     * 
     * @see java.util.concurrent.atomic.AtomicIntegerArray#decrementAndGet(int)
     */
    public abstract int decrementAndGet(int i);

    /*
     * (non-Javadoc)
     * 
     * @see java.util.concurrent.atomic.AtomicIntegerArray#get(int)
     */
    public abstract int get(int i);

    /*
     * (non-Javadoc)
     * 
     * @see java.util.concurrent.atomic.AtomicIntegerArray#incrementAndGet(int)
     */
    public abstract int incrementAndGet(int i);

    /*
     * (non-Javadoc)
     * 
     * @see java.util.concurrent.atomic.AtomicIntegerArray#set(int,int)
     */
    public abstract void set(int i, int newIntegerValue);

    /*
     * (non-Javadoc)
     * 
     * @see java.util.concurrent.atomic.AtomicIntegerArray#toString()
     */
    public abstract String toString();
} // interface AtomicIntegerArray.