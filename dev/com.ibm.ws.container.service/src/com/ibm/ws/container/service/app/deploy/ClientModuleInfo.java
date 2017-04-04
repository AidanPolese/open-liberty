/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2014
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.container.service.app.deploy;

/**
 * Contains information about an application client
 */
public interface ClientModuleInfo extends ModuleInfo {

    /**
     * Returns the main class name for the client
     */
    public String getMainClassName();
}
