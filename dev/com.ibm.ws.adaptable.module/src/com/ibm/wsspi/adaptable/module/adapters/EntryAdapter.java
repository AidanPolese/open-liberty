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
import com.ibm.wsspi.adaptable.module.Entry;
import com.ibm.wsspi.adaptable.module.UnableToAdaptException;
import com.ibm.wsspi.artifact.ArtifactEntry;
import com.ibm.wsspi.artifact.overlay.OverlayContainer;

/**
 * Interface for an Entry Adapter Service.
 * 
 * @param <T> The type this Service adapts Entry to.
 */
public interface EntryAdapter<T> {
    /**
     * Adapt from the Adaptable 'entryToAdapt' to type T<p>
     * Note that artifact layer paths may not be equivalent to adaptable paths.<br>
     * Use the passed 'artifactEntry' to know what the artifact layer path is for the entryToAdapt<p>
     * This allows the Adaptable Layer to have a different concept of 'isRoot' hierarchy than the artifact.
     * 
     * @param root the Container that returns isRoot=true for entryToAdapt.
     * @param rootOverlay the artifact layer entry that holds the data underpinning this adaptable.
     * @param artifactEntry the artifact entry corresponding to the entryToAdapt
     * @param entryToAdapt the adaptable entry to be adapted.
     * @return
     */
    T adapt(Container root, OverlayContainer rootOverlay, ArtifactEntry artifactEntry, Entry entryToAdapt) throws UnableToAdaptException;
}
