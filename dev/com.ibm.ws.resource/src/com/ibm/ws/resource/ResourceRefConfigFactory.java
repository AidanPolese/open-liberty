/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2011, 2013
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
package com.ibm.ws.resource;

/**
 * Factory for creating resource reference configuration and lists.
 * <p>
 * This interface is not intended to be implemented by clients.
 */
public interface ResourceRefConfigFactory
{
    /**
     * Creates a new {@link ResourceRefConfig} object.
     *
     * @param type the non-null interface type of the requested resource.
     */
    ResourceRefConfig createResourceRefConfig(String type);

    /**
     * Creates a new {@link ResourceRefConfigList} object.
     */
    ResourceRefConfigList createResourceRefConfigList();
}
