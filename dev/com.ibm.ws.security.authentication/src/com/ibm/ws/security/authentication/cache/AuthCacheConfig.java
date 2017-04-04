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
package com.ibm.ws.security.authentication.cache;

/**
 * The configuration used by the authentication cache service.
 */
public interface AuthCacheConfig {

    /**
     * Gets the cache initial size.
     * 
     * @return
     */
    int getInitialSize();

    /**
     * Gets the cache max size.
     * 
     * @return
     */
    int getMaxSize();

    /**
     * Gets the cache timeout in seconds.
     * 
     * @return
     */
    long getTimeout();

    /**
     * Indicates if lookup by userid and hashed password is allowed.
     * 
     * @return
     */
    boolean isBasicAuthLookupAllowed();

}
