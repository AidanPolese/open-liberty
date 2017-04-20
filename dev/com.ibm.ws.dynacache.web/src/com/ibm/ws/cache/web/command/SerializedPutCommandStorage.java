// 1.8, 2/27/12
// IBM Confidential OCO Source Material
// 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70 (C) COPYRIGHT International Business Machines Corp. 1997, 2012
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
package com.ibm.ws.cache.web.command;

import java.io.*;

import com.ibm.websphere.command.CacheableCommand;
import com.ibm.ws.cache.intf.DCache;
import com.ibm.ws.cache.DCacheBase;
import com.ibm.ws.cache.DynaCacheConstants;
import com.ibm.ws.cache.EntryInfo;

/**
 * It uses serialization to copy the command on put into the cache
 */
public class SerializedPutCommandStorage extends SerializedCommandStorage {
   
   private static final long serialVersionUID = 2356937049967292797L;
    
   /**
    * This implements the method in the CommandStoragePolicy interface.
    *
    * @param object The cached representation of the command.
    * @return The command that is given out during a cache hit.
    */
   public CacheableCommand prepareForCacheAccess(Serializable inputObject, DCache cache, EntryInfo ei) {
      if (inputObject instanceof byte[]) {
         //if it is still in byte[] format, deserialize and put back into
         // the cache so next cache hit does not require deserialize
         CacheableCommand cc = super.prepareForCacheAccess(inputObject,cache,ei);
         if ( cc != null ) {
        	 EntryInfo nei = (com.ibm.ws.cache.EntryInfo)cc.getEntryInfo();
        	 if (nei != null) {
        		 cache.setValue(nei, cc, !DCacheBase.COORDINATE, DynaCacheConstants.VBC_CACHE_NEW_CONTENT);
        	 }
        	 else { 
        		 cache.setValue(ei, cc, !DCacheBase.COORDINATE, DynaCacheConstants.VBC_CACHE_NEW_CONTENT);
        	 }
         }
         return cc;
      }
      return (CacheableCommand) inputObject;
   }
}
