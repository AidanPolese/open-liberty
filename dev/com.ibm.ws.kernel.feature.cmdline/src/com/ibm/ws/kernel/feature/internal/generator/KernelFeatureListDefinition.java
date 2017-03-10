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
package com.ibm.ws.kernel.feature.internal.generator;

import java.io.File;
import java.io.IOException;

import com.ibm.ws.kernel.feature.internal.subsystem.SubsystemFeatureDefinitionImpl;
import com.ibm.ws.kernel.provisioning.ExtensionConstants;

/**
 * This is very different from the Kernel definition list used at runtime.
 * This aggregates the SubsystemFeatureDefintionImpls (one per file),
 * so they can be printed as one section...
 */
public class KernelFeatureListDefinition extends SubsystemFeatureDefinitionImpl {

    /**
     * @param f
     * @throws IOException
     */
    public KernelFeatureListDefinition(File f) throws IOException {
        super(ExtensionConstants.CORE_EXTENSION, f);
    }

    @Override
    public boolean isKernel() {
        return true;
    }
}
