// IBM Confidential OCO Source Material
// 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70 (C) COPYRIGHT International Business Machines Corp. 1997, 2008
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
package com.ibm.ws.cache;

import java.util.ArrayList;
import java.util.Properties;

import com.ibm.wsspi.library.Library;

public interface CacheService {

    public CacheConfig getCacheConfig();

    public String getCacheName();

    public void addCacheInstanceConfig(CacheConfig cacheconfig, boolean create) throws Exception;

    public CacheConfig addCacheInstanceConfig(Properties properties);

    public CacheConfig getCacheInstanceConfig(String reference);

    public void destroyCacheInstance(String reference);

    public ArrayList getServletCacheInstanceNames();

    public ArrayList getObjectCacheInstanceNames();

    public CacheInstanceInfo[] getCacheInstanceInfo();

    public Library getSharedLibrary();
}
