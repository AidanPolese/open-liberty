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
import com.ibm.wsspi.adaptable.module.Entry;
import com.ibm.wsspi.adaptable.module.MaskedPathEntry;
import com.ibm.wsspi.adaptable.module.UnableToAdaptException;
import com.ibm.wsspi.adaptable.module.adapters.EntryAdapter;
import com.ibm.wsspi.artifact.ArtifactEntry;
import com.ibm.wsspi.artifact.overlay.OverlayContainer;

/**
 *
 */
public class MaskedPathEntryAdapter implements EntryAdapter<MaskedPathEntry> {

    @Override
    public MaskedPathEntry adapt(Container root, OverlayContainer rootOverlay,
                                 ArtifactEntry artifactEntry, Entry entryToAdapt) throws UnableToAdaptException {
        return new MaskedPathEntryImpl(rootOverlay, artifactEntry.getPath());
    }

    private static final class MaskedPathEntryImpl implements MaskedPathEntry {
        private final OverlayContainer rootOverlay;
        private final String path;

        public MaskedPathEntryImpl(OverlayContainer rootOverlay, String path) {
            this.rootOverlay = rootOverlay;
            this.path = path;
        }

        @Override
        public void mask() {
            rootOverlay.mask(path);
        }

        @Override
        public void unMask() {
            rootOverlay.unMask(path);
        }

        @Override
        public boolean isMasked() {
            return rootOverlay.isMasked(path);
        }
    }
}
