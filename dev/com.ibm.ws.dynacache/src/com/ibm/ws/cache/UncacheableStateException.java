// 1.6, 2/10/05
//IBM Confidential OCO Source Material
//5724-I63, 5724-H88, 5655-N01, 5733-W60 (C) COPYRIGHT International Business Machines Corp. 1997, 2005
//The source code for this program is not published or otherwise divested
//of its trade secrets, irrespective of what has been deposited with the
//U.S. Copyright Office.
package com.ibm.ws.cache;


public class UncacheableStateException extends Exception {
   private static final long serialVersionUID = 852878369455272764L;
    
   protected String uri = null;
   protected String parentURI = null;

   public UncacheableStateException(String message, String uri) {
      super(message);
      this.uri = uri;
   }

   public String getURI() {
      return uri;
   }

   public String getParentURI() {
      return parentURI;
   }

   public void setParentURI(String parentURI) {
      this.parentURI = parentURI;
   }
}
