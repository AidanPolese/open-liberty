// 1.2.1.8, 9/30/04
// IBM Confidential OCO Source Material
// 5724-I63, 5724-H88 (C) COPYRIGHT International Business Machines Corp. 1997, 2004
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
package com.ibm.ws.cache.config;

import java.io.*;

public class Invalidation {
   public String    baseName;
   public String    invalidationGenerator;
   public Component components[];

   public Object    invalidationGeneratorImpl;

      public String toString() {
      StringWriter sw = new StringWriter();
      PrintWriter pw = new PrintWriter(sw);
      pw.println("baseName: "+baseName);
      pw.println("invalidationGenerator: " + invalidationGenerator);  
      for (int i=0;components != null && i<components.length;i++) {
         pw.println("Inval. Component "+i);
         pw.println(components[i]);
      }
      return sw.toString();
   }


   public String fancyFormat(int level) {
      StringWriter sw = new StringWriter();
      PrintWriter pw = new PrintWriter(sw);
      for (int i =level;i >0; i--) pw.print("\t");
      pw.println("baseName: "+baseName);
      pw.println("invalidationGenerator: " + invalidationGenerator);
      for (int i=0;components != null && i<components.length;i++) {
         for (int ii = level;ii >0; ii--) pw.print("\t");
         pw.println("Inval. Component "+i);
         pw.println(components[i].fancyFormat(level+1));
      }
      return sw.toString();
   }




   public Object clone() {
      Invalidation c =  new Invalidation();
      c.invalidationGenerator = invalidationGenerator;  
      if (components != null)  {
         c.components = new Component[components.length];
         for (int i = 0; i < components.length;i++) {
            c.components[i] = (Component) components[i].clone();
         }
      }
      c.baseName = baseName;
      return c;
   }

}
