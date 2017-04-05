// IBM Confidential OCO Source Material
// 5724-I63, 5724-H88 (C) COPYRIGHT International Business Machines Corp. 1997, 2004
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
package com.ibm.ws.cache.config;

import java.util.HashMap;
import java.util.Stack;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;


public class RuleHandler extends DefaultHandler {

   protected Stack handlerStack = new Stack();
   protected Stack rulesStack = new Stack();
   protected HashMap rules = new HashMap();
   protected StringBuffer characters = new StringBuffer();

   private static TraceComponent tc = Tr.register(RuleHandler.class, "WebSphere Dynamic Cache", "com.ibm.ws.cache.resources.dynacache");

   public RuleHandler() {
   }

   public void addRule(String name, ElementHandler elemHandler) {
      rules.put(name, elemHandler);
   }

   public void startDocument() {
      //    System.out.println("startDocument");
   }

   public void endDocument() {
      //    System.out.println("endDocument");
   }

   public void startElement(String uri, String name, String qName, Attributes attrs) {
	  
	  if (tc.isDebugEnabled())
		  Tr.entry(tc, "startElement", uri, name, qName, attrs);
	   
	  ElementHandler handler = (ElementHandler) rules.get(qName);
	  if (handler == null) {
         Object o = handlerStack.peek();
         Tr.error(tc, "DYNA0037E", new Object[] { name, o == null ? "root" : o });
         throw new IllegalStateException("DYNA0037E");
      }
      handlerStack.push(handler);
      rulesStack.push(rules);
      rules = new HashMap();
      handler.addRules(this);
      handler.startElement(uri, name, qName, attrs);
      
      if (tc.isDebugEnabled())
    	  Tr.exit(tc, "startElement");
   }

   public void endElement(String uri, String name, String qName) {
      ElementHandler handler = (ElementHandler) handlerStack.pop();
      rules = (HashMap) rulesStack.pop();
      handler.endElement(uri, name, qName);
   }

   public void characters(char chars[], int start, int length) {
      ElementHandler handler = (ElementHandler) handlerStack.peek();
      handler.characters(chars, start, length);
   }

}
