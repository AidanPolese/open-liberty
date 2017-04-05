// IBM Confidential OCO Source Material
// 5724-I63, 5724-H88, 5655-N01, 5733-W61 (C) COPYRIGHT International Business Machines Corp. 2013
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
package com.ibm.ws.cache.stat;

import com.ibm.ws.cache.intf.DCache;

public interface CachePerfFactory {
    
    /**
     * @param cacheName
     * @param swapToDisk
     * @return a CachePerf for the given cache, or null if no performance stats
     */
    CachePerf create(DCache cache);
}
