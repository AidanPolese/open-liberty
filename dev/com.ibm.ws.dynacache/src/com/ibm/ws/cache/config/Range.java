//1.1, 9/7/05
//IBM Confidential OCO Source Material
//5724-I63, 5724-H88, 5655-N02, 5733-W70 (C) COPYRIGHT International Business Machines Corp. 1997, 2005
//The source code for this program is not published or otherwise divested
//of its trade secrets, irrespective of what has been deposited with the
//U.S. Copyright Office.
package com.ibm.ws.cache.config;

import java.io.*;

public class Range {
	public int low;
	public int high;

	public String toString() {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		pw.println("range low: "+low+ " high: "+high);
		return sw.toString();
	}

	public String fancyFormat(int level) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		for (int i = level;i>0;i--) pw.print("\t");
		pw.println("range low: "+low+ " high: "+high);
		return sw.toString();
	}
	
	public Object clone() {
		Range c =  new Range();
		c.low = low;
		c.high = high;
		return c;
	}
}
