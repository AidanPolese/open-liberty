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
package com.ibm.ws.classloader.context;

import java.util.Map;

import com.ibm.wsspi.threadcontext.ThreadContext;

/**
 * Interface for creating thread context that can be captured and applied to other threads.
 */
public interface ClassLoaderThreadContextFactory {

    /**
     * Creates thread context for the given classloader.
     * 
     * The value returned must be a new instance if the thread context implementation stores any state information
     * (for example, previous thread context to restore after a contextual task ends).
     * 
     * @param execProps execution properties that provide information about the contextual task.
     * @param classloaderIdentifier identifies the classloader
     * 
     * @return context that can be applied to a thread.
     * 
     * @see javax.enterprise.concurrent.ManagedTask#getExecutionProperties()
     */
    ThreadContext createThreadContext(Map<String, String> execProps, String classloaderIdentifier);

}
