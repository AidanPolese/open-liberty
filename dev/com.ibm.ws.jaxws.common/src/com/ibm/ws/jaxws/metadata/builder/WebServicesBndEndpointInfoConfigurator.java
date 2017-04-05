/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2013
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.jaxws.metadata.builder;

import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;

import com.ibm.ws.javaee.ddmodel.wsbnd.WebserviceEndpoint;
import com.ibm.ws.javaee.ddmodel.wsbnd.WebservicesBnd;
import com.ibm.ws.jaxws.metadata.EndpointInfo;
import com.ibm.ws.jaxws.utils.URLUtils;
import com.ibm.wsspi.adaptable.module.Container;
import com.ibm.wsspi.adaptable.module.UnableToAdaptException;

/**
 * process custom binding file ibm-ws-bnd.xml
 */
@Component(service = { EndpointInfoConfigurator.class }, configurationPolicy = ConfigurationPolicy.IGNORE, immediate = false, property = { "service.vendor=IBM" })
public class WebServicesBndEndpointInfoConfigurator extends AbstractEndpointInfoConfigurator {

    /**
     * @param phase
     */
    public WebServicesBndEndpointInfoConfigurator() {
        super(EndpointInfoConfigurator.Phase.POST_PROCESS_DESCRIPTOR);
    }

    @Override
    public void prepare(EndpointInfoBuilderContext context, EndpointInfo endpointInfo) throws UnableToAdaptException {

    }

    @Override
    public void config(EndpointInfoBuilderContext context, EndpointInfo endpointInfo) throws UnableToAdaptException {
        Container container = context.getContainer();
        WebservicesBnd webservicesBnd = container.adapt(WebservicesBnd.class);

        if (webservicesBnd == null) {
            return;
        }

        // set default endpoint properties
        Map<String, String> defaultProperties = webservicesBnd.getWebserviceEndpointProperties();
        if (defaultProperties != null && !defaultProperties.isEmpty()) {
            endpointInfo.setEndpointProperties(defaultProperties);
        }

        // set endpoint address
        String portComponentName = endpointInfo.getPortComponentName();
        WebserviceEndpoint webserviceEndpoint = webservicesBnd.getWebserviceEndpoint(portComponentName);

        if (webserviceEndpoint != null) {
            String address = webserviceEndpoint.getAddress();
            address = URLUtils.normalizePath(address);
            if (address != null && !address.isEmpty()) {
                endpointInfo.clearAddresses();
                endpointInfo.addAddress(address);
            }

            // set endpoint properties specified in the endpoint element
            Map<String, String> properties = webserviceEndpoint.getProperties();
            if (properties != null && !properties.isEmpty()) {
                if (endpointInfo.getEndpointProperties() != null) {
                    endpointInfo.getEndpointProperties().putAll(properties);
                } else {
                    endpointInfo.setEndpointProperties(properties);
                }
            }
        }
    }

}