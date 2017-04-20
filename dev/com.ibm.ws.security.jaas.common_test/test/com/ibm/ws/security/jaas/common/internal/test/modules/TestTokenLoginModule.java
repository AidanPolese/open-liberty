/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2011
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.security.jaas.common.internal.test.modules;

import java.io.IOException;
import java.util.Map;

import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;

import com.ibm.ws.security.jaas.common.callback.TokenCallback;

/**
 *
 */
public class TestTokenLoginModule implements LoginModule {

    private CallbackHandler callbackHandler;
    private boolean succeeded;

    /** {@inheritDoc} */
    @Override
    public void initialize(Subject subject, CallbackHandler callbackHandler, Map<String, ?> arg2, Map<String, ?> arg3) {
        this.callbackHandler = callbackHandler;
    }

    /** {@inheritDoc} */
    @Override
    public boolean login() throws LoginException {
        boolean succeeded = false;
        Callback[] callbacks;
        try {
            callbacks = getCallbacks(callbackHandler);
            byte[] token = ((TokenCallback) callbacks[0]).getToken();
            String username = new String(token);
            if ("testuser".equalsIgnoreCase(username)) {
                succeeded = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return succeeded;
    }

    /** {@inheritDoc} */
    @Override
    public boolean commit() throws LoginException {
        return true;
    }

    /** {@inheritDoc} */
    @Override
    public boolean abort() throws LoginException {
        // TODO Auto-generated method stub
        return false;
    }

    /** {@inheritDoc} */
    @Override
    public boolean logout() throws LoginException {
        // TODO Auto-generated method stub
        return false;
    }

    private Callback[] getCallbacks(CallbackHandler callbackHandler) throws IOException, UnsupportedCallbackException {
        Callback[] callbacks = new Callback[1];
        callbacks[0] = new TokenCallback();

        callbackHandler.handle(callbacks);
        return callbacks;
    }
}