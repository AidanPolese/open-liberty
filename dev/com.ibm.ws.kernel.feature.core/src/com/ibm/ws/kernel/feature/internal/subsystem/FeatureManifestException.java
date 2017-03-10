/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2013
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.kernel.feature.internal.subsystem;

import java.io.IOException;

/**
 * Use common messages to denote problems with feature manifests.
 */
public class FeatureManifestException extends IOException {
    /**  */
    private static final long serialVersionUID = -4933600526165761348L;

    private final String translated = null;

    /**
     * Use this when there
     */
    public FeatureManifestException(String message, String translatedMessage) {
        super(message);
    }

    @Override
    public String getLocalizedMessage() {
        if (translated == null)
            return getMessage();

        return translated;
    }
}
