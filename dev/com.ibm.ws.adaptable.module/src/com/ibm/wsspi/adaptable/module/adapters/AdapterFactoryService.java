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
package com.ibm.wsspi.adaptable.module.adapters;

import com.ibm.wsspi.adaptable.module.Container;
import com.ibm.wsspi.adaptable.module.Entry;
import com.ibm.wsspi.adaptable.module.UnableToAdaptException;
import com.ibm.wsspi.artifact.ArtifactContainer;
import com.ibm.wsspi.artifact.ArtifactEntry;
import com.ibm.wsspi.artifact.overlay.OverlayContainer;

/**
 *
 */
public interface AdapterFactoryService {
    public <T> T adapt(final Container root, final OverlayContainer rootOverlay, final ArtifactContainer artifactContainer, final Container containerToAdapt, final Class<T> t) throws UnableToAdaptException;

    public <T> T adapt(Container root, OverlayContainer rootOverlay, ArtifactEntry artifactEntry, Entry entryToAdapt, Class<T> t) throws UnableToAdaptException;
}
