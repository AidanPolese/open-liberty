/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2011, 2013
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.kernel.feature;

import java.util.EnumSet;

import org.osgi.framework.Version;

public interface FeatureDefinition {

    // NOTE: This is in an exported package for other bundles to use.
    // Do not expose bulk or provisioning methods like getHeaders or getHeaderElements:
    // it reduces our ability to clean up between provisioning operations.

    /**
     * Get the Symbolic Name for this feature, as defined by its header.
     * 
     * @return
     */
    public String getSymbolicName();

    /**
     * Get the Feature Name for this feature.
     * 
     * @return
     */
    public String getFeatureName();

    /**
     * Get the Version for this feature, as defined by its header.
     * 
     * @return
     */
    public Version getVersion();

    /**
     * Get the Visibility for this feature, as defined by its header.
     * 
     * @return
     */
    public Visibility getVisibility();

    /**
     * Get the process types for this feature, as defined by its header.
     * 
     * @return the process type
     */
    public EnumSet<ProcessType> getProcessTypes();

    /**
     * Get the IBM-App-ForceRestart setting for this feature, as defined by its header.
     * 
     * @return
     */
    public AppForceRestart getAppForceRestart();

    /**
     * @return true if this is the kernel feature definition
     */
    public boolean isKernel();

    /**
     * @return
     */
    public String getApiServices();

}
