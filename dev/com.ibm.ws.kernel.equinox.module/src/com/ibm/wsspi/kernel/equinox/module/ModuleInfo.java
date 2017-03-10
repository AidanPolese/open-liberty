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
package com.ibm.wsspi.kernel.equinox.module;

import org.eclipse.osgi.storage.bundlefile.BundleFile;

/**
 * A module info is used to describe information about a
 * bundle file and is used as input to a {@link ModuleBundleFileFactory} for creating bundle file objects.
 */
public interface ModuleInfo {
    /**
     * The bundle location for which this module info belongs
     * 
     * @return the bundle location
     */
    public String getLocation();

    /**
     * The base bundle file for this module info
     * 
     * @return
     */
    public BundleFile getBundleFile();

    /**
     * Returns true if this module info is for the root bundle file
     * of a bundle. Note that entries that are used for a bundle class path
     * (except for &quot;.&quot;) are considered NOT to be the bundle root.
     * For example:
     * <pre>
     * Bundle-ClassPath: ., foo.jar
     * </pre>
     * In this example, there will be two module infos associated with the
     * bundle. One for the root bundle file
     * and one for the foo.jar. The foo.jar module info will not be a bundle
     * root.
     * 
     * @return true if this module info is for the base bundle file
     *         of a bundle; false otherwise.
     */
    public boolean isBundleRoot();
}
