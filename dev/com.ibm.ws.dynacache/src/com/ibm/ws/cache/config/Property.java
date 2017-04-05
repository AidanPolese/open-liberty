// 1.2, 9/30/04
// IBM Confidential OCO Source Material
// 5724-I63, 5724-H88 (C) COPYRIGHT International Business Machines Corp. 1997, 2004
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
package com.ibm.ws.cache.config;

import java.io.*;

public class Property {

   public String      name;
   public String value;
//   public HashMap attributeNames;
//   public ArrayList attributeNames;
   public String[] excludeList;

   public String toString() {
      StringWriter sw = new StringWriter();
      PrintWriter pw = new PrintWriter(sw);
      pw.println("name: "+name);
      pw.println("value: "+value);
      for (int i=0;excludeList != null && i<excludeList.length;i++) {         
         pw.println("exclude "+i+"");
         pw.println(excludeList[i]);
      }
      return sw.toString();
   }

   public String fancyFormat(int level) {
      StringWriter sw = new StringWriter();
      PrintWriter pw = new PrintWriter(sw);
      for (int i =level;i >0; i--) pw.print("\t");
      pw.println("name: "+name);
      pw.println("value: "+value);
      for (int i=0;excludeList != null && i<excludeList.length;i++) {
         for (int ii = level;ii >0; ii--) pw.print("\t");
         pw.println("exclude "+i+"");
         pw.println(excludeList[i]);
      }
    
	 return sw.toString();
   }


   public Object clone() {
      Property p =  new Property();
      p.name = name;
      p.value = value;
      

      /*
      if (attributeNames != null)  {
         c.attributeNames = new HashMap();
         for (Iterator i = attributeNames.keySet().iterator(); i.hasNext();) {
            Object key = i.next();
            p.attributeNames.put(key, ((Value)attributeNames.get(key)).clone());
         }
      }
      */
      if (excludeList != null)  {
         p.excludeList = new String[excludeList.length];
         for (int i = 0; i < excludeList.length;i++) {
            p.excludeList[i] = (String) excludeList[i];
         }
      }

      return p;
   }
}

