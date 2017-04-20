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
package com.ibm.websphere.session.monitor;

/**
 * Management interface for the MBean "WebSphere:type=SessionStats".
 * The Liberty profile makes this MBean available in its platform MBean server when the monitor-1.0 feature is
 * enabled to allow monitoring of the Session.
 * 
 * @ibm-api
 * 
 */
public interface SessionStatsMXBean {

    /**
     * The number of concurrently active sessions.
     * A session is active if the WebSphere Application Server is currently processing a request that uses that session.
     */
    public long getActiveCount();

    /**
     * The number of local sessions that are currently cached in memory
     * from the time at which this metric is enabled
     */
    public long getLiveCount();

    /**
     * The number of sessions that were created
     */
    public long getCreateCount();

    /**
     * The number of sessions that were invalidated caused by timeout
     */
    public long getInvalidatedCountbyTimeout();

    /**
     * The number of sessions that were invalidated
     */
    public long getInvalidatedCount();
}
