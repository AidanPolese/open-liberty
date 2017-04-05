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
 * Specifies the statistics provided by a JMS session.
 */
public interface JMSSessionStats extends Stats {

    /*
     * Returns a list of JMSProducerStats that provide statistics about the message
     * producers associated with the referencing JMS session statistics.
     */
    public JMSProducerStats[] getProducers();

    /*
     * Returns a list of JMSConsumerStats that provide statistics about the message
     * consumers associated with the referencing JMS session statistics.
     */
    public JMSConsumerStats[] getConsumers();

    /*
     * Returns the number of messages exchanged.
     */
    public CountStatistic getMessageCount();

    /*
     * Returns the number of pending messages.
     */
    public CountStatistic getPendingMessageCount();

    /*
     * Returns the number of expired messages.
     */
    public CountStatistic getExpiredMessageCount();

    /*
     * Returns the number of expired messages.
     */
    public TimeStatistic getMessageWaitTime();

    /*
     * Returns the number of durable subscriptions.
     */
    public CountStatistic getDurableSubscriptionCount();

}
