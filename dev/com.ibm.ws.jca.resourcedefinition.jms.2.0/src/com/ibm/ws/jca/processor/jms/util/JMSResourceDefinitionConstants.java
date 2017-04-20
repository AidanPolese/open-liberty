/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2015
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
package com.ibm.ws.jca.processor.jms.util;

/**
 *
 */
public interface JMSResourceDefinitionConstants {
    public static final String JMS_CONNECTION_FACTORY_INTERFACE = "javax.jms.ConnectionFactory";
    public static final String RESOURCE_ADAPTER_WASJMS = "wasJms";
    public static final String RESOURCE_ADAPTER_WMQJMS = "wmqJms";

    public static final String DEFAULT_JMS_RESOURCE_ADAPTER = "wasJms";
    public static final boolean DEFAULT_TRANSACTIONAL_VALUE = true;

    public static final String DESTINATION_NAME = "destinationName";

    //wasJms resource adapter related properties
    public static final String JMS_QUEUE_INTERFACE = "javax.jms.Queue";
    public static final String JMS_TOPIC_INTERFACE = "javax.jms.Topic";
    public static final String JMS_TOPIC_NAME = "topicName";
    public static final String JMS_QUEUE_NAME = "queueName";

    //wmqJms resource adapter related properties
    public static final String WMQ_TOPIC_NAME = "baseTopicName";
    public static final String WMQ_QUEUE_NAME = "baseQueueName";

    public static final String PROPERTIES_REF_KEY = "properties.0.";
}
