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
 * The context information passed to the CacheKeyProvider objects when prompting them for the cache key.
 * This context may be used by the providers when creating the key.
 */
public class CacheContext {

    private final CacheObject cacheObject;
    private final AuthCacheConfig config;
    private String userid;
    private String password;

    /**
     * @param config
     * @param cacheObject
     */
    public CacheContext(AuthCacheConfig config, CacheObject cacheObject) {
        this.config = config;
        this.cacheObject = cacheObject;
    }

    /**
     * @param cacheObject
     * @param userid
     * @param password
     */
    public CacheContext(AuthCacheConfig config, CacheObject cacheObject, String userid, @Sensitive String password) {
        this(config, cacheObject);
        this.userid = userid;
        this.password = password;
    }

    /**
     * Gets the AuthCacheConfig object.
     * 
     * @return
     */
    public AuthCacheConfig getAuthCacheConfig() {
        return config;
    }

    /**
     * Gets the subject being cached.
     * 
     * @return
     */
    public Subject getSubject() {
        return cacheObject.getSubject();
    }

    /**
     * Gets the userid currently used.
     * 
     * @return
     */
    public String getUserid() {
        return userid;
    }

    /**
     * Gets the password currently used.
     * 
     * @return
     */
    @Sensitive
    public String getPassword() {
        return password;
    }

}
