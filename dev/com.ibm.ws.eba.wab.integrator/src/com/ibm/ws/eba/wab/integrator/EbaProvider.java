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
package com.ibm.ws.eba.wab.integrator;

import org.osgi.framework.Bundle;

import com.ibm.ws.container.service.app.deploy.ApplicationInfo;

/**
 * This interface will provide application info for a bundle.
 */
public interface EbaProvider {

    /**
     * Returns the application info for the application that contains the supplied bundle.
     * 
     * @param bundle The bundle to find the application info for
     * @return The application info for the bundle or <code>null</code> if there isn't an application for this bundle
     */
    public ApplicationInfo getApplicationInfo(Bundle bundle);

}
