/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2015
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.logging.internal.osgi;

/**
 * A generic recursion detection facility.
 * 
 * Each thread has its own counter.
 * 
 * @author dbourne
 * 
 * 
 */
public class RecursionCounter {

    private final ThreadLocal<Integer> stackDepth = new ThreadLocal<Integer>() {
        @Override
        protected Integer initialValue()
        {
            return Integer.valueOf(0);
        }
    };

    /**
     * Increments the recursion count for this thread.
     * 
     * @return new counter
     */
    public int incrementCount() {
        // get the logging recursion stack depth for the current thread
        int depth = stackDepth.get();
        depth = depth + 1;
        stackDepth.set(depth);
        return depth;
    }

    /**
     * Decrements the recursion count for this thread.
     */
    public void decrementCount() {
        int depth = stackDepth.get();
        depth = depth - 1;
        stackDepth.set(depth);
    }

}
