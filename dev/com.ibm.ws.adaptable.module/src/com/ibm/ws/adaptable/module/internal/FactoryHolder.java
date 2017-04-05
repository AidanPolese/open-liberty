/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2011
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.adaptable.module.internal;

import com.ibm.wsspi.adaptable.module.adapters.AdapterFactoryService;
import com.ibm.wsspi.artifact.factory.ArtifactContainerFactory;
import com.ibm.wsspi.artifact.overlay.OverlayContainerFactory;

/**
 * Small interface to allow the created entries/containers to query back to obtain DS injected factories.
 */
public interface FactoryHolder {
    /**
     * Gets the adapter factory service injected by DS.
     * 
     * @return adapter factory service.
     * @throws IllegalStateException if DS has removed the service (should not happen, as we provide the impl).
     */
    AdapterFactoryService getAdapterFactoryService();

    /**
     * Gets the container factory service injected by DS.
     * 
     * @return container factory service.
     * @throws IllegalStateException if DS has removed the service. (DS will deactivate the adaptableModuleFactory, as the container factory is required)
     */
    ArtifactContainerFactory getContainerFactory();

    /**
     * Gets the overlay container factory service injected by DS.
     * 
     * @return overlay container factory service.
     * @throws IllegalStateException if DS has removed the service. (DS will deactivate the adaptableModuleFactory, as the overlay container factory is required)
     */
    OverlayContainerFactory getOverlayContainerFactory();
}
