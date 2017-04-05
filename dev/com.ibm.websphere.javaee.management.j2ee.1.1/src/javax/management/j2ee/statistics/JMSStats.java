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
 * The JMSStats interface specifies the statistics provided by a JMS resource.
 */
public interface JMSStats extends Stats {

    /*
     * Returns a list of JMSConnectionStats that provide statistics about the
     * connections associated with the referencing JMS resource.
     */
    public JMSConnectionStats[] getConnections();

}
