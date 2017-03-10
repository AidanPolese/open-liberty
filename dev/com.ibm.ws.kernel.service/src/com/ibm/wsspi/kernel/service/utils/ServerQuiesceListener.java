/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2014
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.wsspi.kernel.service.utils;

/**
 * A server quiesce listener. All {@code ServerQuiesceListener}s registered
 * in the service registry are called when the server has been stopped without
 * the {@code --force} option.
 */
public interface ServerQuiesceListener {

    /**
     * Called when the server is stopped without the {@code --force} option
     * to allow the registered service to perform pre-stop quiesce activities
     * to facilitate a clean server stop, like canceling pending scheduled executors,
     * or stopping inbound traffic to the server. This method should not be used to
     * register any new services, nor should it prematurely remove services that
     * other services depend on.
     * <p>
     * This method must complete and return to its caller in a timely manner and can be
     * called concurrently with other {@code ServerQuiesceListener}s in no specific
     * order.
     * </p><p>
     * Note that when this method is called, {@link FrameworkState#isStopping()} will
     * return true, and {@link FrameworkState#isValid()} will return false.
     * </p>
     */
    void serverStopping();
}
