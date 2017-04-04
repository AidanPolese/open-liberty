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
 * The JCAStats interface specifies the statistics provided by a JCA resource.
 */
public interface JCAStats extends Stats {

    /*
     * Returns a list of JCAConnectionStats that provide statistics about the nonpooled
     * connections associated with the referencing JCA resource statistics.
     */
    public JCAConnectionStats[] getConnections();

    /*
     * Returns a a list of JCAConnectionPoolStats that provide statistics about the
     * connection pools associated with the referencing JCA resource statistics.
     */
    public JCAConnectionPoolStats[] getConnectionPools();

}
