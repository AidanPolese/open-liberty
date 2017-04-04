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
 * Specifies the statistics provided by a JDBC connection.
 */
public interface JDBCConnectionStats extends Stats {

    /*
     * Returns the name of the managed object that identifies the JDBC data source
     * for this connection.
     */
    public String getJdbcDataSource();

    /*
     * Returns the time spent waiting for a connection to be available.
     */
    public TimeStatistic getWaitTime();

    /*
     * Returns the time spent using a connection.
     */
    public TimeStatistic getUseTime();

}
