/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2010, 2012
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
package com.ibm.ws.ejbcontainer;

import com.ibm.websphere.csi.J2EEName;

/**
 * JCDIHelper is an abstraction layer to avoid dependencies on the real JCDI
 * service from the core EJB container runtime and the embeddable container. <p>
 *
 * The WAS specific runtime will provide an implementation that access the
 * JCDI Service. <p>
 */
public interface JCDIHelper
{
    /**
     * Returns the EJB interceptor class which is to be invoked as the first
     * interceptor in the chain of EJB interceptors. <p>
     *
     * A null value will be returned if the EJB does not require a JCDI
     * interceptor.
     *
     * @param j2eeName
     *            the unique JavaEE name containing the application, module, and
     *            EJB name
     * @param ejbImpl
     *            the EJB implementation class
     **/
    // F743-29169
    public Class<?> getFirstEJBInterceptor(J2EEName j2eeName, Class<?> ejbImpl);

    /**
     * Returns the EJB interceptor class which is to be invoked as the last
     * interceptor in the chain of EJB interceptors. <p>
     *
     * A null value will be returned if the EJB does not require a JCDI
     * interceptor.
     *
     * @param j2eeName
     *            the unique JavaEE name containing the application, module, and
     *            EJB name
     * @param ejbImpl
     *            the EJB implementation class
     **/
    public Class<?> getEJBInterceptor(J2EEName j2eeName, Class<?> ejbImpl);
}
