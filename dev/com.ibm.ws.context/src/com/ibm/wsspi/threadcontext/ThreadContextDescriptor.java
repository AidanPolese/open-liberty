/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2013, 2014
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.wsspi.threadcontext;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.RejectedExecutionException;

/**
 * Represents captured thread context.
 * Do not implement this interface. Instances should be obtained via WSContextService.captureThreadContext
 * or by cloning an existing instance.
 */
public interface ThreadContextDescriptor extends Cloneable {
    /**
     * Returns a copy of a thread context descriptor.
     * 
     * @return a copy of a thread context descriptor.
     */
    ThreadContextDescriptor clone();

    /**
     * Returns the execution properties.
     * Note that some properties might have been added internally by the context service implementation.
     * 
     * @return the execution properties.
     */
    Map<String, String> getExecutionProperties();

    /**
     * Serializes this thread context descriptor to bytes.
     * 
     * @return serialized bytes representing the thread context descriptor.
     * @throws IOException if a serialization error occurs.
     */
    byte[] serialize() throws IOException;

    /**
     * Sets thread context for an already added thread context provider, otherwise adds it to the end of the list.
     * 
     * @param providerName component name of the thread context provider.
     * @param context new thread context.
     */
    void set(String providerName, ThreadContext context);

    /**
     * Establish context on a thread before a contextual operation is started.
     * 
     * @return list of thread context matching the order in which context has been applied to the thread.
     * @throws IllegalStateException if the application component is not started or deployed.
     * @throws RejectedExecutionException if context cannot be established on the thread.
     */
    ArrayList<ThreadContext> taskStarting() throws RejectedExecutionException;

    /**
     * Remove context from the thread (in reverse of the order in which is was applied) after a contextual operation completes.
     * 
     * @param threadContext list of context previously applied to thread, ordered according to the order in which it was applied to the thread.
     */
    void taskStopping(ArrayList<ThreadContext> threadContext);
}
