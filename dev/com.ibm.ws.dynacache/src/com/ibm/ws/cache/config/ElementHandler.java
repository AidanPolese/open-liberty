// 1.4.1.1, 6/5/06
// IBM Confidential OCO Source Material
// 5724-I63, 5724-H88 (C) COPYRIGHT International Business Machines Corp. 1997, 2004
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
package com.ibm.ws.cache.config;

import org.xml.sax.Attributes;

public class ElementHandler {
     protected StringBuffer characters = new StringBuffer();

     public ElementHandler() {
     }

     public void addRules(RuleHandler ruleHandler) {
     }

     public void startElement (String uri, String name,String qName, Attributes attrs) {
     }
     
     public void endElement (String uri,String name, String qName)
     {
        finished();
        characters = new StringBuffer();
     }

     public void finished() {
     }

     public void characters(char chars[], int start, int length) {
    	String s = new String(chars,start,length);
     	if (!s.equals(" "))
     		s = s.trim();
        if (s.length()==0)
           return;
        characters.append(s);
     }

     public String getCharacters() {
        return characters.toString();
     }

}
