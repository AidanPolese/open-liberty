/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2010
 *
 * The source code for this program is not published or other-
 * wise divested of its trade secrets, irrespective of what has
 * been deposited with the U.S. Copyright Office.
 */

package com.ibm.websphere.ras;

/**
 * Simple change listener interface that is driven as {@link TraceComponent} instances are registered and updated.
 * Register through the {@link TrConfigurator#addTraceComponentListener(TraceComponentChangeListener)} method
 */
public interface TraceComponentChangeListener {

    /**
     * Callback indicating the specified trace component was registered.
     * 
     * @param tc
     *            the {@link TraceComponent} that was registered
     */
    public void traceComponentRegistered(TraceComponent tc);

    /**
     * Callback indicating the specified trace component was updated.
     * 
     * @param tc
     *            the {@link TraceComponent} that was updated
     */
    public void traceComponentUpdated(TraceComponent tc);
}
