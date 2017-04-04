/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2012
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.javaee.dd.ejb;

/**
 * Represents the group of elements common to bean types that support
 * declarative transactions.
 */
public interface TransactionalBean
                extends EnterpriseBean
{
    /**
     * Represents an unspecified value for {@link #getTransactionTypeValue}.
     */
    int TRANSACTION_TYPE_UNSPECIFIED = -1;

    /**
     * Represents "Bean" for {@link #getTransactionTypeValue}.
     * 
     * @see org.eclipse.jst.j2ee.ejb.TransactionType#BEAN
     */
    int TRANSACTION_TYPE_BEAN = 0;

    /**
     * Represents "Container" for {@link #getTransactionTypeValue}.
     * 
     * @see org.eclipse.jst.j2ee.ejb.TransactionType#CONTAINER
     */
    int TRANSACTION_TYPE_CONTAINER = 1;

    /**
     * @return &lt;transaction-type>
     *         <ul>
     *         <li>{@link #TRANSACTION_TYPE_UNSPECIFIED} if unspecified
     *         <li>{@link #TRANSACTION_TYPE_BEAN} - Bean
     *         <li>{@link #TRANSACTION_TYPE_CONTAINER} - Container
     *         </ul>
     */
    int getTransactionTypeValue();
}
