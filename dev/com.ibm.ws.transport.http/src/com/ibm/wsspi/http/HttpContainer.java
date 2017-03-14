/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2009
 *
 * The source code for this program is not published or other-
 * wise divested of its trade secrets, irrespective of what has
 * been deposited with the U.S. Copyright Office.
 */
package com.ibm.wsspi.http;

/**
 * Interface provided by containers that will handle requests from the HTTP
 * dispatcher.
 */
public interface HttpContainer {
    /**
     * Examine the inbound request to ensure the container is configured to
     * handle the incoming request and construct a runnable for request execution.
     * <p>
     * Request processing should not be performed during this method-- it should
     * wait for invocation of the runnable via the ExecutorService for proper
     * workload balancing/scheduling.
     * <p>
     * The container should not assume that the runnable will be invoked on the
     * same thread that called this method. Any state that should be saved
     * between the creation and invocation of the runnable should/can be stored in
     * the runnable instance itself.
     * 
     * @param inboundConnection
     *            {@link HttpInboundConnection} containing information inbound
     *            request: remote host/port, HttpRequest containing the URI and
     *            method.
     * 
     * @return A runnable for the dispatcher to use to queue the work to the
     *         container, or null if the container can not handle the inbound
     *         request (e.g. an unknown context root or URI).
     */
    Runnable createRunnableHandler(HttpInboundConnection inboundConnection);
}
