/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2014
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.websphere.config.mbeans;

import java.util.Collection;

/**
 * The ServerXMLConfigurationMBean provides an interface for retrieving the file paths
 * of all of the server configuration files known to the server.
 * <p>
 * The ObjectName for this MBean is {@value #OBJECT_NAME}.
 * 
 * @ibm-api
 */
public interface ServerXMLConfigurationMBean {

    /**
     * A String representing the {@link javax.management.ObjectName} that this MBean maps to.
     */
    public static final String OBJECT_NAME = "WebSphere:name=com.ibm.websphere.config.mbeans.ServerXMLConfigurationMBean";

    /**
     * Fetches and returns a collection containing the file paths of all the server
     * configuration files known to the server.
     * 
     * @return an unordered collection of server configuration file paths
     */
    public Collection<String> fetchConfigurationFilePaths();

}
