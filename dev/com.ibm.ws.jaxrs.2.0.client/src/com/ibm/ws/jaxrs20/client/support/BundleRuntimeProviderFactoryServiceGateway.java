/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2014
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.jaxrs20.client.support;

import org.osgi.framework.ServiceReference;
import org.osgi.service.component.ComponentContext;

import com.ibm.ws.jaxrs20.api.JaxRsProviderFactoryService;
import com.ibm.wsspi.kernel.service.utils.AtomicServiceReference;

/**
 * as a more reasonable method, for each bundle, it is better to use one service to import & export the service instances
 * this class works as gateway to import service, for client, no service is required to export
 */
public class BundleRuntimeProviderFactoryServiceGateway {

    private static BundleRuntimeProviderFactoryServiceGateway instance;

    public static BundleRuntimeProviderFactoryServiceGateway getInstance() {
        return instance;
    }

    public JaxRsProviderFactoryService getProviderFactory() {
        return providerFactoryServiceSR.getServiceWithException();
    }

    private final AtomicServiceReference<JaxRsProviderFactoryService> providerFactoryServiceSR =
                    new AtomicServiceReference<JaxRsProviderFactoryService>("providerFactoryService");

    protected void activate(ComponentContext cc) {
        if (instance == null) {
            instance = this;
        }
        providerFactoryServiceSR.activate(cc);
    }

    protected void deactivate(ComponentContext cc) {
        providerFactoryServiceSR.deactivate(cc);
    }

    public void setProviderFactoryService(ServiceReference<JaxRsProviderFactoryService> serviceRef) {
        providerFactoryServiceSR.setReference(serviceRef);
    }

    public void unsetProviderFactoryService(ServiceReference<JaxRsProviderFactoryService> serviceRef) {
        providerFactoryServiceSR.unsetReference(serviceRef);
    }
}
