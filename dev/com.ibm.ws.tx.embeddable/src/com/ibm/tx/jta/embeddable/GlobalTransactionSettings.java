/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2013
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.tx.jta.embeddable;

/**
 * Global transaction settings
 */
public interface GlobalTransactionSettings {
    /**
     * @return int The component transaction timeout value.
     */
    public int getTransactionTimeout();
}