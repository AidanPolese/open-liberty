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
 * A list/map of resource reference configuration.
 * <p>
 * This interface is not intended to be implemented by clients.
 */
public interface ResourceRefConfigList
{
    /**
     * @return the number of resource references in the list.
     */
    int size();

    /**
     * @param i the index
     * @return the resource reference at the specified index
     */
    ResourceRefConfig getResourceRefConfig(int i);

    /**
     * Finds resource reference configuration by name.
     *
     * @param name the name
     * @return the resource reference configuration, or null
     */
    ResourceRefConfig findByName(String name);

    /**
     * Finds resource reference configuration by name, or creates a new resource
     * reference configuration and adds it to the list.
     *
     * @param name the name
     * @return the resource reference configuration
     */
    ResourceRefConfig findOrAddByName(String name);
}
