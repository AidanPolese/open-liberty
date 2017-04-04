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
import com.ibm.ws.jaxws.metadata.EndpointType;

/**
 *
 */
public class JaxWsEndpointConfiguratorManager {

    private static final TraceComponent tc = Tr.register(JaxWsEndpointConfiguratorManager.class);

    public Map<EndpointType, JaxWsEndpointConfigurator> endpointTypeJaxWsEndpointConfiguratorMap = new ConcurrentHashMap<EndpointType, JaxWsEndpointConfigurator>();

    public void registerJaxWsEndpointConfigurator(JaxWsEndpointConfigurator configurator) {
        if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
            Tr.debug(this, tc, "Register JaxWsEndpointConfigurator support " + configurator.getSupportedEndpointType());
        }
        endpointTypeJaxWsEndpointConfiguratorMap.put(configurator.getSupportedEndpointType(), configurator);
    }

    public void unregisterJaxWsEndpointConfigurator(JaxWsEndpointConfigurator configurator) {
        if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
            Tr.debug(this, tc, "unregister JaxWsEndpointConfigurator support " + configurator.getSupportedEndpointType());
        }
        endpointTypeJaxWsEndpointConfiguratorMap.remove(configurator.getSupportedEndpointType());
    }

    public JaxWsEndpointConfigurator getJaxWsEndpointConfigurator(EndpointType endpointType) {
        return endpointTypeJaxWsEndpointConfiguratorMap.get(endpointType);
    }
}
