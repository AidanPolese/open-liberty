/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2014
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.jaxrs20.metadata;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.cxf.jaxrs.lifecycle.ResourceProvider;

/**
 *
 */
public class CXFJaxRsProviderResourceHolder {

    private final List<Class<?>> resourceClasses = new ArrayList<Class<?>>();
    private final List<Object> providers = new ArrayList<Object>();
    private final Map<Class<?>, ResourceProvider> resouceProviderMap = new HashMap<Class<?>, ResourceProvider>();
    private final Map<Class<?>, Class<?>> abstractResourceMap = new HashMap<Class<?>, Class<?>>();

    public void addAbstractResourceMapItem(Class<?> abstractInterfaceClass, Class<?> concreteClass) {
        synchronized (abstractResourceMap) {
            abstractResourceMap.put(abstractInterfaceClass, concreteClass);
        }
    }

    /**
     * Note: when iterating over this map, you must synchronize on it
     * to ensure thread safety.
     * 
     * @return a map of implementation classes indexed by abstract resource classes (primarily ejb biz interfaces)
     */
    public Map<Class<?>, Class<?>> getAbstractResourceMap() {
        return abstractResourceMap;
    }

    /**
     * Note: when iterating over this list, you must synchronize on it
     * to ensure thread safety.
     * 
     * @return the list of JAX-RS resource class
     */
    public List<Class<?>> getResourceClasses() {
        return resourceClasses;
    }

    public void addResourceClasses(Class<?> resourceClass) {
        synchronized (resourceClasses) {
            resourceClasses.add(resourceClass);
        }
    }

    public void removeResourceClasses(Class<?> resourceClass) {
        synchronized (resourceClasses) {
            resourceClasses.remove(resourceClass);
        }
    }

    /**
     * Note: when iterating over this list, you must synchronize on it
     * to ensure thread safety.
     * 
     * @return the list of JAX-RS providers
     */
    public List<Object> getProviders() {
        return providers;
    }

    public void addProvider(Object provider) {
        synchronized (providers) {
            providers.add(provider);
        }
    }

    /**
     * Note: when iterating over this map, you must synchronize on it
     * to ensure thread safety.
     * 
     * @return a map of resource providers indexed by the the provider class
     */
    public Map<Class<?>, ResourceProvider> getResouceProviderMap() {
        return resouceProviderMap;
    }

    public boolean addResouceProvider(Class<?> c, ResourceProvider rp) {
        synchronized (resouceProviderMap) {
            if (resouceProviderMap.containsKey(c)) {
                return true;
            }
            resouceProviderMap.put(c, rp);
            return false;
        }
    }

    public boolean removeResouceProvider(Class<?> c) {
        synchronized (resouceProviderMap) {
            if (!this.resouceProviderMap.containsKey(c)) {
                return false;
            }
            this.resouceProviderMap.remove(c);
            return true;
        }
    }
}