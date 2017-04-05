/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2011
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.webcontainer.osgi;

import java.util.HashSet;
import java.util.Iterator;

import com.ibm.ejs.ras.Traceable;
import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.webcontainer.VirtualHostConfiguration;
import com.ibm.ws.webcontainer.osgi.osgi.WebContainerConstants;
import com.ibm.wsspi.http.HttpContainer;
import com.ibm.wsspi.http.VirtualHost;

/**
 *
 */
public class DynamicVirtualHostConfiguration extends VirtualHostConfiguration implements Traceable {
    private static final TraceComponent tc = Tr.register(DynamicVirtualHostConfiguration.class, WebContainerConstants.TR_GROUP, WebContainerConstants.NLS_PROPS);

    final String name;
    final HashSet<String> activeContexts = new HashSet<String>();

    /**
     * Construct the VirtualHostConfiguration object: use this when the
     * associated VirtualHost is unknown or unavailable at construction time.
     */
    public DynamicVirtualHostConfiguration(String name) {
        super(name);
        this.name = name;
        if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
            Tr.debug(tc, "DynamicVirtualHostConfiguration", "name ->"+ this.name);
        }
    }

    /**
     * Construct the VirtualHostConfiguration object with the underlying VirtualHost
     * 
     * @param config VirtualHost object representing the new/updated VirtualHost
     *            configuration
     */
    public DynamicVirtualHostConfiguration(VirtualHost config) {
        super(config);        
        this.name = config.getName();
        if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
            Tr.debug(tc, "DynamicVirtualHostConfiguration", "config name ->"+ this.name);
        }
    }

    /**
     * @param contextRoot
     */
    synchronized void addContextRoot(String contextRoot, HttpContainer container) {
        if (contextRoot != null) {
            if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
                Tr.debug(tc, "addContextRoot", "contextRoot-> ["+ contextRoot+"]");
            }
            activeContexts.add(contextRoot);
        }
        com.ibm.wsspi.http.VirtualHost local = this.config;
        if (local != null) {
            local.addContextRoot(contextRoot, container);
            if (TraceComponent.isAnyTracingEnabled() && tc.isEventEnabled()) {
                Tr.event(this, tc, "Context root added " + contextRoot);
            }
        }
        else{
            if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
                Tr.debug(tc, "addContextRoot", "local VH config is null");
            }
        }
    }

    /**
     * @param contextRoot
     */
    synchronized void removeContextRoot(String contextRoot, HttpContainer container) {
        if (contextRoot != null) {
            if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
                Tr.debug(tc, "removeContextRoot", "contextRoot-> ["+ contextRoot+"]");
            }
            activeContexts.remove(contextRoot);
        }
        com.ibm.wsspi.http.VirtualHost local = this.config;
        if (local != null) {
            local.removeContextRoot(contextRoot, container);
            if (TraceComponent.isAnyTracingEnabled() && tc.isEventEnabled()) {
                Tr.event(this, tc, "Context root removed " + contextRoot);
            }
        }
        else{
            if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
                Tr.debug(tc, "removeContextRoot", "local VH config is null");
            }
        }
    }

    public synchronized Iterator<String> getActiveContext() {
        HashSet<String> copy = new HashSet<String>(activeContexts);
        return copy.iterator();
    }

    @Override
    public String toString() {
        return name;
    }

    /** {@inheritDoc} */
    @Override
    public String toTraceString() {
        return getClass().getSimpleName()
               + "[name=" + name
               + "," + config
               + "]";
    }
}
