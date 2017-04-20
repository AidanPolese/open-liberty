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
package com.ibm.ws.security.authorization.jacc.web.impl;

import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Deactivate;

import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.security.authorization.jacc.web.ServletService;
import com.ibm.ws.security.authorization.jacc.web.WebSecurityPropagator;
import com.ibm.ws.security.authorization.jacc.web.WebSecurityValidator;

@Component(service = ServletService.class,
                immediate = true,
                name = "com.ibm.ws.security.authorization.jacc.web.servletservice",
                configurationPolicy = ConfigurationPolicy.IGNORE,
                property = { "service.vendor=IBM" })
public class ServletServiceImpl implements ServletService {
    private static final TraceComponent tc = Tr.register(ServletServiceImpl.class);

    private static WebSecurityPropagatorImpl wsp = null;
    private static WebSecurityValidatorImpl wsv = null;

    public ServletServiceImpl() {}

    @Activate
    protected synchronized void activate(ComponentContext cc) {}

    @Deactivate
    protected synchronized void deactivate(ComponentContext cc) {}

    /** {@inheritDoc} */
    @Override
    public synchronized WebSecurityPropagator getPropagator() {
        if (wsp == null) {
            wsp = new WebSecurityPropagatorImpl();
        }
        return wsp;
    }

    /** {@inheritDoc} */
    @Override
    public synchronized WebSecurityValidator getValidator() {
        if (wsv == null) {
            wsv = new WebSecurityValidatorImpl();
        }
        return wsv;
    }
}
