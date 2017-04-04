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
package com.ibm.ws.jaxws.support;

import com.ibm.ws.container.service.app.deploy.ModuleInfo;
import com.ibm.ws.container.service.app.deploy.WebModuleInfo;
import com.ibm.ws.container.service.state.StateChangeException;
import com.ibm.wsspi.adaptable.module.UnableToAdaptException;

/**
 *
 */
public interface JaxWsWebContainerManager {

    /**
     * Stop the WebModule, the WebModuleInfo must be created with {@link #createWebModuleInfo(ModuleInfo)}
     * 
     * @param webModuleInfoInfo
     * @throws StateChangeException
     */
    public void stopWebModuleInfo(WebModuleInfo webModuleInfoInfo) throws StateChangeException;

    /**
     * Create Web Router module based on the ModuleInfo parameter, it is mostly used for EJB based web services
     * 
     * @param moduleInfo
     * @return
     * @throws UnableToAdaptException
     */
    public WebModuleInfo createWebModuleInfo(ModuleInfo moduleInfo, String contextPath) throws UnableToAdaptException;

    /**
     * Start Web Web Module, the WebModuleInfo must be created with {@link #createWebModuleInfo(ModuleInfo)}
     * 
     * @param webModuleInfo
     * @throws StateChangeException
     */
    public void startWebModuleInfo(WebModuleInfo webModuleInfo) throws StateChangeException;

    /**
     * Attach webModuleInfo to the lifecycle of ModuleInfo. The {@linkplain #startWebModuleInfo(WebModuleInfo)} will be invoked
     * while ModuleInfo is started, and {@link #stopWebModuleInfo(WebModuleInfo)} will be invoked while ModuleInfo is stopping
     * 
     * @param moduleInfo
     * @param webModuleInfo
     * @throws UnableToAdaptException
     */
    public void attachWebModuleInfo(ModuleInfo moduleInfo, WebModuleInfo webModuleInfo) throws UnableToAdaptException;

}