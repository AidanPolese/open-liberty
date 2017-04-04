/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 1997, 2011
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.security.token.internal;

import java.security.AccessController;

import javax.security.auth.login.LoginException;

import com.ibm.websphere.security.auth.TokenExpiredException;
import com.ibm.ws.security.token.TokenService;
import com.ibm.wsspi.security.ltpa.Token;
import com.ibm.wsspi.security.token.AttributeNameConstants;
import com.ibm.wsspi.security.token.SingleSignonToken;

public class SingleSignonTokenImpl extends AbstractTokenImpl implements SingleSignonToken {

    private Token token = null;
    private final short version = 2;
    private final TokenService tokenService;

    public SingleSignonTokenImpl(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    public void initializeToken(byte[] ssoToken) throws LoginException {
        initializeToken(ssoToken, false);
    }

    @SuppressWarnings("unchecked")
    public void initializeToken(byte[] ssoToken, boolean refreshIfExpired) throws LoginException {
        try {
            token = null;

            try {
                token = tokenService.recreateTokenFromBytes(ssoToken);
            } catch (TokenExpiredException e) {
                // refresh if needed
            }

            final Token tokenPriv = token;
            AccessController.doPrivileged(new java.security.PrivilegedAction()
            {
                public Object run()
                  {
                      setToken(tokenPriv);
                      return null;
                  }
            });
        } catch (Exception e) {
            throw new LoginException(e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public void initializeToken(final Token token) {
        this.token = token;

        AccessController.doPrivileged(new java.security.PrivilegedAction()
        {
            public Object run()
              {
                  setToken(token);
                  return null;
              }
        });
    }

    /** {@inheritDoc} */
    @Override
    public String getName() {
        return AttributeNameConstants.WSSSOTOKEN_NAME;
    }

    /** {@inheritDoc} */
    @Override
    public short getVersion() {
        return version;
    }

    /** {@inheritDoc} */
    @Override
    public Object clone() {
        SingleSignonToken newToken = new SingleSignonTokenImpl(tokenService);
        token = AccessController.doPrivileged(new java.security.PrivilegedAction<Token>()
            {
                public Token run()
                  {
                      return getToken();
                  }
            });
        ((SingleSignonTokenImpl) newToken).initializeToken((Token) token.clone());
        return newToken;
    }

}
