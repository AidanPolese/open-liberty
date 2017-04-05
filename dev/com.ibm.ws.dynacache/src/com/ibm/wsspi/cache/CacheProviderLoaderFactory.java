// %I, %G
// IBM Confidential OCO Source Material
// 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70 (C) COPYRIGHT International Business Machines Corp. 1997, 2007
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
package com.ibm.wsspi.cache;

import com.ibm.ws.cache.CacheProviderLoaderImpl;

/**
 * This factory returns a singleton instance of the 
 * {@link CacheProviderLoader} interface.
 * 
 */
public class CacheProviderLoaderFactory {

	public static CacheProviderLoader getCacheProviderLoader(){		
		return CacheProviderLoaderImpl.getInstance();		
	}	
}
