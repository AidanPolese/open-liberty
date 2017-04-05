// IBM Confidential OCO Source Material
// 5724-I63, 5724-H88 (C) COPYRIGHT International Business Machines Corp. 1997, 2004
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
package com.ibm.ws.cache.config;

import java.io.PrintWriter;
import java.io.StringWriter;


public class CacheInstance {

	public String name;
	public ConfigEntry configEntries[];

	//Object array used for storing processor specific data
	//typically property data that has been parsed
	//format and size are determined by the processor
	public Object processorData[] = null;

	public String toString() {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		pw.println("[CacheInstance]");
		pw.println("name          : " + name);

		for (int i = 0; configEntries != null && i < configEntries.length; i++) {
			pw.println("[CacheEntry " + i + "]");
			pw.println(configEntries[i]);
		}
		return sw.toString();
	}

	//produces nice ascii text
	public String fancyFormat() {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		pw.println("[" + name + "]");

		for (int i = 0; configEntries != null && i < configEntries.length; i++) {
			pw.println("[CacheEntry " + i + "]");
			pw.println(configEntries[i].fancyFormat());
		}
		return sw.toString();
	}

	public Object clone() {
		CacheInstance ci = new CacheInstance();

		ci.name = name;

		if (configEntries != null) {
			ci.configEntries = new ConfigEntry[configEntries.length];
			for (int i = 0; i < configEntries.length; i++) {
				ci.configEntries[i] = (ConfigEntry) configEntries[i].clone();
			}
		}

		return ci;
	}
}
