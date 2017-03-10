/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2016
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.zos.jni.internal;

import java.util.HashSet;
import java.util.Set;

import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;

import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.kernel.zos.AngelUtils;
import com.ibm.ws.zos.core.NativeClientService;
import com.ibm.ws.zos.core.NativeService;

/**
 * This service provides Angel related utilities. Such as verifying Service registration for native associated
 * functions.
 */
public enum AngelUtilsImpl implements AngelUtils {
    INSTANCE;

    private static final TraceComponent tc = Tr.register(AngelUtilsImpl.class);

    private static final String ZOS_SERVERSERVICE_CLASS = NativeService.class.getCanonicalName();
    private static final String ZOS_CLIENTSERVICE_CLASS = NativeClientService.class.getCanonicalName();

    private ServiceRegistration<AngelUtils> serviceRef;

    private BundleContext myBundleCtx;

    public void start(BundleContext systemBundleCtx) {

        if (systemBundleCtx != null) {
            this.myBundleCtx = systemBundleCtx;

            if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
                Tr.debug(tc, "about to register with OSGi");
            }

            // Register as a Service
            this.serviceRef = this.myBundleCtx.registerService(AngelUtils.class, this, null);
        }
    }

    /**
     * Driven by NativeMethodManagerImpl when it is shutting down.
     */
    public void stop() {
        if (INSTANCE.serviceRef != null) {
            INSTANCE.serviceRef.unregister();
            INSTANCE.serviceRef = null;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.ws.kernel.zos.AngelUtils#areServicesAvailable(java.lang.Set)
     */
    @Override
    public boolean areServicesAvailable(Set<String> services) {
        boolean authorized = false;
        //Example: String filter = "(&(native.service.name=IFADEREG)(is.authorized=true))";

        if (services == null || services.isEmpty())
            return false;

        // Get list of current services.
        Set<String> authServices = this.getAvailableServices();

        // Check that each required authorized service is present.
        if (!!!authServices.isEmpty() && (authServices.containsAll(services))) {
            authorized = true;
        }

        return authorized;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.ws.kernel.zos.AngelUtils#getAvailableServices()
     */
    @Override
    public Set<String> getAvailableServices() {
        Set<String> services = new HashSet<String>();

        try {
            ServiceReference<?>[] nativeServices = this.myBundleCtx.getServiceReferences(ZOS_SERVERSERVICE_CLASS, "(is.authorized=true)");

            if (nativeServices != null) {
                for (int i = 0; i < nativeServices.length; i++) {
                    services.add((String) nativeServices[i].getProperty("native.service.name"));
                }
            } else {
                if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
                    Tr.debug(tc, "return from call to getServiceReferences, returned null)");
                }
            }
        } catch (InvalidSyntaxException e) {
            // This exception should really never happen as it pertains to the filter syntax.
        }

        return services;
    }
}
