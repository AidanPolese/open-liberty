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
package com.ibm.ws.classloading;

/**
 * Internal-use-only extension/addition to the ClassLoadingService SPI.
 */
public interface ClassLoaderIdentifierService {
    /**
     * Returns the identifier for the specified classloader.
     * 
     * @param classloader the classloader.
     * @return the identifier for the specified classloader.
     * @throws IllegalArgumentException if the classloader implementation is not recognized by the service.
     */
    String getClassLoaderIdentifier(ClassLoader classloader) throws IllegalArgumentException;

    /**
     * Returns the identifier for the context classloader of the given app / module / component.
     * 
     * @param type the module/comp type, either "WEB" or "EJB"
     * @param appName the application name
     * @param moduleName the module within the application
     * @param componentName the component within the module
     * 
     * @return the context classloader id
     */
    String getClassLoaderIdentifier(String type, String appName, String moduleName, String componentName);

    /**
     * Returns the classloader for the specified identifier. Null if no such classloader exists.
     * 
     * @param identifier the identifier for the classloader.
     * @return the classloader for the specified identifier. Null if no such classloader exists.
     * @throws IllegalArgumentException if the identifier is not recognized by the service.
     */
    ClassLoader getClassLoader(String identifier) throws IllegalArgumentException;

}
