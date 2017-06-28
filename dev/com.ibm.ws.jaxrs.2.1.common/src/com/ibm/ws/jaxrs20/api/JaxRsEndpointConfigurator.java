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

import com.ibm.ws.jaxrs20.endpoint.JaxRsPublisherContext;
import com.ibm.ws.jaxrs20.endpoint.JaxRsWebEndpoint;
import com.ibm.ws.jaxrs20.metadata.EndpointInfo;

/**
 * JaxWsEndpointConfigurator provides the abstraction for the different Web Services configurator, e.g. POJO, EJB and etc.
 */
public interface JaxRsEndpointConfigurator {

    public static final String USE_NAMESPACE_COLLABORATOR = "USE_NAMESPACE_COLLABORATOR";

    /**
     * Create the JaxWsEndpoint used to serve the requests
     * 
     * @param endpointInfo
     * @param configuration
     * @return
     */
    public JaxRsWebEndpoint createWebEndpoint(EndpointInfo endpointInfo, JaxRsPublisherContext configuration);

    /**
     * Return the endpoint property for the configurator
     * 
     * @param name property name
     * @param valueClassType property value class type
     * @return the property value or null if the property is not existed
     */
    public <T> T getEndpointProperty(String name, Class<T> valueClassType);
}
