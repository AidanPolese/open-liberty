/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2013, 2016
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
package com.ibm.ws.cdi.ejb.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ejb.EJB;

import org.jboss.weld.ejb.spi.EjbDescriptor;

import com.ibm.ws.cdi.CDIException;
import com.ibm.ws.cdi.interfaces.CDIArchive;
import com.ibm.ws.cdi.interfaces.EjbEndpointService;
import com.ibm.ws.cdi.interfaces.EndPointsInfo;
import com.ibm.ws.cdi.interfaces.ManagedBeanDescriptor;
import com.ibm.ws.cdi.interfaces.WebSphereEjbServices;
import com.ibm.ws.ejbcontainer.EJBEndpoint;
import com.ibm.ws.ejbcontainer.EJBEndpoints;
import com.ibm.ws.ejbcontainer.ManagedBeanEndpoint;
import com.ibm.ws.ejbcontainer.ManagedBeanEndpoints;
import com.ibm.ws.ffdc.annotation.FFDCIgnore;

/**
 * Interface to process an EJB module
 */

public abstract class AbstractEjbEndpointService implements EjbEndpointService {
    private static EjbEndpointService instance = null;

    private final Map<String, WebSphereEjbServices> ejbServices = new HashMap<String, WebSphereEjbServices>();

    protected static void setInstance(EjbEndpointService instance) {
        AbstractEjbEndpointService.instance = instance;
    }

    /**
     * @param applicationID
     * @return
     */
    public static WebSphereEjbServices _getWebSphereEjbServices(String applicationID) {
        return AbstractEjbEndpointService.instance.getWebSphereEjbServices(applicationID);
    }

    protected abstract EJBEndpoints getEJBEndpoints(CDIArchive archive) throws CDIException;

    protected abstract ManagedBeanEndpoints getManagedBeanEndpoints(CDIArchive archive) throws CDIException;

    @Override
    public EndPointsInfo getEndPointsInfo(CDIArchive archive) throws CDIException {
        Set<EjbDescriptor<?>> ejbDescriptors = new HashSet<EjbDescriptor<?>>();
        Set<String> nonCDIInterceptorClassNames = new HashSet<String>();
        Set<ManagedBeanDescriptor<?>> managedBeanDescriptors = new HashSet<ManagedBeanDescriptor<?>>();

        ClassLoader classLoader = archive.getClassLoader();

        try {
            EJBEndpoints eps = getEJBEndpoints(archive);
            //get hold of ejb descriptors and non cdi interceptors
            if (eps != null) {
                List<EJBEndpoint> ejbs = eps.getEJBEndpoints();
                if (ejbs != null && ejbs.size() > 0) {
                    for (EJBEndpoint ejb : ejbs) {
                        EjbDescriptor<?> descriptor = EjbDescriptorImpl.newInstance(ejb, classLoader);
                        ejbDescriptors.add(descriptor);
                    }
                }
                //get hold of non cdi interceptors

                nonCDIInterceptorClassNames.addAll(eps.getEJBInterceptorClassNames());

            }

            ManagedBeanEndpoints mbeps = getManagedBeanEndpoints(archive);
            //get hold of managed bean descriptors and non cdi interceptors

            if (mbeps != null) {
                List<ManagedBeanEndpoint> managedBeans = mbeps.getManagedBeanEndpoints();
                if (managedBeans != null && managedBeans.size() > 0) {
                    for (ManagedBeanEndpoint managedBean : managedBeans) {
                        ManagedBeanDescriptor<?> descriptor = ManagedBeanDescriptorImpl.newInstance(managedBean, classLoader);
                        managedBeanDescriptors.add(descriptor);
                    }
                }
                //get hold of non cdi interceptors

                nonCDIInterceptorClassNames.addAll(mbeps.getManagedBeanInterceptorClassNames());

            }

        } catch (ClassNotFoundException e) {
            throw new CDIException(e);
        }
        return new EndPointsInfoImpl(managedBeanDescriptors, ejbDescriptors, nonCDIInterceptorClassNames, classLoader);
    }

    /** {@inheritDoc} */
    @Override
    public WebSphereEjbServices getWebSphereEjbServices(String applicationID) {

        WebSphereEjbServices services = null;
        synchronized (ejbServices) {
            services = ejbServices.get(applicationID);
            if (services == null) {
                services = new WebSphereEjbServicesImpl();
                ejbServices.put(applicationID, services);
            }
        }

        return services;
    }

    /**
     * {@inheritDoc}
     *
     * @throws CDIException
     */
    @Override
    @FFDCIgnore({ ClassNotFoundException.class })
    public void validateEjbInjection(EJB ejb, CDIArchive injectionArchive, Class<?> injectionType) throws ClassCastException, CDIException {

        Collection<CDIArchive> moduleArchives = injectionArchive.getApplication().getModuleArchives();
        String applicationName = injectionArchive.getApplication().getName();
        String moduleName = injectionArchive.getName();

        for (CDIArchive archive : moduleArchives) {
            try {

                EJBEndpoint endpoint = findEJBEndpoint(archive, ejb, injectionType, applicationName, moduleName);

                if (endpoint != null) {
                    // Found it!
                    return;
                }
            } catch (ClassNotFoundException e) {
                // Could not find the class, even though it should be in this module
                // Give up looking but don't fail validation
                return;
            } catch (ClassCastException e) {
                // Found the EJB and it's the wrong type
                // Throw on this exception to be handled by the caller
                throw e;
            }

            //TODO what happens if the endpoint is not found in any of the archives??
        }
    }

    private EJBEndpoint findEJBEndpoint(CDIArchive archive, EJB ejb, Class<?> injectionType, String applicationName,
                                        String moduleName) throws CDIException, ClassNotFoundException, ClassCastException {
        EJBEndpoint endpoint = null;

        EJBEndpoints eps = getEJBEndpoints(archive);

        if (eps != null) {
            endpoint = eps.findEJBEndpoint(ejb, injectionType, applicationName, moduleName);
        }
        return endpoint;
    }
}
