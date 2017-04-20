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
package com.ibm.ws.jaxws.web;

import com.ibm.ws.jaxws.endpoint.JaxWsEndpointConfigurator;
import com.ibm.ws.jaxws.endpoint.JaxWsPublisherContext;
import com.ibm.ws.jaxws.endpoint.JaxWsWebEndpoint;
import com.ibm.ws.jaxws.metadata.EndpointInfo;
import com.ibm.ws.jaxws.metadata.EndpointType;

/**
 *
 */
public class JaxWsServletEndpointConfigurator implements JaxWsEndpointConfigurator {

    @Override
    public JaxWsWebEndpoint createWebEndpoint(EndpointInfo endpointInfo, JaxWsPublisherContext context) {
        return new POJOJaxWsWebEndpoint(endpointInfo, context);
    }

    @Override
    public EndpointType getSupportedEndpointType() {
        return EndpointType.SERVLET;
    }

    @Override
    public <T> T getEndpointProperty(String name, Class<T> valueClassType) {
        if (USE_NAMESPACE_COLLABORATOR.equals(name)) {
            return valueClassType.cast(Boolean.FALSE);
        }
        return null;
    }

}
