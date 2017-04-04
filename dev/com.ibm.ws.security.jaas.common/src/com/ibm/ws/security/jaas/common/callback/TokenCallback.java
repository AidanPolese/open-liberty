/*
 *
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
package com.ibm.ws.security.jaas.common.callback;

import java.io.Serializable;

import javax.security.auth.callback.Callback;

import com.ibm.websphere.ras.annotation.Sensitive;

/**
*
*/
public class TokenCallback implements Callback, Serializable {
    private static final long serialVersionUID = 1L;
    private byte[] credToken;

    public TokenCallback() {
        super();
    }

    public void setToken(@Sensitive byte[] token) {
        this.credToken = AuthenticationHelper.copyCredToken(token);
    }

    public @Sensitive
    byte[] getToken() {
        return AuthenticationHelper.copyCredToken(credToken);
    }

}
