// %I, %G
// IBM Confidential OCO Source Material
// 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70 (C) COPYRIGHT International Business Machines Corp. 1997, 2007
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
package com.ibm.wsspi.cache;

import java.util.Map;

/**
 * This interface is used by the WebSphere Admin Console to look
 * up all the cache providers and expose them on the Dynamic 
 * Cache Service panel, Cache Provider drop down menu.
 * 
 * @author Rohit
 * @private
 * @since WAS7.0.0
 * @ibm-spi
 * 
 */
public interface CacheProviderLoader {

	/**
	 * Returns an individual CacheProvider successfully
	 * loaded by Dynacache runtime
	 */
   	public CacheProvider getCacheProvider(String name); 

   	/**
   	 * Returns a map of cache provider names to {@link CacheProvider}
   	 * This map is used by the admin console to flush out the 
   	 * cache provider drop down menu.
   	 */
	public Map<String, CacheProvider> getCacheProviders();
	
}
