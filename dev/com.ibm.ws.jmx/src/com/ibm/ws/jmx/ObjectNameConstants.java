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
package com.ibm.ws.jmx;

import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

/**
 *
 */
public final class ObjectNameConstants {

    public static final ObjectName OSGI_FRAMEWORK_MBEAN_NAME;
    public static final ObjectName OSGI_BUNDLE_STATE_MBEAN_NAME;
    public static final ObjectName OSGI_SERVICE_STATE_MBEAN_NAME;
    public static final ObjectName OSGI_PACKAGE_STATE_MBEAN_NAME;
    public static final ObjectName OSGI_PERMISSION_ADMIN_MBEAN_NAME;
    public static final ObjectName OSGI_CONFIGURATION_ADMIN_MBEAN_NAME;
    public static final ObjectName OSGI_PROVISIONING_SERVICE_MBEAN_NAME;
    public static final ObjectName OSGI_USER_ADMIN_MBEAN_NAME;

    static {
        try {
            OSGI_FRAMEWORK_MBEAN_NAME = new ObjectName("osgi.core:type=framework,version=1.7,*");
            OSGI_BUNDLE_STATE_MBEAN_NAME = new ObjectName("osgi.core:type=bundleState,version=1.7,*");
            OSGI_SERVICE_STATE_MBEAN_NAME = new ObjectName("osgi.core:type=serviceState,version=1.5");
            OSGI_PACKAGE_STATE_MBEAN_NAME = new ObjectName("osgi.core:type=packageState,version=1.5");
            OSGI_PERMISSION_ADMIN_MBEAN_NAME = new ObjectName("osgi.core:service=permissionadmin,version=1.2");
            OSGI_CONFIGURATION_ADMIN_MBEAN_NAME = new ObjectName("osgi.compendium:service=cm,version=1.3,*");
            OSGI_PROVISIONING_SERVICE_MBEAN_NAME = new ObjectName("osgi.compendium:service=provisioning,version=1.2");
            OSGI_USER_ADMIN_MBEAN_NAME = new ObjectName("osgi.compendium:service=useradmin,version=1.1");

        } catch (MalformedObjectNameException e) {
            throw new Error("Could not initialize ObjectName constants.", e);
        }
    }

    private ObjectNameConstants() {}

}
