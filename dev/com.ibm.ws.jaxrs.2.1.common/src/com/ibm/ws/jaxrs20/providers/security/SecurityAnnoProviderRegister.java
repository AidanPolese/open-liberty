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
package com.ibm.ws.jaxrs20.providers.security;

import java.util.List;
import java.util.Set;

import org.apache.cxf.jaxrs.security.SimpleAuthorizingFilter;
import org.osgi.service.component.annotations.Component;

import com.ibm.ws.jaxrs20.providers.api.JaxRsProviderRegister;
import com.ibm.ws.jaxrs20.security.LibertySimpleAuthorizingInterceptor;

@Component(immediate=true)
public class SecurityAnnoProviderRegister implements JaxRsProviderRegister {

    @Override
    public void installProvider(boolean clientSide, List<Object> providers, Set<String> features) {

        if (!clientSide) {
            if (features.contains("appSecurity-2.0") || features.contains("appSecurity-1.0")) {
                //add one built-in ContainerRequestFilter to handle basic security
                SimpleAuthorizingFilter sm = new SimpleAuthorizingFilter();
                LibertySimpleAuthorizingInterceptor in = new LibertySimpleAuthorizingInterceptor();
                sm.setInterceptor(in);
                providers.add(sm);
            }
        }
    }

}
