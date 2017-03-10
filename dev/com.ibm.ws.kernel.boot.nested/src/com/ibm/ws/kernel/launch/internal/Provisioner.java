/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2010
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
package com.ibm.ws.kernel.launch.internal;

import org.osgi.framework.BundleContext;

import com.ibm.ws.kernel.boot.BootstrapConfig;

/**
 * Internal interface to allow for testing via mock
 */
public interface Provisioner {
    void initialProvisioning(BundleContext systemBundleCtx, BootstrapConfig config) throws InvalidBundleContextException;

    final static class InvalidBundleContextException extends Exception {
        private static final long serialVersionUID = 1L;
        // no-op marker class
    }
}
