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
package com.ibm.ws.security.token;

import java.util.Map;

import com.ibm.websphere.security.auth.InvalidTokenException;
import com.ibm.websphere.security.auth.TokenCreationFailedException;
import com.ibm.websphere.security.auth.TokenExpiredException;
import com.ibm.wsspi.security.ltpa.Token;

/**
 * The implementation of this services creates a token specific to the implementation
 * and recreates a token from the token bytes.
 */
public interface TokenService {

    /**
     * Creates a Token object from the specified token data properties.
     * 
     * @param tokenData
     * @return
     * @throws TokenCreationFailedException
     */
    public Token createToken(Map<String, Object> tokenData) throws TokenCreationFailedException;

    /**
     * Recreates a Token object based on previous token bytes.
     * 
     * @param tokenBytes
     * @return
     */
    public Token recreateTokenFromBytes(byte[] tokenBytes) throws InvalidTokenException, TokenExpiredException;

}
