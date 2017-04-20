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
 * This enum holds the annotation properties and xml element name pair as constants.
 */
public enum JMSDestinationProperties {

    NAME("name", "name"),
    INTERFACE_NAME("interface-name", "interfaceName"),
    CLASS_NAME("class-name", "className"),
    RESOURCE_ADAPTER("resource-adapter", "resourceAdapter"),
    DESTINATION_NAME("destination-name", "destinationName"),
    PROPERTIES("properties", "properties"),
    DESCRIPTION("description", "description");

    private final String xmlKey;
    private final String annotationKey;

    private JMSDestinationProperties(String xmlKey, String annotationKey) {
        this.xmlKey = xmlKey;
        this.annotationKey = annotationKey;
    }

    /**
     * @return the xmlElementName
     */
    public String getXmlKey() {
        return xmlKey;
    }

    /**
     * @return the key
     */
    public String getAnnotationKey() {
        return annotationKey;
    }
}