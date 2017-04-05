/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2014
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 * 
 * 
 * Change activity:
 *
 * Issue       Date        Name     Description
 * ----------- ----------- -------- ------------------------------------
 */

package com.ibm.ws.request.probe;


import com.ibm.websphere.logging.hpel.LogRecordContext;

public class ThreadLocalStringExtension implements LogRecordContext.Extension {
	
	public ThreadLocalStringExtension() {}

	private ThreadLocal<String> threadLocalString = new ThreadLocal<String>();

	public void setValue(String string) {
		threadLocalString.set(string);
	}

	@Override
	public String getValue() {
		return threadLocalString.get();
	}
	
	public void remove() {
		 threadLocalString.remove();
	}
}

