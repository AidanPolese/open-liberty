/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2014
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
package com.ibm.ws.transport.iiop.internal;

import org.apache.yoko.osgi.locator.BundleProviderLoader;
import org.apache.yoko.osgi.locator.Register;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

@Component(configurationPolicy = ConfigurationPolicy.IGNORE)
public class WSClassRegistration {
    private Register providerRegistry;
    private BundleProviderLoader proClass;
    private BundleProviderLoader utilClass;

    @Reference
    protected void setRegister(Register providerRegistry) {
        this.providerRegistry = providerRegistry;
    }

    @Activate
    protected void activate(BundleContext bundleContext) {
        Bundle bundle = bundleContext.getBundle();

        proClass = new BundleProviderLoader("javax.rmi.CORBA.PortableRemoteObjectClass", WSPortableRemoteObjectImpl.class.getName(), bundle, 2);
        providerRegistry.registerService(proClass);

        utilClass = new BundleProviderLoader("javax.rmi.CORBA.UtilClass", WSUtilImpl.class.getName(), bundle, 2);
        providerRegistry.registerService(utilClass);
    }

    @Deactivate
    protected void deactivate() {
        providerRegistry.unregisterService(proClass);
        providerRegistry.unregisterService(utilClass);
    }
}
