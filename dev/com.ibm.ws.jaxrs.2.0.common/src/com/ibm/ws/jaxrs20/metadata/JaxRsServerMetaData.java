/*******************************************************************************
 * Copyright (c) 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.ibm.ws.jaxrs20.metadata;

import com.ibm.ws.jaxrs20.bus.LibertyApplicationBus;
import com.ibm.ws.jaxrs20.bus.LibertyApplicationBusFactory;
import com.ibm.ws.jaxrs20.cache.LibertyJaxRsProviderCache;
import com.ibm.ws.jaxrs20.cache.LibertyJaxRsResourceMethodCache;

/**
 *
 */
public class JaxRsServerMetaData {

    private final LibertyApplicationBus applicationBus;

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

//    /**
//     * Add the Endpoint portLink and J2EEName pair
//     * 
//     * @param endpointName
//     * @param j2eeName
//     */
//    public void putEndpointNameAndJ2EENameEntry(String endpointName, J2EEName j2eeName) {
//        endpointNameJ2EENameMap.put(endpointName, j2eeName);
//    }
//
//    /**
//     * Get the J2EEName by endpointName
//     * 
//     * @param endpointName
//     * @return
//     */
//    public J2EEName getEndpointJ2EEName(String endpointName) {
//        return endpointNameJ2EENameMap.get(endpointName);
//    }
//
//    /**
//     * Get the endpoint name by j2eeName
//     * 
//     * @param j2eeName
//     * @return
//     */
//    public String retrieveEndpointName(J2EEName j2eeName) {
//        for (Entry<String, J2EEName> entry : endpointNameJ2EENameMap.entrySet()) {
//            if (entry.getValue().equals(j2eeName)) {
//                return entry.getKey();
//            }
//        }
//        return null;
//    }

}
