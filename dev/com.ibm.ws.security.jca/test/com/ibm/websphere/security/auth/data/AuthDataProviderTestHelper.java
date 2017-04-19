/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2016
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.websphere.security.auth.data;

import org.osgi.framework.ServiceReference;
import org.osgi.service.component.ComponentContext;

public class AuthDataProviderTestHelper {

    private final AuthDataProvider authDataProvider;
    private final ComponentContext cc;

    public AuthDataProviderTestHelper(AuthDataProvider authDataProvider, ComponentContext cc) {
        this.authDataProvider = authDataProvider;
        this.cc = cc;
    }

    public void setAuthData(ServiceReference<AuthData> authDataRef) {
        authDataProvider.setAuthData(authDataRef);
    }

    public void unsetAuthData(ServiceReference<AuthData> authDataRef) {
        authDataProvider.unsetAuthData(authDataRef);
    }

    public void activate() {
        authDataProvider.activate(cc);
    }

    public void deactivate() {
        authDataProvider.deactivate(cc);
    }
}
