/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2011, 2014
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.javaee.dd.common;

import java.util.List;

/**
 * Represents &lt;persistence-context-ref>.
 */
public interface PersistenceContextRef
                extends PersistenceRef
{
    /**
     * Represents an unspecified value for {@link #getTypeValue}.
     */
    int TYPE_UNSPECIFIED = -1;

    /**
     * Represents "Transaction" for {@link #getTypeValue}.
     * 
     * @see org.eclipse.jst.j2ee.common.PersistenceContextType#TRANSACTION
     */
    int TYPE_TRANSACTION = 0;

    /**
     * Represents "Extended" for {@link #getTypeValue}.
     * 
     * @see org.eclipse.jst.j2ee.common.PersistenceContextType#EXTENDED
     */
    int TYPE_EXTENDED = 1;

    /**
     * Represents an unspecified value for {@link #getSynchronizationValue}.
     */
    int SYNCHRONIZATION_UNSPECIFIED = -1;

    /**
     * Represents "Synchronized" for {@link #getTypeValue}.
     */
    int SYNCHRONIZATION_SYNCHRONIZED = 0;

    /**
     * Represents "Unsynchronized" for {@link #getTypeValue}.
     */
    int SYNCHRONIZATION_UNSYNCHRONIZED = 1;

    /**
     * @return &lt;persistence-context-type>
     *         <ul>
     *         <li>{@link #TYPE_UNSPECIFIED} if unspecified
     *         <li>{@link #TYPE_TRANSACTION} - Transaction
     *         <li>{@link #TYPE_EXTENDED} - Extended
     *         </ul>
     */
    int getTypeValue();

    /**
     * @return &lt;persistence-context-synchronization>
     *         <ul>
     *         <li>{@link #SYNCHRONIZATION_UNSPECIFIED} if unspecified
     *         <li>{@link #SYNCHRONIZATION_SYNCHRONIZED} - Synchronized
     *         <li>{@link #SYNCHRONIZATION_UNSYNCHRONIZED} - Unsynchronized
     *         </ul>
     */
    int getSynchronizationValue();

    /**
     * @return &lt;persistence-property> as a read-only list
     */
    List<Property> getProperties();
}
