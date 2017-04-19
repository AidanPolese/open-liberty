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
package com.ibm.ws.rest.handler.internal.service;

import java.io.IOException;
import java.util.Map;

import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Deactivate;

import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.wsspi.rest.handler.RESTRequest;
import com.ibm.wsspi.rest.handler.RESTResponse;
import com.ibm.wsspi.rest.handler.helper.RESTRoutingHelper;

/**
 * <p>This class gets injected with different RESTHandler implementations and holds a reference to those services. It also keeps a set
 * of rest handler registered roots for fast searching.
 *
 * <p>The main function of this container is to be able to match an incoming URL request to its appropriate registered rest handler.
 */
@Component(service = { RESTRoutingHelper.class },
           configurationPolicy = ConfigurationPolicy.IGNORE,
           immediate = true,
           property = { "service.vendor=IBM", "service.ranking:Integer=-1" })
public class DefaultRoutingHelper implements RESTRoutingHelper {
    private static final TraceComponent tc = Tr.register(DefaultRoutingHelper.class);

    @Activate
    protected void activate(ComponentContext context, Map<String, Object> properties) {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEventEnabled()) {
            Tr.event(this, tc, "Activating DefaultRoutingHelper", properties);
        }
    }

    @Deactivate
    protected void deactivate(ComponentContext context, int reason) {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEventEnabled()) {
            Tr.event(this, tc, "Deactivating, reason=" + reason);
        }
    }

    @Override
    public boolean routingAvailable() {
        return false;
    }

    @Override
    public void routeRequest(RESTRequest request, RESTResponse response) throws IOException {
        // nothing to do
    }

    @Override
    public void routeRequest(RESTRequest request, RESTResponse response, boolean legacyURI) throws IOException {
        // nothing to do
    }

    @Override
    public boolean containsLegacyRoutingContext(RESTRequest request) {
        return false;
    }

    @Override
    public boolean containsRoutingContext(RESTRequest request) {
        return false;
    }
}
