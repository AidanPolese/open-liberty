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
 * Represents &lt;connection-factory>.
 */
public interface ConnectionFactory extends JNDIEnvironmentRef, Describable {
    /**
     * Represents an unspecified value for {@link #getTransactionSupportValue}.
     */
    int TRANSACTION_SUPPORT_UNSPECIFIED = -1;

    /**
     * Represents "NoTransaction" for {@link #getTransactionSupportValue}.
     */
    int TRANSACTION_SUPPORT_NO_TRANSACTION = 0;

    /**
     * Represents "LocalTransaction" for {@link #getTransactionSupportValue}.
     */
    int TRANSACTION_SUPPORT_LOCAL_TRANSACTION = 1;

    /**
     * Represents "XATransaction" for {@link #getTransactionSupportValue}.
     */
    int TRANSACTION_SUPPORT_XA_TRANSACTION = 2;

    /**
     * @return &lt;interface-name>
     */
    String getInterfaceNameValue();

    /**
     * @return &lt;resource-adapter>
     */
    String getResourceAdapter();

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

    /**
     * @return &lt;transaction-support>
     *         <li>{@link #TRANSACTION_SUPPORT_UNSPECIFIED} if unspecified
     *         <li>{@link #TRANSACTION_SUPPORT_NO_TRANSACTION} - NoTransaction
     *         <li>{@link #TRANSACTION_SUPPORT_LOCAL_TRANSACTION} - LocalTransaction
     *         <li>{@link #TRANSACTION_SUPPORT_XA_TRANSACTION} - XATransaction
     */
    int getTransactionSupportValue();

    /**
     * @return &lt;property> as a read-only list
     */
    List<Property> getProperties();
}
