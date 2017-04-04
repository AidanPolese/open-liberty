/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2013
 *
 * The source code for this program is not published or other-
 * wise divested of its trade secrets, irrespective of what has
 * been deposited with the U.S. Copyright Office.
 */
package com.ibm.ws.cache;

import org.osgi.service.component.annotations.Component;

import com.ibm.wsspi.cache.CacheFeatureSupport;
import com.ibm.wsspi.cache.CacheProvider;
import com.ibm.wsspi.cache.CoreCache;

@Component(service = CacheProvider.class, property = { "name=" + CacheConfig.CACHE_PROVIDER_DYNACACHE, "service.vendor=IBM" })
public class DefaultCacheProvider implements CacheProvider {
    @Override
    public void stop() {

    }

    @Override
    public void start() {

    }

    @Override
    public String getName() {
        return CacheConfig.CACHE_PROVIDER_DYNACACHE;
    }

    @Override
    public CacheFeatureSupport getCacheFeatureSupport() {
        return null;
    }

    @Override
    public CoreCache createCache(com.ibm.wsspi.cache.CacheConfig cacheConfig) {
        return null;
    }
}
