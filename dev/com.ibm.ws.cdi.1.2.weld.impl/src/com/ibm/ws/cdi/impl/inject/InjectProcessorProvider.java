/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2012, 2015
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
package com.ibm.ws.cdi.impl.inject;

import javax.inject.Inject;

import org.osgi.framework.ServiceReference;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.ibm.ws.cdi.CDIService;
import com.ibm.ws.cdi.interfaces.CDIRuntime;
import com.ibm.wsspi.injectionengine.InjectionProcessorProvider;
import com.ibm.wsspi.injectionengine.InjectionSimpleProcessor;
import com.ibm.wsspi.injectionengine.InjectionSimpleProcessorProvider;
import com.ibm.wsspi.kernel.service.utils.AtomicServiceReference;

@Component(name = "com.ibm.ws.cdi.services.injectProcessorProvider", service = InjectionProcessorProvider.class, property = { "service.vendor=IBM" })
public class InjectProcessorProvider extends InjectionSimpleProcessorProvider<Inject> {

    private final AtomicServiceReference<CDIService> cdiServiceRef = new AtomicServiceReference<CDIService>("cdiService");

    @Override
    public InjectionSimpleProcessor<Inject> createInjectionProcessor() {
        CDIService cdiService = cdiServiceRef.getService();
        CDIRuntime cdiRuntime = (CDIRuntime) cdiService;
        return new InjectInjectionProcessor(cdiRuntime);
    }

    @Override
    public Class<Inject> getAnnotationClass() {
        return Inject.class;
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
