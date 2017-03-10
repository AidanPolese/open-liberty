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
package com.ibm.ws.kernel.boot.jmx.service;

/**
 * Service for injection and removal of MBeanServerForwarder
 * filters into and from the platform MBeanServer.
 */
public interface MBeanServerPipeline {

    /**
     * Returns true if this MBeanServerPipeline contains the
     * given MBeanServerForwarderDelegate.
     */
    public boolean contains(MBeanServerForwarderDelegate filter);

    /**
     * Inserts an MBeanServerForwarderDelegate into the MBeanServerPipeline.
     * Returns true if successful; false otherwise.
     */
    public boolean insert(MBeanServerForwarderDelegate filter);

    /**
     * Removes an MBeanServerForwarderDelegate from the MBeanServerPipeline.
     * Returns true if successful; false otherwise.
     */
    public boolean remove(MBeanServerForwarderDelegate filter);

}
