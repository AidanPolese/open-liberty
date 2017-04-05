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

import javax.security.auth.Subject;

import com.ibm.websphere.ras.annotation.Sensitive;

/**
 * The authentication cache service.
 */
public interface AuthCacheService {

    /**
     * Inserts the subject into the cache.
     * 
     * @param subject
     */
    public void insert(Subject subject);

    /**
     * Inserts the subject into the cache. The userid and password may be used by the BasicAuthCacheKeyProvider
     * to create a key.
     * 
     * @param subject
     * @param userid
     * @param password
     */
    public void insert(Subject subject, String userid, String password);

    /**
     * Gets the subject from the cache using the specified cache key.
     * Only valid subjects are returned. An invalid subject found is immediately removed from the cache.
     * 
     * @param cacheKey
     * @return the valid subject or <code>null</code>.
     */
    public Subject getSubject(@Sensitive Object cacheKey);

    /**
     * Removes the subject specified by the cache key from the cache.
     * 
     * @param cacheKey
     */
    public void remove(@Sensitive Object cacheKey);

    /**
     * Removes all entries from the cache.
     */
    public void removeAllEntries();

}
