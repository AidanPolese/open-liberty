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
package com.ibm.ws.eba.wab.integrator;

import org.osgi.framework.ServiceReference;

import com.ibm.ws.container.service.app.deploy.ApplicationInfo;

/**
 * This is just a marker service used to inform others that the subsystem
 * has been installed as an application and is known by the EbaProvider.
 */
public interface OSGiAppInfo {
    /**
     * A service property that indicates the subsystem service EBA info is for.
     * The value of this property is of type {@link ServiceReference}
     */
    public static final String SERVICE_PROP_FOR_SUBSYSTEM = "com.ibm.ws.http.whiteboard.context.for.subsystem";

    /**
     * The application info for this EBA
     * 
     * @return application info
     */
    public ApplicationInfo getApplicationInfo();
}
