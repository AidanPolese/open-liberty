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

import com.ibm.ws.security.mp.jwt.error.MpJwtProcessingException;

public interface MicroProfileJwtConfig {

    /*
     *
     *
     * @return Id of the mpJwt config
     */
    public String getUniqueId();

    /**
     * @return
     */
    //String getSslRef();

    /**
     * @return
     * @throws SocialLoginException
     */
    //SSLContext getSSLContext() throws SocialLoginException;

    /**
     * @return
     * @throws SocialLoginException
     */
    //SSLSocketFactory getSSLSocketFactory() throws SocialLoginException;

    /**
     * @return
     * @throws MpJwtProcessingException
     */
    //HashMap<String, PublicKey> getPublicKeys() throws SocialLoginException;

    //String getJwksUri();

    String getUserNameAttribute();

    String getGroupNameAttribute();

    //public PublicKey getPublicKey() throws SocialLoginException;

    //public PrivateKey getPrivateKey() throws SocialLoginException;

    //public SecretKey getSecretKey() throws SocialLoginException;

    //public String getAlgorithm();

    /**
     * @return
     */
    //String[] getAudience();
    boolean getTokenReuse();

}
