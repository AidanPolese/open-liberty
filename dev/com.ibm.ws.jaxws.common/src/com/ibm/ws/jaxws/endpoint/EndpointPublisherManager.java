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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;

/**
 *
 */
public class EndpointPublisherManager {

    private static final TraceComponent tc = Tr.register(EndpointPublisherManager.class);

    public Map<String, EndpointPublisher> typeEndpointPublisherManagerMap = new ConcurrentHashMap<String, EndpointPublisher>();

    public void registerEndpointPublisher(EndpointPublisher publisher) {
        if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
            Tr.debug(this, tc, "Register EndpointPublisher support " + publisher.getType());
        }
        typeEndpointPublisherManagerMap.put(publisher.getType(), publisher);
    }

    public void unregisterEndpointPublisher(EndpointPublisher publisher) {
        if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
            Tr.debug(this, tc, "unregister EndpointPublisher support " + publisher.getType());
        }
        typeEndpointPublisherManagerMap.remove(publisher.getType());
    }

    public EndpointPublisher getEndpointPublisher(String type) {
        return typeEndpointPublisherManagerMap.get(type);
    }
}
