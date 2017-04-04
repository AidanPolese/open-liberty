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
package com.ibm.ws.security.jaas.common.internal.callback;

import java.security.cert.X509Certificate;
import java.util.List;
import java.util.Map;

import javax.security.auth.callback.CallbackHandler;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ibm.websphere.ras.annotation.Sensitive;
import com.ibm.ws.security.authentication.AuthenticationData;
import com.ibm.ws.security.authentication.WSAuthenticationData;
import com.ibm.ws.security.jaas.common.callback.AuthenticationDataCallbackHandler;
import com.ibm.wsspi.security.auth.callback.WSCallbackHandlerFactory;

/**
 *
 */
public class WSCallbackHandlerFactoryImpl extends WSCallbackHandlerFactory {

    /** {@inheritDoc} */
    @Override
    public CallbackHandler getCallbackHandler(String userName, @Sensitive String password) {
        AuthenticationData authenticationData = createBasicAuthenticationData(userName, password);
        return new AuthenticationDataCallbackHandler(authenticationData);
    }

    /** {@inheritDoc} */
    @Override
    public CallbackHandler getCallbackHandler(String userName, String realmName, @Sensitive String password) {
        AuthenticationData authenticationData = createBasicAuthenticationData(userName, password);
        authenticationData.set(AuthenticationData.REALM, realmName);
        return new AuthenticationDataCallbackHandler(authenticationData);
    }

    /** {@inheritDoc} */
    @Override
    public CallbackHandler getCallbackHandler(String userName, String realmName, String ccacheFile, String defaultCcache) {
        // TODO Auto-generated method stub
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public CallbackHandler getCallbackHandler(String userName, String realmName, @Sensitive String password, List tokenHolderList) {
        // TODO Auto-generated method stub
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public CallbackHandler getCallbackHandler(String userName, String realmName, @Sensitive String password, HttpServletRequest req, HttpServletResponse resp, Map appContext) {
        AuthenticationData authenticationData = createBasicAuthenticationData(userName, password);
        authenticationData.set(AuthenticationData.REALM, realmName);
        authenticationData.set(AuthenticationData.HTTP_SERVLET_REQUEST, req);
        authenticationData.set(AuthenticationData.HTTP_SERVLET_RESPONSE, resp);
        authenticationData.set(AuthenticationData.APPLICATION_CONTEXT, appContext);
        return new AuthenticationDataCallbackHandler(authenticationData);
    }

    /** {@inheritDoc} */
    @Override
    public CallbackHandler getCallbackHandler(String userName, String realmName, List tokenHolderList) {
        // TODO Auto-generated method stub
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public CallbackHandler getCallbackHandler(String userName, String realmName, List tokenHolderList, Map appContext) {
        // TODO Auto-generated method stub
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public CallbackHandler getCallbackHandler(String realmName, X509Certificate[] certChain) {
        AuthenticationData authenticationData = new WSAuthenticationData();
        authenticationData.set(AuthenticationData.REALM, realmName);
        authenticationData.set(AuthenticationData.CERTCHAIN, certChain);
        return new AuthenticationDataCallbackHandler(authenticationData);
    }

    /** {@inheritDoc} */
    @Override
    public CallbackHandler getCallbackHandler(String realmName, X509Certificate[] certChain, List tokenHolderList) {
        // TODO Auto-generated method stub
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public CallbackHandler getCallbackHandler(String realmName, X509Certificate[] certChain, HttpServletRequest req, HttpServletResponse resp, Map appContext) {
        AuthenticationData authenticationData = new WSAuthenticationData();
        authenticationData.set(AuthenticationData.REALM, realmName);
        authenticationData.set(AuthenticationData.CERTCHAIN, certChain);
        authenticationData.set(AuthenticationData.HTTP_SERVLET_REQUEST, req);
        authenticationData.set(AuthenticationData.HTTP_SERVLET_RESPONSE, resp);
        authenticationData.set(AuthenticationData.APPLICATION_CONTEXT, appContext);
        return new AuthenticationDataCallbackHandler(authenticationData);
    }

    /** {@inheritDoc} */
    @Override
    public CallbackHandler getCallbackHandler(byte[] credToken) {
        AuthenticationData authenticationData = new WSAuthenticationData();
        authenticationData.set(AuthenticationData.TOKEN, credToken);
        return new AuthenticationDataCallbackHandler(authenticationData);
    }

    /** {@inheritDoc} */
    @Override
    public CallbackHandler getCallbackHandler(byte[] credToken, String authMechOid) {
        AuthenticationData authenticationData = new WSAuthenticationData();
        authenticationData.set(AuthenticationData.TOKEN, credToken);
        authenticationData.set(AuthenticationData.AUTHENTICATION_MECH_OID, authMechOid);
        return new AuthenticationDataCallbackHandler(authenticationData);
    }

    /** {@inheritDoc} */
    @Override
    public CallbackHandler getCallbackHandler(byte[] credToken, List tokenHolderList) {
        // TODO Auto-generated method stub
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public CallbackHandler getCallbackHandler(byte[] credToken, List tokenHolderList, String authMechOid) {
        // TODO Auto-generated method stub
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public CallbackHandler getCallbackHandler(byte[] credToken, HttpServletRequest req, HttpServletResponse resp, List tokenHolderList, Map appContext, String authMechOid) {
        // TODO Auto-generated method stub
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public CallbackHandler getCallbackHandler(byte[] credToken, HttpServletRequest req, HttpServletResponse resp, Map appContext) {
        // TODO Auto-generated method stub
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public CallbackHandler getCallbackHandler(byte[] credToken, HttpServletRequest req, HttpServletResponse resp, Map appContext, List tokenHolderList) {
        // TODO Auto-generated method stub
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public CallbackHandler getCallbackHandler(Object protocolPolicy) {
        // TODO Auto-generated method stub
        return null;
    }

    private AuthenticationData createBasicAuthenticationData(String userName, @Sensitive String password) {
        AuthenticationData authenticationData = new WSAuthenticationData();
        authenticationData.set(AuthenticationData.USERNAME, userName);
        authenticationData.set(AuthenticationData.PASSWORD, password);
        return authenticationData;
    }

}
