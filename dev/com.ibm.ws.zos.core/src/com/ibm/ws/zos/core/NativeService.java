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
package com.ibm.ws.zos.core;

/**
 * Marker interface that represents an authorized service entry in the OSGi
 * service registry.
 */
public interface NativeService {

    /**
     * Service property that indicates the native service name.
     */
    public final static String NATIVE_SERVICE_NAME = "native.service.name";

    /**
     * Service property that indicates the authorization group name.
     */
    public final static String AUTHORIZATION_GROUP_NAME = "native.authorization.group.name";

    /**
     * Service property that indicates whether or not this server is
     * authorized to use the associated native service.
     */
    public final static String IS_AUTHORIZED = "is.authorized";

    /**
     * Get the service name as declared in the service vector table.
     * 
     * @return the service name from the vector table
     */
    public String getServiceName();

    /**
     * Get the name of the authorization group used to control access to
     * this native service.
     * 
     * @return the name of the authorization group from the vector table
     */
    public String getAuthorizationGroup();

    /**
     * Determine whether or use of this service has been permitted by the
     * in system administrator in the current environment.
     */
    public boolean isPermitted();
}