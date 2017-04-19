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
package com.ibm.websphere.connectionpool.monitor;

/**
 * Management interface for the MBean "WebSphere:type=ConnectionPoolStats".
 * The Liberty profile makes this MBean available in its platform MBean server when the monitor-1.0 feature is
 * enabled to allow monitoring of the connection pool.
 * 
 * @ibm-api
 */
public interface ConnectionPoolStatsMXBean {

    /**
     * The total number of connections created
     */
    public long getCreateCount();

    /**
     * The total number of connections closed.
     */
    public long getDestroyCount();

    /**
     * The number of Connection objects in use for a particular connection pool.
     */
    public long getConnectionHandleCount();

    /**
     * The number of ManagedConnection objects in use for a particular connection pool.
     */
    public long getManagedConnectionCount();

    /**
     * The average waiting time in milliseconds until a connection is granted.
     */
    public double getWaitTime();

    /**
     * The number of free connections in the pool.
     */
    public long getFreeConnectionCount();
}
