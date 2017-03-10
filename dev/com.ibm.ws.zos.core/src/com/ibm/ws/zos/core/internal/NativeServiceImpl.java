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
package com.ibm.ws.zos.core.internal;

import com.ibm.ws.zos.core.NativeClientService;

/**
 * Implementation of {@code NativeService} that is registered, either client or server.
 */
public final class NativeServiceImpl implements NativeClientService {

    /**
     * Service name from the native vector table.
     */
    final String serviceName;

    /**
     * Authorization group name from the native vector table.
     */
    final String authorizationGroup;

    /**
     * Indication of whether or not the system will permit the use of
     * this native service in the current environment.
     */
    final boolean permitted;

    /**
     * Inidication of whether this is a client or server service.
     */
    final boolean client;

    /**
     * Construct a new mock representation of a native service.
     * 
     * @param servieName the name of the service from the native vector table
     * @param authorizationGroup the name of the SAF authorization group that
     *            controls access to the authorized service
     * @param permitted indication of whether or not the system will permit the
     *            use of this service in the current environment
     * @param client indication of whether this is a client or server service.
     */
    NativeServiceImpl(String serviceName, String authorizationGroup, boolean permitted, boolean client) {
        this.serviceName = serviceName;
        this.authorizationGroup = authorizationGroup;
        this.permitted = permitted;
        this.client = client;
    }

    /** {@inheritDoc} */
    @Override
    public String getServiceName() {
        return serviceName;
    }

    /** {@inheritDoc} */
    @Override
    public String getAuthorizationGroup() {
        return authorizationGroup;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isPermitted() {
        return permitted;
    }

    public boolean isClient() {
        return client;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(super.toString());
        sb.append(";serviceName=").append(serviceName);
        sb.append(",authorizationGroup=").append(authorizationGroup);
        sb.append(",permitted=").append(permitted);
        sb.append(",client=").append(client);
        return sb.toString();
    }

}
