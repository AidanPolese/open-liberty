// 1.2, 9/30/04
// IBM Confidential OCO Source Material
// 5724-I63, 5724-H88 (C) COPYRIGHT International Business Machines Corp. 1997, 2004
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
package com.ibm.ws.cache.servlet;



import com.ibm.ejs.ras.Tr;
import com.ibm.ejs.ras.TraceComponent;

public class ESICacheEntryStats {

   private static final TraceComponent _tc = Tr.register(ESICacheEntryStats.class,"WebSphere Dynamic Cache", "com.ibm.ws.cache.resources.dynacache");
   private String    _cacheId = null;

   public ESICacheEntryStats () {}    

   /**
    * Return the URL of the cache entry.
    * @return The URL of the cache entry.
    */
   public String getCacheId()
   {
      return _cacheId;
   }
   /**
    * Set the URL of the cache entry.
    * @param The URL of the cache entry.
    */
   public void setCacheId (String cacheId)
   {
      _cacheId = cacheId;
      if (_tc.isDebugEnabled()) Tr.debug(_tc, "setCacheId " + cacheId);
   }

   public String toString()
   {
      return _cacheId;
   }
}
