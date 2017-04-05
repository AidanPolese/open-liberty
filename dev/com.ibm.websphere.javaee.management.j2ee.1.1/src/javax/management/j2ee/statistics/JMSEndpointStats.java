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
 * Specifies the base interface for the statistics provided by a JMS message producer or
 * a JMS message consumer.
 */
public interface JMSEndpointStats extends Stats {

    /*
     * Returns the number of messages sent or received.
     */
    public CountStatistic getMessageCount();

    /*
     * Returns the number of pending messages.
     */
    public CountStatistic getPendingMessageCount();

    /*
     * Returns the number of messages that expired before delivery.
     */
    public CountStatistic getExpiredMessageCount();

    /*
     * Returns the time spent by a message before being delivered.
     */
    public TimeStatistic getMessageWaitTime();

}
