// 1.2, 9/30/04
// IBM Confidential OCO Source Material
// 5724-I63, 5724-H88 (C) COPYRIGHT International Business Machines Corp. 1997, 2004
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
package com.ibm.ws.cache.config;

import java.io.*;

public class DependencyId {

   public String      baseName;
   public Component[] components;

   public String toString() {
      StringWriter sw = new StringWriter();
      PrintWriter pw = new PrintWriter(sw);
      pw.println("baseName: "+baseName);
      if (components == null) {
         pw.println("numComponents: 0");
      } else {
      pw.println("numComponents: "+components.length);
      }
      return sw.toString();
   }

   public String fancyFormat(int level) {
      StringWriter sw = new StringWriter();
      PrintWriter pw = new PrintWriter(sw);
      for (int i =level;i >0; i--) pw.print("\t");
      pw.println("baseName: "+baseName);
      if (components != null) {
         for (int i = 0; i<components.length;i++) {
            for (int j = level;j >0; j--) pw.print("\t");
            pw.println("Group Id Component "+i);
            pw.println(components[i].fancyFormat(level+1));
         }
      }
      return sw.toString();
   }


   public Object clone() {
      DependencyId c =  new DependencyId();
      c.baseName = baseName;

      if (components != null)  {
         c.components = new Component[components.length];
         for (int i = 0; i < components.length;i++) {
            c.components[i] = (Component) components[i].clone();
         }
      }

      return c;
   }
}

