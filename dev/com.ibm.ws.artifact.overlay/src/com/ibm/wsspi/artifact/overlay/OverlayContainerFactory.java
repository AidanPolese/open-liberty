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
package com.ibm.wsspi.artifact.overlay;

import com.ibm.wsspi.artifact.ArtifactContainer;

/**
 * Factory for obtaining OverlayContainers.
 */
public interface OverlayContainerFactory {
    /**
     * Create an overlay, for the requested overlayType.
     * 
     * @param <T> The type that will be returned
     * @param overlayType instance of the class of the type requested.
     * @param b the container to base the overlay over.
     * @return Instance of T, or null if unable to handle request.
     */
    <T extends OverlayContainer> T createOverlay(Class<T> overlayType, ArtifactContainer b);
}
