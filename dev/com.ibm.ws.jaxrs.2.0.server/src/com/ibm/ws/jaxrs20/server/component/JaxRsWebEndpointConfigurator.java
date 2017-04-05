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
package com.ibm.ws.jaxrs20.server.component;

import org.osgi.service.component.annotations.Component;

import com.ibm.ws.jaxrs20.api.JaxRsEndpointConfigurator;
import com.ibm.ws.jaxrs20.endpoint.JaxRsPublisherContext;
import com.ibm.ws.jaxrs20.metadata.EndpointInfo;
import com.ibm.ws.jaxrs20.server.JaxRsWebEndpointImpl;

/**
 *
 */
@Component(service = { JaxRsEndpointConfigurator.class }, property = { "service.vendor=IBM" })
public class JaxRsWebEndpointConfigurator implements JaxRsEndpointConfigurator {

    @Override
    public JaxRsWebEndpointImpl createWebEndpoint(EndpointInfo endpointInfo, JaxRsPublisherContext context) {
        return new JaxRsWebEndpointImpl(endpointInfo, context);
    }

    @Override
    public <T> T getEndpointProperty(String name, Class<T> valueClassType) {
        if (USE_NAMESPACE_COLLABORATOR.equals(name)) {
            return valueClassType.cast(Boolean.FALSE);
        }
        return null;
    }

}
