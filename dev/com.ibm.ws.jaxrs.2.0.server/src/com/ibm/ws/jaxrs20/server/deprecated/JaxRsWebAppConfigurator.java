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
package com.ibm.ws.jaxrs20.server.deprecated;

import com.ibm.ws.jaxrs20.metadata.JaxRsModuleInfo;
import com.ibm.wsspi.webcontainer.webapp.WebAppConfig;

/**
 *
 */
@Deprecated
public interface JaxRsWebAppConfigurator {

    /**
     * Publish necessary info in jaxWsModuleInfo to WebAppConfig
     * 
     * @param jaxWsModuleInfo
     * @param servletContext
     */
    public void configure(JaxRsModuleInfo jaxWsModuleInfo, WebAppConfig webAppConfig);

}
