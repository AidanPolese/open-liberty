/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2011, 2016
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.security.authentication.internal.cache.keyproviders;

import java.security.AccessController;
import java.security.PrivilegedAction;

import javax.security.auth.Subject;

import com.ibm.ws.common.internal.encoder.Base64Coder;
import com.ibm.ws.security.authentication.cache.CacheContext;
import com.ibm.ws.security.authentication.cache.CacheKeyProvider;
import com.ibm.ws.security.authentication.internal.SSOTokenHelper;
import com.ibm.wsspi.security.token.SingleSignonToken;

/**
 * Provides the SSO token bytes as the cache key.
 */
public class SSOTokenBytesCacheKeyProvider implements CacheKeyProvider {

    /** {@inheritDoc} */
    @Override
    public Object provideKey(CacheContext context) {
        return getSingleSignonTokenBytes(context.getSubject());
    }

    private String getSingleSignonTokenBytes(final Subject subject) {
        String base64EncodedSSOTokenBytes = null;
        SingleSignonToken ssoToken = AccessController.doPrivileged(new PrivilegedAction<SingleSignonToken>() {

            @Override
            public SingleSignonToken run() {
                return SSOTokenHelper.getSSOToken(subject);
            }
        });
        if (ssoToken != null) {
            base64EncodedSSOTokenBytes = Base64Coder.toString(Base64Coder.base64Encode(ssoToken.getBytes()));
        }
        return base64EncodedSSOTokenBytes;
    }
}
