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
package com.ibm.wsspi.adaptable.module.adapters;

import com.ibm.wsspi.adaptable.module.Container;
import com.ibm.wsspi.adaptable.module.UnableToAdaptException;
import com.ibm.wsspi.artifact.ArtifactContainer;
import com.ibm.wsspi.artifact.overlay.OverlayContainer;

/**
 * Interface for a Container Adapter Service.
 * 
 * @param <T> The type this Service adapts Containers to.
 */
public interface ContainerAdapter<T> {
    /**
     * Adapt from the Adaptable 'containerToAdapt' to type T<p>
     * Note that artifact layer paths may not be equivalent to adaptable paths.<br>
     * Use the passed 'artifactContainer' to know what the artifact layer path is for the containerToAdapt<p>
     * This allows the Adaptable Layer to have a different concept of 'isRoot' hierarchy than the artifact.
     * 
     * @param root the container that returns isRoot=true for containerToAdapt (will be containerToAdapt if containerToAdapt.isRoot=true)
     * @param rootOverlay the artifact layer container that holds the data underpinning this adaptable.
     * @param artifactContainer the artifact container corresponding to the containerToAdapt
     * @param containerToAdapt the adaptable container to be adapted.
     * @return
     */
    T adapt(Container root, OverlayContainer rootOverlay, ArtifactContainer artifactContainer, Container containerToAdapt) throws UnableToAdaptException;
}
