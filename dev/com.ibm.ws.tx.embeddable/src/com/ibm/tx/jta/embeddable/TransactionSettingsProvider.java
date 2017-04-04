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
 * Provide global/local transaction settings
 */
public interface TransactionSettingsProvider {
	
	public boolean isActive();

    public GlobalTransactionSettings getGlobalTransactionSettings();

    public LocalTransactionSettings getLocalTransactionSettings();
}