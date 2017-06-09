/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2017
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
package com.ibm.ws.classloading;

/**
 * Interface for a service that makes services providers in META-INF/services available to the thread context class loader.
 * Service properties must include
 * <ul>
 * <li>implementation.class - String value indicating the fully qualified name of the service provider implementation class</li>
 * <li>file.path - String value indicating the path within the JAR file, excluding the initial / for META-INF/services/{fully.qualified.interface.name}</li>
 * <li>file.url - URL value indicating the location of the META-INF/services/{fully.qualified.interface.name} file</li>
 * </ul>
 */
public interface MetaInfServicesProvider {
    /**
     * Returns the implementation class of the service provider.
     *
     * @return the implementation class of the service provider.
     */
    Class<?> getProviderImplClass();
}
