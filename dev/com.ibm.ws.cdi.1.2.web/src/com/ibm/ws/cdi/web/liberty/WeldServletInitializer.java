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
package com.ibm.ws.cdi.web.liberty;

import javax.servlet.ServletContainerInitializer;

import org.osgi.framework.ServiceReference;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.ibm.ws.cdi.web.impl.AbstractServletInitializer;
import com.ibm.ws.cdi.web.interfaces.CDIWebRuntime;
import com.ibm.ws.runtime.metadata.ApplicationMetaData;
import com.ibm.ws.webcontainer.webapp.WebAppConfigExtended;
import com.ibm.wsspi.webcontainer.metadata.WebModuleMetaData;
import com.ibm.wsspi.webcontainer.servlet.IServletContext;
import com.ibm.wsspi.webcontainer.webapp.WebAppConfig;

@Component(
                name = "com.ibm.ws.cdi.servletInitializer",
                property = { "service.vendor=IBM", "service.ranking:Integer=101" })
public class WeldServletInitializer extends AbstractServletInitializer implements ServletContainerInitializer {

    @Override
    @Reference(name = "cdiWebRuntime", service = CDIWebRuntime.class)
    protected void setCdiWebRuntime(ServiceReference<CDIWebRuntime> ref) {
        super.setCdiWebRuntime(ref);
    }

    /** {@inheritDoc} */
    @Override
    protected String getApplicationJ2EEName(IServletContext isc) {
        WebAppConfig webAppConfig = isc.getWebAppConfig();

        WebModuleMetaData webModuleMetaData = ((WebAppConfigExtended) webAppConfig).getMetaData();
        ApplicationMetaData applicationMetaData = webModuleMetaData.getApplicationMetaData();
        String j2eeName = applicationMetaData.getJ2EEName().toString();

        return j2eeName;
    }

}
