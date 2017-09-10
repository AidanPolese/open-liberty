/*******************************************************************************
 * Copyright (c) 2017 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
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

}
