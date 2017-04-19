//IBM Confidential OCO Source Material
//5724-I63, 5724-H88, 5655-N01, 5733-W60 (C) COPYRIGHT International Business Machines Corp. 1997, 2005
//The source code for this program is not published or otherwise divested
//of its trade secrets, irrespective of what has been deposited with the
//U.S. Copyright Office.
package com.ibm.ws.cache.servlet;

import javax.servlet.Servlet;

public class SingleThreadServletWrapper extends ServletWrapper implements javax.servlet.SingleThreadModel {

   private static final long serialVersionUID = 5840169883288347482L;
    
   public SingleThreadServletWrapper(Servlet s) {
      super(s);
   }

   public Class getProxiedClass() {
      return proxied.getClass();
   }

}
