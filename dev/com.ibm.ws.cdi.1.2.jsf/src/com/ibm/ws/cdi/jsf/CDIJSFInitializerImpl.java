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
package com.ibm.ws.cdi.jsf;

import javax.el.ELResolver;
import javax.enterprise.inject.spi.BeanManager;
import javax.faces.application.Application;

import org.jboss.weld.el.WeldELContextListener;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.ibm.ws.cdi.CDIService;
import com.ibm.ws.cdi.interfaces.CDIRuntime;
import com.ibm.ws.jsf.cdi.CDIJSFInitializer;
import com.ibm.wsspi.kernel.service.utils.AtomicServiceReference;

@Component(
                name = "com.ibm.ws.jsf.cdi.CDIJSFInitializer",
                property = { "service.vendor=IBM", "service.ranking:Integer=100" })
public class CDIJSFInitializerImpl implements CDIJSFInitializer {

    private final AtomicServiceReference<CDIService> cdiServiceRef = new AtomicServiceReference<CDIService>("cdiService");

    /** {@inheritDoc} */
    @Override
    public void initializeJSF(Application application) {
        CDIService cdiService = cdiServiceRef.getService();
        if (cdiService != null) {
            BeanManager beanManager = cdiService.getCurrentBeanManager();
            if (beanManager != null) {
                application.addELContextListener(new WeldELContextListener());

                CDIRuntime cdiRuntime = (CDIRuntime) cdiService;
                String contextID = cdiRuntime.getCurrentApplicationContextID();
                application.setViewHandler(new IBMViewHandler(application.getViewHandler(), contextID));

                ELResolver elResolver = beanManager.getELResolver();
                application.addELResolver(elResolver);
            }
        }
    }

    public void activate(ComponentContext context) {
        cdiServiceRef.activate(context);
    }

    public void deactivate(ComponentContext context) {
        cdiServiceRef.deactivate(context);
    }

    @Reference(name = "cdiService", service = CDIService.class)
    protected void setCdiService(ServiceReference<CDIService> ref) {
        cdiServiceRef.setReference(ref);
    }

    protected void unsetCdiService(ServiceReference<CDIService> ref) {
        cdiServiceRef.unsetReference(ref);
    }
}
