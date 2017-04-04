// IBM Confidential OCO Source Material
// 5724-I63, 5724-H88 (C) COPYRIGHT International Business Machines Corp. 1997, 2004
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
package com.ibm.ws.cache;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;

import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;

public class PropertiesEntityResolver implements EntityResolver {

   private static TraceComponent tc = Tr.register(PropertiesEntityResolver.class, "WebSphere Dynamic Cache", "com.ibm.ws.cache.resources.dynacache");
   static String propertiesDir = "";

   public InputSource resolveEntity(String publicId, String systemId) {
      if (tc.isEntryEnabled())
         Tr.entry(tc, "resolveEntity");
      String fileName = null;

      if (systemId.toLowerCase().endsWith("servletcache.dtd")) {
         fileName = "/servletcache.dtd";
      } else if (systemId.toLowerCase().endsWith("dynacache.dtd")) {
         fileName = "/dynacache.dtd";
      }

      if (fileName == null) {
         if (tc.isDebugEnabled())
            Tr.debug(tc, "If this is being used for servlet caching, we shouldn't be here; systemId=" + systemId + ", publicId=" + publicId);

         if (tc.isEntryEnabled())
            Tr.exit(tc, "resolveEntity");
         return null;
      }

      // build the location of the dtd

      String xmlFileLoc = propertiesDir + fileName;

      try {
         FileInputStream in = new FileInputStream(xmlFileLoc);

         if (tc.isDebugEnabled())
            Tr.debug(tc, "InputStrean for " + fileName + " is " + in);
         org.xml.sax.InputSource is = new org.xml.sax.InputSource(in);
         if (tc.isDebugEnabled())
            Tr.debug(tc, "InputSource.getSystemId() = " + is.getSystemId());

         if (tc.isEntryEnabled())
            Tr.exit(tc, "resolveEntity");
         return is;

      } catch (FileNotFoundException e) {
         com.ibm.ws.ffdc.FFDCFilter.processException(e, "com.ibm.ws.cache.PropertiesEntityResolver.resolveEntity", "80", this);
         return null;
      }
   }
}