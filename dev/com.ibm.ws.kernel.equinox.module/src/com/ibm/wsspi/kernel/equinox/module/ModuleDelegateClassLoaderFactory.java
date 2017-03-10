/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2015
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.wsspi.kernel.equinox.module;

import org.osgi.framework.Bundle;

/**
 * A service to provide class loaders which will be used
 * as a delegate class loader for a bundle class loader.
 * One one instance of this service is used at runtime.
 */
public interface ModuleDelegateClassLoaderFactory {
    /**
     * Return a delegate class loader for the specified bundle or {@code null} if no delegate should be used for the bundle.
     * 
     * @param bundle the bundle
     * @return the delegate class loader
     */
    ClassLoader getDelegateClassLoader(Bundle bundle);
}
