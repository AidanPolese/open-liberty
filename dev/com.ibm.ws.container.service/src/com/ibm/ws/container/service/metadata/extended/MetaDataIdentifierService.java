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
import com.ibm.ws.runtime.metadata.MetaData;

/**
 * Internal-use-only extension/addition to the MetaDataService SPI.
 */
public interface MetaDataIdentifierService {

    /**
     * Returns the identifier for the specified metadata.
     * 
     * @param metaData the metadata.
     * @return the identifier for the specified metadata.
     * @throws IllegalArgumentException if the metadata implementation is not recognized by the service.
     */
    String getMetaDataIdentifier(MetaData metaData) throws IllegalArgumentException;

    /**
     * Returns the metadata identifier for the specified app component.
     * 
     * @param type the module/component type, either "WEB" or "EJB".
     * @param appName the application
     * @param moduleName the module within the application
     * @param componentName the component within the module
     * 
     * @return the metadata identifier for the specified app component.
     * 
     * @throws IllegalArgumentException if the metadata implementation is not recognized by the service.
     */
    String getMetaDataIdentifier(String type, String appName, String moduleName, String componentName) throws IllegalArgumentException;

    /**
     * Returns the metadata for the specified identifier. Null if no such metadata exists.
     * 
     * @param identifier the identifier for the metadata.
     * @return the metadata for the specified identifier. Null if no such metadata exists.
     * @throws IllegalStateException if the identifier is not recognized by the service.
     */
    MetaData getMetaData(String identifier) throws IllegalStateException;

    /**
     * This is a somewhat temporary/hacky method for obtaining the context classloader
     * associated with the given ComponentMetaData. ClassLoadingServiceImpl uses this
     * method to obtain the context classloader/identifier for a given app/module/comp,
     * until we have a proper app classloader service.
     * 
     * @param type the module/component type, either "WEB" or "EJB"
     * @param metaData the module/component meta data
     * 
     * @return the context classloader associated with the given meta data.
     */
    ClassLoader getClassLoader(String type, ComponentMetaData metaData);

    /**
     * Indicates whether or not metadata exists for the specified identifier.
     * 
     * @param identifier the identifier for the metadata.
     * 
     * @return true if metadata is available for the specified identifier, otherwise false.
     */
    boolean isMetaDataAvailable(String identifier);
}
