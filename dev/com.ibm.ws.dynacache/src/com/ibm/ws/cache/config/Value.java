//1.2.1.5, 9/7/05
//IBM Confidential OCO Source Material
//5724-I63, 5724-H88, 5655-N02, 5733-W70 (C) COPYRIGHT International Business Machines Corp. 1997, 2005
//The source code for this program is not published or otherwise divested
//of its trade secrets, irrespective of what has been deposited with the
//U.S. Copyright Office.
package com.ibm.ws.cache.config;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;

public class Value {
   public String   value;
   public ArrayList ranges;
   
   public String toString() {
      StringWriter sw = new StringWriter();
      PrintWriter pw = new PrintWriter(sw);
      pw.println("value:  "+value);
      if (ranges != null){
      	Iterator it = ranges.iterator();
      	while (it.hasNext())
      		pw.println(it.next());
      }
      return sw.toString();
   }

   public String fancyFormat(int level) {
      StringWriter sw = new StringWriter();
      PrintWriter pw = new PrintWriter(sw);
      for (int i = level;i>0;i--) pw.print("\t");
      pw.println("value:  "+value);
      if (ranges != null){
      	Iterator it = ranges.iterator();
      	while (it.hasNext())
      		pw.println(it.next());
      }
      return sw.toString();
   }



   public Object clone() {
      Value c =  new Value();
      c.value = value;
      if (ranges != null){
      	c.ranges = new ArrayList();
      	for (Iterator i = ranges.iterator(); i.hasNext();) {
      		c.ranges.add(((Range)i.next()).clone());
      	}
      }
      return c;
   }
}
