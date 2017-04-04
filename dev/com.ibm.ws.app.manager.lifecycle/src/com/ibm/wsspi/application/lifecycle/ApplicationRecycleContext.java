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
package com.ibm.wsspi.application.lifecycle;

import java.util.concurrent.Future;

/**
 *
 */
public interface ApplicationRecycleContext {
    /**
     * The name of the application which owns this context
     */
    public String getAppName();

    /**
     * Request a Future that will be completed by the application manager after
     * all of the applications using the components of this context have stopped.
     */
    public Future<Boolean> getAppsStoppedFuture();
}
