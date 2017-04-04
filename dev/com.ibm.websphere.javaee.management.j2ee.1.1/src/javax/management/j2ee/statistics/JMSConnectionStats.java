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
 * Specifies the statistics provided by a JMS connection.
 */
public interface JMSConnectionStats extends Stats {

    /*
     * Returns a list of JMSSessionStats that provide statistics about the sessions
     * associated with the referencing JMSConnectionStats.
     */
    public JMSSessionStats[] getSessions();

    /*
     * Returns the transactional state of this JMS connection. If true, indicates that
     * this JMS connection is transactional.
     */
    public boolean isTransactional();
}
