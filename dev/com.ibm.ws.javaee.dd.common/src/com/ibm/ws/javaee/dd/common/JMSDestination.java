/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2014
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
package com.ibm.ws.javaee.dd.common;

import java.util.List;

/**
 * Represents &lt;jms-destination>.
 */
public interface JMSDestination extends JNDIEnvironmentRef, Describable {
    /**
     * @return &lt;interface-name>
     */
    String getInterfaceNameValue();

    /**
     * @return &lt;class-name>, or null if unspecified
     */
    String getClassNameValue();

    /**
     * @return &lt;resource-adapter>, or null if unspecified
     */
    String getResourceAdapter();

    /**
     * @return &lt;destination-name>, or null if unspecified
     */
    String getDestinationName();

    /**
     * @return &lt;property> as a read-only list
     */
    List<Property> getProperties();
}
