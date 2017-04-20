/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2014
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.jaxrs20.appsecurity.component;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.component.annotations.ReferencePolicyOption;

import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.wsspi.ssl.SSLSupport;

/**
 * DON"T EVER USE A CLASS LIKE THIS UNLESS YOU NEED TO ACCESS A SERVICE FROM A NON-OSGI CONTEXT
 * and you understand the lifecycle issues involved and have dealt with them.
 */

@Component(name = "com.ibm.ws.jaxrs20.appsecurity.component.SSLSupportService", property = { "service.vendor=IBM" })
public class SSLSupportService {

    private static final TraceComponent tc = Tr.register(SSLSupportService.class);
    private static volatile SSLSupport sslSupport;

    @Reference(name = "SSLSupportService",
               service = SSLSupport.class,
               cardinality = ReferenceCardinality.OPTIONAL,
               policy = ReferencePolicy.DYNAMIC,
               policyOption = ReferencePolicyOption.GREEDY)
    protected void setSSLSupportService(SSLSupport service) {
        if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
            Tr.debug(this, tc, "registerSSLSupportService");
        }
        sslSupport = service;
    }

    protected void unsetSSLSupportService(SSLSupport service) {
        if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
            Tr.debug(this, tc, "unregisterSSLSupportService");
        }
        if (sslSupport == service)
            sslSupport = null;
    }

    public static boolean isSSLSupportServiceReady() {

        return (sslSupport != null) ? true : false;
    }

    public static SSLSupport getSSLSupport() {

        return sslSupport;
    }

}
