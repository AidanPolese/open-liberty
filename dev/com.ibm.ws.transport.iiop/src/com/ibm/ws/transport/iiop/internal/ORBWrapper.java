/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2015
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
/*
 * Some of the code was derived from code supplied by the Apache Software Foundation licensed under the Apache License, Version 2.0.
 */
package com.ibm.ws.transport.iiop.internal;

import static org.osgi.service.component.annotations.ConfigurationPolicy.REQUIRE;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.component.annotations.ReferencePolicyOption;

import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.transport.iiop.config.ConfigAdapter;
import com.ibm.ws.transport.iiop.spi.IIOPEndpoint;
import com.ibm.ws.transport.iiop.spi.SubsystemFactory;

/**
 * Provides access to the ORB.
 */
@Component(configurationPolicy = REQUIRE,
                service = {},
                property = { "service.vendor=IBM", "service.ranking:Integer=5" })
public class ORBWrapper extends ReadyListenerImpl {
    private static final TraceComponent tc = Tr.register(ORBWrapper.class);
    public static final String pid = ORBWrapperInternal.class.getName();

    private final List<IIOPEndpoint> endpoints = new ArrayList<IIOPEndpoint>();

    private final Map<String, Object> extraConfig = new HashMap<String, Object>();

    @Activate
    protected void activate(Map<String, Object> properties, ComponentContext cc) throws Exception {
        super.activate(properties, cc.getBundleContext());
    }

    /** {@inheritDoc} */
    @Override
    void register() {
        for (SubsystemFactory sf : subsystemFactories.keySet()) {
            sf.register(this, properties, endpoints);
        }
    }

    @Deactivate
    protected void deactivate(ComponentContext cc) {
        super.deactivate();
    }

    @Reference
    protected void setConfigAdapter(ConfigAdapter configAdapter) {}

    @Reference(cardinality = ReferenceCardinality.MULTIPLE, policy = ReferencePolicy.STATIC, policyOption = ReferencePolicyOption.GREEDY)
    protected void setIiopEndpoint(IIOPEndpoint ep) {
        endpoints.add(ep);
    }

}
