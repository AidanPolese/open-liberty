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

import org.eclipse.osgi.storage.bundlefile.BundleFileWrapper;

/**
 * A factory for creating bundle files based on {@link ModuleInfo module info}.
 * This service will get called each time a bundle file is requested by the
 * framework. This happens when bundles are installed or when attempting to
 * load them from a cached state. It also happens when inner jars of a bundle
 * are used as part of the bundle class path.
 */
public interface ModuleBundleFileFactory {
    /**
     * Returns a bundle file for the given {@link ModuleInfo} or {@code null} if none is available.
     * 
     * @param moduleInfo the module info.
     * @return a bundle file for the module info or {@code null}.
     */
    public BundleFileWrapper createBundleFile(ModuleInfo moduleInfo);
}
