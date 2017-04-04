/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2015
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
package com.ibm.ws.cdi.interfaces;

import javax.ejb.EJB;

import com.ibm.ws.cdi.CDIException;

/**
 * Interface to process an EJB module
 */
public interface EjbEndpointService {

    public WebSphereEjbServices getWebSphereEjbServices(String applicationID);

    /**
     * Validate an @EJB injection point
     *
     * @param ejb the EJB annotation
     * @param archive the archive containing the injection point
     * @param injectionType the type of the field being injected into
     * @throws ClassCastException if the corresponding EJB is found but does not match the injectionType
     * @throws CDIException if there is another problem validating the EJB
     */
    public void validateEjbInjection(EJB ejb, CDIArchive archive, Class<?> injectionType) throws ClassCastException, CDIException;

    /**
     * Create the EndPointsInfo, which includes all managed bean descriptors, ejb descriptors and all non-cdi interceptors
     *
     * @param archive The archive
     * @return the EndPointInfo object that contains all managed bean descriptors, ejb descriptors and all non-cdi interceptors
     * @throws EjbEndpointServiceException if anything goes wrong in finding the EJB/ManagedBean Endpoint information
     */
    EndPointsInfo getEndPointsInfo(CDIArchive archive) throws CDIException;

}
