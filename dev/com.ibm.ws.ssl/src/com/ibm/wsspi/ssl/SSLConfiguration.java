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
package com.ibm.wsspi.ssl;

/**
 * Marker interface for a configured repertoire. Use this to allow one component
 * to track/require a configuration other than the default.
 */
public interface SSLConfiguration {
    /**
     * Returns the alias for this SSL configuration.
     * 
     * @return the alias for this SSL configuration.
     */
    String getAlias();
}
