/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2016
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.http.dispatcher.classify;

import java.util.concurrent.Executor;

/**
 * Class supports providing a reference to the Classified Executor for the current piece of work.
 */
public class DecoratedExecutorThread {

    private static ThreadLocal<Executor> currentExecutor = new ThreadLocal<Executor>();

    /**
     * Set the Classified Executor on the thread.
     * 
     * @param ex Classified Executor
     */
    public static void setExecutor(Executor ex) {
        currentExecutor.set(ex);
    }

    /**
     * @return Value for Classified Executor
     */
    public static Executor getExecutor() {
        return currentExecutor.get();
    }

}
