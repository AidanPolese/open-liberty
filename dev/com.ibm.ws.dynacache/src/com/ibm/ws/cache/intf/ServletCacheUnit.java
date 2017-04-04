// IBM Confidential OCO Source Material
// 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70 (C) COPYRIGHT International Business Machines Corp. 1997, 2007
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
package com.ibm.ws.cache.intf;

import java.util.HashMap;

/**
 * This interface is used by CacheUnitImpl so that it can access some methods defined in ServeltCacheUnitImpl. 
 */
public interface ServletCacheUnit {

    /**
     * This is called to get Command Cache object.
     *
     * @param cacheName The cache name
     * @return CommandCache object
     */
    public CommandCache getCommandCache(String cacheName);
    
    /**
     * This is called to get JSP Cache object.
     *
     * @param cacheName The cache name
     * @return JSPCache object
     */
    public JSPCache getJSPCache(String cacheName);
    
    /**
     * This is delegated to the ExternalCacheServices.  
     *
     * @param groupId The external cache group id.
     * @param address The IP address of the target external cache.
     * @param beanName The bean name (bean instance or class) of
     * the ExternalCacheAdaptor that can deal with the protocol of the
     * target external cache.
     */
    public void addExternalCacheAdapter(String groupId, String address, String beanName);
    
    /**
     * This is delegated to the ExternalCacheServices.
     *
     * @param groupId The external cache group id.
     * @param address The IP address of the target external cache.
     */
    public void removeExternalCacheAdapter(String groupId, String address);
    
    /**
     * It applies the updates to the external caches.
     * It validates timestamps to prevent race conditions.
     *
     * @param invalidateIdEvents A HashMap of invalidate by id.
     * @param invalidateTemplateEvents A HashMap of invalidate by template.
     */
    public void invalidateExternalCaches(HashMap invalidateIdEvents, HashMap invalidateTemplateEvents);
    
    /**
     * Drop all state for that cache and reinitialize the ServletCacheUnit
     */
    public void purgeState(String cacheName);

    /**
     * Purge state for ALL servlet caches
     */
    public void purgeState();
    
    /**
     * Additional initialization when the default cache instance is created
     */
    public void createBaseCache();
    
}      
   
  
