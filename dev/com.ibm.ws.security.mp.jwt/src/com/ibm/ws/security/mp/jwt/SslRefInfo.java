/*
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2016, 2017
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
package com.ibm.ws.security.mp.jwt;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.HashMap;

import javax.crypto.SecretKey;

import com.ibm.ws.security.mp.jwt.error.MpJwtProcessingException;

public interface SslRefInfo {

    String getTrustStoreName() throws MpJwtProcessingException;

    String getKeyStoreName() throws MpJwtProcessingException;

    /**
     * @return
     * @throws MpJwtProcessingException
     */
    HashMap<String, PublicKey> getPublicKeys() throws MpJwtProcessingException;

    /**
     * @return public key.
     * @throws MpJwtProcessingException
     */
    PublicKey getPublicKey() throws MpJwtProcessingException;

    /**
     * @return private key.
     * @throws MpJwtProcessingException
     */
    PrivateKey getPrivateKey() throws MpJwtProcessingException;

    /**
     * @return secret key.
     * @throws MpJwtProcessingException
     */
    SecretKey getSecretKey() throws MpJwtProcessingException;

}
