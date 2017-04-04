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

import java.util.Set;

/**
 *
 */
public interface ApplicationRecycleComponent {
    /**
     * Returns the context which this recycle component is a part of, or null if it is an independent component
     */
    public ApplicationRecycleContext getContext();

    /**
     * Stops the requested applications and restarts them when a corresponding startApplications()
     * call is invoked. The stop is done in coordination with configuration updates and other users
     * of the application recycler such that no applications are started until all users have driven
     * startApplications() and any in-flight configuration updates are complete, whichever comes
     * last.
     */
    public Set<String> getDependentApplications();
}
