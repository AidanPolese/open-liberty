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
package com.ibm.ws.jaxrs20.endpoint;

import java.util.HashMap;
import java.util.Map;

import org.apache.cxf.Bus;

import com.ibm.ws.container.service.app.deploy.ModuleInfo;
import com.ibm.ws.jaxrs20.metadata.JaxRsModuleMetaData;
import com.ibm.wsspi.adaptable.module.Container;

/**
 *
 */
public class JaxRsPublisherContext {

    public static final String SERVLET_CONTEXT = "SERVLET_CONTEXT";

    private final JaxRsModuleMetaData moduleMetaData;

    private final Container publisherModuleContainer;

    private final ModuleInfo publisherModuleInfo;

    private final Map<String, Object> attributes = new HashMap<String, Object>();

    public JaxRsPublisherContext(JaxRsModuleMetaData moduleMetaData, Container publisherModuleContainer, ModuleInfo publisherModuleInfo) {
        this.moduleMetaData = moduleMetaData;
        this.publisherModuleContainer = publisherModuleContainer;
        this.publisherModuleInfo = publisherModuleInfo;
    }

    /**
     * @return the applicationBus
     */
    public Bus getServerBus() {
        return moduleMetaData.getServerMetaData().getServerBus();
    }

    public ModuleInfo getPublisherModuleInfo() {
        return publisherModuleInfo;
    }

    public Container getPublisherModuleContainer() {
        return publisherModuleContainer;
    }

    public void setAttribute(String name, Object value) {
        attributes.put(name, value);
    }

    public Object getAttribute(String name) {
        return attributes.get(name);
    }

    public <T> T getAttribute(String name, Class<T> cls) {
        return cls.cast(attributes.get(name));
    }

    public JaxRsModuleMetaData getModuleMetaData() {
        return this.moduleMetaData;
    }
}
