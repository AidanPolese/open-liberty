// 1.9, 9/30/04
// IBM Confidential OCO Source Material
// 5724-I63, 5724-H88 (C) COPYRIGHT International Business Machines Corp. 1997, 2004
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
package com.ibm.websphere.servlet.cache;

/**
 * 
 * 
 * @ibm-api 
 * @see com.ibm.websphere.cache.DynamicCacheAccessor
 * @deprecated 
 *             You should use com.ibm.websphere.cache.DynamicCacheAccessor
 * @ibm-api 
 */
public class DynamicCacheAccessor {


/**
 * This obtains a reference to the dynamic cache.
 * @return Reference to the cache or null if caching is disabled 
 * @deprecated 
 *             You should use com.ibm.websphere.cache.DynamicCacheAccessor
 * @ibm-api 
 */
   public static com.ibm.websphere.cache.Cache getCache() {
      return (com.ibm.websphere.cache.Cache) com.ibm.websphere.cache.DynamicCacheAccessor.getCache();
   }

/**
 * This determines if caching is enabled.
 * @return true if caching is enabled, false if it is disabled.
 * @deprecated 
 *             You should use com.ibm.websphere.cache.DynamicCacheAccessor
 * @ibm-api 
 */
   public static boolean isCachingEnabled() {
      return com.ibm.websphere.cache.DynamicCacheAccessor.isCachingEnabled();
   }


}
