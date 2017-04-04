/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2012
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.jaxrs20.client.component;

import java.util.Map;

import javax.net.ssl.SSLSocketFactory;
import javax.servlet.http.Cookie;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.component.annotations.ReferencePolicyOption;

import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.jaxrs20.api.JaxRsAppSecurityService;

/**
 * 
 */
@Component(name = "com.ibm.ws.jaxrs20.client.component.JaxRsAppSecurity", immediate = true, property = { "service.vendor=IBM" })
public class JaxRsAppSecurity {
    private static final TraceComponent tc = Tr.register(JaxRsAppSecurity.class);
    private static volatile JaxRsAppSecurityService appSecurityService = null;

    @Reference(name = "JaxRsAppSecurityService",
               service = JaxRsAppSecurityService.class,
               cardinality = ReferenceCardinality.OPTIONAL,
               policy = ReferencePolicy.DYNAMIC,
               policyOption = ReferencePolicyOption.GREEDY)
    protected void setJaxRsAppSecurityService(JaxRsAppSecurityService service) {
        if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
            Tr.debug(this, tc, "registerJaxRsAppSecurityService");
        }
        appSecurityService = service;
    }

    protected void unsetJaxRsAppSecurityService(JaxRsAppSecurityService service) {
        if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
            Tr.debug(this, tc, "unregisterJaxRsAppSecurityService");
        }
        if (appSecurityService == service)
            appSecurityService = null;
    }

    public static Cookie getSSOCookieFromSSOToken() {
        if (appSecurityService != null) {
            return appSecurityService.getSSOCookieFromSSOToken();
        }
        return null;
    }

    public static SSLSocketFactory getSSLSocketFactory(String sslRef, Map<String, Object> props) {

        if (appSecurityService != null) {
            return appSecurityService.getSSLSocketFactory(sslRef, props);
        }

        return null;
    }
}
