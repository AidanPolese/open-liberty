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
package com.ibm.ws.jaxrs20.support;

import com.ibm.ws.container.service.app.deploy.extended.ExtendedModuleInfo;
import com.ibm.ws.container.service.app.deploy.extended.ExtendedWebModuleInfo;
import com.ibm.wsspi.adaptable.module.UnableToAdaptException;

/**
 *
 */
public interface JaxRsWebContainerManager {

    /**
     * Create Web Router module based on the ModuleInfo parameter, it is mostly used for EJB based web services
     *
     * @param moduleInfo
     * @return
     * @throws UnableToAdaptException
     */
    public ExtendedWebModuleInfo createWebModuleInfo(ExtendedModuleInfo moduleInfo) throws UnableToAdaptException;

}