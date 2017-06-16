/*******************************************************************************
 * Copyright (c) 2009, 2014 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.ibm.ws.channelfw.internal.chains;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.management.DynamicMBean;
import javax.management.NotCompliantMBeanException;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceRegistration;

import com.ibm.websphere.channelfw.EndPointInfo;
import com.ibm.websphere.channelfw.EndPointMgr;
import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.channelfw.internal.ChannelFrameworkConstants;

/**
 * Temporary version of the WAS runtimefw EndPointMgr.
 */
public class EndPointMgrImpl implements EndPointMgr {
    /** Trace service */
    private static final TraceComponent tc = Tr.register(EndPointMgrImpl.class,
                                                         ChannelFrameworkConstants.BASE_TRACE_NAME,
                                                         ChannelFrameworkConstants.BASE_BUNDLE);
    /**
     * Name key to be used in the jmx.objectname
     */
    private static final String MBEAN_OBJECT_NAME_PREFIX = "WebSphere:feature=channelfw,type=endpoint,name=";

    /** Singleton reference */
    private static class EndpointManagerHolder {
        private static EndPointMgr singleton;

        static {
            BundleContext bundleContext = null;
            Bundle b = FrameworkUtil.getBundle(EndpointManagerHolder.class);
            if (b != null) {
                bundleContext = b.getBundleContext();
            }
            singleton = new EndPointMgrImpl(bundleContext);
        }
    }

    /** BundleContext for registering the MBeans */
    private final BundleContext bundleContext;

    /** Map of defined endpoints */
    private final Map<String, EndPointInfo> endpoints;

    /** Map of registered mbeans */
    private final ConcurrentMap<String, ServiceRegistration<DynamicMBean>> endpointMBeans;

    /**
     * Private constructor, use the getRef() static api for access.
     */
    public EndPointMgrImpl(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
        this.endpoints = new HashMap<String, EndPointInfo>();
        this.endpointMBeans = new ConcurrentHashMap<String, ServiceRegistration<DynamicMBean>>();
    }

    /**
     * Access the singleton instance of this class, creating if necessary.
     *
     * @return EndPointMgrImpl
     */
    public static EndPointMgr getRef() {
        return EndpointManagerHolder.singleton;
    }

    public static void setRef(EndPointMgr singleton) {
        EndpointManagerHolder.singleton = singleton;
    }

    /**
     * Construct the endpoint MBean object name.
     *
     * @param name endpoint name
     *            WebSphere:feature=channelfw,type=endpoint,name=name
     * @return the value used in jmx.objectname property
     */
    private String getMBeanObjectName(String name) {
        return MBEAN_OBJECT_NAME_PREFIX + name;
    }

    private Hashtable<String, String> createMBeanServiceProperties(String name, EndPointInfo endpoint) {
        Hashtable<String, String> properties = new Hashtable<String, String>();
        properties.put("service.vendor", "IBM");
        properties.put("jmx.objectname", getMBeanObjectName(name));
        properties.put("type", "endpoint");
        return properties;
    }

    /**
     * @param name endpoint name of the mbean to be registered
     * @param endpoint instance of an EndPointInfo containing the endpoint info
     */
    private ServiceRegistration<DynamicMBean> registerMBeanAsService(String name, EndPointInfo endpoint) {
        return bundleContext.registerService(DynamicMBean.class, endpoint, createMBeanServiceProperties(name, endpoint));

    }

    /**
     * Register an endpoint MBean and publish it.
     *
     * @param endpoint
     */
    private void registerEndpointMBean(String name, EndPointInfo ep) {
        endpointMBeans.put(name, registerMBeanAsService(name, ep));
    }

    /**
     * Unregister and register the endpoint MBean and publish it.
     *
     * @param endpoint
     */
    private void updateEndpointMBean(String name, EndPointInfo ep) {
        unregisterMBeanInService(name);
        registerEndpointMBean(name, ep);
        this.endpoints.put(name, ep);
    }

    /**
     * For each registered MBean, unpublish, unregister and remove from the map.
     */
    private void destroyEndpointMBeans() {
        for (Map.Entry<String, ServiceRegistration<DynamicMBean>> mbean : endpointMBeans.entrySet()) {
            String mbeanName = mbean.getKey();
            endpointMBeans.remove(mbeanName);

            mbean.getValue().unregister();
        }
    }

    /**
     * Destroy all of the defined endpoints.
     */
    public static void destroyEndpoints() {
        EndPointMgrImpl _this = (EndPointMgrImpl) getRef();
        synchronized (_this.endpoints) {
            _this.destroyEndpointMBeans();
            _this.endpoints.clear();
        }
    }

    /** {@inheritDoc} */
    @Override
    public EndPointInfo defineEndPoint(String name, String host, int port) {
        try {
            EndPointInfo ep = new EndPointInfo(name, host, port);
            synchronized (this.endpoints) {
                // if the endpoint with the same name already exists,
                //    - unregister the mbean from service registry
                //    - delete the mbean from Atlas
                if (this.endpoints.containsKey(name)) {
                    if (TraceComponent.isAnyTracingEnabled() && tc.isEventEnabled()) {
                        Tr.event(tc, "The new endpoint " + name + "already exists. Update the properties of the registered service");
                    }
                    updateEndpointMBean(name, ep);
                } else {
                    // register the end point and publish it to Atlas
                    registerEndpointMBean(name, ep);
                    this.endpoints.put(name, ep);
                }
            }
            return ep;
        } catch (NotCompliantMBeanException ex) {
            // This should never happen
            throw new IllegalStateException("Encountered a situation that should never occur. The EndPointInfo resulted in NotCompliantMBeanException", ex);
        }
    }

    /** {@inheritDoc} */
    @Override
    public EndPointInfo getEndPoint(String name) {
        synchronized (this.endpoints) {
            return this.endpoints.get(name);
        }
    }

    /**
     * If the MBean was registered, unpublish and unregister, and finally
     * remove it from the map of registered MBeans.
     *
     * @param name endpoint name of the mbean to be unregistered
     */
    private void unregisterMBeanInService(String name) {
        if (endpointMBeans.containsKey(name)) {
            // unregister the existing mbean
            ServiceRegistration<DynamicMBean> existingMBean = endpointMBeans.get(name);
            existingMBean.unregister();
            this.endpointMBeans.remove(name);
        }
    }

    /**
     * Delete the endpoint that matches the provided name.
     *
     * @param name
     */
    @Override
    public void removeEndPoint(String name) {
        if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
            Tr.debug(tc, "Deleting endpoint: " + name);
        }
        synchronized (this.endpoints) {
            if (this.endpoints.remove(name) != null) {
                unregisterMBeanInService(name);
            }
        }
    }

    /** {@inheritDpc} */
    @Override
    public List<EndPointInfo> getEndPoints(String address, int port) {
        List<EndPointInfo> rc = new LinkedList<EndPointInfo>();
        if (null == address) {
            return rc;
        }
        boolean isAddrWild = "*".equals(address);

        synchronized (this.endpoints) {
            for (EndPointInfo ep : this.endpoints.values()) {
                if ((isAddrWild || "*".equals(ep.getHost()) || address.equals(ep.getHost()))
                    && (0 == port || 0 == ep.getPort())
                    || port == ep.getPort()) {
                    rc.add(ep);
                }
            }
        }
        return rc;
    }

    /** {@inheritDpc} */
    @Override
    public List<EndPointInfo> getEndsPoints() {
        synchronized (this.endpoints) {
            return new ArrayList<EndPointInfo>(endpoints.values());
        }
    }

}
