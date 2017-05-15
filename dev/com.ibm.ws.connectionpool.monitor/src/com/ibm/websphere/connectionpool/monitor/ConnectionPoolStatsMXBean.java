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
     * The total number of managed connections created since pool creation.
     */
    public long getCreateCount();

    /**
     * The total number of managed connections destroyed since pool creation.
     */
    public long getDestroyCount();

    /**
     * The number of connections that are in use, including multiple connections shared from a single managed connection.
     */
    public long getConnectionHandleCount();

    /**
     * The total number of managed connections in the free, shared, and unshared pools.
     */
    public long getManagedConnectionCount();

    /**
     * The average waiting time in milliseconds until a connection is granted if a connection is not currently available.
     */
    public double getWaitTime();

    /**
     * The number of managed connections in the free pool.
     */
    public long getFreeConnectionCount();

    /**
     * The average time in milliseconds a connection is in use.
     */
    public double getInUseTime();
}
