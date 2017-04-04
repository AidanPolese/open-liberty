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
 * The JCAConnectionStats interface specifies the statistics provided by a JCA
 * connection.
 */
public interface JCAConnectionStats extends Stats {

    /*
     * Returns the JCAConnectionFactory string of the managed object
     * that identifies the connector’s connection factory for this connection.
     */
    public String getConnectionFactory();

    /*
     * Returns the JCAManagedConnectionFactory string of the
     * managed object that identifies the connector’s managed connection factory for
     * this connection.
     */
    public String getManagedConnectionFactory();

    /*
     * Returns time spent waiting for a connection to be available.
     * 
     * public TimeStatistic getWaitTime();
     * 
     * /*
     * Returns the time spent using a connection.
     */
    public TimeStatistic getUseTime();

    /*
     * Returns the time spent waiting for a connection to be available.
     */
    public TimeStatistic getWaitTime();

}
