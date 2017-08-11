/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2013
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.aries.buildtasks.utils;

import org.apache.aries.util.VersionRange;
import org.apache.aries.util.manifest.BundleManifest;

/**
 *
 */
public class RequiredBundle {
    private final String bundleName;
    private final VersionRange versionRange;

    /**
     * This object represents a bundle required for another plugin, this is bundle symbolic name and version
     * 
     * @param namespace The symbolic name, like "org.eclpse.ui"
     * @param version The verion of the required bundle, e.g. "2.3.0"
     */
    public RequiredBundle(String namespace, String version) {
        version = (version == null || version.length() < 1) ? "0.0.0" : version;
        versionRange = VersionRange.parseVersionRange(version);
        bundleName = namespace;
    }

    @Override
    public String toString() {
        return bundleName + "_" + versionRange;
    }

    public String getBundleName() {
        return bundleName;
    }

    public VersionRange getVersionRange() {
        return versionRange;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj != null && obj instanceof BundleManifest) {
            BundleManifest bundleManifest = (BundleManifest) obj;
            if (bundleManifest.getSymbolicName().equals(bundleName)) {
                return versionRange.matches(bundleManifest.getVersion());
            }
        }
        return false;
    }
}
