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
package com.ibm.wsspi.webcontainer.metadata;

import com.ibm.wsspi.adaptable.module.Container;
import com.ibm.wsspi.adaptable.module.UnableToAdaptException;
import com.ibm.wsspi.adaptable.module.adapters.ContainerAdapter;
import com.ibm.wsspi.artifact.ArtifactContainer;
import com.ibm.wsspi.artifact.overlay.OverlayContainer;

/**
 *
 */
public class WebModuleMetaDataAdapter implements ContainerAdapter<WebModuleMetaData> {

    @Override
    public WebModuleMetaData adapt(Container root, OverlayContainer rootOverlay, ArtifactContainer artifactContainer, Container containerToAdapt) throws UnableToAdaptException {
        WebModuleMetaData wmmd = (WebModuleMetaData) rootOverlay.getFromNonPersistentCache(artifactContainer.getPath(), WebModuleMetaData.class);
        return wmmd;
    }
}
