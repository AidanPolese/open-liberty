// 1.10, 4/18/08
// IBM Confidential OCO Source Material
// 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70 (C) COPYRIGHT International Business Machines Corp. 1997, 2008
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
package com.ibm.websphere.servlet.cache;

/**
 * A CacheConfig class holds the configuration for a cache entry.
 * @deprecated
 * @ibm-api 
 */
public interface CacheConfig {

   /**
    * This method returns the class of IdGenerator.
    * 
	* @return The class name 
	*/
   public Class getIdGenerator();
   
   /**
    * This method returns the class of MetaDataGenerator.
    * 
	* @return The class of MetaDataGeneator
	*/
   public Class getMetaDataGenerator();

   /**
    * This method returns the array of URIs.
    * 
	* @return The array of URIs
	*/
   public String[] getURIs() ;               
   
   /**
    * This method returns the array of ConfigElement for request parameters.
    * 
	* @return The array of ConfigElement for request parameters
	*/
   public ConfigElement[] getRequestParameters();
   
   /**
    * This method returns the array of ConfigElement for request attributes.
    * 
	* @return The array of ConfigElement for request attributes
	*/
   public ConfigElement[] getRequestAttributes();
   
   /**
    * This method returns the array of ConfigElement for session parameters.
    * 
	* @return The array of ConfigElement for session parameters
	*/
   public ConfigElement[] getSessionParameters();
   
   /**
    * This method returns the array of ConfigElement for cookies.
    * 
	* @return The array of ConfigElement for cookies
	*/
   public ConfigElement[] getCookies();

   /**
    * This method determines to look for invalidation only.
    *   
	* @return The boolean True to look for invalidation only
	*/
   public boolean getInvalidateonly() ;
   
   /**
    * This method returns the priority.
    * 
	* @return The priority  
	*/
   public int getPriority() ;
   
   /**
    * This method returns the sharing policy.
    * 
	* @return The sharing policy  
	*/
   public int getSharingPolicy();
   
   /**
    * This method returns the external cache. 
    * 
	* @return The external cache  
	*/
   public String getExternalCache() ;
   
   /**
    * This method returns the timeout in seconds.
    * 
	* @return The timeout  
	*/
   public int getTimeout() ;
   
   /**
    * This method returns the inactivity timeout in seconds.
    * 
	* @return The inactivity timeout  
	*/
   public int getInactivity();
   
   /**
    * This method returns the name of cache config.
    * 
	* @return The name of cache config 
	*/
   public String getName() ;
      
}
