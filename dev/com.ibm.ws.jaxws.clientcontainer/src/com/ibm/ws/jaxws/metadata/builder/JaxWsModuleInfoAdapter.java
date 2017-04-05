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
package com.ibm.ws.jaxws.metadata.builder;

import com.ibm.ws.jaxws.metadata.JaxWsModuleInfo;
import com.ibm.wsspi.adaptable.module.Container;
import com.ibm.wsspi.adaptable.module.NonPersistentCache;
import com.ibm.wsspi.adaptable.module.UnableToAdaptException;
import com.ibm.wsspi.adaptable.module.adapters.ContainerAdapter;
import com.ibm.wsspi.artifact.ArtifactContainer;
import com.ibm.wsspi.artifact.overlay.OverlayContainer;

public class JaxWsModuleInfoAdapter implements ContainerAdapter<JaxWsModuleInfo> {

    /** {@inheritDoc} */
    @Override
    public JaxWsModuleInfo adapt(Container root, OverlayContainer rootOverlay, ArtifactContainer artifactContainer, Container containerToAdapt) throws UnableToAdaptException {
        NonPersistentCache overlayCache = containerToAdapt.adapt(NonPersistentCache.class);

        JaxWsModuleInfo jaxWsModuleInfo = (JaxWsModuleInfo) overlayCache.getFromCache(JaxWsModuleInfo.class);

        return jaxWsModuleInfo;
    }
}
