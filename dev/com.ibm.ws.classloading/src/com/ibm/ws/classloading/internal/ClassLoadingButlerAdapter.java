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
package com.ibm.ws.classloading.internal;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;

import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.classloading.ClassLoadingButler;
import com.ibm.wsspi.adaptable.module.Container;
import com.ibm.wsspi.adaptable.module.UnableToAdaptException;
import com.ibm.wsspi.adaptable.module.adapters.ContainerAdapter;
import com.ibm.wsspi.artifact.ArtifactContainer;
import com.ibm.wsspi.artifact.overlay.OverlayContainer;

/**
 *
 */

@Component(service = ContainerAdapter.class,
           configurationPolicy = ConfigurationPolicy.OPTIONAL,
           immediate = true,
           property = { "service.vendor=IBM", "toType=com.ibm.ws.classloading.ClassLoadingButler" })
public class ClassLoadingButlerAdapter implements ContainerAdapter<ClassLoadingButler> {
    private final static TraceComponent tc = Tr.register(ClassLoadingButlerAdapter.class);

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.wsspi.adaptable.module.adapters.ContainerAdapter#adapt(com.ibm.wsspi.adaptable.module.Container, com.ibm.wsspi.adaptable.module.adapters.OverlayContainer,
     * com.ibm.wsspi.artifact.ArtifactContainer, com.ibm.wsspi.adaptable.module.Container)
     */
    @Override
    public ClassLoadingButler adapt(Container root, OverlayContainer rootOverlay, ArtifactContainer artifactContainer, Container containerToAdapt) throws UnableToAdaptException {
        ClassLoadingButler butler = (ClassLoadingButler) rootOverlay.getFromNonPersistentCache(artifactContainer.getPath(), ClassLoadingButler.class);
        if (butler != null) {
            return butler;
        }

        butler = new ClassLoadingButlerImpl(containerToAdapt);

        rootOverlay.addToNonPersistentCache(artifactContainer.getPath(), ClassLoadingButler.class, butler);
        if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
            Tr.debug(tc, "adapt adding container to map - container = " + containerToAdapt);
        }

        return butler;
    }

}
