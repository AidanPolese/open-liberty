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
package com.ibm.ws.webcontainer.security.openidconnect;

import java.security.PrivateKey;
import java.security.PublicKey;

/**
 *
 */
public interface JSONWebKey {

    public abstract String getKeyID();

    public abstract String getKeyX5t();

    public abstract String getAlgorithm();

    public abstract String getKeyUse();

    public abstract String getKeyType();

    public abstract PublicKey getPublicKey();

    public abstract PrivateKey getPrivateKey();

    public abstract byte[] getSharedKey();

    public abstract long getCreated();

}