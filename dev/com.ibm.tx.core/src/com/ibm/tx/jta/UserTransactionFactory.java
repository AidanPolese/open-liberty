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
package com.ibm.tx.jta;

import javax.transaction.UserTransaction;

/**
 *
 */
public class UserTransactionFactory {

    private static UserTransaction _ut;

    public static UserTransaction getUserTransaction() {
        return _ut;
    }

    /**
     * DS method
     */
    public void setUT(UserTransaction ut) {
        _ut = ut;
    }

    /**
     * DS method
     */
    public void unsetUT(UserTransaction ut) {
        _ut = null;
    }
}