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
package com.ibm.ws.jaxrs20.api;

import java.util.Set;

import com.ibm.ws.jaxrs20.metadata.JaxRsModuleInfo;
import com.ibm.ws.jaxrs20.metadata.JaxRsModuleType;
import com.ibm.ws.jaxrs20.metadata.builder.JaxRsModuleInfoBuilderContext;
import com.ibm.wsspi.adaptable.module.UnableToAdaptException;

/**
 * The extension of a JaxWsModuleInfoBuilder to help build JaxWsModuleInfo.
 */
public interface JaxRsModuleInfoBuilderExtension {

    public void preBuild(JaxRsModuleInfoBuilderContext jaxWsModuleInfoBuilderContext, JaxRsModuleInfo jaxWsModuleInfo) throws UnableToAdaptException;

    public void postBuild(JaxRsModuleInfoBuilderContext jaxWsModuleInfoBuilderContext, JaxRsModuleInfo jaxWsModuleInfo) throws UnableToAdaptException;

    public Set<JaxRsModuleType> getSupportTypes();

}
