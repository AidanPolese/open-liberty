// 1.2, 10/8/07
// IBM Confidential OCO Source Material
// 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70 (C) COPYRIGHT International Business Machines Corp. 1997, 2007
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
package com.ibm.ws.cache.intf;

import com.ibm.wsspi.cache.EventSource;

/**
 * This interface is used by CacheUnitImpl so that it can access some methods defined in ObjectCacheUnitImpl. 
 */
public interface ObjectCacheUnit  {

    /**
     * This is called to create object cache.
     *
     * @param reference The cache name
     */
	public Object createObjectCache(String reference);
    
    /**
     * This is called to create event source object.
     *
     * @param createAsyncEventSource boolean true - using async thread context for callback; false - using caller thread for callback
     * @param cacheName The cache name
     * @param cacheNameNonPrefix The non-prefix cache name
     * @return EventSourceIntf The event source
     */
	public EventSource createEventSource(boolean createAsyncEventSource, String cacheName);
}      
