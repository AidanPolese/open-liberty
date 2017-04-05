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
package com.ibm.ws.jaxws.metadata.builder;

import com.ibm.ws.container.service.app.deploy.extended.ExtendedModuleInfo;
import com.ibm.ws.jaxws.metadata.JaxWsModuleInfo;
import com.ibm.ws.jaxws.metadata.JaxWsModuleType;
import com.ibm.ws.runtime.metadata.ModuleMetaData;
import com.ibm.wsspi.adaptable.module.Container;
import com.ibm.wsspi.adaptable.module.UnableToAdaptException;

/**
 * The implementations of this interface will analysis the current module and add EndpointInfo in the JaxWsModuleInfo instance
 */
public interface JaxWsModuleInfoBuilder {

    public ExtendedModuleInfo build(ModuleMetaData moduleMetaData, Container containerToAdapt, JaxWsModuleInfo jaxWsModuleInfo) throws UnableToAdaptException;

    public JaxWsModuleType getSupportType();

}
