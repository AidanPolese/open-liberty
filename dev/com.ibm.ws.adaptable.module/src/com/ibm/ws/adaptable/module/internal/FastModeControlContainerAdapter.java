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
import com.ibm.wsspi.adaptable.module.FastModeControl;
import com.ibm.wsspi.adaptable.module.UnableToAdaptException;
import com.ibm.wsspi.adaptable.module.adapters.ContainerAdapter;
import com.ibm.wsspi.artifact.ArtifactContainer;
import com.ibm.wsspi.artifact.overlay.OverlayContainer;

/**
 *
 */
public class FastModeControlContainerAdapter implements ContainerAdapter<FastModeControl> {

    @Override
    public FastModeControl adapt(Container root, OverlayContainer rootOverlay,
                                 ArtifactContainer artifactContainer, Container containerToAdapt) throws UnableToAdaptException {
        return new FastModeControlImpl(rootOverlay);
    }

    private static class FastModeControlImpl implements FastModeControl {

        private final OverlayContainer rootOverlay;

        public FastModeControlImpl(OverlayContainer rootOverlay) {
            this.rootOverlay = rootOverlay;
        }

        @Override
        public void useFastMode() {
            rootOverlay.useFastMode();
        }

        @Override
        public void stopUsingFastMode() {
            rootOverlay.stopUsingFastMode();
        }
    }
}
