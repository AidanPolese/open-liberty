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

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.cdi.CDIException;
import com.ibm.ws.cdi.CDIService;
import com.ibm.ws.cdi.CDIServiceUtils;
import com.ibm.ws.cdi.ejb.impl.JCDIHelperImpl;
import com.ibm.ws.cdi.interfaces.CDIRuntime;
import com.ibm.ws.ejbcontainer.JCDIHelper;
import com.ibm.ws.ejbcontainer.osgi.JCDIHelperFactory;
import com.ibm.ws.runtime.metadata.ModuleMetaData;
import com.ibm.wsspi.adaptable.module.Container;

@Component(
                name = "com.ibm.ws.cdi.ejb.liberty.JCDIHelperFactoryImpl",
                property = { "service.vendor=IBM" })
public class JCDIHelperFactoryImpl implements JCDIHelperFactory {

    private static final TraceComponent tc = Tr.register(JCDIHelperFactoryImpl.class);

    private volatile CDIService cdiService = null;

    // declarative service
    @Reference(name = "cdiService")
    protected void setCdiService(CDIService service) {
        cdiService = service;
    }

    // declarative service
    protected void unsetCdiService(CDIService service) {
        cdiService = null;
    }

    @Override
    public JCDIHelper getJCDIHelper(Container container) {
        JCDIHelper returnValue = null;

        try {
            // Capture locally so that we can hold the cdiService
            // instance until after any other thread tries to remove it
            CDIRuntime cdiRuntime = (CDIRuntime) cdiService;

            ModuleMetaData moduleMetaData = CDIServiceUtils.getModuleMetaData(container);
            if (cdiRuntime != null && cdiRuntime.isModuleCDIEnabled(moduleMetaData)) {
                returnValue = JCDIHelperImpl.instance;
            }
        } catch (CDIException e) {
            if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
            {
                Tr.debug(tc, "Problem establishing if CDI Service is enabled. Error: {0} ", e);
            }
        }
        return returnValue;
    }

}
