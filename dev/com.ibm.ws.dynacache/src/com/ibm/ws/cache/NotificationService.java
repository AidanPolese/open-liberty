// IBM Confidential OCO Source Material
// 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70 (C) COPYRIGHT International Business Machines Corp. 1997, 2007
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
package com.ibm.ws.cache;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.ibm.ws.cache.intf.CacheStatisticsListener;

public interface NotificationService extends Serializable {
   public void start();

   /**
	* This applies a set of invalidations and new entries to this CacheUnit, 
	* including the local internal cache and external caches registered
	* with this CacheUnit.
	* 
	* @param invalidateIdEvents A Vector of invalidate by id events.
	* @param invalidateTemplateEvents A Vector of invalidate by template events.
	* @param pushEntryEvents A Vector of cache entries.
	*/
   public void batchUpdate(HashMap invalidateIdEvents, HashMap invalidateTemplateEvents, ArrayList pushEntryEvents, ArrayList aliasEntryEvents, CacheUnit cacheUnit);  //CCC

   /**
	* This allows a CacheUnit that has just come up to register itself
	* with the other CacheUnits that are already up and running. This
	* allows this CacheUnit to become part of the distributed coordinated
	* cache.
	* 
	* @param name The unique name of the CacheUnit.
	*/
   public void registerCacheUnit(String name, CacheUnit cacheUnit);

   public void setCacheName(String cacheName);
   
   public void setCacheStatisticsListener(CacheStatisticsListener cacheStatisticsListener);

   /**
    * This gets the current size of the pushPullTable for debug use only.
    * 
    * @return the current size of pushPullTable. 
    */
   public int getPushPullTableSize();
   
   /**
    * This gets hashcode for all the cache ids in PushPullTable for debug use only.
    * 
    * @return hashcode for all cache ids. 
    */
   public int getCacheIdsHashcodeInPushPullTable(boolean debug);  // LI4337-17

   /**
    * This gets all the cache ids in PushPullTable for debug use only.
    * 
    * @return array list of cache ids. 
    */
   public List getCacheIdsInPushPullTable();  // LI4337-17
}
