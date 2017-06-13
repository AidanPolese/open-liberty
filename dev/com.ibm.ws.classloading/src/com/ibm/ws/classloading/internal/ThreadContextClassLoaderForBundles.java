/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2013,2017
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.classloading.internal;

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleReference;

/**
 * Created for defect 83996. A wrapped UnifiedClassLoader for
 * OSGi application classloaders that implement BundleReference
 */
public class ThreadContextClassLoaderForBundles extends ThreadContextClassLoader implements BundleReference
{

    public ThreadContextClassLoaderForBundles(GatewayClassLoader augLoader, ClassLoader appLoader, String key, ClassLoadingServiceImpl clSvc) {
        super(augLoader, appLoader, key, clSvc);
        _bundleClassLoader = (BundleReference) appLoader;
    }

    private final BundleReference _bundleClassLoader;

    @Override
    public Bundle getBundle() {
        return _bundleClassLoader.getBundle();
    }

    /*********************************************************************************/
    /** Override classloading related methods so this class shows up in stacktraces **/
    /*********************************************************************************/
    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        return super.loadClass(name, resolve);
    }

    @Override
    protected URL findResource(String arg0) {
        return super.findResource(arg0);
    }

    @Override
    protected Enumeration<URL> findResources(String arg0) throws IOException {
        return super.findResources(arg0);
    }
}
