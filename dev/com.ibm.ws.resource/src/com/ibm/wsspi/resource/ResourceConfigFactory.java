/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2013
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
package com.ibm.wsspi.resource;

/**
 * Factory for creating resource configuration.
 * <p>
 * This interface is not intended to be implemented by clients.
 */
public interface ResourceConfigFactory
{
    /**
     * Creates a new {@link ResourceConfig} object.
     *
     * @param type the non-null interface type of the requested resource.
     */
    ResourceConfig createResourceConfig(String type);
}
