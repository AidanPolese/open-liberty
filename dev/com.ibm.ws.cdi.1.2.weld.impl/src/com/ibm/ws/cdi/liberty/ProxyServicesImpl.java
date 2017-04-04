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
package com.ibm.ws.cdi.liberty;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.eclipse.osgi.container.ModuleLoader;
import org.eclipse.osgi.container.ModuleWiring;
import org.eclipse.osgi.util.ManifestElement;
import org.jboss.weld.serialization.spi.ProxyServices;
import org.osgi.framework.Bundle;
import org.osgi.framework.wiring.BundleWiring;

import com.ibm.ws.cdi.impl.weld.AbstractProxyServices;

/**
 * This service is used to load proxy classes. We need a special classloader so that
 * we can load both weld classes and app classes.
 *
 *
 */
public class ProxyServicesImpl extends AbstractProxyServices implements ProxyServices {

    @Override
    protected void addWeldDynamicImports(Bundle b, ManifestElement[] dynamicImports) {
        // There is no OSGi API for adding a dynamic import to a class loader except
        // by using a weaving hook, but that is too late for our usecase here.
        // Resorting to using Equinox internals to add imports just before weld does
        // a defineClass for the proxy class using the bundle's class loader.
        BundleWiring bWiring = b.adapt(BundleWiring.class);
        // Just doing a blind cast here because a cast exception will quickly tell us that something
        // changed in Equinox that broke us.
        ModuleWiring mWiring = (ModuleWiring) bWiring;
        ModuleLoader loader = mWiring.getModuleLoader();
        try {
            // there is not even Equinox API to do this.  Resorting to reflecting the internal
            // method of Equinox (org.eclipse.osgi.internal.loader.BundleLoader.addDynamicImportPackage(ManifestElement[]))
            // Note that BundleLoader is the implementation of ModuleLoader here.
            Method method = loader.getClass().getDeclaredMethod("addDynamicImportPackage", ManifestElement[].class);
            method.invoke(loader, new Object[] { dynamicImports });
            // Any exceptions here are bad news, just propagating them up.  Auto-FFDC is fine here
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e.getTargetException());
        }
    }
}
