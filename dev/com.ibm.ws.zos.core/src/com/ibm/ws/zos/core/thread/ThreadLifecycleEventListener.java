/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2012
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.zos.core.thread;

/**
 * Interface definition to encapsulate JVMTI thread callback functions.
 */
public interface ThreadLifecycleEventListener {

    /**
     * Notification that the current thread is about to enter its run method.
     */
    public void threadStarted();

    /**
     * Notification that the current thread has returned from its run method.
     */
    public void threadTerminating();

}
