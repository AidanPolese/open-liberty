/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2015
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
/*
 * Some of the code was derived from code supplied by the Apache Software Foundation licensed under the Apache License, Version 2.0.
 */
package com.ibm.ws.transport.iiop.security;

import javax.security.auth.Destroyable;
import javax.security.auth.DestroyFailedException;


/**
 * @version $Revision: 451417 $ $Date: 2006-09-29 13:13:22 -0700 (Fri, 29 Sep 2006) $
 */
public class FinalContextToken implements Destroyable {

    private byte[] token;

    public FinalContextToken(byte[] token) {
        this.token = new byte[token.length];
        System.arraycopy(token, 0, this.token, 0, token.length);
    }

    public byte[] getToken() {
        return token;
    }

    public void destroy() throws DestroyFailedException {
        for (int i=0; i<token.length; i++) {
            token[i] = 0;
        }
        token = null;
    }

    public boolean isDestroyed() {
        return token == null;
    }
}
