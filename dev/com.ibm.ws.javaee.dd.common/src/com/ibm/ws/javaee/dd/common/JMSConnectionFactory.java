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
 * Represents &lt;jms-connection-factory>.
 */
public interface JMSConnectionFactory extends JNDIEnvironmentRef, Describable {
    /**
     * @return &lt;interface-name>, or null if unspecified
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
     * @return &lt;user>, or null if unspecified
     */
    String getUser();

    /**
     * @return &lt;password>, or null if unspecified
     */
    String getPassword();

    /**
     * @return &lt;client-id>, or null if unspecified
     */
    String getClientId();

    /**
     * @return &lt;property> as a read-only list
     */
    List<Property> getProperties();

    /**
     * @return true if &lt;transactional> is specified
     * @see #isTransactional
     */
    boolean isSetTransactional();

    /**
     * @return &lt;transactional> if specified
     * @see #isSetTransactional
     */
    boolean isTransactional();

    /**
     * @return true if &lt;max-pool-size> is specified
     * @see #getMaxPoolSize
     */
    boolean isSetMaxPoolSize();

    /**
     * @return &lt;max-pool-size> if specified
     * @see #isSetMaxPoolSize
     */
    int getMaxPoolSize();

    /**
     * @return true if &lt;min-pool-size> is specified
     * @see #getMinPoolSize
     */
    boolean isSetMinPoolSize();

    /**
     * @return &lt;min-pool-size> if specified
     * @see #isSetMinPoolSize
     */
    int getMinPoolSize();
}
