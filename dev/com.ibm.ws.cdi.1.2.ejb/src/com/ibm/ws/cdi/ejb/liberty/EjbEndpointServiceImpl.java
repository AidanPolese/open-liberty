/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2013, 2015
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
package com.ibm.ws.cdi.ejb.liberty;

import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Component;

import com.ibm.ws.cdi.CDIException;
import com.ibm.ws.cdi.ejb.impl.AbstractEjbEndpointService;
import com.ibm.ws.cdi.interfaces.CDIArchive;
import com.ibm.ws.cdi.interfaces.EjbEndpointService;
import com.ibm.ws.cdi.liberty.CDIArchiveImpl;
import com.ibm.ws.ejbcontainer.EJBEndpoints;
import com.ibm.ws.ejbcontainer.ManagedBeanEndpoints;
import com.ibm.wsspi.adaptable.module.Container;
import com.ibm.wsspi.adaptable.module.UnableToAdaptException;

/**
 * Interface to process an EJB module
 */

@Component(name = "com.ibm.ws.cdi.ejb.EjbEndpointService", immediate = true, property = { "service.vendor=IBM", "service.ranking:Integer=100" })
public class EjbEndpointServiceImpl extends AbstractEjbEndpointService implements EjbEndpointService {

    public void activate(ComponentContext cc) {
        setInstance(this);
    }

    public void deactivate(ComponentContext cc) {
        setInstance(null);
    }

    @Override
    protected EJBEndpoints getEJBEndpoints(CDIArchive archive) throws CDIException {
        CDIArchiveImpl libertyArchive = (CDIArchiveImpl) archive;
        Container container = libertyArchive.getContainer();
        EJBEndpoints endpoints;
        try {
            endpoints = container.adapt(EJBEndpoints.class);
        } catch (UnableToAdaptException e) {
            throw new CDIException(e);
        }
        return endpoints;
    }

    @Override
    protected ManagedBeanEndpoints getManagedBeanEndpoints(CDIArchive archive) throws CDIException {
        CDIArchiveImpl libertyArchive = (CDIArchiveImpl) archive;
        Container container = libertyArchive.getContainer();
        ManagedBeanEndpoints endpoints;
        try {
            endpoints = container.adapt(ManagedBeanEndpoints.class);
        } catch (UnableToAdaptException e) {
            throw new CDIException(e);
        }
        return endpoints;
    }
}
