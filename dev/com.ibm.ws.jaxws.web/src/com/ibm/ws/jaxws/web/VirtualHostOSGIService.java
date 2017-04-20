/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2015
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.jaxws.web;

import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;

import com.ibm.ws.webcontainer.osgi.DynamicVirtualHostManager;

/**
 *
 */
public class VirtualHostOSGIService {
    private VirtualHostOSGIService() {

    }

    private static VirtualHostOSGIService instance = null;

    public static VirtualHostOSGIService getInstance() {
        if (instance == null) {
            instance = new VirtualHostOSGIService();
        }
        return instance;
    }

    private DynamicVirtualHostManager _vhostManager;

    public DynamicVirtualHostManager getDynamicVirtualHostManagerService() {
        if (_vhostManager == null) {
            BundleContext context = FrameworkUtil.getBundle(DynamicVirtualHostManager.class)
                            .getBundleContext();
            ServiceReference<DynamicVirtualHostManager> serviceRef = context
                            .getServiceReference(DynamicVirtualHostManager.class);
            _vhostManager = context.getService(serviceRef);
        }
        return _vhostManager;

    }
}
