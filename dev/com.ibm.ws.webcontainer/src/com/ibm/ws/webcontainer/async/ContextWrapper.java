/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2013
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.webcontainer.async;

/**
 * Added for PM90834.
 * Wrap a runnable in the context data from the ServiceWrapper.
 */
public class ContextWrapper implements Runnable {

    private ServiceWrapper serviceWrapper;
    private Runnable runnable;

    ContextWrapper(Runnable runnable, ServiceWrapper serviceWrapper) {
        this.serviceWrapper = serviceWrapper;
        this.runnable = runnable;
    }

    public void run() {
        serviceWrapper.wrapAndRun(this.runnable);
    }

}
