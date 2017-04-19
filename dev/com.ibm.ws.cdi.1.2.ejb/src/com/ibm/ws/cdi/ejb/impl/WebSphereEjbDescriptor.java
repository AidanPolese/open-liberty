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
package com.ibm.ws.cdi.ejb.impl;

import org.jboss.weld.ejb.spi.EjbDescriptor;

import com.ibm.websphere.csi.J2EEName;
import com.ibm.ws.ejbcontainer.EJBReferenceFactory;

public interface WebSphereEjbDescriptor<T> extends EjbDescriptor<T> {

    /**
     * @return The full J2EEName of the EJB
     */
    public J2EEName getEjbJ2EEName();

    /**
     * @return the EJBReferenceFactory used to create EJB References
     */
    public EJBReferenceFactory getReferenceFactory();

}
