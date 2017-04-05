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
package com.ibm.ws.jaxws.endpoint;

import com.ibm.ws.jaxws.metadata.EndpointInfo;

/**
 * EndpointPublisher provides the abstraction for publishing the target endpoint into the target container.
 * e.g. Web Container, JMS Container and etc.
 */
public interface EndpointPublisher {

    /**
     * Publish the endpointInfo to the supported container,
     * 
     * @param endpointInfo endpoint meta data
     * @param context
     */
    public void publish(EndpointInfo endpointInfo, JaxWsPublisherContext context);

    /**
     * Return the endpoint publisher type, e.g. WEB and etc.
     * 
     * @return
     */
    public String getType();
}
