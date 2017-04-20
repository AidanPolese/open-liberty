// 1.11, 9/27/04
// IBM Confidential OCO Source Material
// 5630-A36 (C) COPYRIGHT International Business Machines Corp. 1997, 2004
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
package com.ibm.ws.cache.servlet;

import javax.servlet.http.HttpServletResponse;

import com.ibm.websphere.servlet.cache.CacheConfig;
import com.ibm.websphere.servlet.cache.MetaDataGenerator;
import com.ibm.websphere.servlet.cache.ServletCacheRequest;

public class DefaultMetaDataGeneratorImpl implements MetaDataGenerator {
   private int timeout=0;
   private int inactivity=0; // CPF-Inactivity
   private int priority=0;
   private String externalCacheId;
   boolean consumeSubfragments;
   boolean doNotConsume;
   
   public void setMetaData(ServletCacheRequest req, HttpServletResponse resp) {
      FragmentInfo fragmentInfo = (FragmentInfo) req.getFragmentInfo();           

      if (timeout!=0)
         fragmentInfo.setTimeLimit(timeout);
      if (inactivity!=0)
         fragmentInfo.setInactivity(inactivity); // CPF-Inactivity
      if (priority!=0)
         fragmentInfo.setPriority(priority);
      if (externalCacheId != null) {
         fragmentInfo.setExternalCacheGroupId(externalCacheId);
      }
      fragmentInfo.setConsumeSubfragments(consumeSubfragments);
      fragmentInfo.setDoNotConsume(doNotConsume);
   }

   public void initialize(CacheConfig cc) {
      timeout = cc.getTimeout();
      inactivity = cc.getInactivity(); // CPF-Inactivity
      priority=cc.getPriority();
      externalCacheId = cc.getExternalCache();
      consumeSubfragments = ((com.ibm.ws.cache.servlet.CacheConfigImpl)cc).getConsumeSubfragments();
      doNotConsume = ((com.ibm.ws.cache.servlet.CacheConfigImpl)cc).getDoNotConsume();
   }

}
