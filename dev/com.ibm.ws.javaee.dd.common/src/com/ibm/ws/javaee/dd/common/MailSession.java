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
 * Represents &lt;mail-session>.
 */
public interface MailSession extends JNDIEnvironmentRef, Describable {
    /**
     * @return &lt;store-protocol>, or null if unspecified
     */
    String getStoreProtocol();

    /**
     * @return &lt;store-protocol-class>, or null if unspecified
     */
    String getStoreProtocolClassName();

    /**
     * @return &lt;transport-protocol>, or null if unspecified
     */
    String getTransportProtocol();

    /**
     * @return &lt;transport-protocol-class>, or null if unspecified
     */
    String getTransportProtocolClassName();

    /**
     * @return &lt;host>, or null if unspecified
     */
    String getHost();

    /**
     * @return &lt;user>, or null if unspecified
     */
    String getUser();

    /**
     * @return &lt;password>, or null if unspecified
     */
    String getPassword();

    /**
     * @return &lt;from>, or null if unspecified
     */
    String getFrom();

    /**
     * @return &lt;property> as a read-only list
     */
    List<Property> getProperties();
}
