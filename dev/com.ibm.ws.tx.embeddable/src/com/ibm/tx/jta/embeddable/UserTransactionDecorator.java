/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2012, 2014
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
package com.ibm.tx.jta.embeddable;

import javax.naming.NamingException;
import javax.transaction.UserTransaction;

/**
 *
 */
public interface UserTransactionDecorator {
    /**
     *
     * @param ut the actual UserTransaction object
     * @param injection true if the object is being injected
     * @param injectionContext the injection target context if injection is true, or null if unspecified
     * @return the actual UserTransaction object or a wrapper
     * @throws NamingException
     */
    public UserTransaction decorateUserTransaction(UserTransaction ut, boolean injection, Object injectionContext) throws NamingException;
}