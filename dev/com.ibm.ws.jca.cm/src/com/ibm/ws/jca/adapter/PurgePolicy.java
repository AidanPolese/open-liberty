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
package com.ibm.ws.jca.adapter;

/**
 * Configurable values for purgePolicy
 */
public enum PurgePolicy {
    EntirePool,
    FailingConnectionOnly,
    ValidateAllConnections
}
