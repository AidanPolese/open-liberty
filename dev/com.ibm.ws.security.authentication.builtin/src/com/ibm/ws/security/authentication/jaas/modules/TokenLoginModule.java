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
package com.ibm.ws.security.authentication.jaas.modules;

import java.io.IOException;

import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;

import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.websphere.security.auth.InvalidTokenException;
import com.ibm.websphere.security.auth.TokenExpiredException;
import com.ibm.ws.ffdc.annotation.FFDCIgnore;
import com.ibm.ws.security.AccessIdUtil;
import com.ibm.ws.security.authentication.AuthenticationException;
import com.ibm.ws.security.authentication.internal.jaas.modules.ServerCommonLoginModule;
import com.ibm.ws.security.authentication.principals.WSPrincipal;
import com.ibm.ws.security.jaas.common.callback.AuthenticationHelper;
import com.ibm.ws.security.jaas.common.callback.TokenCallback;
import com.ibm.ws.security.registry.UserRegistry;
import com.ibm.ws.security.token.TokenManager;
import com.ibm.wsspi.security.ltpa.Token;

/**
 * Handles token based authentication, such as Single Sign-on.
 */
public class TokenLoginModule extends ServerCommonLoginModule implements LoginModule {

    private static final TraceComponent tc = Tr.register(TokenLoginModule.class);
    private String accessId = null;
    private Token recreatedToken;

    /** {@inheritDoc} */
    @Override
    @FFDCIgnore({ InvalidTokenException.class, TokenExpiredException.class })
    public boolean login() throws LoginException {
        if (isAlreadyProcessed()) {
            if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
                Tr.debug(tc, "Already processed by other login module, abstaining.");
            }
            return false;
        }

        try {
            Callback[] callbacks = getRequiredCallbacks(callbackHandler);
            byte[] token = ((TokenCallback) callbacks[0]).getToken();

            // If we have insufficient data, abstain.
            if (token == null) {
                return false;
            }

            setAlreadyProcessed();

            byte[] credToken = AuthenticationHelper.copyCredToken(token);
            TokenManager tokenManager = getTokenManager();
            recreatedToken = tokenManager.recreateTokenFromBytes(credToken);
            accessId = recreatedToken.getAttributes("u")[0];
            if (AccessIdUtil.isServerAccessId(accessId)) {
                setUpTemporaryServerSubject();
            } else {
                setUpTemporaryUserSubject();
            }
            updateSharedState();
            return true;
        } catch (InvalidTokenException e) {
            throw new AuthenticationException(e.getLocalizedMessage(), e);
        } catch (TokenExpiredException e) {
            throw new AuthenticationException(e.getLocalizedMessage(), e);
        } catch (Exception e) {
            throw new AuthenticationException(e.getLocalizedMessage(), e);
        }
    }

    /**
     * Gets the required Callback objects needed by this login module.
     * 
     * @param callbackHandler
     * @return
     * @throws IOException
     * @throws UnsupportedCallbackException
     */
    @Override
    public Callback[] getRequiredCallbacks(CallbackHandler callbackHandler) throws IOException, UnsupportedCallbackException {
        Callback[] callbacks = new Callback[1];
        callbacks[0] = new TokenCallback();
        callbackHandler.handle(callbacks);
        return callbacks;
    }

    /**
     * @see #setUpTemporaryUserSubject()
     * @throws Exception
     */
    private void setUpTemporaryServerSubject() throws Exception {
        temporarySubject = new Subject();
        temporarySubject.getPrivateCredentials().add(recreatedToken);
        String securityName = AccessIdUtil.getUniqueId(accessId);
        setPrincipalAndCredentials(temporarySubject, securityName, null, accessId, WSPrincipal.AUTH_METHOD_TOKEN);
    }

    /**
     * Populate a temporary subject in response to a successful authentication.
     * We use a temporary Subject because if something goes wrong in this flow,
     * we are not updating the "live" Subject. If performance is a problem, it may
     * be necessary to create a placeholder instead of a subject and modify the credentials
     * service to return a set of credentials or update the holder in order to place in
     * the shared state.
     * 
     * @throws Exception
     */
    private void setUpTemporaryUserSubject() throws Exception {
        temporarySubject = new Subject();
        temporarySubject.getPrivateCredentials().add(recreatedToken);
        UserRegistry ur = getUserRegistry();
        String securityName = ur.getUserSecurityName(AccessIdUtil.getUniqueId(accessId));
        securityName = getSecurityName(securityName, securityName); // Special handling for LDAP under here.
        setPrincipalAndCredentials(temporarySubject, securityName, null, accessId, WSPrincipal.AUTH_METHOD_TOKEN);
    }

    /** {@inheritDoc} */
    @Override
    public boolean commit() throws LoginException {
        if (accessId == null) {
            if (TraceComponent.isAnyTracingEnabled() && tc.isEventEnabled())
                Tr.event(tc, "Authentication did not occur for this login module, abstaining.");
            return false;
        }
        setUpSubject();
        return true;
    }

    /** {@inheritDoc} */
    @Override
    public boolean abort() {
        cleanUpSubject();
        accessId = null;
        return true;
    }

    /** {@inheritDoc} */
    @Override
    public boolean logout() {
        cleanUpSubject();
        accessId = null;
        return true;
    }
}
