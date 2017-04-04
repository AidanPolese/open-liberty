/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2011
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.container.service.app.deploy;

/**
 * Helps to retrieve nested configuration for an application
 */
public interface NestedConfigHelper {

    /**
     * Get the named property
     * 
     * @param propName
     * @return
     */
    public Object get(String propName);
}
