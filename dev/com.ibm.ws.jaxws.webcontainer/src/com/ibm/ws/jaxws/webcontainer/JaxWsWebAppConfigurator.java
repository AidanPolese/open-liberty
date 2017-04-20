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
package com.ibm.ws.jaxws.webcontainer;

import com.ibm.ws.jaxws.metadata.JaxWsModuleInfo;
import com.ibm.wsspi.webcontainer.webapp.WebAppConfig;

/**
 *
 */
public interface JaxWsWebAppConfigurator {

    /**
     * Publish necessary info in jaxWsModuleInfo to WebAppConfig
     * 
     * @param jaxWsModuleInfo
     * @param servletContext
     */
    public void configure(JaxWsModuleInfo jaxWsModuleInfo, WebAppConfig webAppConfig);

}
