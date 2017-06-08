/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2013, 2017
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.classloading.internal;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;
import org.osgi.framework.ServiceReference;

import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.websphere.ras.annotation.Trivial;
import com.ibm.ws.classloading.internal.util.Keyed;
import com.ibm.ws.ffdc.annotation.FFDCIgnore;

/**
 * A specific type of UnifiedClassLoader: ThreadContextClassLoader
 */
public class ThreadContextClassLoader extends UnifiedClassLoader implements Keyed<String> {
    static final TraceComponent tc = Tr.register(ThreadContextClassLoader.class);
    private final AtomicReference<Bundle> bundle = new AtomicReference<Bundle>();
    protected final String key;
    private final AtomicInteger refCount = new AtomicInteger(0);
    private final ClassLoader appLoader;
    private final ClassLoadingServiceImpl clSvc;

    public ThreadContextClassLoader(GatewayClassLoader augLoader, ClassLoader appLoader, String key, ClassLoadingServiceImpl clSvc) {
        super(appLoader instanceof ParentLastClassLoader ? appLoader : augLoader,
              appLoader instanceof ParentLastClassLoader ? augLoader : appLoader);
        bundle.set(augLoader.getBundle());
        this.key = key;
        this.appLoader = appLoader;
        this.clSvc = clSvc;
    }

    /**
     * Cleans up the TCCL instance. Once called, this TCCL is effectively disabled.
     * It's associated gateway bundle will have been removed.
     */
    private void cleanup() {
        final String methodName = "cleanup(): ";
        try {
            Bundle b = bundle.getAndSet(null);
            if (b != null) {
                if (tc.isDebugEnabled())
                    Tr.debug(tc, methodName + "Uninstalling bundle location: " + b.getLocation() + ", bundle id: " + b.getBundleId());
                b.uninstall();
            }
        } catch (BundleException ignored) {
        } catch (IllegalStateException ignored) {

        }
    }

    /**
     * The ClassLoadingService implementation should call this method when it's
     * destroyThreadContextClassLoader method is called. Each call to destroyTCCL
     * should decrement this ref counter. When there are no more references to this
     * TCCL, it will be cleaned up, which effectively invalidates it.
     * 
     * Users of the ClassLoadingService should understand that:<ol>
     * <li>They are responsible for destroying every instance of a TCCL that they
     * create via the CLS.createTCCL method, and,
     * <li>If they still have references to the TCCL instance after destroying all
     * instances they created, they will be holding on to a stale (leaked) classloader,
     * which can result in OOM situations.</li>
     * </ol>
     * 
     * @return the new current count of references to this TCCL instance
     */
    int decrementRefCount() {
        final String methodName = "decrementRefCount(): ";
        final int count = refCount.decrementAndGet();
        if (count < 0) {
            // more destroys than creates
            if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
                Tr.debug(tc, methodName + " refCount < 0 - too many calls to destroy/cleaup", new Throwable("stack trace"));
            }
        }
        if (count == 0) {
            cleanup();
        }
        return count;
    }

    /**
     * The ClassLoadingService implementation should call this method when it's
     * createThreadContextClassLoader method is called, both for new and pre-existing
     * instances. Each call to createTCCL should increment this ref counter - likewise,
     * each call to destroy should call decrementRefCount();
     * 
     * @return the new current count of references to this TCCL instance
     */
    int incrementRefCount() {
        return refCount.incrementAndGet();
    }

    @Override
    @FFDCIgnore(ClassNotFoundException.class)
    @Trivial
    protected Class<?> findClass(String className) throws ClassNotFoundException {
        try {
            return super.findClass(className);
        } catch (ClassNotFoundException x) {
            // Special case to find and load META-INF/services provider classes made available by bells
            ConcurrentHashMap<ServiceReference<?>, Class<?>> bellServices = clSvc.bellMetaInfServices.get(className);
            if (bellServices != null) {
                Class<?> c = getMetaInfServiceClassFromBell(bellServices);
                if (c != null)
                    return c;
            }
            throw x;
        }
    }

    /*********************************************************************************/
    /** Override classloading related methods so this class shows up in stacktraces **/
    /*********************************************************************************/
    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        return super.loadClass(name, resolve);
    }

    @Override
    protected URL findResource(String name) {
        URL url = super.findResource(name);
        if (url == null) {
            ConcurrentLinkedQueue<String> providerNames = clSvc.bellMetaInfServiceProviders.get(name);
            if (providerNames != null)
                for (String providerImplClassName : providerNames) {
                    if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
                        Tr.debug(this, tc, providerImplClassName);
                    ConcurrentHashMap<ServiceReference<?>, Class<?>> bellServices = clSvc.bellMetaInfServices.get(providerImplClassName);
                    if (bellServices != null) {
                        Class<?> c = getMetaInfServiceClassFromBell(bellServices);
                        url = c.getClassLoader().getResource(name);
                        if (url != null)
                            break;
                    }
                }
        }
        return url;
    }

    @Override
    protected Enumeration<URL> findResources(String name) throws IOException {
        Enumeration<URL> urlEnum = super.findResources(name);
        ConcurrentLinkedQueue<String> providerNames = clSvc.bellMetaInfServiceProviders.get(name);
        if (providerNames != null && !providerNames.isEmpty()) {
            Set<URL> urls = new LinkedHashSet<URL>();
            while (urlEnum.hasMoreElements())
                urls.add(urlEnum.nextElement());
            for (String providerImplClassName : providerNames) {
                if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
                    Tr.debug(this, tc, providerImplClassName);
                ConcurrentHashMap<ServiceReference<?>, Class<?>> bellServices = clSvc.bellMetaInfServices.get(providerImplClassName);
                for (Entry<ServiceReference<?>, Class<?>> entry : bellServices.entrySet()) {
                    Class<?> c = entry.getValue();
                    if (Void.class.equals(c)) {
                        // lazily initialize
                        ServiceReference<?> ref = entry.getKey();
                        Object metaInfSvc = clSvc.bundleContext.getService(ref);
                        if (metaInfSvc != null)
                            entry.setValue(c = metaInfSvc.getClass());
                    }
                    if (!Void.class.equals(c)) {
                        URL url = c.getClassLoader().getResource(name);
                        if (url != null)
                            urls.add(url);
                    }
                }
            }
            urlEnum = Collections.enumeration(urls);
        }
        return urlEnum;
    }

    @Override
    public String getKey() {
        return key;
    }

    /**
     * Lazily initialize and return the service provider implementation class from a META-INF/services entry for a bell.
     * 
     * @param bellServices mapping of service reference to class
     * @return the class. Null if not found.
     */
    private Class<?> getMetaInfServiceClassFromBell(ConcurrentHashMap<ServiceReference<?>, Class<?>> bellServices) {
        for (Entry<ServiceReference<?>, Class<?>> entry : bellServices.entrySet()) {
            Class<?> c = entry.getValue();
            if (Void.class.equals(c)) {
                // lazily initialize
                ServiceReference<?> ref = entry.getKey();
                Object metaInfSvc = clSvc.bundleContext.getService(ref);
                if (metaInfSvc != null) {
                    entry.setValue(c = metaInfSvc.getClass());
                    return c;
                }
            } else
                return c;
        }
        return null;
    }

    final boolean isFor(ClassLoader classLoader) {
        return classLoader == appLoader;
    }

}