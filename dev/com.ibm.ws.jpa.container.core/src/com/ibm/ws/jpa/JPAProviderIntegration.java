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
package com.ibm.ws.jpa;

import java.util.Map;
import java.util.Properties;

import javax.persistence.spi.PersistenceProvider;
import javax.persistence.spi.PersistenceUnitInfo;

import com.ibm.ws.container.service.app.deploy.ModuleInfo;

/**
 * JPA Provider interface.
 */
public interface JPAProviderIntegration {
    /**
     * Returns the provider class name.
     * 
     * <li>provider configuration attribute -- server.xml</li> <li>provider specific to
     * implementors of this interface</li>
     * 
     */
    public String getProviderClassName();

    /**
     * Affirm that this provider should be used in the EntityManager pool
     */
    public boolean supportsEntityManagerPooling();

    /**
     * Update the provided map with any integration properties that are required for the provided
     * persistence unit. This map is provided to {@link PersistenceProvider#createContainerEntityManagerFactory}.
     */
    public void updatePersistenceProviderIntegrationProperties(PersistenceUnitInfo puInfo, Map<String, Object> props);

    /**
     * Update the properties returned from {@link PersistenceUnitInfo#getProperties} with
     * provider-specific properties.
     */
    public void updatePersistenceUnitProperties(String providerClassName, Properties props);

    /**
     * Update the provided integration properties map such that INFO and WARNING logging is
     * disabled(or redirected to TRACE).
     */
    public void disablePersistenceUnitLogging(Map<String, Object> integrationProperties);

    public void moduleStarting(ModuleInfo moduleInfo);

    public void moduleStarted(ModuleInfo moduleInfo);

    public void moduleStopping(ModuleInfo moduleInfo);

    public void moduleStopped(ModuleInfo moduleInfo);
}
