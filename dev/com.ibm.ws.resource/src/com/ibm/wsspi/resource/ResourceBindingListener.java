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
package com.ibm.wsspi.resource;

/**
 * Callback notified when a binding is needed for a resource access.
 *
 * <p>Implementations should be registered in the OSGi service registry. If
 * multiple listeners are registered, they are called in
 * org.osgi.framework.Constants.SERVICE_RANKING order. The last call to {@link ResourceBinding#setBindingName} will be used.
 *
 * <p>If errors occur, the org.osgi.framework.Constants.SERVICE_DESCRIPTION
 * property should be used to identify this service.
 */
public interface ResourceBindingListener
{
    /**
     * Notification that a binding is being selected. The binding object is
     * valid for the duration of this method invocation only. This method might
     * be called concurrently from multiple threads.
     *
     * @param binding the binding
     */
    void binding(ResourceBinding binding);
}
