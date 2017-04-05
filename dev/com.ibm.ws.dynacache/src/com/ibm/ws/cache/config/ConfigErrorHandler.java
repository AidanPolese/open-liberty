
// IBM Confidential OCO Source Material
// 5724-I63, 5724-H88 (C) COPYRIGHT International Business Machines Corp. 1997, 2004
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
package com.ibm.ws.cache.config;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXParseException;

import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.ffdc.FFDCFilter;


public class ConfigErrorHandler implements ErrorHandler {

   private static TraceComponent tc = Tr.register(ConfigErrorHandler.class, "WebSphere Dynamic Cache", "com.ibm.ws.cache.resources.dynacache");
   private URI _url;

   public ConfigErrorHandler(URL url) {
      try {
		_url = url.toURI();
	} catch (URISyntaxException e) {
		FFDCFilter.processException(e, this.getClass().getName() + "<init>", "30", this);
	}
   }

   public void error(SAXParseException exception) {
      String filename = exception.getSystemId();
      if (filename == null && _url != null)
         filename = _url.toString();
      String msg = exception.getMessage();
      String line = Integer.toString(exception.getLineNumber());
      String col = Integer.toString(exception.getColumnNumber());
      Tr.error(tc, "DYNA0044E", new Object[] { msg, filename, line, col });
   }

   public void fatalError(SAXParseException exception) {
      String filename = exception.getSystemId();
      if (filename == null && _url != null)
    	  filename = _url.toString();
      String msg = exception.getMessage();
      String line = Integer.toString(exception.getLineNumber());
      String col = Integer.toString(exception.getColumnNumber());
      Tr.error(tc, "DYNA0045E", new Object[] { msg, filename, line, col });
   }

   public void warning(SAXParseException exception) {
      String filename = exception.getSystemId();
      if (filename == null && _url != null)
    	  filename = _url.toString();
      String msg = exception.getMessage();
      String line = Integer.toString(exception.getLineNumber());
      String col = Integer.toString(exception.getColumnNumber());
      Tr.error(tc, "DYNA0044E", new Object[] { msg, filename, line, col });
   }

}
