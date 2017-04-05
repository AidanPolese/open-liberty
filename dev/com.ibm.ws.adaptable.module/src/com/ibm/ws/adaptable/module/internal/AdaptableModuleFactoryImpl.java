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

import java.io.File;

import org.osgi.service.component.ComponentContext;

import com.ibm.wsspi.adaptable.module.AdaptableModuleFactory;
import com.ibm.wsspi.adaptable.module.Container;
import com.ibm.wsspi.adaptable.module.adapters.AdapterFactoryService;
import com.ibm.wsspi.artifact.ArtifactContainer;
import com.ibm.wsspi.artifact.factory.ArtifactContainerFactory;
import com.ibm.wsspi.artifact.overlay.OverlayContainer;
import com.ibm.wsspi.artifact.overlay.OverlayContainerFactory;

/**
 *     
 */
public class AdaptableModuleFactoryImpl implements AdaptableModuleFactory, FactoryHolder {

    private volatile AdapterFactoryService adapterFactoryService;
    private volatile ArtifactContainerFactory containerFactory;
    private volatile OverlayContainerFactory overlayContainerFactory;

    protected void activate(ComponentContext ctx) {
        //no op
    }

    protected void deactivate(ComponentContext ctx) {
        this.containerFactory = null;
        this.overlayContainerFactory = null;
        this.adapterFactoryService = null;
    }

    protected void setAdapterFactoryService(AdapterFactoryService afs) {
        this.adapterFactoryService = afs;
    }

    protected void setContainerFactory(ArtifactContainerFactory cf) {
        this.containerFactory = cf;
    }

    protected void setOverlayContainerFactory(OverlayContainerFactory ocf) {
        this.overlayContainerFactory = ocf;
    }

    protected void unsetAdapterFactoryService(AdapterFactoryService afs) {
        if (this.adapterFactoryService == afs) {
            this.adapterFactoryService = null;
        }
    }

    protected void unsetContainerFactory(ArtifactContainerFactory cf) {
        if (this.containerFactory == cf) {
            this.containerFactory = null;
        }
    }

    protected void unsetOverlayContainerFactory(OverlayContainerFactory ocf) {
        if (this.overlayContainerFactory == ocf) {
            this.overlayContainerFactory = null;
        }
    }

    /** {@inheritDoc} */
    @Override
    public Container getContainer(File overlayDir, File cacheDirForOverlayContent, ArtifactContainer container) {
        com.ibm.wsspi.artifact.overlay.OverlayContainer o = getOverlayContainerFactory().createOverlay(OverlayContainer.class, container);
        if (o != null) {
            o.setOverlayDirectory(cacheDirForOverlayContent, overlayDir);
            AdaptableContainerImpl a = new AdaptableContainerImpl(o, this);
            return a;
        }
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public AdapterFactoryService getAdapterFactoryService() {
        if (adapterFactoryService != null)
            return adapterFactoryService;
        else
            throw new IllegalStateException();

    }

    /** {@inheritDoc} */
    @Override
    public ArtifactContainerFactory getContainerFactory() {
        if (containerFactory != null)
            return containerFactory;
        else
            throw new IllegalStateException();
    }

    /** {@inheritDoc} */
    @Override
    public OverlayContainerFactory getOverlayContainerFactory() {
        if (overlayContainerFactory != null)
            return overlayContainerFactory;
        else
            throw new IllegalStateException();
    }

}
