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
package com.ibm.ws.security.authentication.internal;

import java.util.Iterator;
import java.util.Set;

import javax.security.auth.Subject;

import com.ibm.wsspi.security.token.SingleSignonToken;

/**
 *
 */
public class SSOTokenHelper {
    /**
     * Gets the SSO token from the subject.
     * 
     * @param subject {@code null} is not supported.
     * @return
     */
    public static SingleSignonToken getSSOToken(Subject subject) {
        SingleSignonToken ssoToken = null;
        Set<SingleSignonToken> ssoTokens = subject.getPrivateCredentials(SingleSignonToken.class);
        Iterator<SingleSignonToken> ssoTokensIterator = ssoTokens.iterator();
        if (ssoTokensIterator.hasNext()) {
            ssoToken = ssoTokensIterator.next();
        }
        return ssoToken;
    }
}
