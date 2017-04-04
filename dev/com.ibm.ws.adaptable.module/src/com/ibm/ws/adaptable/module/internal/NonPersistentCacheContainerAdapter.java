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
package com.ibm.ws.adaptable.module.internal;

import com.ibm.wsspi.adaptable.module.Container;
import com.ibm.wsspi.adaptable.module.NonPersistentCache;
import com.ibm.wsspi.adaptable.module.UnableToAdaptException;
import com.ibm.wsspi.adaptable.module.adapters.ContainerAdapter;
import com.ibm.wsspi.artifact.ArtifactContainer;
import com.ibm.wsspi.artifact.overlay.OverlayContainer;

/**
 *
 */
public class NonPersistentCacheContainerAdapter implements ContainerAdapter<NonPersistentCache> {

    @Override
    public NonPersistentCache adapt(Container root, OverlayContainer rootOverlay,
                                    ArtifactContainer artifactContainer, Container containerToAdapt) throws UnableToAdaptException {
        return new NonPersistentCacheImpl(rootOverlay, artifactContainer.getPath());
    }
}
