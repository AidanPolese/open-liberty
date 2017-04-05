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
 * The JDBCStats type specifies the statistics provided by a JDBC resource.
 */
public interface JDBCStats extends Stats {

    /*
     * Returns a list of JDBCConnectionStats that provide statistics about the nonpooled
     * connections associated with the referencing JDBC resource statistics.
     */
    public JDBCConnectionStats[] getConnections();

    /*
     * Returns a list of JDBCConnectionPoolStats that provide statistics about the
     * connection pools associated with the referencing JDBC resource statistics.
     */
    public JDBCConnectionPoolStats[] getConnectionPools();

}
