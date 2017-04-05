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
package com.ibm.websphere.monitor.jmx;

/**
 * Management interface for the MBean "WebSphere:type=ThreadPoolStats,name=Default Executor".
 * The Liberty profile makes this MBean available in its platform MBean server when the monitor-1.0 feature is
 * enabled to allow monitoring of the thread pool. This interface can be used to request a proxy object via the {@link javax.management.JMX#newMXBeanProxy} method.
 * 
 * @ibm-api
 */
public interface ThreadPoolMXBean {

    /**
     * Retrieves the value of the read-only attribute PoolName; all web requests execute in a thread pool called "Default Executor"
     * 
     * @return thread pool name
     */
    public String getPoolName();

    /**
     * Retrieves the value of the read-only attribute ActiveThreads, which is the number of active threads in the pool
     * 
     * @return active thread count
     */
    public int getActiveThreads();

    /**
     * Retrieves the value of the read-only attribute PoolSize, which is the total number of threads in the pool, including both active and inactive threads.
     * 
     * @return thread pool size
     */
    public int getPoolSize();
}
