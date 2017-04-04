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
package com.ibm.ws.resource;

import java.util.Map;

/**
 * Builder for resource factories. Implementations should be registered in the
 * service registry with the {@link ResourceFactory#CREATES_OBJECT_CLASS} property.
 */
public interface ResourceFactoryBuilder
{
    /**
     * Creates a resource factory that creates handles of the type specified
     * by the {@link ResourceFactory#CREATES_OBJECT_CLASS} property.
     *
     * @param props the resource-specific type information
     * @return the resource factory
     * @throws Exception a resource-specific exception
     */
    public ResourceFactory createResourceFactory(Map<String, Object> props)
                    throws Exception;

    public boolean removeExistingConfigurations(String filter) throws Exception;
}
