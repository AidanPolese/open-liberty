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
package com.ibm.ws.container.service.config;

/**
 * Used to create a new instance of ServletConfiguratorHelper
 */
public interface ServletConfiguratorHelperFactory {

    /**
     * Create and return a new instance of ServletConfiguratorHelper
     * 
     * @param configurator
     * @return
     */
    ServletConfiguratorHelper createConfiguratorHelper(ServletConfigurator configurator);
}
