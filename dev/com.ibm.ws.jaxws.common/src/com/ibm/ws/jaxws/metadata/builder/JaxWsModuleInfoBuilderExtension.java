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

import java.util.Set;

import com.ibm.ws.jaxws.metadata.JaxWsModuleInfo;
import com.ibm.ws.jaxws.metadata.JaxWsModuleType;
import com.ibm.wsspi.adaptable.module.UnableToAdaptException;

/**
 * The extension of a JaxWsModuleInfoBuilder to help build JaxWsModuleInfo.
 */
public interface JaxWsModuleInfoBuilderExtension {

    public void preBuild(JaxWsModuleInfoBuilderContext jaxWsModuleInfoBuilderContext, JaxWsModuleInfo jaxWsModuleInfo) throws UnableToAdaptException;

    public void postBuild(JaxWsModuleInfoBuilderContext jaxWsModuleInfoBuilderContext, JaxWsModuleInfo jaxWsModuleInfo) throws UnableToAdaptException;

    public Set<JaxWsModuleType> getSupportTypes();

}
