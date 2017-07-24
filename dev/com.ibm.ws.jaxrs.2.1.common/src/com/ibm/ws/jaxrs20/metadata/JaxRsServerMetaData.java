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
package com.ibm.ws.jaxrs20.metadata;

import com.ibm.ws.jaxrs20.bus.LibertyApplicationBus;
import com.ibm.ws.jaxrs20.bus.LibertyApplicationBusFactory;
import com.ibm.ws.jaxrs20.cache.LibertyJaxRsProviderCache;
import com.ibm.ws.jaxrs20.cache.LibertyJaxRsResourceMethodCache;

/**
 *
 */
public class JaxRsServerMetaData {

    private LibertyApplicationBus applicationBus;

    private final JaxRsModuleMetaData moduleMetaData;

    private final LibertyJaxRsProviderCache providerCache = new LibertyJaxRsProviderCache();

    private final LibertyJaxRsResourceMethodCache resourceMethodCache = new LibertyJaxRsResourceMethodCache();

//    private final Map<String, J2EEName> endpointNameJ2EENameMap = new HashMap<String, J2EEName>();

    public JaxRsServerMetaData(JaxRsModuleMetaData moduleMetaData) {
        this.moduleMetaData = moduleMetaData;
        this.applicationBus = LibertyApplicationBusFactory.getInstance().createServerScopedBus(moduleMetaData);
        //add LibertyJaxRsProviderCache to server bus
        this.applicationBus.setExtension(providerCache, LibertyJaxRsProviderCache.class);
        //add LibertyJaxRsUriToResourceCache to server bus
        this.applicationBus.setExtension(resourceMethodCache, LibertyJaxRsResourceMethodCache.class);
    }

    public void destroy() {
        if (applicationBus != null)
            applicationBus.shutdown(false);
        applicationBus = null;

        //destroy provider cache
        this.providerCache.destroy();
        //destroy resourceMethodCache cache
        this.resourceMethodCache.destroy();
    }

    /**
     * @return the applicationBus
     */
    public LibertyApplicationBus getServerBus() {
        return applicationBus;
    }

    /**
     * @return the moduleMetaData
     */
    public JaxRsModuleMetaData getModuleMetaData() {
        return moduleMetaData;
    }
}
