/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2007, 2010
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
package com.ibm.wsspi.injectionengine.factory;

import javax.naming.Reference;

import com.ibm.wsspi.injectionengine.InjectionConfigurationException;

/**
 * Instances of this interface are used to create Reference objects with
 * lookup information for EJBLink References, which the caller then binds
 * to a JNDI name space. When the object is looked up, the associated
 * factory uses the EJBLink information and component or environment
 * specific information to resolve the EJBLink. <p>
 *
 * Implementations of this interface may resolve the EJBLink reference
 * by either looking up and using the WebSphere provided EJBFactory
 * for the application, or accessing EJB container internals directly
 * (i.e. if the implementation is provided by EJB Container). <p>
 **/
public interface EJBLinkReferenceFactory
{
    /**
     * This method creates an EJBLink Reference based on the application,
     * module, bean name, bean interface, home interface, and an indication
     * if the ref is remote or local. <p>
     *
     * @param refName name of the ejb-ref.
     * @param application name of the application containing the ref.
     * @param module name of the module containing the ref.
     * @param component name of the component containing the ref.
     * @param beanName name of the bean specified on ejb-link or annotation.
     * @param beanInterface interface of the referenced bean.
     * @param homeInterface home interface of the referenced bean.
     * @param localRef true if this represents an ejb-local-ref.
     * @param remoteRef true if this represents an ejb-ref.
     *
     * @return the created EJBLink Reference.
     **/
    public Reference createEJBLinkReference(String refName,
                                            String application,
                                            String module,
                                            String component,
                                            String beanName,
                                            String beanInterface,
                                            String homeInterface,
                                            boolean localRef,
                                            boolean remoteRef)
                    throws InjectionConfigurationException;
}
