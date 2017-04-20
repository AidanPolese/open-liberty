// 1.6, 9/30/04
// IBM Confidential OCO Source Material
// 5724-I63, 5724-H88 (C) COPYRIGHT International Business Machines Corp. 1997, 2004
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
package com.ibm.websphere.servlet.cache;


/**
 * The IdGenerator normally is responsible for generating cache 
 * entry ids and data ids, and invalidating data ids. <p>
 * One IdGenerator instance will exist for each cacheable servlet 
 * identified in WebSphere.  When implementing this interface, be
 * aware that multiple threads may be using the same IdGenerator
 * concurrently. 
 * @ibm-api 
 */
public interface IdGenerator {

   /**
    * @deprecated
    * This method is called once on servlet initialization, 
    * and should take configuration values from its CacheConfig
    * argurment and store them locally. Additional config 
    * information from user applications or other sources may be 
    * read here as well.  
    * @ibm-api 
    */
   public void initialize(CacheConfig cc);

   /**
    * This method is called once on every request for a cacheable 
    * servlet. It generates the id that is used as a key by the 
    * cache to identify the output of the servlet.
    *
    * @param request The request object being used for this invocation
    *
    * @return a String uniquely identifying this invocation
    * of a cacheable servlet.
    * @ibm-api 
    */
   public String getId(ServletCacheRequest request);

   /** 
    * @deprecated
    *
    * @return the Sharing Policy of this cache entry
    * @ibm-api 
    */
   public int getSharingPolicy(ServletCacheRequest request);

}
