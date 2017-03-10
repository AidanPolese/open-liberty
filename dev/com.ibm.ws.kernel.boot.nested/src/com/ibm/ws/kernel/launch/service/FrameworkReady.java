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
package com.ibm.ws.kernel.launch.service;

public interface FrameworkReady {
    /**
     * Waits for a framework service to finish starting. After initial bundle
     * provisioning, the kernel will call this method for all registered
     * services before it considers the framework to be "ready".
     * 
     * @throws InterruptedException
     */
    void waitForFrameworkReady() throws InterruptedException;
}
