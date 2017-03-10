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
package com.ibm.ws.logging.internal.osgi.hpel;

import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedService;

import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;

/**
 * This abstract class is a base for classes instantiated during HPEL bundle activation.
 * It registers itself as a ManagedService with the Config service in order to receive
 * updates to the different parts of HPEL configuration based on the PID.
 */
abstract class AbstractHPELConfigService implements ManagedService {
    private static final TraceComponent tc = Tr.register(AbstractHPELConfigService.class);

    /** reference to registered HPEL config service */
    private final ServiceRegistration<ManagedService> configRef;

    private final String pid;

    AbstractHPELConfigService(BundleContext context, String pid) {

        this.pid = pid;

        Hashtable<String, String> ht = new Hashtable<String, String>();
        ht.put(org.osgi.framework.Constants.SERVICE_PID, pid);

        // Register this as a "ManagedService" to get calls when the config is
        // updated after we've taken care of merging config manually via
        // getConfiguration
        configRef = context.registerService(ManagedService.class, this, ht);
    }

    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    @Override
    public void updated(Dictionary<String, ?> properties) throws ConfigurationException {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEventEnabled())
            Tr.event(tc, "HPEL properties updated for pid " + pid + ", properties=" + properties);

        if (properties == null)
            return;

        Map<String, Object> newMap = null;
        if (properties instanceof Map<?, ?>) {
            newMap = (Map<String, Object>) properties;
        } else {
            newMap = new HashMap<String, Object>();
            Enumeration<String> keys = properties.keys();
            while (keys.hasMoreElements()) {
                String key = keys.nextElement();
                newMap.put(key, properties.get(key));
            }
        }

        forwardUpdated(newMap);
    }

    /**
     * Stop this service and free any allocated resources when the owning bundle
     * is being stopped.
     */
    void stop() {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEventEnabled()) {
            Tr.event(tc, "Stopping the HPEL managed service");
        }
        // disconnect from the config admin
        this.configRef.unregister();
    }

    abstract void forwardUpdated(Map<String, Object> map);
}
