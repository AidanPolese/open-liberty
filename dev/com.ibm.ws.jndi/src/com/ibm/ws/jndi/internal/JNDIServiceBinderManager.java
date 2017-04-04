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
package com.ibm.ws.jndi.internal;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.concurrent.atomic.AtomicBoolean;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;

/**
 *
 */
public class JNDIServiceBinderManager implements BundleActivator {

    /** {@inheritDoc} */
    @Override
    public void start(BundleContext bundleContext) throws Exception {
        //We don't initialize the JNDIServiceBinder here because we want to be as lazy as possible.
        //It will be initialized on first use of the JNDIServiceBinderHolder
    }

    /** {@inheritDoc} */
    @Override
    public void stop(BundleContext bundleContext) throws Exception {
        //clean up the JNDIServiceBinder when the bundle is shutdown
        if (serviceBinderCreated.get()) {
            JNDIServiceBinderHolder.HELPER.deactivate(bundleContext);
        }
    }

    private static final AtomicBoolean serviceBinderCreated = new AtomicBoolean();

    static final class JNDIServiceBinderHolder {
        static final JNDIServiceBinder HELPER = createAndInitializeServiceBinder();
        static {
            serviceBinderCreated.set(true);
        }

        private static JNDIServiceBinder createAndInitializeServiceBinder() {
            final JNDIServiceBinder helper = new JNDIServiceBinder();
            final Bundle b = FrameworkUtil.getBundle(JNDIServiceBinderManager.class);
            if (System.getSecurityManager() == null)
                helper.activate(b.getBundleContext());
            else
                AccessController.doPrivileged(new PrivilegedAction<Void>() {
                    @Override
                    public Void run() {
                        helper.activate(b.getBundleContext());
                        return null;
                    }
                });
            return helper;
        }

    }

}
