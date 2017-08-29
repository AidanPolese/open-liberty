/*
* IBM Confidential
*
* OCO Source Materials
*
* WLP Copyright IBM Corp. 2017
*
* The source code for this program is not published or otherwise divested
* of its trade secrets, irrespective of what has been deposited with the
* U.S. Copyright Office.
*/
package com.ibm.ws.jaxrs20.component;

import java.util.ArrayList;
import java.util.List;

import org.apache.cxf.Bus;
import org.apache.cxf.endpoint.Server;
import org.apache.cxf.feature.AbstractFeature;
import org.apache.cxf.feature.Feature;
import org.apache.cxf.jaxrs.provider.ServerProviderFactory;
import org.apache.cxf.jaxrs.sse.SseContextProvider;
import org.osgi.service.component.annotations.Component;

import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.jaxrs21.sse.LibertySseEventSinkContextProvider;

/**
 * This feature is a DS service, allowing it to be auto registered via
 * the JaxRsWebEndpointConfigurator. It extends CXF's SseFeature, but
 * also provides additional configuration that is more specific to
 * Liberty.
 */
@Component(immediate = true)
public class LibertySseFeature extends AbstractFeature implements Feature {
    @SuppressWarnings("unused")
    private final static TraceComponent tc = Tr.register(LibertySseFeature.class);

    public LibertySseFeature() {
        super();
    }

    /* (non-Javadoc)
     * @see org.apache.cxf.feature.Feature#initialize(org.apache.cxf.endpoint.Server, org.apache.cxf.Bus)
     */
    @Override
    public void initialize(Server server, Bus bus) {
        List<Object> providers = new ArrayList<>();

        providers.add(new LibertySseEventSinkContextProvider());
        providers.add(new SseContextProvider());

        ((ServerProviderFactory)server.getEndpoint().get(ServerProviderFactory.class
                                                         .getName())).setUserProviders(providers);
    }

}
