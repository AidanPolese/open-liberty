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
package com.ibm.ws.security.auth.data.internal;

import java.util.HashMap;
import java.util.Map;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.ComponentContext;

import com.ibm.websphere.security.auth.data.AuthData;
import com.ibm.wsspi.kernel.service.utils.SerializableProtectedString;

public class AuthDataTestHelper {

    private final Mockery mockery;
    private final ComponentContext cc;

    public AuthDataTestHelper(Mockery mockery, ComponentContext cc) {
        this.mockery = mockery;
        this.cc = cc;
    }

    public AuthData createAuthData(String name, String password) {
        AuthDataImpl authDataConfig = new AuthDataImpl();
        Map<String, Object> props = new HashMap<String, Object>();
        props.put(AuthDataImpl.CFG_KEY_USER, name);
        props.put(AuthDataImpl.CFG_KEY_PASSWORD, password == null ? null : new SerializableProtectedString(password.toCharArray()));
        authDataConfig.activate(props);
        return authDataConfig;
    }

    @SuppressWarnings("unchecked")
    public ServiceReference<AuthData> createAuthDataRef(final String authDataAlias, final String displayId, final AuthData authDataConfig) {
        final ServiceReference<AuthData> authDataRef = mockery.mock(ServiceReference.class, authDataAlias + displayId);
        mockery.checking(new Expectations() {
            {
                allowing(authDataRef).getProperty(Constants.SERVICE_ID);
                will(returnValue(0L));
                allowing(authDataRef).getProperty(Constants.SERVICE_RANKING);
                will(returnValue(0));
                allowing(authDataRef).getProperty(AuthDataImpl.CFG_KEY_ID);
                will(returnValue(authDataAlias));
                allowing(authDataRef).getProperty(AuthDataImpl.CFG_KEY_DISPLAY_ID);
                will(returnValue(displayId));
                allowing(cc).locateService("authData", authDataRef);
                will(returnValue(authDataConfig));
            }
        });
        return authDataRef;
    }

}
