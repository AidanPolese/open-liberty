/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2011
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.wsspi.kernel.service.utils;

import org.osgi.framework.ServiceReference;

/**
 * A simple interface to associate a DS instance of a Service, with the ServiceReference that created it.
 * <br>
 * Handy for when you want to query properties on the ServiceReference for a given Service.
 * 
 * @param <T> The type of the Service
 */
public interface ServiceAndServiceReferencePair<T> {
    /**
     * Get the Service instance for this pair.
     * 
     * @return
     */
    public T getService();

    /**
     * Get the ServiceReference instance for this pair.
     * 
     * @return
     */
    public ServiceReference<T> getServiceReference();
}
