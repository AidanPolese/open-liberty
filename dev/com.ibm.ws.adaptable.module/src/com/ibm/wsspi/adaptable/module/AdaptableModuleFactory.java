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
package com.ibm.wsspi.adaptable.module;

import java.io.File;

import com.ibm.wsspi.artifact.ArtifactContainer;

/**
 * Factory to create Adaptable Modules.
 */
public interface AdaptableModuleFactory {

    /**
     * Obtain an adaptable container for a given artifact api container.
     * 
     * @param overlayDirectory directory holding overlay content.
     * @param cacheDirForOverlayContent directory overlay can use as a cache location.
     * @param container the underlying artifact container for this module.
     * @return Adaptable Container.
     */
    Container getContainer(File overlayDirectory, File cacheDirForOverlayContent, ArtifactContainer container);
}
