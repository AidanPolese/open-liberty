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
package com.ibm.ws.security.authorization.jacc.ejb.impl;

import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Deactivate;

import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.security.authorization.jacc.ejb.EJBSecurityPropagator;
import com.ibm.ws.security.authorization.jacc.ejb.EJBSecurityValidator;
import com.ibm.ws.security.authorization.jacc.ejb.EJBService;

@Component(service = EJBService.class,
                immediate = true,
                name = "com.ibm.ws.security.authorization.jacc.ejb.ejbservice",
                configurationPolicy = ConfigurationPolicy.IGNORE,
                property = { "service.vendor=IBM" })
public class EJBServiceImpl implements EJBService {
    private static final TraceComponent tc = Tr.register(EJBServiceImpl.class);

    private static EJBSecurityPropagatorImpl esp = null;
    private static EJBSecurityValidatorImpl esv = null;

    public EJBServiceImpl() {}

    @Activate
    protected synchronized void activate(ComponentContext cc) {}

    @Deactivate
    protected synchronized void deactivate(ComponentContext cc) {}

    /** {@inheritDoc} */
    @Override
    public synchronized EJBSecurityPropagator getPropagator() {
        if (esp == null) {
            esp = new EJBSecurityPropagatorImpl();
        }
        return esp;
    }

    /** {@inheritDoc} */
    @Override
    public synchronized EJBSecurityValidator getValidator() {
        if (esv == null) {
            esv = new EJBSecurityValidatorImpl();
        }
        return esv;
    }
}
