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
package com.ibm.ws.container.service.app.deploy;

import com.ibm.wsspi.adaptable.module.Container;

/**
 * Contains information about a JEE application
 */
public interface ApplicationInfo {

    /**
     * Returns the unique application name. This will normally be the name
     * specified in the deployment descriptor (or the URI base name) unless
     * that value would conflict with another application, in which case a
     * unique name will have been generated and will be returned.
     */
    String getName();

    /**
     * Returns the Container object associated with this application
     * 
     */
    Container getContainer();

    /**
     * Returns the unique deployment name for an application.
     */
    String getDeploymentName();

    /**
     * Returns an instance of NestedConfigHelper that can be used to obtain
     * application properties
     * 
     */
    NestedConfigHelper getConfigHelper();

}
