/*******************************************************************************
 * Copyright (c) 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.ibm.ws.jaxrs20.providers.security;

import java.util.List;
import java.util.Set;

import org.apache.cxf.jaxrs.security.SimpleAuthorizingFilter;

import com.ibm.ws.jaxrs20.providers.api.JaxRsProviderRegister;
import com.ibm.ws.jaxrs20.security.LibertySimpleAuthorizingInterceptor;

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
