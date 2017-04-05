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
package com.ibm.ws.cdi.impl.weld.injection;

import org.jboss.weld.injection.spi.InjectionServices;

import com.ibm.ws.cdi.CDIException;
import com.ibm.wsspi.injectionengine.InjectionTarget;

public interface WebSphereInjectionServices extends InjectionServices {

    /**
     * @param targetClass
     * @return
     * @throws CDIException
     */
    public InjectionTarget[] getInjectionTargets(Class<?> targetClass) throws CDIException;

}
