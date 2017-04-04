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
 * Implement this interface in order to provide cache keys to the authentication cache.
 */
public interface CacheKeyProvider {

    /**
     * Provides the cache key to be used by the authentication cache.
     * Optionally, return a Set of objects for returning more than one key. In such case,
     * the authentication cache will use each element in the set as an individual key.
     * 
     * @param cacheContext The cache context provided by the authentication cache.
     * @return the cache key(s).
     */
    public Object provideKey(CacheContext cacheContext);

}
