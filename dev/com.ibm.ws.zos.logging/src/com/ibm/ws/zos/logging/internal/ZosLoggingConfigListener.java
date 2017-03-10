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
package com.ibm.ws.zos.logging.internal;

import java.util.Dictionary;
import java.util.Hashtable;

import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;

/**
 * Keeps track of config updates to <zosLogging> and forwards them to
 * ZosLoggingBundleActivator.
 */
public class ZosLoggingConfigListener {

    /**
     * Config updates are relayed to this guy, who then registers/deregisters
     * z LogHandlers accordingly.
     */
    private final ZosLoggingBundleActivator zosLoggingBundleActivator;

    /**
     * Used to track and handle registration of this service
     */
    private ServiceRegistration<ZosLoggingConfigListener> serviceRegistration;

    /**
     * CTOR.
     */
    public ZosLoggingConfigListener(ZosLoggingBundleActivator zosLoggingBundleActivator) {
        this.zosLoggingBundleActivator = zosLoggingBundleActivator;
    }

    /**
     * Forward config update to ZosLoggingBundleActivator.
     */
    public void updated(Dictionary conf) {
        // We can be called with a null dictionary. Skip.
        if (conf == null)
            return;

        zosLoggingBundleActivator.configUpdated(conf);
    }

    /**
     * Register as a ManagedService
     * 
     * @return this
     */
    protected synchronized ZosLoggingConfigListener register(BundleContext bundleContext) {

        if (serviceRegistration != null) {
            return this; // Already registered.
        }

        Dictionary<String, Object> props = new Hashtable<String, Object>(1);
        props.put(Constants.SERVICE_VENDOR, "IBM");

        // register the ManagedService that will handle the config
        serviceRegistration = bundleContext.registerService(ZosLoggingConfigListener.class, this, props);
        return this;
    }

    /**
     * Unregister this ManagedService from OSGI.
     */
    protected synchronized void unregister() {
        if (serviceRegistration != null) {
            serviceRegistration.unregister();
            serviceRegistration = null;
        }
    }

}