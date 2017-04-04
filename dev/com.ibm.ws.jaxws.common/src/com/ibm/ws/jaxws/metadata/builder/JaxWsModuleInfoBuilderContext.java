/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2013
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.jaxws.metadata.builder;

import java.util.HashMap;
import java.util.Map;

import com.ibm.ws.runtime.metadata.ModuleMetaData;
import com.ibm.wsspi.adaptable.module.Container;

/**
 * The context used in JaxWsModuleInfoBuilder and its extensions
 */
public class JaxWsModuleInfoBuilderContext {

    private ModuleMetaData moduleMetaData;

    private Container container;

    private EndpointInfoBuilderContext endpointInfoBuilderContext;

    private final Map<String, Object> contextEnv = new HashMap<String, Object>();

    public JaxWsModuleInfoBuilderContext(ModuleMetaData moduleMetaData, Container container, EndpointInfoBuilderContext endpointInfoBuilderContext) {
        this.moduleMetaData = moduleMetaData;
        this.container = container;
        this.endpointInfoBuilderContext = endpointInfoBuilderContext;
    }

    public EndpointInfoBuilderContext getEndpointInfoBuilderContext() {
        return endpointInfoBuilderContext;
    }

    public void setEndpointInfoBuilderContext(EndpointInfoBuilderContext endpointInfoBuilderContext) {
        this.endpointInfoBuilderContext = endpointInfoBuilderContext;
    }

    /**
     * @return the contextEnv object based on key
     */
    public Object getContextEnv(String key) {
        return this.contextEnv.get(key);
    }

    public void addContextEnv(String key, Object value) {
        this.contextEnv.put(key, value);
    }

    public void clearContextEnv() {
        this.contextEnv.clear();
    }

    public ModuleMetaData getModuleMetaData() {
        return moduleMetaData;
    }

    public void setModuleMetaData(ModuleMetaData moduleMetaData) {
        this.moduleMetaData = moduleMetaData;
    }

    public Container getContainer() {
        return container;
    }

    public void setContainer(Container container) {
        this.container = container;
    }

}
