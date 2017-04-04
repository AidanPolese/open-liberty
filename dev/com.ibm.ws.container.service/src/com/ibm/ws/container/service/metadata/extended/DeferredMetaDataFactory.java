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
package com.ibm.ws.container.service.metadata.extended;

import com.ibm.ws.runtime.metadata.ComponentMetaData;

/**
 * Interface by which a container provides deferred metadata creation.
 * Each implementation must be registered with a service property deferredMetaData that identifies
 * the prefix(es) for the type or types of metadata that it handles.
 * The following prefixes are already taken (EJB, WEB, CONNECTOR).
 */
public interface DeferredMetaDataFactory {
    /**
     * Attempt to create component metadata for the specified identifier.
     * If the corresponding component cannot be found, <code>null</code> must be returned.
     * 
     * @param identifier identifier for metadata
     * @return component metadata that matches the identifier. Null if none is found.
     */
    ComponentMetaData createComponentMetaData(String identifier);

    /**
     * Provides any deferred initialization for the component represented by this metadata.
     * If this instance provides deferred initialization for metadata, it must register a service property
     * "supportsDeferredInit" with a Boolean value of true.
     * This method does nothing if already initialized.
     * 
     * @param metadata component metadata.
     * @throws IllegalStateException if not initialized and unable to perform initialization.
     */
    void initialize(ComponentMetaData metadata) throws IllegalStateException;

    /**
     * Returns the metadata identifier for the specified app component.
     * 
     * @param appName the application
     * @param moduleName the module within the application
     * @param componentName the component within the module
     * 
     * @return the metadata identifier for the specified app component.
     */
    String getMetaDataIdentifier(String appName, String moduleName, String componentName);

    /**
     * @return the context classloader associated with the given metadata.
     */
    ClassLoader getClassLoader(ComponentMetaData metadata);
}
