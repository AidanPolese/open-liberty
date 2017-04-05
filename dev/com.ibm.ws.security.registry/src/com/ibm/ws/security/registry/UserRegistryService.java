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
package com.ibm.ws.security.registry;

/**
 * Defines the service API which allows for retrieving a configured, initialized
 * UserRegistry instance.
 *
 * Implementors must define the component property: {@link #REGISTRY_TYPE}.
 * The value of the type property must be unique and is used to find the correct
 * UserRegistryFactory service.
 */
public interface UserRegistryService {

    /**
     * The {@link #REGISTRY_TYPE} must be unique for each type
     * of UserRegistryFactory and UserRegistryConfiguration implementation.
     * The value must be of type String.
     */
    String REGISTRY_TYPE = "com.ibm.ws.security.registry.type";

    /**
     * Determines if the UserRegistry for the current domain is configured.
     * <p>
     * Use this method with caution. There are some cases where no UserRegistry
     * is valid, but they are specific cases.
     *
     * @return {@code true} if a UserRegistry is configured, {@code false} otherwise.
     * @throws RegistryException if there is a configuration detection problem.
     */
    boolean isUserRegistryConfigured() throws RegistryException;

    /**
     * Returns the "active" UserRegistry based on the current effective
     * configuration.
     *
     * @return A UserRegistry instance. <code>null</code> is not returned.
     * @exception RegistryException if there is any UserRegistry configuration,
     *                creation or initialization problem
     */
    UserRegistry getUserRegistry() throws RegistryException;

    /**
     * Returns the UserRegistry instance which has the specified id.
     *
     * @param id a specific id. Must not be <code>null</code>.
     * @return A UserRegistry instance. <code>null</code> is not returned.
     * @exception RegistryException if there is any UserRegistry configuration,
     *                creation or initialization problem.
     *                Also thrown if the requested id can not be satisfied.
     * @exception NullPointerException if id is <code>null</code>
     */
    UserRegistry getUserRegistry(String id) throws RegistryException;

    /**
     * Returns the "active" UserRegistry type based on the current effective
     * configuration.
     *
     * @return A unique string indicating the type of the UserRegistry instance. <code>null</code> is not returned.
     */
    String getUserRegistryType();

    /**
     * unwrap or wrap as appropriate
     *
     * @param userRegistry
     * @return
     */
    com.ibm.websphere.security.UserRegistry getExternalUserRegistry(com.ibm.ws.security.registry.UserRegistry userRegistry);
}
