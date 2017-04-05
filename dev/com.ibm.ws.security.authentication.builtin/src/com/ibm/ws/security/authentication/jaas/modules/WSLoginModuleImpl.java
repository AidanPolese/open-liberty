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
package com.ibm.ws.security.authentication.jaas.modules;

import java.io.IOException;

import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;

import com.ibm.ws.security.authentication.AuthenticationService;
import com.ibm.ws.security.authentication.internal.jaas.modules.ServerCommonLoginModule;
import com.ibm.ws.security.authentication.utility.JaasLoginConfigConstants;

/**
 * This login module calls the authentication service to authenticate
 */
public class WSLoginModuleImpl extends ServerCommonLoginModule implements LoginModule {
    //We have no plan to public this option
    private static final String KEY_JAAS_LOGIN_CONTEXT_ENTRY_NAME = "jaasLoginContextEntryName";

    @Override
    public boolean login() throws LoginException {
        String jaasEntryName = null;
        if (options != null) {
            jaasEntryName = (String) options.get(KEY_JAAS_LOGIN_CONTEXT_ENTRY_NAME);
        }
        if (jaasEntryName == null)
            jaasEntryName = JaasLoginConfigConstants.SYSTEM_DEFAULT;

        AuthenticationService authenticationService = getAuthenticationService();
        if (authenticationService == null) {
            throw new LoginException("An internal error occured. Unable to get authenticate service.");
        }
        Subject authSubj = authenticationService.authenticate(jaasEntryName, callbackHandler, subject);

        setUpSubject(authSubj);

        setAlreadyProcessed();

        return true;
    }

    /** {@inheritDoc} */
    @Override
    public Callback[] getRequiredCallbacks(CallbackHandler callbackHandler) throws IOException, UnsupportedCallbackException {
        return null;
    }
}
