/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2012, 2015
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
package com.ibm.ws.managedobject;

import com.ibm.ws.runtime.metadata.ModuleMetaData;
import com.ibm.wsspi.injectionengine.ReferenceContext;

public interface ManagedObjectService
{
    /**
     * Creates a factory for a specified class.
     *
     * Note that this method will only work if the class is in the unrestricted set of bean types
     * for the managed bean. The {@link #createEJBManagedObjectFactory(Class<?>, String) createEJBManagedObjectFactory} method below is recommended for instantiating EJB classes.
     *
     * @param <T> the type of object being managed
     *
     * @param mmd the ModuleMetaData for the module which contains the managed object
     * @param klass the class of instances to create
     * @param requestManagingInjectionAndInterceptors true requests that the returned ManagedObjectFactory
     *            will perform dependency injection when creating an instance of the managed object and
     *            the instance will handle interceptors, including around construct.
     * @return the managed object factory
     * @throws ManagedObjectException if an exception occurs creating the factory
     */
    <T> ManagedObjectFactory<T> createManagedObjectFactory(ModuleMetaData mmd, Class<T> klass, boolean requestManagingInjectionAndInterceptors)
                    throws ManagedObjectException;
    
    /**
     * Creates a factory for a specified class.
     *
     * Note that this method will only work if the class is in the unrestricted set of bean types
     * for the managed bean. The {@link #createEJBManagedObjectFactory(Class<?>, String) createEJBManagedObjectFactory} method below is recommended for instantiating EJB classes.
     *
     * @param <T> the type of object being managed
     *
     * @param mmd the ModuleMetaData for the module which contains the managed object
     * @param klass the class of instances to create
     * @param requestManagingInjectionAndInterceptors true requests that the returned ManagedObjectFactory
     *            will perform dependency injection when creating an instance of the managed object and
     *            the instance will handle interceptors, including around construct.
     * @param referenceContext the referenecContext which will be used for dependancy injection. If null, the WeldCreationalContext will be used.           
     * @return the managed object factory
     * @throws ManagedObjectException if an exception occurs creating the factory
     */
    <T> ManagedObjectFactory<T> createManagedObjectFactory(ModuleMetaData mmd, Class<T> klass, boolean requestManagingInjectionAndInterceptors,ReferenceContext referenceContext)
                    throws ManagedObjectException;

    /**
     * Creates a factory for a specified ejb class.
     *
     * @param <T> the type of object being managed
     *
     * @param mmd the ModuleMetaData for the module which contains the managed object
     * @param klass the class of instances to create
     * @param ejbName the enterprise bean name of the ejb to be instantiated
     * @return the managed object factory
     * @throws ManagedObjectException if an exception occurs creating the factory
     */
    <T> ManagedObjectFactory<T> createEJBManagedObjectFactory(ModuleMetaData mmd, Class<T> klass, String ejbName)
                    throws ManagedObjectException;

    /**
     * Creates a managed object from a specific instance. When using this
     * method, the caller is responsible for constructor injection.
     *
     * @param <T> the type of object being managed
     *
     * @param mmd the ModuleMetaData for the module which contains the managed object
     * @param instance the object being managed
     * @return the managed object
     * @throws ManagedObjectException if an exception occurs creating the managed object
     */
    <T> ManagedObject<T> createManagedObject(ModuleMetaData mmd, T instance)
                    throws ManagedObjectException;

    /**
     * Creates a factory for a specified interceptor class.
     *
     * @param <T> the type of object being managed
     *
     * @param mmd the ModuleMetaData for the module which contains the managed object
     * @param klass the class of instances to create
     * @return the managed object factory
     * @throws ManagedObjectException if an exception occurs creating the factory
     */
    <T> ManagedObjectFactory<T> createInterceptorManagedObjectFactory(ModuleMetaData mmd, Class<T> klass)
                    throws ManagedObjectException;

}
