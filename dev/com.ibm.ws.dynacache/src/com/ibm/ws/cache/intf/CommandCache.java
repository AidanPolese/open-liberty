// 1.2, 10/8/07
// IBM Confidential OCO Source Material
// 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70 (C) COPYRIGHT International Business Machines Corp. 1997, 2007
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
package com.ibm.ws.cache.intf;

/**
 * This is the underlying CommandCache mechanism which is
 * used by the ServerCache and ServletCacheUnitimpl.
 */
public interface CommandCache {
	
	/**
	 * Sets the Cache for the command cache. It is called by the ServletCacheUnitImpl when Dynamic cache servlet service gets started.
	 * @param cache The Cache.
	 */
	public void setCache(DCache cache);
	
}
