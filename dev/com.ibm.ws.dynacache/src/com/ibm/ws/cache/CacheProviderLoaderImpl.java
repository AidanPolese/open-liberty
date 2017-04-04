// 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70 (C) COPYRIGHT International Business Machines Corp. 1997, 2010
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
package com.ibm.ws.cache;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.wsspi.cache.CacheProvider;
import com.ibm.wsspi.cache.CacheProviderLoader;

/**
 * Loads the CacheProviders configured as eclipse plugins using eclipse extension registry pattern or placed in the WAS
 * lib dir. using the WAS ExtClassloader.
 */
public class CacheProviderLoaderImpl implements CacheProviderLoader {

    private static TraceComponent tc = Tr.register(CacheProviderLoaderImpl.class, "WebSphere Dynamic Cache", "com.ibm.ws.cache.resources.dynacache");

    private List<CacheServiceImpl> cacheServices = new LinkedList<CacheServiceImpl>();

    // singleton design pattern
    static CacheProviderLoaderImpl cacheLoader = null;

    private CacheProviderLoaderImpl() {
    }

    public static synchronized CacheProviderLoaderImpl getInstance() {
        if (null == cacheLoader) {
            cacheLoader = new CacheProviderLoaderImpl();
        }
        return cacheLoader;
    }

    @Override
    public CacheProvider getCacheProvider(String name) {
        synchronized (cacheServices) {
            for (CacheServiceImpl csi : cacheServices) {
                if (csi.getCacheConfig().getCacheProviderName().equals(name)) {
                    return csi.getCacheProvider();
                }
            }
        }

        return null;
    }

    @Override
    public Map<String, CacheProvider> getCacheProviders() {
        HashMap<String, CacheProvider> providers = new HashMap<String, CacheProvider>(cacheServices.size());
        synchronized (cacheServices) {
            for (CacheServiceImpl csi : cacheServices) {
                providers.put(csi.getCacheConfig().getCacheProviderName(), csi.getCacheProvider());
            }
        }

        return providers;
    }

    public void addCacheProvider(CacheServiceImpl cacheServiceImpl) {
        synchronized (cacheServices) {
            cacheServices.add(cacheServiceImpl);
        }
    }

    public void removeCacheProvider(CacheServiceImpl cacheServiceImpl) {
        synchronized (cacheServices) {
            cacheServices.remove(cacheServiceImpl);
        }
    }
}
