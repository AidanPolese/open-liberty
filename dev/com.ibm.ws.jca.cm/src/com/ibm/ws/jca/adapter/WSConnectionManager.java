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
package com.ibm.ws.jca.adapter;

import javax.resource.spi.ConnectionManager;
import com.ibm.ws.resource.ResourceRefInfo;

/**
 * WebSphere Application Server extensions to the ConnectionManager interface.
 */
public interface WSConnectionManager extends ConnectionManager {
    /**
     * Returns the purge policy for this connection manager.
     * 
     * @return the purge policy for this connection manager.
     */
    PurgePolicy getPurgePolicy();

    /**
     * Returns resource reference attributes.
     * 
     * @return resource reference attributes.
     */
    ResourceRefInfo getResourceRefInfo();
}