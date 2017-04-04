/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2015
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package javax.management.j2ee.statistics;

/**
 * The JCAConnectionPoolStats interface specifies the statistics provided by a JCA
 * connection pool.
 */
public interface JCAConnectionPoolStats extends JCAConnectionStats {

    /*
     * Returns the number of connections closed.
     */
    public CountStatistic getCloseCount();

    /*
     * Returns the number of connections created.
     */
    public CountStatistic getCreateCount();

    /*
     * Returns the number of free connections in the pool.
     */
    public BoundedRangeStatistic getFreePoolSize();

    /*
     * Returns the size of the connection pool.
     */
    public BoundedRangeStatistic getPoolSize();

    /*
     * Returns the number of threads waiting for a connection.
     */
    public RangeStatistic getWaitingThreadCount();
}
