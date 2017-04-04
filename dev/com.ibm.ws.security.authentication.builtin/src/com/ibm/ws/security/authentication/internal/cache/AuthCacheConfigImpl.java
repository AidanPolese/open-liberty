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
package com.ibm.ws.security.authentication.internal.cache;

import com.ibm.ws.security.authentication.cache.AuthCacheConfig;

/**
 * The configuration used by the AuthCacheServiceImpl.
 */
public class AuthCacheConfigImpl implements AuthCacheConfig {

    private final int initialSize;
    private final int maxSize;
    private final long timeout;
    private final boolean allowBasicAuthLookup;

    /**
     * @param initialSize
     * @param maxSize
     * @param timeout
     * @param allowBasicAuthLookup
     */
    public AuthCacheConfigImpl(int initialSize, int maxSize, long timeout, boolean allowBasicAuthLookup) {
        this.initialSize = initialSize;
        this.maxSize = maxSize;
        this.timeout = timeout;
        this.allowBasicAuthLookup = allowBasicAuthLookup;
    }

    /** {@inheritDoc} */
    @Override
    public int getInitialSize() {
        return initialSize;
    }

    /** {@inheritDoc} */
    @Override
    public int getMaxSize() {
        return maxSize;
    }

    /** {@inheritDoc} */
    @Override
    public long getTimeout() {
        return timeout;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isBasicAuthLookupAllowed() {
        return allowBasicAuthLookup;
    }

}
